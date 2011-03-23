package com.ldap.obi.user;

import java.util.ArrayList;
import java.util.List;

import com.ldap.jedi.JediAttributeList;
import com.ldap.jedi.JediConnectionException;
import com.ldap.jedi.JediException;
import com.ldap.jedi.JediFilter;
import com.ldap.jedi.JediLog;
import com.ldap.jedi.JediObject;
import com.ldap.jedi.JediPath;
import com.ldap.jedi.JediServer;
import com.ldap.obi.ObiConnectionException;
import com.ldap.obi.ObiData;
import com.ldap.obi.ObiDataException;
import com.ldap.obi.ObiInvalidDnException;
import com.ldap.obi.ObiOne;
import com.ldap.obi.ObiOneException;
import com.ldap.obi.ObiService;
import com.ldap.obi.ObiServiceException;

/**
 * File : ObiUserService.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-10 
 * Modification date : 2010-03-10
 */

public class ObiUserService extends ObiService<ObiUserData> {

	public ObiUserService(ObiOne one) throws ObiServiceException {
		super(one);

		ldapClassName = ObiUserConstants.CLASS_NAME_USER;

		ldapCategory = ObiUserConstants.CATEGORY_USER;

		ldapFullClassName = new ArrayList<String>();
		ldapFullClassName.add(ObiUserConstants.CLASS_NAME_TOP);
		ldapFullClassName.add(ldapClassName);

		defaultAttributes = new ArrayList<String>();
		defaultAttributes.add(ObiUserConstants.ATTRIBUTE_DISTINGUISHED_NAME);
		defaultAttributes.add(ObiUserConstants.ATTRIBUTE_DISPLAY_NAME);
		defaultAttributes.add(ObiUserConstants.ATTRIBUTE_SAMACCOUNT_NAME);
	}

	@Override
	protected boolean isRdnModified(String DN, ObiData myData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected ObiUserData newObiData(JediAttributeList jediAttributeList) throws ObiDataException {
		return new ObiUserData(jediAttributeList);
	}

	@Override
	protected ObiUserData newObiData(JediObject jediObject) throws ObiDataException {
		return new ObiUserData(jediObject);
	}

	/**
	 * Récupération d'un ObiUserData depuis l'annuaire à partir d'un DN. <BR>
	 * <BR>
	 * Doit appeller OBIService.getData.
	 * 
	 * @param DN
	 *            distinguishedName COMPLET de l'objet à récupérer.
	 * @param requiredAttributes
	 *            liste d'attributs à charger.
	 * @return un OBIUserData.
	 * @throws ObiOneException
	 * @throws ObiInvalidDnException
	 * @throws ObiServiceException
	 * @throws ObiConnectionException
	 */
	public ObiUserData getUserData(String dn, List<String> requiredAttributes) throws ObiConnectionException, ObiServiceException, ObiInvalidDnException,
			ObiOneException {
		// Verification de la validite des parametres de la methode
		if (dn == null || dn.length() == 0 || requiredAttributes == null || requiredAttributes.size() == 0) {
			List<String> paramList = new ArrayList<String>();
			paramList.add(dn);

			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_parameter_error", paramList, this);

			throw new ObiServiceException("OBIUserService : getUserData(String, String[]) : Paramètres incorrects");
		}

		// Construction de la liste d'attribut à l'aide du DN et des attributs requis
		return getData(dn, requiredAttributes);
	}// Fin de la methode

	/**
	 * Récupération d'un OBIUserData depuis l'annuaire à partir d'un DN. La liste des attributs chargés est celle par defaut. <BR>
	 * <BR>
	 * Doit appeller OBIService.getData.
	 * 
	 * @param DN
	 *            distinguishedName COMPLET de l'objet à récupérer.
	 * @return un OBIUserData.
	 * @throws ObiOneException
	 * @throws ObiInvalidDnException
	 * @throws ObiServiceException
	 * @throws ObiConnectionException
	 */
	public ObiUserData getUserData(String dn) throws ObiConnectionException, ObiServiceException, ObiInvalidDnException, ObiOneException {
		return getUserData(dn, defaultAttributes);
	}

	public List<ObiUserData> findUserByFilter(String attribute, String value) throws ObiServiceException, ObiConnectionException {
		JediFilter jediFilter = new JediFilter();
		jediFilter.setAlias(one.getDirectoryAlias());
		jediFilter.setPath("");
		jediFilter.setFilter("(&(objectCategory=Person)(objectClass=" + ObiUserConstants.CLASS_NAME_USER + ")(" + attribute + "=" + value + "))");
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

		List<ObiUserData> result = new ArrayList<ObiUserData>();

		try {
			if (jediObjectList != null) {
				for (JediObject jediObject : jediObjectList) {
					result.add(new ObiUserData(jediObject));
				}
			}
		} catch (ObiDataException e) {
			throw new ObiServiceException();
		}

		return result;
	}

	public List<ObiUserData> getAllUsers() throws ObiServiceException, ObiConnectionException {
		return findUserByFilter("objectCategory", "Person");
	}
	
	/**
	 * Methode permettant de recuperer une connection pour le login passé en parametre.
	 * 
	 * @param login
	 * @param password
	 * @return
	 * @throws ObiServiceException
	 * @throws ObiConnectionException
	 */
	public JediServer getUserConnection(String login, String password) throws ObiServiceException, ObiConnectionException {
		List<ObiUserData> list = findUserByFilter("sAMAccountName", login);

		if (list == null || list.size() == 0) {
			return null;
		} else if (list.size() != 1) {
			throw new ObiServiceException("");
		} else {
			try {
				// Recuperation du DN du user
				String dn = (list.get(0)).getValue(ObiUserConstants.ATTRIBUTE_DISTINGUISHED_NAME);

				// Etablissement de la connection
				JediServer server = new JediServer();
				server.addConnectionParameters("loginAlias_" + login, one.getDomainLdap(), new JediPath(one.getDomainRoot()), new JediPath(dn), password);

				//Verification de la connection
				JediFilter jediFilter = new JediFilter();
				jediFilter.setAlias("loginAlias_" + login);
				jediFilter.setPath("");
				jediFilter.setFilter("(&(objectClass=user)(sAMAccountName=" + login + "))");
				jediFilter.setSubtree(true);

				List<JediObject> listControle = server.findByFilter(jediFilter);
				
				if (listControle != null && listControle.isEmpty() == false) {
					return server;
				} else {
					throw new ObiConnectionException("");
				}
			} catch (Exception ex) {
				throw new ObiConnectionException("");
			}
		}
	}

	/**
	 * Methode permettant de valider le login et le password passés en parametres.
	 * 
	 * @param login
	 * @param password
	 * @return
	 */
	public boolean checkUserAuthentification(String login, String password) {
		try {
			JediServer jediServer = getUserConnection(login, password);
			jediServer.closeConnection();
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
