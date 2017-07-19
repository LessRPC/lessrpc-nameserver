package me.salimm.jrns.server;

import java.sql.SQLException;

import org.eclipse.jetty.server.Server;

import com.allConfig.conf.AbstractConfig;
import com.googlecode.jsonrpc4j.JsonRpcServer;
import com.googlecode.jsonrpc4j.ProxyUtil;

import me.salimm.jrns.constants.Constants;
import me.salimm.jrns.db.DBFactory;
import me.salimm.jrns.db.DBUtils;
import me.salimm.jrns.errors.DatabaseNotSupported;
import me.salimm.jrns.rpc.DBBasedNSService;
import me.salimm.jrns.rpc.NSService;

/**
 * 
 * NameServer class to
 * 
 * @author Salim
 *
 */
public class NameServer implements Constants {

	private AbstractConfig conf;

	private DBUtils dbUtils;

	public NameServer(AbstractConfig conf) throws ClassNotFoundException, SQLException, DatabaseNotSupported {
		this.conf = conf;
		this.dbUtils = DBFactory.getDBUtils(conf);
	}

	public void start() throws Exception {
		Object compositeService = getCompositeService();
		// creating json rpc service
		JsonRpcServer jsonRpcServer = new JsonRpcServer(compositeService);
		// default port
		int port = conf.getInteger(CONF_TAG_NAME_VIPE_API_PORT);
		// server started
		Server server = new Server(port);
		server.setHandler(new NSServiceHandler(jsonRpcServer));
		server.start();
		server.join();

	}

	private Object getCompositeService() throws Exception {

		// creating the compose service
		Object compositeService = ProxyUtil.createCompositeServiceProxy(this.getClass().getClassLoader(),
				new Object[] { new DBBasedNSService(conf, dbUtils) }, new Class[] { NSService.class }, true);
		return compositeService;
	}

	public DBUtils getDbUtils() {
		return dbUtils;
	}

	public void setDbUtils(DBUtils dbUtils) {
		this.dbUtils = dbUtils;
	}
}
