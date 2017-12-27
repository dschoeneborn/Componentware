package de.fh_dortmund.inf.cw.chat.server.entities;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Dennis Schöneborn
 *
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "getAllCommonStatistics", query = "SELECT cs FROM CommonStatistic cs"),
	@NamedQuery(name = "getLastCommonStatistics", query = "SELECT cs FROM CommonStatistic cs "
				+ "WHERE cs.startingDate IN ( SELECT max( s.startingDate ) FROM CommonStatistic s )") })
public class CommonStatistic extends Statistic implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Temporal(TemporalType.TIMESTAMP)
	private Date startingDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Basic(optional = false)
	@Column(nullable = false)
	private Date endDate;

	public CommonStatistic() {
		Calendar tmp = new GregorianCalendar();
		tmp.set(Calendar.MINUTE, 0);
		tmp.set(Calendar.SECOND, 0);
		tmp.set(Calendar.MILLISECOND, 0);

		startingDate = tmp.getTime();

		tmp.set(Calendar.MINUTE, 59);
		tmp.set(Calendar.SECOND, 59);

		endDate = tmp.getTime();
	}

	/**
	 * @return the startingDate
	 */
	public Date getStartingDate() {
		return startingDate;
	}

	/**
	 * @param startingDate
	 *            the startingDate to set
	 */
	public void setStartingDate(Date startingDate) {
		this.startingDate = startingDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
