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
 * Classe gérant un ensemble, une liste d'attributs mono et multi-valués.
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
	 * Constructeur initialisé à vide.
	 */
	public JediAttributeList() {
		attributes = new BasicAttributes();
	}

	/**
	 * Constructeur initialisé avec une liste d'attributs.
	 * 
	 * @param attributes
	 *            Une liste d'Attribute.
	 */
	public JediAttributeList(Attributes attributes) {
		this.attributes = attributes;
	}

	/**
	 * Méthode qui renvoie le nombre d'attributs.
	 * 
	 * @return Le nombre d'attributs.
	 */
	public int size() {
		return attributes.size();
	}

	/**
	 * Méthode qui renvoie l'attribut avec le nom demandé.
	 * 
	 * @param attrID
	 *            Le nom de l'attribut recherché.
	 * @return L'attribut recherché.
	 */
	public Attribute get(String attrID) {
		return attributes.get(attrID);
	}

	/**
	 * Méthode qui retourne la liste des attributs sous forme de NamingEnumeration.
	 * 
	 * @return La liste des attributs.
	 */
	@SuppressWarnings("unchecked")
	public NamingEnumeration getAll() {
		return attributes.getAll();
	}

	/**
	 * Méthode qui retourne la liste des noms des attributs sous forme de NamingEnumeration.
	 * 
	 * @return La liste des noms des attributs.
	 */
	public NamingEnumeration<String> getIDs() {
		return attributes.getIDs();
	}

	/**
	 * Méthode qui retourne la liste des attributs sous forme de JediAttribute.
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
	 * Méthode qui construit une liste restreinte d'attribut. Cette liste est constituée des attributs référencés dans la liste passée en paramètre.
	 * 
	 * @param list
	 *            La liste des attributs que l'on désire.
	 * @return Une liste contenant les attributs référencés dans la liste passée en paramètre.
	 */
	public JediAttributeList getRestrictedAttributeList(List<String> list) {
		JediAttributeList newList = new JediAttributeList();

		// Pour chaque element de la liste passée en parametre on l'ajoute à la liste à retourner
		for (String att : list) {
			newList.put((Attribute) get(att));
		}

		/* On retourne la liste restreinte */
		return newList;
	}

	/**
	 * Méthode qui construit une liste restreinte d'attribut. Cette liste est constituée des attributs référencés dans le tableau passé en paramètre.
	 * 
	 * @param lstID
	 *            Le tableau des attributs que l'on désire.
	 * @return Une liste contenant les attributs référencés dans le tableau passé en paramètre.
	 */
	public JediAttributeList getRestrictedAttributeList(String[] lstID) {
		return getRestrictedAttributeList(Arrays.asList(lstID));
	}

	/**
	 * Méthode qui permet d'ajouter un attribut à la liste des attributs.
	 * 
	 * @param attr
	 *            L'attribut que l'on veut ajouter.
	 * @return L'attribut que l'on a ajouté.
	 */
	public Attribute put(Attribute attr) {
		return attributes.put(attr);
	}

	/**
	 * Méthode qui permet de supprimer un attribut à la liste des attributs.
	 * 
	 * @param attr
	 *            L'attribut que l'on veut supprimer.
	 * @return L'attribut que l'on a supprimé.
	 */
	public Attribute remove(String attrID) {
		return attributes.remove(attrID);
	}

	/**
	 * Méthode qui permet de retourner la liste.
	 * 
	 * @return La liste des attributs.
	 */
	public Attributes getAttributes() {
		return attributes;
	}

	/**
	 * Méthode qui supprime tous les attributs. Après application de cette méthode la liste est vide.
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