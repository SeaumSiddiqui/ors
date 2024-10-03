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
public class FamilyInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate dod;
    private String causeOfDeath;
    @Enumerated(EnumType.STRING)
    private MothersStatus mothersStatus;
    private String occupation; // TODO -> will set it to an enum
    private String earning; // TODO -> data type might change
    private String prop; // TODO -> variable name and data type might change
    private String institution;
    private int $class; // TODO -> need to change the name
    // TODO -> complete the class
}
