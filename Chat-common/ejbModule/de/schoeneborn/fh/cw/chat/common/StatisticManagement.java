package de.schoeneborn.fh.cw.chat.common;

import java.util.List;

import de.fh_dortmund.inf.cw.chat.server.entities.CommonStatistic;
import de.fh_dortmund.inf.cw.chat.server.entities.UserStatistic;

public interface StatisticManagement {
	public UserStatistic getUserStaticstic(String userName);
	public List<CommonStatistic> getCommonStatistic();
}
