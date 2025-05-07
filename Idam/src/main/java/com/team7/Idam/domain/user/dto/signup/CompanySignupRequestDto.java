package com.team7.Idam.domain.user.dto.signup;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanySignupRequestDto {

    @Email
    @NotBlank
    @Size(max = 100)
    private String email;  // 이메일 형식(xxxx@yyy.zzz)

    @NotBlank
    @Size(min = 8, max = 20)
    private String password;  // 비밀번호 8~20자 필수

    @NotBlank
    @Size(max = 50)
    private String businessRegistrationNumber;

    @NotBlank
    @Size(max = 100)
    private String companyName;

    @NotBlank
    @Size(max = 200)
    private String address;

    @NotBlank
    @Size(max = 255)
    private String website;

    @NotBlank
    @Size(max = 20)
    private String phone;

    @Size(max = 255)
    private String profileImage;
}
