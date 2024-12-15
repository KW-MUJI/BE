<div align="center">
    <img width="300px" alt="Title image" src="https://github.com/user-attachments/assets/9cca16cb-36a4-439c-a9ce-8013bdbe9833">
</div>

## 💡 Introduction

## ⭐ 서비스 기능

## 📜 시스템 아키텍처

## 💻 Convention

### Git Flow 전략

**브랜치 흐름**

    (개발 과정 - develop 브랜치 및 하위 브랜치)
    1. Upstream Repository에서 Issue를 생성합니다.
        1-1. Commit Convention에 해당하는 이름(대문자)과 기능 목표를 제목으로 생성합니다.
        1-2. 예시: [FEAT] 로그인 기능 구현
    2. develop 브랜치에서 Issue에 맞는 브랜치를 생성합니다.
        2-1. feature(기능 개발), fix(오류 등 수정), refactor(리팩토링) 브랜치로 생성합니다.
        2-2. 예시: feature/#{이슈 번호}_{브랜치 개발 목표} -> feature/#12_login
    3. Local에서 생성한 브랜치로 checkout하여 개발을 진행합니다.
    4. Local에서 기능 개발이 완료되면 작업 브랜치로 push합니다.
        4-1. 커밋 컨벤션은 아래의 컨벤션을 따릅니다.
        4-2. 예시: {Commit Type}: {커밋 메시지}({이슈 번호})
    5. 작업 브랜치의 내용을 develop 브랜치로 PR을 보냅니다.

    (배포 과정 - main 브랜치)
    6. Upstream Repository에서 develop 브랜치에서 최종 release될 브랜치인 main에 PR을 보냅니다.
    7. Deployments와 gitHub Actions의 설정에 따라 모두 정상 통과되면 정상 배포가 완료됩니다.


### Commit Convention

    {Commit Type}: {커밋 메시지}({이슈 번호})
    {description}

    예시: feat: 로그인 기능 구현
         - spring security 설정


| Commit Type | Description          |
|-------------|----------------------|
| feat        | 기능 개발                |
| fix         | 버그 수정                |
| docs        | 문서 수정                |
| refactor    | 코드 리팩토링              |
| test        | 테스트 관련 코드            |
| chore       | 빌드 업무 수정, 패키지 매니저 수정 |
| cicd        | 배포 관련 작업             |

## 👨‍👩‍👧‍👦 팀원 소개
