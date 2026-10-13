package br.edu.faculdade.vaga.model;

import br.edu.faculdade.vaga.service.VagaService;
import br.edu.faculdade.vaga.repository.VagaRepository;
import br.edu.faculdade.vaga.dto.VagaEntrada;
import br.edu.faculdade.vaga.dto.VagaResposta;

/**
 * Modelo interno da frente Vaga: o dado como o sistema o guarda.
 *
 * É um {@code record}, ou seja, só dado e imutável. Por isso não leva
 * anotação de estereótipo ({@code @Service}, {@code @Repository}…): esses
 * marcam classes que fazem algo, e um record apenas guarda. Também não tem
 * lógica: quem gera o {@code id} e valida os campos é o {@link VagaService}, e
 * quem guarda é o {@link VagaRepository}.
 *
 * Esta é a única representação que tem {@code id} e
 * {@code descricao} ao mesmo tempo. Ela circula do service para o repository e
 * nunca chega ao cliente: a API fala {@link VagaEntrada} e
 * {@link VagaResposta}.
 *
 * O {@code id} é {@code String} porque o que vem da URL é texto; assim a
 * comparação é texto com texto. Quando o banco entrar (aula 05), vira
 * {@code Long} e a conversão passa a ser do Spring.
 *
 * @param id              identificador (UUID), gerado pelo service
 * @param titulo          nome da vaga
 * @param descricao       texto longo; opcional e nunca exposto na resposta
 * @param area            área de atuação (Front-end, Back-end, Dados…)
 * @param senioridade     forma canônica: Estágio, Júnior, Pleno ou Sênior
 * @param local           modalidade e cidade; opcional
 * @param aceitaIniciante {@code true} se a vaga aceita quem está começando
 * @param empresaSlug     identificador textual da empresa (ex.: {@code aurora-tech})
 * @author Carlos Neto
 * @since 2026-10-13
 */
public record Vaga(
    String id,
    String titulo,
    String descricao,
    String area,
    String senioridade,
    String local,
    boolean aceitaIniciante,
    String empresaSlug
) { }
