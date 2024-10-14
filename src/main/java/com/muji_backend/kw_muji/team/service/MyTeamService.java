package com.muji_backend.kw_muji.team.service;

import com.muji_backend.kw_muji.common.entity.ParticipationEntity;
import com.muji_backend.kw_muji.common.entity.UserEntity;
import com.muji_backend.kw_muji.common.entity.enums.ProjectRole;
import com.muji_backend.kw_muji.team.dto.response.ApplicantResponseDTO;
import com.muji_backend.kw_muji.team.dto.response.MemberResponseDTO;
import com.muji_backend.kw_muji.team.dto.response.MyCreatedProjectResponseDTO;
import com.muji_backend.kw_muji.team.dto.response.MyProjectResponseDTO;
import com.muji_backend.kw_muji.team.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class MyTeamService {
    private final RoleRepository roleRepo;

    public List<MyProjectResponseDTO> getMyProjects(final UserEntity user) {
        final List<ParticipationEntity> participationList = roleRepo.findAllByUsersAndRole(user, ProjectRole.MEMBER); // 내가 맴버로 참가한 참가자 리스트

        return participationList.stream().map(list -> {
            final MyProjectResponseDTO myProjectResponseDTO = new MyProjectResponseDTO();
            myProjectResponseDTO.setName(list.getProject().getName());

            final List<MemberResponseDTO> members = new ArrayList<>();
            final List<ParticipationEntity> participations = new ArrayList<>();
            participations.addAll(roleRepo.findAllByProjectAndRole(list.getProject(), ProjectRole.CREATOR));
            participations.addAll(roleRepo.findAllByProjectAndRole(list.getProject(), ProjectRole.MEMBER));

            for(ParticipationEntity participation : participations) {
                final MemberResponseDTO member = MemberResponseDTO.builder()
                        .image(participation.getUsers().getImage())
                        .name(participation.getUsers().getName())
                        .stuNum(participation.getUsers().getStuNum())
                        .major(participation.getUsers().getMajor())
                        .email(participation.getUsers().getEmail())
                        .build();
                members.add(member);
            }

            myProjectResponseDTO.setMembers(members);
            return myProjectResponseDTO;
        }).toList();
    }

    public List<MyCreatedProjectResponseDTO> getMyCreatedProjects(final UserEntity user) {
        final List<ParticipationEntity> participationList = roleRepo.findAllByUsersAndRole(user, ProjectRole.CREATOR); // 내가 생성한 프로젝트

        return participationList.stream().map(list -> {
            final MyCreatedProjectResponseDTO myCreatedProjectResponseDTO = new MyCreatedProjectResponseDTO();
            myCreatedProjectResponseDTO.setName(list.getProject().getName());

            final List<ApplicantResponseDTO> members = new ArrayList<>();
            final List<ParticipationEntity> applicants = new ArrayList<>();
            applicants.addAll(roleRepo.findAllByProjectAndRole(list.getProject(), ProjectRole.APPLICANT));

            for(ParticipationEntity applicant : applicants) {
                final ApplicantResponseDTO member = ApplicantResponseDTO.builder()
                        .id(applicant.getId())
                        .image(applicant.getUsers().getImage())
                        .name(applicant.getUsers().getName())
                        .stuNum(applicant.getUsers().getStuNum())
                        .major(applicant.getUsers().getMajor())
                        .resume(applicant.getResumePath())
                        .build();
                members.add(member);
            }

            myCreatedProjectResponseDTO.setApplicants(members);
            return myCreatedProjectResponseDTO;
        }).toList();
    }
}
