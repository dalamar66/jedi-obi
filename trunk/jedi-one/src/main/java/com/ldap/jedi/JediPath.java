package com.ldap.jedi;

/**
 * File : JediPath.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-04 
 * Modification date : 2011-04-21
 */

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Classe gérant les chemins sous Ldap.
 * 
 * @author HUMEAU Xavier
 * @version Version 1.0
 */
public class JediPath {

	private List<String> varPath = new LinkedList<String>();

	public List<String> getVarPath() {
		return varPath;
	}

	/**
	 * Constructeur vide.
	 */
	public JediPath() throws JediException {
	}

	/**
	 * Constructeur prenant en paramètre un String.
	 * 
	 * @param path
	 *            String contenant le chemin.
	 * @throws JediException
	 *             Si le path est null.
	 */
	public JediPath(String path) throws JediException {
		// Verification de la validité des paramètres
		if (path == null) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", path, this);

			throw new JediException("JediPath : JediPath(String) : Paramètre d'initialisation incorrect");
		}

		// On passe le String et le separateur dans un StringTokenizer
		StringTokenizer stringTokenizer = new StringTokenizer(path, JediUtil.SEPARATOR);

		// Chaque element entre les separateur est ajoute au path Multi
		while (stringTokenizer.hasMoreTokens()) {
			varPath.add(stringTokenizer.nextToken());
		}
	}

	/**
	 * Constructeur prenant en paramètre un tableau de String.
	 * 
	 * @param pathTable
	 *            Tableau contenant le chemin.
	 * @throws JediException
	 *             Si la pathTable est null ou vide.
	 */
	public JediPath(String[] pathTable) throws JediException {
		// Verification de la validité des paramètres
		if (pathTable == null || pathTable.length == 0) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", Integer.toString(pathTable.length), this);

			throw new JediException("JediPath : JediPath(String []) : Paramètre d'initialisation incorrect");
		}

		for (int i = 0; i < pathTable.length; i++) {
			varPath.add(pathTable[i]);
		}
	}

	/**
	 * Méthode qui permet d'ajouter une branche à un chemin.
	 * 
	 * @param element
	 *            Branche qu'il faut ajouter au chemin.
	 * @throws JediException
	 *             Si l'élément est null ou vide.
	 */
	public void addElement(String element) throws JediException {
		// Verification de la validité des paramètres
		if (element == null || element.length() == 0) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", element, this);

			throw new JediException("JediPath : addElement(String) : Paramètre d'initialisation incorrect");
		}

		List<String> temp = new LinkedList<String>();
		temp.add(element);
		temp.addAll(varPath);

		varPath = temp;
	}// fin de la methode

	/**
	 * Méthode qui permet de récupérer la branche du chemin à l'index spécifié.
	 * 
	 * @param index
	 *            Index de la branche que l'on veut retourner.
	 * @return Branche à l'index spécifié.
	 * @throws JediException
	 *             Si l'index est < 0 ou s'il est supérieur au nombre d'éléments.
	 */
	public String get(int index) throws JediException {
		// Verification de la validité des paramètres
		if (index < 0 || index >= varPath.size()) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", Integer.toString(index), this);

			throw new JediException("JediPath : get(int) : Paramètre d'initialisation incorrect");
		}

		return varPath.get(index);
	}

	/**
	 * Méthode qui permet de récupérer le DN.
	 * 
	 * @return Le DN de l'objet.
	 * @throws JediException
	 *             Si on a des caractères incompatibles dans le DN.
	 */
	public String getDN() throws JediException {
		StringBuffer result = new StringBuffer("");

		// Si le chemin est null ou vide
		if (varPath == null || varPath.size() == 0) {
			return ("");
		}

		for (String name : varPath) {
			if (result.length() != 0) {
				result.append(JediUtil.SEPARATOR);
			}

			result.append(name);
		}

		// On renvoie une erreur si il y a des caractères incompatibles dans le nouveau chemin
		if (JediUtil.searchIncompatibility(result.toString()) == true) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_internal_error", "", this);

			throw new JediException("JediPath : getDN() : Caractères incompatibles");
		}

		return result.toString();
	}

	/**
	 * Methode permettant de recuperer le dn sans la racine
	 * 
	 * @param domainRoot
	 * @return
	 * @throws JediException
	 */
	public String getDNWithoutRacine(String domainRoot) throws JediException {
		String dn = getDN();

		// Formattage des String
		String dnFormat = dn.toLowerCase().trim();
		String domainRootFormat = domainRoot.toLowerCase().trim();

		if (dnFormat.endsWith(domainRootFormat)) {
			dnFormat = dnFormat.substring(0, dnFormat.indexOf(domainRootFormat)-1);
		}

		return dnFormat;
	}
	
	/**
	 * Méthode qui permet de récupérer le RDN.
	 * 
	 * @return Le RDN de l'objet.
	 * @throws JediException
	 *             Si on a des caractères incompatibles dans le RDN.
	 */
	public String getRDN() throws JediException {
		String result = null;

		// Si le chemin est null ou vide
		if (varPath == null || varPath.size() == 0) {
			return ("");
		} else {
			result = varPath.get(0);
		}

		// On renvoie une erreur si il y a des caractères incompatibles dans le nouveau chemin
		if (JediUtil.searchIncompatibility(result) == true) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_path_consult_failed", "", this);

			throw new JediException("JediPath : getRDN() : Caractères incompatibles");
		}

		return result;
	}

	/**
	 * Méthode qui permet de récupérer le DN du père.
	 * 
	 * @return Le DN du père de l'objet.
	 * @throws JediException
	 *             Si on a des caractères incompatibles dans le Node.
	 */
	public String getNode() throws JediException {
		StringBuffer result = new StringBuffer("");

		// Si le chemin est null ou vide ou egal à 1
		if (varPath == null || varPath.size() <= 1) {
			return ("");
		} else {
			boolean isFirst = true;
			for (String name : varPath) {
				if (isFirst) {
					isFirst = false;
					continue;
				} else {
					if (result.length() != 0) {
						result.append(JediUtil.SEPARATOR);
					}

					result.append(name);
				}
			}
		}

		// On renvoie une erreur si il y a des caractères incompatibles dans le nouveau chemin
		if (JediUtil.searchIncompatibility(result.toString()) == true) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_path_consult_failed", "", this);

			throw new JediException("JediPath : getNode() : Caractères incompatibles");
		}

		return result.toString();
	}

	/**
	 * Méthode qui permet de récupérer la profondeur du chemin.
	 * 
	 * @return La profondeur du chemin.
	 */
	public int getPathSize() {
		return varPath.size();
	}

} // fin de la classe