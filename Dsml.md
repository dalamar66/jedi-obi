# **DSML** #

De nos jours, les services d’annuaire et XML constituent les fondements d’une bonne gestion des données. Avec DSML (Directory Service Markup Language), langage standardisé semblable au XML pour représenter les informations hiérarchiques, les services d’annuaires peuvent bénéficier des avantages du XML. En effet les services d’annuaires permettent de stocker et gérer les données tandis que XML permet de présenter et d’échanger les données. Ainsi le DSML permet aux services d’annuaires d’être plus dynamiques en intégrant du XML.

Un document DSML peut décrire les entrées d’une structure hiérarchique (comme les bases LDAP), ou la structure hiérarchique elle-même, ou les deux à la fois.
La balise racine est `<DSML>` et ses enfants peuvent être `<DIRECTORY-ENTRIES>` si c’est la représentation des entrées de la base, ou `<DIRECTORY-SCHEMA>` si c’est la représentation du schéma de la base LDAP.

```
<DSML>
<DIRECTORY-SCHEMA>
…
</DIRECTORY-SCHEMA>
<DIRECTORY-ENTRIES >
…
</DIRECTORY-ENTRIES>
</DSML>
```

### Directory-entries ###

Les nœuds `<DIRECTORY-ENTRIES>` peuvent contenir les informations de la classe d’objets ainsi que les informations sur les attributs comme ci-dessous :

```
<DSML>
<DIRECTORY-ENTRIES>
   <ENTRY DN="OU=HUMEAU Xavier, DC=Personnes, DC=societe, DC=com">
      <OBJECTCLASS>
         <OC-VALUE>top</OC-VALUE>
         <OC-VALUE>organizationalUnit</OC-VALUE>
      </OBJECTCLASS>
      <ATTR NAME="otherMail">
         <VALUE>xhumeau@gmail.com</VALUE>
      </ATTR>
      <ATTR NAME="OID">
         <VALUE ENCODING="base64">MIICJ+6K…<VALUE>
      </ATTR>
   </ENTRY>
</DIRECTORY-ENTRIES>
</DSML>
```

Les enfants directs de `<DIRECTORY-ENTRIES>` sont les `<ENTRY>` avec pour attribut de balise "DN" qui est le distinguishdName de l’entrée et qui identifie parfaitement l’entrée traitée.
Pour chaque `<ENTRY>` il n’existe que 2 types d’enfants possibles : `<OBJECTCLASS>` ou `<ATTR>`.
  * `<OBJECTCLASS>` définit l’attribut LDAP objectClass et chaque valeur LDAP de cet attribut est entre les balises `<OC-VALUE>` et `</OC-VALUE>`.
  * `<ATTR>` définit les autres attributs LDAP et chaque valeur LDAP de ces attributs est entre les balises `<VALUE>` et `</VALUE>`. Contrairement à la balise `<OBJECTCLASS>` il faut donner le nom de l’attribut LDAP en renseignant l’attribut de balise "NAME".

Pour les valeurs d’attributs LDAP binaires il faut le préciser dans le fichier DSML. On renseigne donc l’attribut de balise "encoding" avec pour valeur base64 au niveau de la balise `<VALUE>`.

### Directory schema ###

Pour ce qui est du schéma de la base LDAP, la syntaxe DSML est la suivante :

```
<DSML>
<CLASS ID="person" SUPERIOR="#top" TYPE="structural">
<NAME>person</NAME>
<DESCRIPTION>…</DESCRIPTION>
<ATTRIBUTE REF="#sn" REQUIRED="true" />
<ATTRIBUTE REF="#cn" REQUIRED="true" />
<ATTRIBUTE REF="#seeAlso" REQUIRED="false" />
</CLASS>
<ATTRIBUTE-TYPE ID="cn">
	<NAME>cn</NAME>
	<DESCRIPTION>…</DESCRIPTION>
	…
</ATTRIBUTE-TYPE>
</DSML>
```

Pour la description du schéma il faut distinguer les objets des attributs.
Les objets du schéma LDAP sont délimités par les balises `<CLASS>` et `</CLASS>`, et les attributs du schéma LDAP par les balises `<ATTRIBUE-TYPE>` et `</ATTRIBUE-TYPE>`

  * Pour les objets du schéma il faut renseigner la valeur de chaque attribut de l’objet LDAP en les encadrant entre des balises spécifiant le nom de l’attribut, par exemple : `<NAME>`Nom de test`</NAME>`. De plus, par définition, les objets du schéma peuvent avoir une liste d’attributs pointés. On les précise de la façon suivante :
(Exemple)    `<ATTRIBUTE REF="#sn" REQUIRED="true" />`, ce qui signifie que l’attribut sn est un des attributs pointés par l’objet de la base et que celui-ci est obligatoire.

  * Pour les attributs du schéma il faut renseigner la valeur de chaque attribut de l’objet LDAP en les encadrant entre des balises spécifiant le nom de l’attribut, par exemple : `<NAME>`Nom de test`</NAME>`.

### Fichier de configuration ###

```
com.byconst.ref.dsml.directoryserver   = ldap://256.256.256.256
com.byconst.ref.dsml.directoryserver   = ldap://societe.domaine.com

com.byconst.ref.dsml.directorypassword = xxx

com.byconst.ref.dsml.directoryrootpath = OU=Comptes,DC=societe,DC=domaine,DC=com

com.byconst.ref.dsml.directoryuser     = cn=Administrateur,cn=Users,DC=societe,DC=domaine,DC=com

com.byconst.ref.dsml.filter            = (&(ou=mar*))

com.ldap.dsml.attributerequired        = cn,sn,givenName
```

### Export ###

```
PATH = %PATH%;c:\jdk1.5\bin

@echo off
echo.
echo Usage : export [chemin / fichier de properties] [chemin / fichier xml]
echo.
echo [chemin / fichier de properties] : Fichier de configuration
echo [chemin / fichier xml]           : Fichier de destination
echo.
java.exe -Xms512m -Xmx512m -classpath jedi-obi.jar com.ldap.dsml.ExportDsml %1 %2
```

### Import ###

```
PATH = %PATH%;c:\jdk1.5\bin

@echo off
echo.
echo Usage : export [chemin / fichier de properties] [chemin / fichier xml]
echo.
echo [chemin / fichier de properties] : Fichier de configuration
echo [chemin / fichier xml]           : Fichier source
echo.
java.exe -Xms512m -Xmx512m -classpath jedi-obi.jar com.ldap.dsml.ImportDsml %1 %2
```

### Differentiel ###

```
PATH = %PATH%;c:\jdk1.5\bin

@echo off
echo.
echo Usage : diff [chemin / fichier xml] [chemin / fichier xml] [entry/attr/all] [chemin / fichier xml]
echo.
echo [chemin / fichier xml] : Fichier XML de référence
echo [chemin / fichier xml] : Fichier XML à comparer
echo [entry]           	    : Les nouveaux elements du modele sont extraits
echo [attr]                 : Seul les attributs sont traites
echo [all]		    : Les deux cas precedents sont appliqués
echo [chemin / fichier xml] : Fichier XML notifiant les différences
echo.
java.exe -Xms512m -Xmx512m -classpath jedi-obi.jar com.ldap.dsml.DiffDsml %1 %2 %3 %4
```