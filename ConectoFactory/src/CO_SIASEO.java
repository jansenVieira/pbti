import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import oracle.jdbc.OracleTypes;
import openconnector.*;

/*

 This file  provides a skeletal structure of the openconnector interfaces.
 It is the responsiblity of the connector developer to implement the methods.

 */
public class CO_SIASEO extends AbstractConnector
	{

		public String coUsuario = "CO_USUARIO"; // used as
		// nativeIdentityAttribute
		// for account objectType.
		public String noUsuario = "NO_USUARIO";
		public String perfis = "PERFIS";

		public String coSistema = "CO_SISTEMA";
		public String nuPerfil = "NU_PERFIL";
		public String noPerfil = "NO_PERFIL";
		public String sisPerfil = "SIS_PERFIL"; // used as
		// nativeIdentityAttribute
		// for group objectType.
		public String icStatus = "IC_STATUS";
		public String nuMatricula = "NU_MATR";
		public String icTipo = "IC_TIPO";
		public String coSenhaAcesso = "CO_SENHA_ACESSO";
		public int tipoOperacao;

		public String password = "password";

		private boolean m_bIsDriverLoaded;
		private String m_sUser;
		private String m_sPasswd;
		private String m_sUrl;
		private String m_sDriverName;
		private Map<String, Object> account;
		private List<Map<String, Object>> resultObjectList;
		private Iterator<Map<String, Object>> it;
		private Map<String, Object> groups;
		private Connection m_dbConn;

		// variavel conexão
		private static Connection connection;
		public static String URL;
		public static String SENHA;
		public static String USUARIO;
		public static String DRIVE;
		public CallableStatement cs;

		// Retorno Lista
		public ResultSet retornoUsuario;
		public ResultSet retornoPerfil;
		public ResultSet retornoGroups;
		public ResultSet retornoConexaoUsuario;
		public ResultSet resultListaUsuarioDelete;
		public boolean usuarioExiste;

		// Constantes
		public static String PATTERN_DATE_CONTROLO = "yyyyMMddHHmmss";
		public static String PATTERN_DATE_PADRAO = "dd/MM/yyyy HHmmss";
		public static String IC_STATUS_USUARIO_ATIVO = "0";
		public static String IC_STATUS_USUARIO_INATIVO = "1";
		public static String IC_STATUS_USUARIO_LOCK = "L";
		public static String IC_STATUS_USUARIO_UNLOCK = "U";
		public static String INCLUIR = "I";
		public static String ALTERAR = "A";
		public static String EXCLUIR = "E";
		public static String NAO = "N";
		public static String REVOKED = "REVOKED";
		public static String ACTIVE = "ACTIVE";
		public Date DT_LIMITE_ACESSO = null;
		public String IC_VISAO_NACIONAL = "0";

		// ////////////////////////////////////////////
		// ////////////////////////////
		//
		// INNER CLASS
		//
		// //////////////////////////////////////////////////////////////////////////

		/**
		 * An iterator that handle the paging logic to send data in chunks.
		 */
		private class PagingIterator implements Iterator<Map<String, Object>>
			{

				private Iterator<Map<String, Object>> it;
				Map<String, Object> obj;

				public PagingIterator(Iterator<Map<String, Object>> it)
				{
					this.it = it;
				}

				public boolean hasNext() {
					/*
					 * Check if the iterator has data if not try to fetch the
					 * next chunk from managed system
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
		public CO_SIASEO()
		{
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
		public CO_SIASEO(ConnectorConfig config, openconnector.Log log)
				throws Exception
		{
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
		 * Return the list of Features that are supported for the given object
		 * type. If a Feature is returned, the corresponding method should not
		 * throw an UnsupportedOperationException for the given object type.
		 */

		public List<Feature> getSupportedFeatures(String objectType) {
			return Arrays.asList(Feature.values());
		}

		/**
		 * Return a list of the object types supported by this connector. This
		 * list can contain any of the OBJECT_TYPE_* constants or any arbitrary
		 * type that is supported by the connector.
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
		 * Test the configured to see if a connection can be made. This should
		 * throw exceptions if the configuration is not complete, has invalid
		 * values, the resource cannot be contacted. If the connection succeeds,
		 * no exceptions are thrown.
		 */
		public void testConnection() {
			try
				{
					init();

				} catch (Exception e)
				{
					if (log.isDebugEnabled())
						log.error("Error for TestConnection ", e);
					throw new ConnectorException(
							"Test Connection failed with message : "
									+ e.getMessage()
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
		// UnsupportedOperationException {
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
		 * Return the object of the configured object type that has the given
		 * native identifier.
		 * 
		 * @param id
		 *            A unique identifier for the object.
		 * 
		 * @return The object that was read from the resource, or null if an
		 *         object with the given id was not found.
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
			groups = null;
			it = null;

			try
				{
					init();
				} catch (Exception e1)
				{
					e1.printStackTrace();
				}

			if (OBJECT_TYPE_ACCOUNT.equals(this.objectType))
				{
					try
						{

							it = getAccounts(nativeIdentifier);

						} catch (Exception e)
						{
							e.printStackTrace();
						}

					if (it.hasNext())
						{
							account = it.next();
							return account;
						}
				}

			if (OBJECT_TYPE_GROUP.equals(this.objectType))
				{
					try
						{
							it = getGroups(nativeIdentifier);

						} catch (Exception e)
						{
							e.printStackTrace();
						}

					if (it.hasNext())
						{
							groups = it.next();
							return groups;
						}
				}

			// Add the code for single read of objectType
			return readMap;
		}

		/**
		 * Return an iterator over the objects of the configured object type
		 * that match the given filter (or all objects if no filter is
		 * specified).
		 * 
		 * @param filter
		 *            The possibly null filter to use to constrain which objects
		 *            are returned.
		 * 
		 * @return An iterator over the object of the configured object type
		 *         that match the given filter.
		 *
		 * @throws ConnectorException
		 *             If the operation fails.
		 * @throws UnsupportedOperationException
		 *             If the ITERATE feature is not supported.
		 */

		public Iterator<Map<String, Object>> iterate(Filter filterArgs)
				throws ConnectorException, UnsupportedOperationException {

			try
				{
					init();
				} catch (Exception e1)
				{
					e1.printStackTrace();
				}

			String filter = "*";

			it = null;

			if (OBJECT_TYPE_ACCOUNT.equals(this.objectType))
				{
					try
						{
							it = getAccounts(filter);
						} catch (Exception e)
						{
							e.printStackTrace();
						}
				}

			if (OBJECT_TYPE_GROUP.equals(this.objectType))
				{
					try
						{
							it = getGroups(filter);
						} catch (Exception e)
						{
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

			// Result result = new Result(Result.Status.Committed);
			//
			// // Add the code here for handling the create operation
			//
			// return result;

			Result result = new Result();

			String codSistema = "", codUsuario = "", numPerfil = "", passwordLimpo = "", sistemaPerfis = "";
			ArrayList<String> arrayPerfilSistema = new ArrayList<String>();

			codUsuario = nativeIdentifier;

			log.debug("Create - ID USUARIO " + codUsuario);

			try
				{

					for (openconnector.Item item : items)
						{

							String name = item.getName();
							String value = item.getValue().toString();

							if (name.equalsIgnoreCase(coSenhaAcesso))
								{

									passwordLimpo = value;
								}

							if (name.equalsIgnoreCase(sisPerfil))
								{

									ArrayList valueList = new ArrayList();

									if (item.getValue() instanceof String)
										{
											valueList.add(item.getValue());

										} else
										{
											valueList = (ArrayList) item
													.getValue();

										}

									if (valueList.size() > 1)
										{
											for (Object sistemaPerfil : valueList)
												{
													String sitemPerf = sistemaPerfil
															.toString();

													String[] array = sitemPerf
															.split("/");

													String sistema = array[0];

													for (Object sistemaPerfil2 : valueList)
														{
															String sitemPerf2 = sistemaPerfil2
																	.toString();

															String[] array2 = sitemPerf2
																	.split("/");

															String sistema2 = array2[0];

															if (sistema
																	.equals(sistema2))
																{
																	result = new Result(
																			Result.Status.Failed);
																	result.add("TIPO APP SIASEO "
																			+ "O sistema não permite adicionar mais de um perfil.");
																	return result;
																}
														}
													arrayPerfilSistema
															.add(sitemPerf);
												}
										} else
										{

											sistemaPerfis = valueList.get(0)
													.toString();

											arrayPerfilSistema
													.add(sistemaPerfis);
										}
								}
						}

					usuarioExiste(codUsuario);

					if (usuarioExiste == false)
						{
							criarUsuarioOracle(codUsuario, passwordLimpo);

							criarUsuarioTabelasOracle(codUsuario);
						}

					for (String sistemPerfis : arrayPerfilSistema)
						{

							String[] array = sistemPerfis.split("/");

							String sistema = array[0];
							String perfil = array[1];

							dadosPerfil(sistema, perfil);

							numPerfil = nuPerfil;
							codSistema = sistema;

							createConnection(codSistema, codUsuario, numPerfil);

							result = new Result(Result.Status.Committed);

						}

				} catch (Exception e)
				{
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());
					result.add("CO_SISTEMA: " + codSistema + " CO_USUARIO: "
							+ codUsuario + " NUM_PERFIL: " + numPerfil
							+ " PASSWORD: " + passwordLimpo);
					if (log.isDebugEnabled())
						log.debug("Exception occured ", e);
					String message = "Exception occured while creating the entity";
					result.add(message);
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

		public Result update(String nativeIdentifier, List<Item> items)
				throws ConnectorException, ObjectNotFoundException {

					Result result = new Result();

					Map<String, Object> existing = read(nativeIdentifier);
					if (null == existing) {
						throw new ObjectNotFoundException(nativeIdentifier);
					}

					String codSistema = "", codUsuario = "", nomUsuario = "", icStatu = "", icType = "", 
							codSenha = "", numPerfil = "", passwordLimpo = "Partner2015";
					int operacao = 0;
					ArrayList<String> arrayPerfilSistema = new ArrayList<String>();

					codUsuario = nativeIdentifier;

					log.debug("UPDATE CodUSUARIO " + codUsuario);

					if (items != null) {
						for (Item item : items) {
							String name = item.getName();
							// String value = item.getValue().toString();
							Item.Operation op = item.getOperation();
							ArrayList valueList = new ArrayList();

							if (item.getValue() instanceof String) {
								valueList.add(item.getValue());

							} else {
								valueList = (ArrayList) item.getValue();

							}

							if (valueList.size() > 1) {
								for (Object sistemaPerfil : valueList) {
									String sitemPerf = sistemaPerfil.toString();

									String[] array = sitemPerf.split("/");

									String sistema = array[0];

									for (Object sistemaPerfil2 : valueList) {
										String sitemPerf2 = sistemaPerfil2.toString();

										String[] array2 = sitemPerf2.split("/");

										String sistema2 = array2[0];

										if (sistema.equals(sistema2)) {
											result = new Result(Result.Status.Failed);
											result.add("TIPO APP SIASES "
													+ "O sistema não permite adicionar mais de um perfil.");
											return result;
										}
									}
								}

							}

							log.debug("Item name " + name);
							log.debug("Item value " + valueList.get(0).toString());
							log.debug("Item operation " + op);

							switch (op) {

							case Add: {

								if (name.equalsIgnoreCase(sisPerfil)) {

									for (Object sistemaPerfil : valueList) {
										String sistemaPerfis = sistemaPerfil.toString();

										arrayPerfilSistema.add(sistemaPerfis);
									}

								}

								for (String sistemaPerfis : arrayPerfilSistema) {

									String[] array = sistemaPerfis.split("/");

									String sistema = array[0];
									String perfil = array[1];

									dadosPerfil(sistema, perfil);

									numPerfil = nuPerfil;
									codSistema = sistema;

									try {
										
										usuarioExiste(codUsuario);

										if(usuarioExiste == false)
										{
											criarUsuarioOracle(codUsuario, passwordLimpo);

											criarUsuarioTabelasOracle(codUsuario);
										}
										
										createConnection(codSistema, codUsuario, numPerfil);

										log.debug("UPDADE - PROCEDURE ADD " + "CodUsuario "
												+ codUsuario + " CodSistema " + codSistema
												+ " nomUsuario " + nomUsuario + " icStatu "
												+ icStatu + " icType " + icType
												+ " codSenha " + codSenha + " numPerfil "
												+ numPerfil + " operacao " + operacao);

										result = new Result(Result.Status.Committed);
									} catch (Exception e) {
										e.printStackTrace();
										result = new Result(Result.Status.Failed);
										result.add("TIPO APP SIASES " + e.getMessage());
									}
								}
							}

								break;

							case Remove: {

								if (name.equalsIgnoreCase(sisPerfil)) {

									for (Object sistemaPerfil : valueList) {
										String sistemaPerfis = sistemaPerfil.toString();

										arrayPerfilSistema.add(sistemaPerfis);
									}


									try {

										for (String sistemaPerfis : arrayPerfilSistema) {

											String[] array = sistemaPerfis.split("/");

											String sistema = array[0];
											String perfil = array[1];

											codSistema = sistema;
											
											dadosPerfil(sistema, perfil);

											numPerfil = nuPerfil;
											codSistema = sistema;
											

											deletarConnectionUsuario(codSistema, codUsuario, numPerfil);
											
											
											log.debug("UPDADE - PROCEDURE DESVINCULAR "
													+ "CodUsuario " + codUsuario
													+ " CodSistema " + codSistema);

											result = new Result(Result.Status.Committed);

										}
									} catch (Exception e) {
										e.printStackTrace();
										result = new Result(Result.Status.Failed);
										result.add("TIPO APP SIASES " + e.getMessage());
									}
								}

							}
								break;
							case Set: {

								existing.put(name, valueList.get(0).toString());
								
								log.debug("SET UPDATE");
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
		 * Delete the object of the configured object type that has the given
		 * native identifier.
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
		public Result delete(String nativeIdentifier,
				Map<String, Object> options) throws ConnectorException,
				ObjectNotFoundException {

					Result result = new Result();

					String codUsuario = "", codSistema= "", numPerfil= "";

					codUsuario = nativeIdentifier;

					log.debug("DELETA USUARIO " + "CodUsuario " + codUsuario);
					
					
						listaConexoesDelete(codUsuario);
						
						try {
							while (resultListaUsuarioDelete.next()) {
								
								codSistema = resultListaUsuarioDelete.getString("CO_SISTEMA");
								numPerfil = resultListaUsuarioDelete.getString("NU_PERFIL");

								deletarConnectionUsuario(codSistema, codUsuario, numPerfil);
							}
						} catch (SQLException e1) {
							e1.printStackTrace();
							result = new Result(Result.Status.Failed);
							result.add("TIPO APP SIASEO " + e1.getMessage());
						}
						
						deletarUsuarioTabelasOracle(codUsuario);
						deletarUsuarioOracle(codUsuario);
						result = new Result(Result.Status.Committed);
					

					Map<String, Object> obj = read(nativeIdentifier);
					if (null == obj) {
						throw new ObjectNotFoundException(nativeIdentifier);
					}
					return result;
		}

		/**
		 * Enable the object of the configured object type that has the given
		 * native identifier.
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
		public Result enable(String nativeIdentifier,
				Map<String, Object> options) throws ConnectorException,
				ObjectNotFoundException {

					Result result = new Result();

					String codUsuario = "";

					codUsuario = nativeIdentifier;
					
					log.debug("ENABLE USUARIO " + "CodUsuario " + codUsuario);
					
					try {
						alterarUsuarioTabelasOracle(codUsuario, IC_STATUS_USUARIO_ATIVO);
//						alterarStatus(codUsuario, IC_STATUS_USUARIO_UNLOCK);
						
						result = new Result(Result.Status.Committed);
					
					} catch (Exception e) {
						result = new Result(Result.Status.Failed);
						result.add("TIPO APP SIASEO " + e.getMessage());
					}

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
		public Result disable(String nativeIdentifier,
				Map<String, Object> options) throws ConnectorException,
				ObjectNotFoundException {

					Result result = new Result();

					String codUsuario = "";

					codUsuario = nativeIdentifier;

					log.debug("DISABLE USUARIO " + "CodUsuario " + codUsuario);
					
					try {
					
						alterarUsuarioTabelasOracle(codUsuario, IC_STATUS_USUARIO_INATIVO);
//						alterarStatus(codUsuario, IC_STATUS_USUARIO_LOCK);
					
					result = new Result(Result.Status.Committed);

					} catch (Exception e) {
						result = new Result(Result.Status.Failed);
						result.add("TIPO APP SIASEO " + e.getMessage());
					}


					return result;
		}

		/**
		 * Unlock the object of the configured object type that has the given
		 * native identifier. An account is typically locked due to excessive
		 * invalid login attempts, whereas a disable is usually performed on
		 * accounts that are no longer in use.
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

		public Result unlock(String nativeIdentifier,
				Map<String, Object> options) throws ConnectorException,
				ObjectNotFoundException, UnsupportedOperationException {

			Result result = new Result(Result.Status.Committed);

			Map<String, Object> obj = read(nativeIdentifier);
			if (null == obj)
				{
					throw new ObjectNotFoundException(nativeIdentifier);
				}

			return result;
		}

		/**
		 * Set the password for the given object to the newPassword. The current
		 * password may be passed in, but is not typically required. If
		 * available, the current password should be passed in, since some
		 * resources require this for full behavior around password policy
		 * checking, password history, etc...
		 * 
		 * @param nativeIdentifier
		 *            The value for the identity attribute.
		 * @param newPassword
		 *            The password to set for the account.
		 * @param currentPassword
		 *            The current password of the account. This should be
		 *            provided if available, but is not always required.
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
				String currentPassword, Date expiration,
				Map<String, Object> options) throws ConnectorException,
				ObjectNotFoundException {


			log.debug("SET PASSWORD ");

			Result result = new Result();

			String codSistema = "", codUsuario = "", nomUsuario = "", icStatu = "", icType = "", codSenha = "", numPerfil = "", clearPassword = "";
			int operacao = 0;

			
			codUsuario = nativeIdentifier;
			clearPassword = newPassword;

			log.debug("SET PASSWORD " + "CodUsuario " + codUsuario);
			log.debug("NEW PASSWORD " + clearPassword);

			try
				{

					alterarSenha(codUsuario, clearPassword);
					result = new Result(Result.Status.Committed);

				} catch (SecurityException e)
				{
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());
				}

			Map<String, Object> obj = read(nativeIdentifier);
			if (null == obj)
				{
					throw new ObjectNotFoundException(nativeIdentifier);
				}

			return result;

		}

		/**
		 * Attempt to authenticate to the underlying resource using the given
		 * identity and password. The identity may be the value of the identity
		 * attribute, but could also be a value that can be used to search for
		 * the account (eg - sAMAccountName, full name, etc...). If successful,
		 * this returns the account that was authenticated to.
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
			if (null == obj)
				{
					throw new ObjectNotFoundException(identity);
				}

			// If there was a problem we would have thrown already. Return the
			// // matched object.
			return obj;
		}

		/**
		 * Using the id passed into this method to check the request to see if
		 * has completed.
		 * 
		 * This method is used to poll the system for status when things in any
		 * of the results are marked Queued and have a requestToken.
		 *
		 * The new status, returned result's errors, and warnings will be merged
		 * back into the main project.
		 * 
		 * Returning a null result will have no change on the request and it
		 * will remained queued.
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
		 * Clients MUST call this when they are done with a connector or else
		 * there may be resource leaks.
		 */
		public void close() {
			// add code to free resources like file or database connection
		}

		public void init() throws Exception {

			if (!m_bIsDriverLoaded)
				{
					m_sUser = config.getString("user");
					m_sPasswd = config.getString("password");
					m_sUrl = config.getString("url");
					m_sDriverName = config.getString("driverClass");

					Class.forName(m_sDriverName);
					if (log.isDebugEnabled())
						log.debug("driver loaded ");
					m_dbConn = DriverManager.getConnection(m_sUrl, m_sUser,
							m_sPasswd);

					m_bIsDriverLoaded = true;
				}
		}

		public Iterator<Map<String, Object>> getAccounts(String filter) {

			account = null;
			resultObjectList = null;
			resultObjectList = new ArrayList<Map<String, Object>>();

			ResultSet queryResult = null;

			log.debug("GetAccounts - FILTER " + filter);

			if (filter.equalsIgnoreCase("*"))
				{
					listarUsusario(null);

				} else
				{
					listarUsusario(filter);
				}

			try
				{
					init();

					queryResult = retornoUsuario;
					while (queryResult.next())
						{
							account = new HashMap<String, Object>();
							String userid = queryResult.getString("CO_USUARIO");
							account.put(coUsuario, userid);
							log.debug("userid " + userid);
							account.put(noUsuario,
									queryResult.getString("NO_USUARIO"));
							log.debug("NO_USUARIO "
									+ queryResult.getString("NO_USUARIO"));
							account.put(icStatus,
									queryResult.getString("IC_STATUS"));
							log.debug("icStatus "
									+ queryResult.getString("IC_STATUS"));
							account.put(icTipo,
									queryResult.getString("IC_TIPO"));
							log.debug("icTipo "
									+ queryResult.getString("IC_TIPO"));
							account.put(sisPerfil, groupsAccount(userid));

							String temp = queryResult.getString("IC_STATUS");
							if (temp.equals("1"))
								{
									account.put(
											openconnector.Connector.ATT_DISABLED,
											new Boolean(true));
								} else
								{
									account.put(
											openconnector.Connector.ATT_DISABLED,
											new Boolean(false));
								}

							resultObjectList.add(account);

							log.debug("coUsuario " + userid);
							log.debug("noUsuario "
									+ queryResult.getString("NO_USUARIO"));
							log.debug("icStatus "
									+ queryResult.getString("IC_STATUS"));
							log.debug("icTipo "
									+ queryResult.getString("IC_TIPO"));
							log.debug("sisPerfil " + groupsAccount(userid));
						}

					if (queryResult != null)
						queryResult.close();

					cs.close();
				} catch (Exception e)
				{
					if (log.isDebugEnabled())
						log.debug("Exception occured ", e);
					throw new ConnectorException(e);
				}

			return resultObjectList.iterator();
		}

		public Iterator<Map<String, Object>> getGroups(String filter) {

			groups = null;
			resultObjectList = new ArrayList<Map<String, Object>>();
			ResultSet queryResult = null;

			log.debug("getGroups - FILTER " + filter);

			if (filter.equalsIgnoreCase("*"))
				{
					listarGroups(null, null);

				} else
				{

					String[] array = filter.split("/");

					String sistema = array[0];
					String perfil = array[1];

					listarGroups(sistema, perfil);
				}

			try
				{

					init();

					queryResult = retornoGroups;

					while (queryResult.next())
						{

							groups = new HashMap<String, Object>();

							String numeroPerfil = queryResult
									.getString("NU_PERFIL");
							String codSistema = queryResult
									.getString("CO_SISTEMA");
							String nomePerfil = queryResult
									.getString("NO_PERFIL");
							String perfilComposto = codSistema + "/"
									+ nomePerfil;

							groups.put(sisPerfil, perfilComposto);
							groups.put(nuPerfil, numeroPerfil);
							groups.put(coSistema, codSistema);
							groups.put(noPerfil, nomePerfil);

							resultObjectList.add(groups);

							log.debug("numeroPerfil " + numeroPerfil);
							log.debug("codSistema " + codSistema);
							log.debug("nomePerfil " + nomePerfil);
							log.debug("perfilComposto " + perfilComposto);
						}

					if (queryResult != null)
						queryResult.close();

					cs.close();

				} catch (Exception e)
				{

					if (log.isDebugEnabled())
						log.debug("Exception occured ", e);
					throw new ConnectorException(e);
				}

			return resultObjectList.iterator();
		}

		public ArrayList<String> groupsAccount(String userName)
				throws Exception {

			log.debug("userNameGroupAccount " + userName);

			listarConexoeUsuario(userName);

			log.debug("userNameGroupAccount2 " + userName);

			ArrayList<String> listaGrupos = new ArrayList<String>();

			ResultSet rs = retornoConexaoUsuario;

			while (rs.next())
				{
					String codSistema = rs.getString("CO_SISTEMA");
					String nomePerfil = rs.getString("NO_PERFIL");
					String perfilComposto = codSistema + "/" + nomePerfil;

					listaGrupos.add(perfilComposto);
				}

			cs.close();

			log.debug("listaGrupos " + listaGrupos);

			return listaGrupos;

		}

		// conexao ao banco
		protected CallableStatement callProcedure(String procedure)
				throws SQLException, ClassNotFoundException {
			valorConexao();
			cs = null;
			cs = getConnection().prepareCall(procedure);

			return cs;
		}

		// conexao ao banco
		protected Connection getConnection() throws SQLException,
				ClassNotFoundException {
			if (connection == null)
				{
					Class.forName(DRIVE);
					connection = DriverManager.getConnection(URL, USUARIO,
							SENHA);
				}
			return connection;
		}

		// Obtem os dados para acesso ao banco
		public void valorConexao() {
			URL = config.getString("url");
			SENHA = config.getString("password");
			USUARIO = config.getString("user");
			DRIVE = config.getString("driverClass");

		}

		public void listarUsusario(String codUsuario) {

			Result result = new Result();

			try
				{
					cs = callProcedure("{call ASE.ASESP033_LISTA_USUARIOS(?, ?) }");
					cs.setString(1, codUsuario);
					cs.registerOutParameter(2, OracleTypes.CURSOR);
					cs.execute();
					retornoUsuario = (ResultSet) cs.getObject(2);
				} catch (ClassNotFoundException e)
				{
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());
				} catch (SQLException e)
				{
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());
					result.add("TIPO APP SIASEO " + e.getErrorCode());

				}
		}

		public boolean usuarioExiste(String codUsuario) {

			Result result = new Result();

			ResultSet resultado = null;

			listarUsusario(codUsuario);

			resultado = retornoUsuario;

			try
				{
					while (resultado.next())
						{

							usuarioExiste = true;

							return usuarioExiste;
						}

					cs.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());
					result.add("TIPO APP SIASEO " + e.getErrorCode());
				}

			usuarioExiste = false;

			return usuarioExiste;
		}

		public void listarGroups(String codSitema, String CodPerfil) {

			Result result = new Result();

			try
				{
					cs = callProcedure("{call ASE.ASESP036_LISTA_PERFIS(?, ?, ?) }");
					cs.registerOutParameter(1, OracleTypes.CURSOR);
					cs.setString(2, codSitema);
					cs.setString(3, CodPerfil);
					cs.execute();
					retornoGroups = (ResultSet) cs.getObject(1);
				} catch (ClassNotFoundException e)
				{
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());

				} catch (SQLException e)
				{
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());
					result.add("TIPO APP SIASEO " + e.getErrorCode());
				}

		}

		public void listarConexoeUsuario(String codUsuario) {

			Result result = new Result();

			log.debug("userName " + codUsuario);

			try
				{
					cs = callProcedure("{call ASE.ASESP037_LISTA_CONEXOES (?, ?, ?, ?) }");
					cs.registerOutParameter(1, OracleTypes.CURSOR);
					cs.setString(2, null);
					cs.setString(3, null);
					cs.setString(4, codUsuario + "%");
					cs.execute();
					retornoConexaoUsuario = (ResultSet) cs.getObject(1);
				} catch (ClassNotFoundException e)
				{
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());
				} catch (SQLException e)
				{
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());
					result.add("TIPO APP SIASEO " + e.getErrorCode());
				}

		}

		public void dadosPerfil(String codSitema, String CodPerfil) {

			Result result = new Result();

			try
				{
					cs = callProcedure("{call ASE.ASESP036_LISTA_PERFIS(?, ?, ?) }");
					cs.registerOutParameter(1, OracleTypes.CURSOR);
					cs.setString(2, codSitema);
					cs.setString(3, CodPerfil);
					cs.execute();
					retornoGroups = (ResultSet) cs.getObject(1);

					while (retornoGroups.next())
						{
							nuPerfil = retornoGroups.getString("NU_PERFIL");
						}
					cs.close();

				} catch (ClassNotFoundException e)
				{
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());
				} catch (SQLException e)
				{
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());
					result.add("TIPO APP SIASEO " + e.getErrorCode());
				}

		}

		private void criarUsuarioOracle(String coUsuario, String Senha) {
			Result result = new Result();
			try
				{
					cs = callProcedure("{call ASE.ASESP906_CREATE_USER(?, ?) }");
					cs.setString(1, coUsuario);
					cs.setString(2, Senha);
					cs.execute();
				} catch (ClassNotFoundException e)
				{
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());
				} catch (SQLException e)
				{
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());
					result.add("TIPO APP SIASEO " + e.getErrorCode());
				}
		}

		public void criarUsuarioTabelasOracle(String codUsuario) {
			Result result = new Result();
			try
				{
					cs = callProcedure("{call ASE.ASESP025_MAN_USUARIO(?, ?, ?, ?, ?, ?) }");
					cs.setString(1, INCLUIR);
					cs.setString(2, codUsuario);
					cs.registerOutParameter(3, OracleTypes.NUMBER);
					cs.registerOutParameter(4, OracleTypes.VARCHAR);
					cs.setString(5, IC_STATUS_USUARIO_ATIVO);
					cs.setString(6, null);
					cs.execute();
					cs.close();
				} catch (ClassNotFoundException e)
				{
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());
				} catch (SQLException e)
				{
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());
					result.add("TIPO APP SIASEO " + e.getErrorCode());
				}
		}

		public void createConnection(String coSistema, String coUsuario,
				String nuPerfil) {

			Result result = new Result();

			try
				{
					cs = callProcedure("{call ASE.ASESP026_MAN_SIS_USUARIO(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
					cs.setString(1, INCLUIR);
					cs.setString(2, coSistema);
					cs.setString(3, coUsuario);
					cs.setString(4, nuPerfil);
					cs.setString(5, "0");
					cs.setDate(6, null);
					cs.setString(7, IC_VISAO_NACIONAL);
					cs.setString(8, null);
					cs.registerOutParameter(9, OracleTypes.NUMBER);
					cs.registerOutParameter(10, OracleTypes.VARCHAR);
					cs.execute();
					cs.close();

				} catch (ClassNotFoundException e)
				{
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());
				} catch (SQLException e)
				{
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());
					result.add("TIPO APP SIASEO " + e.getErrorCode());
				}

		}

		public void deletarConnectionUsuario(String coSistema,
				String coUsuario, String nuPerfil) {
			Result result = new Result();

			try
				{
					cs = callProcedure("{call ASE.ASESP026_MAN_SIS_USUARIO(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
					cs.setString(1, EXCLUIR);
					cs.setString(2, coSistema);
					cs.setString(3, coUsuario);
					cs.setString(4, nuPerfil);
					cs.setString(5, null);
					cs.setString(6, null);
					cs.setString(7, null);
					cs.setString(8, null);
					cs.registerOutParameter(9, OracleTypes.NUMBER);
					cs.registerOutParameter(10, OracleTypes.VARCHAR);
					cs.execute();
					cs.close();

				} catch (ClassNotFoundException e)
				{
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());
				} catch (SQLException e)
				{
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());
					result.add("TIPO APP SIASEO " + e.getErrorCode());
				}

		}

		public void listaConexoesDelete(String coUsuario) {
			Result result = new Result();
			try
				{
					cs = callProcedure("{call ASE.ASESP037_LISTA_CONEXOES (?, ?, ?, ?) }");
					cs.registerOutParameter(1, OracleTypes.CURSOR);
					cs.setString(2, null);
					cs.setString(3, null);
					cs.setString(4, coUsuario + "%");
					cs.execute();

					resultListaUsuarioDelete = (ResultSet) cs.getObject(1);
				} catch (ClassNotFoundException e)
				{
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());
				} catch (SQLException e)
				{
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());
					result.add("TIPO APP SIASEO " + e.getErrorCode());
				}
		}

		private void deletarUsuarioTabelasOracle(String coUsuario) {
			Result result = new Result();

			try
				{
					cs = callProcedure("{call ASE.ASESP025_MAN_USUARIO(?, ?, ?, ?, ?, ?) }");
					cs.setString(1, EXCLUIR);
					cs.setString(2, coUsuario);
					cs.registerOutParameter(3, OracleTypes.NUMBER);
					cs.registerOutParameter(4, OracleTypes.VARCHAR);
					cs.setString(5, null);
					cs.setString(6, null);
					cs.execute();

				} catch (ClassNotFoundException e)
				{
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());
				} catch (SQLException e)
				{
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());
					result.add("TIPO APP SIASEO " + e.getErrorCode());
				}

		}

		private void deletarUsuarioOracle(String coUsuario) {
			Result result = new Result();

			try
				{
					cs = callProcedure("{call ASE.ASESP910_DROP_USER(?) }");
					cs.setString(1, coUsuario);
					cs.execute();

				} catch (ClassNotFoundException e)
				{
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());
				} catch (SQLException e)
				{
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());
					result.add("TIPO APP SIASEO " + e.getErrorCode());
				}
		}

		public void alterarUsuarioTabelasOracle(String coUsuario,
				String icStatus) {
			Result result = new Result();

			try
				{
					cs = callProcedure("{call ASE.ASESP025_MAN_USUARIO(?, ?, ?, ?, ?, ?) }");
					cs.setString(1, ALTERAR);
					cs.setString(2, coUsuario);
					cs.registerOutParameter(3, OracleTypes.NUMBER);
					cs.registerOutParameter(4, OracleTypes.VARCHAR);
					cs.setString(5, icStatus);
					cs.setString(6, null);
					cs.execute();
					cs.close();
				} catch (ClassNotFoundException e)
				{
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());
				} catch (SQLException e)
				{
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());
					result.add("TIPO APP SIASEO " + e.getErrorCode());
				}
		}

		private void alterarSenha(String coUsuario, String senhaLimpa) {

			log.debug("Procedure Alterar Senha " + "CodUsuario " + coUsuario
					+ " Senha " + senhaLimpa);

			Result result = new Result();
			try
				{
					cs = callProcedure("{call ASE.ASESP911_PASSWORD(?, ?, ?) }");
					cs.setString(1, coUsuario);
					cs.setString(2, senhaLimpa);
					cs.setString(3, NAO);
					cs.execute();
					cs.close();
				} catch (ClassNotFoundException e)
				{
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());

				} catch (SQLException e)
				{
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("TIPO APP SIASEO " + e.getMessage());
					result.add("TIPO APP SIASEO " + e.getErrorCode());
				}

		}

	}
