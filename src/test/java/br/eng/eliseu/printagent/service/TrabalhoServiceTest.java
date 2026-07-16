package br.eng.eliseu.printagent.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrabalhoServiceTest {
    @Test
    void compactaZplPreservandoEspacosDosCamposImpressos() {
        String zpl = """
            ^XA
            ^MMT

            ^FO50,50^A0N,30,30^FDEmpresa Exemplo Ltda^FS
            ^FO50,90^FDPedido: #987654321^FS
            ^XZ
            """;

        assertEquals(
            "^XA^MMT^FO50,50^A0N,30,30^FDEmpresa Exemplo Ltda^FS^FO50,90^FDPedido: #987654321^FS^XZ",
            TrabalhoService.normalizarZpl(zpl)
        );
    }
}
