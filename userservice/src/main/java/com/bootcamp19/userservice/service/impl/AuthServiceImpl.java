package com.bootcamp19.userservice.service.impl;

import com.bootcamp19.userservice.dto.request.ReqLoginDto;
import com.bootcamp19.userservice.dto.response.ResLoginDto;
import com.bootcamp19.userservice.entity.UserEntity;
import com.bootcamp19.userservice.exception.UnauthorizedException;
import com.bootcamp19.userservice.repository.UserRepository;
import com.bootcamp19.userservice.service.AuthService;
import com.bootcamp19.userservice.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResLoginDto loginUser(ReqLoginDto reqLoginDto) {
        // pesan dibuat sama untuk email tidak ada & password salah,
        // supaya orang lain tidak bisa menebak email mana yang terdaftar
        UserEntity user = userRepository.findByEmail(reqLoginDto.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Email atau password salah"));

        // matches(password asli, hash di database)
        if (!passwordEncoder.matches(reqLoginDto.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Email atau password salah");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getId());

        return ResLoginDto.builder()
                .email(user.getEmail())
                .token(token)
                .build();
    }
}
