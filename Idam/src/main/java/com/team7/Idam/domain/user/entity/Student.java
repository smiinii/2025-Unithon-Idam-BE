package com.team7.Idam.domain.user.entity;

import com.team7.Idam.domain.user.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    /* <User> id를 FK + PK로 삼는 종속 엔티티 */
    @OneToOne
    @MapsId // PK
    @JoinColumn(name = "id")  // FK (User)
    private User user;

    /* <TagCategory> category_id를 FK로 삼는 종속 엔티티 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id") // FK (TagCategory)
    private TagCategory category;

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

    /*
        student_tag 테이블 생성 / <student>와 <student_tag> 다대다 관계
        -> user_id (FK to student.id)
        -> tag_id  (FK to tag_option.id)
     */
    @ManyToMany
    @JoinTable(
            name = "student_tag",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    // Student가 가지고 있는 TagOption 목록 (Set으로 중복 허용 X)
    @Builder.Default
    private Set<TagOption> tags = new HashSet<>();

    // 태그 수정
    public void setTags(Set<TagOption> tags) {
        this.tags.clear();        // 기존 태그 초기화
        this.tags.addAll(tags);   // 새 태그 추가
    }

    // 포트폴리오
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Portfolio> portfolios = new ArrayList<>();
}
