package org.mouji.ns.core.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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

import com.allConfig.conf.AbstractConfig;

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
		initDatabase(DBFactory.getDBInfo(conf));

	}

	private void initDatabase(DBInfo dbInfo) throws ClassNotFoundException, SQLException, DatabaseNotSupported {
		// opening connection
		Connection conn = DBFactory.getConnection(dbInfo);
		// SERVICE table
		dbUtils.createServiceTable(conn);
		// SERVICE_PROVIDER table
		dbUtils.createServiceProviderTable(conn);
		// SERVICE_PROVIDER_SUPPORT table
		dbUtils.createServiceSupportTable(conn);
		// closing connection
		conn.close();
		// print
		System.out.println("Finished checking if schema exists....");
	}

	public void start() throws Exception {
		// default port
		int port = conf.getInteger(CONF_TAG_NAME_VIPE_API_PORT);
		// server started
		ServerStub stub = new ServerStub(new ArrayList<Serializer>(), port);
		stub.init(new NameServerServiceProvider(new DBBasedNameServer(conf, dbUtils, port, new RandomLoadBalancer())));
		stub.start();

	}

	public DBUtils getDbUtils() {
		return dbUtils;
	}

	public void setDbUtils(DBUtils dbUtils) {
		this.dbUtils = dbUtils;
	}
}