package br.edu.faculdade.person;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PersonRepository {
    List<PersonDTO> allPerson(){
        PersonDTO pessoaDTO = new PersonDTO(1, "Andre", "andre@neckel.tech", "Backend", Seniority.SENIOR);
        return List.of(pessoaDTO);
    }

    PersonDTO findPersonById(Integer id){
        return null;
    }
}
