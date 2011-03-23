package com.ldap.jedi;

/**
 * File : JediConnectionException.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-04 
 * Modification date : 2010-03-04
 */

/**
 * Classe générant les messages d'erreurs pour les exceptions de connection.
 * 
 * @author HUMEAU Xavier
 * @version Version 1.0
 */
public class JediConnectionException extends Exception {

	private static final long serialVersionUID = -5777847222178005246L;

	/**
	 * Constructeur vide.
	 */
	public JediConnectionException() {
		super();
	}

	/**
	 * Constructeur prenant en paramètre le message à afficher.
	 * 
	 * @param message
	 *            Message à afficher.
	 */
	public JediConnectionException(String message) {
		super(message);
	}

}// fin de l'interface