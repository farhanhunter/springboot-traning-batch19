package com.bootcamp19.calculatewordservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReqCreateAnalysisDto {
    @NotBlank(message = "kalimat is required")
    @Size(max = 1000, message = "kalimat maksimal 1000 karakter")
    private String kalimat;
}
