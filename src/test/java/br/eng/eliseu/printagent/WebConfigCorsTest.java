package br.eng.eliseu.printagent;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WebConfigCorsTest {
    @Autowired private MockMvc mvc;

    @Test
    void permiteConsultaDeImpressorasPelasOrigensLocaisDoPainel() throws Exception {
        for (String origem : new String[]{"http://localhost:8080", "http://127.0.0.1:8080"}) {
            mvc.perform(options("/api/v1/impressoras")
                            .header("Origin", origem)
                            .header("Access-Control-Request-Method", "GET"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Access-Control-Allow-Origin", "*"));
        }
    }

    @Test
    void permitePreflightJsonSemEnviarTrabalhoAImpressora() throws Exception {
        mvc.perform(options("/api/v1/trabalhos")
                        .header("Origin", "http://localhost:8080")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"))
                .andExpect(header().string("Access-Control-Allow-Headers", "content-type"));
    }

    @Test
    void permiteQualquerOrigemExternaSemCredenciais() throws Exception {
        mvc.perform(options("/api/v1/trabalhos")
                        .header("Origin", "https://nao-autorizado.example")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"))
                .andExpect(header().doesNotExist("Access-Control-Allow-Credentials"));
    }
}
