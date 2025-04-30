package com.team7.Idam.domain.user.service;

import com.team7.Idam.domain.user.dto.login.LoginResultDto;
import com.team7.Idam.domain.user.dto.signup.StudentSignupRequestDto;
import com.team7.Idam.domain.user.dto.signup.CompanySignupRequestDto;
import com.team7.Idam.domain.user.dto.login.LoginRequestDto;
import com.team7.Idam.domain.user.entity.*;
import com.team7.Idam.domain.user.entity.enums.UserType;
import com.team7.Idam.domain.user.entity.enums.UserStatus;
import com.team7.Idam.domain.user.repository.UserRepository;
import com.team7.Idam.domain.user.repository.StudentRepository;
import com.team7.Idam.domain.user.repository.CompanyRepository;
import com.team7.Idam.jwt.JwtTokenProvider;
import com.team7.Idam.global.util.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenStore refreshTokenStore;

    // 학생 회원가입
    public void signupStudent(StudentSignupRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("이미 등록된 전화번호입니다.");
        }

        if (studentRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 별명입니다.");
        }

        if (studentRepository.existsBySchoolId(request.getSchoolId())) {
            throw new IllegalArgumentException("이미 등록된 학번입니다.");
        }

        // User 생성
        User user = User.builder()
                .email(request.getEmail())
                .userType(UserType.STUDENT)
                .userStatus(UserStatus.ACTIVE)
                .phone(request.getPhone())
                .build();
        userRepository.save(user);

        // Student 생성
        Student student = Student.builder()
                .user(user)
                .name(request.getName())
                .nickname(request.getNickname())
                .schoolName(request.getSchoolName())
                .major(request.getMajor())
                .schoolId(request.getSchoolId())
                .password(passwordEncoder.encode(request.getPassword()))
                .gender(request.getGender())
                .profileImage(request.getProfileImage())
                .build();
        studentRepository.save(student);
    }

    // 기업 회원가입
    public void signupCompany(CompanySignupRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("이미 등록된 전화번호입니다.");
        }

        if (companyRepository.existsByBusinessRegistrationNumber(request.getBusinessRegistrationNumber())) {
            throw new IllegalArgumentException("이미 등록된 사업자 등록번호입니다.");
        }

        // User 생성
        User user = User.builder()
                .email(request.getEmail())
                .userType(UserType.COMPANY)
                .userStatus(UserStatus.ACTIVE)
                .phone(request.getPhone())
                .build();
        userRepository.save(user);

        // Company 생성
        Company company = Company.builder()
                .user(user)
                .password(passwordEncoder.encode(request.getPassword()))
                .businessRegistrationNumber(request.getBusinessRegistrationNumber())
                .companyName(request.getCompanyName())
                .address(request.getAddress())
                .website(request.getWebsite())
                .profileImage(request.getProfileImage())
                .build();
        companyRepository.save(company);
    }

    // 학생 로그인
    public LoginResultDto loginStudent(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (user.getUserType() != UserType.STUDENT) {
            throw new IllegalArgumentException("학생 계정이 아닙니다.");
        }

        Student student = studentRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("학생 정보가 존재하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), student.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUserType().name());
        String refreshToken = jwtTokenProvider.generateRefreshToken();
        refreshTokenStore.save(user.getId(), refreshToken);

        return new LoginResultDto(accessToken, refreshToken, user.getUserType().name());
    }

    // 기업 로그인
    public LoginResultDto loginCompany(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (user.getUserType() != UserType.COMPANY) {
            throw new IllegalArgumentException("기업 계정이 아닙니다.");
        }

        Company company = companyRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("기업 정보가 존재하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), company.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUserType().name());
        String refreshToken = jwtTokenProvider.generateRefreshToken();
        refreshTokenStore.save(user.getId(), refreshToken);

        return new LoginResultDto(accessToken, refreshToken, user.getUserType().name());
    }

    // Refresh Token으로 Access Token 재발급
    public LoginResultDto reissueToken(Long userId, String refreshToken) {
        String storedRefreshToken = refreshTokenStore.get(userId);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new IllegalArgumentException("Refresh Token이 유효하지 않습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUserType().name());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken();
        refreshTokenStore.save(user.getId(), newRefreshToken);

        return new LoginResultDto(newAccessToken, newRefreshToken, user.getUserType().name());
    }
}
