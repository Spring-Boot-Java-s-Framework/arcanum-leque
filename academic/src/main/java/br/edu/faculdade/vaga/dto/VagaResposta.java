package br.edu.faculdade.vaga.dto;

import br.edu.faculdade.vaga.model.Vaga;
import br.edu.faculdade.vaga.service.VagaService;

/**
 * Contrato de saída: o que a API devolve ao cliente.
 *
 * Tem {@code id}, porque quem inventa o id é a API e o cliente precisa dele
 * para chamar {@code /vagas/{id}} depois. Não tem {@code descricao}: a
 * listagem não precisa do texto longo, e a saída poder esconder campo é o
 * ponto desta separação (na aula 10, é ela que impede a senha de uma Pessoa de
 * vazar).
 *
 * O par deste record é {@link VagaEntrada}. Quem converte {@link Vaga} em
 * {@code VagaResposta} é o {@link VagaService}, num método só.
 *
 * @param id              identificador da vaga
 * @param titulo          nome da vaga
 * @param area            área de atuação
 * @param senioridade     Estágio, Júnior, Pleno ou Sênior
 * @param local           modalidade e cidade; pode ser {@code null}
 * @param aceitaIniciante se a vaga aceita quem está começando
 * @param empresaSlug     identificador textual da empresa
 * @author Carlos Neto
 * @since 2026-10-13
 */
public record VagaResposta(
    String id,
    String titulo,
    String area,
    String senioridade,
    String local,
    boolean aceitaIniciante,
    String empresaSlug
) { }
