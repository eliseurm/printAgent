package br.eng.eliseu.printagent.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties="server.port=0")
@AutoConfigureMockMvc
class ApiControllerTest {
    @Autowired MockMvc mvc;
    @Test void statusUsaEnvelopePadrao() throws Exception {mvc.perform(get("/api/v1/status")).andExpect(status().isOk()).andExpect(jsonPath("$.sucesso").value(true)).andExpect(jsonPath("$.dados.status").value("OPERACIONAL")).andExpect(jsonPath("$.erros").isArray());}
    @Test void rejeitaJsonInvalido() throws Exception {mvc.perform(post("/api/v1/trabalhos").contentType("application/json").content("{}")) .andExpect(status().isBadRequest()).andExpect(jsonPath("$.sucesso").value(false)).andExpect(jsonPath("$.erros").isArray());}
    @Test void painelEstaDisponivelNaRotaDocumentada() throws Exception {mvc.perform(get("/printAgent")).andExpect(status().isOk()).andExpect(forwardedUrl("/printAgent/index.html"));}
    @Test void painelContemDocumentacaoETeste() throws Exception {mvc.perform(get("/printAgent/index.html")).andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("id=\"documentacao\""))).andExpect(content().string(org.hamcrest.Matchers.containsString("id=\"exemploTeste\""))).andExpect(content().string(org.hamcrest.Matchers.containsString("id=\"imprimir\""))).andExpect(content().string(org.hamcrest.Matchers.containsString("id=\"respostaTeste\"")));}
}
