package br.eng.eliseu.printagent.service;

import br.eng.eliseu.printagent.api.ApiDtos.*;
import br.eng.eliseu.printagent.config.PrintAgentProperties;
import br.eng.eliseu.printagent.domain.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

@Service
public class TrabalhoService {
    private static final Logger log=LoggerFactory.getLogger(TrabalhoService.class);
    private final PrinterService printers; private final PrintAgentProperties props; private final BlockingQueue<TrabalhoImpressao> fila=new LinkedBlockingQueue<>(); private final Map<UUID,TrabalhoImpressao> atuais=new ConcurrentHashMap<>(); private final Deque<TrabalhoImpressao> historico=new ConcurrentLinkedDeque<>(); private volatile boolean ativo=true; private Thread consumidor;
    public TrabalhoService(PrinterService printers,PrintAgentProperties props){this.printers=printers;this.props=props;}
    @PostConstruct void iniciar(){consumidor=new Thread(this::consumir,"print-agent-queue");consumidor.setDaemon(true);consumidor.start();}
    @PreDestroy void parar(){ativo=false;consumidor.interrupt();}
    public TrabalhoImpressao criar(TrabalhoImpressaoRequestDTO r){validar(r);String conteudo="application/zpl".equals(r.documento().tipoConteudo())?normalizarZpl(r.documento().conteudo()):r.documento().conteudo();TrabalhoImpressao t=new TrabalhoImpressao(r.trabalho().nome(),r.impressora().nome(),r.documento().tipoConteudo(),r.documento().codificacao(),conteudo,r.trabalho().copias(),r.configuracoesImpressao());t.status(StatusTrabalho.VALIDADO);atuais.put(t.getId(),t);t.status(StatusTrabalho.NA_FILA);fila.add(t);return t;}
    static String normalizarZpl(String conteudo){
        StringBuilder resultado=new StringBuilder(conteudo.length());
        boolean dadosDoCampo=false;
        for(int i=0;i<conteudo.length();i++){
            if(conteudo.startsWith("^FD",i)){resultado.append("^FD");dadosDoCampo=true;i+=2;continue;}
            if(dadosDoCampo&&conteudo.startsWith("^FS",i)){resultado.append("^FS");dadosDoCampo=false;i+=2;continue;}
            char caractere=conteudo.charAt(i);
            if(!dadosDoCampo&&Character.isWhitespace(caractere))continue;
            resultado.append(caractere);
        }
        return resultado.toString();
    }
    private void validar(TrabalhoImpressaoRequestDTO r){printers.consultar(r.impressora().nome());String tipo=r.documento().tipoConteudo(),cod=r.documento().codificacao();int bytes;
        if("application/pdf".equals(tipo)){if(!"base64".equalsIgnoreCase(cod))invalido("CODIFICACAO_INVALIDA","PDF exige codificação base64.","documento.codificacao");try{bytes=Base64.getDecoder().decode(r.documento().conteudo()).length;}catch(IllegalArgumentException e){invalido("CONTEUDO_INVALIDO","PDF Base64 inválido.","documento.conteudo");return;}if(bytes>props.getLimites().getPdfMaximoBytes())grande();}
        else if("application/zpl".equals(tipo)||"text/plain".equals(tipo)){if(!"utf-8".equalsIgnoreCase(cod))invalido("CODIFICACAO_INVALIDA","O conteúdo exige codificação utf-8.","documento.codificacao");bytes=r.documento().conteudo().getBytes(StandardCharsets.UTF_8).length;int max="application/zpl".equals(tipo)?props.getLimites().getZplMaximoBytes():props.getLimites().getRawMaximoBytes();if(bytes>max)grande();}
        else throw new ApiException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"TIPO_CONTEUDO_NAO_SUPORTADO","Tipo de conteúdo não suportado.","documento.tipoConteudo");
    }
    private void invalido(String c,String m,String f){throw new ApiException(HttpStatus.BAD_REQUEST,c,m,f);} private void grande(){throw new ApiException(HttpStatus.PAYLOAD_TOO_LARGE,"CONTEUDO_MUITO_GRANDE","Documento excede o limite configurado.","documento.conteudo");}
    private void consumir(){while(ativo){try{TrabalhoImpressao t=fila.take();if(t.getStatus()==StatusTrabalho.CANCELADO)continue;t.status(StatusTrabalho.IMPRIMINDO);try{printers.imprimir(t);t.status(StatusTrabalho.CONCLUIDO);log.info("Trabalho {} encaminhado à impressora {}.",t.getId(),t.getImpressora());}catch(ApiException e){t.erro(e.getCodigo(),e.getMessage());log.error("Falha no trabalho {} [{}]: {}",t.getId(),e.getCodigo(),e.getMessage());}catch(Exception e){String mensagem=e.getMessage()==null?"Falha de impressão.":e.getMessage();t.erro("FALHA_IMPRESSAO",mensagem);log.error("Falha no trabalho {} [FALHA_IMPRESSAO]: {}",t.getId(),mensagem,e);}finalizar(t);}catch(InterruptedException e){Thread.currentThread().interrupt();}}}
    private void finalizar(TrabalhoImpressao t){atuais.remove(t.getId());historico.addFirst(t);while(historico.size()>props.getHistorico().getTamanhoMaximo())historico.pollLast();}
    public List<TrabalhoConsultaResponseDTO> atuais(StatusTrabalho status,String impressora,int limite){return atuais.values().stream().filter(t->status==null||t.getStatus()==status).filter(t->impressora==null||t.getImpressora().equals(impressora)).sorted(Comparator.comparing(x->x.dto().dataCriacao())).limit(limite).map(TrabalhoImpressao::dto).toList();}
    public TrabalhoImpressao obter(UUID id){TrabalhoImpressao t=atuais.get(id);if(t==null)t=historico.stream().filter(x->x.getId().equals(id)).findFirst().orElse(null);if(t==null)throw new ApiException(HttpStatus.NOT_FOUND,"TRABALHO_NAO_ENCONTRADO","Trabalho não encontrado.","id");return t;}
    public void cancelar(UUID id){TrabalhoImpressao t=obter(id);if(t.getStatus()!=StatusTrabalho.NA_FILA&&t.getStatus()!=StatusTrabalho.RECEBIDO&&t.getStatus()!=StatusTrabalho.VALIDADO)throw new ApiException(HttpStatus.CONFLICT,"TRABALHO_NAO_PODE_SER_CANCELADO","O estado atual impede o cancelamento.",null);fila.remove(t);t.status(StatusTrabalho.CANCELADO);finalizar(t);}
    public List<TrabalhoConsultaResponseDTO> historico(StatusTrabalho status,String impressora,String tipo,int limite){return historico.stream().filter(t->status==null||t.getStatus()==status).filter(t->impressora==null||t.getImpressora().equals(impressora)).filter(t->tipo==null||t.getTipoConteudo().equals(tipo)).limit(limite).map(TrabalhoImpressao::dto).toList();} public void limparHistorico(){historico.clear();} public int tamanhoFila(){return fila.size();}
}
