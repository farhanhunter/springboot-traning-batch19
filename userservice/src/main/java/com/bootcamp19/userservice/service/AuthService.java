package com.bootcamp19.userservice.service;

import com.bootcamp19.userservice.dto.request.ReqLoginDto;
import com.bootcamp19.userservice.dto.response.ResLoginDto;

public interface AuthService {
    ResLoginDto loginUser(ReqLoginDto reqLoginDto);
}
