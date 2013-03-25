package net.okjsp.spring.star;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class StarServiceTest {
	StarService service;
	
	@Before
	public void setUp() {
		service = new StarServiceImpl();
	}

	@Test
	public void getAverageOfItem() {
		double average = service.getAverageOfItem(0);
		assertTrue(Math.abs(0 - average) < 1e-15);
	}
	
	@Test
	public void add() {
		service.add(0, 5.0, 0);
		double average = service.getAverageOfItem(0);
		assertTrue(Math.abs(5 - average) < 1e-15);
	}
	
	@Test
	public void 별점평균() {
		service.add(0, 5.0, 0);
		service.add(0, 4.0, 1);
		double average = service.getAverageOfItem(0);
		assertTrue(Math.abs(4.5 - average) < 1e-15);
	}
	
	@Test(expected=DuplicateVoteException.class)
	public void 중복투표방지() {
		service.add(0, 5.0, 0);
		service.add(1, 5.0, 0);
		double average = service.getAverageOfItem(0);
		assertTrue(Math.abs(5.0 - average) < 1e-15);
		double average2 = service.getAverageOfItem(0);
		assertTrue(Math.abs(5.0 - average2) < 1e-15);
		
		try {
			service.add(0, 4.0, 0);
			fail("Duplication made");
		} catch (DuplicateVoteException e) {
			throw e;
		}
	}

}
