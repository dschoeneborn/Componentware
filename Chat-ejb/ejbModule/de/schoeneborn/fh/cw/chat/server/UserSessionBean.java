package de.schoeneborn.fh.cw.chat.server;

import javax.ejb.EJB;
import javax.ejb.Remove;
import javax.ejb.Stateful;

import de.schoeneborn.fh.cw.chat.common.UserManagementLocal;
import de.schoeneborn.fh.cw.chat.common.UserSessionLocal;
import de.schoeneborn.fh.cw.chat.common.UserSessionRemote;

/**
 * @author Dennis Schöneborn
 * Verwaltet eine Session eines Benutzers
 */
@Stateful
public class UserSessionBean implements UserSessionLocal, UserSessionRemote {
	
	private String userName;
	@EJB
	private UserManagementLocal userManagement;
	
	@Override
	public String getUserName() {
		return this.userName;
	}

	@Override
	public void login(String userName, String password) throws Exception {
			userManagement.login(userName, password);
			this.userName = userName;

	}

	@Override
	public void logout() throws Exception{
			userManagement.logout(this.userName);
			this.userName = null;
	}
	
	@Remove
	@Override
	public void disconnect(){
		try {
			this.logout();
		} catch (Exception e) {
		}
	}

	@Override
	public void changePassword(String oldPassword, String newPassword) throws Exception{
			userManagement.changePassword(userName, oldPassword, newPassword);
	}
	
	@Override
	public void delete(String password) throws Exception{
			userManagement.delete(userName, password);
	}



}
