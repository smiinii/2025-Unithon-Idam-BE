package com.team7.Idam.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import com.team7.Idam.domain.user.entity.enums.UserType;
import com.team7.Idam.domain.user.entity.enums.UserStatus;

@Entity
@Table(name = "user")  // DB 테이블 이름
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 20)
    private UserType userType;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false, length = 20)
    @Builder.Default
    private UserStatus userStatus = UserStatus.ACTIVE;  // 기본값은 ACTIVE

    @Column(length = 20, unique = true)
    private String phone;

    // User 엔티티에서 Student/Company 엔티티와 1:1 양방향 연관관계를 설정하는 코드
    // mappedBy = "user": 조인 컬럼을 만들지 않고 읽기 전용으로 동작함
    // fetch = FetchType.LAZY: 실제 사용할 때까지 DB에서 불러오지 말고, 나중에 .getStudent(), .getCompany()를 호출할 때 로딩
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Student student;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Company company;
}
