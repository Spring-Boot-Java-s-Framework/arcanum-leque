package br.edu.faculdade.vaga.dto;

import br.edu.faculdade.vaga.service.VagaService;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Contrato de entrada: o que o cliente manda no POST e no PUT.
 *
 * Não tem {@code id}: quem inventa o id é a API, e o cliente não tem onde
 * escrever um. Não é uma regra checando nada, o campo simplesmente não existe e
 * o Jackson descarta o que sobra antes de o nosso código ver.
 *
 * As anotações de validação aqui cuidam só do formato (campo ausente
 * ou em branco, tamanho máximo) e só valem com {@code @Valid} no controller;
 * falha vira HTTP 400. O significado dos dados (senioridade existente,
 * slug válido, regra do iniciante, duplicidade) é verificado pelo
 * {@link VagaService}.
 *
 * {@code aceitaIniciante} é {@code Boolean}, e não {@code boolean}, de
 * propósito: com o primitivo, um corpo sem o campo pode morrer no parse do
 * Jackson (conforme a versão) com 400 genérico, em vez do {@code problem+json}
 * com a lista de campos. Com o objeto, "não respondeu" chega como {@code null}
 * e o service o trata como {@code false}.
 *
 * @param titulo          nome da vaga (obrigatório, até 120 caracteres)
 * @param area            área de atuação (obrigatória)
 * @param senioridade     Estágio, Júnior, Pleno ou Sênior (obrigatória)
 * @param empresaSlug     identificador textual da empresa (obrigatório)
 * @param descricao       texto longo (opcional)
 * @param local           modalidade e cidade (opcional)
 * @param aceitaIniciante se aceita iniciante (opcional; ausente vale {@code false})
 * @author Carlos Neto
 * @since 2026-10-13
 */
public record VagaEntrada(
        @NotBlank(message = "o título é obrigatório")
        @Size(max = 120, message = "o título pode ter no máximo 120 caracteres")
        String titulo,

        @NotBlank(message = "a área é obrigatória")
        String area,

        @NotBlank(message = "a senioridade é obrigatória")
        String senioridade,

        @NotBlank(message = "a empresa é obrigatória")
        String empresaSlug,

        String descricao,
        String local,
        Boolean aceitaIniciante
) { }
