package de.schoeneborn.fh.cw.chat.common;

public interface UserSession {
	public String getUserName();
	public void login(String userName, String password) throws Exception;
	public void logout() throws Exception;
	public void disconnect();
	public void changePassword(String oldPassword, String newPassword) throws Exception;
	public void delete(String password) throws Exception;
}
