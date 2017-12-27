package de.fh_dortmund.inf.cw.chat.server.entities;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * @author Dennis Schöneborn
 * Entity Listener
 */
public class SuperEntityListener {
	@PrePersist
	public void newEntity(SuperEntity e) {
		System.out.println("Entity gespeichert: " + e.getClass().getName());
		
		Calendar tmp = new GregorianCalendar();
		System.out.println(tmp.getTime());
		e.setCreatedAt(tmp.getTime());
		e.setUpdatedAt(tmp.getTime());
	}

	@PreUpdate
	public void updateEntity(SuperEntity e) {
		System.out.println("Entity aktualisiert: " + e.getClass().getName());
		
		Calendar tmp = new GregorianCalendar();
		System.out.println(tmp.getTime());
		e.setUpdatedAt(tmp.getTime());
	}
}
