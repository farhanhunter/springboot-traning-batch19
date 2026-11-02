package com.bootcamp19.userservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "mst_user")
@Data // getter, setter, toString, equals, hashCode
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto increment
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;

    // yang disimpan HASH BCrypt, bukan password asli
    @Column(name = "password", nullable = false)
    private String password;
}
