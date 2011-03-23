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
 * Classe générant une implémentation de connexion.
 * 
 * @author HUMEAU Xavier
 * @version Version 1.0
 */
class JediConnectionImpl implements JediConnection {

	/**
	 * Contexte de l'implémantation de la connexion.
	 */
	protected InitialLdapContext dirContext = null;

	/**
	 * Variable permettant de savoir si l'implémantation de la connexion est utilisée.
	 */
	protected boolean used = false;

	/**
	 * Variable permettant de connaitre le chemin racine de l'implémentation de la connexion.
	 */
	protected String rootPath = null;

	/**
	 * Variable permattant de connaitre l'alias de l'implémentation de la connexion.
	 */
	protected String alias = null;

	/**
	 * Variable permettant de connaitre le JediServer de l'implémentation de la connexion.
	 */
	protected JediServer jediServer = null;

	/**
	 * Constructeur prenant en paramètre un contexte, un chemin racine et un alias.
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
	 * Méthode permettant de récupérer le contexte de la connexion.
	 * 
	 * @return Le contexte de la connexion.
	 * @throws JediConnectionException
	 *             Si la connexion est relachée ou si la connexion n'est pas effectuée.
	 */
	public InitialLdapContext getDirContext() throws JediConnectionException {
		// Si la connexion est relachée
		if (used == false) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_connection_error", "", this);
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_connexion_context_released", "", this);

			throw new JediConnectionException("JediConnectionImpl : getDirContext() : Connexion relachée");
		}

		// Si la connexion n'est pas effectuée
		if (dirContext == null) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_connection_error", "", this);
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_connexion_context_null", "", this);

			JediLog.log(JediLog.LOG_PRODUCTION, JediLog.ERROR, "jedi_msg_connection_error", "", this);
			JediLog.log(JediLog.LOG_PRODUCTION, JediLog.ERROR, "jedi_msg_connexion_context_null", "JediConnectionImpl : getDirContext()", this);
			throw new JediConnectionException("JediConnectionImpl : getDirContext() : Connexion non effectuée");
		}

		// On retourne le contexte de la connexion
		return dirContext;
	}

	/**
	 * Méthode permettant de positionner le contexte de la connexion.
	 * 
	 * @param dirContext
	 *            Contexte de la connexion.
	 */
	void setDirContext(InitialLdapContext dirContext) {
		this.dirContext = dirContext;
	}

	/**
	 * Méthode qui relache la connexion de la base.
	 */
	public void doRelease() {
		used = false;
	}

	/**
	 * Méthode qui permet de récupérer le chemin racine.
	 * 
	 * @return Le chemin racine.
	 */
	public String getRootPath() {
		return rootPath;
	}

	/**
	 * Méthode qui permet de récupérer l'alias de la connexion.
	 * 
	 * @return L'alias de la connexion.
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * Méthode qui permet de récupérer le JediServer de la connexion.
	 * 
	 * @return Le JediServer de la connexion.
	 */
	public JediServer getJediServer() {
		return jediServer;
	}

	/**
	 * Méthode qui permet de prendre la connexion.
	 * 
	 * @param used
	 *            Booléen qui détermine si l'on prend ou non la connexion.
	 */
	public void setUsed(boolean used) {
		this.used = used;
	}

	/**
	 * Méthode qui permet de savoir si la connexion est utilisée.
	 * 
	 * @return True si la connexion est utilisée, false sinon.
	 */
	public boolean getUsed() {
		return used;
	}

	/**
	 * Méthode qui permet de savoir si la connexion est encore valide.
	 * 
	 * @return True si la connexion est valide, false sinon.
	 */
	public boolean isConnectionAlive() {
		try {
			// Construction du controle pour la requete
			SearchControls control = new SearchControls();

			// Controle fixant le temps à ne pas dépasser
			control.setTimeLimit(JediUtil.getTimeLimit());

			// Controle fixant la profondeur de la recherche
			control.setSearchScope(SearchControls.ONELEVEL_SCOPE);

			// Recherche dans le contexte de la requête
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
	 * Méthode permettant de connaitre le chemin racine de la connexion sous forme de JediPath.
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
	 * Méthode permettant de connaitre l'hôte de la connexion.
	 * 
	 * @return L'hôte de la connexion.
	 * @throws JediException.
	 */
	public String getLdapHost() throws JediException {
		return this.jediServer.getLdapHost(this.alias);
	}

}// fin de la classe