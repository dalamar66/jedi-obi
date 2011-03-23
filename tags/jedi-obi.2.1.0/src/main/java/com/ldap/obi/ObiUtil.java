package com.ldap.obi;

public class ObiUtil {

	// Constante specifiant l'affichage ou non de la trace.
	protected static boolean CST_PRINT_TRACE = false;

	public static boolean getWithTrace() {
		return (CST_PRINT_TRACE);
	}

	public static void setWithTrace(boolean trace) {
		CST_PRINT_TRACE = trace;
	}

	public static String dcToDC(String path) {
		int i = path.indexOf("dc=");

		String firstPart = null;
		String secondPart = null;

		/* Si on trouve la chaine dc= */
		while (i != -1) {
			firstPart = path.substring(0, i);
			secondPart = path.substring(i + 3);
			path = firstPart + "DC=" + secondPart;
			i = path.indexOf("dc=");
		}

		return path;
	}

	public static String cnToCN(String path) {
		int i = path.indexOf("cn=");

		String firstPart = null;
		String secondPart = null;

		/* Si on trouve la chaine dc= */
		while (i != -1) {
			firstPart = path.substring(0, i);
			secondPart = path.substring(i + 3);
			path = firstPart + "CN=" + secondPart;
			i = path.indexOf("cn=");
		}

		return path;
	}

	public static String ouToOU(String path) {
		int i = path.indexOf("ou=");

		String firstPart = null;
		String secondPart = null;

		/* Si on trouve la chaine dc= */
		while (i != -1) {
			firstPart = path.substring(0, i);
			secondPart = path.substring(i + 3);
			path = firstPart + "OU=" + secondPart;
			i = path.indexOf("ou=");
		}

		return path;
	}

	public static String upperCasePath(String path) {
		String res = null;

		res = cnToCN(path);
		res = ouToOU(res);
		res = dcToDC(res);

		return res;
	}

	public static boolean endWithPath(String completeString, String endString) {
		if (completeString != null && endString != null) {
			return trimPath(completeString).toLowerCase().endsWith((trimPath(endString)).toLowerCase());
		} else {
			return false;
		}
	}

	private static String trimPath(String stringToConvert) {
		return stringToConvert.replaceAll(" ", "");
	}

}
