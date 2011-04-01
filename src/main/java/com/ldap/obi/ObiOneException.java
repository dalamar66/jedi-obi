package com.ldap.obi;

/**
 * File : ObiOneException.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-09 
 * Modification date : 2010-03-09
 */

public class ObiOneException extends Exception {

	private static final long serialVersionUID = 6829231737120547070L;

	/**
	 * Exception qui n'affiche aucun message.
	 */
	public ObiOneException() {
		super();
	}

	/**
	 * Exception qui affiche le message passé en paramètre.
	 * 
	 * @param message
	 *            Message à afficher.
	 */
	public ObiOneException(String message) {
		super(message);
	}

}// fin de la classe
