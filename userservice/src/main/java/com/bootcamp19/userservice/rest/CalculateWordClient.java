package com.bootcamp19.userservice.rest;

import com.bootcamp19.userservice.dto.response.BaseResponse;
import com.bootcamp19.userservice.dto.response.ResFeignAnalysisDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign + Eureka: cukup tulis NAMA service, alamatnya dicari di Eureka.
 * Path diawali /calculateword karena itu context-path calculatewordservice.
 */
@FeignClient(name = "calculatewordservice")
public interface CalculateWordClient {

    @GetMapping("/calculateword/rest/v1/analyses/{id}")
    BaseResponse<ResFeignAnalysisDto> getAnalysisById(@PathVariable("id") Long id);
}
