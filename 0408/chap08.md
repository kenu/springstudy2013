#Chapter 08 데이터베이스 연동 지원과 JDBC 지원
##1. 스프링의 데이터베이스 연동 지원 

1.1 데이터베이스 연동

- 데이터베이스 연동을 구현하는 전형적인 방법
- 데이터 베이스에 접근하는 클래스와 서비스 로직을 구현한 클래스를 구분.
- 데이터베이스 접근을 위한 DAO(Data Access Object)를 만들고, 서비스 클래스에서는 DAO를 호출해서 데이터에 대한 CRUD를 처리.

스프링의 데이터베이스 연동 지원

JDBC, 하이버네이트, iBATIS 등의 다양한 기술을 이용해서 손쉽게 DAO 클래스를 구현할 수 있도록 지원.

- 템플릿 클래스를 통한 데이터 접근 지원.
- DaoSupport 클래스를 이용한 DAO 클래스 구현.
- 의미 있는 예외 클래스 제공.

데이터베이스 연동을 위한 템플릿 클래스
- 데이터에 접근하는 코드는 거의 동일한 코드 구성.
- JDBC를 사용해서 특정 테이블에서 데이터를 로딩하는 코드 형식



```java
Connection conn = null;
 PreparedStatement pstmt = null;
 ResultSet rs = null;

 try {
    conn = getConnection();
    pstmt = conn.prepareStatement("select * from message where guestBookId = ?");
    pstmt.setInt(1, guestBookId);
    
    rs = pstmt.executeQuery();

    if(rs.next()) {
        do {
            Message message = new Message();
            message.setContent(rs.getString("content"));
            ...
        } while(rs.next());
    }
 } catch(SQLException ex) {
    // 알맞은 예외 처리.
 } finally {
    if(rs != null) rs.close();
    if(pstmt != null) pstmt.close();
    if(conn != null) conn.close();
 }
```
- Connection을 생성하고 PreparedStatement, ResultSet, 그리고 Connection 등의 자원을 반환하는 코드는 거의 모든 JDBC 코드에서 중복되는 코드.
- PreparedStatement를 구하고 ResultSet으로부터 데이터를 읽어와 자바빈 객체에 저장하는 코드 역시 동일한 형식



1.2 스프링의 예외 지원 
- 데이터베이스 처리 과정에서 발생한 예외가 왜 발생했는지를 좀 더 구체적으로 확인하기 위해 데이터베이스 처리와 관련된 예외 클래스 제공.
- OptimisticLockingFailureException 이나 DataRetrievalFailureException과 같이 보다 구체적인 실패 원인을 설명해 주는 예외 클래스 제공.
- 스프링이 제공하는 템플릿 클래스는 내부적으로 발생하는 예외 클래스를 스프링이 제공하는 예외 클래스로 알맞게 변환해서 예외를 발생시킴.
- 데이터베이스 연동을 위해 사용하는 기술에 상관없이 동일한 방식으로 예외 처리.

DAO에서 사용하는 기본적인 기능을 제공, 상속받아서 사용

올바르지 않은 SQL 쿼리를 실행하는 경우 JdbcTemplate은 BadSqlGrammarException 구체적인 예외(Exception)를 발생 시킴.

```java
JdbcTemplate jdbcTemplate = getJdbcTemplate();
 List<Message> list = jdbcTemplate.query(
    "select * from GUESTBOOK_MESSAGE order by GUESTBOOK_MESSAGE_ID desc limited ?,?",
    // 잘못된 SQL 입력시
    ...
 );
```

필요한 경우에만 try-catch  블록을 이용하여 예외 처리를 하면 됨

##2. DataSource 설정
2.1. 커넥션 풀을 이용한 Datasource 설정
```xml
<bean id="defaultDataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
        <property name="driverClassName" value="${mysql.jdbc.driverClassName}" />
        <property name="url" value="${mysql.jdbc.url}" />
        <property name="username" value="${mysql.jdbc.username}" />
        <property name="password" value="${mysql.jdbc.password}" />
        <property name="initialSize" value="10"/>
        <property name="maxActive" value="30"/>
        <property name="maxIdle" value="15" />
        <property name="minIdle" value="15" />
        <property name="testOnBorrow" value="false" />
        <property name="validationQuery" value="select 1" />
        <property name="timeBetweenEvictionRunsMillis" value="10000" />
        <property name="testWhileIdle" value="true" />  
        <property name="numTestsPerEvictionRun" value="3" />        
        <property name="minEvictableIdleTimeMillis" value="-1" />
    </bean> 
```

```xml
<bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource" destroy-method="close">
    <property name="driverClass" value="org.gjt.mm.mysql.Driver" />
    <property name="jdbcUrl" value="jdbc:mysql://localhost/testdb" />
    <property name="user" value="${jdbc.username}" />
    <property name="password" value="${jdbc.password}" />
    <property name="initialPoolSize" value="5" />
    <property name="maxPoolSize" value="30" />
    <property name="minPoolSize" value="5" />
    <property name="acquireIncrement" value="3" />
    <property name="acquireRetryAttempts" value="30" />
    <property name="acquireRetryDelay" value="1000" />
    <property name="idleConnectionTestPeriod" value="60" />
    <property name="preferredTestQuery" value="SELECT 1" />
    <property name="testConnectionOnCheckin" value="true" />
    <property name="testConnectionOnCheckout" value="false" />
</bean>
```
   dataSource : DAO가 데이터에 접근하기 위한 포인트
 DBCP(Jakarta Commons Database Connection Pool) API(connection library) 이용
 
 2.2. JNDI를 이용한 DataSource 설정

```xml
<?xml version="1.0" encoding="UTF-8"?>

<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:context="http://www.springframework.org/schema/context" xmlns:p="http://www.springframework.org/schema/p"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context-3.0.xsd">

<!-- "java:comp/env/jdbc/guestbook"을 사용해서 JNDI에서 객체 검색. -->
<jee:jndi-lookup id="dbDataSource" jndi-name="jdbc/guestbook"  resource-ref="true" expected-type="javax.sql.DataSource" />

  <bean id="messageDao" class="kame.spring.guestbook.dao.jdbc.JdbcMessageDao"
        p:dataSource-ref="dataSource" />
</beans>
```

 JEE App server, Tomcat, Resin 등 web container 사용하는 경우 <jee:jndi-lookup> 태그 사용하여 JNDI에 등록된 객체명 명시

2.3. DriverManager를 이용한 DataSource 설정
```xml
<bean id=“dataSource“
  class=“org.springframework.jdbc.dataSource.DriverManagerDataSource”
	p:driverClassName=“com.mysql.jdbc.Driver”
	p:url=“jdbc:mysql://localhost/test”
	p:username=“test”
	p:password=“root” />
```

2.4. DataSource로부터 Connection 구하기
```java
public class JdbcMessageDao omplements MessageDao {
  private DataSource dataSource;
	public void setDataSource(DataSource dataSource) {
		this.dataSource=dataSource;
	}
	@Override
	public int selectCount() {
		Connection conn=null;
		try {
			// conn=dataSource.getConnection();
			conn=DataSourceUils.getConnection(dataSource);
		} finally {
			// JdbcUtils.closeConnection(conn);
			datasourceutils.releaseConnection(conn,dataSource);	
		}
	}
} 
```
 DataSource로부터 Connection 구하기
 getConnection() 활용하면 되나 스프링이 제공하는 트랜잭션 관리 기능 활용할 수 없음
 이를 방지하기 위해 DataSourceUtils 클래스를 이용하여 connection을 구하고 반환함.

##3. 스프링 JDBC 지원
