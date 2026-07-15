package br.eng.eliseu.printagent.api;

import br.eng.eliseu.printagent.api.ApiDtos.*;
import br.eng.eliseu.printagent.config.PrintAgentProperties;
import br.eng.eliseu.printagent.service.ApiException;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.*;
import java.time.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class AdministracaoController {
    private final PrintAgentProperties p;
    private final Path logs = Path.of("logs").toAbsolutePath().normalize();

    public AdministracaoController(PrintAgentProperties p) {
        this.p = p;
    }

    @GetMapping("/configuracoes")
    public EnvelopeRespostaDTO<Map<String, Object>> configuracoes() {
        return EnvelopeRespostaDTO.sucesso("Configurações consultadas com sucesso.", configMap());
    }

    @PutMapping("/configuracoes")
    public EnvelopeRespostaDTO<ConfiguracoesAtualizadasResponseDTO> configurar(@RequestBody Map<String, Map<String, Object>> r) {
        Optional.ofNullable(r.get("historico")).map(x -> x.get("tamanhoMaximo")).ifPresent(v -> p.getHistorico().setTamanhoMaximo(numero(v, 1, 100000, "historico.tamanhoMaximo")));
        Optional.ofNullable(r.get("trabalho")).map(x -> x.get("timeoutSincronoSegundos")).ifPresent(v -> p.getTrabalho().setTimeoutSincronoSegundos(numero(v, 1, 600, "trabalho.timeoutSincronoSegundos")));
        Optional.ofNullable(r.get("provider")).map(x -> x.get("timeoutComandoSegundos")).ifPresent(v -> p.getProvider().setTimeoutComandoSegundos(numero(v, 1, 300, "provider.timeoutComandoSegundos")));
        Optional.ofNullable(r.get("impressao")).map(x -> x.get("configuracoesDesconhecidas")).ifPresent(v -> {
            try {
                p.getImpressao().setConfiguracoesDesconhecidas(PrintAgentProperties.TratamentoConfiguracaoDesconhecida.valueOf(v.toString()));
            } catch (Exception e) {
                throw invalido("impressao.configuracoesDesconhecidas");
            }
        });
        return EnvelopeRespostaDTO.sucesso("Configurações atualizadas com sucesso.", new ConfiguracoesAtualizadasResponseDTO(false));
    }

    @GetMapping("/logs")
    public EnvelopeRespostaDTO<LogsResponseDTO> logs(@RequestParam(defaultValue = "500") int linhas, @RequestParam(required = false) String nivel, @RequestParam(required = false) String busca) throws IOException {
        Path f = logs.resolve("print-agent.log");
        if (!Files.exists(f)) return EnvelopeRespostaDTO.sucesso("Logs consultados com sucesso.", new LogsResponseDTO("print-agent.log", List.of()));
        List<String> all = Files.readAllLines(f).stream().filter(x -> nivel == null || x.contains(" " + nivel.toUpperCase(Locale.ROOT) + " ")).filter(x -> busca == null || x.contains(busca)).toList();
        return EnvelopeRespostaDTO.sucesso("Logs consultados com sucesso.", new LogsResponseDTO("print-agent.log", all.subList(Math.max(0, all.size() - Math.min(linhas, 1000)), all.size())));
    }

    @GetMapping("/logs/arquivos")
    public EnvelopeRespostaDTO<List<ArquivoLogResponseDTO>> arquivos() throws IOException {
        if (!Files.isDirectory(logs)) return EnvelopeRespostaDTO.sucesso("Arquivos de log consultados com sucesso.", List.of());
        try (var s = Files.list(logs)) {
            return EnvelopeRespostaDTO.sucesso("Arquivos de log consultados com sucesso.", s.filter(Files::isRegularFile).map(this::arquivo).toList());
        }
    }

    @GetMapping("/logs/arquivos/{nome}")
    public ResponseEntity<Resource> baixar(@PathVariable String nome) {
        Path f = logs.resolve(nome).normalize();
        if (!f.startsWith(logs) || !Files.isRegularFile(f)) throw new ApiException(HttpStatus.NOT_FOUND, "ARQUIVO_LOG_NAO_ENCONTRADO", "Arquivo de log não encontrado.", "nome");
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + f.getFileName() + "\"").contentType(MediaType.TEXT_PLAIN).body(new FileSystemResource(f));
    }

    @GetMapping("/atualizacoes")
    public EnvelopeRespostaDTO<AtualizacaoResponseDTO> atualizacao() {
        return EnvelopeRespostaDTO.sucesso("O Print Agent está atualizado.", new AtualizacaoResponseDTO("1.0.2", "1.0.2", false, ""));
    }

    @PostMapping("/atualizacoes/verificar")
    public EnvelopeRespostaDTO<AtualizacaoResponseDTO> verificar() {
        return atualizacao();
    }

    private Map<String, Object> configMap() {
        return Map.of("historico", Map.of("tamanhoMaximo", p.getHistorico().getTamanhoMaximo()), "trabalho", Map.of("timeoutSincronoSegundos", p.getTrabalho().getTimeoutSincronoSegundos()), "provider", Map.of("timeoutComandoSegundos", p.getProvider().getTimeoutComandoSegundos()), "impressao", Map.of("configuracoesDesconhecidas", p.getImpressao().getConfiguracoesDesconhecidas()), "atualizacao", Map.of("habilitada", p.getAtualizacao().isHabilitada(), "verificarAoIniciar", p.getAtualizacao().isVerificarAoIniciar()), "log", Map.of("nivel", "INFO"));
    }

    private int numero(Object v, int min, int max, String campo) {
        if (!(v instanceof Number n) || n.intValue() < min || n.intValue() > max) throw invalido(campo);
        return n.intValue();
    }

    private ApiException invalido(String campo) {
        return new ApiException(HttpStatus.BAD_REQUEST, "VALOR_INVALIDO", "Valor de configuração inválido.", campo);
    }

    private ArquivoLogResponseDTO arquivo(Path f) {
        try {
            return new ArquivoLogResponseDTO(f.getFileName().toString(), Files.size(f), Files.getLastModifiedTime(f).toInstant().atOffset(ZoneOffset.UTC));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
