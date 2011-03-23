package com.ldap.jedi;

/**
 * File : JediAttributeList.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-04 
 * Modification date : 2010-03-04
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

/**
 * Classe g�rant un ensemble, une liste d'attributs mono et multi-valu�s.
 * 
 * @author HUMEAU Xavier
 * @version Version 1.0
 */
public class JediAttributeList {

	/**
	 * Variable contenant la liste d'attributs.
	 */
	protected Attributes attributes = null;

	/**
	 * Constructeur initialis� � vide.
	 */
	public JediAttributeList() {
		attributes = new BasicAttributes();
	}

	/**
	 * Constructeur initialis� avec une liste d'attributs.
	 * 
	 * @param attributes
	 *            Une liste d'Attribute.
	 */
	public JediAttributeList(Attributes attributes) {
		this.attributes = attributes;
	}

	/**
	 * M�thode qui renvoie le nombre d'attributs.
	 * 
	 * @return Le nombre d'attributs.
	 */
	public int size() {
		return attributes.size();
	}

	/**
	 * M�thode qui renvoie l'attribut avec le nom demand�.
	 * 
	 * @param attrID
	 *            Le nom de l'attribut recherch�.
	 * @return L'attribut recherch�.
	 */
	public Attribute get(String attrID) {
		return attributes.get(attrID);
	}

	/**
	 * M�thode qui retourne la liste des attributs sous forme de NamingEnumeration.
	 * 
	 * @return La liste des attributs.
	 */
	@SuppressWarnings("unchecked")
	public NamingEnumeration getAll() {
		return attributes.getAll();
	}

	/**
	 * M�thode qui retourne la liste des noms des attributs sous forme de NamingEnumeration.
	 * 
	 * @return La liste des noms des attributs.
	 */
	public NamingEnumeration<String> getIDs() {
		return attributes.getIDs();
	}

	/**
	 * M�thode qui retourne la liste des attributs sous forme de JediAttribute.
	 * 
	 * @return La liste des attributs sous forme de JediAttribute.
	 */
	@SuppressWarnings("unchecked")
	public List<JediAttribute> getAllJediAttribute() throws JediException {
		List<JediAttribute> jediAttributeList = new ArrayList<JediAttribute>();

		try {
			// On recupere la liste des attributs
			final NamingEnumeration<Attribute> namingEnumeration = getAll();

			// Si il y a des attributs on l'indique et on les ajoute sous forme de JediAttribute dans
			// la liste de retour
			while (namingEnumeration.hasMore()) {
				jediAttributeList.add(new JediAttribute(namingEnumeration.next()));
			}

			// Sinon on retourne la liste de JediAttribute
			return jediAttributeList;
		} catch (Exception ex) {
			JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_attribute_list_consult_failed", "", this);

			throw new JediException("JediAttributeList : getAllJediAttribute() : Erreur lors de la recuperation des valeurs");
		}
	}

	/**
	 * M�thode qui construit une liste restreinte d'attribut. Cette liste est constitu�e des attributs r�f�renc�s dans la liste pass�e en param�tre.
	 * 
	 * @param list
	 *            La liste des attributs que l'on d�sire.
	 * @return Une liste contenant les attributs r�f�renc�s dans la liste pass�e en param�tre.
	 */
	public JediAttributeList getRestrictedAttributeList(List<String> list) {
		JediAttributeList newList = new JediAttributeList();

		// Pour chaque element de la liste pass�e en parametre on l'ajoute � la liste � retourner
		for (String att : list) {
			newList.put((Attribute) get(att));
		}

		/* On retourne la liste restreinte */
		return newList;
	}

	/**
	 * M�thode qui construit une liste restreinte d'attribut. Cette liste est constitu�e des attributs r�f�renc�s dans le tableau pass� en param�tre.
	 * 
	 * @param lstID
	 *            Le tableau des attributs que l'on d�sire.
	 * @return Une liste contenant les attributs r�f�renc�s dans le tableau pass� en param�tre.
	 */
	public JediAttributeList getRestrictedAttributeList(String[] lstID) {
		return getRestrictedAttributeList(Arrays.asList(lstID));
	}

	/**
	 * M�thode qui permet d'ajouter un attribut � la liste des attributs.
	 * 
	 * @param attr
	 *            L'attribut que l'on veut ajouter.
	 * @return L'attribut que l'on a ajout�.
	 */
	public Attribute put(Attribute attr) {
		return attributes.put(attr);
	}

	/**
	 * M�thode qui permet de supprimer un attribut � la liste des attributs.
	 * 
	 * @param attr
	 *            L'attribut que l'on veut supprimer.
	 * @return L'attribut que l'on a supprim�.
	 */
	public Attribute remove(String attrID) {
		return attributes.remove(attrID);
	}

	/**
	 * M�thode qui permet de retourner la liste.
	 * 
	 * @return La liste des attributs.
	 */
	public Attributes getAttributes() {
		return attributes;
	}

	/**
	 * M�thode qui supprime tous les attributs. Apr�s application de cette m�thode la liste est vide.
	 */
	public void clearAttributes() {
		attributes = new BasicAttributes();
	}

	public Object getAttributeValue(String attributeName) throws JediException {
    	Attribute attTemp = attributes.get(attributeName);

    	if (attTemp == null) {
    		return null;
    	} else {
        	JediAttribute jediAttribute = new JediAttribute(attTemp);
        	
        	if (jediAttribute != null) {
    	    	return jediAttribute.getValue();
        	} else {
    	    	return null;
        	}
    	}
	}
	
}// fin de la classe