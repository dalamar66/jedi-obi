package com.ldap.obi.group;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;

import com.ldap.jedi.JediAttributeList;
import com.ldap.jedi.JediObject;
import com.ldap.obi.ObiOne;
import com.ldap.obi.ObiServiceException;
import com.ldap.obi.ObiUtil;
import com.ldap.obi.user.ObiUserConstants;

/**
 * File : ObiGroupUserService 
 * Component : Version : 1.0 
 * Creation date : 2011-04-21 
 * Modification date : 2011-04-21
 */

public class ObiGroupUserService extends ObiGroupService {

	public ObiGroupUserService(ObiOne one) throws ObiServiceException {
		super(one);
	}

    /**
     * Permet de recuperer la liste des groupes du user
     * 
     * @param dnUser
     * @return
     * @throws ObiServiceException
     */
    public List<String> getAllGroups(String dnUser) throws ObiServiceException {
        return getAllGroups(dnUser, false);
    }

    /**
     * Permet de recuperer la liste des groupes du user en specifiant s'il faut ou non formater les dn
     * 
     * @param dnUser
     * @param format
     * @return
     * @throws ObiServiceException
     */
    private List<String> getAllGroups(String dnUser, boolean format) throws ObiServiceException {
        // Test de la validite des parametres
        if (dnUser == null || dnUser.equalsIgnoreCase("")) {
            throw new ObiServiceException("ObiGroupUserService : getAllGroups(String) : Paramètres incorrects");
        }
        
        try {
            // Creation de l'objet correspondant au user
            JediObject jediObject = getJediObject(dnUser);
            
            // Recuperation de l'attribut memberOf du user
            JediAttributeList attributeList = jediObject.loadAttributeList(new String[]{ObiUserConstants.ATTRIBUTE_MEMBEROF});

            // Recuperation des dn des groupes
            Attribute att = attributeList.get(ObiUserConstants.ATTRIBUTE_MEMBEROF);
            @SuppressWarnings("rawtypes")
			NamingEnumeration ne = att.getAll();

            List<String> groupList = new ArrayList<String>();
            while (ne.hasMore()) {
            	if (format) {
            		groupList.add(ObiUtil.upperCasePath((String)ne.next()));
            	} else {
                	groupList.add((String)ne.next());
            	}
            }

            return groupList;
        } catch (Exception ex) {
            throw new ObiServiceException("ObiGroupUserService : getAllGroups(String) : Erreur lors de la recuperation");
        }
    }
    
    /**
     * Permet de determiner si un user appartient a un groupe. La methode recherche la liste des groupes du user
     * et indique si le groupe passé en parametre en fait partie. 
     * <br>
     * Il est preferable de faire ainsi plutot que de tester si la personne est membre du groupe. En effet la liste
     * des membres d'un groupe est generalement plus volumineuse que la liste des groupes d'un user.
     * 
     * @param dnGroup
     * @param dnUser
     * @return
     * @throws ObiServiceException
     */
    public boolean isMemberOf(String dnGroup, String dnUser) throws ObiServiceException {
        // Test de la validite des parametres
        if (dnUser == null || dnUser.equalsIgnoreCase("") || dnGroup == null || dnGroup.equalsIgnoreCase("")) {
            throw new ObiServiceException("ObiGroupUserService : isMemberOf(String, String) : Paramètres incorrects");
        }

        dnGroup = ObiUtil.upperCasePath(dnGroup);

        // Recuperation de la liste des groupes dont l'utilisateur est membre
        List<String> allGroups = getAllGroups(dnUser, true);

        if (allGroups == null || allGroups.isEmpty()) {
            return false;
        }

        return (allGroups.contains(dnGroup));
    }

}
