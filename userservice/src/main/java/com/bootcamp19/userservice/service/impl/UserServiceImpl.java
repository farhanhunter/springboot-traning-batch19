package com.bootcamp19.userservice.service.impl;

import com.bootcamp19.userservice.dto.request.ReqCreateUserDto;
import com.bootcamp19.userservice.dto.response.ResCreateUserDto;
import com.bootcamp19.userservice.dto.response.ResDetailUserDto;
import com.bootcamp19.userservice.entity.UserEntity;
import com.bootcamp19.userservice.exception.BadRequestException;
import com.bootcamp19.userservice.exception.DataNotFoundException;
import com.bootcamp19.userservice.repository.UserRepository;
import com.bootcamp19.userservice.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // constructor injection: dependency wajib terisi & bisa di-mock saat unit test
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResCreateUserDto createUser(ReqCreateUserDto reqCreateUserDto) {
        if (userRepository.existsByEmail(reqCreateUserDto.getEmail())) {
            throw new BadRequestException("Email sudah terdaftar");
        }

        UserEntity user = new UserEntity();
        user.setEmail(reqCreateUserDto.getEmail());
        user.setPassword(passwordEncoder.encode(reqCreateUserDto.getPassword())); // simpan hash
        user.setFirstName(reqCreateUserDto.getFirstName());
        user.setLastName(reqCreateUserDto.getLastName());
        user.setPhoneNumber(reqCreateUserDto.getPhoneNumber());

        UserEntity savedUser = userRepository.save(user);

        return ResCreateUserDto.builder()
                .id(savedUser.getId())
                .fullName(savedUser.getFirstName() + " " + savedUser.getLastName())
                .email(savedUser.getEmail())
                .build();
    }

    @Override
    public ResDetailUserDto getDetailUser(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User tidak ditemukan"));

        return ResDetailUserDto.builder()
                .id(user.getId())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
