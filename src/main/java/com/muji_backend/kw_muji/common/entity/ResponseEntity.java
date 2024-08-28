package com.muji_backend.kw_muji.common.entity;

import jakarta.persistence.*;
import lombok.*;

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


}
