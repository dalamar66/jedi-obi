package com.ldap.jedi;

/**
 * File : JediConnectionManager.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-04 
 * Modification date : 2010-03-04
 */

import java.util.*;

/**
 * Interface permettant de g�rer les connexions
 * 
 * @author HUMEAU Xavier
 * @version Version 1.0
 */
interface JediConnectionManager {

	/**
	 * M�thode qui permet de r�cup�rer une JediConnection.
	 * 
	 * @param alias
	 *            L'alias de la connexion.
	 * @return Le JediConnexion d�sir�.
	 * @throws JediConnectionException.
	 */
	public JediConnection getJediConnection(String alias) throws JediConnectionException;

	/**
	 * M�thode qui permet de sp�cifier les param�tres de connexion.
	 * 
	 * @param alias
	 *            L'alias de la connexion.
	 * @param parameters
	 *            Les param�tres de la connexion.
	 * @throws JediConnectionException.
	 */
	public void addConnectionProvider(String alias, Hashtable<String, String> parameters) throws JediException;

	/**
	 * M�thode qui permet de fermer une connexion.
	 * 
	 * @param alias
	 *            L'alias de la connexion � fermer.
	 * @param force
	 *            .
	 * @throws JediException.
	 */
	public void closeConnections(String alias, boolean force) throws JediConnectionException;

	/**
	 * M�thode qui permet de fermer toutes les connexions existantes.
	 * 
	 * @throws JediConnectionException.
	 */
	public void closeAllConnections() throws JediConnectionException;

	/**
	 * M�thode qui permet de connaitre le chemin racine d'une connexion donn�e.
	 * 
	 * @param alias
	 *            L'alias de la connexion.
	 * @return Le chemin racine de la connexion sous forme de String.
	 * @throws JediConnectionException.
	 */
	public String getRootPath(String alias) throws JediException;

	/**
	 * M�thode qui permet de connaitre le chemin racine d'une connexion donn�e.
	 * 
	 * @param alias
	 *            L'alias de la connexion.
	 * @return Le chemin racine de la connexion sous forme de JediPath.
	 * @throws JediConnectionException.
	 */
	public JediPath getJediRootPath(String alias) throws JediException;

	/**
	 * M�thode qui permet de connaitre l'h�te d'une connexion donn�e.
	 * 
	 * @param alias
	 *            L'alias de la connexion.
	 * @return L'h�te de la connexion.
	 * @throws JediConnectionException.
	 */
	public String getLdapHost(String alias) throws JediException;

}// fin de l'interface