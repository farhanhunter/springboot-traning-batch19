package com.bootcamp19.userservice.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Bentuk data yang diterima dari calculatewordservice lewat Feign.
 * Field-nya cukup yang dibutuhkan saja, field lain akan diabaikan.
 */
@JsonIgnoreProperties(ignoreUnknown = true) // abaikan field yang tidak kita butuhkan
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResFeignAnalysisDto {
    private Long id;
    private String kalimat;
    private Integer totalHuruf;
    private Map<String, Integer> frekuensi;
}
