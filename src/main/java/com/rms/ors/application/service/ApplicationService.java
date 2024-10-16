package com.rms.ors.application.service;

import com.rms.ors.application.domain.Application;
import com.rms.ors.application.dto.ApplicationDTO;
import com.rms.ors.application.specification.ApplicationSpecification;
import com.rms.ors.shared.Role;
import com.rms.ors.shared.Status;
import com.rms.ors.user.domain.User;
import com.rms.ors.exception.ResourceNotFoundException;
import com.rms.ors.exception.UnauthorizedAccessException;
import com.rms.ors.application.repository.ApplicationRepository;
import com.rms.ors.user.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final UserManagementService userManagementService;


    public Page<ApplicationDTO> getAllApplications(Long submittedBy, Long reviewedBy,
                                                   LocalDateTime startedDate, LocalDateTime endDate,
                                                   String fullName, String fathersName, String applicationStatus,
                                                   String sortField, String sortDirection, int page, int size) {

        // Get current authenticated user
        User user = getCurrentUser();
        // Validate user role, USER can only see applications submitted by themselves
        if (!isAdminOrManagement(user)) submittedBy = user.getId();

        Specification<Application> specification = ApplicationSpecification.buildSearchSpecification
                (submittedBy, reviewedBy, startedDate, endDate, fullName, fathersName, applicationStatus);

        // sort direction DESC/ASC
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        // sort field createdAt/lastModifiedAt/applicationStatus...
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        return applicationRepository.findAll(specification, pageable).map(this::mapApplicationToDTO);
    }


    private boolean isAdminOrManagement(User user) {
        return user.getRole().equals(Role.ADMIN) || user.getRole().equals(Role.MANAGEMENT);
    }


    public Application getApplicationsById(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(()->
                {
                    log.warn("Application with Id {} not found", applicationId);
                    return new ResourceNotFoundException("Content not found");
                });
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


    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userManagementService.findUserByEmail(username);
    }


    private boolean hasUpdatePermission(Status applicationStatus) {

        if (getCurrentUser().getRole().equals(Role.ADMIN)) {
            return true;
        }
        else if (getCurrentUser().getRole().equals(Role.MANAGEMENT) && applicationStatus.equals(Status.PENDING)) {
            return true;
        }
        else return getCurrentUser().getRole().equals(Role.USER) && (applicationStatus.equals(Status.INCOMPLETE) || applicationStatus.equals(Status.REJECTED));
    }


    public Application updateApplications(Application updatedApplication, Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(()->
                {
                    log.warn("Application with Id {} not found for update", applicationId);
                    return new ResourceNotFoundException("Content not found");
                });

        if (!hasUpdatePermission(application.getApplicationStatus())) {
            throw new UnauthorizedAccessException("You do not have permission to update this content");
        }
        updateApplicationFields(application, updatedApplication);
        return applicationRepository.save(application);
    }


    public void deleteApplications(Long applicationId) {
        try {
            applicationRepository.deleteById(applicationId);
            log.info("Application with Id {} successfully deleted", applicationId);
        } catch (EmptyResultDataAccessException ex) {
            log.warn("Application with Id {} not found for deletion", applicationId);
            throw new ResourceNotFoundException("No content found to delete");
        }
    }


    private ApplicationDTO mapApplicationToDTO(Application application) {
        return ApplicationDTO.builder()
                .fullName(application.getPersonalInformation().getFullName())
                .fathersName(application.getPersonalInformation().getFathersName())
                .mothersName(application.getPersonalInformation().getMothersName())
                .district(application.getAddress().getPresentDistrict())
                .subDistrict(application.getAddress().getPresentSubDistrict())
                .applicationStatus(application.getApplicationStatus())
                .build();
    }

}
