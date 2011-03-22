package com.ldap.dsml;

/**
 * File                 : ExportDsml.java
 * Component            :
 * Version              : 1.0
 * Creation date        : 2011-03-17
 * Modification date    : 2011-03-17
 *
 * Classe servant à exporter une branche d'une base LDap vers un fichier XML.
 *
 * @author    HUMEAU Xavier
 * @version   Version 1.0
 */

import org.w3c.dom.*;

import com.ldap.jedi.JediLog;

import java.text.SimpleDateFormat;

public class DiffDsml {

	private static final String className = ExportDsml.class.getName();

    public static void main (String[] args) {
        try {
        	Document documentReference = DsmlAdapter.load(args[0]);
        	Document documentToCompare = DsmlAdapter.load(args[1]);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
            String currentDate = simpleDateFormat.format(new java.util.Date());

            JediLog.log(JediLog.LOG_PRODUCTION, JediLog.INFO, "\n****************************************", "", className);
            JediLog.log(JediLog.LOG_PRODUCTION, JediLog.INFO, "DIFFERENTIEL du ", currentDate, className);
            JediLog.log(JediLog.LOG_PRODUCTION, JediLog.INFO, "", "", className);
            JediLog.log(JediLog.LOG_PRODUCTION, JediLog.INFO, "\tFichier DSML de reference : ", args[0], className);
            JediLog.log(JediLog.LOG_PRODUCTION, JediLog.INFO, "\tFichier DSML à comparer : ", args[1], className);
            JediLog.log(JediLog.LOG_PRODUCTION, JediLog.INFO, "\tComparateur : ", args[2], className);
            JediLog.log(JediLog.LOG_PRODUCTION, JediLog.INFO, "\tFichier DSML de sortie : ", args[3], className);

            JediLog.log(JediLog.LOG_FUNCTIONAL, JediLog.INFO, "\n****************************************", "", className);
            JediLog.log(JediLog.LOG_FUNCTIONAL, JediLog.INFO, "DIFFERENTIEL du ", currentDate, className);
            JediLog.log(JediLog.LOG_FUNCTIONAL, JediLog.INFO, "", "", className);

            DsmlAdapter.diff(documentReference, documentToCompare, args[2], args[3]);
        } catch (Exception e) {
            System.out.println("ERREUR LORS DE L'EXECUTION");
        }
    }

}
