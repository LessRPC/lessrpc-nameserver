package org.mouji.ns.test;

import org.mouji.ns.core.server.NameServer;

import me.salimm.allconfig.core.types.XMLConfig;

public class TestMain2 {

	public static void main(String[] args) throws Exception {
		NameServer ns = new NameServer(new XMLConfig("conf.xml"));
		ns.start();
	}
}
