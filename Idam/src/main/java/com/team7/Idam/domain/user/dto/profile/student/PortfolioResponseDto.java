package com.team7.Idam.domain.user.dto.profile.student;

import com.team7.Idam.domain.user.entity.Portfolio;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PortfolioResponseDto {
    private Long id;
    private String url;
    private String type; // "File" 또는 "URL"

    public static PortfolioResponseDto from(Portfolio p) {
        String value = p.getPortfolio();
        String type = isS3FileUrl(value) ? "FILE" : "URL";
        return new PortfolioResponseDto(p.getPortfolioId(), value, type);
    }

    private static boolean isS3FileUrl(String value) {
        return value != null && value.startsWith("https://unithon-idam.s3.");
    }
}
