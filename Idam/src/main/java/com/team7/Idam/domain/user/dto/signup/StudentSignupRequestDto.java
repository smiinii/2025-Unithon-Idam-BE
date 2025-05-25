package com.team7.Idam.domain.user.dto.signup;

import com.team7.Idam.domain.user.entity.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentSignupRequestDto {

    @Email
    @NotBlank
    @Size(max = 100)
    private String email;

    @NotBlank
    @Size(min = 8, max = 20)  // 비밀번호 8~20자
    private String password;

    @NotBlank
    @Size(max = 50)
    private String name;

    @NotBlank
    @Size(max = 50)
    private String nickname;

    @NotBlank
    @Size(max = 100)
    private String schoolName;

    @NotBlank
    @Size(max = 100)
    private String major;

    @NotBlank
    @Size(max = 50)
    private String schoolId;

    @NotBlank
    @Size(max = 20)
    private String phone;

    @NotNull
    private Gender gender;

    @Size(max = 255)
    private String profileImage;

    @NotBlank
    @Size(max = 20)
    private String categoryName;
}
