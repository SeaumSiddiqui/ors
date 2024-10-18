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
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;

    private String accountTitle;
    private String accountNumber;
    private String bankTitle;
    private String branch;
    private String routingNumber;
}
