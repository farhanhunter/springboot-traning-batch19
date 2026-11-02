package com.bootcamp19.calculatewordservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseResponse<T> {
    @Builder.Default
    private UUID reqId = UUID.randomUUID();

    @Builder.Default
    private String status = "T"; // T = sukses, F = gagal

    @Builder.Default
    private String message = "Success";

    private T data;
}
