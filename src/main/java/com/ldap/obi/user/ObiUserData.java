package com.ldap.obi.user;

import java.util.List;

import com.ldap.jedi.JediAttributeList;
import com.ldap.jedi.JediObject;
import com.ldap.obi.ObiData;
import com.ldap.obi.ObiDataException;

/**
 * File : ObiUserData.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-10 
 * Modification date : 2010-03-10
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

	public void formatDataList(boolean store) {
		super.formatDataList(store);
	}

	public boolean controlDataList(boolean create) {
		return (super.controlDataList(create));
	}

	public String getValue(String attributeName) throws ObiDataException {
		return super.getValue(attributeName);
	}

	public List<String> getValues(String attributeName) throws ObiDataException {
		return super.getValues(attributeName);
	}

	public void setValue(String attributeName, String value) throws ObiDataException {
		super.setValue(attributeName, value);
	}

	public void setValues(String attributeName, List<String> values) throws ObiDataException {
		super.setValues(attributeName, values);
	}

}
