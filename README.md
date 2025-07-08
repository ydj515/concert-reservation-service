# 콘서트 예약 서비스

## 프로젝트 소개

**콘서트 예약 서비스**는 대규모 트래픽 환경에서도 안정적으로 좌석을 예약하고 결제할 수 있는 시스템을 목표로 개발되었습니다. 이 프로젝트는 MSA(Microservice Architecture) 전환을 염두에 두고 모놀리식 아키텍처로 구현되었으며, 주요 기술적 과제는 다음과 같습니다.

- **동시성 제어**: 다수의 사용자가 동시에 좌석을 예약할 때 발생할 수 있는 문제를 해결하고 데이터 정합성을 보장합니다.
- **대기열 시스템**: 공정한 순서에 따라 사용자에게 서비스 접근 기회를 제공합니다.
- **캐싱 전략**: 자주 조회되는 데이터를 캐시에 저장하여 시스템 성능을 최적화하고 DB 부하를 줄입니다.

## 주요 기능

- **대기열 토큰 관리**: 서비스 이용을 위한 토큰을 발급하고, 대기열 순서에 따라 활성화합니다.
- **콘서트 정보 조회**: 예약 가능한 콘서트, 날짜, 좌석 정보를 조회합니다.
- **좌석 예약 및 결제**: 좌석을 임시 배정한 후, 사용자의 잔액을 통해 결제를 완료합니다.
- **잔액 관리**: 사용자의 잔액을 충전하고 조회합니다.

##  아키텍처 및 설계

이 프로젝트는 확장성과 유지보수성을 고려하여 신중하게 설계되었습니다. 주요 아키텍처 및 설계 결정은 `docs` 폴더에 상세히 기술되어 있으며, 핵심 내용은 다음과 같습니다.

- **동시성 제어**:
  - **좌석 예약**: `Redis 분산 락`과 `MySQL 비관적 락`을 함께 사용하여 여러 인스턴스 환경에서도 데이터 정합성을 유지하고 동시성을 안전하게 제어합니다.
  - **잔액 충전**: `비관적 락`을 적용하여 충전 중 다른 트랜잭션의 접근을 막아 데이터 무결성을 보장합니다.
  - 관련 문서: [동시성 이슈 해결 방안](./docs/concurrency/concurrency.md)

- **대기열 시스템**:
  - `Redis Sorted Set`을 활용하여 대기열을 구현하고, 스케줄러를 통해 공정하게 토큰을 활성화합니다. 이를 통해 사용자에게 순차적인 서비스 접근을 보장합니다.
  - 관련 문서: [Redis 활용 방안](./docs/redis/redis.md)

- **MSA 전환 설계**:
  - 향후 MSA로의 원활한 전환을 위해 서비스 간 결합도를 낮추고 API 기반으로 통신하도록 설계했습니다.
  - 관련 문서: [MSA 전환 설계](./docs/convert-MSA/explain.md)

## 주요 문서

프로젝트의 상세한 설계 및 구현 내용은 아래 문서에서 확인하실 수 있습니다.

- **요구사항**: [요구사항 정의서](./docs/요구사항정의서.md)
- **API 명세**: [API 명세서](./docs/api-spec/api-spec.md) / [Swagger](https://app.swaggerhub.com/apis/dongjinyoo/reservation-concert/1.0.0)
- **데이터베이스**: [ERD](./docs/erd/erd.png) / [인덱스 설계](./docs/database-index/index.md)
- **시퀀스 다이어그램**: [상세 흐름도](./docs/sequence-diagram/sequence.md)
- **테스트**: [통합 테스트](./docs/integration-test/통합테스트.md) / [부하 테스트 보고서](./docs/load-test/부하테스트보고서.md)
- **회고**: [1차 회고](./docs/retrospect/1차회고.md)

##  기술 스택

- **언어**: Kotlin, Java 17
- **프레임워크**: Spring Boot 3.3.5
- **데이터베이스**: MySQL, Redis
- **테스트**: Kotest, Mockk, K6
- **기타**: Ktlint, Gradle

##  시작하기

### 1. Git Hooks 설정

```shell
./gradlew installLocalGitHook
./gradlew addKtlintCheckGitPreCommitHook
```

### 2. 설정 파일 생성

```shell
cp .env-sample .env
cp src/main/resources/application-sample.yml src/main/resources/application.yml
```

### 3. 더미 데이터 생성

```shell
pip install -r requirements.txt
python dummy_data_maker.py
```

### 4. 부하 테스트 실행

```shell
K6_WEB_DASHBOARD=true k6 run --tag test=my-load-test --out influxdb=http://localhost:8086/k6 ./k6/load_test.js
```

## 패키지 구조

```
src
└── main
    └── kotlin
        └── io
            └── hhplus
                └── concertreservationservice
                    ├── application
                    │   ├── client
                    │   ├── consumer
                    │   ├── facade
                    │   ├── helper
                    │   ├── job
                    │   └── listener
                    ├── common
                    │   ├── exception
                    │   ├── filter
                    │   ├── interceptor
                    │   └── response
                    ├── config
                    ├── domain
                    │   ├── balance
                    │   ├── concert
                    │   ├── payment
                    │   ├── reservation
                    │   ├── token
                    │   └── user
                    ├── infrastructure
                    │   ├── config
                    │   ├── eventpublisher
                    │   ├── lock
                    │   └── persistence
                    └── presentation
                        ├── advice
                        ├── constants
                        └── controller
```
