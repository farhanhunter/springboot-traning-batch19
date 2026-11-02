package com.bootcamp19.calculatewordservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResTrendingDto {
    private String huruf;
    private Long jumlah;
}
