package com.team7.Idam.domain.user.repository;

import com.team7.Idam.domain.user.entity.TagCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagCategoryRepository extends JpaRepository<TagCategory, Long> {
    Optional<TagCategory> findByCategoryName(String categoryName);
}
