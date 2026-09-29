package br.edu.faculdade.pessoa;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/pessoa")
@RestController
public class PessoaController {
    private final PessoaService pessoaService;

    public PessoaController(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    @GetMapping
    public List<PessoaRespostaDTO> todasPessoas(){
        return pessoaService.todasPessoas();
    }

     @GetMapping("/{id}")
     public ResponseEntity<PessoaRespostaDTO> buscaPessoaPorId(@PathVariable String id) {
         return pessoaService.buscaPessoaPorId(id)
             .map(ResponseEntity::ok)
             .orElse(ResponseEntity.notFound().build());
     }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<PessoaRespostaDTO> atualizarPessoa(@RequestBody PessoaEntradaDTO pessoaEntradaDTO, @PathVariable String id){
        return pessoaService.atualizarPessoa(pessoaEntradaDTO, id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PessoaRespostaDTO criarPessoa(@Validated @RequestBody PessoaEntradaDTO pessoaEntradaDTO){
        return pessoaService.criarPessoa(pessoaEntradaDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removePessoa(@PathVariable String id){
        return pessoaService.removePessoa(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}