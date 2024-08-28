package com.muji_backend.kw_muji.common.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "response")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // survey : response = 1 : N
    @ManyToOne(targetEntity = SurveryEntity.class)
    @JoinColumn(name = "surveyId", nullable = false)
    private SurveryEntity survey;

    // users : response = 1 : N
    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity users;

    // response : answer = 1 : N
    @OneToMany(mappedBy = "response", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnswerEntity> answer = new ArrayList<>();
}
