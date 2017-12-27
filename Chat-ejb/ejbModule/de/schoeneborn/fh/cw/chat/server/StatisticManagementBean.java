package de.schoeneborn.fh.cw.chat.server;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import de.fh_dortmund.inf.cw.chat.server.entities.CommonStatistic;
import de.fh_dortmund.inf.cw.chat.server.entities.UserStatistic;
import de.fh_dortmund.inf.cw.chat.server.shared.ChatMessage;
import de.fh_dortmund.inf.cw.chat.server.shared.ChatMessageType;
import de.schoeneborn.fh.cw.chat.common.StatisticManagementLocal;
import de.schoeneborn.fh.cw.chat.common.StatisticManagementRemote;
import de.schoeneborn.fh.cw.chat.common.UserManagementLocal;

/**
 * @author Dennis Schöneborn
 * Loggt alle Statistiken
 */
@Stateless
public class StatisticManagementBean implements StatisticManagementLocal, StatisticManagementRemote {
	@EJB
	private UserManagementLocal userManagement;
	
	@PersistenceContext(unitName = "ChatDB")
	private EntityManager entityManager;
	
	@Inject
	private JMSContext jmsContext;
	
	@Resource(lookup = "java:global/jms/ChatMessageTopic")
	private Topic chatMessageTopic;

	@Override
	public UserStatistic getUserStaticstic(String userName) {
		return entityManager.find(UserStatistic.class, userName);
	}

	@Override
	public List<CommonStatistic> getCommonStatistic() {
		TypedQuery<CommonStatistic> tq = entityManager.createNamedQuery("getAllCommonStatistics", CommonStatistic.class);
		List<CommonStatistic> commonStatisticList = tq.getResultList();
		
		return commonStatisticList;
	}

	@Override
	public void logRegister(String userName) {
		UserStatistic us = new UserStatistic(userName);
		entityManager.persist(us);
		entityManager.flush();
	}

	@Override
	public void logLogin(String userName) {
		UserStatistic us = entityManager.find(UserStatistic.class, userName);
		entityManager.lock(us, LockModeType.PESSIMISTIC_READ);
		us.setLastLogin(new Date());
		us.increaseLogins();
		entityManager.merge(us);
		
		TypedQuery<CommonStatistic> tq = entityManager.createNamedQuery("getLastCommonStatistics", CommonStatistic.class);
		CommonStatistic cs = tq.getSingleResult();
		entityManager.lock(cs, LockModeType.PESSIMISTIC_READ);
		cs.increaseLogins();
		entityManager.merge(cs);
	}

	@Override
	public void logLogout(String userName) {
		UserStatistic us = entityManager.find(UserStatistic.class, userName);
		entityManager.lock(us, LockModeType.PESSIMISTIC_READ);
		us.increaseLogouts();
		entityManager.merge(us);
		
		TypedQuery<CommonStatistic> tq = entityManager.createNamedQuery("getLastCommonStatistics", CommonStatistic.class);
		CommonStatistic cs = tq.getSingleResult();
		entityManager.lock(cs, LockModeType.PESSIMISTIC_READ);
		cs.increaseLogouts();
		entityManager.merge(cs);
	}
	
	@Override
	public void logMessage(String userName) {
		UserStatistic us = entityManager.find(UserStatistic.class, userName);
		entityManager.lock(us, LockModeType.PESSIMISTIC_READ);
		us.increaseMessages();
		entityManager.merge(us);
				
		TypedQuery<CommonStatistic> tq = entityManager.createNamedQuery("getLastCommonStatistics", CommonStatistic.class);
		CommonStatistic cs = tq.getSingleResult();
		entityManager.lock(cs, LockModeType.PESSIMISTIC_READ);
		cs.increaseMessages();
		entityManager.merge(cs);
	}

	@Override
	public void addNewCommonStatistic()
	{
		CommonStatistic cs = new CommonStatistic();
		try {
		entityManager.persist(cs);
		entityManager.flush();
		}
		catch(Exception e)
		{ 
			e.printStackTrace();
		}
	}

	@Override
	public void sendStatisticToChat() {
		TypedQuery<CommonStatistic> tq = entityManager.createNamedQuery("getLastCommonStatistics", CommonStatistic.class);
		CommonStatistic stat = tq.getSingleResult();
		
		int minute = LocalDateTime.now().getMinute();
		String timeFrame = minute < 15 ? "Stunde" : "halben Stunde";
		String text = String.format(
				"Statistik der letzten %s\nAnzahl der Anmeldungen: %d\nAnzahl der Abmeldungen: %d\nAnzahl der geschriebenen Nachrichten: %d",
				timeFrame, stat.getLogins(), stat.getLogouts(), stat.getMessages());


		ChatMessage chatMessage = new ChatMessage(ChatMessageType.STATISTIC, "Statistik", text, new Date());

		ObjectMessage objectMessage = jmsContext.createObjectMessage(chatMessage);
		try {
			objectMessage.setIntProperty("type", ChatMessageType.STATISTIC.getValue());
			objectMessage.setStringProperty("username", "Statistik");
		} catch (JMSException e) {
			e.printStackTrace();
		}

		jmsContext.createProducer().send(chatMessageTopic, objectMessage);
	}
	
}
