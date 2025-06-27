package com.team7.Idam.domain.user.service;

import com.team7.Idam.domain.user.dto.signup.BusinessStatusResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${odcloud.api.key}")
    private String apiKey;

    public List<BusinessStatusResponseDto> checkBusinessStatus(String businessNumber) {
        // 🔐 서비스 키를 URL 인코딩 후 사용
        String encodedKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);
        String url = "https://api.odcloud.kr/api/nts-businessman/v1/status?serviceKey=" + encodedKey;
        URI uri = URI.create(url);  // 이중 인코딩 방지

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("b_no", List.of(businessNumber));

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.getBody().get("data");
        List<BusinessStatusResponseDto> result = new ArrayList<>();

        for (Map<String, Object> data : dataList) {
            BusinessStatusResponseDto dto = new BusinessStatusResponseDto();
            dto.setB_no((String) data.get("b_no"));
            dto.setB_stt((String) data.get("b_stt"));
            dto.setB_stt_cd((String) data.get("b_stt_cd"));
            dto.setTax_type((String) data.get("tax_type"));
            dto.setTax_type_cd((String) data.get("tax_type_cd"));
            dto.setEnd_dt((String) data.get("end_dt"));
            dto.setUtcc_yn((String) data.get("utcc_yn"));
            dto.setRbf_tax_type((String) data.get("rbf_tax_type"));
            dto.setRbf_tax_type_cd((String) data.get("rbf_tax_type_cd"));
            result.add(dto);
        }

        return result;
    }
}
