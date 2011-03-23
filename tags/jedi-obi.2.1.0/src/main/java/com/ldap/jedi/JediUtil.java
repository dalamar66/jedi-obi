package com.ldap.jedi;

/**
 * File : JediUtil.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-04 
 * Modification date : 2010-03-04
 */

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;

import javax.naming.NamingEnumeration;

/**
 * Classe contenant l'ensemble des méthodes nécessaires pour le traitement des caractères spéciaux ainsi que des méthodes attachés à aucun objet du package.
 * 
 * @author HUMEAU Xavier
 * @version Version 1.0
 */
public class JediUtil {

	/**
	 * Constante de séparation pour les chemins LDAP.
	 */
	public static final String SEPARATOR = ",";

	/**
	 * Constante fixant le temps limite de recherche (en ms).
	 */
	protected static int SEARCH_TIME_LIMIT = 300000;

	/**
	 * Constante specifiant l'affichage ou non de la trace.
	 */
	protected static boolean CST_PRINT_TRACE = false;

	/**
	 * Constante specifiant le débugage ou non.
	 */
	protected static boolean CST_PRINT_DEBUG = false;

	// *****************************************************************************************************
	//
	// VALUATION DES PARAMS
	//
	// *****************************************************************************************************

	/**
	 * Méthode qui retourne le temps limite fixé.
	 * 
	 * @return Le temps fixé.
	 */
	public static int getTimeLimit() {
		return (SEARCH_TIME_LIMIT);
	}

	/**
	 * Méthode qui spécifie combien de temps la recherche doit durer au maximum. Si le temps est égal à 0 alors le temps est infini.
	 * 
	 * @param time
	 *            Le temps que la recherche ne doit pas dépasser.
	 * @throws JediException
	 *             Si le temps passé en paramètre est négatif.
	 */
	public static void setTimeLimit(int time) throws JediException {
		if (time < 0) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", Integer.toString(time), null);

			throw new JediException("JediUtil : setTimeLimit(int) : paramètre d'initialisation incorrect");
		}

		SEARCH_TIME_LIMIT = time;
	}

	/**
	 * Méthode qui indique si on affiche la trace.
	 * 
	 * @return Si il y a ou non affichage de la trace.
	 */
	public static boolean getWithTrace() {
		return (CST_PRINT_TRACE);
	}

	/**
	 * Méthode qui spécifie si on veut ou non afficher la trace.
	 * 
	 * @param trace
	 *            Spécifie si on affiche la trace.
	 */
	public static void setWithTrace(boolean trace) {
		CST_PRINT_TRACE = trace;
	}

	/**
	 * Méthode qui indique si on debug.
	 * 
	 * @return Si il y a ou non un debugage.
	 */
	public static boolean getDebug() {
		return (CST_PRINT_DEBUG);
	}

	/**
	 * Méthode qui spécifie si on veut ou non débuger.
	 * 
	 * @param trace
	 *            Spécifie si on debug.
	 */
	public static void setDebug(boolean deb) {
		CST_PRINT_DEBUG = deb;

		if (deb == true) {
			JediUtil.setWithTrace(true);
		}
	}

	// *****************************************************************************************************
	//
	// GESTION DES PROPERTIES
	//
	// *****************************************************************************************************

	/**
	 * Méthode permettant de charger un properties
	 * 
	 * @param name
	 *            L'URL du properties
	 * @return Un properties
	 */
	public static Properties loadProperties(String name) throws JediException {
		URL url = null;
		InputStream conf = null;
		Properties prop = new Properties();
		BufferedInputStream bconf = null;

		// Verification de la validité des paramètres
		if (name == null) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", "", null);

			throw new JediException("URI nulle");
		}
		// Try to open URL connection first
		try {
			url = new URL(name);
			conf = url.openStream();
		} catch (MalformedURLException e) {
			// Try to open plain file, if `name' is not a URL specification
			try {
				conf = new FileInputStream(name);
			} catch (FileNotFoundException ex) {
				JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_internal_error", "", null);

				throw new JediException("Le fichier de configuration est introuvable");
			}
		} catch (IOException ex) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_internal_error", "", null);

			throw new JediException("Le fichier de configuration est inaccessible");
		}

		if (conf == null) {
			return null;
		}

		try {
			bconf = new BufferedInputStream(conf);
			prop.load(bconf);
			conf.close();
		} catch (IOException ex) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_internal_error", "", null);

			throw new JediException("Erreur en cours de lecture du fichier de configuration");
		}

		return prop;
	}

	/**
	 * 
	 * @param fichierProperties
	 * @return
	 * @throws Exception
	 */
	public static Properties getPropsFromFile(String fichierProperties) throws Exception {
		Properties props = null;
		InputStream is = JediUtil.class.getResourceAsStream(fichierProperties);
		if (is == null) {
			throw new Exception();
		}
		props = new Properties();
		props.load(is);
		return props;
	}

	// *****************************************************************************************************
	//
	// GESTION DES CARACTERES SPECIAUX
	//
	// *****************************************************************************************************

	/**
	 * Tableau référençant les caractères nécessitant un caractère d'échappement au sein de la méthode otherToLdap.
	 */
	protected static char[] SPECIALS_CARACTERES = { '/', '\\', '<', '>', '+', '#', ';', ',', '\"' };

	/**
	 * Méthode ajoutant un caractère d'échappement devant tous les caractères de stringToModify que l'on retrouve dans le tableau arrayWithoutRead.
	 * 
	 * @param stringToModify
	 *            String à modifier.
	 * @return Le String passé en paramètre avec des caractères d'échappement.
	 * @throws JediException
	 *             Si le String est null ou vide.
	 */
	public static String formatForLdap(String stringToModify) throws JediException {
		boolean charIsSpecial = false;
		boolean charIsVerySpecial = false;

		int indexChar = 0;

		int stringToModifyLength = stringToModify.length();

		// Verification de la validité des paramètres
		if (stringToModify == null || stringToModify.length() == 0) {
			throw new JediException("JediUtil : otherToLdap(String) : Paramètre d'initialisation incorrect");
		}

		// Pour chaque caratère on parcours le tableau référençant les caractères à traiter (arrayWithoutRead)
		while (indexChar < stringToModifyLength) {
			charIsSpecial = false;
			charIsVerySpecial = false;

			// On parcours le tableau pour chaque caractère du mot
			for (int i = 0; i < SPECIALS_CARACTERES.length; i++) {
				// Si le caractère apparait dans le tableau on le notifie
				charIsSpecial = charIsSpecial || (stringToModify.charAt(indexChar) == SPECIALS_CARACTERES[i]);
				// Si le caractère est un "\" on le notifie aussi car il pose un problème d'incompatibilité
				// avec d'autres caractères
				charIsVerySpecial = charIsVerySpecial || (stringToModify.charAt(indexChar) == '\"');
			}

			// Si on trouve un caractère spécial sans trouver de caractère "tres spécial" on ajoute "\\" devant
			// le caractère spécial
			if (charIsSpecial == true && charIsVerySpecial == false) {
				stringToModify = stringToModify.substring(0, indexChar) + "\\" + stringToModify.substring(indexChar, stringToModifyLength);
				indexChar++;
				stringToModifyLength++;
			}

			// Si on a relevé un "\" et que l'on n'est pas à la fin du mot alors on précède le caractère par "\"
			// et on conctène la chaine suivant le caractère spécial
			if (charIsSpecial == true && charIsVerySpecial == true && indexChar != stringToModifyLength) {
				stringToModify = stringToModify.substring(0, indexChar) + "\'" + stringToModify.substring(indexChar + 1, stringToModifyLength);
			}

			// Si on a relevé un "\" et que l'on est à la fin du mot alors on le précède de "\"
			if (charIsSpecial == true && charIsVerySpecial == true && indexChar == stringToModifyLength) {
				stringToModify = stringToModify.substring(0, indexChar) + "\'";
			}
			indexChar++;
		}

		return (stringToModify);
	}

	/**
	 * Méthode qui détecte si le String passé en paramètre contient à la fois des caractères du tableau arraySpecial et du tableau arrayIncompatible car ces
	 * deux tableaux sont incompatibles au sein d'un même String.
	 * 
	 * @param stringToModify
	 *            String à analyser.
	 * @return Si le String contient des caractères incompatibles ou non.
	 * @throws JediException
	 *             Si le String est null ou vide.
	 */
	public static boolean searchIncompatibility(String stringToModify) throws JediException {
		boolean charIsSpecial = false;
		boolean charIncompatible = false;

		char charAtIndex = 0;
		int indexChar = 0;

		// Verification de la validité des paramètres
		if (stringToModify == null) {
			throw new JediException("JediUtil : searchIncompatibility(String) : Paramètre d'initialisation incorrect");
		}

		// Pour chaque caratère on parcours le tableau référençant les caractères spéciaux (arraySpecial) et
		// le tableau des caractères incompatibles
		while (indexChar < stringToModify.length()) {
			// On parcours le tableau des caractères spéciaux pour chaque caractère. Si on en trouve un on le notifie
			charIsSpecial = charIsSpecial || (stringToModify.charAt(indexChar) == '/');

			// On parcours le tableau des caractères incompatibles pour chaque caractère
			// Si on est sur le dernier caractère on regarde si le caractère apparait dans le tableau incompatible
			if (indexChar == stringToModify.length() - 1) {
				charIncompatible = charIncompatible || (stringToModify.charAt(indexChar) == '\\');
			}
			// Si on n'est pas sur le dernier caractère on regarde s'il est dans le tableau incompatible
			else {
				charAtIndex = (stringToModify.charAt(indexChar + 1));
				charIncompatible = charIncompatible || ((stringToModify.charAt(indexChar) == '\\') && (Arrays.binarySearch(SPECIALS_CARACTERES, charAtIndex) < 0));
			}
			indexChar++;
		}

		return (charIsSpecial && charIncompatible);
	}

	// *****************************************************************************************************
	//
	// GESTION DES ATTRIBUTS
	//
	// *****************************************************************************************************

	/**
	 * Méthode qui supprime de la liste tous les attributs ayant une valeur nulle.
	 * 
	 * @param attributeList
	 *            La liste dans laquelle on veut supprimer les elements nuls.
	 * @throws JediException
	 *             Si il y a une erreur de suppression
	 */
	public static void removeNullValueFromList(JediAttributeList attributeList) throws JediException {
		// Recuperation des clés des attributs de la liste
		NamingEnumeration<String> ne = attributeList.getIDs();

		String nameAttribute = null;

		try {
			while (ne.hasMoreElements()) {
				// Recuperation de la cle de l'attribut sous forme de String
				nameAttribute = ne.nextElement();
				try {
					// Si la valeur de l'attibut est nulle alors on supprime l'attribut de la liste
					if (attributeList.get(nameAttribute).get() == null) {
						attributeList.remove(nameAttribute);
					}
				} catch (Exception e) {
					// Exception levée si la valeur de l'attribut n'est pas une valeur valide
					attributeList.remove(nameAttribute);
				}// fin du catch
			}// fin du while
		} catch (Exception ex) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_internal_error", "", null);

			throw new JediException("JediUtil : removeNullValueFromList(JediAttributeList) : Erreur de suppression dans la liste des attributs");
		}
	}

	// *****************************************************************************************************
	//
	// GESTION DES PATHS
	//
	// *****************************************************************************************************

	public static boolean endWithPath(String completeString, String endString) {
		if (completeString != null && endString != null) {
			return trimPath(completeString).toLowerCase().endsWith((trimPath(endString)).toLowerCase());
		} else {
			return false;
		}
	}

	private static String trimPath(String stringToConvert) {
		return stringToConvert.replaceAll(" ", "");
	}

}// fin de la classe
