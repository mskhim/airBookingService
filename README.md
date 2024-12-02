# 🛫 AirplaneService  
> **항공편 예약 시스템**  

---

## 📅 프로젝트 개요  

- **제작 기간**: 2024년 11월 28일 ~ 12월 1일  
- **목적**: 사용자와 관리자를 위한 직관적인 항공편 예약 및 관리 시스템 구축  

---

## 🛠 개발 환경  

### 언어 및 플랫폼  

- **Java**  
  - JDK 21.0.4  
  - Eclipse IDE  

### 데이터베이스  

- **Oracle DB**  
  - 버전: 21c Express Edition  
  - SQL Developer로 관리 및 쿼리 작업 수행  

### 주요 도구  

- **SQL Developer**  
  - Oracle DB를 위한 공식 도구로, SQL 쿼리 작성 및 데이터베이스 관리에 사용  
  - [다운로드 SQL Developer](https://www.oracle.com/database/sqldeveloper/)  

---

## 📌 주요 기능  

1. **사용자용 기능**  
   - 항공편 조회  
   - 항공편 예약 및 예약 취소  
   - 개인 정보 확인 및 수정  
   - 예약 내역 조회  

2. **관리자용 기능**  
   - 고객, 항공기, 국가, 항공편 관리 (CRUD 기능)  
   - 예약 데이터 관리  
   - 고객 권한 추가/회수  

3. **UI 및 구조**  
   - 직관적인 사용자 인터페이스  
   - **MVC 아키텍처** 기반 코드 구조화  

---

## :bar_chart: ERD  
![ERD 이미지](https://github.com/user-attachments/assets/1283b7de-7ded-4f93-8447-6941cc31487a)  

---

## 💻 실행 화면  

### **1. 로그인 화면**  

- **일반 계정**  
  ![일반 로그인](https://github.com/user-attachments/assets/db71069a-c7c6-4be2-a46b-27ecf35bc72c)  

- **관리자 계정**  
  ![관리자 로그인](https://github.com/user-attachments/assets/647f0d7f-83b2-4076-96f2-ded01dc87ee0)  

> **기능**  
> - **일반 계정**: 항공편 조회, 예약/취소, 개인 정보 관리  
> - **관리자 계정**: 데이터베이스 내 모든 데이터 관리 (CRUD)  

---

### **2. 항공편 조회 화면**  

![항공편 조회](https://github.com/user-attachments/assets/26a40667-4faa-47d4-8309-a92da65508a6)  

- 로그인 여부와 관계없이 이용 가능한 항공편 조회 가능  
- `JOIN`, `GROUP BY`, `COUNT`를 사용해 잔여 좌석 정보를 동적으로 출력  

---

### **3. 고객 화면**  

#### (1) **좌석 예매**  
![좌석 예매](https://github.com/user-attachments/assets/1e9522a9-e613-4123-9843-c03d66f89121)  

- 예약 가능 좌석 목록 제공  
- 이미 예약된 좌석은 선택 불가  

#### (2) **예약 내역 조회 및 관리**  
![예매 내역](https://github.com/user-attachments/assets/2aac652d-2a18-4a32-bda8-d2f1f70e3be3)  

- 본인의 예약 내역 출력  
- 예약 변경 및 취소 기능 제공  

#### (3) **마이페이지**  
![마이페이지](https://github.com/user-attachments/assets/799c3f23-95ce-4f1b-a9f1-3e512bde5bb3)  

- 회원 정보 확인 및 수정  
- 회원 탈퇴 기능 제공  

---

### **4. 관리자 화면**  

#### (1) **관리자 기능**  
![관리자 화면](https://github.com/user-attachments/assets/37992032-a4f9-461c-9dd9-03ef09840d79)  

- 고객, 항공기, 국가, 항공편, 예약 현황 관리  

#### (2) **추가 기능**  
![권한 관리](https://github.com/user-attachments/assets/523e0975-dc03-459c-9b87-0ff933855400)  

- 고객 권한 추가 및 회수  

---

## 🚧 핵심 트러블 슈팅  

1. **예약 시 고객 예약 합계 증가 문제**  
   - **트리거 구현**  
     ```sql
     CREATE OR REPLACE TRIGGER COUNT_UP_TRG
     AFTER INSERT ON BOOKING
     FOR EACH ROW
     BEGIN
         UPDATE CUSTOMER SET C_COUNT = C_COUNT + 1 WHERE NO = :NEW.CUSTOMER_NO;
     END;
     /
     ```

2. **예약 삭제 시 합계 감소 문제**  
   - **트리거 구현**  
     ```sql
     CREATE OR REPLACE TRIGGER COUNT_DOWN_TRG
     AFTER DELETE ON BOOKING
     FOR EACH ROW
     BEGIN
         UPDATE CUSTOMER SET C_COUNT = C_COUNT - 1 WHERE NO = :OLD.CUSTOMER_NO;
     END;
     /
     ```

3. **항공기 추가 시 좌석 자동 생성 문제**  
   - **트리거 구현**  
     ```sql
     CREATE OR REPLACE TRIGGER PLANE_INSERT_TRG
     AFTER INSERT ON PLANE
     FOR EACH ROW
     DECLARE
         X1 NUMBER;
         X2 NUMBER;
     BEGIN
         FOR i IN 1 .. :NEW.ROWX LOOP
             FOR j IN 1 .. :NEW.COLY LOOP
                 X1 := ASCII('A') + TRUNC(i / 26);
                 X2 := ASCII('A') + MOD(i, 26);
                 INSERT INTO SEATS VALUES (
                     TO_CHAR((SELECT NVL(MAX(NO), 0) + 1 FROM SEATS), 'FM000000'),
                     :NEW.NO,
                     CHR(X1) || CHR(X2),
                     TO_CHAR(j, 'FM00')
                 );
             END LOOP;
         END LOOP;
     END;
     /
     ```

4. **항공편 가격 및 도착 시간 자동 계산 문제**  
   - 가격 계산 및 도착 시간 계산 트리거 구현  

---

## 💡 추가 구현  

- **뷰(View) 생성**  
  - **예약 정보 통합 뷰**  
    ```sql
    CREATE OR REPLACE VIEW BOOKING_JOIN_ALL_VIEW AS
    SELECT DISTINCT B.CUSTOMER_NO, GROUP_NO, B.CODE, C.NAME, P.NAME AS PLANE_NAME, DC.NAME AS DEP_COUNTRY, AC.NAME AS ARV_COUNTRY, PRICE, DEPARTURE_HOUR, ARRIVAL_HOUR, SEAT, BOOKING_DATE
    FROM BOOKING B
    JOIN FLIGHT F ON B.FLIGHT_NO = F.NO
    JOIN CUSTOMER C ON B.CUSTOMER_NO = C.NO
    JOIN COUNTRY DC ON DC.NO = F.DEPARTURE_COUNTRY_NO
    JOIN COUNTRY AC ON AC.NO = F.ARRIVAL_COUNTRY_NO
    JOIN PLANE P ON P.NO = F.PLANE_NO;
    /
    ```

  - **항공편 통합 뷰**  
     ```sql
     CREATE OR REPLACE VIEW FLIGHT_COUNTRY_JOIN_VIEW AS
     SELECT F.NO,REMAIN, F.PLANE_NO,F.ARRIVAL_COUNTRY_NO, F.DEPARTURE_COUNTRY_NO, C.NAME AS ARVNAME,C2.NAME AS DEPNAME,F.PRICE, F.DEPARTURE_HOUR,F.ARRIVAL_HOUR 
     FROM FLIGHT F 
     LEFT JOIN COUNTRY C 
     ON C.NO=F.ARRIVAL_COUNTRY_NO 
     LEFT JOIN COUNTRY C2 
     ON C2.NO=F.DEPARTURE_COUNTRY_NO
     JOIN (SELECT FNO, count(*) AS REMAIN
     FROM (
     SELECT s.no AS sno, f.no AS fno
     FROM seats s
     INNER JOIN flight f
     ON s.plane_no = f.plane_no ) j
     LEFT JOIN booking b
     ON b.seats_no = j.sno
     where code is null
     GROUP BY FNO)Q
     ON Q.FNO=F.NO;
     ```

  - **현재 좌석 현황 출력 SQL**  
    ```java
    private final String SELECT_BY_FLIGHT_SQL = 
    "SELECT S.ROWX, S.COLY, CUSTOMER_NO, CODE, GROUP_NO, P.COLY AS YNUM 
    FROM SEATS S 
    LEFT JOIN BOOKING B ON S.NO = B.SEATS_NO 
    JOIN PLANE P ON P.NO = S.PLANE_NO 
    WHERE S.PLANE_NO = (SELECT PLANE_NO FROM FLIGHT WHERE NO = TO_CHAR(?,'FM00000')) 
    ORDER BY ROWX, TO_NUMBER(COLY)";
