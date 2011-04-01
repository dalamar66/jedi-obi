package com.ldap.jedi;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class Sample extends Assert {

	JediServer server = null;

	private static final String ldap_domain = "ldap://xxx:389/";
	private static final String ldap_racine = "dc=xxx";
	private static final String ldap_user = "cn=xxx,ou=xxx,dc=xxx";
	private static final String ldap_pwd = "xxx";
	private static final String ldap_connectionName = "authentification";

	@Before
	public void setup() throws JediException {
		server = new JediServer();

		// Creation de la connection
		server.addConnectionParameters(ldap_connectionName, ldap_domain, new JediPath(ldap_racine), new JediPath(ldap_user), ldap_pwd);
	}
	
	@After
	public void cleanup() throws JediException, JediConnectionException {
		assertNotNull(server);

		// Fermeture de la connection
		server.closeConnection();
	}

	@Test
	public void basicSearch() throws JediException, JediConnectionException {
		assertNotNull(server);

		// Liste des attributs que l'on veut recuperer
		List<String> attList = new ArrayList<String>();
		attList.add("sn");
		attList.add("givenName");
		attList.add("name");
		attList.add("sAMAccountName");
		
	    // Creation du filtre
		JediFilter jediFilter = new JediFilter();
		jediFilter.setAlias(ldap_connectionName);//Definition de la connection
		jediFilter.setPath("");//Definition du chemin à ajouter au ldap_racine a partir duquel sera fait la recherche
		jediFilter.setAttributesList(attList);//Affectation de la liste des attributs a rappatriés
		jediFilter.setFilter("(&(&(objectClass=user)(!(objectClass=computer)))(sAMAccountName=*))");//Criteres de filtre
		jediFilter.setSubtree(true);//La recherche doit se faire dans tous les descendants
		jediFilter.setPageSize(900);//Definition de la pagination de la recherche

		// Application du filtre
		List<JediObject> list = server.findByFilter(jediFilter);

		assertNotNull(list);
		assertEquals(1, list.size());
	}

	@Ignore
	@Test
	public void advancedSearch() throws JediException, JediConnectionException {
		assertNotNull(server);

		JediFilter jediFilter = new JediFilter();
		jediFilter.setAlias(ldap_connectionName);//Definition de la connection
		jediFilter.setPath("ou=Paris");//Definition du chemin à ajouter au ldap_racine a partir duquel sera fait la recherche
		jediFilter.setAttributesList(null);//Affectation de la liste des attributs a rappatriés : Tous
		jediFilter.setFilter("(objectClass=user)");//Criteres de filtre
		jediFilter.setSubtree(true);//La recherche doit se faire dans tous les descendants

		jediFilter.setApproximation("humo_x");//Valeur a approximer
		jediFilter.setIndice(80);//Indice de rapprochement
		jediFilter.setAttribute("sAMAccountName");//Attribut a approximer
		jediFilter.setMetric(JediFilterConstants.METRIC_JARO);//Methode d'approximation

		jediFilter.setPageSize(900);//Definition de la pagination de la recherche
		jediFilter.setLimitation(100);//Limitation des resultats au 100 premiers
		jediFilter.setSorted(new JediObjectComparator(JediObjectComparator.DN));//Definition du critere de tri

		// Application du filtre
		List<JediObject> list = server.findByFilter(jediFilter);

		assertNotNull(list);
	}

	@Ignore
	@Test
	public void objectPath() throws JediException, JediConnectionException {
		assertNotNull(server);

		JediFilter jediFilter = new JediFilter();
		jediFilter.setAlias(ldap_connectionName);//Definition de la connection
		jediFilter.setPath("");//Definition du chemin à ajouter au ldap_racine a partir duquel sera fait la recherche
		jediFilter.setAttributesList(new ArrayList<String>());//Affectation de la liste des attributs a rappatriés : Aucun
		jediFilter.setFilter("(&(objectClass=user)(sAMAccountName=humeau_x))");//Criteres de filtre
		jediFilter.setSubtree(true);//La recherche doit se faire dans tous les descendants

		// Application du filtre
		List<JediObject> list = server.findByFilter(jediFilter);
		
		//Recuperation des differentes valeurs de path
		if (list != null && list.isEmpty() == false) {
			if (list.size() == 1) {
				JediObject jediObject = list.get(0);

				System.out.println("getCompleteDN : " + jediObject.getCompleteDN());
				System.out.println("getPartialDN : " + jediObject.getPartialDN());
				System.out.println("getPartialDNWihoutRac : " + jediObject.getPartialDNWihoutRac());
				System.out.println("getCompleteNode : " + jediObject.getCompleteNode());
				System.out.println("getPartialNode : " + jediObject.getPartialNode());
				System.out.println("getPartialNodeWihoutRac : " + jediObject.getPartialNodeWihoutRac());

				System.out.println("getRDN : " + jediObject.getRDN());

				System.out.println("getJediCompleteDN : " + jediObject.getJediCompleteDN());
				System.out.println("getJediPartialDNWihoutRac : " + jediObject.getJediPartialDNWihoutRac());
				System.out.println("getJediCompleteNode : " + jediObject.getJediCompleteNode());
				System.out.println("getJediPartialNodeWihoutRac : " + jediObject.getJediPartialNodeWihoutRac());

				System.out.println("getJediRDN : " + jediObject.getJediRDN());
			}
		}
	}

}
