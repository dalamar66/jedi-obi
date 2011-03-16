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
 * Classe contenant l'ensemble des m�thodes n�cessaires pour le traitement des caract�res sp�ciaux ainsi que des m�thodes attach�s � aucun objet du package.
 * 
 * @author HUMEAU Xavier
 * @version Version 1.0
 */
public class JediUtil {

	/**
	 * Constante de s�paration pour les chemins LDAP.
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
	 * Constante specifiant le d�bugage ou non.
	 */
	protected static boolean CST_PRINT_DEBUG = false;

	// *****************************************************************************************************
	//
	// VALUATION DES PARAMS
	//
	// *****************************************************************************************************

	/**
	 * M�thode qui retourne le temps limite fix�.
	 * 
	 * @return Le temps fix�.
	 */
	public static int getTimeLimit() {
		return (SEARCH_TIME_LIMIT);
	}

	/**
	 * M�thode qui sp�cifie combien de temps la recherche doit durer au maximum. Si le temps est �gal � 0 alors le temps est infini.
	 * 
	 * @param time
	 *            Le temps que la recherche ne doit pas d�passer.
	 * @throws JediException
	 *             Si le temps pass� en param�tre est n�gatif.
	 */
	public static void setTimeLimit(int time) throws JediException {
		if (time < 0) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", Integer.toString(time), null);

			throw new JediException("JediUtil : setTimeLimit(int) : param�tre d'initialisation incorrect");
		}

		SEARCH_TIME_LIMIT = time;
	}

	/**
	 * M�thode qui indique si on affiche la trace.
	 * 
	 * @return Si il y a ou non affichage de la trace.
	 */
	public static boolean getWithTrace() {
		return (CST_PRINT_TRACE);
	}

	/**
	 * M�thode qui sp�cifie si on veut ou non afficher la trace.
	 * 
	 * @param trace
	 *            Sp�cifie si on affiche la trace.
	 */
	public static void setWithTrace(boolean trace) {
		CST_PRINT_TRACE = trace;
	}

	/**
	 * M�thode qui indique si on debug.
	 * 
	 * @return Si il y a ou non un debugage.
	 */
	public static boolean getDebug() {
		return (CST_PRINT_DEBUG);
	}

	/**
	 * M�thode qui sp�cifie si on veut ou non d�buger.
	 * 
	 * @param trace
	 *            Sp�cifie si on debug.
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
	 * M�thode permettant de charger un properties
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

		// Verification de la validit� des param�tres
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
	 * Tableau r�f�ren�ant les caract�res n�cessitant un caract�re d'�chappement au sein de la m�thode otherToLdap.
	 */
	protected static char[] SPECIALS_CARACTERES = { '/', '\\', '<', '>', '+', '#', ';', ',', '\"' };

	/**
	 * M�thode ajoutant un caract�re d'�chappement devant tous les caract�res de stringToModify que l'on retrouve dans le tableau arrayWithoutRead.
	 * 
	 * @param stringToModify
	 *            String � modifier.
	 * @return Le String pass� en param�tre avec des caract�res d'�chappement.
	 * @throws JediException
	 *             Si le String est null ou vide.
	 */
	public static String formatForLdap(String stringToModify) throws JediException {
		boolean charIsSpecial = false;
		boolean charIsVerySpecial = false;

		int indexChar = 0;

		int stringToModifyLength = stringToModify.length();

		// Verification de la validit� des param�tres
		if (stringToModify == null || stringToModify.length() == 0) {
			throw new JediException("JediUtil : otherToLdap(String) : Param�tre d'initialisation incorrect");
		}

		// Pour chaque carat�re on parcours le tableau r�f�ren�ant les caract�res � traiter (arrayWithoutRead)
		while (indexChar < stringToModifyLength) {
			charIsSpecial = false;
			charIsVerySpecial = false;

			// On parcours le tableau pour chaque caract�re du mot
			for (int i = 0; i < SPECIALS_CARACTERES.length; i++) {
				// Si le caract�re apparait dans le tableau on le notifie
				charIsSpecial = charIsSpecial || (stringToModify.charAt(indexChar) == SPECIALS_CARACTERES[i]);
				// Si le caract�re est un "\" on le notifie aussi car il pose un probl�me d'incompatibilit�
				// avec d'autres caract�res
				charIsVerySpecial = charIsVerySpecial || (stringToModify.charAt(indexChar) == '\"');
			}

			// Si on trouve un caract�re sp�cial sans trouver de caract�re "tres sp�cial" on ajoute "\\" devant
			// le caract�re sp�cial
			if (charIsSpecial == true && charIsVerySpecial == false) {
				stringToModify = stringToModify.substring(0, indexChar) + "\\" + stringToModify.substring(indexChar, stringToModifyLength);
				indexChar++;
				stringToModifyLength++;
			}

			// Si on a relev� un "\" et que l'on n'est pas � la fin du mot alors on pr�c�de le caract�re par "\"
			// et on conct�ne la chaine suivant le caract�re sp�cial
			if (charIsSpecial == true && charIsVerySpecial == true && indexChar != stringToModifyLength) {
				stringToModify = stringToModify.substring(0, indexChar) + "\'" + stringToModify.substring(indexChar + 1, stringToModifyLength);
			}

			// Si on a relev� un "\" et que l'on est � la fin du mot alors on le pr�c�de de "\"
			if (charIsSpecial == true && charIsVerySpecial == true && indexChar == stringToModifyLength) {
				stringToModify = stringToModify.substring(0, indexChar) + "\'";
			}
			indexChar++;
		}

		return (stringToModify);
	}

	/**
	 * M�thode qui d�tecte si le String pass� en param�tre contient � la fois des caract�res du tableau arraySpecial et du tableau arrayIncompatible car ces
	 * deux tableaux sont incompatibles au sein d'un m�me String.
	 * 
	 * @param stringToModify
	 *            String � analyser.
	 * @return Si le String contient des caract�res incompatibles ou non.
	 * @throws JediException
	 *             Si le String est null ou vide.
	 */
	public static boolean searchIncompatibility(String stringToModify) throws JediException {
		boolean charIsSpecial = false;
		boolean charIncompatible = false;

		char charAtIndex = 0;
		int indexChar = 0;

		// Verification de la validit� des param�tres
		if (stringToModify == null) {
			throw new JediException("JediUtil : searchIncompatibility(String) : Param�tre d'initialisation incorrect");
		}

		// Pour chaque carat�re on parcours le tableau r�f�ren�ant les caract�res sp�ciaux (arraySpecial) et
		// le tableau des caract�res incompatibles
		while (indexChar < stringToModify.length()) {
			// On parcours le tableau des caract�res sp�ciaux pour chaque caract�re. Si on en trouve un on le notifie
			charIsSpecial = charIsSpecial || (stringToModify.charAt(indexChar) == '/');

			// On parcours le tableau des caract�res incompatibles pour chaque caract�re
			// Si on est sur le dernier caract�re on regarde si le caract�re apparait dans le tableau incompatible
			if (indexChar == stringToModify.length() - 1) {
				charIncompatible = charIncompatible || (stringToModify.charAt(indexChar) == '\\');
			}
			// Si on n'est pas sur le dernier caract�re on regarde s'il est dans le tableau incompatible
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
	 * M�thode qui supprime de la liste tous les attributs ayant une valeur nulle.
	 * 
	 * @param attributeList
	 *            La liste dans laquelle on veut supprimer les elements nuls.
	 * @throws JediException
	 *             Si il y a une erreur de suppression
	 */
	public static void removeNullValueFromList(JediAttributeList attributeList) throws JediException {
		// Recuperation des cl�s des attributs de la liste
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
					// Exception lev�e si la valeur de l'attribut n'est pas une valeur valide
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
