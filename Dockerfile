#빌드 스테이지
FROM gradle:8.14-jdk21 AS builder

WORKDIR /app

# Gradle 파일 복사
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
# 의존성 다운로드 (캐싱)
RUN gradle dependencies --no-daemon || true
# 소스 코드 복사
COPY src ./src
# 빌드 (테스트 제외)
RUN gradle clean build -x test --no-daemon




#  실행 스테이지
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar
# 포트 노출
EXPOSE 8080
# 환경변수 기본값 설정 (런타임에 덮어쓰기 가능)
ENV SPRING_PROFILES_ACTIVE=prod
# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]