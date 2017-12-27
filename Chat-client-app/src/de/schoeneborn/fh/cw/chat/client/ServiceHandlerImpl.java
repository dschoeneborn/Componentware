package de.schoeneborn.fh.cw.chat.client;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import de.fh_dortmund.inf.cw.chat.client.shared.ChatMessageHandler;
import de.fh_dortmund.inf.cw.chat.client.shared.ServiceHandler;
import de.fh_dortmund.inf.cw.chat.client.shared.StatisticHandler;
import de.fh_dortmund.inf.cw.chat.client.shared.UserSessionHandler;
import de.fh_dortmund.inf.cw.chat.server.entities.CommonStatistic;
import de.fh_dortmund.inf.cw.chat.server.entities.UserStatistic;
import de.fh_dortmund.inf.cw.chat.server.shared.ChatMessage;
import de.fh_dortmund.inf.cw.chat.server.shared.ChatMessageType;
import de.schoeneborn.fh.cw.chat.common.StatisticManagementRemote;
import de.schoeneborn.fh.cw.chat.common.UserManagementRemote;
import de.schoeneborn.fh.cw.chat.common.UserSessionRemote;

public class ServiceHandlerImpl extends ServiceHandler
		implements UserSessionHandler, ChatMessageHandler, MessageListener, StatisticHandler {

	private static ServiceHandlerImpl instance;

	private Context ctx;
	private UserSessionRemote userSession;
	private UserManagementRemote userManagement;
	private StatisticManagementRemote statistic;

	private JMSContext jmsContext;
	private Topic chatMessageTopic;
	private Queue chatMessageQueue;

	private JMSConsumer consumer;

	private ServiceHandlerImpl() {
		try {
			ctx = new InitialContext();

			userSession = (UserSessionRemote) ctx.lookup(
					"java:global/Chat-ear/Chat-ejb/UserSessionBean!de.schoeneborn.fh.cw.chat.common.UserSessionRemote");
			userManagement = (UserManagementRemote) ctx.lookup(
					"java:global/Chat-ear/Chat-ejb/UserManagementBean!de.schoeneborn.fh.cw.chat.common.UserManagementRemote");
			statistic = (StatisticManagementRemote) ctx.lookup(
					"java:global/Chat-ear/Chat-ejb/StatisticManagementBean!de.schoeneborn.fh.cw.chat.common.StatisticManagementRemote");

			this.initJMSConntections();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public static ServiceHandlerImpl getInstance() {
		if (instance == null) {
			instance = new ServiceHandlerImpl();
		}

		return instance;
	}

	private void initJMSConntections() {
		try {
			ConnectionFactory connectionFactory = (ConnectionFactory) ctx
					.lookup("java:comp/DefaultJMSConnectionFactory");
			jmsContext = connectionFactory.createContext();

			chatMessageTopic = (Topic) ctx.lookup("java:global/jms/ChatMessageTopic");
			chatMessageQueue = (Queue) ctx.lookup("java:global/jms/ChatClientToServerQueue");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void changePassword(String arg0, String arg1) throws Exception {
		userSession.changePassword(arg0, arg1);
	}

	@Override
	public void delete(String arg0) throws Exception {
		userSession.delete(arg0);
	}

	@Override
	public void disconnect() {
		userSession.disconnect();
	}

	@Override
	public int getNumberOfOnlineUsers() {
		return userManagement.getNumberOfOnlineUsers();
	}

	@Override
	public int getNumberOfRegisteredUsers() {
		return userManagement.getNumberOfRegisteredUsers();
	}

	@Override
	public List<String> getOnlineUsers() {
		return new LinkedList<String>(Arrays.asList(userManagement.getOnlineUsers()));
	}

	@Override
	public String getUserName() {
		return userSession.getUserName();
	}

	@Override
	public void login(String arg0, String arg1) throws Exception {
		/*
		 * try { userSession.login(arg0, arg1); } catch (Exception e) {
		 * if(e.getMessage().equals("User bereits angemeldet!")) {
		 * System.out.println("User bereits angemeldet!"); try { ObjectMessage
		 * objectMessage = jmsContext.createObjectMessage();
		 * 
		 * ChatMessage chatMessage = new ChatMessage(ChatMessageType.DISCONNECT,
		 * getUserName(), null, new Date()); objectMessage.setObject(chatMessage);
		 * 
		 * jmsContext.createProducer().send(chatMessageQueue, objectMessage); } catch
		 * (JMSException ex) { ex.printStackTrace(); } } else { throw e; }
		 * 
		 * }
		 * 
		 * try { ObjectMessage objectMessage = jmsContext.createObjectMessage();
		 * 
		 * ChatMessage chatMessage = new ChatMessage(ChatMessageType.LOGIN,
		 * getUserName(), null, new Date()); objectMessage.setObject(chatMessage);
		 * 
		 * jmsContext.createProducer().send(chatMessageQueue, objectMessage); } catch
		 * (JMSException e) { e.printStackTrace(); }
		 */
		userSession.login(arg0, arg1);

		this.consumer = jmsContext.createConsumer(chatMessageTopic,
				"(type <> " + ChatMessageType.DISCONNECT.getValue() + ") or (username = '" + arg0 + "')");
		consumer.setMessageListener(this);
		// this.notifyObservers(this);
	}

	@Override
	public void logout() throws Exception {
		userSession.logout();

	}

	@Override
	public void register(String arg0, String arg1) throws Exception {
		userManagement.register(arg0, arg1);
	}

	@Override
	public void sendChatMessage(String message) {
		try {
			ObjectMessage objectMessage = jmsContext.createObjectMessage();

			ChatMessage chatMessage = new ChatMessage(ChatMessageType.TEXT, getUserName(), message, new Date());
			objectMessage.setObject(chatMessage);

			jmsContext.createProducer().send(chatMessageQueue, objectMessage);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessage(Message message) {
		try {
			if (message.getJMSDestination().equals(chatMessageTopic)) {
				ObjectMessage objectMessage = (ObjectMessage) message;
				ChatMessage chatMessage = (ChatMessage) objectMessage.getObject();

				setChanged();
				notifyObservers(chatMessage);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<CommonStatistic> getStatistics() {
		return statistic.getCommonStatistic();
	}

	@Override
	public UserStatistic getUserStatistic() {
		return statistic.getUserStaticstic(this.getUserName());
	}

}
