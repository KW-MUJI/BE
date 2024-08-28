package com.muji_backend.kw_muji.common.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "choice")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChoiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String choiceText;

    // question : choice = 1 : N
    @ManyToOne(targetEntity = QuestionEntity.class)
    @JoinColumn(name = "questionId", nullable = false)
    private QuestionEntity question;

    // choice : answer = 1 : N
    @OneToMany(mappedBy = "choice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnswerEntity> answer = new ArrayList<>();
}
