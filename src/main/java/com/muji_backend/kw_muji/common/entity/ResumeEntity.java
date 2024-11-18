package com.muji_backend.kw_muji.common.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "resume")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResumeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String resumePath;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist // entity가 영속화되기 직전에 실행
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        createdAt = now;
    }

    // users : resume = 1 : N
    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity users;
}
