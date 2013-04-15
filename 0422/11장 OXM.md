#11. OXM (Object XML Mapping)

##차례
1. OXM 모듈
2. JAXB2 설정

##스프링 - oxm모듈 제공
##장점
- 빈 팩토리 클래스를 이용해서 손쉽게 설정가능
- 약간의 노력( 설정 파일 변경 및 일부 코드 변경 ) 만으로 구현기술 변경 가능 ( Marshaller, Unmarshaller 인터페이스 이용 )
- 구현 기술이 변경되더라도 예외 처리를 변경할 필요 없음 ( 구현 기술에 상관없이 XmlMappingException 발생)

###Marshaller 인터페이스
- 자바 객체를 XML 객체로 변환해주는 기능을 정의한 인터페이스
- marshal() 메서드 - javax.xml.transform.Result객체로 변환

###Unmarshaller
- XML을 자바 객체로 변환해주는 기능을 정의한 인터페이스
- unmarshal() 메서드 - 자바 객체 리턴


###Marshaller와 Unmarshaller 사용
- 구현 기술에 맞는 Marshaller / Unmarshaller 구현객체를 설정파일에 기록
- 각 객체를 이용해서 Object와 XML 사이의 매핑처리

###OXM 구현기술
- JAXB 2, XStream, JiBX, XMLBeans 등

예외 클래스 계층도 (P 646 참고)


##JAXB 2를 이용한 OXM
- 실습파일과 교재 참고