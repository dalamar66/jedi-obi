package com.ldap.obi;

/**
 * File : ObiServiceException.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-10 
 * Modification date : 2010-03-10
 */

public class ObiServiceException extends Exception {

	private static final long serialVersionUID = 6824349658701772723L;

	/**
	 * Exception qui n'affiche aucun message.
	 */
	public ObiServiceException() {
		super();
	}

	/**
	 * Exception qui affiche le message passé en paramètre.
	 * 
	 * @param message
	 *            Message à afficher.
	 */
	public ObiServiceException(String message) {
		super(message);
	}

}// fin de la classe
