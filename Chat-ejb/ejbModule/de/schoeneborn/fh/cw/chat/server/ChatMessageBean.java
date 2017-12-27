package de.schoeneborn.fh.cw.chat.server;

import java.util.LinkedList;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Topic;

import de.fh_dortmund.inf.cw.chat.server.shared.ChatMessage;
import de.fh_dortmund.inf.cw.chat.server.shared.ChatMessageType;
import de.schoeneborn.fh.cw.chat.common.StatisticManagementLocal;

/**
 * @author Dennis Schöneborn Empfängt Chatnachrichten von Clients, filtert diese
 *         und Sendet sie an alle Clients weiter
 */
@MessageDriven(mappedName = "java:global/jms/ChatClientToServerQueue", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") })
public class ChatMessageBean implements MessageListener {

	private LinkedList<String> schimpfwortListe = new LinkedList<String>();

	@Inject
	private JMSContext jmsContext;
	@Resource(lookup = "java:global/jms/ChatMessageTopic")
	private Topic chatMessageTopic;

	@EJB
	private StatisticManagementLocal statistics;

	public ChatMessageBean() {
		schimpfwortListe.add("fuck");
	}

	@Override
	public void onMessage(Message message) {
		ChatMessage newChatMessage;
		ObjectMessage objectMessage = (ObjectMessage) message;
		ChatMessage chatMessage;
		try {
			chatMessage = (ChatMessage) objectMessage.getObject();
			if (chatMessage.getType().equals(ChatMessageType.TEXT)) {
				String tempText;
				tempText = chatMessage.getText();
				// Schnimpfwortfilter
				for (String word : schimpfwortListe) {

					tempText = tempText.replaceAll(word, "****");

				}
				// gefilterte Nachricht an alle Clients senden
				newChatMessage = new ChatMessage(chatMessage.getType(), chatMessage.getSender(), tempText,
						chatMessage.getDate());
				objectMessage = jmsContext.createObjectMessage();
				objectMessage.setObject(newChatMessage);

				statistics.logMessage(chatMessage.getSender());
			} else {
				newChatMessage = new ChatMessage(chatMessage.getType(), chatMessage.getSender(), chatMessage.getText(),
						chatMessage.getDate());
				objectMessage = jmsContext.createObjectMessage();
				objectMessage.setObject(newChatMessage);
			}
			objectMessage.setIntProperty("type", chatMessage.getType().getValue());
			objectMessage.setStringProperty("username", chatMessage.getSender());
			jmsContext.createProducer().send(chatMessageTopic, objectMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
