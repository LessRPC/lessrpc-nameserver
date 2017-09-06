package org.mouji.ns.core.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.mouji.common.db.DBUtils;
import org.mouji.common.info.ServiceInfo;
import org.mouji.common.info.ServiceProviderInfo;
import org.mouji.common.info.ServiceSupportInfo;
import org.mouji.common.info.StubEnvInfo;
import org.mouji.ns.core.constants.Constants;

public abstract class SQLBasedUtils implements DBUtils, Constants {

	@Override
	public ServiceSupportInfo[] getProviders(Connection conn, ServiceInfo<?> service) throws SQLException {
		ArrayList<ServiceProviderInfo> list = new ArrayList<ServiceProviderInfo>();

		String sql = "SELECT * from " + DB_SQL_TABLE_NAME_SERVICE_PROVIDER + "  WHERE "
				+ DB_SQL_TABLE_COLUMN_NAME_SERVICE_ID + " = " + service.getId() + ";";
		ResultSet rs = conn.createStatement().executeQuery(sql);
		while (rs.next()) {
			list.add(new ServiceProviderInfo(rs.getString(DB_SQL_TABLE_COLUMN_NAME_URL),
					rs.getInt(DB_SQL_TABLE_COLUMN_NAME_PORT), StubEnvInfo.currentEnvInfo()));
		}
		rs.getStatement().close();
		return list.toArray(new ServiceSupportInfo[] {});

	}

	@Override
	public ServiceSupportInfo[] getAllProviders(Connection conn) throws SQLException {
		ArrayList<ServiceProviderInfo> list = new ArrayList<ServiceProviderInfo>();

		String sql = "SELECT * from " + DB_SQL_TABLE_NAME_SERVICE_PROVIDER + ";";
		ResultSet rs = conn.createStatement().executeQuery(sql);
		while (rs.next()) {
			list.add(new ServiceProviderInfo(rs.getString(DB_SQL_TABLE_COLUMN_NAME_URL),
					rs.getInt(DB_SQL_TABLE_COLUMN_NAME_PORT), StubEnvInfo.currentEnvInfo()));
		}
		rs.getStatement().close();
		return list.toArray(new ServiceSupportInfo[] {});
	}

	@Override
	public ServiceInfo<?> getServiceInfo(Connection conn, String serviceName)
			throws SQLException, ClassNotFoundException {
		String sql = "SELECT * from " + DB_SQL_TABLE_NAME_SERVICE + "  WHERE " + DB_SQL_TABLE_COLUMN_NAME_NAME + " = '"
				+ serviceName + "'";
		ResultSet rs = conn.createStatement().executeQuery(sql);
		if (!rs.next())
			return null;
		String name = rs.getString(DB_SQL_TABLE_COLUMN_NAME_NAME);
		int id = rs.getInt(DB_SQL_TABLE_COLUMN_NAME_SERVICE_ID);
		rs.getStatement().close();
		return new ServiceInfo<>(name, id);
	}

	@Override
	public ServiceInfo<?> getServiceInfo(Connection conn, int serviceId) throws SQLException, ClassNotFoundException {
		String sql = "SELECT * from " + DB_SQL_TABLE_NAME_SERVICE + "  WHERE " + DB_SQL_TABLE_COLUMN_NAME_SERVICE_ID
				+ " = " + serviceId + "";
		ResultSet rs = conn.createStatement().executeQuery(sql);
		if (!rs.next())
			return null;
		String name = rs.getString(DB_SQL_TABLE_COLUMN_NAME_NAME);
		int id = rs.getInt(DB_SQL_TABLE_COLUMN_NAME_SERVICE_ID);
		rs.getStatement().close();
		return new ServiceInfo<>(name, id);
	}

	@Override
	public boolean register(Connection conn, ServiceSupportInfo support) {
		ServiceInfo<?> service = support.getService();
		ServiceProviderInfo provider = support.getProvider();

		String sql = "INSERT INTO " + DB_SQL_TABLE_NAME_SERVICE_PROVIDER + " (" + DB_SQL_TABLE_COLUMN_NAME_SERVICE_ID
				+ ", " + DB_SQL_TABLE_COLUMN_NAME_SERVICE_PROVIDER_ID + ", " + DB_SQL_TABLE_COLUMN_NAME_PORT
				+ ") VALUES (" + service.getId() + ",'" + provider.getURL() + "'," + provider.getPort() + ");";
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean unregister(Connection conn, ServiceInfo<?> service, ServiceProviderInfo provider) {

		try {
			String sql = "DELETE FROM TABLE " + DB_SQL_TABLE_NAME_SERVICE_PROVIDER + " WHERE "
					+ DB_SQL_TABLE_COLUMN_NAME_URL + " = '" + provider.getURL() + "' and "
					+ DB_SQL_TABLE_COLUMN_NAME_PORT + "=" + provider.getPort() + ";";

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
