package org.mouji.ns.core.constants;

public interface Constants {

	public static final String CONF_TAG_NAME_DB_TYPE = "CONF.DBINFO.DB_TYPE";
	public static final String CONF_TAG_NAME_DB_USER = "CONF.DBINFO.USER";
	public static final String CONF_TAG_NAME_DB_PASS = "CONF.DBINFO.PASSWORD";
	public static final String CONF_TAG_NAME_DB_URL = "CONF.DBINFO.URL";
	public static final String CONF_TAG_NAME_DB_PORT = "CONF.DBINFO.PORT";
	public static final String CONF_TAG_NAME_DB_DBNAME = "CONF.DBINFO.NAME";

	public static final String ORACLE_CONN_DRIVER_CLASSPATH = "oracle.jdbc.driver.OracleDriver";
	public static final String MYSQL_CONN_DRIVER_CLASSPATH = "com.mysql.jdbc.Driver";
	public static final String SQLLITE_CONN_DRIVER_CLASSPATH = "org.sqlite.JDBC";

	public static final String CONF_TAG_NAME_VIPE_API_PORT = "CONF.RPC.PORT";

	public static final int TYPE_SERVICE_IO_IN = 0;
	public static final int TYPE_SERVICE_IO_OUT = 1;

	public static final String DB_SQL_TABLE_NAME_SERVICE_PROVIDER = "SERVICE_PROVIDER";
	public static final String DB_SQL_TABLE_NAME_SERVICE_PROVIDER_SUPPORT = "SERVICE_PROVIDER_SUPPORT";
	public static final String DB_SQL_TABLE_NAME_SERVICE = "SERVICE";

	public static final String DB_SQL_TABLE_COLUMN_NAME_URL = "URL";
	public static final String DB_SQL_TABLE_COLUMN_NAME_SUPPORT_ID = "SUPPORT_ID";
	public static final String DB_SQL_TABLE_COLUMN_NAME_FORMAT_NAME = "FORMAT_NAME";
	public static final String DB_SQL_TABLE_COLUMN_NAME_FORMAT_VERSION = "FORMAT_VERSION";
	public static final String DB_SQL_TABLE_COLUMN_NAME_PORT = "PORT";
	public static final String DB_SQL_TABLE_COLUMN_NAME_NAME = "NAME";
	public static final String DB_SQL_TABLE_COLUMN_NAME_SERVICE_ID = "SERVICE_ID";
	public static final String DB_SQL_TABLE_COLUMN_NAME_SERVICE_PROVIDER_ID = "SERVICE_PROVIDER_ID";
	public static final String DB_SQL_TABLE_COLUMN_NAME_ENV_LANG = "ENV_LANG";
	public static final String DB_SQL_TABLE_COLUMN_NAME_ENV_OS = "ENV_OS";
	public static final String DB_SQL_TABLE_COLUMN_NAME_ENV_COMPILER = "ENV_COMPILER";

}
