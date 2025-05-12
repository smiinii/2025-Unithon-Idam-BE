package com.team7.Idam.domain.user.entity;

import com.team7.Idam.domain.user.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

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

    @Column(length = 50, unique = true)
    private String nickname;

    @Column(name = "school_name", length = 100)
    private String schoolName;

    @Column(length = 100)
    private String major;

    @Column(name = "school_id", length = 50, unique = true)
    private String schoolId;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "profile_image", length = 255)
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true, length = 10)
    private Gender gender;

    @ManyToMany
    @JoinTable(
            name = "student_tag",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<TagOption> tags = new HashSet<>();

    public void setTags(Set<TagOption> tags) {
        this.tags.clear();        // 기존 태그 초기화
        this.tags.addAll(tags);   // 새 태그 추가
    }

}
