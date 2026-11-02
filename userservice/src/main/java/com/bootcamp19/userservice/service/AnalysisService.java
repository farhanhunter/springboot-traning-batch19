package com.bootcamp19.userservice.service;

import com.bootcamp19.userservice.dto.response.ResFeignAnalysisDto;

public interface AnalysisService {
    ResFeignAnalysisDto getAnalysisById(Long id);
}
