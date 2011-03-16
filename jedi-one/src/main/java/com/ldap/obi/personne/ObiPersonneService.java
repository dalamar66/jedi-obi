package com.ldap.obi.personne;

import java.util.ArrayList;
import java.util.List;

import com.ldap.jedi.JediAttributeList;
import com.ldap.jedi.JediConnectionException;
import com.ldap.jedi.JediException;
import com.ldap.jedi.JediFilter;
import com.ldap.jedi.JediLog;
import com.ldap.jedi.JediObject;
import com.ldap.obi.ObiConnectionException;
import com.ldap.obi.ObiData;
import com.ldap.obi.ObiDataException;
import com.ldap.obi.ObiInvalidDnException;
import com.ldap.obi.ObiOne;
import com.ldap.obi.ObiOneException;
import com.ldap.obi.ObiService;
import com.ldap.obi.ObiServiceException;

/**
 * File : ObiPersonneService.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-10 
 * Modification date : 2010-03-10
 */

public class ObiPersonneService extends ObiService<ObiPersonneData> {

	public ObiPersonneService(ObiOne one) throws ObiServiceException {
		super(one);

		ldapClassName = ObiPersonneConstants.CLASS_NAME_PERSON;

		ldapCategory = ObiPersonneConstants.CATEGORY_USER;

		ldapFullClassName = new ArrayList<String>();
		ldapFullClassName.add(ObiPersonneConstants.CLASS_NAME_TOP);
		ldapFullClassName.add(ObiPersonneConstants.CLASS_NAME_USER);
		ldapFullClassName.add(ldapClassName);

		defaultAttributes = new ArrayList<String>();
		defaultAttributes.add(ObiPersonneConstants.ATTRIBUTE_SN);
		defaultAttributes.add(ObiPersonneConstants.ATTRIBUTE_GIVEN_NAME);
		defaultAttributes.add(ObiPersonneConstants.ATTRIBUTE_DISTINGUISHED_NAME);
	}

	@Override
	protected boolean isRdnModified(String DN, ObiData myData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected ObiPersonneData newObiData(JediAttributeList jediAttributeList) throws ObiDataException {
		return new ObiPersonneData(jediAttributeList);
	}

	@Override
	protected ObiPersonneData newObiData(JediObject jediObject) throws ObiDataException {
		return new ObiPersonneData(jediObject);
	}

	/**
	 * Récupération d'un OBIPersonData depuis l'annuaire à partir d'un DN. <BR>
	 * <BR>
	 * Doit appeller OBIService.getData.
	 * 
	 * @param DN
	 *            distinguishedName COMPLET de l'objet à récupérer.
	 * @param requiredAttributes
	 *            liste d'attributs à charger.
	 * @return un OBIPersonData.
	 * @throws ObiOneException
	 * @throws ObiInvalidDnException
	 * @throws ObiServiceException
	 * @throws ObiConnectionException
	 */
	public ObiPersonneData getPersonData(String dn, List<String> requiredAttributes) throws ObiConnectionException, ObiServiceException, ObiInvalidDnException, ObiOneException {
		// Verification de la validite des parametres de la methode
		if (dn == null || dn.length() == 0 || requiredAttributes == null || requiredAttributes.size() == 0) {
			List<String> paramList = new ArrayList<String>();
			paramList.add(dn);

			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_parameter_error", paramList, this);

			throw new ObiServiceException("OBIPersonService : getPersonData(String, String[]) : Paramètres incorrects");
		}

		// Construction de la liste d'attribut à l'aide du DN et des attributs requis
		return getData(dn, requiredAttributes);
	}// Fin de la methode

	/**
	 * Récupération d'un OBIPersonData depuis l'annuaire à partir d'un DN. La liste des attributs chargés est celle par defaut. <BR>
	 * <BR>
	 * Doit appeller OBIService.getData.
	 * 
	 * @param DN
	 *            distinguishedName COMPLET de l'objet à récupérer.
	 * @return un OBIPersonData.
	 * @throws ObiOneException
	 * @throws ObiInvalidDnException
	 * @throws ObiServiceException
	 * @throws ObiConnectionException
	 */
	public ObiPersonneData getPersonData(String dn) throws ObiConnectionException, ObiServiceException, ObiInvalidDnException, ObiOneException {
		return getPersonData(dn, defaultAttributes);
	}
	
	public List<ObiPersonneData> findPersonDataByFilter(String attributeName, String value) throws ObiServiceException, ObiConnectionException {
		JediFilter jediFilter = new JediFilter();
		jediFilter.setAlias(one.getDirectoryAlias());
		jediFilter.setPath("");
		jediFilter.setFilter("(&(objectCategory=Person)(objectClass=user)(" + attributeName + "=" + value + "))");
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
		
		List<ObiPersonneData> result = new ArrayList<ObiPersonneData>();

		try {
			if (jediObjectList != null) {
				for (JediObject jediObject : jediObjectList) {
					result.add(new ObiPersonneData(jediObject));
				}
			}
		} catch (ObiDataException e) {
			throw new ObiServiceException();
		}
		
		return result;
	}

	public List<ObiPersonneData> getAllPersons() throws ObiServiceException, ObiConnectionException {
		return findPersonDataByFilter("objectCategory", "Person");
	}

}
