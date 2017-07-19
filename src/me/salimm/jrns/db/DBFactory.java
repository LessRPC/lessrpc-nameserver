package me.salimm.jrns.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.allConfig.conf.AbstractConfig;

import me.salimm.jrns.constants.Constants;
import me.salimm.jrns.errors.DatabaseNotSupported;
import me.salimm.jrns.types.DBType;

/**
 * 
 * DBConnections contains a set of static functions to create connections to
 * databases.
 * 
 * @author Salim
 *
 */
public class DBFactory implements Constants {

	/**
	 * 
	 * Create Oracle connection
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static Connection createOracleConnection(String DB_CONN, String USER, String PASS)
			throws ClassNotFoundException, SQLException {
		Class.forName(ORACLE_CONN_DRIVER_CLASSPATH);
		Connection connection = DriverManager.getConnection(DB_CONN, USER, PASS);
		return connection;
	}

	/**
	 * 
	 * 
	 * Create MySQL Connection
	 * 
	 * @param JDBC_DRIVER
	 * @param DB_URL
	 * @param USER
	 * @param PASS
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static Connection createMySQLConnection(String DB_URL, String USER, String PASS)
			throws ClassNotFoundException, SQLException {
		Connection conn = null;
		Class.forName(MYSQL_CONN_DRIVER_CLASSPATH);
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		return conn;
	}

	/**
	 * 
	 * 
	 * Create SQLLite Connection
	 * 
	 * @param JDBC_DRIVER
	 * @param DB_URL
	 * @param USER
	 * @param PASS
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static Connection createSQLLiteConnection(String DB_URL, String USER, String PASS)
			throws ClassNotFoundException, SQLException {
		Connection conn = null;
		Class.forName(SQLLITE_CONN_DRIVER_CLASSPATH);
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		return conn;
	}

	/**
	 * Get DBUtils instance based on database type in conf file
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws DatabaseNotSupported
	 */
	public static DBUtils getDBUtils(AbstractConfig conf)
			throws ClassNotFoundException, SQLException, DatabaseNotSupported {
		if (conf.getValue(CONF_TAG_NAME_DB_TYPE).equals(DBType.MYSQL.name())) {
			return new MySQLUtils();
		} else if (conf.getValue(CONF_TAG_NAME_DB_TYPE).equals(DBType.ORACLE.name())) {
			return new OracleUtils();
		} else if (conf.getValue(CONF_TAG_NAME_DB_TYPE).equals(DBType.SQLLITE.name())) {
			return new SQLLiteDBUtils();
		} else {
			throw new DatabaseNotSupported(CONF_TAG_NAME_DB_TYPE);
		}

	}

	/**
	 * Get specific db connection based on database info in conf file
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws DatabaseNotSupported
	 */
	public static Connection getConnection(DBInfo dbInfo)
			throws ClassNotFoundException, SQLException, DatabaseNotSupported {
		if (dbInfo.getType().equals(DBType.MYSQL)) {
			return DBFactory.createMySQLConnection(dbInfo.getDbUrl(), dbInfo.getDbUser(), dbInfo.getDbPass());
		} else if (dbInfo.getType().equals(DBType.ORACLE)) {
			return DBFactory.createOracleConnection(dbInfo.getDbUrl(), dbInfo.getDbUser(), dbInfo.getDbPass());
		} else if (dbInfo.getType().equals(DBType.SQLLITE)) {
			return DBFactory.createSQLLiteConnection(dbInfo.getDbUrl(), dbInfo.getDbUser(), dbInfo.getDbPass());
		}
		return null;

	}

	public static DBInfo getDBInfo(AbstractConfig conf) {
		return new DBInfo(conf.getValue(CONF_TAG_NAME_DB_URL), conf.getValue(CONF_TAG_NAME_DB_DBNAME),
				conf.getValue(CONF_TAG_NAME_DB_USER), conf.getValue(CONF_TAG_NAME_DB_PASS),
				DBType.fromString(conf.getValue(CONF_TAG_NAME_DB_TYPE)));
	}

}
