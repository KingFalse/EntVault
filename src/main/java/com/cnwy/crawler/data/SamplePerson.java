package com.cnwy.crawler.data;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class SamplePerson extends AbstractEntity {

    private String firstName;
    private String lastName;
    @Email
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String occupation;
    private String role;
    private boolean important;

}
