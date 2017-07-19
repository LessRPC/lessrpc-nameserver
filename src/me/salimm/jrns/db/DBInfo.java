package me.salimm.jrns.db;

import me.salimm.jrns.types.DBType;

/**
 * 
 * abstract class for required information to create database connection
 * 
 * @author salimm
 *
 */
public class DBInfo {

	/**
	 * url to database
	 */
	private String dbUrl;
	/**
	 * name of  the database to connect to
	 */
	private String dbName;
	/**
	 * username  for authentication
	 */
	private String dbUser;
	/**
	 * password for authentication
	 */
	private String dbPass;
	
	private DBType type;

	public DBInfo(String dbUrl, String dbName, String dbUser, String dbPass, DBType type) {
		this.setType(type);
		this.setDbUser(dbUser);
		this.setDbPass(dbPass);
		this.setDbName(dbName);
		this.setDbUrl(dbUrl);

	}

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPass() {
		return dbPass;
	}

	public void setDbPass(String dbPass) {
		this.dbPass = dbPass;
	}

	@Override
	public String toString() {
		return " dbName: " + dbName + " dbUser: " + dbUser + " dbURL: " + dbUrl + " dbPass:" + dbPass;
	}

	public DBType getType() {
		return type;
	}

	public void setType(DBType type) {
		this.type = type;
	}
}
