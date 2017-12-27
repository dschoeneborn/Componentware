package de.schoeneborn.fh.cw.chat.test;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.schoeneborn.fh.cw.chat.client.ServiceHandlerImpl;

public class UserSessionTest {
	private static ServiceHandlerImpl handler;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		handler = ServiceHandlerImpl.getInstance();

		handler.register("1", "123");
		handler.register("2", "123");
		handler.register("3", "123");
		handler.register("4", "123");
	}

	@AfterClass
	public static void remove() throws Exception {
		handler.login("1", "123");
		handler.delete("123");
		handler.login("2", "123");
		handler.delete("123");
		handler.login("3", "123");
		handler.delete("123");
		handler.login("4", "123");
		handler.delete("123");
	}

	@Test
	public void test_count_registered_user() {

		assertEquals(4, handler.getNumberOfRegisteredUsers());
	}

	@Test
	public void test_register_user() {
		int before=0;
		try {
			before = handler.getNumberOfRegisteredUsers();
			handler.register("5", "123");
			assertEquals(before+1, handler.getNumberOfRegisteredUsers());
		} catch (Exception e) {
			fail();
			e.printStackTrace();
		}
	}

	@Test
	public void test_delete_registered_user() {
		int count2 = 0;
		try {
			handler.register("delete", "del");

			count2 = handler.getNumberOfRegisteredUsers();
			handler.login("delete", "del");
			handler.delete("del");
			assertEquals(count2 - 1, handler.getNumberOfRegisteredUsers());
		} catch (Exception e) {
			fail();
			e.printStackTrace();
		}
	}

	@Test
	public void test_change_password() {
		String user = "changePW";
		String pw = "test";
		String newPW = "nichtTest";

		try {
			handler.register(user, pw);
			handler.changePassword(user, newPW);
			handler.login(user, pw);
			fail("Passwort sollte falsch sein, ist es aber scheinbar nicht.");
		} catch (Exception e) {
		}

	}

	@Test
	public void test_login_logout_user() {
		String user = "loginlogout";
		String pw = "letMeIn";
		try {
			handler.register(user, pw);
			try {
				handler.login(user, pw);
				try{
					handler.logout();
					try{
						handler.logout();
					} catch(Exception e)
					{
						assertEquals("User ist null", e.getMessage());
					}
				} catch(Exception e)
				{
					fail(e.getMessage());
				}
			} catch (Exception e) {
				fail(e.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
