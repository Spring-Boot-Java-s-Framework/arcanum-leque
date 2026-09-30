package br.edu.faculdade.pessoa;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
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
    public ResponseEntity<PessoaRespostaDTO> atualizarPessoa(@Validated @RequestBody PessoaEntradaDTO pessoaEntradaDTO, @PathVariable String id){
        return pessoaService.atualizarPessoa(pessoaEntradaDTO, id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PessoaRespostaDTO> criarPessoa(@Validated @RequestBody PessoaEntradaDTO pessoaEntradaDTO, UriComponentsBuilder uriBuilder) {
        PessoaRespostaDTO pessoaCriada = pessoaService.criarPessoa(pessoaEntradaDTO);
        URI uri = uriBuilder.path("/pessoa/{id}").buildAndExpand(pessoaCriada.id()).toUri();
        return ResponseEntity.created(uri).body(pessoaCriada);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removePessoa(@PathVariable String id){
        return pessoaService.removePessoa(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}