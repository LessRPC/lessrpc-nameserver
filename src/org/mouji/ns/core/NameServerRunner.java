package org.mouji.ns.core;

import java.sql.SQLException;

import org.mouji.common.errors.DatabaseNotSupported;
import org.mouji.ns.core.server.NameServer;

import me.salimm.allConfig.types.XMLConfig;



public class NameServerRunner {

	public static void main(String[] args) throws ClassNotFoundException, SQLException, DatabaseNotSupported, Exception {
		NameServer server = new NameServer(new XMLConfig("conf.xml"));
		server.start();
	}
}
