package com.muji_backend.kw_muji.common.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "univCalendar")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnivCalendarEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    // users : univCalendar = 1 : N
    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity users;
}
