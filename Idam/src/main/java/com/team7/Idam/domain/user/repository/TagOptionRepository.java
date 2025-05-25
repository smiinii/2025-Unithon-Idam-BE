package com.team7.Idam.domain.user.repository;

import com.team7.Idam.domain.user.entity.TagCategory;
import com.team7.Idam.domain.user.entity.TagOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagOptionRepository extends JpaRepository<TagOption, Long> {
    List<TagOption> findAllByTagNameInAndCategoryId(List<String> tagNames, Long categoryId);
    List<TagOption> findAllByCategoryId(Long categoryId);

    List<TagOption> findAllByCategory(TagCategory itCategory);
}
