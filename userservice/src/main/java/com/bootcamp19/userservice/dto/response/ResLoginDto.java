package com.bootcamp19.userservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResLoginDto {
    private String token;
    private String email;
}
