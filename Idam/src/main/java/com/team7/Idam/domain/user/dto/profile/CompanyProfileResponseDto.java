package com.team7.Idam.domain.user.dto.profile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyProfileResponseDto {
    private String companyName;
    private String businessRegistrationNumber;
    private String address;
    private String website;
    private String profileImage;
    private String email;
    private String phone;
}
