package com.ldap.jedi;

/**
 * File : JediLog.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-04 
 * Modification date : 2010-03-04
 */
import java.util.List;

/**
 * @author HUMEAU Xavier
 * @version Version 1.0
 */
public class JediLog {

	public final static int LOG_FUNCTIONAL = 0;
	public final static int LOG_PRODUCTION = 1;
	public final static int LOG_TECHNICAL = 2;

	public final static int INFO = 0;
	public final static int WARNING = 1;
	public final static int ERROR = 2;
	public final static int EMERGENCY = 3;

	private static StringBuffer messageComplet = null;

	public static void log(int categorie, int priorite, String message, String value, Object origin) {
		// Construction du message
		messageComplet = new StringBuffer();
		messageComplet.append(message);

		value = nullToBlank(value);
		if (value.length() != 0) {
			messageComplet.append(value);
		}

		// String cat = null;
		switch (categorie) {
			case 0:
				// cat = "com.jedi.fonctional";
				break;
			case 1:
				// cat = "com.jedi.production";
				break;
			case 2:
				// cat = "com.jedi.technical";
				break;
			default:
				// cat = "com.jedi.production";
		}

		switch (priorite) {
			case 0:
				// Logger.log(cat, "info", messageComplet.toString());
				break;
			case 1:
				// Logger.log(cat, "warn", messageComplet.toString());
				break;
			case 2:
				// Logger.log(cat, "error", messageComplet.toString());
				break;
			case 3:
				// Logger.log(cat, "fatal", messageComplet.toString());
				break;
			default:
				// Logger.log(cat, "debug", messageComplet.toString());
		}
	}

	/**
	 * Méthode de log.
	 * 
	 * @param categorie
	 * @param priorite
	 * @param message
	 * @param origin
	 */
	public static void log(int categorie, int priorite, String message, List<String> values, Object origin) {
		if (values != null && values.isEmpty() == false) {
			for (String value : values) {
				log(categorie, priorite, message, value, origin);
			}
		}
	}

	/**
	 * Méthode qui remplace les messages null ou vide par " ".
	 * 
	 * @param message
	 *            Message à analyser.
	 * @return Le message à afficher.
	 */
	public static String nullToBlank(String message) {
		if (message == null || message.length() == 0) {
			return ("");
		} else {
			return (message);
		}
	}

}// fin de la classe