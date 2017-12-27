package de.fh_dortmund.inf.cw.chat.server.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;


/**
 * @author Dennis Schöneborn
 * 
 */
@MappedSuperclass
@EntityListeners({SuperEntityListener.class})
public abstract class Statistic extends SuperEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(nullable = false)
	private int logins = 0;
	@Column(nullable = false)
	private int logouts = 0;
	@Column(nullable = false)
	private int messages = 0;
	
	/**
	 * @return the logins
	 */
	public int getLogins() {
		return logins;
	}
	/**
	 * @param logins the logins to set
	 */
	public void setLogins(int logins) {
		this.logins = logins;
	}
	/**
	 * @return the logouts
	 */
	public int getLogouts() {
		return logouts;
	}
	/**
	 * @param logouts the logouts to set
	 */
	public void setLogouts(int logouts) {
		this.logouts = logouts;
	}
	/**
	 * @return the messages
	 */
	public int getMessages() {
		return messages;
	}
	/**
	 * @param messages the messages to set
	 */
	public void setMessages(int messages) {
		this.messages = messages;
	}
	
	/**
	 * @return Logins
	 */
	public int increaseLogins()
	{
		return ++logins;
	}
	
	/**
	 * @return Logouts
	 */
	public int increaseLogouts()
	{
		return ++logouts;
	}
	
	/**
	 * @return Messages
	 */
	public int increaseMessages()
	{
		return ++messages;
	}
}
