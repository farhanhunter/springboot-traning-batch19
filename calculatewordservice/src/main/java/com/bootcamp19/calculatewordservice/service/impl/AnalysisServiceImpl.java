package com.bootcamp19.calculatewordservice.service.impl;

import com.bootcamp19.calculatewordservice.consumer.WordAnalyzedConsumer;
import com.bootcamp19.calculatewordservice.dto.feign.ResQuoteDto;
import com.bootcamp19.calculatewordservice.dto.request.ReqCreateAnalysisDto;
import com.bootcamp19.calculatewordservice.dto.response.ResAnalysisDto;
import com.bootcamp19.calculatewordservice.dto.response.ResTrendingDto;
import com.bootcamp19.calculatewordservice.entity.AnalysisEntity;
import com.bootcamp19.calculatewordservice.exception.BadRequestException;
import com.bootcamp19.calculatewordservice.exception.DataNotFoundException;
import com.bootcamp19.calculatewordservice.repository.AnalysisRepository;
import com.bootcamp19.calculatewordservice.rest.QuoteClient;
import com.bootcamp19.calculatewordservice.service.AnalysisService;
import com.bootcamp19.calculatewordservice.util.CharCounterUtil;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class AnalysisServiceImpl implements AnalysisService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AnalysisRepository analysisRepository;
    private final QuoteClient quoteClient;
    private final StringRedisTemplate stringRedisTemplate;

    public AnalysisServiceImpl(AnalysisRepository analysisRepository,
                               QuoteClient quoteClient,
                               StringRedisTemplate stringRedisTemplate) {
        this.analysisRepository = analysisRepository;
        this.quoteClient = quoteClient;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public ResAnalysisDto createAnalysis(ReqCreateAnalysisDto request, Long userId) {
        return saveAnalysis(request.getKalimat(), userId);
    }

    @Override
    public ResAnalysisDto analyzeRandomQuote(Long userId) {
        try {
            ResQuoteDto quote = quoteClient.getRandomQuote();
            log.info("Quote dari pihak ketiga: {} - {}", quote.getQuote(), quote.getAuthor());
            // sengaja memanggil method private, bukan createAnalysis():
            // pemanggilan di class yang sama tidak lewat proxy Spring (self-invocation)
            return saveAnalysis(quote.getQuote(), userId);
        } catch (FeignException e) {
            throw new BadRequestException("Gagal mengambil quote: " + e.status());
        }
    }

    @Override
    public List<ResAnalysisDto> getAllAnalyses(String huruf) {
        List<AnalysisEntity> entities = (huruf == null || huruf.isBlank())
                ? analysisRepository.findAll()
                : analysisRepository.findByKalimatContainingIgnoreCaseOrderByIdDesc(huruf);

        List<ResAnalysisDto> result = new ArrayList<>();
        entities.forEach(entity -> result.add(toDto(entity)));
        return result;
    }

    @Override
    public List<ResAnalysisDto> getMyAnalyses(Long userId) {
        return analysisRepository.findByCreatedByJPQL(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Cache-Aside dengan @Cacheable:
     * - cache hit  -> method TIDAK dijalankan, data langsung dari Redis
     * - cache miss -> method jalan (query DB), hasilnya disimpan ke Redis
     * Key di Redis: batch19:analysis::{id}
     */
    @Override
    @Cacheable(value = "analysis", key = "#id")
    public ResAnalysisDto getAnalysisById(Long id) {
        log.info("Cache miss -> query ke database untuk id {}", id);
        AnalysisEntity entity = analysisRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Analisis dengan id " + id + " tidak ditemukan"));
        return toDto(entity);
    }

    // hapus juga cache-nya supaya tidak mengembalikan data yang sudah tidak ada
    @Override
    @CacheEvict(value = "analysis", key = "#id")
    public void deleteAnalysis(Long id) {
        if (!analysisRepository.existsById(id)) {
            throw new DataNotFoundException("Analisis dengan id " + id + " tidak ditemukan");
        }
        analysisRepository.deleteById(id);
    }

    @Override
    public List<ResTrendingDto> getTrendingHuruf(int limit) {
        Set<ZSetOperations.TypedTuple<String>> tuples = stringRedisTemplate.opsForZSet()
                .reverseRangeWithScores(WordAnalyzedConsumer.TRENDING_KEY, 0, limit - 1L);

        List<ResTrendingDto> result = new ArrayList<>();
        if (tuples != null) {
            tuples.forEach(t -> result.add(new ResTrendingDto(
                    t.getValue(),
                    t.getScore() == null ? 0L : t.getScore().longValue()
            )));
        }
        return result;
    }

    private ResAnalysisDto saveAnalysis(String kalimat, Long userId) {
        Map<String, Integer> frekuensi = CharCounterUtil.hitungFrekuensi(kalimat);

        AnalysisEntity entity = new AnalysisEntity();
        entity.setKalimat(kalimat);
        entity.setTotalHuruf(CharCounterUtil.totalHuruf(frekuensi));
        entity.setHurufTerbanyak(CharCounterUtil.hurufTerbanyak(frekuensi));
        entity.setCreatedBy(userId);

        return toDto(analysisRepository.save(entity));
    }

    private ResAnalysisDto toDto(AnalysisEntity entity) {
        return ResAnalysisDto.builder()
                .id(entity.getId())
                .kalimat(entity.getKalimat())
                .totalHuruf(entity.getTotalHuruf())
                .hurufTerbanyak(entity.getHurufTerbanyak())
                // frekuensi tidak disimpan di DB, dihitung ulang dari kalimat
                .frekuensi(CharCounterUtil.hitungFrekuensi(entity.getKalimat()))
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt() == null ? null : entity.getCreatedAt().format(FORMATTER))
                .build();
    }
}
