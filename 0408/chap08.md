#Chapter 08 데이터베이스 연동 지원과 JDBC 지원
##1. 스프링의 데이터베이스 연동 지원 

 중복된 코드 제거를 위한 템플릿 클래스 제공(GoF의 템플릿 패턴)
Connection conn = null;
…
try {
      conn = getConnection();
      stmt = conn.createStatement();
      rs = stmt.executeQuery(“…”);
 } catch(SQLException e) {
 … 
 } finally {
 …
      if(conn != null) conn.close();
 }
 DaoSupport : DAO에서 사용하는 기본적인 기능을 제공, 상속받아서 사용
 구체적인 Exception을 발생시킴


##2. DataSource 설정
2.1. 커넥션 풀을 이용한 Datasource 설정
<bean id="dataSource”
  class="org.apache.commons.dbcp.BasicDataSource"
	p:driverClassName="com.mysql.jdbc.Driver"
	p:url="jdbc:mysql://localhost/test"
	p:userName="test"
	p:password="1234" />
  
   dataSource : DAO가 데이터에 접근하기 위한 포인트
 DBCP(Jakarta Commons Database Connection Pool) API(connection library) 이용
 
 2.2. JNDI를 이용한 DataSource 설정
 <?xml version="1.0" encoding="UTF-8"?>

<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:jee="http://www.springframework.org/schema/jee"
	xmlns:p="http://www.springframework.org/schema/p"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
		http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
		http://www.springframework.org/schema/jee
		http://www.springframework.org/schema/jee/spring-j22-2.5.xsd">

<jee:jndi-lookup id="dataSource" jndi-name="jdbc/guestbook" resource-ref="true" />
<!– JNDI에서 객체를 검색하는 위치  : java:comp/env/jdbc/guestbook -->
<bean id="messageDao" class="MessageDao" p:dataSource-ref="dataSource" />

</beans>

 JEE App server, Tomcat, Resin 등 web container 사용하는 경우 <jee:jndi-lookup> 태그 사용하여 JNDI에 등록된 객체명 명시

2.3. DriverManager를 이용한 DataSource 설정

<bean id=“dataSource“
  class=“org.springframework.jdbc.dataSource.DriverManagerDataSource”
	p:driverClassName=“com.mysql.jdbc.Driver”
	p:url=“jdbc:mysql://localhost/test”
	p:username=“test”
	p:password=“root” />

2.4. DataSource로부터 Connection 구하기
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

 DataSource로부터 Connection 구하기
 getConnection() 활용하면 되나 스프링이 제공하는 트랜잭션 관리 기능 활용할 수 없음
 이를 방지하기 위해 DataSourceUtils 클래스를 이용하여 connection을 구하고 반환함.

##3. 스프링 JDBC 지원
