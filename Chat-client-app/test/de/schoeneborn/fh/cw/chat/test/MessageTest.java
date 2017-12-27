package de.schoeneborn.fh.cw.chat.test;

import org.junit.BeforeClass;
import org.junit.Test;

import de.schoeneborn.fh.cw.chat.client.ServiceHandlerImpl;

public class MessageTest {
	
	private static ServiceHandlerImpl handler;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		handler = ServiceHandlerImpl.getInstance();
	}

	@Test
	public void chat_test() throws Exception {
			handler.register("chat_test", "chat_test");
			handler.login("chat_test", "chat_test");
			handler.sendChatMessage("Dies ist ein Test");
	}

}
