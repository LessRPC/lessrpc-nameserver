package me.salimm.jrns.rpc;

import java.sql.SQLException;

import org.omg.PortableInterceptor.ServerRequestInfo;

import me.salimm.jrns.errors.DatabaseNotSupported;

public interface NSService {
	
	public ServiceProviderInfo getServer(int serviceId) throws ClassNotFoundException, SQLException, DatabaseNotSupported;
	
	public ServiceProviderInfo getServer(String serviceName) throws ClassNotFoundException, SQLException, DatabaseNotSupported;
	
	
	
	public ServiceProviderInfo[] getAllServer(int serviceId) throws ClassNotFoundException, SQLException, DatabaseNotSupported;
	
	public ServiceProviderInfo[] getAllServer(String serviceName) throws ClassNotFoundException, SQLException, DatabaseNotSupported;
	
	
	public ServiceInfo getServiceInfo(String serviceName) throws ClassNotFoundException, SQLException, DatabaseNotSupported;
	
	public ServiceInfo getServiceInfo(int serviceId) throws ClassNotFoundException, SQLException, DatabaseNotSupported;
	
	
	public boolean registerServiceProvider(ServiceInfo service, ServiceProviderInfo provider) throws ClassNotFoundException, SQLException, DatabaseNotSupported;
	
	public boolean removeServiceProvider(ServiceInfo service, ServiceProviderInfo provider) throws ClassNotFoundException, SQLException, DatabaseNotSupported;

}
