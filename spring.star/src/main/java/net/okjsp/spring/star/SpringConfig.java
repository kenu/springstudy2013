package net.okjsp.spring.star;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

/*@MapperScan("net.okjsp.spring.star")*/
@Configuration
public class SpringConfig {
	private @Value("${url}")
	String url;
	private @Value("${username}")
	String username;
	private @Value("${password}")
	String password;
	private @Value("${driver}")
	String driver;

	@Bean(name = "StarService")
	public StarServiceImpl starService() {
		return new StarServiceImpl();
	}

	@Bean(name = "StarController")
	public StarController starController() {
		return new StarController();
	}

	@Bean(name="StarDao")
    public StarDao StarDao() throws Exception {
      SqlSessionTemplate sessionTemplate = new SqlSessionTemplate(sqlSessionFactory());
      return sessionTemplate.getMapper(StarDao.class);
    }
	
	@Bean(name = "sqlSessionFactory")
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
		sqlSessionFactory.setDataSource(dataSource());
		Resource mapperLocations = new ClassPathResource("sql/Star.xml");
		Resource[] r = {mapperLocations};
		sqlSessionFactory.setMapperLocations(r);
		return (SqlSessionFactory) sqlSessionFactory.getObject();
	}

	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		BasicDataSource db = new BasicDataSource();
		db.setUrl(url);
		db.setUsername(username);
		db.setPassword(password);
		db.setDriverClassName(driver);
		return db;
	}
	
	@Bean(name="jsonConverter")
	public MappingJacksonHttpMessageConverter jsonConverter(){
		MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter = new MappingJacksonHttpMessageConverter();
		List supportedMediaTypes = new ArrayList<MediaType>();
		supportedMediaTypes.add(MediaType.APPLICATION_JSON);
		mappingJacksonHttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
		return mappingJacksonHttpMessageConverter;
	}
	
	@Bean(name="jsonConverterWrapper")
	public JsonConverterWrapper jsonConverterWrapper(){
		return new JsonConverterWrapper();
	}
	
	
}
