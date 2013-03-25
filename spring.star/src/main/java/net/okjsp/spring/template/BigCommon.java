package net.okjsp.spring.template;

public class BigCommon {
	Interface interface1;
	public BigCommon(Interface interface1) {
		this.interface1 = interface1;
	}
	public void amthod(){
		Logger.doLogBefore();
		interface1.execute();
		Logger.doLogAfter();
	}
}
