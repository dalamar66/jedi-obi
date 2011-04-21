package com.ldap.obi.group;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;

import com.ldap.jedi.JediAttribute;
import com.ldap.jedi.JediAttributeList;
import com.ldap.jedi.JediObject;
import com.ldap.obi.ObiData;
import com.ldap.obi.ObiDataException;
import com.ldap.obi.ObiOne;
import com.ldap.obi.ObiService;
import com.ldap.obi.ObiServiceException;
import com.ldap.obi.ObiUtil;

/**
 * File : ObiGroupService.java 
 * Component : Version : 1.0 
 * Creation date : 2011-04-01 
 * Modification date : 2011-04-21
 */

public class ObiGroupService extends ObiService<ObiGroupData> {

	public ObiGroupService(ObiOne one) throws ObiServiceException {
		super(one);

		ldapClassName = ObiGroupConstants.CLASS_NAME_GROUP;

		ldapCategory = ObiGroupConstants.CATEGORY_GROUP;

		ldapFullClassName = new ArrayList<String>();
		ldapFullClassName.add(ObiGroupConstants.CLASS_NAME_TOP);
		ldapFullClassName.add(ldapClassName);

		defaultAttributes = new ArrayList<String>();
		defaultAttributes.add(ObiGroupConstants.ATTRIBUTE_DISTINGUISHED_NAME);
		defaultAttributes.add(ObiGroupConstants.ATTRIBUTE_MEMBER);
		defaultAttributes.add(ObiGroupConstants.ATTRIBUTE_CN);
	}

	@Override
	protected ObiGroupData newObiData(JediAttributeList jediAttributeList) throws ObiDataException {
		return new ObiGroupData(jediAttributeList);
	}

	@Override
	protected ObiGroupData newObiData(JediObject jediObject) throws ObiDataException {
		return new ObiGroupData(jediObject);
	}
	
	/**
	 * Permet d'ajouter un objet a un groupe
	 * 
	 * @param dnGroup DN du groupe auquel l'object doit etre ajouté.
	 * @param dnObject DN à ajouter au groupe.
	 * @throws OBIServiceException
	 */
    public void add(String dnGroup, String dnObject) throws ObiServiceException {
        // Test de la validite des parametres
        if (dnGroup == null || dnGroup.equalsIgnoreCase("") || dnObject == null || dnObject.equalsIgnoreCase("")) {
            throw new ObiServiceException("ObiGroupService : add(String, String) : Paramètres incorrects");
        }

        try {
            // Creation de l'objet correspondant au groupe
            JediObject groupe = getJediObject(dnGroup);

            // On charge l'attribut contenant les objets du groupe et on y ajoute le dn a ajouter a la liste.
            // Si on a une exception c'est que le groupe est vide
            JediAttributeList attributeList = new JediAttributeList();
            try {
            	attributeList = groupe.loadAttributeList(new String[]{ObiGroupConstants.ATTRIBUTE_MEMBER});
            	attributeList.get(ObiGroupConstants.ATTRIBUTE_MEMBER).add(dnObject);
            } catch (Exception ex) {
                JediAttribute member = new JediAttribute(ObiGroupConstants.ATTRIBUTE_MEMBER, dnObject);
                attributeList.put(member);
            }

            ObiData obiData = new ObiGroupData(attributeList);
            setData(groupe, obiData);
        } catch (Exception ex) {
            throw new ObiServiceException("ObiGroupService : add(String, String) : Erreur de mise a jour");
        }
    }

    /**
	 * Permet de retirer un objet d'un groupe
	 * 
	 * @param dnGroup DN du groupe duquel l'object doit etre supprimé.
	 * @param dnObject DN à retirer du groupe.
	 * @throws OBIServiceException
	 */
    public void remove(String dnGroup, String dnObject) throws ObiServiceException {
        // Test de la validite des parametres
        if (dnGroup == null || dnGroup.equalsIgnoreCase("") || dnObject == null || dnObject.equalsIgnoreCase("")) {
            throw new ObiServiceException("ObiGroupService : remove(String, String) : Paramètres incorrects");
        }

        dnObject = ObiUtil.upperCasePath(dnObject);

        try {
            // Creation de l'objet correspondant au groupe
            JediObject groupe = getJediObject(dnGroup);

            // Recuperation de l'attribut member du groupe
            JediAttributeList attributeList = groupe.loadAttributeList(new String[]{ObiGroupConstants.ATTRIBUTE_MEMBER});

            // Suppression de l'objet des membres
            Attribute att = attributeList.get(ObiGroupConstants.ATTRIBUTE_MEMBER);
            @SuppressWarnings("rawtypes")
			NamingEnumeration ne = att.getAll();

            List<String> newMemberList = new ArrayList<String>();

            while (ne.hasMore()) {
            	String dn = (String)ne.next();
                dn = ObiUtil.upperCasePath(dn);
                if (dn.indexOf(dnObject) == -1) {
                	newMemberList.add(dn);
                }
            }

            ObiData obiData = new ObiGroupData(new JediAttribute(ObiGroupConstants.ATTRIBUTE_MEMBER, newMemberList));
            setData(groupe, obiData);
        } catch (Exception ex) {
            throw new ObiServiceException("ObiGroupService : remove(String, String) : Erreur de mise a jour");
        }
    }
    
	/**
	 * Permet de retourner la liste des dn des objets d'un groupe
	 * 
	 * @param dnGroup DN du groupe duquel on veut recuperer la liste des objets.
	 * @return La liste des dn du groupe
	 * @throws OBIServiceException
	 */
    public List<String> getAll(String dnGroup) throws ObiServiceException {
        // Test de la validite des parametres
        if (dnGroup == null || dnGroup.equalsIgnoreCase("")) {
            throw new ObiServiceException("ObiGroupService : getAll(String, String) : Paramètres incorrects");
        }

        try {
            // Creation de l'objet correspondant au groupe
            JediObject groupe = getJediObject(dnGroup);

            // Recuperation de l'attribut member du groupe
            JediAttributeList attributeList = groupe.loadAttributeList(new String[]{ObiGroupConstants.ATTRIBUTE_MEMBER});

            // Recuperation des dn des membres
            Attribute att = attributeList.get(ObiGroupConstants.ATTRIBUTE_MEMBER);
            @SuppressWarnings("rawtypes")
			NamingEnumeration ne = att.getAll();

            List<String> memberList = new ArrayList<String>();
            while (ne.hasMore()) {
            	memberList.add((String)ne.next());
            }

            return memberList;
        } catch (Exception ex) {
            throw new ObiServiceException("ObiGroupService : getAll(String, String) : Erreur lors de la recuperation");
        }    	
    }

}
