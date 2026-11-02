package com.bootcamp19.calculatewordservice.service;

import com.bootcamp19.calculatewordservice.dto.request.ReqCreateAnalysisDto;
import com.bootcamp19.calculatewordservice.dto.response.ResAnalysisDto;
import com.bootcamp19.calculatewordservice.dto.response.ResTrendingDto;

import java.util.List;

public interface AnalysisService {
    ResAnalysisDto createAnalysis(ReqCreateAnalysisDto request, Long userId);
    ResAnalysisDto analyzeRandomQuote(Long userId);
    List<ResAnalysisDto> getAllAnalyses(String huruf);
    List<ResAnalysisDto> getMyAnalyses(Long userId);
    ResAnalysisDto getAnalysisById(Long id);
    void deleteAnalysis(Long id);
    List<ResTrendingDto> getTrendingHuruf(int limit);
}
