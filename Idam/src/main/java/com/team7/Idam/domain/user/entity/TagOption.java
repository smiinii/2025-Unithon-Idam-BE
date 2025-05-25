package com.team7.Idam.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tag_option")
@Getter
@Setter
@NoArgsConstructor
public class TagOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;

    @Column(nullable = false, length = 100)
    private String tagName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private TagCategory category;

    @ManyToMany(mappedBy = "tags")
    private List<Student> students = new ArrayList<>();

    public TagOption(String tagName, TagCategory category) {
        this.tagName = tagName;
        this.category = category;
    }
}
