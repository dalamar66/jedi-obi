# **OBI** #

OBI étant une approche métier appuyée sur le toolkit Jedi, celle-ci sera moins détaillée.

La connexion et l’obtention d’un objet métier sont réalisées au moyen de la classe ObiOne. L’objet métier est désigné par la classe ObixxxxxService et ses données par la classe ObixxxxxData. Les opérations d’interrogation, de mise à jour, de création et de suppression de ces entrées sont donc assurées par la classe ObixxxxxService.

Des exemples de OBI sont disponibles sous formes de tests JUnit.

Un exemple simple permet de comprendre l’apport de OBI : Simplicité de la connexion, du code, des opérations de contrôles.

```
//Création des 3 connexions
ObiOne one = new ObiOne(ldap_domain, ldap_racine, ldap_user, ldap_pwd, null, null, null);

//Récuperation du service user
ObiUserService userService = one.getUserService();

//Recherche d’un utilisateur par son nom
List<ObiUserData> list = userService.findUserByFilter("sn", "Humeau");

//Test d’authentification d’une personne
userService.checkUserAuthentification("login", "password");

//Fermeture des connexions
one.closeConnections();
```

### ObiOne ###

ObiOne etablit nativement 3 connections aux bases Ldap : Une sur le directory courant, une seconde au Global Catalog et une troisième au schéma de la base. De plus ObiOne est le fournisseur de services métier.

### ObixxxxxxService ###

Cette classe est la classe métier d’un objet. Dans l’implémentation actuelle, seules les classes users et personnes sont implémentées et permettent les opérations de bases.