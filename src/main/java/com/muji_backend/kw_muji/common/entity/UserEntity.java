package com.muji_backend.kw_muji.common.entity;

import com.muji_backend.kw_muji.common.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String major;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int stuNum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER; // deafult 값: USER

    @Column
    private String image;

    // users : univCalendar = 1 : N
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UnivCalendarEntity> univCalendar = new ArrayList<>();

    // users : userCalendar = 1 : N
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCalendarEntity> userCalendar = new ArrayList<>();

    // users : resume = 1 : N
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResumeEntity> resume = new ArrayList<>();

    // users : survey = 1 : N
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SurveyEntity> survey = new ArrayList<>();

    // users : response = 1 : N
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResponseEntity> response = new ArrayList<>();

    // users : participation = 1 : N
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParticipationEntity> participation = new ArrayList<>();
}
