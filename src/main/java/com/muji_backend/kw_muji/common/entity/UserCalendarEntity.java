package com.muji_backend.kw_muji.common.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    // userCalendar : userEventLink = 1 : N
    @OneToMany(mappedBy = "userCalendar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserEventLinkEntity> userEventLink = new ArrayList<>();
}
