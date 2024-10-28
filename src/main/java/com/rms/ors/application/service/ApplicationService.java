package com.rms.ors.application.service;

import com.rms.ors.application.domain.*;
import com.rms.ors.application.dto.ApplicationDTO;
import com.rms.ors.application.specification.ApplicationSpecification;
import com.rms.ors.shared.Role;
import com.rms.ors.shared.Status;
import com.rms.ors.user.domain.User;
import com.rms.ors.exception.ResourceNotFoundException;
import com.rms.ors.exception.UnauthorizedAccessException;
import com.rms.ors.application.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

import static com.rms.ors.shared.Status.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;


    public Page<ApplicationDTO> getAllApplications(User currentUser, Long createdBy, Long reviewedBy,
                                                   LocalDateTime startedDate, LocalDateTime endDate,
                                                   String fullName, String fathersName, String applicationStatus,
                                                   String sortField, String sortDirection, int page, int size) {

        // validate user role, USER can only see applications submitted by themselves
        if (!isAdminOrManagement(currentUser)) createdBy = currentUser.getId();

        Specification<Application> specification = ApplicationSpecification.buildSearchSpecification
                (createdBy, reviewedBy, startedDate, endDate, fullName, fathersName, applicationStatus);

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


    public ApplicationDTO createApplications(Application application) {
        application.setApplicationStatus(INCOMPLETE);
        return mapApplicationToDTO(applicationRepository.save(application));
    }


    public Application updateApplications(User currentUser, Application updatedApplication, Long applicationId) {
        Application application = getApplicationsById(applicationId);

        if (!hasUpdatePermission(currentUser.getRole(), application.getApplicationStatus())) {
            throw new UnauthorizedAccessException("You do not have permission to update this content");
        }
        updateApplicationFields(currentUser, application, updatedApplication);
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


    private boolean hasUpdatePermission(Role currentUserRole, Status applicationStatus) {

        if (currentUserRole.equals(Role.ADMIN)) {
            return true;
        }
        else if (currentUserRole.equals(Role.MANAGEMENT) && applicationStatus.equals(PENDING)) {
            return true;
        }
        else return currentUserRole.equals(Role.USER) && (applicationStatus.equals(INCOMPLETE) || applicationStatus.equals(REJECTED));
    }


    private void updateApplicationFields(User currentUser, Application existingApplication, Application updatedApplication) {
        // Application
        existingApplication.setApplicationStatus(updatedApplication.getApplicationStatus());
        // If application gets rejected
        if (updatedApplication.getApplicationStatus().equals(REJECTED)) {
            existingApplication.setRejectionMessage(updatedApplication.getRejectionMessage());
        }

        // Personal Information
        if (updatedApplication.getPrimaryInformation() != null) {
            PrimaryInformation existing = existingApplication.getPrimaryInformation();
            PrimaryInformation updated = updatedApplication.getPrimaryInformation();

            updatePrimaryInformationFields(existing, updated);
        }

        // Address
        if (updatedApplication.getAddress() != null) {
            Address existing = existingApplication.getAddress();
            Address updated = updatedApplication.getAddress();

            updateAddressFields(existing, updated);
        }

        // Family Member
        if (updatedApplication.getFamilyMemberList() != null) {
            List<FamilyMember> existing = existingApplication.getFamilyMemberList();
            List<FamilyMember> updated = updatedApplication.getFamilyMemberList();

            updateFamilyMemberFields(existingApplication, existing, updated);
        }

        // Basic Information
        if (updatedApplication.getBasicInformation() != null) {
            BasicInformation existing = existingApplication.getBasicInformation();
            BasicInformation updated = updatedApplication.getBasicInformation();

            updateBasicInformationFields(existing, updated);
        }

        // Verification
        if (updatedApplication.getApplicationStatus().equals(ACCEPTED)) {
            Verification existing = existingApplication.getVerification();
            existing.setReviewedBy(currentUser.getSignatureURL());
        }
        if (updatedApplication.getApplicationStatus().equals(GRANTED)) {
            Verification existing = existingApplication.getVerification();
            existing.setInvestigatedBy(currentUser.getSignatureURL());
        }

        // Documents

        // Transaction
        if (updatedApplication.getTransaction() != null) {
            Transaction existing = existingApplication.getTransaction();
            Transaction updated = updatedApplication.getTransaction();

            existing.setAccountTitle(updated.getAccountTitle());
            existing.setAccountNumber(updated.getAccountNumber());
            existing.setBankTitle(updated.getBankTitle());
            existing.setBranch(updated.getBranch());
            existing.setRoutingNumber(updated.getRoutingNumber());
        }
    }

    private static void updateBasicInformationFields(BasicInformation existing, BasicInformation updated) {
        existing.setPhysicalCondition(updated.getPhysicalCondition());
        existing.setHasCriticalIllness(updated.isHasCriticalIllness());
        existing.setTypeOfIllness(updated.getTypeOfIllness());
        existing.setResident(updated.isResident());
        existing.setResidenceStatus(updated.getResidenceStatus());
        existing.setHouseType(updated.getHouseType());

        existing.setBedroom(updated.getBedroom());
        existing.setBalcony(updated.getBalcony());
        existing.setKitchen(updated.getKitchen());
        existing.setStore(updated.getStore());
        existing.setRoom(updated.getRoom());
        existing.setHasTubeWell(updated.isHasTubeWell());

        existing.setGuardiansName(updated.getGuardiansName());
        existing.setGuardiansRelation(updated.getGuardiansRelation());
        existing.setNID(updated.getNID());
        existing.setCell1(updated.getCell1());
        existing.setCell2(updated.getCell2());
    }


    private void updateFamilyMemberFields(Application existingApplication, List<FamilyMember> existing, List<FamilyMember> updated) {

        // Remove member that are no longer present in the update
        Iterator<FamilyMember> iterator = existing.iterator();
        while (iterator.hasNext()) {
            FamilyMember existingMember = iterator.next();
            boolean stillExist = updated.stream()
                    .anyMatch(up-> up.getId() != null &&  up.getId().equals(existingMember.getId()));

            if (!stillExist) {
                iterator.remove();
            }
        }

        // Update or add family member
        for (FamilyMember updatedMember : updated) {
            if (updatedMember.getId() != null) {
                FamilyMember existingMember = existing
                        .stream()
                        .filter(f -> f.getId().equals(updatedMember.getId()))
                        .findFirst()
                        .orElse(null);

                if (existingMember != null) {
                    // Update existing family member's fields
                    existingMember.setName(updatedMember.getName());
                    existingMember.setAge(updatedMember.getAge());
                    existingMember.setSiblingsGrade(updatedMember.getSiblingsGrade());
                    existingMember.setOccupation(updatedMember.getOccupation());
                    existingMember.setMaritalStatus(updatedMember.getMaritalStatus());
                }
            } else {
                // Add a new family member to the list
                boolean isAlreadyAdded = existing.stream()
                        .anyMatch(n -> n.getName().equals(updatedMember.getName()) && n.getAge() == updatedMember.getAge());

                if (!isAlreadyAdded) {
                    updatedMember.setApplication(existingApplication);
                    existing.add(updatedMember);
                }
            }
        }
    }


    private void updateAddressFields(Address existing, Address updated) {
        existing.setPresentVillage(updated.getPresentVillage());
        existing.setPresentUnion(updated.getPresentUnion());
        existing.setPresentSubDistrict(updated.getPresentSubDistrict());
        existing.setPresentDistrict(updated.getPresentDistrict());
        existing.setPresentLocation(updated.getPresentLocation());

        existing.setPermanentVillage(updated.getPermanentVillage());
        existing.setPresentUnion(updated.getPresentUnion());
        existing.setPermanentSubDistrict(updated.getPermanentSubDistrict());
        existing.setPermanentDistrict(updated.getPermanentDistrict());
        existing.setPermanentLocation(updated.getPermanentLocation());
    }


    private void updatePrimaryInformationFields(PrimaryInformation existing, PrimaryInformation updated) {
        existing.setFullName(updated.getFullName());
        existing.setBcRegistration(updated.getBcRegistration());
        existing.setFathersName(updated.getFathersName());
        existing.setMothersName(updated.getMothersName());
        existing.setDob(updated.getDob());
        existing.setPlaceOfBirth(updated.getPlaceOfBirth());
        existing.setDod(updated.getDod());
        existing.setCauseOfDeath(updated.getCauseOfDeath());
        existing.setMothersStatus(updated.getMothersStatus());
        existing.setMothersOccupation(updated.getMothersOccupation());
        existing.setAnnualIncome(updated.getAnnualIncome());
        existing.setFixedAsset(updated.getFixedAsset());
        existing.setAcademicInstitution(updated.getAcademicInstitution());
        existing.setGrade(updated.getGrade());
        existing.setGender(updated.getGender());
    }


    private ApplicationDTO mapApplicationToDTO(Application application) {
        return ApplicationDTO.builder()
                .id(application.getId())
                .fullName(application.getPrimaryInformation().getFullName())
                .fathersName(application.getPrimaryInformation().getFathersName())
                .mothersName(application.getPrimaryInformation().getMothersName())
                .district(application.getAddress().getPresentDistrict())
                .subDistrict(application.getAddress().getPresentSubDistrict())
                .applicationStatus(application.getApplicationStatus())
                .build();
    }

}
