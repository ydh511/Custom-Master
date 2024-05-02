# [2주차] Project_4팀 진행상황 공유

## 팀 구성원, 개인 별 역할

---

프로젝트 팀 구성원을 기재해 주시고, 그 주의 팀원이 어떤 역할을 맡아서 개발을 진행했는지 구체적으로 작성해 주세요. 🙂 

- 양동화(팀장) - 인증,인가 구현
- 김슬찬
- 김세인
- 박지수 - 아이디어톤 PPT 제작, 주문 승락/거절 구현, 리뷰 구현
- 오시은
- 장수빈

## 팀 내부 회의 진행 회차 및 일자

---

예) 1회차(2024.03.18) 구글 밋 진행, (OOO님 불참)

- 일주일 간 진행한 내부 회의 횟수와 일자, 진행 방법, 불참 인원을 위와 같이 작성해 주세요.

## 현재까지 개발 과정 요약

---

현재까지 진행하고 있는 개발 현황을 기능별 목표, 목표달성률, 성과자체평가(상세히) 작성해주세요.

- 양동화
    - 기능별 목표 : 토큰 만료시 재발급 기능
    - 기능별 목표 : 자바스크립트로 토큰 다루기
        - 현재 진행률 10%
        - 자바스크립트로 프론트에서 토큰을 저장하고 불러오는 과정을 구현해야 한다.
        - 하지만 자바스크립트 문법을 하나도 몰라서 자바스크립트를 공부부터 하고있다.
        
    - 목표 달성률  50%
        - 토큰 만료시 `ExpiredException`이 발생하게 되는데 이는 스프링에서 발생하는 예외가 아니라 필터에서 발생하는 예외처리라 dispatcherServlet까지 들어오지 못하고 필터단에서 예외처리되어 나가버린다.
        
        ![dispatcher.png](https://github.com/likelion-backend-8th-pj2-team4-JuDBu/Custom-Master/assets/70869505/df829466-7bd3-4fc2-a39d-02d6035a9246)
        
        - 때문에 필터에서 발생하는 예외를 따로 예외처리를 구분해서 해줘야한다.
        - 방법이 대략 2가지가 있는 것 같다.
            - 첫 번째는 customAuthenticationEntryPoint를 만들고 `securityFilterChain` 에 추가하는 것이다.
            
            ```java
            public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                    log.info("web");
                    http
                            .csrf(AbstractHttpConfigurer::disable)
                            .authorizeHttpRequests(authorizeHttpRequests ->
                                    authorizeHttpRequests                       
                            )
            
                            )
                            .logout(logout -> logout
                                    .logoutUrl("/account/logout")
                                    .logoutSuccessUrl("/account/home")
                                    .invalidateHttpSession(true)
                                    .clearAuthentication(true)
                                    .deleteCookies("JSESSIONID", "jwtToken")
                            )
                            .exceptionHandling(e -> e
                                    .authenticationEntryPoint(new JwtAuthenticationEntryPoint()))
                            .addFilterBefore(new JwtTokenFilter(jwtTokenUtils, manager),
                                    AuthorizationFilter.class)
            
                    ;
                    return http.build();
                }
            ```
            
            - 두 번째는 예외처리하는 필터를 하나 만들어서  `securityFilterChain` 에 추가하는 것이다.
            
            ```java
            public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                    log.info("web");
                    http
                            .csrf(AbstractHttpConfigurer::disable)
                            .authorizeHttpRequests(authorizeHttpRequests ->
                                    authorizeHttpRequests                       
                            )
            
                            )
                            .logout(logout -> logout
                                    .logoutUrl("/account/logout")
                                    .logoutSuccessUrl("/account/home")
                                    .invalidateHttpSession(true)
                                    .clearAuthentication(true)
                                    .deleteCookies("JSESSIONID", "jwtToken")
                            )
                        
                            .addFilterBefore(new JwtTokenFilter(jwtTokenUtils, manager),
                                    AuthorizationFilter.class)
                            .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class)
            
                    ;
                    return http.build();
                }
            ```
            
            - 현재 첫 번째 방법으로 시도하고 있으나 필터인 `doFilterInternal` 에서 getWriter()관련 오류가 발생해서 해결방법을 찾고있다.
- 김세인
    - Review 기능 구현
- 박지수
    - **기능별 목표**: 주문 승락/거절하기 기능 추가
    - **목표 달성률**: 100%
        - `order-accept/{shopId}/read-all`에서 특정 shop에 요청된 주문을 확인 가능하다.
            - `Pagenation`이 추가되어 있다.
            - 최대 10개의 데이터가 한 페이지에 노출된다.
        - `order-accept/{shopId}/read/{ordId}`에서 주문을 상세보기 가능하다.
            - 주문의 요청사항, 이미지, 상태를 확인 가능하다.
            - 주문의 상태를 확인하고, 주문을 승락/거절할 수 있다.
            - 주문의 승락은 최종 금액을 입력해 결정할 수 있다.
            - 주문을 거절하게 되면, 주문이 삭제된다.
    
    ---
    
    - **기능별 목표**: 리뷰 CRUD 기능 추가
    - **목표 달성률**: 100%
        - `review/{shopId}/read-all`에서 특정 shop에 등록된 review를 확인 가능하다.
            - `Pagenation`이 추가되어 있다.
            - 최대 10개의 데이터가 한 페이지에 노출된다.
            - 리뷰를 등록한 사람의 이름, 내용이 노출된다.
        - `review/{shopId}/create`에서 특정 shop에 대한 review를 작성 가능하다.
            - 등록하려는 사람의 `Account` 정보가 저장된다.
            - 리뷰에 등록하려는 내용을 포함해 리뷰를 등록할 수 있다.
        - `review/{shopId}/read/{reviewId}`에서 특정 리뷰를 상세보기 할 수 있다.
            - 해당 페이지에서 리뷰 수정하기, 리뷰 삭제하기의 요청이 가능하다.
        - `review/{shopId}/update/{reviewId}`에서 등록된 리뷰를 수정 가능하다.
        - `reveiw/{shopId}/delete/{reviewId}`에서 등록된 리뷰를 삭제 가능하다.
- 오시은
    - 
- 장수빈
    - 기능별 목표: 결제 하기
    - 목표 달성률: 80% - 자체적으로 추가한 상품의 결제 가능, 사용자의 마이페이지 구현이 완료 되면 연결 예정
        - 임시 사용 예시
        - `OrdDto` 의 `shopId` , `accountId`와 `ProductDto` 의 `shopId` 를 주석처리 ( 아직 생성되지 않아서 오류 발생)
        - `localhost:8080/pay` 에 접속 후 상품 주문 또는 주문 전체 목록을 볼 수 있다.
        - 상품 주문을 하면 주문 목록에 업데이트 되며 상태는 `CONFIRM` 으로 저장된다.
        - 주문 전체 목록을  누르면 지금까지 한 주문 목록들을 볼 수 있다.
        - 각 주문은 주문 상세보기를 통해 상품 이름과 가격을 볼 수 있고 주문 상태(`CONFIRM` : 결제 가능, `PAID` : 결제 완료 )에 따라 결제를 진행할 수 있다.
        - 결제가 완료되면 상품목록 또는 주문 전체 목록으로 돌아갈 수 있다.
        - 결제가 완료되면 `CONFIRM` 이 `PAID` 로 업데이트 되며 `PAID` 가 된 주문은 상세보기에 결제하기 버튼이 사라진다.
    - 성과자체평가: 결제 기능을 어느 정도 완성했고 추후 다른 페이지와 연결하면 기능 구현이 완성될 것 같다. 이번에 결제 기능 위젯을 추가하기 위해 html에서 script 부분에 코드를 작성해야 할게 많았는데 post, get mapping 했던 백엔드 로직 부분을 불러 올 수 있는 방법에 대해 알게 되었다. 여태까지 베워왔던 다른 언어들과 형식이 조금 달라서 어려웠다.
- 김슬찬
    - 기능별 목표: 상점 개설, 리스트 조회, 상세 조회, 정보 수정, 삭제 기능 구현
    - 목표달성률: 80%
    1. 상점 개설하기
        - 목표달성률: 80%
        - 성과자체평가:
            - Thymeleaf를 이용한 폼 데이터 처리 학습
            - 기초적인 유효성 검증 및 오류 처리 구현
    2. 리스트 조회
        - 목표달성률: 100%
        - 성과자체평가:
            - Thymeleaf를 활용한 페이지네이션 구현
            - 검색 및 필터링 추가를 통한 기능 향상 가능
        - 목표달성률: 80%
        - 성과자체평가:
            - Thymeleaf를 활용한 페이지네이션 구현
            - 검색 및 필터링 추가를 통한 기능 향상 가능
    3. 상세 조회
        - 목표달성률: 80%
        - 성과자체평가:
            - Thymeleaf를 이용한 동적 정보 표시 구현
            - 추가 정보 표시 및 사용자 경험 향상이 필요
    4. 정보 수정
        - 목표달성률: 80%
        - 성과자체평가:
            - Thymeleaf를 활용한 폼 데이터 수정 구현
            - 변경 이력 관리 및 권한 제어 추가 필요
    5. 삭제
        - 목표달성률: 80%
        - 성과자체평가:
            - Thymeleaf를 활용한 데이터 삭제 구현
            - 삭제 이후 후속 작업 및 복구 기능 추가 필요

## 개발 과정에서 나왔던 질문 (최소 200자 이상)

---

개발을 진행하며 나왔던 질문 중 핵심적인 것을 요약하여 작성해 주세요 🙂

- 질의응답 과정 중 해결되지 않은 질문을 정리하여도 좋습니다.

### Q.

- review 백엔드 기능 구현 및 프론트 구현 완료하였는데, Account 계정을 가져와서 계정이 작성한 리뷰를 보여주는데 어려움이 있다.

### A.

- `auth`폴더의 `AuthenticationFacade`에서 `getAccount` 사용하면 인증된 사용자 계정 정보 받아오는 것이 가능하다. 해당 방법을 이용해 `Account`정보를 사용할 수 있다.
- HTML에서 토큰 불러오는게 문제라면 그 부분은 `script`를 사용해서 가져와야 한다. 해당 방법으로 백엔드의 토큰을 프론트로 전달 가능하다.

```java
<script>
    $(document).ready(function () {
        // 로그인 페이지에서 저장한 토큰을 가져옴
        const token = localStorage.getItem("jwtToken");
        const headers = {};

        // 토큰이 있으면 헤더에 추가
        if (token) {
            headers["Authorization"] = "Bearer " + token; // 토큰을 Authorization 헤더에 담아 전달
        }

        console.log(token);
});
</script>
```

- 만약, 로그인 한 계정이라 계정에 토큰 정보가 저장되어 있다면 토큰 정보는 아마 `jwtTokenFilter`의 `doFilter` 수행 과정에서 콘솔에 확인 가능하다.

## 개발 결과물 공유

---

Github Repository URL: https://github.com/likelion-backend-8th-pj2-team4-JuDBu/Custom-Master

- 필수) 팀원들과 함께 찍은 인증샷(온라인 만남시 스크린 캡쳐)도 함께 업로드 해주세요
![2주차1](https://github.com/likelion-backend-8th-pj2-team4-JuDBu/Custom-Master/assets/70869505/ad8e9586-a111-4afe-b21a-090afc659c52)
![2주차2](https://github.com/likelion-backend-8th-pj2-team4-JuDBu/Custom-Master/assets/70869505/5b88732e-0391-4371-b705-e2ed91a7f718)

