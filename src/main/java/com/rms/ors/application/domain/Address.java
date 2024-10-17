package com.rms.ors.application.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DynamicInsert
@DynamicUpdate
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // present address
    private String presentVillage;
    private String presentUnion;
    private String presentSubDistrict;
    private String presentDistrict;
    private String presentLocation;

    // permanent address
    private String permanentVillage;
    private String permanentUnion;
    private String permanentSubDistrict;
    private String permanentDistrict;
    private String permanentLocation;

}
