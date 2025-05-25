package com.team7.Idam.global.util;

import com.team7.Idam.domain.user.entity.enums.UserType;
import com.team7.Idam.jwt.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/*
    AccessToken에 담긴 사용자 정보를 기반으로, "현재 로그인한 사용자"의 정보를 알려주는 도구들
 */
public class SecurityUtil {

    // 현재 로그인한 사용자의 User.id (DB PK)를 반환
    public static Long getCurrentUserId() {
        /*
            Authentication 객체 == Spring Security의 현재 로그인 상태를 나타냄.
            SecurityContextHolder == 현재 요청 스레드의 인증 정보를 저장하고 있는 보안 컨텍스트.
         */
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 로그인하지 않은 요청 || 우리가 기대하는 CustomUserDetails 타입이 아님
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new IllegalStateException("인증된 사용자가 존재하지 않습니다.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }

    // 현재 로그인한 사용자의 UserType (STUDENT, COMPANY, ADMIN)을 반환
    public static UserType getCurrentUserType() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new IllegalStateException("인증된 사용자가 존재하지 않습니다.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUserType();  // UserType.STUDENT / COMPANY / ADMIN 등
    }

    // 현재 로그인한 사용자의 전체 정보가 담긴 CustomUserDetails 객체 자체를 반환
    // (여러 필드 동시 필요 시)
    public static CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new IllegalStateException("인증된 사용자가 존재하지 않습니다.");
        }

        return (CustomUserDetails) authentication.getPrincipal();
    }
}
