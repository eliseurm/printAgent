package br.eng.eliseu.printagent.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Validated
@ConfigurationProperties("print-agent")
public class PrintAgentProperties {
    @Valid
    private final Historico historico = new Historico();
    @Valid
    private final Trabalho trabalho = new Trabalho();
    @Valid
    private final Provider provider = new Provider();
    @Valid
    private final Impressao impressao = new Impressao();
    @Valid
    private final Cors cors = new Cors();
    @Valid
    private final Atualizacao atualizacao = new Atualizacao();
    @Valid
    private final Limites limites = new Limites();

    public Historico getHistorico() {
        return historico;
    }

    public Trabalho getTrabalho() {
        return trabalho;
    }

    public Provider getProvider() {
        return provider;
    }

    public Impressao getImpressao() {
        return impressao;
    }

    public Cors getCors() {
        return cors;
    }

    public Atualizacao getAtualizacao() {
        return atualizacao;
    }

    public Limites getLimites() {
        return limites;
    }

    public static class Historico {
        @Min(1)
        private int tamanhoMaximo = 100;

        public int getTamanhoMaximo() {
            return tamanhoMaximo;
        }

        public void setTamanhoMaximo(int v) {
            tamanhoMaximo = v;
        }
    }

    public static class Trabalho {
        @Min(1)
        @Max(600)
        private int timeoutSincronoSegundos = 30;

        public int getTimeoutSincronoSegundos() {
            return timeoutSincronoSegundos;
        }

        public void setTimeoutSincronoSegundos(int v) {
            timeoutSincronoSegundos = v;
        }
    }

    public static class Provider {
        @Min(1)
        @Max(300)
        private int timeoutComandoSegundos = 15;

        public int getTimeoutComandoSegundos() {
            return timeoutComandoSegundos;
        }

        public void setTimeoutComandoSegundos(int v) {
            timeoutComandoSegundos = v;
        }
    }

    public static class Impressao {
        private TratamentoConfiguracaoDesconhecida configuracoesDesconhecidas = TratamentoConfiguracaoDesconhecida.IGNORAR;

        public TratamentoConfiguracaoDesconhecida getConfiguracoesDesconhecidas() {
            return configuracoesDesconhecidas;
        }

        public void setConfiguracoesDesconhecidas(TratamentoConfiguracaoDesconhecida v) {
            configuracoesDesconhecidas = v;
        }
    }

    public static class Cors {
        private List<String> origensPermitidas = new ArrayList<>();

        public List<String> getOrigensPermitidas() {
            return origensPermitidas;
        }

        public void setOrigensPermitidas(List<String> v) {
            origensPermitidas = v == null ? new ArrayList<>() : v;
        }
    }

    public static class Atualizacao {
        private boolean habilitada;
        private boolean verificarAoIniciar;

        public boolean isHabilitada() {
            return habilitada;
        }

        public void setHabilitada(boolean v) {
            habilitada = v;
        }

        public boolean isVerificarAoIniciar() {
            return verificarAoIniciar;
        }

        public void setVerificarAoIniciar(boolean v) {
            verificarAoIniciar = v;
        }
    }

    public static class Limites {
        private int zplMaximoBytes = 2097152, rawMaximoBytes = 2097152, pdfMaximoBytes = 15728640;

        public int getZplMaximoBytes() {
            return zplMaximoBytes;
        }

        public void setZplMaximoBytes(int v) {
            zplMaximoBytes = v;
        }

        public int getRawMaximoBytes() {
            return rawMaximoBytes;
        }

        public void setRawMaximoBytes(int v) {
            rawMaximoBytes = v;
        }

        public int getPdfMaximoBytes() {
            return pdfMaximoBytes;
        }

        public void setPdfMaximoBytes(int v) {
            pdfMaximoBytes = v;
        }
    }

    public enum TratamentoConfiguracaoDesconhecida {IGNORAR, REJEITAR}
}
