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
 * Classe gérant les objets LDAP.
 * 
 * @author HUMEAU Xavier
 * @version Version 1.0
 */
public class JediObject {

	/**
	 * Variable permettant de connaitre les paramètres de la connexion
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
	 * Varaible contenant le chemin du père sous forme de String
	 */
	private String pathNode = null;

	/**
	 * Variable contenant la liste d'attriubuts rattachés au JediObject
	 */
	protected JediAttributeList jediAttributeList = null;

	/**
	 * Constructeur de JediObject à partir de son JediPath. Le JediPath passé en paramètre est le DN de la feuille vers la racine, la feuille étant le nom de
	 * l'objet à créer, la racine ne comprenant pas le chemin racine de la connexion.
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

			throw new JediException("JediObject : JediObject(String, JediContextProvider, JediPath) : Paramètres d'initialisations incorrects");
		}

		// Affectation aux variables membres des paramètres
		this.alias = alias;
		this.path = path;

		this.jediContextProvider = jediContextProvider;

		this.pathDN = path.getDN();
		this.pathRDN = path.getRDN();
		this.pathNode = path.getNode();
	}

	/**
	 * Constructeur de JediObject à partir de son chemin. Le chemin passé en paramètre est le chemin de la feuille vers la racine, la feuille étant le nom de
	 * l'objet à créer, la racine ne comprenant pas le chemin racine de la connexion.
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
	 * Constructeur de JediObject à partir du JediObject père. Le JediPath du père passé en paramètre est le DN de la feuille vers la racine, la feuille étant
	 * le nom de l'objet père, la racine ne comprenant pas le chemin racine de la connexion. Le nom passé en paramètre est le nom de l'objet à créer.
	 * 
	 * @param alias
	 *            L'alias de la connexion.
	 * @param jediContextProvider
	 *            Le contexte de la connexion.
	 * @param object
	 *            Le JediObject père du JediObject que l'on veut créer.
	 * @param name
	 *            Le nom du JediObject que l'on veut créer.
	 * @throws JediException
	 *             Si le contexte est null, ou si l'objet père est null, ou si le name est null ou vide.
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

			throw new JediException("JediObject : JediObject(String, JediContextProvider, JediObject, String) : Paramètres d'initialisations incorrects");
		}

		// On recupere le JediPath du pere
		objectPath = object.getJediPartialDNWihoutRac();

		// Au JediPath temporaire on ajoute le nom de l'objet que l'on veut créer. On fait cela car dans le JediPath
		// le DN est stocké de la feuille vers la racine
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
	 * Méthode qui permet d'obtenir le DN complet sous forme de String. Le DN renvoyé contient le chemin racine et l'hôte Ldap de la connexion.
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
	 * Méthode qui permet d'obtenir le DN complet sans l'hote sous forme de String. Le DN renvoyé contient le chemin racine sans l'hôte Ldap de la connexion.
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
	 * Méthode qui permet d'obtenir le DN partiel sous forme de String. Le DN renvoyé est relatif au chemin racine de la connexion.
	 * 
	 * @return Le DN partiel de l'objet sous forme de String.
	 * @throws JediException.
	 */
	public String getPartialDNWihoutRac() throws JediException {
		return this.pathDN;
	}

	/**
	 * Méthode qui permet d'obtenir le DN complet du pere de l'objet sous forme de String. Le DN renvoyé contient le chemin racine et l'hôte Ldap de la
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
	 * Méthode qui permet d'obtenir le DN partiel du père de l'objet sous forme de String. Le DN renvoyé est relatif au chemin racine de la connexion.
	 * 
	 * @return Le DN partiel du père de l'objet sous forme de String.
	 * @throws JediException.
	 */
	public String getPartialNodeWihoutRac() throws JediException {
		return this.pathNode;
	}

	/**
	 * Méthode qui permet d'obtenir le RDN partiel sous forme de String.
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
	 * Méthode qui permet d'obtenir le DN complet sous forme de JediPath. Le DN renvoyé contient le chemin racine et l'hôte Ldap de la connexion.
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
	 * Méthode qui permet d'obtenir le DN partiel sous forme de JediPath. Le DN renvoyé est relatif au chemin racine de la connexion.
	 * 
	 * @return Le DN partiel de l'objet sous forme de JediPath.
	 * @throws JediException.
	 */
	public JediPath getJediPartialDNWihoutRac() throws JediException {
		return this.path;
	}

	/**
	 * Méthode qui permet d'obtenir le DN complet du père de l'objet sous forme de JediPath. Le DN renvoyé contient le chemin racine et l'hôte Ldap de la
	 * connexion.
	 * 
	 * @return Le DN complet du père de l'objet sous forme de JediPath.
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
	 * Méthode qui permet d'obtenir le DN partiel du père de l'objet sous forme de JediPath. Le DN renvoyé est relatif au chemin racine de la connexion.
	 * 
	 * @return Le DN partiel du père de l'objet sous forme de JediPath.
	 * @throws JediException.
	 */
	public JediPath getJediPartialNodeWihoutRac() throws JediException {
		return new JediPath(getPartialNodeWihoutRac());
	}

	/**
	 * Méthode qui permet d'obtenir le RDN partiel sous forme de JediPath.
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
	 * Méthode qui retourne la liste d'attribut rattachée au JediObject
	 * 
	 * @return La liste des attributs rattachée au JediObject.
	 */
	public JediAttributeList getJediAttributeList() {
		return this.jediAttributeList;
	}

	/**
	 * Méthode qui attache une liste d'attribut au JediObject.
	 * 
	 * @param jediAttributeList
	 *            La liste que l'on rattache au JediObject.
	 */
	public void setJediAttributeList(JediAttributeList jediAttributeList) {
		this.jediAttributeList = jediAttributeList;
	}

	/**
	 * Méthode qui charge toute la liste des attributs. Attention cette liste n'est pas rattachée au JediObject.
	 * 
	 * @return La liste des attributs du JediObject.
	 * @throws JediException.
	 * @throws JediConnectionException
	 *             Problème de connexion.
	 */
	public JediAttributeList loadAllAttribute() throws JediException, JediConnectionException {
		return loadAttributeList(null);
	}

	/**
	 * Méthode qui charge la liste des attributs demandés. Si le tableau est vide c'est la liste de tous les attributs qui est renvoyée. Attention cette liste
	 * n'est pas rattachée au JediObject.
	 * 
	 * @param attributesToLoad
	 *            La liste des attributs à charger.
	 * @return La liste des attributs demandée du JediObject.
	 * @throws JediException.
	 * @throws JediConnectionException
	 *             Problème de connexion.
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
			// Sinon on charge que les valeurs demandées
			else {
				attributes = dirContext.getAttributes(pathDN, attributesToLoad);
			}
			// On créer l'attributeList que l'on va retourner
			attributeList = new JediAttributeList(attributes);
		} catch (Exception e) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_object_load_attributes_failed", "", this);

			throw new JediException("JediObject : loadAttributeList(String[]) : Requête échouée");
		} finally {
			// On relache la connexion a la base
			jediConnection.doRelease();
		}

		return attributeList;
	}

	/**
	 * Méthode qui fusionne la liste d'attributs passée en parametre avec la liste d'attributs du JediObject. Cette méthode fusionne que les attributs et non
	 * les valeurs d'attributs. Si par exemple on a la liste d'attributs A, B, C et que l'on passe en paramètre de la méthode la liste C, D alors le JediObject
	 * aura comme liste A, B, C, D Si C est un attribut multivalué avec comme valeur originale a, b, c et qu'il a comme valeurs dans le paramètre x, y ,alors la
	 * fusion donnera x, y comme valeurs à C
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
			// passée en parametre
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
	 * Méthode qui met a jour dans la base la liste des attributs. La méthode ne touche pas aux attributs non referencés dans la liste.
	 * 
	 * @param jediAttributeList
	 *            La liste que l'on veut rattacher au JediObject.
	 * @throws JediException
	 *             La mise a jour a échouée.
	 * @throws JediConnectionException
	 *             Problème de connexion.
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

			throw new JediException("JediObject : updateAttributeList(JediAttributeList) : Mise à jour échouée");
		} finally {
			// On relache la connexion a la base
			jediConnection.doRelease();
		}
	}

}// fin de la classe