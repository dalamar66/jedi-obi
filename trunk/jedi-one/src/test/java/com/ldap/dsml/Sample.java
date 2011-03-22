package com.ldap.dsml;

import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.ldap.jedi.JediConnectionException;
import com.ldap.jedi.JediException;
import com.ldap.jedi.JediFilter;
import com.ldap.jedi.JediObject;
import com.ldap.obi.ObiConnectionException;
import com.ldap.obi.ObiNamingException;
import com.ldap.obi.ObiOne;
import com.ldap.obi.ObiOneException;
import com.ldap.obi.ObiSchemaAccessException;

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
	public void getDocument() throws JediException, JediConnectionException, DsmlAdapterException {
		assertNotNull(one);
		
		JediFilter jediFilter = new JediFilter();
		jediFilter.setAlias(one.getDirectoryAlias());
		jediFilter.setPath("cn=schema,cn=configuration");
		jediFilter.setSubtree(true);
		jediFilter.setPageSize(900);
		jediFilter.setFilter("(|(cn=BCC-*)(cn=BCA-*))");

		List<JediObject> jediObjectList = one.getServer().findByFilter(jediFilter);
		DsmlAdapter.getDocument(jediObjectList, "c:\\export.xml");
	}
	
	@Ignore
	@Test
	public void setDocument() {
		assertNotNull(one);

	}

	@Ignore
	@Test
	public void diff() {
		assertNotNull(one);

	}
	
}