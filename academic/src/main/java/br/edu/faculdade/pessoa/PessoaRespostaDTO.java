package br.edu.faculdade.pessoa;

public record PessoaRespostaDTO(String id,
                                String nome,
                                String email,
                                String area,
                                Senioridade senioridade) {

    public static PessoaRespostaDTO toRespostaDTO(Pessoa pessoa) {
        return new PessoaRespostaDTO(
                pessoa.getId(),
                pessoa.getNome(),
                pessoa.getEmail(),
                pessoa.getArea(),
                pessoa.getSenioridade());
    }
}
