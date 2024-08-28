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
}
