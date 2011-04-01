package com.ldap.obi;

/**
 * File : ObiConnectionException.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-09
 * Modification date : 2010-03-09
 */

public class ObiConnectionException extends Exception {

	private static final long serialVersionUID = -538360839331587158L;

	/**
     * Exception qui n'affiche aucun message.
     */
    public ObiConnectionException(){
        super();
    }

    /**
     * Exception qui affiche le message passé en paramètre.
     *
     * @param message Message à afficher.
     */
    public ObiConnectionException(String message){
        super(message);
    }

}// fin de la classe