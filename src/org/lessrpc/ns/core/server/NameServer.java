package org.lessrpc.ns.core.server;

import java.sql.SQLException;
import java.util.ArrayList;

import org.lessrpc.common.db.DBInfo;
import org.lessrpc.common.db.DBUtils;
import org.lessrpc.common.errors.DatabaseNotSupported;
import org.lessrpc.common.serializer.Serializer;
import org.lessrpc.ns.core.RandomLoadBalancer;
import org.lessrpc.ns.core.constants.Constants;
import org.lessrpc.ns.core.db.DBFactory;
import org.lessrpc.ns.core.rpc.DBBasedNameServer;
import org.lessrpc.ns.core.rpc.NameServerServiceProvider;
import org.lessrpc.stub.java.stubs.ServerStub;

import me.salimm.allconfig.core.Config;
import me.salimm.allconfig.core.errors.PrefixNotANestedConfigException;

/**
 * 
 * NameServer class to
 * 
 * @author Salim
 *
 */
public class NameServer implements Constants {

	private ServerStub stub;
	private org.lessrpc.common.services.NameServer ns;
	private int port;
	private DBInfo dbInfo;
	private DBUtils dbUtils;

	public NameServer(Config conf)
			throws ClassNotFoundException, SQLException, DatabaseNotSupported, PrefixNotANestedConfigException {
		this(conf.getInteger(CONF_TAG_NAME_VIPE_API_PORT), DBFactory.getDBInfo(conf), DBFactory.getDBUtils(conf));
	}

	public NameServer(int port, DBInfo dbInfo, DBUtils dbUtils)
			throws ClassNotFoundException, SQLException, DatabaseNotSupported, PrefixNotANestedConfigException {
		this.port = port;
		this.dbInfo = dbInfo;
		this.dbUtils = dbUtils;
	}

	public void start() throws Exception {
		// default port
		this.ns = new DBBasedNameServer(dbInfo, dbUtils, port, new RandomLoadBalancer());
		stub = new ServerStub(port, new ArrayList<Serializer>());
		stub.init(new NameServerServiceProvider(ns));
		stub.start();

	}

	public void reset() throws ClassNotFoundException, SQLException, DatabaseNotSupported {
		ns.reset();
	}

	public void stop() throws Exception {
		stub.stop();
	}
}