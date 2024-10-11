package com.rms.ors.application.domain;

import com.rms.ors.shared.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Status applicationStatus;
    private String rejectionMessage;


    @OneToOne(cascade = CascadeType.ALL)
    private PersonalInformation personalInformation;

    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FamilyMember> familyMemberList;

    @OneToOne(cascade = CascadeType.ALL)
    private OtherInformation otherInformation ;


    @OneToMany(mappedBy = "application")
    private List<Verification> verificationList;

    @OneToMany(mappedBy = "application")
    private List<Document> documentList;


    @CreatedBy
    @Column(nullable = false, updatable = false)
    private Long submittedBy;

    @LastModifiedBy
    @Column(insertable = false)
    private Long LastReviewedBy;


    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedAt;
}
