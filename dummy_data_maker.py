import mysql.connector
from faker import Faker
import random
from tqdm import tqdm
from datetime import timedelta

# MySQL 연결 설정
conn = mysql.connector.connect(
    host="localhost",
    user="test",
    password="test",
    database="test_db"
)
cursor = conn.cursor()

fake = Faker()
BATCH_SIZE = 10_000

# User 생성
def generate_users(n):
    users = []
    for _ in tqdm(range(n), desc="Generating Users"):
        name = fake.name()
        balance = random.randint(0, 1000000)
        users.append((name, balance))
    cursor.executemany("INSERT INTO user (name, balance) VALUES (%s, %s)", users)
    conn.commit()

# Place 생성
def generate_places(n):
    places = []
    for _ in tqdm(range(n), desc="Generating Places"):
        name = fake.company()
        available_seat_count = random.randint(100, 3000)
        places.append((name, available_seat_count))
    cursor.executemany("INSERT INTO place (name, available_seat_count) VALUES (%s, %s)", places)
    conn.commit()

# Concert 생성
def generate_concerts(n):
    concerts = []
    for _ in tqdm(range(n), desc="Generating Concerts"):
        title = fake.catch_phrase()
        concerts.append((title,))
    cursor.executemany("INSERT INTO concert (title) VALUES (%s)", concerts)
    conn.commit()

# ConcertSchedule 생성
def generate_concert_schedules(concert_count, schedules_per_concert):
    cursor.execute("SELECT id FROM concert")
    concert_ids = [row[0] for row in cursor.fetchall()]

    cursor.execute("SELECT id FROM place")
    place_ids = [row[0] for row in cursor.fetchall()]

    schedules = []
    for concert_id in tqdm(concert_ids, desc="Generating ConcertSchedules"):
        for _ in range(schedules_per_concert):
            place_id = random.choice(place_ids)
            performance_date = fake.date_between(start_date="-1y", end_date="+1y")
            reserved_start_at = fake.date_between(start_date=performance_date - timedelta(days=90), end_date=performance_date - timedelta(days=30))
            reserved_end_at = fake.date_between(start_date=reserved_start_at, end_date=reserved_start_at + timedelta(days=30))  # 예약 시작 후 1일~30일 이내로 설정
            performance_time = random.randint(120, 360)
            schedules.append((performance_date, performance_time, reserved_start_at, reserved_end_at, concert_id, place_id))

    cursor.executemany("INSERT INTO concert_schedule (performance_date, performance_time, reserved_start_at, reserved_end_at, concert_id, place_id) VALUES (%s, %s, %s, %s, %s, %s)", schedules)
    conn.commit()

# ScheduleSeat 생성
def generate_schedule_seats():
    cursor.execute("SELECT id FROM concert_schedule")
    schedule_ids = [row[0] for row in cursor.fetchall()]

    seats = []
    for schedule_id in tqdm(schedule_ids, desc="Generating ScheduleSeats"):
        for _ in range(1):  # 1개의 좌석 유형
            price = random.randint(50000, 100000)
            seat_count = 50
            seat_type = "UNDEFINED"
            seats.append((seat_type, price, seat_count, schedule_id))
    cursor.executemany("INSERT INTO schedule_seat (type, price, seat_count, schedule_id) VALUES (%s, %s, %s, %s)", seats)
    conn.commit()

# Seat 생성
def generate_seats():
    cursor.execute("SELECT id, seat_count FROM schedule_seat")
    schedule_seats = cursor.fetchall()

    seats = []
    for schedule_seat_id, seat_count in tqdm(schedule_seats, desc="Generating Seats"):
        for no in range(1, seat_count + 1):
            seats.append((no, schedule_seat_id))
    cursor.executemany("INSERT INTO seat (no, schedule_seat_id) VALUES (%s, %s)", seats)
    conn.commit()

if __name__ == "__main__":
    generate_users(1000)
    generate_places(10)
    generate_concerts(20)
    generate_concert_schedules(20, 500)
    generate_schedule_seats()
    generate_seats()

    cursor.close()
    conn.close()
    print("Data generation completed.")
