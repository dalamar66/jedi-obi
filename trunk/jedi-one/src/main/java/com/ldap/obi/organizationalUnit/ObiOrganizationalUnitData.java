package com.ldap.obi.organizationalUnit;

import com.ldap.jedi.JediAttribute;
import com.ldap.jedi.JediAttributeList;
import com.ldap.jedi.JediObject;
import com.ldap.obi.ObiData;
import com.ldap.obi.ObiDataException;

/**
 * File : ObiOrganizationalUnitData.java 
 * Component : Version : 1.0 
 * Creation date : 2011-03-28 
 * Modification date : 2011-04-21
 */

public class ObiOrganizationalUnitData extends ObiData {

	public ObiOrganizationalUnitData() {
		super();
	}

	public ObiOrganizationalUnitData(JediObject jediObject) throws ObiDataException {
		super(jediObject);
	}

	public ObiOrganizationalUnitData(JediAttributeList jediAttributeList) throws ObiDataException {
		super(jediAttributeList);
	}

	public ObiOrganizationalUnitData(JediAttribute jediAttribute) throws ObiDataException {
		super(jediAttribute);
	}

}
