package com.muji_backend.kw_muji.common.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "survey")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SurveyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDate endDate;

    @PrePersist // entity가 영속화되기 직전에 실행
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        createdAt = now;
    }

    // users : survey = 1 : N
    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity users;

    // survey : response = 1 : N
    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResponseEntity> response = new ArrayList<>();

    // survey : question = 1 : N
    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionEntity> question = new ArrayList<>();
}
