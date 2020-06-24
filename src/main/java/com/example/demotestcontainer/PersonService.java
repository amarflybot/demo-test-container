package com.example.demotestcontainer;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonService {

    private final PersonRepo personRepo;


    public PersonService(PersonRepo personRepo) {
        this.personRepo = personRepo;
    }

    @Cacheable(key = "#id", cacheNames = "personCache")
    public Person getPersonById(Long id){
        final Optional<Person> byId = personRepo.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        } else {
            return null;
        }
    }
}
