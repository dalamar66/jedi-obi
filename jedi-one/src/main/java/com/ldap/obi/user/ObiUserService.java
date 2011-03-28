package com.ldap.obi.user;

import java.util.ArrayList;
import java.util.List;

import com.ldap.jedi.JediAttributeList;
import com.ldap.jedi.JediFilter;
import com.ldap.jedi.JediObject;
import com.ldap.jedi.JediPath;
import com.ldap.jedi.JediServer;
import com.ldap.obi.ObiConnectionException;
import com.ldap.obi.ObiConstants;
import com.ldap.obi.ObiDataException;
import com.ldap.obi.ObiOne;
import com.ldap.obi.ObiService;
import com.ldap.obi.ObiServiceException;

/**
 * File : ObiUserService.java 
 * Component : Version : 1.0 
 * Creation date : 2011-03-10 
 * Modification date : 2011-03-28
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
	protected ObiUserData newObiData(JediAttributeList jediAttributeList) throws ObiDataException {
		return new ObiUserData(jediAttributeList);
	}

	@Override
	protected ObiUserData newObiData(JediObject jediObject) throws ObiDataException {
		return new ObiUserData(jediObject);
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
	public JediServer getConnection(String login, String password) throws ObiServiceException, ObiConnectionException {
		List<ObiUserData> list = findByFilter("sAMAccountName", login);

		if (list == null || list.size() == 0) {
			return null;
		} else if (list.size() != 1) {
			throw new ObiServiceException("");
		} else {
			try {
				// Recuperation du DN du user
				String dn = (list.get(0)).getValue(ObiConstants.ATTRIBUTE_DISTINGUISHED_NAME);

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
	public boolean checkAuthentification(String login, String password) {
		try {
			JediServer jediServer = getConnection(login, password);
			jediServer.closeConnection();
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
