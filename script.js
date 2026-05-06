import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  // 사용자가 설정한 vus와 duration을 스크립트 내부에서도 정의 가능
  vus: 200,
  duration: '10s',
};

export default function () {
  const couponId = 1;

  // 요청할 서버 주소
  // 로컬 k6 설치 시: localhost:8080
  const url = 'http://localhost:8080/api/coupons/1/issue';

  // 3. 가상 유저마다 고유한 userId 생성 (부하 테스트 시 중복 방지)
  // __VU: 현재 가상 유저 번호, __ITER: 해당 유저의 반복 횟수
  const payload = JSON.stringify({
    userId: (__VU * 1000) + __ITER,
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const res = http.post(url, payload, params);

  // 결과 검증 (HTTP 상태 코드가 202 Accepted인지 확인)
  check(res, {
    'is status 202': (r) => r.status === 202,
  });

  // 실제 사용자의 행동 패턴을 시뮬레이션하려면 추가, 극강의 부하를 원하면 주석 처리
  sleep(0.1);
}

// 결과 요약 보고서를 HTML로 보고 싶을 때 추가
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";

export function handleSummary(data) {
  return {
    "summary.html": htmlReport(data),
  };
}