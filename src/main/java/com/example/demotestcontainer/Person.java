package com.example.demotestcontainer;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Person implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String name;
}
