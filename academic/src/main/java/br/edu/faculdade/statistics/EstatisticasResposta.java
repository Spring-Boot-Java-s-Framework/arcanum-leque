package br.edu.faculdade.statistics;

import java.util.Map;

// o que o GET /estatisticas devolve
public record EstatisticasResposta(
        int totalDeVagas,
        long aceitamIniciante,
        Map<String, Long> porArea,
        Map<String, Long> porSenioridade) {
}
