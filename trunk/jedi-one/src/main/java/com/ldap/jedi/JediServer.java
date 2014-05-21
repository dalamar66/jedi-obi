package com.ldap.jedi;

/**
 * File : JediServer.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-04 
 * Modification date : 2010-03-08
 */

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;

/**
 * Classe contenant l'ensemble des méthodes nécessaires pour le traitement des JediObject.
 * 
 * @author HUMEAU Xavier
 * @version Version 1.0
 */
public class JediServer implements JediContextProvider {

   protected JediConnectionManager jediCtxMgr = null;
   protected final static String[] emptyArrayString = new String[0];

   private static final Integer FILTER_TYPE_DN = 0;
   private static final Integer FILTER_TYPE_GUID = 1;
   private static final Integer FILTER_TYPE_FILTRE = 2;

   // *****************************************************************************************************
   //
   // METHODES DE CONNECTION
   //
   // *****************************************************************************************************

   public JediServer() {
	initJediServer();
   }

   /**
    * Initialisation du pool de connexion.
    */
   protected void initJediServer() {
	jediCtxMgr = new JediConnectionPool();
   }

   /**
    * Méthode qui permet de récupérer une connexion en spécifiant l'alias de celle-ci.
    * 
    * @param alias
    *           Alias de la connexion.
    * @return La JediConnection si possible.
    * @throws JediException
    * @throws JediConnectionException
    *            Si il y a un problème de connexion.
    */
   public JediConnection getJediConnection(String alias) throws JediException, JediConnectionException {
	// Verification de la validite des parametres de la methode
	if (alias == null || alias.length() == 0) {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", alias, this);
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_connection_failed", alias, this);

	   throw new JediException("JediServer : getJediConnection(String) : Paramètres d'initialisation incorrects");
	}

	try {
	   return jediCtxMgr.getJediConnection(alias);
	} catch (JediConnectionException e) {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_connection_failed", "", this);

	   throw new JediConnectionException("JediServer : getJediConnection(String) : Impossible de se connecter pour l'alias : " + alias);
	}
   }

   public void closeConnection() throws JediException, JediConnectionException {
	jediCtxMgr.closeAllConnections();
   }

   /**
    * Méthode qui permet de paramétrer une connexion à la base Ldap.
    * 
    * @param alias
    *           Alias de la connexion.
    * @param ldap
    *           La base Ldap à laquelle on veut se connecter.
    * @param rootPath
    *           Le chemin racine qui déterminera le contexte de la connexion.
    * @param user
    *           L'user avec lequel se connecte.
    * @param password
    *           Le mot de passe du user.
    * @throws JediException
    *            Si l'alias est null ou vide, ou si ldap est null ou vide, ou si le rootPath est null, ou si le user est null ou vide, ou si
    *            le password est
    *            null ou vide.
    */
   public void addConnectionParameters(String alias, String ldap, JediPath rootPath, JediPath user, String password) throws JediException {
	final Hashtable<String, String> connectionParameters = new Hashtable<String, String>(8, 0.75f);
	String rootPathDN = null;
	String userDN = null;

	JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "jedi_msg_connection_begin", "", this);

	// Verification de la validite des parametres de la methode
	if (alias == null || alias.length() == 0 || ldap == null || ldap.length() == 0 || rootPath == null || user == null
		|| user.getPathSize() == 0) {
	   List<String> paramList = new ArrayList<String>();
	   paramList.add(alias);
	   paramList.add(ldap);
	   paramList.add(Integer.toString(rootPath.getPathSize()));
	   paramList.add(password);

	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", paramList, this);
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_connection_failed", paramList, this);

	   throw new JediException(
		   "JediServer : addConnectionParameters(String, String, JediPath, JediPath, String) : paramètres d'initialisation incorrects");
	}

	// On récupère le rootPath et le user sous forme de String
	try {
	   rootPathDN = rootPath.getDN();
	   userDN = user.getDN();
	} catch (Exception e) {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "jedi_msg_connection_failed", "", this);

	   throw new JediException(
		   "JediServer : addConnectionParameters(String, String, JediPath, JediPath, String) : paramètres d'initialisation incorrects");
	}

	// Remplissage de la hashtable paramètrant la connexion à la base Ldap
	connectionParameters.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
	connectionParameters.put(Context.PROVIDER_URL, ldap + rootPathDN);
	connectionParameters.put(Context.SECURITY_AUTHENTICATION, "simple");
	connectionParameters.put(Context.SECURITY_PRINCIPAL, userDN);
	connectionParameters.put(Context.SECURITY_CREDENTIALS, password);
	connectionParameters.put(Context.AUTHORITATIVE, "true");
	connectionParameters.put("java.naming.ldap.attributes.binary",
		"objectGUID objectSid thumbnailPhoto oMObjectClass schemaIDGUID attributeSecurityGUID");
	connectionParameters.put("ROOTPATH", rootPath.getDN());
	connectionParameters.put("LDAPHOST", ldap);

	// On ajoute les paramètres de connexion au Manager
	jediCtxMgr.addConnectionProvider(alias, connectionParameters);

	JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "jedi_msg_connection_success", "", this);
   }

   /**
    * Méthode qui permet de récupérer le chemin de base de la connexion sous forme de String.
    * 
    * @param alias
    *           Alias de la connexion.
    * @return Le chemin de base de la connexion sous forme de String.
    * @throws JediException
    */
   public String getRootPath(String alias) throws JediException {
	return jediCtxMgr.getRootPath(alias);
   }

   /**
    * Méthode qui permet de récupérer le chemin de base de la connexion sous forme de JediPath.
    * 
    * @param alias
    *           Alias de la connexion.
    * @return Le chemin de base de la connexion sous forme de JediPath.
    * @throws JediException
    */
   public JediPath getJediRootPath(String alias) throws JediException {
	return jediCtxMgr.getJediRootPath(alias);
   }

   /**
    * Méthode qui permet de récupérer l'hôte Ldap de la connexion.
    * 
    * @param alias
    *           Alias de la connexion.
    * @return L'hôte Ldap de la connexion.
    * @throws JediException
    */
   public String getLdapHost(String alias) throws JediException {
	return jediCtxMgr.getLdapHost(alias);
   }

   // *****************************************************************************************************
   //
   // METHODES DE CREATE
   //
   // *****************************************************************************************************

   /**
    * Méthode qui construit une entrée dans la base Ldap à partir d'un JediObject.
    * 
    * @param alias
    *           Alias de la connexion.
    * @param jediObject
    *           L'objet à insérer dans la base Ldap.
    * @return L'objet créé.
    * @throws JediException
    *            Si l'alias est null ou vide, ou si le JediObject est null.
    * @throws JediConnectionException
    *            Si il y a un problème de connexion.
    */
   public JediObject createLdapEntry(String alias, JediObject jediObject) throws JediException, JediConnectionException {
	// Verification de la validite des parametres de la methode
	if (alias == null || alias.length() == 0 || jediObject == null) {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", alias, this);

	   throw new JediException("JediServer : createLdapEntry(String, JediObject) : Paramètres d'initialisations incorrects");
	}

	Attributes attributeList = null;
	JediPath dnPath = null;

	try {
	   // Recuperation du dn de l'objet
	   dnPath = jediObject.getJediPartialDNWihoutRac();

	   // Recuperation de la liste des attributs de l'objet
	   attributeList = jediObject.getJediAttributeList().getAttributes();
	} catch (NullPointerException npe) {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_object_load_attributes_failed", "", this);

	   throw new JediException("Pas d'attribut de renseigné");
	}

	// Création de l'objet dans la base
	return createLdapEntry(alias, dnPath, attributeList);
   }

   /**
    * Méthode qui construit une entrée dans la base Ldap en ayant le dn , l'attribut obligatoire objectClass et la liste des attributs de
    * l'objet
    * 
    * @param alias
    *           Alias de la connexion.
    * @param jediPath
    *           Le dn de l'objet à créer.
    * @param objectClass
    *           L'attribut objectClass obligatoire.
    * @param attributeList
    *           La liste des attributs de l'objet.
    * @return L'objet créé.
    * @throws JediException
    *            Si l'alias est vide ou null, ou si le jediPath est null ou vide, ou si objectclass est null, ou si attributeList est null.
    * @throws JediConnectionException
    *            Si il y a un problème de connexion.
    */
   public JediObject createLdapEntry(String alias, JediPath jediPath, JediAttribute objectClass, Attributes attributeList)
	   throws JediException, JediConnectionException {
	// Verification de la validite des parametres de la methode
	if (alias == null || alias.length() == 0 || jediPath == null || jediPath.getPathSize() == 0 || objectClass == null
		|| attributeList == null) {
	   List<String> paramList = new ArrayList<String>();
	   paramList.add(alias);
	   paramList.add(Integer.toString(jediPath.getPathSize()));
	   paramList.add(Integer.toString(attributeList.size()));

	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", paramList, this);

	   throw new JediException(
		   "JediServer : createLdapEntry(String, JediPath, JediAttribute, Attributes) : Paramètres d'initialisations incorrects");
	}

	// On cherche si objectClass est deja reference dans la liste d'attributs
	Attribute testObjectClass = attributeList.get("objectClass");

	// Si l'attribut objectClass n'est pas present dans la liste d'attributs alors on l'ajoute et on lance le traitement
	// On considere que objectClass ne doit etre renseigne qu'une fois pour que la creation soit exécutée
	if (testObjectClass == null) {
	   attributeList.put(objectClass);
	}

	return createLdapEntry(alias, jediPath, attributeList);
   }

   /**
    * Méthode qui créé une entrée dans la base Ldap à partir du dn et des attributs.
    * 
    * @param alias
    *           Alias de la connexion.
    * @param jediPath
    *           Le dn de l'objet à mettre en base.
    * @param attributeList
    *           La liste des attributs de l'objet.
    * @return L'objet créé.
    * @throws JediException
    *            Si l'alias est vide ou null, ou si le jediPath est null ou vide, ou si attributeList est null.
    * @throws JediConnectionException
    *            Si il y a un problème de connexion.
    */
   public JediObject createLdapEntry(String alias, JediPath jediPath, Attributes attributeList) throws JediException,
	   JediConnectionException {
	try {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "", "", this);
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "jedi_msg_creation_begin", " : " + jediPath.getDN(), this);
	} catch (Exception ex) {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "", "", this);
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "jedi_msg_creation_begin", " DN non affichable", this);
	}

	// Verification de la validite des parametres de la methode
	if (alias == null || alias.length() == 0 || jediPath == null || jediPath.getPathSize() == 0 || attributeList == null) {
	   List<String> paramList = new ArrayList<String>();
	   paramList.add(alias);
	   paramList.add(Integer.toString(jediPath.getPathSize()));
	   paramList.add(Integer.toString(attributeList.size()));

	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", paramList, this);
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "jedi_msg_creation_failed", "", this);

	   throw new JediException("JediServer : createLdapEntry(String, JediPath, Attributes) : Paramètres d'initialisations incorrects");
	}

	JediConnection jediConnection = null;

	try {
	   // On essaie de récupérer une connexion à la base Ldap
	   jediConnection = getJediConnection(alias);
	   InitialLdapContext dirContext = jediConnection.getDirContext();
	   String dnToString = jediPath.getDN();

	   // On regarde si l'objet existe deja dans la base
	   if (existInDir(alias, jediPath) == true) {
		JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_exist_in_dir", "", this);
		JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "jedi_msg_creation_failed", "", this);

		// On relache la connexion de la base Ldap
		jediConnection.doRelease();
		throw new JediException("JediServer : createLdapEntry(String, jediPath, Attributes) : Objet déjà existant");
	   } else {
		// On essaie de l'insérer dans la base
		dirContext.createSubcontext(dnToString, attributeList);
	   }
	} catch (Exception e) {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "jedi_msg_creation_failed", "", this);

	   throw new JediException("JediServer : createLdapEntry(String, jediPath, Attributes) : Erreur de création");
	} finally {
	   // On relache la connexion de la base Ldap
	   jediConnection.doRelease();
	}

	List<String> attributeListName = new ArrayList<String>();
	NamingEnumeration<String> namingAttributeList = attributeList.getIDs();

	// On transforme la Naming en tableau pour le filtre
	while (namingAttributeList.hasMoreElements()) {
	   attributeListName.add(namingAttributeList.nextElement());
	}

	JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "jedi_msg_creation_success", "", this);

	return getJediObject(alias, attributeListName, jediPath.getDN());
   }

   // *****************************************************************************************************
   //
   // METHODES DE DELETE
   //
   // *****************************************************************************************************

   /**
    * Méthode qui supprime une entrée dans la base Ldap à partir d'un JediObject.
    * 
    * @param alias
    *           Alias de la connexion.
    * @param jediObject
    *           L'objet à supprimer.
    * @throws JediException
    *            Si l'alias est null ou vide, ou si le jediObject est null.
    */
   public void deleteLdapEntry(String alias, JediObject jediObject) throws JediException {
	// Verification de la validite des parametres de la methode
	if (alias == null || alias.length() == 0 || jediObject == null) {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", alias, this);
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_suppression_failed", alias, this);

	   throw new JediException("JediServer : deleteLdapEntry(String, JediObject) : Paramètres d'initialisations incorrects");
	}

	// On supprime l'objet de la base
	deleteLdapEntry(alias, jediObject.getJediPartialDNWihoutRac());
   }

   /**
    * Méthode qui supprime une entrée à partir de son dn.
    * 
    * @param alias
    *           Alias de la connexion.
    * @param dn
    *           Le JediPath correspondant au dn.
    * @throws JediException
    *            Si l'alias est null ou vide, ou si le jediPath est null ou vide.
    */
   public void deleteLdapEntry(String alias, JediPath dn) throws JediException {
	JediConnection jediConnection = null;

	try {
	   String stringDn = dn.getDN();

	   try {
		JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "jedi_msg_suppression_begin", stringDn, this);
	   } catch (Exception ex) {
		JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "jedi_msg_suppression_begin", "DN non affichable", this);
	   }

	   // Si le dn du niveau contient le chemin racine on le supprime
	   if (JediUtil.endWithPath(stringDn, this.getJediConnection(alias).getRootPath())) {
		stringDn = stringDn.substring(0, stringDn.toLowerCase().indexOf(this.getJediConnection(alias).getRootPath().toLowerCase()) - 1);
	   }

	   // On essaie de récupérer une connexion à la base Ldap
	   jediConnection = getJediConnection(alias);
	   InitialLdapContext dirContext = jediConnection.getDirContext();

	   // On tente de supprimer l'objet en ayant récupérer son dn
	   dirContext.unbind(stringDn);
	} catch (Exception e) {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "jedi_msg_suppression_failed", "", this);

	   throw new JediException("JediServer : deleteLdapEntry(String, JediPath) : Erreur de suppression");
	} finally {
	   // On relache ensuite la connexion
	   jediConnection.doRelease();
	}

	JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "jedi_msg_suppression_success", "", this);
   }

   /**
    * Méthode qui supprime une entrée à partir de son GUID.
    * 
    * @param alias
    *           Alias de la connexion.
    * @param guid
    *           GUID de l'objet à supprimer.
    * @throws JediException
    *            , JediConnectionException
    */
   public void deleteLdapEntry(String alias, byte[] guid) throws JediException, JediConnectionException {
	JediFilter jediFilter = new JediFilter();
	jediFilter.setAlias(alias);
	jediFilter.setPath(this.getJediRootPath(alias).getDN());
	jediFilter.setAttributesList(null);
	jediFilter.setGuid(guid);

	List<JediObject> result = findByFilter(jediFilter);

	if (result != null && result.isEmpty() == false) {
	   deleteLdapEntry(alias, result.get(0));
	}
   }

   public void deleteRecursifLdapEntry(String alias, JediPath pathDn) throws JediException {
	JediConnection jediConnection = null;

	JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_suppression_recursif_begin", "", this);

	JediObject object = new JediObject(alias, this, pathDn);
	String dn = object.getPartialDNWihoutRac();

	try {
	   // On essaie de récupérer une connexion à la base Ldap
	   jediConnection = getJediConnection(alias);
	   InitialLdapContext dirContext = jediConnection.getDirContext();

	   List<JediObject> list = this.getChildren(alias, new JediPath(dn));

	   if (list == null || list.size() == 0) {
		dirContext.destroySubcontext(dn);
	   } else {
		for (JediObject jediObject : list) {
		   deleteRecursifLdapEntry(alias, new JediPath(jediObject.getPartialDNWihoutRac()));
		}
		dirContext.destroySubcontext(dn);
	   }
	} catch (Exception e) {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_suppression_recursif_failed", "", this);

	   throw new JediException("JediServer : deleteRecursifLdapEntry(String, JediPath) : Erreur de suppression");
	} finally {
	   // On relache ensuite la connexion
	   jediConnection.doRelease();
	}

	JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_suppression_recursif_success", "", this);
   }

   // *****************************************************************************************************
   //
   // METHODES DE RENAME
   //
   // *****************************************************************************************************

   /**
    * Méthode qui renomme une entrée Ldap en fournissant le dn de l'objet à renommer et en fournissant son nouveau nom.
    * 
    * @param alias
    *           Alias de la connexion.
    * @param jediObject
    *           Objet à renommer.
    * @param nrdn
    *           Le nouveau rdn de l'objet à renommer.
    * @throws JediException
    *            Si l'alias est null ou vide, ou si le jediObject est null, ou si le nrdn est null ou vide.
    */
   public void renameLdapEntry(String alias, JediObject jediObject, String nrdn) throws JediException {
	// Verification de la validite des parametres de la methode
	if (alias == null || alias.length() == 0 || jediObject == null || nrdn == null || nrdn.length() == 0) {
	   List<String> paramList = new ArrayList<String>();
	   paramList.add(alias);
	   paramList.add(nrdn);

	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", paramList, this);
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_rename_failed", paramList, this);

	   throw new JediException("JediServer : renameLdapEntry(String, JediObject, String) : Paramètres d'initialisations incorrects");
	}

	// On renomme l'objet dans la base
	renameLdapEntry(alias, jediObject.getJediPartialDNWihoutRac(), nrdn);
   }

   /**
    * Méthode qui renomme une entrée Ldap en fournissant le dn de l'objet à renommer et en fournissant son nouveau nom.
    * 
    * @param alias
    *           Alias de la connexion.
    * @param dn
    *           Le dn de l'objet à renommer.
    * @param rdn
    *           Le nouveau rdn de l'objet à renommer.
    * @throws JediException
    *            Si l'alias est null ou vide, ou si le dn est null ou vide , ou si le rdn est null ou vide.
    */
   public void renameLdapEntry(String alias, JediPath dn, String rdn) throws JediException {
	JediConnection jediConnection = null;
	InitialLdapContext dirContext = null;

	try {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "jedi_msg_rename_begin", dn.getDN() + " a pour nouveau RDN : " + rdn, this);
	} catch (Exception ex) {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "jedi_msg_rename_begin", "Nouveau RDN : " + rdn, this);
	}

	// Verification de la validite des parametres de la methode
	if (alias == null || alias.length() == 0 || dn == null || dn.getPathSize() == 0 || rdn == null || rdn.length() == 0) {
	   List<String> paramList = new ArrayList<String>();
	   paramList.add(alias);
	   paramList.add(Integer.toString(dn.getPathSize()));
	   paramList.add(rdn);

	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", paramList, this);
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "jedi_msg_rename_failed", "", this);

	   throw new JediException("JediServer : renameLdapEntry(String, JediPath, String) : Paramètres d'initialisations incorrects");
	}

	try {
	   // On essaie de réécupérer une connexion à la base Ldap
	   jediConnection = getJediConnection(alias);
	   dirContext = jediConnection.getDirContext();

	   // On teste s'il n'y a pas de caractères incompatibles avec le nouveau rdn
	   if (JediUtil.searchIncompatibility(rdn + JediUtil.SEPARATOR + dn.getNode())) {
		JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_path_consult_failed", "", this);
		JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "jedi_msg_rename_failed", "", this);

		throw new JediException("JediServer : renameLdapEntry(String, JediPath, String) : Caractères incompatibles");
	   }

	   // On renomme l'objet avec le nouveau rdn
	   dirContext.rename(dn.getDN(), rdn + JediUtil.SEPARATOR + dn.getNode());
	} catch (Exception e) {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "jedi_msg_rename_failed", "", this);

	   throw new JediException("JediServer : renameLdapEntry(String, JediPath, String) : Erreur de renommage");
	} finally {
	   // On relache enfin la connexion de la base
	   jediConnection.doRelease();
	}

	JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "jedi_msg_rename_success", "", this);
   }

   // *****************************************************************************************************
   //
   // METHODES DE MOVE
   //
   // *****************************************************************************************************

   /**
    * Méthode qui déplace un objet dans la base Ldap en ayant fournit son dn et son nouveau dn.
    * 
    * @param alias
    *           Alias de la connexion.
    * @param jediObject
    *           L'objet à déplacer.
    * @param jediPath
    *           Le dn du nouveau père de l'objet.
    * @throws JediException
    *            si l'alias est null ou vide, ou si le jediObject est null, ou si le jediPath est null ou vide.
    */
   public void moveLdapEntry(String alias, JediObject jediObject, JediPath jediPath) throws JediException {
	// Verification de la validite des parametres de la methode
	if (alias == null || alias.length() == 0 || jediObject == null) {
	   List<String> paramList = new ArrayList<String>();
	   paramList.add(alias);
	   paramList.add(Integer.toString(jediPath.getPathSize()));

	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", paramList, this);
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_moving_failed", "", this);

	   throw new JediException("JediServer : moveLdapEntry(String, JediObject, jediPath) : Paramètres d'initialisations incorrects");
	}

	// On deplace l'objet dans la base
	moveLdapEntry(alias, jediObject.getJediPartialDNWihoutRac(), jediPath);
   }

   /**
    * Méthode qui déplace un objet au sein de la base Ldap en fournissant son ancien et nouveau dn.
    * 
    * @param alias
    *           Alias de la connexion.
    * @param dn
    *           Le dn de l'objet à déplacer.
    * @param ndn
    *           Le dn du nouveau père de l'objet en question.
    * @throws JediException
    *            Si l'alias est null ou vide, ou si le dn est null ou vide, ou si le ndn est null ou vide.
    */
   public void moveLdapEntry(String alias, JediPath dn, JediPath ndn) throws JediException {
	JediConnection jediConnection = null;
	InitialLdapContext dirContext = null;
	String newPath = null;

	try {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "jedi_msg_moving_begin", ndn.getDN(), this);
	} catch (Exception ex) {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "jedi_msg_moving_begin", "DN non affichable", this);
	}

	// Verification de la validite des parametres de la methode
	if (alias == null || alias.length() == 0 || dn == null || dn.getPathSize() == 0) {
	   List<String> paramList = new ArrayList<String>();
	   paramList.add(alias);
	   paramList.add(dn.getDN());
	   paramList.add(ndn.getDN());

	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", paramList, this);
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_moving_failed", "", this);

	   throw new JediException("JediServer : moveLdapEntry(String, JediPath, JediPath) : Paramètres d'initialisations incorrects");
	}

	// Nouveau path de l'objet : On concatene le nom de l'objet au chemin du nouveau père de celui-ci
	if (ndn == null || (ndn.getPathSize() == 1 && ndn.get(0).length() == 0) || ndn.getPathSize() == 0) {
	   newPath = dn.getRDN();
	} else {
	   newPath = dn.getRDN() + JediUtil.SEPARATOR + ndn.getDN();
	}

	try {
	   // On essaie de récuperer une connexion a la base Ldap
	   jediConnection = getJediConnection(alias);
	   dirContext = jediConnection.getDirContext();

	   // On regarde s'il y a des caractères incompatibles dans le nouveau dn
	   if (JediUtil.searchIncompatibility(newPath)) {
		JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_path_consult_failed", "", this);
		JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_moving_failed", "", this);

		throw new JediException("JediServer : moveLdapEntry(String, JediPath, JediPath) : Caractères incompatibles");
	   }

	   // On effectue le déplacement au sein de la base Ldap
	   dirContext.rename(dn.getDN(), newPath);
	} catch (Exception je) {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_moving_failed", "", this);

	   throw new JediException("JediServer : moveLdapEntry(String, JediPath, JediPath) : Renommage impossible");
	} finally {
	   // On relache la connexion de la base Ldap
	   jediConnection.doRelease();
	}

	JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "jedi_msg_moving_success", "", this);
   }

   // *****************************************************************************************************
   //
   // METHODES DE FIND
   //
   // *****************************************************************************************************

   /**
    * Methode permettant d'effectuer tout type de recherche. JediFilter est controlé par la methode checkJediFilter.
    * 
    * @param jediFilter
    * @return List<JediObject>
    * @throws JediException
    * @throws JediConnectionException
    */
   public List<JediObject> findByFilter(JediFilter jediFilter) throws JediException, JediConnectionException {
	if (checkJediFilter(jediFilter) == false) {
	   throw new JediException();
	}

	// Chargement du tri
	Set<JediObject> jediObjectSet = null;
	Comparator<JediObject> comp = jediFilter.getSorted();
	if (comp != null) {
	   jediObjectSet = new TreeSet<JediObject>(comp);
	} else {
	   jediObjectSet = new HashSet<JediObject>();
	}

	// Chargement de l'alias
	String alias = jediFilter.getAlias();

	// Chargement du pageSize
	int pageSize = 0;
	if (jediFilter.getPageSize() != null) {
	   pageSize = jediFilter.getPageSize();
	}

	// Chargement de l'attribut en cas de recherche approximative afin de l'ajouter
	// dans la liste des attributs a charger s'il n'y est pas.
	List<String> attributesList = jediFilter.getAttributesList();

	String attribute = jediFilter.getAttribute();
	if (attribute != null && attribute.length() != 0) {
	   // Si attributeList est null cela signifie que l'on charge tous les attributs.
	   if (attributesList != null && attributesList.contains(attribute) == false) {
		attributesList.add(attribute);
	   }
	}

	// Chargement des attributs
	String[] attributesTab;
	if (attributesList == null) {
	   attributesTab = null;
	} else if (attributesList.isEmpty() == true) {
	   attributesTab = new String[0];
	} else {
	   attributesTab = new String[attributesList.size()];
	   for (int i = 0; i < attributesList.size(); i++) {
		attributesTab[i] = attributesList.get(i);
	   }
	}

	// Recuperation du contexte
	final JediConnection jediConnection = getJediConnection(alias);
	final InitialLdapContext dirContext = jediConnection.getDirContext();

	// Construction du controle
	SearchControls control = new SearchControls();
	control.setTimeLimit(JediUtil.getTimeLimit());
	control.setReturningAttributes(attributesTab);

	byte[] guid = jediFilter.getGuid();
	String dn = jediFilter.getDn();
	Integer filterType = 0;

	// -------------------------------------------------
	// RECHERCHE PAR GUID
	// -------------------------------------------------
	// Le subtree est obligatoire
	if (guid != null && guid.length > 0) {
	   filterType = FILTER_TYPE_GUID;

	   control.setSearchScope(SearchControls.SUBTREE_SCOPE);
	}
	// -------------------------------------------------
	// RECHERCHE PAR DN
	// -------------------------------------------------
	// On construit un filtre du type : distinguishedName=...
	// Le subtree est obligatoire
	else if (dn != null && dn.length() != 0) {
	   filterType = FILTER_TYPE_DN;

	   control.setSearchScope(SearchControls.SUBTREE_SCOPE);

	   // Recherche dans le contexte de la requête
	   if (this.getRootPath(alias).length() == 0) {
		jediFilter.setFilter("distinguishedName=" + dn);
	   } else {
		// Si le dn du groupe contient le chemin racine on le supprime
		if (dn.indexOf(this.getRootPath(alias)) != -1) {
		   dn = dn.substring(0, dn.toLowerCase().indexOf(this.getRootPath(alias).toLowerCase()) - 1);
		}

		jediFilter.setFilter("distinguishedName=" + dn + JediUtil.SEPARATOR + this.getRootPath(alias));
	   }
	}
	// -------------------------------------------------
	// RECHERCHE PAR FILTRE
	// -------------------------------------------------
	else {
	   filterType = FILTER_TYPE_FILTRE;

	   // Chargement du subtree
	   if (jediFilter.getSubtree() != null && jediFilter.getSubtree().booleanValue() == true) {
		control.setSearchScope(SearchControls.SUBTREE_SCOPE);
	   }
	}

	int counter = 0;

	try {
	   // Recherche non paginée
	   if (pageSize == 0) {
		populateSet(filterType, jediFilter, control, counter, dirContext, jediObjectSet);
	   }
	   // Recherche paginée permettant de dépasser les limites du serveur
	   else {
		dirContext.setRequestControls(new Control[] { new javax.naming.ldap.PagedResultsControl(pageSize, Control.CRITICAL) });

		boolean exit = false;
		byte[] cookie = new byte[1];

		while ((cookie != null) && (cookie.length != 0) && !exit) {
		   exit = populateSet(filterType, jediFilter, control, counter, dirContext, jediObjectSet);

		   cookie = parseControls(dirContext.getResponseControls());

		   // Reactivation du pagedResultsControl
		   dirContext
			   .setRequestControls(new Control[] { new javax.naming.ldap.PagedResultsControl(pageSize, cookie, Control.CRITICAL) });
		}
	   }
	} catch (Exception e) {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_search_failed", "", this);

	   throw new JediException("Erreur sur la recherche");
	} finally {
	   jediConnection.doRelease();
	}

	return new ArrayList<JediObject>(jediObjectSet);
   }

   /**
    * Methode permettant de remplir la liste en fonction du filtre passé
    * 
    * @param filterType
    * @param jediFilter
    * @param control
    * @param counter
    * @param dirContext
    * @param result
    * @return
    * @throws JediException
    */
   private boolean populateSet(Integer filterType, JediFilter jediFilter, SearchControls control, int counter,
	   InitialLdapContext dirContext, Set<JediObject> jediObjectSet) throws JediException {
	String path = jediFilter.getPath();
	String filter = jediFilter.getFilter();
	String alias = jediFilter.getAlias();

	int limitation = 0;
	if (jediFilter.getLimitation() != null) {
	   limitation = jediFilter.getLimitation();
	}

	Integer indice = jediFilter.getIndice();
	boolean isApproximateSearch = false;
	if (indice != null && indice >= 0) {
	   isApproximateSearch = true;
	}

	AbstractStringMetric metric = jediFilter.getMetric();
	String approximation = jediFilter.getApproximation();
	String attribute = jediFilter.getAttribute();

	NamingEnumeration<SearchResult> answer = null;

	try {
	   // -------------------------------------------------
	   // RECHERCHE PAR GUID
	   // -------------------------------------------------
	   if (filterType.intValue() == FILTER_TYPE_GUID.intValue()) {
		Object[] filterArgs = new Object[1];
		filterArgs[0] = jediFilter.getGuid();
		answer = dirContext.search(path, "objectGuid={0}", filterArgs, control);
	   }
	   // -------------------------------------------------
	   // RECHERCHE PAR DN
	   // -------------------------------------------------
	   else if (filterType.intValue() == FILTER_TYPE_DN.intValue()) {
		answer = dirContext.search("", filter, control);
	   }
	   // -------------------------------------------------
	   // RECHERCHE PAR FILTRE
	   // -------------------------------------------------
	   else {
		answer = dirContext.search(path, filter, control);
	   }

	   if (answer != null) {
		while (answer.hasMoreElements()) {
		   SearchResult searchResult = (SearchResult) answer.nextElement();

		   JediPath pathTemp = null;

		   if (path == null || path.length() == 0) {
			pathTemp = new JediPath(searchResult.getName());
		   } else {
			pathTemp = new JediPath(searchResult.getName() + JediUtil.SEPARATOR + path);
		   }

		   JediObject jediObjectTemp = new JediObject(alias, this, pathTemp);

		   // Affectation des attributs demandés au JediObject
		   jediObjectTemp.setJediAttributeList(new JediAttributeList(searchResult.getAttributes()));

		   if (isApproximateSearch) {
			String attributeValue = (String) jediObjectTemp.getJediAttributeList().get(attribute).get();

			if (attributeValue != null && isMatching(attributeValue, approximation, metric, indice)) {
			   counter++;
			   jediObjectSet.add(jediObjectTemp);
			}
		   } else {
			counter++;
			jediObjectSet.add(jediObjectTemp);
		   }

		   if (limitation > 0 && counter == limitation) {
			return true;
		   }
		}// fin du while
	   }

	   return false;
	} catch (Exception ex) {
	   throw new JediException("Erreur sur la recherche");
	}
   }

   private static byte[] parseControls(Control[] controls) throws NamingException {
	byte[] cookie = null;

	if (controls != null) {
	   for (int i = 0; i < controls.length; i++) {
		if (controls[i] instanceof javax.naming.ldap.PagedResultsResponseControl) {
		   javax.naming.ldap.PagedResultsResponseControl prrc = (javax.naming.ldap.PagedResultsResponseControl) controls[i];
		   cookie = prrc.getCookie();
		} else {
		   // Handle other response controls (if any)
		}
	   }
	}

	return (cookie == null) ? new byte[0] : cookie;
   }// Fin de la methode

   /**
    * Methode permettant d'effectuer les controles des parametres du filtre.
    * 
    * @param jediFilter
    * @return
    * @throws JediException
    * @throws JediConnectionException
    */
   private boolean checkJediFilter(JediFilter jediFilter) throws JediException, JediConnectionException {
	String alias = jediFilter.getAlias();
	String approximation = jediFilter.getApproximation();
	String attribute = jediFilter.getAttribute();
	String dn = jediFilter.getDn();
	String filter = jediFilter.getFilter();
	byte[] guid = jediFilter.getGuid();
	Integer indice = jediFilter.getIndice();
	Integer limitation = jediFilter.getLimitation();
	AbstractStringMetric metric = jediFilter.getMetric();
	Integer pageSize = jediFilter.getPageSize();
	String path = jediFilter.getPath();
	Comparator<JediObject> sorted = jediFilter.getSorted();
	Boolean subtree = jediFilter.getSubtree();

	// Si alias n'est pas rensigné alors on a une erreur
	if (alias == null || alias.isEmpty()) {
	   return false;
	}

	// Si on fait une recherche par guid
	if (guid != null) {
	   // ... le guid doit etre correctement renseigné
	   if (guid.length == 0) {
		return false;
	   }

	   // ... le chemin de recherche doit etre correctement renseigné
	   if (path == null) {
		return false;
	   }

	   // ... tous les autres attributs ne doivent pas etre renseignés sauf la liste des attributs
	   if (approximation != null || attribute != null || dn != null || filter != null || indice != null || limitation != null
		   || metric != null || pageSize != null || sorted != null || subtree != null) {
		return false;
	   }
	}
	// Si on fait une recherche par dn
	else if (dn != null) {
	   // ... le dn doit etre correctement renseigné
	   if (dn.trim().length() == 0) {
		return false;
	   }

	   // ... tous les autres attributs ne doivent pas etre renseignés sauf la liste des attributs
	   if (approximation != null || attribute != null || filter != null || guid != null || indice != null || limitation != null
		   || metric != null || pageSize != null || path != null || sorted != null || subtree != null) {
		return false;
	   }
	}
	// Si on fait une recherche par filtre
	else if (filter != null) {
	   // ... le filtre doit etre correctement renseigné
	   if (filter.trim().length() == 0) {
		return false;
	   }

	   // ... le chemin de recherche doit etre correctement renseigné
	   if (path == null) {
		return false;
	   }

	   // ... le guid et le dn ne doivent pas etre renseignés
	   if (guid != null || dn != null) {
		return false;
	   }

	   // ... la pagination doit etre comprise entre 1 et 1000
	   if (pageSize != null && (pageSize < 1 || pageSize > 1000)) {
		return false;
	   }

	   // ... la limitation doit etre un chiffre positif
	   if (limitation != null && limitation < 1) {
		return false;
	   }

	   // ... tous les parametres d'approximation doivent etre renseignés
	   if (approximation != null) {
		if (indice == null || attribute == null || metric == null) {
		   return false;
		}
	   }
	   // ... ou aucun
	   else {
		if (indice != null || attribute != null || metric != null) {
		   return false;
		}
	   }

	   // ... la valeur d'approximation doit etre renseignée
	   if (approximation != null && approximation.isEmpty()) {
		return false;
	   }

	   // ... l'indice doit etre compris entre 0 et 100
	   if (indice != null && (indice < 0 || indice > 100)) {
		return false;
	   }
	}
	// On n'a pas reussi a determiner le type de recherche
	else {
	   return false;
	}

	return true;
   }

   /**
    * Methode permettant de savoir si 2 chaines matchs approximativement
    * 
    * @param chaine1
    * @param chaine2
    * @param metric
    * @param indice
    * @return
    */
   private boolean isMatching(String chaine1, String chaine2, AbstractStringMetric metric, Integer indice) {
	float res = metric.getSimilarity(chaine1.toUpperCase(), chaine2.toUpperCase());

	if (res >= (new Float(indice) / 100)) {
	   return true;
	} else {
	   return false;
	}
   }

   // *****************************************************************************************************
   //
   // METHODES DE PARENTEE
   //
   // *****************************************************************************************************

   /**
    * Methode qui permet de récupérer un vecteur de JediObject, chacun étant un fils de l'objet dont le dn est passé en paramètre.
    * 
    * @param alias
    *           Alias de la connexion.
    * @param jediObject
    *           Objet dont on veut récupérer tous les fils.
    * @throws JediException
    *            Si l'alias est null ou vide, ou si le jediObject est null.
    */
   public List<JediObject> getChildren(String alias, JediObject jediObject) throws JediException {
	// Verification de la validite des parametres de la methode
	if (alias == null || alias.length() == 0 || jediObject == null) {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", alias, this);
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_child_consult_failed", alias, this);

	   throw new JediException("JediServer : getChildren (String, JediObject) : Paramètres d'initialisations incorrects");
	}

	// On recupere les fils de l'objet
	return getChildren(alias, jediObject.getJediPartialDNWihoutRac());
   }

   /**
    * Methode qui permet de récupérer un vecteur de JediObject, chacun étant un fils de l'objet dont le dn est passé en paramètre.
    * 
    * @param alias
    *           Alias de la connexion.
    * @param dn
    *           Le dn de l'objet dont on veut récupérer tous les fils.
    * @return Vecteur de fils de l'objet spécifié.
    * @throws JediException
    *            Si l'alias est null ou vide, ou si le dn est null.
    */
   public List<JediObject> getChildren(String alias, JediPath dn) throws JediException {
	List<JediObject> result = new ArrayList<JediObject>();
	NamingEnumeration<NameClassPair> list = null;
	JediConnection jediConnection = null;
	InitialLdapContext dirContext = null;
	NameClassPair listElement = null;
	JediObject childObject = null;
	String listElementName = null;
	JediPath childPath = null;

	// Verification de la validite des parametres de la methode
	if (alias == null || alias.length() == 0 || dn == null) {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", alias, this);
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_child_consult_failed", alias, this);

	   throw new JediException("JediServer : getChildren (String, JediPath) : Paramètres d'initialisations incorrects");
	}

	try {
	   // On recupere une connexion a la base Ldap
	   jediConnection = getJediConnection(alias);
	   dirContext = jediConnection.getDirContext();

	   // On recupere sous forme de NamingEnumeration la liste des fils de l'objet en passant le dn complet du pere
	   list = dirContext.list(this.getLdapHost(alias) + dn.getDN() + JediUtil.SEPARATOR + this.getRootPath(alias));

	   // Si l'objet a des fils
	   if (list != null) {
		// Tant que l'objet a des fils on les stockes dans la liste
		while (list.hasMore()) {
		   listElement = (NameClassPair) list.next();

		   // On recupere le nom de l'objet fils
		   listElementName = listElement.getName();

		   // On cree le nouvel objet fils
		   childPath = new JediPath(dn.getDN());
		   childPath.addElement(listElementName);
		   childObject = new JediObject(alias, this, childPath);

		   // On stocke le nouvel objet fils dans la liste
		   result.add(childObject);
		}// fin du while
	   }// fin du if
	} catch (Exception e) {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_child_consult_failed", "", this);

	   throw new JediException("JediServer : getChildren (String, JediPath) : : Erreur de renommage");
	} finally {
	   // On relache la connexion de la base Ldap
	   jediConnection.doRelease();
	}

	return result;
   }

   /**
    * Methode qui permet de récupérer sous forme de JediObject le fils d'un objet.
    * 
    * @param alias
    *           Alias de la connexion.
    * @param jediPath
    *           Le dn de l'objet père.
    * @param childName
    *           Le nom de l'objet fils que l'on veut récupérer.
    * @return Le fils désiré sous forme de JediObject.
    * @throws JediException
    *            Si l'alias est null ou vide, ou si le jediPath est null, ou si le childName est null ou vide.
    */
   public JediObject getChild(String alias, JediPath jediPath, String childName) throws JediException {
	NamingEnumeration<NameClassPair> list = null;
	InitialLdapContext dirContext = null;
	JediConnection jediConnection = null;
	NameClassPair listElement = null;
	JediObject childObject = null;
	String listElementName = null;

	// Verification de la validite des parametres de la methode
	if (alias == null || alias.length() == 0 || jediPath == null || childName == null || childName.length() == 0) {
	   List<String> paramList = new ArrayList<String>();
	   paramList.add(alias);
	   paramList.add(Integer.toString(jediPath.getPathSize()));
	   paramList.add(childName);

	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", paramList, this);
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_child_consult_failed", paramList, this);

	   throw new JediException("JediServer : getChild (String, JediPath, String) : Paramètres d'initialisations incorrects");
	}

	try {
	   // On recupere une connexion a la base Ldap
	   jediConnection = getJediConnection(alias);
	   dirContext = jediConnection.getDirContext();

	   // On recupere sous forme de NamingEnumeration la liste des objets fils
	   list = dirContext.list(this.getLdapHost(alias) + jediPath + JediUtil.SEPARATOR + this.getRootPath(alias));

	   // Si l'objet a des fils
	   if (list != null) {
		// On cherche dans la liste le fils en question
		while (list.hasMore()) {
		   listElement = (NameClassPair) list.next();

		   // On recupere le nom du fils
		   listElementName = listElement.getName();

		   // On regarde s'il correspond au fils que l'on desire
		   if (listElementName.equalsIgnoreCase(childName)) {
			jediPath.addElement(childName);

			// On construit enfin le JediObject fils desire
			childObject = new JediObject(alias, this, jediPath);
		   }// fin du if
		}// fin du while
	   }// fin du if
	} catch (Exception e) {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_child_consult_failed", "", this);

	   throw new JediException("JediServer : getChild (String, JediPath, String) : Erreur sur la recherche");
	} finally {
	   // On relache la connexion de la base Ldap
	   jediConnection.doRelease();
	}

	return childObject;
   }// fin de la methode

   /**
    * Methode qui permet de récupérer sous forme de JediObject le fils d'un objet.
    * 
    * @param alias
    *           Alias de la connexion.
    * @param jediObject
    *           L'objet père.
    * @param childName
    *           Le nom de l'objet fils que l'on veut récupérer.
    * @return Le fils désiré sous forme de JediObject.
    * @throws JediException
    *            Si l'alias est null ou vide, ou si le jediObject est null, ou si le childName est null ou vide.
    */
   public JediObject getChild(String alias, JediObject jediObject, String childName) throws JediException {
	// Verification de la validite des parametres de la methode
	if (alias == null || alias.length() == 0 || jediObject == null || childName == null || childName.length() == 0) {
	   List<String> paramList = new ArrayList<String>();
	   paramList.add(alias);
	   paramList.add(childName);

	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", paramList, this);
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_child_consult_failed", paramList, this);

	   throw new JediException("JediServer : getChild (String, JediObject, String) : Paramètres d'initialisations incorrects");
	}

	// On recupere le fils de l'objet
	return getChild(alias, jediObject.getJediPartialDNWihoutRac(), childName);
   }

   /**
    * Methode qui permet de récupérer l'objet père de l'objet désiré.
    * 
    * @param alias
    *           Alias de la connexion.
    * @param jediObject
    *           Le JediObject dont on veut le père.
    * @return Le JediObject père de l'objet spécifié.
    * @throws JediException
    *            Si l'alias est null ou vide, ou si le jediObject est null.
    */
   public JediObject getJediObjectFather(String alias, JediObject jediObject) throws JediException {
	// Verification de la validite des parametres de la methode
	if (alias == null || alias.length() == 0 || jediObject == null) {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", alias, this);
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_father_consult_failed", alias, this);

	   throw new JediException("JediServer : getJediObjectFather (String, JediObject) : Paramètres d'initialisations incorrects");
	}

	// On recupere sousforme de JediPath le dn de l'objet dont on veut le pere
	return getJediObjectFather(alias, jediObject.getJediPartialDNWihoutRac());
   }

   /**
    * Methode qui permet de récupérer l'objet père de l'objet désiré.
    * 
    * @param alias
    *           Alias de la connexion.
    * @param jediPath
    *           Le JediPath de l'objet dont on veut le père.
    * @return Le jediObject père de l'objet spécifié.
    * @throws JediException
    *            Si l'alias est null ou vide, ou si le JediPath est null ou vide.
    */
   public JediObject getJediObjectFather(String alias, JediPath jediPath) throws JediException {
	JediPath temporalyPath = null;

	// Verification de la validite des parametres de la methode
	if (alias == null || alias.length() == 0 || jediPath == null || jediPath.getPathSize() == 0) {
	   List<String> paramList = new ArrayList<String>();
	   paramList.add(alias);
	   paramList.add(Integer.toString(jediPath.getPathSize()));

	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", paramList, this);
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_father_consult_failed", paramList, this);

	   throw new JediException("JediServer : getJediObjectFather (String, JediPath) : Paramètres d'initialisations incorrects");
	}
	temporalyPath = new JediPath();

	// Si l'objet dont on veut le pere n'est pas a la racine
	if (jediPath != null && jediPath.getPathSize() != 0) {
	   // On recupere tous les elements du JediPath sauf le premier qui est en effet la feuille du chemin
	   for (int i = 1; i < jediPath.getPathSize(); i++) {
		temporalyPath.addElement(jediPath.get(i));
	   }
	}

	// On construit le JediObject correspondant a l'objet pere
	return new JediObject(alias, this, temporalyPath);
   }

   // *****************************************************************************************************
   //
   // METHODES TOOLS
   //
   // *****************************************************************************************************

   /**
    * Méthode permettant de savoir si un objet existe en effectuant une recherche par le DN.
    * 
    * @param alias
    *           Alias de la connexion.
    * @param jediPath
    *           DN de l'objet cherché.
    * @return True si l'objet existe, false sinon.
    * @throws JediException
    *            Si l'alias est null ou vide, ou si le DN est null ou vide.
    * @throws JediConnectionException
    *            Problème de connexion.
    */
   public boolean existInDir(String alias, JediPath jediPath) throws JediException, JediConnectionException {
	JediFilter jediFilter = new JediFilter();
	jediFilter.setAlias(alias);
	jediFilter.setPath("");
	jediFilter.setAttributesList(new ArrayList<String>());
	jediFilter.setDn(jediPath.getDN());

	List<JediObject> result = findByFilter(jediFilter);

	if (result != null && result.isEmpty() == false) {
	   return true;
	} else {
	   return false;
	}
   }

   /**
    * Methode permettant de recuperer un JediObject avec les attributs voulus
    * 
    * @param alias
    * @param attributeList
    * @param dn
    * @return JediObject
    * @throws JediException
    * @throws JediConnectionException
    */
   public JediObject getJediObject(String alias, List<String> attributeList, String dn) throws JediException, JediConnectionException {
	JediFilter jediFilter = new JediFilter();
	jediFilter.setAlias(alias);
	jediFilter.setPath("");
	jediFilter.setAttributesList(attributeList);
	jediFilter.setDn(dn);

	List<JediObject> result = findByFilter(jediFilter);

	if (result != null && result.isEmpty() == false) {
	   if (result.size() == 1) {
		return result.get(0);
	   } else {
		throw new JediException();
	   }
	} else {
	   return null;
	}
   }

   // *****************************************************************************************************
   //
   // METHODES DE FACTORY
   //
   // *****************************************************************************************************

   /**
    * Methode de factory : crée une instance de JediAttribute avec un attribut mono-valué. La stratégie de log correctement renseignée. Si
    * la valeur de
    * l'attribut est null ou vide alors il n'y a pas d'initialisation de la valeur.
    * 
    * @param nameAttribute
    *           Nom de l'attribut.
    * @param valueAttribute
    *           Valeur de l'attribut (String).
    * @return Instance de JediAttribute créée
    * @throws JediException
    *            Les paramètres d'initialisation sont incorrects. Si nameAttribute est null ou vide.
    */
   public JediAttribute getJediAttribute(String nameAttribute, String valueAttribute) throws JediException {
	JediAttribute jediAttribute = new JediAttribute(nameAttribute, valueAttribute);
	return jediAttribute;
   }

   /**
    * Methode de factory : crée une instance de JediAttribute n'initialisant pas la valeur de l'attribut. La stratégie de log correctement
    * renseignée.
    * 
    * @param nameAttribute
    *           Nom de l'attribut.
    * @return Instance de JediAttribute créée
    * @throws JediException
    *            Les paramètres d'initialisation sont incorrects. Si nameAttribute est null ou vide.
    */
   public JediAttribute getJediAttribute(String nameAttribute) throws JediException {
	JediAttribute jediAttribute = new JediAttribute(nameAttribute);
	return jediAttribute;
   }

   /**
    * Methode de factory : crée une instance de JediAttribute avec un Attribute. La stratégie de log correctement renseignée.
    * 
    * @param attribute
    *           Attribut jndi recopié pour l'initialisation
    * @return Instance de JediAttribute créée
    * @throws JediException
    *            Les paramètres d'initialisation sont incorrects. Si attribute est null ou vide.
    */
   public JediAttribute getJediAttribute(Attribute attribute) throws JediException {
	JediAttribute jediAttribute = new JediAttribute(attribute);
	return jediAttribute;
   }

   /**
    * Methode de factory : crée une instance de JediAttribute ayant une valeur mono-valué binaire. La stratégie de log correctement
    * renseignée.
    * 
    * @param nameAttribute
    *           Nom de l'attribut.
    * @param valueAttribute
    *           Valeur de l'attribut (byte[]).
    * @return Instance de JediAttribute créée
    * @throws JediException
    *            Les paramètres d'initialisation sont incorrects. Si nameAttribute est null ou vide.
    */
   public JediAttribute getJediAttribute(String nameAttribute, byte[] valueAttribute) throws JediException {
	JediAttribute jediAttribute = new JediAttribute(nameAttribute, valueAttribute);
	return jediAttribute;
   }

   /**
    * Methode de factory : crée une instance de JediAttribute ayant une valeur multivaluée. La stratégie de log correctement renseignée. Si
    * une des valeurs de
    * l'attribut multi-valué est null ou vide, elle ne sera pas ajoutée à la liste des valeurs. Si toutes les valeurs sont null ou vide
    * alors la valeur de
    * l'attribut n'est pas initialisée.
    * 
    * @param nameAttribute
    *           Nom de l'attribut.
    * @param valuesAttribute
    *           Valeurs de l'attribut
    * @return Instance de JediAttribute créée
    * @throws JediException
    *            Les paramètres d'initialisation sont incorrects. Si nameAttribute est null ou vide.
    */
   public JediAttribute getJediAttribute(String nameAttribute, Object[] valuesAttribute) throws JediException {
	JediAttribute jediAttribute = new JediAttribute(nameAttribute, valuesAttribute);
	return jediAttribute;
   }

   /**
    * Methode de factory : crée une instance de JediAttribute ayant une valeur multivaluée. La stratégie de log correctement renseignée. Si
    * une des valeurs de
    * l'attribut multi-valué est null ou vide, elle ne sera pas ajoutée à la liste des valeurs. Si toutes les valeurs sont null ou vide
    * alors la valeur de
    * l'attribut n'est pas initialisée.
    * 
    * @param nameAttribute
    *           Nom de l'attribut.
    * @param list
    *           Valeurs de l'attribut qui seront castées en String.
    * @return Instance de JediAttribute créée
    * @throws JediException
    *            Les paramètres d'initialisation sont incorrects. Si nameAttribute est null ou vide.
    */
   public JediAttribute getJediAttribute(String nameAttribute, List<String> list) throws JediException {
	JediAttribute jediAttribute = new JediAttribute(nameAttribute, list);
	return jediAttribute;
   }

   /**
    * Methode de factory : créé une instance de JediPath prenant en paramètre un String.
    * 
    * @param path
    *           String contenant le chemin.
    * @return Instance de JediPath créée
    * @throws JediException
    *            Si le path est null.
    */
   public JediPath getJediPath(String path) throws JediException {
	JediPath jediPath = new JediPath(path);
	return jediPath;
   }

   /**
    * Methode de factory : créé une instance de JediPath prenant en paramètre un tableau de String.
    * 
    * @param pathTable
    *           Tableau contenant le chemin.
    * @return Instance de JediPath créée
    * @throws JediException
    *            Si le path est null.
    */
   public JediPath getJediPath(String[] pathTable) throws JediException {
	JediPath jediPath = new JediPath(pathTable);
	return jediPath;
   }

   /**
    * Methode de factory : créé une instance de JediPath vide
    * 
    * @return Instance de JediPath créée
    * @throws JediException
    *            Si le path est null.
    */
   public JediPath getJediPath() throws JediException {
	JediPath jediPath = new JediPath();
	return jediPath;
   }

}// fin de la classe
