package br.edu.faculdade.pessoa;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PessoaService {
    private final PessoaRepository pessoaRepository;

    public PessoaService(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    public List<PessoaRespostaDTO> todasPessoas() {
        return pessoaRepository.todasPessoas().stream()
                .map(PessoaRespostaDTO::toRespostaDTO)
                .collect(Collectors.toList());
    }

    public Optional<PessoaRespostaDTO> buscaPessoaPorId(String id) {
        return pessoaRepository.buscaPessoaPorId(id).map(PessoaRespostaDTO::toRespostaDTO);
    }

    public Optional<PessoaRespostaDTO> atualizarPessoa(PessoaEntradaDTO dto, String id) {
        Pessoa pessoa = new Pessoa(
                id,
                dto.nome(), dto.email(), dto.area(), dto.senioridade());

        return pessoaRepository.atualizarPessoa(pessoa, id)
                .map(PessoaRespostaDTO::toRespostaDTO);
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
