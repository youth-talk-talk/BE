# 청년톡톡

<br><br>
## 프로젝트 개요

**청년톡톡**은 청년들을 위한 맞춤형 정보와 커뮤니티를 제공하는 iOS 및 Android 앱 서비스입니다. 취업, 창업, 문화생활 등 여러 분야에서 청년들이 필요로 하는 지원 정책과 프로그램 정보를 쉽게 제공하며, 사용자들이 자신의 경험을 공유할 수 있는 커뮤니티 기능을 갖추고 있습니다. 향후 청년들의 요구에 따라 청년 공간 등 다양한 기능으로 확장될 예정입니다. 청년들의 삶에 실질적인 도움을 주고자 하는 플랫폼으로의 성장을 목표로 하고 있습니다.
  
<br><br>
## 주요 기능

- **회원 관리**: 회원가입, 로그인
- **정책 정보**: 회원이 설정한 지역에 따른 다양한 청년 정책 정보 제공
- **후기 게시판**: 정책별 게시글 작성, 댓글 작성 기능
- **자유 게시판**: 자유 게시글 작성, 댓글 작성 기능

<br><br>
## 프로젝트 기간
- 2024.04 ~ ing (9~10월 내로 출시 예정)

<br><br>
## 백엔드 프로젝트 팀원
- **팀원**: [신은혜](https://github.com/HideOnCodec), [한슬기](https://github.com/ssggii), [허은정](https://github.com/EunjeongHeo)

<br><br>
## 기술 스택
| **카테고리**        | **스택**                                                                                                                |
|---------------------|------------------------------------------------------------------------------------------------------------------------|
| **IDE**             | IntelliJ                                                                                                               |
| **Language**        | Java 17                                                                                                                |
| **Framework**       | Spring Boot 3.2.5, Spring Cloud                                                                                         |
| **Database**        | MySQL, H2                                                                                                             |
| **ORM**             | Spring Data JPA, Querydsl                                                                                              |
| **Authentication**  | OAuth2, JWT (JSON Web Tokens)                                                                                          |
| **Deployment**      | AWS EC2, RDS, S3, Nginx                                                                                                 |
| **CI/CD**           | Jenkins                                                                                                                |
| **Collaboration Tools** | Notion, Slack, Postman API 명세서                                                                                  |


<br><br>
## 아키텍처
<img src="https://github.com/user-attachments/assets/eea0cc93-aae6-4075-a454-54265caafc8a" alt="youthtalktalk_origin drawio" width="600"/>

<br><br>
## ERD
<img src="https://github.com/user-attachments/assets/2a7f4da2-690c-4142-8d9f-8e6af8612ae6" alt="ERD" width="600"/>



<br><br>
## 폴더 구조

```
📂 BE-submodule-data

📁 youthtalktalk
├── 📁 controller
│   ├── 📁 comment
│   ├── 📁 member
│   ├── 📁 policy
│   └── 📁 post
├── 📁 domain
│   ├── 📁 comment
│   ├── 📁 member
│   ├── 📁 policy
│   └── 📁 post
├── 📁 dto
│   ├── 📁 comment
│   ├── 📁 member
│   ├── 📁 policy
│   └── 📁 post
├── 📁 global
│   ├── 📁 config
│   ├── 📁 jwt
│   ├── 📁 login
│   ├── 📁 response
│   │   └── 📁 exception
│   └── 📁 util
├── 📁 repository
└── 📁 service
    ├── 📁 comment
    ├── 📁 image
    ├── 📁 member
    ├── 📁 policy
    └── 📁 post
```

