package com.ldap.obi;

/**
 * File : ObiInvalidDnException.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-10 
 * Modification date : 2010-03-10
 */

public class ObiInvalidDnException extends Exception {

	private static final long serialVersionUID = -8780765761000884602L;

	/**
	 * Exception qui n'affiche aucun message.
	 */
	public ObiInvalidDnException() {
		super();
	}

	/**
	 * Exception qui affiche le message passé en paramètre.
	 * 
	 * @param message
	 *            Message à afficher.
	 */
	public ObiInvalidDnException(String message) {
		super(message);
	}

}// fin de la classe

