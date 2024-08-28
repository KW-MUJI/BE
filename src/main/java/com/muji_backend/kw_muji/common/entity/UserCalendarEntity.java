package com.muji_backend.kw_muji.common.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "userCalendar")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCalendarEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    // users : userCalendar = 1 : N
    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity users;

    // project : userCalendar = 1 : N
    @ManyToOne(targetEntity = ProjectEntity.class)
    @JoinColumn(name = "projectId", nullable = false)
    private ProjectEntity project;
}
