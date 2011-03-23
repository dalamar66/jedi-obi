package com.ldap.jedi;

/**
 * File : JediConnectionImpl.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-04 
 * Modification date : 2010-03-04
 */

import javax.naming.directory.SearchControls;
import javax.naming.ldap.InitialLdapContext;

/**
 * Classe g�n�rant une impl�mentation de connexion.
 * 
 * @author HUMEAU Xavier
 * @version Version 1.0
 */
class JediConnectionImpl implements JediConnection {

	/**
	 * Contexte de l'impl�mantation de la connexion.
	 */
	protected InitialLdapContext dirContext = null;

	/**
	 * Variable permettant de savoir si l'impl�mantation de la connexion est utilis�e.
	 */
	protected boolean used = false;

	/**
	 * Variable permettant de connaitre le chemin racine de l'impl�mentation de la connexion.
	 */
	protected String rootPath = null;

	/**
	 * Variable permattant de connaitre l'alias de l'impl�mentation de la connexion.
	 */
	protected String alias = null;

	/**
	 * Variable permettant de connaitre le JediServer de l'impl�mentation de la connexion.
	 */
	protected JediServer jediServer = null;

	/**
	 * Constructeur prenant en param�tre un contexte, un chemin racine et un alias.
	 * 
	 * @param dirContext
	 *            Contexte de la connexion.
	 * @param rootPath
	 *            Chemin racine de la connexion.
	 * @param alias
	 *            Alias de la connexion.
	 */
	public JediConnectionImpl(InitialLdapContext dirContext, String rootPath, String alias) {
		this.alias = alias;
		this.rootPath = rootPath;
		this.dirContext = dirContext;
	}

	/**
	 * M�thode permettant de r�cup�rer le contexte de la connexion.
	 * 
	 * @return Le contexte de la connexion.
	 * @throws JediConnectionException
	 *             Si la connexion est relach�e ou si la connexion n'est pas effectu�e.
	 */
	public InitialLdapContext getDirContext() throws JediConnectionException {
		// Si la connexion est relach�e
		if (used == false) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_connection_error", "", this);
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_connexion_context_released", "", this);

			throw new JediConnectionException("JediConnectionImpl : getDirContext() : Connexion relach�e");
		}

		// Si la connexion n'est pas effectu�e
		if (dirContext == null) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_connection_error", "", this);
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_connexion_context_null", "", this);

			JediLog.log(JediLog.LOG_PRODUCTION, JediLog.ERROR, "jedi_msg_connection_error", "", this);
			JediLog.log(JediLog.LOG_PRODUCTION, JediLog.ERROR, "jedi_msg_connexion_context_null", "JediConnectionImpl : getDirContext()", this);
			throw new JediConnectionException("JediConnectionImpl : getDirContext() : Connexion non effectu�e");
		}

		// On retourne le contexte de la connexion
		return dirContext;
	}

	/**
	 * M�thode permettant de positionner le contexte de la connexion.
	 * 
	 * @param dirContext
	 *            Contexte de la connexion.
	 */
	void setDirContext(InitialLdapContext dirContext) {
		this.dirContext = dirContext;
	}

	/**
	 * M�thode qui relache la connexion de la base.
	 */
	public void doRelease() {
		used = false;
	}

	/**
	 * M�thode qui permet de r�cup�rer le chemin racine.
	 * 
	 * @return Le chemin racine.
	 */
	public String getRootPath() {
		return rootPath;
	}

	/**
	 * M�thode qui permet de r�cup�rer l'alias de la connexion.
	 * 
	 * @return L'alias de la connexion.
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * M�thode qui permet de r�cup�rer le JediServer de la connexion.
	 * 
	 * @return Le JediServer de la connexion.
	 */
	public JediServer getJediServer() {
		return jediServer;
	}

	/**
	 * M�thode qui permet de prendre la connexion.
	 * 
	 * @param used
	 *            Bool�en qui d�termine si l'on prend ou non la connexion.
	 */
	public void setUsed(boolean used) {
		this.used = used;
	}

	/**
	 * M�thode qui permet de savoir si la connexion est utilis�e.
	 * 
	 * @return True si la connexion est utilis�e, false sinon.
	 */
	public boolean getUsed() {
		return used;
	}

	/**
	 * M�thode qui permet de savoir si la connexion est encore valide.
	 * 
	 * @return True si la connexion est valide, false sinon.
	 */
	public boolean isConnectionAlive() {
		try {
			// Construction du controle pour la requete
			SearchControls control = new SearchControls();

			// Controle fixant le temps � ne pas d�passer
			control.setTimeLimit(JediUtil.getTimeLimit());

			// Controle fixant la profondeur de la recherche
			control.setSearchScope(SearchControls.ONELEVEL_SCOPE);

			// Recherche dans le contexte de la requ�te
			dirContext.search("", "(|(cn=*)(ou=*))", control);
		} catch (Throwable t) {
			try {
				dirContext.close();
			} catch (Exception ex) {
			}

			return false;
		}

		return true;
	}

	/**
	 * M�thode permettant de connaitre le chemin racine de la connexion sous forme de JediPath.
	 * 
	 * @return Le chemin racine de la connexion.
	 * @throws JediException.
	 */
	public JediPath getJediRootPath() {
		JediPath jediPath = null;

		try {
			jediPath = new JediPath(rootPath);
		} catch (Exception ex) {
			return null;
		}

		return jediPath;
	}

	/**
	 * M�thode permettant de connaitre l'h�te de la connexion.
	 * 
	 * @return L'h�te de la connexion.
	 * @throws JediException.
	 */
	public String getLdapHost() throws JediException {
		return this.jediServer.getLdapHost(this.alias);
	}

}// fin de la classe