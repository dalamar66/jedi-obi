package com.ldap.obi.user;

import com.ldap.jedi.JediAttributeList;
import com.ldap.jedi.JediObject;
import com.ldap.obi.ObiData;
import com.ldap.obi.ObiDataException;

/**
 * File : ObiUserData.java 
 * Component : Version : 1.0 
 * Creation date : 2011-03-10 
 * Modification date : 2011-03-28
 */

public class ObiUserData extends ObiData {

	public ObiUserData() {
		super();
	}

	public ObiUserData(JediObject jediObject) throws ObiDataException {
		super(jediObject);
	}

	public ObiUserData(JediAttributeList jediAttributeList) throws ObiDataException {
		super(jediAttributeList);
	}

}
