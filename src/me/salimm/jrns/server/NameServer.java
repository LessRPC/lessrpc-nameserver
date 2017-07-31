package me.salimm.jrns.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.jetty.server.Server;

import com.allConfig.conf.AbstractConfig;
import com.googlecode.jsonrpc4j.JsonRpcServer;
import com.googlecode.jsonrpc4j.ProxyUtil;

import me.salimm.jrns.common.db.DBUtils;
import me.salimm.jrns.common.errors.DatabaseNotSupported;
import me.salimm.jrns.common.services.NSService;
import me.salimm.jrns.constants.Constants;
import me.salimm.jrns.db.DBFactory;
import me.salimm.jrns.rpc.DBBasedNSService;

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

		// init database
		Connection conn = DBFactory.getConnection(DBFactory.getDBInfo(conf));
		String sql1 = "CREATE TABLE IF NOT EXISTS SERVICE (SID integer, NAME text);";
		// 0:in, 1: out
		// String sql2 = "CREATE TABLE IF NOT EXISTS SERVICE_IO (SID integer,
		// CLSPATH text, TYPE integer);";
		String sql3 = "CREATE TABLE IF NOT EXISTS SERVICE_PROVIDER (PID integer,SID integer, URL text, PORT integer);";

		Statement stmt = conn.createStatement();

		stmt.executeUpdate(sql1);
		// stmt.executeUpdate(sql2);
		stmt.executeUpdate(sql3);

		stmt.close();
		System.out.println("Finished checking if schema exists....");

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
