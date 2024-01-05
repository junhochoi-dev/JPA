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

> JAVA ORM

EJB 엔티티 빈 → 하이버네이트 Hibernate → JPA 자바 표준

# JPA를 왜 사용해야 하는가?
- SQL 중심적인 개발에서 객체 중심으로 개발
- 생산성 
- 유지보수
- 패러다임의 불일치 해결
- 성능
- 데이터 접근 추상화와 벤더 독립성
- 표준

> 생산성

- 저장: jpa.persist(member)
- 조회: Member member = jpa.find(memberId)
- 수정: member.setName(“변경할 이름”)
- 삭제: jpa.remove(member)

> 유지 보수

- 예전에는 데이터 필드가 추가 시 모든 SQL을 변경해야 함
- JPA로 SQL 변경없이 필드만 추가하면 자동으로 변경해 줌

> 패러다임 불일치 해결


1. JPA와 상속
    - 저장
        - JPA `jpa.persist(album)`
        - SQL `INSERT INTO ITEM ... / INSERT INTO ALBUM ...`
    - 조회
        - JPA `Album album = jpa.find(Album.class, albumId);`
        - SQL `SELECT I.*, A.* FROM ITEM I JOIN ALBUM A ON I.ITEM_ID = A.ITEM_ID`
2. JPA와 연관관계
    - `member.setTeam(team);`
    - `jpa.persist(member);`
3. JPA와 객체 그래프 탐색
    - `Member member = jpa.find(Member.class, memberId);`
    - `Team team = member.getTeam();`
```java
class MemberService {
   ...
   public void process() {
      Member member = memberDAO.find(memberId);
      member.getTeam(); //자유로운 객체 그래프 탐색
      member.getOrder().getDelivery();
   }
}
```
4. JPA와 비교하기
```java
String memberId = "100";
Member member1 = jpa.find(Member.class, memberId);
Member member2 = jpa.find(Member.class, memberId);
member1 == member2; //같다.
// 동일한 트랜잭션에서 조회한 엔티티는 같음을 보장
```

> 성능

1. 1차 캐시와 동일성(identity) 보장
    1. 같은 트랜잭션 안에서는 같은 엔티티를 반환 - 약간의 조회 성능 향상
    2. DB Isolation Level이 Read Commit이어도 애플리케이션에서 Repeatable Read 보장
```java
String memberId = "100";
Member m1 = jpa.find(Member.class, memberId); //SQL
Member m2 = jpa.find(Member.class, memberId); //캐시
println(m1 == m2) //true
SQL 1번만 실행
```
2. 트랜잭션을 지원하는 쓰기 지연(transactional write-behind)
    - INSERT
        1. 트랜잭션을 커밋할 때까지 INSERT SQL을 모음
        2. JDBC BATCH SQL 기능을 사용해서 한번에 SQL 전송
```java
transaction.begin(); // [트랜잭션] 시작
em.persist(memberA);
em.persist(memberB);
em.persist(memberC);
//여기까지 INSERT SQL을 데이터베이스에 보내지 않는다.
//커밋하는 순간 데이터베이스에 INSERT SQL을 모아서 보낸다.
transaction.commit(); // [트랜잭션] 커밋
```
2. 트랜잭션을 지원하는 쓰기 지연(transactional write-behind)
    - UPDATE
        1. UPDATE, DELETE로 인한 로우(ROW)락 시간 최소화
        2. 트랜잭션 커밋 시 UPDATE, DELETE SQL 실행하고, 바로 커밋
```java
transaction.begin(); // [트랜잭션] 시작
changeMember(memberA); 
deleteMember(memberB); 
비즈니스_로직_수행(); //비즈니스 로직 수행 동안 DB 로우 락이 걸리지 않는다. 
//커밋하는 순간 데이터베이스에 UPDATE, DELETE SQL을 보낸다.
transaction.commit(); // [트랜잭션] 커밋
```
3. 지연 로딩(Lazy Loading) + 즉시 로딩(Eager Loading)
    - 지연 로딩: 객체가 실제 사용될 때 로딩
```java
Member member = memberDAO.find(memberId);
Team team = member.getTeam();
String teamName = team.getName();
```
3. 지연 로딩(Lazy Loading) + 즉시 로딩(Eager Loading)
   - 즉시 로딩: JOIN SQL로 한번에 연관된 객체까지 미리 조회
```java
Member member = memberDAO.find(memberId);
Team team = member.getTeam();
String teamName = team.getName();
```