# concert-reservation-service

this is concert-reservation-service

## environment

1. springboot3.3.5
2. kotlin
3. java 17
4. [kotest][kotest] & [mockk][mockk]
5. [ktlint12][ktlint]

## project setup

1. git hooks setting  
   git hooks를 세팅합니다.
    ```shell
    ./gradlew installLocalGitHook # commit-msge
    ./gradlew addKtlintCheckGitPreCommitHook # pre-commit
    ```
2. create config files  
   프로젝트에 필요한 환경변수 및 설정 파일을 세팅합니다.
    ```shell
    cp .env-sample .env
    cp src/main/resources/application-sample.yml src/main/resources/application.yml
    ```
