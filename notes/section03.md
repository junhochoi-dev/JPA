# JPA에서 가장 중요한 2가지
- 객체와 관계형 데이터베이스 매핑하기 (Object Relational Mapping) 
- 영속성 컨텍스트

# 영속성 컨텍스트
- JPA를 이해하는데 가장 중요한 용어 
- "엔티티를 영구 저장하는 환경"이라는 뜻 
- `EntityManager.persist(entity);`

# 엔티티 매니저? 영속성 컨텍스트?
- 영속성 컨텍스트는 논리적인 개념 
- 눈에 보이지 않는다. 
- 엔티티 매니저를 통해서 영속성 컨텍스트에 접근

# 엔티티의 생명주기
- 비영속 (new/transient)
  - 영속성 컨텍스트와 전혀 관계가 없는 새로운 상태 
- 영속 (managed)
  - 영속성 컨텍스트에 관리되는 상태 
- 준영속 (detached)
  - 영속성 컨텍스트에 저장되었다가 분리된 상태 
- 삭제 (removed)
  - 삭제된 상태

# 비영속
```java
//객체를 생성한 상태(비영속) 
Member member = new Member(); 
member.setId("member1"); 
member.setUsername("회원1");
```

# 영속
```java
//객체를 생성한 상태(비영속) 
Member member = new Member(); 
member.setId("member1"); 
member.setUsername(“회원1”);
EntityManager em = emf.createEntityManager();
em.getTransaction().begin();
//객체를 저장한 상태(영속)
em.persist(member);
```
- 아직 DB에 저장된 것이 아님
- DB는 이후에 Commit해야 저장이 됨

# 준영속, 삭제
```java
//회원 엔티티를 영속성 컨텍스트에서 분리, 준영속 상태 
em.detach(member); 
//객체를 삭제한 상태(삭제) 
em.remove(member);
```

# 영속성 컨텍스트의 이점
- 1차 캐시 
```java
// 엔티티를 생성한 상태(비영속) 
Member member = new Member(); 
member.setId("member1"); 
member.setUsername("회원1");
// 엔티티를 영속 
em.persist(member);
// 1차 캐시에 저장됨
em.persist(member);
// 1차 캐시에서 조회
Member findMember = em.find(Member.class, "member1");
// 데이터베이스에서 조회
Member findMember2 = em.find(Member.class, "member2");
```
```java
// 비영속
Member member = new Member();
member.setId(101L);
member.setName("HELLO JPA #");

// 영속
System.out.println("### BEFORE");
em.persist(member);
System.out.println("### AFTER");

Member findMember = em.find(Member.class, 101L);
System.out.println("findMember.id = " + findMember.getId());
System.out.println("findMember.name = " + findMember.getName());
```
```
### BEFORE
### AFTER
findMember.id = 101
findMember.name = HELLO JPA #
Hibernate: 
    /* insert com.project.jpa.Member
        */ insert 
        into
            Member
            (name, id) 
        values
            (?, ?)
```
- 동일성(identity) 보장 
```java
Member a = em.find(Member.class, "member1"); 
Member b = em.find(Member.class, "member1");
System.out.println(a == b); //동일성 비교 true
1차 캐시로 반복 가능한 읽기(REPEATABLE READ) 등급의 트랜잭
션 격리 수준을 데이터베이스가 아닌 애플리케이션 차원에서 제공
```
- 트랜잭션을 지원하는 쓰기 지연 (transactional write-behind) 
```java
EntityManager em = emf.createEntityManager();
EntityTransaction transaction = em.getTransaction();
//엔티티 매니저는 데이터 변경시 트랜잭션을 시작해야 한다.
transaction.begin(); // [트랜잭션] 시작
em.persist(memberA);
em.persist(memberB);
//여기까지 INSERT SQL을 데이터베이스에 보내지 않는다.
//커밋하는 순간 데이터베이스에 INSERT SQL을 보낸다.
transaction.commit(); // [트랜잭션] 커밋
```
- 변경 감지(Dirty Checking) 
- 지연 로딩(Lazy Loading)

# 엔티티 수정
변경 감지
```java
EntityManager em = emf.createEntityManager();
EntityTransaction transaction = em.getTransaction();
transaction.begin(); // [트랜잭션] 시작
// 영속 엔티티 조회
Member memberA = em.find(Member.class, "memberA");
// 영속 엔티티 데이터 수정
memberA.setUsername("hi");
memberA.setAge(10);
//em.update(member) 이런 코드가 있어야 하지 않을까?
transaction.commit(); // [트랜잭션] 커밋
```

# 엔티티 삭제
```java
//삭제 대상 엔티티 조회 
Member memberA = em.find(Member.class, "memberA");
em.remove(memberA); //엔티티 삭제
```

# 플러시
영속성 컨텍스트의 변경내용을 데이터베이스에 반영

# 영속성 컨텍스트를 플러시하는 방법
- em.flush() - 직접 호출 
- 트랜잭션 커밋 - 플러시 자동 호출 
- JPQL 쿼리 실행 - 플러시 자동 호출

```java
Member member = new Member(300L, "member300");
em.persist(member);

System.out.println("BEFORE FLUSH");
em.flush();
System.out.println("AFTER FLUSH");
```

# JPQL 쿼리 실행시 플러시가 자동으로 호출되는 이유
```java
em.persist(memberA);
em.persist(memberB);
em.persist(memberC);
//중간에 JPQL 실행
query = em.createQuery("select m from Member m", Member.class);
List<Member> members= query.getResultList();
```

# 플러시 모드 옵션
`em.setFlushMode(FlushModeType.COMMIT)`
- FlushModeType.AUTO
커밋이나 쿼리를 실행할 때 플러시 (기본값) 
- FlushModeType.COMMIT
커밋할 때만 플러시

# 플러시는!
- 영속성 컨텍스트를 비우지 않음 
- 영속성 컨텍스트의 변경내용을 데이터베이스에 동기화 
- 트랜잭션이라는 작업 단위가 중요 -> 커밋 직전에만 동기화하면 됨

# 준영속 상태
- 영속 -> 준영속 
- 영속 상태의 엔티티가 영속성 컨텍스트에서 분리(detached) 
- 영속성 컨텍스트가 제공하는 기능을 사용 못함

# 준영속 상태로 만드는 방법
- em.detach(entity)
  - 특정 엔티티만 준영속 상태로 전환 
- em.clear()
  - 영속성 컨텍스트를 완전히 초기화 
- em.close()
  - 영속성 컨텍스트를 종료

```java
Member member1 = em.find(Member.class, 300L);
member1.setName("AAAAA");

em.clear();

Member member2 = em.find(Member.class, 300L);
```

test