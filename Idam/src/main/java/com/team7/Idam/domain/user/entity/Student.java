package com.team7.Idam.domain.user.entity;

import com.team7.Idam.domain.user.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    private Long id;

    /* User의 ID를 FK + PK로 삼는 종속 엔티티 */
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")  // FK이자 PK
    private User user;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 50)
    private String nickname;

    @Column(name = "school_name", length = 100)
    private String schoolName;

    @Column(length = 100)
    private String major;

    @Column(name = "school_id", length = 50)
    private String schoolId;

    @Column(name = "profile_image", length = 255)
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true, length = 10)
    private Gender gender;
}
