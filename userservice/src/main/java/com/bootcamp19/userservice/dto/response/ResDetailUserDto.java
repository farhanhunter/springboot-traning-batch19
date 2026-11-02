package com.bootcamp19.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResDetailUserDto {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
}
