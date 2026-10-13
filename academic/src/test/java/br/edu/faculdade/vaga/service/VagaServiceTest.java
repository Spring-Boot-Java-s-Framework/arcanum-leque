package br.edu.faculdade.vaga.service;

import br.edu.faculdade.vaga.dto.VagaEntrada;
import br.edu.faculdade.vaga.dto.VagaResposta;
import br.edu.faculdade.vaga.exception.RegraDeNegocioException;
import br.edu.faculdade.vaga.exception.VagaDuplicadaException;
import br.edu.faculdade.vaga.exception.VagaNaoEncontradaException;
import br.edu.faculdade.vaga.repository.VagaRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testes unitários das regras de negócio do {@link VagaService}, sem Spring e
 * com um {@link VagaRepository} novo (12 vagas iniciais) a cada teste.
 *
 * @author Carlos Neto
 * @since 2026-10-13
 */
class VagaServiceTest {

    private VagaService servico;

    @BeforeEach
    void preparar() {
        servico = new VagaService(new VagaRepository());
    }

    private static VagaEntrada entrada(String titulo, String senioridade, String slug, Boolean iniciante) {
        return new VagaEntrada(titulo, "Back-end", senioridade, slug, "Descrição interna", "Remoto", iniciante);
    }

    private static VagaEntrada valida() {
        return entrada("Pessoa Desenvolvedora Kotlin", "Júnior", "aurora-tech", true);
    }

    // ------------------------------------------------------------- leitura

    @Test
    @DisplayName("listar devolve as 12 vagas iniciais")
    void listarDevolveVagasIniciais() {
        assertEquals(12, servico.listar().size());
    }

    @Test
    @DisplayName("buscarPorId devolve a vaga existente")
    void buscarPorIdExistente() {
        assertEquals("1", servico.buscarPorId("1").id());
    }

    @Test
    @DisplayName("buscarPorId ignora espaços ao redor do id")
    void buscarPorIdComEspacos() {
        assertEquals("1", servico.buscarPorId("  1 ").id());
    }

    @Test
    @DisplayName("buscarPorId de id inexistente lança VagaNaoEncontradaException")
    void buscarPorIdInexistente() {
        assertThrows(VagaNaoEncontradaException.class, () -> servico.buscarPorId("999"));
    }

    @Test
    @DisplayName("buscarPorId com id nulo ou em branco lança RegraDeNegocioException")
    void buscarPorIdInvalido() {
        assertThrows(RegraDeNegocioException.class, () -> servico.buscarPorId(null));
        assertThrows(RegraDeNegocioException.class, () -> servico.buscarPorId("   "));
    }

    // ------------------------------------------------------------ criação

    @Test
    @DisplayName("criar gera um id novo (UUID) e a vaga passa a ser listada")
    void criarGeraIdEPersiste() {
        VagaResposta criada = servico.criar(valida());

        assertNotNull(criada.id());
        assertEquals(36, criada.id().length());
        assertEquals(13, servico.listar().size());
        assertEquals(criada.id(), servico.buscarPorId(criada.id()).id());
    }

    @Test
    @DisplayName("criar normaliza espaços, senioridade sem acento e slug em maiúsculas")
    void criarNormalizaEntrada() {
        VagaEntrada suja = new VagaEntrada("  Pessoa Desenvolvedora Go  ", "  Back-end ",
                "junior", " AURORA-TECH ", "  ", "  ", true);

        VagaResposta criada = servico.criar(suja);

        assertEquals("Pessoa Desenvolvedora Go", criada.titulo());
        assertEquals("Back-end", criada.area());
        assertEquals("Júnior", criada.senioridade());
        assertEquals("aurora-tech", criada.empresaSlug());
        assertEquals(null, criada.local());
    }

    @Test
    @DisplayName("criar aceita senioridade com qualquer caixa e sem acento (SENIOR vira Sênior)")
    void criarAceitaSenioridadeLivre() {
        assertEquals("Sênior", servico.criar(entrada("Arquiteta de Software", "SENIOR", "aurora-tech", false)).senioridade());
        assertEquals("Estágio", servico.criar(entrada("Estágio em Qualidade", "estagio", "aurora-tech", true)).senioridade());
    }

    @Test
    @DisplayName("aceitaIniciante ausente (null) vira false")
    void aceitaInicianteNuloViraFalse() {
        assertFalse(servico.criar(entrada("Pessoa Desenvolvedora Rust", "Pleno", "aurora-tech", null)).aceitaIniciante());
    }

    @Test
    @DisplayName("criar com entrada nula lança RegraDeNegocioException")
    void criarEntradaNula() {
        assertThrows(RegraDeNegocioException.class, () -> servico.criar(null));
    }

    @Test
    @DisplayName("título curto demais (< 5) é rejeitado")
    void tituloCurto() {
        assertThrows(RegraDeNegocioException.class,
                () -> servico.criar(entrada("Dev", "Júnior", "aurora-tech", true)));
    }

    @Test
    @DisplayName("título acima de 120 caracteres é rejeitado")
    void tituloLongo() {
        assertThrows(RegraDeNegocioException.class,
                () -> servico.criar(entrada("a".repeat(121), "Júnior", "aurora-tech", true)));
    }

    @Test
    @DisplayName("descrição acima de 2000 caracteres é rejeitada")
    void descricaoLonga() {
        VagaEntrada longa = new VagaEntrada("Pessoa Desenvolvedora Elixir", "Back-end", "Pleno",
                "aurora-tech", "x".repeat(2001), "Remoto", false);
        assertThrows(RegraDeNegocioException.class, () -> servico.criar(longa));
    }

    @Test
    @DisplayName("senioridade inexistente é rejeitada")
    void senioridadeInvalida() {
        assertThrows(RegraDeNegocioException.class,
                () -> servico.criar(entrada("Pessoa Desenvolvedora Lua", "Ninja", "aurora-tech", false)));
        assertThrows(RegraDeNegocioException.class,
                () -> servico.criar(entrada("Pessoa Desenvolvedora Lua", null, "aurora-tech", false)));
    }

    @Test
    @DisplayName("slug com espaço, símbolo ou hifens soltos é rejeitado")
    void slugInvalido() {
        for (String ruim : new String[] {"aurora tech", "aurora_tech", "-aurora", "aurora-", "aurora--tech", "", null}) {
            assertThrows(RegraDeNegocioException.class,
                    () -> servico.criar(entrada("Pessoa Desenvolvedora Lua", "Júnior", ruim, true)),
                    "slug: " + ruim);
        }
    }

    @Test
    @DisplayName("vaga Pleno ou Sênior não pode aceitar iniciante")
    void seniorNaoAceitaIniciante() {
        assertThrows(RegraDeNegocioException.class,
                () -> servico.criar(entrada("Pessoa Desenvolvedora Scala", "Pleno", "aurora-tech", true)));
        assertThrows(RegraDeNegocioException.class,
                () -> servico.criar(entrada("Pessoa Desenvolvedora Scala", "Sênior", "aurora-tech", true)));
    }

    @Test
    @DisplayName("mesmo título na mesma empresa é duplicidade, mesmo com outra caixa e sem acento")
    void duplicidadeNaMesmaEmpresa() {
        // Já existe "Estágio em Front-end" para mareh-digital (id 2).
        assertThrows(VagaDuplicadaException.class,
                () -> servico.criar(entrada("ESTAGIO EM FRONT-END", "Estágio", "mareh-digital", true)));
    }

    @Test
    @DisplayName("mesmo título em empresa diferente é permitido")
    void mesmoTituloEmOutraEmpresa() {
        VagaResposta criada = servico.criar(entrada("Estágio em Front-end", "Estágio", "coral-labs", true));
        assertEquals("coral-labs", criada.empresaSlug());
    }

    @Test
    @DisplayName("falha de regra não grava nada")
    void falhaNaoPersiste() {
        assertThrows(RegraDeNegocioException.class,
                () -> servico.criar(entrada("Pessoa Desenvolvedora Scala", "Pleno", "aurora-tech", true)));
        assertEquals(12, servico.listar().size());
    }

    // -------------------------------------------------------------- troca

    @Test
    @DisplayName("trocar preserva o id e substitui os dados")
    void trocarPreservaId() {
        VagaResposta trocada = servico.trocar("3", entrada("Pessoa Desenvolvedora Front-end Sênior", "Sênior", "nuvem-rosa", false));

        assertEquals("3", trocada.id());
        assertEquals("Sênior", trocada.senioridade());
        assertEquals("Pessoa Desenvolvedora Front-end Sênior", servico.buscarPorId("3").titulo());
        assertEquals(12, servico.listar().size());
    }

    @Test
    @DisplayName("trocar mantendo o mesmo título da própria vaga não conta como duplicidade")
    void trocarMesmoTituloDaPropriaVaga() {
        VagaResposta trocada = servico.trocar("2", entrada("Estágio em Front-end", "Estágio", "mareh-digital", true));
        assertEquals("2", trocada.id());
    }

    @Test
    @DisplayName("trocar para título já usado por outra vaga da empresa lança VagaDuplicadaException")
    void trocarGeraDuplicidade() {
        // 1 e 4 são de empresas diferentes; muda a vaga 6 (aurora-tech) para o título da vaga 1 (aurora-tech).
        assertThrows(VagaDuplicadaException.class,
                () -> servico.trocar("6", entrada("Pessoa Desenvolvedora Front-end Júnior", "Pleno", "aurora-tech", false)));
    }

    @Test
    @DisplayName("trocar vaga inexistente lança VagaNaoEncontradaException")
    void trocarInexistente() {
        assertThrows(VagaNaoEncontradaException.class, () -> servico.trocar("999", valida()));
    }

    @Test
    @DisplayName("trocar com id em branco lança RegraDeNegocioException")
    void trocarIdEmBranco() {
        assertThrows(RegraDeNegocioException.class, () -> servico.trocar(" ", valida()));
    }

    @Test
    @DisplayName("trocar com dados inválidos não altera a vaga existente")
    void trocarInvalidoNaoAltera() {
        String tituloAntes = servico.buscarPorId("5").titulo();
        assertThrows(RegraDeNegocioException.class,
                () -> servico.trocar("5", entrada("Dev", "Júnior", "tucano-systems", true)));
        assertEquals(tituloAntes, servico.buscarPorId("5").titulo());
    }

    // ------------------------------------------------------------- remoção

    @Test
    @DisplayName("apagar remove a vaga; depois dela, buscar dá 404")
    void apagarRemove() {
        servico.apagar("12");

        assertEquals(11, servico.listar().size());
        assertThrows(VagaNaoEncontradaException.class, () -> servico.buscarPorId("12"));
    }

    @Test
    @DisplayName("apagar duas vezes: na segunda a vaga não existe mais")
    void apagarDuasVezes() {
        servico.apagar("7");
        assertThrows(VagaNaoEncontradaException.class, () -> servico.apagar("7"));
    }

    @Test
    @DisplayName("apagar com id nulo lança RegraDeNegocioException")
    void apagarIdNulo() {
        assertThrows(RegraDeNegocioException.class, () -> servico.apagar(null));
    }

    // ------------------------------------------------------------ segurança

    @Test
    @DisplayName("a resposta nunca expõe a descrição (VagaResposta não tem esse campo)")
    void respostaNaoExpoeDescricao() {
        Set<String> campos = new java.util.HashSet<>();
        for (var c : VagaResposta.class.getRecordComponents()) {
            campos.add(c.getName());
        }
        assertFalse(campos.contains("descricao"));
        assertTrue(campos.contains("id"));
    }

    @Test
    @DisplayName("criações concorrentes do mesmo título: só uma passa, as demais são duplicidade")
    void criacaoConcorrente() throws Exception {
        int tarefas = 20;
        ExecutorService pool = Executors.newFixedThreadPool(tarefas);
        CountDownLatch largada = new CountDownLatch(1);
        AtomicInteger criadas = new AtomicInteger();
        AtomicInteger duplicadas = new AtomicInteger();
        Set<String> ids = ConcurrentHashMap.newKeySet();

        for (int i = 0; i < tarefas; i++) {
            pool.submit(() -> {
                try {
                    largada.await();
                    ids.add(servico.criar(valida()).id());
                    criadas.incrementAndGet();
                } catch (VagaDuplicadaException e) {
                    duplicadas.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        largada.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));

        assertEquals(1, criadas.get());
        assertEquals(tarefas - 1, duplicadas.get());
        assertEquals(13, servico.listar().size());
        assertNotEquals(0, ids.size());
    }
}
