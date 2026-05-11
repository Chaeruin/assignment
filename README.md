# 실시간 선착순 쿠폰 발급 시스템 구현

## 브라우저 부하 테스트 사용

### 재고 확인 및 초기화
![11.png](src/main/resources/image/11.png)

### 동시 발급 테스트
![22.png](src/main/resources/image/22.png)

### 단일 유저 중복 요청 테스트
![33.png](src/main/resources/image/33.png)

----
## 실제 부하 테스트 도구 테스트

```bash
# k6 사용
k6 run --vus 200 --duration 10s script.js
```
<br>

### 결과 Screenshot (summary.html)

![1.png](src/main/resources/image/1.png)
![2.png](src/main/resources/image/2.png)
![3.png](src/main/resources/image/3.png)