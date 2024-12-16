<div align="center">
    <a href="https://kwmuji.com/">
    <img width="300px" alt="KW Muji" src="https://github.com/user-attachments/assets/9cca16cb-36a4-439c-a9ce-8013bdbe9833">
    </a>
    <h3>"학생 중심의 학업 및 협업 효율화 플랫폼"</h3>
</div>

<br/>

## 💡 프로젝트 개요

대학 생활을 하게 되면 많은 팀 프로젝트와 설문 조사를 진행하게 됩니다.

팀원을 모집 및 설문 조사를 하기 위해, 대학교 유명 커뮤니티 에브리타임에 글을 올리거나 카카오톡 등 SNS를 통해 개인적인 연락을 하고 있습니다.

하지만, 이 플랫폼들은 팀원 모집과 설문 조사와는 맞지 않는 플랫폼으로 많은 사람들에게 관심을 끌기 어려우며, 이는 학업에 악영향을 끼치게 됩니다.

그래서 광운대학교 학생들의 **팀 프로젝트 효율성을 향상**시키고 **학생들의 학업과 대외활동을 체계적으로 관리**하도록 돕고자, **대학 생활을 통합적으로 관리할 수 있는 플랫폼,** <광운 대학 생활 도우미>를 만들게 되었습니다.

<br/>

## ⭐ 서비스 기능



<br/>

## ⚒️ 시스템 아키텍처

<img width="100%" src="https://github.com/user-attachments/assets/df2e466c-98f7-42e5-a033-efec3cc69cd7" alt="시스템 아키텍처"/>

<br/>

## 📜 ERD

<img width="100%" src="https://github.com/user-attachments/assets/02071288-edbe-46af-96a1-5b288951370a" alt="ERD"/>

<br/>

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

<br/>

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

<br/>

## 👨‍👩‍👧‍👦 팀원 소개

<table >
  <tr height="50px">
    <td align="center" width="130px">
      <a href="https://github.com/cjh-19">최지훈</a>
    </td>
    <td align="center" width="130px">
      <a href="https://github.com/pipi-shortstocking">김정윤</a>
    </td>
    <td align="center" width="130px">
      <a href="https://github.com/goodsmell">조은향</a>
    </td>
    <td align="center" width="130px">
      <a href="https://github.com/minggong222">김민곤</a>
    </td>
  </tr>
  <tr height="130px">
    <td align="center" width="130px">
      <a href="https://github.com/cjh-19"><img src="https://avatars.githubusercontent.com/u/66457014?v=4" style="border-radius:50%"/></a>
    </td>
    <td align="center" width="130px">
      <a href="https://github.com/pipi-shortstocking"><img src="https://avatars.githubusercontent.com/u/95032287?v=4" style="border-radius:50%" /></a>
    </td>
    <td align="center" width="130px">
      <a href="https://github.com/goodsmell"><img src="https://avatars.githubusercontent.com/u/87801306?v=4" style="border-radius:50%"/></a>
    </td>
    <td align="center" width="130px">
      <a href="https://github.com/minggong222"><img src="https://avatars.githubusercontent.com/u/144299899?v=4" style="border-radius:50%"/></a>
    </td>
  </tr>
  <tr height="50px">
    <td align="center" width="130px">
      팀장, BE, DB, Infra(CI/CD) 
    </td>
    <td align="center" width="130px">
      BE, DB
    </td>
    <td align="center" width="130px">
      FE
    </td>
    <td align="center" width="130px">
      FE
    </td>
  </tr>
</table>