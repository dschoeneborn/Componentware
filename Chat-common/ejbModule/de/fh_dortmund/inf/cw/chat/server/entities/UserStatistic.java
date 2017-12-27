package de.fh_dortmund.inf.cw.chat.server.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Dennis Schöneborn
 * Benutzerstatistiken
 */
@Entity
public class UserStatistic extends Statistic implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String userName;
	@Temporal(TemporalType.DATE)
	private Date lastLogin;
	
	public UserStatistic() {
		super();
	}

	public UserStatistic(String userName)
	{
		this();
		this.setUserName(userName);
	}
	/**
	 * @return the lastLogin
	 */
	public Date getLastLogin() {
		return lastLogin;
	}

	/**
	 * @param lastLogin
	 *            the lastLogin to set
	 */
	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
}
