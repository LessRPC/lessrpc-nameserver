package org.mouji.ns.core.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.mouji.common.db.DBUtils;
import org.mouji.common.info.SerializationFormat;
import org.mouji.common.info.ServiceInfo;
import org.mouji.common.info.ServiceProviderInfo;
import org.mouji.common.info.ServiceSupportInfo;
import org.mouji.common.info.EnvironmentInfo;
import org.mouji.ns.core.constants.Constants;

public abstract class SQLBasedUtils implements DBUtils, Constants {

	@Override
	public ServiceSupportInfo[] getProviders(Connection conn, ServiceInfo<?> service) throws SQLException {
		ArrayList<ServiceProviderInfo> list = new ArrayList<ServiceProviderInfo>();

		String sql = "SELECT URL, PORT from " + DB_SQL_TABLE_NAME_SERVICE_PROVIDER + "  WHERE "
				+ DB_SQL_TABLE_COLUMN_NAME_SERVICE_ID + " = " + service.getId() + ";";
		ResultSet rs = conn.createStatement().executeQuery(sql);
		while (rs.next()) {
			list.add(new ServiceProviderInfo(rs.getString(DB_SQL_TABLE_COLUMN_NAME_URL),
					rs.getInt(DB_SQL_TABLE_COLUMN_NAME_PORT), EnvironmentInfo.currentEnvInfo()));
		}
		rs.getStatement().close();

		ServiceSupportInfo[] out = new ServiceSupportInfo[list.size()];

		for (int i = 0; i < list.size(); i++) {
			ServiceProviderInfo provider = list.get(i);
			ServiceSupportInfo sup = new ServiceSupportInfo();
			sup.setProvider(provider);
			sup.setService(service);

			String sql2 = "SELECT " + DB_SQL_TABLE_COLUMN_NAME_FORMAT_NAME + ", "
					+ DB_SQL_TABLE_COLUMN_NAME_FORMAT_VERSION + " from " + DB_SQL_TABLE_NAME_SERVICE_PROVIDER
					+ " as sp, " + DB_SQL_TABLE_NAME_SERVICE_PROVIDER_SUPPORT + " as sps  WHERE sp."
					+ DB_SQL_TABLE_COLUMN_NAME_SERVICE_PROVIDER_ID + " = sps."
					+ DB_SQL_TABLE_COLUMN_NAME_SERVICE_PROVIDER_ID + " and " + DB_SQL_TABLE_COLUMN_NAME_SERVICE_ID
					+ " = " + service.getId() + " and sp." + DB_SQL_TABLE_COLUMN_NAME_URL + " = '" + provider.getURL()
					+ "' and sp." + DB_SQL_TABLE_COLUMN_NAME_PORT + "=" + provider.getPort() + " ;";

			ResultSet rs2 = conn.createStatement().executeQuery(sql2);
			List<SerializationFormat> tmp = new ArrayList<SerializationFormat>();
			while (rs2.next()) {
				tmp.add(new SerializationFormat(rs2.getString(1), rs2.getString(2)));
			}

			sup.setSerializers(tmp.toArray(new SerializationFormat[0]));
			out[i] = sup;
		}

		return out;

	}

	@Override
	public ServiceSupportInfo[] getAllProviders(Connection conn) throws SQLException {
		ArrayList<ServiceProviderInfo> list = new ArrayList<ServiceProviderInfo>();
		ArrayList<ServiceInfo<?>> services = new ArrayList<ServiceInfo<?>>();

		String sql = "SELECT sp.URL, sp.PORT, s.NAME as SERVICE_NAME, s.SERVICE_ID from "
				+ DB_SQL_TABLE_NAME_SERVICE_PROVIDER + " as sp, " + DB_SQL_TABLE_NAME_SERVICE + " as s  WHERE s."
				+ DB_SQL_TABLE_COLUMN_NAME_SERVICE_ID + " = sp." + DB_SQL_TABLE_COLUMN_NAME_SERVICE_ID + ";";
		ResultSet rs = conn.createStatement().executeQuery(sql);
		while (rs.next()) {
			list.add(new ServiceProviderInfo(rs.getString(DB_SQL_TABLE_COLUMN_NAME_URL),
					rs.getInt(DB_SQL_TABLE_COLUMN_NAME_PORT), EnvironmentInfo.currentEnvInfo()));
			services.add(new ServiceInfo<>(rs.getString("SERVICE_NAME"), rs.getInt("SERVICE_ID")));
		}
		rs.getStatement().close();

		ServiceSupportInfo[] out = new ServiceSupportInfo[list.size()];

		for (int i = 0; i < list.size(); i++) {
			ServiceProviderInfo provider = list.get(i);
			ServiceSupportInfo sup = new ServiceSupportInfo();
			sup.setProvider(provider);
			sup.setService(services.get(i));

			String sql2 = "SELECT " + DB_SQL_TABLE_COLUMN_NAME_FORMAT_NAME + ", "
					+ DB_SQL_TABLE_COLUMN_NAME_FORMAT_VERSION + " from " + DB_SQL_TABLE_NAME_SERVICE_PROVIDER
					+ " as sp, " + DB_SQL_TABLE_NAME_SERVICE_PROVIDER_SUPPORT + " as sps  WHERE sp."
					+ DB_SQL_TABLE_COLUMN_NAME_SERVICE_PROVIDER_ID + " = sps."
					+ DB_SQL_TABLE_COLUMN_NAME_SERVICE_PROVIDER_ID + " and " + DB_SQL_TABLE_COLUMN_NAME_SERVICE_ID
					+ " = " + services.get(i).getId() + " and sp." + DB_SQL_TABLE_COLUMN_NAME_URL + " = '"
					+ provider.getURL() + "' and sp." + DB_SQL_TABLE_COLUMN_NAME_PORT + "=" + provider.getPort() + " ;";

			ResultSet rs2 = conn.createStatement().executeQuery(sql2);
			List<SerializationFormat> tmp = new ArrayList<SerializationFormat>();
			while (rs2.next()) {
				tmp.add(new SerializationFormat(rs2.getString(1), rs2.getString(2)));
			}

			sup.setSerializers(tmp.toArray(new SerializationFormat[0]));
			out[i] = sup;
		}

		return out;
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

		String sqlService = "INSERT OR IGNORE INTO " + DB_SQL_TABLE_NAME_SERVICE + " ("
				+ DB_SQL_TABLE_COLUMN_NAME_SERVICE_ID + ", " + DB_SQL_TABLE_COLUMN_NAME_NAME + " ) VALUES ("
				+ service.getId() + ",'" + service.getName() + "' );";

		String sql = "INSERT INTO " + DB_SQL_TABLE_NAME_SERVICE_PROVIDER + " (" + DB_SQL_TABLE_COLUMN_NAME_SERVICE_ID
				+ ", " + DB_SQL_TABLE_COLUMN_NAME_URL + ", " + DB_SQL_TABLE_COLUMN_NAME_PORT + ") VALUES ("
				+ service.getId() + ",'" + provider.getURL() + "'," + provider.getPort() + ");";

		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sqlService);
			stmt.executeUpdate(sql);

			int pid = -1;
			ResultSet generatedKeys = stmt.executeQuery("SELECT last_insert_rowid()");
			if (generatedKeys.next()) {
				pid = generatedKeys.getInt(1);
			} else {
				throw new SQLException("Creating user failed, no ID obtained.");
			}

			for (SerializationFormat format : support.getSerializers()) {
				String sql2 = "INSERT INTO " + DB_SQL_TABLE_NAME_SERVICE_PROVIDER_SUPPORT + " ("
						+ DB_SQL_TABLE_COLUMN_NAME_SERVICE_PROVIDER_ID + ", " + DB_SQL_TABLE_COLUMN_NAME_FORMAT_NAME
						+ ", " + DB_SQL_TABLE_COLUMN_NAME_FORMAT_VERSION + ") VALUES (" + pid + ",'" + format.getName()
						+ "','" + format.getVersion() + "');";
				try {
					stmt.executeUpdate(sql2);

				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				}

			}
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
			String sql = "DELETE FROM  " + DB_SQL_TABLE_NAME_SERVICE_PROVIDER + " WHERE " + DB_SQL_TABLE_COLUMN_NAME_URL
					+ " = '" + provider.getURL() + "' and " + DB_SQL_TABLE_COLUMN_NAME_PORT + "=" + provider.getPort()
					+ " and SERVICE_ID=" + service.getId() + " ;";

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
	public boolean unregisterAll(Connection conn, ServiceProviderInfo provider) {

		try {
			String sql = "DELETE FROM  " + DB_SQL_TABLE_NAME_SERVICE_PROVIDER + " WHERE " + DB_SQL_TABLE_COLUMN_NAME_URL
					+ " = '" + provider.getURL() + "' and " + DB_SQL_TABLE_COLUMN_NAME_PORT + "=" + provider.getPort()
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
