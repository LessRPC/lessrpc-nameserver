package me.salimm.jrns.db;

import java.sql.Connection;
import java.sql.SQLException;


import me.salimm.jrns.rpc.ServiceInfo;
import me.salimm.jrns.rpc.ServiceProviderInfo;

public interface DBUtils {

	/**
	 * get Server info that implements service with given id
	 * 
	 * @param serviceId
	 * @return
	 * @throws SQLException 
	 */
	public ServiceProviderInfo getServer(Connection conn,int serviceId) throws SQLException;

	/**
	 * get Server info that implements service with given name
	 * 
	 * @param serviceName
	 * @return
	 * @throws SQLException 
	 */
	public ServiceProviderInfo getServer(Connection conn,String serviceName) throws SQLException;

	/**
	 * get list of all Server info that implements service with given id
	 * 
	 * @param serviceId
	 * @return
	 * @throws SQLException 
	 */
	public ServiceProviderInfo[] getAllServer(Connection conn,int serviceId) throws SQLException;

	/**
	 * 
	 * get list of all Server info that implements service with given name
	 * 
	 * @param serviceName
	 * @return
	 * @throws SQLException 
	 */
	public ServiceProviderInfo[] getAllServer(Connection conn,String serviceName) throws SQLException;

	/**
	 * 
	 * get ServiceInfo for given service name
	 * 
	 * @param serviceName
	 * @return
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public ServiceInfo getServiceInfo(Connection conn,String serviceName) throws SQLException, ClassNotFoundException;

	/**
	 * 
	 * get ServiceInfo for given service id
	 * 
	 * @param serviceId
	 * @return
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public ServiceInfo getServiceInfo(Connection conn,int serviceId) throws SQLException, ClassNotFoundException;

	/**
	 * 
	 * register given server for given service
	 * 
	 * @param service
	 * @param provider
	 * @return
	 */
	public boolean registerServiceProvider(Connection conn,ServiceInfo service, ServiceProviderInfo provider);

	/**
	 * remove given server from given service
	 * 
	 * 
	 * @param service
	 * @param provider
	 * @return
	 */
	public boolean removeServiceProvider(Connection conn,ServiceInfo service, ServiceProviderInfo provider);

}
