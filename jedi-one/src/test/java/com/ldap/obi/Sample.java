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

   private static final String ldap_domain = "ldap://insitu.ad.exane.com:389/";
   private static final String ldap_racine = "dc=insitu,dc=ad,dc=exane,dc=com";
   private static final String ldap_user = "cn=svc-ceweb,ou=services,ou=accounts,dc=insitu,dc=ad,dc=exane,dc=com";
   private static final String ldap_pwd = "k43AbfK";

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
	boolean test = false;

	try {
	   for (int i = 0; i < 100; i++) {
		System.out.println("****************************************************************************");
		System.out.println("ITERATION : " + (i + 1));
		System.out.println("****************************************************************************");
		test = userService.checkAuthentification("humeau_x", "Village#50");
		System.out.println("humeau_x : " + test);
		// Thread.sleep(500);
		test = userService.checkAuthentification("thao_y", "Exane*-2014");
		System.out.println("thao_y : " + test);
		// Thread.sleep(500);
		test = userService.checkAuthentification("spinosa_a", "ASP_avr_2014");
		System.out.println("spinosa_a : " + test);
		// Thread.sleep(500);
		test = userService.checkAuthentification("trzewik_t", "Jmai2qbpJmai2qbp");
		System.out.println("trzewik_t : " + test);
		// Thread.sleep(500);
		test = userService.checkAuthentification("demange_l", "Luch0407#3");
		System.out.println("demange_l : " + test);
		// Thread.sleep(500);
		test = userService.checkAuthentification("cornet_h", "E2x2a2n2e2/");
		System.out.println("cornet_h : " + test);
		// Thread.sleep(500);
	   }
	} catch (Exception e) {
	   System.out.println(e.getMessage());
	}

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
	filtre.setAlias(one.getDirectoryAlias());// Definition de la connection
	filtre.setPath("");// Definition du chemin à ajouter au ldap_racine a partir duquel sera fait la recherche
	filtre.setSubtree(true);// La recherche doit se faire dans tous les descendants
	filtre.setPageSize(900);// Definition de la pagination de la recherche
	filtre.setAttributesList(attrList);// Affectation de la liste des attributs a rappatriés

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

   @Test
   @Ignore
   public void test() {
	String samAccountName = "*";

	// Liste des attributs que l'on veut recuperer
	final List<String> attrList = new ArrayList<String>();
	attrList.add("samaccountname");
	attrList.add("sn");
	attrList.add("givenName");
	attrList.add("mail");
	attrList.add("distinguishedName");

	final JediFilter jediFilter = new JediFilter();
	jediFilter.setAlias(one.getDirectoryAlias());
	jediFilter.setPath("");
	jediFilter
		.setFilter("(&(objectCategory=Person)(objectClass=user)(!(userAccountControl:1.2.840.113556.1.4.803:=2))(!(company=-1))(!(samaccountname=Adm-*))(samaccountname="
			+ samAccountName + "))");
	jediFilter.setAttributesList(attrList);
	jediFilter.setSubtree(true);
	jediFilter.setPageSize(900);

	try {
	   final List<ObiUserData> obiUserDataList = one.getUserService().findObiDataByFilter(jediFilter);

	   System.out.println(obiUserDataList.size());

	   for (final ObiUserData obiUserData : obiUserDataList) {
		final String dn = obiUserData.getValue("distinguishedName");
		System.out.println(dn);
		// if (dn.toLowerCase().indexOf("obsolete") != -1) {
		// continue;
		// }
	   }
	} catch (ObiServiceException e) {
	   // TODO Auto-generated catch block
	   e.printStackTrace();
	} catch (ObiConnectionException e) {
	   // TODO Auto-generated catch block
	   e.printStackTrace();
	} catch (ObiDataException e) {
	   // TODO Auto-generated catch block
	   e.printStackTrace();
	}

   }

}