package com.ldap.obi;

import java.util.ArrayList;
import java.util.List;

import com.ldap.jedi.JediAttributeList;
import com.ldap.jedi.JediConnectionException;
import com.ldap.jedi.JediException;
import com.ldap.jedi.JediFilter;
import com.ldap.jedi.JediLog;
import com.ldap.jedi.JediObject;
import com.ldap.jedi.JediPath;

/**
 * File : ObiService.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-10 
 * Modification date : 2011-03-28
 */

public abstract class ObiService<T extends ObiData> {

	protected ObiOne one;
	protected List<String> defaultAttributes = null;
	protected String ldapClassName = null;
	protected String ldapCategory = null;
	protected List<String> ldapFullClassName = null;

	/**
	 * Constructeur prenant en paramètre un ObiOne.
	 * 
	 * @param one
	 *            lien vers l'annuaire.
	 * @throws OBIServiceException
	 *             Si le paramètre OBIOne est null.
	 */
	public ObiService(ObiOne one) throws ObiServiceException {
		// Verification de la validite des parametres de la methode
		if (one == null) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_parameter_error", "", this);

			throw new ObiServiceException("OBIService : OBIService(OBIOne) : Paramètre incorrect");
		}

		this.one = one;
	}

	/**
	 * Methode qui retourne le tableau des attributs a charger par defaut.
	 * 
	 * @return Une liste contenant les attributs a charger par defaut.
	 */
	public List<String> getDefaultAttributes() {
		return defaultAttributes;
	}

	/**
	 * Récupération d'une liste d'attributs de l'annuaire à partir d'un DN. Si le DN est dans mon domaine : récupération sur le domaine. Si le DN n'est pas dans
	 * mon domaine : si tous les attributs demandés sont répliqués, récupération sur le Catalogue Global, sinon sur le domaine. Cette méthode sera utilisée par
	 * toutes les méthodes du type getXXXXXData.
	 * 
	 * @param DN
	 *            distinguishedName de l'objet à récupérer.
	 * @param requiredAttributes
	 *            liste d'attributs à charger.
	 * @return liste d'attributs.
	 * @throws ObiDataException
	 * @throws OBIConnectionException
	 *             Si il y a un problème de connexion.
	 * @throws OBIInvalidDnException
	 *             Si le dn de recherche est invalide.
	 * @throws OBIOneException
	 *             Si la liste des attributs requis est null.
	 * @throws OBIServiceException
	 *             Si les paramètres sont incorrects (null) ou si il ya une erreur sur la requete.
	 */
	protected T getData(String dnWithRac, List<String> attributesList) throws ObiConnectionException, ObiServiceException, ObiInvalidDnException, ObiOneException {
		// Verification de la validite des parametres de la methode
		if (dnWithRac == null || attributesList == null) {
			List<String> paramList = new ArrayList<String>();
			paramList.add(dnWithRac);
			paramList.add(Integer.toString(attributesList.size()));

			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_parameter_error", paramList, this);

			throw new ObiServiceException("OBIService : getData(String, String[]) : Paramètres incorrects");
		}

		String aliasOfConnexion = null;
		String sourceDnTmp = null;

		// Si la liste des attributs requis est vide alors on renvoie un JediAttributeList vide
		if (attributesList.size() == 0) {
			try {
				return newObiData(new JediAttributeList());
			} catch (ObiDataException ex) {
				throw new ObiServiceException("OBIService : getData(String, String[]) : Erreur lors de la construction du data");
			}
		}

		// Si le DN est en local alors la connexion est en local
		if (ObiUtil.endWithPath(dnWithRac, one.getDomainRoot()) && one.getDomainRoot().equalsIgnoreCase("") == false) {
			aliasOfConnexion = one.getDirectoryAlias();
			sourceDnTmp = dnWithRac.substring(0, dnWithRac.toLowerCase().indexOf(one.getDomainRoot().toLowerCase()) - 1);
		}
		// Si le DN n'est pas en local alors la connexion est sur le GC
		else {
			aliasOfConnexion = one.getGlobalCatalogAlias();
			sourceDnTmp = dnWithRac;

			// On regarde si les attributs demandés sont repliqués sur le GC
			for (String attribute : attributesList) {
				// Si l'un d'eux n'est pas repliqué alors la connexion est en locale
				if (one.isAttributeInGc(attribute) == false) {
					aliasOfConnexion = one.getDirectoryAlias();
					break;
				}
			}
		}// fin du else

		// On recupere l'objet et les attributs requis
		try {
			JediFilter jediFilter = new JediFilter();
			jediFilter.setAlias(aliasOfConnexion);
			jediFilter.setDn(sourceDnTmp);
			jediFilter.setAttributesList(attributesList);

			List<JediObject> listFilter = one.getServer().findByFilter(jediFilter);

			if (listFilter != null && listFilter.size() == 1) {
				// Si le dn existe on renvoie sa liste d'attribut
				return newObiData(listFilter.get(0));
			} else {
				// Si le dn n'existe pas alors on signale que le dn est invalide
				JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "DN invalide", "", this);

				throw new ObiInvalidDnException("OBIService : getData(String, String[]) : DN de recherche invalide");
			}
		} catch (ObiDataException ex) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);

			throw new ObiServiceException("OBIService : getData(String, String[]) : Erreur lors de la construction de la data");
		} catch (JediException ex) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);

			throw new ObiServiceException("OBIService : getData(String, String[]) : Erreur sur la requête");
		} catch (JediConnectionException ex) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);

			throw new ObiConnectionException("OBIService : getData(String, String[]) : Erreur sur la connection");
		}
	}

	/**
	 * Mise à jour dans l'annuaire d'une liste d'attributs à partir d'un DN. Connexion au domaine. Cette méthode sera utilisée par toutes les méthodes du type
	 * setXXXXXData. Attention la modification de RDN est impossible et doit passer pa un renommage.
	 * 
	 * @param dnWithRac
	 *            distinguishedName de l'objet à mettre à jour.
	 * @param myData
	 *            liste d'attributs à mettre à jour.
	 * @throws OBIConnectionException
	 *             En cas de problème de connexion.
	 * @throws OBIInvalidDnException
	 *             Si le dn est invalide.
	 * @throws OBIServiceException
	 *             Si les paramètres sont invalides, si il y a une erreur sur le rappatriement de l'objet, si il y a une erreur sur le remplacement des
	 *             attributs, ou si il y a un echec du controle.
	 */
	public void setData(String dnWithRac, ObiData myData) throws ObiInvalidDnException, ObiServiceException, ObiConnectionException {
		// Verification de la validite des parametres de la methode
		if (dnWithRac == null || dnWithRac.length() == 0 || myData == null || isRdnModified(dnWithRac, myData)) {
			List<String> paramList = new ArrayList<String>();
			paramList.add(dnWithRac);

			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_parameter_error", paramList, this);

			throw new ObiServiceException("OBIService : setData(String, OBIData) : Paramètres incorrects");
		}

		// Si le dn n'existe pas on signale l'invalidité du DN
		if (existDn(dnWithRac) == false) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);

			throw new ObiInvalidDnException("OBIService : setData(String, OBIData) : DN inexistant");
		}

		// Si le DN est en local alors la connexion est en local
		if (ObiUtil.endWithPath(dnWithRac, one.getDomainRoot())) {
			dnWithRac = dnWithRac.substring(0, dnWithRac.toLowerCase().indexOf(one.getDomainRoot().toLowerCase()) - 1);
		}

		// Si le DN existe, on recupere de la base l'objet correspondant au DN
		JediObject jediObject = null;
		try {
			jediObject = new JediObject(one.getDirectoryAlias(), one.getServer(), new JediPath(dnWithRac));
		} catch (JediException ex) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);

			throw new ObiServiceException("OBIService : setData(String, OBIData) : Erreur sur le rappatriement de l'objet");
		}

		// Si la liste apres controle de modification est valide
		if (myData.controlDataList(false) == true) {
			// On formate la liste pour le stockage en base
			myData.formatDataList(true);

			// On met a jour l'objet avec la liste des attributs
			try {
				jediObject.updateAttributeList(myData.getDataList());
			} catch (JediException ex) {
				JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);

				throw new ObiServiceException("OBIService : setData(String, OBIData) : Erreur sur le remplacement des attributs");
			} catch (JediConnectionException ex) {
				JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);

				throw new ObiConnectionException("OBIService : setData(String, OBIData) : Erreur de connexion");
			}
		} else {
			// Ne pas mettre en base car controle pas valide
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);

			throw new ObiServiceException("OBIService : setData(String, OBIData) : Echec du contrôle");
		}
	}

	/**
	 * Teste si un DN existe. Connexion au Catalogue Global.
	 * 
	 * @param dn
	 *            distinguishedName à tester.
	 * @return true si le dn existe, false sinon.
	 * @throws OBIConnectionException
	 *             Si il y a un problème de connexion.
	 * @throws OBIServiceException
	 *             Si le paramètre est null ou vide, ou si il y a une erreur sur le filtre.
	 */
	protected boolean existDn(String dnWithRac) throws ObiServiceException, ObiConnectionException {
		// Verification de la validite des parametres de la methode
		if (dnWithRac == null || dnWithRac.length() == 0) {
			List<String> paramList = new ArrayList<String>();
			paramList.add(dnWithRac);

			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_parameter_error", paramList, this);

			throw new ObiServiceException("OBIService : existsDn(String) : Paramètre incorrect");
		}

		try {
			// Application du filtre et recuperation des jediObject verifiant le filtre
			JediFilter jediFilter = new JediFilter();
			jediFilter.setAlias(one.getGlobalCatalogAlias());
			jediFilter.setDn(dnWithRac);
			jediFilter.setAttributesList(new ArrayList<String>());

			List<JediObject> listFilter = one.getServer().findByFilter(jediFilter);
			if (listFilter != null && listFilter.size() == 1) {
				return true;
			} else {
				return false;
			}
		} catch (JediException ex) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);

			throw new ObiServiceException("OBIService : existsDn(String) : Erreur sur le filtre");
		} catch (JediConnectionException ex) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);

			throw new ObiConnectionException("OBIService : existsDn(String) : Erreur de connexion");
		}
	}

	/**
	 * Création d'une entrée LDAP à l'aide du DN et d'une liste d'attributs comprenant au moins tous les attributs obligatoires. Connexion au domaine.
	 * 
	 * @param DN
	 *            distinguishedName PARTIEL (SANS LA RACINE) de l'objet à créer.
	 * @param myData
	 *            liste d'attributs pour la création.
	 * @throws OBIConnectionException
	 *             En cas de problème de connexion.
	 * @throws OBINamingException
	 *             Si le dn de l'objet existe déjà.
	 * @throws OBIServiceException
	 *             Si les paramètres sont invalides, si il y a une erreur de creation d'objet intermediaire, ou si il y a une erreur de création d'objet.
	 */
	protected JediObject create(String dn, ObiData data) throws ObiConnectionException, ObiServiceException, ObiNamingException {
		// Principes :
		// - appel à myData.controlDataList(true)
		// - appel à myData.formatDataList(...)
		// - génération de la clé (myData.generateKey) et du RDN (myData.generateRDN)
		// - instanciation d'un JediObject temporaire à partir de la dataList, de la clé et du RDN
		// - création dans l'annuaire à l'aide du JediObject

		// Verification de la validite des parametres de la methode
		if (dn == null || dn.length() == 0 || data == null) {
			List<String> paramList = new ArrayList<String>();
			paramList.add(dn);

			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_parameter_error", paramList, this);

			throw new ObiServiceException("OBIService : create(String, OBIData) : Paramètres incorrects");
		}

		JediObject jediObject = null;

		if (ObiUtil.endWithPath(dn, one.getDomainRoot())) {
			dn = dn.substring(0, dn.toLowerCase().indexOf(one.getDomainRoot().toLowerCase()) - 1);
		}

		// Si le DN existe deja on le signale
		if (existDn(dn)) {
			throw new ObiNamingException("OBIService : create(String, OBIData) : DN existant");
		}
		// Si le DN n'existe pas dans la base
		else {
			// On créé l'objet correspondant au DN
			try {
				jediObject = new JediObject(one.getDirectoryAlias(), one.getServer(), new JediPath(dn));
			} catch (JediException ex) {
				JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);

				throw new ObiServiceException("OBIService : create(String, OBIData) : Erreur de creation d'objet intermediaire");
			}

			// Si la liste est valide pour une creation
			if (data.controlDataList(true)) {
				// On fait le formatage adequat
				data.formatDataList(true);

				// On rattache la liste des attributs a l'objet
				jediObject.setJediAttributeList(data.getDataList());

				// On insere le nouvel objet dans la base
				try {
					return (one.getServer().createLdapEntry(one.getDirectoryAlias(), jediObject));
				} catch (JediException ex) {
					JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);

					throw new ObiServiceException("OBIService : create(String, OBIData) : Erreur de creation d'objet");
				} catch (JediConnectionException ex) {
					JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);

					throw new ObiConnectionException("OBIService : create(String, OBIData) : Problème de connexion");
				}
			} else {
				// Ne pas mettre en base car pas valide
				JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);

				throw new ObiServiceException("OBIService : create(String, OBIData) : Erreur de creation d'objet");
			}// fin du else
		}// fin du else
	}// fin de la méthode

	/**
	 * Methode qui supprime une entree de la base.
	 * 
	 * @param dn
	 *            DN  à supprimer.
	 * @throws ObiNamingException
	 * @throws ObiConnectionException
	 * @throws ObiServiceException
	 */
	protected void delete(String dn) throws ObiServiceException, ObiConnectionException, ObiNamingException {
		JediLog.log(JediLog.LOG_FUNCTIONAL, JediLog.INFO, "obi_msg_remove", "", this);

		// Si le dn de la personne termine par la racine on supprime la racine
		if (ObiUtil.endWithPath(dn, one.getDomainRoot())) {
			dn = dn.substring(0, dn.toLowerCase().indexOf(one.getDomainRoot().toLowerCase()) - 1);
		}

		// Si le DN n'existe pas on le signale
		if (existDn(dn) == false) {
			throw new ObiNamingException("OBIService : remove(String, OBIData) : DN inexistant");
		}

		try {
			one.getServer().deleteLdapEntry(one.getDirectoryAlias(), new JediPath(dn));
		} catch (JediException je) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);

			throw new ObiServiceException("OBIService : remove(String) : Erreur");
		}
	}// Fin de la méthode

	/**
	 * Methode permettant de construire le filtre
	 * 
	 * @param filter
	 * @return
	 */
	private String appendObiDataFilter(String filter) {
		StringBuffer obiDataFilter = new StringBuffer("");
		obiDataFilter.append("(&(objectClass=").append(ldapClassName).append(")");
		obiDataFilter.append("(objectCategory=").append(ldapCategory).append("))");

		if (filter != null && filter.trim().equals("") == false) {
			StringBuffer resultFilter = new StringBuffer("");
			resultFilter.append("(&").append(obiDataFilter.toString()).append(filter).append(")");
			return resultFilter.toString();
		} else {
			return obiDataFilter.toString();
		}
		
	}
	
	@SuppressWarnings("deprecation")
	public List<T> findObiDataByFilter(JediFilter jediFilter) throws ObiServiceException, ObiConnectionException, ObiDataException {
		//Ajout des parametres obligatoires au filtre
		String filter = jediFilter.getFilter();
		jediFilter.setFilter(appendObiDataFilter(filter));

		//Execution de la requete
		List<JediObject> list = findByFilter(jediFilter);

		//Liste de resultats
		List<T> dataList = new ArrayList<T>();

		if (list == null) {
			return null;
		} else {
			for (JediObject object : list) {
				dataList.add(newObiData(object));
			}
		}

		return dataList;
	}
	
	@Deprecated
	public List<JediObject> findByFilter(JediFilter jediFilter) throws ObiServiceException, ObiConnectionException {
		// Verification de la validite des parametres de la methode
		if (jediFilter == null) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_parameter_error", "", this);

			throw new ObiServiceException("OBIService : findByFilter(String, OBIPersonData) : Paramètres incorrects");
		}

		try {
			return one.getServer().findByFilter(jediFilter);
		} catch (JediException e) {
			throw new ObiServiceException("OBIService : findByFilter(String, OBIPersonData) : Paramètres incorrects");
		} catch (JediConnectionException e) {
			throw new ObiConnectionException("OBIService : findByFilter(String, OBIPersonData) : Paramètres incorrects");
		}
	}

	protected boolean isRdnModified(String dn, ObiData myData) throws ObiServiceException {
		// Verification de la validite des parametres de la methode
		if (dn == null || myData == null) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_parameter_error", "", this);

			throw new ObiServiceException("OBIService : isRdnModified(String, ObiData) : Paramètres incorrects");
		}

		try {
			// Recuperation du rdn correspondant au dn
			JediPath pathDn = new JediPath(dn);
			String rdn = pathDn.getRDN();

			// Recuperation du rdn correspondant au data et comparaison
			String dnData = myData.getValue(ObiConstants.ATTRIBUTE_DISTINGUISHED_NAME);
			if (dnData != null && dnData.equals("") == false) {
				JediPath pathDnData = new JediPath(dnData);
				String rdnData = pathDnData.getRDN();
				
				if (rdnData.equalsIgnoreCase(rdn)) {
					return true;
				}
			}
		} catch (Exception e) {
			throw new ObiServiceException("OBIService : findByFilter(String, OBIPersonData) : Paramètres incorrects");
		}

		return false;
	}

	/**
	 * Récupération d'un OBIData depuis l'annuaire à partir d'un DN. <BR>
	 * <BR>
	 * Doit appeller OBIService.getData.
	 * 
	 * @param DN
	 *            distinguishedName COMPLET de l'objet à récupérer.
	 * @param requiredAttributes
	 *            liste d'attributs à charger.
	 * @return un OBIData.
	 * @throws ObiOneException
	 * @throws ObiInvalidDnException
	 * @throws ObiServiceException
	 * @throws ObiConnectionException
	 */
	protected T get(String dn, List<String> requiredAttributes) throws ObiConnectionException, ObiServiceException, ObiInvalidDnException, ObiOneException {
		// Verification de la validite des parametres de la methode
		if (dn == null || dn.length() == 0 || requiredAttributes == null || requiredAttributes.size() == 0) {
			List<String> paramList = new ArrayList<String>();
			paramList.add(dn);

			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_parameter_error", paramList, this);

			throw new ObiServiceException("OBIService : get(String, String[]) : Paramètres incorrects");
		}

		// Construction de la liste d'attribut à l'aide du DN et des attributs requis
		return getData(dn, requiredAttributes);
	}// Fin de la methode

	/**
	 * Récupération d'un OBIData depuis l'annuaire à partir d'un DN. La liste des attributs chargés est celle par defaut. <BR>
	 * <BR>
	 * Doit appeller OBIService.getData.
	 * 
	 * @param DN
	 *            distinguishedName COMPLET de l'objet à récupérer.
	 * @return un OBIData.
	 * @throws ObiOneException
	 * @throws ObiInvalidDnException
	 * @throws ObiServiceException
	 * @throws ObiConnectionException
	 */
	protected T get(String dn) throws ObiConnectionException, ObiServiceException, ObiInvalidDnException, ObiOneException {
		return get(dn, defaultAttributes);
	}
	
	/**
	 * Methode permettant de recuperer la liste des ObiData correspondant au filtrage sur l'attribut.
	 * 
	 * @param attributeName
	 * @param value
	 * @return
	 * @throws ObiServiceException
	 * @throws ObiConnectionException
	 */
	protected List<T> findByFilter(String attributeName, String value) throws ObiServiceException, ObiConnectionException {
		JediFilter jediFilter = new JediFilter();
		jediFilter.setAlias(one.getDirectoryAlias());
		jediFilter.setPath("");
		jediFilter.setFilter("(&(objectCategory=" + ldapCategory + ")(objectClass=" +  ldapClassName + ")(" + attributeName + "=" + value + "))");
		jediFilter.setAttributesList(defaultAttributes);
		jediFilter.setPageSize(900);
		jediFilter.setSubtree(true);

		List<JediObject> jediObjectList;
		try {
			jediObjectList = one.getServer().findByFilter(jediFilter);
		} catch (JediException e) {
			throw new ObiServiceException();
		} catch (JediConnectionException e) {
			throw new ObiConnectionException();
		}
		
		List<T> result = new ArrayList<T>();

		try {
			if (jediObjectList != null) {
				for (JediObject jediObject : jediObjectList) {
					result.add(newObiData(jediObject));
				}
			}
		} catch (ObiDataException e) {
			throw new ObiServiceException();
		}
		
		return result;
	}

	/**
	 * Methode permettant de recuperer la liste des ObiData correspondant au service.
	 * 
	 * @return
	 * @throws ObiServiceException
	 * @throws ObiConnectionException
	 */
	protected List<T> getAll() throws ObiServiceException, ObiConnectionException {
		return findByFilter("objectCategory", ldapCategory);
	}	

	// *****************************************************************************************************
	//
	// METHODES ABSTRAITES A DEFINIR DANS LES CLASSES FILLES
	//
	// *****************************************************************************************************
	
	protected abstract T newObiData(JediAttributeList jediAttributeList) throws ObiDataException;

	protected abstract T newObiData(JediObject jediObject) throws ObiDataException;

}
