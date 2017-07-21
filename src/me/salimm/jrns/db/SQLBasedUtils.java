package me.salimm.jrns.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import me.salimm.jrns.common.db.DBUtils;
import me.salimm.jrns.common.info.ServiceInfo;
import me.salimm.jrns.common.info.ServiceProviderInfo;
import me.salimm.jrns.common.types.StubEnvType;
import me.salimm.jrns.constants.Constants;

public class SQLBasedUtils implements DBUtils, Constants {

	@Override
	public ServiceProviderInfo getServer(Connection conn, int serviceId) throws SQLException {
		String sql = "SELECT * from SERVICE_PROVIDER  WHERE SID = " + serviceId + " ORDER BY RANDOM() LIMIT 1;";
		ResultSet rs = conn.createStatement().executeQuery(sql);
		if (!rs.next()) {
			rs.getStatement().close();
			return null;
		}
		ServiceProviderInfo info = new ServiceProviderInfo(rs.getString("URL"), rs.getInt("PORT"), "",
				StubEnvType.JAVA);
		rs.getStatement().close();
		return info;
	}

	@Override
	public ServiceProviderInfo getServer(Connection conn, String serviceName) throws SQLException {
		String sql = "SELECT * from SERVICE_PROVIDER  WHERE NAME = '" + serviceName + "' ORDER BY RANDOM() LIMIT 1;";
		ResultSet rs = conn.createStatement().executeQuery(sql);
		if (!rs.next()) {
			rs.getStatement().close();
			return null;
		}
		ServiceProviderInfo info = new ServiceProviderInfo(rs.getString("IP"), rs.getInt("PORT"), "", StubEnvType.JAVA);
		rs.getStatement().close();
		return info;
	}

	@Override
	public ServiceProviderInfo[] getAllServer(Connection conn, int serviceId) throws SQLException {
		ArrayList<ServiceProviderInfo> list = new ArrayList<ServiceProviderInfo>();

		String sql = "SELECT * from SERVICE_PROVIDER  WHERE ID = " + serviceId + ";";
		ResultSet rs = conn.createStatement().executeQuery(sql);
		while (rs.next()) {
			list.add(new ServiceProviderInfo(rs.getString("IP"), rs.getInt("PORT"), rs.getString("NAME"),
					StubEnvType.JAVA));
		}
		rs.getStatement().close();
		return list.toArray(new ServiceProviderInfo[] {});

	}

	@Override
	public ServiceProviderInfo[] getAllServer(Connection conn, String serviceName) throws SQLException {
		ArrayList<ServiceProviderInfo> list = new ArrayList<ServiceProviderInfo>();

		String sql = "SELECT * from SERVICE_PROVIDER  WHERE NAME = '" + serviceName + "';";
		ResultSet rs = conn.createStatement().executeQuery(sql);
		while (rs.next()) {
			list.add(new ServiceProviderInfo(rs.getString("IP"), rs.getInt("PORT"), rs.getString("NAME"),
					StubEnvType.JAVA));
		}
		rs.getStatement().close();
		return list.toArray(new ServiceProviderInfo[] {});
	}

	@Override
	public ServiceInfo<?> getServiceInfo(Connection conn, String serviceName)
			throws SQLException, ClassNotFoundException {
		String sql = "SELECT * from SERVICE  WHERE NAME = '" + serviceName + "'";
		ResultSet rs = conn.createStatement().executeQuery(sql);
		if (!rs.next())
			return null;
		String name = rs.getString("NAME");
		int id = rs.getInt("SID");
		rs.getStatement().close();
		Class<?> output = null;
		List<Class<?>> inputs = new ArrayList<Class<?>>();
		sql = "SELECT * from SERVICE_IO  WHERE SID = " + id + "";
		rs = conn.createStatement().executeQuery(sql);
		while (rs.next()) {
			if (rs.getInt("TYPE") == TYPE_SERVICE_IO_IN) {
				inputs.add(Class.forName(rs.getString("CLSPATH")));
			} else {
				output = Class.forName(rs.getString("CLSPATH"));
			}
		}
		rs.getStatement().close();

		return new ServiceInfo<>(name, id, inputs.toArray(new Class<?>[] {}), output);
	}

	@Override
	public ServiceInfo<?> getServiceInfo(Connection conn, int serviceId) throws SQLException, ClassNotFoundException {
		String sql = "SELECT * from SERVICE  WHERE SID = " + serviceId + "";
		ResultSet rs = conn.createStatement().executeQuery(sql);
		if (!rs.next())
			return null;
		String name = rs.getString("NAME");
		int id = rs.getInt("SID");
		rs.getStatement().close();
		Class<?> output = null;
		List<Class<?>> inputs = new ArrayList<Class<?>>();
		sql = "SELECT * from SERVICE_IO  WHERE SID = " + id + "";
		rs = conn.createStatement().executeQuery(sql);
		while (rs.next()) {
			if (rs.getInt("TYPE") == TYPE_SERVICE_IO_IN) {
				inputs.add(Class.forName(rs.getString("CLSPATH")));
			} else {
				output = Class.forName(rs.getString("CLSPATH"));
			}
		}
		rs.getStatement().close();

		return new ServiceInfo<>(name, id, inputs.toArray(new Class<?>[] {}), output);
	}

	@Override
	public boolean registerServiceProvider(Connection conn, ServiceInfo<?> service, ServiceProviderInfo provider) {
		String sql = "INSERT INTO SERVICE_PROVIDER (SID, URL, PORT) VALUES (" + service.getId() + ",'"
				+ provider.getIp() + "'," + provider.getPort() + ");";
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
	public boolean removeServiceProvider(Connection conn, ServiceInfo<?> service, ServiceProviderInfo provider) {
		String sql = "DELETE FROM TABLE SERVICE_PROVIDER WHERE URL = '" + provider.getIp() + "' and PORT="
				+ provider.getPort() + ";";
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

}
