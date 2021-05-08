package com.sms.satp.dto;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiImportRequest {

    @NotNull(message = "The documentType must not be null.")
    @Range(min = 0, max = 1)
    private Integer documentType;
    private String documentUrl;
    private MultipartFile file;
    private SaveMode saveMode;
}
