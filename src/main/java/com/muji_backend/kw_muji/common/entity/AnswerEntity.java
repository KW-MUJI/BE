package com.muji_backend.kw_muji.common.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "answer")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String answerText;

    // response : answer = 1 : N
    @ManyToOne(targetEntity = ResponseEntity.class)
    @JoinColumn(name = "responseId", nullable = false)
    private ResponseEntity response;

    // question : answer = 1 : N
    @ManyToOne(targetEntity = QuestionEntity.class)
    @JoinColumn(name = "questionId", nullable = false)
    private QuestionEntity question;

    // choice : answer = 1 : N
    @ManyToOne(targetEntity = ChoiceEntity.class)
    @JoinColumn(name = "choiceId")
    private ChoiceEntity choice;
}
