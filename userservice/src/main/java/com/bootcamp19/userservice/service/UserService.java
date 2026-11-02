package com.bootcamp19.userservice.service;

import com.bootcamp19.userservice.dto.request.ReqCreateUserDto;
import com.bootcamp19.userservice.dto.response.ResCreateUserDto;
import com.bootcamp19.userservice.dto.response.ResDetailUserDto;

public interface UserService {
    ResCreateUserDto createUser(ReqCreateUserDto reqCreateUserDto);
    ResDetailUserDto getDetailUser(Long id);
}
