package br.edu.faculdade.smoke;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Scaffolding TEMPORÁRIO — junto com {@link SmokeController}. Só prova que a
 * stack (controller + JSON) responde. Remover quando a frente Vaga começar.
 */
@WebMvcTest(SmokeController.class)
class SmokeControllerTest {

    @Autowired
    MockMvc mvc;

    @Test
    void getSmoke_respondeOkComJson() throws Exception {
        mvc.perform(get("/smoke"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.status").value("ok"));
    }
}
