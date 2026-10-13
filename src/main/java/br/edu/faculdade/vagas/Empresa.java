package br.edu.faculdade.vagas;

public record Empresa(
    String id,
    String nome,
    String slug,
    String site,
    String descricao
) { }