package com.muji_backend.kw_muji.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "userEventLink")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEventLinkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // users : userEventLink = 1 : N
    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity users;

    // project : userEventLink = 1 : N
    @ManyToOne(targetEntity = ProjectEntity.class)
    @JoinColumn(name = "projectId", nullable = false)
    private ProjectEntity project;

    // userCalendar : userEventLink = 1 : N
    @ManyToOne(targetEntity = ProjectEntity.class)
    @JoinColumn(name = "userCalendarId", nullable = false)
    private UserCalendarEntity userCalendar;
}
