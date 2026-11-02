package com.bootcamp19.calculatewordservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "trx_analysis")
@Data
public class AnalysisEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kalimat", nullable = false, length = 1000)
    private String kalimat;

    @Column(name = "total_huruf")
    private Integer totalHuruf;

    @Column(name = "huruf_terbanyak", length = 1)
    private String hurufTerbanyak;

    // diisi dari header X-User-Id yang ditambahkan gateway
    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
