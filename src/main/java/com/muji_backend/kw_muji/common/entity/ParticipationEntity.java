package com.muji_backend.kw_muji.common.entity;

import com.muji_backend.kw_muji.common.entity.enums.ProjectRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "participation")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String resumePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectRole role;

    // users : participation = 1 : N
    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity users;

    // project : participation = 1 : N
    @ManyToOne(targetEntity = ProjectEntity.class)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;
}
