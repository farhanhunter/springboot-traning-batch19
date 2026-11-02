package com.bootcamp19.userservice.controller;

import com.bootcamp19.userservice.dto.request.ReqLoginDto;
import com.bootcamp19.userservice.dto.response.BaseResponse;
import com.bootcamp19.userservice.dto.response.ResLoginDto;
import com.bootcamp19.userservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<ResLoginDto>> loginUser(
            @Valid @RequestBody ReqLoginDto request
    ) {
        BaseResponse<ResLoginDto> response = new BaseResponse<>();
        response.setMessage("Success login user");
        response.setData(authService.loginUser(request));
        return ResponseEntity.ok(response);
    }
}
