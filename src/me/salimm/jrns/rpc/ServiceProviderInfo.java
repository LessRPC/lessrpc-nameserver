package me.salimm.jrns.rpc;

/**
 * class that provides information of a service provider
 * 
 * @author Salim
 *
 */
public class ServiceProviderInfo {

	/**
	 * ip address of the server
	 */
	private String ip;

	/**
	 * port of the RPC server
	 */
	private int port;

	/**
	 * name of the provider
	 */
	private String providerName;
	
	public ServiceProviderInfo(String ip, int port, String providerName) {
		this.ip = ip;
		this.port = port;
		this.providerName = providerName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

}
