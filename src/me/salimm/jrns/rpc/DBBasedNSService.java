package me.salimm.jrns.rpc;

import java.sql.Connection;
import java.sql.SQLException;

import com.allConfig.conf.AbstractConfig;

import me.salimm.jrns.common.db.DBInfo;
import me.salimm.jrns.common.db.DBUtils;
import me.salimm.jrns.common.errors.DatabaseNotSupported;
import me.salimm.jrns.common.info.ServiceInfo;
import me.salimm.jrns.common.info.ServiceProviderInfo;
import me.salimm.jrns.common.services.NSService;
import me.salimm.jrns.db.DBFactory;

/**
 * 
 * 
 * 
 * @author Salim
 *
 */
public class DBBasedNSService implements NSService {

	private DBUtils dbUtils;

	private DBInfo dbInfo;

	public DBBasedNSService(AbstractConfig conf, DBUtils dbUtils) {
		this.dbUtils = dbUtils;
		this.dbInfo = DBFactory.getDBInfo(conf);
	}

	@Override
	public ServiceProviderInfo getServerById(int serviceId)
			throws ClassNotFoundException, SQLException, DatabaseNotSupported {
		Connection conn = DBFactory.getConnection(dbInfo);
		ServiceProviderInfo spi = dbUtils.getServer(conn, serviceId);
		conn.close();
		return spi;
	}

	@Override
	public ServiceProviderInfo getServerByName(String serviceName)
			throws ClassNotFoundException, SQLException, DatabaseNotSupported {
		Connection conn = DBFactory.getConnection(dbInfo);
		ServiceProviderInfo spi = dbUtils.getServer(conn, serviceName);
		conn.close();
		return spi;
	}

	@Override
	public ServiceProviderInfo[] getAllServersById(int serviceId)
			throws ClassNotFoundException, SQLException, DatabaseNotSupported {
		Connection conn = DBFactory.getConnection(dbInfo);
		ServiceProviderInfo[] spis = dbUtils.getAllServer(DBFactory.getConnection(dbInfo), serviceId);
		conn.close();
		return spis;
	}

	@Override
	public ServiceProviderInfo[] getAllServersByName(String serviceName)
			throws ClassNotFoundException, SQLException, DatabaseNotSupported {
		Connection conn = DBFactory.getConnection(dbInfo);
		ServiceProviderInfo[] spis = dbUtils.getAllServer(DBFactory.getConnection(dbInfo), serviceName);
		conn.close();
		return spis;
	}

	@Override
	public ServiceInfo<?> getServiceInfoByName(String serviceName)
			throws ClassNotFoundException, SQLException, DatabaseNotSupported {
		Connection conn = DBFactory.getConnection(dbInfo);
		ServiceInfo<?> si = dbUtils.getServiceInfo(DBFactory.getConnection(dbInfo), serviceName);
		conn.close();
		System.out.println(si);
		return si;
	}

	@Override
	public ServiceInfo<?> getServiceInfoById(int serviceId)
			throws ClassNotFoundException, SQLException, DatabaseNotSupported {
		Connection conn = DBFactory.getConnection(dbInfo);
		ServiceInfo<?> si = dbUtils.getServiceInfo(DBFactory.getConnection(dbInfo), serviceId);
		conn.close();
		System.out.println(si);
		return si;
	}

	@Override
	public boolean registerServiceProvider(ServiceInfo<?> service, ServiceProviderInfo provider)
			throws ClassNotFoundException, SQLException, DatabaseNotSupported {
		System.out.println(service);
		Connection conn = DBFactory.getConnection(dbInfo);
		boolean flag = dbUtils.registerServiceProvider(DBFactory.getConnection(dbInfo), service, provider);
		conn.close();
		return flag;
	}

	@Override
	public boolean removeServiceProvider(ServiceInfo<?> service, ServiceProviderInfo provider)
			throws ClassNotFoundException, SQLException, DatabaseNotSupported {
		Connection conn = DBFactory.getConnection(dbInfo);
		boolean flag = dbUtils.removeServiceProvider(DBFactory.getConnection(dbInfo), service, provider);
		conn.close();
		return flag;
	}

	@Override
	public ServiceProviderInfo[] getAllServer() throws ClassNotFoundException, SQLException, DatabaseNotSupported {
		Connection conn = DBFactory.getConnection(dbInfo);
		ServiceProviderInfo[] spis = dbUtils.getAllServer(DBFactory.getConnection(dbInfo));
		conn.close();
		return spis;
	}

}
