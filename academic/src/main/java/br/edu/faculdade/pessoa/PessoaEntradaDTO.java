package br.edu.faculdade.pessoa;

public record PessoaDTO(String id,
                        String nome,
                        String email,
                        String area,
                        Senioridade senioridade) {
}
