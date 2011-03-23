package com.ldap.dsml;

/**
 * File                 : DsmlAdapterException.java
 * Component            :
 * Version              : 1.0
 * Creation date        : 2011-03-17
 * Modification date    : 2011-03-17
 *
 * Classe générant les messages d'erreurs pour les exceptions DsmlAdapter.
 *
 * @author    HUMEAU Xavier
 * @version   Version 1.0
 */

public class DsmlAdapterException extends Exception {

	private static final long serialVersionUID = 1L;

	public DsmlAdapterException() {
        super();
    }

    public DsmlAdapterException(String message) {
        super(message);
    }

}