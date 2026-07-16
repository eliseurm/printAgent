package br.eng.eliseu.printagent.service;

import br.eng.eliseu.printagent.api.ApiDtos.*;
import br.eng.eliseu.printagent.config.PrintAgentProperties;
import br.eng.eliseu.printagent.domain.TrabalhoImpressao;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import javax.print.*;
import javax.print.attribute.Attribute;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class PrinterService {
    private final PrintAgentProperties properties;
    public PrinterService(PrintAgentProperties properties){this.properties=properties;}
    public List<ImpressoraResponseDTO> listar(){
        PrintService padrao=PrintServiceLookup.lookupDefaultPrintService();
        return Arrays.stream(PrintServiceLookup.lookupPrintServices(null,null)).map(p->new ImpressoraResponseDTO(p.getName(),padrao!=null&&padrao.getName().equals(p.getName()),status(p))).toList();
    }
    public ImpressoraResponseDTO consultar(String nome){PrintService p=encontrar(nome);PrintService d=PrintServiceLookup.lookupDefaultPrintService();return new ImpressoraResponseDTO(p.getName(),d!=null&&d.getName().equals(p.getName()),status(p));}
    public CapacidadesImpressoraResponseDTO capacidades(String nome){
        PrintService p=encontrar(nome); Map<String,List<String>> dados=new TreeMap<>();
        for(Class<?> categoria:p.getSupportedAttributeCategories()){
            Class<? extends Attribute> tipo=(Class<? extends Attribute>)categoria;
            Object suportados=p.getSupportedAttributeValues(tipo,null,null);
            List<String> valores=valores(suportados);
            if(valores.isEmpty()) valores=valores(p.getDefaultAttributeValue(tipo));
            dados.put(categoria.getSimpleName(),valores);
        }
        return new CapacidadesImpressoraResponseDTO(nome,dados);
    }
    private List<String> valores(Object valor){
        if(valor==null)return List.of();
        List<String> resultado=new ArrayList<>();
        if(valor.getClass().isArray()){
            int tamanho=Array.getLength(valor);
            for(int i=0;i<tamanho;i++)adicionar(resultado,Array.get(valor,i));
        }else if(valor instanceof Collection<?> colecao){
            colecao.forEach(item->adicionar(resultado,item));
        }else adicionar(resultado,valor);
        return resultado.stream().distinct().toList();
    }
    private void adicionar(List<String> resultado,Object valor){
        if(valor==null)return;
        if(valor.getClass().isArray()){
            int tamanho=Array.getLength(valor);
            for(int i=0;i<tamanho;i++)adicionar(resultado,Array.get(valor,i));
        }else resultado.add(valor.toString());
    }
    public void imprimir(TrabalhoImpressao t) throws Exception {
        PrintService printer=encontrar(t.getImpressora());
        byte[] bytes=conteudo(t);
        if("application/pdf".equals(t.getTipoConteudo())){imprimirPdfLinux(t,bytes);return;}
        DocPrintJob job=printer.createPrintJob();
        for(int i=0;i<t.getCopias();i++) job.print(new SimpleDoc(bytes,DocFlavor.BYTE_ARRAY.AUTOSENSE,null),null);
    }
    private byte[] conteudo(TrabalhoImpressao t){
        return switch(t.getTipoConteudo()){
            case "application/zpl","text/plain" -> t.getConteudo().getBytes(StandardCharsets.UTF_8);
            case "application/pdf" -> {try{yield Base64.getDecoder().decode(t.getConteudo());}catch(IllegalArgumentException e){throw new ApiException(HttpStatus.BAD_REQUEST,"CONTEUDO_INVALIDO","PDF Base64 inválido.","documento.conteudo");}}
            default -> throw new ApiException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"TIPO_CONTEUDO_NAO_SUPORTADO","Tipo de conteúdo não suportado.","documento.tipoConteudo");
        };
    }
    private void imprimirPdfLinux(TrabalhoImpressao t,byte[] bytes) throws Exception {
        if(!System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("linux")) throw new ApiException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"TIPO_CONTEUDO_NAO_SUPORTADO_PELO_PROVIDER","PDF não é suportado pelo Provider Windows na versão 1.0.0.","documento.tipoConteudo");
        Path arquivo=Files.createTempFile("print-agent-",".pdf");
        try{Files.write(arquivo,bytes,StandardOpenOption.TRUNCATE_EXISTING);List<String> cmd=new ArrayList<>(List.of("lp","-d",t.getImpressora()));t.getConfiguracoes().forEach((k,v)->{cmd.add("-o");cmd.add(k+"="+v);});cmd.add(arquivo.toString());Process p=new ProcessBuilder(cmd).redirectErrorStream(true).start();if(!p.waitFor(properties.getProvider().getTimeoutComandoSegundos(),TimeUnit.SECONDS)){p.destroyForcibly();throw new IOException("Timeout ao executar lp.");}if(p.exitValue()!=0)throw new IOException(new String(p.getInputStream().readAllBytes(),StandardCharsets.UTF_8));}finally{Files.deleteIfExists(arquivo);}
    }
    private PrintService encontrar(String nome){return Arrays.stream(PrintServiceLookup.lookupPrintServices(null,null)).filter(p->p.getName().equals(nome)).findFirst().orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"IMPRESSORA_NAO_ENCONTRADA","A impressora '"+nome+"' não está instalada.","impressora.nome"));}
    private String status(PrintService p){PrinterIsAcceptingJobs a=p.getAttribute(PrinterIsAcceptingJobs.class);return PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS.equals(a)?"STOPPED":"READY";}
}
