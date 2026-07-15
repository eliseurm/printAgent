package br.eng.eliseu.printagent.api;

import br.eng.eliseu.printagent.api.ApiDtos.*;
import br.eng.eliseu.printagent.config.PrintAgentProperties;
import br.eng.eliseu.printagent.domain.*;
import br.eng.eliseu.printagent.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1")
public class ApiController {
    private final TrabalhoService trabalhos;
    private final PrinterService printers;
    private final PrintAgentProperties props;
    private final int porta;

    public ApiController(TrabalhoService trabalhos, PrinterService printers, PrintAgentProperties props, @Value("${server.port:18181}") int porta) {
        this.trabalhos = trabalhos;
        this.printers = printers;
        this.props = props;
        this.porta = porta;
    }

    @GetMapping("/status")
    public EnvelopeRespostaDTO<StatusAplicacaoResponseDTO> status() {
        var ps = printers.listar();
        return EnvelopeRespostaDTO.sucesso("Status consultado com sucesso.", new StatusAplicacaoResponseDTO("OPERACIONAL", "1.0.2", System.getProperty("java.version"), System.getProperty("os.name"), porta, trabalhos.tamanhoFila(), ps.size()));
    }

    @PostMapping("/trabalhos")
    public ResponseEntity<EnvelopeRespostaDTO<TrabalhoResponseDTO>> criar(@Valid @RequestBody TrabalhoImpressaoRequestDTO request) {
        TrabalhoImpressao t = trabalhos.criar(request);
        boolean sync = Boolean.TRUE.equals(request.trabalho().sincrono());
        if (sync) try {
            t.getConclusao().get(props.getTrabalho().getTimeoutSincronoSegundos(), TimeUnit.SECONDS);
        } catch (Exception ignored) {
        }
        String msg = t.getStatus() == StatusTrabalho.CONCLUIDO ? "Trabalho concluído com sucesso." : sync ? "O trabalho continua em processamento." : "Trabalho recebido com sucesso.";
        String estado = sync ? t.getStatus().name() : "RECEBIDO";
        return ResponseEntity.status(sync ? HttpStatus.OK : HttpStatus.CREATED).body(EnvelopeRespostaDTO.sucesso(msg, new TrabalhoResponseDTO(t.getId().toString(), estado)));
    }

    @GetMapping("/trabalhos")
    public EnvelopeRespostaDTO<List<TrabalhoConsultaResponseDTO>> listar(@RequestParam(required = false) StatusTrabalho status, @RequestParam(required = false) String impressora, @RequestParam(defaultValue = "100") int limite) {
        return EnvelopeRespostaDTO.sucesso("Trabalhos consultados com sucesso.", trabalhos.atuais(status, impressora, Math.max(1, Math.min(limite, 1000))));
    }

    @GetMapping("/trabalhos/{id}")
    public EnvelopeRespostaDTO<TrabalhoConsultaResponseDTO> trabalho(@PathVariable UUID id) {
        return EnvelopeRespostaDTO.sucesso("Trabalho consultado com sucesso.", trabalhos.obter(id).dto());
    }

    @DeleteMapping("/trabalhos/{id}")
    public EnvelopeRespostaDTO<TrabalhoResponseDTO> cancelar(@PathVariable UUID id) {
        trabalhos.cancelar(id);
        return EnvelopeRespostaDTO.sucesso("Trabalho cancelado com sucesso.", new TrabalhoResponseDTO(id.toString(), "CANCELADO"));
    }

    @GetMapping("/historico")
    public EnvelopeRespostaDTO<List<TrabalhoConsultaResponseDTO>> historico(@RequestParam(required = false) StatusTrabalho status, @RequestParam(required = false) String impressora, @RequestParam(required = false) String tipoConteudo, @RequestParam(defaultValue = "100") int limite) {
        return EnvelopeRespostaDTO.sucesso("Histórico consultado com sucesso.", trabalhos.historico(status, impressora, tipoConteudo, Math.max(1, Math.min(limite, 1000))));
    }

    @DeleteMapping("/historico")
    public EnvelopeRespostaDTO<Map<String, Object>> limpar() {
        trabalhos.limparHistorico();
        return EnvelopeRespostaDTO.sucesso("Histórico limpo com sucesso.", Map.of());
    }

    @GetMapping("/impressoras")
    public EnvelopeRespostaDTO<List<ImpressoraResponseDTO>> impressoras() {
        return EnvelopeRespostaDTO.sucesso("Impressoras consultadas com sucesso.", printers.listar());
    }

    @PostMapping("/impressoras/atualizar")
    public EnvelopeRespostaDTO<List<ImpressoraResponseDTO>> atualizar() {
        return EnvelopeRespostaDTO.sucesso("Catálogo de impressoras atualizado com sucesso.", printers.listar());
    }

    @GetMapping("/impressoras/{nome}")
    public EnvelopeRespostaDTO<ImpressoraResponseDTO> impressora(@PathVariable String nome) {
        return EnvelopeRespostaDTO.sucesso("Impressora consultada com sucesso.", printers.consultar(nome));
    }

    @GetMapping("/impressoras/{nome}/capacidades")
    public EnvelopeRespostaDTO<CapacidadesImpressoraResponseDTO> capacidades(@PathVariable String nome) {
        return EnvelopeRespostaDTO.sucesso("Capacidades consultadas com sucesso.", printers.capacidades(nome));
    }

    @PostMapping("/testes/impressao")
    public ResponseEntity<EnvelopeRespostaDTO<TrabalhoResponseDTO>> teste(@Valid @RequestBody TesteImpressaoRequestDTO r) {
        String tipo = r.tipoTeste().toUpperCase(Locale.ROOT);
        DocumentoDTO doc = switch (tipo) {
            case "ZPL" -> new DocumentoDTO("application/zpl", "utf-8", "^XA^FO20,20^FDTESTE PRINT AGENT^FS^XZ");
            case "RAW" -> new DocumentoDTO("text/plain", "utf-8", "TESTE PRINT AGENT");
            default -> throw new ApiException(HttpStatus.BAD_REQUEST, "TIPO_TESTE_INVALIDO", "Tipo de teste não suportado nesta versão.", "tipoTeste");
        };
        return criar(new TrabalhoImpressaoRequestDTO(r.impressora(), r.configuracoesImpressao(), new TrabalhoDTO("Teste " + tipo, 1, false), doc));
    }
}
