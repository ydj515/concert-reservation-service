import http from 'k6/http';
import {check, sleep} from 'k6';
import {randomIntBetween, randomItem} from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';
// import {htmlReport} from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";

export const options = {
    stages: [
        { duration: '5m', target: 100 }, // traffic ramp-up from 1 to 100 users over 5 minutes.
        { duration: '30m', target: 100 }, // stay at 100 users for 30 minutes
        { duration: '5m', target: 0 }, // ramp-down to 0 users
    ]
};

// 공통으로 사용할 토큰 저장
let accessToken = null;
const BASE_URL = 'http://localhost:18080';

// 1. setup()에서 한 번만 실행 -> 모든 VU가 이 토큰을 사용
export function setup() {
    let tokenResponse = http.post(
        `${BASE_URL}/reservation-token`,
        JSON.stringify({ userId: 1 }),
        {
            headers: {
                'Content-Type': 'application/json'
            },
        }
    );

    check(tokenResponse, {
        '토큰 요청 성공': (res) => res.status === 200,
        '토큰이 존재함': (res) => JSON.parse(res.body).data?.token !== undefined,
    });

    // 토큰 저장
    accessToken = JSON.parse(tokenResponse.body).data?.token;
    if (!accessToken) {
        console.error('토큰을 가져오지 못했습니다.');
        return null;
    }

    console.log(`발급된 토큰: ${accessToken}`);

    // 2. 11초 대기
    sleep(11);

    return accessToken; // 모든 VU에게 전달됨
}

export default function (token) {
    const concertId = randomIntBetween(1, 20);
    const scheduleId = randomIntBetween(1, 100);
    const date = randomItem([
        '2024-01-01', '2024-01-02', '2024-01-03', '2024-01-04',
        '2024-01-05', '2024-01-06', '2024-01-07', '2024-01-08',
        '2024-01-09', '2024-01-10'
    ]);

    const url = `${BASE_URL}/api/concert/${concertId}/schedules/${scheduleId}/seats?date=${date}`;

    let response = http.get(url, {
        headers: {
            'Content-Type': 'application/json',
            'X-Reservation-Queue-Token': token
        }
    });

    check(response, {
        '콘서트 스케쥴 예약 날짜 조회': (res) => res.status === 200,
    });
}

// export function handleSummary(data) {
//     return {
//         "summary.html": htmlReport(data),
//     };
// }
