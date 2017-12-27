package de.schoeneborn.fh.cw.chat.server;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.ObjectMessage;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import de.fh_dortmund.inf.cw.chat.server.entities.User;
import de.fh_dortmund.inf.cw.chat.server.shared.ChatMessage;
import de.fh_dortmund.inf.cw.chat.server.shared.ChatMessageType;
import de.schoeneborn.fh.cw.chat.common.StatisticManagementLocal;
import de.schoeneborn.fh.cw.chat.common.StatisticTimerLocal;
import de.schoeneborn.fh.cw.chat.common.UserManagementLocal;
import de.schoeneborn.fh.cw.chat.common.UserManagementRemote;
import static java.lang.Math.toIntExact;

/**
 * @author Dennis Schöneborn
 * Verwaltungsbean für die Benutzer
 */
@Stateless
public class UserManagementBean implements UserManagementLocal, UserManagementRemote {
	
	@Inject
	private JMSContext jmsContext;
	
	@Resource(lookup = "java:global/jms/ChatMessageTopic")
	private Topic chatMessageTopic;
	
	@PersistenceContext(unitName = "ChatDB")
	private EntityManager entityManager;

	@Resource(name = "algorithm")
	private String algorithm;// = "SHA-1";
	
	@EJB
	private StatisticManagementLocal statistic;
	
	@EJB
	private StatisticTimerLocal statisticTimer;

	@PostConstruct
	private void init() {
		
	}

	@Override
	public void register(String userName, String password) throws Exception {
		User user = new User();
		user.setName(userName);
		user.setPasswordHash(this.generateHash(password, algorithm));
		try {
			entityManager.persist(user);
			entityManager.flush();

			
		} catch (Exception e) {
			System.out.println("sdufhsiodjfisodjfsiodjfsiodfjioJ");
			throw e;
		}
		
		ObjectMessage objectMessage = jmsContext.createObjectMessage();

		ChatMessage chatMessage = new ChatMessage(ChatMessageType.REGISTER, userName, null, new Date());
		objectMessage.setObject(chatMessage);
		objectMessage.setIntProperty("type", ChatMessageType.REGISTER.getValue());
		objectMessage.setStringProperty("username", userName);

		jmsContext.createProducer().send(chatMessageTopic, objectMessage);
		
		statistic.logRegister(userName);
	}

	@Override
	public void changePassword(String userName, String oldPassword, String newPassword) throws Exception {
		if (userName == null) {
			throw new Exception("User ist null");
		}
		
		User user = entityManager.find(User.class, userName);
		
		if (user == null) {
			throw new Exception("User existiert nicht");
		}
		if (!user.getPasswordHash().equals(this.generateHash(oldPassword, this.algorithm))) {
			throw new Exception("Passwort ist nicht richtig");
		}
		
		user.setPasswordHash(this.generateHash(newPassword, algorithm));
		
		entityManager.merge(user);
		//user.put(userName, this.generateHash(newPassword, this.algorithm));
	}

	@Override
	public int getNumberOfRegisteredUsers() {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cq = qb.createQuery(Long.class);
		cq.select(qb.count(cq.from(User.class)));

		return toIntExact(entityManager.createQuery(cq).getSingleResult());
	}

	private String generateHash(String plaintext, String algorithm) {
		String hash;
		try {
			MessageDigest encoder = MessageDigest.getInstance(algorithm);
			// MessageDigest encoder = MessageDigest.getInstance("SHA-1");
			hash = String.format("%040x", new BigInteger(1, encoder.digest(plaintext.getBytes())));
		} catch (NoSuchAlgorithmException e) {
			hash = null;
		}
		return hash;
	}

	@Override
	public void login(String userName, String password) throws Exception {
		ObjectMessage objectMessage;
		ChatMessage chatMessage;
		
		if (userName == null) {
			throw new Exception("User ist null");
		}
		
		User user = entityManager.find(User.class, userName);
		
		if (user == null) {
			throw new Exception("User nicht vorhanden");
		}
		
		if (!user.getPasswordHash().equals(this.generateHash(password, this.algorithm))) {
			throw new Exception("Passwort ist falsch");
		}
		if (user.isOnline()) {
			objectMessage = jmsContext.createObjectMessage();

			chatMessage = new ChatMessage(ChatMessageType.DISCONNECT, userName, null, new Date());
			objectMessage.setObject(chatMessage);
			objectMessage.setIntProperty("type", ChatMessageType.DISCONNECT.getValue());
			objectMessage.setStringProperty("username", userName);

			jmsContext.createProducer().send(chatMessageTopic, objectMessage);
		}
		

		statistic.logLogin(userName);
		statisticTimer.createIntervalTimer();
		
		objectMessage = jmsContext.createObjectMessage();

		chatMessage = new ChatMessage(ChatMessageType.LOGIN, userName, null, new Date());
		objectMessage.setObject(chatMessage);
		objectMessage.setIntProperty("type", ChatMessageType.LOGIN.getValue());
		objectMessage.setStringProperty("username", userName);

		jmsContext.createProducer().send(chatMessageTopic, objectMessage);
		
		user.setOnline(true);
		entityManager.merge(user);
	}

	@Override
	public void logout(String userName) throws Exception {
		if (userName == null) {
			throw new Exception("User ist null");
		}
		
		User user = entityManager.find(User.class, userName);
		
		if (!user.isOnline()) {
			throw new Exception("User ist nicht angemeldet");
		}
		user.setOnline(false);
		entityManager.merge(user);

		statistic.logLogout(userName);
		
		ObjectMessage objectMessage = jmsContext.createObjectMessage();

		ChatMessage chatMessage = new ChatMessage(ChatMessageType.LOGOUT, userName, null, new Date());
		objectMessage.setObject(chatMessage);
		objectMessage.setIntProperty("type", ChatMessageType.LOGOUT.getValue());
		objectMessage.setStringProperty("username", userName);

		jmsContext.createProducer().send(chatMessageTopic, objectMessage);
	}

	@Override
	public String[] getOnlineUsers() {
		TypedQuery<User> tq = entityManager.createNamedQuery("getOnlineUsers", User.class);
		List<User> onlineUsers = tq.getResultList();
		String[] onlineNames = new String[onlineUsers.size()];
		int i=0;
		for(User user : onlineUsers)
		{
			onlineNames[i++] = user.getName();
		}
		return onlineNames;
	}

	@Override
	public int getNumberOfOnlineUsers() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<User> root = cq.from(User.class);
		EntityType<User> user_ = root.getModel();
		
		cq.select(cb.count(root));
		
		cq.where(cb.equal(root.get(user_.getSingularAttribute("online")), true));
		
		return toIntExact(entityManager.createQuery(cq).getSingleResult());
	}

	@Override
	public void delete(String userName, String password) throws Exception {
		if (userName == null) {
			throw new Exception("User ist null");
		}
		
		User user = entityManager.find(User.class, userName);
		
		if (user == null) {
			throw new Exception("User existiert nicht");
		}
		if (!user.getPasswordHash().equals(this.generateHash(password, this.algorithm))) {
			throw new Exception("Passwort ist falsch");
		}
		entityManager.remove(user);
	}

}
