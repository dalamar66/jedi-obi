package com.ldap.jedi;

/**
 * File : JediAttribute.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-04 
 * Modification date : 2010-03-05
 */

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;

/**
 * Classe gérant les valeurs d'un attribut mono et multi-valués.
 * 
 * @author HUMEAU Xavier
 * @version Version 1.0
 */
public class JediAttribute extends BasicAttribute {

	private static final long serialVersionUID = 7799376807369983086L;

	/**
	 * Constructeur n'initialisant pas la valeur de l'attribut.
	 * 
	 * @param nameAttribute
	 *            Nom de l'attribut.
	 * @throws JediException
	 *             Les paramètres d'initialisation sont incorrects. Si nameAttribute est null ou vide.
	 */
	public JediAttribute(String nameAttribute) throws JediException {
		super(nameAttribute);

		// Verification de la validité des paramètres
		if (nameAttribute == null || nameAttribute.length() == 0) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", nameAttribute, this);

			throw new JediException("JediAttribute : JediAttribute(String) : Paramètre d'initialisation incorrect");
		}
	}

	/**
	 * Constructeur avec un Attribute.
	 * 
	 * @param attribute
	 *            Attribut jndi recopié pour l'initialisation
	 * @throws JediException
	 *             Si le parametre est incorrect ou une erreur interne est survenue.
	 */
	public JediAttribute(Attribute attribute) throws JediException {
		super(attribute.getID(), attribute.isOrdered());

		// Verification de la validité des paramètres
		if (attribute == null) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", "", this);

			throw new JediException("JediAttribute : JediAttribute(attribute) : Paramètre d'initialisation incorrect");
		}

		try {
			// Sur toute la taille de l'attribut on ajoute les valeurs de celui-ci
			for (int i = 0; i < attribute.size(); i++) {
				this.add(attribute.get(i));
			}
		} catch (NamingException n) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_attribute_creation_failed", "", this);

			throw new JediException("JediAttribute : JediAttribute(attribute) : Erreur de recuperation des valeurs");
		}
	}

	/**
	 * Constructeur pour un attribut mono-valué binaire Si la valeur de l'attribut est null ou vide alors il n'y a pas d'initialisation de la valeur.
	 * 
	 * @param nameAttribute
	 *            Nom de l'attribut.
	 * @param valueAttribute
	 *            Valeur de l'attribut (byte[]).
	 * @throws JediException
	 *             Les paramètres d'initialisation sont incorrects. Si nameAttribute est null ou vide.
	 */
	public JediAttribute(String nameAttribute, byte[] valueAttribute) throws JediException {
		super(nameAttribute, valueAttribute);

		// Verification de la validité des paramètres
		if (nameAttribute == null || nameAttribute.length() == 0) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", nameAttribute, this);

			throw new JediException("JediAttribute : JediAttribute(String, byte[]) : Paramètres d'initialisations incorrects");
		}

		// Si la valeur de l'attribut est null alors on construit un Jediattribute vide
		if (valueAttribute == null || valueAttribute.length == 0) {
			this.clear();
		}
	}

	/**
	 * Constructeur pour un attribut mono-valué. Si la valeur de l'attribut est null ou vide alors il n'y a pas d'initialisation de la valeur.
	 * 
	 * @param nameAttribute
	 *            Nom de l'attribut.
	 * @param valueAttribute
	 *            Valeur de l'attribut (String).
	 * @throws JediException
	 *             Les paramètres d'initialisation sont incorrects. Si nameAttribute est null ou vide.
	 */
	public JediAttribute(String nameAttribute, String valueAttribute) throws JediException {
		super(nameAttribute, valueAttribute);

		// Verification de la validité des paramètres
		if (nameAttribute == null || nameAttribute.length() == 0) {
			List<String> paramList = new ArrayList<String>();
			paramList.add(nameAttribute);
			paramList.add(valueAttribute);

			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", paramList, this);

			throw new JediException("JediAttribute : JediAttribute(String, String) : Paramètres d'initialisations incorrects");
		}

		// Si la valeur de l'attribut est null alors on construit un Jediattribute vide
		if (valueAttribute == null || valueAttribute.length() == 0) {
			this.clear();
		}
	}

	/**
	 * Constructeur pour un attribut multi-valué. Si une des valeurs de l'attribut multi-valué est null ou vide, elle ne sera pas ajoutée à la liste des
	 * valeurs. Si toutes les valeurs sont null ou vide alors la valeur de l'attribut n'est pas initialisée.
	 * 
	 * @param nameAttribute
	 *            Nom de l'attribut.
	 * @param valuesAttribute
	 *            Valeurs de l'attribut qui seront castées en String.
	 * @throws JediException
	 *             Les paramètres d'initialisation sont incorrects. Si nameAttribute est null ou vide.
	 */
	public JediAttribute(String nameAttribute, Object[] valuesAttribute) throws JediException {
		super(nameAttribute);

		// Verification de la validité des paramètres
		if (nameAttribute == null || nameAttribute.length() == 0) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", nameAttribute, this);

			throw new JediException("JediAttribute : JediAttribute(String, Object []) : Paramètres d'initialisations incorrects");
		}

		// Si le tableau passé en paramètre n'est pas null ou vide
		if (valuesAttribute != null && valuesAttribute.length != 0) {
			// Pour chaque valeur du tableau on verifie qu'elle n'est pas vide et si c'est le cas alors
			// on l'ajoute à l'attribut. */
			for (int i = valuesAttribute.length - 1; i >= 0; --i) {
				if (valuesAttribute[i] != null && ((String) valuesAttribute[i]).length() > 0) {
					add((String) valuesAttribute[i]);
				}
			}
		} else {
			// Si la valeur de l'attribut est null alors on construit un Jediattribute vide
			this.clear();
		}
	}

	/**
	 * Constructeur pour un attribut multi-valué. Si une des valeurs de l'attribut multi-valué est null ou vide, elle ne sera pas ajoutée à la liste des
	 * valeurs. Si toutes les valeurs sont null ou vide alors la valeur de l'attribut n'est pas initialisée.
	 * 
	 * @param nameAttribute
	 *            Nom de l'attribut.
	 * @param vect
	 *            Valeurs de l'attribut qui seront castées en String.
	 * @throws JediException
	 *             Les paramètres d'initialisation sont incorrects. Si nameAttribute est null ou vide.
	 */
	public JediAttribute(String nameAttribute, List<String> list) throws JediException {
		super(nameAttribute);

		// Verification de la validité des paramètres
		if (nameAttribute == null || nameAttribute.length() == 0) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", nameAttribute, this);

			throw new JediException("JediAttribute : JediAttribute(String, List) : Paramètres d'initialisations incorrects");
		}

		if (list != null && list.size() != 0) {
			// Pour chaque valeur du tableau on verifie qu'elle n'est pas vide et si c'est le cas alors on l'ajoute à l'attribut.
			for (String val : list) {
				if (val != null && nameAttribute.length() != 0) {
					add(val);
				}
			}
		} else {
			// Si la valeur de l'attribut est null alors on construit un Jediattribute vide
			this.clear();
		}
	}

	/**
	 * Méthode qui retourne la valeur d'un attribut mono-valué. Cette valeur peut être soit une String soit un tableau de byte (attribut binaire). Pour être sûr
	 * de traiter le bon type, il faut tester la valeur de retour : String myString = null; byte myBytes[] = null; Object myObject = aJediAttribute.getValue();
	 * if ( myObject instanceof String) { myString = (String)myObject; } else if ( myObject instanceof byte[] ) { myBytes = (byte[])myObject; }
	 * 
	 * 
	 * @return la valeur de l'attribut mono-valué.
	 * @throws JediException
	 *             Si l'attribut est null ou si il est multi-valué, ou si la valeur n'est pas retournable sous forme de String.
	 */
	// TODO : getMultiValue ne suffit il pas pour recuperer les datas. Si c'est le cas
	// alors il faut renommer getMultiValue en gatValue et supprimer cette methode
	public Object getValue() throws JediException {
		// Si l'attribut n'est pas null et qu'il est multi-valué alors on renvoie une erreur
		if (size() > 1) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_attribute_consult_failed", "", this);

			throw new JediException("JediAttribute : getMonoValue() : Attribut multi-valué");
		}

		// On recupere la valeur de l'attribut sous forme de String
		try {
			return get();
		} catch (NamingException ne) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_attribute_consult_failed", "", this);

			throw new JediException("JediAttribute : getMonoValue() : Valeur incorrect");
		}
	}// fin de la méthode

	/**
	 * Méthode qui retourne les valeurs d'un attribut multi-valué. Si l'attribut est mono-valué la valeur est quand même retounée.
	 * 
	 * @return La liste des valeurs pour l'attribut multi-valué.
	 * @throws JediException
	 *             Si l'attribut est null.
	 */
	@SuppressWarnings("unchecked")
	public List<Object> getMultiValue() throws JediException {
		ArrayList<Object> arrayAttribute = new ArrayList<Object>();

		try {
			// On recupère dans une namingEnumeration la liste des valeurs
			final NamingEnumeration namingEnumeration = getAll();

			while (namingEnumeration.hasMore()) {
				Object myObject = namingEnumeration.next();

				// Si l'attribut est une instance de String
				if (myObject instanceof String) {
					arrayAttribute.add((String) myObject);
				}
				// Si l'attribut est une instance de tableau de bytes
				else if (myObject instanceof byte[]) {
					arrayAttribute.add((byte[]) myObject);
				}
			}

			return arrayAttribute;
		} catch (Exception ex) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_attribute_consult_failed", "", this);

			throw new JediException("JediAttribute : getMultiValue() : Erreur lors de la recuperation des valeurs");
		}
	}

	/**
	 * Méthode qui retourne le nom de l'attribut.
	 * 
	 * @return Le nom de l'attribut.
	 */
	public String getName() {
		return getID();
	}

}// fin de la classe