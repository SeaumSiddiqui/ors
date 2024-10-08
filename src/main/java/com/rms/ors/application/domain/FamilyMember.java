package com.rms.ors.application.domain;

import com.rms.ors.shared.MaritalStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FamilyMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int age;
    private int siblingsGrade;
    private String occupation;
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;
}
