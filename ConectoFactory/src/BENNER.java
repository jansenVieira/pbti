import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

/*

This file  provides a skeletal structure of the openconnector interfaces.
It is the responsiblity of the connector developer to implement the methods.

*/
public class BENNER extends AbstractConnector {

	// Variaveis
	private boolean m_bIsDriverLoaded;
	private Iterator<Map<String, Object>> it;

	// Connection
	private String m_sUser;
	private String m_sPasswd;
	private String m_sUrl;
	private String m_sDriverName;
	private String m_sUser_benner;
	private String m_sPasswd_benner;
	private String m_sUrl_benner;
	private Connection m_dbConn;
	public Connection connection;
	public PreparedStatement ps;

	// Agreggation
	private Map<String, Object> account;
	private List<Map<String, Object>> resultObjectList;
	private Map<String, Object> groups;

	//////////////////////////////////////////////
	//////////////////////////////
	//
	// INNER CLASS
	//
	////////////////////////////////////////////////////////////////////////////

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

	////////////////////////////////////////////////////////////////////////////
	//
	// CONSTRUCTORS
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Default constructor.
	 */
	public BENNER() {
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
	public BENNER(ConnectorConfig config, openconnector.Log log) throws Exception {
		super(config, log);
		m_bIsDriverLoaded = false;
		it = null;
		init();
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// CONNECTOR CAPABILITIES
	//
	////////////////////////////////////////////////////////////////////////////

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

	////////////////////////////////////////////////////////////////////////////
	//
	// CONNECTOR DIAGNOSTICS
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Test the configured to see if a connection can be made. This should throw
	 * exceptions if the configuration is not complete, has invalid values, the
	 * resource cannot be contacted. If the connection succeeds, no exceptions
	 * are thrown.
	 */
	public void testConnection() {
		try {
			System.out.println("ENTREI NO TESTE CONECTION");

			init();

		} catch (Exception e) {
			if (log.isDebugEnabled())
				log.error("Error for TestConnection ", e);
			throw new ConnectorException("Test Connection failed with message : " + e.getMessage()
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

	////////////////////////////////////////////////////////////////////////////
	//
	// OBJECT ACCESS
	//
	////////////////////////////////////////////////////////////////////////////

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
			throws ConnectorException, IllegalArgumentException, UnsupportedOperationException {

		Map<String, Object> readMap = new HashMap<String, Object>();

		account = null;
		groups = null;
		it = null;

		try {
			init();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (OBJECT_TYPE_ACCOUNT.equals(this.objectType)) {
			try {
				init();
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

				init();
				it = getGroups(nativeIdentifier);

			} catch (Exception e) {
				e.printStackTrace();
			}

			if (it.hasNext()) {
				groups = it.next();
				return groups;
			}
		}
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

		try {
			init();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		String filter = "*";

		it = null;

		if (OBJECT_TYPE_ACCOUNT.equals(this.objectType)) {
			try {
				init();

				it = getAccounts(filter);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (OBJECT_TYPE_GROUP.equals(this.objectType)) {
			try {
				init();

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

		Result result = new Result();

		String nome = "", login = "", cpf = "", senha = "", centroCusto = "", cpfTemp = "";

		ArrayList valueList = new ArrayList();

		System.out.println("===============Entrei no Metodo CREATE===================");

		for (openconnector.Item item : items) {

			String name = item.getName();
			String value = item.getValue().toString();

			if (name.equalsIgnoreCase("nome")) {

				nome = value;
			}

			if (name.equalsIgnoreCase("login")) {

				login = value;
			}

			if (name.equalsIgnoreCase("cpf")) {

				cpf = value;
			}

			if (name.equalsIgnoreCase("senha")) {

				senha = value;
			}

			if (name.equalsIgnoreCase("centroCusto")) {

				centroCusto = value;
			}

			if (name.equalsIgnoreCase("nome_perfil")) {

				if (item.getValue() instanceof String) {
					valueList.add(item.getValue());

				} else {
					valueList = (ArrayList) item.getValue();

				}
			}
		}

		System.out.println("Centro Custo: " + centroCusto);

		System.out.println("CPF: " + cpf);

		System.out.println("Login: " + login);

		System.out.println("nome: " + nome);

		System.out.println("senha: " + senha);

		try {

			cpfTemp = recuperaDadosUsuario(login);

			if (cpfTemp.isEmpty()) {
				cadastraUsuario(config.getString("userBenner"), config.getString("passwordBenner"), centroCusto, cpf, login, nome, senha);
			} else {

				alteraUsuario(config.getString("userBenner"), config.getString("passwordBenner"), cpfTemp, centroCusto, senha, nome);
			}

		} catch (IOException e) {

			result.add("Falha ao criar usuario no Benner");
			return result = new Result(Result.Status.Failed);

		}

		if (valueList.size() > 1) {
			for (Object perfil : valueList) {

				String numPerfil = recuperaNumeroPerfil(perfil.toString());

				if (numPerfil.isEmpty()) {
					result.add("Falha ao inserir o Grupo: " + perfil + " numero perfil vazio");
					return result = new Result(Result.Status.Failed);

				} else {
					try {

						System.out.println("numPerfil: " + numPerfil);

						if (cpfTemp.isEmpty()) {
							vincularGrupo(config.getString("userBenner"), config.getString("passwordBenner"), cpf, numPerfil);
						} else {

							vincularGrupo(config.getString("userBenner"), config.getString("passwordBenner"), cpfTemp, numPerfil);
						}

					} catch (IOException e) {
						result.add("Falha ao vincular usuario Benner");
						return result = new Result(Result.Status.Failed);
					}
				}
			}
		} else {

			String numPerfil = recuperaNumeroPerfil(valueList.get(0).toString());

			if (numPerfil.isEmpty()) {
				result.add("Falha ao inserir o Grupo: " + valueList.get(0).toString() + " numero perfil vazio");
				return result = new Result(Result.Status.Failed);

			} else {
				try {
					System.out.println("numPerfil: " + numPerfil);

					if (cpfTemp.isEmpty()) {

						vincularGrupo(config.getString("userBenner"), config.getString("passwordBenner"), cpf, numPerfil);

					} else {

						vincularGrupo(config.getString("userBenner"), config.getString("passwordBenner"), cpfTemp, numPerfil);
					}

				} catch (IOException e) {
					result.add("Falha ao vincular usuario Benner");
					return result = new Result(Result.Status.Failed);
				}
			}
		}

		// Map<String, Object> obj = read(nativeIdentifier);
		// if (null == obj) {
		// throw new ObjectNotFoundException(nativeIdentifier);
		// }

		result = new Result(Result.Status.Committed);

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

	public Result update(String nativeIdentifier, List<Item> items) throws ConnectorException, ObjectNotFoundException {

		System.out.println("===============Entrei no Metodo UPDATE===================");
		System.out.println("===============Entrei no Metodo UPDATE TESTE===================");

		System.out.println(
				"===============Entrei no Metodo UPDATE nativeIdentifie===================" + nativeIdentifier);

		Result result = new Result();

		String cpf = "";

		if (items != null) {
			for (Item item : items) {
				System.out.println("Dentro do For");

				String name = item.getName();
				Object value = item.getValue();
				Item.Operation op = item.getOperation();

				System.out.println("===OPERATION :" + op.toString());
				System.out.println("===name :" + name.toLowerCase());
				System.out.println("===value :" + value.toString());

				ArrayList valueList = new ArrayList();

				if (item.getValue() instanceof String) {
					valueList.add(item.getValue());

				} else {
					valueList = (ArrayList) item.getValue();

				}

				switch (op) {
				case Add: {

					System.out.println("===Entrei no ADD===");

					if (name.equalsIgnoreCase("nome_perfil")) {

						if (item.getValue() instanceof String) {
							valueList.add(item.getValue());

						} else {
							valueList = (ArrayList) item.getValue();

						}
					}

					System.out.println("ValueList: " + valueList);

					if (valueList.size() > 1) {
						for (Object perfil : valueList) {

							System.out.println("perfil: " + perfil);

							String numPerfil = recuperaNumeroPerfil(perfil.toString());

							System.out.println("numPerfil: " + numPerfil);

							if (numPerfil.isEmpty()) {
								result.add("Falha ao inserir o Grupo: " + perfil + " numero perfil vazio");
								return result = new Result(Result.Status.Failed);

							} else {
								try {

									System.out.println("nativeIdentifier: " + nativeIdentifier);

									cpf = recuperaCpf(nativeIdentifier);

									System.out.println("cpf: " + cpf);

									vincularGrupo(config.getString("userBenner"), config.getString("passwordBenner"), cpf, numPerfil);

									System.out.println("numPerfil: " + numPerfil);

									System.out.println("cpf: " + cpf);
								} catch (IOException e) {
									result.add("Falha ao vincular usuario Benner");
									return result = new Result(Result.Status.Failed);
								}
							}
						}
					} else {

						String numPerfil = recuperaNumeroPerfil(valueList.get(0).toString());

						if (numPerfil.isEmpty()) {
							result.add("Falha ao inserir o Grupo: " + valueList.get(0).toString()
									+ " numero perfil vazio");
							return result = new Result(Result.Status.Failed);

						} else {
							try {

								System.out.println("nativeIdentifier: " + nativeIdentifier);

								cpf = recuperaCpf(nativeIdentifier);

								System.out.println("cpf: " + cpf);

								vincularGrupo(config.getString("userBenner"), config.getString("passwordBenner"), cpf, numPerfil);
							} catch (IOException e) {
								result.add("Falha ao vincular usuario Benner");
								return result = new Result(Result.Status.Failed);
							}
						}

					}
				}
					break;
				case Remove: {

					if (item.getValue() instanceof String) {
						valueList.add(item.getValue());

					} else {
						valueList = (ArrayList) item.getValue();

					}

					if (valueList.size() > 1) {
						for (Object perfil : valueList) {

							String numPerfil = recuperaNumeroPerfil(perfil.toString());

							if (numPerfil.isEmpty()) {
								result.add("Falha ao inserir o Grupo: " + perfil + " numero perfil vazio");
								return result = new Result(Result.Status.Failed);

							} else {
								try {

									cpf = recuperaCpf(nativeIdentifier);

									desvincularGrupo(config.getString("userBenner"), config.getString("passwordBenner"), cpf, numPerfil);
								} catch (IOException e) {
									result.add("Falha ao vincular usuario Benner");
									return result = new Result(Result.Status.Failed);
								}
							}
						}
					} else {

						String numPerfil = recuperaNumeroPerfil(valueList.get(0).toString());

						if (numPerfil.isEmpty()) {
							result.add("Falha ao inserir o Grupo: " + valueList.get(0).toString()
									+ " numero perfil vazio");
							return result = new Result(Result.Status.Failed);

						} else {
							try {

								cpf = recuperaCpf(nativeIdentifier);

								desvincularGrupo(config.getString("userBenner"), config.getString("passwordBenner"), cpf, numPerfil);
							} catch (IOException e) {
								result.add("Falha ao vincular usuario Benner");
								return result = new Result(Result.Status.Failed);
							}
						}

					}
				}
					break;
				case Set: {
					// existing.put(name, value);
				}
					break;

				default:
					throw new IllegalArgumentException("Unknown operation: " + op);
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
		Map<String, Object> obj = read(nativeIdentifier);
		if (null == obj) {
			throw new ObjectNotFoundException(nativeIdentifier);
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

		Result result = new Result();

		System.out.println("===============Entrei no Metodo ENABLE===================");

		System.out.println("== nativeIdentifier" + nativeIdentifier);

		String cpf = recuperaCpf(nativeIdentifier);

		try {

			if (cpf.isEmpty()) {
				System.out.println("CPF vazio ou nulo");
			} else {

				desbloquearUsuario(config.getString("userBenner"), config.getString("passwordBenner"), cpf, "123456");
			}

		} catch (IOException e) {

			result.add("Falha ao bloquerUsuario");
			return result = new Result(Result.Status.Failed);
		}

		result = new Result(Result.Status.Committed);

		// Map<String, Object> obj = read(nativeIdentifier);
		// if (null == obj) {
		// throw new ObjectNotFoundException(nativeIdentifier);
		// }

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

		Result result = new Result();

		System.out.println("===============Entrei no Metodo DISABLE==================");

		System.out.println("== nativeIdentifier" + nativeIdentifier);

		String cpf = recuperaCpf(nativeIdentifier);

		try {
			if (cpf.isEmpty()) {
				System.out.println("CPF vazio ou nulo");
			} else {

				bloquearUsuario(config.getString("userBenner"), config.getString("passwordBenner"), cpf);
			}
		} catch (IOException e) {

			result.add("Falha ao bloquerUsuario");
			return result = new Result(Result.Status.Failed);
		}

		result = new Result(Result.Status.Committed);

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
			throws ConnectorException, ObjectNotFoundException, UnsupportedOperationException {

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

	public Result setPassword(String nativeIdentifier, String newPassword, String currentPassword, Date expiration,
			Map<String, Object> options) throws ConnectorException, ObjectNotFoundException {

		System.out.println("===============Entrei no Metodo SETPASSWORD===================");

		Result result = new Result();

		System.out.println("== nativeIdentifier" + nativeIdentifier);

		String cpf = recuperaCpf(nativeIdentifier);

		try {
			if (cpf.isEmpty()) {
				System.out.println("CPF vazio ou nulo");
			} else {

				alteraSenha(config.getString("userBenner"), config.getString("passwordBenner"), cpf, newPassword);
			}
		} catch (IOException e) {

			result.add("Falha ao bloquerUsuario");
			return result = new Result(Result.Status.Failed);
		}

		result = new Result(Result.Status.Committed);

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

	public Map<String, Object> authenticate(String identity, String password) throws ConnectorException,
			ObjectNotFoundException, AuthenticationFailedException, ExpiredPasswordException {

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
	public Result checkStatus(String id)
			throws ConnectorException, ObjectNotFoundException, UnsupportedOperationException {

		Result result = new Result();
		return result;
	}

	/**
	 * Close the underlying resources that are being held by this connector.
	 * Clients MUST call this when they are done with a connector or else there
	 * may be resource leaks.
	 */
	public void close() {
		try {

			if (m_dbConn != null) {
				m_dbConn.close();
				m_dbConn = null;
			}

			if (connection != null) {
				connection.close();
				connection = null;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void init() throws Exception {

		m_sUser = config.getString("user");
		m_sPasswd = config.getString("password");
		m_sUrl = config.getString("url");
		m_sDriverName = config.getString("driverClass");
		
		m_sUser_benner = config.getString("userBenner");
		m_sPasswd_benner = config.getString("passwordBenner");
		m_sUrl_benner = config.getString("urlBenner");
		

		if (!m_bIsDriverLoaded) {
			if (m_dbConn == null) {
				Class.forName(m_sDriverName);

				m_dbConn = DriverManager.getConnection(m_sUrl, m_sUser, m_sPasswd);

				m_bIsDriverLoaded = true;
			}
		}
	}

	public Iterator<Map<String, Object>> getAccounts(String filter) throws Exception {

		groups = null;
		resultObjectList = new ArrayList<Map<String, Object>>();
		ResultSet queryResult = null;

		if (filter.equalsIgnoreCase("*")) {

			ps = preparedStatement("SELECT DISTINCT U.HANDLE, U.NOME, U.APELIDO, GGA.NOME, U.INATIVO "
					+ "FROM Z_GRUPOUSUARIOS U " + "LEFT JOIN Z_GRUPOUSUARIOGRUPOS GA " + "ON (GA.USUARIO = U.HANDLE) "
					+ "LEFT JOIN Z_GRUPOS GGA " + "ON (GGA.HANDLE = GA.GRUPOADICIONADO OR GGA.HANDLE = U.GRUPO) "
					+ "ORDER BY 2 ");

			queryResult = ps.executeQuery();

		} else {

			ps = preparedStatement("SELECT DISTINCT U.HANDLE, U.NOME, U.APELIDO, GGA.NOME, U.INATIVO "
					+ "FROM Z_GRUPOUSUARIOS U " + "LEFT JOIN Z_GRUPOUSUARIOGRUPOS GA " + "ON (GA.USUARIO = U.HANDLE) "
					+ "LEFT JOIN Z_GRUPOS GGA " + "ON (GGA.HANDLE = GA.GRUPOADICIONADO OR GGA.HANDLE = U.GRUPO) "
					+ " where u.nome = '" + filter + "' " + "ORDER BY 2 ");

			queryResult = ps.executeQuery();
		}

		try {

			String userid = "";
			ArrayList<String> perfil = new ArrayList<String>();

			while (queryResult.next()) {

				if (userid.trim().toUpperCase().equals(queryResult.getString(3).trim().toUpperCase())) {

					perfil.add(queryResult.getString(4));

				} else {
					if (account != null) {
						account.put("NOME_PERFIL", perfil);
						resultObjectList.add(account);
					}

					account = new HashMap<String, Object>();
					perfil = new ArrayList<String>();

					userid = queryResult.getString(3).trim();
					account.put("APELIDO", userid.trim());
					account.put("HANDLE", queryResult.getString(1));
					account.put("NOME", queryResult.getString(2));
					account.put("INATIVO", queryResult.getString(5));
					perfil.add(queryResult.getString(4));

					String temp = queryResult.getString(5);
					if (temp.equals("S")) {
						account.put(openconnector.Connector.ATT_DISABLED, new Boolean(true));
					} else {
						account.put(openconnector.Connector.ATT_DISABLED, new Boolean(false));
					}

				}

			}

			resultObjectList.add(account);

			if (queryResult != null)
				queryResult.close();
		} catch (Exception e) {
			throw new ConnectorException("Erro BENNER Metodo GetAccount " + e);
		} finally {
			connection.close();
			connection = null;
		}

		return resultObjectList.iterator();
	}

	public Iterator<Map<String, Object>> getGroups(String filter) throws Exception {

		groups = null;
		resultObjectList = new ArrayList<Map<String, Object>>();
		ResultSet queryResult = null;

		if (filter.equalsIgnoreCase("*")) {

			ps = preparedStatement("SELECT HANDLE, NOME " + "FROM Z_GRUPOS " + "ORDER BY NOME");

			queryResult = ps.executeQuery();

		} else {

			ps = preparedStatement(
					"SELECT HANDLE, NOME " + "FROM Z_GRUPOS " + "WHERE NOME= '" + filter + "' " + "ORDER BY NOME");

			queryResult = ps.executeQuery();
		}

		try {

			while (queryResult.next()) {

				groups = new HashMap<String, Object>();

				groups.put("HANDLE", queryResult.getString(1));
				groups.put("NOME", queryResult.getString(2));

				resultObjectList.add(groups);
			}

			if (queryResult != null)
				queryResult.close();

		} catch (Exception e) {

			throw new ConnectorException("Erro BEMMER Metodo GetGroup " + e);
		} finally {
			connection.close();
			connection = null;
		}

		return resultObjectList.iterator();
	}

	public PreparedStatement preparedStatement(String sql) throws SQLException, ClassNotFoundException {

		ps = null;
		ps = getConnection().prepareStatement(sql);

		return ps;
	}

	// conexao ao banco
	protected Connection getConnection() throws SQLException, ClassNotFoundException {

		if (connection == null) {
			Class.forName(config.getString("driverClass"));
			connection = DriverManager.getConnection(config.getString("url"), config.getString("user"),
					config.getString("password"));
		}
		return connection;
	}

	public String recuperaNumeroPerfil(String nomePerfil) {
		ResultSet queryResult = null;
		String numeroPerfil = "";

		try {

			ps = preparedStatement("SELECT HANDLE FROM Z_GRUPOS " + "WHERE NOME= '" + nomePerfil + "'");

			queryResult = ps.executeQuery();

			while (queryResult.next()) {

				numeroPerfil = queryResult.getString(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return numeroPerfil;

	}

	public String recuperaCpf(String apelido) {
		ResultSet queryResult = null;
		String cpf = "";

		System.out.println("Entrei no recuperaCpf apelido: " + apelido);

		try {

			ps = preparedStatement("SELECT CPF FROM SOCPRO.Z_GRUPOUSUARIOS WHERE apelido = '" + apelido + "'");

			System.out.println("sql : SELECT CPF FROM SOCPRO.Z_GRUPOUSUARIOS WHERE apelido = '" + apelido + "'");

			queryResult = ps.executeQuery();

			while (queryResult.next()) {

				cpf = queryResult.getString(1);
			}

			System.out.println("Returno CPF: " + cpf);

		} catch (SQLException e) {
			System.out.println("Erro no recuperaCPF " + e);
		} catch (ClassNotFoundException e) {
			System.out.println("Erro no recuperaCPF " + e);
		}

		return cpf;

	}

	public String recuperaDadosUsuario(String apelido) {
		ResultSet queryResult = null;
		String cpf = "";

		System.out.println("Entrei no recuperaCpf apelido: " + apelido);

		try {

			ps = preparedStatement(
					"SELECT CPF FROM SOCPRO.Z_GRUPOUSUARIOS WHERE UPPER (apelido) = UPPER('" + apelido + "')");

			System.out.println("sql : SELECT CPF FROM SOCPRO.Z_GRUPOUSUARIOS WHERE apelido = '" + apelido + "'");

			queryResult = ps.executeQuery();

			while (queryResult.next()) {

				cpf = queryResult.getString(1);
			}

			System.out.println("Returno CPF: " + cpf);

		} catch (SQLException e) {
			System.out.println("Erro no recuperaCPF " + e);
		} catch (ClassNotFoundException e) {
			System.out.println("Erro no recuperaCPF " + e);
		}

		return cpf;

	}

	public HttpURLConnection getConection() throws IOException {

		System.out.println("============ENTREI NO getConectio SOAP===============");

		URL url = new URL(config.getString("urlBenner"));
		URLConnection urclConnection = url.openConnection();
		HttpURLConnection connection = (HttpURLConnection) urclConnection;
		connection.setDoOutput(true);
		connection.setDoInput(true);

		connection.setAllowUserInteraction(true);

		connection.setRequestMethod("POST");

		return connection;
	}

	public void enviaMensagem(OutputStreamWriter infWebSvcReqWriter, String mensagem,
			HttpURLConnection connection) throws IOException {

		System.out.println("============ENTREI NO ENVIAR MENSAGEM===============");

		System.out.println("XML de ENVIO " + mensagem);
		String infWebSvcRequestMessage = mensagem;

		infWebSvcReqWriter.write(infWebSvcRequestMessage);

		infWebSvcReqWriter.flush();

		BufferedReader reader = null;

		/**
		 *  * Verifica sucesso da requisição, em caso positivo obtém XML
		 * esperado em caso negativo  * obtém XML de Erro  
		 */
		if (connection.getResponseCode() == 200) {

			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		} else {

			reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
		}

		String line;

		String infWebSvcReplyString = "";

		while ((line = reader.readLine()) != null) {

			infWebSvcReplyString = infWebSvcReplyString.concat(line);

		}

		infWebSvcReqWriter.close();

		reader.close();

		connection.disconnect();

		/**
		 *  * Apresenta no console do Java o XML de recuperado           
		 */
		System.out.println("RETORNO DA MENSAGEM " + infWebSvcReplyString);
	}

	public void cadastraUsuario(String usuarioServico, String senhaServico, String centroCusto, String cpf,
			String login, String nome, String senha) throws IOException {

		System.out.println("============ENTREI NO CADASTRAR USUARIO===============");

		System.out.println(
				"Dados: usuarioServico: " + usuarioServico + " senhaServico: " + senhaServico + " centroCusto: "
						+ centroCusto + " cpf: " + cpf + " login: " + login + " nome: " + nome + " senha: " + senha);

		HttpURLConnection connection = getConection();

		connection.setRequestProperty("SOAPAction", "http://tempuri.org/ICadastro/InserirUsuario");

		connection.setRequestProperty("Host", "socweb025.homol.cassi.com.br");

		connection.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");

		OutputStreamWriter infWebSvcReqWriter = new OutputStreamWriter(connection.getOutputStream());

		String mensagem = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:tem=\"http://tempuri.org/\" xmlns:ben=\"http://schemas.datacontract.org/2004/07/Benner.SOC.Integracao.ClassesDeNegocio.Usuarios\">"
				+ "<soap:Header xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" xmlns:wsrm=\"http://docs.oasis-open.org/ws-rx/wsrm/200702/\">"
				+ "<wsa:Action>http://tempuri.org/ICadastro/InserirUsuario</wsa:Action>"
				+ "<wsa:To>https://socweb025.homol.cassi.com.br/BennerIAM/Benner.SOC.Integracao.IAM.Cadastro.svc</wsa:To>"
				+ "</soap:Header>"

				+ "<soap:Body>" + "<tem:InserirUsuario>"

		// Usuario de Servico
				+ "<tem:login>" + usuarioServico + "</tem:login>"
				// Senha de servico
				+ "<tem:senha>" + senhaServico + "</tem:senha>" + "<tem:usuario>"

		// Centro de custo
				+ "<ben:CentroDeCusto>" + centroCusto + "</ben:CentroDeCusto>"
				// CPF
				+ "<ben:Cpf>" + cpf + "</ben:Cpf>"
				// Login
				+ "<ben:Login>" + login + "</ben:Login>"
				// Nome
				+ "<ben:Nome>" + nome + "</ben:Nome>"
				// Senha
				+ "<ben:Senha>" + senha + "</ben:Senha>"

				+ "</tem:usuario>" + "</tem:InserirUsuario>" + "</soap:Body>" + "</soap:Envelope>";

		enviaMensagem(infWebSvcReqWriter, mensagem, connection);
	}

	public void vincularGrupo(String usuarioServico, String senhaServico, String cpf, String grupo)
			throws IOException {

		System.out.println("============ENTREI NO VINCULAR USUARIO===============");

		System.out.println("Dados: usuarioServico: " + usuarioServico + " senhaServico: " + senhaServico + " cpf: "
				+ cpf + " grupo: " + grupo);

		HttpURLConnection connection = getConection();

		connection.setRequestProperty("SOAPAction", "http://tempuri.org/ICadastro/InserirGruposDeSeguranca");

		connection.setRequestProperty("Host", "socweb025.homol.cassi.com.br");

		connection.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");

		OutputStreamWriter infWebSvcReqWriter = new OutputStreamWriter(connection.getOutputStream());

		String mensagem =
		// Cabecalho fix
		"<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:tem=\"http://tempuri.org/\" "
				// Campo de acordo com programa aut
				+ "xmlns:arr=\"http://schemas.microsoft.com/2003/10/Serialization/Arrays\">"
				// Header cabecalho fix
				+ "<soap:Header xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" xmlns:wsrm=\"http://docs.oasis-open.org/ws-rx/wsrm/200702/\">"
				// Acrion aut
				+ "<wsa:Action>http://tempuri.org/ICadastro/InserirGruposDeSeguranca</wsa:Action>"
				// TO fix
				+ "<wsa:To>https://socweb025.homol.cassi.com.br/BennerIAM/Benner.SOC.Integracao.IAM.Cadastro.svc</wsa:To>"
				+ "</soap:Header>"

				+ "<soap:Body>"

		// Aut
				+ "<tem:InserirGruposDeSeguranca>"

		// Usuario de Servico
				+ "<tem:login>" + usuarioServico + "</tem:login>"
				// Senha de servico
				+ "<tem:senha>" + senhaServico + "</tem:senha>"

		// Centro de custo
		// CPF
				+ "<tem:cpf>" + cpf + "</tem:cpf>"
				// Id Gruop pode inserir varios grupos
				+ "<tem:grupos>" + "<arr:int>" + grupo + "</arr:int>" + "</tem:grupos>"
				// aut
				+ "</tem:InserirGruposDeSeguranca>"
				// fix
				+ "</soap:Body>"
				// fix
				+ "</soap:Envelope>";

		enviaMensagem(infWebSvcReqWriter, mensagem, connection);
	}

	public void desvincularGrupo(String usuarioServico, String senhaServico, String cpf, String grupo)
			throws IOException {

		System.out.println("============ENTREI NO DESVINCULAR USUARIO===============");

		System.out.println("Dados: usuarioServico: " + usuarioServico + " senhaServico: " + senhaServico + " cpf: "
				+ cpf + " grupo: " + grupo);

		HttpURLConnection connection = getConection();

		// Aut
		connection.setRequestProperty("SOAPAction", "http://tempuri.org/ICadastro/ExcluirGruposDeSeguranca");

		// fix
		connection.setRequestProperty("Host", "socweb025.homol.cassi.com.br");

		// fix
		connection.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");

		OutputStreamWriter infWebSvcReqWriter = new OutputStreamWriter(connection.getOutputStream());

		String mensagem =
		// Cabecalho fix
		"<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:tem=\"http://tempuri.org/\" "
				// Campo de acordo com programa aut
				+ "xmlns:arr=\"http://schemas.microsoft.com/2003/10/Serialization/Arrays\">"
				// Header cabecalho fix
				+ "<soap:Header xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" xmlns:wsrm=\"http://docs.oasis-open.org/ws-rx/wsrm/200702/\">"
				// Acrion aut
				+ "<wsa:Action>http://tempuri.org/ICadastro/ExcluirGruposDeSeguranca</wsa:Action>"
				// TO fix
				+ "<wsa:To>https://socweb025.homol.cassi.com.br/BennerIAM/Benner.SOC.Integracao.IAM.Cadastro.svc</wsa:To>"
				+ "</soap:Header>"

				+ "<soap:Body>"

		// Aut
				+ "<tem:ExcluirGruposDeSeguranca>"

		// Usuario de Servico
				+ "<tem:login>" + usuarioServico + "</tem:login>"
				// Senha de servico
				+ "<tem:senha>" + senhaServico + "</tem:senha>"

		// Centro de custo
		// CPF
				+ "<tem:cpf>" + cpf + "</tem:cpf>"
				// Id Gruop pode inserir varios grupos
				+ "<tem:grupos>" + "<arr:int>" + grupo + "</arr:int>" + "</tem:grupos>"
				// aut
				+ "</tem:ExcluirGruposDeSeguranca>"
				// fix
				+ "</soap:Body>"
				// fix
				+ "</soap:Envelope>";

		enviaMensagem(infWebSvcReqWriter, mensagem, connection);
	}

	public void bloquearUsuario(String usuarioServico, String senhaServico, String cpf) throws IOException {

		System.out.println("============ENTREI NO BLOQUEAR USUARIO===============");

		System.out.println(
				"Dados: usuarioServico: " + usuarioServico + " senhaServico: " + senhaServico + " cpf: " + cpf);

		HttpURLConnection connection = getConection();

		// Aut
		connection.setRequestProperty("SOAPAction", "http://tempuri.org/ICadastro/InativarUsuario");

		// fix
		connection.setRequestProperty("Host", "socweb025.homol.cassi.com.br");

		// fix
		connection.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");

		OutputStreamWriter infWebSvcReqWriter = new OutputStreamWriter(connection.getOutputStream());

		String mensagem =
		// Cabecalho fix
		"<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:tem=\"http://tempuri.org/\">"
				// Campo de acordo com programa aut
				// Header cabecalho fix
				+ "<soap:Header xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" xmlns:wsrm=\"http://docs.oasis-open.org/ws-rx/wsrm/200702/\">"
				// Acrion aut
				+ "<wsa:Action>http://tempuri.org/ICadastro/InativarUsuario</wsa:Action>"
				// TO fix
				+ "<wsa:To>https://socweb025.homol.cassi.com.br/BennerIAM/Benner.SOC.Integracao.IAM.Cadastro.svc</wsa:To>"
				+ "</soap:Header>"

		// fix
				+ "<soap:Body>"

		// Aut
				+ "<tem:InativarUsuario>"

		// Fix Usuario de Servico
				+ "<tem:login>" + usuarioServico + "</tem:login>"
				// Fix Senha de servico
				+ "<tem:senha>" + senhaServico + "</tem:senha>"
				// Aut CPF
				+ "<tem:cpf>" + cpf + "</tem:cpf>"
				// Id Gruop pode inserir varios grupos

		// fix
				+ "</tem:InativarUsuario>"
				// fix
				+ "</soap:Body>"
				// fix
				+ "</soap:Envelope>";

		enviaMensagem(infWebSvcReqWriter, mensagem, connection);
	}

	public void desbloquearUsuario(String usuarioServico, String senhaServico, String cpf, String senha)
			throws IOException {

		System.out.println("============ENTREI NO DESBLOQUEAR USUARIO===============");

		System.out.println("Dados: usuarioServico: " + usuarioServico + " senhaServico: " + senhaServico + " cpf: "
				+ cpf + " senha: " + senha);

		HttpURLConnection connection = getConection();

		// Aut
		connection.setRequestProperty("SOAPAction", "http://tempuri.org/ICadastro/AtivarUsuario");

		// fix
		connection.setRequestProperty("Host", "socweb025.homol.cassi.com.br");

		// fix
		connection.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");

		OutputStreamWriter infWebSvcReqWriter = new OutputStreamWriter(connection.getOutputStream());

		String mensagem =
		// Cabecalho fix
		"<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:tem=\"http://tempuri.org/\">"
				// Header cabecalho fix
				+ "<soap:Header xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" xmlns:wsrm=\"http://docs.oasis-open.org/ws-rx/wsrm/200702/\">"
				// WSA aut
				+ "<wsa:Action>http://tempuri.org/ICadastro/AtivarUsuario</wsa:Action>"
				// TO fix
				+ "<wsa:To>https://socweb025.homol.cassi.com.br/BennerIAM/Benner.SOC.Integracao.IAM.Cadastro.svc</wsa:To>"
				+ "</soap:Header>"

		// fix
				+ "<soap:Body>"

		// Aut
				+ "<tem:AtivarUsuario>"

		// Fix Usuario de Servico
				+ "<tem:login>" + usuarioServico + "</tem:login>"
				// Fix Senha de servico
				+ "<tem:senha>" + senhaServico + "</tem:senha>"
				// Aut CPF
				+ "<tem:cpf>" + cpf + "</tem:cpf>"
				// Nova Senha
				+ "<tem:novaSenha>" + senha + "</tem:novaSenha>"

		// fix
				+ "</tem:AtivarUsuario>"
				// fix
				+ "</soap:Body>"
				// fix
				+ "</soap:Envelope>";

		enviaMensagem(infWebSvcReqWriter, mensagem, connection);
	}

	public void alteraSenha(String usuarioServico, String senhaServico, String cpf, String senha)
			throws IOException {

		System.out.println("============ALTERAR SENHA===============");

		System.out.println("Dados: usuarioServico: " + usuarioServico + " senhaServico: " + senhaServico + " cpf: "
				+ cpf + " senha: " + senha);

		HttpURLConnection connection = getConection();

		// Aut
		connection.setRequestProperty("SOAPAction", "http://tempuri.org/ICadastro/AlterarUsuario");

		// fix
		connection.setRequestProperty("Host", "socweb025.homol.cassi.com.br");

		// fix
		connection.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");

		OutputStreamWriter infWebSvcReqWriter = new OutputStreamWriter(connection.getOutputStream());

		String mensagem =
		// Cabecalho fix
		"<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:tem=\"http://tempuri.org/\" xmlns:ben=\"http://schemas.datacontract.org/2004/07/Benner.SOC.Integracao.ClassesDeNegocio.Usuarios\">"
				// Header cabecalho fix
				+ "<soap:Header xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" xmlns:wsrm=\"http://docs.oasis-open.org/ws-rx/wsrm/200702/\">"
				// WSA aut
				+ "<wsa:Action>http://tempuri.org/ICadastro/AlterarUsuario</wsa:Action>"
				// TO fix
				+ "<wsa:To>https://socweb025.homol.cassi.com.br/BennerIAM/Benner.SOC.Integracao.IAM.Cadastro.svc</wsa:To>"
				+ "</soap:Header>"

		// fix
				+ "<soap:Body>"

		// Aut
				+ "<tem:AlterarUsuario>"

		// Fix Usuario de Servico
				+ "<tem:login>" + usuarioServico + "</tem:login>"
				// Fix Senha de servico
				+ "<tem:senha>" + senhaServico + "</tem:senha>"

				+ "<tem:usuario>" + "<ben:Cpf>" + cpf + "</ben:Cpf>" + "<ben:Senha>" + senha + "</ben:Senha>"
				+ "</tem:usuario>"

		// fix
				+ "</tem:AlterarUsuario>"
				// fix
				+ "</soap:Body>"
				// fix
				+ "</soap:Envelope>";

		enviaMensagem(infWebSvcReqWriter, mensagem, connection);
	}

	public void alteraUsuario(String usuarioServico, String senhaServico, String cpf, String centroCusto,
			String senha, String nome) throws IOException {

		System.out.println("============ALTERAR USUARIO===============");

		System.out.println("Dados: usuarioServico: " + usuarioServico + " senhaServico: " + senhaServico + " cpf: "
				+ cpf + " senha: " + senha);

		HttpURLConnection connection = getConection();

		// Aut
		connection.setRequestProperty("SOAPAction", "http://tempuri.org/ICadastro/AlterarUsuario");

		// fix
		connection.setRequestProperty("Host", "socweb025.homol.cassi.com.br");

		// fix
		connection.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");

		OutputStreamWriter infWebSvcReqWriter = new OutputStreamWriter(connection.getOutputStream());

		String mensagem =
		// Cabecalho fix
		"<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:tem=\"http://tempuri.org/\" xmlns:ben=\"http://schemas.datacontract.org/2004/07/Benner.SOC.Integracao.ClassesDeNegocio.Usuarios\">"
				// Header cabecalho fix
				+ "<soap:Header xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" xmlns:wsrm=\"http://docs.oasis-open.org/ws-rx/wsrm/200702/\">"
				// WSA aut
				+ "<wsa:Action>http://tempuri.org/ICadastro/AlterarUsuario</wsa:Action>"
				// TO fix
				+ "<wsa:To>https://socweb025.homol.cassi.com.br/BennerIAM/Benner.SOC.Integracao.IAM.Cadastro.svc</wsa:To>"
				+ "</soap:Header>"

		// fix
				+ "<soap:Body>"

		// Aut
				+ "<tem:AlterarUsuario>"

		// Fix Usuario de Servico
				+ "<tem:login>" + usuarioServico + "</tem:login>"
				// Fix Senha de servico
				+ "<tem:senha>" + senhaServico + "</tem:senha>"

				+ "<tem:usuario>" + "<ben:Cpf>" + cpf + "</ben:Cpf>" + "<ben:CentroDeCusto>" + centroCusto
				+ "</ben:CentroDeCusto>" + "<ben:Senha>" + senha + "</ben:Senha>" + "<ben:Nome>" + nome + "</ben:Nome>"

				+ "</tem:usuario>"

		// fix
				+ "</tem:AlterarUsuario>"
				// fix
				+ "</soap:Body>"
				// fix
				+ "</soap:Envelope>";

		enviaMensagem(infWebSvcReqWriter, mensagem, connection);
	}

}
