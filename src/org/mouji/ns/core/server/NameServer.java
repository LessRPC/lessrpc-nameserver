package org.mouji.ns.core.server;

import java.sql.SQLException;
import java.util.ArrayList;

import org.mouji.common.db.DBInfo;
import org.mouji.common.db.DBUtils;
import org.mouji.common.errors.DatabaseNotSupported;
import org.mouji.common.serializer.Serializer;
import org.mouji.ns.core.RandomLoadBalancer;
import org.mouji.ns.core.constants.Constants;
import org.mouji.ns.core.db.DBFactory;
import org.mouji.ns.core.rpc.DBBasedNameServer;
import org.mouji.ns.core.rpc.NameServerServiceProvider;
import org.mouji.stub.java.stubs.ServerStub;

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
	private org.mouji.common.services.NameServer ns;
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