package com.ldap.obi.user;

import com.ldap.obi.ObiConstants;

/**
 * File : ObiUserConstants.java 
 * Component : Version : 1.0 
 * Creation date : 2011-03-10 
 * Modification date : 2011-03-28
 */
public interface ObiUserConstants extends ObiConstants{

	static final String CATEGORY_USER = "Person";

	static final String CLASS_NAME_PERSON = "person";
	static final String CLASS_NAME_ORGANIZATIONAL_PERSON = "organizationalPerson";
	static final String CLASS_NAME_USER = "user";

	static final String ATTRIBUTE_DISPLAY_NAME = "displayName";
	static final String ATTRIBUTE_SAMACCOUNT_NAME = "sAMAccountName";

}
