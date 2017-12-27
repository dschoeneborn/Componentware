package de.schoeneborn.fh.cw.chat.common;

import javax.ejb.Local;

@Local
public interface StatisticManagementLocal extends StatisticManagement {
	/**
	 * Registrierung loggen
	 * @param userName
	 */
	public void logRegister(String userName);
	/**
	 * Login loggen
	 * @param userName
	 */
	public void logLogin(String userName);
	/**
	 * Logout loggen
	 * @param userName
	 */
	public void logLogout(String userName);
	/**
	 * Chatnachricht loggen
	 * @param userName
	 */
	public void logMessage(String userName);
	/**
	 * Neue Common Statistik erstellen
	 */
	public void addNewCommonStatistic();
	/**
	 * Sendet aktuelle Statistic in den Chat
	 */
	public void sendStatisticToChat();
}
