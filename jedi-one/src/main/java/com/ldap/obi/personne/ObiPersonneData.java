package com.ldap.obi.personne;

import com.ldap.jedi.JediAttribute;
import com.ldap.jedi.JediAttributeList;
import com.ldap.jedi.JediObject;
import com.ldap.obi.ObiData;
import com.ldap.obi.ObiDataException;

/**
 * File : ObiPersonneData.java 
 * Component : Version : 1.0 
 * Creation date : 2011-03-10 
 * Modification date : 2011-04-21
 */

public class ObiPersonneData extends ObiData {

	public ObiPersonneData() {
		super();
	}

	public ObiPersonneData(JediObject jediObject) throws ObiDataException {
		super(jediObject);
	}

	public ObiPersonneData(JediAttributeList jediAttributeList) throws ObiDataException {
		super(jediAttributeList);
	}

	public ObiPersonneData(JediAttribute jediAttribute) throws ObiDataException {
		super(jediAttribute);
	}

}
