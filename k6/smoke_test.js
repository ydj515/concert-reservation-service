import http from 'k6/http';
import {check, sleep} from 'k6';

export const options = {
    vus: 1,
    iterations: 1, // 각 VU당 한 번 실행
};

let accessToken = null;
const BASE_URL = 'http://localhost:18080';

export function setup() {
    let tokenResponse = http.post(
        `${BASE_URL}/reservation-token`,
        JSON.stringify({userId: 1}),
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

    accessToken = JSON.parse(tokenResponse.body).data?.token;
    if (!accessToken) {
        console.error('토큰을 가져오지 못했습니다.');
        return null;
    }

    console.log(`발급된 토큰: ${accessToken}`);

    sleep(11);

    return accessToken;
}

export default function (token) {

    const apiEndpoints = [
        {
            method: 'POST',
            url: `${BASE_URL}/api/balance`,
            body: {amount: 5000},
            headers: {'X-Reservation-Queue-Token': token},
            checkMsg: '잔액 충전 요청 성공'
        },
        {
            method: 'GET',
            url: `${BASE_URL}/api/balance`,
            body: null,
            headers: {'X-Reservation-Queue-Token': token},
            checkMsg: '잔액 조회'
        },
        {
            method: 'GET',
            url: `${BASE_URL}/api/concert/1/schedules/1/seats?date=2024-01-01`,
            body: null,
            headers: {'X-Reservation-Queue-Token': token},
            checkMsg: '콘서트 스케쥴 예약 날짜 조회'
        },
        {
            method: 'POST',
            url: `${BASE_URL}/api/concert/1/schedules/1/reservations`,
            body: {seatNo: 13},
            headers: {'X-Reservation-Queue-Token': token},
            checkMsg: '콘서트 스케쥴 좌석 예약'
        },
        {
            method: 'POST',
            url: `${BASE_URL}/api/payment`,
            body: {reservationId: 1, amount: 1000},
            headers: {'X-Reservation-Queue-Token': token},
            checkMsg: '결제 진행'
        },
    ];

    for (let api of apiEndpoints) {
        let response = api.method === 'GET'
            ? http.get(api.url, {headers: {'Content-Type': 'application/json', ...api.headers}})
            : http.post(api.url, JSON.stringify(api.body), {headers: {'Content-Type': 'application/json', ...api.headers}});

        check(response, {
            [api.checkMsg]: (res) => res.status === 200,
        });

        sleep(1);
    }

}
