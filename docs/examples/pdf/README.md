# Exemplos de PDF

## Objetivo

Esta pasta contém exemplos de documentos PDF que poderão ser utilizados para testar o Print Agent.

Os arquivos aqui presentes destinam-se exclusivamente a testes de integração e homologação.

---

# Estrutura

```text
examples/
└── pdf/
    ├── README.md
    ├── etiqueta-100x60.pdf
    ├── etiqueta-a4.pdf
    └── multipagina.pdf
```

Os arquivos PDF poderão ser substituídos ou ampliados conforme a necessidade dos testes.

---

# Arquivos recomendados

## etiqueta-100x60.pdf

Objetivo:

Validar impressão de uma etiqueta térmica de **100 mm x 60 mm**.

Características:

* uma única página;
* orientação correta para a impressora;
* texto;
* código de barras;
* QR Code;
* margens mínimas.

Uso esperado:

```json
{
    "impressora": {
        "nome": "ElginL42"
    },
    "configuracoesImpressao": {
        "PageSize": "w100h60"
    },
    "trabalho": {
        "nome": "Teste PDF",
        "copias": 1
    },
    "documento": {
        "tipoConteudo": "application/pdf",
        "codificacao": "base64",
        "conteudo": "<PDF em Base64>"
    }
}
```

---

## etiqueta-a4.pdf

Objetivo:

Validar impressão em impressoras convencionais A4.

Características:

* página A4;
* texto;
* tabelas;
* imagens simples.

Permite verificar o funcionamento do agente com impressoras não térmicas.

---

## multipagina.pdf

Objetivo:

Validar impressão de documentos com várias páginas.

Características:

* duas ou mais páginas;
* numeração;
* diferentes tamanhos de conteúdo.

Este documento é útil para validar:

* envio correto ao spooler;
* impressão completa;
* tratamento de múltiplas páginas.

---

# Como gerar o Base64

Linux

```bash
base64 -w0 etiqueta-100x60.pdf
```

macOS

```bash
base64 etiqueta-100x60.pdf
```

Windows (PowerShell)

```powershell
[Convert]::ToBase64String(
    [IO.File]::ReadAllBytes("etiqueta-100x60.pdf")
)
```

O resultado deverá ser enviado no campo:

```json
{
    "documento": {
        "tipoConteudo": "application/pdf",
        "codificacao": "base64",
        "conteudo": "JVBERi0xLjQKJ..."
    }
}
```

---

# Recomendações

Os PDFs utilizados para testes deverão:

* possuir tamanho reduzido;
* não conter informações confidenciais;
* representar cenários reais de impressão;
* possuir dimensões compatíveis com a impressora utilizada.

---

# Impressoras térmicas

Quando o objetivo for imprimir em impressoras térmicas, recomenda-se priorizar **ZPL** sempre que possível.

O uso de PDF é indicado para:

* impressoras laser;
* impressoras jato de tinta;
* impressoras térmicas cujo driver ofereça suporte adequado à impressão de PDF.

---

# Homologação

Durante a homologação, recomenda-se testar pelo menos:

* PDF de uma página;
* PDF de múltiplas páginas;
* PDF em orientação retrato;
* PDF em orientação paisagem;
* PDF contendo código de barras;
* PDF contendo QR Code;
* PDF de etiqueta 100 × 60 mm;
* PDF A4.

---

# Limitações

O Print Agent **não altera** automaticamente:

* orientação do PDF;
* tamanho da página;
* margens;
* escala;
* layout do documento.

Essas características são definidas pelo documento PDF e pelo driver da impressora.

---

# Observação

Para impressoras compatíveis com ZPL, como a **Elgin L42 Pro Full**, a utilização de ZPL normalmente oferece:

* maior velocidade de impressão;
* melhor qualidade;
* maior precisão no posicionamento;
* menor consumo de memória;
* menor dependência do driver do sistema operacional.

Por esse motivo, para etiquetas térmicas, o ZPL é o formato recomendado, deixando o PDF como alternativa para cenários específicos.

---

**Fim do documento `README.md`.**
