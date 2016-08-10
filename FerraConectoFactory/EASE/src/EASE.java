import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import openconnector.AbstractConnector;
import openconnector.AuthenticationFailedException;
import openconnector.ConnectorConfig;
import openconnector.ConnectorException;
import openconnector.ExpiredPasswordException;
import openconnector.Filter;
import openconnector.Item;
import openconnector.ObjectAlreadyExistsException;
import openconnector.ObjectNotFoundException;
import openconnector.Result;
import to.AccountTO;
import to.GroupTO;
import util.UtilDate;
import br.com.pbti.ease.EaseWSClientFacade;
import br.com.pbti.ease.xml.EaseWSRetorno;

/*

 This file  provides a skeletal structure of the openconnector interfaces.
 It is the responsiblity of the connector developer to implement the methods.

 */
public class EASE extends AbstractConnector {

	private boolean m_bIsDriverLoaded;
	private String m_sUser;
	private String m_sPasswd;
	private String m_sUrl;

	private Map<String, Object> account;
	private Map<String, Object> groups;
	private List<Map<String, Object>> resultObjectList;
	private Iterator<Map<String, Object>> it;

	private String coUsuario = "CO_USUARIO";
	private String noUsuario = "NO_USUARIO";
	private String password = "PASSWORD";
	private String coPerfil = "CO_PERFIL";
	private String noPerfil = "NO_PERFIL";
	private String desLotacao = "NO_LOTACAO";

	// Groups
	private String nuPerfil = "CO_PERFIL";
	private String desPerfil = "NO_PERFIL";

	private boolean isDebug;

	// ////////////////////////////////////////////
	// ////////////////////////////
	//
	// INNER CLASS
	//
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * An iterator that handle the paging logic to send data in chunks.
	 */
	@SuppressWarnings("unused")
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
	public EASE() {
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
	public EASE(ConnectorConfig config, openconnector.Log log) throws Exception {
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
			if (isDebug == true) {
				log.error("Error for TestConnection ", e);
			}
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

		Map<String, Object> readMap = new HashMap<String, Object>();

		account = null;
		// groups = null;
		it = null;

		try {
			init();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (OBJECT_TYPE_ACCOUNT.equals(this.objectType)) {
			try {

				it = getAccounts(nativeIdentifier);

			} catch (Exception e) {
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
				e.printStackTrace();
			}

			if (it.hasNext()) {
				groups = it.next();
				return groups;
			}
		}

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

		try {
			init();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		String filter = "*";

		it = null;

		if (OBJECT_TYPE_ACCOUNT.equals(this.objectType)) {
			try {
				it = getAccounts(filter);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (OBJECT_TYPE_GROUP.equals(this.objectType)) {
			try {
				it = getGroups(filter);
			} catch (Exception e) {
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
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	public Result create(String nativeIdentifier, List<Item> items)
			throws ConnectorException, ObjectAlreadyExistsException {

		Result result = new Result();

		EaseWSRetorno wsRetorno;

		String loginDay = UtilDate.formatDate(Calendar.getInstance().getTime(),
				"S");

		String codUsuario = "", nomUsuario = "", lotacao = "", codSenha = "", numPerfil = "";
		// ArrayList<String> arrayPerfilSistema = new ArrayList<String>();

		codUsuario = nativeIdentifier;

		try {

			init();

			EaseWSClientFacade easeWSDL = EaseWSClientFacade.getInstance();
			AccountTO usuarioInclusao = new AccountTO();

			usuarioInclusao.setUsuario(codUsuario);

			for (openconnector.Item item : items) {

				String name = item.getName();
				String value = item.getValue().toString();

				if (name.equalsIgnoreCase(noUsuario)) {

					nomUsuario = value;
					usuarioInclusao.setNombreusu(nomUsuario);
				}

				if (name.equalsIgnoreCase(password)) {

					codSenha = value;
					usuarioInclusao.setPassword(codSenha);
				}

				if (name.equalsIgnoreCase(nuPerfil)) {

					ArrayList valueList = new ArrayList();

					if (item.getValue() instanceof String) {
						valueList.add(item.getValue());

					} else {
						valueList = (ArrayList) item.getValue();

					}

					if (valueList.size() > 1) {
						result = new Result(Result.Status.Failed);
						result.add("TIPO APP EASE "
								+ "O sistema não permite adicionar mais de um perfil.");

						return result;
					}

					numPerfil = valueList.get(0).toString();

					usuarioInclusao.setCodperfil(numPerfil);
				}

				if (name.equalsIgnoreCase(desLotacao)) {

					lotacao = value;
					usuarioInclusao.setOficina(lotacao);
				}
			}

			usuarioInclusao.setNivsegusu("0");
			usuarioInclusao.setCodidioma("PO");
			usuarioInclusao.setFecactiva(Calendar.getInstance().getTime());
			usuarioInclusao.setFecdesact(UtilDate.getDateInc(1000));
			usuarioInclusao.setCenttra("");

			usuarioInclusao.setMaxSenhasIncorretas("5");

			wsRetorno = easeWSDL.addUsuario(usuarioInclusao);

			
			result = new Result(Result.Status.Committed);

		} catch (Exception e) {
			result = new Result(Result.Status.Failed);

			if (isDebug == true) {
				log.debug("Exception occured ", e);
			}
			String message = "Exception occured while creating the entity";
			result.add("TIPO APP EASE " + message);
		}

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

	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	public Result update(String nativeIdentifier, List<Item> items)
			throws ConnectorException, ObjectNotFoundException {

		Result result = new Result();

		EaseWSRetorno wsRetorno = null;

		List<AccountTO> listAccounts = null;

		// Map<String, Object> existing = read(nativeIdentifier);
		// if (null == existing) {
		// throw new ObjectNotFoundException(nativeIdentifier);
		// }

		String codUsuario = "", numPerfil = "";
		ArrayList<String> arrayPerfilSistema = new ArrayList<String>();

		codUsuario = nativeIdentifier;

		try {
			init();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (items != null) {
			for (Item item : items) {
				String name = item.getName();
				ArrayList valueList = new ArrayList();

				if (item.getValue() instanceof String) {
					valueList.add(item.getValue());

				} else {
					valueList = (ArrayList) item.getValue();

				}

				if (valueList.size() > 1) {
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP EASE "
							+ "O sistema não permite adicionar mais de um perfil.");

					return result;
				}

				// String value = item.getValue().toString();
				Item.Operation op = item.getOperation();

				if (isDebug == true) {
					log.debug("Item name " + name);
					log.debug("Item value " + valueList.get(0).toString());
					log.debug("Item operation " + op);
				}

				switch (op) {
				case Add: {
					if (name.equalsIgnoreCase(coPerfil)) {

						numPerfil = valueList.get(0).toString();
					}

					if (isDebug == true) {
						log.debug("ADD - CodUsuario: " + codUsuario);
						log.debug("ADD - CodPerfil: " + numPerfil);
					}

					try {

						boolean returnUsuario = false;

						wsRetorno = EaseWSClientFacade.getInstance()
								.getUsuario(codUsuario);

						listAccounts = EaseWSClientFacade.getInstance()
								.populateAccounts(wsRetorno.getXml());

						for (int i = 0; i < listAccounts.size(); i++) {
							AccountTO conta = listAccounts.get(i);
							String codPerfil = conta.getCodperfil().toString();

							if (!codPerfil.equals("")) {
								returnUsuario = false;
							} else {

								returnUsuario = true;
							}
						}

						if (returnUsuario == true) {
							wsRetorno = EaseWSClientFacade
									.getInstance()
									.vinculaUsuarioPerfil(codUsuario, numPerfil);

							
							result = new Result(Result.Status.Committed);
							
						} else {
							result = new Result(Result.Status.Failed);
							result.add("TIPO APP EASE "
									+ "O sistema não permite adicionar mais de um perfil.");
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
					break;
				case Remove: {
					if (isDebug == true) {
						log.debug("Remove - CodUsuario: " + codUsuario);
					}
					try {
						wsRetorno = EaseWSClientFacade.getInstance()
								.desvinculaUsuarioPerfil(codUsuario);

						result = new Result(Result.Status.Committed);
						
					} catch (Exception e) {
						if (isDebug == true) {
							log.debug("Remove - Cath: ");
						}
						e.printStackTrace();
						result = new Result(Result.Status.Failed);
						result.add("TIPO APP EASE " + e.getMessage());
					}
				}
					break;
				case Set: {
					// existing.put(name, value);
				}
					break;

				default:
					throw new IllegalArgumentException("Unknown operation: "
							+ op);
				}
			}
		}

		// result = new Result(Result.Status.Committed);

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

		Map<String, Object> obj = read(nativeIdentifier);
		if (null == obj) {
			throw new ObjectNotFoundException(nativeIdentifier);
		}

		try {
			init();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		Result result = new Result();

		String codUsuario = "";

		codUsuario = nativeIdentifier;

		if (isDebug == true) {
			log.debug("DELETE - CodUsuario: " + codUsuario);
		}

		try {
			EaseWSRetorno wsRetorno = EaseWSClientFacade.getInstance()
					.excluirUsuario(codUsuario);

			result = new Result(Result.Status.Committed);
			
		} catch (Exception e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			result.add("TIPO APP EASE " + e.getMessage());
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

		try {
			init();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		Map<String, Object> obj = read(nativeIdentifier);
		if (null == obj) {
			throw new ObjectNotFoundException(nativeIdentifier);
		}

		Result result = new Result();

		String fecdesact = UtilDate.getDataAtivacaoDefaultForEASE();
		String codUsuario = "";
		codUsuario = nativeIdentifier;

		if (isDebug == true) {
			log.debug("Enable - CodUsuario: " + codUsuario);
		}

		try {
			EaseWSRetorno wsRetorno = EaseWSClientFacade.getInstance()
					.habilitarUsuario(codUsuario, fecdesact);

			result = new Result(Result.Status.Committed);
			
		} catch (Exception e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			result.add("TIPO APP EASE " + e.getMessage());
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

		try {
			init();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		Map<String, Object> obj = read(nativeIdentifier);
		if (null == obj) {
			throw new ObjectNotFoundException(nativeIdentifier);
		}

		Result result = new Result(Result.Status.Committed);

		String codUsuario = "";
		codUsuario = nativeIdentifier;

		if (isDebug == true) {
			log.debug("Disable - CodUsuario: " + codUsuario);
		}

		try {
			EaseWSRetorno wsRetorno = EaseWSClientFacade.getInstance()
					.desabilitarUsuario(codUsuario);

			result = new Result(Result.Status.Committed);
			
		} catch (Exception e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			result.add("TIPO APP EASE " + e.getMessage());
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

		Result result = new Result();

		String password = newPassword;
		String codUsuario = nativeIdentifier;

		if (isDebug == true) {
			log.debug("SetPassword - CodUsuario: " + codUsuario);
		}

		try {
			init();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			EaseWSRetorno wsRetorno = EaseWSClientFacade.getInstance()
					.setNewPassword(codUsuario, password);

			result = new Result(Result.Status.Committed);

		} catch (Exception e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			result.add("TIPO APP EASE " + e.getMessage());
		}

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

		m_sUser = "";
		m_sPasswd = "";
		m_sUrl = "";
		
			log.debug("<USER "+m_sUser+"> <URL "+m_sUrl+"> < PASS"+m_sPasswd+">");
			
			m_sUser = config.getString("user");
			m_sPasswd = config.getString("password");
			m_sUrl = config.getString("url");
			
			log.debug("USER <"+m_sUser+"> <URL "+m_sUrl+"> < PASS "+m_sPasswd+">");

			EaseWSClientFacade _instance = null;
			
			try {
				EaseWSClientFacade.init(m_sUser, m_sPasswd, m_sUrl);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			debub();

			if (isDebug == true) {
				log.debug("driver loaded ");
			}
			// m_dbConn = DriverManager.getConnection(m_sUrl, m_sUser,
			// m_sPasswd);

			m_bIsDriverLoaded = true;
	}

	public void debub() {
		boolean debug = config.getBoolean("debug");

		if (debug == true) {

			isDebug = true;
		} else {

			isDebug = false;
		}
	}

	public Iterator<Map<String, Object>> getAccounts(String filter)
			throws Exception {

		account = null;
		resultObjectList = null;
		resultObjectList = new ArrayList<Map<String, Object>>();

		EaseWSRetorno wsRetorno;
		List<AccountTO> listAccounts = null;

		init();

		if (filter.equalsIgnoreCase("*")) {
			wsRetorno = EaseWSClientFacade.getInstance().getUsuarios();

		} else {
			wsRetorno = EaseWSClientFacade.getInstance().getUsuario(filter);

			listAccounts = EaseWSClientFacade.getInstance().populateAccounts(
					wsRetorno.getXml());

			for (int i = 0; i < listAccounts.size(); i++) {
				AccountTO conta = listAccounts.get(i);

				String valorFacbaja = conta.getFecbajaForEASE();
				String ativo = "01-01-0001";

				if (ativo.equals(valorFacbaja)) {
					account = new HashMap<String, Object>();
					String userid = conta.getUsuario();
					account.put(coUsuario, userid);
					account.put(noUsuario, conta.getNombreusu());
					account.put(password, conta.getPassword());
					account.put(coPerfil, conta.getCodperfil());
					account.put(noPerfil, conta.getDesperfil());
					account.put(desLotacao, conta.getOficina());

					resultObjectList.add(account);

					String dataString = conta.getFecactivaForEASE();
					SimpleDateFormat formatterAtive = new SimpleDateFormat(
							"dd-MM-yyyy");
					Date dataAtiva = formatterAtive.parse(dataString);

					String dataDesativa = conta.getFecdesactForEASE();
					SimpleDateFormat formatterDesative = new SimpleDateFormat(
							"dd-MM-yyyy");
					Date dataDesat = formatterDesative.parse(dataDesativa);

					if (dataAtiva.before(dataDesat)) {

						account.put(openconnector.Connector.ATT_DISABLED,
								new Boolean(false));

					} else {
						account.put(openconnector.Connector.ATT_DISABLED,
								new Boolean(true));
					}
				}
			}
		}

		try {

			if (isDebug == true) {
				log.debug(" query Accounts");
			}

			listAccounts = EaseWSClientFacade.getInstance().populateAccounts(
					wsRetorno.getXml());

			for (int i = 0; i < listAccounts.size(); i++) {
				AccountTO conta = listAccounts.get(i);

				account = new HashMap<String, Object>();
				String userid = conta.getUsuario();
				account.put(coUsuario, userid);
				account.put(noUsuario, conta.getNombreusu());
				account.put(password, conta.getPassword());
				account.put(coPerfil, conta.getCodperfil());
				account.put(noPerfil, conta.getDesperfil());
				account.put(desLotacao, conta.getOficina());

				resultObjectList.add(account);

				String dataString = conta.getFecactivaForEASE();
				SimpleDateFormat formatterAtive = new SimpleDateFormat(
						"dd-MM-yyyy");
				Date dataAtiva = formatterAtive.parse(dataString);

				String dataDesativa = conta.getFecdesactForEASE();
				SimpleDateFormat formatterDesative = new SimpleDateFormat(
						"dd-MM-yyyy");
				Date dataDesat = formatterDesative.parse(dataDesativa);

				if (dataAtiva.before(dataDesat)) {

					account.put(openconnector.Connector.ATT_DISABLED,
							new Boolean(false));

				} else {
					account.put(openconnector.Connector.ATT_DISABLED,
							new Boolean(true));
				}
			}

		} catch (Exception e) {

			if (isDebug == true) {
				log.debug("Exception occured Account", e);
			}
			throw new ConnectorException(e);
		}

		return resultObjectList.iterator();

	}

	@SuppressWarnings("unused")
	public Iterator<Map<String, Object>> getGroups(String filter)
			throws Exception {

		if (isDebug == true) {
			log.debug("Get Groups: ");
		}

		groups = null;
		resultObjectList = null;
		resultObjectList = new ArrayList<Map<String, Object>>();

		EaseWSRetorno wsRetorno;
		List<AccountTO> listAccounts = null;

		init();

		if (filter.equalsIgnoreCase("*")) {
			wsRetorno = EaseWSClientFacade.getInstance().getPerfis();
		} else {

			wsRetorno = EaseWSClientFacade.getInstance().getPerfil(filter);
		}

		try {

			if (isDebug == true) {
				log.debug(" query Groups");
			}

			List<GroupTO> listGroup = EaseWSClientFacade.getInstance()
					.populateGroups(wsRetorno.getXml());

			if (isDebug == true) {
				log.debug("listGroup");
			}

			for (int i = 0; i < listGroup.size(); i++) {
				GroupTO group = listGroup.get(i);
				groups = new HashMap<String, Object>();
				groups.put(nuPerfil, group.getCodPerfil());
				groups.put(desPerfil, group.getDesPerfil());

				resultObjectList.add(groups);

			}

		} catch (Exception e) {

			if (isDebug == true)
			{
				log.debug("Exception occured Groups", e);
			}
			throw new ConnectorException(e);
		}

		return resultObjectList.iterator();
	}

}
