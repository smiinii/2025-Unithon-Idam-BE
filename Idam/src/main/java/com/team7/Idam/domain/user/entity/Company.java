package com.team7.Idam.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "company")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")  // FK이자 PK
    private User user;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "business_registration_number", nullable = false, unique = true, length = 50)
    private String businessRegistrationNumber;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(length = 200)
    private String address;

    @Column(length = 255)
    private String website;

    @Column(name = "profile_image", length = 255)
    private String profileImage;
}