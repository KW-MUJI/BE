package com.muji_backend.kw_muji.common.entity;

import com.muji_backend.kw_muji.common.entity.enums.QuestionType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "question")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType questionType;

    // survey : question = 1 : N
    @ManyToOne(targetEntity = SurveryEntity.class)
    @JoinColumn(name = "surveyId", nullable = false)
    private SurveryEntity survey;

    // question : choice = 1 : N
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChoiceEntity> choice = new ArrayList<>();

    // question : answer = 1 : N
    @OneToMany(mappedBy= "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnswerEntity> answer = new ArrayList<>();
}
