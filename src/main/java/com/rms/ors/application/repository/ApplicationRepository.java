package com.rms.ors.application.repository;

import com.rms.ors.application.domain.Application;
import com.rms.ors.shared.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;

public interface ApplicationRepository extends JpaRepository<Application, Long>, JpaSpecificationExecutor<Application> {

    /**
    // get all applications by user
    Page<Application> findBySubmittedBy(Long submittedBy, Pageable pageable);
    // get all applications by status
    Page<Application> findByApplicationStatus(Status applicationStatus, Pageable pageable);
    // get all applications by user and status
    Page<Application> findBySubmittedByAndApplicationStatus(Long submittedBy, Status applicationStatus, Pageable pageable);
     */


    // today application count by status // TODO date might throw error, replace with createdAt
    int countByCreatedAtAfterAndApplicationStatus(LocalDateTime date, Status applicationStatus);
    // total application count by status
    int countByApplicationStatus(Status applicationStatus);
    // total application count by user
    int countBySubmittedByAndApplicationStatus(Long submittedBy, Status applicationStatus);
}
