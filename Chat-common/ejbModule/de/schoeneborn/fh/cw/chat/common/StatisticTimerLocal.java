package de.schoeneborn.fh.cw.chat.common;

import javax.ejb.Local;

@Local
public interface StatisticTimerLocal extends StatisticTimer {
	/**
	 * Erstellt einen IntervalTimer zur Statistikausgabe
	 */
	public void createIntervalTimer();

	/**
	 * Sendet eine Statistik an den Chat
	 */
	void sendStatistic();
}
