package me.salimm.jrns;

import java.sql.SQLException;

import com.allConfig.conf.XMLConfig;

import me.salimm.jrns.common.errors.DatabaseNotSupported;
import me.salimm.jrns.server.NameServer;

public class NameServerRunner {

	public static void main(String[] args) throws ClassNotFoundException, SQLException, DatabaseNotSupported, Exception {
		NameServer server = new NameServer(new XMLConfig("conf.xml"));
		server.start();
	}
}
