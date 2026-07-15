package br.eng.eliseu.printagent.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public final class ApiDtos {
    private ApiDtos() {
    }

    public record EnvelopeRespostaDTO<T>(boolean sucesso, String mensagem, T dados, List<ErroDTO> erros, OffsetDateTime dataHora) {
        public static <T> EnvelopeRespostaDTO<T> sucesso(String mensagem, T dados) {
            return new EnvelopeRespostaDTO<>(true, mensagem, dados, List.of(), OffsetDateTime.now());
        }

        public static EnvelopeRespostaDTO<Map<String, Object>> erro(String mensagem, List<ErroDTO> erros) {
            return new EnvelopeRespostaDTO<>(false, mensagem, Map.of(), erros, OffsetDateTime.now());
        }
    }

    public record ErroDTO(String codigo, String descricao, String campo) {
    }

    public record ImpressoraDTO(@NotBlank @Size(max = 255) String nome) {
    }

    public record TrabalhoDTO(@NotBlank @Size(max = 100) String nome, @NotNull @Min(1) @Max(999) Integer copias, @NotNull Boolean sincrono) {
    }

    public record DocumentoDTO(@NotBlank String tipoConteudo, @NotBlank String codificacao, @NotBlank String conteudo) {
    }

    public record TrabalhoImpressaoRequestDTO(@NotNull @Valid ImpressoraDTO impressora, Map<String, String> configuracoesImpressao, @NotNull @Valid TrabalhoDTO trabalho, @NotNull @Valid DocumentoDTO documento) {
    }

    public record TrabalhoResponseDTO(String idTrabalho, String status) {
    }

    public record TrabalhoConsultaResponseDTO(String idTrabalho, String nome, String impressora, String tipoConteudo, Integer copias, String status, OffsetDateTime dataCriacao, OffsetDateTime dataInicio,
                                              OffsetDateTime dataTermino, String mensagemErro) {
    }

    public record ImpressoraResponseDTO(String nome, boolean padrao, String status) {
    }

    public record CapacidadesImpressoraResponseDTO(String nome, Map<String, List<String>> configuracoes) {
    }

    public record StatusAplicacaoResponseDTO(String status, String versao, String java, String sistemaOperacional, int porta, int trabalhosNaFila, int impressorasEncontradas) {
    }

    public record TesteImpressaoRequestDTO(@NotNull @Valid ImpressoraDTO impressora, @NotBlank String tipoTeste, Map<String, String> configuracoesImpressao) {
    }

    public record LogsResponseDTO(String arquivo, List<String> linhas) {
    }

    public record ArquivoLogResponseDTO(String nome, long tamanhoBytes, OffsetDateTime modificadoEm) {
    }

    public record AtualizacaoResponseDTO(String versaoAtual, String versaoDisponivel, boolean atualizacaoDisponivel, String notas) {
    }

    public record ConfiguracoesAtualizadasResponseDTO(boolean reinicializacaoNecessaria) {
    }
}
