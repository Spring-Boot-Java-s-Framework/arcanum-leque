package br.edu.faculdade.person;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/pessoas")
@RestController
public class PersonController {
    private final PersonService pessoaService;

    public PersonController(PersonService pessoaService) {
        this.pessoaService = pessoaService;
    }

    @GetMapping
    public List<PersonDTO> allPerson(){
        return pessoaService.allPerson();
    }

    @GetMapping("/{id}")
    public PersonDTO findPersonById(@PathVariable Integer id){
        return pessoaService.findPersonById(id);
    }
}