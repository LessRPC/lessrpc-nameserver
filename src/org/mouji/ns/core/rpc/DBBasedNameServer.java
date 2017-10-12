package org.mouji.ns.core.rpc;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;


import org.mouji.common.db.DBInfo;
import org.mouji.common.db.DBUtils;
import org.mouji.common.errors.DatabaseNotSupported;
import org.mouji.common.info.ServiceInfo;
import org.mouji.common.info.ServiceProviderInfo;
import org.mouji.common.info.ServiceSupportInfo;
import org.mouji.common.loadbalance.ProviderLoadBalancer;
import org.mouji.common.services.NameServer;
import org.mouji.ns.core.db.DBFactory;

import me.salimm.allConfig.Config;
import me.salimm.allConfig.errors.PrefixNotANestedConfigException;

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

	public DBBasedNameServer(Config conf, DBUtils dbUtils, String url, int port,
			ProviderLoadBalancer balancer) throws PrefixNotANestedConfigException {
		this.dbUtils = dbUtils;
		this.url = url;
		this.port = port;
		this.setLoadBalancer(balancer);
		this.dbInfo = DBFactory.getDBInfo(conf);
	}

	public DBBasedNameServer(Config conf, DBUtils dbUtils, int port, ProviderLoadBalancer balancer)
			throws UnknownHostException, PrefixNotANestedConfigException {
		this(conf, dbUtils, Inet4Address.getLocalHost().getHostAddress(), port, balancer);
	}

	@Override
	public ServiceSupportInfo getProvider(ServiceInfo<?> service)
			throws ClassNotFoundException, SQLException, DatabaseNotSupported {
		Connection conn = DBFactory.getConnection(dbInfo);
		ServiceSupportInfo[] supports = dbUtils.getProviders(conn, service);
		conn.close();

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

}
