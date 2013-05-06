package net.okjsp.spring.star;

import java.util.Map;

public interface StarDao {
	public int add(Map<String, Object> map);
	public double getAverageOfItem(int contents);
}
