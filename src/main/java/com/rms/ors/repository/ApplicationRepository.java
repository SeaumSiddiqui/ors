package com.rms.ors.repository;

import com.rms.ors.domain.Application;
import com.rms.ors.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    // get all applications by user
    List<Application> findBySubmittedBy(Long submittedBy);

    // get an application by user
    Optional<Application> findByIdAndSubmittedBy(Long applicationId, Long submittedBy);

    // get applications by application status
    List<Application> findByApplicationStatus(Status applicationStatus);


}
