1. What is Message ? - Message란 무엇이고 왜 사용 하는가
2. JMS Architecture - JMS의 전반적인 아키텍쳐와 사용 모델
3. JMS Element - JMS에서 사용하는 엘리먼트들에 대한 설명


##1. What is Message? - Message란 무엇이고 왜 사용 하는가

메시지은 느슨하게 결합된 분산 통신의 한 형태이다. 
여기서 통신이란 용어는 소프트웨어 컴포넌트들 간의 구성요소들 사이에 메시지의 교환으로 이해할 수 있다. 
메시지 지향 테크놀로지들은 강하게 결합된 통신(TCP network socekst, CORBA or RMI 같은)을 중간요소의 
도입을 통해 완하하려고 시도한다.  후자의 접근 방식을 통해 소프트웨어 컴포넌트가 서로 '간접적으로' 통신 할 수 있다. 
이러한 메시지의 장점은 보내는 쪽에서 받는 쪽에 대한 정확한 정보가 없어도 가능 하다는 점이다.



![screenshot](http://cfile25.uf.tistory.com/image/171199524E0AB5AA2DC047)
####안좋은 예

간단한 메시징 시스템의 예로 콜센터를 들 수 있다.
고객과 콜센터 직원이 라인을 통해 다이렉트로 통화를 할 경우, 
고객이 늘어날 경우에 콜센터 직원이 통화가 가능 할 때까지 
계속 다시 전화를 걸어서 통화가 가능한 라인을 찾아야 한다.

![screenshot](http://cfile27.uf.tistory.com/image/17435D544E0AB64C0B118B)
####기존에 시스템에 경유할수 있는 경유지를 두는 경우

위 그림처럼 중간에 경유할 수 있는 Voice Mail Box 같은 장치를 두면, 
고객의 요청은 box에 쌓이게 되고 직원은 이것을 확인하고 처리 하면 되기 
때문에 고객은 계속 전화를 걸어 통화가 가능한 라인이 있는지 확인할 필요 없이 처리 할 수 있다.

JMS는 응용프로그램 개발자가 같은 API로 다른 시스템들에 사용할 수 있는 JDBC와 유사 하다.
만약 JMS를 준수하여 서비스를 할 경우, JMS API를 통해 공급자가 해당 업체에 메시지를 주고 받을 수 있다.
예를 들어 SonicMQ를 사용하여 메시지를 보내고 IBM WebSphere MQ를 통해 메시지를 처리 할 수 있다.

##Spring Jms 

http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/jms.html


```java
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;

import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.core.JmsTemplate;

public class JmsQueueSender {

    private JmsTemplate jmsTemplate;
    private Queue queue;

    public void setConnectionFactory(ConnectionFactory cf) {
        this.jmsTemplate = new JmsTemplate(cf);
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public void simpleSend() {
        this.jmsTemplate.send(this.queue, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
              return session.createTextMessage("hello queue world");
            }
        });
    }
}

```
스프링이 제고하는 JmsTemplate클래스를 사용할려면 2가지를 설정해야 함 

###JMS 서버와의 연결을 제공하는 ConnectionFactory
###메시지의 목저지 설정 (Queue또는topic)

Javax.jms.ConnectionFactory를 설정해야함 

activeMQ를 메시지 브로커로 사용시 

ActiveMQConnectionFactory 클래스를 사용해서 ConnectionFacotry를 설정한다 


```xml
    <bean id="connectionFactory" class="org.apache.activemq.ActiveMQConnectionFactory">
		<property name="brokerURL">
			<value>tcp://localhost:61616</value>
		</property>
	</bean>

```

메시지 목적지 설정 

```xml

    <bean id="queueDestination" class="org.apache.activemq.command.ActiveMQQueue">
		<constructor-arg value="bbs.article.queue" />
	</bean>
    
    
    <bean id="queueDestination" class="org.apache.activemq.command.ActiveMQTopic">
		<constructor-arg value="bbs.article.topic" />
	</bean>
```

#JmsTemplate이용한 메시지 송수신 

1.JMS이용할때 자원이 처리를 위해 반복되는 코드를 제거 
2.JMS연동 과정에서 발생하는 javax.jms.JMSException 타입및 하위 타입의 예외로 알맞게 제공해 주는 기능도 제공 
3.불필요한 try-catch 제거 

```xml

    <bean id="jmsTemplate" class="org.springframework.jms.core.JmsTemplate">

        <property name="connectionFactory" ref="connectionFactory" />
        <!--ConnectionFactory 설정  -->
        <property name="messageConverter" ref="articleMessageConverter" />
	</bean>
    
    
    <bean class="madvirus.spring.chap14.domain.ArticleMessageSender">
		<property name="jmsTemplate" ref="jmsTemplate" />
		<property name="destination" ref="queueDestination" />
	</bean>
    
```
jms를 이용해서 메시지를 송신하거나 수신할 빈은 JmsTemplate과 큐나 토픽 목적지를 설정한 빈을 전달 받아 사용하면 된다 


## JMS message보내기 

send() 메서드는 메시지를 전송할 목적지를 의미하는 Destination 객체와 
MessageCreator()메서드를 이용해서 전송할 message객체를 생성한다. 


```java 
package madvirus.spring.chap14.domain;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class ArticleMessageSender {

    private JmsTemplate jmsTemplate;
	private Destination destination;

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
		System.out.println("목적지:"+destination);
	}

	public void sendMessage(final Article article) {
		jmsTemplate.send(destination, new MessageCreator() {
        
        //Message Creator 

			@Override
            
			public Message createMessage(Session session) throws JMSException {
				MapMessage mapMessage = session.createMapMessage();
				mapMessage.setString("subject", article.getSubject());
                
				return mapMessage;
                //javax.jms.Message객체를 리턴
			}
		});
//		jmsTemplate.convertAndSend(destination, article);
	}

}

```

JmsTemplate.send()메서드는 createMessage()메서드가 리턴한 message객체를 목적지에 전송한다. 
messageCreator 인터페이스 구현객체의 createMessage()메서드는 메시지를 전송하기 
위한 알맞은 타입의 javax.jms.Message객체를 리턴하면 된다. 


##JMS message 받기 

```java
package madvirus.spring.chap14.domain;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.JmsUtils;

public class ArticleMessageReceiver {

    private JmsTemplate jmsTemplate;
	private Destination destination;

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	public Article receive() {
		MapMessage mapMessage = (MapMessage) jmsTemplate.receive(destination);
        //receive 메서드에 메시지를 전달 받을 목적지를 지정 
		Article article = new Article();
		try {
			article.setSubject(mapMessage.getString("subject"));
			System.out.println(mapMessage.getString("subject"));
			return article;
            //해당 목적지의로 부터 message 를 리턴 
		} catch (JMSException e) {
			throw JmsUtils.convertJmsAccessException(e);
		}
//		Article article = (Article)jmsTemplate.receiveAndConvert(destination);
//		return article;
	}
}

```
JmsTemplate의 기본 목적지 설정 

JmsTemplate 클래스의 send()메서드와 recive()메서드를 호출할때 목적지를 정정하지 않으면 defaultDestnition프로퍼티로 설정한다. 
```xml
	<bean id="articleQueueDestination" class="org.apache.activemq.command.ActiveMQQueue">
		<constructor-arg value="bbs.article.queue" />
	</bean>
	
	<bean id="jmsTemplate" class="org.springframework.jms.core.JmsTemplate"
		p:connectionFactory-ref="connectionFactory" p:destination-ref="articleQueueDestination">
	</bean>
```

위와 같이 설정시 

목적지 지정하지 않고 rend() receive() 가능 

```java
jmsTemplate.send(destination, new MessageCreator() {
	@Override
	public Message createMessage(Session session) throws JMSException {
		MapMessage mapMessage = session.createMapMessage();
		mapMessage.setString("subject", article.getSubject());
	return mapMessage;
}

jmsTemplate.send(new MessageCreator() {
	@Override
	public Message createMessage(Session session) throws JMSException {
		MapMessage mapMessage = session.createMapMessage();
		mapMessage.setString("subject", article.getSubject());
	return mapMessage;
}


MapMessage mapMessage = (MapMessage) jmsTemplate.receive(destination);
MapMessage mapMessage = (MapMessage) jmsTemplate.receive();
```




