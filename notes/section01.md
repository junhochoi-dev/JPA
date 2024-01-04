# 객체 지향 언어 + 관계형 DB

- 객체를 관계형 DB에 관리 (자바 객체 ↔ SQL)

```java
public class Member {
    private String memberId;
    private String name;
    ...
}
```
```sql
INSERT INTO MEMBER(MEMBER_ID, NAME) VALUES
SELECT MEMBER_ID, NAME FROM MEMBER M
UPDATE MEMBER SET …
```

String tel 추가

```java
public class Member {
    private String memberId;
    private String name;
    private String tel;
    ...
}
```
```sql
INSERT INTO MEMBER(MEMBER_ID, NAME, TEL) VALUES
SELECT MEMBER_ID, NAME, TEL FROM MEMBER M
UPDATE MEMBER SET … TEL = ?
```

- 객체가 수정되면 Query문을 다 수정하게 되는 문제
- SQL에 의존적인 개발을 피하기 어렵다

# 객체와 관계형 데이터베이스의 차이

1. 상속
   - 객체의 상속 관계
   - Table 슈퍼타입과 서브타입 관계
2. 연관관계
   - 객체는 참조를 사용 (`member.getTeam()`)
   - 테이블은 외래 키를 사용 (`JOIN ON M.TEAM_ID = T.TEAM_ID`)
```java
class Member{
	String id;      // MEMBER_ID 컬럼 사용
	Long teamId;    // TEAM_ID FK 컬럼 사용
	String username;// USERNAME 컬럼 사용
}
class Team{
	Long id;        // TEAM_ID PK 사용
    String name;    // NAME 컬럼 사용
}
```
3. 데이터 타입

4. 데이터 식별 방법
