import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    vus: 50,  // 50명의 사용자를 동시에 유지
    duration: '1m', // 1분 동안 지속
};

const BASE_URL = 'http://localhost:8095/api/user/balance';
const USER_ID = Math.floor(Math.random() * 100) + 1; // 1~100 랜덤 유저 ID

export default function () {
    // 잔액 충전 요청
    let chargeRes = http.post(BASE_URL, JSON.stringify({
        userId: USER_ID,
        amount: 3000
    }), {
        headers: { 'Content-Type': 'application/json' },
    });

    check(chargeRes, {
        '잔액 충전 응답 상태 201': (r) => r.status === 201,
    });

    // 잔액 조회 요청
    let balanceRes = http.get(`${BASE_URL}/${USER_ID}`);

    check(balanceRes, {
        '잔액 조회 응답 상태 200': (r) => r.status === 200,
    });

    sleep(1);
}
