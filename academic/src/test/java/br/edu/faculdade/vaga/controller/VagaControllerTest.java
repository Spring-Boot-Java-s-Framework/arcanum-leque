package br.edu.faculdade.vaga.controller;

import br.edu.faculdade.vaga.repository.VagaRepository;
import br.edu.faculdade.vaga.service.VagaService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.edu.faculdade.config.TratadorDeErros;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Testes de fatia web: controller + {@link TratadorDeErros} + service +
 * repository reais, sem subir o servidor. Confere o mapeamento de cada exceção
 * para o status HTTP correto.
 *
 * O repository é um bean singleton com dados mutáveis; por isso cada teste
 * recarrega o contexto ({@code AFTER_EACH_TEST_METHOD}) e começa com as 12
 * vagas iniciais.
 *
 * @author Carlos Neto
 * @since 2026-10-13
 */
@WebMvcTest(VagaController.class)
@Import({VagaService.class, VagaRepository.class, TratadorDeErros.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class VagaControllerTest {

    @Autowired
    MockMvc mvc;

    private static String json(String titulo, String senioridade, String slug, String iniciante) {
        return """
                {"titulo":"%s","area":"Back-end","senioridade":"%s","empresaSlug":"%s",
                 "descricao":"texto interno","local":"Remoto"%s}
                """.formatted(titulo, senioridade, slug,
                iniciante == null ? "" : ",\"aceitaIniciante\":" + iniciante);
    }

    @Test
    @DisplayName("GET /vagas -> 200 com 12 vagas, sem o campo descricao")
    void listar() throws Exception {
        mvc.perform(get("/vagas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(12))
                .andExpect(jsonPath("$[0].descricao").doesNotExist());
    }

    @Test
    @DisplayName("GET /vagas/1 -> 200")
    void porId() throws Exception {
        mvc.perform(get("/vagas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    @DisplayName("GET /vagas/999 -> 404 com problem+json")
    void porIdInexistente() throws Exception {
        mvc.perform(get("/vagas/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Vaga não encontrada"));
    }

    @Test
    @DisplayName("POST válido -> 201, Location e corpo sem descricao")
    void criar() throws Exception {
        mvc.perform(post("/vagas").contentType(MediaType.APPLICATION_JSON)
                        .content(json("Pessoa Desenvolvedora Kotlin", "Júnior", "aurora-tech", "true")))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.descricao").doesNotExist());
    }

    @Test
    @DisplayName("POST ignora um id enviado pelo cliente")
    void criarIgnoraIdDoCliente() throws Exception {
        String corpo = """
                {"id":"hackeado","titulo":"Pessoa Desenvolvedora Kotlin","area":"Back-end",
                 "senioridade":"Júnior","empresaSlug":"aurora-tech"}
                """;
        mvc.perform(post("/vagas").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(org.hamcrest.Matchers.not("hackeado")));
    }

    @Test
    @DisplayName("POST sem campos obrigatórios -> 400 com a lista de campos")
    void criarCamposFaltando() throws Exception {
        mvc.perform(post("/vagas").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.titulo").exists())
                .andExpect(jsonPath("$.campos.area").exists())
                .andExpect(jsonPath("$.campos.senioridade").exists())
                .andExpect(jsonPath("$.campos.empresaSlug").exists());
    }

    @Test
    @DisplayName("POST sem corpo -> 400")
    void criarSemCorpo() throws Exception {
        mvc.perform(post("/vagas").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST com senioridade inexistente -> 422")
    void criarSenioridadeInvalida() throws Exception {
        mvc.perform(post("/vagas").contentType(MediaType.APPLICATION_JSON)
                        .content(json("Pessoa Desenvolvedora Kotlin", "Ninja", "aurora-tech", "true")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Regra de negócio violada"));
    }

    @Test
    @DisplayName("POST de vaga Sênior aceitando iniciante -> 422")
    void criarSeniorComIniciante() throws Exception {
        mvc.perform(post("/vagas").contentType(MediaType.APPLICATION_JSON)
                        .content(json("Pessoa Desenvolvedora Kotlin", "Sênior", "aurora-tech", "true")))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("POST duplicado (mesma empresa e título) -> 409")
    void criarDuplicada() throws Exception {
        String corpo = json("Pessoa Desenvolvedora Kotlin", "Júnior", "aurora-tech", "true");
        mvc.perform(post("/vagas").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isCreated());
        mvc.perform(post("/vagas").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Vaga duplicada"));
    }

    @Test
    @DisplayName("PUT em vaga existente -> 200 e mesmo id")
    void trocar() throws Exception {
        mvc.perform(put("/vagas/3").contentType(MediaType.APPLICATION_JSON)
                        .content(json("Pessoa Desenvolvedora Front-end Sênior", "Sênior", "nuvem-rosa", "false")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("3"))
                .andExpect(jsonPath("$.senioridade").value("Sênior"));
    }

    @Test
    @DisplayName("PUT em vaga inexistente -> 404")
    void trocarInexistente() throws Exception {
        mvc.perform(put("/vagas/999").contentType(MediaType.APPLICATION_JSON)
                        .content(json("Pessoa Desenvolvedora Kotlin", "Júnior", "aurora-tech", "true")))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE existente -> 204; repetir -> 404; GET depois -> 404")
    void apagar() throws Exception {
        mvc.perform(delete("/vagas/12")).andExpect(status().isNoContent());
        mvc.perform(delete("/vagas/12")).andExpect(status().isNotFound());
        mvc.perform(get("/vagas/12")).andExpect(status().isNotFound());
    }
}
