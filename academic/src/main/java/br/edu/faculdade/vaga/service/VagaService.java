package br.edu.faculdade.vaga.service;

import br.edu.faculdade.vaga.dto.VagaEntrada;
import br.edu.faculdade.vaga.dto.VagaResposta;
import br.edu.faculdade.vaga.model.Vaga;
import br.edu.faculdade.vaga.repository.VagaRepository;
import br.edu.faculdade.vaga.exception.RegraDeNegocioException;
import br.edu.faculdade.vaga.exception.VagaDuplicadaException;
import br.edu.faculdade.vaga.exception.VagaNaoEncontradaException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Camada de serviço da frente Vaga: é aqui que moram as regras de negócio, a
 * validação semântica e a tradução entre os três contratos.
 *
 * O fluxo é sempre o mesmo: o controller entrega um {@link VagaEntrada}; o
 * service o valida, normaliza e transforma em {@link Vaga} (o modelo interno);
 * o {@link VagaRepository} guarda; e o resultado volta como
 * {@link VagaResposta}. O controller nunca vê {@code Vaga} e o repository nunca
 * vê as regras.
 *
 * Regras aplicadas:
 * - Identificador: nunca nulo nem em branco; vaga inexistente gera
 *   {@link VagaNaoEncontradaException} (HTTP 404).
 * - Texto: todos os campos têm espaços das pontas removidos; título
 *   de 5 a 120 caracteres, área até 60, local até 80, descrição até 2000.
 * - Senioridade: só {@code Estágio}, {@code Júnior}, {@code Pleno}
 *   ou {@code Sênior}; aceita grafia sem acento e qualquer caixa, e grava a
 *   forma canônica.
 * - Empresa: o slug precisa ser minúsculo, com letras, números e
 *   hifens ({@code aurora-tech}).
 * - Iniciante: vaga {@code Pleno} ou {@code Sênior} não pode aceitar
 *   iniciante; quando o campo não é enviado, vale {@code false}.
 * - Duplicidade: a mesma empresa não pode ter duas vagas com o mesmo
 *   título (ignorando caixa e acento); gera {@link VagaDuplicadaException}
 *   (HTTP 409).
 * - Concorrência: criar, trocar e apagar são sincronizados, para que
 *   a checagem de duplicidade e a gravação aconteçam como um passo só.
 *
 * @author Carlos Neto
 * @since 2026-10-13
 */
@Service
public class VagaService {

    private static final Logger log = LoggerFactory.getLogger(VagaService.class);

    static final int TITULO_MIN = 5;
    static final int TITULO_MAX = 120;
    static final int AREA_MAX = 60;
    static final int LOCAL_MAX = 80;
    static final int DESCRICAO_MAX = 2000;

    private static final Pattern SLUG = Pattern.compile("^[a-z0-9]+(-[a-z0-9]+)*$");

    /** Chave sem acento e em minúsculas, apontando para a grafia canônica. */
    private static final Map<String, String> SENIORIDADES = Map.of(
            "estagio", "Estágio",
            "junior", "Júnior",
            "pleno", "Pleno",
            "senior", "Sênior");

    private static final List<String> SENIORIDADES_SEM_INICIANTE = List.of("Pleno", "Sênior");

    private final VagaRepository repositorio;

    public VagaService(VagaRepository repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Lista todas as vagas, já no formato de saída (sem a descrição).
     */
    public List<VagaResposta> listar() {
        return repositorio.todas().stream()
                .map(this::paraResposta)
                .toList();
    }

    /**
     * Busca uma vaga pelo identificador.
     *
     * @throws RegraDeNegocioException     se o id for nulo ou em branco
     * @throws VagaNaoEncontradaException  se não existir vaga com esse id
     */
    public VagaResposta buscarPorId(String id) {
        return paraResposta(exigirExistente(exigirId(id)));
    }

    /**
     * Cria uma vaga nova. O id é gerado aqui, nunca pelo cliente.
     *
     * @throws RegraDeNegocioException  se algum dado violar as regras de negócio
     * @throws VagaDuplicadaException   se a empresa já tiver vaga com o mesmo título
     */
    public synchronized VagaResposta criar(VagaEntrada entrada) {
        Vaga nova = montar(UUID.randomUUID().toString(), entrada);
        exigirSemDuplicidade(nova);

        Vaga salva = repositorio.salvar(nova);
        log.info("Vaga criada: id={} empresa={}", salva.id(), salva.empresaSlug());
        return paraResposta(salva);
    }

    /**
     * Substitui por completo a vaga de um id existente, preservando o id.
     *
     * @throws RegraDeNegocioException     se o id ou algum dado for inválido
     * @throws VagaNaoEncontradaException  se não existir vaga com esse id
     * @throws VagaDuplicadaException      se a troca colidir com outra vaga da empresa
     */
    public synchronized VagaResposta trocar(String id, VagaEntrada nova) {
        exigirExistente(exigirId(id));

        Vaga comId = montar(id, nova);
        exigirSemDuplicidade(comId);

        Vaga trocada = repositorio.trocar(id, comId)
                .orElseThrow(() -> new VagaNaoEncontradaException(id));
        log.info("Vaga trocada: id={}", id);
        return paraResposta(trocada);
    }

    /**
     * Remove a vaga de um id existente.
     *
     * @throws RegraDeNegocioException     se o id for nulo ou em branco
     * @throws VagaNaoEncontradaException  se não existir vaga com esse id
     */
    public synchronized void apagar(String id) {
        exigirId(id);
        if (!repositorio.remover(id)) {
            throw new VagaNaoEncontradaException(id);
        }
        log.info("Vaga apagada: id={}", id);
    }

    // ---------------------------------------------------------------- regras

    private String exigirId(String id) {
        if (id == null || id.isBlank()) {
            throw new RegraDeNegocioException("O id da vaga é obrigatório.");
        }
        return id.trim();
    }

    private Vaga exigirExistente(String id) {
        return repositorio.porId(id).orElseThrow(() -> new VagaNaoEncontradaException(id));
    }

    /**
     * Valida e normaliza a entrada, devolvendo o modelo interno já com id.
     */
    private Vaga montar(String id, VagaEntrada entrada) {
        if (entrada == null) {
            throw new RegraDeNegocioException("Os dados da vaga são obrigatórios.");
        }

        String titulo = limitar("título", entrada.titulo(), TITULO_MIN, TITULO_MAX);
        String area = limitar("área", entrada.area(), 1, AREA_MAX);
        String senioridade = senioridadeCanonica(entrada.senioridade());
        String empresaSlug = slugValido(entrada.empresaSlug());
        String local = opcional("local", entrada.local(), LOCAL_MAX);
        String descricao = opcional("descrição", entrada.descricao(), DESCRICAO_MAX);

        // Boolean.TRUE.equals: null, que é como "não respondeu" chega, vira false.
        boolean aceitaIniciante = Boolean.TRUE.equals(entrada.aceitaIniciante());
        if (aceitaIniciante && SENIORIDADES_SEM_INICIANTE.contains(senioridade)) {
            throw new RegraDeNegocioException(
                    "Vaga de senioridade " + senioridade + " não pode aceitar iniciante.");
        }

        return new Vaga(id, titulo, descricao, area, senioridade, local,
                aceitaIniciante, empresaSlug);
    }

    private String limitar(String campo, String valor, int minimo, int maximo) {
        String texto = valor == null ? "" : valor.trim();
        if (texto.length() < minimo) {
            throw new RegraDeNegocioException(
                    "O campo '" + campo + "' precisa ter pelo menos " + minimo + " caractere(s).");
        }
        if (texto.length() > maximo) {
            throw new RegraDeNegocioException(
                    "O campo '" + campo + "' não pode passar de " + maximo + " caracteres.");
        }
        return texto;
    }

    /** Campo opcional: vazio vira null; se vier preenchido, respeita o limite. */
    private String opcional(String campo, String valor, int maximo) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return limitar(campo, valor, 1, maximo);
    }

    private String senioridadeCanonica(String valor) {
        String canonica = SENIORIDADES.get(semAcento(valor));
        if (canonica == null) {
            throw new RegraDeNegocioException(
                    "Senioridade inválida. Use Estágio, Júnior, Pleno ou Sênior.");
        }
        return canonica;
    }

    private String slugValido(String valor) {
        String slug = valor == null ? "" : valor.trim().toLowerCase(Locale.ROOT);
        if (!SLUG.matcher(slug).matches()) {
            throw new RegraDeNegocioException(
                    "O slug da empresa deve ter só letras minúsculas, números e hifens (ex.: aurora-tech).");
        }
        return slug;
    }

    private void exigirSemDuplicidade(Vaga candidata) {
        String tituloChave = semAcento(candidata.titulo());
        boolean existe = repositorio.todas().stream()
                .filter(v -> !v.id().equals(candidata.id()))
                .anyMatch(v -> v.empresaSlug().equals(candidata.empresaSlug())
                        && semAcento(v.titulo()).equals(tituloChave));
        if (existe) {
            throw new VagaDuplicadaException(candidata.titulo(), candidata.empresaSlug());
        }
    }

    /** Minúsculas, sem acento e sem espaços nas pontas: serve de chave de comparação. */
    private static String semAcento(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }

    // Uma função só, usada por todos os métodos que devolvem vaga.
    private VagaResposta paraResposta(Vaga v) {
        return new VagaResposta(v.id(), v.titulo(), v.area(),
                v.senioridade(), v.local(), v.aceitaIniciante(), v.empresaSlug());
    }
}
