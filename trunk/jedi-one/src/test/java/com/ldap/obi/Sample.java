package com.ldap.obi;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.ldap.jedi.JediException;
import com.ldap.jedi.JediFilter;
import com.ldap.obi.personne.ObiPersonneData;
import com.ldap.obi.personne.ObiPersonneService;
import com.ldap.obi.user.ObiUserData;
import com.ldap.obi.user.ObiUserService;

public class Sample extends Assert {

	ObiOne one = null;

	private static final String ldap_domain = "ldap://xxx:389/";
	private static final String ldap_racine = "dc=xxx";
	private static final String ldap_user = "cn=xxx,ou=xxx,dc=xxx";
	private static final String ldap_pwd = "xxx";

	@Before
	public void setup() throws ObiOneException, ObiSchemaAccessException, ObiConnectionException, ObiNamingException {
		one = new ObiOne(ldap_domain, ldap_racine, ldap_user, ldap_pwd, null, null, null);
	}

	@After
	public void cleanup() throws ObiOneException {
		assertNotNull(one);

		one.closeConnections();
	}

	@Ignore
	@Test
	public void getPersonData() throws ObiServiceException, ObiConnectionException, ObiInvalidDnException, ObiOneException {
		assertNotNull(one);

		// Recuperation du service de personne
		ObiPersonneService personneService = one.getPersonService();

		// Recuperation des data de personne
		ObiPersonneData personneData = personneService.get(ldap_user);

		personneData.dataSize();
	}

	@Ignore
	@Test
	public void findUserData() throws ObiServiceException, ObiConnectionException, ObiInvalidDnException, ObiOneException {
		assertNotNull(one);
		
		// Recuperation du service de personne
		ObiUserService userService = one.getUserService();

		//Recherche du compte de humeau_x
		List<ObiUserData> list = userService.findByFilter("sAMAccountName", "humeau_x");

		assertEquals(list.size(), 1);
	}

	@Test
	public void checkUserConnection() throws ObiServiceException {
		assertNotNull(one);

		// Recuperation du service de personne
		ObiUserService userService = one.getUserService();

		//Test des parametres d'authentification
		boolean test = userService.checkAuthentification("humeau_x", "Village");

		assertEquals(test, true);
	}
	
	@Ignore
	@Test
	public void test() throws JediException, ObiServiceException, ObiConnectionException, ObiDataException {
		ObiUserService userService = one.getUserService();
		
		List<String> attrList = new ArrayList<String>();
	    attrList.add("givenName");
	    attrList.add("sn");
	    attrList.add("displayName");
	    attrList.add("sAMAccountName");
	    attrList.add("distinguishedName");

		JediFilter filtre = new JediFilter();
		filtre.setAlias(one.getDirectoryAlias());
		filtre.setPath("");
		filtre.setSubtree(true);
		filtre.setPageSize(900);
		filtre.setAttributesList(attrList);

		List<ObiUserData> obiUserDataList = userService.findObiDataByFilter(filtre);
		
		for (ObiUserData obiUserData : obiUserDataList) {
			System.out.println(obiUserData.getValue("givenName"));
	    	System.out.println(obiUserData.getValue("sn"));
	    	System.out.println(obiUserData.getValue("displayName"));
	    	System.out.println(obiUserData.getValue("sAMAccountName"));
	    	System.out.println(obiUserData.getValue("distinguishedName"));
	
	    	System.out.println("***********************************");
		}
	}
	
}