package com.team7.Idam.domain.user.dto.profile.company;

import lombok.Data;

@Data
public class CompanyProfileUpdateRequestDto {
    private String companyDescription;
    private String website;
    private String phone;
    private String email;
}
