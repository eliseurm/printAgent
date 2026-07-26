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
    void deveMontarComandoCupRawParaZplNoLinux() {
        TrabalhoImpressao trabalho = new TrabalhoImpressao(
                "Etiqueta", "ElginL42", "application/zpl", "utf-8", "^XA^XZ",
                2, Map.of("PageSize", "w100h60"));

        List<String> comando = service.comandoLp(trabalho, Path.of("/tmp/etiqueta.bin"), true);

        assertThat(comando).containsExactly(
                "lp", "-d", "ElginL42", "-n", "2",
                "-o", "raw",
                "-o", "PageSize=w100h60",
                "/tmp/etiqueta.bin");
    }

    @Test
    void naoDeveUsarRawParaPdf() {
        TrabalhoImpressao trabalho = new TrabalhoImpressao(
                "Documento", "ElginL42", "application/pdf", "base64", "JVBERi0=",
                1, Map.of());

        List<String> comando = service.comandoLp(trabalho, Path.of("/tmp/documento.pdf"), false);

        assertThat(comando).containsExactly(
                "lp", "-d", "ElginL42", "-n", "1", "/tmp/documento.pdf");
    }
}
