package org.mouji.ns.core.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLUtils extends SQLBasedUtils {

	@Override
	public boolean createServiceTable(Connection conn) {
		String sql = "CREATE TABLE IF NOT EXISTS " + DB_SQL_TABLE_NAME_SERVICE + " ("
				+ DB_SQL_TABLE_COLUMN_NAME_SERVICE_ID + " int(11) unsigned NOT NULL," + DB_SQL_TABLE_COLUMN_NAME_NAME
				+ " varchar(150) NOT NULL DEFAULT '', PRIMARY KEY (`" + DB_SQL_TABLE_COLUMN_NAME_SERVICE_ID
				+ "`), UNIQUE KEY `IDX_" + DB_SQL_TABLE_COLUMN_NAME_NAME + "` (" + DB_SQL_TABLE_COLUMN_NAME_NAME
				+ ") ) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			return false;
		}

		return true;
	}

	@Override
	public boolean createServiceProviderTable(Connection conn) {

		String sql = "CREATE TABLE IF NOT EXISTS `" + DB_SQL_TABLE_NAME_SERVICE_PROVIDER + "` (`"
				+ DB_SQL_TABLE_COLUMN_NAME_SERVICE_PROVIDER_ID + "` int(11) unsigned NOT NULL AUTO_INCREMENT, `"
				+ DB_SQL_TABLE_COLUMN_NAME_SERVICE_ID + "` int(11) DEFAULT NULL, `" + DB_SQL_TABLE_COLUMN_NAME_URL
				+ "` varchar(200) DEFAULT NULL, `" + DB_SQL_TABLE_COLUMN_NAME_PORT + "` int(11) DEFAULT NULL, `"
				+ DB_SQL_TABLE_COLUMN_NAME_ENV_OS + "` varchar(200) DEFAULT NULL, `" + DB_SQL_TABLE_COLUMN_NAME_ENV_LANG
				+ "` varchar(200) DEFAULT NULL, `" + DB_SQL_TABLE_COLUMN_NAME_ENV_COMPILER
				+ "` varchar(200) DEFAULT NULL , PRIMARY KEY (`" + DB_SQL_TABLE_COLUMN_NAME_SERVICE_PROVIDER_ID
				+ "`), KEY `SERVICE_IDX` (`" + DB_SQL_TABLE_COLUMN_NAME_SERVICE_ID
				+ "`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			return false;
		}

		return true;
	}

	@Override
	public boolean createServiceSupportTable(Connection conn) {
		String sql1 = "CREATE TABLE IF NOT EXISTS `" + DB_SQL_TABLE_NAME_SERVICE_PROVIDER_SUPPORT + "` (`"
				+ DB_SQL_TABLE_COLUMN_NAME_SUPPORT_ID + "` int(11) unsigned NOT NULL AUTO_INCREMENT, `"
				+ DB_SQL_TABLE_COLUMN_NAME_SERVICE_PROVIDER_ID + "` int(11) NOT NULL, `"
				+ DB_SQL_TABLE_COLUMN_NAME_FORMAT_NAME + "` varchar(100) NOT NULL DEFAULT '', `"
				+ DB_SQL_TABLE_COLUMN_NAME_FORMAT_VERSION + "` varchar(100) NOT NULL DEFAULT '', PRIMARY KEY (`"
				+ DB_SQL_TABLE_COLUMN_NAME_SUPPORT_ID + "`), KEY `SERVICE_PROVIDER_ID` (`"
				+ DB_SQL_TABLE_COLUMN_NAME_SERVICE_PROVIDER_ID + "`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql1);
			stmt.close();
		} catch (SQLException e) {
			return false;
		}

		return true;
	}

	@Override
	public boolean cleanAllTables(Connection conn) {

		try {

			String sql = "truncate table " + DB_SQL_TABLE_NAME_SERVICE_PROVIDER + " ; truncate table "
					+ DB_SQL_TABLE_NAME_SERVICE + " ; truncate table " + DB_SQL_TABLE_NAME_SERVICE_PROVIDER_SUPPORT
					+ " ;";
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;

	}
}
