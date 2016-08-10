import static org.jinterop.dcom.impls.automation.IJIDispatch.IID;

import java.io.IOException;
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

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.JIObjectFactory;
import org.jinterop.dcom.impls.automation.IJIDispatch;

import br.com.pbti.dto.AccountAgreggation;
import br.com.pbti.dto.GroupAgreggation;
import br.com.pbti.vo.Account;
import br.com.pbti.vo.Group;
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

	private Iterator<Map<String, Object>> it;

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
	@SuppressWarnings("unused")
	private boolean m_bIsDriverLoaded;
	private Connection m_dbConn;

	// Drive connect DLL
	private static String urlServerCripto;
	private static String userServerCripto;
	@SuppressWarnings("unused")
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
	public static final String EMPTY_STR = " ";
	private Filter _filter;
	private Map<String, Map<String, Object>> m_acctsMap = new HashMap<String, Map<String, Object>>();
	private Map<String, Map<String, Object>> m_groupsMap = new HashMap<String, Map<String, Object>>();
	private int contador = 0;
	private ArrayList<Map<String, Object>> listAgregAccount = new ArrayList<Map<String, Object>>();
	private ArrayList<Map<String, Object>> listAgregGoup = new ArrayList<Map<String, Object>>();

	// AcccountAgreggation
	public AccountAgreggation accountAgreggation = new AccountAgreggation();
	public GroupAgreggation groupAgregation = new GroupAgreggation();

	public int tamanhoListaAccount;
	public int tamanhoListaGroup;

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
	public SIASES(ConnectorConfig config, openconnector.Log log) throws Exception {
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
			close();
			if (log.isDebugEnabled())
				log.error("Erro teste Conexao ", e);
			throw new ConnectorException(
					"Falha no teste de conexao : " + e.getMessage() + ".  Check os detalhes da conexao.");
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
			throws ConnectorException, IllegalArgumentException, UnsupportedOperationException {

		debug("Metodo Read");

		Map<String, Object> acct = null;
		Map<String, Object> grp = null;

		try {
			if (OBJECT_TYPE_ACCOUNT.equals(this.objectType)) {

				debug("Account");

				accountAgreggation.setUrlServer(config.getString("url"));
				accountAgreggation.setDriveClass(config.getString("driverClass"));
				accountAgreggation.setUserServer(config.getString("user"));
				accountAgreggation.setPasswordServer(config.getString("password"));

				accountAgreggation.consulta(nativeIdentifier);

				acct = accountAgreggation.read();

				debug("Mapa Account " + acct);

				return acct;

			} else if (OBJECT_TYPE_GROUP.equals(this.objectType)) {

				debug("Group");

				groupAgregation.setUrlServer(config.getString("url"));
				groupAgregation.setDriveClass(config.getString("driverClass"));
				groupAgregation.setUserServer(config.getString("user"));
				groupAgregation.setPasswordServer(config.getString("password"));

				groupAgregation.consultaGroup(nativeIdentifier);

				grp = groupAgregation.read();

				debug("Mapa Group " + grp);

				return grp;

			}

		} catch (ClassNotFoundException e) {
			debug("Erro Siases: metodo read " + e);
			throw new ConnectorException("Erro Siases: metodo read " + e);
		} catch (SQLException e) {
			debug("Erro Siases: Erro metodo read " + e);
			throw new ConnectorException("Erro Siases: metodo read " + e);
		} catch (Exception e) {
			debug("Erro Siases: metodo read " + e);
			throw new ConnectorException("Erro Siases: metodo read " + e);
		}

		if (OBJECT_TYPE_ACCOUNT.equals(this.objectType))
			return acct;
		else if (OBJECT_TYPE_GROUP.equals(this.objectType))
			return grp;
		else
			throw new ConnectorException("Erro Siases: metodo read : " + this.objectType);
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

		debug("=====Metodo Iterate ====");

		it = null;

		debug("Iterate filter: " + filter + ">");

		try {
			_filter = filter;
			it = new ArrayList<Map<String, Object>>(getObjectsMap().values()).iterator();
		} catch (Exception e) {
			debug("Erro Siases: metodo Iterate: " + e);

			throw new ConnectorException("Erro Siases: metodo Iterate: " + e);
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

		debug("===== Metodo Create ====");

		Result result = new Result();

		try {

			String codSistema = "", codUsuario = "", nomUsuario = "", icStatu = "", icType = "", codSenha = "",
					numPerfil = "", passwordLimpo = "", sistemaPerfis = "";
			int operacao = 0;
			ArrayList<String> arrayPerfilSistema = new ArrayList<String>();

			codUsuario = nativeIdentifier;

			IC_STATUS_ACESSO = "0";

			debug("Create <codUsuario: " + codUsuario + ">");

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
									result.add("Erro Siases: metodo create: "
											+ "O sistema não permite adicionar mais de um perfil.");

									debug("Erro Siases: metodo create: "
											+ "O sistema não permite adicionar mais de um perfil.");

									throw new ConnectorException("Erro Siases: metodo create: "
											+ "O sistema não permite adicionar mais de um perfil.");
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

				dadosPerfil(sistema, perfil);

				numPerfil = nuPerfil;
				codSistema = sistema;

				debug("Create " + "<CodUsuario: " + codUsuario + "> <CodSistema: " + codSistema + "> <nomUsuario: "
						+ nomUsuario + "> <icStatu: " + icStatu + "> <icType: " + icType + "> <codSenha: " + codSenha
						+ "> <numPerfil: " + numPerfil + "> <operacao> " + operacao + ">");

				createUser(codSistema, codUsuario, nomUsuario, icStatu, icType, codSenha, numPerfil, operacao);

				result = new Result(Result.Status.Committed);
			}

		} catch (Exception e) {
			close();
			result = new Result(Result.Status.Failed);
			result.add("Erro Siases: metodo Create: " + e);
			debug("Erro Siases: metodo Create: " + e);

			throw new ConnectorException("Erro Siases: metodo Create: " + e);
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
	public Result update(String nativeIdentifier, List<Item> items) throws ConnectorException, ObjectNotFoundException {

		debug("===== Metodo Update ====");

		Result result = new Result();

		String codSistema = "", codUsuario = "", nomUsuario = "", icStatu = "", icType = "", codSenha = "",
				numPerfil = "";
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
								result.add("Erro Siases: metodo update: "
										+ "O sistema não permite adicionar mais de um perfil.");
								return result;
							}
						}
					}

				}

				debug("Siases metodo Update - <Nome Item: " + name + ">\n <Valor Item: " + valueList.get(0).toString()
						+ ">\n <Operation: " + op + ">");

				switch (op) {

				case Add: {

					debug("Case Add");

					if (name.equalsIgnoreCase(sisPerfil)) {

						for (Object sistemaPerfil : valueList) {
							String sistemaPerfis = sistemaPerfil.toString();

							arrayPerfilSistema.add(sistemaPerfis);
						}

					}

					dadosUsuario(codUsuario);

					nomUsuario = noUsuario;
					icStatu = icStatus;
					icType = icTipo;
					codSenha = coSenhaAcesso;
					operacao = 1;

					for (String sistemaPerfis : arrayPerfilSistema) {

						try {

							String[] array = sistemaPerfis.split("/");

							String sistema = array[0];
							String perfil = array[1];

							dadosPerfil(sistema, perfil);

							numPerfil = nuPerfil;
							codSistema = sistema;

							debug("Metodo Update: Operacao: ADD: <CodUsuario: " + codUsuario + "> <CodSistema: "
									+ codSistema + "> <nomUsuario: " + nomUsuario + "> <icStatu: " + icStatu
									+ "> <icType: " + icType + "> <codSenha: " + codSenha + "> <numPerfil: " + numPerfil
									+ "> <operacao: " + operacao + ">");

							createUser(codSistema, codUsuario, nomUsuario, icStatu, icType, codSenha, numPerfil,
									operacao);

							result = new Result(Result.Status.Committed);
						} catch (Exception e) {
							close();
							result = new Result(Result.Status.Failed);
							result.add("Erro Siases: Metodo update: Operacao ADD: " + e);

							debug("Erro Siases: Metodo update: Operacao ADD: " + e);

							throw new ConnectorException("Erro Siases: Metodo update: Operacao ADD: " + e);
						}
					}

				}

					break;

				case Remove: {

					debug("Case Remove");

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

								debug("UPDADE - REMOVER <" + "CodUsuario: " + codUsuario + "> <CodSistema: "
										+ codSistema + ">");

								desvincularGrupo(codSistema, codUsuario);

								result = new Result(Result.Status.Committed);

							}
						} catch (Exception e) {
							close();
							result = new Result(Result.Status.Failed);
							result.add("Erro Siases: Update: Remove: " + e);

							debug("Erro Siases: Update: Remove: " + e);

							throw new ConnectorException("Erro Siases: Update: Remove: " + e);
						}
					}
				}
					break;
				case Set: {

					// existing.put(name, valueList.get(0).toString());
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

		debug("=====Metodo Delete ====");

		Result result = new Result();

		try {

			String codUsuario = "";

			codUsuario = nativeIdentifier;

			debug("Delete " + "<CodUsuario " + codUsuario + ">");

			deletaUser(codUsuario);

			result = new Result(Result.Status.Committed);
		} catch (Exception e) {
			close();
			result = new Result(Result.Status.Failed);
			result.add("Erro Siases: Delete: " + e);
			debug("Erro Siases: Delete: " + e);

			throw new ConnectorException("Erro Siases: Delete: " + e);
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

		debug("===== Metodo Enable ====");

		Result result = new Result();

		try {

			String codSistema = "", codUsuario = "", nomUsuario = "", icStatu = "", icType = "", codSenha = "",
					numPerfil = "";
			int operacao = 0;

			codUsuario = nativeIdentifier;

			IC_STATUS_ACESSO = "0";

			debug("ENABLE USUARIO " + "<CodUsuario " + codUsuario + ">");

			dadosConexoes(codUsuario);

			numPerfil = nuPerfil;
			codSistema = coSistema;

			dadosUsuario(codUsuario);

			nomUsuario = noUsuario;
			icStatu = "0";
			icType = icTipo;
			operacao = 2;

			debug("Siases: Enable:" + "<CodUsuario: " + codUsuario + "> <CodSistema: " + codSistema + "> <nomUsuario: "
					+ nomUsuario + "> <icStatu: " + icStatu + "> <icType: " + icType + "> <codSenha: " + codSenha
					+ "> <numPerfil: " + numPerfil + "> <operacao: " + operacao + ">");

			createUser(codSistema, codUsuario, nomUsuario, icStatu, icType, codSenha, numPerfil, operacao);

			result = new Result(Result.Status.Committed);
		} catch (Exception e) {
			close();
			result = new Result(Result.Status.Failed);
			result.add("Erro Siases: Enable " + e);

			debug("Erro Siases: Enable " + e);

			throw new ConnectorException("Erro Siases: Enable " + e);
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

		debug("===== Metodo Disable ====");

		Result result = new Result();

		try {

			String codSistema = "", codUsuario = "", nomUsuario = "", icStatu = "", icType = "", codSenha = "",
					numPerfil = "";
			int operacao = 0;

			codUsuario = nativeIdentifier;

			IC_STATUS_ACESSO = "1";

			debug("Siases Disable" + "<CodUsuario: " + codUsuario + ">");

			dadosConexoes(codUsuario);

			numPerfil = nuPerfil;
			codSistema = coSistema;

			dadosUsuario(codUsuario);

			nomUsuario = noUsuario;
			icStatu = "1";
			icType = icTipo;
			operacao = 2;

			debug("Siases Disable " + "<CodUsuario: " + codUsuario + "> <CodSistema: " + codSistema + "> <nomUsuario: "
					+ nomUsuario + "> <icStatu: " + icStatu + "> <icType: " + icType + "> <codSenha: " + codSenha
					+ "> <numPerfil: " + numPerfil + "> <operacao: " + operacao + ">");

			createUser(codSistema, codUsuario, nomUsuario, icStatu, icType, codSenha, numPerfil, operacao);

			result = new Result(Result.Status.Committed);
		} catch (Exception e) {
			close();
			result = new Result(Result.Status.Failed);
			result.add("Erro Siases: Disable " + e);
			debug("Erro Siases: Disable " + e);

			throw new ConnectorException("Erro Siases: Disable " + e);
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
			throws ConnectorException, ObjectNotFoundException, UnsupportedOperationException {

		Result result = new Result(Result.Status.Committed);

		// Map<String, Object> obj = read(nativeIdentifier);
		// if (null == obj) {
		// throw new ObjectNotFoundException(nativeIdentifier);
		// }

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

		debug("===== Metodo SetPassword ====");

		Result result = new Result();

		try {

			String codSistema = "", codUsuario = "", nomUsuario = "", icStatu = "", icType = "", codSenha = "",
					numPerfil = "", clearPassword = "";
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

			debug("Siases Password" + "<CodUsuario: " + codUsuario + "> <CodSistema: " + codSistema + "> <nomUsuario: "
					+ nomUsuario + "> <icStatu: " + icStatu + "> <icType: " + icType + "> <SenhaCripto: " + codSenha
					+ "> <numPerfil: " + numPerfil + "> <operacao: " + operacao + ">");

			createUser(codSistema, codUsuario, nomUsuario, icStatu, icType, codSenha, numPerfil, operacao);
			result = new Result(Result.Status.Committed);
		} catch (Exception e) {
			close();
			result = new Result(Result.Status.Failed);
			result.add("Erro Siases: Metodo setPassword: " + e);

			throw new ConnectorException("Erro Siases: Metodo setPassword: " + e);
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

	public Map<String, Object> authenticate(String identity, String password) throws ConnectorException,
			ObjectNotFoundException, AuthenticationFailedException, ExpiredPasswordException {

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

		debug("=== Metodo Close === ");

		try {

			if (connection != null) {

				debug("Antes Connection Close");
				connection.close();
				debug("Pos Connection Close");
			}

			if (m_dbConn != null) {
				m_dbConn.close();
			}

		} catch (SQLException e) {
			debug("Erro ao fechar conexao: " + e);
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

		debug("INIT " + "<user " + m_sUser + "> <m_sUrl " + m_sUrl + "> <m_sDriverName " + m_sDriverName
				+ "> <urlServerCripto " + urlServerCripto + "> <userServerCripto " + userServerCripto + "> <nameDLL "
				+ nameDLL + "> <dominioServerCripto " + dominioServerCripto + "> <keyCripto " + keyCripto + ">");

		if (m_dbConn == null) {
			Class.forName(m_sDriverName);

			m_dbConn = DriverManager.getConnection(m_sUrl, m_sUser, m_sPasswd);

			m_bIsDriverLoaded = true;
		}
	}

	public void debug(String mensagem) {
		boolean debug = config.getBoolean("debug");

		if (debug == true) {
			log.debug(mensagem);
		}

	}

	public Iterator<Map<String, Object>> IterateNextPage(Filter filter) {
		try {

			debug("=== Metodo IterateNextPage ===");

			if (OBJECT_TYPE_ACCOUNT.equals(this.objectType)) {

				debug("IterateNextPage Account");
				debug("Tamanho Lista " + listAgregAccount.size());

				if (listAgregAccount.size() == 0) {
					debug("end of group iteration...");
				} else {
					it = (getObjectsMap().values()).iterator();
				}

			} else if (OBJECT_TYPE_GROUP.equals(this.objectType)) {
				debug("IterateNextPage Group");
				debug("Tamanho Lista " + listAgregGoup.size());

				if (listAgregGoup.size() == 0) {
					debug("end of group iteration...");
				} else {
					it = (getObjectsMap().values()).iterator();
				}
			}
		} catch (Exception e) {
			debug("Exception occured " + e);
			throw new ObjectNotFoundException("Erro no metodo IterateNextPage: " + e);
		}
		return it;
	}

	private Map<String, Map<String, Object>> getObjectsMap() throws Exception {

		debug("=== Metodo Get Object Map ===");

		Filter filter = _filter;

		Map<String, Object> dados = new HashMap<String, Object>();

		m_acctsMap.clear();
		m_groupsMap.clear();

		try {
			if (OBJECT_TYPE_ACCOUNT.equals(this.objectType)) {

				debug("Account");

				if (listAgregAccount.isEmpty()) {
					if (filter != null) {

						debug("Filter <" + filter + ">");

						accountAgreggation.setUrlServer(config.getString("url"));
						accountAgreggation.setDriveClass(config.getString("driverClass"));
						accountAgreggation.setUserServer(config.getString("user"));
						accountAgreggation.setPasswordServer(config.getString("password"));

						accountAgreggation.consulta(filter.toString());

						listAgregAccount = accountAgreggation.getListUser();

						debug("listAgregAccount " + listAgregAccount);

					} else {

						accountAgreggation.setUrlServer(config.getString("url"));
						accountAgreggation.setDriveClass(config.getString("driverClass"));
						accountAgreggation.setUserServer(config.getString("user"));
						accountAgreggation.setPasswordServer(config.getString("password"));

						accountAgreggation.consulta("");

						listAgregAccount = accountAgreggation.getListUser();

						debug("listAgregAccount " + listAgregAccount);
					}
				} else {
					for (int i = 0; i < 10; i++) {
						listAgregAccount.remove(0);
					}
					contador = 0;
				}

				for (Map<String, Object> mapUser : listAgregAccount) {
					if (contador < 10) {

						Account acc = (Account) mapUser.get("Account");
						dados = new HashMap<String, Object>();

						dados.put("CO_USUARIO", acc.getCoUsuario().trim());
						dados.put("NO_USUARIO", acc.getNoUsuario().trim());
						dados.put("IC_STATUS", acc.getIcStatus().trim());
						dados.put("IC_TIPO", acc.getIcTipo().trim());
						dados.put("NU_MATR", acc.getNuMatricula().trim());
						dados.put("SIS_PERFIL", mapUser.get("SISPERFIL"));

						if (acc.getIcStatus().equals("1")) {
							dados.put(openconnector.Connector.ATT_DISABLED, new Boolean(true));
						} else {
							dados.put(openconnector.Connector.ATT_DISABLED, new Boolean(false));
						}

						m_acctsMap.put(acc.getCoUsuario().trim(), dados);

						contador = contador + 1;

						debug("Contador " + contador);
						debug("Dados " + dados);

					} else {
						break;
					}
				}

				if (listAgregAccount.size() < 10) {

					int cout = listAgregAccount.size();
					for (int i = 0; i < cout; i++) {
						listAgregAccount.remove(0);
					}
				}

				debug("Map Resultado " + m_acctsMap);

				return m_acctsMap;

			} else if (OBJECT_TYPE_GROUP.equals(this.objectType)) {

				debug("Group");

				if (listAgregGoup.isEmpty()) {

					if (filter != null) {

						debug("Filter <" + filter + ">");

						groupAgregation.setUrlServer(config.getString("url"));
						groupAgregation.setDriveClass(config.getString("driverClass"));
						groupAgregation.setUserServer(config.getString("user"));
						groupAgregation.setPasswordServer(config.getString("password"));

						groupAgregation.consultaGroup(filter.toString());

						listAgregGoup = groupAgregation.getListGroup();

						debug("Lista Agreg " + listAgregGoup);

					} else {

						groupAgregation.setUrlServer(config.getString("url"));
						groupAgregation.setDriveClass(config.getString("driverClass"));
						groupAgregation.setUserServer(config.getString("user"));
						groupAgregation.setPasswordServer(config.getString("password"));

						groupAgregation.consultaGroup("");
						listAgregGoup = groupAgregation.getListGroup();

						debug("Lista Agreg " + listAgregGoup);

					}
				} else {
					for (int i = 0; i < 10; i++) {
						listAgregGoup.remove(0);
					}
					contador = 0;
				}

				for (Map<String, Object> mapGroup : listAgregGoup) {
					if (contador < 10) {

						Group gpr = (Group) mapGroup.get("Group");
						dados = new HashMap<String, Object>();

						dados.put("NU_PERFIL", gpr.getCodigoPerfil().trim());
						dados.put("CO_SISTEMA", gpr.getCodigoSistema().trim());
						dados.put("NO_PERFIL", gpr.getNomePerfil().trim());
						dados.put("SIS_PERFIL", gpr.getCodigoSistema().trim() + "/" + gpr.getNomePerfil().trim());

						m_groupsMap.put(gpr.getCodigoSistema().trim() + "/" + gpr.getNomePerfil().trim(), dados);

						contador = contador + 1;

						debug("Contador " + contador);
						debug("Dados " + dados);

					} else {

						break;
					}
				}

				if (listAgregGoup.size() < 10) {

					int cout = listAgregGoup.size();
					for (int i = 0; i < cout; i++) {
						listAgregGoup.remove(0);
					}
				}

				debug("Map Resultado " + m_groupsMap);

				return m_groupsMap;

			}
		} catch (Exception e) {
			debug("connector exception " + e);

			throw new ConnectorException("Erro no metodo: getObjectsMap: " + e);
		}

		throw new ConnectorException("Unhandled object type: " + this.objectType);

	}

	public void encripta(String clearPassword) {

		debug("=== Metodo Encripta === ");

		Result result = new Result();

		try {

			connectServerCriptografia();

			JISystem.setAutoRegisteration(true);

			Object[] paramsEncripto = new Object[] { new JIString(clearPassword),
					new JIString(config.getString("keyCripto") + clearPassword) };

			JIVariant[] resultsCript;

			resultsCript = comLocator.callMethodA("Criptografa", paramsEncripto);

			String senhaCripto = resultsCript[0].getObjectAsString2().toString();

			@SuppressWarnings("unused")
			Object[] paramsDecripto = new Object[] { new JIString(senhaCripto),
					new JIString(config.getString("keyCripto") + clearPassword) };

			passowrdCriptografada = resultsCript[0].getObjectAsString2().toString();
		} catch (JIException e) {
			result = new Result(Result.Status.Failed);
			result.add("Erro Siases: Metodo encripta: " + e);
			throw new ConnectorException("Erro Siases: Metodo encripta: " + e);
		} catch (Exception e) {
			result = new Result(Result.Status.Failed);
			result.add("Erro Siases: Metodo encripta: " + e);
			throw new ConnectorException("Erro Siases: Metodo encripta: " + e);
		}

	}

	public void connectServerCriptografia() throws JIException, SecurityException, IOException {

		debug("=== Metodo ConnectServerCripto === ");

		// JISystem.setInBuiltLogHandler(true);

		session = JISession.createSession(config.getString("dominioServerCripto"), config.getString("userServerCripto"),
				config.getString("senhaServerCripto"));

		session.setGlobalSocketTimeout(60000);

		try {
			comServer = new JIComServer(JIProgId.valueOf(config.getString("nameDLL")),
					config.getString("urlServerCripto"), session);
		} catch (JIException e) {

			comServer = new JIComServer(JIProgId.valueOf(config.getString("nameDLL")),
					config.getString("urlServerCripto"), session);

		}

		comLocator = (IJIDispatch) JIObjectFactory.narrowObject(comServer.createInstance().queryInterface(IID));

		comLocator.setInstanceLevelSocketTimeout(1000);

	}

	public void dadosUsuario(String codUsuario) {
		debug("=== Metodo dadosUsuario === ");

		try {

			listaUsuario(codUsuario);

			while (retornoUsuario.next()) {

				noUsuario = retornoUsuario.getString("NO_USUARIO");
				icTipo = retornoUsuario.getString("IC_TIPO");
				icStatus = retornoUsuario.getString("IC_STATUS");
				coSenhaAcesso = retornoUsuario.getString("CO_SENHA_ACESSO");

				debug("Resultado Dados Usuario <noUsuario " + noUsuario + "> <icTipo " + icTipo + "> <icStatus "
						+ icStatus + "> <coSenhaAcesso " + coSenhaAcesso + ">");

			}

			cs.close();

		} catch (Exception e) {
			close();
			throw new ConnectorException("Erro no metodo: dadosUsuario: " + e);
		}

	}

	public void dadosPerfil(String sistema, String perfil) {

		debug("=== Metodo dadosPerfil === ");

		try {

			listarPerfil(sistema, perfil);

			while (retornoPerfil.next()) {
				nuPerfil = retornoPerfil.getString("NU_PERFIL");

				debug("<nuPerfil " + nuPerfil + ">");

			}

			cs.close();

		} catch (Exception e) {
			close();
			throw new ConnectorException("Erro no metodo: dadosPerfil: " + e);
		}
	}

	public void dadosConexoes(String codUsuario) {

		debug("=== Metodo dadosUsuario === ");

		try {

			listarConexoes(codUsuario);

			while (retornoConexao.next()) {
				coSistema = retornoConexao.getString("CO_SISTEMA");
				nuPerfil = retornoConexao.getString("NU_PERFIL");

				debug("<coSistema " + coSistema + ">");

				break;
			}

			cs.close();

		} catch (Exception e) {
			close();
			throw new ConnectorException("Erro no metodo: dadosConexoes: " + e);
		}
	}

	// conexao ao banco
	protected CallableStatement callProcedure(String procedure) {

		debug("=== Metodo callProcedure === ");

		try {

			valorConexao();
			cs = null;
			cs = getConnection().prepareCall(procedure);

		} catch (Exception e) {
			throw new ConnectorException("Erro no metodo: callProcedure: " + e);
		}

		return cs;
	}

	// conexao ao banco
	protected Connection getConnection() {

		debug("=== Metodo getConnection === ");

		try {

			if (connection == null) {
				Class.forName(DRIVE);
				connection = DriverManager.getConnection(URL, USUARIO, SENHA);
			}

		} catch (ClassNotFoundException e) {
			throw new ConnectorException("Erro no metodo: getConnection: " + e);
		} catch (SQLException e) {
			throw new ConnectorException("Erro no metodo: getConnection: " + e);
		}

		return connection;
	}

	// Obtem os dados para acesso ao banco
	public void valorConexao() {

		debug("=== Metodo valorConexao === ");

		URL = config.getString("url");
		SENHA = config.getString("password");
		USUARIO = config.getString("user");
		DRIVE = config.getString("driverClass");

	}

	public void listaUsuario(String codUsuario) {

		debug("=== Metodo listaUsuario === ");

		Result result = new Result();

		try {

			cs = callProcedure("{call ASESP503_LISTA_USUARIOS (?) }");
			cs.setString(1, codUsuario);
			cs.execute();
			retornoUsuario = cs.getResultSet();
		} catch (SQLException e) {
			close();
			result = new Result(Result.Status.Failed);
			result.add("Erro Siases: Metodo listaUsuario: Procedure ASESP503_LISTA_USUARIOS: " + e);
			throw new ConnectorException("Erro Siases: Metodo listaUsuario: Procedure ASESP503_LISTA_USUARIOS: " + e);
		}
	}

	public void listarPerfil(String sistema, String perfil) throws Exception {

		debug("=== Metodo listaPerfil === ");

		Result result = new Result();

		try {

			cs = callProcedure("{call ASESP502_LISTA_PERFIS (?, ?)}");
			cs.setString(1, sistema);
			cs.setString(2, perfil);
			cs.execute();
			retornoPerfil = cs.getResultSet();

		} catch (SQLException e) {
			close();
			result = new Result(Result.Status.Failed);
			result.add("Erro Siases: Metodo listarPerfil: Procedure ASESP502_LISTA_PERFIS: " + e);
			throw new ConnectorException("Erro Siases: Metodo listarPerfil: Procedure ASESP502_LISTA_PERFIS: " + e);
		}
	}

	public void listarConexoes(String codUsuario) throws Exception {

		debug("=== Metodo listaConexao === ");

		Result result = new Result();

		try {
			cs = callProcedure("{call ASESP500_LISTA_CONEXOES (?, ?, ?)}");
			cs.setString(1, null);
			cs.setString(2, null);
			cs.setString(3, codUsuario);
			cs.execute();
			retornoConexao = cs.getResultSet();
		} catch (SQLException e) {
			close();
			result = new Result(Result.Status.Failed);
			result.add("Erro Siases: Metodo listarConexoes: Procedure ASESP500_LISTA_CONEXOES: " + e);
			throw new ConnectorException("Erro Siases: Metodo listarConexoes: Procedure ASESP500_LISTA_CONEXOES: " + e);
		}

	}

	public void deletaUser(String codUsuario) throws Exception {

		debug("=== Metodo deletaUser === ");

		Result result = new Result();

		try {
			CallableStatement cs = callProcedure("{call ASESP026_EXCLUI_USU_SIASE (?) }");
			cs.setString(1, codUsuario);
			cs.execute();
			cs.close();

		} catch (SQLException e) {
			close();
			result = new Result(Result.Status.Failed);
			result.add("Erro Siases: Metodo deletaUser: Procedure ASESP026_EXCLUI_USU_SIASE: " + e);
			throw new ConnectorException("Erro Siases: Metodo deletaUser: Procedure ASESP026_EXCLUI_USU_SIASE: " + e);
		}

	}

	public void desvincularGrupo(String codSistema, String codUsuario) throws Exception {

		debug("=== Metodo desvinculaGrupo ===");

		Result result = new Result();

		try {
			cs = callProcedure("{call ASESP027_EXCLUI_USU_SISTEMA (?, ?) }");
			cs.setString(1, codUsuario);
			cs.setString(2, codSistema);
			cs.execute();
			cs.close();

		} catch (SQLException e) {
			close();
			result = new Result(Result.Status.Failed);
			result.add("Erro Siases: Metodo desvincularGrupo: Procedure ASESP027_EXCLUI_USU_SISTEMA: " + e);
			throw new ConnectorException(
					"Erro Siases: Metodo desvincularGrupo: Procedure ASESP027_EXCLUI_USU_SISTEMA: " + e);
		}
	}

	public void createUser(String coSistema, String coUsuario, String noUsuario, String icStatus, String icTipo,
			String coSenhaAcesso, String nuPerfil, int operacao) throws ClassNotFoundException, SQLException {

		debug("=== Metodo createUser === ");

		Result result = new Result();

		nuPerfil = nuPerfil.trim();

		int numeroPerfil = 0;

		if (nuPerfil.equals("NU_PERFIL")) {
			result = new Result(Result.Status.Failed);
			result.add("O Campo Perfil não pode ser vazio");

		} else {

			numeroPerfil = Integer.parseInt(nuPerfil);

		}
		debug("numeroPerfil " + numeroPerfil);

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

		} catch (SQLException e) {
			close();
			result = new Result(Result.Status.Failed);
			result.add("Erro Siases: Metodo createUser: Procedure ASESP901_NOVO_USUARIO: " + e);
			throw new ConnectorException("Erro Siases: Metodo createUser: Procedure ASESP901_NOVO_USUARIO: " + e);
		} catch (Exception e) {
			close();
			result = new Result(Result.Status.Failed);
			result.add("Erro Siases: Metodo createUser: Procedure ASESP901_NOVO_USUARIO: " + e);
			throw new ConnectorException("Erro Siases: Metodo createUser: Procedure ASESP901_NOVO_USUARIO: " + e);

		}
	}
}
