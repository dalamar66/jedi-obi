package com.ldap.dsml;

/**
 * File                 : DsmlAdapter.java
 * Component            :
 * Version              : 1.0
 * Creation date        : 2011-03-17
 * Modification date    : 2011-03-17
 *
 * Classe traitant l'import et l'export des bases LDAP au moyen de fichiers XML.
 *
 * @author    HUMEAU Xavier
 * @version   Version 1.0
 */

import java.io.*;
import java.util.*;

import org.w3c.dom.*;

import javax.naming.*;

import java.lang.Runtime;
import javax.xml.parsers.*;
import com.ldap.jedi.JediAttribute;
import com.ldap.jedi.JediAttributeList;
import com.ldap.jedi.JediLog;
import com.ldap.jedi.JediObject;
import com.ldap.jedi.JediPath;
import com.ldap.jedi.JediServer;
import com.ldap.obi.ObiUtil;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.sun.org.apache.xpath.internal.XPathAPI;

public class DsmlAdapter {

    private static List<String> attributeToIgnoreForDiff = new ArrayList<String>();

    static {
        attributeToIgnoreForDiff.add("uSNCreated");
        attributeToIgnoreForDiff.add("uSNChanged");
        attributeToIgnoreForDiff.add("objectGUID");
        attributeToIgnoreForDiff.add("whenCreated");
        attributeToIgnoreForDiff.add("objectCategory");
        attributeToIgnoreForDiff.add("schemaIDGUID");
        attributeToIgnoreForDiff.add("distinguishedName");
        attributeToIgnoreForDiff.add("whenChanged");
    }

    /**
     * Methode permettant de contruire le document dsml a partir de la liste d'objet pass� en parametre
     * 
     * @param jediObjectList
     * @param fileName
     * @throws DsmlAdapterException
     */
    public static void getDocument(List<JediObject> jediObjectList, String fileName) throws DsmlAdapterException {
        DocumentBuilder builder = null;
        Document document = null;

        int sizeFreeMemory  = 0;
        int countNumberPart = 0;
        boolean isPartial = false;
        
        if (jediObjectList == null) {
            throw new DsmlAdapterException("DsmlAdapter : getDocument : La liste pass�e en param�tre est null");
        }

        int index = fileName.lastIndexOf(".");
        String fileNameRac = fileName.substring(0, index);
        String fileNameExt = fileName.substring(index);

    	//*************************************************************
        // Construction du nouveau document
    	//*************************************************************
        try {
        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        	builder = factory.newDocumentBuilder();
            document = builder.newDocument();
        } catch (Exception ex) {
            throw new DsmlAdapterException("DsmlAdapter : getDocument : Erreur lors de la construcion du document");
        }

    	//*************************************************************
        // Construction de l'entr�e <dsml>
    	//*************************************************************
        Element elementDsml = document.createElement("dsml");
        document.appendChild(elementDsml);

    	//*************************************************************
        // Construction de l'entr�e <directory-entries>
    	//*************************************************************
        Element elementDirectoryEntries = document.createElement("directory-entries");
        elementDsml.appendChild(elementDirectoryEntries);

        JediLog.log(JediLog.LOG_FUNCTIONAL, JediLog.INFO, "", "", null);
        JediLog.log(JediLog.LOG_FUNCTIONAL, JediLog.INFO, "TotalMemory : ", (new Long(Runtime.getRuntime().totalMemory())).toString(), null);
        JediLog.log(JediLog.LOG_FUNCTIONAL, JediLog.INFO, "FreeMemory : ", (new Long(Runtime.getRuntime().freeMemory())).toString(), null);

        int counter = 0;

        for (JediObject jediObject : jediObjectList) {
            counter++;

            try {
            	JediAttributeList listResult = jediObject.loadAllAttribute();

            	//*************************************************************
                // Construction de l'entr�e <entry dn=...>
            	//*************************************************************
            	Element elementDirectoryEntryDn = document.createElement("entry");
                elementDirectoryEntryDn.setAttribute("dn", (String)listResult.get("distinguishedName").get());
                elementDirectoryEntries.appendChild(elementDirectoryEntryDn);
                
                JediLog.log(JediLog.LOG_FUNCTIONAL, JediLog.INFO, "Ajout de l'entr�e : ", (String)listResult.get("distinguishedName").get(), null);

                JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "", "", null);
                JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "Ajout de l'entr�e : ", (String)listResult.get("distinguishedName").get(), null);

            	//*************************************************************
                // Construction de l'entr�e <objectClass>
            	//*************************************************************
                Element elementDirectoryEntryObjectClass = document.createElement("objectClass");
                elementDirectoryEntryDn.appendChild(elementDirectoryEntryObjectClass);

                
                @SuppressWarnings("rawtypes")
				NamingEnumeration ne = listResult.get("objectClass").getAll();

                JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "\tobjectClass", "", null);

            	//-------------------------------------------------------------
                // Construction des valeurs de l'attribut objectClass
            	//-------------------------------------------------------------
                while (ne.hasMoreElements()) {
                	Element elementDirectoryEntryObjectClassValue = document.createElement("oc-value");
                    elementDirectoryEntryObjectClass.appendChild(elementDirectoryEntryObjectClassValue);

                    Text text = document.createTextNode("oc-value");
                    text.setNodeValue((String)ne.nextElement());
                    elementDirectoryEntryObjectClassValue.appendChild(text);
                }

            	//*************************************************************
                // Construction des autres attributs du JediObject
            	//*************************************************************
                List<JediAttribute> jediAttributeList = listResult.getAllJediAttribute();

                if (jediAttributeList == null || jediAttributeList.isEmpty()) {
                    continue;
                }

                Iterator<JediAttribute> iterator = jediAttributeList.iterator();
                while (iterator.hasNext()) {
                	JediAttribute jediAttribute = iterator.next();

                	// Si cet attribut est objectClass ou objectSID on l'ignore car objectClass est 
                	// trait� au pr�alable et objectSID est renseign� automatiquement lors de la
                    // cr�ation de l'objet
                    if (jediAttribute.getName().equalsIgnoreCase("objectClass") || jediAttribute.getName().equalsIgnoreCase("objectSid")) {
                        continue;
                    }

                    JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "\t", jediAttribute.getName(), null);

                    addAttributeInDocument(jediAttribute, document, elementDirectoryEntryDn);
                }// Fin du while
            } catch (Exception e) {
                JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "", "", null);
                JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "ERREUR LORS DE LA CONSTRUCTION DU DOCUMENT", "", null);

                throw new DsmlAdapterException("DsmlAdapter : getDocument (Vector) : Erreur interne lors de la construction du document");
            }

            //Controle de la memoire
            sizeFreeMemory = (new Long(Runtime.getRuntime().freeMemory())).intValue();

            if (sizeFreeMemory <= 100000000 || counter == 1000) {
                isPartial  = true;
                counter = 0;
                countNumberPart ++;

                serializeDocument(document, fileNameRac + "-" + countNumberPart + fileNameExt);

                document = null;
                System.gc();

                //*************************************************************
                // Construction du nouveau document
            	//*************************************************************
                document = builder.newDocument();

                //*************************************************************
                // Construction de l'entr�e <dsml>
            	//*************************************************************
                elementDsml = document.createElement("dsml");
                document.appendChild(elementDsml);

            	//*************************************************************
                // Construction de l'entr�e <directory-entries>
            	//*************************************************************
                elementDirectoryEntries = document.createElement("directory-entries");
                elementDsml.appendChild(elementDirectoryEntries);
            }
        }// Fin du for

        if (isPartial) {
            countNumberPart ++;
            serializeDocument(document, fileNameRac + "-" + countNumberPart + fileNameExt);
        } else {
        	serializeDocument(document, fileName);
        }
    }

    /**
     * Methode permettant d'ajouter au document dsml l'attribut pass� en parametre
     * 
     * @param jediAttribute
     * @param document
     * @param elementDirectoryEntryDn
     * @throws NamingException 
     */
    private static void addAttributeInDocument(JediAttribute jediAttribute, Document document, Element elementDirectoryEntryDn) throws NamingException {
    	//*************************************************************
        // Construction de l'entr�e <attr>
    	//*************************************************************
        Element elementDirectoryEntryAttr = document.createElement("attr");
        elementDirectoryEntryDn.appendChild(elementDirectoryEntryAttr);
        elementDirectoryEntryAttr.setAttribute("name", jediAttribute.getName());

    	//*************************************************************
        // Construction des valeurs de l'attributs
    	//*************************************************************
        @SuppressWarnings("rawtypes")
		NamingEnumeration valueEnumeration = jediAttribute.getAll();

        if (valueEnumeration == null) {
            return;
        }

        Base64Encoder encoder = new Base64Encoder();

        while (valueEnumeration.hasMore()) {
            try {
                Object object = valueEnumeration.next();

            	//-------------------------------------------------------------
                // Si la valeur de l'attribut est une instance de string alors 
                // on l'ajoute au document
            	//-------------------------------------------------------------
                if (object instanceof String) {
                	Element elementDirectoryEntryAttrValue = document.createElement("value");
                    elementDirectoryEntryAttr.appendChild(elementDirectoryEntryAttrValue);

                    Text text = document.createTextNode((String)object);
                    elementDirectoryEntryAttrValue.appendChild(text);
                }
            	//-------------------------------------------------------------
                // Si la valeur de l'attribut est une instance binaire on 
                // l'encode en base 64
            	//-------------------------------------------------------------
                else if (object instanceof byte[]) {
                	Element elementDirectoryEntryAttrValue = document.createElement("value");
                    elementDirectoryEntryAttrValue.setAttribute("encoding","base64");
                    elementDirectoryEntryAttr.appendChild(elementDirectoryEntryAttrValue);

                    encoder.translate((byte[])object);
                    char[] chars = encoder.getCharArray();

                    Text text = document.createTextNode(new String(chars));
                    elementDirectoryEntryAttrValue.appendChild(text);
                }
            } catch (Exception ex) {
                JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "ERREUR LORS DE LA CONSTRUCTION DE L'ATTRIBUT", jediAttribute.getName(), null);

                continue;
            }
        }
    }

    /**
     * M�thode qui g�n�re le fichier � partir du Document pass� en param�tre.
     *
     * @param doc Document dont on veut le fichier XML.
     * @param fileName Chemin du fichier XML qui sera g�n�r�.
     * @throws DsmlAdapterException
     */
    private static void serializeDocument(Document document, String fileName) throws DsmlAdapterException {
        JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "", "", null);
        JediLog.log(JediLog.LOG_FUNCTIONAL, JediLog.INFO, "", "", null);

    	//*************************************************************
        // V�rification de la validit� des param�tres
    	//*************************************************************
        if (document == null || fileName == null) {
            JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "Erreur de sauvegarde du fichier DSML", "", null);
            JediLog.log(JediLog.LOG_FUNCTIONAL, JediLog.INFO, "Erreur de sauvegarde du fichier DSML", "", null);

            throw new DsmlAdapterException("DsmlAdapter : save (Document, String) : L'un des param�tres au moins est null");
        }

        try {
        	//*************************************************************
        	// Serialisation du document
        	//*************************************************************
            OutputFormat format = new OutputFormat(document);
            format.setEncoding("ISO-8859-1");
            format.setIndenting(true);
            format.setPreserveSpace(false);

            FileWriter fileWriter = new FileWriter(fileName);
            XMLSerializer serial = new XMLSerializer(fileWriter, format);

            serial.asDOMSerializer();
            serial.serialize(document.getDocumentElement());
        } catch (java.io.IOException ioe) {
            JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "Erreur de sauvegarde du fichier DSML", "", null);
            JediLog.log(JediLog.LOG_FUNCTIONAL, JediLog.INFO, "Erreur de sauvegarde du fichier DSML", "", null);

            throw new DsmlAdapterException("DsmlAdapter : save (Document, String) : Erreur interne lors de la construction du fichier XML");
        }

        JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "Sauvegarde du fichier DSML r�ussie", "", null);
        JediLog.log(JediLog.LOG_FUNCTIONAL, JediLog.INFO, "Sauvegarde du fichier DSML r�ussie", "", null);
    }

    /**
     * M�thode qui met � jour la base � partir du Document.
     *
     * @param doc Document � "int�grer" dans la base.
     * @param server Serveur sur lequel on va se connecter.
     * @param alias Alias de la connexion.
     * @throws DsmlAdapterException
     */
    public static void setDocument(Document document, JediServer server, String alias) throws DsmlAdapterException {
    	//*************************************************************
        // V�rification de la validit� des param�tres
    	//*************************************************************
        if (document == null || server == null || alias == null) {
            throw new DsmlAdapterException("DsmlAdapter : setDocument (Document, JediServer, String) : L'un des param�tres au moins est null");
        }

    	//*************************************************************
        // Conversion des nodes en jediObjet
    	//*************************************************************
        List<JediObject> jediObjectList = convertNodeListToJediObjectList(document, server, alias);

    	//*************************************************************
        // Insertion en base de la liste des JediObjet
    	//*************************************************************
        insertIntoDatabaseJediObjectList(jediObjectList, server, alias);
    }// Fin de la m�thode

    /**
     * Methode permettant de convertir les nodeEntry d'un document en liste de JediObject
     * 
     * @param document
     * @param server
     * @param alias
     * @return
     * @throws DsmlAdapterException 
     */
    @SuppressWarnings("unchecked")
	private static List<JediObject> convertNodeListToJediObjectList(Document document, JediServer server, String alias) throws DsmlAdapterException {
    	List<JediObject> result = new ArrayList<JediObject>();

        try {
        	Base64Decoder decoder = new Base64Decoder();
        	
        	NodeList nodeListEntry  = XPathAPI.selectNodeList(document, "/dsml/directory-entries/entry");

            JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "", "", null);
            JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "Nombre d'entr�e dans le fichier DSML : " + nodeListEntry.getLength(), "", null);

        	//*************************************************************
            // Transformation de chaque element du document sous forme de
            // JediObject
        	//*************************************************************
            for (int indexEntry = 0; indexEntry < nodeListEntry.getLength(); indexEntry++) {
                Node nodeEntry = nodeListEntry.item(indexEntry);

                JediAttributeList jediAttributeList = new JediAttributeList();

            	//*************************************************************
                // On r�cup�re le dn de l'objet et on regarde si il a le chemin
                // racine de sp�cifi�. Si c'est le cas il faut retirer le 
                // chemin racine du DN.
            	//*************************************************************
                String tempDn = nodeEntry.getAttributes().item(0).getNodeValue();

                JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO,"\nEntry : ", tempDn, null);

                tempDn = ObiUtil.upperCasePath(tempDn);
                String rootPath = server.getRootPath(alias);
                rootPath = ObiUtil.upperCasePath(rootPath);

                if (tempDn.indexOf(rootPath) != -1) {
                    tempDn = tempDn.substring(0,tempDn.indexOf(rootPath) - 1);
                }

            	//*************************************************************
                // Creation de la nouvelle instance de JediObject 
            	//*************************************************************
                JediObject jediObject = new JediObject(alias, server, new JediPath(tempDn));

            	//*************************************************************
                // Construction des attributs pour le JediObject
            	//*************************************************************
                NodeList nodeListAttr = nodeEntry.getChildNodes();

                for (int indexAttr = 0; indexAttr< nodeListAttr.getLength();indexAttr++) {
                    Node nodeAttr = nodeListAttr.item(indexAttr);

                    if (nodeAttr == null || nodeAttr.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }

                    NodeList nodeListValue = nodeAttr.getChildNodes();

					@SuppressWarnings("rawtypes")
					List valuesList = new ArrayList();

                    for (int indexValue = 0; indexValue< nodeListValue.getLength(); indexValue++) {
                        Node nodeValue = nodeListValue.item(indexValue);

                        if (nodeValue == null || nodeValue.getNodeType() != Node.ELEMENT_NODE) {
                            continue;
                        }

                        String valueAtt = null;
                    	//-------------------------------------------------------------
                        // On regarde si l'attribut est code en base 64
                    	//-------------------------------------------------------------
                        try {
                            valueAtt = nodeValue.getAttributes().getNamedItem("encoding").getNodeValue();
                        } catch (Exception ex) {
                            valueAtt = "";
                        }

                    	//-------------------------------------------------------------
                        // Si l'attribut est cod� en base 64 alors on le d�code, sinon
                        // on ne laisse tel quel.
                    	//-------------------------------------------------------------
                        if (valueAtt.equalsIgnoreCase("base64")) {
                            decoder.translate(nodeValue.getChildNodes().item(0).getNodeValue());
                            valuesList.add(decoder.getByteArray());
                        } else {
                            NodeList nl = nodeValue.getChildNodes();
                            Node n = nl.item(0);
                            valuesList.add(n.getNodeValue());
                        }
                    }

                	//-------------------------------------------------------------
                    // On r�cup�re le nom de l'attribut. Si il a une erreur c'est 
                    // qu'il s'agit de objectClass
                	//-------------------------------------------------------------
                    String nameAttr = null;
                    try {
                        nameAttr = nodeAttr.getAttributes().item(0).getNodeValue();
                    } catch (NullPointerException e) {
                        nameAttr = "objectClass";
                    }

                    nameAttr = nameAttr.trim();

                    try {
                        jediAttributeList.put(new JediAttribute(nameAttr, valuesList));

                        JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "Attribut : ", nameAttr, null);
                    } catch (Exception ex) {
                    	//-------------------------------------------------------------
                        // Ne pas renseigner l'attribut objectGUID pour la cr�ation
                    	//-------------------------------------------------------------
                    	if (nameAttr.equalsIgnoreCase("objectGUID") == false) {
                        	jediAttributeList.put(new JediAttribute(nameAttr, (byte[])valuesList.get(0)));

                            JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "Attribut : ", nameAttr, null);
                        }// Fin du if
                    }// Fin du catch
                }// Fin du for

            	//*************************************************************
                // Rattachement de la liste d'attributs au JediObject
            	//*************************************************************
                jediObject.setJediAttributeList(jediAttributeList);

            	//*************************************************************
                // Ajout du JediObject a la liste de resultats
            	//*************************************************************
                result.add(jediObject);
            }
        } catch (Exception ex) {
            throw new DsmlAdapterException("DsmlAdapter : setDocument (Document, JediServer, String) : Erreur interne lors de la construction du vecteur d'objets");
        }
        
        return result;
    }
    
    /**
     * Methode permettant l'insertion en base de la liste des JediObjet
     * 
     * @param jediObjectList
     * @param server
     * @param alias
     */
    private static void insertIntoDatabaseJediObjectList(List<JediObject> jediObjectList, JediServer server, String alias) {
        for (JediObject jediObject : jediObjectList) {
            try {
                server.createLdapEntry(alias, jediObject);

                JediLog.log(JediLog.LOG_TECHNICAL,JediLog.INFO, "Creation reussie de l'entree : ", jediObject.getPartialDN(), null);
                JediLog.log(JediLog.LOG_FUNCTIONAL,JediLog.INFO, "Creation reussie de l'entree : ", jediObject.getPartialDN(), null);
            } catch (Exception ex) {
                try {
                	//*************************************************************
                    // Echec � la cr�ation de l'objet. On essaie de faire une mise
                	// � jour des attributs afin de supprimer les non modifiables
                	//*************************************************************
                	JediAttributeList list = jediObject.getJediAttributeList();
                    list.remove("distinguishedName");
                    list.remove("adminDisplayName");
                    list.remove("schemaIDGUID");
                    list.remove("name");
                    list.remove("whenCreated");
                    list.remove("whenChanged");
                    list.remove("objectGUID");
                    list.remove("uSNChanged");
                    list.remove("uSNCreated");
                    list.remove("oMObjectClass");
                    list.remove("ou");

                    jediObject.setJediAttributeList(list);

                    server.createLdapEntry(alias, jediObject);

                    JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "Creation reussie de l'entree : ", jediObject.getPartialDN(), null);
                    JediLog.log(JediLog.LOG_FUNCTIONAL, JediLog.INFO, "Creation reussie de l'entree : ", jediObject.getPartialDN(), null);
                } catch (Exception ex1) {
                    try {
                        JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "Echec de creation de l'entree : ", jediObject.getPartialDN(), null);
                    } catch (Exception ex2) {
                        JediLog.log(JediLog.LOG_TECHNICAL, JediLog.INFO, "Echec de creation d'une entree : ", "", null);
                    }
                }
            }
        }
    }

    /**
     * M�thode qui � partir d'un fichier XML g�n�re le document associ�.
     *
     * @param fileName Chemin d'acc�s au fichier XML.
     * @return Le document g�n�r� � partir du fichier XML.
     * @throws DsmlAdapterException
     */
    public static Document load (String fileName) throws DsmlAdapterException {
    	//*************************************************************
        // V�rification de la validit� des param�tres
    	//*************************************************************
        if (fileName == null) {
            throw new DsmlAdapterException("DsmlAdapter : load (String) : fileName est null");
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setCoalescing(false);
        factory.setValidating(false);

        factory.setIgnoringElementContentWhitespace(true);
        factory.setIgnoringComments(true);

        try {
        	DocumentBuilder builder  = factory.newDocumentBuilder();
            return builder.parse(new FileInputStream(fileName));
        } catch (javax.xml.parsers.ParserConfigurationException pcex) {
            throw new DsmlAdapterException("DsmlAdapter : load (String) : Erreur de configuration du parser");
        } catch (java.io.FileNotFoundException fnfex) {
            throw new DsmlAdapterException("DsmlAdapter : load (String) : Le fichier n'est pas trouv�");
        } catch (java.io.IOException ioex) {
            throw new DsmlAdapterException("DsmlAdapter : load (String) : Erreur d'entr�e/sortie sur le fichier");
        } catch (org.xml.sax.SAXException saxex) {
            throw new DsmlAdapterException("DsmlAdapter : load (String) : Erreur interne");
        }
    }

    /**
     * M�thode qui fait le diff�rentiel entre deux Documents et qui renvoie sous
     * forme de Document les diff�rences.
     *
     * @param documentReference Premier Document � comparer.
     * @param documentToCompare Second Document � comparer.
     * @param mode Mode de comparaison : entry / attr / all.
     * @param resultFile Fichier resultat.
     * @throws DsmlAdapterException
     */
    public static void diff(Document documentReference, Document documentToCompare, String mode, String resultFile) throws DsmlAdapterException {
        NodeList nodeListEntryReference = null;
        NodeList nodeListEntryToCompare = null;
    	Document document = null;

        boolean attributeCompare = false;
        boolean dnCompare = false;

    	//*************************************************************
        // V�rification de la validit� des param�tres
    	//*************************************************************
        if (documentReference == null || documentToCompare == null || mode == null || mode.length() == 0) {
            throw new DsmlAdapterException("DsmlAdapter : diff (Document, Document, boolean, boolean) : L'un des param�tres au moins est null ou les 2 bool�ens sont � false");
        }

        if (mode.equalsIgnoreCase("entry")) {
            attributeCompare = false;
            dnCompare        = true;
        } else if (mode.equalsIgnoreCase("attr")) {
            attributeCompare = true;
            dnCompare        = false;
        } else if (mode.equalsIgnoreCase("all")) {
            attributeCompare = true;
            dnCompare        = true;
        } else {
            throw new DsmlAdapterException("DsmlAdapter : diff (Document, Document, boolean, boolean) : entry / attr / all");
        }

        try {
            nodeListEntryReference = XPathAPI.selectNodeList(documentReference,"/dsml/directory-entries/entry");
            nodeListEntryToCompare = XPathAPI.selectNodeList(documentToCompare,"/dsml/directory-entries/entry");
        } catch (Exception e) {
            throw new DsmlAdapterException("DsmlAdapter : diff (Document, Document, boolean, boolean)");
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.newDocument();
        } catch (Exception ex) {
            throw new DsmlAdapterException("DsmlAdapter : diff (Document, Document, boolean, boolean) : Erreur lors de la construcion du document ");
        }

    	//*************************************************************
        // Construction de l'entr�e <dsml>
    	//*************************************************************
        Element elementDsml = document.createElement("dsml");
        document.appendChild(elementDsml);

    	//*************************************************************
        // Construction de l'entr�e <directory-entries>
    	//*************************************************************
        Element elementDirectoryEntries = document.createElement("directory-entries");
        elementDsml.appendChild(elementDirectoryEntries);

    	//*************************************************************
        // On r�cup�re les entr�es et on regarde si l'entr�e existe
        // dans le second Document
    	//*************************************************************
        for (int i = 0; i < nodeListEntryReference.getLength(); i++) {
        	//-------------------------------------------------------------
        	// Comparaison sur les dn car c'est toujours le cas quelque
        	// soit le mode choisi
        	//-------------------------------------------------------------
        	Node nodeReference = nodeListEntryReference.item(i);
            String dnReference = nodeReference.getAttributes().item(0).getNodeValue();

            Node nodeToCompare = null;
            String dnToCompare = null;
            boolean exist = false;

            for (int j = 0; j < nodeListEntryToCompare.getLength(); j++) {
                nodeToCompare = nodeListEntryToCompare.item(j);
                dnToCompare = nodeToCompare.getAttributes().item(0).getNodeValue();

                if (dnToCompare.equalsIgnoreCase(dnReference)) {
                    JediLog.log(JediLog.LOG_TECHNICAL,JediLog.INFO,"Mise en correspondance de : ", dnToCompare, null);
                    JediLog.log(JediLog.LOG_FUNCTIONAL,JediLog.INFO,"Mise en correspondance de : ", dnToCompare, null);

                    exist = true;
                    break;
                }
            }

        	//-------------------------------------------------------------
            // Si l'entr�e existe il faut regarder s'il y a des diff�rences
            // au niveau des noeuds fils
        	//-------------------------------------------------------------
            if (exist == true && attributeCompare == true) {
	        	//-------------------------------------------------------------
	            // Si il existe des diff�rences au niveau des noeuds fils alors
	            // il faut cr�er l'objet dans la nouvelle DOM
	        	//-------------------------------------------------------------
	            if (compareAttr(nodeReference, nodeToCompare) == false) {
	                Node nodeDiff = document.importNode(nodeReference, true);
	                elementDirectoryEntries.appendChild(nodeDiff);

	                JediLog.log(JediLog.LOG_TECHNICAL,JediLog.INFO,"Difference d'attributs : ", dnToCompare, null);
	                JediLog.log(JediLog.LOG_FUNCTIONAL,JediLog.INFO,"Difference d'attributs : ", dnToCompare, null);
	            }
            }
        	//-------------------------------------------------------------
            // Si l'entr�e n'existe pas alors il faut la cr�er
        	//-------------------------------------------------------------
            else {
                if (exist == false && dnCompare == true) {
                	Node nodeDiff = document.importNode(nodeReference, true);
                    elementDirectoryEntries.appendChild(nodeDiff);

                    JediLog.log(JediLog.LOG_TECHNICAL,JediLog.INFO,"Cr�ation de l'entr�e : ", dnReference, null);
                    JediLog.log(JediLog.LOG_FUNCTIONAL,JediLog.INFO,"Cr�ation de l'entr�e : ", dnReference, null);
                }
            }
        }// Fin du for

        serializeDocument(document, resultFile);
    }

    /**
     * M�thode qui compare les attributs des 2 noeuds et qui dit s'il y a des differences.
     * Les attributs exclus de la comparaison  sont ceux de la liste attributeToIgnoreForDiff
     *
     * @param node1 Premier noeud � comparer.
     * @param node2 Second noeud � comparer.
     * @return True si les noeuds sont identiques, false sinon.
     * @throws DsmlAdapterException
     */
    private static boolean compareAttr (Node node1, Node node2) throws DsmlAdapterException {
        String nameNodeTemp1   = null;
        String nameNodeTemp2   = null;
        Node nodeTemp1     = null;
        Node nodeTemp2     = null;

    	//*************************************************************
        // V�rification de la validit� des param�tres
    	//*************************************************************
        if (node1 == null || node2 == null) {
            throw new DsmlAdapterException("DsmlAdapter : compareAttr (Node, Node) : L'un des param�tres au moins est null");
        }

    	//*************************************************************
        // Si les noeuds ont un nombre diff�rent de fils alors ils sont
        // diff�rents
    	//*************************************************************
        NodeList nodeList1 = node1.getChildNodes();
        NodeList nodeList2 = node2.getChildNodes();

        if (nodeList1.getLength() != nodeList2.getLength()) {
            return false;
        }

    	//*************************************************************
        // Pour chaque fils du noeud on va regarder ses valeurs
    	//*************************************************************
        for (int i = 0; i < nodeList1.getLength(); i++) {
        	//-------------------------------------------------------------
            // R�cup�ration du fils du noeud
        	//-------------------------------------------------------------
            nodeTemp1 = nodeList1.item(i);

        	//-------------------------------------------------------------
            // On r�cup�re son nom. Si on le trouve pas alors il s'agit de
            // l'attribut objectClass
        	//-------------------------------------------------------------
            try {
                nameNodeTemp1 = nodeTemp1.getAttributes().item(0).getNodeValue();
                if (attributeToIgnoreForDiff.contains(nameNodeTemp1)) {
                    continue;
                }
            } catch (Exception ex) {
                nameNodeTemp1 = "objectClass";
            }

        	//-------------------------------------------------------------
            // Parcours du second Document � la recherche d'un fils portant
            // le meme nom
        	//-------------------------------------------------------------
            boolean foundAttr = false;
            for (int j = 0; j < nodeList2.getLength(); j++) {
                try {
                	nodeTemp2 = nodeList2.item(j);
                    nameNodeTemp2 = nodeTemp2.getAttributes().item(0).getNodeValue();
                } catch (Exception ex) {
                    nameNodeTemp2 = "objectClass";
                }

                // Si on trouve l'attribut alors on le signale et on sort
                if (nameNodeTemp1.equalsIgnoreCase(nameNodeTemp2)) {
                    foundAttr = true;
                    break;
                }
            }

        	//-------------------------------------------------------------
            // Si on n'a pas trouv� l'attribut dans le noeud 2 alors il
            // y a des diff�rences
        	//-------------------------------------------------------------
            if (foundAttr == false) {
                JediLog.log(JediLog.LOG_TECHNICAL,JediLog.INFO,"Attribut non trouv� : ", nameNodeTemp2, null);

                return false;
            }

        	//-------------------------------------------------------------
            // On a trouv� l'attribut, et on compare maintenant les valeurs
        	//-------------------------------------------------------------
            NodeList nodeListTemp1 = nodeTemp1.getChildNodes();
            NodeList nodeListTemp2 = nodeTemp2.getChildNodes();

            List<String> nodeValueList2 = new ArrayList<String>();
            for (int k = 0; k < nodeListTemp2.getLength(); k++) {
            	nodeValueList2.add(nodeListTemp2.item(k).getChildNodes().item(0).getNodeValue());
            }

            // Pour chaque valeur du noeud du premier document on regarde si 
            // elle fait partie des valeurs du noeud du second  document
            boolean isDifferent = false;
            for (int m = 0; m < nodeListTemp1.getLength(); m++) {
                if (nodeValueList2.contains(nodeListTemp1.item(m).getChildNodes().item(0).getNodeValue()) == false) {
                    isDifferent = true;
                }
            }

        	//-------------------------------------------------------------
            // Si les valeurs d'attributs sont diff�rentes
        	//-------------------------------------------------------------
            if (isDifferent == true) {
                JediLog.log(JediLog.LOG_TECHNICAL,JediLog.INFO,"Difference de valeurs pour l'attribut : ", nameNodeTemp2, null);

                return false;
            }
        }// Fin du for

        return true;
    }// Fin de la m�thode

}//Fin de la classe