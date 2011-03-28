package com.ldap.obi.organizationalUnit;

import java.util.ArrayList;

import com.ldap.jedi.JediAttributeList;
import com.ldap.jedi.JediObject;
import com.ldap.obi.ObiDataException;
import com.ldap.obi.ObiOne;
import com.ldap.obi.ObiService;
import com.ldap.obi.ObiServiceException;

/**
 * File : ObiOrganizationalUnitService.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-28 
 * Modification date : 2010-03-28
 */

public class ObiOrganizationalUnitService extends ObiService<ObiOrganizationalUnitData> {

	public ObiOrganizationalUnitService(ObiOne one) throws ObiServiceException {
		super(one);

		ldapClassName = ObiOrganizationalUnitConstants.CLASS_NAME_ORGANIZATIONAL_UNIT;

		ldapCategory = ObiOrganizationalUnitConstants.CATEGORY_ORGANIZATIONAL_UNIT;

		ldapFullClassName = new ArrayList<String>();
		ldapFullClassName.add(ObiOrganizationalUnitConstants.CLASS_NAME_TOP);
		ldapFullClassName.add(ldapClassName);

		defaultAttributes = new ArrayList<String>();
		defaultAttributes.add(ObiOrganizationalUnitConstants.ATTRIBUTE_DISPLAY_NAME);
		defaultAttributes.add(ObiOrganizationalUnitConstants.ATTRIBUTE_DISTINGUISHED_NAME);
		defaultAttributes.add(ObiOrganizationalUnitConstants.ATTRIBUTE_NAME);
		defaultAttributes.add(ObiOrganizationalUnitConstants.ATTRIBUTE_OU);
	}

	@Override
	protected ObiOrganizationalUnitData newObiData(JediAttributeList jediAttributeList) throws ObiDataException {
		return new ObiOrganizationalUnitData(jediAttributeList);
	}

	@Override
	protected ObiOrganizationalUnitData newObiData(JediObject jediObject) throws ObiDataException {
		return new ObiOrganizationalUnitData(jediObject);
	}

}
