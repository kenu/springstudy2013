package net.okjsp.spring.star;

import java.util.HashMap;
import java.util.Map;

public class StarServiceImpl implements StarService {

	private Map<Integer, Double> dao;
	private Map<Integer, Integer> countDao;
	private Map<String, Long> historyDao;

	public StarServiceImpl() {
		dao = new HashMap<Integer, Double>();
		countDao = new HashMap<Integer, Integer>();
		historyDao = new HashMap<String, Long>();
	}

	public double getAverageOfItem(int i) {
		Double sum = dao.get(i);
		Integer count = countDao.get(i);
		if (sum == null) {
			return 0;
		}
		return sum / count;
	}

	public void add(int i, double d, int userid) {
		Long time = historyDao.get(i + "_" + userid);
		if (time != null) {
			throw new DuplicateVoteException();
		} else {
			historyDao.put(i + "_" + userid, System.currentTimeMillis());
		}
		double sum = (dao.get(i) != null) ? dao.get(i) : 0;
		sum = sum + d;
		dao.put(i, sum);
		int count = (countDao.get(i) != null) ? countDao.get(i) + 1 : 1;
		countDao.put(i, count);
	}

}
