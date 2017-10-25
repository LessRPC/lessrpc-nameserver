package org.mouji.ns.core.server;

import java.sql.SQLException;
import java.util.ArrayList;

import org.mouji.common.errors.DatabaseNotSupported;
import org.mouji.common.serializer.Serializer;
import org.mouji.ns.core.RandomLoadBalancer;
import org.mouji.ns.core.constants.Constants;
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

	private Config conf;
	private ServerStub stub;
	private org.mouji.common.services.NameServer ns;

	public NameServer(Config conf)
			throws ClassNotFoundException, SQLException, DatabaseNotSupported, PrefixNotANestedConfigException {
		this.conf = conf;

	}

	public void start() throws Exception {
		// default port
		int port = conf.getInteger(CONF_TAG_NAME_VIPE_API_PORT);
		this.ns = new DBBasedNameServer(conf, port, new RandomLoadBalancer());
		stub = new ServerStub(new ArrayList<Serializer>(), port);
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