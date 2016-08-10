import java.io.InputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;

/*

 This file  provides a skeletal structure of the openconnector interfaces.
 It is the responsiblity of the connector developer to implement the methods.

 */
public class SISFIN extends AbstractConnector {

	// ////////////////////////////////////////////
	// ////////////////////////////
	//
	// INNER CLASS
	//
	// //////////////////////////////////////////////////////////////////////////

	private boolean m_bIsDriverLoaded;
	private String m_sUser;
	private String m_sPasswd;
	private String m_sUrl;
	private String m_time_provision;
	private String m_time_aggregation;
	private String m_retry;

	private Iterator<Map<String, Object>> it;
	private Map<String, Object> account;
	private List<Map<String, Object>> resultObjectList;

	private Map<String, Object> groups;

	protected static String SUCESSO = "00";
	protected static String FALHA = "01";

	public static String ADD_ACOUNT = "0";
	public static String ADD_CONNECTION = "1";
	public static String UPDATE_PASSWORD = "2";
	public static String UPDATE_ACCOUNT = "3";
	public static String DELETE_ACCOUNT = "4";
	public static String LOCK_ACCOUNT = "5";
	public static String RESTORE_ACCOUNT = "6";
	public static String DELETE_CONNECTION = "7";
	public static String GET_ACCOUNT = "8";
	public static String GET_ACCOUNT_BY_GROUP = "9";
	public static String GET_GROUP = "10";

	public String MATRICULA = "MATRICULA";
	public String NOME = "NOME";
	public String UNIDADE = "UNIDADE";
	public String UNIDADE_ADM = "UNIDADE_ADM";
	public String FUNC_EFET = "FUNC_EFET";
	public String FUNC_EVENT = "FUNC_EVENT";
	public String FUNC_EXERC = "FUNC_EXERC";
	public String SISTEMA = "SISTEMA";
	public String PERFIL = "PERFIL";
	public String SIS_PERFIL = "SIS_PERFIL";
	public String SIS_PERFIL_GROUP = "SIS_PERFIL";
	private boolean isDebug;
	private long time;
	private int erroConnection = 0;
	private int erroFinal = 0;

	int retry;
	int quantidade = 0;
	int valor = 0;
	int tentativa = 0;

	TelnetCommandRunner client = null;

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
	public SISFIN() {
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
	public SISFIN(ConnectorConfig config, openconnector.Log log)
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
//			init();
//
//			client.disconnect();

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

		Map<String, Object> readMap = new HashMap<String, Object>();

		if (isDebug == true) {
			log.debug("==============READ==================");
		}

		account = null;
		// groups = null;
		it = null;

		try {

			if (isDebug == true) {
				log.debug("READ CLIENTE " + client);
			}

			if (client == null) {
				init();
			}

		} catch (Exception e1) {
			throw new ConnectorException(
					"ERRO COM CONEXÃO NO SISFIN QUANTIDADE DE TENTATIVA "
							+ tentativa);
		}

		if (OBJECT_TYPE_ACCOUNT.equals(this.objectType)) {
			try {

				if (isDebug == true) {
					log.debug("READ ACCOUNT <FILTER" + nativeIdentifier);
				}

				it = getAccounts(nativeIdentifier);

			} catch (Exception e) {

				throw new ConnectorException(
						"ERRO COM CONEXÃO NO SISFIN QUANTIDADE DE TENTATIVA "
								+ tentativa);
			}

			if (isDebug == true) {
				log.debug("READ ACCOUNT <IT" + it.toString());
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

		erroConnection = 0;

		client.disconnect();

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

	public Iterator<Map<String, Object>> iterate(Filter filterAgs)
			throws ConnectorException, UnsupportedOperationException {

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

				throw new ConnectorException(
						"ERRO COM CONEXÃO NO SISFIN QUANTIDADE DE TENTATIVA "
								+ tentativa);
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
	public Result create(String nativeIdentifier, List<Item> items)
			throws ConnectorException, ObjectAlreadyExistsException {

		Result result = new Result(Result.Status.Committed);

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

//		Map<String, Object> existing = read(nativeIdentifier);
//		if (null == existing) {
//			throw new ObjectNotFoundException(nativeIdentifier);
//		}

		if (items != null) {
			for (Item item : items) {
				String name = item.getName();
				Object value = item.getValue();
				Item.Operation op = item.getOperation();

				switch (op) {
				case Add: {
					// adding entitlement
				}
					break;
				case Remove: {
					// remove entitlement
				}
					break;
				case Set: {
//					existing.put(name, value);
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

		Result result = new Result(Result.Status.Committed);
//		Map<String, Object> obj = read(nativeIdentifier);
//		if (null == obj) {
//			throw new ObjectNotFoundException(nativeIdentifier);
//		}

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

//		Map<String, Object> obj = read(nativeIdentifier);
//		if (null == obj) {
//			throw new ObjectNotFoundException(nativeIdentifier);
//		}

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

//		Map<String, Object> obj = read(nativeIdentifier);
//		if (null == obj) {
//			throw new ObjectNotFoundException(nativeIdentifier);
//		}

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

//		Map<String, Object> obj = read(nativeIdentifier);
//		if (null == obj) {
//			throw new ObjectNotFoundException(nativeIdentifier);
//		}

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

		debub();

		if (isDebug == true) {
			log.debug("=================SET PASSWORD==============");
		}
		
	

		String retorno = null;

		Result result = new Result();

		if (isDebug == true) {
			log.debug("Password nativeIdentifier " + nativeIdentifier);
		}

		String tipoUsuario = nativeIdentifier.substring(0, 1);
		String nuMatricula = nativeIdentifier.substring(1);

		String criptografa = new Base64()
				.encodeToString(newPassword.getBytes());
		
		if (isDebug == true) {
			log.debug("Senha Criptograda " + criptografa);
		}

		try {

			init();

			time = Integer.parseInt(m_time_provision);

			if (isDebug == true) {
				log.debug("Account " + "<time " + time + ">");
			}

			String command = UPDATE_PASSWORD + ";" + tipoUsuario + ";"
					+ nuMatricula + ";" + criptografa;

			retorno = client.sendCommand(command);

			retorno = retorno.trim();

			String resultado = "00;OK";

			if (retorno.equals(resultado)) {
				result = new Result(Result.Status.Committed);
				if (isDebug == true) {
					log.debug("Retorno Sucesso Password" + retorno);
				}

				erroConnection = 0;

			} else {

				result = new Result(Result.Status.Failed);
				result.add("SISFIN PASSWORD - NÃO FOI POSSÍVEL TROCAR A SENHA RETORNO VAZIO");
				if (isDebug == true) {
					log.debug("Retorno Falha Password" + retorno);
				}

				client.disconnect();
			}

		} catch (Exception e) {

			if (isDebug == true) {
				log.debug("PASS CATCH Falha");
			}

			if (client != null) {
				client.disconnect();
			}

			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			result.add("SISFIN PASSWORD - NÃO FOI POSSÍVEL TROCAR A SENHA");

			throw new ConnectorException(
					"Erro de Conexão com SISFIN");
		}

		// Map<String, Object> obj = read(nativeIdentifier);
		// if (null == obj) {
		// throw new ObjectNotFoundException(nativeIdentifier);
		// }

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
//		Map<String, Object> obj = new HashMap<String, Object>();
//		if (null == obj) {
//			throw new ObjectNotFoundException(identity);
//		}

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
	// public void close() {
	//
	// if (client != null) {
	// client.disconnect();
	// }
	//
	// client = null;
	//
	// }

	public class TelnetCommandRunner {
		private TelnetClient tc = new TelnetClient();
		private InputStream in;
		private PrintStream out;
		private String prompt = "==>";
		private String timeoutmsg = "Timeout period expired";
		private String logedoutmsg = "logged out";
		private String unauthorizedmsg = "User authorization failure";
		private long TIMEOUT = 1000 * 60 * time;
		Result result;

		public TelnetCommandRunner(String server, String user, String password)
				throws Exception {
			try {

				//fiz essa alteracao
				
				tc.setDefaultTimeout(60000);
				
				tc.connect(server, 23);
				TerminalTypeOptionHandler ttopt = new TerminalTypeOptionHandler(
						"VT100", false, false, true, false);
				EchoOptionHandler echoopt = new EchoOptionHandler(true, false,
						true, false);
				SuppressGAOptionHandler gaopt = new SuppressGAOptionHandler(
						true, true, true, true);

				tc.addOptionHandler(ttopt);
				tc.addOptionHandler(echoopt);
				tc.addOptionHandler(gaopt);

				
			
				in = tc.getInputStream();
				out = new PrintStream(tc.getOutputStream());

				readUntil("Username:");
				write(user);

				readUntil("Password:");
				write(password);

				readUntil(prompt);
			} catch (Exception e) {

				disconnect();

				erroConnection = 1;

				if (isDebug == true) {
					log.debug("Erro Telnet SISFIN");
				}

				throw new ConnectorException("Erro Conexão SISFIN");
			}
		}

		public String readUntil(String pattern) throws Exception {

			try {

				Result result;

				erroConnection = 0;

				char lastChar = pattern.charAt(pattern.length() - 1);
				StringBuilder sb = new StringBuilder(10);
				byte buffer[] = new byte[500];
				int totalBytesLidos = in.read(buffer);

				long startTime = System.currentTimeMillis();
				long endTime = startTime + TIMEOUT;

				System.out.println("Start at: "
						+ new Time(System.currentTimeMillis()));
				while (totalBytesLidos > 0) {
					String linha = new String(buffer, 0, (totalBytesLidos));
					sb.append(linha);

					if (System.currentTimeMillis() > endTime) {

						if (isDebug == true) {

							log.debug("Controle de Tempo SISFIN");

						}

						erroConnection = 1;
						
//						log.debug("O tempo de conexão com o servidor do SISFIN expirou");
						
						throw new Exception("O tempo de conexão com o servidor do SISFIN expirou");

					}

					if (linha.contains(pattern)) {
						return sb.toString();
					} else if (sb.toString().endsWith(timeoutmsg)) {
						throw new Exception(sb.toString());
					} else if (sb.toString().endsWith(logedoutmsg)) {
						throw new Exception(sb.toString());
					} else if (sb.toString().endsWith(unauthorizedmsg)) {
						throw new Exception(sb.toString());
					}
					totalBytesLidos = in.read(buffer);
				}
				return sb.toString();

			} catch (Exception e) {

				erroConnection = 1;

				disconnect();

				throw new ConnectorException(
						"Erro de Conexão com SISFIN mensagem telnet");
			}

		}

		public void write(String value) {
			out.println(value);
			out.flush();
		}

		public String sendCommand(String command) throws Exception {

			try {

				System.out.println(command);

				write(command);
				String result = readUntil(prompt);
				if (result.endsWith(prompt)) {
					result = result.replaceAll(prompt, "");
				}
				return result;

			} catch (Exception e) {

				erroConnection = 1;

				throw new ConnectorException(
						"Erro de Conexão com SISFIN mensagem telnet");
			}
		}

		public void disconnect() {
			try {
				tc.disconnect();

				if (isDebug == true) {
					log.debug("CLOSE CONNECTION");

				}

			} catch (Exception e) {

				erroConnection = 1;

				throw new ConnectorException(
						"Erro de Conexão com SISFIN mensagem telnet");
			}

		}
	}

	public void init() throws Exception {

		valor = 0;

		m_retry = config.getString("retryAggregation");
		retry = Integer.parseInt(m_retry);

		if (isDebug == true) {

			log.debug("=============INIT============");

		}

		for (int i2 = 0; i2 < retry; i2++) {

			try {

				m_sUser = config.getString("user");
				m_sPasswd = config.getString("password");
				m_sUrl = config.getString("url");
				m_time_provision = config.getString("timeProvision");
				// m_time_provision = "5";
				m_time_aggregation = config.getString("timeAggregation");
				// m_time_aggregation = "5";

				debub();

				if (isDebug == true) {

					log.debug("=============INIT============ tentativa "
							+ tentativa);

				}

				valor = i2;

				time = time + 1;

				if (isDebug == true) {
					log.debug("INIT " + "<user " + m_sUser + "> <m_sUrl "
							+ m_sUrl + ">");
				}

				client = new TelnetCommandRunner(m_sUrl, m_sUser, m_sPasswd);

				if (isDebug == true) {
					log.debug("valor connection " + erroConnection);
					log.debug("Valor " + i2);

				}

				if (erroConnection == 0) {

					if (isDebug == true) {
						log.debug("erroconnecrtio " + erroConnection);

						log.debug("Valor " + i2);

					}

					break;
				}

			} catch (Exception e) {

				if (erroFinal == 1) {
					valor = 0;

					if (client != null) {

						client.disconnect();
					}

					throw new ConnectorException(
							"Erro de Conexão com SISFIN INIT QUANTIDADE DE TENTATIVA "
									+ tentativa);
				}
			}

		}

	}

	public void close() {
		try {

			client.disconnect();

			if (isDebug == true) {
				log.debug("CLOSE CONNECTION");

			}

		} catch (Exception e) {
			log.debug("ERRO Connection Close");

			if (client != null) {

				try {
					client.disconnect();

				} catch (Exception e1) {
//					throw new ConnectorException(
//							"LISTA GET SISFIN VAZIA QUANTIDADE DE TENTATIVA "
//									+ tentativa);

				}
			}
		}

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

		debub();
		if (isDebug == true) {
			log.debug("==========GetAccounts===============");

		}

		account = null;
		resultObjectList = null;
		String retorno = null;
		resultObjectList = new ArrayList<Map<String, Object>>();

		if (client == null) {
			init();
		}

		for (int i = 0; i < retry; i++) {

			if (isDebug == true) {
				log.debug("retry " + retry);
				log.debug("quantidade " + quantidade);
			}

			try {

				time = Integer.parseInt(m_time_aggregation);

				if (isDebug == true) {
					log.debug("Account " + "<filter " + filter + ">");
				}

				if (filter.equalsIgnoreCase("*")) {

					String command = GET_ACCOUNT_BY_GROUP + ";*;*";

					if (isDebug == true) {
						log.debug("command " + "<command " + command + ">");
					}

					retorno = client.sendCommand(command);

				} else {

					if (isDebug == true) {
						log.debug("Account Filter TRUE" + "<filter " + filter
								+ ">");
					}

					String classe = filter.substring(0, 1);
					String matricula = filter.substring(1);

					if (isDebug == true) {
						log.debug("Account Valor" + "<classe " + classe
								+ "> <matricula " + matricula + ">");
					}

					String commandFilter = "8" + ";" + classe + ";" + matricula;

					if (isDebug == true) {
						log.debug("<command " + classe + "> <matricula "
								+ matricula + ">");
					}
					if (client == null) {
						init();
					}

					if (erroConnection == 1) {

						tentativa = tentativa + 1;

						throw new ConnectorException(
								"Erro de Conexão com SISFIN");
					}

					retorno = client.sendCommand(commandFilter);

					if (isDebug == true) {
						log.debug("Account " + "<retorno " + retorno + ">");
					}

				}

				if (retorno != null) {

					String[] linha = retorno.split("\r\n");

					for (String l : linha) {

						for (String linhaSeparada : l.split("\n")) {
							account = new HashMap<String, Object>();

							if (isDebug == true) {
								log.debug("Account linhas: " + linhaSeparada);
							}

							if (!linhaSeparada.isEmpty()) {

								String[] array = linhaSeparada.split(";");

								int tamanho = array.length;

								if (isDebug == true) {
									log.debug("tamanho" + tamanho);
								}

								if (tamanho > 9) {
									String classe = array[0];
									String numero = array[1];

									account.put(MATRICULA, classe + numero);
									account.put(NOME, array[2]);
									account.put(UNIDADE, array[3]);
									account.put(UNIDADE_ADM, array[4]);
									account.put(FUNC_EFET, array[5]);
									account.put(FUNC_EVENT, array[6]);
									account.put(FUNC_EXERC, array[7]);
									String sisperfil = array[8] + "/"
											+ array[9];
									account.put(SIS_PERFIL, sisperfil);

									if (isDebug == true) {
										log.debug("Account "
												+ "\n <MATRICULA: " + classe
												+ numero + ">\n <NOME: "
												+ array[2] + ">\n <UNIDADE: "
												+ array[3]
												+ ">\n <UNIDADE_ADM: "
												+ array[4] + ">\n <FUNC_EFET: "
												+ array[5]
												+ ">\n <FUNC_EVENT: "
												+ array[6]
												+ ">\n <FUNC_EXERC: "
												+ array[7] + ">\n <sisperfil: "
												+ sisperfil.trim() + ">");
									}
									resultObjectList.add(account);
								}
							}
						}

					}

				}

				if (resultObjectList.isEmpty()) {

					if (isDebug == true) {

						log.debug("Lista Vazia");
					}

					tentativa = tentativa + 1;

					client.disconnect();

//					throw new ConnectorException(
//							"LISTA GET SISFIN VAZIA QUANTIDADE DE TENTATIVA "
//									+ tentativa);
				}

				if (resultObjectList.size() > 4) {
					break;
				}

			} catch (Exception e1) {

				if (isDebug == true) {
					log.debug("Erro SISFIN");
				}

				if (erroConnection == 1) {

					tentativa = tentativa + 1;
				}

				if (i == retry) {

					client.disconnect();

					throw new ConnectorException(
							"Erro de Conexão com SISFIN QUANTIDADE DE TENTATIVA "
									+ tentativa);
				}
			}

		}

		if (isDebug == true) {
			log.debug("Tentativa " + tentativa);
		}

		if (resultObjectList.size() < 10) {
			client.disconnect();

			throw new ConnectorException(
					"Erro de Conexão com SISFIN QUANTIDADE DE TENTATIVA "
							+ tentativa);
		}

		client.disconnect();

		return resultObjectList.iterator();

	}

	public Iterator<Map<String, Object>> getGroups(String filter)
			throws Exception {

		debub();

		groups = null;
		resultObjectList = new ArrayList<Map<String, Object>>();
		String retorno = null;

		init();

		if (filter.equalsIgnoreCase("*")) {

			String command = GET_GROUP + ";*;*";

			retorno = client.sendCommand(command);

			client.disconnect();

		} else {

			String[] array = filter.split("/");

			String sistema = array[0];
			String perfil = array[1];

			String command = GET_GROUP + ";" + sistema + ";" + perfil;

			retorno = client.sendCommand(command);

			client.disconnect();
		}

		try {

			if (isDebug == true) {
				log.debug("RETORNO Group" + retorno);
			}

			for (String linhas : retorno.split("\r\n")) {
				groups = new HashMap<String, Object>();

				if (isDebug == true) {
					log.debug("LINHA " + linhas);
				}

				if (!linhas.isEmpty()) {
					String[] array = linhas.split(";");

					String zero = "00";

					if (isDebug == true) {
						log.debug("ARRAY Group " + array[0]);
					}

					String sisPerfil = array[0] + "/" + array[1];

					if (!sisPerfil.equals("00")) {

						groups.put(SIS_PERFIL_GROUP, sisPerfil);
						groups.put(SISTEMA, array[0]);
						groups.put(PERFIL, array[1]);

						resultObjectList.add(groups);
					}
					continue;
				}
			}

		} catch (Exception e) {

			if (log.isDebugEnabled())
				log.debug("Exception occured ", e);
			throw new ConnectorException("SISFIN GET GROUP " + e);
		}

		return resultObjectList.iterator();
	}

}
