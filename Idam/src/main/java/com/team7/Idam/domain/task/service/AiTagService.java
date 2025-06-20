package com.team7.Idam.domain.task.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team7.Idam.domain.task.client.AiTagClient;
import com.team7.Idam.domain.task.dto.AiTagRequestDto;
import com.team7.Idam.domain.task.dto.AiTagResultDto;
import com.team7.Idam.domain.user.entity.enums.UserType;
import com.team7.Idam.global.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class AiTagService {

    private static final Logger log = LoggerFactory.getLogger(AiTagService.class);

    private final AiTagClient aiTagClient;
    private final ObjectMapper objectMapper;

    public AiTagService(AiTagClient aiTagClient, ObjectMapper objectMapper) {
        this.aiTagClient = aiTagClient;
        this.objectMapper = objectMapper;
    }

    public void validateCompanyAccess() {
        if (SecurityUtil.getCurrentUserType() != UserType.COMPANY) {
            throw new AccessDeniedException("해당 기능은 기업 회원만 사용할 수 있습니다.");
        }
    }

    public Mono<List<String>> fetchDeduplicatedTagList(AiTagRequestDto requestDto) {
        validateCompanyAccess(); // 기업 타입만 실행 가능
        return aiTagClient.getAiTag(requestDto.getDomain(), requestDto.getPrompt())
                .map(jsonString -> {
                    log.info("🔥 서비스 수신된 Raw JSON: {}", jsonString);
                    try {
                        List<AiTagResultDto> resultList = objectMapper.readValue(
                                jsonString,
                                new TypeReference<List<AiTagResultDto>>() {}
                        );

                        Set<String> tagSet = new HashSet<>();

                        for (AiTagResultDto dto : resultList) {
                            if (dto == null) continue;

                            String domain = requestDto.getDomain();

                            if ("IT·프로그래밍".equals(domain)) {
                                if (dto.getDomain() != null) tagSet.add(dto.getDomain().trim());
                                if (dto.getRole() != null) tagSet.add(dto.getRole().trim());

                                if (dto.getLanguages() != null) {
                                    dto.getLanguages().stream()
                                            .filter(Objects::nonNull)
                                            .map(String::trim)
                                            .forEach(tagSet::add);
                                }

                                if (dto.getFrameworks() != null) {
                                    dto.getFrameworks().values().forEach(list -> list.stream()
                                            .filter(Objects::nonNull)
                                            .map(String::trim)
                                            .forEach(tagSet::add));
                                }

                                extractToolTags(dto.getTools(), tagSet);

                            } else if ("디자인".equals(domain) || "마케팅".equals(domain)) {
                                extractToolTags(dto.getTools(), tagSet);
                            } else {
                                log.warn("알 수 없는 도메인: {}", domain);
                            }
                        }

                        List<String> deduplicatedTags = new ArrayList<>(tagSet);
                        log.info("🔥 최종 deduplicated 태그: {}", deduplicatedTags);
                        return deduplicatedTags;

                    } catch (Exception e) {
                        log.error("🔥 JSON 파싱 실패", e);
                        throw new RuntimeException("서비스 JSON 파싱 실패", e);
                    }
                });
    }

    // 도메인에 따라 tools를 유연하게 파싱
    private void extractToolTags(Object tools, Set<String> tagSet) {
        if (tools instanceof Map<?, ?> toolMap) {
            toolMap.values().forEach(list -> {
                if (list instanceof List<?> subList) {
                    subList.stream()
                            .filter(Objects::nonNull)
                            .map(Object::toString)
                            .map(String::trim)
                            .forEach(tagSet::add);
                }
            });
        } else if (tools instanceof List<?> toolList) {
            toolList.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .map(String::trim)
                    .forEach(tagSet::add);
        }
    }
}