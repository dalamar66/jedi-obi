package com.ldap.obi;

/**
 * File : OBISchemaAccessException.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-09
 * Modification date : 2010-03-09
 */

public class ObiSchemaAccessException extends Exception {

	private static final long serialVersionUID = -8782658764116526755L;

	/**
     * Exception qui n'affiche aucun message.
     */
    public ObiSchemaAccessException(){
        super();
    }

    /**
     * Exception qui affiche le message passé en paramètre.
     *
     * @param message Message à afficher.
     */
    public ObiSchemaAccessException(String message){
        super(message);
    }

}// fin de la classe
