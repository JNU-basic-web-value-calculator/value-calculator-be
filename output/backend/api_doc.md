# 가치계산기 API 명세서

## 기본 정보

> 가치 계산기 프로젝트 API명세 

**작성자**: 이은서 <br>
**프로젝트**: 가치 계산기 백엔드 시스템  
**기술 스택**: Spring Boot 4.0, Docker, AWS EC2/RDS, GitHub Actions<br>
**깃허브 저장소**
- [프로젝트 실제 개발 레파지토리 : 개인 레포](https://github.com/str-leshs/value-calculator)<br>
- [프로젝트 오가니제이션 BE 저장소 : 개인 레포에서 작업 후 fork해두었음](https://github.com/JNU-basic-web-value-calculator/value-calculator-be)<br>

|||
|----------------------| --- |
| **Base URL**         | http://3.39.22.207:8080 |
| **Swagger 문서**       | http://3.39.22.207:8080/swagger-ui/index.html |
| **API 명세서 (NOTION)** | https://shrouded-line-2d9.notion.site/API-2b9d2c03fdc880ed9fedff45db4460b4?source=copy_link |

## 전체 API 목록

| No | 도메인 | 기능 | HTTP | API 경로 | 인증 |
| --- | --- | --- | --- | --- | --- |
| 1 | User(Kakao) | 카카오 로그인 URL 조회 | GET | `/api/auth/kakao/login` | ❌ |
| 2 | User(Kakao) | 카카오 로그인 콜백 | GET | `/api/auth/kakao/callback` | ❌ |
| 3 | User(Kakao) | 토큰 갱신 | POST | `/api/auth/refresh` | ❌ |
| 4 | Unit | 기본 단위 목록 조회 | GET | `/api/units/default` | ❌ |
| 5 | Unit | 내 커스텀 단위 목록 조회 | GET | `/api/units/my` | ✅ |
| 6 | Unit | 모든 단위 조회 (기본+커스텀) | GET | `/api/units/all` | ✅ |
| 7 | Unit | 커스텀 단위 생성 | POST | `/api/units` | ✅ |
| 8 | Unit | 커스텀 단위 수정 | PUT | `/api/units/{unitId}` | ✅ |
| 9 | Unit | 커스텀 단위 삭제 | DELETE | `/api/units/{unitId}` | ✅ |
| 10 | Unit | 아이콘 목록 조회 | GET | `/api/units/icons` | ❌ |
| 11 | Wishlist | 위시리스트 생성 | POST | `/api/wishlists` | ✅ |
| 12 | Wishlist | 진행 중 위시리스트 조회 | GET | `/api/wishlists/my/in-progress` | ✅ |
| 13 | Wishlist | 위시리스트 상세 조회 | GET | `/api/wishlists/{wishlistId}` | ✅ |
| 14 | Wishlist | 위시리스트 수정 | PUT | `/api/wishlists/{wishlistId}` | ✅ |
| 15 | Wishlist | 위시리스트 삭제 | DELETE | `/api/wishlists/{wishlistId}` | ✅ |
| 16 | Saving | 절약 기록 추가 | POST | `/api/savings` | ✅ |
| 17 | Saving | 절약 통계 조회 | GET | `/api/savings/statistics` | ✅ |
| 18 | Calculation | 가치 계산 | POST | `/api/calculate` | 🟡 |

## 도메인 구성

- **User(Kakao)**: 카카오 OAuth 로그인 및 JWT 발급/갱신
- **Unit**: 기본 단위 / 사용자 커스텀 단위 / 아이콘 목록
- **Calculation**: 금액을 단위로 환산(반올림 규칙 포함)
- **Wishlist**: 목표 아이템 생성/조회/수정/삭제 (진행중 조회 포함)
- **Saving**: 위시리스트에 절약 금액 누적 + 사용자 총 절약 통계

## 인증 방식

### JWT Bearer Token

인증이 필요한 API는 아래 헤더를 포함합니다.
```
Authorization: Bearer {accessToken}
```

- `accessToken`은 로그인 콜백(`/api/auth/kakao/callback`)에서 발급됩니다.
- 사용자 식별은 서버에서 `@CurrentUserId`로 처리합니다(클라이언트가 userId를 넘기지 않음)

## 공통 응답/에러 규칙

- 성공 응답은 각 API 문서의 `Response 200/201` 예시를 따릅니다.
- 일부 실패 응답은 **JSON 객체가 아니라 문자열 메시지**로 내려옵니다.

---

## 도메인 별 상세 명세

- [User(Kakao) API](#user-kakao-api)
- [Unit API](#unit-api)
- [Wishlist API](#wishlist-api)
- [Saving API](#saving-api)
- [Calculation API](#calculation-api)

---

# User (Kakao) API

> 카카오 OAuth 로그인 및 JWT(Access/Refresh) 발급/갱신을 제공하는 API

## 📌 프론트 개발자 참고 사항

1. **로그인 시작 흐름**
    - 먼저 `GET /api/auth/kakao/login` 으로 **카카오 인증 URL**을 받음
    - 프론트에서 해당 URL로 **redirect** 시켜 로그인 진행

2. **콜백 처리**
    - 카카오 로그인 후 리다이렉트되는 콜백은 백엔드 엔드포인트 `GET /api/auth/kakao/callback?code=...`
    - 응답으로 받은 `accessToken`을 이후 인증 API 호출 시 사용

3. **토큰 갱신**
    - `POST /api/auth/refresh`는 **Refresh Token이 유효하고 DB에 존재**해야 성공
    - 실패 시 **400 + 문자열 메시지**로 내려옴(에러 JSON 아님)

4. **닉네임**
    - 비즈앱이 아니라 프로필에서 **nickname만 사용** (null 가능성 있음)

## API 목록

| 기능 | HTTP | API 경로 | 인증 | Request DTO | Response DTO |
| --- | --- | --- | --- | --- | --- |
| 카카오 로그인 URL 조회 | `GET` | `/api/auth/kakao/login` | ❌ | 없음 | `String`(URL) |
| 카카오 로그인 콜백 | `GET` | `/api/auth/kakao/callback` | ❌ | 없음 | `TokenResponse` |
| 토큰 갱신 | `POST` | `/api/auth/refresh` | ❌ | `RefreshTokenRequest` | `AccessTokenResponse` 또는 에러 문자열 |

## 1. 카카오 로그인 URL 조회
```http
GET /api/auth/kakao/login
```

### Response 200 OK
```json
"https://kauth.kakao.com/oauth/authorize?client_id=...&redirect_uri=...&response_type=code"
```

> 프론트는 이 URL로 사용자를 이동(redirect)시키면 됩니다.

---

## 2. 카카오 로그인 콜백
```http
GET /api/auth/kakao/callback?code={authorization_code}
```

### Parameters

| Name | Type | Required | Description |
| --- | --- | --- | --- |
| code | string | ✅ | 카카오 인가 코드 |

### Response 200 OK — `TokenResponse`
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "nickname": "이은서"
}
```

### Response 400 Bad Request

예: 인가 코드 오류/카카오 토큰 발급 실패

---

## 3. 토큰 갱신
```http
POST /api/auth/refresh
Content-Type: application/json
```

### Request Body — `RefreshTokenRequest`
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Response 200 OK — `AccessTokenResponse`
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1
}
```

### Response 400 Bad Request

**JWT 유효성 실패**
```json
"유효하지 않은 리프레시 토큰입니다."
```

**DB에 토큰이 없음**
```json
"존재하지 않는 리프레시 토큰입니다."
```

**만료됨**
```json
"만료된 리프레시 토큰입니다. 다시 로그인해주세요."
```

**사용자 없음**
```json
"존재하지 않는 사용자입니다."
```

---

# Unit API

> 환산 단위(기본/커스텀) 및 아이콘 목록을 제공하는 API

## 📌 프론트 개발자 참고 사항

1. **커스텀 단위 생성 제한**
    - 사용자당 커스텀 단위는 **최대 3개**
    - 초과 시 `400` + 메시지 문자열 반환

2. **기본 단위 수정/삭제 불가**
    - 수정/삭제 시도 시 `400` + `"기본 단위는 ...할 수 없습니다."`

3. **권한 체크**
    - 커스텀 단위는 **본인(userId) 소유만 수정/삭제 가능**
    - 위반 시 `400` + `"본인의 단위만 ...할 수 있습니다."`

## API 목록

| 기능 | HTTP | API 경로 | 인증 | Request DTO | Response DTO |
| --- | --- | --- | --- | --- | --- |
| 기본 단위 목록 조회 | `GET` | `/api/units/default` | ❌ | 없음 | `List<UnitResponse>` |
| 내 커스텀 단위 목록 조회 | `GET` | `/api/units/my` | ✅ | 없음 | `List<UnitResponse>` |
| 모든 단위 조회(기본+커스텀) | `GET` | `/api/units/all` | ✅ | 없음 | `List<UnitResponse>` |
| 커스텀 단위 생성 | `POST` | `/api/units` | ✅ | `UnitRequest` | `UnitResponse` |
| 커스텀 단위 수정 | `PUT` | `/api/units/{unitId}` | ✅ | `UnitRequest` | `UnitResponse` 또는 에러 문자열 |
| 커스텀 단위 삭제 | `DELETE` | `/api/units/{unitId}` | ✅ | 없음 | `"커스텀 단위 삭제 성공"` 또는 에러 문자열 |
| 아이콘 목록 조회 | `GET` | `/api/units/icons` | ❌ | 없음 | `List<UnitIconResponse>` |

## 1. 기본 단위 목록 조회
```http
GET /api/units/default
```

### Response 200 OK
```json
[
  {
    "unitId": 1,
    "unitName": "커피",
    "unitPrice": 5000,
    "unitCounter": "잔",
    "iconPath": "/uploads/icons/coffee.png",
    "iconName": "coffee",
    "isDefault": true
  }
]
```

---

## 2. 내 커스텀 단위 목록 조회
```http
GET /api/units/my
Authorization: Bearer {accessToken}
```

### Response 200 OK
```json
[
  {
    "unitId": 10,
    "unitName": "스타벅스 라떼",
    "unitPrice": 5500,
    "unitCounter": "잔",
    "iconPath": "/uploads/icons/latte.png",
    "iconName": "latte",
    "isDefault": false
  }
]
```

---

## 3. 모든 단위 조회 (기본 + 커스텀)
```http
GET /api/units/all
Authorization: Bearer {accessToken}
```

### Response 200 OK
```json
[
  {
    "unitId": 1,
    "unitName": "커피",
    "unitPrice": 5000,
    "unitCounter": "잔",
    "iconPath": "/uploads/icons/coffee.png",
    "iconName": "coffee",
    "isDefault": true
  },
  {
    "unitId": 10,
    "unitName": "스타벅스 라떼",
    "unitPrice": 5500,
    "unitCounter": "잔",
    "iconPath": "/uploads/icons/latte.png",
    "iconName": "latte",
    "isDefault": false
  }
]
```

---

## 4. 커스텀 단위 생성
```http
POST /api/units
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Body — `UnitRequest`
```json
{
  "iconId": 1,
  "unitName": "스타벅스 라떼",
  "unitPrice": 5500,
  "unitCounter": "잔"
}
```

### Response 201 Created — `UnitResponse`
```json
{
  "unitId": 10,
  "unitName": "스타벅스 라떼",
  "unitPrice": 5500,
  "unitCounter": "잔",
  "iconPath": "/uploads/icons/latte.png",
  "iconName": "latte",
  "isDefault": false
}
```

### Response 400 Bad Request

**커스텀 단위 3개 초과**
```json
"커스텀 단위는 최대 3개까지만 생성할 수 있습니다."
```

**존재하지 않는 아이콘**
```json
"존재하지 않는 아이콘입니다."
```

---

## 5. 커스텀 단위 수정
```http
PUT /api/units/{unitId}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Path Parameters

| Name | Type | Required | Description |
| --- | --- | --- | --- |
| unitId | Long | ✅ | 수정할 단위 ID |

### Request Body — `UnitRequest`
```json
{
  "iconId": 2,
  "unitName": "스타벅스 라떼 Tall",
  "unitPrice": 6000,
  "unitCounter": "잔"
}
```

### Response 200 OK — `UnitResponse`
```json
{
  "unitId": 10,
  "unitName": "스타벅스 라떼 Tall",
  "unitPrice": 6000,
  "unitCounter": "잔",
  "iconPath": "/uploads/icons/latte_tall.png",
  "iconName": "latte_tall",
  "isDefault": false
}
```

### Response 400 Bad Request

**기본 단위 수정 시도**
```json
"기본 단위는 수정할 수 없습니다."
```

**본인 단위 아님**
```json
"본인의 단위만 수정할 수 있습니다."
```

**존재하지 않는 단위**
```json
"존재하지 않는 단위입니다."
```

---

## 6. 커스텀 단위 삭제
```http
DELETE /api/units/{unitId}
Authorization: Bearer {accessToken}
```

### Response 200 OK
```json
"커스텀 단위 삭제 성공"
```

### Response 400 Bad Request

**기본 단위 삭제 시도**
```json
"기본 단위는 삭제할 수 없습니다."
```

**본인 단위 아님**
```json
"본인의 단위만 삭제할 수 있습니다."
```

**존재하지 않는 단위**
```json
"존재하지 않는 단위입니다."
```

---

## 7. 아이콘 목록 조회
```http
GET /api/units/icons
```

### Response 200 OK — `List<UnitIconResponse>`
```json
[
  {
    "iconId": 1,
    "iconPath": "/uploads/icons/coffee.png",
    "iconName": "coffee"
  },
  {
    "iconId": 2,
    "iconPath": "/uploads/icons/latte.png",
    "iconName": "latte"
  }
]
```

---

# Wishlist API

> 사용자의 목표 소비/저축 아이템(위시리스트)을 관리하는 API

## 📌 프론트 개발자 참고 사항

1. **모든 위시리스트 API는 인증 필요**
    - `Authorization: Bearer {accessToken}` 필수
    - 컨트롤러에서 `@CurrentUserId`로 사용자 식별

2. **권한 체크 방식**
    - `wishlistId`로 조회 시 **반드시 userId까지 같이 조회** (`findByWishlistIdAndUserId`)
    - 즉, **본인 위시리스트가 아니면 "찾을 수 없음" 처리**

3. **삭제 응답**
    - 삭제 성공 시 `204 No Content` (응답 바디 없음)

4. **에러 형태**
    - 서비스에서 `IllegalArgumentException("위시리스트를 찾을 수 없습니다.")` 발생 가능

## API 목록

| 기능 | HTTP | API 경로 | 인증 | Request DTO | Response DTO |
| --- | --- | --- | --- | --- | --- |
| 위시리스트 생성 | `POST` | `/api/wishlists` | ✅ | `WishlistCreateRequest` | `WishlistResponse` |
| 진행 중인 위시리스트 조회 | `GET` | `/api/wishlists/my/in-progress` | ✅ | 없음 | `List<WishlistResponse>` |
| 위시리스트 상세 조회 | `GET` | `/api/wishlists/{wishlistId}` | ✅ | 없음 | `WishlistDetailResponse` |
| 위시리스트 수정 | `PUT` | `/api/wishlists/{wishlistId}` | ✅ | `WishlistUpdateRequest` | `WishlistResponse` |
| 위시리스트 삭제 | `DELETE` | `/api/wishlists/{wishlistId}` | ✅ | 없음 | 없음 (`204 No Content`) |

## 1. 위시리스트 생성
```http
POST /api/wishlists
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Body — `WishlistCreateRequest`
```json
{
  "itemName": "맥북 프로",
  "targetPrice": 3000000,
  "itemUrl": "https://www.apple.com/kr/macbook-pro"
}
```

### Response 201 Created — `WishlistResponse`
```json
{
  "wishlistId": 1,
  "userId": 1,
  "itemName": "맥북 프로",
  "targetPrice": 3000000,
  "currentAmount": 0,
  "itemUrl": "https://www.apple.com/kr/macbook-pro",
  "isCompleted": false,
  "createdAt": "2025-12-19T00:00:00"
}
```

---

## 2. 진행 중인 위시리스트 조회
```http
GET /api/wishlists/my/in-progress
Authorization: Bearer {accessToken}
```

### Response 200 OK — `List<WishlistResponse>`
```json
[
  {
    "wishlistId": 1,
    "userId": 1,
    "itemName": "맥북 프로",
    "targetPrice": 3000000,
    "currentAmount": 500000,
    "itemUrl": "https://www.apple.com/kr/macbook-pro",
    "isCompleted": false,
    "createdAt": "2025-12-19T00:00:00"
  }
]
```

> 서비스 레포지토리에서 createdAt 최신순 정렬: `findByUserIdAndIsCompletedFalseOrderByCreatedAtDesc`

---

## 3. 위시리스트 상세 조회
```http
GET /api/wishlists/{wishlistId}
Authorization: Bearer {accessToken}
```

### Path Parameters

| Name | Type | Required | Description |
| --- | --- | --- | --- |
| wishlistId | Long | ✅ | 위시리스트 ID |

### Response 200 OK — `WishlistDetailResponse`
```json
{
  "wishlistId": 1,
  "userId": 1,
  "itemName": "맥북 프로",
  "targetPrice": 3000000,
  "currentAmount": 500000,
  "itemUrl": "https://www.apple.com/kr/macbook-pro",
  "isCompleted": false,
  "completedAt": null,
  "createdAt": "2025-12-19T00:00:00",
  "updatedAt": "2025-12-19T00:00:00"
}
```

### Response 404 Not Found
```json
"위시리스트를 찾을 수 없습니다."
```

---

## 4. 위시리스트 수정
```http
PUT /api/wishlists/{wishlistId}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Path Parameters

| Name | Type | Required | Description |
| --- | --- | --- | --- |
| wishlistId | Long | ✅ | 위시리스트 ID |

### Request Body — `WishlistUpdateRequest`
```json
{
  "itemName": "맥북 프로 M3",
  "targetPrice": 3500000,
  "itemUrl": "https://www.apple.com/kr/macbook-pro-m3"
}
```

### Response 200 OK — `WishlistResponse`
```json
{
  "wishlistId": 1,
  "userId": 1,
  "itemName": "맥북 프로 M3",
  "targetPrice": 3500000,
  "currentAmount": 500000,
  "itemUrl": "https://www.apple.com/kr/macbook-pro-m3",
  "isCompleted": false,
  "createdAt": "2025-12-19T00:00:00"
}
```

### Response 404 Not Found
```json
"위시리스트를 찾을 수 없습니다."
```

---

## 5. 위시리스트 삭제
```http
DELETE /api/wishlists/{wishlistId}
Authorization: Bearer {accessToken}
```

### Path Parameters

| Name | Type | Required | Description |
| --- | --- | --- | --- |
| wishlistId | Long | ✅ | 위시리스트 ID |

### Response 204 No Content

응답 바디 없음

### Response 404 Not Found
```json
"위시리스트를 찾을 수 없습니다."
```

---

# Saving API

> 사용자의 절약 기록을 관리하는 API

## 📌 프론트 개발자 참고 사항

1. **모든 Saving API는 인증 필요**
    - `Authorization: Bearer {accessToken}` 필수
    - 컨트롤러에서 `@CurrentUserId`로 사용자 식별

2. **완료된 위시리스트에는 절약 기록 추가 불가**
    - `wishlist.isCompleted == true`면 예외 발생
    - 메시지: `"완료된 위시리스트에는 절약 기록을 추가할 수 없습니다."`

3. **권한 체크**
    - `wishlistId`로 조회 시 **userId까지 함께 조회**하여 본인 위시리스트만 허용
    - 본인 위시리스트가 아니거나 없으면 `"위시리스트를 찾을 수 없습니다."`

4. **통계 값이 없을 때**
    - 저장된 절약 기록이 없으면 `totalAmount`가 `null`일 수 있음
    - 프론트는 0으로 처리하거나, 백엔드에서 0으로 내려주도록 맞추는 걸 추천

## API 목록

| 기능 | HTTP | API 경로 | 인증 | Request DTO | Response DTO |
| --- | --- | --- | --- | --- | --- |
| 절약 기록 추가 | `POST` | `/api/savings` | ✅ | `SavingCreateRequest` | `SavingResponse` |
| 절약 통계 조회(총 절약 금액) | `GET` | `/api/savings/statistics` | ✅ | 없음 | `SavingStatisticsResponse` |

## 1. 절약 기록 추가
```http
POST /api/savings
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Body — `SavingCreateRequest`
```json
{
  "wishlistId": 1,
  "amount": 50000
}
```

### Response 201 Created — `SavingResponse`
```json
{
  "savingId": 1,
  "wishlistId": 1,
  "amount": 50000,
  "savedAt": "2025-12-19T00:00:00"
}
```

### Response 404 Not Found
```json
"위시리스트를 찾을 수 없습니다."
```

### Response 400 Bad Request
```json
"완료된 위시리스트에는 절약 기록을 추가할 수 없습니다."
```

---

## 2. 절약 통계 조회 (총 절약 금액)
```http
GET /api/savings/statistics
Authorization: Bearer {accessToken}
```

### Response 200 OK — `SavingStatisticsResponse`
```json
{
  "totalAmount": 80000
}
```

> totalAmount는 savingRepository.getTotalSavingAmount(userId) 결과 기반

---

# Calculation API

> 입력 금액을 선택한 환산 단위(Unit) 기준으로 변환하는 API

## 📌 프론트 개발자 참고 사항

1. **인증 선택사항**
    - 비로그인 사용자도 계산 API 사용 가능
    - 로그인 시: `Authorization: Bearer {accessToken}` 포함
    - 비로그인 시: 헤더 없이 요청 가능

2. **권한 체크**
    - 단위가 커스텀(`isDefault=false`)이면 `unit.userId == 현재 userId`인 경우에만 계산 가능
    - 위반 시 `400` + `"해당 환산 단위를 사용할 권한이 없습니다."`

3. **반올림 규칙**
    - 결과값 `result`는 **소수점 1자리**로 반올림됨
    - 예: `50000 / 5500 = 9.0909... → 9.1`

4. **에러 문자열 반환 가능**
    - 서비스에서 `IllegalArgumentException` 메시지를 그대로 쓰는 흐름이므로 프론트는 문자열 에러 응답도 처리 필요

## API 목록

| 기능 | HTTP | API 경로 | 인증 | Request DTO | Response DTO |
| --- | --- | --- | --- | --- | --- |
| 가치 계산 | `POST` | `/api/calculate` | 🟡(선택) | `CalculationRequest` | `CalculationResponse` |

## 1. 가치 계산
```http
POST /api/calculate
Authorization: Bearer {accessToken}  # 선택사항 (로그인 시에만 포함)
Content-Type: application/json
```

### Request Body — `CalculationRequest`
```json
{
  "amount": 50000,
  "unitId": 1
}
```

### Response 200 OK — `CalculationResponse`
```json
{
  "amount": 50000,
  "unitId": 1,
  "unitName": "커피",
  "unitPrice": 5000,
  "unitCounter": "잔",
  "result": 10.0,
  "iconPath": "/uploads/icons/coffee.png",
  "iconName": "coffee"
}
```

### Response 400 Bad Request

**환산 단위가 존재하지 않음**
```json
"환산 단위를 찾을 수 없습니다."
```

**커스텀 단위 권한 없음**
```json
"해당 환산 단위를 사용할 권한이 없습니다."
```

---

## 라이선스

Copyright © 2025 Value Calculator Project