package br.edu.faculdade.person;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {
    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<PersonDTO> allPerson() {
        return personRepository.allPerson();
    }

    public PersonDTO findPersonById(Integer id) {
        return personRepository.findPersonById(id);
    }
}
