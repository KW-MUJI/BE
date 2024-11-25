package com.muji_backend.kw_muji.survey.scheduling;

import com.muji_backend.kw_muji.common.entity.SurveyEntity;
import com.muji_backend.kw_muji.survey.repository.SurveyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SurveyScheduledTask {

    private final SurveyRepository surveyRepository;

    /**
     * 매일 자정에 설문조사 상태를 업데이트
     * 크론 표현식: 매일 자정에 실행
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateSurveyStatus() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        // 현재 ongoing 상태인 설문조사 중 endDate가 오늘보다 이전인 것들을 검색
        List<SurveyEntity> ongoingSurveys = surveyRepository.findByIsOngoingTrueAndEndDateLessThan(today);

        ongoingSurveys.forEach(survey -> {
            survey.setOngoing(false);
        });

        // 한 번에 플러시
        surveyRepository.saveAll(ongoingSurveys);

        System.out.println("설문조사 상태 업데이트 작업이 완료되었습니다.");
    }
}
