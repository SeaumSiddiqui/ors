package com.rms.ors.application.service;

import com.rms.ors.application.domain.Application;
import com.rms.ors.application.specification.ApplicationSpecification;
import com.rms.ors.shared.Role;
import com.rms.ors.shared.Status;
import com.rms.ors.user.domain.User;
import com.rms.ors.exception.ResourceNotFoundException;
import com.rms.ors.exception.UnauthorizedAccessException;
import com.rms.ors.exception.UserNotFoundException;
import com.rms.ors.application.repository.ApplicationRepository;
import com.rms.ors.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;


    public Page<Application> getAllApplications(Long submittedBy, Long reviewedBy,
                                                LocalDateTime startedDate, LocalDateTime endDate,
                                                String fullName, String fathersName, String applicationStatus,
                                                String sortField, String sortDirection, int page, int size) {

        Specification<Application> specification = ApplicationSpecification.buildSearchSpecification
                (submittedBy, reviewedBy, startedDate, endDate, fullName, fathersName, applicationStatus);
        // sort direction DESC/ASC
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        // sort field createdAt/lastModifiedAt/applicationStatus...
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        return applicationRepository.findAll(specification, pageable);
    }

    // get all by userId(optional) and status(optional)
    public Page<Application> getAllApplicationsByUser(Long submittedBy, LocalDateTime startDate,
                                                      LocalDateTime endDate, String status, int page, int size) {

        Specification<Application> specification = ApplicationSpecification.buildDashboarrdSpecification(submittedBy, startDate, endDate, status);

        Pageable pageable =PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "applicationStatus"));

        return applicationRepository.findAll(specification, pageable);
    }


    public Application getApplicationsById(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(()-> new ResourceNotFoundException("Content not found"));
    }


    public Application createApplications(Application application) {
        application.setApplicationStatus(Status.INCOMPLETE);
        return applicationRepository.save(application);
    }


    private void updateApplicationFields(Application existingApplication, Application updatedApplication) {
        if (updatedApplication.getPersonalInformation() != null) {
            existingApplication.setPersonalInformation(updatedApplication.getPersonalInformation());
        }
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


    public Application updateApplications(Application updatedApplication, Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(()-> new ResourceNotFoundException("Content not found"));
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!hasUpdatePermission(application.getApplicationStatus(), username)) {
            throw new UnauthorizedAccessException("You do not have permission to update this content");
        }
        updateApplicationFields(application, updatedApplication);
        return applicationRepository.save(application);
    }


    public void deleteApplications(Long applicationId) {
        if (!applicationRepository.existsById(applicationId)) {
            throw new ResourceNotFoundException("No content found to delete");
        }
        applicationRepository.deleteById(applicationId);
    }


    // service methods for admin dashboard
    public int countByDateAndStatus(LocalDateTime today, Status status) {
        return applicationRepository.countByCreatedAtAfterAndApplicationStatus(today, status);
    }

    public int countByStatus(Status status) {
        return applicationRepository.countByApplicationStatus(status);
    }

    public int countByUserAndStatus(Long userId, Status status) {
        return applicationRepository.countByCreatedByAndApplicationStatus(userId, status);
    }
}
