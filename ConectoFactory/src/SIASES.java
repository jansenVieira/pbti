import static org.jinterop.dcom.impls.automation.IJIDispatch.IID;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.JIObjectFactory;
import org.jinterop.dcom.impls.automation.IJIDispatch;

/*

 This file  provides a skeletal structure of the openconnector interfaces.
 It is the responsiblity of the connector developer to implement the methods.

 */
public class SIASES extends AbstractConnector {

	// ////////////////////////////////////////////
	// ////////////////////////////
	//
	// INNER CLASS
	//
	// //////////////////////////////////////////////////////////////////////////

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

	// Retorno Lista
	public ResultSet retornoUsuario;
	public ResultSet retornoPerfil;
	public ResultSet retornoConexao;

	private String m_sDriverName;
	private String m_sUser;
	private String m_sPasswd;
	private String m_sUrl;
	private String sqlAccount;
	private boolean m_bIsDriverLoaded;
	private Connection m_dbConn;

	// Drive connect DLL
	private static String urlServerCripto;
	private static String userServerCripto;
	private static String senhaServerCripto;
	private static String nameDLL;
	private static String dominioServerCripto;
	private static String keyCripto;

	// Cripto
	public static JIComServer comServer;
	public static JISession session;
	public static IJIDispatch comLocator;
	public static String passowrdCriptografada;

	// Constates
	public static String IC_STATUS_ACESSO;

	// Variavel Result
	public ResultSet queryResult = null;
	public ResultSet queryResultGrupo = null;

	// Part Variaveis
	private String m_sLastUser;
	private String m_sLastGroup;
	public static final String EMPTY_STR = " ";
	private Filter _filter;
	private int m_iChunkSize = 10;
	private Map<String, Map<String, Object>> m_acctsMap = new HashMap<String, Map<String, Object>>();
	private Map<String, Map<String, Object>> m_groupsMap = new HashMap<String, Map<String, Object>>();
	private int contador = 0;

	private boolean isDebug;

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

			if (it.hasNext() == false) {
				it = IterateNextPage(null);
				boolean hasNext = it.hasNext();

				return hasNext;
			} else {
				return it.hasNext();
			}
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
	public SIASES() {
		super();

		m_iChunkSize = 10;
		m_sLastUser = EMPTY_STR;
		m_sLastGroup = EMPTY_STR;
		m_bIsDriverLoaded = false;
		it = null;

		if (isDebug == true) {
			log.debug("Construtor: <valor m_iChunkSize: " + m_iChunkSize
					+ "> <valor m_sLastUser: " + m_sLastUser
					+ "> <m_sLastGroup: " + m_sLastGroup);
		}

	}

	/**
	 * Constructor for an OpenConnectorSample.
	 * 
	 * @param config
	 *            The ConnectorConfig to use.
	 * @param log
	 *            The Log to use.
	 */
	public SIASES(ConnectorConfig config, openconnector.Log log)
			throws Exception {
		super(config, log);
		m_iChunkSize = 10;
		m_sLastUser = EMPTY_STR;
		m_sLastGroup = EMPTY_STR;
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
				log.error("Erro teste Conexao ", e);
			throw new ConnectorException("Falha no teste de conexao : "
					+ e.getMessage() + ".  Check os detalhes da conexao.");
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
	// if (OBJECT_TYPE_GROUP.equals(this.objectType))
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

		debug();

		if (isDebug == true) {
			log.debug("===== READ ====");
		}

		Map<String, Object> acct = null;
		Map<String, Object> grp = null;

		ResultSet queryResultRead = null;
		Statement stmtRead = null;

		try {

			String query = null;

			if (OBJECT_TYPE_ACCOUNT.equals(this.objectType)) {

				query = "select distinct a.CO_USUARIO, a.NO_USUARIO, a.IC_STATUS, a.NU_MATR, a.IC_TIPO, b.CO_SISTEMA, b.NU_PERFIL, b.NO_PERFIL"
						+ " from ASEDB001.dbo.ASEVW021_USUARIO_SAILPOINT a, ASEDB001.dbo.ASEVW022_USUARIO_CONEXAO_SAILP b"
						+ " where a.CO_USUARIO = '"
						+ nativeIdentifier
						+ "' and a.CO_USUARIO = b.CO_USUARIO";

				stmtRead = m_dbConn.createStatement();

				if (isDebug == true) {
					log.debug("READ Account: <query: " + query
							+ ">\n <FILTER: " + nativeIdentifier + ">");
				}

				queryResultRead = stmtRead.executeQuery(query);

				ArrayList<String> arraySisperfil = new ArrayList<String>();

				while (queryResultRead.next()) {

					String userid = "";

					acct = new HashMap<String, Object>();

					userid = queryResultRead.getString("CO_USUARIO");

					if (userid != null) {
						acct.put(coUsuario, userid);
					} else {
						acct.put(coUsuario, "-");
					}

					String noUsuario = queryResultRead.getString("NO_USUARIO");

					noUsuario = noUsuario.trim();
					if (noUsuario != null) {
						acct.put(this.noUsuario, noUsuario);
					} else {
						acct.put(this.noUsuario, "-");
					}

					String icStatus = queryResultRead.getString("IC_STATUS");

					if (icStatus != null) {
						acct.put(icStatus, icStatus);
					} else {
						acct.put(icStatus, "-");
					}

					String nuMatr = queryResultRead.getString("NU_MATR");
					if (nuMatr != null) {
						acct.put(nuMatricula, nuMatr);
					} else {
						acct.put(nuMatricula, "-");
					}

					String icTipo = queryResultRead.getString("IC_TIPO");
					if (icTipo != null) {
						acct.put(this.icTipo, icTipo);
					} else {
						acct.put(this.icTipo, "-");
					}

					String noPerfil = queryResultRead.getString("NO_PERFIL");
					String noSistema = queryResultRead.getString("CO_SISTEMA");

					if (noPerfil != null && noSistema != null) {

						String sistemaPerfil = noSistema.trim() + "/"
								+ noPerfil.trim();

						arraySisperfil.add(sistemaPerfil);
					}

					acct.put(sisPerfil, arraySisperfil);

					String temp = queryResultRead.getString("IC_STATUS");

					if (temp.equals("1")) {
						acct.put(openconnector.Connector.ATT_DISABLED,
								new Boolean(true));
					} else {
						acct.put(openconnector.Connector.ATT_DISABLED,
								new Boolean(false));
					}

					if (isDebug == true) {
						log.debug("READ Account MAP: <acct" + acct.toString()
								+ ">");
					}

				}
			} else if (OBJECT_TYPE_GROUP.equals(this.objectType)) {

				String seperaGrupo = nativeIdentifier.toString();

				String sistema = "", perfil = "";

				String[] separa = seperaGrupo.split("/");
				sistema = separa[0].toString();
				perfil = separa[1].toString();

				query = "select distinct NU_PERFIL, CO_SISTEMA, NO_PERFIL  from ASEDB001.dbo.ASEVW022_USUARIO_CONEXAO_SAILP where CO_SISTEMA='"
						+ sistema + "' AND NO_PERFIL='" + perfil + "'";

				stmtRead = m_dbConn.createStatement();
				if (isDebug == true) {
					log.debug("READ Group : <query" + query + ">");
				}

				queryResultRead = stmtRead.executeQuery(query);

				while (queryResultRead.next()) {
					grp = new HashMap<String, Object>();

					String numeroPerfil = queryResultRead
							.getString("NU_PERFIL").trim();
					String codSistema = queryResultRead.getString("CO_SISTEMA")
							.trim();
					String nomePerfil = queryResultRead.getString("NO_PERFIL")
							.trim();
					String perfilComposto = codSistema.trim() + "/"
							+ nomePerfil.trim();

					grp.put(sisPerfil, perfilComposto);
					grp.put(nuPerfil, numeroPerfil);
					grp.put(coSistema, codSistema);
					grp.put(noPerfil, nomePerfil);

					if (isDebug == true) {
						log.debug("m_groupsMap <" + m_groupsMap.toString()
								+ ">");
					}

				}

			}

			if (queryResultRead != null)
				queryResultRead.close();

			if (stmtRead != null)
				stmtRead.close();

		} catch (Exception e) {
			if (isDebug == true) {
				log.debug("Exception occured READ " + e + ">");
			}
			throw new ConnectorException("ERRO AO LER O USUARIO READ <"
					+ nativeIdentifier + ">");
		}

		if (OBJECT_TYPE_ACCOUNT.equals(this.objectType))
			return acct;
		else if (OBJECT_TYPE_GROUP.equals(this.objectType))
			return grp;
		else
			throw new ConnectorException("Unhandled object type: "
					+ this.objectType);
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

	public Iterator<Map<String, Object>> iterate(Filter filter)
			throws ConnectorException, UnsupportedOperationException {

		debug();

		if (isDebug == true) {
			log.debug("===== Iterate ====");
		}

		it = null;

		if (isDebug == true) {
			log.debug("Iterator " + filter + ">");
		}

		try {
			_filter = filter;
			it = new ArrayList<Map<String, Object>>(getObjectsMap().values())
					.iterator();
		} catch (Exception e) {
			if (isDebug == true) {
				log.debug("Exception occured Interator" + e + ">");
			}
			throw new ConnectorException("ERRO AO LER AO UTILIZAR O INTERATE");
		}

		return new PagingIterator(it);

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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Result create(String nativeIdentifier, List<Item> items)
			throws ConnectorException, ObjectAlreadyExistsException {

		debug();

		if (isDebug == true) {
			log.debug("===== CREATE ====");
		}

		Result result = new Result();

		try {

			String codSistema = "", codUsuario = "", nomUsuario = "", icStatu = "", icType = "", codSenha = "", numPerfil = "", passwordLimpo = "", sistemaPerfis = "";
			int operacao = 0;
			ArrayList<String> arrayPerfilSistema = new ArrayList<String>();

			codUsuario = nativeIdentifier;

			IC_STATUS_ACESSO = "0";

			if (isDebug == true) {
				log.debug("CREATE <codUsuario: " + codUsuario + ">");
			}

			for (openconnector.Item item : items) {

				String name = item.getName();
				String value = item.getValue().toString();

				if (name.equalsIgnoreCase(noUsuario)) {

					nomUsuario = value;
				}

				if (name.equalsIgnoreCase(coSenhaAcesso)) {

					passwordLimpo = value;

					encripta(passwordLimpo);

					codSenha = passowrdCriptografada;
				}

				if (name.equalsIgnoreCase(this.icTipo)) {
					icType = value;
				}

				if (name.equalsIgnoreCase(sisPerfil)) {

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
									result.add("TIPO APP SIASES Create"
											+ "O sistema não permite adicionar mais de um perfil.");

									if (isDebug == true) {
										log.debug("O sistema não permite adicionar mais de um perfil.");
									}

									throw new ConnectorException(
											"CREATE - O sistema não permite adicionar mais de um perfil");
								}
							}

							arrayPerfilSistema.add(sitemPerf);
						}
					} else {

						sistemaPerfis = valueList.get(0).toString();

						arrayPerfilSistema.add(sistemaPerfis);

					}

				}
			}

			icStatu = "0";
			operacao = 1;

			for (String sistemPerfis : arrayPerfilSistema) {

				String[] array = sistemPerfis.split("/");

				String sistema = array[0];
				String perfil = array[1];

				try {
					dadosPerfil(sistema, perfil);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					result = new Result(Result.Status.Failed);
					result.add("ERRO SIASE CREATE");

					if (isDebug == true) {
						log.debug("ERRO CREATE SIASE USER: " + codUsuario);
					}

					throw new ConnectorException(
							"CREATE - ERRO CREATE USUARIO " + e);

				} catch (SQLException e) {
					e.printStackTrace();
					result = new Result(Result.Status.Failed);

					if (isDebug == true) {
						log.debug("ERRO CREATE SIASE USER: " + codUsuario);
					}

					throw new ConnectorException(
							"CREATE - ERRO CREATE USUARIO " + e);
				}

				numPerfil = nuPerfil;
				codSistema = sistema;

				if (isDebug == true) {
					log.debug("Create " + "<CodUsuario: " + codUsuario
							+ "> <CodSistema: " + codSistema
							+ "> <nomUsuario: " + nomUsuario + "> <icStatu: "
							+ icStatu + "> <icType: " + icType
							+ "> <codSenha: " + codSenha + "> <numPerfil: "
							+ numPerfil + "> <operacao> " + operacao + ">");

				}

				createUser(codSistema, codUsuario, nomUsuario, icStatu, icType,
						codSenha, numPerfil, operacao);

				result = new Result(Result.Status.Committed);
			}

		} catch (Exception e) {
			result = new Result(Result.Status.Failed);
			result.add("TIPO APP SIASES CREATE ");

			if (isDebug == true) {
				log.debug("Exception occured CREATE ", e);
			}
			String message = "Exception occured while creating the entity";
			result.add(message);

			if (isDebug == true) {
				log.debug("ERRO CREATE SIASE " + e);
			}

			throw new ConnectorException("CREATE - ERRO CREATE USUARIO " + e);
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Result update(String nativeIdentifier, List<Item> items)
			throws ConnectorException, ObjectNotFoundException {

		debug();

		if (isDebug == true) {
			log.debug("===== UPDATE ====");
		}

		Result result = new Result();

		String codSistema = "", codUsuario = "", nomUsuario = "", icStatu = "", icType = "", codSenha = "", numPerfil = "";
		int operacao = 0;
		ArrayList<String> arrayPerfilSistema = new ArrayList<String>();

		codUsuario = nativeIdentifier;

		IC_STATUS_ACESSO = "0";

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

							if (sistema.equals(sistema2) && op.equals("Add")) {
								result = new Result(Result.Status.Failed);
								result.add("TIPO APP SIASES UPDATE"
										+ "O sistema não permite adicionar mais de um perfil.");
								return result;
							}
						}
					}

				}

				if (isDebug == true) {
					log.debug("UPDATE - <Nome Item: " + name
							+ ">\n <Valor Item: " + valueList.get(0).toString()
							+ ">\n <Operation: " + op + ">");
				}

				switch (op) {

				case Add: {

					try {
						if (name.equalsIgnoreCase(sisPerfil)) {

							for (Object sistemaPerfil : valueList) {
								String sistemaPerfis = sistemaPerfil.toString();

								arrayPerfilSistema.add(sistemaPerfis);
							}

						}

						dadosUsuario(codUsuario);
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
						result = new Result(Result.Status.Failed);
						result.add("TIPO APP SIASES UPDATE ADD");

						if (isDebug == true) {
							log.debug("ERRO UPDATE ADD SIASE USER: "
									+ codUsuario);
						}

						throw new ConnectorException(
								"ERRO UPDATE ADD SIASE USER: " + e1);

					} catch (SQLException e1) {
						e1.printStackTrace();
						result = new Result(Result.Status.Failed);
						result.add("TIPO APP SIASES UPDATE");

						if (isDebug == true) {
							log.debug("ERRO UPDATE ADD SIASE USER: "
									+ codUsuario);
						}

						throw new ConnectorException(
								"ERRO UPDATE ADD SIASE USER: " + e1);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();

						if (isDebug == true) {
							log.debug("ERRO UPDATE ADD SIASE USER: "
									+ codUsuario);
						}

						throw new ConnectorException(
								"ERRO UPDATE ADD SIASE USER: " + e);

					}

					nomUsuario = noUsuario;
					icStatu = icStatus;
					icType = icTipo;
					codSenha = coSenhaAcesso;
					operacao = 1;

					for (String sistemaPerfis : arrayPerfilSistema) {

						String[] array = sistemaPerfis.split("/");

						String sistema = array[0];
						String perfil = array[1];

						try {
							dadosPerfil(sistema, perfil);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
							result = new Result(Result.Status.Failed);
							result.add("TIPO APP SIASES UPDATE" + e);
							if (isDebug == true) {
								log.debug("ERRO UPDATE ADD SIASE USER: "
										+ codUsuario);
							}

							throw new ConnectorException(
									"ERRO UPDATE ADD SIASE USER: " + e);

						} catch (SQLException e) {
							e.printStackTrace();
							result = new Result(Result.Status.Failed);
							result.add("TIPO APP SIASES UPDATE" + e);
							if (isDebug == true) {
								log.debug("ERRO UPDATE ADD SIASE USER: "
										+ codUsuario);
							}

							throw new ConnectorException(
									"ERRO UPDATE ADD SIASE USER: " + e);

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();

							if (isDebug == true) {
								log.debug("ERRO UPDATE ADD SIASE USER: "
										+ codUsuario);
							}

							throw new ConnectorException(
									"ERRO UPDATE ADD SIASE USER: " + e);
						}

						numPerfil = nuPerfil;
						codSistema = sistema;

						try {
							if (isDebug == true) {
								log.debug("UPDADE ADD - <CodUsuario: "
										+ codUsuario + "> <CodSistema: "
										+ codSistema + "> <nomUsuario: "
										+ nomUsuario + "> <icStatu: " + icStatu
										+ "> <icType: " + icType
										+ "> <codSenha: " + codSenha
										+ "> <numPerfil: " + numPerfil
										+ "> <operacao: " + operacao + ">");
							}

							createUser(codSistema, codUsuario, nomUsuario,
									icStatu, icType, codSenha, numPerfil,
									operacao);

							result = new Result(Result.Status.Committed);
						} catch (Exception e) {
							e.printStackTrace();
							result = new Result(Result.Status.Failed);
							result.add("TIPO APP SIASES UPDATE" + e);

							if (isDebug == true) {
								log.debug("ERRO UPDATE ADD SIASE USER: "
										+ codUsuario);
							}

							throw new ConnectorException(
									"ERRO UPDATE ADD SIASE USER: " + e);
						}
					}
				}

					break;

				case Remove: {

					if (isDebug == true) {
						log.debug("UPDATE - REMOVE");

					}
					if (name.equalsIgnoreCase(sisPerfil)) {

						for (Object sistemaPerfil : valueList) {
							String sistemaPerfis = sistemaPerfil.toString();

							arrayPerfilSistema.add(sistemaPerfis);
						}

						try {

							for (String sistemaPerfis : arrayPerfilSistema) {

								String[] array = sistemaPerfis.split("/");

								String sistema = array[0];

								codSistema = sistema;

								if (isDebug == true) {
									log.debug("UPDADE - REMOVER <"
											+ "CodUsuario: " + codUsuario
											+ "> <CodSistema: " + codSistema
											+ ">");

								}

								desvincularGrupo(codSistema, codUsuario);

								result = new Result(Result.Status.Committed);

							}
						} catch (Exception e) {
							e.printStackTrace();
							result = new Result(Result.Status.Failed);
							result.add("TIPO APP SIASES " + e);

							if (isDebug == true) {
								log.debug("ERRO UPDATE REMOVE SIASE USER: "
										+ codUsuario);
							}

							throw new ConnectorException(
									"ERRO UPDATE REMOVE SIASE USER: " + e);
						}
					}

				}
					break;
				case Set: {

					// existing.put(name, valueList.get(0).toString());
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

		debug();

		if (isDebug == true) {
			log.debug("===== DELETE ====");
		}

		Result result = new Result();

		try {

			String codUsuario = "";

			codUsuario = nativeIdentifier;

			if (isDebug == true) {
				log.debug("DELETE " + "<CodUsuario " + codUsuario + ">");
			}

			deletaUser(codUsuario);
			result = new Result(Result.Status.Committed);
		} catch (Exception e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			result.add("TIPO APP SIASES " + e);

			if (isDebug == true) {
				log.debug("ERRO DELETE SIASE USER: " + e);
			}

			throw new ConnectorException("ERRO DELETE SIASE USER: " + e);
		}

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

		debug();

		if (isDebug == true) {
			log.debug("===== ENABLE ====");
		}

		Result result = new Result();

		try {

			String codSistema = "", codUsuario = "", nomUsuario = "", icStatu = "", icType = "", codSenha = "", numPerfil = "";
			int operacao = 0;

			codUsuario = nativeIdentifier;

			IC_STATUS_ACESSO = "0";

			if (isDebug == true) {
				log.debug("ENABLE USUARIO " + "<CodUsuario " + codUsuario + ">");
			}

			dadosConexoes(codUsuario);

			numPerfil = nuPerfil;
			codSistema = coSistema;

			dadosUsuario(codUsuario);

			nomUsuario = noUsuario;
			icStatu = "0";
			icType = icTipo;
			operacao = 2;

			if (isDebug == true) {
				log.debug("ENABLE" + "<CodUsuario: " + codUsuario
						+ "> <CodSistema: " + codSistema + "> <nomUsuario: "
						+ nomUsuario + "> <icStatu: " + icStatu + "> <icType: "
						+ icType + "> <codSenha: " + codSenha
						+ "> <numPerfil: " + numPerfil + "> <operacao: "
						+ operacao + ">");
			}

			createUser(codSistema, codUsuario, nomUsuario, icStatu, icType,
					codSenha, numPerfil, operacao);

			result = new Result(Result.Status.Committed);
		} catch (Exception e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			result.add("TIPO APP SIASES ENABLE " + e);

			if (isDebug == true) {
				log.debug("ERRO ENABLE SIASE USER: " + e);
			}

			throw new ConnectorException("ERRO ENABLE SIASE USER: " + e);
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
	public Result disable(String nativeIdentifier, Map<String, Object> options)
			throws ConnectorException, ObjectNotFoundException {

		debug();

		if (isDebug == true) {
			log.debug("===== DISABLE ====");
		}

		Result result = new Result();

		try {

			String codSistema = "", codUsuario = "", nomUsuario = "", icStatu = "", icType = "", codSenha = "", numPerfil = "";
			int operacao = 0;

			codUsuario = nativeIdentifier;

			IC_STATUS_ACESSO = "1";

			if (isDebug == true) {
				log.debug("DISABLE" + "<CodUsuario: " + codUsuario + ">");
			}

			dadosConexoes(codUsuario);

			numPerfil = nuPerfil;
			codSistema = coSistema;

			dadosUsuario(codUsuario);

			nomUsuario = noUsuario;
			icStatu = "1";
			icType = icTipo;
			operacao = 2;

			if (isDebug == true) {
				log.debug("DISABLE " + "<CodUsuario: " + codUsuario
						+ "> <CodSistema: " + codSistema + "> <nomUsuario: "
						+ nomUsuario + "> <icStatu: " + icStatu + "> <icType: "
						+ icType + "> <codSenha: " + codSenha
						+ "> <numPerfil: " + numPerfil + "> <operacao: "
						+ operacao + ">");
			}

			createUser(codSistema, codUsuario, nomUsuario, icStatu, icType,
					codSenha, numPerfil, operacao);

			result = new Result(Result.Status.Committed);
		} catch (Exception e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			result.add("TIPO APP SIASES DISABLE" + e);

			if (isDebug == true) {
				log.debug("ERRO DISABLE SIASE " + e);
			}

			throw new ConnectorException("ERRO DISABEL SIASE " + e);

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

	@SuppressWarnings("static-access")
	public Result setPassword(String nativeIdentifier, String newPassword,
			String currentPassword, Date expiration, Map<String, Object> options)
			throws ConnectorException, ObjectNotFoundException {

		debug();

		if (isDebug == true) {
			log.debug("===== SETPASSWORD ====");
		}

		Result result = new Result();

		try {

			String codSistema = "", codUsuario = "", nomUsuario = "", icStatu = "", icType = "", codSenha = "", numPerfil = "", clearPassword = "";
			int operacao = 0;

			clearPassword = newPassword;

			IC_STATUS_ACESSO = "0";

			encripta(clearPassword);

			codSenha = passowrdCriptografada;
			codUsuario = nativeIdentifier;

			// session.destroySession(session);

			dadosConexoes(codUsuario);

			numPerfil = nuPerfil;
			codSistema = coSistema;

			dadosUsuario(codUsuario);

			nomUsuario = noUsuario;
			icStatu = "0";
			icType = icTipo;
			operacao = 2;

			if (isDebug == true) {
				log.debug("SET PASSWORD" + "<CodUsuario: " + codUsuario
						+ "> <CodSistema: " + codSistema + "> <nomUsuario: "
						+ nomUsuario + "> <icStatu: " + icStatu + "> <icType: "
						+ icType + "> <SenhaCripto: " + codSenha
						+ "> <numPerfil: " + numPerfil + "> <operacao: "
						+ operacao + ">");
			}

			createUser(codSistema, codUsuario, nomUsuario, icStatu, icType,
					codSenha, numPerfil, operacao);

			result = new Result(Result.Status.Committed);
		} catch (Exception e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			result.add("TIPO APP SIASES SET PASSWORD " + e.getMessage());
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

		// If there was a problem we would have thrown already. Return the
		// //
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

		debug();

		if (isDebug == true) {
			log.debug("=== CLOSE === ");
		}

		try {

			if (connection != null) {

				log.debug("Antes Connection Close");
				connection.close();
				log.debug("Pos Connection Close");
			}

			if (m_dbConn != null) {
				m_dbConn.close();
			}

			if (isDebug == true) {
				log.debug("Connection Close");

			}

		} catch (SQLException e) {
			log.debug("ERRO Connection Close");
			e.printStackTrace();
		}

		connection = null;
		m_dbConn = null;

	}

	public void init() throws Exception {

		m_sUser = config.getString("user");
		m_sPasswd = config.getString("password");
		m_sUrl = config.getString("url");
		m_sDriverName = config.getString("driverClass");

		urlServerCripto = config.getString("urlServerCripto");
		userServerCripto = config.getString("userServerCripto");
		senhaServerCripto = config.getString("senhaServerCripto");
		nameDLL = config.getString("nameDLL");
		dominioServerCripto = config.getString("dominioServerCripto");
		keyCripto = config.getString("keyCripto");

		debug();
		if (isDebug == true) {
			log.debug("INIT " + "<user " + m_sUser + "> <m_sUrl " + m_sUrl
					+ "> <m_sDriverName " + m_sDriverName
					+ "> <urlServerCripto " + urlServerCripto
					+ "> <userServerCripto " + userServerCripto + "> <nameDLL "
					+ nameDLL + "> <dominioServerCripto " + dominioServerCripto
					+ "> <keyCripto " + keyCripto + ">");
		}

		if (m_dbConn == null) {
			Class.forName(m_sDriverName);

			m_dbConn = DriverManager.getConnection(m_sUrl, m_sUser, m_sPasswd);

			m_bIsDriverLoaded = true;
		}
	}

	public void debug() {
		boolean debug = config.getBoolean("debug");

		if (debug == true) {

			isDebug = true;
		} else {

			isDebug = false;
		}

	}

	public Iterator<Map<String, Object>> IterateNextPage(Filter filter) {
		try {

			debug();

			if (isDebug == true) {
				log.debug("=== IterateNextPage === ");
			}

			if (OBJECT_TYPE_ACCOUNT.equals(this.objectType)) {

				if (m_sLastUser.equals(EMPTY_STR)) {
					if (isDebug) {
						log.debug("end of user iteration...");
					}
				} else {

					it = (getObjectsMap().values()).iterator();
				}

			} else if (OBJECT_TYPE_GROUP.equals(this.objectType)) {
				if (m_sLastGroup.equals(EMPTY_STR)) {
					if (isDebug) {
						log.debug("end of group iteration...");
					}
				} else {

					it = (getObjectsMap().values()).iterator();
				}
			}
		} catch (Exception e) {
			if (isDebug) {
				log.debug("Exception occured ", e);
			}
		}
		return it;
	}

	ArrayList<String> arraySisperfil = new ArrayList<String>();
	String chaveMapa = "";

	private Map<String, Map<String, Object>> getObjectsMap() throws Exception {

		debug();

		if (isDebug == true) {
			log.debug("=== Get Object Map === ");
		}

		Filter filter = _filter;
		Statement stmt = null;
		String query = null;
		Map<String, Object> acct = null;
		Map<String, Object> grp = null;

		m_acctsMap.clear();
		m_groupsMap.clear();

		try {
			if (OBJECT_TYPE_ACCOUNT.equals(this.objectType)) {

				if (isDebug == true) {
					log.debug("Get Objects Map Account....");
					log.debug("Account Filter <" + filter + ">");
				}

				if (filter != null) {

					log.debug("Get Objects Map Account FILTER NULL");

					query = "select distinct a.CO_USUARIO, a.NO_USUARIO, a.IC_STATUS, a.NU_MATR, a.IC_TIPO, b.CO_SISTEMA, b.NU_PERFIL, b.NO_PERFIL"
							+ " from ASEDB001.dbo.ASEVW021_USUARIO_SAILPOINT a, ASEDB001.dbo.ASEVW022_USUARIO_CONEXAO_SAILP b"
							+ " where a.CO_USUARIO = '"
							+ filter
							+ "' and a.CO_USUARIO = b.CO_USUARIO";

					stmt = m_dbConn.createStatement();

					if (isDebug == true) {
						log.debug("Query <" + filter + ">");
					}

					queryResult = stmt.executeQuery(query);

					ArrayList<String> sisperfil = new ArrayList<String>();

					while (queryResult.next()) {

						String userid = "";

						if ((acct != null)
								&& (acct.get(coUsuario).equals(queryResult
										.getString("CO_USUARIO")))) {

							String noPerfil = queryResult
									.getString("NO_PERFIL");
							String noSistema = queryResult
									.getString("CO_SISTEMA");

							if (noPerfil != null && noSistema != null) {

								String sistemaPerfil = noSistema.trim() + "/"
										+ noPerfil.trim();

								arraySisperfil.add(sistemaPerfil);
							}

						} else {

							if ((acct != null)) {

								acct.put(sisPerfil, arraySisperfil);

								m_acctsMap.put(chaveMapa, acct);

								if (isDebug == true) {
									log.debug("M_acctsMap <" + m_acctsMap + ">");
								}
							}

							acct = new HashMap<String, Object>();

							arraySisperfil = new ArrayList<String>();

							userid = queryResult.getString("CO_USUARIO");
							chaveMapa = userid;

							if (userid != null) {
								acct.put(coUsuario, userid);
							} else {
								acct.put(coUsuario, "");
							}

							String noUsuario = queryResult
									.getString("NO_USUARIO");
							noUsuario = noUsuario.trim();
							if (noUsuario != null) {
								acct.put(this.noUsuario, noUsuario);
							} else {
								acct.put(this.noUsuario, "-");
							}

							String codUnidade = queryResult
									.getString("IC_STATUS");
							if (codUnidade != null) {
								acct.put(icStatus, codUnidade);
							} else {
								acct.put(icStatus, "-");
							}

							String cdSenha = queryResult.getString("NU_MATR");
							if (cdSenha != null) {
								acct.put(nuMatricula, cdSenha);
							} else {
								acct.put(nuMatricula, "-");
							}

							String cdSisDefaul = queryResult
									.getString("IC_TIPO");
							if (cdSisDefaul != null) {
								acct.put(icTipo, cdSisDefaul);
							} else {
								acct.put(icTipo, "-");
							}

							String noPerfil = queryResult
									.getString("NO_PERFIL");
							String noSistema = queryResult
									.getString("CO_SISTEMA");

							if (noPerfil != null && noSistema != null) {

								String sistemaPerfil = noSistema.trim() + "/"
										+ noPerfil.trim();

								arraySisperfil.add(sistemaPerfil);
							}

							acct.put(sisPerfil, arraySisperfil);

							String temp = queryResult.getString("IC_STATUS");

							if (temp.equals("1")) {
								account.put(
										openconnector.Connector.ATT_DISABLED,
										new Boolean(true));
							} else {
								account.put(
										openconnector.Connector.ATT_DISABLED,
										new Boolean(false));
							}

						}
					}
					m_sLastUser = EMPTY_STR;
					return m_acctsMap;

				}

				if (m_sLastUser.equals(EMPTY_STR)) {

					if (isDebug == true) {
						log.debug("First fetch ... ");
					}

				} else {

					if (isDebug == true) {
						log.debug("subsequent fecth of users");
					}
				}

				if (queryResult == null) {

					query = "select distinct a.CO_USUARIO, a.NO_USUARIO, a.IC_STATUS, a.NU_MATR, a.IC_TIPO, b.CO_SISTEMA, b.NU_PERFIL, b.NO_PERFIL"
							+ " from ASEDB001.dbo.ASEVW021_USUARIO_SAILPOINT a, ASEDB001.dbo.ASEVW022_USUARIO_CONEXAO_SAILP b"
							+ " where a.CO_USUARIO != null and a.CO_USUARIO = b.CO_USUARIO";

					stmt = m_dbConn.createStatement();

					if (isDebug == true) {
						log.debug("Query <" + query + ">");
					}

					queryResult = stmt.executeQuery(query);

				}

				int fetched = 0;

				acct = acctValor;

				while (queryResult.next()) {

					String userid = "";

					if (acct != null
							&& acct.get(coUsuario)
									.toString()
									.trim()
									.equals(queryResult.getString("CO_USUARIO")
											.toString().trim())) {

						String noPerfil = queryResult.getString("NO_PERFIL");
						String noSistema = queryResult.getString("CO_SISTEMA");

						if (noPerfil != null && noSistema != null) {

							String sistemaPerfil = noSistema.trim() + "/"
									+ noPerfil.trim();

							arraySisperfil.add(sistemaPerfil);

							queryResult.rowDeleted();

							continue;
						}

					} else {

						if ((acct != null)) {

							acct.put(sisPerfil, arraySisperfil);

							m_acctsMap.put(chaveMapa, acct);

							fetched = fetched + 1;

							arraySisperfil = new ArrayList<String>();

							if (isDebug == true) {
								log.debug("Fetched " + fetched);
								log.debug("M_IChunkSize " + m_iChunkSize);
								log.debug("Quantidade " + contador);
							}

							if (fetched >= m_iChunkSize) {
								m_sLastUser = userid;
								if (isDebug == true) {
									log.debug("Chunk size is equal ,exitting ...");
								}

								guardaValorAct();

								break;
							}

							if (isDebug == true) {
								log.debug("M_acctsMap <" + m_acctsMap + ">");
							}
						}

						arraySisperfil = new ArrayList<String>();

						acct = new HashMap<String, Object>();

						userid = queryResult.getString("CO_USUARIO");
						chaveMapa = userid;

						if (userid != null) {
							acct.put(coUsuario, userid);
						} else {
							acct.put(coUsuario, "-");
						}

						String noUsuario = queryResult.getString("NO_USUARIO");
						noUsuario = noUsuario.trim();

						if (noUsuario != null) {
							acct.put(this.noUsuario, noUsuario);
						} else {
							acct.put(this.noUsuario, "-");
						}

						String status = queryResult.getString("IC_STATUS");
						if (status != null) {
							acct.put(icStatus, status);
						} else {
							acct.put(icStatus, "-");
						}

						String nuMatr = queryResult.getString("NU_MATR");
						if (nuMatr != null) {
							acct.put(nuMatricula, nuMatr);
						} else {
							acct.put(nuMatricula, "-");
						}

						String iTipo = queryResult.getString("IC_TIPO");
						if (iTipo != null) {
							acct.put(icTipo, iTipo);
						} else {
							acct.put(icTipo, "-");
						}

						String noPerfil = queryResult.getString("NO_PERFIL");
						String noSistema = queryResult.getString("CO_SISTEMA");

						if (noPerfil != null && noSistema != null) {

							String sistemaPerfil = noSistema.trim() + "/"
									+ noPerfil.trim();

							arraySisperfil.add(sistemaPerfil);

							acct.put(sisPerfil, arraySisperfil);
						}

						String temp = queryResult.getString("IC_STATUS");

						if (temp.equals("1")) {
							acct.put(openconnector.Connector.ATT_DISABLED,
									new Boolean(true));
						} else {
							acct.put(openconnector.Connector.ATT_DISABLED,
									new Boolean(false));
						}

					}

					m_acctsMap.put(chaveMapa, acct);

					contador = contador + 1;

					queryResult.rowDeleted();

				}

				if (fetched < m_iChunkSize) {

					if (isDebug == true) {
						log.debug(" fetch done ... ");
					}
					m_sLastUser = EMPTY_STR;
				}

				return m_acctsMap;

			} else if (OBJECT_TYPE_GROUP.equals(this.objectType)) {

				if (isDebug == true) {
					log.debug("Get Objects Map Group....");
					log.debug("Group Filter <" + filter + ">");
				}

				if (filter != null) {

					String seperaGrupo = filter.toString();

					String sistema = "", perfil = "";

					String[] separa = seperaGrupo.split("/");
					sistema = separa[0].toString();
					perfil = separa[1].toString();

					cs = callProcedure("{call ASESP502_LISTA_PERFIS (?, ?)}");
					cs.setString(1, sistema);
					cs.setString(2, perfil);
					cs.execute();
					queryResult = cs.getResultSet();

					stmt = m_dbConn.createStatement();

					if (isDebug == true) {
						log.debug(" query " + query);
					}

					// queryResult = stmt.executeQuery(query);

					while (queryResult.next()) {
						grp = new HashMap<String, Object>();

						String numeroPerfil = queryResult
								.getString("NU_PERFIL").trim();
						String codSistema = queryResult.getString("CO_SISTEMA")
								.trim();
						String nomePerfil = queryResult.getString("NO_PERFIL")
								.trim();
						String perfilComposto = codSistema.trim() + "/"
								+ nomePerfil.trim();

						grp.put(sisPerfil, perfilComposto);
						grp.put(nuPerfil, numeroPerfil);
						grp.put(coSistema, codSistema);
						grp.put(noPerfil, nomePerfil);

						m_groupsMap.put(perfilComposto, grp);

						if (isDebug == true) {
							log.debug("m_groupsMap <" + m_groupsMap.toString()
									+ ">");
						}

					}
				}

				if (queryResultGrupo == null) {

					// query =
					// "select distinct NU_PERFIL, CO_SISTEMA, NO_PERFIL from ASEDB001.dbo.ASEVW022_USUARIO_CONEXAO_SAILP";

					cs = callProcedure("{call ASESP502_LISTA_PERFIS (?, ?)}");
					cs.setString(1, null);
					cs.setString(2, null);
					cs.execute();
					queryResultGrupo = cs.getResultSet();

					stmt = m_dbConn.createStatement();

					if (isDebug == true) {
						log.debug(" query ");
					}

					// queryResultGrupo = stmt.executeQuery(query);

				}

				int fetched = 0;

				while (queryResultGrupo.next()) {

					grp = new HashMap<String, Object>();

					String numeroPerfil = queryResultGrupo.getString(
							"NU_PERFIL").trim();
					String codSistema = queryResultGrupo
							.getString("CO_SISTEMA").trim();
					String nomePerfil = queryResultGrupo.getString("NO_PERFIL")
							.trim();
					String perfilComposto = codSistema.trim() + "/"
							+ nomePerfil.trim();

					grp.put(sisPerfil, perfilComposto);
					grp.put(nuPerfil, numeroPerfil);
					grp.put(coSistema, codSistema);
					grp.put(noPerfil, nomePerfil);

					m_groupsMap.put(perfilComposto, grp);

					if (isDebug == true) {
						log.debug("m_groupsMap <" + m_groupsMap.toString()
								+ ">");
						log.debug("fetched <" + fetched + ">");
					}

					fetched = fetched + 1;
					contador = contador + 1;

					if (isDebug == true) {
						log.debug("Fetched " + fetched);
						log.debug("M_IChunkSize " + m_iChunkSize);
						log.debug("Quantidade " + contador);
					}

					if (fetched >= m_iChunkSize) {
						m_sLastGroup = sisPerfil;
						if (isDebug == true) {
							log.debug("Chunk size is equal ,exitting ...");
						}

						break;
					} else {

						queryResultGrupo.rowDeleted();
					}

				}

				if (fetched < m_iChunkSize) {
					if (isDebug == true) {
						log.debug(" fetch done ... ");
					}

					m_sLastGroup = EMPTY_STR;
				}

				return m_groupsMap;
			}
		} catch (Exception e) {
			if (isDebug == true) {
				log.debug("connector exception ", e);
			}

			throw new ConnectorException(e);
		}

		throw new ConnectorException("Unhandled object type: "
				+ this.objectType);

	}

	HashMap<String, Object> acctValor;

	public void guardaValorAct() throws SQLException {

		debug();

		if (isDebug == true) {
			log.debug("=== Guarda Valor ACT === ");
		}

		acctValor = new HashMap<String, Object>();

		String userid = queryResult.getString("CO_USUARIO");
		chaveMapa = userid;

		if (userid != null) {
			acctValor.put(coUsuario, userid);
		} else {
			acctValor.put(coUsuario, "-");
		}

		String noUsuario = queryResult.getString("NO_USUARIO");
		noUsuario = noUsuario.trim();

		if (noUsuario != null) {
			acctValor.put(this.noUsuario, noUsuario);
		} else {
			acctValor.put(this.noUsuario, "-");
		}

		String status = queryResult.getString("IC_STATUS");
		if (status != null) {
			acctValor.put(icStatus, status);
		} else {
			acctValor.put(icStatus, "-");
		}

		String nuMatr = queryResult.getString("NU_MATR");
		if (nuMatr != null) {
			acctValor.put(nuMatricula, nuMatr);
		} else {
			acctValor.put(nuMatricula, "-");
		}

		String iTipo = queryResult.getString("IC_TIPO");
		if (iTipo != null) {
			acctValor.put(icTipo, iTipo);
		} else {
			acctValor.put(icTipo, "-");
		}

		String noPerfil = queryResult.getString("NO_PERFIL");
		String noSistema = queryResult.getString("CO_SISTEMA");

		if (noPerfil != null && noSistema != null) {

			String sistemaPerfil = noSistema.trim() + "/" + noPerfil.trim();

			arraySisperfil.add(sistemaPerfil);

			acctValor.put(sisPerfil, arraySisperfil);
		}

		String temp = queryResult.getString("IC_STATUS");

		if (temp.equals("1")) {
			acctValor.put(openconnector.Connector.ATT_DISABLED, new Boolean(
					true));
		} else {
			acctValor.put(openconnector.Connector.ATT_DISABLED, new Boolean(
					false));
		}

	}

	public ArrayList<String> groupsAccount(String userName) throws Exception {

		debug();

		if (isDebug == true) {
			log.debug("=== Groups Account === ");
		}

		String query = "select CO_USUARIO, CO_SISTEMA, NU_PERFIL, NO_PERFIL from ASEDB001.dbo.ASEVW022_USUARIO_CONEXAO_SAILP where CO_USUARIO='"
				+ userName + "'";
		ArrayList<String> listaGrupos = new ArrayList<String>();
		Statement stmt = m_dbConn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		while (rs.next()) {
			String codSistema = rs.getString("CO_SISTEMA");
			String nomePerfil = rs.getString("NO_PERFIL");
			String perfilComposto = codSistema + "/" + nomePerfil;

			listaGrupos.add(perfilComposto);
		}

		return listaGrupos;

	}

	public void encripta(String clearPassword) throws Exception {

		debug();

		if (isDebug == true) {
			log.debug("=== Encripta === ");
		}

		Result result = new Result();

		connectServerCriptografia();

		JISystem.setAutoRegisteration(true);

		Object[] paramsEncripto = new Object[] { new JIString(clearPassword),
				new JIString(config.getString("keyCripto") + clearPassword) };

		JIVariant[] resultsCript;
		try {
			resultsCript = comLocator
					.callMethodA("Criptografa", paramsEncripto);

			String senhaCripto = resultsCript[0].getObjectAsString2()
					.toString();

			@SuppressWarnings("unused")
			Object[] paramsDecripto = new Object[] { new JIString(senhaCripto),
					new JIString(config.getString("keyCripto") + clearPassword) };

			passowrdCriptografada = resultsCript[0].getObjectAsString2()
					.toString();
		} catch (JIException e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			result.add("TIPO APP SIASES " + e.getMessage());
		}

	}

	public void connectServerCriptografia() throws JIException,
			SecurityException, IOException {

		try {

			debug();

			if (isDebug == true) {
				log.debug("=== Connect Server Cripto === ");
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		// JISystem.setInBuiltLogHandler(true);

		if (isDebug == true) {
			log.debug("ConectServerCripto");
		}

		session = JISession.createSession(
				config.getString("dominioServerCripto"),
				config.getString("userServerCripto"),
				config.getString("senhaServerCripto"));

		try {
			comServer = new JIComServer(JIProgId.valueOf(config
					.getString("nameDLL")),
					config.getString("urlServerCripto"), session);
		} catch (JIException e) {

			comServer = new JIComServer(JIProgId.valueOf(config
					.getString("nameDLL")),
					config.getString("urlServerCripto"), session);
		}

		comLocator = (IJIDispatch) JIObjectFactory.narrowObject(comServer
				.createInstance().queryInterface(IID));

		userServerCripto = config.getString("userServerCripto");
		senhaServerCripto = config.getString("senhaServerCripto");
		nameDLL = config.getString("nameDLL");
		dominioServerCripto = config.getString("dominioServerCripto");
		keyCripto = config.getString("keyCripto");

	}

	public void dadosUsuario(String codUsuario) throws Exception {

		debug();

		if (isDebug == true) {
			log.debug("=== Dados Usuario === ");
		}

		listaUsuario(codUsuario);

		while (retornoUsuario.next()) {

			noUsuario = retornoUsuario.getString("NO_USUARIO");
			icTipo = retornoUsuario.getString("IC_TIPO");
			icStatus = retornoUsuario.getString("IC_STATUS");
			coSenhaAcesso = retornoUsuario.getString("CO_SENHA_ACESSO");

			if (isDebug == true) {
				log.debug("<noUsuario " + noUsuario + "> <icTipo " + icTipo
						+ "> <icStatus " + icStatus + "> <coSenhaAcesso "
						+ coSenhaAcesso + ">");
			}
		}

		cs.close();

	}

	public void dadosPerfil(String sistema, String perfil) throws Exception {

		debug();

		if (isDebug == true) {
			log.debug("=== Dados Perfil === ");
		}

		listarPerfil(sistema, perfil);

		while (retornoPerfil.next()) {
			nuPerfil = retornoPerfil.getString("NU_PERFIL");

			if (isDebug == true) {
				log.debug("<nuPerfil " + nuPerfil + ">");
			}

		}

		cs.close();
	}

	public void dadosConexoes(String codUsuario) throws Exception {

		debug();

		if (isDebug == true) {
			log.debug("=== Dados Usuario === ");
		}

		listarConexoes(codUsuario);

		while (retornoConexao.next()) {
			coSistema = retornoConexao.getString("CO_SISTEMA");
			nuPerfil = retornoConexao.getString("NU_PERFIL");

			if (isDebug == true) {
				log.debug("<coSistema " + coSistema + ">");
			}

			break;
		}

		cs.close();
	}

	// conexao ao banco
	protected CallableStatement callProcedure(String procedure)
			throws Exception {

		debug();

		if (isDebug == true) {
			log.debug("=== Call Procedure === ");
		}

		valorConexao();
		cs = null;
		cs = getConnection().prepareCall(procedure);

		return cs;
	}

	// conexao ao banco
	protected Connection getConnection() throws SQLException,
			ClassNotFoundException {
		try {

			debug();

			if (isDebug == true) {
				log.debug("=== Get Connection === ");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (connection == null) {
			Class.forName(DRIVE);

			connection = DriverManager.getConnection(URL, USUARIO, SENHA);
		}
		return connection;
	}

	// Obtem os dados para acesso ao banco
	public void valorConexao() throws Exception {

		debug();

		if (isDebug == true) {
			log.debug("=== Valor Conexao === ");
		}

		URL = config.getString("url");
		SENHA = config.getString("password");
		USUARIO = config.getString("user");
		DRIVE = config.getString("driverClass");

	}

	public void listaUsuario(String codUsuario) throws Exception {

		debug();

		if (isDebug == true) {
			log.debug("=== Lista Usuario === ");
		}

		Result result = new Result();

		try {

			cs = callProcedure("{call ASESP503_LISTA_USUARIOS (?) }");
			cs.setString(1, codUsuario);
			cs.execute();
			retornoUsuario = cs.getResultSet();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			result.add("TIPO APP SIASES LISTA USUARIO " + e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			result.add("TIPO APP SIASES LISTA USUARIO" + e.getMessage());
		}
	}

	public void listarPerfil(String sistema, String perfil) throws Exception {

		debug();

		if (isDebug == true) {
			log.debug("=== Lista Perfil === ");
		}

		Result result = new Result();

		try {

			cs = callProcedure("{call ASESP502_LISTA_PERFIS (?, ?)}");
			cs.setString(1, sistema);
			cs.setString(2, perfil);
			cs.execute();
			retornoPerfil = cs.getResultSet();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			result.add(e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			result.add("TIPO APP SIASES " + e.getMessage());
		}
	}

	public void listarConexoes(String codUsuario) throws Exception {

		debug();

		if (isDebug == true) {
			log.debug("=== Lista Conexao === ");
		}

		Result result = new Result();

		try {

			cs = callProcedure("{call ASESP500_LISTA_CONEXOES (?, ?, ?)}");
			cs.setString(1, null);
			cs.setString(2, null);
			cs.setString(3, codUsuario);
			cs.execute();
			retornoConexao = cs.getResultSet();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			result.add("TIPO APP SIASES " + e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			result.add("TIPO APP SIASES " + e.getMessage());
		}

	}

	public void deletaUser(String codUsuario) throws Exception {

		debug();

		if (isDebug == true) {
			log.debug("=== Lista Conexao === ");
		}

		Result result = new Result();

		try {
			CallableStatement cs = callProcedure("{call ASESP026_EXCLUI_USU_SIASE (?) }");
			cs.setString(1, codUsuario);
			cs.execute();
			cs.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			result.add("TIPO APP SIASES " + e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			result.add("TIPO APP SIASES " + e.getMessage());
		}

	}

	public void desvincularGrupo(String codSistema, String codUsuario)
			throws Exception {

		debug();

		if (isDebug == true) {
			log.debug("=== Desvincula Grupo === ");
		}

		Result result = new Result();

		try {
			cs = callProcedure("{call ASESP027_EXCLUI_USU_SISTEMA (?, ?) }");
			cs.setString(1, codUsuario);
			cs.setString(2, codSistema);
			cs.execute();
			cs.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			result.add("TIPO APP SIASES " + e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			result.add("TIPO APP SIASES " + e.getMessage());
		}
	}

	public void createUser(String coSistema, String coUsuario,
			String noUsuario, String icStatus, String icTipo,
			String coSenhaAcesso, String nuPerfil, int operacao)
			throws ClassNotFoundException, SQLException {

		debug();

		if (isDebug == true) {
			log.debug("=== Criar Usuario === ");
		}

		Result result = new Result();

		nuPerfil = nuPerfil.trim();

		int numeroPerfil = 0;

		if (nuPerfil.equals("NU_PERFIL")) {
			result = new Result(Result.Status.Failed);
			result.add("O Campo Perfil não pode ser vazio");

		} else {

			numeroPerfil = Integer.parseInt(nuPerfil);

		}

		log.debug("numeroPerfil " + numeroPerfil);
		if (isDebug == true) {
			log.debug("Procedure Create");
		}

		try {

			cs = callProcedure("{call ASESP901_NOVO_USUARIO (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }");
			cs.setInt(1, operacao);
			cs.setString(2, coSistema);
			cs.setString(3, coUsuario);
			cs.setString(4, noUsuario);
			cs.setString(5, icStatus);
			cs.setDate(6, null);
			cs.setDate(7, null);
			cs.setInt(8, 0);
			cs.setString(9, icTipo);
			cs.setString(10, coSenhaAcesso);
			cs.setInt(11, numeroPerfil);
			cs.setString(12, IC_STATUS_ACESSO);
			cs.setString(13, null);
			cs.setString(14, null);
			cs.setString(15, null);
			cs.setString(16, null);
			cs.execute();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			result.add("TIPO APP SIASES PROCEDURE CREATE" + e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			result.add("TIPO APP SIASES PROCEDURE CREATE SQLException "
					+ e.getMessage());
			result.add("TIPO APP SIASES PROCEDURE CREATE SQLException "
					+ e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			result.add("TIPO APP SIASES PROCEDURE CREATE Excpetion "
					+ e.getMessage());

		}
	}
}
