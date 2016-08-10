import static org.jinterop.dcom.impls.automation.IJIDispatch.IID;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.JIObjectFactory;
import org.jinterop.dcom.impls.automation.IJIDispatch;

import openconnector.*;

/*

 This file  provides a skeletal structure of the openconnector interfaces.
 It is the responsiblity of the connector developer to implement the methods.

 */
public class STAFFWARE extends AbstractConnector {

	private static String urlServerDLL;
	private static String userServerDLL;
	private static String senhaServerDLL;
	private static String nameDLL;
	private static String dominioServerDLL;
	private static String keyCripto;

	// Connect DLL
	public static JIComServer comServer;
	public static JISession session;
	public static IJIDispatch comLocator;
	public static String passowrdCriptografada;

	private Map<String, Object> account;
	private List<Map<String, Object>> resultObjectList;
	private Iterator<Map<String, Object>> it;
	private Map<String, Object> groups;

	// variavel conexão
	private static Connection connection;
	public static String URL;
	public static String SENHA;
	public static String USUARIO;
	public static String DRIVE;
	public CallableStatement cs;
	private boolean m_bIsDriverLoaded;
	public static String host;
	public static String nodName;
	public static String ip;
	public static String porta;
	public static String usuarioStaffaware;
	public static String senhaStaffaware;

	// Variavel
	private static String CO_USUARIO = "CO_USUARIO";
	private static String CO_GRUPO = "CO_GRUPO";
	private static String NO_USUARIO = "NO_USUARIO";

	// ////////////////////////////////////////////
	// ////////////////////////////
	//
	// INNER CLASS
	//
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * An iterator that handle the paging logic to send data in chunks.
	 */
	private class PagingIterator implements Iterator<Map<String, Object>> {

		private Iterator<Map<String, Object>> it;
		Map<String, Object> obj;

		public PagingIterator(Iterator<Map<String, Object>> it) {
			this.it = it;
		}

		public boolean hasNext() {
			/*
			 * Check if the iterator has data if not try to fetch the next chunk
			 * from managed system
			 */
			return this.it.hasNext();
		}

		public Map<String, Object> next() {

			obj = it.next();

			if (obj != null)
				remove();

			return obj;
		}

		public void remove() {
			this.it.remove();
		}
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// CONSTRUCTORS
	//
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Default constructor.
	 */
	public STAFFWARE() {
		super();
		m_bIsDriverLoaded = false;
		it = null;
	}

	/**
	 * Constructor for an OpenConnectorSample.
	 * 
	 * @param config
	 *            The ConnectorConfig to use.
	 * @param log
	 *            The Log to use.
	 */
	public STAFFWARE(ConnectorConfig config, openconnector.Log log)
			throws Exception {
		super(config, log);
		m_bIsDriverLoaded = false;
		it = null;
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// CONNECTOR CAPABILITIES
	//
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Return the list of Features that are supported for the given object type.
	 * If a Feature is returned, the corresponding method should not throw an
	 * UnsupportedOperationException for the given object type.
	 */

	public List<Feature> getSupportedFeatures(String objectType) {
		return Arrays.asList(Feature.values());
	}

	/**
	 * Return a list of the object types supported by this connector. This list
	 * can contain any of the OBJECT_TYPE_* constants or any arbitrary type that
	 * is supported by the connector.
	 */
	public List<String> getSupportedObjectTypes() {

		List<String> types = super.getSupportedObjectTypes();
		types.add(OBJECT_TYPE_GROUP);
		return types;
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// CONNECTOR DIAGNOSTICS
	//
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Test the configured to see if a connection can be made. This should throw
	 * exceptions if the configuration is not complete, has invalid values, the
	 * resource cannot be contacted. If the connection succeeds, no exceptions
	 * are thrown.
	 */
	public void testConnection() {
		try {
			init();

		} catch (Exception e) {
			if (log.isDebugEnabled())
				log.error("Error for TestConnection ", e);
			throw new ConnectorException(
					"Test Connection failed with message : " + e.getMessage()
							+ ".  Please check the connection details.");
		}
	}

	/**
	 * Discover the Schema for the currently configured object type.
	 * 
	 * @throws ConnectorException
	 *             If the operation fails.
	 * @throws UnsupportedOperationException
	 *             If the DISCOVER_SCHEMA feature is not supported.
	 */
	// public Schema discoverSchema() throws ConnectorException,
	// UnsupportedOperationException
	// {
	// Schema schema = new Schema();
	//
	// if (OBJECT_TYPE_ACCOUNT.equals(this.objectType))
	// {
	//
	// } else
	// {
	//
	// }
	//
	// return schema;
	// }

	// //////////////////////////////////////////////////////////////////////////
	//
	// OBJECT ACCESS
	//
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Return the object of the configured object type that has the given native
	 * identifier.
	 * 
	 * @param id
	 *            A unique identifier for the object.
	 * 
	 * @return The object that was read from the resource, or null if an object
	 *         with the given id was not found.
	 *
	 * @throws ConnectorException
	 *             If the operation fails.
	 * @throws UnsupportedOperationException
	 *             If the GET feature is not supported.
	 */
	public Map<String, Object> read(String nativeIdentifier)
			throws ConnectorException, IllegalArgumentException,
			UnsupportedOperationException {

		account = null;
		groups = null;
		it = null;

		if (OBJECT_TYPE_ACCOUNT.equals(this.objectType)) {
			try {
				it = getAccounts(nativeIdentifier);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (it.hasNext()) {
				account = it.next();
				return account;
			}
		}
		if (OBJECT_TYPE_GROUP.equals(this.objectType)) {
			try {
				it = getGroups(nativeIdentifier);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (it.hasNext()) {
				groups = it.next();
				return groups;
			}
		}

		Map<String, Object> readMap = new HashMap<String, Object>();
		// Add the code for single read of objectType
		return readMap;
	}

	/**
	 * Return an iterator over the objects of the configured object type that
	 * match the given filter (or all objects if no filter is specified).
	 * 
	 * @param filter
	 *            The possibly null filter to use to constrain which objects are
	 *            returned.
	 * 
	 * @return An iterator over the object of the configured object type that
	 *         match the given filter.
	 *
	 * @throws ConnectorException
	 *             If the operation fails.
	 * @throws UnsupportedOperationException
	 *             If the ITERATE feature is not supported.
	 */

	public Iterator<Map<String, Object>> iterate(Filter filterArgs)
			throws ConnectorException, UnsupportedOperationException {

		// Return the iterator on a copy of the list to avoid concurrent mod
		// exceptions if entries are added/removed while iterating.

		String filter = "*";

		it = null;

		if (OBJECT_TYPE_ACCOUNT.equals(this.objectType)) {
			try {
				it = getAccounts(filter);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (OBJECT_TYPE_GROUP.equals(this.objectType)) {
			try {
				it = getGroups(filter);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return it;
	}

	/**
	 * Create an object of the currently configured type on the resource.
	 * 
	 * @param nativeIdentifier
	 *            The nativeindentifier of the object to create.
	 * @param items
	 *            The initial objecttype attributes.
	 * 
	 * @throws ConnectorException
	 *             If the operation fails.
	 * @throws ObjectAlreadyExistsException
	 *             If this object already exists on the managed system.
	 * @throws UnsupportedOperationException
	 *             If the CREATE feature is not supported.
	 */
	public Result create(String nativeIdentifier, List<Item> items)
			throws ConnectorException, ObjectAlreadyExistsException {

		String codUsuario = "", noUsuario = "", coGrupo;
		ArrayList valueList =  new ArrayList();
		
		log.debug("Create");
		
		codUsuario = nativeIdentifier;

		for (openconnector.Item item : items) {

			String name = item.getName();
			String value = item.getValue().toString();
			
			log.debug("name "+name);
			log.debug("value "+value);

			if (name.equalsIgnoreCase(NO_USUARIO)) {
				noUsuario = value;
				
				log.debug("noUsuario "+noUsuario);
			}
			if (name.equalsIgnoreCase(CO_GRUPO)) {
								
				valueList = new ArrayList();

				if (item.getValue() instanceof String)
					{
						valueList.add(item.getValue());
						
						log.debug("valueList "+valueList);

					} else
					{
						valueList = (ArrayList) item
								.getValue();
						
						log.debug("valueList "+valueList);

					}
			}
		}
		

		Result result = new Result();
		
		try {
			log.debug("codUsuario "+codUsuario+" noUsuario "+noUsuario);
			
			criaUsuario(codUsuario,noUsuario);
			result = new Result(Result.Status.Committed);
		} catch (Exception e) {
			result = new Result(Result.Status.Failed);
			e.printStackTrace();
		}
		
		for(Object grupo : valueList)
		{
			
			coGrupo = grupo.toString(); 
		
			
			try {
				vinculaUsuarioGrupo(codUsuario,coGrupo);
				
				result = new Result(Result.Status.Committed);
			} catch (Exception e) {
				result = new Result(Result.Status.Failed);
				e.printStackTrace();
			}
		}
		
		// Add the code here for handling the create operation

		return result;
	}

	/**
	 * Update the object of the given type using the given item list.
	 * 
	 * @param nativeIdentitifer
	 *            The object to update
	 * @param items
	 *            A list of items to update
	 *
	 * @throws ConnectorException
	 *             If the operation fails.
	 * @throws ObjectNotFoundException
	 *             If the object to update cannot be found.
	 * @throws UnsupportedOperationException
	 *             If the UPDATE feature is not supported or the options map
	 *             contains operations that are not supported.
	 */

	public Result update(String nativeIdentifier, List<Item> items)
			throws ConnectorException, ObjectNotFoundException {

		Result result = new Result(Result.Status.Committed);
		
		String codUsuario = "", noUsuario = "", coGrupo;
		ArrayList valueList =  new ArrayList();
		
		codUsuario = nativeIdentifier;

		Map<String, Object> existing = read(nativeIdentifier);
		if (null == existing) {
			throw new ObjectNotFoundException(nativeIdentifier);
		}

		if (items != null) {
			for (Item item : items) {
				String name = item.getName();
				String value = item.getValue().toString();
				Item.Operation op = item.getOperation();
				
					if (name.equalsIgnoreCase(NO_USUARIO)) {
						noUsuario = value;
					}
					if (name.equalsIgnoreCase(CO_GRUPO)) {
										
						valueList = new ArrayList();

						if (item.getValue() instanceof String)
							{
								valueList.add(item.getValue());

							} else
							{
								valueList = (ArrayList) item
										.getValue();

							}
					}

				switch (op) {
				case Add: {
					
					for(Object grupo : valueList)
					{
						
						coGrupo = grupo.toString(); 
					
						
						try {
							vinculaUsuarioGrupo(codUsuario,coGrupo);
							
							result = new Result(Result.Status.Committed);
						} catch (Exception e) {
							result = new Result(Result.Status.Failed);
							e.printStackTrace();
						}
					}
				}
					break;
				case Remove: {
					
					for(Object grupo : valueList)
					{
						
						coGrupo = grupo.toString(); 
					
						
						try {
							desvinculaUsuarioGrupo(codUsuario,coGrupo);
							
							result = new Result(Result.Status.Committed);
						} catch (Exception e) {
							result = new Result(Result.Status.Failed);
							e.printStackTrace();
						}
					}
				}
					break;
				case Set: {
					existing.put(name, value);
				}
					break;

				default:
					throw new IllegalArgumentException("Unknown operation: "
							+ op);
				}
			}
		}

		return result;
	}

	/**
	 * Delete the object of the configured object type that has the given native
	 * identifier.
	 * 
	 * @param nativeIdentitifer
	 *            The value for the identity attribute.
	 * @param options
	 *            Options that may influence the deletion.
	 *
	 * @throws ConnectorException
	 *             If the operation fails.
	 * @throws UnsupportedOperationException
	 *             If the DELETE feature is not supported.
	 *
	 */
	public Result delete(String nativeIdentifier, Map<String, Object> options)
			throws ConnectorException, ObjectNotFoundException {

		Result result = new Result();
		
		String codUsuario = "";
		
		codUsuario = nativeIdentifier;
		
		try {
			deleteUsuario(codUsuario);
			
			result = new Result(Result.Status.Committed);
			
		} catch (Exception e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
		}
		
		
		

		return result;
	}

	/**
	 * Enable the object of the configured object type that has the given native
	 * identifier.
	 * 
	 * @param id
	 *            The value for the identity attribute.
	 * @param options
	 *            Options that may influence the enable.
	 *
	 * @throws ConnectorException
	 *             If the operation fails.
	 * @throws ObjectNotFoundException
	 *             If the object to enable cannot be found.
	 * @throws UnsupportedOperationException
	 *             If the ENABLE feature is not supported.
	 */
	public Result enable(String nativeIdentifier, Map<String, Object> options)
			throws ConnectorException, ObjectNotFoundException {

		Result result = new Result(Result.Status.Committed);

		Map<String, Object> obj = read(nativeIdentifier);
		if (null == obj) {
			throw new ObjectNotFoundException(nativeIdentifier);
		}

		return result;
	}

	/**
	 * Disable the object of the configured object type that has the given
	 * native identifier.
	 * 
	 * @param id
	 *            The value for the identity attribute.
	 * @param options
	 *            Options that may influence the disable.
	 *
	 * @throws ConnectorException
	 *             If the operation fails.
	 * @throws ObjectNotFoundException
	 *             If the object to disable cannot be found.
	 * @throws UnsupportedOperationException
	 *             If the DISABLE feature is not supported.
	 */
	public Result disable(String nativeIdentifier, Map<String, Object> options)
			throws ConnectorException, ObjectNotFoundException {

		Result result = new Result(Result.Status.Committed);

		Map<String, Object> obj = read(nativeIdentifier);
		if (null == obj) {
			throw new ObjectNotFoundException(nativeIdentifier);
		}

		return result;
	}

	/**
	 * Unlock the object of the configured object type that has the given native
	 * identifier. An account is typically locked due to excessive invalid login
	 * attempts, whereas a disable is usually performed on accounts that are no
	 * longer in use.
	 * 
	 * @param nativeIdentifier
	 *            The value for the identity attribute.
	 * @param options
	 *            Options that may influence the unlock.
	 *
	 * @throws ConnectorException
	 *             If the operation fails.
	 * @throws ObjectNotFoundException
	 *             If the object to unlock cannot be found.
	 * @throws UnsupportedOperationException
	 *             If the UNLOCK feature is not supported.
	 */

	public Result unlock(String nativeIdentifier, Map<String, Object> options)
			throws ConnectorException, ObjectNotFoundException,
			UnsupportedOperationException {

		Result result = new Result(Result.Status.Committed);

		Map<String, Object> obj = read(nativeIdentifier);
		if (null == obj) {
			throw new ObjectNotFoundException(nativeIdentifier);
		}

		return result;
	}

	/**
	 * Set the password for the given object to the newPassword. The current
	 * password may be passed in, but is not typically required. If available,
	 * the current password should be passed in, since some resources require
	 * this for full behavior around password policy checking, password history,
	 * etc...
	 * 
	 * @param nativeIdentifier
	 *            The value for the identity attribute.
	 * @param newPassword
	 *            The password to set for the account.
	 * @param currentPassword
	 *            The current password of the account. This should be provided
	 *            if available, but is not always required.
	 * @param expiration
	 *            The expiration date for the password.
	 * @param options
	 *            An optional map of options that can provide additional
	 *            information about the password change.
	 *
	 * @throws ConnectorException
	 *             If the operation fails.
	 * @throws ObjectNotFoundException
	 *             If the object to set the password on cannot be found.
	 * @throws UnsupportedOperationException
	 *             If the SET_PASSWORD feature is not supported.
	 */

	public Result setPassword(String nativeIdentifier, String newPassword,
			String currentPassword, Date expiration, Map<String, Object> options)
			throws ConnectorException, ObjectNotFoundException {

		Result result = new Result(Result.Status.Committed);

		Map<String, Object> obj = read(nativeIdentifier);
		if (null == obj) {
			throw new ObjectNotFoundException(nativeIdentifier);
		}

		return result;
	}

	/**
	 * Attempt to authenticate to the underlying resource using the given
	 * identity and password. The identity may be the value of the identity
	 * attribute, but could also be a value that can be used to search for the
	 * account (eg - sAMAccountName, full name, etc...). If successful, this
	 * returns the account that was authenticated to.
	 * 
	 * @param identity
	 *            A value for an identifying attribute of the account to
	 *            authenticate with.
	 * @param password
	 *            The password to use to authenticate.
	 * 
	 * @return The authenticated account if authentication was successful.
	 * 
	 * @throws ConnectorException
	 *             If the operation fails.
	 * @throws AuthenticationFailedException
	 *             If authentication failed.
	 * @throws ExpiredPasswordException
	 *             If authentication failed because of an expired password.
	 * @throws ObjectNotFoundException
	 *             If an object that matches the given identity could not be
	 *             found.
	 * @throws UnsupportedOperationException
	 *             If the AUTHENTICATE feature is not supported.
	 */

	public Map<String, Object> authenticate(String identity, String password)
			throws ConnectorException, ObjectNotFoundException,
			AuthenticationFailedException, ExpiredPasswordException {

		Map<String, Object> obj = read(identity);
		if (null == obj) {
			throw new ObjectNotFoundException(identity);
		}

		// If there was a problem we would have thrown already. Return the //
		// matched object.
		return obj;
	}

	/**
	 * Using the id passed into this method to check the request to see if has
	 * completed.
	 * 
	 * This method is used to poll the system for status when things in any of
	 * the results are marked Queued and have a requestToken.
	 *
	 * The new status, returned result's errors, and warnings will be merged
	 * back into the main project.
	 * 
	 * Returning a null result will have no change on the request and it will
	 * remained queued.
	 * 
	 * @param id
	 * @return Result
	 */
	public Result checkStatus(String id) throws ConnectorException,
			ObjectNotFoundException, UnsupportedOperationException {

		Result result = new Result();
		return result;
	}

	/**
	 * Close the underlying resources that are being held by this connector.
	 * Clients MUST call this when they are done with a connector or else there
	 * may be resource leaks.
	 */
	public void close() {
		// add code to free resources like file or database connection
	}

	public void init() throws Exception {

		if (!m_bIsDriverLoaded) {

			log.debug("INIT");

			urlServerDLL = config.getString("urlServerDLL");
			log.debug(urlServerDLL);
			userServerDLL = config.getString("userServerDLL");
			log.debug(userServerDLL);
			senhaServerDLL = config.getString("senhaServerDLL");
//			log.debug(senhaServerDLL);
			nameDLL = config.getString("nameDLL");
			log.debug(nameDLL);
			dominioServerDLL = config.getString("dominioServerDLL");
			log.debug(dominioServerDLL);

			host = config.getString("host");
			log.debug(host);
			nodName = config.getString("nodName");
			log.debug(nodName);
			ip = config.getString("ip");
			log.debug(ip);
			porta = config.getString("porta");
			log.debug(porta);
			usuarioStaffaware = config.getString("usuarioStaffaware");
			log.debug(usuarioStaffaware);
			senhaStaffaware = config.getString("senhaStaffaware");
//			log.debug(senhaStaffaware);

			m_bIsDriverLoaded = true;
		}
	}

	public Iterator<Map<String, Object>> getAccounts(String filter)
			throws Exception {

		account = null;
		resultObjectList = null;
		resultObjectList = new ArrayList<Map<String, Object>>();
		
		init();

		log.debug("GetAccount ");

		log.debug("GetAccounts - FILTER " + filter);

		if (comLocator == null) {
			connectServerDLL();
		}

		String retornoUsuario = "";

		if (filter.equalsIgnoreCase("*")) {

			retornoUsuario = getTodosUsuarios();

			log.debug("query " + retornoUsuario);

		} else {

			// retornoUsuario = consultaGrupoUsuario(filter);

		}

		try {

			log.debug("retornoUsuario " + retornoUsuario);

			for (String usuario : retornoUsuario.split(";")) {

				log.debug("FOR ");
				log.debug("usuario ");

				account = new HashMap<String, Object>();
				String userid = usuario;
				log.debug(userid);

				if(userid.equals("Não foi possível estabelacer conexão com o Staffware."))
				{
					log.debug("Entrei no useri equals");
					throw new ConnectorException("Não foi possível estabelacer conexão com o Staffware.");
				}
				
				account.put(CO_USUARIO, userid);

				if (userid == "" || userid == null) {
					log.debug("Usuario vazio ou Nulo");
				} else {

					String grupo = consultaGrupoUsuario(userid);
					
					if(grupo != null && grupo != "")
					{
						log.debug("userid " + userid);
						log.debug("grupo " + grupo);

						List listaGrupo = new ArrayList();

						String[] sp = grupo.split("\\|");

						for (String gp : sp[2].split(";")) {

							listaGrupo.add(gp);

						}

						account.put(CO_GRUPO, listaGrupo);
						log.debug("listaGrupo " + listaGrupo);

						log.debug(grupo);

						resultObjectList.add(account);
					} else {
						
						resultObjectList.add(account);
					}
				}

			}

		} catch (Exception e) {
			if (log.isDebugEnabled())
				log.debug("Exception occured ", e);
			throw new ConnectorException(e);
		}

		return resultObjectList.iterator();
	}

	public Iterator<Map<String, Object>> getGroups(String filter)
			throws Exception {

		groups = null;
		resultObjectList = new ArrayList<Map<String, Object>>();

		log.debug("GetAccounts - FILTER " + filter);

		init();

		if (comLocator == null) {
			connectServerDLL();
		}

		String retornoGrupo = "";

		log.debug("getGroups - FILTER " + filter);

		if (filter.equalsIgnoreCase("*")) {
			retornoGrupo = getTodosGrupo();

			log.debug("retornoGrupo" + retornoGrupo);
		}

		try {

			for (String grupo : retornoGrupo.split(";")) {
				groups = new HashMap<String, Object>();

				groups.put(CO_GRUPO, grupo);
				log.debug("CO_GRUPO" + grupo);

				resultObjectList.add(groups);

			}

		} catch (Exception e) {

			if (log.isDebugEnabled())
				log.debug("Exception occured ", e);
			throw new ConnectorException(e);
		}

		return resultObjectList.iterator();
	}

	public static void connectServerDLL() throws JIException,
			SecurityException, IOException {

//		JISystem.setInBuiltLogHandler(false);

		session = JISession.createSession(dominioServerDLL, userServerDLL,
				senhaServerDLL);

		try {

			comServer = new JIComServer(JIProgId.valueOf(nameDLL),
					urlServerDLL, session);
		} catch (JIException e) {

			comServer = new JIComServer(JIProgId.valueOf(nameDLL),
					urlServerDLL, session);
		}

		comLocator = (IJIDispatch) JIObjectFactory.narrowObject(comServer
				.createInstance().queryInterface(IID));

	}

	public String getTodosUsuarios() throws Exception {

		init();

		log.debug("Get Todos Usuarios ");

		if (comLocator == null) {
			connectServerDLL();
		}

		String retornoUsuarios = "";

		JISystem.setAutoRegisteration(true);

		Object[] paramsEncripto = new Object[] { new JIString(host),
				new JIString(nodName), new JIString(ip), new JIString(porta),
				new JIString(usuarioStaffaware), new JIString(senhaStaffaware) };

		JIVariant[] resultsCript;
		try {
			resultsCript = comLocator.callMethodA("ListaUsuariosStaffware",
					paramsEncripto);

			retornoUsuarios = resultsCript[0].getObjectAsString2().toString();

			log.debug("retornoUsuario" + retornoUsuarios);

		} catch (JIException e) {
			e.printStackTrace();
		}

		return retornoUsuarios;
	}

	public String consultaGrupoUsuario(String codUsuario) throws Exception {

		init();

		log.debug("ConsultaGrupoUSAURIO");

		if (comLocator == null) {
			connectServerDLL();
		}

		String grupoUsuario = "";

		JISystem.setAutoRegisteration(true);

		Object[] paramsEncripto = new Object[] { new JIString(host),
				new JIString(nodName), new JIString(ip), new JIString(porta),
				new JIString(usuarioStaffaware), new JIString(senhaStaffaware),
				new JIString("C"), new JIString(codUsuario), new JIString("") };

		JIVariant[] resultsCript;
		try {
			resultsCript = comLocator.callMethodA("GerenciaUsuarioStaffware",
					paramsEncripto);

			grupoUsuario = resultsCript[0].getObjectAsString2().toString();

			log.debug("grupoUsuario" + grupoUsuario);

		} catch (JIException e) {
			System.out.print("Usuario: "+codUsuario+" não tem grupo");
		}

		return grupoUsuario;
	}

	public String getTodosGrupo() throws Exception {

		init();

		log.debug("getTodosGrupo");

		if (comLocator == null) {
			connectServerDLL();
		}

		String coGrupo = "";

		JISystem.setAutoRegisteration(true);

		Object[] paramsEncripto = new Object[] { new JIString(host),
				new JIString(nodName), new JIString(ip), new JIString(porta),
				new JIString(usuarioStaffaware), new JIString(senhaStaffaware) };

		JIVariant[] resultsCript;
		try {
			resultsCript = comLocator.callMethodA("ListaGruposStaffware",
					paramsEncripto);

			coGrupo = resultsCript[0].getObjectAsString2().toString();

			log.debug("coGrupo" + coGrupo);

		} catch (JIException e) {
			e.printStackTrace();
		}

		return coGrupo;
	}
	
	public void criaUsuario(String codUsuario, String noUsuario) throws Exception
	{
		init();

		log.debug("CriaUsuario codUsuario<"+codUsuario+"> noUsuario<"+noUsuario+">");

		if (comLocator == null) {
			connectServerDLL();
		}

		String result = "";

		JISystem.setAutoRegisteration(true);

		Object[] paramsEncripto = new Object[] { new JIString(host),
				new JIString(nodName), new JIString(ip), new JIString(porta),
				new JIString(usuarioStaffaware), new JIString(senhaStaffaware),
				new JIString("I"), new JIString(codUsuario), new JIString(noUsuario) };

		JIVariant[] resultsCript;
		try {
			resultsCript = comLocator.callMethodA("GerenciaUsuarioStaffware",
					paramsEncripto);

			result = resultsCript[0].getObjectAsString2().toString();

			log.debug("result" + result);

		} catch (JIException e) {
			e.printStackTrace();
		}
	}
	
	public void vinculaUsuarioGrupo(String codUsuario,String coGrupo) throws Exception
	{
		init();

		log.debug("Vincular Usuario Grupo codUsuario<"+codUsuario+"> coGrupo<"+coGrupo+">");

		if (comLocator == null) {
			connectServerDLL();
		}

		String result = "";

		JISystem.setAutoRegisteration(true);

		Object[] paramsEncripto = new Object[] { new JIString(host),
				new JIString(nodName), new JIString(ip), new JIString(porta),
				new JIString(usuarioStaffaware), new JIString(senhaStaffaware),
				new JIString("I"), new JIString(codUsuario), new JIString(coGrupo) };

		JIVariant[] resultsCript;
		try {
			resultsCript = comLocator.callMethodA("GerenciaUsuarioGrupoStaffware",
					paramsEncripto);

			result = resultsCript[0].getObjectAsString2().toString();

			log.debug("result" + result);

		} catch (JIException e) {
			e.printStackTrace();
		}
	}
	
	
	public void desvinculaUsuarioGrupo(String codUsuario,String coGrupo) throws Exception
	{
		init();

		log.debug("ConsultaGrupoUSAURIO");

		if (comLocator == null) {
			connectServerDLL();
		}

		String result = "";

		JISystem.setAutoRegisteration(true);

		Object[] paramsEncripto = new Object[] { new JIString(host),
				new JIString(nodName), new JIString(ip), new JIString(porta),
				new JIString(usuarioStaffaware), new JIString(senhaStaffaware),
				new JIString("E"), new JIString(codUsuario), new JIString(coGrupo) };

		JIVariant[] resultsCript;
		try {
			resultsCript = comLocator.callMethodA("GerenciaUsuarioGrupoStaffware",
					paramsEncripto);

			result = resultsCript[0].getObjectAsString2().toString();

			log.debug("result" + result);

		} catch (JIException e) {
			e.printStackTrace();
		}
	}
	
	
	public void deleteUsuario(String codUsuario) throws Exception
	{
		init();

		log.debug("ConsultaGrupoUSAURIO");

		if (comLocator == null) {
			connectServerDLL();
		}

		String result = "";

		JISystem.setAutoRegisteration(true);

		Object[] paramsEncripto = new Object[] { new JIString(host),
				new JIString(nodName), new JIString(ip), new JIString(porta),
				new JIString(usuarioStaffaware), new JIString(senhaStaffaware),
				new JIString("E"), new JIString(codUsuario), new JIString("") };

		JIVariant[] resultsCript;
		try {
			resultsCript = comLocator.callMethodA("GerenciaUsuarioStaffware",
					paramsEncripto);

			result = resultsCript[0].getObjectAsString2().toString();

			log.debug("result" + result);

		} catch (JIException e) {
			e.printStackTrace();
		}
	}
	

}
