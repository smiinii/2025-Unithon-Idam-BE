package com.team7.Idam.domain.user.dto.profile.company;

import com.team7.Idam.domain.user.entity.Company;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyPreviewResponseDto {
    private Long userId;
    private String companyName;
    private String profileImage;
    private String companyDescription;
    public String website;

    /*
        Company 도메인 객체를 CompanyPreviewResponseDto로 변환하는 정적 팩토리 메서드.
     */
    public static CompanyPreviewResponseDto from(Company company) {
        return new CompanyPreviewResponseDto(
                company.getId(),
                company.getCompanyName(),
                company.getProfileImage(),
                company.getCompanyDescription(),
                company.getWebsite()
        );
    }
}
