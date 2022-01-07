# 자바 ORM 표준 JPA 기본편
인프런 김영한님의 '자바 ORM 표쥰 JPA 기본편' 강의를 학습한 프로젝트

## 영속성 관리
### 영속성 컨텍스트
- 영속성 컨텍스트란 엔티티를 영구 저장하는 환경이다.
- 엔티티 매니저로 엔티티를 저장하거나 조회하면 엔티티 매니저는 영속성 컨텍스트에 엔티티를 보관하고 관리한다.
- 영속성 컨텍스트는 엔티티 매니저를 생성할 때 하나 만들어진다.

### 엔티티 생명주기
- 비영속(new/transient): 영속성 컨텍스트와 전혀 관계가 없는 상태
- 영속(managed): 영속성 컨텍스트에 저장된 상태
- 준영속(detacked): 영속성 컨텍스트에 저장되었다가 분리된 상태
- 삭제(removed): 삭제된 상태

### 영속성 컨텍스트의 특징
- <b>영속성 컨텍스트와 식별자 값</b><br>- 영속성 컨텍스트는 엔티티를 식별자 값(@Id로 테이블의 기본 키와 매핑한 값)으로 구분한다. 따라서 영속 상태는 식별자 값이 반드시 있어야 한다.
- <b>영속성 컨텍스트와 데이터베이스 저장</b><br>- 엔티티를 데이터베이스에 반영하는데 이것을 플러시(flush)라 한다.
- <b>영속성 컨텍스트 관리 장점</b><br>- 1차 캐시<br>- 동일성 보장<br>- 트랜잭션을 지원하는 쓰기 지연<br>- 지연 로딩

### 엔티티 조회
- 영속성 컨텍스트는 내부에 캐시를 가지고 있는데 이것을 1차 캐시라 한다. 영속성 컨텍스트 내부에 Map이 하나 있는데 키는 @Id로 매핑한 식별자고 값은 엔티티 인스턴스다


- <b>엔티티 등록</b><br>- 엔티티 매니저는 트랜잭션을 커밋하기 직전까지 데이터베이스에 엔티티를 저장하지 않고 내부 쿼리 저장소에 INSERT SQL을 모아둔다. 이를 트랜잭션을 지원하는 쓰기 지연이라 한다.<br>- 트랜잭션을 커밋하면 엔티티 매니저는 우선 영속성 컨텍스트를 플러시한다. 플러시는 영속성 컨텍스트의 변경 내용을 데이터베이스에 동기화하는 작업인데 이때 등록, 수정, 삭제한 엔티티를 데이터베이스에 반영한다.
- <b>변경 감지</b><br>1. 트랜잭션을 커밋하면 엔티티 매니저 내부에서 먼저 플러시가 호출된다.<br>2. 엔티티와 스냅샷을 비교해서 변경된 엔티티를 찾는다.<br>3. 변경된 엔티티가 있으면 수정 쿼리를 생성해서 쓰기 지연 SQL 저장소에 보낸다.<br>4. 쓰기 지연 저장소의 SQL을 데이터베이스에 보낸다.<br>5. 데이터베이스 트랜잭션을 커밋한다.
<br><b>변경 감지는 영속성 컨텍스트가 관리하는 영속 상태의 엔티티에만 적용된다</b>

### 플러시
플러시는 영속성 컨텍스트의 변경 내용을 데이터베이스에 반영한다.
1. 변경 감지가 동작해서 영속성 컨텍스트에 있는 모든 엔티티를 스냅샷과 비교해서 수정된 엔티티를 찾는다. 수정된 엔티티는 수정 쿼리를 만들어 쓰기 지연 SQL 저장소에 등록한다.
2. 쓰기 지연 SQL 저장소의 쿼리를 데이터베이스에 전송한다.
<br>
![flsu](https://user-images.githubusercontent.com/66157892/148165444-bde6fa47-2b69-4145-ba07-6c15cb2b566d.PNG)

## 엔티티 매핑
#### 매핑 어노테이션
- 객체와 테이블 매핑 : @Entity, @Table
- 기본 키 매핑 : @Id
- 필드와 컬럼 매핑 : @Column
- 연관관계 매핑 : @ManyToOne, @JoinColumn

### @Entity
- JPA를 사용해서 테이블과 매핑할 클래스는 @Entity 어노테이션을 필수로 붙여야 한다.
- 기본 생성자는 필수다(파라미터가 없는 public 또는 protected 생성자)
- final 클래스, enum, interface, inner 클래스에는 사용할 수 없다.
- 저장할 필드에 final을 사용하면 안 된다.

### @Table
- @Table은 엔티티와 매핑할 테이블을 지정한다. 생략하면 매핑한 엔티티 이름을 테이블 이름으로 사용한다.

### 기본 키 매핑
- 직접 할당 : em.persist()를 호출하기 전에 애플리케이션에서 직접 식별자 값을 할당해야 한다. 만약 식별자 값이 없으면 예외가 발생한다.
- SEQUENCE : 데이터베이스 시퀀스에서 식별자 값을 획득한 후 영속성 컨텍스트에 저장한다.
- TABLE : 데이터베이스 시퀀스 생성용 테이블에서 식별자 값을 획득한 후 영속성 컨텍스트에 저장한다.
- IDENTITY : 데이터베이스에 엔티티를 저장해서 식별자 값을 획득한 후 영속성 컨텍스트에 저장한다. (IDENTITY 전략은 테이블에 데이터를 저장해야 식별자 값을 획득할 수 있다.)

### 필드와 컬럼 매핑
![매핑 어노테이션](https://user-images.githubusercontent.com/66157892/148383712-125e2293-6863-45c3-88aa-20f4efadc86f.PNG)<br>

### @Column
![column](https://user-images.githubusercontent.com/66157892/148383921-601e77fb-2b34-4e5a-90b1-01255ece6e2e.PNG)<br>

## 연관관계 매핑 기초
<b>객체를 테이블에 맞추어 데이터 중심으로 모델링하면, 협력 관계를 만들 수 없다</b>
- 테이블은 외래 키로 조인을 사용해서 연관된 테이블을 찾는다.
- 객체는 참조를 사용해서 연관된 객체를 찾는다.
- 테이블과 객체 사이에는 이런 큰 간격이 있다.
### 객체 연관관계와 테이블 연관관계의 가장 큰 차이
- 참조를 통한 연관관계는 언제나 단방향이다. 객체간에 연관관계를 양방향으로 만들고 싶으면 반대쪽에도 필드를 추가해서 참조를 보관해야 한다. 하지만 정확히 이야기하면 이것은 양방향 관계가 아니라 서로 다른 단방향 관계 2개다.

### 단방향 연관관계

    @Entity
    public class Member {
        @Id
        @Column(name = "MEMBER_ID")
        private String id;
        private String username;
    
        //연관관계 매핑
        @ManyToOne
        @JoinColumn(name="TEAM_ID")
        private Team team;
        
        //연관관계 설정
        public void setTeam(Team team) {
            this.team = team;
        }
        //Getter, Setter ...
    }

    @Entity
    public class Team {
    @Id
    @Column (name = "TEAM_ID")
    private String id;
    
        private String name;
        //Getter, Setter ...
    }
- @JoinColumn은 외래 키를 매핑할 때 사용한다.
- @ManyToOne 어노테이션은 다대일 관계에서 사용한다.
### 양방향 연관관계

    @Entity
    public class Member {
        @Id
        @Column (name = ”MEMBER_ID”)
        private String id;
        private String username;
        
        @ManyToOne
        @JoinColumn(name="TEAM_ID H)
        private Team team;
        
        //연관관계 :설정
        public void setTeam(Team team) {
        this.team = team;
        }
        //Getter, Setter ...  
    }

    @Entity
    public class Team {
        @Id
        @Column(name = ”TEAM_ID”)
        private String id;
        
        private String name;
        
        //==추가==//
        @OneToMany (mappedBy = "team")
        private List<Member> members = new ArrayList<Member> () ;
        
        ...
    }

### 연관관계의 주인
<b>@OneToMany만 있으면 되지 mappedBy는 왜 필요할까?</b>
- 객체에는 양방향 연관관계라는 것이 없다. 서로 다른 단방향 연관관계 2개를 애플리케이션 로직으로 잘 묶어서 양방향인 것처럼 보이게 할 뿐이다.
- 연관관계의 주인은 외래 키가 있는 곳

## 다양한 연관관계 매핑
<b>연관관계 매핑 시 고려사항 3가지</b>
- 다중성
- 단방향, 양방향
- 연관관계의 주인

### 다대일(@ManyToOne)
#### 다대일 단방향
- 가장 많이 사용하는 연관관계
- 다대일의 반대는 일대다
#### 다대일 양방향
- 외래 키가 있는 쪽이 연관관계의 주인
- 양쪽을 서로 참조하도록 개발

### 일대다(@OneToMany)
#### 일대다 단방향
- 일대다 단방향은 일대다(1:N)에서 일(1)이 연관관계의 주인
- 테이블 일대다 관계는 항상 다(N) 쪽에 외래 키가 있음
- 객체와 테이블의 차이 때문에 반대편 테이블의 외래 키를 관리하는 특이한 구조
- @JoinColumn을 꼭 사용해야 함. 그렇지 않으면 조인 테이블 방식을 사용함(중간에 테이블을 하나 추가함)
- 일대다 단방향 매핑의 단점
- 엔티티가 관리하는 외래 키가 다른 테이블에 있음
- 연관관계 관리를 위해 추가로 UPDATE SQL 실행
- 일대다 단방향 매핑보다는 다대일 양방향 매핑을 사용하자

#### 일대다 양방향
- 이런 매핑은 공식적으로 존재X
- @JoinColumn(insertable=false, updatable=false)
- 읽기 전용 필드를 사용해서 양방향 처럼 사용하는 방법
- 다대일 양방향을 사용하자

### 일대일(@OneToOne)
- 일대일 관계는 그 반대도 일대일
- 주 테이블이나 대상 테이블 중에 외래 키 선택 가능
- 다대일 양방향 매핑 처럼 외래 키가 있는 곳이 연관관계의 주인 
- 반대편은 mappedBy 적용

### 다대다(@ManyToMany)
- 관계형 데이터베이스는 정규화된 테이블 2개로 다대다 관계를 표현할 수 없음
- 연결 테이블을 추가해서 일대다, 다대일 관계로 풀어내야함
- @JoinTable로 연결 테이블 지정
- 다대다 매핑: 단방향, 양방향 가능

## 고급 매핑
- 상속관계 매핑
- @MappedSuperclass
### 상속관계 매핑
- 관계형 데이터베이스는 상속 관계X
- 슈퍼타입 서브타입 관계라는 모델링 기법이 객체 상속과 유사
- 상속관계 매핑: 객체의 상속과 구조와 DB의 슈퍼타입 서브타입 관계를 매핑

### 주요 어노테이션과 상속 전략
- @Inheritance(strategy=InheritanceType.XXX)
- @DiscriminatorColumn(name=“DTYPE”)
- @DiscriminatorValue(“XXX”)
- JOINED: 조인 전략
- SINGLE_TABLE: 단일 테이블 전략
- TABLE_PER_CLASS: 구현 클래스마다 테이블 전략

### 조인 전략

    @Entity
    @Inheritance(strategy = InheritanceType.JOINED) // 상속 매핑은 부모 클래스에 선언해야 한다.
    @DiscriminatorColumn(name = "DTYPE") // 부모 클래스에 구분 컬럼을 지정한다.
        public abstract class Item {
    
        @Id @GeneratedValue
        @Column(name = "ITEM_ID")
        private Long id;
    
        private String name; //이름
        private int price; //가격
        ...
    }
    
    @Entity
    @DiscriminatorValue("A") // 엔티티를 저장할 때 구분 컬럼에 입력할 값을 지정한다.
    public class Album extends Item {
        private String artist;
        ...
    }
    
    @Entity
    @DiscriminatorValue("M")
    @PrimaryKeyJoinColumn(name = "MOVIE_ID") // 자식 테이블의 기본 키 컬럼명을 변경 (기본 값은 부모 테이블의 ID 컬럼명)
    public class Movie extends Item {
        private String director; //감독
        private String actor; //배우
        ...
    }

#### 장점
- 테이블이 정규화된다.
- 외래 키 참조 무결성 제약 조건을 활용할 수 있다.
- 저장공간을 효율적으로 사용한다.
#### 단점
- 조회할 때 조인이 많이 사용되므로 성능이 저하될 수 있다.
- 조회 쿼리가 복잡하다
- 데이터를 등록할 INSERT SQL을 두 번 실행한다.

### 단일 테이블 전략
- 이름 그대로 테이블을 하나만 사용한다. 그리고 구분 컬럼(DTYPE)으로 어떤 자식 데이터가 저장되었는지 구분한다. 조회할 때 조인을 사용하지 않으므로 일반적으로 가장 빠르다. 이 전략을 사용할 때 주의점은 자식 엔티티가 매핑한 컬럼은 모두 null을 허용해야 한다

    
    @Entity
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    @DiscriminatorColumn(name = "DTYPE")
    public abstract class Item {
    
        @Id @GeneratedValue
        @Column(name = "ITEM_ID")
        private Long id;
    
        private String name; //이름
        private int price; //가격
        ...
    }

    @Entity
    @DiscriminatorValue("A")
    public class Album extends Item {
        ...
    }
    
    @Entity
    @DiscriminatorValue("M")
    public class Movie extends Item {
        ...
    }

#### 장점
- 조인이 필요 없으므로 일반적으로 조회 성능이 빠르다
- 조회 쿼리가 단순하다
#### 단점
- 자식 엔티티가 매핑한 컬럼은 모두 null을 허용해야 한다.
- 단일 테이블에 모든 것을 저장하므로 테이블이 커질 수 있다. (성능이 더 안좋아 질 수 있음)
#### 특징
- 구분 컬럼을 꼭 사용해야 한다. (지정하지 않으면 기본으로 엔티티 이름을 사용한다)

### 구현 클래스마다 테이블 전략
#### 장점
- 서브 타입을 구분해서 처리할 때 효과적이다.
- not null 제약조건을 사용할 수 있다.
#### 단점
- 여러 자식 테이블을 함께 조회할 때 성능이 느리다. (SQL에 UNION을 사용해야 한다)
- 자식 테이블을 통합해서 쿼리하기 어렵다
## 프록시와 연관관계 관리
<b>엔티티를 조회할 때 연관된 엔티티들이 항상 사용되는 것은 아니다. 연관관계의 엔티티는 비즈니스 로직에 따라 사용될 때도 있지만 그렇지 않을 때도 있다.</b>

### 프록시
#### 프록시 특징
- 실제 클래스를 상속 받아서 만들어짐.
- 실제 클래스와 겉 모양이 같다.
- 사용하는 입장에서는 진짜 객체인지 프록시 객체인지 구분하지 않고 사용하면 됨(이론상)
- 프록시 객체는 실제 객체의 참조(target)를 보관
- 프록시 객체를 호출하면 프록시 객체는 실제 객체의 메소드 호출
![de](https://user-images.githubusercontent.com/66157892/148494907-e651c2c0-8e32-4ca2-9c4d-51424534f8ef.PNG)
- <b>프록시 초기화</b>
![image](https://user-images.githubusercontent.com/66157892/148494876-ba2a2a4f-97e6-4609-ab69-e2e561bd40f1.png)
- 프록시 객체는 처음 사용할 때 한 번만 초기화
- 프록시 객체를 초기화 할 때, 프록시 객체가 실제 엔티티로 바뀌는 것은 아님, 초
- 기화되면 프록시 객체를 통해서 실제 엔티티에 접근 가능
- 프록시 객체는 원본 엔티티를 상속받음, 따라서 타입 체크시 주의해야함 (== 비
  교 실패, 대신 instance of 사용)
- 영속성 컨텍스트에 찾는 엔티티가 이미 있으면 em.getReference()를 호출해
  도 실제 엔티티 반환
### 지연 로딩
#### Member를 조회할 때 Team도 함께 조회해야 할까?
#### 지연 로딩 LAZY을 사용해서 프록시로 조회
    @Entity
    public class Member {
        @Id
        @GeneratedValue
        private Long id;
        @Column(name = "USERNAME")
        private String name;
        @ManyToOne(fetch = FetchType.LAZY) //**
        @JoinColumn(name = "TEAM_ID")
        private Team team;
        ..
    }
- 지연로딩을 활용한 프록시 조회
![laz](https://user-images.githubusercontent.com/66157892/148495248-ff6a0aa6-e831-475d-8088-542d5baca7e4.PNG)

#### 지연 로딩 활용
- 모든 연관관계에 지연 로딩을 사용해라!
- 실무에서 즉시 로딩을 사용하지 마라!
- JPQL fetch 조인이나, 엔티티 그래프 기능을 사용해라!

### 즉시 로딩
#### Member와 Team을 자주 함께 사용한다면?

    @Entity
    public class Member {
        @Id
        @GeneratedValue
        private Long id;
        @Column(name = "USERNAME")
        private String name;
        @ManyToOne(fetch = FetchType.EAGER) //**
        @JoinColumn(name = "TEAM_ID")
        private Team team;
        ..
    }
- 즉시로딩을 통해 조회
  ![eager](https://user-images.githubusercontent.com/66157892/148495471-9796bbed-3703-4504-920c-c8050fe2f2c5.PNG)

#### 프록시와 즉시로딩 주의
- 가급적 지연 로딩만 사용(특히 실무에서)
- 즉시 로딩은 JPQL에서 N+1 문제를 일으킨다.
- @ManyToOne, @OneToOne은 기본이 즉시 로딩 -> LAZY로 설정
- @OneToMany, @ManyToMany는 기본이 지연 로딩
  
### 영속성 전이: CASCADE
- 특정 엔티티를 영속 상태로 만들 때 연관된 엔티티도 함께 영속 상태로 만들도 싶을 때 (예: 부모 엔티티를 저장할 때 자식 엔티티도 함께 저장.)
  ![cascade](https://user-images.githubusercontent.com/66157892/148495894-4306eef3-1101-4358-ba22-142a9c63b903.PNG)

#### CASCADE 의 종류
- ALL: 모두 적용
- PERSIST: 영속
- REMOVE: 삭제
- MERGE: 병합
- REFRESH: REFRESH
- DETACH: DETACH

##### 영속성 전이 주의
- 영속성 전이는 연관관계를 매핑하는 것과 아무 관련이 없음
- 엔티티를 영속화할 때 연관된 엔티티도 함께 영속화하는 편리함
을 제공할 뿐

### 고아 객체
- 고아 객체 제거: 부모 엔티티와 연관관계가 끊어진 자식 엔티티
를 자동으로 삭제
- orphanRemoval = true
    

    @Entity
    public class Parent {
    @Id @GeneratedValue
    private Long id;
    
        @OneToMany(mappedBy = "parent", orphanRemoval = true)
        private List<Child> children = new ArrayList<Child>();
        ...
    }


    //자식 엔티티를 컬렉션에서 제거
    Parent parent1 = em.find(Parent.class, id);
    parent1.getChildren().remove(0);

### 영속성 전이 + 고아 객체, 생명주기
- CascadeType.ALL + orphanRemoval = true를 동시에 사용하면 어떻게 될까?
  일반적으로 엔티티는 EntityManager.persist()를 통해 영속화되고 EntityManager.remove()를 통해 제거된다. 이것은 엔티티 스스로 생명주기를 관리한다는 뜻이다. 그런데 두 옵션을 모두 활성화하면 부모 엔티티를 통해서 자식의 생명주기를 관리할 수 있다.
  영속성 전이는 DDD의 Aggregate Root개념을 구현할 때 사용하면 편리하다
  
## 값 타입
- int, Integer, String처럼 단순히 값으로 사용하는 자바 기본 타입이나 객체
- 식별자가 없고 값만 있으므로 변경시 추적 불가 예) 숫자 100을 200으로 변경하면 완전히 다른 값으로 대체
- 값 타입은 크게 세가지(기본 값, 임베디드 타입, 값 타입 컬렉션)

### 기본 값
- 예): String name, int age
- 생명주기를 엔티티의 의존 예) 회원을 삭제하면 이름, 나이 필드도 함께 삭제
- 값 타입은 공유하면X 예) 회원 이름 변경시 다른 회원의 이름도 함께 변경되면 안됨

### 임베디드 타입
- 새로운 값 타입을 직접 정의할 수 있음
- JPA는 임베디드 타입(embedded type)이라 함
- 주로 기본 값 타입을 모아서 만들어서 복합 값 타입이라고도 함
- int, String과 같은 값 타입