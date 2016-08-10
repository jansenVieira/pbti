import static org.jinterop.dcom.impls.automation.IJIDispatch.IID;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
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

import br.com.pbti.siric.dto.AccountAgreggationSiric;
import br.com.pbti.siric.dto.GroupAgreggationSiric;
import br.com.pbti.siric.vo.AccountSiric;
import br.com.pbti.siric.vo.GroupSiric;
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
@SuppressWarnings("unused")
public class SIRIC extends AbstractConnector {

	private String m_sDriverName;
	private String m_sUser;
	private String m_sPasswd;
	private String m_sUrl;
	private boolean m_bIsDriverLoaded;
	private Connection m_dbConn;

	public CallableStatement cs;
	private static Connection connection;
	public static String URL;
	public static String SENHA;
	public static String USUARIO;
	public static String DRIVE;
	public ResultSet retornoUsuario;
	public ResultSet retornoGrupos;
	public ResultSet retornaConeccao;
	public ResultSet retornaConeccaoEspecifica;

	private Iterator<Map<String, Object>> it;
	private Map<String, Object> account;
	private Map<String, Object> groups;
	private List<Map<String, Object>> resultObjectList;

	public String codUsuario = "codigo_usuario";
	public String noUsuario = "nome";
	public String codUnidadeLotacao = "cod_unid_lotacao";
	public String statusUsuario = "status_usuario";
	public String lotacaoId = "lotacaoid";
	public String nuFuncao = "NU_FUNCAO";
	private String statusGrupo = "STATUS_GRUPO";

	public String numGrupo = "CO_GRUPO";
	public String nomeGrupo = "NO_GRUPO";

	// Drive connect DLL
	private static String urlServerCripto;
	private static String userServerCripto;
	private static String senhaServerCripto;
	private static String nameDLL;
	private static String dominioServerCripto;

	// Cripto
	public static JIComServer comServer;
	public static JISession session;
	public static IJIDispatch comLocator;
	public static String passowrdCriptografada;

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

	private ArrayList<Map<String, Object>> listAgregAccount = new ArrayList<Map<String, Object>>();
	private ArrayList<Map<String, Object>> listAgregGoup = new ArrayList<Map<String, Object>>();
	private AccountAgreggationSiric accountAgreggation = new AccountAgreggationSiric();
	private GroupAgreggationSiric groupAgregation = new GroupAgreggationSiric();
	private ArrayList<String> arrayperfil = new ArrayList<String>();

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
	public SIRIC() {
		super();
		m_iChunkSize = 10;
		m_sLastUser = EMPTY_STR;
		m_sLastGroup = EMPTY_STR;
		m_bIsDriverLoaded = false;
		it = null;

		if (isDebug == true) {
			log.debug("Construtor: <valor m_iChunkSize: " + m_iChunkSize + "> <valor m_sLastUser: " + m_sLastUser
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
	public SIRIC(ConnectorConfig config, openconnector.Log log) throws Exception {
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

			log.debug("Teste Connection");

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

		Map<String, Object> acct = null;
		Map<String, Object> grp = null;

		ResultSet queryResultRead = null;
		Statement stmtRead = null;

		if (isDebug == true) {
			log.debug(" READ");
		}

		try {

			init();
			String query = null;

			if (OBJECT_TYPE_ACCOUNT.equals(this.objectType)) {

				listaConnection(nativeIdentifier);

				if (isDebug == true) {
					log.debug(" READ Filter <" + nativeIdentifier + ">");
				}

				arrayperfil = new ArrayList<String>();

				while (retornaConeccao.next()) {

					acct = new HashMap<String, Object>();

					String userid = retornaConeccao.getString("codigo_usuario");

					if (userid != null) {
						acct.put(codUsuario, userid);
					} else {
						acct.put(codUsuario, "");
					}

					acct.put(noUsuario, retornaConeccao.getString("nome"));
					acct.put(statusUsuario, retornaConeccao.getString("status_usuario"));
					acct.put(lotacaoId, retornaConeccao.getString("lotacaoid"));
					acct.put(codUnidadeLotacao, retornaConeccao.getString("cod_unid_lotacao"));
					acct.put(nuFuncao, retornaConeccao.getString("NU_FUNCAO"));
					// acct.put(numGrupo,
					// retornaConeccao.getString("cod_grupo"));
					// acct.put(statusGrupo,
					// retornaConeccao.getString("cod_status_grupo"));

					String nomGrupo = retornaConeccao.getString("descricao_grupo");

					arrayperfil.add(nomGrupo);

					acct.put(nomeGrupo, arrayperfil);

					String temp = retornaConeccao.getString("status_usuario");
					if (temp.equals("C")) {
						acct.put(openconnector.Connector.ATT_DISABLED, new Boolean(true));
					} else if (temp.equals("A")) {
						acct.put(openconnector.Connector.ATT_DISABLED, new Boolean(false));
					}

				}
			} else if (OBJECT_TYPE_GROUP.equals(this.objectType)) {

				listaGrupo(nativeIdentifier);

				if (isDebug == true) {
					log.debug(" Filter " + nativeIdentifier);
				}

				while (retornoGrupos.next()) {
					grp = new HashMap<String, Object>();

					String chaveGrupo = retornoGrupos.getString("Código do grupo");

					grp.put(numGrupo, retornoGrupos.getString("Código do grupo"));
					grp.put(nomeGrupo, retornoGrupos.getString("Descrição do grupo"));

					if (isDebug == true) {
						log.debug("grp <" + grp.toString() + ">");
					}

				}
			}

		} catch (Exception e) {
			if (isDebug == true) {
				log.debug("Exception occured READ " + e + ">");
			}
			throw new ConnectorException(e);
		}

		// try {
		// // retornaConeccao.close();
		// // retornoGrupos.close();
		// // connection.close();
		//
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }

		if (OBJECT_TYPE_ACCOUNT.equals(this.objectType))
			return acct;
		else if (OBJECT_TYPE_GROUP.equals(this.objectType))
			return grp;
		else
			throw new ConnectorException("Unhandled object type: " + this.objectType);
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

		it = null;

		log.debug("==== Interate ====");

		try {
			init();

			if (isDebug == true) {
				log.debug("Iterator " + filter + ">");
			}

			_filter = filter;
			it = new ArrayList<Map<String, Object>>(getObjectsMap().values()).iterator();
		} catch (Exception e) {
			if (isDebug == true) {
				log.debug("Exception occured ITERATE " + e + ">");
			}
			e.printStackTrace();
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Result create(String nativeIdentifier, List<Item> items)
			throws ConnectorException, ObjectAlreadyExistsException {

		Result result = new Result();

		String codigoUsuario = "", nomeUsuario = "", codUndLot = "", nuFuncao = "", nomeGrupo = "";
		ArrayList listNomeGrupo = new ArrayList();

		codigoUsuario = nativeIdentifier;

		if (isDebug == true) {
			log.debug("Create - ID USUARIO <" + codigoUsuario + ">");
		}

		try {

			init();

			ArrayList valueList = new ArrayList();

			for (openconnector.Item item : items) {

				String name = item.getName();
				String value = item.getValue().toString();

				if (name.equalsIgnoreCase(this.noUsuario)) {

					nomeUsuario = value;

					debug("Create - nomeUsuario <" + nomeUsuario + ">");
					

				}

				if (name.equalsIgnoreCase(this.codUnidadeLotacao)) {

					codUndLot = value;

					debug("Create - codUndLot <" + codUndLot + ">");
					

				}

				if (name.equalsIgnoreCase(this.nuFuncao)) {

					if (value != "") {
						nuFuncao = value;
					} else {

						nuFuncao = null;
					}

					debug("Create - nuFuncao <" + nuFuncao + ">");
					
				}

				if (name.equalsIgnoreCase(this.nomeGrupo)) {

					if (item.getValue() instanceof String) {
						valueList.add(item.getValue());

					} else {
						valueList = (ArrayList) item.getValue();

					}

					listNomeGrupo = valueList;

					debug("Create - nomeGrupo <" + nomeGrupo + ">");
					

				}
			}

			debug("listNomeGrupo" + listNomeGrupo);
			

			listaUsuario(codigoUsuario);

			String verCodUsuario = "";

			while (retornoUsuario.next()) {
				verCodUsuario = retornoUsuario.getString("codigo_usuario");
			}

			if (codigoUsuario.equals(verCodUsuario)) {

				debug("Create - Usuario ja Criado CodigoUsuario <" + codigoUsuario + "> CodUsuario Retorno <"
							+ verCodUsuario + ">");
				

				for (Object separaValue : listNomeGrupo) {
					nomeGrupo = separaValue.toString();

					try {
						debug("CREATE ADD GRUPO <" + "codigoUsuario " + codigoUsuario + "> <nomeGrupo "
									+ nomeGrupo + ">");
						

						criarConnection(codigoUsuario, nomeGrupo);

						String descGrupo = "";

						listaConnectionEspecifica(codigoUsuario, nomeGrupo);

						while (retornaConeccaoEspecifica.next()) {
							descGrupo = retornaConeccaoEspecifica.getString("descricao_grupo");
						}

						debug("CREATE ADD GRUPO <" + "nomeGrupo " + nomeGrupo + "> <descGrupo " + descGrupo
									+ ">");
						

						if (!nomeGrupo.equals(descGrupo)) {
							result = new Result(Result.Status.Failed);

							throw new ConnectorException("Siric metodo: Create: Erro ao inserir grupo ao usuario");
						}

					} catch (Exception e) {
						e.printStackTrace();

						result = new Result(Result.Status.Failed);

						throw new ConnectorException("Siric metodo: Create: Erro ao inserir grupo ao usuario");
					}
				}

				result = new Result(Result.Status.Committed);

			} else {

				debug("Create - Usuario nao Criado CodigoUsuario <" + codigoUsuario + ">");
				

				if (nuFuncao == "" || nuFuncao == null) {
					nuFuncao = null;
				}

				debug("Create Dados " + "<codigoUsuario " + codigoUsuario + "> <nomeUsuario " + nomeUsuario
							+ "> <codUndLot " + codUndLot + "> <nuFuncao " + nuFuncao + "> <codigoUsuario "
							+ codigoUsuario + "> nomeGrupo " + nomeGrupo + ">");
				

				criarAccount(codigoUsuario, nomeUsuario, codUndLot, nuFuncao);

				listaUsuario(codigoUsuario);

				while (retornoUsuario.next()) {
					verCodUsuario = retornoUsuario.getString("codigo_usuario");
				}

				if (!codigoUsuario.equals(verCodUsuario)) {
					result = new Result(Result.Status.Failed);

					throw new ConnectorException("Siric metodo: Create: Erro ao criar um usuario");
				}

				for (Object separaValue : listNomeGrupo) {
					nomeGrupo = separaValue.toString();

					try {
						debug("CREATE ADD <" + "codigoUsuario " + codigoUsuario + "> <nomeGrupo " + nomeGrupo
									+ ">");
						

						criarConnection(codigoUsuario, nomeGrupo);

						String descGrupo = "";

						listaConnectionEspecifica(codigoUsuario, nomeGrupo);

						while (retornaConeccaoEspecifica.next()) {
							descGrupo = retornaConeccaoEspecifica.getString("descricao_grupo");
						}

						debug("descGrupo<" + descGrupo + ">" + "nomeGrupo<" + nomeGrupo + ">");
						
						if (!nomeGrupo.equals(descGrupo)) {
							result = new Result(Result.Status.Failed);

							throw new ConnectorException("Siric metodo: Create: Erro ao inserir grupo ao usuario");
						}

					} catch (Exception e) {
						e.printStackTrace();

						result = new Result(Result.Status.Failed);

						throw new ConnectorException("Siric metodo: Create: Erro ao inserir grupo ao usuario");
					}
				}

				result = new Result(Result.Status.Committed);
			}

		} catch (Exception e) {
			result = new Result(Result.Status.Failed);
			if (log.isDebugEnabled())
				log.debug("Exception occured ", e);
			throw new ConnectorException("Erro Siric: metodo create");
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

		Result result = new Result();

		String codigoUsuario = "", nomeGrupo = "", coUnidLotacao = "", funcao = "", noUsuario = "", status = "";
		ArrayList<String> arrayPerfilSistema = new ArrayList<String>();

		codigoUsuario = nativeIdentifier;

		debug("UPDATE <CodUsuario: " + codigoUsuario + ">");
		

		try {
			init();
		} catch (Exception e1) {
			throw new ConnectorException("SIRIC ERRO METODO UPDATE>");
		}

		if (items != null) {
			for (Item item : items) {

				String name = item.getName();
				String value = item.getValue().toString();
				Item.Operation op = item.getOperation();
				ArrayList valueList = new ArrayList();

				if (item.getValue() instanceof String) {
					valueList.add(item.getValue());

				} else {
					valueList = (ArrayList) item.getValue();

				}

				debug("<Item name " + name + "> <Item value " + value + "> <Item operation " + op + ">");
				

				switch (op) {

				case Add: {

					try {
						init();
					} catch (Exception e1) {
						throw new ConnectorException("ERRO METODO UPDATE>");
					}

					if (name.equalsIgnoreCase(this.nomeGrupo)) {

						for (Object separaValue : valueList) {
							nomeGrupo = separaValue.toString();

							try {
								debug("UPDADE ADD <" + "codigoUsuario " + codigoUsuario + "> <nomeGrupo "
											+ nomeGrupo + ">");
								

								criarConnection(codigoUsuario, nomeGrupo);

								String descGrupo = "";

								listaConnectionEspecifica(codigoUsuario, nomeGrupo);

								while (retornaConeccaoEspecifica.next()) {
									descGrupo = retornaConeccaoEspecifica.getString("descricao_grupo");
								}

								if (!nomeGrupo.equals(descGrupo)) {
									result = new Result(Result.Status.Failed);

									throw new ConnectorException("Siric metodo: UPDATE: Operacao: ADD: Erro ao inserir usuario a grupo");
								}

							} catch (Exception e) {
								result = new Result(Result.Status.Failed);
								throw new ConnectorException("Siric metodo: UPDATE: Operacao: ADD: Erro ao inserir usuario a grupo "+e);
							}
						}

						result = new Result(Result.Status.Committed);

					}
				}

					break;

				case Remove: {

					try {
						init();
					} catch (Exception e1) {
						e1.printStackTrace();
					}

					if (name.equalsIgnoreCase(this.nomeGrupo)) {

						for (Object separaValue : valueList) {
							nomeGrupo = separaValue.toString();

							try {
								debug("UPDADE - REMOVE <" + "codigoUsuario " + codigoUsuario + "> <nomeGrupo "
											+ nomeGrupo + ">");
								
								deletaConnection(codigoUsuario, nomeGrupo);

							} catch (Exception e) {
								e.printStackTrace();
								result = new Result(Result.Status.Failed);
								throw new ConnectorException("Siric metodo: UPDATE: Operacao: Remove: "+e);
							}
						}
						result = new Result(Result.Status.Committed);
					}

				}
					break;
				case Set: {

					log.debug("==== SET ====");
					log.debug(" Name " + name);
					log.debug(" value " + value);

					if (name.equalsIgnoreCase(this.codUnidadeLotacao)) {

						coUnidLotacao = value;
					}

					if (name.equalsIgnoreCase(this.nuFuncao)) {

						funcao = value;
					}

					try {

						log.debug("==== Lista Usuario ====");
						listaUsuario(codigoUsuario);

						while (retornoUsuario.next()) {
							noUsuario = retornoUsuario.getString("nome");

							log.debug("==== nousuario ====" + noUsuario);
						}

					} catch (ClassNotFoundException e) {
						result = new Result(Result.Status.Failed);
						throw new ConnectorException("Siric metodo: UPDATE: Operacao: Set: "+e);
					} catch (SQLException e) {
						throw new ConnectorException("Siric metodo: UPDATE: Operacao: Set: "+e);
					}

					alteraAccount(codigoUsuario, noUsuario, status, coUnidLotacao, funcao);

					result = new Result(Result.Status.Committed);

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

		Result result = new Result();

		try {
			init();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		String codUsuario = "";

		codUsuario = nativeIdentifier;

		if (isDebug == true) {
			log.debug("DELETA USUARIO " + "<CodUsuario " + codUsuario + ">");
		}

		try {
			deletaAccount(codUsuario);

			result = new Result(Result.Status.Committed);

		} catch (Exception e) {
			result = new Result(Result.Status.Failed);
			throw new ConnectorException("Siric metodo: delete: "+e);
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

		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}

		String codUsuario = "", noUsuario = "", statusUsuario = "";

		codUsuario = nativeIdentifier;

		if (isDebug == true) {
			log.debug("ENABLE USUARIO " + "<CodUsuario " + codUsuario + ">");
		}

		try {
			listaUsuario(codUsuario);

			while (retornoUsuario.next()) {
				noUsuario = retornoUsuario.getString("nome");
			}

			statusUsuario = "A";

			if (isDebug == true) {
				log.debug("ENABLE " + "<CodUsuario " + codUsuario + "> <noUsuario " + noUsuario + "> <statusUsuario "
						+ statusUsuario + ">");
			}

			alteraAccount(codUsuario, noUsuario, statusUsuario, null, null);

			result = new Result(Result.Status.Committed);

		} catch (ClassNotFoundException e1) {
			result = new Result(Result.Status.Failed);
			throw new ConnectorException("Siric metodo: enable: "+e1);
		} catch (SQLException e1) {
			result = new Result(Result.Status.Failed);
			throw new ConnectorException("Siric metodo: enable: "+e1);
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

		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}

		String codUsuario = "", noUsuario = "", statusUsuario = "";

		codUsuario = nativeIdentifier;

		if (isDebug == true) {
			log.debug("DISABLE USUARIO " + "<CodUsuario " + codUsuario + ">");
		}

		try {
			listaUsuario(codUsuario);

			while (retornoUsuario.next()) {
				noUsuario = retornoUsuario.getString("nome");
			}

			statusUsuario = "C";

			if (isDebug == true) {
				log.debug("DISABLE " + "<CodUsuario " + codUsuario + "> <noUsuario " + noUsuario + "> <statusUsuario "
						+ statusUsuario + ">");
			}

			alteraAccount(codUsuario, noUsuario, statusUsuario, null, null);

			result = new Result(Result.Status.Committed);

		} catch (ClassNotFoundException e1) {
			result = new Result(Result.Status.Failed);
			throw new ConnectorException("Siric metodo: disable: "+e1);
		} catch (SQLException e1) {
			result = new Result(Result.Status.Failed);
			throw new ConnectorException("Siric metodo: disable: "+e1);
		}

		// Map<String, Object> obj = read(nativeIdentifier);
		// if (null == obj) {
		// throw new ObjectNotFoundException(nativeIdentifier);
		// }

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

		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
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

		Result result = new Result();

		String statusUsuario = null;

		try {
			init();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		String codUsuario = "", codSenha = "", clearPassword = "";

		clearPassword = newPassword;

		codUsuario = nativeIdentifier;

		if (isDebug == true) {
			log.debug("SET PASSWORD " + "<CodUsuario " + codUsuario + ">");
		}

		try {
			init();

			listaUsuario(codUsuario);

			while (retornoUsuario.next()) {
				noUsuario = retornoUsuario.getString("nome");
				statusUsuario = retornoUsuario.getString("status_usuario");
			}

			if (statusUsuario.equals("C")) {
				statusUsuario = "A";

				if (isDebug == true) {
					log.debug("ENABLE USUARIO PASSWORD " + "<CodUsuario " + codUsuario + "> <noUsuario " + noUsuario
							+ "> <statusUsuario " + statusUsuario + ">");
				}

				alteraAccount(codUsuario, noUsuario, statusUsuario, null, null);
			}

			encripta(clearPassword);

			if (passowrdCriptografada.isEmpty()) {
				if (isDebug == true) {
					log.debug("PASSWORD VAZIO USUARIO <" + codUsuario + "> PASSWORD <" + passowrdCriptografada + ">");
				}

				result = new Result(Result.Status.Failed);
				throw new ConnectorException("Siric metodo: setPassword: Erro na Criptografia ");
			} else {
				codSenha = passowrdCriptografada;

				if (isDebug == true) {
					log.debug("PASSWORD USUARIO <" + codUsuario + "> PASSWORD <" + codSenha + ">");
				}

				alteraSenha(codUsuario, codSenha);

				result = new Result(Result.Status.Committed);
			}

		} catch (SecurityException e2) {
			result = new Result(Result.Status.Failed);
			throw new ConnectorException("Siric metodo: setPassword: "+e2);
		} catch (JIException e2) {
			result = new Result(Result.Status.Failed);
			throw new ConnectorException("Siric metodo: setPassword: "+e2);
		} catch (IOException e2) {
			result = new Result(Result.Status.Failed);
			throw new ConnectorException("Siric metodo: setPassword: "+e2);
		} catch (ClassNotFoundException e) {
			result = new Result(Result.Status.Failed);
			throw new ConnectorException("Siric metodo: setPassword: "+e);
		} catch (SQLException e) {
			result = new Result(Result.Status.Failed);
			throw new ConnectorException("Siric metodo: setPassword: "+e);
		} catch (Exception e) {
			result = new Result(Result.Status.Failed);
			throw new ConnectorException("Siric metodo: setPassword: "+e);
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
			if (connection != null) {
				connection.close();

				log.debug("CONECTION CLOSE");
			}

		} catch (SQLException e) {

			throw new ConnectorException("Erro ao fechar a Conexao : "+e);
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

		// Class.forName(m_sDriverName);
		//
		// m_dbConn = DriverManager.getConnection(m_sUrl, m_sUser, m_sPasswd);
		// m_bIsDriverLoaded = true;

		// }
	}

	public void debug(String mensagem) {
		boolean debug = config.getBoolean("debug");

		if (debug == true) {
			log.debug(mensagem);
		}

	}

	public Iterator<Map<String, Object>> IterateNextPage(Filter filter) {
		try {

			if (OBJECT_TYPE_ACCOUNT.equals(this.objectType)) {

				debug("IterateNextPage Account");

				if (listAgregAccount.size() == 0) {
					log.debug("end of group iteration...");
				} else {
					it = (getObjectsMap().values()).iterator();
				}

			} else if (OBJECT_TYPE_GROUP.equals(this.objectType)) {
				debug("Iterete Group");

				if (listAgregGoup.size() == 0) {
					debug("end of group iteration...");
				} else {
					it = (getObjectsMap().values()).iterator();
				}
			}
		} catch (Exception e) {
			throw new ConnectorException("Erro metodo: IterateNextPage: "+e);
		}
		return it;
	}

//	ArrayList<String> arrayperfil = new ArrayList<String>();
//
//	String chaveMapa = "";
//
//	AccountAgreggationSiric accountAgreggation = new AccountAgreggationSiric();
//	GroupAgreggationSiric groupAgregation = new GroupAgreggationSiric();
//	public int tamanhoListaAccount;
//	public int tamanhoListaGroup;
//
//	public static void main(String[] args) throws Exception {
//
//		Map<String, Map<String, Object>> m_acctsMap = new HashMap<String, Map<String, Object>>();
//
//		AccountAgreggationSiric accountAgreggation = new AccountAgreggationSiric();
//
//		int tamanhoListaAccount;
//
//		accountAgreggation.consulta("");
//
//		tamanhoListaAccount = (accountAgreggation.getSizeListUser() / 10) + 1;
//
//		for (int i = 0; i < tamanhoListaAccount; i++) {
//
//			m_acctsMap = accountAgreggation.agregGeral();
//
//			System.out.println("Map Resuldado " + m_acctsMap);
//
//		}
//
//	}

	private Map<String, Map<String, Object>> getObjectsMap() throws Exception {

		debug("Metodo Get Object Map");

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

					} else {

						accountAgreggation.setUrlServer(config.getString("url"));
						accountAgreggation.setDriveClass(config.getString("driverClass"));
						accountAgreggation.setUserServer(config.getString("user"));
						accountAgreggation.setPasswordServer(config.getString("password"));

						accountAgreggation.consulta("");

						listAgregAccount = accountAgreggation.getListUser();
					}
				} else {
					for (int i = 0; i < 10; i++) {
						listAgregAccount.remove(0);
					}
					contador = 0;
				}

				for (Map<String, Object> mapUser : listAgregAccount) {
					if (contador < 10) {

						AccountSiric acc = (AccountSiric) mapUser.get("Account");
						dados = new HashMap<String, Object>();

						dados.put("codigo_usuario", acc.getCoUsuario().trim());
						dados.put("nome", acc.getNoUsuario().trim());
						dados.put("cod_unid_lotacao", acc.getCodUnidLotacao().trim());
						dados.put("status_usuario", acc.getStatus().trim());
						dados.put("lotacaoid", acc.getLotacaoId().trim());
						dados.put("NU_FUNCAO", acc.getNuFuncao().trim());
						dados.put("NO_GRUPO", mapUser.get("NO_GRUPO"));

						if (acc.getStatus().equals("C")) {
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
					}
				} else {
					for (int i = 0; i < 10; i++) {
						listAgregGoup.remove(0);
					}
					contador = 0;
				}

				for (Map<String, Object> mapGroup : listAgregGoup) {
					if (contador < 10) {

						GroupSiric gpr = (GroupSiric) mapGroup.get("Group");
						dados = new HashMap<String, Object>();

						dados.put("CO_GRUPO", gpr.getNumGrupo().trim());
						dados.put("NO_GRUPO", gpr.getDescGrupo().trim());

						m_groupsMap.put(gpr.getNumGrupo().trim(), dados);

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

			throw new ConnectorException("Erro metodo: getAccount: "+e);
		}

		throw new ConnectorException("Unhandled object type: " + this.objectType);

	}

//	HashMap<String, Object> acctValor;

//	public void guardaValorAct() throws SQLException {
//
//		if (isDebug == true) {
//			log.debug("Get Guarda Valor ACT....");
//		}
//
//		acctValor = new HashMap<String, Object>();
//
//		String userid = retornaConeccao.getString("codigo_usuario");
//		String chaveMapa = userid;
//		acctValor.put(codUsuario, userid);
//		acctValor.put(noUsuario, retornaConeccao.getString("nome"));
//		acctValor.put(statusUsuario, retornaConeccao.getString("status_usuario"));
//		acctValor.put(lotacaoId, retornaConeccao.getString("lotacaoid"));
//		acctValor.put(codUnidadeLotacao, retornaConeccao.getString("cod_unid_lotacao"));
//		acctValor.put(nuFuncao, retornaConeccao.getString("NU_FUNCAO"));
//		// acctValor.put(numGrupo, retornaConeccao.getString("cod_grupo"));
//		String nomeGrupo = retornaConeccao.getString("descricao_grupo");
//		// acctValor.put(statusGrupo,
//		// retornaConeccao.getString("cod_status_grupo"));
//
//		arrayperfil.add(nomeGrupo);
//		acctValor.put(nomeGrupo, arrayperfil);
//
//		String temp = retornaConeccao.getString("status_usuario");
//		if (temp.equals("C")) {
//			acctValor.put(openconnector.Connector.ATT_DISABLED, new Boolean(true));
//		} else if (temp.equals("A")) {
//			acctValor.put(openconnector.Connector.ATT_DISABLED, new Boolean(false));
//		}
//	}

	// conexao ao banco
	protected CallableStatement callProcedure(String procedure) {

		try {
			init();

			valorConexao();

			cs = getConnection().prepareCall(procedure);

		} catch (ClassNotFoundException e) {
			throw new ConnectorException("Erro metodo: callProcedure: "+e);
		} catch (SQLException e) {
			throw new ConnectorException("Erro metodo: callProcedure: "+e);
		} catch (Exception e) {
			throw new ConnectorException("Erro metodo: callProcedure: "+e);

		}

		return cs;
	}

	public void encripta(String clearPassword) throws JIException, SecurityException, IOException {

		Result result = new Result();

		try {

			if (isDebug == true) {
				log.debug("Encripta....");
			}

			init();

			connectServerCriptografia();

			JISystem.setAutoRegisteration(true);

			Object[] paramsEncripto = new Object[] { new JIString(clearPassword) };

			JIVariant[] resultsCript = comLocator.callMethodA("Criptografar", paramsEncripto);

			passowrdCriptografada = resultsCript[0].getObjectAsString2().toString();

			if (isDebug == true) {

				log.debug("passowrdCriptografada " + passowrdCriptografada);

			}

		} catch (Exception e) {
			result = new Result(Result.Status.Failed);
			throw new ConnectorException("Erro metodo: encripta: "+e);

		}

	}

	public void connectServerCriptografia() throws JIException, SecurityException, IOException {

		try {

			if (isDebug == true) {
				log.debug("ConnectServerCriptografia....");
			}

			init();

		} catch (Exception e1) {
			throw new ConnectorException("ERRO ENCRIPTA SENHA");
		}

		// JISystem.setInBuiltLogHandler(true);

		session = JISession.createSession(dominioServerCripto, userServerCripto, senhaServerCripto);

		try {
			comServer = new JIComServer(JIProgId.valueOf(nameDLL), urlServerCripto, session);
		} catch (JIException e) {
			comServer = new JIComServer(JIProgId.valueOf(nameDLL), urlServerCripto, session);
		} catch (Exception e) {
			comServer = new JIComServer(JIProgId.valueOf(nameDLL), urlServerCripto, session);
		}

		comLocator = (IJIDispatch) JIObjectFactory.narrowObject(comServer.createInstance().queryInterface(IID));

	}

	// conexao ao banco
	protected Connection getConnection() throws SQLException, ClassNotFoundException {

		try {

			init();

			if (connection == null) {
				Class.forName(DRIVE);

				connection = DriverManager.getConnection(URL, USUARIO, SENHA);
			}

		} catch (Exception e) {

			throw new ConnectorException("Erro metodo: getConnection: "+e);
		}

		return connection;
	}

	// Obtem os dados para acesso ao banco
	public void valorConexao() {
		try {

			init();
			URL = config.getString("url");
			SENHA = config.getString("password");
			USUARIO = config.getString("user");
			DRIVE = config.getString("driverClass");
		} catch (Exception e) {
			throw new ConnectorException("Erro metodo: valorConexao: "+e);
		}
	}

	public void listaUsuario(String codUsuario) throws SQLException, ClassNotFoundException {
		try {

			if (isDebug == true) {
				log.debug("Lista Usuario FILTRO <" + codUsuario + ">");
			}

			init();

			cs = callProcedure("{call RICSPI09_CONSULTA_USUARIO(?) }");
			cs.setString(1, codUsuario);
			cs.execute();
			retornoUsuario = cs.getResultSet();

		} catch (Exception e) {
			throw new ConnectorException("Erro metodo: listaUsuario: procedure: RICSPI09_CONSULTA_USUARIO "+e);
		}
	}

	public void listaConnection(String codUsuario) throws SQLException, ClassNotFoundException {

		try {
			if (isDebug == true) {
				log.debug("Lista Conexao....");
			}

			init();

			cs = callProcedure("{call RICSPT17_CONS_GRU_USU_SIGAL(?, ?) }");
			cs.setString(1, codUsuario);
			cs.setString(2, null);
			cs.execute();
			retornaConeccao = cs.getResultSet();

		} catch (Exception e) {
			throw new ConnectorException("Erro metodo: listaConnection: procedure: RICSPT17_CONS_GRU_USU_SIGAL "+e);
		}

	}

	public void listaConnectionEspecifica(String codUsuario, String nomeGrupo)
			throws SQLException, ClassNotFoundException {

		try {
			if (isDebug == true) {
				log.debug("Lista Conexao Especifica....");
				log.debug("FILTRO <codUsuario " + codUsuario + "> <nomeGrupo " + nomeGrupo + ">");
			}

			init();

			cs = callProcedure("{call RICSPT17_CONS_GRU_USU_SIGAL(?, ?) }");
			cs.setString(1, codUsuario);
			cs.setString(2, nomeGrupo);
			cs.execute();
			retornaConeccaoEspecifica = cs.getResultSet();

		} catch (Exception e) {
			throw new ConnectorException("Erro metodo: listaConnectionEspecifica: procedure: RICSPT17_CONS_GRU_USU_SIGAL "+e);
		}

	}

	public void listaGrupo(String noGrupo) {

		Result result = new Result();

		try {

			if (isDebug == true) {
				log.debug("Lista Grupos....");
			}

			init();

			cs = callProcedure("{call RICSPT20_CONSULTA_GRU_SIGAL(?) }");

			if (noGrupo.equals("*")) {
				cs.setString(1, null);
			} else {
				cs.setString(1, noGrupo);
			}
			cs.execute();

			retornoGrupos = cs.getResultSet();

		} catch (SQLException e) {
			result = new Result(Result.Status.Failed);
			throw new ConnectorException("Erro metodo: listaGrupo: procedure: RICSPT20_CONSULTA_GRU_SIGAL "+e);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// public void listaGrupo(int codGrupo) {
	//
	// Result result = new Result();
	//
	// try {
	// cs = callProcedure("{call RICSPT20_CONSULTA_GRU_SIGAL(?) }");
	//
	// if (codGrupo == 0) {
	// cs.setNull(1, Types.INTEGER);
	// } else {
	// cs.setNull(1, codGrupo);
	// }
	// cs.execute();
	// retornoGrupos = cs.getResultSet();
	// } catch (ClassNotFoundException e) {
	// e.printStackTrace();
	// result = new Result(Result.Status.Failed);
	// result.add("TIPO APP SIRIC " + e.getMessage());
	// } catch (SQLException e) {
	// e.printStackTrace();
	// result = new Result(Result.Status.Failed);
	// result.add("TIPO APP SIRIC " + e.getMessage());
	// result.add("TIPO APP SIRIC " + e.getErrorCode());
	// }
	//
	// }

	public void criarAccount(String codUsuario, String noUsuario, String codUnidaLotacao, String NumFuncao) {

		Result result = new Result();

		String ADD_ACCOUNT = "I";

		try {

			if (isDebug == true) {
				log.debug("Criar Conta....");
				log.debug("Valor Criar Conta <ADD_ACCOUNT " + ADD_ACCOUNT + "> <codUsuario " + codUsuario
						+ "> <noUsuario " + noUsuario + "> <codUnidaLotacao " + codUnidaLotacao + "> <NumFuncao "
						+ NumFuncao + ">");
			}

			if (codUnidaLotacao.isEmpty()) {
				result = new Result(Result.Status.Failed);
				throw new ConnectorException("Erro metodo: criarAccount: Codigo Unidade Vazio ");
			}

			if (NumFuncao.isEmpty()) {
				result = new Result(Result.Status.Failed);
				throw new ConnectorException("Erro metodo: criarAccount: Codigo Funcao Vazio ");
			}

			init();

			cs = callProcedure("{call RICSPT16_CADASTRA_USU_SIGAL(?,?,?,?,?,?)}");
			cs.setString(1, ADD_ACCOUNT);
			cs.setString(2, codUsuario);
			cs.setString(3, noUsuario);
			cs.setInt(4, Integer.parseInt(codUnidaLotacao));
			cs.setInt(5, Integer.parseInt(NumFuncao));
			cs.setNull(6, Types.CHAR);
			cs.execute();
			// cs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			throw new ConnectorException("ERRO DE CONEXAO AO BANCO");
		} catch (Exception e) {
			throw new ConnectorException("ERRO DE CONEXAO AO BANCO");
		}

	}

	public void criarConnection(String codUsuario, String nomeGrupo) {

		Result result = new Result();

		String ADD_CONNECTION = "V";
		try {

			if (isDebug == true) {
				log.debug("Criar Conexao....");
				log.debug("Valor Criar Conta <codUsuario " + codUsuario + "> <nomeGrupo " + nomeGrupo
						+ "> <ADD_CONNECTION " + ADD_CONNECTION + ">");
			}

			init();

			cs = callProcedure("{call RICSPT18_CAD_GRU_USU_SIGAL(?,?,?)}");
			cs.setString(1, codUsuario);
			cs.setString(2, nomeGrupo);
			cs.setString(3, ADD_CONNECTION);
			cs.execute();
			// cs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			result.add("TIPO APP SIRIC " + e.getMessage());
			result.add("TIPO APP SIRIC " + e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void deletaConnection(String codUsuario, String nomeGrupo) {

		Result result = new Result();

		String DELETE_CONNECTION = "D";
		try {

			if (isDebug == true) {
				log.debug("Deleta Conexao....");
			}

			init();
			cs = callProcedure("{call RICSPT18_CAD_GRU_USU_SIGAL(?,?,?)}");
			cs.setString(1, codUsuario);
			cs.setString(2, nomeGrupo);
			cs.setString(3, DELETE_CONNECTION);
			cs.execute();
			// cs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			result.add("TIPO APP SIRIC " + e.getMessage());
			result.add("TIPO APP SIRIC " + e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void deletaAccount(String codUsuario) {

		Result result = new Result();

		String DELETE_ACCOUNT = "E";
		String EXCLUIDO = "E";

		try {

			if (isDebug == true) {
				log.debug("Deleta Conta....");
			}

			init();
			cs = callProcedure("{call RICSPT16_CADASTRA_USU_SIGAL(?,?,?,?,?,?)}");
			cs.setString(1, DELETE_ACCOUNT);
			cs.setString(2, codUsuario);
			cs.setNull(3, Types.VARCHAR);
			cs.setNull(4, Types.INTEGER);
			cs.setNull(5, Types.SMALLINT);
			cs.setString(6, EXCLUIDO);
			cs.execute();
			// cs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			result.add("TIPO APP SIRIC " + e.getMessage());
			result.add("TIPO APP SIRIC " + e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void alteraAccount(String codUsuario, String noUsuario, String statusUsuario, String lotacao,
			String funcao) {

		Result result = new Result();

		String UPDATE_ACCOUNT = "A";

		try {

			if (isDebug == true) {
				log.debug("Alterar Conta....");
			}

			init();
			cs = callProcedure("{call RICSPT16_CADASTRA_USU_SIGAL(?,?,?,?,?,?)}");
			cs.setString(1, UPDATE_ACCOUNT);
			cs.setString(2, codUsuario);
			cs.setString(3, noUsuario);

			if (lotacao != null && !lotacao.isEmpty()) {
				int vrLotacao = Integer.parseInt(lotacao);

				cs.setInt(4, vrLotacao);

			} else {

				cs.setNull(4, Types.INTEGER);

			}

			if (funcao != null && !funcao.isEmpty()) {
				int vrFuncao = Integer.parseInt(funcao);

				cs.setInt(5, vrFuncao);

			} else {

				cs.setNull(5, Types.INTEGER);

			}

			if (statusUsuario != null && !statusUsuario.isEmpty()) {

				cs.setString(6, statusUsuario);

			} else {

				cs.setString(6, null);

			}
			cs.execute();
			// cs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			result.add("TIPO APP SIRIC " + e.getMessage());
			result.add("TIPO APP SIRIC " + e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void alteraSenha(String codUsuario, String senha) {

		Result result = new Result();

		int ACAO = 3;
		String RESPONSAVEL = "MASTER";

		try {

			if (isDebug == true) {
				log.debug("Alterar Senha....");
			}

			init();
			cs = callProcedure("{call RICSPI03_ATUALIZA_SENHA(?, ?, ?, ?) }");
			cs.setString(1, codUsuario);
			cs.setInt(2, ACAO);
			cs.setString(3, senha);
			cs.setString(4, RESPONSAVEL);
			cs.execute();
			// cs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			result = new Result(Result.Status.Failed);
			result.add("TIPO APP SIRIC " + e.getMessage());
			result.add("TIPO APP SIRIC " + e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
