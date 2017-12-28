package org.lessrpc.ns.core.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLLiteDBUtils extends SQLBasedUtils {

	@Override
	public boolean createServiceTable(Connection conn) {
		String sql = "CREATE TABLE IF NOT EXISTS " + DB_SQL_TABLE_NAME_SERVICE + "("
				+ DB_SQL_TABLE_COLUMN_NAME_SERVICE_ID + " INT PRIMARY KEY ASC, " + DB_SQL_TABLE_COLUMN_NAME_NAME
				+ " TEXT); CREATE INDEX IF NOT EXISTS SERIVCE_NAME_IDX ON SERVICE(" + DB_SQL_TABLE_COLUMN_NAME_NAME
				+ ");";
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

		String sql = "CREATE TABLE IF NOT EXISTS " + DB_SQL_TABLE_NAME_SERVICE_PROVIDER + " ("
				+ DB_SQL_TABLE_COLUMN_NAME_SERVICE_PROVIDER_ID + " INTEGER PRIMARY KEY, "
				+ DB_SQL_TABLE_COLUMN_NAME_SERVICE_ID + " INT, " + DB_SQL_TABLE_COLUMN_NAME_URL + " TEXT, "
				+ DB_SQL_TABLE_COLUMN_NAME_PORT + " INT, " + DB_SQL_TABLE_COLUMN_NAME_ENV_OS + " TEXT, "
				+ DB_SQL_TABLE_COLUMN_NAME_ENV_LANG + " TEXT, " + DB_SQL_TABLE_COLUMN_NAME_ENV_COMPILER
				+ " TEXT , CONSTRAINT SP_POINTER_UNQ UNIQUE (" + DB_SQL_TABLE_COLUMN_NAME_SERVICE_ID + ", "
				+ DB_SQL_TABLE_COLUMN_NAME_URL + ", " + DB_SQL_TABLE_COLUMN_NAME_PORT
				+ ")); CREATE INDEX SERIVCE_PROVIDER_SERVICE_ID_IDX ON " + DB_SQL_TABLE_NAME_SERVICE_PROVIDER + "("
				+ DB_SQL_TABLE_COLUMN_NAME_SERVICE_ID
				+ ");CREATE INDEX  IF NOT EXISTS SERIVCE_PROVIDER_URL_PORT_IDX ON " + DB_SQL_TABLE_NAME_SERVICE_PROVIDER
				+ "(" + DB_SQL_TABLE_COLUMN_NAME_URL + "," + DB_SQL_TABLE_COLUMN_NAME_PORT + ");";
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
		String sql1 = "CREATE TABLE IF NOT EXISTS " + DB_SQL_TABLE_NAME_SERVICE_PROVIDER_SUPPORT + "("
				+ DB_SQL_TABLE_COLUMN_NAME_SUPPORT_ID + " INTEGER PRIMARY KEY, "
				+ DB_SQL_TABLE_COLUMN_NAME_SERVICE_PROVIDER_ID + " INT, " + DB_SQL_TABLE_COLUMN_NAME_FORMAT_NAME
				+ " TEXT, " + DB_SQL_TABLE_COLUMN_NAME_FORMAT_VERSION
				+ " TEXT); CREATE  IF NOT EXISTS INDEX SUPPORT_SP_ID_IDX ON SERVICE_PROVIDER_SUPPORT("
				+ DB_SQL_TABLE_COLUMN_NAME_SERVICE_PROVIDER_ID + ");";
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

			String sql = "delete from " + DB_SQL_TABLE_NAME_SERVICE_PROVIDER + " ; delete from "
					+ DB_SQL_TABLE_NAME_SERVICE + " ; delete from " + DB_SQL_TABLE_NAME_SERVICE_PROVIDER_SUPPORT + " ;";
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
