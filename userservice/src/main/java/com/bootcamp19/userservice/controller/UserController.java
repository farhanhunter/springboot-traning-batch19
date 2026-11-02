package com.bootcamp19.userservice.controller;

import com.bootcamp19.userservice.dto.request.ReqCreateUserDto;
import com.bootcamp19.userservice.dto.response.BaseResponse;
import com.bootcamp19.userservice.dto.response.ResCreateUserDto;
import com.bootcamp19.userservice.dto.response.ResDetailUserDto;
import com.bootcamp19.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // @RequestBody: data dari body JSON
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<ResCreateUserDto>> registerUser(
            @Valid @RequestBody ReqCreateUserDto reqCreateUserDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponse.<ResCreateUserDto>builder()
                        .message("Success register user")
                        .data(userService.createUser(reqCreateUserDto))
                        .build()
        );
    }

    // @RequestHeader: data dari header. X-User-Id diisi oleh gateway dari token JWT
    @GetMapping("/me")
    public ResponseEntity<BaseResponse<ResDetailUserDto>> getMyProfile(
            @RequestHeader("X-User-Id") Long userId
    ) {
        return ResponseEntity.ok(
                BaseResponse.<ResDetailUserDto>builder()
                        .data(userService.getDetailUser(userId))
                        .build()
        );
    }

    // @RequestParam: /rest/v1/users/param?id=1
    @GetMapping("/param")
    public ResponseEntity<BaseResponse<ResDetailUserDto>> getUserByIdParam(
            @RequestParam Long id
    ) {
        BaseResponse<ResDetailUserDto> response = new BaseResponse<>();
        response.setMessage("Success get user by id");
        response.setData(userService.getDetailUser(id));
        return ResponseEntity.ok(response);
    }

    // @PathVariable: /rest/v1/users/1
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ResDetailUserDto>> getUserById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                BaseResponse.<ResDetailUserDto>builder()
                        .data(userService.getDetailUser(id))
                        .build()
        );
    }
}
