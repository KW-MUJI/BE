package com.muji_backend.kw_muji.common.entity;

import jakarta.persistence.*;
import lombok.*;

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
    private String resumePath;

    // users : resume = 1 : N
    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity users;
}
