package org.mouji.ns.core;

import java.sql.SQLException;

import org.mouji.common.errors.DatabaseNotSupported;
import org.mouji.ns.core.server.NameServer;

import me.salimm.allconfig.core.Config;
import me.salimm.allconfig.core.types.XMLConfig;




public class NameServerRunner {

	public static void main(String[] args) throws ClassNotFoundException, SQLException, DatabaseNotSupported, Exception {
		Config conf = new XMLConfig("conf.xml");
		NameServer server = new NameServer(conf);
		server.start();
	}
}
