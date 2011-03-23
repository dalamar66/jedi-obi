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
		server.addConnectionParameters(ldap_connectionName, ldap_domain, new JediPath(ldap_racine), new JediPath(ldap_user), ldap_pwd);
	}
	
	@After
	public void cleanup() throws JediException, JediConnectionException {
		assertNotNull(server);

		server.closeConnection();
	}

	@Test
	public void basicSearch() throws JediException, JediConnectionException {
		assertNotNull(server);

		String ldap_baseSearch = "";
		String requestSearchUser = "(&(&(objectClass=user)(!(objectClass=computer)))(sAMAccountName=*))";

		List<String> attList = new ArrayList<String>();
		attList.add("sn");
		attList.add("givenName");
		attList.add("name");
		attList.add("sAMAccountName");
		
		JediFilter jediFilter = new JediFilter();
		jediFilter.setAlias(ldap_connectionName);
		jediFilter.setPath(ldap_baseSearch);
		jediFilter.setAttributesList(attList);
		jediFilter.setFilter(requestSearchUser);
		jediFilter.setSubtree(true);
		jediFilter.setPageSize(900);

		List<JediObject> list = server.findByFilter(jediFilter);

		assertNotNull(list);
		assertEquals(1, list.size());
	}

	@Ignore
	@Test
	public void advancedSearch() throws JediException, JediConnectionException {
		assertNotNull(server);

		JediFilter jediFilter = new JediFilter();
		jediFilter.setAlias(ldap_connectionName);
		jediFilter.setPath("ou=Paris");
		jediFilter.setAttributesList(null);
		jediFilter.setFilter("(objectClass=user)");
		jediFilter.setSubtree(true);

		jediFilter.setApproximation("humo_x");
		jediFilter.setIndice(80);
		jediFilter.setAttribute("sAMAccountName");
		jediFilter.setMetric(JediFilterConstants.METRIC_JARO);
		
		jediFilter.setPageSize(900);
		jediFilter.setLimitation(100);
		jediFilter.setSorted(new JediObjectComparator(JediObjectComparator.DN));

		List<JediObject> list = server.findByFilter(jediFilter);

		assertNotNull(list);
	}

	@Ignore
	@Test
	public void objectPath() throws JediException, JediConnectionException {
		assertNotNull(server);

		JediFilter jediFilter = new JediFilter();
		jediFilter.setAlias(ldap_connectionName);
		jediFilter.setPath("");
		jediFilter.setAttributesList(new ArrayList<String>());
		jediFilter.setFilter("(&(objectClass=user)(sAMAccountName=humeau_x))");
		jediFilter.setSubtree(true);

		List<JediObject> list = server.findByFilter(jediFilter);
		
		if (list != null && list.isEmpty() == false) {
			if (list.size() == 1) {
				JediObject jediObject = list.get(0);
				
				jediObject.getCompleteDN();
				jediObject.getPartialDN();
				jediObject.getPartialDNWihoutRac();
				jediObject.getCompleteNode();
				jediObject.getPartialNode();
				jediObject.getPartialNodeWihoutRac();

				jediObject.getRDN();
				
				jediObject.getJediCompleteDN();
				jediObject.getJediPartialDNWihoutRac();
				jediObject.getJediCompleteNode();
				jediObject.getJediPartialNodeWihoutRac();

				jediObject.getJediRDN();
			}
		}
	}

	@Ignore
	@Test
	public void path() throws JediException, JediConnectionException {
		assertNotNull(server);

		JediFilter jediFilter = new JediFilter();
		jediFilter.setAlias(ldap_connectionName);
		jediFilter.setPath("");
		jediFilter.setAttributesList(new ArrayList<String>());
		jediFilter.setFilter("(&(objectClass=user)(sAMAccountName=humeau_x))");
		jediFilter.setSubtree(true);

		List<JediObject> list = server.findByFilter(jediFilter);
		
		if (list != null && list.isEmpty() == false) {
			if (list.size() == 1) {
				JediObject jediObject = list.get(0);
				
				JediPath jediPath = jediObject.getJediPartialDNWihoutRac();

				jediPath.get(0);
				jediPath.getDN();
				jediPath.getNode();
				jediPath.getRDN();
				jediPath.getPathSize();
			}
		}
	}

	@Test(expected=JediException.class) @Ignore
	public void sampleJUnit() throws JediException, JediConnectionException {
		throw new JediException("");
		//fail("toto");
	}

}
