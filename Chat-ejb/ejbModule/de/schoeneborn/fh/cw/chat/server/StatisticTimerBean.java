package de.schoeneborn.fh.cw.chat.server;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

import de.schoeneborn.fh.cw.chat.common.StatisticManagementLocal;
import de.schoeneborn.fh.cw.chat.common.StatisticTimerLocal;
import de.schoeneborn.fh.cw.chat.common.StatisticTimerRemote;

/**
 * @author Dennis Schöneborn Timer Bean um jede Stunde eine Common Statistic
 *         anulegen
 */
@Singleton
@Startup
public class StatisticTimerBean implements StatisticTimerLocal, StatisticTimerRemote {
	private final String STATISTIC_TIMER = "STATISTIC_TIMER";

	@Resource
	private TimerService timerService;

	@EJB
	StatisticManagementLocal statistic;

	@PostConstruct
	private void init() {
		statistic.addNewCommonStatistic();
	}

	@Schedule(info = STATISTIC_TIMER, persistent = false, hour = "*/1")
	public void timeout(Timer timer) {
		System.out.println("Timer has been expired");
		sendStatistic();

		statistic.addNewCommonStatistic();
	}

	@Override
	public void createIntervalTimer() {
		String name = "chat_statistic";

		for (Timer timer : timerService.getTimers()) {
			if (name.equals(timer.getInfo()))
				return;
		}

		TimerConfig config = new TimerConfig();
		config.setPersistent(false);
		config.setInfo(name);

		LocalDateTime firstStart = LocalDateTime.now().withMinute(30);
		if (firstStart.isBefore(LocalDateTime.now()))
			firstStart = firstStart.plusHours(1);

		//1000*60*60 = jede Stunde
		timerService.createIntervalTimer(Date.from(firstStart.atZone(ZoneId.systemDefault()).toInstant()),
				1000 * 60 * 60, config);
		System.out.println("30min Timer erstellt");
		// sendStatistic();
	}

	@Timeout
	@Override
	public void sendStatistic() {
		statistic.sendStatisticToChat();
	}

}
