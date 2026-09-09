package br.eng.eliseu.printagent.service;

import br.eng.eliseu.printagent.api.ApiDtos.*;
import br.eng.eliseu.printagent.config.PrintAgentProperties;
import br.eng.eliseu.printagent.domain.TrabalhoImpressao;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class TrabalhoServiceTest {
    @Test
    void encaminhaZplIntegralmenteDaRequisicaoAoProvider() throws Exception {
        String zpl = "  ^XA\r\n^PW800\n^LL1200\n^LH0,0\n^LT0\n"
                + "^FO20,20^FD  União  Central\t\nAssociação Norte  ^FS\n"
                + "^FO20,80^FV  Campo com espaços  ^FS\n^XZ\n\n"
                + "^XA\n^FO20,20^FD Segunda etiqueta ^FS\n^XZ\n";
        var recebido = new AtomicReference<TrabalhoImpressao>();
        var enviado = new CountDownLatch(1);
        var properties = new PrintAgentProperties();
        var printer = new PrinterService(properties) {
            @Override
            public ImpressoraResponseDTO consultar(String nome) {
                return new ImpressoraResponseDTO(nome, false, "READY");
            }

            @Override
            public void imprimir(TrabalhoImpressao trabalho) {
                recebido.set(trabalho);
                enviado.countDown();
            }
        };
        var service = new TrabalhoService(printer, properties);
        service.iniciar();
        try {
            var trabalho = service.criar(new TrabalhoImpressaoRequestDTO(
                    new ImpressoraDTO("ElginL42"), Map.of(), new TrabalhoDTO("Etiqueta", 1, false),
                    new DocumentoDTO("application/zpl", "utf-8", zpl)));

            assertThat(enviado.await(5, TimeUnit.SECONDS)).isTrue();
            assertThat(recebido.get()).isSameAs(trabalho);
            assertThat(recebido.get().getConteudo().getBytes(StandardCharsets.UTF_8))
                    .containsExactly(zpl.getBytes(StandardCharsets.UTF_8));
        } finally {
            service.parar();
        }
    }
}
