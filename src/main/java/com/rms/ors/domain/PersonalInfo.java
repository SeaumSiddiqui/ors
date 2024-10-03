package com.rms.ors.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PersonalInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String fathersName;
    private String mothersName;
    private LocalDate dob;
    private String bcRegistration;
    private String placeOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;
}
