package com.ldap.obi;

/**
 * File : ObiNamingException.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-09
 * Modification date : 2010-03-09
 */

public class ObiNamingException extends Exception {

	private static final long serialVersionUID = -4787481907438159796L;

	/**
     * Exception qui n'affiche aucun message.
     */
    public ObiNamingException(){
        super();
    }

    /**
     * Exception qui affiche le message passé en paramètre.
     *
     * @param message Message à afficher.
     */
    public ObiNamingException(String message){
        super(message);
    }

}// fin de la classe