package com.bootcamp19.calculatewordservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResReportDto {
    private String fileName;
    private Integer totalData;
    private String downloadUrl; // presigned URL, berlaku sementara
}
