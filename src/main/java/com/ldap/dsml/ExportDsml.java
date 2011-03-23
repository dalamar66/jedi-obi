package com.ldap.dsml;

/**
 * File                 : ExportDsml.java
 * Component            :
 * Version              : 1.0
 * Creation date        : 2011-03-16
 * Modification date    : 2001-03-17
 * 
 * Classe servant à exporter une branche d'une base Ldap vers un fichier DSML.
 *
 * @author    HUMEAU Xavier
 * @version   Version 1.0
 */

import java.io.*;
import java.util.*;

import com.ldap.jedi.JediFilter;
import com.ldap.jedi.JediLog;
import com.ldap.jedi.JediObject;
import com.ldap.obi.ObiOne;

import java.text.SimpleDateFormat;

public class ExportDsml {

	private static final String className = ExportDsml.class.getName();

	public static void main(String[] args) {
		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream(args[0]));

			String propDomain = prop.getProperty("com.ldap.dsml.directoryserver");
			String propRacine = prop.getProperty("com.ldap.dsml.directoryrootpath");
			String propUser = prop.getProperty("com.ldap.dsml.directoryuser");
			String propPwd = prop.getProperty("com.ldap.dsml.directorypassword");
			String propFilter = prop.getProperty("com.ldap.dsml.filter");
			String propAttributes = prop.getProperty("com.ldap.dsml.attributerequired");

			if (propFilter == null || propFilter.equalsIgnoreCase("")) {
				propFilter = "(objectClass=*)";
			}

			ObiOne obiOne = new ObiOne(propDomain, propRacine, propUser, propPwd, null, null, null);

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
			String currentDate = simpleDateFormat.format(new java.util.Date());

			JediLog.log(JediLog.LOG_PRODUCTION, JediLog.INFO, "\n****************************************", "", className);
			JediLog.log(JediLog.LOG_PRODUCTION, JediLog.INFO, "EXPORT du ", currentDate, className);
			JediLog.log(JediLog.LOG_PRODUCTION, JediLog.INFO, "", "", className);
			JediLog.log(JediLog.LOG_PRODUCTION, JediLog.INFO, "\tcom.ldap.dsml.directoryserver : ", propDomain, className);
			JediLog.log(JediLog.LOG_PRODUCTION, JediLog.INFO, "\tcom.ldap.dsml.directoryrootpath : ", propRacine, className);
			JediLog.log(JediLog.LOG_PRODUCTION, JediLog.INFO, "\tcom.ldap.dsml.directoryuser : ", propUser, className);
			JediLog.log(JediLog.LOG_PRODUCTION, JediLog.INFO, "\tcom.ldap.dsml.filter : ", propFilter, className);
			JediLog.log(JediLog.LOG_PRODUCTION, JediLog.INFO, "\tcom.ldap.dsml.attributerequired : ", propAttributes, className);
			JediLog.log(JediLog.LOG_PRODUCTION, JediLog.INFO, "\n\tfichier de destination : ", args[1], className);

			JediLog.log(JediLog.LOG_FUNCTIONAL, JediLog.INFO, "\n****************************************", "", className);
			JediLog.log(JediLog.LOG_FUNCTIONAL, JediLog.INFO, "EXPORT du ", currentDate, className);
			JediLog.log(JediLog.LOG_FUNCTIONAL, JediLog.INFO, "", "", className);
			JediLog.log(JediLog.LOG_FUNCTIONAL, JediLog.INFO, "", "", className);
			JediLog.log(JediLog.LOG_FUNCTIONAL, JediLog.INFO, "Recherche des objets correspondants au fichier properties : ", propFilter, className);

			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "", "", obiOne.getServer());
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "Recherche des objets correspondants au fichier properties : ", propFilter, className);

			JediFilter jediFilter = new JediFilter();
			jediFilter.setAlias(obiOne.getDirectoryAlias());
			jediFilter.setAttributesList(getRequiredAttributes(propAttributes));
			jediFilter.setPath("");
            jediFilter.setSubtree(true);
            jediFilter.setPageSize(900);
            jediFilter.setFilter(propFilter);

            List<JediObject> resSearch = obiOne.getServer().findByFilter(jediFilter);

			DsmlAdapter.getDocument(resSearch, args[1]);
		} catch (Exception ex) {
			System.out.println("ERREUR LORS DE L'EXECUTION");
		}
	}

	/**
	 * Methode permettant de transformer la liste des attributs en une liste
	 * 
	 * @param propAttributes
	 * @return
	 * @throws DsmlAdapterException
	 */
	private static List<String> getRequiredAttributes(String propAttributes) throws DsmlAdapterException {
		List<String> requiredAttributes = null;

		if (propAttributes != null && propAttributes.trim().equalsIgnoreCase("") == false) {
			requiredAttributes = new ArrayList<String>();

			StringTokenizer temp = new StringTokenizer(propAttributes, ",");
			while (temp.hasMoreElements()) {
				requiredAttributes.add((String)temp.nextElement());
			}
		}

		return requiredAttributes;
	}

}
