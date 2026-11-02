package com.bootcamp19.calculatewordservice.repository;

import com.bootcamp19.calculatewordservice.entity.AnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AnalysisRepository extends JpaRepository<AnalysisEntity, Long> {

    // 1. Built-in / derived query: kalimat yang mengandung huruf tertentu
    List<AnalysisEntity> findByKalimatContainingIgnoreCaseOrderByIdDesc(String huruf);

    List<AnalysisEntity> findByCreatedAtBetweenOrderByIdAsc(LocalDateTime from, LocalDateTime to);

    // 2. JPQL: pakai nama entity & field Java
    @Query("SELECT a FROM AnalysisEntity a WHERE a.createdBy = :userId ORDER BY a.id DESC")
    List<AnalysisEntity> findByCreatedByJPQL(@Param("userId") Long userId);

    // 3. Native query: SQL asli ke nama tabel & kolom
    @Query(value = "SELECT * FROM trx_analysis WHERE total_huruf >= :minimal ORDER BY total_huruf DESC",
            nativeQuery = true)
    List<AnalysisEntity> findByMinimalHurufNative(@Param("minimal") Integer minimal);
}
