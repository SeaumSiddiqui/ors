package com.rms.ors.service;

import com.rms.ors.domain.Application;
import com.rms.ors.domain.Status;
import com.rms.ors.domain.User;
import com.rms.ors.exception.ResourceNotFoundException;
import com.rms.ors.exception.UserNotFoundException;
import com.rms.ors.repository.ApplicationRepository;
import com.rms.ors.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public List<Application> getAllApplicationsByUserId(String username) {
        return applicationRepository.findBySubmittedBy(getUser(username));
    }


    public Application getAllApplicationsByIdAndUserId(String username, Long applicationId) {

        return applicationRepository.findByIdAndSubmittedBy(getUser(username), applicationId)
                .orElseThrow(()-> new ResourceNotFoundException("No application found for this user"));
    }


    public Application createApplication(Application application) {
        application.setApplicationStatus(Status.INCOMPLETE);
        return applicationRepository.save(application);
        // on submit application status will be set on Status.PENDING
    }

    // TODO -> need to check if it work on postman
    public Application updateApplication(Application updatedApplication, Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(()-> new ResourceNotFoundException("No Application found for this user"));
        if (!(application.getApplicationStatus().equals(Status.INCOMPLETE) || application.getApplicationStatus().equals(Status.REJECTED))) {
            throw new IllegalStateException("Only incomplete or rejected applications can be updated");
        }
        updateApplicationFields(application, updatedApplication);
        return applicationRepository.save(application);
    }


    private void updateApplicationFields(Application existingApplication, Application updatedApplication) {
        if (updatedApplication.getPersonalInfo() != null) {
            existingApplication.setPersonalInfo(updatedApplication.getPersonalInfo());
        }
    }

    // TODO -> make all UserNotFoundException return this error message
    private User getUser(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(()-> new UserNotFoundException("User <%s> not found".formatted(username)));
    }

}
