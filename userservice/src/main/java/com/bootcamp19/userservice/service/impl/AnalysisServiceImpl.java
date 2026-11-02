package com.bootcamp19.userservice.service.impl;

import com.bootcamp19.userservice.dto.response.BaseResponse;
import com.bootcamp19.userservice.dto.response.ResFeignAnalysisDto;
import com.bootcamp19.userservice.exception.BadRequestException;
import com.bootcamp19.userservice.exception.DataNotFoundException;
import com.bootcamp19.userservice.rest.CalculateWordClient;
import com.bootcamp19.userservice.service.AnalysisService;
import feign.FeignException;
import org.springframework.stereotype.Service;

@Service
public class AnalysisServiceImpl implements AnalysisService {
    private final CalculateWordClient calculateWordClient;

    public AnalysisServiceImpl(CalculateWordClient calculateWordClient) {
        this.calculateWordClient = calculateWordClient;
    }

    @Override
    public ResFeignAnalysisDto getAnalysisById(Long id) {
        try {
            BaseResponse<ResFeignAnalysisDto> response = calculateWordClient.getAnalysisById(id);
            return response.getData();
        } catch (FeignException.NotFound e) {
            throw new DataNotFoundException("Analisis tidak ditemukan di calculatewordservice");
        } catch (FeignException e) {
            throw new BadRequestException("Gagal memanggil calculatewordservice: " + e.status());
        }
    }
}
