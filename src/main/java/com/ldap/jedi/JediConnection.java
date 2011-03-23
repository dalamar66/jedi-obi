package com.ldap.jedi;

/**
 * File : JediConnection.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-04 
 * Modification date : 2010-03-04
 */

import javax.naming.ldap.InitialLdapContext;

/**
 * Interface permettant de connaitre les param�tres de connexion.
 * 
 * @author HUMEAU Xavier
 * @version Version 1.0
 */
public interface JediConnection {

	/**
	 * M�thode qui permet de connaitre le contexte.
	 * 
	 * @return Le contexte de la connexion.
	 * @throws JediConnectionException.
	 */
	public InitialLdapContext getDirContext() throws JediConnectionException;

	/**
	 * Methode qui permet de relacher la connexion.
	 */
	public void doRelease();

	/**
	 * M�thode qui permet de savoir si la connexion est encore valide.
	 * 
	 * @return True si la connexion est valide, False sinon.
	 */
	public boolean isConnectionAlive();

	/**
	 * M�thode qui permet de r�cup�rer le chemin racine de la connexion.
	 * 
	 * @return Le chemin racine de la connexion.
	 */
	public String getRootPath();

	/**
	 * M�thode qui permet de connaitre l'alias de la connexion.
	 * 
	 * @return L'alias de la connexion.
	 */
	public String getAlias();

	/**
	 * M�thode qui permet de connaitre le JediServer de la connexion.
	 * 
	 * @return Le JediServer de la connexion.
	 */
	public JediServer getJediServer();

	/**
	 * M�thode permettant de connaitre le chemin racine de la connexion sous forme de JediPath.
	 * 
	 * @return Le chemin racine de la connexion.
	 */
	public JediPath getJediRootPath();

	/**
	 * M�thode permettant de connaitre l'h�te de la connexion.
	 * 
	 * @return L'h�te de la connexion.
	 * @throws JediException
	 */
	public String getLdapHost() throws JediException;

}// fin de l'interface