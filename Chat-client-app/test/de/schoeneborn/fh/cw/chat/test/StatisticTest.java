package de.schoeneborn.fh.cw.chat.test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import de.fh_dortmund.inf.cw.chat.server.entities.UserStatistic;
import de.schoeneborn.fh.cw.chat.client.ServiceHandlerImpl;

public class StatisticTest {
	
	private static ServiceHandlerImpl handler;
	private static String user = "statisticUser";
	private static String pw = "statisticPW";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		handler = ServiceHandlerImpl.getInstance();
		
		handler.register(user, pw);
		handler.login(user, pw);
	}

	@Test
	public void test_user_statistic() {
		UserStatistic statistic = handler.getUserStatistic();
		
		assertEquals(1, statistic.getLogins());
		assertEquals(0, statistic.getLogouts());
		try {
			handler.logout();
			handler.login(user, pw);
			statistic = handler.getUserStatistic();
			assertEquals(2, statistic.getLogins());
			assertEquals(1, statistic.getLogouts());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
