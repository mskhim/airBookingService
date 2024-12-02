# 🛫 airplaneService
> **항공편 예약 시스템**

---
## 🗓 제작 기간 
- 2024년 11월 28일 ~ 12월 1일

## 🛠 개발 환경
- `Java`
- `JDK-21.0.4`
- **IDE** : Eclipse
- **Database** : Oracle DB(11g Express Edition)

## 📌주요 기능
1. 사용자가 항공편 예약을 쉽게 할 수 있는 시스템 구현
2. 관리자 모드를 통해 고객, 항공기, 국가, 항공편 관리 가능
3. 사용자와 관리자가 모두 사용할 수 있는 직관적인 UI 제공
4. **MVC 아키텍처**를 통한 코드 구조화

## 💻 실행화면
- 로그인 화면

  
![캡처](https://github.com/user-attachments/assets/4724d8c5-348e-466d-a3e4-93911eff845a)

- 항공편 예매 화면


![항공편](https://github.com/user-attachments/assets/8027e285-ef28-4d72-b3e8-4643aa5fbeec)
- 좌석 예매 화면

  
![image](https://github.com/user-attachments/assets/1e9522a9-e613-4123-9843-c03d66f89121)


## :grey_exclamation: ERD 
![image](https://github.com/user-attachments/assets/1283b7de-7ded-4f93-8447-6941cc31487a)

## 🚧 **핵심 트러블 슈팅**

- **예약 시 고객 예약 합계 증가 문제**
  - 예약 테이블에 데이터가 추가될 때마다 고객 예약 합계를 자동으로 증가시키는 트리거를 구현하여 문제 해결
  - 트리거 코드:
    ```sql
    CREATE OR REPLACE TRIGGER COUNT_UP_TRG
    AFTER INSERT ON BOOKING
    FOR EACH ROW
    BEGIN
        UPDATE CUSTOMER SET C_COUNT = C_COUNT + 1 WHERE NO = :NEW.CUSTOMER_NO;
    END;
    /
    ```

- **예약 삭제 시 고객 예약 합계 감소 문제**
  - 예약 테이블에서 데이터가 삭제될 때 고객 예약 합계를 감소시키는 트리거를 추가하여 문제 해결
  - 트리거 코드:
    ```sql
    CREATE OR REPLACE TRIGGER COUNT_DOWN_TRG
    AFTER DELETE ON BOOKING
    FOR EACH ROW
    BEGIN
        UPDATE CUSTOMER SET C_COUNT = C_COUNT - 1 WHERE NO = :OLD.CUSTOMER_NO;
    END;
    /
    ```

- **항공기 추가 시 좌석 자동 생성 문제**
  - 항공기 정보를 추가할 때 좌석 정보를 자동으로 생성하는 트리거를 작성하여 관리의 편의성 제공
  - 트리거 코드:
    ```sql
    CREATE OR REPLACE TRIGGER PLANE_INSERT_TRG
    AFTER INSERT ON PLANE
    FOR EACH ROW
    DECLARE
        X1 NUMBER;
        X2 NUMBER;
    BEGIN
        FOR i IN 1 .. :NEW.ROWX - 1 LOOP
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

- **항공편 가격 및 도착 시간 자동 계산 문제**
  - 국가 간 거리를 기준으로 항공편의 가격을 자동 계산하는 트리거를 구현하여 사용자가 입력할 필요 없이 자동으로 설정되도록 개선
  - 또한 출발 시간에 소요 시간을 더하여 도착 시간을 자동으로 계산하는 트리거 구현
  - 가격 및 도착 시간 자동 계산 트리거 코드:
    ```sql
    CREATE OR REPLACE TRIGGER FLIGHT_PRICE_TRIGGER
    BEFORE INSERT ON FLIGHT
    FOR EACH ROW
    DECLARE
        SHOUR NUMBER(4.2);
    BEGIN
        SELECT HOUR INTO SHOUR FROM COUNTRY C WHERE C.NO = :NEW.ARRIVAL_COUNTRY_NO;
        :NEW.PRICE := SHOUR * 150000;
    END;
    /
    
    CREATE OR REPLACE TRIGGER FLIGHT_ARRIVAL_TRIGGER
    BEFORE INSERT ON FLIGHT
    FOR EACH ROW
    DECLARE
        SHOUR NUMBER(4.2);
    BEGIN
        SELECT HOUR INTO SHOUR FROM COUNTRY C WHERE C.NO = :NEW.ARRIVAL_COUNTRY_NO;
        :NEW.ARRIVAL_HOUR := :NEW.DEPARTURE_HOUR + (SHOUR / 24);
    END;
    /
    ```

- **예약 시 좌석 자동 배정 문제**
  - 예약이 입력되거나 업데이트될 때 입력된 좌석에 맞춰 좌석 번호를 자동으로 업데이트하는 트리거 구현
  - 트리거 코드:
    ```sql
    CREATE OR REPLACE TRIGGER BOOKING_SEATS_TRG
    BEFORE INSERT OR UPDATE ON BOOKING
    FOR EACH ROW
    DECLARE
        SNO CHAR(6);
    BEGIN
        SELECT NO INTO SNO FROM SEATS 
        WHERE ROWX = SUBSTR(:NEW.SEAT, 1, 2) 
        AND COLY = SUBSTR(:NEW.SEAT, 3) 
        AND PLANE_NO = (SELECT PLANE_NO FROM FLIGHT WHERE NO = :NEW.FLIGHT_NO);
        :NEW.SEATS_NO := SNO;
    END;
    /
    ```

- **예약 시 코드 자동 생성 문제**
  - 예약 데이터를 삽입할 때 예약 코드(`CODE`)를 자동으로 생성하도록 트리거를 작성하여 관리 편의성 제공
  - 트리거 코드:
    ```sql
    CREATE OR REPLACE TRIGGER BOOKING_INSERT_TRG
    BEFORE INSERT ON BOOKING
    FOR EACH ROW
    BEGIN
        :NEW.CODE := :NEW.GROUP_NO || '-' || :NEW.CUSTOMER_NO || '-' || :NEW.FLIGHT_NO;
    END;
    /
    ```

## 💡 추가 구현
- **VIEW를 이용한 통합 정보 제공**
  - 모든 예약 정보를 한눈에 볼 수 있도록 여러 테이블을 조인한 `BOOKING_JOIN_ALL_VIEW` 뷰 작성
  - 뷰 정의 코드:
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
    
  - 항공편 출력시 잔여좌석과, 국가 이름 출력을 위해 left Join과 inner조인을 사용한 'FLIGHT_COUNTRY_JOIN_VIEW' 뷰 작성
  - 뷰 정의 코드 :
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

   - 현재 좌석 출력시 항공편 번호를 입력받아 해당 항공편의 좌석 예매 현황을 출력하기위한 쿼리문 추가
   - Booking, Seats , Plane 테이블을 조인, Booking의 SEATS_NO 컬럼과 Seats 의 NO 컬럼을 기준으로 JOIN하여 예매 현황 출력 가능하게 함. 
   - 쿼리문 정의 코드 :
     ```java
	   private final String SELECT_BY_FLIGHT_SQL = "SELECT S.ROWX,S.COLY,CUSTOMER_NO,CODE,GROUP_NO,P.COLY AS YNUM FROM SEATS S LEFT JOIN BOOKING B \r\n"
		 + "ON S.NO = B.SEATS_NO \r\n"
		 + "JOIN PLANE P \r\n"
		 + "ON P.NO = S.PLANE_NO\r\n"
	   + "WHERE S.PLANE_NO = (SELECT PLANE_NO FROM FLIGHT WHERE NO = TO_CHAR(?,'FM00000'))\r\n"
		 + "ORDER BY ROWX, TO_NUMBER(COLY)";
     ```
