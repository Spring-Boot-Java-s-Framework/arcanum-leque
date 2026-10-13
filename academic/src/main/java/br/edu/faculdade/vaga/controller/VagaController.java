package br.edu.faculdade.vaga.controller;

import br.edu.faculdade.vaga.dto.VagaEntrada;
import br.edu.faculdade.vaga.dto.VagaResposta;
import br.edu.faculdade.vaga.model.Vaga;
import br.edu.faculdade.vaga.service.VagaService;
import br.edu.faculdade.vaga.exception.RegraDeNegocioException;
import br.edu.faculdade.vaga.exception.VagaDuplicadaException;
import br.edu.faculdade.vaga.exception.VagaNaoEncontradaException;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Porta de entrada HTTP da frente Vaga: recebe o pedido, chama o
 * {@link VagaService} e devolve a resposta. Nada além disso.
 *
 * Cada método tem uma linha. Não há {@code if}, {@code for} nem regra aqui:
 * quando algo dá errado, o service lança uma exceção
 * ({@link VagaNaoEncontradaException}, {@link VagaDuplicadaException} ou
 * {@link RegraDeNegocioException}) e o {@code TratadorDeErros} a converte em
 * 404, 409 ou 422. Assim o controller não precisa montar resposta de erro.
 *
 * Rotas:
 *   GET    /vagas        200  lista as vagas
 *   GET    /vagas/{id}   200  uma vaga           | 404 não existe
 *   POST   /vagas        201  cria (Location)    | 400 formato | 409 duplicada | 422 regra
 *   PUT    /vagas/{id}   200  troca por completo | 400 | 404 | 409 | 422
 *   DELETE /vagas/{id}   204  apaga              | 404
 *
 * Só trafegam {@link VagaEntrada} (entra) e {@link VagaResposta} (sai); o
 * modelo {@link Vaga} fica do lado de dentro.
 *
 * @author Carlos Neto
 * @since 2026-10-13
 */
@RestController
@RequestMapping("/vagas")
public class VagaController {

    private final VagaService servico;

    public VagaController(VagaService servico) {
        this.servico = servico;
    }

    @GetMapping
    public List<VagaResposta> listar() {
        return servico.listar();
    }

    @GetMapping("/{id}")
    public VagaResposta porId(@PathVariable String id) {
        return servico.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<VagaResposta> criar(@Valid @RequestBody VagaEntrada entrada) {
        VagaResposta salva = servico.criar(entrada);
        URI onde = URI.create("/vagas/" + salva.id());
        return ResponseEntity.created(onde).body(salva);
    }

    @PutMapping("/{id}")
    public VagaResposta trocar(@PathVariable String id,
                               @Valid @RequestBody VagaEntrada nova) {
        return servico.trocar(id, nova);
    }

    // ResponseEntity<Void>: não há corpo nenhum para tipar. Devolver o objeto
    // recém-apagado sugeriria que ele ainda está lá.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable String id) {
        servico.apagar(id);
        return ResponseEntity.noContent().build();
    }
}
