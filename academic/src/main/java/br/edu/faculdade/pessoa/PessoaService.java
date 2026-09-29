package br.edu.faculdade.pessoa;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PessoaService {
    private final PessoaRepository pessoaRepository;

    public PessoaService(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    public List<PessoaRespostaDTO> todasPessoas() {
        return pessoaRepository.todasPessoas();
    }

    public Optional<PessoaRespostaDTO> buscaPessoaPorId(String id) {
        return pessoaRepository.buscaPorId(id);
    }

    public Optional<Pessoa> atualizarPessoa(PessoaEntradaDTO dto, String id) {
        Pessoa pessoa = new Pessoa(
                id,
                dto.nome(), dto.email(), dto.area(), dto.senioridade());

        return pessoaRepository.atualizarPessoa(pessoa, id);
    }

    public PessoaRespostaDTO criarPessoa(PessoaEntradaDTO dto) {
        Pessoa pessoa = new Pessoa(
                UUID.randomUUID().toString(),
                dto.nome(), dto.email(), dto.area(), dto.senioridade());

        pessoa =  pessoaRepository.criarPessoa(pessoa);

        return PessoaRespostaDTO.toRespostaDTO(pessoa);
    }

    public boolean removePessoa(String id) {
        return pessoaRepository.removePessoa(id);
    }
}
