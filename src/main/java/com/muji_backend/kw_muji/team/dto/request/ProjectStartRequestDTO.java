package com.muji_backend.kw_muji.team.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectStartRequestDTO {
    private Long projectId;
    private List<Long> memberIdList; // 지원자 id 리스트
}
