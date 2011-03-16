package com.ldap.jedi;

/**
 * File : JediConnectionProvider.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-04 
 * Modification date : 2010-03-04
 */

/**
 * Interface permettant de connaitre les paramètres de connexion.
 * 
 * @author HUMEAU Xavier
 * @version Version 1.0
 */
interface JediContextProvider {

	/**
	 * Méthode permettant de prendre une connexion.
	 * 
	 * @param alias
	 *            Alias de la connexion que l'on désire.
	 * @return La connexion demandé.
	 * @throws JediException.
	 * @throws JediConnectionException.
	 */
	public JediConnection getJediConnection(String alias) throws JediException, JediConnectionException;

	/**
	 * Méthode permettant de connaitre le chemin racine de la connexion sous forme de String.
	 * 
	 * @param alias
	 *            Alias de la connexion.
	 * @return Le chemin racine de la connexion.
	 * @throws JediException.
	 */
	public String getRootPath(String alias) throws JediException;

	/**
	 * Méthode permettant de connaitre le chemin racine de la connexion sous forme de JediPath.
	 * 
	 * @param alias
	 *            Alias de la connexion.
	 * @return Le chemin racine de la connexion.
	 * @throws JediException.
	 */
	public JediPath getJediRootPath(String alias) throws JediException;

	/**
	 * Méthode permettant de connaitre l'hôte de la connexion.
	 * 
	 * @param alias
	 *            Alias de la connexion.
	 * @return L'hôte de la connexion.
	 * @throws JediException.
	 */
	public String getLdapHost(String alias) throws JediException;

}// fin de l'interface