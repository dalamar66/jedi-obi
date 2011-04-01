package com.ldap.obi.personne;

import java.util.ArrayList;

import com.ldap.jedi.JediAttributeList;
import com.ldap.jedi.JediObject;
import com.ldap.obi.ObiDataException;
import com.ldap.obi.ObiOne;
import com.ldap.obi.ObiService;
import com.ldap.obi.ObiServiceException;

/**
 * File : ObiPersonneService.java 
 * Component : Version : 1.0 
 * Creation date : 2011-03-10 
 * Modification date : 2011-03-28
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
	protected ObiPersonneData newObiData(JediAttributeList jediAttributeList) throws ObiDataException {
		return new ObiPersonneData(jediAttributeList);
	}

	@Override
	protected ObiPersonneData newObiData(JediObject jediObject) throws ObiDataException {
		return new ObiPersonneData(jediObject);
	}

}
