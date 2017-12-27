package de.schoeneborn.fh.cw.chat.server;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import de.schoeneborn.fh.cw.chat.common.UserManagementLocal;

/**
 * @author Dennis Schöneborn
 * Bean, falls irgendetwas bei Serverstart gemacht werden soll
 */
@Singleton
@Startup
public class StartupBean {
	@EJB
	private UserManagementLocal userManagement;
	
	@PostConstruct
	private void init()
	{
		try {
			//userManagement.register("test", "abc");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
