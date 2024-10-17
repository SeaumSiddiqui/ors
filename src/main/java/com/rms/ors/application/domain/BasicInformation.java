package com.rms.ors.application.domain;

import com.rms.ors.shared.HouseType;
import com.rms.ors.shared.PhysicalCondition;
import com.rms.ors.shared.ResidenceStatus;
import jakarta.persistence.*;
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
public class BasicInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PhysicalCondition physicalCondition;

    private boolean hasCriticalIllness;
    private String typeOfIllness;
    private boolean isResident;

    @Enumerated(EnumType.STRING)
    private ResidenceStatus residenceStatus;
    @Enumerated(EnumType.STRING)
    private HouseType houseType;

    // household details
    private int bedroom;
    private int balcony;
    private int kitchen;
    private int store;
    private int room;
    private boolean hastTubeWell;

    private String guardiansName;
    private String guardiansRelation;
    private String NID;
    private String cell1;
    private String cell2;

}
