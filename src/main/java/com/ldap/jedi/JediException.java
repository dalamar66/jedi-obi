package com.ldap.jedi;

/**
 * File : JediException.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-04 
 * Modification date : 2010-03-04
 */

/**
 * Classe générant les messages d'erreurs pour les exceptions Jedi.
 * 
 * @author HUMEAU Xavier
 * @version Version 1.0
 */
public class JediException extends Exception {

	private static final long serialVersionUID = -4333840510082819466L;

	/**
	 * Constructeur vide.
	 */
	public JediException() {
		super();
	}

	/**
	 * Constructeur prenant en paramètre le message à afficher.
	 * 
	 * @param message
	 *            Message à afficher.
	 */
	public JediException(String message) {
		super(message);
	}

}// fin de l'interface