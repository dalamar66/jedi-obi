package com.ldap.obi.computer;

import java.util.ArrayList;

import com.ldap.jedi.JediAttributeList;
import com.ldap.jedi.JediObject;
import com.ldap.obi.ObiDataException;
import com.ldap.obi.ObiOne;
import com.ldap.obi.ObiService;
import com.ldap.obi.ObiServiceException;

/**
 * File : ObiComputerService.java 
 * Component : Version : 1.0 
 * Creation date : 2011-03-28 
 * Modification date : 2011-03-28
 */

public class ObiComputerService extends ObiService<ObiComputerData> {

	public ObiComputerService(ObiOne one) throws ObiServiceException {
		super(one);

		ldapClassName = ObiComputerConstants.CLASS_NAME_COMPUTER;

		ldapCategory = ObiComputerConstants.CATEGORY_COMPUTER;

		ldapFullClassName = new ArrayList<String>();
		ldapFullClassName.add(ObiComputerConstants.CLASS_NAME_TOP);
		ldapFullClassName.add(ObiComputerConstants.CLASS_NAME_PERSON);
		ldapFullClassName.add(ObiComputerConstants.CLASS_NAME_ORGANIZATIONAL_PERSON);
		ldapFullClassName.add(ObiComputerConstants.CLASS_NAME_USER);
		ldapFullClassName.add(ldapClassName);

		defaultAttributes = new ArrayList<String>();
		defaultAttributes.add(ObiComputerConstants.ATTRIBUTE_DISPLAY_NAME);
		defaultAttributes.add(ObiComputerConstants.ATTRIBUTE_DISTINGUISHED_NAME);
		defaultAttributes.add(ObiComputerConstants.ATTRIBUTE_NAME);
		defaultAttributes.add(ObiComputerConstants.ATTRIBUTE_CN);
	}

	@Override
	protected ObiComputerData newObiData(JediAttributeList jediAttributeList) throws ObiDataException {
		return new ObiComputerData(jediAttributeList);
	}

	@Override
	protected ObiComputerData newObiData(JediObject jediObject) throws ObiDataException {
		return new ObiComputerData(jediObject);
	}

}
