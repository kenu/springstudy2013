package net.okjsp.spring.star;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

public class StarServiceImpl implements StarService {
	private Log log = LogFactory.getLog(this.getClass());
	
	@Autowired
	StarDao starDao;
	
	
	public double getAverageOfItem(int i) {
		return starDao.getAverageOfItem(i);
	}

	public double add(int i, double d, int userid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userid", userid);
		map.put("contents", i);
		map.put("point", d);
		try{
			starDao.add(map);
		}catch (DuplicateKeyException e){
			throw new DuplicateVoteException();
		}
		return starDao.getAverageOfItem(i);
	}

}
