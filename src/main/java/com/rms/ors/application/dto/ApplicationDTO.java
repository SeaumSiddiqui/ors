package com.rms.ors.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.rms.ors.shared.Status;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationDTO {
    private String fullName;
    private String fathersName;
    private String mothersName;
    private String district;
    private String subDistrict;
    private Status applicationStatus;
}
