package com.bootcamp19.calculatewordservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * implements Serializable karena object ini disimpan di Redis oleh @Cacheable
 * (serializer default Redis cache = JDK serialization).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResAnalysisDto implements Serializable {
    private Long id;
    private String kalimat;
    private Integer totalHuruf;
    private String hurufTerbanyak;
    private Map<String, Integer> frekuensi;
    private Long createdBy;
    private String createdAt;
}
