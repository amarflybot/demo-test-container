package com.example.demotestcontainer;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
@EnableCaching
public class DemoTestContainerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoTestContainerApplication.class, args);
    }

    /*@Bean
    public ApplicationRunner applicationRunner(PersonRepo personRepo) {
        return args -> Arrays.asList("Amar","Amit","Ankush","Rohit")
                .forEach(name -> {
                    personRepo.save(new Person(name));
                });
    }*/
}
