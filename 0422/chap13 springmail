스프링을 이용한 메일 발송 
==
<b>
MailSender 와 JavaMailSender를 이용한 메일 발송<b> 
MimeMessageHelper를 이용한 파일 첨부 

MailSender 인터페이스를 제공 

```java
public interface MailSender {

    /**
     * Send the given simple mail message.
     * @param simpleMessage message to send
     * @throws MailException in case of message, authentication, or send errors
     */
    public void send(SimpleMailMessage simpleMessage) throws MailException;

    /**
     * Send the given array of simple mail messages in batch.
     * @param simpleMessages messages to send
     * @throws MailException in case of message, authentication, or send errors
     */
    public void send(SimpleMailMessage[] simpleMessages) throws MailException;

}
