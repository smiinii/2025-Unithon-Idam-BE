package com.team7.Idam.jwt;

import com.team7.Idam.domain.user.entity.User;
import com.team7.Idam.domain.user.entity.enums.UserType;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final UserType userType;

    // ✅ 전체 정보가 필요한 경우 (기존 생성자)
    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.userType = user.getUserType();
    }

    // ✅ userId만 필요한 경우 (로그아웃/refresh용)
    public CustomUserDetails(Long id) {
        this.id = id;
        this.email = null;
        this.userType = UserType.STUDENT; // or null (안 쓰면 의미 없음)
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> userType != null ? userType.name() : "ROLE_USER");
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
