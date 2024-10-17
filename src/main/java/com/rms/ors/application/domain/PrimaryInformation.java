package com.rms.ors.application.domain;

import com.rms.ors.shared.Gender;
import com.rms.ors.shared.MothersStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DynamicInsert
@DynamicUpdate
public class PrimaryInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String bcRegistration;
    private String fathersName;
    private String mothersName;
    private LocalDate dob;
    private String placeOfBirth;
    private LocalDate dod;
    private String causeOfDeath;

    @Enumerated(EnumType.STRING)
    private MothersStatus mothersStatus;
    private String mothersOccupation; // TODO -> will set it to an enum
    private int annualIncome;
    private String fixedAsset;
    private String academicInstitution;
    private int grade;
    @Enumerated(EnumType.STRING)
    private Gender gender;
}
