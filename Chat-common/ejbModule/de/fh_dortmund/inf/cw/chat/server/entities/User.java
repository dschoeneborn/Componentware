package de.fh_dortmund.inf.cw.chat.server.entities;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * @author Dennis Schöneborn
 *
 */
@Entity
@Table(name = "UserObject")
@NamedQuery(name = "getOnlineUsers",
			query = "SELECT u FROM User u WHERE u.online = true")
@EntityListeners({SuperEntityListener.class})
public class User extends SuperEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	private String name;
	@Basic(optional = false)
	@Column(nullable = false)
	private String passwordHash;
	@Column(nullable = false)
	private boolean online;
	
	public User()
	{
		super();
	}
	
	public User(String name, String passwordHash)
	{
		this.name = name;
		this.passwordHash=passwordHash;
		this.setOnline(false);
	}
	
	/**
	 * @return the passwordHash
	 */
	public String getPasswordHash() {
		return passwordHash;
	}
	/**
	 * @param passwordHash the passwordHash to set
	 */
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the online
	 */
	public boolean isOnline() {
		return online;
	}

	/**
	 * @param online the online to set
	 */
	public void setOnline(boolean online) {
		this.online = online;
	}
}
