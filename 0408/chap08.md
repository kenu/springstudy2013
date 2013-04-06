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

###JDBC를 위한 템플릿 클래스
- Connection을 구하고, try-catch-finally로 자원을 관리하는 등의 중복된 코드를 제거.
- JDBC를 위한 세 개의 템플릿 클래스
####JdbcTemplate
- 기본적인 JDBC 템플릿 클래스.
- JDBC를 이용해서 데이터에 대한 접근을 제공.
###NamedParameterJdbcTemplate
- PreparedStatement에서 인덱스 기반의 파라미터가 아닌 이름을 가진 파라미터를 사용할 수 있도록 지원하는 템플릿 클래스.
###SimpleJdbcTemplate
- 자바 5의 가변 인자를 이용해서 쿼리를 실행할 때 사용되는 데이터를 전달할 수 있는 템플릿 클래스.
- 자바 1.4 이하의 버전에서는 사용할 수 없음.

###JdbcTemplate 클래스를 이용한 JDBC 프로그래밍
JdbcTemplate 클래스
- DataSource를 필요로 함.
- 설정 파일을 이용해서 JdbcTemplate의 dataSource 프로퍼티에 DataSource를 전달.

```xml      
 <bean id="dataSource"
    class="org.apache.commons.dbcp.BasicDataSource"
    p:driverClassName="com.mysql.jdbc.Driver"
    p:url="jdbc:mysql://localhost/test?useUnicode=true&amp;characterEncoding=euckr"
    p:username="root" p:password="root" />

 <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate"
    p:dataSource-ref="dataSource" />
```
###DAO 클래스에서 JdbcTemplate 사용
- set 메서드나 생성자를 통해서 JdbcTemplate을 전달 받을 수 있도록 함.

```java
public class JdbcTemplateMessageDao implements MessageDao {
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    ...
 }
```

- 설정 파일에서는 DAO에 JdbcTemplate을 전달하도록 설정.

```xml 
<bean id="messageDao"
    class="kame.spring.guestbook.dao.jdbc.JdbcTemplateMessageDao"
    p:jdbcTemplate-ref="jdbcTemplate" />
```

- 설정 완료 후 JdbcTemplate이 제공하는 기능 사용 가능.
- JdbcTemplate 클래스를 이용해서 SELECT 쿼리 실행
- query() 메서드 사용.
- List query(String sql, Object[] args, RowMapper rowMapper)
- PreparedStatement를 이용해서 SELECT 쿼리를 실행할 때 사용.
-List query(String sql, RowMapper rowMapper)
- 정적 SQL을 이용해서 SELECT 쿼리를 실행할 때 사용.
- query() 메서드
- sql 파라미터로 전달받은 SELECT 쿼리를 실행한 뒤 결과를 리턴.
- PreparedStatement를 사용할 경우 쿼리에 포함된 물음표(인덱스 파라미터)에 전달할 값을 Object[]로 전달.
###RowMapper
- ResultSet에서 값을 가져와 원하는 타입으로 매핑할 때 사용.
- 인터페이스 정의

```java
public interface RowMapper {
    Object mapRow(ResultSet rs, int rowNum) throws SQLException;
 }
```

### mapRow() 메서드
- ResultSet 에서 읽어온 값을 이용해서 원하는 타입의 객체를 생성한 뒤 리턴.
- rowNum은 행번호를 의미. (0부터 시작)
    

■ query() 메서드 실행 예

```java
private static final String SELECT_LIST_SQL = 
    "select * from GUESTBOOK_MESSAGE order by GUESTBOOK_MESSAGE_ID desc limit ?, ?";

 @SuppressWarnings("unchecked")
 @Override
 public List<Message> selectList(int page, int pageSize) {
    int startRow = (page - 1) * pageSize;
    List<Message> list = jdbcTemplate.query(SELECT_LIST_SQL, 
        new Object[] {startRow, pageSize},
        new RowMapper() {
            @Override
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                Message message = new Message();
                message.setId(rs.getInt("GUESTBOOK_MESSAGE_ID"));
                message.setGuestName(rs.getString("GUEST_NAME"));
                message.setContent(rs.getString("CONTENT"));

                return message;
            }
        }
    );

    return list;
 }
```

- query() 메서드에 RowMapper의 구현 객체를 전달할 때는 임의의 클래스(Anonymous Class)를 주로 사용.
■ RowMapper 구현 클래스
- 여러 메서드에서 공통으로 사용되는 코드가 있다면 RowMapper 구현 클래스를 별도로 구현해서 재사용.

```java
import org.springframework.jdbc.core.RowMapper;

 public class MemberRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        Message message = new Message();
        message.setId(rs.getInt("GUESTBOOK_MESSAGE_ID"));
        message.setGuestName(rs.getString("GUEST_NAME"));
        message.setContent(rs.getString("CONTENT"));

        return message;
    }
 }
 
```

■ queryForObject() 메서드
○ 정의
- 쿼리 실행 결과 행의 개수가 한 개인 경우에 사용.
      ○ 종류
        - public Object queryForObject(String sql, RowMapper rowMapper)
        - public Object queryForObject(String sql, Object[] args, RowMapper rowMapper)
      ○ 특징
        - 전달되는 각각의 파라미터는 query() 메서드와 동일.
        - 차이점 : List 대신 한 개의 객체를 리턴.
        - 쿼리 실행 결과의 행 개수가 한 개가 아닌 경우에는 IncorrectResultSizeDataAccessException 예외를 발생.
    ■ JdbcTemplate 에서 제공하는 메서드
      - Object가 아닌 int나 long 타입을 결과를 구할 때 사용할 수 있는 메서드.
      ○ 종류
        - public int queryForInt(String sql)
        - public int queryForInt(String sql, Object[] args)
        - public long queryForLong(String sql)
        - public long queryForLong(String sql, Object[] args)
      ○ 특징
        - 결과 행 개수가 한 개가 아닌 경우 예외 발생.
      ○ 사용 예

```java 
 private String SELECT_COUNT_SQL = "select count(*) form GUESTBOOK_MESSAGE";
 
 public int selectCount() {
    return jdbcTemplate.queryForInt(SELECT_COUNT_SQL);
 }
 
```

■ update() 메서드
      - INSERT, UPDATE, DELETE 쿼리를 실행할 때에 사용.
      - query() 메서드와 마찬가지로 인덱스 파라미터를 위한 값을 전달받는 메서드와 그렇지 않은 메서드로 구분.
      ○ 종류
- public int update(String sql)
- public int update(String sql, Object[] args)
○ 특징
- 쿼리 실행 결과 변경된 행의 개수를 리턴.
○ 사용 예

```java 
 private static final String INSERT_SQL = "insert into GUESTBOOK_MESSAGE " + 
    "(GUEST_NAME, CONTENT) values (?, ?)" ;

 @Override
 public void insert(Message message) {
    jdbcTemplate.update(INSERT_SQL, 
        new Object[] {message.getGuestName(), message.getContent()}
    );
 }
```

■ execute() 메서드
- Connection 을 직접 사용해야 할 경우 사용.
- 파라미터로 전달받은 ConnectionCallback 인터페이스 구현 객체의 doInConnection() 메서드를 호출하는데 이때 Connection을
doInConnection()에 전달 해야함.
- 커넥션 생성과 종료는 JdbcTemplate이 처리하므로 Connection 종료할 필요가 없음

```java 
 jdbcTemplate.execute(new ConnectionCallback() {
    @Override
    public Object doInConnection(Connection con) throws SQLException, DataAccessException {
        Statement stmt = null;

        try {
            stmt = con.createStatement();
            ...
        } finally {
            JdbcUtils.closeStatement(stmt);
        }

         return someValue;
    }
 }
```

□ NamedParameterJdbcTemplate 클래스를 이용한 JDBC 프로그래밍
■ NamedParameterJdbcTemplate 클래스
- 인덱스 기반의 파라미터가 아닌 이름 기반의 파라미터를 설정할 수 있도록 해주는 템플릿 클래스.
- 인덱스 기반의 파라미터를 전달받는 물음표를 사용하지 않고 이름 기반의 파라미터를 쿼리에서 사용할 수 있도록 지원.


    select * from GUESTBOOK_MESSAGE order by GUESTBOOK_MESSAGE_ID desc limit :startRow, :fetchSize

■ 설정
- 생성자를 이용해서 DataSource를 전달 받음.

```xml 
 <bean id="namedParameterJdbcTemplate"
    class="org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate">
    <constructor-arg>
        <ref bean="dataSource" />
    </constructor-arg>
 </bean>
```

■ 메서드
- JdbcTemplate 과 비슷한 메서드 제공.
- Object[] 대신에 Map을 전달 받음.
○ 종류
        - public List query(String sql, Map paramMap, RowMapper rowMapper)
        - public Object queryForObject(String sql, Map paramMap, RowMapper rowMapper)
        - public int queryForInt(String sql, Map paramMap)
        - public List queryForList(String sql, Map paramMap)
        - public int update(String sql, Map paramMap)
      ○ paramMap 파라미터
        - 이름 기반의 파라미터에 삽입될 값을 설정하기 위한 Map.
        - 쿼리에서 사용되는 이름과 값을 저장.
      ○ 사용 예

```java
private static final String SELECT_LIST_SQL = "select * from GUESTBOOK_MESSAGE " +
    "order by GUESTBOOK_MESSAGE_ID desc limit :startRow, :fetchSize";

 @SuppressWarnings("unchecked")
 @Override
 public List<Message> selectList(int page, int pageSize) {
    int startRow = (page - 1) * pageSize;
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("startRow", startRow);
    params.put("fetchSize", pageSize);

    List<Message> list = jdbcTemplate.query(SELECT_LIST_SQL, params,
        new RowMapper() {
            @Override
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                Message message = new Message();
                message.setId(rs.getInt("GUESTBOOK_MESSAGE_ID"));
                message.setGuestName(rs.getString("GUEST_NAME"));
                message.setContent(rs.getString("CONTENT"));

                return message;
            }
        }
    );

    return list;
 }
 
```

○ 이름 기반의 파라미터를 갖지 않는 쿼리를 실행하는 경우
- 아무 값도 갖지 않는 Map 객체를 사용.

```java
private String SELECT_COUNT_SQL = "select count(*) form GUESTBOOK_MESSAGE";

 @SuppressWarnings("unchecked")
 @Override
 public int selectCount() {
    Map paramMap = Collections.emtpyMap();
    
    return jdbcTemplate.queryForInt(SELECT_COUNT_SQL, paramMap);
 }
```

□ SimpleJdbcTemplate 클래스를 이용한 JDBC 프로그래밍
■ SimpleJdbcTemplate 클래스
- NamedParameterJdbcTemplate이 제공하는 기능을 제공하면서 더불어 JdbcTemplate과 비슷한 기능을 제공.
■ 메서드
- Map을 전달받는 메서드와 가변 인자를 전달받는 메서드를 제공.
      ○ public <T> List<T> query(String sql, ParameterizedRowMapper<T> rm, Map args)
        - 이름 기반의 파라미터를 사용하는 쿼리를 실행할 때 사용.
      ○ public <T> List<T> query(String sql, ParameterizedRowMapper<T> rm, Object... args)
        - 일반 JDBC와 마찬가지로 인덱스 기반의 파라미터를 사용하는 쿼리를 실행할 때 사용.
        - 각 물음표에 삽입될 값은 Object 배열이 아닌 가변 인자를 통해서 입력하기 때문에 코드 작성이 좀더 간단해짐.
    ■ 사용 예

```java

private static final String SELECT_LIST_SQL = "select * from GUECTBOOK_MESSAGE " +
    "order by GUESTBOOK_MESSAGE_ID desc limit ?,?";

 @Override
 public List<Message> selectList(int page, int pageSize) {
    int startRow = (page - 1) * pageSize;
    List<Message> list = jdbcTemplate.query(SELECT_LIST_SQL,
        new ParameterizedRowMapper<Message>() {
            @Override
            public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
                Message message = new Message();
                message.setId(rs.getInt("GUESTBOOK_MESSAGE_ID"));
                message.setGuestName(rs.getString("GUEST_NAME"));
                message.setContent(rs.getString("CONTENT"));

                return message;
            }
        }, 
        startRow, pageSize // 가변 인수로 파라미터 값 지정.
    );

    return list;
 }
```

■ 설정
- 생성자를 통해서 DataSource를 설정.

```xml
<bean id="simpleJdbcTemplate"
    class="org.springframework.jdbc.core.simple.SimpleJdbcTemplate">
    <constructor-arg>
        <ref-bean="dataSource" />
    </constructor-arg>
 </bean>
```

□ DaoSupport 클래스를 이용한 JDBC 기반의 DAO 구현
■ DaoSupport 클래스
- 스프링 DAO 클래스를 구현할 때 상위 클래스로 사용.
- 각각의 템플릿 클래스 별로 존재.
    ■ JDBC 기반의 DAO 클래스 구현
      ○ 종류
        ● JdbcDaoSupport
          - JdbcTemplate을 지원하는 DaoSupport 클래스.
        ● NamedParameterJdbcDaoSupport
          - NamedParameterJdbcTemplate을 지원하는 DaoSupport 클래스.
        ● SimpleJdbcDaoSupport
          - SimpleJdbcTemplate을 지원하는 DaoSupport 클래스.
      ○ 특징
        - 해당하는 템플릿 클래스를 구할 수 있는 메서드를 제공.
    ■ 설정
      - DaoSource를 프로퍼티로 전달 받음.

```xml
 <bean id="dataSource"
    class="org.apache.commons.dbcp.BasicDataSource"
    p:driverClassName="com.mysql.jdbc.Driver"
    p:url="jdbc:mysql://localhost/test?useUnicode=true&amp;characterEncoding=euckr"
    p:username="root" p:password="root" />

 <bean id="messageDao" class="kame.spring.guestbook.dao.jdbc.JdbcMessageDao"
    p:dataSource-ref="dataSource" />
```

- NamedParameterJdbcDaoSupport 클래스와 SimpleJdbcDaoSupport 클래스를 상속받은 클래스에서도 동일하게 dataSource
        프로퍼티에 DataSource를 전달.
    ■ JdbcDaoSupport 클래스를 상속받은 클래스
      - getJdbcTemplate() 메서드를 사용해서 JdbcTemplate를 구함.

```java

public class JdbcMessageDao extends JdbcDaoSupport implements MessageDao {
    private String SELECT_COUNT_SQL = "select count(*) form GUESTBOOK_MESSAGE";

    @Override
    public int selectCount() {
        return  getJdbcTemplate().queryForInt(SELECT_COUNT_SQL);
    }
 }
```

■ NamedParameterJdbcDaoSupport 클래스를 상속받은 클래스

```java
public class NamedMessageDao extends NamedParameterJdbcDaoSupport implements MessageDao {
    ...
    @Override
    public void insert(Message message) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("guestName", message.getGuestName());
        params.put("content", message.getContent());
        getNamedParameterJdbcTemplate().update(INSERT_SQL, params);
    }
    ...
 }
```

■ SimpleJdbcDaoSupport 클래스를 상속받은 클래스

```java

public class SimpleTemplateMessageDao extends SimpleJdbcDaoSupport
    implements MessageDao {
    ...
    @Override
    public void insert(Message message) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("guestName", message.getGuestName());
        params.put("content", message.getContent());

        getSimpleJdbcTemplate().update(INSERT_SQL, 
            new Object[] {message.getGuestName(), message.getContent()}
        );
    }
    ...
}
```

