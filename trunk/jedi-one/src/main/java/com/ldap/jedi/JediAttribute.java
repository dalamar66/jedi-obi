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
 * Classe g�rant les valeurs d'un attribut mono et multi-valu�s.
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
	 *             Les param�tres d'initialisation sont incorrects. Si nameAttribute est null ou vide.
	 */
	public JediAttribute(String nameAttribute) throws JediException {
		super(nameAttribute);

		// Verification de la validit� des param�tres
		if (nameAttribute == null || nameAttribute.length() == 0) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", nameAttribute, this);

			throw new JediException("JediAttribute : JediAttribute(String) : Param�tre d'initialisation incorrect");
		}
	}

	/**
	 * Constructeur avec un Attribute.
	 * 
	 * @param attribute
	 *            Attribut jndi recopi� pour l'initialisation
	 * @throws JediException
	 *             Si le parametre est incorrect ou une erreur interne est survenue.
	 */
	public JediAttribute(Attribute attribute) throws JediException {
		super(attribute.getID(), attribute.isOrdered());

		// Verification de la validit� des param�tres
		if (attribute == null) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", "", this);

			throw new JediException("JediAttribute : JediAttribute(attribute) : Param�tre d'initialisation incorrect");
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
	 * Constructeur pour un attribut mono-valu� binaire Si la valeur de l'attribut est null ou vide alors il n'y a pas d'initialisation de la valeur.
	 * 
	 * @param nameAttribute
	 *            Nom de l'attribut.
	 * @param valueAttribute
	 *            Valeur de l'attribut (byte[]).
	 * @throws JediException
	 *             Les param�tres d'initialisation sont incorrects. Si nameAttribute est null ou vide.
	 */
	public JediAttribute(String nameAttribute, byte[] valueAttribute) throws JediException {
		super(nameAttribute, valueAttribute);

		// Verification de la validit� des param�tres
		if (nameAttribute == null || nameAttribute.length() == 0) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", nameAttribute, this);

			throw new JediException("JediAttribute : JediAttribute(String, byte[]) : Param�tres d'initialisations incorrects");
		}

		// Si la valeur de l'attribut est null alors on construit un Jediattribute vide
		if (valueAttribute == null || valueAttribute.length == 0) {
			this.clear();
		}
	}

	/**
	 * Constructeur pour un attribut mono-valu�. Si la valeur de l'attribut est null ou vide alors il n'y a pas d'initialisation de la valeur.
	 * 
	 * @param nameAttribute
	 *            Nom de l'attribut.
	 * @param valueAttribute
	 *            Valeur de l'attribut (String).
	 * @throws JediException
	 *             Les param�tres d'initialisation sont incorrects. Si nameAttribute est null ou vide.
	 */
	public JediAttribute(String nameAttribute, String valueAttribute) throws JediException {
		super(nameAttribute, valueAttribute);

		// Verification de la validit� des param�tres
		if (nameAttribute == null || nameAttribute.length() == 0) {
			List<String> paramList = new ArrayList<String>();
			paramList.add(nameAttribute);
			paramList.add(valueAttribute);

			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", paramList, this);

			throw new JediException("JediAttribute : JediAttribute(String, String) : Param�tres d'initialisations incorrects");
		}

		// Si la valeur de l'attribut est null alors on construit un Jediattribute vide
		if (valueAttribute == null || valueAttribute.length() == 0) {
			this.clear();
		}
	}

	/**
	 * Constructeur pour un attribut multi-valu�. Si une des valeurs de l'attribut multi-valu� est null ou vide, elle ne sera pas ajout�e � la liste des
	 * valeurs. Si toutes les valeurs sont null ou vide alors la valeur de l'attribut n'est pas initialis�e.
	 * 
	 * @param nameAttribute
	 *            Nom de l'attribut.
	 * @param valuesAttribute
	 *            Valeurs de l'attribut qui seront cast�es en String.
	 * @throws JediException
	 *             Les param�tres d'initialisation sont incorrects. Si nameAttribute est null ou vide.
	 */
	public JediAttribute(String nameAttribute, Object[] valuesAttribute) throws JediException {
		super(nameAttribute);

		// Verification de la validit� des param�tres
		if (nameAttribute == null || nameAttribute.length() == 0) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", nameAttribute, this);

			throw new JediException("JediAttribute : JediAttribute(String, Object []) : Param�tres d'initialisations incorrects");
		}

		// Si le tableau pass� en param�tre n'est pas null ou vide
		if (valuesAttribute != null && valuesAttribute.length != 0) {
			// Pour chaque valeur du tableau on verifie qu'elle n'est pas vide et si c'est le cas alors
			// on l'ajoute � l'attribut. */
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
	 * Constructeur pour un attribut multi-valu�. Si une des valeurs de l'attribut multi-valu� est null ou vide, elle ne sera pas ajout�e � la liste des
	 * valeurs. Si toutes les valeurs sont null ou vide alors la valeur de l'attribut n'est pas initialis�e.
	 * 
	 * @param nameAttribute
	 *            Nom de l'attribut.
	 * @param vect
	 *            Valeurs de l'attribut qui seront cast�es en String.
	 * @throws JediException
	 *             Les param�tres d'initialisation sont incorrects. Si nameAttribute est null ou vide.
	 */
	public JediAttribute(String nameAttribute, List<String> list) throws JediException {
		super(nameAttribute);

		// Verification de la validit� des param�tres
		if (nameAttribute == null || nameAttribute.length() == 0) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_parameter_error", nameAttribute, this);

			throw new JediException("JediAttribute : JediAttribute(String, List) : Param�tres d'initialisations incorrects");
		}

		if (list != null && list.size() != 0) {
			// Pour chaque valeur du tableau on verifie qu'elle n'est pas vide et si c'est le cas alors on l'ajoute � l'attribut.
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
	 * M�thode qui retourne la valeur d'un attribut mono-valu�. Cette valeur peut �tre soit une String soit un tableau de byte (attribut binaire). Pour �tre s�r
	 * de traiter le bon type, il faut tester la valeur de retour : String myString = null; byte myBytes[] = null; Object myObject = aJediAttribute.getValue();
	 * if ( myObject instanceof String) { myString = (String)myObject; } else if ( myObject instanceof byte[] ) { myBytes = (byte[])myObject; }
	 * 
	 * 
	 * @return la valeur de l'attribut mono-valu�.
	 * @throws JediException
	 *             Si l'attribut est null ou si il est multi-valu�, ou si la valeur n'est pas retournable sous forme de String.
	 */
	// TODO : getMultiValue ne suffit il pas pour recuperer les datas. Si c'est le cas
	// alors il faut renommer getMultiValue en gatValue et supprimer cette methode
	public Object getValue() throws JediException {
		// Si l'attribut n'est pas null et qu'il est multi-valu� alors on renvoie une erreur
		if (size() > 1) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_attribute_consult_failed", "", this);

			throw new JediException("JediAttribute : getMonoValue() : Attribut multi-valu�");
		}

		// On recupere la valeur de l'attribut sous forme de String
		try {
			return get();
		} catch (NamingException ne) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_attribute_consult_failed", "", this);

			throw new JediException("JediAttribute : getMonoValue() : Valeur incorrect");
		}
	}// fin de la m�thode

	/**
	 * M�thode qui retourne les valeurs d'un attribut multi-valu�. Si l'attribut est mono-valu� la valeur est quand m�me retoun�e.
	 * 
	 * @return La liste des valeurs pour l'attribut multi-valu�.
	 * @throws JediException
	 *             Si l'attribut est null.
	 */
	@SuppressWarnings("unchecked")
	public List<Object> getMultiValue() throws JediException {
		ArrayList<Object> arrayAttribute = new ArrayList<Object>();

		try {
			// On recup�re dans une namingEnumeration la liste des valeurs
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
	 * M�thode qui retourne le nom de l'attribut.
	 * 
	 * @return Le nom de l'attribut.
	 */
	public String getName() {
		return getID();
	}

}// fin de la classe