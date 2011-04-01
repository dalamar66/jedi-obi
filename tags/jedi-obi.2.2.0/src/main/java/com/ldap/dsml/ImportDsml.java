package com.ldap.dsml;

/**
 * File                 : ExportDsml.java
 * Component            :
 * Version              : 1.0
 * Creation date        : 2011-03-17
 * Modification date    : 2011-03-17
 *
 * Classe servant à importer un fichier DSML.
 *
 * @author    HUMEAU Xavier
 * @version   Version 1.0
 */

import java.io.*;
import java.util.*;

import org.w3c.dom.*;
import java.text.SimpleDateFormat;
import com.ldap.jedi.JediLog;
import com.ldap.obi.ObiOne;

public class ImportDsml {

	private static final String className = ExportDsml.class.getName();

    public static void main (String[] args) {
        try {
        	Properties prop = new Properties();
			prop.load(new FileInputStream(args[0]));
        	
			String propDomain = prop.getProperty("com.ldap.dsml.directoryserver");
			String propRacine = prop.getProperty("com.ldap.dsml.directoryrootpath");
			String propUser = prop.getProperty("com.ldap.dsml.directoryuser");
			String propPwd = prop.getProperty("com.ldap.dsml.directorypassword");

			ObiOne obiOne = new ObiOne(propDomain, propRacine, propUser, propPwd, null, null, null);

			Document document = DsmlAdapter.load(args[1]);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
            String currentDate = simpleDateFormat.format(new java.util.Date());

            JediLog.log(JediLog.LOG_PRODUCTION, JediLog.INFO, "\n****************************************", "", className);
            JediLog.log(JediLog.LOG_PRODUCTION, JediLog.INFO, "IMPORTATION du ", currentDate, className);
            JediLog.log(JediLog.LOG_PRODUCTION, JediLog.INFO, "", "", className);
            JediLog.log(JediLog.LOG_PRODUCTION, JediLog.INFO, "\tcom.byconst.ref.dsml.directoryserver : ", propDomain, className);
            JediLog.log(JediLog.LOG_PRODUCTION, JediLog.INFO, "\tcom.byconst.ref.dsml.directorypassword : ", propPwd, className);
            JediLog.log(JediLog.LOG_PRODUCTION, JediLog.INFO, "\tcom.byconst.ref.dsml.directoryrootpath : ", propRacine, className);
            JediLog.log(JediLog.LOG_PRODUCTION, JediLog.INFO, "\tcom.byconst.ref.dsml.directoryuser : ", propUser, className);
            JediLog.log(JediLog.LOG_PRODUCTION, JediLog.INFO, "\n\tfichier à importer : ", args[1], className);

            JediLog.log(JediLog.LOG_FUNCTIONAL, JediLog.INFO, "\n****************************************", "", className);
            JediLog.log(JediLog.LOG_FUNCTIONAL, JediLog.INFO, "IMPORTATION du ", currentDate, className);
            JediLog.log(JediLog.LOG_FUNCTIONAL, JediLog.INFO, "", "", className);

            DsmlAdapter.setDocument(document, obiOne.getServer(), obiOne.getDirectoryAlias());
        } catch (Exception ex) {
            System.out.println("ERREUR LORS DE L'EXECUTION");
        }
    }

}
