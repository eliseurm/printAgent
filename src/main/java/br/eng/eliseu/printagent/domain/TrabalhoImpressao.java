package br.eng.eliseu.printagent.domain;

import br.eng.eliseu.printagent.api.ApiDtos.TrabalhoConsultaResponseDTO;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TrabalhoImpressao {
    private final UUID id = UUID.randomUUID();
    private final String nome, impressora, tipoConteudo, codificacao, conteudo;
    private final int copias;
    private final Map<String, String> configuracoes;
    private final OffsetDateTime dataCriacao = OffsetDateTime.now();
    private volatile OffsetDateTime dataInicio, dataConclusao;
    private volatile StatusTrabalho status = StatusTrabalho.RECEBIDO;
    private volatile String codigoErro, mensagemErro;
    private final CompletableFuture<StatusTrabalho> conclusao = new CompletableFuture<>();

    public TrabalhoImpressao(String nome, String impressora, String tipoConteudo, String codificacao, String conteudo, int copias, Map<String, String> configuracoes) {
        this.nome = nome;
        this.impressora = impressora;
        this.tipoConteudo = tipoConteudo;
        this.codificacao = codificacao;
        this.conteudo = conteudo;
        this.copias = copias;
        this.configuracoes = configuracoes == null ? Map.of() : Map.copyOf(configuracoes);
    }

    public UUID getId() {
        return id;
    }

    public String getImpressora() {
        return impressora;
    }

    public String getTipoConteudo() {
        return tipoConteudo;
    }

    public String getCodificacao() {
        return codificacao;
    }

    public String getConteudo() {
        return conteudo;
    }

    public int getCopias() {
        return copias;
    }

    public Map<String, String> getConfiguracoes() {
        return configuracoes;
    }

    public StatusTrabalho getStatus() {
        return status;
    }

    public CompletableFuture<StatusTrabalho> getConclusao() {
        return conclusao;
    }

    public synchronized void status(StatusTrabalho novo) {
        status = novo;
        if (novo == StatusTrabalho.IMPRIMINDO) dataInicio = OffsetDateTime.now();
        if (novo == StatusTrabalho.CONCLUIDO || novo == StatusTrabalho.CANCELADO) {
            dataConclusao = OffsetDateTime.now();
            conclusao.complete(novo);
        }
    }

    public synchronized void erro(String codigo, String mensagem) {
        codigoErro = codigo;
        mensagemErro = mensagem;
        status = StatusTrabalho.ERRO;
        dataConclusao = OffsetDateTime.now();
        conclusao.complete(status);
    }

    public TrabalhoConsultaResponseDTO dto() {
        return new TrabalhoConsultaResponseDTO(id.toString(), nome, impressora, tipoConteudo, copias, status.name(), dataCriacao, dataInicio, dataConclusao, mensagemErro == null ? "" : mensagemErro);
    }
}
