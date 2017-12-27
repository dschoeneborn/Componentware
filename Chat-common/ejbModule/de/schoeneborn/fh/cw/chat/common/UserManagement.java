package de.schoeneborn.fh.cw.chat.common;

public interface UserManagement {
	public void register(String userName, String password) throws Exception;
	public void changePassword(String userName, String oldPassword, String newPassword) throws Exception;
	public int getNumberOfRegisteredUsers();
	public void login(String userName, String password) throws Exception;
	public void logout(String userName) throws Exception;
	public String[] getOnlineUsers();
	public int getNumberOfOnlineUsers();
	public void delete(String userName, String password) throws Exception;
}
