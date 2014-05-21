package com.ldap.jedi;

/**
 * File : JediConnectionPool.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-04 
 * Modification date : 2010-03-04
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.naming.ldap.InitialLdapContext;

/**
 * Classe gérant le pool des connexions.
 * 
 * @author HUMEAU Xavier
 * @version Version 1.0
 */
class JediConnectionPool implements JediConnectionManager {

   protected HashMap<String, ArrayList<JediConnectionImpl>> jediConnectionAlias = null;
   protected HashMap<String, Hashtable<String, String>> jediConnectionsParameters = null;

   protected List<String> aliasList = new ArrayList<String>();

   public JediConnectionPool() {
	jediConnectionAlias = new HashMap<String, ArrayList<JediConnectionImpl>>();
	jediConnectionsParameters = new HashMap<String, Hashtable<String, String>>();
   }

   synchronized public JediConnection getJediConnection(String alias) throws JediConnectionException {
	List<JediConnectionImpl> jediConnectionList = getConnectionAlias(alias);
	boolean foundAliveConnection = false;

	JediConnectionImpl jediConnectionImpl = null;

	try {
	   for (int i = 0; i < jediConnectionList.size(); i++) {
		jediConnectionImpl = jediConnectionList.get(i);
		if (jediConnectionImpl != null && jediConnectionImpl.getUsed() == false) {
		   if (jediConnectionImpl.isConnectionAlive() == true) {
			foundAliveConnection = true;
			jediConnectionImpl.setUsed(true);
			break;
		   }
		   reconnect(jediConnectionImpl);
		   if (jediConnectionImpl.isConnectionAlive() == true) {
			foundAliveConnection = true;
			jediConnectionImpl.setUsed(true);
			break;
		   }
		}
	   }

	   if (foundAliveConnection == false) {
		jediConnectionImpl = createJediConnection(alias);
		jediConnectionImpl.setUsed(true);

		jediConnectionList.add(jediConnectionImpl);
	   }
	} catch (Exception ex) {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_connection_error", "", this);
	   JediLog.log(JediLog.LOG_PRODUCTION, JediLog.ERROR, "jedi_msg_connection_error",
		   "JediConnectionPool : getJediConnection(String alias)", this);

	   throw new JediConnectionException("Alias inexistant ou Connexion impossible");
	}

	return jediConnectionImpl;
   }

   public void addConnectionProvider(String alias, Hashtable<String, String> parameters) throws JediException {
	if (alias == null || alias.length() < 1) {
	   List<String> paramList = new ArrayList<String>();
	   paramList.add(alias);
	   paramList.add(Integer.toString(parameters.size()));

	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_connection_error", paramList, this);
	   JediLog.log(JediLog.LOG_PRODUCTION, JediLog.ERROR, "jedi_msg_connection_error",
		   "JediConnectionPool : addConnectionProvider(String alias, Hashtable parameters)", this);

	   throw new JediException("");
	}

	jediConnectionsParameters.put(alias, parameters);
	jediConnectionAlias.put(alias, new ArrayList<JediConnectionImpl>());
	aliasList.add(alias);
   }

   /**
    * Methode permettant de fermer toutes les connections pour un alias donné.
    */
   synchronized public void closeConnections(String alias, boolean force) throws JediConnectionException {
	try {
	   // Recuperation des connections pour l'alias
	   ArrayList<JediConnectionImpl> jediConnections = getConnectionAlias(alias);

	   for (Iterator<JediConnectionImpl> iterator = jediConnections.iterator(); iterator.hasNext();) {
		JediConnectionImpl jediConnectionImpl = iterator.next();

		if (force == false && jediConnectionImpl.getUsed() == true) {
		   throw new JediException("Connection is still used");
		}

		jediConnectionImpl.setUsed(true);
		InitialLdapContext initialDirContext = jediConnectionImpl.getDirContext();
		initialDirContext.close();

		iterator.remove();
	   }
	} catch (Exception ex) {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_connection_error", "", this);
	   JediLog.log(JediLog.LOG_PRODUCTION, JediLog.ERROR, "jedi_msg_connection_error",
		   "JediConnectionPool : closeConnections(String alias, boolean force)", this);

	   throw new JediConnectionException("Impossible deconnection");
	}
   }

   /**
    * Methode permettant de fermer toutes les connections pour tous les alias.
    */
   public void closeAllConnections() throws JediConnectionException {
	for (String alias : aliasList) {
	   closeConnections(alias, true);
	}
   }

   /**
    * Methode permettant de recuperer l'host ldap pour un alias donné.
    */
   public String getLdapHost(String alias) throws JediException {
	try {
	   return getConnectionParameters(alias).get("LDAPHOST");
	} catch (Exception ex) {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_connection_error", "", this);
	   JediLog.log(JediLog.LOG_PRODUCTION, JediLog.ERROR, "jedi_msg_connection_error", "JediConnectionPool : getLdapHost(String alias)",
		   this);

	   throw new JediException("Unknown alias");
	}
   }

   /**
    * Methode permettant de recuperer le rootPath pour un alias donné.
    */
   public String getRootPath(String alias) throws JediException {
	try {
	   return getConnectionParameters(alias).get("ROOTPATH");
	} catch (Exception ex) {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_connection_error", "", this);
	   JediLog.log(JediLog.LOG_PRODUCTION, JediLog.ERROR, "jedi_msg_connection_error", "JediConnectionPool : getRootPath(String alias)",
		   this);

	   throw new JediException("Unknown alias");
	}
   }

   /**
    * Methode permettant de recuperer le rootPath pour un alias donné.
    */
   public JediPath getJediRootPath(String alias) throws JediException {
	return new JediPath(getRootPath(alias));
   }

   /**
    * Methode permettant de recuperer l'initialContext.
    * 
    * @param alias
    * @return
    * @throws JediConnectionException
    */
   protected InitialLdapContext getInitialLdapContext(String alias) throws JediConnectionException {
	try {
	   return new InitialLdapContext(getConnectionParameters(alias), null);
	} catch (Exception ex) {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_connection_error", "", this);
	   JediLog.log(JediLog.LOG_PRODUCTION, JediLog.ERROR, "jedi_msg_connection_error",
		   "JediConnectionPool : reconnect(JediConnectionImpl jediconnectionimpl)", this);

	   throw new JediConnectionException("Unable to create LDAP Connection");
	}
   }

   /**
    * Methode permettant de reinitialiser une connection.
    * 
    * @param jediconnectionimpl
    * @throws JediConnectionException
    * @throws JediException
    */
   protected void reconnect(JediConnectionImpl jediconnectionimpl) throws JediConnectionException, JediException {
	try {
	   jediconnectionimpl.setDirContext(getInitialLdapContext(jediconnectionimpl.getAlias()));
	} catch (Exception ex) {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_connection_error", "", this);
	   JediLog.log(JediLog.LOG_PRODUCTION, JediLog.ERROR, "jedi_msg_connection_error",
		   "JediConnectionPool : reconnect(JediConnectionImpl jediconnectionimpl)", this);

	   throw new JediConnectionException("Unable to create LDAP Connection");
	}
   }

   /**
    * Methode permettant de créer une connection pour un alias donné.
    * 
    * @param alias
    * @return
    * @throws JediConnectionException
    * @throws JediException
    */
   protected JediConnectionImpl createJediConnection(String alias) throws JediConnectionException, JediException {
	try {
	   return new JediConnectionImpl(getInitialLdapContext(alias), getRootPath(alias), alias);
	} catch (Exception ex) {
	   JediLog.log(JediLog.LOG_TECHNICAL, JediLog.ERROR, "jedi_msg_connection_error", "", this);
	   JediLog.log(JediLog.LOG_PRODUCTION, JediLog.ERROR, "jedi_msg_connection_error",
		   "JediConnectionPool : createJediConnection(String alias)", this);

	   throw new JediConnectionException("Unable to create LDAP Connection");
	}
   }

   /**
    * Methode permettant de recuperer la liste des connections pour un alias donné.
    * 
    * @param alias
    * @return
    * @throws JediConnectionException
    */
   protected ArrayList<JediConnectionImpl> getConnectionAlias(String alias) throws JediConnectionException {
	ArrayList<JediConnectionImpl> connectionAlias = jediConnectionAlias.get(alias);

	if (connectionAlias == null) {
	   JediLog.log(JediLog.LOG_PRODUCTION, JediLog.ERROR, "jedi_msg_connection_error",
		   "JediConnectionPool : getJediConnectionList(String alias)", this);

	   throw new JediConnectionException("ConnectionList not initialized");
	}

	return connectionAlias;
   }

   /**
    * Methode permettant de recuperer les parametres de connection pour un alias donné.
    * 
    * @param alias
    * @return
    * @throws JediException
    */
   protected Hashtable<String, String> getConnectionParameters(String alias) throws JediException {
	Hashtable<String, String> connectionParameters = jediConnectionsParameters.get(alias);

	if (connectionParameters == null) {
	   JediLog.log(JediLog.LOG_PRODUCTION, JediLog.ERROR, "jedi_msg_connection_error",
		   "JediConnectionPool : getConnectionParameters(String alias)", this);

	   throw new JediException("ConnectionList not initialized");
	}

	return connectionParameters;
   }

}// fin de la classe