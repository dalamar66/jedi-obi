package com.ldap.jedi;

/**
 * File : JediConnectionManager.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-04 
 * Modification date : 2010-03-04
 */

import java.util.*;

/**
 * Interface permettant de gérer les connexions
 * 
 * @author HUMEAU Xavier
 * @version Version 1.0
 */
interface JediConnectionManager {

	/**
	 * Méthode qui permet de récupérer une JediConnection.
	 * 
	 * @param alias
	 *            L'alias de la connexion.
	 * @return Le JediConnexion désiré.
	 * @throws JediConnectionException.
	 */
	public JediConnection getJediConnection(String alias) throws JediConnectionException;

	/**
	 * Méthode qui permet de spécifier les paramètres de connexion.
	 * 
	 * @param alias
	 *            L'alias de la connexion.
	 * @param parameters
	 *            Les paramètres de la connexion.
	 * @throws JediConnectionException.
	 */
	public void addConnectionProvider(String alias, Hashtable<String, String> parameters) throws JediException;

	/**
	 * Méthode qui permet de fermer une connexion.
	 * 
	 * @param alias
	 *            L'alias de la connexion à fermer.
	 * @param force
	 *            .
	 * @throws JediException.
	 */
	public void closeConnections(String alias, boolean force) throws JediConnectionException;

	/**
	 * Méthode qui permet de fermer toutes les connexions existantes.
	 * 
	 * @throws JediConnectionException.
	 */
	public void closeAllConnections() throws JediConnectionException;

	/**
	 * Méthode qui permet de connaitre le chemin racine d'une connexion donnée.
	 * 
	 * @param alias
	 *            L'alias de la connexion.
	 * @return Le chemin racine de la connexion sous forme de String.
	 * @throws JediConnectionException.
	 */
	public String getRootPath(String alias) throws JediException;

	/**
	 * Méthode qui permet de connaitre le chemin racine d'une connexion donnée.
	 * 
	 * @param alias
	 *            L'alias de la connexion.
	 * @return Le chemin racine de la connexion sous forme de JediPath.
	 * @throws JediConnectionException.
	 */
	public JediPath getJediRootPath(String alias) throws JediException;

	/**
	 * Méthode qui permet de connaitre l'hôte d'une connexion donnée.
	 * 
	 * @param alias
	 *            L'alias de la connexion.
	 * @return L'hôte de la connexion.
	 * @throws JediConnectionException.
	 */
	public String getLdapHost(String alias) throws JediException;

}// fin de l'interface