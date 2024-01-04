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
3. 데이터 타입
4. 데이터 식별 방법
```java
class Member{
	String id;      // MEMBER_ID 컬럼 사용
	Team team;    // TEAM_ID FK 컬럼 사용
	String username;// USERNAME 컬럼 사용
    Team getTeam(){
		return team;
    }   
}
class Team{
	Long id;        // TEAM_ID PK 사용
    String name;    // NAME 컬럼 사용
}
```
```sql
SELECT  M.*, T.*
FROM    MEMBER M
JOIN    TEAM T ON M.TEAM_ID = T.TEAM_ID
```
```java
public Member find(String memberId) {
	Member member = new Member();
	Team team = new Team();
	
	member.setTeam(team);
	return member;
}
```

# 엔티티 신뢰 문제
```java
class MemberService {
	public void process() {
		Member member = memberDAO.find(memberId);
		member.getTeam();
		member.getOrder().getDelivery();
		// MemberDAO 구조를 봐야한다
	}
}
```

# 모든 객체를 미리 로딩할 수 없다
```java
memberDAO.getMember();
memberDAO.getMemberWithTeam();
memberDAO.getMemberWithOrderWithDelivery();
```

# 비교하기
```java
String memberId = "100";
Member member1 = memberDAO.getMember(memberId);
Member member2 = memberDAO.getMember(memberId);

member1 == member2; // 다르다

class MemberDAO {
	public Member getMember(String memberId) {
		String sql = "SELECT * FROM MEMBER WHERE MEMBER_ID = ?";
		...
        // JDBC API, SQL 실행
        return new Member(...);
       
    }
}
```

# 비교하기 - 자바 컬렉션에서 조회
```java
String memberId = "100";
Member member1 = list.add(memberId);
Member member2 = list.add(memberId);

member1 == member2; // 같다
```

→ 객체를 자바 컬렉션에 저장하듯이 DB에 저장할 수는 없을까? = JPA = Java Persistence API

# JPA

> JPA(Java Persistence API)

- Java Persistence API
- 자바 진영의 ORM 표준

> ORM

- Object-relational mapping(객체 관계 매핑)
- 객체는 객체대로 설계
- 관계형 데이터베이스는 관계형 데이터베이스대로 설계
- ORM 프레임워크가 중간에서 매핑
- 대중적인 언어에서는 대부분 ORM이 존재

