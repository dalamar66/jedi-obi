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
		// Ouverture de la connection
		one = new ObiOne(ldap_domain, ldap_racine, ldap_user, ldap_pwd, null, null, null);
	}

	@After
	public void cleanup() throws ObiOneException {
		assertNotNull(one);

		// Fermeture de la connection
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

		// Recherche du compte de humeau_x
		List<ObiUserData> list = userService.findByFilter("sAMAccountName", "humeau_x");

		assertEquals(list.size(), 1);
	}

	@Test
	public void checkUserConnection() throws ObiServiceException {
		assertNotNull(one);

		// Recuperation du service de personne
		ObiUserService userService = one.getUserService();

		// Test des parametres d'authentification
		boolean test = userService.checkAuthentification("humeau_x", "Village");

		assertEquals(test, true);
	}
	
	@Ignore
	@Test
	public void searchUsers() throws JediException, ObiServiceException, ObiConnectionException, ObiDataException {
		assertNotNull(one);

		// Recuperation du service de personne
		ObiUserService userService = one.getUserService();

		// Liste des attributs que l'on veut recuperer
		List<String> attrList = new ArrayList<String>();
	    attrList.add("givenName");
	    attrList.add("sn");
	    attrList.add("displayName");
	    attrList.add("sAMAccountName");
	    attrList.add("distinguishedName");

	    // Creation du filtre
		JediFilter filtre = new JediFilter();
		filtre.setAlias(one.getDirectoryAlias());//Definition de la connection
		filtre.setPath("");//Definition du chemin � ajouter au ldap_racine a partir duquel sera fait la recherche
		filtre.setSubtree(true);//La recherche doit se faire dans tous les descendants
		filtre.setPageSize(900);//Definition de la pagination de la recherche
		filtre.setAttributesList(attrList);//Affectation de la liste des attributs a rappatri�s

		// Application du filtre qui sera fait automatiquement sur les user
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