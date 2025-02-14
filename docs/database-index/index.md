# 조회 성능 분석을 위한 Database Index 최적화 테스트 보고서

서비스에서 성능 개선이 필요한 쿼리를 분석합니다.

## 테스트 환경

| 항목         | 사양 및 내용                      |
|------------|------------------------------|
| **장비**     | Mac 15.3 ARM64 M4 24GB 512GB |
| **데이터베이스** | MySQL 8.4.3                  |
| **데이터 양**  | 모든 테이블에 각 10만 건 삽입           |
| **데이터 분포** | 모든 테이블에 치우치지 않고 고르게 데이터를 준비  |

## 테스트 수행

- `EXPLAIN`을 사용한 쿼리 실행 계획 분석
- 속도 측정: 독립적인 데이터베이스에서 index 적용 전/후 쿼리 성능 비교

## 테스트 대상

현재 서비스에서 복잡한 쿼리 혹은 성능 개선이 필요한 쿼리들을 선정하였습니다.

### 1. 예약 가능한 콘서트 일정 목록 조회(복잡한 쿼리)

#### 원본 sql

```sql
EXPLAIN
ANALYZE
SELECT s.*
FROM seat s
         JOIN schedule_seat ss ON s.schedule_seat_id = ss.id
         JOIN concert_schedule sc ON ss.schedule_id = sc.id
WHERE sc.concert_id = 1
  AND sc.id = 1
  AND sc.performance_date = '2024-01-01'
  AND NOT EXISTS (SELECT 1
                  FROM seat_reservation r
                  WHERE r.seat_id = s.id)
```

#### 원본 실행 분석

```shell
-> Nested loop antijoin  (cost=59.1 rows=51.5) (actual time=0.191..0.208 rows=50 loops=1)
    -> Nested loop inner join  (cost=48.8 rows=51.5) (actual time=0.181..0.187 rows=50 loops=1)
        -> Covering index lookup on ss using FK9xtqbddyvwmholiyj1i1k9lug (schedule_id=1)  (cost=0.668 rows=1) (actual time=0.00892..0.00933 rows=1 loops=1)
        -> Index lookup on s using FK6vewx6n4vx8ywahdhey1l67qg (schedule_seat_id=ss.id)  (cost=48.2 rows=51.5) (actual time=0.171..0.175 rows=50 loops=1)
    -> Single-row index lookup on <subquery2> using <auto_distinct_key> (seat_id=s.id)  (cost=6.45..6.45 rows=1) (actual time=301e-6..301e-6 rows=0 loops=50)
        -> Materialize with deduplication  (cost=1.2..1.2 rows=1) (actual time=0.00658..0.00658 rows=0 loops=1)
            -> Filter: (r.seat_id is not null)  (cost=1.1 rows=1) (actual time=0.00413..0.00413 rows=0 loops=1)
                -> Covering index scan on r using FK51vmmeopdicuwwwkr0vd85jpo  (cost=1.1 rows=1) (actual time=0.00337..0.00337 rows=0 loops=1)
```

#### index 추가

```sql
CREATE INDEX idx_concert_schedule
    ON concert_schedule (concert_id, id, performance_date);

CREATE INDEX idx_schedule_seat
    ON schedule_seat (schedule_id);

CREATE INDEX idx_seat_schedule
    ON seat (schedule_seat_id);

CREATE INDEX idx_seat_id
    ON seat (id);

CREATE INDEX idx_seat_reservation
    ON seat_reservation (seat_id);
```

#### index 추가후 실행 분석

```sql
-> Nested loop antijoin  (cost=63.5 rows=48.8) (actual time=0.102..0.121 rows=50 loops=1)
    -> Nested loop inner join  (cost=53.8 rows=48.8) (actual time=0.0923..0.0995 rows=50 loops=1)
        -> Covering index lookup on ss using idx_schedule_seat (schedule_id=1)  (cost=1.1 rows=1) (actual time=0.00483..0.00529 rows=1 loops=1)
        -> Index lookup on s using idx_seat_schedule (schedule_seat_id=ss.id)  (cost=52.7 rows=48.8) (actual time=0.0868..0.0915 rows=50 loops=1)
    -> Single-row index lookup on <subquery2> using <auto_distinct_key> (seat_id=s.id)  (cost=5.43..5.43 rows=1) (actual time=316e-6..316e-6 rows=0 loops=50)
        -> Materialize with deduplication  (cost=0.45..0.45 rows=1) (actual time=0.00637..0.00637 rows=0 loops=1)
            -> Filter: (r.seat_id is not null)  (cost=0.35 rows=1) (actual time=0.004..0.004 rows=0 loops=1)
                -> Covering index scan on r using idx_seat_reservation  (cost=0.35 rows=1) (actual time=0.00354..0.00354 rows=0 loops=1)
```

- index 추가 이유
    - `idx_concert_schedule`: where절에서 사용하는 순서에 맞게 index추가를 통해 조회 성능 개선
    - `idx_schedule_seat`: schedule_id 컬럼을 인덱싱하여 schedule_seat 테이블에서 schedule_id를 통해 조회 성능 개선
    - `idx_seat_schedule`: schedule_seat_id 컬럼에 대한 index를 추가하여, seat 테이블에서 schedule_seat_id를 이용한 조회 성능 개선
    - `idx_seat_id`: seat_id에 대한 index를 추가하여, seat_reservation 테이블에서 예약된 좌석 조회 성능 개선

- 개선 사항
    - 쿼리 속도 개선: covering index lookup을 통해 필요한 데이터만 빠르게 조회할 수 있어 성능이 크게 향상되었습니다.
    - 서브쿼리 성능 개선: seat_reservation 서브쿼리에서 idx_seat_reservation index를 사용하여 성능이 개선되었습니다. 이전에는 서브쿼리에서 테이블을 스캔하는 방식이었으나,
      index를 통해 빠르게 조회할 수 있게 되었습니다.

### 2. 콘서트 스케쥴 목록 조회(자주 조회하는 쿼리)

#### 원본 sql

```sql
EXPLAIN
ANALYZE
SELECT p.name, c.title, sc.*
FROM concert c
         JOIN concert_schedule sc on c.id = sc.concert_id
         JOIN place p on p.id = sc.place_id
WHERE sc.performance_date = '2024-01-01'
```

#### 원본 실행 분석

```shell
-> Nested loop inner join  (cost=32130 rows=39712) (actual time=6.5..62.5 rows=20000 loops=1)
    -> Nested loop inner join  (cost=18231 rows=39712) (actual time=6.49..42.9 rows=20000 loops=1)
        -> Index lookup on sc using idx_concert_schedule_performance_composite (performance_date=DATE'2024-01-01')  (cost=4332 rows=39712) (actual time=6.46..23.7 rows=20000 loops=1)
        -> Single-row index lookup on p using PRIMARY (id=sc.place_id)  (cost=0.25 rows=1) (actual time=825e-6..847e-6 rows=1 loops=20000)
    -> Single-row index lookup on c using PRIMARY (id=sc.concert_id)  (cost=0.25 rows=1) (actual time=845e-6..867e-6 rows=1 loops=20000)
```

#### index 추가

```sql
CREATE INDEX idx_concert_schedule_performance_date
    ON concert_schedule (performance_date);

CREATE INDEX idx_concert_schedule_performance_composite
    ON concert_schedule (performance_date, concert_id, place_id);
```

#### index 추가후 실행 분석

```sql
-> Nested loop inner join  (cost=31692 rows=39164) (actual time=3.51..54.3 rows=20000 loops=1)
    -> Nested loop inner join  (cost=17985 rows=39164) (actual time=3.5..36.3 rows=20000 loops=1)
        -> Index lookup on sc using idx_concert_schedule_performance_date (performance_date=DATE'2024-01-01')  (cost=4277 rows=39164) (actual time=3.48..18.3 rows=20000 loops=1)
        -> Single-row index lookup on p using PRIMARY (id=sc.place_id)  (cost=0.25 rows=1) (actual time=775e-6..797e-6 rows=1 loops=20000)
    -> Single-row index lookup on c using PRIMARY (id=sc.concert_id)  (cost=0.25 rows=1) (actual time=779e-6..799e-6 rows=1 loops=20000)
```

- index 추가 이유
    - `idx_concert_schedule_performance_date`:  where절에서 사용하는 컬럼 index추가를 통해 조회 성능 개선
    - `idx_concert_schedule_performance_composite`: performance_date, concert_id, place_id를 포함하는 복합 index를 추가하여,
      concert_schedule 테이블에서 다중 조건 필터링 성능 개선

- 개선 사항
    - 쿼리 속도 개선: idx_concert_schedule_performance_date index를 통해 where절을 빠르게 조회하며,
      idx_concert_schedule_performance_composite index 덕분에 concert_id와 place_id 조건을 동시에 빠르게 처리할 수 있었습니다.
    - 조인 성능 향상: concert, concert_schedule, place 테이블 간의 조인이 index를 사용하여 이전보다 더 효율적으로 이루어졌습니다. Single-row index lookup이
      빠르게 수행되며, 조인 성능이 크게 개선되었습니다.
