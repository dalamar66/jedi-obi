package com.ldap.obi;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;

import com.ldap.jedi.JediAttribute;
import com.ldap.jedi.JediAttributeList;
import com.ldap.jedi.JediException;
import com.ldap.jedi.JediLog;
import com.ldap.jedi.JediObject;

/**
 * File : ObiData.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-09 
 * Modification date : 2011-04-21
 */

public abstract class ObiData {

	protected JediAttributeList dataList;

	// *****************************************************************************************************
	//
	// CONSTRUCTEURS ET ACCESSEURS
	//
	// *****************************************************************************************************

	public ObiData() {
		dataList = new JediAttributeList();
	}

	/**
	 * Constructeur à partir d'un JediObject déjà chargé. L'attribut distinguishedName sera systématiquement chargé.
	 * 
	 * @param jediObject
	 *            JediObject déjà chargé.
	 * @throws ObiDataException
	 *             Si le JediObject est null ou non valide.
	 */
	public ObiData(JediObject jediObject) throws ObiDataException {
		// Verification de la validite des parametres de la methode
		if (jediObject == null) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_parameter_error", "", this);

			throw new ObiDataException("OBIData : OBIData(JediObject) : Paramètres incorrects");
		}

		// Affectation des attributs de l'objet à la DataList
		dataList = jediObject.getJediAttributeList();
	}

	/**
	 * Constructeur à partir d'une JediAttributeList. L'attribut distinguishedName sera systématiquement chargé.
	 * 
	 * @param jediAttributeList
	 *            JediAttributeList déjà chargé.
	 * @throws ObiDataException
	 *             Si le JediAttributeList est null ou non valide.
	 */
	public ObiData(JediAttributeList jediAttributeList) throws ObiDataException {
		// Verification de la validite des parametres de la methode
		if (jediAttributeList == null) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_parameter_error", "", this);

			throw new ObiDataException("OBIData : OBIData(JediAttributeList) : Paramètres incorrects");
		}

		// Affectation de la JediAttributeList à la DataList
		dataList = jediAttributeList;
	}

	/**
	 * Constructeur à partir d'un JediAttribute. L'attribut distinguishedName sera systématiquement chargé.
	 * 
	 * @param jediAttribute JediAttribute déjà chargé.
	 * @throws ObiDataException Si le JediAttribute est null ou non valide.
	 */
	public ObiData(JediAttribute jediAttribute) throws ObiDataException {
		// Verification de la validite des parametres de la methode
		if (jediAttribute == null) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_parameter_error", "", this);

			throw new ObiDataException("OBIData : OBIData(JediAttribute) : Paramètres incorrects");
		}

		// Affectation de la JediAttributeList à la DataList
        JediAttributeList jediAttributeList = new JediAttributeList();
        jediAttributeList.put(jediAttribute);

		dataList = jediAttributeList;
	}
	
	/**
	 * Méthode qui retourne la liste d'attributs de l'OBIData.
	 * 
	 * @return La liste des attributs de l'ObiData sous forme de JediAttributeList.
	 */
	public JediAttributeList getDataList() {
		return dataList;
	}

	// *****************************************************************************************************
	//
	// METHODES DE BASE ET DE CONTROLE
	//
	// *****************************************************************************************************

	/**
	 * Méthode qui retourne la taille de la dataList.
	 * 
	 * return La taille de la dataListe.
	 */
	public int dataSize() {
		return dataList.size();
	}

	/**
	 * Formate les attributs de la dataList pour la consultation ou le stockage.
	 * 
	 * @param store
	 *            Type de formatage (pour consultation ou pour stockage).
	 */
	public void formatDataList(boolean store) {
		/** @todo implémenter les formatages globaux à tous les types */
	}

	/**
	 * Effectue des contrôles de validité sur une liste d'attributs (pour une création ou une mise à jour). En création, les champs à partir desquels sont
	 * générés le RDN et bYCNRefKey doivent être obligatoires.
	 * 
	 * @param create
	 *            true si c'est un contrôle pour une création, false sinon
	 * @return True si la liste est valide, false sinon
	 */
	public boolean controlDataList(boolean create) {
		/** @todo implémenter les contrôles globaux à tous les types */
		return true;
	}

	/**
	 * Permet de savoir si la dataList contient une valeur donnée dans un attribut donné (qu'il soit mono ou multi).
	 * 
	 * @param attributeName
	 *            Nom de l'attribut.
	 * @param value
	 *            Valeur cherchée.
	 * @return true si la valeur est présente, false sinon.
	 * @throws ObiDataException
	 *             Si les paramètres sont nulls ou vides.
	 */
	public boolean containsValue(String attributeName, String value) throws ObiDataException {
		// Verification de la validite des parametres de la methode
		if (attributeName == null || attributeName.length() == 0 || value == null || value.length() == 0) {
			List<String> paramList = new ArrayList<String>();
			paramList.add(attributeName);
			paramList.add(value);

			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_parameter_error", paramList, this);

			throw new ObiDataException("OBIData : containsValue(String, String) : Paramètres incorrects");
		}

		// Si l'attribut existe dans la liste on regarde si la valeur existe dans les valeurs de l'attribut
		if (dataList.get(attributeName) != null) {
			return (dataList.get(attributeName).contains(value));
		}

		return false;
	}

	/**
	 * Permet de savoir si la dataList contient un attribut donné (qu'il soit mono ou multi).
	 * 
	 * @param attributeName
	 *            Nom de l'attribut.
	 * @return true si l'attribut est présent, false sinon.
	 * @throws ObiDataException
	 *             Si attributeName est null ou vide.
	 */
	public boolean containsAttribute(String attributeName) throws ObiDataException {
		// Verification de la validite des parametres de la methode
		if (attributeName == null || attributeName.length() == 0) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_parameter_error", "", this);

			throw new ObiDataException("OBIData : containsAttribute(String) : Paramètres incorrects");
		}

		// Si l'attribut existe dans la liste on retourne true, sinon false
		return (dataList.get(attributeName) != null);
	}

	/**
	 * Retourne la liste des noms d'attributs présents dans le dataList.
	 * 
	 * @return Un vecteur de String.
	 * @throws ObiDataException
	 *             Si la liste des attributs contient un autre type que des String.
	 */
	public List<String> getAllAttributes() throws ObiDataException {
		List<String> result = new ArrayList<String>();

		try {
			// On recupere sous forme de Naming la liste des noms des attributs de la liste
			NamingEnumeration<String> naming = dataList.getIDs();

			while (naming.hasMore()) {
				result.add(naming.nextElement());
			}
		} catch (javax.naming.NamingException ex) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);

			throw new ObiDataException("OBIdata : getAllAttributes() : Liste non valide");
		}

		// Et on retourne le resultat
		return result;
	}

	/**
	 * Retourne le nombre de valeurs présentes dans un attribut de la dataList.
	 * 
	 * @param attributeName
	 *            Nom de l'attribut.
	 * @return Le nombre de valeurs de l'attribut.
	 * @throws ObiDataException
	 *             Si attributeName est null ou vide, ou s'il n'est pas valide.
	 */
	public int countValues(String attributeName) throws ObiDataException {
		// Verification de la validite des parametres de la methode
		if (attributeName == null || attributeName.length() == 0) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_parameter_error", "", this);

			throw new ObiDataException("OBIData : countValues(String) : Paramètres incorrects");
		}

		try {
			// Si l'attribut existe dans la liste on retourne son nombre de valeur
			return dataList.get(attributeName).size();
		} catch (NullPointerException npe) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);

			// L'attribut n'est pas dans la liste
			throw new ObiDataException("OBIdata : getMultiValue(String, int) : Nom d'attribut non valide");
		}
	}

	// *****************************************************************************************************
	//
	// METHODES DE SUPPRESSION
	//
	// *****************************************************************************************************

	/**
	 * Vide entièrement la dataList de tous ses éléments. Après application de cette méthode la liste est vide.
	 */
	public void clearData() {
		dataList.clearAttributes();
	}

	/**
	 * Nettoie les valeurs d'un attribut mono ou multi-valué s'il est dans la dataList en vue de sa suppression dans l'annuaire lors du prochain setData.
	 * 
	 * @param attributeName
	 *            Nom de l'attribut.
	 * @throws ObiDataException
	 *             Si le paramètre est null ou vide.
	 */
	public void clearValue(String attributeName) throws ObiDataException {
		// Verification de la validite des parametres de la methode
		if (attributeName == null || attributeName.length() == 0) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_parameter_error", "", this);

			throw new ObiDataException("OBIData : clear(String) : Paramètres incorrects");
		}

		// On vide les valeurs de l'attribut désiré
		dataList.get(attributeName).clear();
	}

	/**
	 * Retire un attribut de la dataList.
	 * 
	 * @param attributeName
	 *            Nom de l'attribut.
	 * @throws ObiDataException
	 *             Si le paramètre est null ou vide.
	 */
	public void removeAttribute(String attributeName) throws ObiDataException {
		// Verification de la validite des parametres de la methode
		if (attributeName == null || attributeName.length() == 0) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_parameter_error", "", this);

			throw new ObiDataException("OBIData : remove(String) : Paramètres incorrects");
		}

		// On supprime de la liste l'attribut désiré
		dataList.remove(attributeName);
	}

	/**
	 * Retire la nième valeur d'un attribut multi-valué présent dans la dataList.
	 * 
	 * @param attributeName
	 *            Nom de l'attribut.
	 * @param position
	 *            Position de la valeur à retirer.
	 * @throws ObiDataException
	 *             Si attributeName est null ou vide, ou s'il n'est pas valide, ou si l'index est trop grand.
	 */
	public void removeValue(String attributeName, int position) throws ObiDataException {
		// Verification de la validite des parametres de la methode
		if (attributeName == null || attributeName.length() == 0 || position < 0) {
			List<String> paramList = new ArrayList<String>();
			paramList.add(attributeName);
			paramList.add(Integer.toString(position));

			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_parameter_error", paramList, this);

			throw new ObiDataException("OBIData : removeMultiValue(String, int) : Paramètres incorrects");
		}

		try {
			// Si l'attribuit existe dans la liste alors on lui retire sa nieme valeur
			dataList.get(attributeName).remove(position);
		} catch (NullPointerException npe) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);

			// L'attribut n'est pas dans la liste
			throw new ObiDataException("OBIdata : removeMultiValue(String, int) : Nom d'attribut non valide");
		} catch (IndexOutOfBoundsException bex) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);

			// L'attribut ne dispose pas d'une valeur à cet index
			throw new ObiDataException("OBIdata : removeMultiValue(String, int) : Index trop grand");
		}
	}

	/**
	 * Retire une valeur donnée d'un attribut multi-valué présent dans la dataList.
	 * 
	 * @param attributeName
	 *            Nom de l'attribut.
	 * @param value
	 *            Valeur à retirer.
	 * @throws ObiDataException
	 *             Si les paramètres sont nulls ou vides, ou si l'attribut n'est pas valide.
	 */
	public void removeValue(String attributeName, String value) throws ObiDataException {
		// Verification de la validite des parametres de la methode
		if (attributeName == null || attributeName.length() == 0 || value == null || value.length() == 0) {
			List<String> paramList = new ArrayList<String>();
			paramList.add(attributeName);
			paramList.add(value);

			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_parameter_error", paramList, this);

			throw new ObiDataException("OBIData : removeMultiValue(String, String) : Paramètres incorrects");
		}

		try {
			// Si l'attribut existe dans la liste alors on lui retire la valeur desiree
			boolean removed = dataList.get(attributeName).remove(value);

			if (removed == false) {
				JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);

				throw new ObiDataException("OBIdata : removeMultiValue(String, String) : Nom d'attribut non valide");
			}
		} catch (NullPointerException npe) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);

			// L'attribut n'est pas dans la liste
			throw new ObiDataException("OBIdata : removeMultiValue(String, String) : Nom d'attribut non valide");
		}
	}

	// *****************************************************************************************************
	//
	// METHODES D'AJOUT
	//
	// *****************************************************************************************************

	/**
	 * Ajoute une valeur à un attribut multi-valué présent dans la dataList. Si l'attribut n'est pas présent dans la dataList, il est créé dans la dataList.
	 * Attention : lors du setData, si un attribut multi-valué est dans une dataList, toutes les valeurs dans l'annuaire seront supprimées et remplacées par les
	 * valeurs présentes dans la dataList.
	 * 
	 * @param attributeName
	 *            Nom de l'attribut.
	 * @param value
	 *            Valeur à ajouter à l'attribut.
	 * @throws ObiDataException
	 *             Si les paramètres ont nulls ou vides, ou lors de la création de l'attribut.
	 */
	public void addValue(String attributeName, String value) throws ObiDataException {
		// Verification de la validite des parametres de la methode
		if (attributeName == null || attributeName.length() == 0 || value == null || value.length() == 0) {
			List<String> paramList = new ArrayList<String>();
			paramList.add(attributeName);
			paramList.add(value);

			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_parameter_error", paramList, this);

			throw new ObiDataException("OBIData : addValue(String, String) : Paramètres incorrects");
		}

		// Si l'attribut existe dans la liste alors on lui ajoute la valeur desiree
		Attribute attribute = dataList.get(attributeName);
		if (attribute != null) {
			attribute.set(attribute.size(), value);
		}
		// Si l'attribut n'existe pas dans la liste alors on le créé et on l'ajoute a la liste des attributs
		else {
			try {
				// Creation de l'attribut
				JediAttribute attribut = new JediAttribute(attributeName, value);

				// Ajout de l'attribut à la liste
				dataList.put(attribut);
			} catch (JediException ex) {
				JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);

				throw new ObiDataException("OBIdata : addMultiValue(String, String) : Erreur de création de l'attribut car non existant");
			}
		}
	}

	/**
	 * Met à jour les valeurs d'un attribut multi-valué présent dans la dataList. Si l'attribut n'est pas présent dans la dataList, il est créé dans la
	 * dataList.
	 * 
	 * @param attributeName
	 *            Nom de l'attribut.
	 * @param values
	 *            Valeurs de l'attribut.
	 * @throws ObiDataException
	 *             Si les paramètres sont nulls ou vides, ou en cas de problème de création de l'attribut.
	 */
	public void setValues(String attributeName, List<String> values) throws ObiDataException {
		// Verification de la validite des parametres de la methode
		if (attributeName == null || attributeName.length() == 0) {
			List<String> paramList = new ArrayList<String>();
			paramList.add(attributeName);

			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_parameter_error", paramList, this);

			throw new ObiDataException("OBIData : setMultiValues(String, Vector) : Paramètres incorrects");
		}

		if (values == null) {
			this.clearValue(attributeName);
		} else {
			// Si l'attribut existe dans la liste alors on lui affecte sa nouvelle valeur
			if (dataList.get(attributeName) != null) {
				dataList.get(attributeName).clear();

				for (int i = 0; i < values.size(); i++) {
					dataList.get(attributeName).add(values.get(i));
				}
			}
			// Si l'attribut n'existe pas dans la liste alors on le cree et on l'ajoute a la liste
			else {
				try {
					// Création de l'attribut
					JediAttribute attribut = new JediAttribute(attributeName, values);

					// Ajout de l'attribut à la liste
					dataList.put(attribut);
				} catch (JediException ex) {
					JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);

					throw new ObiDataException("OBIdata : setMultiValues(String, Vector) : xxx");
				}
			}
		}
	}

	/**
	 * Met à jour la valeur d'un attribut mono-valué présent dans la dataList. Si l'attribut n'est pas présent dans la dataList, il est créé dans la dataList.
	 * 
	 * @param attributeName
	 *            Nom de l'attribut.
	 * @param value
	 *            Valeur de l'attribut.
	 * @throws ObiDataException
	 *             Si les paramètres sont nulls ou vides, ou en cas de problème de création de l'attribut.
	 */
	public void setValue(String attributeName, String value) throws ObiDataException {
		List<String> values = new ArrayList<String>();
		values.add(value);

		setValues(attributeName, values);
	}// Fin de la methode

	// *****************************************************************************************************
	//
	// METHODES D'ACCES
	//
	// *****************************************************************************************************

	/**
	 * Retourne la nième valeur d'un attribut multi-valué s'il est dans la dataList.
	 * 
	 * @param attributeName
	 *            Nom de l'attribut.
	 * @param position
	 *            Position de la valeur à récupérer.
	 * @return La valeur demandée.
	 * @throws ObiDataException
	 *             Si attributeName est null ou vide, ou s'il n'est pas valide, ou si l'index est trop grand.
	 */
	public String getValue(String attributeName, int position) throws ObiDataException {
		// Verification de la validite des parametres de la methode
		if (attributeName == null || attributeName.length() == 0 || position < 0) {
			List<String> paramList = new ArrayList<String>();
			paramList.add(attributeName);
			paramList.add(Integer.toString(position));

			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_parameter_error", paramList, this);

			throw new ObiDataException("OBIData : getMultiValues(String, int) : Paramètres incorrects");
		}

		try {
			// On retourne la nieme valeur de l'attribut desire
			return (String) dataList.get(attributeName).get(position);
		} catch (NullPointerException npe) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);

			// L'attribut n'est pas dans la liste
			throw new ObiDataException("OBIdata : getMultiValue(String, int) : Nom d'attribut non valide");
		} catch (NamingException ex) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);

			// L'attribut ne dispose pas d'une valeur à cet index
			throw new ObiDataException("OBIdata : getMultiValue(String, int) : Nom d'attribut non valide");
		} catch (IndexOutOfBoundsException bex) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);

			// L'attribut ne dispose pas d'une valeur à cet index
			throw new ObiDataException("OBIdata : getMultiValue(String, int) : Index trop grand");
		}
	}

	/**
	 * Retourne la liste des valeurs d'un attribut multi-valué s'il est dans la dataList.
	 * 
	 * @param attributeName
	 *            Nom de l'attribut.
	 * @return Vecteur de String contenant la liste des valeurs de l'attribut.
	 * @throws ObiDataException
	 *             Si attributeName est null ou vide, ou s'il n'est pas valide.
	 */
	public List<String> getValues(String attributeName) throws ObiDataException {
		// Verification de la validite des parametres de la methode
		if (attributeName == null || attributeName.length() == 0) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_parameter_error", "", this);

			throw new ObiDataException("OBIData : getMultiValues(String) : Paramètres incorrects");
		}

		try {
			List<String> result = new ArrayList<String>();

			// Si l'attribut existe dans la liste alors on charge ses valeurs dans la NamingEnumeration
			@SuppressWarnings("rawtypes")
			NamingEnumeration naming = dataList.get(attributeName).getAll();

			// Si on a recupere une Naming il faut la transformer pour retoiurner un vecteur
			while (naming.hasMoreElements()) {
				result.add((String) naming.nextElement());
			}

			return result;
		} catch (NullPointerException npe) {
			// L'attribut n'est pas dans la liste
			return null;
		} catch (NamingException ex) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);

			throw new ObiDataException("OBIdata : getMultiValues(String) : Nom d'attribut non valide");
		}
	}

	/**
	 * Retourne sous forme de String la valeur d'un attribut mono-valué s'il est dans la dataList.
	 * 
	 * @param attributeName
	 *            Nom de l'attribut.
	 * @return Valeur de l'attribut.
	 * @throws ObiDataException
	 *             Si le paramètre est null ou vide, ou si le nom du paramètre n'est pas valide.
	 */
	public String getValue(String attributeName) throws ObiDataException {
		// Verification de la validite des parametres de la methode
		if (attributeName == null || attributeName.length() == 0) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_parameter_error", "", this);

			throw new ObiDataException("OBIData : getMonoValue(String) : Paramètres incorrects");
		}

		try {
			// On retourne la valeur de l'attribut desire
			return (String) dataList.get(attributeName).get(0);
		} catch (NullPointerException npe) {
			// L'attribut n'est pas dans la liste
			return null;
		} catch (NamingException ex) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "obi_msg_internal_error", "", this);

			throw new ObiDataException("OBIdata : getMonoValue(String) : Nom d'attribut non valide");
		}
	}

}