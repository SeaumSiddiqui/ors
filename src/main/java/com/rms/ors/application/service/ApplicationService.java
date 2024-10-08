package com.rms.ors.application.service;

import com.rms.ors.application.domain.Application;
import com.rms.ors.shared.Role;
import com.rms.ors.shared.Status;
import com.rms.ors.user.domain.User;
import com.rms.ors.exception.ResourceNotFoundException;
import com.rms.ors.exception.UnauthorizedAccessException;
import com.rms.ors.exception.UserNotFoundException;
import com.rms.ors.application.repository.ApplicationRepository;
import com.rms.ors.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;


    public List<Application> getAllApplicationsByUser(Long userId) {
        return applicationRepository.findBySubmittedBy(userId);
    }


    public Application getApplicationsByIdAndUser(Long applicationId, Long userId) {

        return applicationRepository.findByIdAndSubmittedBy(applicationId, userId)
                .orElseThrow(()-> new ResourceNotFoundException("No content found for this user"));
    }


    public Application createApplications(Application application) {
        application.setApplicationStatus(Status.INCOMPLETE);
        return applicationRepository.save(application);
        // after submitting application status will be set on Status.PENDING
    }


    public Application updateApplications(Application updatedApplication, Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(()-> new ResourceNotFoundException("No content found for this user"));
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!hasUpdatePermission(application.getApplicationStatus(), username)) {
            throw new UnauthorizedAccessException("You do not have permission to update this content");
        }
        updateApplicationFields(application, updatedApplication);
        return applicationRepository.save(application);
    }


    private boolean hasUpdatePermission(Status applicationStatus, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(()-> new UserNotFoundException("User <%s> not found".formatted(username)));

        if (user.getRole().equals(Role.ADMIN)) {
            return true;
        }
        else if (user.getRole().equals(Role.MANAGEMENT) && applicationStatus.equals(Status.PENDING)) {
            return true;
        }
        else return user.getRole().equals(Role.USER) && (applicationStatus.equals(Status.INCOMPLETE) || applicationStatus.equals(Status.REJECTED));
    }


    private void updateApplicationFields(Application existingApplication, Application updatedApplication) {
        if (updatedApplication.getPersonalInformation() != null) {
            existingApplication.setPersonalInformation(updatedApplication.getPersonalInformation());
        }
    }

    /* ------------------------- MANAGEMENT & ADMIN Exclusive --------------------------- */

    public List<Application> getApplicationsByStatus(String status) {
        return applicationRepository.findByApplicationStatus(Status.valueOf(status.toUpperCase()));
    }


    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }


    public Application getApplicationsById(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(()-> new UserNotFoundException("User with Id <%s> not found".formatted(applicationId)));
    }

    public void deleteApplications(Long applicationId) {
        if (!applicationRepository.existsById(applicationId)) {
            throw new ResourceNotFoundException("No content found to delete");
        }
        applicationRepository.deleteById(applicationId);
    }

}
