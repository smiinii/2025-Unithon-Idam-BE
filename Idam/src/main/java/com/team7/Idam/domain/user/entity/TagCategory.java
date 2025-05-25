package com.team7.Idam.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tag_category")
@Getter
@Setter
@NoArgsConstructor
public class TagCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String categoryName;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<TagOption> tagOptions = new ArrayList<>();

    public TagCategory(String categoryName) {
        this.categoryName = categoryName;
    }
}