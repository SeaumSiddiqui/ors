package com.rms.ors.application.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    private PrimaryInformation primaryInformation;

    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<FamilyMember> familyMemberList;

    @OneToOne(cascade = CascadeType.ALL)
    private BasicInformation basicInformation;


    @OneToMany(mappedBy = "application")
    private List<Verification> verificationList; // TODO-> remove:: this should be filled by user.getSign()

    @OneToOne(cascade = CascadeType.ALL)
    private Transaction transaction;

    @OneToMany(mappedBy = "application")
    private List<Document> documentList;


    @CreatedBy
    @Column(nullable = false, updatable = false)
    private Long submittedBy;

    @LastModifiedBy
    @Column(insertable = false)
    private Long LastReviewedBy;


    @CreatedDate
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @Column(insertable = false)
    private LocalDateTime lastModifiedAt;
}
