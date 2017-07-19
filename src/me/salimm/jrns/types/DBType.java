package me.salimm.jrns.types;

/**
 * DBType enum for supported database types
 * 
 * @author Salim
 *
 */
public enum DBType {

	MYSQL, SQLLITE, ORACLE;

	public static DBType fromString(String str) {
		if (str.toUpperCase().equals(MYSQL.name())) {
			return MYSQL;
		} else if (str.toUpperCase().equals(SQLLITE.name())) {
			return SQLLITE;
		} else if (str.toUpperCase().equals(ORACLE.name())) {
			return ORACLE;
		}
		return null;
	}
}
