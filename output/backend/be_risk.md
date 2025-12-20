# 가치계산기 백엔드 트러블슈팅

> 가치 계산기 프로젝트 백엔드 개발 중 발생한 주요 이슈와 해결 과정

**작성자**: 이은서 <br>
**프로젝트**: 가치 계산기 백엔드 시스템  
**기술 스택**: Spring Boot 4.0, Docker, AWS EC2/RDS, GitHub Actions<br>
**깃허브 저장소**
- [프로젝트 실제 개발 레파지토리 : 개인 레포](https://github.com/str-leshs/value-calculator)<br>
- [프로젝트 오가니제이션 BE 저장소 : 개인 레포에서 작업 후 fork해두었음](https://github.com/JNU-basic-web-value-calculator/value-calculator-be)<br>

---

## 목차

1. [Docker Compose 설치 문제](#1-docker-compose-설치-문제)
2. [환경 변수 로드 실패](#2-환경-변수-로드-실패)
3. [GitHub Actions Secrets 미설정](#3-github-actions-secrets-미설정)
4. [JWT 인증 후 403 Forbidden 에러](#4-jwt-인증-후-403-forbidden-에러)
5. [프론트엔드에서 CORS 에러 발생](#5-프론트엔드에서-cors-에러-발생)

---

## 1. Docker Compose 설치 문제

### 문제 상황

EC2 인스턴스(Amazon Linux 2023)에서 Docker Compose 명령어가 작동하지 않음

```bash
docker-compose: command not found
```

### 문제 확인 및 대안 탐색

- `dnf` 패키지 매니저로 설치 시도했으나 패키지를 찾을 수 없다는 에러
- Amazon Linux 2023의 기본 저장소에 해당 패키지가 없는 것으로 판단


- Amazon Linux 2023 공식 문서 확인
- Docker 공식 문서에서 Amazon Linux용 설치 방법 검색
- `docker-compose-plugin`이 기본 저장소에 포함되지 않음을 확인


- Docker Compose 공식 GitHub에서 바이너리를 직접 다운

### 해결 방안 및 구현

```bash
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" \
  -o /usr/local/bin/docker-compose
# 실행 권한 부여
sudo chmod +x /usr/local/bin/docker-compose

docker-compose version
```
> Docker Compose 설치 완료
### 정리

- Ubuntu와 Amazon Linux의 패키지 저장소가 다르다는 점을 알게되었음
- 패키지 매니저가 아닌 바이너리 직접 설치 방법 알게 되었음

**참고 자료**:
- [Docker Compose 공식 설치 문서](https://docs.docker.com/compose/install/)
- [Amazon Linux 2023 공식 문서](https://docs.aws.amazon.com/linux/al2023/)

---

## 2. 환경 변수 로드 실패

### 문제 상황

Docker 컨테이너가 시작되었지만 애플리케이션이 정상적으로 실행되지 않음

```bash
[ec2-user@ip-172-31-37-109 app]$ docker-compose logs -f
value-calculator-app  | ***************************
value-calculator-app  | APPLICATION FAILED TO START
value-calculator-app  | ***************************
value-calculator-app  | 
value-calculator-app  | Description:
value-calculator-app  | 
value-calculator-app  | Failed to configure a DataSource: 'url' attribute is not specified
```

### 문제 확인 및 대안 탐색

- DataSource URL이 지정되지 않았다는 에러 메시지
- 환경변수가 제대로 로드되지 않은 것으로 추정

#### 가능성
- `.env` 파일이 존재하지 않음 ?
- `.env` 파일 형식이 잘못됨 ?
- `docker-compose.yml`에서 환경변수 주입 설정이 누락됨 ?

#### 가능성 하나씩 대입해보기

```bash
# .env 파일 존재 여부 확인 -> 있음
ls -la /home/ec2-user/app/

# .env 파일 내용 확인 -> 환경 변수 잘 작성되어 있음
cat .env

# 숨겨진 문자나 형식 문제 확인 -> 문제 발견
cat -A .env
```

- `.env` 파일에 불필요한 공백 같은 것이 포함되어 있었음
- GitHub Actions에서 heredoc으로 생성한 `.env` 파일에 형식 문제 발생

### 해결 과정

**GitHub Actions 워크플로우 수정**:

```yaml
- name: Deploy on EC2
  script: |
    cat > .env << 'EOF'
    DB_URL=${{ secrets.DB_URL }}
    DB_USERNAME=${{ secrets.DB_USERNAME }}
    DB_PASSWORD=${{ secrets.DB_PASSWORD }}
    KAKAO_CLIENT_ID=${{ secrets.KAKAO_CLIENT_ID }}
    KAKAO_REDIRECT_URI=${{ secrets.KAKAO_REDIRECT_URI }}
    JWT_SECRET=${{ secrets.JWT_SECRET }}
    EOF
```


```bash
cat .env
# 올바른 형식으로 생성된 것 확인하고
docker-compose down
docker-compose up -d
```
> 애플리케이션 정상작동 확인

### 정리

- 공백, 줄바꿈, 특수문자가 환경변수 로드에 영향을 줄 수 있으므로 환경 변수 파일 작성 시 주의를 해야함
- `cat -A` 명령어로 숨겨진 문자 확인이 가능함!
- GitHub Actions에서 heredoc 사용 시 따옴표 처리 방법


- 환경변수 파일 생성 후 반드시 내용 확인해야함을 깨달음
- 특수문자나 공백 문제를 미연에 방지할 수 있도록 할 것!!!!

**참고 자료**:
- [Docker Compose 환경변수 문서](https://docs.docker.com/compose/environment-variables/)
- [Bash heredoc 사용법](https://tldp.org/LDP/abs/html/here-docs.html)

---

## 3. GitHub Actions Secrets 미설정

### 문제 상황

GitHub Actions에서 배포 워크플로우는 성공했지만, EC2에서 애플리케이션이 시작되지 않음

### 문제 확인 및 대안 탐색


- GitHub Actions는 성공 
- EC2에서 컨테이너가 실행되지 않음
- 배포는 성공했지만 애플리케이션 구동 실패


- GitHub Actions의 Secrets가 변수로 치환되지 않음 -> GitHub Repository Secrets가 설정되지 않았거나 이름이 잘못됨

### 해결 방안 및 구현

**GitHub Secrets 설정 확인**:

```
GitHub Repository → Settings → Secrets and variables → Actions
```

**필요한 Secrets 목록**:

| Secret Name | 설명 |
|-------------|------|
| `EC2_HOST` | EC2 퍼블릭 IP |
| `EC2_USERNAME` | EC2 사용자명 | 
| `EC2_SSH_KEY` | SSH 개인키 |
| `DB_URL` | 데이터베이스 URL |
| `DB_USERNAME` | DB 사용자명 |
| `DB_PASSWORD` | DB 비밀번호 |
| `KAKAO_CLIENT_ID` | 카카오 클라이언트 ID |
| `KAKAO_REDIRECT_URI` | 카카오 리다이렉트 URI |
| `JWT_SECRET` | JWT 시크릿 키 |

**2. Secrets 재설정 후 재배포 -> 애플리케이션 정상 작동 확인**:

### 정리

- GitHub Secrets 사용의 필수성 및 작성 시 몇 번 더 확인해야함
- 배포 성공과 애플리케이션 실행 성공이 반드시 따라오는 것은 아님을 깨달음
- CI/CD 파이프라인의 각 단계마다 실제 결과를 확인해야함


**참고 자료**:
- [GitHub Actions Secrets 문서](https://docs.github.com/en/actions/security-guides/encrypted-secrets)

---

## 4. JWT 인증 후 403 Forbidden 에러

### 문제 상황

프론트 팀원 : 카카오 로그인 후 JWT 토큰을 받았지만, 인증이 필요한 API 호출 시 403 Forbidden 에러가 발생

```bash
# 로그인은 성공함 -> JWT 토큰 발급됨
POST /api/auth/kakao/callback
Response: {
  "accessToken": "eyJhbGc...",
  "userId": 1
}

# 그런데 인증이 필요한 API를 호출하면?
GET /api/wishlists/my/in-progress
Authorization: Bearer eyJhbGc...

Response: 403 Forbidden
```

### 문제 확인 및 대안 탐색

- 로그인은 정상적으로 완료됨
- JWT 토큰은 발급되었으나 인증 실패
- 403 에러: 인증은 되었지만 권한이 없다

#### 가능성
- JWT 토큰이 유효하지 않거나
- JWT 필터가 제대로 작동하지 않거나
- Spring Security 설정 문제?
- 권한 설정 문제?

#### 가능성 하나씩 대입해보기

```bash
# JWT 토큰 디코딩 -> userId는 정상적으로 들어있음
{
  "sub": "1",
  "iat": 1702987654,
  "exp": 1702991254
}

# 애플리케이션 로그 확인
docker logs value-calculator-app | grep JWT
# "JWT token validation failed" 메시지 없음 -> JWT는 정상적으로 검증됨

# Spring Security 필터 체인 로그 확인
# ->JwtAuthenticationFilter는 통과
# -> 하지만 권한 체크에서 실패
```

#### 4. 원인 분석

```java
// JwtAuthenticationFilter.java 확인
Authentication authentication = new UsernamePasswordAuthenticationToken(
    userId, 
    null, 
    Collections.emptyList()  //빈 권한 리스트 확인
);
```

- JWT에서 userId는 추출했지만 **권한(Authority)을 설정하지 않음**
- Spring Security는 권한이 없는 사용자로 판단하여 403 반환

### 해결 방안 및 구현

**JwtAuthenticationFilter.java 수정**

```java
// Before
Authentication authentication = new UsernamePasswordAuthenticationToken(
    userId,
    null,
    Collections.emptyList() 
);

// After
Authentication authentication = new UsernamePasswordAuthenticationToken(
    userId,
    null,
    List.of(new SimpleGrantedAuthority("ROLE_USER"))  //USER 권한 부여
);
```

**SecurityConfig.java 확인**:

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter, 
                        UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
}
```
> 정상 응답 확인


### 정리

**Spring Security의 권한 체계도 점검하고 더 공부하기**
- Authentication 객체에는 반드시 GrantedAuthority가 필요
- `ROLE_` prefix를 붙여야 Spring Security가 인식

**에러 403 vs 401 헷갈리지 말고 주의하자**
- **401 Unauthorized**: 인증 자체가 실패 (토큰 없음, 만료됨)
- **403 Forbidden**: 인증은 되었지만 권한 없음


**참고 자료**:
- [Spring Security Architecture](https://spring.io/guides/topicals/spring-security-architecture/)
- [JWT Best Practices](https://datatracker.ietf.org/doc/html/rfc8725)

---

## 5. 프론트엔드에서 CORS 에러 발생

### 문제 상황

프론트엔드에서 백엔드 API 호출 시 CORS(Cross-Origin Resource Sharing) 에러가 발생했습니다.


### 문제 확인 및 대안 탐색

- 프론트엔드에서 API 호출 시 브라우저 콘솔에 CORS 에러
- swagger 테스트 시에는 문제 없엇음
- 브라우저에서만 요청이 차단됨


#### 3. 문제 원인
- Spring Boot 애플리케이션에 CORS 설정이 없음
- 마지막에 배포 작업을 하면서 yaml 파일 수정할 때 CORS 설정을 지웠었음

- Spring에서 CORS 설정 추가
- 허용할 출처 명시
- 허용할 HTTP 메서드 및 헤더 설정

### 해결 방안 및 구현

**SecurityConfig.java에 CORS 설정 추가**:

```java
/**
 * CORS 설정 수정했음
 * - 프론트엔드 로컬 개발 환경에서 백엔드 API 호출 허용
 */
@Bean
public CorsConfigurationSource corsConfigurationSource() {
   CorsConfiguration configuration = new CorsConfiguration();

   // 허용할 출처 (Live Server 기본 포트)
   configuration.setAllowedOrigins(Arrays.asList(
           "http://localhost:5500",      // VSCode Live Server 기본 포트
           "http://127.0.0.1:5500",
           "http://localhost:5501",      // 다른 Live Server 인스턴스
           "http://127.0.0.1:5501"
           // 프론트엔드 배포 URL도 여기에 추가 가능
           // "https://your-frontend-domain.com"
   ));

   // 허용할 HTTP 메서드
   configuration.setAllowedMethods(Arrays.asList(
           "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
   ));

   // 허용할 헤더
   configuration.setAllowedHeaders(Arrays.asList("*"));

   // 인증 정보 포함 허용 (쿠키, Authorization 헤더 등)
   configuration.setAllowCredentials(true);

   // preflight 요청 캐시 시간 (초)
   configuration.setMaxAge(3600L);

   // 모든 경로에 대해 CORS 설정 적용
   UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
   source.registerCorsConfiguration("/**", configuration);

   return source;
}
```
> main 브랜치에 머지 후 프론트 재확인 
> 정상 접근 가능 확인

### 정리

- CORS의 동작 원리 한 번 더 공부하기
- 배포 전에 CORS 설정은 반드시 확인할 것

**참고 자료**:
- [MDN - CORS](https://developer.mozilla.org/ko/docs/Web/HTTP/CORS)
- [Spring CORS 공식 문서](https://spring.io/guides/gs/rest-service-cors/)

---

## 전체 회고

### 🧐 왜 이렇게 트러블 슈팅을 작성했느냐 하신다면/...
이전에는 에러를 마주하면 "어떻게 하면 빨리 해결할까?"에만 급급했습니다. 
단순히 AI에이전트나 구글링을 통해 찾은 코드를 복사해서 붙여넣고,
에러가 사라지면 "해결했다!"라며 넘어가곤 했습니다. 
하지만 시간이 흐른 뒤 비슷한 에러를 다시 만났을 때, 
과거의 제가 어떻게 해결했는지 기억나지 않아 똑같은 삽질을 반복하는 제 자신을 발견했습니다.

그래서 이번 프로젝트에서는 단순히 '해결법'만 적는 것이 아니라,
'내가 어떤 고민을 했고, 어떤 과정을 거쳐 이 결론에 도달했는지'를 기록해보기로 했습니다.

### 🧐 이번 프로젝트에서 이렇게 트러블슈팅을 정해보면서...
"아 이거 같은데"가 아니라 '현상 파악 → 가설 나열 → 검증' 단계를 만들고 문제를 해결하고자 했습니다.
   덕분에 원인이 복합적인 JWT 권한 문제나 환경 변수 오류도 차근차근 해결해 나갈 수 있습니다. 또 단순 직감이 아닌 발생할 수 있는 여러 상황들을 생각해보면서, 되려 같은 오류가 또 다른 상황에서 발생해도 그 전에 생각했던 원인들을 찾아보며 좀 더 빠른 해결책을 찾는 데 도움을 받았습니다.

더불어 에러 메시지를 대하는 태도가 바뀌었습니다. 전에는 에러 메시지를 보면 스트레스를 받고, 프론트에서 이게 안된다고 하면 긴장부터 하기 바빴습니다. 여전히 긴장되는것이 달라지진 않지만 수립한 단계별 문제 해결 알고리즘을 스스로 적용하면 그래도 해결해볼 수 있지 않을까라는 자신감도 생긴 것 같습니다.

이번 프로젝트를 진행하며 시스템의 전체적인 뼈대를 구상하고, 제가 설계한 아키텍처 위에서 데이터가 의도한 대로 흐르는 것을 확인하는 과정이 정말 즐거웠습니다. 인프라 설정부터 데이터 흐름까지 제 손으로 직접 하나씩 구현하며 시스템이 완성되어 갈 때 백엔드 개발의 진짜 매력을 느꼈습니다.

### 🧐 짧지만 이번 프로젝트를 진행하면서...
때로는 예상치 못한 문제로 어려운 상황도 있었지만, 그 과정을 거치면서 제가 설계한 구조가 더 탄탄해지는 걸 보며하게 오히려 더 욕심을 내며 몰입할 수 있었습니다. 
앞으로도 마주할 수많은 삽질의 시간들을 기꺼이 즐기며, 탄탄하고 이유있는 시스템을 만들어내는 개발자가 되고 싶다는 다짐도 하게 되었습니다."

---
