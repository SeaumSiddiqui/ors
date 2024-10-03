package com.rms.ors.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Beneficiary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Status applicationStatus;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "personal_info_id")
    private PersonalInfo personalInfo;

    // TODO -> add other information tables

    @OneToMany(mappedBy = "beneficiary")
    private List<Document> documentList;

    @ManyToOne
    @JoinColumn(name = "submitted_by")
    private User submittedBy;
    @ManyToOne
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    private String rejectionMessage;
    // TODO -> add creation and last-update stamp
}
