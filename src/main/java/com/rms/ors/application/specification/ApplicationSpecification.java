package com.rms.ors.application.specification;
import com.rms.ors.application.domain.Application;
import com.rms.ors.application.domain.PrimaryInformation;
import com.rms.ors.shared.Status;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class ApplicationSpecification {

    public static Specification<Application> submittedBy(Long userId) {
        return (root, query, criteriaBuilder) ->
                userId != null ? criteriaBuilder.equal(root.get("submittedBy"), userId) : criteriaBuilder.conjunction();
    }

    public static Specification<Application> lastReviewedBy(Long reviewerId) {
        return (root, query, criteriaBuilder) ->
                reviewerId != null ? criteriaBuilder.equal(root.get("lastReviewedBy"), reviewerId) : criteriaBuilder.conjunction();
    }


    public static Specification<Application> createdAt(LocalDateTime start, LocalDateTime end) {
        return (root, query, criteriaBuilder) -> {
            if (start != null && end != null) {
                return criteriaBuilder.between(root.get("createdAt"), start, end);
            } else if (start != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), start);
            } else if (end != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), end);
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Application> lastModifiedAt(LocalDateTime start, LocalDateTime end) {
        return (root, query, criteriaBuilder) -> {
            if (start != null && end != null) {
                return criteriaBuilder.between(root.get("lastModifiedAt"), start, end);
            } else if (start != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("lastModifiedAt"), start);
            } else if (end != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("lastModifiedAt"), end);
            }
            return criteriaBuilder.conjunction();
        };
    }


    public static Specification<Application> personalInformationFullName(String fullName) {
        return (root, query, criteriaBuilder) -> {
            if (fullName != null) {
                Join<Application, PrimaryInformation> personalInfoJoin = root.join("primaryInformation");
                return criteriaBuilder.like(personalInfoJoin.get("fullName"), String.format("%%%s%%", fullName));
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Application> personalInformationFathersName(String fathersName) {
        return (root, query, criteriaBuilder) -> {
            if (fathersName != null) {
                Join<Application, PrimaryInformation> personalInfoJoin = root.join("primaryInformation");
                return criteriaBuilder.like(personalInfoJoin.get("fathersName"), String.format("%%%s%%", fathersName));
            }
            return criteriaBuilder.conjunction();
        };
    }


    public static Specification<Application> applicationStatus(String applicationStatus) {
        return (root, query, criteriaBuilder) -> {
            if (applicationStatus != null) {
                Status status = Status.valueOf(applicationStatus.toUpperCase());
                return criteriaBuilder.equal(root.get("applicationStatus"), status);
            }
            return null;
        };
    }


    public static Specification<Application> buildSearchSpecification(Long submittedBy, Long reviewedBy, LocalDateTime startDate, LocalDateTime endDate,
                                                                String fullName, String fathersName, String applicationStatus) {
        return Specification.where(submittedBy(submittedBy))
                .and(lastReviewedBy(reviewedBy))
                .and(createdAt(startDate, endDate))
                .and(lastModifiedAt(startDate, endDate))
                .and(personalInformationFullName(fullName))
                .and(personalInformationFathersName(fathersName))
                .and(applicationStatus(applicationStatus));
    }

}
