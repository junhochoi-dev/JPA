# H2 Console
H2 데이터베이스 설치와 실행
- http://www.h2database.com/
- 최고의 실습용 DB
- 가볍다.(1.5M)
- 웹용 쿼리툴 제공
- MySQL, Oracle 데이터베이스 시뮬레이션 기능
- 시퀀스, AUTO INCREMENT 기능 지원
- javaw 오류 https://www.inflearn.com/chats/469083/h2-%EC%BD%98%EC%86%94-%EC%8B%A4%ED%96%89%EC%8B%9C-javaw%EB%A5%BC-%EC%B0%BE%EC%9D%84-%EC%88%98-%EC%97%86%EC%8A%B5%EB%8B%88%EB%8B%A4-%EC%98%A4%EB%A5%98-%ED%95%B4%EA%B2%B0%EB%B0%A9%EB%B2%95
- DB not found 오류 C:\Users\SSAFY 에 test.mv.db 파일 생성
- 추가 방법 아래

위 이미지 처럼 JDBC URL에 `jdbc:h2:~/test` 라고 적어주시고 한번만 연결을 해주시면 데이터베이스 파일이 생성되면서 연결됩니다.
그리고 이후에는 jdbc:h2:tcp://localhost/~/test 로 접속해주세요.
- H2 버전 디펜던시와 설치파일 맞추기

# Maven
- https://maven.apache.org/
- 자바 라이브러리, 빌드 관리
- 라이브러리 자동 다운로드 및 의존성 관리
- 최근에는 그래들(Gradle)이 점점 유명

# Project
자바 8 이상(8 권장)
- 메이븐 설정
- groupId: jpa-basic
- artifactId: ex1-hello-jpa
- version: 1.0.0

```xml
<?xml version="1.0" encoding="UTF-8"?> 
<project xmlns="http://maven.apache.org/POM/4.0.0" 
 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
 xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd"> 
 <modelVersion>4.0.0</modelVersion> 
 <groupId>jpa-basic</groupId> 
 <artifactId>ex1-hello-jpa</artifactId> 
 <version>1.0.0</version> 
 <dependencies> 
 <!-- JPA 하이버네이트 --> 
 <dependency> 
 <groupId>org.hibernate</groupId> 
 <artifactId>hibernate-entitymanager</artifactId> 
 <version>5.3.10.Final</version> 
 </dependency> 
 <!-- H2 데이터베이스 --> 
 <dependency> 
 <groupId>com.h2database</groupId> 
 <artifactId>h2</artifactId> 
 <version>1.4.199</version> 
 </dependency> 
 </dependencies> 
</project>
```

# JPA 설정하기 - persistence.xml
- JPA 설정 파일
- /META-INF/persistence.xml 위치
- persistence-unit name으로 이름 지정
- javax.persistence로 시작: JPA 표준 속성
- hibernate로 시작: 하이버네이트 전용 속성

`persistence.xml`
```xml
?xml version="1.0" encoding="UTF-8"?> 
<persistence version="2.2" 
 xmlns="http://xmlns.jcp.org/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
 xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd"> 
 <persistence-unit name="hello"> 
 <properties> 
 <!-- 필수 속성 --> 
 <property name="javax.persistence.jdbc.driver" value="org.h2.Driver"/> 
 <property name="javax.persistence.jdbc.user" value="sa"/> 
 <property name="javax.persistence.jdbc.password" value=""/> 
 <property name="javax.persistence.jdbc.url" value="jdbc:h2:tcp://localhost/~/test"/> 
 <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/> 
 
 <!-- 옵션 --> 
 <property name="hibernate.show_sql" value="true"/> 
 <property name="hibernate.format_sql" value="true"/> 
 <property name="hibernate.use_sql_comments" value="true"/> 
 <!--<property name="hibernate.hbm2ddl.auto" value="create" />--> 
 </properties> 
 </persistence-unit> 
</persistence> 
```

# 데이터베이스 방언
- JPA는 특정 데이터베이스에 종속 X
- 각각의 데이터베이스가 제공하는 SQL 문법과 함수는 조금씩 다름
- 가변 문자: MySQL은 VARCHAR, Oracle은 VARCHAR2
- 문자열을 자르는 함수: SQL 표준은 SUBSTRING(), Oracle은 SUBSTR()
- 페이징: MySQL은 LIMIT , Oracle은 ROWNUM
- 방언: SQL 표준을 지키지 않는 특정 데이터베이스만의 고유한 기능

# 데이터베이스 방언
- hibernate.dialect 속성에 지정
- H2 : org.hibernate.dialect.H2Dialect
- Oracle 10g : org.hibernate.dialect.Oracle10gDialect
- MySQL : org.hibernate.dialect.MySQL5InnoDBDialect
- 하이버네이트는 40가지 이상의 데이터베이스 방언 지원

# JPA MAIN + Member

```java
public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        Member member = new Member();
        member.setId(1L);
        member.setName("HELLO");

        em.persist(member);

        tx.commit();

        em.close();

        emf.close();
    }
}
```
```java
package com.project.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Member {
    @Id
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```
# CRUD
```java
// Create
Member member = new Member();
member.setId(3L);
member.setName("TEST");
em.persist(member);

// Read
Member findMember = em.find(Member.class, 1L);
System.out.println("findMember.id \t\t = " + findMember.getId());
System.out.println("findMember.name \t = " + findMember.getName());

// Update
Member findMember = em.find(Member.class, 2L);
findMember.setName("HelloJPA");

// Delete
Member findMember = em.find(Member.class, 1L);
em.remove(findMember);
```
# 주의

엔티티 매니저 팩토리는 하나만 생성해서 애플리케이션 전체에
서 공유
- 엔티티 매니저는 쓰레드간에 공유X (사용하고 버려야 한다). 
- JPA의 모든 데이터 변경은 트랜잭션 안에서 실행

# JPQL(Java Persistence Query Language)

JPQL 소개
- 가장 단순한 조회 방법
- EntityManager.find() 
- 객체 그래프 탐색(a.getB().getC()) 
- 나이가 18살 이상인 회원을 모두 검색하고 싶다면??
```java
List<Member> result = em.createQuery("select m from Member as m", Member.class)
            .setFirstResult(2) // 시작 index
            .setMaxResults(3) // paging
            .getResultList();

for(Member member : result){
    System.out.println(member.getId() + " " + member.getName());
}
```
- JPA를 사용하면 엔티티 객체를 중심으로 개발
- 문제는 검색 쿼리
- 검색을 할 때도 테이블이 아닌 엔티티 객체를 대상으로 검색
- 모든 DB 데이터를 객체로 변환해서 검색하는 것은 불가능
- 애플리케이션이 필요한 데이터만 DB에서 불러오려면 결국 검색 조건이 포함된 SQL이 필요

- JPA는 SQL을 추상화한 JPQL이라는 객체 지향 쿼리 언어 제공
- SQL과 문법 유사, SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN 지원
- JPQL은 엔티티 객체를 대상으로 쿼리
- SQL은 데이터베이스 테이블을 대상으로 쿼리

- 테이블이 아닌 객체를 대상으로 검색하는 객체 지향 쿼리
- SQL을 추상화해서 특정 데이터베이스 SQL에 의존X 
- JPQL을 한마디로 정의하면 객체 지향 SQL 
- JPQL은 뒤에서 아주 자세히 다룸