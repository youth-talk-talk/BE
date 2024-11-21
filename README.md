<img width="880" alt="0" src="https://github.com/user-attachments/assets/0ae0cf78-dc04-4284-a77c-f20671bd3ad6">
<img width="800" alt="1" src="https://github.com/user-attachments/assets/ae311514-d7e7-4ed9-b08c-707dbc7b7fb7">
<img width="800" alt="2" src="https://github.com/user-attachments/assets/5a0dff03-3893-4cc7-82a6-5b65e56b1ca7">
<img width="800" alt="3" src="https://github.com/user-attachments/assets/7de375ba-2fb4-4ec5-a8da-fe6050882c2f">
<img width="800" alt="4" src="https://github.com/user-attachments/assets/3c3b6f26-6739-4ffa-9ef0-d675a99698c8">
<img width="800" alt="5" src="https://github.com/user-attachments/assets/219c9427-2fea-483b-bbb6-22d10397f071">

<br><br>
## 서비스 개요

**청년톡톡**은 청년들을 위한 맞춤형 정보와 커뮤니티를 제공하는 iOS 및 Android 앱 서비스입니다. 취업, 창업, 문화생활 등 여러 분야에서 청년들이 필요로 하는 지원 정책과 프로그램 정보를 쉽게 제공하며, 사용자들이 자신의 경험을 공유할 수 있는 커뮤니티 기능을 갖추고 있습니다. 향후 청년들의 요구에 따라 청년 공간 등 다양한 기능으로 확장될 예정입니다. 청년들의 삶에 실질적인 도움을 주고자 하는 플랫폼으로의 성장을 목표로 하고 있습니다.

<br><br>
## 앱 다운 링크
- **android**
    - [구글 스토어 다운 받기](https://play.google.com/store/apps/details?id=com.youth.yongproject.app&hl=ko)
- **ios**
  - 24년 12월 내 출시 예정


<br><br>
## 프로젝트 소개
- **개발 기간** : 2024.04 ~ ing
- **백엔드 팀원**: [신은혜](https://github.com/HideOnCodec), [한슬기](https://github.com/ssggii), [허은정](https://github.com/EunjeongHeo)


<br><br>
## 기술 스택
| **카테고리**        | **스택**                                                                                                                |
|---------------------|------------------------------------------------------------------------------------------------------------------------|
| **IDE**             | IntelliJ                                                                                                               |
| **Language**        | Java 17                                                                                                                |
| **Framework**       | Spring Boot 3                                                                                       |
| **Database**        | MySQL, H2                                                                                                             |
| **ORM**             | Spring Data JPA, Querydsl                                                                                              |
| **Authentication**  | OAuth2, Spring Security                                                                                        |
| **Deploy**          | AWS EC2, RDS, S3                                                                                                 |
| **CI/CD**           | Jenkins, Blue/Green                                                                                                                |


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

