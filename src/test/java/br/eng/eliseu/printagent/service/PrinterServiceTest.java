package br.eng.eliseu.printagent.service;

import br.eng.eliseu.printagent.config.PrintAgentProperties;
import br.eng.eliseu.printagent.domain.TrabalhoImpressao;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PrinterServiceTest {

    private final PrinterService service = new PrinterService(new PrintAgentProperties());

    @Test
    void deveEncaminharZplRawSemAplicarConfiguracoesDoDriver() {
        TrabalhoImpressao trabalho = new TrabalhoImpressao(
                "Etiqueta", "ElginL42", "application/zpl", "utf-8", "^XA^XZ",
                2, Map.of("PageSize", "w100h60", "Orientation", "landscape",
                        "scaling", "50", "PrintDarkness", "20"));

        Path arquivo = Path.of("/tmp/etiqueta.bin");
        List<String> comando = service.comandoLp(trabalho, arquivo, true);

        assertThat(comando).containsExactly(
                "lp", "-d", "ElginL42", "-n", "2",
                "-o", "raw",
                arquivo.toString());
    }

    @Test
    void naoDeveUsarRawParaPdf() {
        TrabalhoImpressao trabalho = new TrabalhoImpressao(
                "Documento", "ElginL42", "application/pdf", "base64", "JVBERi0=",
                1, Map.of("PageSize", "A4"));

        Path arquivo = Path.of("/tmp/documento.pdf");
        List<String> comando = service.comandoLp(trabalho, arquivo, false);

        assertThat(comando).containsExactly(
                "lp", "-d", "ElginL42", "-n", "1", "-o", "PageSize=A4", arquivo.toString());
    }
}
