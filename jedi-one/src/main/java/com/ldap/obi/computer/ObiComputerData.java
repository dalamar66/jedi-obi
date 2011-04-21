package com.ldap.obi.computer;

import com.ldap.jedi.JediAttribute;
import com.ldap.jedi.JediAttributeList;
import com.ldap.jedi.JediObject;
import com.ldap.obi.ObiData;
import com.ldap.obi.ObiDataException;

/**
 * File : ObiComputerData.java 
 * Component : Version : 1.0 
 * Creation date : 2011-03-28 
 * Modification date : 2011-04-21
 */

public class ObiComputerData extends ObiData {

	public ObiComputerData() {
		super();
	}

	public ObiComputerData(JediObject jediObject) throws ObiDataException {
		super(jediObject);
	}

	public ObiComputerData(JediAttributeList jediAttributeList) throws ObiDataException {
		super(jediAttributeList);
	}
	
	public ObiComputerData(JediAttribute jediAttribute) throws ObiDataException {
		super(jediAttribute);
	}

}
