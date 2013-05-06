package net.okjsp.spring.star;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
/**
 * convert to json class 
 * @author Donguk
 *
 */
public class JsonConverterWrapper extends MappingJacksonHttpMessageConverter{
	
	private Log log = LogFactory.getLog(this.getClass());
	
	protected  Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) {
		
		Map<String, Object> map = null;
		
		String theString = "";
		String theHeaderString = "";
		try {
			theString = IOUtils.toString(inputMessage.getBody(),"UTF-8");
			theHeaderString = inputMessage.getHeaders().toString();
			
			log.info("HEADERS \t-> " + theHeaderString);
						
			if (0 < theString.length()) {
				ObjectMapper om = new ObjectMapper();
				log.info("INPUT STRING : " + theString) ;
				map = om.readValue(theString, Map.class);
	        }
		} catch (HttpMessageNotReadableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
		
	}

	
	protected void writeInternal(Object o, HttpOutputMessage outputMessage){
		try {
			super.writeInternal(o, outputMessage);
			log.info("OUTPUT STRING :" + new ObjectMapper().writeValueAsString(o));
			
		} catch (HttpMessageNotWritableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
