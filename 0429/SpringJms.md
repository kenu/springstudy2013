
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


