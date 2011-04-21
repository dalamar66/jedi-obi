package com.ldap.obi.group;

import com.ldap.jedi.JediAttribute;
import com.ldap.jedi.JediAttributeList;
import com.ldap.jedi.JediObject;
import com.ldap.obi.ObiData;
import com.ldap.obi.ObiDataException;

/**
 * File : ObiGroupData.java 
 * Component : Version : 1.0 
 * Creation date : 2011-04-01 
 * Modification date : 2011-04-21
 */

public class ObiGroupData extends ObiData {

	public ObiGroupData() {
		super();
	}

	public ObiGroupData(JediObject jediObject) throws ObiDataException {
		super(jediObject);
	}

	public ObiGroupData(JediAttributeList jediAttributeList) throws ObiDataException {
		super(jediAttributeList);
	}

	public ObiGroupData(JediAttribute jediAttribute) throws ObiDataException {
		super(jediAttribute);
	}
}
