package com.ldap.jedi;

/**
 * File : JediConnectionProvider.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-04 
 * Modification date : 2010-03-04
 */

/**
 * Interface permettant de connaitre les param�tres de connexion.
 * 
 * @author HUMEAU Xavier
 * @version Version 1.0
 */
interface JediContextProvider {

	/**
	 * M�thode permettant de prendre une connexion.
	 * 
	 * @param alias
	 *            Alias de la connexion que l'on d�sire.
	 * @return La connexion demand�.
	 * @throws JediException.
	 * @throws JediConnectionException.
	 */
	public JediConnection getJediConnection(String alias) throws JediException, JediConnectionException;

	/**
	 * M�thode permettant de connaitre le chemin racine de la connexion sous forme de String.
	 * 
	 * @param alias
	 *            Alias de la connexion.
	 * @return Le chemin racine de la connexion.
	 * @throws JediException.
	 */
	public String getRootPath(String alias) throws JediException;

	/**
	 * M�thode permettant de connaitre le chemin racine de la connexion sous forme de JediPath.
	 * 
	 * @param alias
	 *            Alias de la connexion.
	 * @return Le chemin racine de la connexion.
	 * @throws JediException.
	 */
	public JediPath getJediRootPath(String alias) throws JediException;

	/**
	 * M�thode permettant de connaitre l'h�te de la connexion.
	 * 
	 * @param alias
	 *            Alias de la connexion.
	 * @return L'h�te de la connexion.
	 * @throws JediException.
	 */
	public String getLdapHost(String alias) throws JediException;

}// fin de l'interface