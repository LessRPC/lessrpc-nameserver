package me.salimm.jrns.errors;

public class DatabaseNotSupported extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DatabaseNotSupported(String type) {
		super("Database " + type + " not supported by the manager");
	}

}
