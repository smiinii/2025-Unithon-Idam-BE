package com.team7.Idam.domain.user.repository;

import com.team7.Idam.domain.user.entity.TagCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagCategoryRepository extends JpaRepository<TagCategory, Long> {
}
