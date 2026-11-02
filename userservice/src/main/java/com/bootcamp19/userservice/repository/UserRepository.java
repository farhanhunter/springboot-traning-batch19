package com.bootcamp19.userservice.repository;

import com.bootcamp19.userservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // 1. Built-in / derived query: query dibuat otomatis dari nama method
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    // 2. JPQL: query ke nama ENTITY & FIELD Java
    @Query("SELECT u FROM UserEntity u WHERE u.email = :email")
    Optional<UserEntity> findUserByEmailJPQL(@Param("email") String email);

    // 3. Native query: SQL asli ke nama TABEL & KOLOM database
    @Query(value = "SELECT * FROM mst_user u WHERE u.email = :email", nativeQuery = true)
    Optional<UserEntity> findUserByEmailNative(@Param("email") String email);
}
