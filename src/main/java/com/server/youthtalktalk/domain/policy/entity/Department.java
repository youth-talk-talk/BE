package com.server.youthtalktalk.domain.policy.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@RequiredArgsConstructor
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long departmentId;

    @Column(nullable = false, length = 7, unique = true)
    private String code;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(length = 500)
    private String image_url;

    @OneToMany(mappedBy = "department")
    private List<Policy> policies = new ArrayList<>();

    @Builder
    public Department(Long departmentId, String code, String name, String image_url) {
        this.departmentId = departmentId;
        this.code = code;
        this.name = name;
        this.image_url = image_url;
    }
}