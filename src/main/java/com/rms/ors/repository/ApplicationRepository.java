package com.rms.ors.repository;

import com.rms.ors.domain.Application;
import com.rms.ors.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    // get all application create by a specific user
    List<Application> findBySubmittedBy(User submittedBy);

    // get an application created by a user
    Optional<Application> findByIdAndSubmittedBy(User submittedBy, Long applicationId);


}
