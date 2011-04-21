package com.ldap.jedi;

/**
 * File : JediObject.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-04 
 * Modification date : 2011-04-21
 */

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.ldap.InitialLdapContext;

/**
 * Classe g�rant les objets LDAP.
 * 
 * @author HUMEAU Xavier
 * @version Version 1.0
 */
public class JediObject {

	/**
	 * Variable permettant de connaitre les param�tres de la connexion
	 */
	private JediContextProvider jediContextProvider = null;

	/**
	 * Variable contenant le path du JediObject
	 */
	private JediPath path = null;

	/**
	 * Variable contenant l'alias de la connexion
	 */
	private String alias = null;

	/**
	 * Variable contenant le DN sous forme de String
	 */
	private String pathDN = null;

	/**
	 * Varaible contenant le RDN sous forme de String
	 */
	private String pathRDN = null;

	/**
	 * Varaible contenant le chemin du p�re sous forme de String
	 */
	private String pathNode = null;

	/**
	 * Variable contenant la liste d'attriubuts rattach�s au JediObject
	 */
	protected JediAttributeList jediAttributeList = null;

	/**
	 * Constructeur de JediObject � partir de son JediPath. Le JediPath pass� en param�tre est le DN de la feuille vers la racine, la feuille �tant le nom de
	 * l'objet � cr�er, la racine ne comprenant pas le chemin racine de la connexion.
	 * 
	 * @param alias
	 *            L'alias de la connexion.
	 * @param jediContextProvider
	 *            Le contexte de la connexion.
	 * @param path
	 *            Le DN de l'objet sous forme de JediPath.
	 * @throws JediException
	 *             Si l'alias est null ou vide, ou si le contexte est null, ou si le JediPath est null ou vide.
	 */
	public JediObject(String alias, JediContextProvider jediContextProvider, JediPath path) throws JediException {
		// Verification de la validite des parametres de la methode
		if (alias == null || alias.length() == 0 || jediContextProvider == null || path == null) {
			List<String> paramList = new ArrayList<String>();
			paramList.add(alias);
			paramList.add(Integer.toString(path.getPathSize()));

			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", paramList, this);

			throw new JediException("JediObject : JediObject(String, JediContextProvider, JediPath) : Param�tres d'initialisations incorrects");
		}

		// Affectation aux variables membres des param�tres
		this.alias = alias;
		this.path = path;

		this.jediContextProvider = jediContextProvider;

		this.pathDN = path.getDN();
		this.pathRDN = path.getRDN();
		this.pathNode = path.getNode();
	}

	/**
	 * Constructeur de JediObject � partir de son chemin. Le chemin pass� en param�tre est le chemin de la feuille vers la racine, la feuille �tant le nom de
	 * l'objet � cr�er, la racine ne comprenant pas le chemin racine de la connexion.
	 * 
	 * @param alias
	 * @param jediContextProvider
	 * @param path
	 * @throws JediException
	 */
	public JediObject(String alias, JediContextProvider jediContextProvider, String path) throws JediException {
		// Verification de la validite des parametres de la methode
		if (path == null) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", "", this);

			throw new JediException("JediObject : JediObject(String, JediContextProvider, JediPath) : le path est null");
		}
		
		new JediObject(alias, jediContextProvider, new JediPath(path));
	}

	/**
	 * Constructeur de JediObject � partir du JediObject p�re. Le JediPath du p�re pass� en param�tre est le DN de la feuille vers la racine, la feuille �tant
	 * le nom de l'objet p�re, la racine ne comprenant pas le chemin racine de la connexion. Le nom pass� en param�tre est le nom de l'objet � cr�er.
	 * 
	 * @param alias
	 *            L'alias de la connexion.
	 * @param jediContextProvider
	 *            Le contexte de la connexion.
	 * @param object
	 *            Le JediObject p�re du JediObject que l'on veut cr�er.
	 * @param name
	 *            Le nom du JediObject que l'on veut cr�er.
	 * @throws JediException
	 *             Si le contexte est null, ou si l'objet p�re est null, ou si le name est null ou vide.
	 */
	public JediObject(String alias, JediContextProvider jediContextProvider, JediObject object, String name) throws JediException {
		JediPath objectPath = new JediPath();
		JediPath temporalyPath = new JediPath();

		// Verification de la validite des parametres de la methode
		if (jediContextProvider == null || object == null || name == null || name.length() == 0) {
			List<String> paramList = new ArrayList<String>();
			paramList.add(alias);
			paramList.add(object.getPartialDNWihoutRac());
			paramList.add(name);

			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", paramList, this);

			throw new JediException("JediObject : JediObject(String, JediContextProvider, JediObject, String) : Param�tres d'initialisations incorrects");
		}

		// On recupere le JediPath du pere
		objectPath = object.getJediPartialDNWihoutRac();

		// Au JediPath temporaire on ajoute le nom de l'objet que l'on veut cr�er. On fait cela car dans le JediPath
		// le DN est stock� de la feuille vers la racine
		temporalyPath.addElement(name);

		// On ajoute ensuite au JediPath temporaire le DN du pere
		if (objectPath != null && objectPath.getPathSize() != 0) {
			for (int i = 0; i < objectPath.getPathSize(); i++) {
				temporalyPath.addElement(objectPath.get(i));
			}
		}

		// On affecte ensuite les valeur qu'il faut au variables membres
		this.alias = alias;
		this.path = temporalyPath;
		this.jediContextProvider = jediContextProvider;

		this.pathDN = path.getDN();
		this.pathRDN = path.getRDN();
		this.pathNode = path.getNode();
	}

	// *****************************************************************************************************
	//
	// METHODES DE PATH STRING
	//
	// *****************************************************************************************************

	/**
	 * M�thode qui permet d'obtenir le DN complet sous forme de String. Le DN renvoy� contient le chemin racine et l'h�te Ldap de la connexion.
	 * 
	 * @return Le DN complet de l'objet sous forme de String.
	 * @throws JediException.
	 */
	public String getCompleteDN() throws JediException {
		// A partir de l'alias on recupere l'hote LDAP de la connexion
		String ldapHost = jediContextProvider.getLdapHost(alias);
		// A partir de l'alias on recupere le chemin racine de la connexion
		String rootPath = jediContextProvider.getRootPath(alias);

		// On retourne sous forme de String le DN de l'objet
		if (rootPath == null || rootPath.length() == 0) {
			return (ldapHost + "/" + this.pathDN);
		} else {
			return (ldapHost + this.pathDN + JediUtil.SEPARATOR + rootPath);
		}
	}

	/**
	 * M�thode qui permet d'obtenir le DN complet sans l'hote sous forme de String. Le DN renvoy� contient le chemin racine sans l'h�te Ldap de la connexion.
	 * 
	 * @return Le DN complet sans l'hote de l'objet sous forme de String.
	 * @throws JediException.
	 */
	public String getPartialDN() throws JediException {
		// A partir de l'alias on recupere le chemin racine de la connexion
		String rootPath = jediContextProvider.getRootPath(alias);
		// On retourne sous forme de String le DN de l'objet

		if (rootPath == null || rootPath.length() == 0) {
			return this.pathDN;
		} else {
			// Si le rootPath n'est pas null alors on l'ajoute
			return (this.pathDN + JediUtil.SEPARATOR + rootPath);
		}
	}

	/**
	 * M�thode qui permet d'obtenir le DN partiel sous forme de String. Le DN renvoy� est relatif au chemin racine de la connexion.
	 * 
	 * @return Le DN partiel de l'objet sous forme de String.
	 * @throws JediException.
	 */
	public String getPartialDNWihoutRac() throws JediException {
		return this.pathDN;
	}

	/**
	 * M�thode qui permet d'obtenir le DN complet du pere de l'objet sous forme de String. Le DN renvoy� contient le chemin racine et l'h�te Ldap de la
	 * connexion.
	 * 
	 * @return Le DN complet du pere de l'objet sous forme de String.
	 * @throws JediException.
	 */
	public String getCompleteNode() throws JediException {
		// A partir de l'alias on recupere l'hote LDAP de la connexion
		String ldapHost = jediContextProvider.getLdapHost(alias);
		// A partir de l'alias on recupere le chemin racine de la connexion
		String rootPath = jediContextProvider.getRootPath(alias);

		// On retourne sous forme de String le DN du pere de l'objet
		if (this.pathNode == null || this.pathNode.length() == 0) {
			return (ldapHost + rootPath);
		} else {
			// Si le rootPath n'est pas null alors on l'ajoute
			return (ldapHost + this.pathNode + JediUtil.SEPARATOR + rootPath);
		}
	}

	public String getPartialNode() throws JediException {
		// A partir de l'alias on recupere le chemin racine de la connexion
		String rootPath = jediContextProvider.getRootPath(alias);
		// On retourne sous forme de String le DN de l'objet

		if (rootPath == null || rootPath.length() == 0) {
			return this.pathNode;
		} else {
			// Si le rootPath n'est pas null alors on l'ajoute
			return (this.pathNode + JediUtil.SEPARATOR + rootPath);
		}
	}

	/**
	 * M�thode qui permet d'obtenir le DN partiel du p�re de l'objet sous forme de String. Le DN renvoy� est relatif au chemin racine de la connexion.
	 * 
	 * @return Le DN partiel du p�re de l'objet sous forme de String.
	 * @throws JediException.
	 */
	public String getPartialNodeWihoutRac() throws JediException {
		return this.pathNode;
	}

	/**
	 * M�thode qui permet d'obtenir le RDN partiel sous forme de String.
	 * 
	 * @return Le RDN de l'objet sous forme de String.
	 * @throws JediException.
	 */
	public String getRDN() throws JediException {
		return this.pathRDN;
	}

	// *****************************************************************************************************
	//
	// METHODES DE PATH JEDI
	//
	// *****************************************************************************************************

	/**
	 * M�thode qui permet d'obtenir le DN complet sous forme de JediPath. Le DN renvoy� contient le chemin racine et l'h�te Ldap de la connexion.
	 * 
	 * @return Le DN complet de l'objet sous forme de JediPath.
	 * @throws JediException.
	 */
	public JediPath getJediCompleteDN() throws JediException {
		JediPath path = new JediPath(getPartialDN());

		// A partir de l'alias on recupere l'hote LDAP de la connexion
		String ldapHost = jediContextProvider.getLdapHost(alias);

		if (ldapHost != null && ldapHost.isEmpty() == false) {
			path.addElement(jediContextProvider.getLdapHost(alias));
		}

		return path;
	}

	/**
	 * M�thode qui permet d'obtenir le DN partiel sous forme de JediPath. Le DN renvoy� est relatif au chemin racine de la connexion.
	 * 
	 * @return Le DN partiel de l'objet sous forme de JediPath.
	 * @throws JediException.
	 */
	public JediPath getJediPartialDNWihoutRac() throws JediException {
		return this.path;
	}

	/**
	 * M�thode qui permet d'obtenir le DN complet du p�re de l'objet sous forme de JediPath. Le DN renvoy� contient le chemin racine et l'h�te Ldap de la
	 * connexion.
	 * 
	 * @return Le DN complet du p�re de l'objet sous forme de JediPath.
	 * @throws JediException.
	 */
	public JediPath getJediCompleteNode() throws JediException {
		JediPath path = new JediPath(getPartialNode());

		// A partir de l'alias on recupere l'hote LDAP de la connexion
		String ldapHost = jediContextProvider.getLdapHost(alias);

		if (ldapHost != null && ldapHost.isEmpty() == false) {
			path.addElement(jediContextProvider.getLdapHost(alias));
		}

		return path;
	}

	/**
	 * M�thode qui permet d'obtenir le DN partiel du p�re de l'objet sous forme de JediPath. Le DN renvoy� est relatif au chemin racine de la connexion.
	 * 
	 * @return Le DN partiel du p�re de l'objet sous forme de JediPath.
	 * @throws JediException.
	 */
	public JediPath getJediPartialNodeWihoutRac() throws JediException {
		return new JediPath(getPartialNodeWihoutRac());
	}

	/**
	 * M�thode qui permet d'obtenir le RDN partiel sous forme de JediPath.
	 * 
	 * @return Le RDN de l'objet sous forme de JediPath.
	 * @throws JediException.
	 */
	public JediPath getJediRDN() throws JediException {
		return new JediPath(this.pathRDN);
	}

	// *****************************************************************************************************
	//
	// METHODES SUR LES ATTRIBUTS
	//
	// *****************************************************************************************************

	/**
	 * M�thode qui retourne la liste d'attribut rattach�e au JediObject
	 * 
	 * @return La liste des attributs rattach�e au JediObject.
	 */
	public JediAttributeList getJediAttributeList() {
		return this.jediAttributeList;
	}

	/**
	 * M�thode qui attache une liste d'attribut au JediObject.
	 * 
	 * @param jediAttributeList
	 *            La liste que l'on rattache au JediObject.
	 */
	public void setJediAttributeList(JediAttributeList jediAttributeList) {
		this.jediAttributeList = jediAttributeList;
	}

	/**
	 * M�thode qui charge toute la liste des attributs. Attention cette liste n'est pas rattach�e au JediObject.
	 * 
	 * @return La liste des attributs du JediObject.
	 * @throws JediException.
	 * @throws JediConnectionException
	 *             Probl�me de connexion.
	 */
	public JediAttributeList loadAllAttribute() throws JediException, JediConnectionException {
		return loadAttributeList(null);
	}

	/**
	 * M�thode qui charge la liste des attributs demand�s. Si le tableau est vide c'est la liste de tous les attributs qui est renvoy�e. Attention cette liste
	 * n'est pas rattach�e au JediObject.
	 * 
	 * @param attributesToLoad
	 *            La liste des attributs � charger.
	 * @return La liste des attributs demand�e du JediObject.
	 * @throws JediException.
	 * @throws JediConnectionException
	 *             Probl�me de connexion.
	 */
	public JediAttributeList loadAttributeList(String[] attributesToLoad) throws JediException, JediConnectionException {
		// On essaie de recuperer une connexion
		final JediConnection jediConnection = jediContextProvider.getJediConnection(alias);
		final InitialLdapContext dirContext = jediConnection.getDirContext();

		Attributes attributes = null;
		JediAttributeList attributeList = null;

		String root = jediConnection.getRootPath();

		if (JediUtil.endWithPath(pathDN, root) && root.length() != 0) {
			pathDN = pathDN.substring(0, pathDN.toLowerCase().indexOf(root.toLowerCase()) - 1);
		}

		try {
			// Si le tableau est vide on charge tous les attributs
			if (attributesToLoad == null || attributesToLoad.length == 0) {
				attributes = dirContext.getAttributes(pathDN);
			}
			// Sinon on charge que les valeurs demand�es
			else {
				attributes = dirContext.getAttributes(pathDN, attributesToLoad);
			}
			// On cr�er l'attributeList que l'on va retourner
			attributeList = new JediAttributeList(attributes);
		} catch (Exception e) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_object_load_attributes_failed", "", this);

			throw new JediException("JediObject : loadAttributeList(String[]) : Requ�te �chou�e");
		} finally {
			// On relache la connexion a la base
			jediConnection.doRelease();
		}

		return attributeList;
	}

	/**
	 * M�thode qui fusionne la liste d'attributs pass�e en parametre avec la liste d'attributs du JediObject. Cette m�thode fusionne que les attributs et non
	 * les valeurs d'attributs. Si par exemple on a la liste d'attributs A, B, C et que l'on passe en param�tre de la m�thode la liste C, D alors le JediObject
	 * aura comme liste A, B, C, D Si C est un attribut multivalu� avec comme valeur originale a, b, c et qu'il a comme valeurs dans le param�tre x, y ,alors la
	 * fusion donnera x, y comme valeurs � C
	 * 
	 * @param jediAttributeList
	 *            La liste que l'on veut fusionner avec la liste du JediObject.
	 * @throws JediException
	 *             Si la liste d'attributs est incorrecte.
	 */
	@SuppressWarnings("unchecked")
	public void mergeJediAttributeList(JediAttributeList jediAttributeList) throws JediException {
		try {
			// Si le JediObject n'a pas de liste d'attribut alors on lui affecte comme liste d'attributs la liste
			// pass�e en parametre
			if (this.jediAttributeList == null) {
				this.jediAttributeList = jediAttributeList;
			}
			// Si le JediObject a une liste d'attributs
			else {
				// On recupere dans une NamingEnum la liste des attributs
				NamingEnumeration<JediAttribute> namingEnumeration = jediAttributeList.getAll();
				// Pour chaque attribut on le met a jour dans la liste des attributs du JediObject
				while (namingEnumeration.hasMore()) {
					this.jediAttributeList.put((JediAttribute) namingEnumeration.next());
				}// fin du while

				// On recupere dans une NamingEnum la liste des attributs
				List<JediAttribute> temp = jediAttributeList.getAllJediAttribute();

				if (temp != null && temp.isEmpty() == false) {
					// Pour chaque attribut on le met a jour dans la liste des attributs du JediObject
					for (JediAttribute jediAttribute : temp) {
						this.jediAttributeList.put(jediAttribute);
					}
				}
			}// fin du else
		} catch (NamingException nex) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_object_merge_attributes_failed", "", this);

			throw new JediException("JediObject : mergeJediAttributeList(JediAttributeList) : Liste d'attributs incorrecte");
		}
	}

	/**
	 * M�thode qui met a jour dans la base la liste des attributs. La m�thode ne touche pas aux attributs non referenc�s dans la liste.
	 * 
	 * @param jediAttributeList
	 *            La liste que l'on veut rattacher au JediObject.
	 * @throws JediException
	 *             La mise a jour a �chou�e.
	 * @throws JediConnectionException
	 *             Probl�me de connexion.
	 */
	public void updateAttributeList(JediAttributeList jediAttributeList) throws JediException, JediConnectionException {
		// On essaie de recuperer une connection a la base
		final JediConnection jediConnection = jediContextProvider.getJediConnection(alias);
		final InitialLdapContext dirContext = jediConnection.getDirContext();

		// La methode update remplace les attributs
		try {
			dirContext.modifyAttributes(this.getCompleteDN(), DirContext.REPLACE_ATTRIBUTE, jediAttributeList.getAttributes());
		} catch (javax.naming.NameAlreadyBoundException ex1) {
			return;
		} catch (Exception ex) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_object_update_attributes_failed", "", this);

			throw new JediException("JediObject : updateAttributeList(JediAttributeList) : Mise � jour �chou�e");
		} finally {
			// On relache la connexion a la base
			jediConnection.doRelease();
		}
	}

}// fin de la classe