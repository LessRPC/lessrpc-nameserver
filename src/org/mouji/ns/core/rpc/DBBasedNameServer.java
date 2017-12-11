package org.mouji.ns.core.rpc;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

import org.mouji.common.db.DBInfo;
import org.mouji.common.db.DBUtils;
import org.mouji.common.errors.DatabaseNotSupported;
import org.mouji.common.info.ServiceInfo;
import org.mouji.common.info.ServiceProviderInfo;
import org.mouji.common.info.ServiceSupportInfo;
import org.mouji.common.loadbalance.ProviderLoadBalancer;
import org.mouji.common.serializer.Serializer;
import org.mouji.common.services.NameServer;
import org.mouji.ns.core.db.DBFactory;
import org.mouji.stub.java.serializer.JsonSerializer;
import org.mouji.stub.java.stubs.ClientStub;

import me.salimm.allconfig.core.Config;
import me.salimm.allconfig.core.errors.PrefixNotANestedConfigException;

/**
 * 
 * 
 * 
 * @author Salim
 *
 */
public class DBBasedNameServer implements NameServer {

	private final DBUtils dbUtils;

	private final DBInfo dbInfo;

	private final int port;

	/**
	 * url of the current name server
	 */
	private final String url;

	/**
	 * Instance of the load balancer used by the name server
	 */
	private ProviderLoadBalancer balancer;

	public DBBasedNameServer(DBInfo dbInfo, DBUtils dbUtils, String url, int port, ProviderLoadBalancer balancer)
			throws PrefixNotANestedConfigException, ClassNotFoundException, SQLException, DatabaseNotSupported {
		this.dbUtils = dbUtils;
		this.url = url;
		this.port = port;
		this.setLoadBalancer(balancer);
		this.dbInfo = dbInfo;
		initDatabase(dbInfo);
	}

	public DBBasedNameServer(DBInfo dbInfo, DBUtils dbUtils, int port, ProviderLoadBalancer balancer)
			throws UnknownHostException, PrefixNotANestedConfigException, ClassNotFoundException, SQLException,
			DatabaseNotSupported {
		this(dbInfo, dbUtils, Inet4Address.getLocalHost().getHostAddress(), port, balancer);
	}

	public DBBasedNameServer(Config conf, String url, int port, ProviderLoadBalancer balancer)
			throws UnknownHostException, PrefixNotANestedConfigException, ClassNotFoundException, SQLException,
			DatabaseNotSupported {
		this(DBFactory.getDBInfo(conf), DBFactory.getDBUtils(conf), Inet4Address.getLocalHost().getHostAddress(), port, balancer);
	}

	public DBBasedNameServer(Config conf, int port, ProviderLoadBalancer balancer) throws UnknownHostException,
			PrefixNotANestedConfigException, ClassNotFoundException, SQLException, DatabaseNotSupported {
		this(conf, Inet4Address.getLocalHost().getHostAddress(), port, balancer);
	}

	private void initDatabase(DBInfo dbInfo) throws ClassNotFoundException, SQLException, DatabaseNotSupported {
		// opening connection
		Connection conn = DBFactory.getConnection(dbInfo);
		// SERVICE table
		dbUtils.createServiceTable(conn);
		// SERVICE_PROVIDER table
		dbUtils.createServiceProviderTable(conn);
		// SERVICE_PROVIDER_SUPPORT table
		dbUtils.createServiceSupportTable(conn);
		// closing connection
		conn.close();
		// print
		System.out.println("Finished checking if schema exists....");
	}

	@Override
	public ServiceSupportInfo getProvider(ServiceInfo<?> service)
			throws ClassNotFoundException, SQLException, DatabaseNotSupported {
		Connection conn = DBFactory.getConnection(dbInfo);
		ServiceSupportInfo[] supports = dbUtils.getProviders(conn, service);
		conn.close();

		if (supports.length == 0) {
			return null;
		}
		return getLoadBalancer().select(service, supports);
	}

	@Override
	public ServiceSupportInfo[] getProviders(ServiceInfo<?> service)
			throws ClassNotFoundException, SQLException, DatabaseNotSupported {
		Connection conn = DBFactory.getConnection(dbInfo);
		ServiceSupportInfo[] supports = dbUtils.getProviders(conn, service);
		conn.close();

		return supports;
	}

	@Override
	public ServiceSupportInfo[] getAllProviders() throws ClassNotFoundException, SQLException, DatabaseNotSupported {
		Connection conn = DBFactory.getConnection(dbInfo);
		ServiceSupportInfo[] supports = dbUtils.getAllProviders(conn);
		conn.close();

		return supports;
	}

	@Override
	public boolean register(ServiceSupportInfo support)
			throws ClassNotFoundException, SQLException, DatabaseNotSupported {
		Connection conn = DBFactory.getConnection(dbInfo);
		boolean flag = dbUtils.register(DBFactory.getConnection(dbInfo), support);
		conn.close();
		return flag;
	}

	@Override
	public boolean unregister(ServiceInfo<?> service, ServiceProviderInfo provider)
			throws ClassNotFoundException, SQLException, DatabaseNotSupported {
		Connection conn = DBFactory.getConnection(dbInfo);
		boolean flag = dbUtils.unregister(DBFactory.getConnection(dbInfo), service, provider);
		conn.close();
		return flag;
	}

	@Override
	public ServiceInfo<?> getServiceInfoByName(String serviceName)
			throws ClassNotFoundException, SQLException, DatabaseNotSupported {
		Connection conn = DBFactory.getConnection(dbInfo);
		ServiceInfo<?> si = dbUtils.getServiceInfo(DBFactory.getConnection(dbInfo), serviceName);
		conn.close();
		return si;
	}

	@Override
	public ServiceInfo<?> getServiceInfoById(int serviceId)
			throws ClassNotFoundException, SQLException, DatabaseNotSupported {
		Connection conn = DBFactory.getConnection(dbInfo);
		ServiceInfo<?> si = dbUtils.getServiceInfo(DBFactory.getConnection(dbInfo), serviceId);
		conn.close();
		return si;
	}

	@Override
	public String getURL() {
		return url;
	}

	@Override
	public int getPort() {
		return port;
	}

	/**
	 * Determines if the name server is working properly. Specifically this name
	 * server checks if the database server is running to indicate its status
	 */
	@Override
	public boolean ping() {
		try {
			Connection conn = DBFactory.getConnection(dbInfo);
			conn.close();
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	@Override
	public ProviderLoadBalancer getLoadBalancer() {
		return balancer;
	}

	@Override
	public void setLoadBalancer(ProviderLoadBalancer balancer) {
		this.balancer = balancer;
	}

	public void reset() throws SQLException, ClassNotFoundException, DatabaseNotSupported {
		Connection conn = DBFactory.getConnection(dbInfo);
		// clear tables
		dbUtils.cleanAllTables(conn);
		conn.close();
	}

	@Override
	public boolean checkProviderStatus(ServiceProviderInfo provider) {
		ClientStub client = new ClientStub(Arrays.asList(new Serializer[] { new JsonSerializer() }));
		try {
			boolean flag = client.ping(provider);
			if (!flag) {
				unregisterAll(provider);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean unregisterAll(ServiceProviderInfo provider)
			throws ClassNotFoundException, SQLException, DatabaseNotSupported {
		Connection conn = DBFactory.getConnection(dbInfo);
		boolean flag = dbUtils.unregisterAll(DBFactory.getConnection(dbInfo), provider);
		conn.close();
		return flag;
	}

}
