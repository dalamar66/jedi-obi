package com.ldap.obi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;

import com.ldap.jedi.JediConnectionException;
import com.ldap.jedi.JediException;
import com.ldap.jedi.JediFilter;
import com.ldap.jedi.JediLog;
import com.ldap.jedi.JediObject;
import com.ldap.jedi.JediPath;
import com.ldap.jedi.JediServer;
import com.ldap.obi.computer.ObiComputerService;
import com.ldap.obi.group.ObiGroupService;
import com.ldap.obi.group.ObiGroupUserService;
import com.ldap.obi.organizationalUnit.ObiOrganizationalUnitService;
import com.ldap.obi.personne.ObiPersonneService;
import com.ldap.obi.user.ObiUserService;

/**
 * File : ObiOne.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-09 
 * Modification date : 2011-04-21
 */

public class ObiOne {

	protected JediServer server = null;

	protected String domainRoot = null;

	protected String serverLogin = null;

	protected String serverPass = null;

	protected String serverName = null;
	
	protected String domainLdap = null;

	protected HashMap<String, String> mapServerName = null;

	// Liste des noms d'attributs du schéma (String) présents dans le Catalogue Global.
	protected Set<String> gcAttributeList = new HashSet<String>();

	private final static String ALIAS_GLOBAL_CATALOG = "ObiOneCatalog";
	private final static String ALIAS_DIRECTORY = "ObiOneDomain";
	private final static String ALIAS_SCHEMA_RACVIDE = "ObiOneSchemaRacVide";

	// *****************************************************************************************************
	//
	// METHODES DE CONNECTIONS
	//
	// *****************************************************************************************************

	public ObiOne(String domainLdap, String domainRoot, String domainUser, String domainPassword, String gcLdap, String gcUser, String gcPassword) throws ObiOneException, ObiSchemaAccessException, ObiConnectionException, ObiNamingException {
		JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "obi_msg_open_connexion_begin", "", this);

		// Affectation des paramètres aux variables membres
		this.server = new JediServer();

		this.domainRoot = ObiUtil.upperCasePath(domainRoot);

		this.domainLdap = domainLdap;
		
		try {
			JediPath jediPathDomain = new JediPath(ObiUtil.dcToDC(domainRoot));
			JediPath jediPathDomainUser = new JediPath(domainUser);

			this.serverLogin = jediPathDomainUser.getRDN();
			this.serverPass = domainPassword;

			// Création des 3 connexions (Une au domaine locale, l'autre au gc et la derniere au schema)
			server.addConnectionParameters(ALIAS_DIRECTORY, domainLdap, jediPathDomain, jediPathDomainUser, domainPassword);
			server.addConnectionParameters(ALIAS_SCHEMA_RACVIDE, domainLdap, new JediPath(new String[] { "" }), jediPathDomainUser, domainPassword);

			if (gcLdap != null && gcLdap.isEmpty() == false) {
				// On considere qu'il y a une tentative de connection au gc
				server.addConnectionParameters(ALIAS_GLOBAL_CATALOG, gcLdap, new JediPath(new String[] { "" }), new JediPath(gcUser), gcPassword);
			}
		} catch (JediException ex) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_connection_error", "", this);
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "obi_msg_open_connexion_failed", "", this);

			throw new ObiOneException("OBIOne : OBIOne(String, String, JediPath, String, String, JediPath, String) : Echec de création des connexions");
		}

		// Recuperation du rootDse
		List<String> listDSE = getListDse();

		// remplissage de la map des serveurs
		fillServerName(listDSE);

		// Remplissage de la liste des attributs répliqués dans le CG
		fillGcAttributeList(listDSE);

		try {
			server.getJediConnection(ALIAS_SCHEMA_RACVIDE).doRelease();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "obi_msg_open_connexion_success", "", this);
	}

	/**
	 * Methode permettant de recuperer le rootDse
	 * 
	 * @return
	 */

	@SuppressWarnings("rawtypes")
	private List<String> getListDse() {
		List<String> listDse = new ArrayList<String>();

		try {
			DirContext dx = server.getJediConnection(ALIAS_SCHEMA_RACVIDE).getDirContext();
			Attributes atts = dx.getAttributes("", new String[] { "namingcontexts" });
			NamingEnumeration ne = atts.getAll();

			listDse = new ArrayList<String>();
			while (ne.hasMoreElements()) {
				Attribute attribute = (Attribute) ne.nextElement();
				NamingEnumeration neDSE = attribute.getAll();
				while (neDSE.hasMoreElements()) {
					listDse.add((String) neDSE.nextElement());
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return listDse;
	}

	public void fillServerName(List<String> listDse) throws ObiOneException {
		mapServerName = new HashMap<String, String>();

		List<JediObject> listResult = new ArrayList<JediObject>();

		try {
			String rootPathSearch = null;
			for (String element : listDse) {
				if (element.indexOf("CN=Configuration,DC=") == 0) {
					rootPathSearch = element;
					break;
				}
			}

			List<String> attributesList = new ArrayList<String>();
			attributesList.add("dnsRoot");
			attributesList.add("cn");

			JediFilter jediFilter = new JediFilter();
			jediFilter.setAlias(ALIAS_SCHEMA_RACVIDE);
			jediFilter.setFilter("(&(objectClass=crossRef)(systemFlags=3))");
			jediFilter.setPath("CN=Partitions," + rootPathSearch);
			jediFilter.setAttributesList(attributesList);
			jediFilter.setSubtree(true);

			listResult = server.findByFilter(jediFilter);

			for (JediObject jediObject : listResult) {
				String dnsRoot = (String) jediObject.getJediAttributeList().get("dnsRoot").get();
				String cn = (String) jediObject.getJediAttributeList().get("cn").get();

				StringTokenizer st = new StringTokenizer(dnsRoot, ".");
				StringBuffer sb = new StringBuffer();

				while (st.hasMoreTokens()) {
					sb.append("DC=").append(st.nextToken()).append(",");
				}

				dnsRoot = sb.toString();

				if (dnsRoot != null && dnsRoot.length() > 0) {
					dnsRoot = dnsRoot.substring(0, dnsRoot.length() - 1);
				} else {
					dnsRoot = "";
				}

				if (domainRoot.equals(dnsRoot)) {
					serverName = cn;
				}

				mapServerName.put(dnsRoot, cn);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Remplit la liste des noms d'attributs présents dans le Catalogue Global.
	 * 
	 * @throws OBIConnectionException
	 *             En cas de problème de connexion pour charger les attributs.
	 * @throws OBINamingException
	 *             En cas de problème de stockage des attributs.
	 * @throws OBIOneException
	 *             Si une erreur interne sur le filtre intervient.
	 * @throws OBISchemaAccessException
	 *             Si le chemin d'accès au schéma est erroné.
	 */
	/**
	 * @throws ObiSchemaAccessException
	 * @throws ObiConnectionException
	 * @throws ObiNamingException
	 * @todo : deprecated
	 */
	protected void fillGcAttributeList(List<String> listDse) throws ObiOneException, ObiSchemaAccessException, ObiConnectionException, ObiNamingException {
		List<JediObject> listResult = new ArrayList<JediObject>();

		// Construction du chemin racine. Les attributs repliqués sont dans le schema de la base
		String rootPathSearch = null;
		for (String element : listDse) {
			if (element.indexOf("CN=Schema,CN=Configuration,DC=") == 0) {
				rootPathSearch = element;
				break;
			}
		}

		try {
			// Recuperation par filtre des objets dont les attributs sont repliqués dans un vecteur
			List<String> attributesList = new ArrayList<String>();
			attributesList.add("lDAPDisplayName");

			JediFilter jediFilter = new JediFilter();
			jediFilter.setAlias(ALIAS_SCHEMA_RACVIDE);
			jediFilter.setFilter("(&(objectClass=attributeSchema)(isMemberOfPartialAttributeSet=TRUE))");
			jediFilter.setPath(rootPathSearch);
			jediFilter.setAttributesList(attributesList);
			jediFilter.setSubtree(true);

			listResult = server.findByFilter(jediFilter);

			// Pour chaque objet recupéré on ne conserve que le nom de l'attribut que l'on stocke dans gcAttributeList
			for (JediObject jediObject : listResult) {
				this.gcAttributeList.add((String) jediObject.getJediAttributeList().get("lDAPDisplayName").get());
			}
		} catch (JediException ex) {
			if (ObiUtil.getWithTrace()) {
				JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);
				JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "obi_msg_open_connexion_failed", "", this);
			}

			throw new ObiOneException("OBIOne : fillGcAttributeList() : Erreur sur le filtre");
		} catch (JediConnectionException ex) {
			if (ObiUtil.getWithTrace()) {
				JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);
				JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "obi_msg_open_connexion_failed", "", this);
			}

			throw new ObiConnectionException("OBIOne : fillGcAttributeList() : Erreur de connection");
		} catch (javax.naming.NamingException ex) {
			if (ObiUtil.getWithTrace()) {
				JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);
				JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "obi_msg_open_connexion_failed", "", this);
			}

			throw new ObiNamingException("OBIOne : fillGcAttributeList() : Erreur lors du stockage des attributs");
		}
	}

	/**
	 * Indique si un nom d'attribut LDAP est présent dans le Catalogue Global.
	 * 
	 * @param attributeName
	 *            Nom de l'attribut.
	 * @return True si l'attribut est répliqué dans le Catalogue Global, false sinon.
	 * @throws OBIOneException
	 *             Si les paramètres ne sont pas valides.
	 */
	public boolean isAttributeInGc(String attributeName) throws ObiOneException {
		// Verification de la validite des parametres de la methode
		if (attributeName == null || attributeName.length() == 0) {
			List<String> paramList = new ArrayList<String>();
			paramList.add(attributeName);

			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_parameter_error", paramList, this);

			throw new ObiOneException("OBIOne : isAttributeInGc(String) : Paramètre incorrect");
		}

		return this.gcAttributeList.contains(attributeName);
	}

	public void closeConnections() throws ObiOneException {
		try {
			server.closeConnection();
		} catch (JediException e) {
			throw new ObiOneException("");
		} catch (JediConnectionException e) {
			throw new ObiOneException("");
		}
	}

	// *****************************************************************************************************
	//
	// GETTER ET SETTER
	//
	// *****************************************************************************************************

	public JediServer getServer() {
		return server;
	}

	public void setServer(JediServer server) {
		this.server = server;
	}

	public String getDomainRoot() {
		return domainRoot;
	}

	public void setDomainRoot(String domainRoot) {
		this.domainRoot = domainRoot;
	}

	public String getDomainLdap() {
		return domainLdap;
	}

	public String getServerLogin() {
		return serverLogin;
	}

	public void setServerLogin(String serverLogin) {
		this.serverLogin = serverLogin;
	}

	public String getServerPass() {
		return serverPass;
	}

	public void setServerPass(String serverPass) {
		this.serverPass = serverPass;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getGlobalCatalogAlias() {
		return ALIAS_GLOBAL_CATALOG;
	}

	public String getDirectoryAlias() {
		return ALIAS_DIRECTORY;
	}

	public String getSchemaRacVideAlias() {
		return ALIAS_SCHEMA_RACVIDE;
	}

	// *****************************************************************************************************
	//
	// METHODES DE FOURNISSEUR DE SERVICE
	//
	// *****************************************************************************************************

	public ObiPersonneService getPersonService() throws ObiServiceException {
		return new ObiPersonneService(this);
	}

	public ObiUserService getUserService() throws ObiServiceException {
		return new ObiUserService(this);
	}

	public ObiOrganizationalUnitService getOrganizationalUnitService() throws ObiServiceException {
		return new ObiOrganizationalUnitService(this);
	}

	public ObiComputerService getComputerService() throws ObiServiceException {
		return new ObiComputerService(this);
	}

	public ObiGroupService getGroupService() throws ObiServiceException {
		return new ObiGroupService(this);
	}

	public ObiGroupUserService getGroupUserService() throws ObiServiceException {
		return new ObiGroupUserService(this);
	}

}
