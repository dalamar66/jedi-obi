# **JEDI** #

La connexion à une base LDAP est réalisée au moyen de la classe JediServer. Le nœud de connexion est désigné par la classe JediPath qui représente un DN (Distinguished Name). JediPath facilite la constitution et la gestion (agrégation, extraction, traitement des caractères spéciaux) des DN LDAP.

La classe JediObject est la représentation d’une entrée LDAP et les classes JediAttribute, JediAttributeList les attributs LDAP de ces entrées.

Les opérations d’interrogation, de mise à jour, de création et de suppression de ces entrées sont assurées par la classe JediServer.

Des exemples de JEDI sont disponibles sous formes de tests JUnit.

### JediServer ###

La classe JediServer permet de se connecter à une base LDAP et renvoie toutes les informations nécessaires sur la connexion. Cette classe permet aussi d’effectuer toutes les opérations de déplacement, de renommage, de création, de suppression et d’interrogation sur les entrées de la base.

Cette classe est la plus importante du package Jedi dans le sens où aucune autre classe ne peut « exister » sans elle.

### JediPath ###

La classe JediPath permet de formater les chemins LDAP et offre la possibilité d’effectuer les opérations les plus courantes comme le retour du chemin du « père », la taille du chemin, l’ajout d’une branche, etc.

### **JediObject** ###

La classe JediObject est la représentation d’une entrée LDAP. Elle permet de charger et mettre à jour les attributs LDAP qui lui sont rattachés en base (représenté par la classe JediAttributeList).

### **JediAttributeList** ###

La classe JediAttributeList représente en mémoire une liste d’attributs LDAP (sous forme de JediAttribute). Ses principales fonctions sont l’interrogation, l’ajout, la mise à jour et la suppression d’un attribut. Ces opérations n’ont d’effet qu’en mémoire (c.f. JediObject pour la mise en base).

### **JediAttribute** ###

La classe JediAttribute  correspond à un attribut LDAP. Elle permet de gérer les attributs multi et mono valués ainsi que les attributs binaires.

### **JediFilter** ###

La classe JediFilter permet d’effectuer des requêtes sur la base LDAP complexes ou non.

Lors de l’exécution d’une requête les paramètres possibles sont les suivants :
  * Alias : Alias de connexion à la base
  * Path : Chemin à partir duquel s’effectue la requête
  * Attributes : Liste des attributs à charger (Si null, alors tous les attributs seront chargés)
  * Guid : Guid de l’objet recherché
  * Dn : Distinguished Name de l’objet recherché
  * Filter : Filtre Active Directory permettant de rechercher l’objet à partir de ses caractéristiques
  * Subtree : Recherche ou non dans le sous arbre
  * PageSize : Taille de la pagination lors de la recherche. Cela permet d’outrepasser les restrictions du serveur LDAP
  * Limitation : Nombre de résultats désirés
  * Sorted : Critère de tri
  * Approximation : Valeur à approximer
  * Indice : Indice de confiance de l’approximation
  * Attribute : Attribut sur lequel est effectuée l’approximation
  * Metric : Méthode d’approximation désirée

Seul l’alias est obligatoire.

```
//Création du JediFilter
JediFilter jediFilter = new JediFilter();
jediFilter.setAlias("global");
jediFilter.setPath("");
jediFilter.setFilter("(&(objectCategory=Person)(objectClass=user)(sn=Humeau))");
jediFilter.setAttributesList(defaultAttributes);
jediFilter.setSubtree(true);
```

Cet exemple montre comment rechercher une personne dans la base dont le nom est "humeau". La recherche s’effectue à partir de la racine et dans tous les sous arbres, la liste des attributs chargés étant ceux par défaut.