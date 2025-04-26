package com.team7.Idam.domain.user.repository;

import com.team7.Idam.domain.user.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}