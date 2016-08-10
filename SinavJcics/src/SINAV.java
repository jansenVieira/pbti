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

import br.com.pbti.dto.AccountAgreggationSinav;
import br.com.pbti.dto.GroupAgreggationSinav;
import br.com.pbti.vo.AccountSinav;
import br.com.pbti.vo.GroupSinav;
import br.gov.caixa.seguranca.LoginException;
import br.gov.caixa.util.jcicsconnect.ExecuteException;
import br.gov.caixa.util.jcicsconnect.JCicsConnect;
import br.gov.caixa.util.jcicsconnect.LeMensagemException;
import br.gov.caixa.util.jcicsconnect.MensagemCics;
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
public class SINAV extends AbstractConnector {

	// ////////////////////////////////////////////
	// ////////////////////////////
	//
	// INNER CLASS
	//
	// //////////////////////////////////////////////////////////////////////////

	private String m_sDriverName;
	private String m_sUser;
	private String m_sPasswd;
	private String m_sUrl;
	private String nomeServidorSics;
	private String numPortaSics;
	private String userNameSics;
	private String passwordSiscs;

	private boolean m_bIsDriverLoaded;
	private Connection m_dbConn;

	private Iterator<Map<String, Object>> it;
	// private Map<String, Object> acct;

	// Variavel acct
	private String CD_USUARIO = "CD_USUARIO";
	private String NO_USUARIO = "NO_USUARIO";
	private String CD_UNIDADE = "CD_UNIDADE";
	private String CD_SIS_DEFAUL = "CD_SIS_DEFAUL";
	private String CD_IMPR_REMOT = "CD_IMPR_REMOT";
	private String CD_SIT_USU = "CD_SIT_USU";
	private String CD_SUREG_LOT = "CD_SUREG_LOT";
	private String CD_CEADM_AUT = "CD_CEADM_AUT";
	private String CD_SUREG_AUT = "CD_SUREG_AUT";
	private String CD_UNID_AUT = "CD_UNID_AUT";
	private String ID_DIRETORIA = "ID_DIRETORIA";
	private String CD_TERMINAL = "CD_TERMINAL";
	private String ID_STATUS = "ID_STATUS";
	private String ID_CEF = "ID_CEF";
	private String CD_NO_REMOT = "CD_NO_REMOT";
	private String CD_FUNCAO = "CD_FUNCAO";
	private String CO_SEGMENTO = "CO_SEGMENTO";
	private String NU_CNPJ = "NU_CNPJ";
	private String NU_MATR_EMP = "NU_MATR_EMP";
	private String NU_CA_EN_FISICA = "NU_CA_EN_FISICA";
	private String sisPerfil = "SIS_PERFIL";

	// Variavel Group
	private String CD_SISTEMA = "CD_SISTEMA";
	private String CD_PERFIL = "CD_PERFIL";
	private String NO_PERFIL = "NO_PERFIL";

	// Constante Conta
	private String consultaNacional = "consulta_nacional";
	private String atualizacaoNacional = "atualizacao_nacional";
	private String nivelSeguranca = "nivel_seguranca";
	// private String cdUsuario = "CD_USUARIO";
	private String noUsuario = "NO-USUARIO";
	private String cdUnidade = "CD_UNIDADE";
	private String cdSisDefaul = "CD_SIS_DEFAUL";
	private String cdSistUsua = "CD_SIT_USU";
	private String cdSuregLot = "CD_SUREG_LOT";
	private String idDeretoria = "ID_DIRETORIA";
	private String idVisaoDeretoria = "ID_VISAO_DIRETORIA";
	private String idStatus = "ID_STATUS";
	private String idCef = "ID_CEF";
	private String cdFuncao = "CD_FUNCAO";

	// Variavel Sics
	public JCicsConnect jcisConnect;
	public MensagemCics mensage;

	// Variavel MAPs Arrays
	public Map<String, Object> dadosUsuario;
	@SuppressWarnings("rawtypes")
	public List arrayGrupoUsuario = new ArrayList();

	// Part Variaveis
	private Filter _filter;
	private Map<String, Map<String, Object>> m_acctsMap = new HashMap<String, Map<String, Object>>();
	private Map<String, Map<String, Object>> m_groupsMap = new HashMap<String, Map<String, Object>>();

	// Variavel Debug
	private boolean isDebug;

	// variavel Result
	public ResultSet queryResult = null;
	public ResultSet queryResultGrupo = null;

	public AccountAgreggationSinav accountAgreggation = new AccountAgreggationSinav();
	public GroupAgreggationSinav groupAgregation = new GroupAgreggationSinav();
	public int tamanhoListaAccount;
	public int tamanhoListaGroup;

	ArrayList<String> sisperfil = new ArrayList<String>();
	String chaveMapa = "";

	private ArrayList<Map<String, Object>> listAgregAccount = new ArrayList<Map<String, Object>>();
	private ArrayList<Map<String, Object>> listAgregGoup = new ArrayList<Map<String, Object>>();
	private int contador = 0;

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
	public SINAV() {
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
	public SINAV(ConnectorConfig config, openconnector.Log log) throws Exception {
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
	// if (OBJECT_TYPE_acct.equals(this.objectType))
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

				return grp;

			}
		} catch (Exception e) {
			debug("Exception occured " + e + ">");

			throw new ConnectorException(e);
		}

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

		debug("METODO ITERATE");

		it = null;

		try {
			_filter = filter;
			it = new ArrayList<Map<String, Object>>(getObjectsMap().values()).iterator();
		} catch (Exception e) {
			debug("Erro SINAV metodo Iterator " + e + ">");
			throw new ConnectorException("Erro SINAV metodo Iterator " + e);
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

		debug("Metodo Create ");

		Result result = new Result();

		ArrayList<String> arrayPerfilSistema = new ArrayList<String>();

		String consultaNaci = "", atualizacaoNac = "", nivelSeg = "", codUsuario = "", nomeUsuario = "";
		String codigoUnidade = "", codigoSisDefaul = "", codigoSistUsua = "", codigoSuregLot = "", idDeret = "";
		String status = "", cef = "", funcao = "", sistema = "", perfil = "", matricula = "", idVisaoDerct = "";

		codUsuario = nativeIdentifier;

		debug("CREATE <codUsuario: " + codUsuario + ">");

		try {

			for (openconnector.Item item : items) {

				String name = item.getName();
				String value = item.getValue().toString();

				debug("NAME: " + name + ">");
				debug("value: " + value + ">");

				if (name.equalsIgnoreCase(consultaNacional)) {

					consultaNaci = value;
				}

				if (name.equalsIgnoreCase(atualizacaoNacional)) {

					atualizacaoNac = value;
				}

				if (name.equalsIgnoreCase(nivelSeguranca)) {

					nivelSeg = value;
				}

				if (name.equalsIgnoreCase(noUsuario)) {

					nomeUsuario = value;
				}

				if (name.equalsIgnoreCase(cdUnidade)) {

					codigoUnidade = value;
				}

				if (name.equalsIgnoreCase(cdSisDefaul)) {

					codigoSisDefaul = value;
				}

				if (name.equalsIgnoreCase(cdSistUsua)) {

					codigoSistUsua = value;
				}

				if (name.equalsIgnoreCase(cdSuregLot)) {

					codigoSuregLot = value;
				}

				if (name.equalsIgnoreCase(idDeretoria)) {

					idDeret = value;
				}

				if (name.equalsIgnoreCase(idVisaoDeretoria)) {

					idVisaoDerct = value;
				}

				if (name.equalsIgnoreCase(idStatus)) {

					status = value;
				}

				if (name.equalsIgnoreCase(idCef)) {

					cef = value;
				}
				if (name.equalsIgnoreCase(cdFuncao)) {

					funcao = value;
				}

				if (codUsuario.startsWith("C")) {
					matricula = codUsuario.substring(1);
				} else {

					matricula = "00000000";
				}

				if (name.equalsIgnoreCase(sisPerfil)) {

					ArrayList valueList = new ArrayList();

					if (item.getValue() instanceof String) {
						valueList.add(item.getValue());

					} else {
						valueList = (ArrayList) item.getValue();

					}

					if (valueList.size() > 1) {

						arrayPerfilSistema.addAll(valueList);

					} else {

						String sistemaPerfis = valueList.get(0).toString();

						arrayPerfilSistema.add(sistemaPerfis);
					}
				}
			}

			if (idDeret.equals("")) {
				idDeret = "N";
			} else {
				idDeret = "S";
			}

			if (codigoUnidade.equals("")) {
				codigoUnidade = "0";
			}

			if (codigoSuregLot.equals("")) {
				codigoSuregLot = "0";
			}

			if (funcao.equals("")) {
				funcao = "0";
			}

			if (atualizacaoNac.equals("true")) {
				atualizacaoNac = "S";
			} else {
				atualizacaoNac = "N";
			}

			if (consultaNaci.equals("true")) {
				consultaNaci = "S";
			} else {
				consultaNaci = "N";
			}

			debug("idVisaoDerct: " + idVisaoDerct + ">");

			if (idVisaoDerct.equals("true")) {
				idVisaoDerct = "S";
			} else {
				idVisaoDerct = "N";
			}

			debug("idVisaoDerct: " + idVisaoDerct + ">");

			for (String sistemPerfis : arrayPerfilSistema) {

				String[] array = sistemPerfis.split("/");

				sistema = array[0];
				perfil = array[1];

				// perfil = recuperaCodPerfil(perfil, sistema);

				boolean usarioExiste = consultaCreate(codUsuario);

				if (usarioExiste == true) {

					String noUsuario = "", codUnidade = "", cdSistemDefaul = "";
					String codSisUsu = "", codSuregLota = "", idDiretoria = "", idStat = "", idCf = "";

					Map<String, Object> dUsuario = dadosUsuario(codUsuario);

					noUsuario = dUsuario.get(NO_USUARIO).toString();
					codUnidade = dUsuario.get(CD_UNIDADE).toString();
					cdSistemDefaul = dUsuario.get(CD_SIS_DEFAUL).toString();
					codSisUsu = dUsuario.get(CD_SIT_USU).toString();
					codSuregLota = dUsuario.get(CD_SUREG_LOT).toString();
					idDiretoria = dUsuario.get(ID_DIRETORIA).toString();
					idCf = dUsuario.get(ID_CEF).toString();

					idStat = "A";

					debug("CREATE USUARIO EXISTE ALTERANDO STATUS <codUsuario: " + codUsuario + "> <noUsuario: "
							+ noUsuario + "> <codUnidade: " + codUnidade + "> <cdSistemDefaul: " + cdSistemDefaul
							+ "> <codSisUsu: " + codSisUsu + "> <codSuregLota: " + codSuregLota + "> <idDiretoria: "
							+ idDiretoria + "> <idStat: " + idStat + "> <idCf: " + idCf + ">");

					trataDadosAtualizacao(codUsuario, noUsuario, codUnidade, cdSistemDefaul, codSisUsu, codSuregLota,
							idDiretoria, idStat, idCf);

				} else {

					debug("CREATE USUARIO CRIAR USUARIO <codUsuario: " + codUsuario + "> <nomeUsuario: " + nomeUsuario
							+ "> <codigoUnidade: " + codigoUnidade + "> <codigoSisDefaul: " + codigoSisDefaul
							+ "> <codigoSistUsua: " + codigoSistUsua + "> <codigoSuregLot: " + codigoSuregLot
							+ "> <idDeret: " + idDeret + "> <status: " + status + "> <cef: " + cef + "> <funcao: "
							+ funcao + "> <matricula" + matricula + ">");

					trataDadoCreate(codUsuario, nomeUsuario, codigoUnidade, codigoSisDefaul, codigoSistUsua,
							codigoSuregLot, idDeret, status, cef, funcao, matricula);
				}

				debug("CREATE USUARIO CRIAR CONEXAO <sistema: " + sistema + "> <codUsuario: " + codUsuario
						+ "> <perfil: " + perfil + "> <nivelSeg: " + nivelSeg + "> <codigoSuregLot: " + codigoSuregLot
						+ "> <codigoUnidade: " + codigoUnidade + "> <idVisaoDerct: " + idVisaoDerct + "> <status: "
						+ status + "> <atualizacaoNac: " + atualizacaoNac + "> <consultaNaci: " + consultaNaci + ">");

				

				boolean usarioPossuiGrupo = consultaGrupoUsuario(sistema, codUsuario);

				if (usarioPossuiGrupo == true) {
					debug("Usuario possui Grupo" + usarioPossuiGrupo);

					trataDadoDesvincularConexao(sistema, codUsuario);
				}
				
				trataDadoConexao(sistema, codUsuario, perfil, nivelSeg, codigoSuregLot, codigoUnidade, idVisaoDerct,
						status, atualizacaoNac, consultaNaci);

				boolean usuarioCriado = consultaCreate(codUsuario);
				boolean usuarioNoGrupo = consultaUsuarioGrupo(codUsuario, sistema, perfil);

				if (usuarioCriado == true && usuarioNoGrupo == true) {
					result = new Result(Result.Status.Committed);
				}
				if (usuarioCriado == false || usuarioNoGrupo == false) {
					throw new ConnectorException("Erro SINAV metodo: Create: Usuario nao foi criado com sucesso ");
				}
			}

		} catch (Exception e) {
			throw new ConnectorException("Erro SINAV metodo: Create: " + e);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Result update(String nativeIdentifier, List<Item> items) throws ConnectorException, ObjectNotFoundException {

		Result result = new Result();

		debug("=====UPDATE====");

		List<String> arrayPerfilSistema = new ArrayList<String>();

		String codUsuario = "", nivelSeg = "", codigoSuregLot = "", codigoUnidade = "", idVisaoDeret = "", status = "",
				atualizacaoNac = "", consultaNaci = "", sistema = "", perfil = "";

		codUsuario = nativeIdentifier;

		if (items != null) {
			for (Item item : items) {
				String name = item.getName();
				String value = item.getValue().toString();
				Item.Operation op = item.getOperation();

				switch (op) {
				case Add: {

					try {
						boolean usuarioCriado = consultaCreate(codUsuario);

						debug("INICIO ADD name " + name);
						debug("INICIO ADD value " + value);

						if (usuarioCriado == true) {

							if (name.equalsIgnoreCase(nivelSeguranca)) {
								nivelSeg = value;
							}

							if (name.equalsIgnoreCase(atualizacaoNacional)) {

								atualizacaoNac = value;
							}

							if (name.equalsIgnoreCase(consultaNacional)) {

								consultaNaci = value;
							}

							if (name.equalsIgnoreCase(idVisaoDeretoria)) {

								idVisaoDeret = value;
							}

							if (name.equalsIgnoreCase(sisPerfil)) {
								ArrayList valueList = new ArrayList();

								arrayPerfilSistema = new ArrayList<String>();

								if (item.getValue() instanceof String) {
									valueList.add(item.getValue());
								} else {
									valueList = (ArrayList) item.getValue();
								}

								if (valueList.size() > 1) {

									arrayPerfilSistema.addAll(valueList);

								} else {

									String sistemaPerfis = valueList.get(0).toString();

									arrayPerfilSistema.add(sistemaPerfis);
								}
							}

							if (nivelSeg.equals("") || atualizacaoNac.equals("") || consultaNaci.equals("")
									|| arrayPerfilSistema.equals("") || idVisaoDeret.equals("")) {

								debug("Dados Ainda Nulo nivelSeg " + nivelSeg);
								debug("Dados Ainda Nulo atualizacaoNac " + atualizacaoNac);
								debug("Dados Ainda Nulo atualizacaoNac " + consultaNaci);
								debug("Dados Ainda Nulo arrayPerfilSistema " + arrayPerfilSistema.toString());
								debug("Dados Ainda Nulo idVisaoDeret " + idVisaoDeret.toString());

								continue;

							} else {

								debug("Dados Preenchidos nivelSeg " + nivelSeg);
								debug("atualizacaoNac " + atualizacaoNac);
								debug("consultaNaci " + consultaNaci);
								debug("idVisaoDeret " + idVisaoDeret);

								if (atualizacaoNac.equals("true")) {
									atualizacaoNac = "S";
								} else {
									atualizacaoNac = "N";
								}

								if (consultaNaci.equals("true")) {
									consultaNaci = "S";
								} else {
									consultaNaci = "N";
								}

								if (idVisaoDeret.equals("true")) {
									idVisaoDeret = "S";
								} else {
									idVisaoDeret = "N";
								}

								for (String sistemPerfis : arrayPerfilSistema) {

									String[] array = sistemPerfis.split("/");

									sistema = array[0];
									perfil = array[1];

									boolean usarioPossuiGrupo = consultaGrupoUsuario(sistema, codUsuario);

									if (usarioPossuiGrupo == true) {
										debug("Usuario possui Grupo" + usarioPossuiGrupo);

										trataDadoDesvincularConexao(sistema, codUsuario);
									}

									Map<String, Object> recuperaDados = dadosUsuario(codUsuario);

									codigoSuregLot = recuperaDados.get(CD_SUREG_LOT).toString();
									codigoUnidade = recuperaDados.get(CD_UNIDADE).toString();
									idStatus = recuperaDados.get(ID_STATUS).toString();

									if (codigoUnidade.equals("")) {
										codigoUnidade = "0";
									}

									if (codigoSuregLot.equals("")) {
										codigoSuregLot = "0";
									}

									status = "A";

									debug("UPDATE ADD CONEXAO <sistema: " + sistema + "> <codUsuario: " + codUsuario
											+ "> <perfil: " + perfil + "> <nivelSeg: " + nivelSeg
											+ "> <codigoSuregLot: " + codigoSuregLot + "> <codigoUnidade: "
											+ codigoUnidade + "> <idVisaoDeret: " + idVisaoDeret + "> <status: "
											+ status + "> <atualizacaoNac: " + atualizacaoNac + "> <consultaNaci: "
											+ consultaNaci + ">");

									trataDadoConexao(sistema, codUsuario, perfil, nivelSeg, codigoSuregLot,
											codigoUnidade, idVisaoDeret, status, atualizacaoNac, consultaNaci);

									boolean usuarioNoGrupo = consultaUsuarioGrupo(codUsuario, sistema, perfil);

									if (usuarioNoGrupo == true) {
										result = new Result(Result.Status.Committed);
									}
									if (usuarioNoGrupo == false) {
										throw new ConnectorException(
												"Erro SINAV metodo: UPDATE: Usuario nao cadastrado no grupo");
									}
								}

							}

						}
						if (usuarioCriado == false) {
							throw new ConnectorException("Erro SINAV metodo: UPDATE: Usuario nao cadastrado na base ");

						}

					} catch (Exception e) {
						throw new ConnectorException("Erro SINAV metodo: UPDATE: " + e);
					}

				}
					break;
				case Remove: {

					arrayPerfilSistema = new ArrayList<String>();

					if (name.equalsIgnoreCase(sisPerfil)) {
						ArrayList valueList = new ArrayList();

						if (item.getValue() instanceof String) {
							valueList.add(item.getValue());

						} else {
							valueList = (ArrayList) item.getValue();

						}

						if (valueList.size() > 1) {

							arrayPerfilSistema.addAll(valueList);

						} else {

							String sistemaPerfis = valueList.get(0).toString();

							arrayPerfilSistema.add(sistemaPerfis);
						}
					}

					try {

						for (String sistemPerfis : arrayPerfilSistema) {

							String[] array = sistemPerfis.split("/");

							sistema = array[0];
							perfil = array[1];

							debug("UPDATE REMOVE CONEXAO <sistema: " + sistema + "> <codUsuario: " + codUsuario + ">");

							trataDadoDesvincularConexao(sistema, codUsuario);

							boolean usuarioNoGrupo = consultaUsuarioGrupo(codUsuario, sistema, perfil);

							if (usuarioNoGrupo == true) {
								throw new ConnectorException("Erro SINAV metodo: UPDATE: Grupo não removido do grupo ");
							}
							if (usuarioNoGrupo == false) {
								result = new Result(Result.Status.Committed);

							}

						}

					} catch (Exception e) {
						throw new ConnectorException("Erro SINAV metodo: UPDATE: " + e);
					}

				}
					break;
				case Set: {

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

		debug("====DELETE==== ");

		Result result = new Result();

		try {

			String cdUsuario, sistema;

			cdUsuario = nativeIdentifier;

			listaGrupoUsuario(cdUsuario);

			if (arrayGrupoUsuario.equals("")) {

				trataExclusaoUsuario(cdUsuario);

			} else {

				for (Object sis : arrayGrupoUsuario) {

					sistema = sis.toString();

					trataDadoDesvincularConexao(sistema, cdUsuario);
				}

				trataExclusaoUsuario(cdUsuario);

				boolean usuarioCriado = consultaCreate(cdUsuario);

				if (usuarioCriado == true) {

					boolean bloqueio = bloquearUsuario(cdUsuario);

					if (bloqueio == true) {
						result = new Result(Result.Status.Committed);

					} else {

						throw new ConnectorException(
								"Erro SINAV metodo: UPDATE: Usuario nao foi excluido pela rotina Sinav");
					}
				}
				if (usuarioCriado == false) {
					result = new Result(Result.Status.Committed);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
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

		// String codUsuario = "", nomeUsuario = "", codUnidade = "",
		// cdSistemDefaul = "";
		// String codSisUsu = "", codSuregLota = "", idDiretoria = "", idStat =
		// "", idCf = "";
		//
		// codUsuario = nativeIdentifier;
		//
		// try {
		// Map<String, Object> dUsuario = dadosUsuario(codUsuario);
		//
		// nomeUsuario = dUsuario.get(NO_USUARIO).toString();
		// codUnidade = dUsuario.get(CD_UNIDADE).toString();
		// cdSistemDefaul = dUsuario.get(CD_SIS_DEFAUL).toString();
		// codSisUsu = dUsuario.get(CD_SIT_USU).toString();
		// codSuregLota = dUsuario.get(CD_SUREG_LOT).toString();
		// idDiretoria = dUsuario.get(ID_DIRETORIA).toString();
		// idCf = dUsuario.get(ID_CEF).toString();
		//
		// idStat = "A";
		//
		// trataDadosAtualizacao(codUsuario, nomeUsuario, codUnidade,
		// cdSistemDefaul, codSisUsu, codSuregLota, idDiretoria,
		// idStat, idCf);
		//
		// Map<String, Object> vrUsuario = dadosUsuario(codUsuario);
		//
		// String st = vrUsuario.get(ID_STATUS).toString();
		//
		// if (st.equals(idStat)) {
		// result = new Result(Result.Status.Committed);
		// } else {
		// result = new Result(Result.Status.Failed);
		// }
		//
		// } catch (SQLException e) {
		// result = new Result(Result.Status.Failed);
		// e.printStackTrace();
		// } catch (Exception e) {
		// result = new Result(Result.Status.Failed);
		// e.printStackTrace();
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

		Result result = new Result(Result.Status.Committed);

		// log.debug("Disable");
		//
		// String codUsuario = "", nomeUsuario = "", codUnidade = "",
		// cdSistemDefaul = "";
		// String codSisUsu = "", codSuregLota = "", idDiretoria = "", idStat =
		// "", idCf = "";
		//
		// codUsuario = nativeIdentifier;
		//
		// log.debug("CodUsuario" + codUsuario);
		//
		// try {
		// Map<String, Object> dUsuario = dadosUsuario(codUsuario);
		//
		// nomeUsuario = dUsuario.get(NO_USUARIO).toString();
		// codUnidade = dUsuario.get(CD_UNIDADE).toString();
		// cdSistemDefaul = dUsuario.get(CD_SIS_DEFAUL).toString();
		// codSisUsu = dUsuario.get(CD_SIT_USU).toString();
		// codSuregLota = dUsuario.get(CD_SUREG_LOT).toString();
		// idDiretoria = dUsuario.get(ID_DIRETORIA).toString();
		// idCf = dUsuario.get(ID_CEF).toString();
		//
		// idStat = "E";
		//
		// trataDadosAtualizacao(codUsuario, nomeUsuario, codUnidade,
		// cdSistemDefaul, codSisUsu, codSuregLota, idDiretoria,
		// idStat, idCf);
		//
		// Map<String, Object> vrUsuario = dadosUsuario(codUsuario);
		//
		// String st = vrUsuario.get(ID_STATUS).toString();
		//
		// if (st.equals(idStat)) {
		// result = new Result(Result.Status.Committed);
		// } else {
		// result = new Result(Result.Status.Failed);
		// }
		//
		// } catch (SQLException e) {
		// result = new Result(Result.Status.Failed);
		// e.printStackTrace();
		// } catch (Exception e) {
		// result = new Result(Result.Status.Failed);
		// e.printStackTrace();
		// }

		return result;
	}

	/**
	 * Unlock the object of the configured object type that has the given native
	 * identifier. An acct is typically locked due to excessive invalid login
	 * attempts, whereas a disable is usually performed on accts that are no
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
	 *            The password to set for the acct.
	 * @param currentPassword
	 *            The current password of the acct. This should be provided if
	 *            available, but is not always required.
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

		Result result = new Result(Result.Status.Committed);

		return result;
	}

	/**
	 * Attempt to authenticate to the underlying resource using the given
	 * identity and password. The identity may be the value of the identity
	 * attribute, but could also be a value that can be used to search for the
	 * acct (eg - sAMacctName, full name, etc...). If successful, this returns
	 * the acct that was authenticated to.
	 * 
	 * @param identity
	 *            A value for an identifying attribute of the acct to
	 *            authenticate with.
	 * @param password
	 *            The password to use to authenticate.
	 * 
	 * @return The authenticated acct if authentication was successful.
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
		try {

			debug("Metodo Close Connection");

			if (m_dbConn != null) {
				m_dbConn.close();
			}

		} catch (SQLException e) {
			debug("ERRO SINAV: CLOSE CONNECTION: " + e);
		}

		m_dbConn = null;
	}

	public void init() throws Exception {

		if (!m_bIsDriverLoaded) {
			m_sUser = config.getString("user");
			m_sPasswd = config.getString("password");
			m_sUrl = config.getString("url");
			m_sDriverName = config.getString("driverClass");
			nomeServidorSics = config.getString("nomeServidorSics");
			numPortaSics = config.getString("numPortaSics");
			userNameSics = config.getString("userNameSics");
			passwordSiscs = config.getString("passwordSiscs");

			Class.forName(m_sDriverName);

			m_dbConn = DriverManager.getConnection(m_sUrl, m_sUser, m_sPasswd);
			m_bIsDriverLoaded = true;

			debug("INIT  user <" + m_sUser + ">  m_sUrl <" + m_sUrl + "> m_sDriverName <" + m_sDriverName
					+ "> nomeServidorSics <" + nomeServidorSics + "> numPortaSics <" + numPortaSics + "> userNameSics <"
					+ userNameSics + ">");
		}
	}

	public void debug(String mensagem) {
		boolean debug = config.getBoolean("debug");

		log.debug("Valor Debug" + debug);

		if (debug == true) {
			log.debug(mensagem);
		}

	}

	public Iterator<Map<String, Object>> IterateNextPage(Filter filter) {
		try {

			debug("IterateNextPage");

			if (OBJECT_TYPE_ACCOUNT.equals(this.objectType)) {

				debug("Iterete Account");
				debug("Tamanho Array " + tamanhoListaAccount);

				if (listAgregAccount.size() == 0) {

					debug("end of group iteration...");
				} else {
					it = (getObjectsMap().values()).iterator();
				}

			} else if (OBJECT_TYPE_GROUP.equals(this.objectType)) {
				debug("Iterete Group");
				debug("Tamanho Array " + tamanhoListaGroup);

				if (listAgregGoup.size() == 0) {
					debug("end of group iteration...");
				} else {
					it = (getObjectsMap().values()).iterator();
				}
			}
		} catch (Exception e) {
			throw new ConnectorException("Erro SINAV IterateNextPage: " + e);
		}
		return it;
	}

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

						debug("Lista Usuarios " + listAgregAccount);

					}
				} else {
					for (int i = 0; i < 10; i++) {
						listAgregAccount.remove(0);
					}
					contador = 0;
				}

				for (Map<String, Object> mapUser : listAgregAccount) {
					if (contador < 10) {

						AccountSinav acc = (AccountSinav) mapUser.get("Account");
						dados = new HashMap<String, Object>();

						dados.put("CD_USUARIO", acc.getCdUsuario().trim());
						dados.put("NO_USUARIO", acc.getNoUsuario().trim());
						dados.put("CD_UNIDADE", acc.getCodUnidade().trim());
						dados.put("CD_SIS_DEFAUL", acc.getCdSisDefaul().trim());
						dados.put("CD_IMPR_REMOT", acc.getCdImprRemot().trim());
						dados.put("CD_SIT_USU", acc.getCdSitUsu().trim());
						dados.put("CD_SUREG_LOT", acc.getCdSuregLot().trim());
						dados.put("CD_CEADM_AUT", acc.getCdCeadmAut().trim());
						dados.put("CD_SUREG_AUT", acc.getCdSuregAut().trim());
						dados.put("CD_UNID_AUT", acc.getCdUnidAut().trim());
						dados.put("ID_DIRETORIA", acc.getIdDiretoria().trim());
						dados.put("CD_TERMINAL", acc.getCdTerminal().trim());
						dados.put("ID_STATUS", acc.getIdStatus().trim());
						dados.put("ID_CEF", acc.getIdCef().trim());
						dados.put("CD_NO_REMOT", acc.getCdNoRemot().trim());
						dados.put("CD_FUNCAO", acc.getCdFuncao().trim());
						dados.put("CO_SEGMENTO", acc.getCoSegmento().trim());
						dados.put("NU_CNPJ", acc.getNuCnpj().trim());
						dados.put("NU_MATR_EMP", acc.getNuMatrEmp().trim());
						dados.put("NU_CA_EN_FISICA", acc.getNuCaEnFisica().trim());
						dados.put("SIS_PERFIL", mapUser.get("SISPERFIL"));

						m_acctsMap.put(acc.getCdUsuario().trim(), dados);

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

						GroupSinav gpr = (GroupSinav) mapGroup.get("Group");
						dados = new HashMap<String, Object>();

						dados.put("CD_PERFIL", gpr.getCodigoPerfil().trim());
						dados.put("CD_SISTEMA", gpr.getCodigoSistema().trim());
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
			throw new ConnectorException("Erro SINAV metodo getObjectsMap  " + e);

		}

		throw new ConnectorException("Unhandled object type: " + this.objectType);

	}

	public Map<String, Object> dadosUsuario(String codUsuario) throws Exception {

		init();

		dadosUsuario = new HashMap<String, Object>();

		debug("Dados Usuario codUsuario <" + codUsuario + ">");

		String query = "SELECT * FROM gal.GALVWS07_USRO_SAILPOINT where CD_USUARIO='" + codUsuario + "'";

		debug("Dados Usuario Query <" + query + ">");

		Statement stmt = m_dbConn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		while (rs.next()) {

			String noUsuario = rs.getString(NO_USUARIO);
			if (noUsuario != null) {
				dadosUsuario.put(NO_USUARIO, noUsuario);
			} else {
				dadosUsuario.put(NO_USUARIO, "");
			}

			String codUnidade = rs.getString(CD_UNIDADE);
			if (codUnidade != null) {
				dadosUsuario.put(CD_UNIDADE, codUnidade);
			} else {
				dadosUsuario.put(CD_UNIDADE, "");
			}

			String cdSisDefaul = rs.getString(CD_SIS_DEFAUL);
			if (cdSisDefaul != null) {
				dadosUsuario.put(CD_SIS_DEFAUL, cdSisDefaul);
			} else {
				dadosUsuario.put(CD_SIS_DEFAUL, "");
			}

			String cdImprRemot = rs.getString(CD_IMPR_REMOT);
			if (cdImprRemot != null) {
				dadosUsuario.put(CD_IMPR_REMOT, cdImprRemot);
			} else {
				dadosUsuario.put(CD_IMPR_REMOT, "");
			}

			String cdSitUsu = rs.getString(CD_SIT_USU);
			if (cdSitUsu != null) {
				dadosUsuario.put(CD_SIT_USU, cdSitUsu);
			} else {
				dadosUsuario.put(CD_SIT_USU, "");
			}

			String cdSuregLot = rs.getString(CD_SUREG_LOT);
			if (cdSuregLot != null) {
				dadosUsuario.put(CD_SUREG_LOT, cdSuregLot);
			} else {
				dadosUsuario.put(CD_SUREG_LOT, "");
			}

			String cdCeadmAut = rs.getString(CD_CEADM_AUT);
			if (cdCeadmAut != null) {
				dadosUsuario.put(CD_CEADM_AUT, cdCeadmAut);
			} else {
				dadosUsuario.put(CD_CEADM_AUT, "");
			}

			String cdSuregAut = rs.getString(CD_SUREG_AUT);
			if (cdSuregAut != null) {
				dadosUsuario.put(CD_SUREG_AUT, cdSuregAut);
			} else {
				dadosUsuario.put(CD_SUREG_AUT, "");
			}

			String cdUnidAut = rs.getString(CD_UNID_AUT);
			if (cdUnidAut != null) {
				dadosUsuario.put(CD_UNID_AUT, cdUnidAut);
			} else {
				dadosUsuario.put(CD_UNID_AUT, "");
			}

			String idDiretoria = rs.getString(ID_DIRETORIA);
			if (idDiretoria != null) {
				dadosUsuario.put(ID_DIRETORIA, idDiretoria);
			} else {
				dadosUsuario.put(ID_DIRETORIA, "");
			}

			String cdTerminal = rs.getString(CD_TERMINAL);
			if (cdTerminal != null) {
				dadosUsuario.put(CD_TERMINAL, cdTerminal);
			} else {
				dadosUsuario.put(CD_TERMINAL, "");
			}

			String idStatus = rs.getString(ID_STATUS);
			if (idStatus != null) {
				dadosUsuario.put(ID_STATUS, idStatus);
			} else {
				dadosUsuario.put(ID_STATUS, "");
			}

			String idCef = rs.getString(ID_CEF);
			if (idCef != null) {
				dadosUsuario.put(ID_CEF, idCef);
			} else {
				dadosUsuario.put(ID_CEF, "");
			}

			String cdNoRemot = rs.getString(CD_NO_REMOT);
			if (cdNoRemot != null) {
				dadosUsuario.put(CD_NO_REMOT, cdNoRemot);
			} else {
				dadosUsuario.put(CD_NO_REMOT, "");
			}

			String cdFuncao = rs.getString(CD_FUNCAO);
			if (cdFuncao != null) {
				dadosUsuario.put(CD_FUNCAO, cdFuncao);
			} else {
				dadosUsuario.put(CD_FUNCAO, "");
			}

			String coSegmento = rs.getString(CO_SEGMENTO);
			if (coSegmento != null) {
				dadosUsuario.put(CO_SEGMENTO, coSegmento);
			} else {
				dadosUsuario.put(CO_SEGMENTO, "");
			}

			String nuCnpj = rs.getString(NU_CNPJ);
			if (nuCnpj != null) {
				dadosUsuario.put(NU_CNPJ, nuCnpj);
			} else {
				dadosUsuario.put(NU_CNPJ, "");
			}

			String nuMatrEmp = rs.getString(NU_MATR_EMP);
			if (nuMatrEmp != null) {
				dadosUsuario.put(NU_MATR_EMP, nuMatrEmp);
			} else {
				dadosUsuario.put(NU_MATR_EMP, "");
			}

			String nuCaEnFisica = rs.getString(NU_CA_EN_FISICA);
			if (nuCaEnFisica != null) {
				dadosUsuario.put(NU_CA_EN_FISICA, nuCaEnFisica);
			} else {
				dadosUsuario.put(NU_CA_EN_FISICA, "");
			}
		}

		debug("Dados Usuario dadosUsuario <" + dadosUsuario.toString() + ">");

		return dadosUsuario;

	}

	public boolean bloquearUsuario(String nativeIdentifier) throws Exception {

		String codUsuario = "", nomeUsuario = "", codUnidade = "", cdSistemDefaul = "";
		String codSisUsu = "", codSuregLota = "", idDiretoria = "", idStat = "", idCf = "";

		codUsuario = nativeIdentifier;

		Map<String, Object> dUsuario = dadosUsuario(codUsuario);

		nomeUsuario = dUsuario.get(NO_USUARIO).toString();
		codUnidade = dUsuario.get(CD_UNIDADE).toString();
		cdSistemDefaul = dUsuario.get(CD_SIS_DEFAUL).toString();
		codSisUsu = dUsuario.get(CD_SIT_USU).toString();
		codSuregLota = dUsuario.get(CD_SUREG_LOT).toString();
		idDiretoria = dUsuario.get(ID_DIRETORIA).toString();
		idCf = dUsuario.get(ID_CEF).toString();

		idStat = "E";

		trataDadosAtualizacao(codUsuario, nomeUsuario, codUnidade, cdSistemDefaul, codSisUsu, codSuregLota, idDiretoria,
				idStat, idCf);

		Map<String, Object> vrUsuario = dadosUsuario(codUsuario);

		String st = vrUsuario.get(ID_STATUS).toString();

		if (st.equals(idStat)) {

			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List listaGrupoUsuario(String codUsuario) throws Exception {
		String sistema = "";

		init();
		String query = "SELECT * FROM gal.GALVWS08_USRO_SSTMA_SAILPOINT Where CD_USUARIO= '" + codUsuario + "'";

		debug("Consulta usaurio no Grupo Query: <" + query + ">");

		Statement stmt = m_dbConn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		while (rs.next()) {
			if (rs.getString(CD_SISTEMA) != null) {

				sistema = rs.getString(CD_SISTEMA);

				arrayGrupoUsuario.add(sistema);

			}
		}

		stmt.close();

		debug("Consulta usaurio no Grupo arrayGrupoUsuario: <" + arrayGrupoUsuario.toString() + ">");

		return arrayGrupoUsuario;
	}

	public String nomeGroup(String codPerfil, String codSistema) throws Exception {

		String query = "SELECT * FROM gal.GALVWS09_PRFL_SAILPOINT where CD_PERFIL='" + codPerfil + "' AND CD_SISTEMA='"
				+ codSistema + "'";

		String noGrupo = "";
		Statement stmt = m_dbConn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		while (rs.next()) {
			if (rs.getString(NO_PERFIL) != null) {
				noGrupo = rs.getString(NO_PERFIL);
			}
		}

		stmt.close();

		return noGrupo;

	}

	public boolean consultaGrupoUsuario(String sistema, String codUsuario) throws Exception {
		boolean ret = false;

		init();
		String query = "SELECT * FROM gal.GALVWS08_USRO_SSTMA_SAILPOINT where CD_USUARIO = '" + codUsuario
				+ "' and CD_SISTEMA = '" + sistema + "'";

		debug("Query Consulta Usuario Criado Query: <" + query + ">");

		Statement stmt = m_dbConn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		ret = rs.next();

		stmt.close();

		debug("Consulta Usuario Criado Retorno: <" + ret + ">");

		return ret;
	}

	public boolean consultaCreate(String codUsuario) throws Exception {
		boolean ret = false;
		String usuario = "";

		init();
		String query = "SELECT * FROM gal.GALVWS07_USRO_SAILPOINT where gal.GALVWS07_USRO_SAILPOINT.CD_USUARIO = '"
				+ codUsuario + "'";

		debug("Query Consulta Usuario Criado Query: <" + query + ">");

		Statement stmt = m_dbConn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		ret = rs.next();

		stmt.close();

		if (!usuario.equals("") && usuario != null) {
			ret = true;
		}

		debug("Consulta Usuario Criado Retorno: <" + ret + ">");

		return ret;
	}

	public boolean consultaUsuarioGrupo(String codUsuario, String sistema, String perfil) throws Exception {
		boolean ret = false;
		String usuario = "";

		init();
		String query = "SELECT * FROM gal.GALVWS08_USRO_SSTMA_SAILPOINT Where CD_USUARIO= '" + codUsuario
				+ "' and CD_SISTEMA='" + sistema + "' and CD_PERFIL='" + perfil + "'";

		debug("Consulta Usuario Grupo Query: " + query);

		Statement stmt = m_dbConn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		ret = rs.next();

		stmt.close();

		if (!usuario.equals("") && usuario != null) {
			ret = true;
		}

		debug("Consulta Usuario Grupo Return: " + ret);

		return ret;
	}

	public String recuperaCodPerfil(String codPerfil, String codSistema) throws Exception {

		init();
		String query = "SELECT * FROM gal.GALVWS09_PRFL_SAILPOINT where NO_PERFIL='" + codPerfil + "' AND CD_SISTEMA='"
				+ codSistema + "'";

		debug("Recupera numero Perfil Query <" + query + ">");

		String numPerfil = "";
		Statement stmt = m_dbConn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		while (rs.next()) {

			if (rs.getString(CD_PERFIL) != null) {
				numPerfil = rs.getString(CD_PERFIL);
			}
		}

		stmt.close();

		debug("Recupera numero Perfil numPerfil <" + numPerfil + ">");

		return numPerfil;
	}

	public void trataDadoCreate(String codUsuario, String nomeUsuario, String codigoUnidade, String codigoSisDefaul,
			String codigoSistUsua, String codigoSuregLot, String idDeret, String status, String cef, String funcao,
			String matricula) throws Exception {

		StringBuilder dadosCreate = new StringBuilder();

		// CD-USUARIO X(08)
		codUsuario = String.format("%-8s", codUsuario);
		dadosCreate.append(codUsuario);

		// NO-USUARIO X(40)
		nomeUsuario = String.format("%-40s", nomeUsuario);
		dadosCreate.append(nomeUsuario);

		// CD-UNIDADE 9(04)
		codigoUnidade = String.format("%04d", Integer.parseInt(codigoUnidade));
		dadosCreate.append(codigoUnidade);

		// CD-SIS-DEFAUL x(08)
		codigoSisDefaul = String.format("%-8s", codigoSisDefaul);
		dadosCreate.append(codigoSisDefaul);

		// CD-SIS-USU x(01)
		codigoSistUsua = String.format("%-1s", codigoSistUsua);
		dadosCreate.append(codigoSistUsua);

		// CD-SUREG-LOT 9(04)
		codigoSuregLot = String.format("%04d", Integer.parseInt(codigoSuregLot));
		dadosCreate.append("0183");

		// ID-DIRETORIA X(01)
		idDeret = String.format("%-1s", idDeret);
		dadosCreate.append(idDeret);

		// ID-STATUS X(01)
		status = String.format("%-1s", status);
		dadosCreate.append(status);

		// ID-CEF X(01)
		cef = String.format("%-1s", cef);
		dadosCreate.append(cef);

		// DT-PRV-DESL X(10)
		dadosCreate.append("          ");

		// CD-FUNCAO 9(04)
		funcao = String.format("%04d", Integer.parseInt(funcao));
		dadosCreate.append(funcao);

		// NU-MATR-EMP 9(08)
		matricula = String.format("%08d", Integer.parseInt(matricula));
		dadosCreate.append(matricula);

		// NU-CA-EN-FISICA 9(04)
		dadosCreate.append("0000");

		// NU-UNIDADE-FISICA 9(04)
		dadosCreate.append("0000");

		debug("Trata Dados Create dadosCreate: <" + dadosCreate.toString() + ">");

		criaUsuario(dadosCreate.toString());

	}

	public void trataDadoConexao(String sistema, String codUsuario, String perfil, String nivelSeg,
			String codigoSuregLot, String codigoUnidade, String idDeret, String status, String consultaNaci,
			String atualizacaoNac) throws Exception {

		StringBuilder dadosConexao = new StringBuilder();

		debug("TRADA DADOS CONEXAO <sistema: " + sistema + "> <codUsuario: " + codUsuario + "> <perfil: " + perfil
				+ "> <nivelSeg: " + nivelSeg + "> <codigoSuregLot: " + codigoSuregLot + "> <codigoUnidade: "
				+ codigoUnidade + "> <idVisaoDeret: " + idDeret + "> <status: " + status + "> <atualizacaoNac: "
				+ atualizacaoNac + "> <consultaNaci: " + consultaNaci + ">");

		// CD-SISTEMA x(08)
		sistema = String.format("%-8s", sistema);
		dadosConexao.append(sistema);

		// CD-USUARIO x(08)
		codUsuario = String.format("%-8s", codUsuario);
		dadosConexao.append(codUsuario);

		// CD-PERFIL x(03)
		perfil = String.format("%-3s", perfil);
		dadosConexao.append(perfil);

		// CD-NIV-SEG x(01)
		nivelSeg = String.format("%-1s", nivelSeg);
		dadosConexao.append(nivelSeg);

		// CD-CEADM-AUT 9(04)
		dadosConexao.append("0000");

		// CD-SUREG-AUT 9(04)
		codigoSuregLot = String.format("%04d", Integer.parseInt(codigoSuregLot));
		dadosConexao.append(codigoSuregLot);

		// CD-UNID-AUT 9(04)
		codigoUnidade = String.format("%04d", Integer.parseInt(codigoUnidade));
		dadosConexao.append(codigoUnidade);

		// ID-DIRETORIA x(01)
		idDeret = String.format("%-1s", idDeret);
		dadosConexao.append(idDeret);

		// ID-STATUS x(01)
		status = String.format("%-1s", status);
		dadosConexao.append(status);

		// DT-PRV-DESL x(10)
		dadosConexao.append("          ");

		// DT-ULT-ACESSO x(10)
		dadosConexao.append("          ");

		// QT-ACESSOS 9(09)
		dadosConexao.append("000000000");

		// QT-MENS-GERAL 9(04)
		dadosConexao.append("0000");

		// QT-MENS-PERFIL 9(04)
		dadosConexao.append("0000");

		// ID-USU-UNID x(01) -----------Perguntar para o shimit
		dadosConexao.append("N");

		// ID-VISAO-NACIONAL x(01) -- consulta Nacional
		consultaNaci = String.format("%-1s", consultaNaci);
		dadosConexao.append(consultaNaci);

		// IC-ATUAL-NACIONAL x(01) --- aceita nulo -- atualização Nacional
		atualizacaoNac = String.format("%-1s", atualizacaoNac);
		dadosConexao.append(atualizacaoNac);

		debug("Trata Dados Conexao <dadosConexao: " + dadosConexao.toString() + ">");

		criaConexao(dadosConexao.toString());

	}

	public void trataDadoDesvincularConexao(String sistema, String codUsuario) throws Exception {

		StringBuilder dadosDesvincular = new StringBuilder();

		// CD-SISTEMA x(08)
		sistema = String.format("%-8s", sistema);
		dadosDesvincular.append(sistema);

		// CD-USUARIO x(08)
		codUsuario = String.format("%-8s", codUsuario);
		dadosDesvincular.append(codUsuario);

		debug("Trada dados Desvincular Conexao : <dadosDesvincular: " + dadosDesvincular.toString() + ">");

		desvincularConexao(dadosDesvincular.toString());

	}

	public void trataDadosAtualizacao(String codUsuario, String nomeUsuario, String codUnidade, String cdSistemDefaul,
			String codSisUsu, String codSuregLota, String idDiretoria, String idStat, String idCf) throws Exception {

		StringBuilder dadoAtualizacao = new StringBuilder();

		// CD-USUARIO X(08)
		codUsuario = String.format("%-8s", codUsuario);
		dadoAtualizacao.append(codUsuario);

		// NO-USUARIO X(40)
		nomeUsuario = String.format("%-40s", nomeUsuario);
		dadoAtualizacao.append(nomeUsuario);

		// CD-UNIDADE 9(04)
		codUnidade = String.format("%04d", Integer.parseInt(codUnidade));
		dadoAtualizacao.append(codUnidade);

		// CD-SIS-DEFAUL x(08)
		cdSistemDefaul = String.format("%-8s", cdSistemDefaul);
		dadoAtualizacao.append(cdSistemDefaul);

		// CD-SIS-USU x(01)
		codSisUsu = String.format("%-1s", codSisUsu);
		dadoAtualizacao.append(codSisUsu);

		// CD-SUREG-LOT 9(04)
		codSuregLota = String.format("%04d", Integer.parseInt(codSuregLota));
		dadoAtualizacao.append(codSuregLota);

		// ID-DIRETORIA X(01)
		idDiretoria = String.format("%-1s", idDiretoria);
		dadoAtualizacao.append(idDiretoria);

		// ID-STATUS X(01)
		idStat = String.format("%-1s", idStat);
		dadoAtualizacao.append(idStat);

		// ID-CEF X(01)
		idCf = String.format("%-1s", idCf);
		dadoAtualizacao.append(idCf);

		// DT-PRV-DESL-I X(1)
		dadoAtualizacao.append("N");

		// DT-PRV-DESL X(10)
		dadoAtualizacao.append("          ");

		// CD-FUNCAO-I X(1)
		dadoAtualizacao.append("N");

		// CD-FUNCAO 9(04)
		dadoAtualizacao.append("0000");

		// NU-CA-EN-FISICA-I X(01)
		dadoAtualizacao.append("N");

		// NU-CA-EN-FISICA 9(04)
		dadoAtualizacao.append("0000");

		// NU-UNIDADE-FISICA-I X(1)
		dadoAtualizacao.append("N");

		// NU-UNIDADE-FISICA 9(04)
		dadoAtualizacao.append("0000");

		debug("TrataDadosAtualizacao <dadoAtualizacao: " + dadoAtualizacao.toString() + ">");

		atulizacaoUsuario(dadoAtualizacao.toString());

	}

	public void trataExclusaoUsuario(String cdUsuario) throws Exception {

		StringBuilder excluiUsuario = new StringBuilder();

		// CD-USUARIO X(08)
		cdUsuario = String.format("%-8s", cdUsuario);
		excluiUsuario.append(cdUsuario);

		debug("Trata Exclusao Usuario ExcluiUsuario: <" + excluiUsuario.toString() + ">");

		excluiUsuario(excluiUsuario.toString());

	}

	public void excluiUsuario(String excluiUsuario) throws Exception {

		init();

		jcisConnect = new JCicsConnect();

		mensage = new MensagemCics();

		try {

			mensage.setNomeServidor(nomeServidorSics);
			mensage.setNumPorta(Integer.parseInt(numPortaSics));
			mensage.setUserName(userNameSics);
			mensage.setPassword(passwordSiscs);

			mensage = jcisConnect.login(mensage);
			mensage.setTransId("NA18");
			mensage.setProgramName("NAVPO019");
			mensage.setTipoPrograma(mensage.getTipoPrograma());

			mensage.setDadosEnvio(excluiUsuario.toString());

			mensage = jcisConnect.execute(mensage);

			debug("Exclui Usuario Retorno: " + mensage.getDadosRetorno());

		} catch (LoginException e) {
			debug("Cód. Erro:" + e.getErroCics() + ", Complemento: " + e.getComplementoErroCics() + ", Cód. Sql: "
					+ e.getCodSql() + ", Resp: " + e.getResp() + ", Resp2: " + e.getResp2() + ": " + e);

		} catch (LeMensagemException e) {
			debug("Cód. Erro:" + e.getErroCics() + ", Complemento: " + e.getComplementoErroCics() + ", Cód. Sql: "
					+ e.getCodSql() + ", Resp: " + e.getResp() + ", Resp2: " + e.getResp2() + ": " + e);

		} catch (ExecuteException e) {
			if (isDebug == true) {
				log.debug(
						"Cód. Erro:" + e.getErroCics() + ", Complemento: " + e.getComplementoErroCics() + ", Cód. Sql: "
								+ e.getCodSql() + ", Resp: " + e.getResp() + ", Resp2: " + e.getResp2() + ": " + e);
			}
		}
	}

	public void atulizacaoUsuario(String dadoAtualizacao) throws Exception {

		init();

		jcisConnect = new JCicsConnect();

		mensage = new MensagemCics();

		try {

			mensage.setNomeServidor(nomeServidorSics);
			mensage.setNumPorta(Integer.parseInt(numPortaSics));
			mensage.setUserName(userNameSics);
			mensage.setPassword(passwordSiscs);

			mensage = jcisConnect.login(mensage);
			mensage.setTransId("NA18");
			mensage.setProgramName("NAVPO022");
			mensage.setTipoPrograma(mensage.getTipoPrograma());

			mensage.setDadosEnvio(dadoAtualizacao.toString());

			mensage = jcisConnect.execute(mensage);

			debug("AtulizacaoUsuario Retorno: " + mensage.getDadosRetorno());

		} catch (LoginException e) {
			debug("Cód. Erro:" + e.getErroCics() + ", Complemento: " + e.getComplementoErroCics() + ", Cód. Sql: "
					+ e.getCodSql() + ", Resp: " + e.getResp() + ", Resp2: " + e.getResp2() + ": " + e);

		} catch (LeMensagemException e) {
			debug("Cód. Erro:" + e.getErroCics() + ", Complemento: " + e.getComplementoErroCics() + ", Cód. Sql: "
					+ e.getCodSql() + ", Resp: " + e.getResp() + ", Resp2: " + e.getResp2() + ": " + e);

		} catch (ExecuteException e) {
			debug("Cód. Erro:" + e.getErroCics() + ", Complemento: " + e.getComplementoErroCics() + ", Cód. Sql: "
					+ e.getCodSql() + ", Resp: " + e.getResp() + ", Resp2: " + e.getResp2() + ": " + e.getMessage());

		}
	}

	public void desvincularConexao(String dadosDesvinculo) throws Exception {

		init();

		jcisConnect = new JCicsConnect();

		mensage = new MensagemCics();

		try {

			mensage.setNomeServidor(nomeServidorSics);
			mensage.setNumPorta(Integer.parseInt(numPortaSics));
			mensage.setUserName(userNameSics);
			mensage.setPassword(passwordSiscs);

			mensage = jcisConnect.login(mensage);
			mensage.setTransId("NA18");
			mensage.setProgramName("NAVPO021");
			mensage.setTipoPrograma(mensage.getTipoPrograma());

			mensage.setDadosEnvio(dadosDesvinculo.toString());

			mensage = jcisConnect.execute(mensage);

			debug("Desvincular Conexao Retorno: " + mensage.getDadosRetorno());

		} catch (LoginException e) {
			debug("Cód. Erro:" + e.getErroCics() + ", Complemento: " + e.getComplementoErroCics() + ", Cód. Sql: "
					+ e.getCodSql() + ", Resp: " + e.getResp() + ", Resp2: " + e.getResp2() + ": " + e);

		} catch (LeMensagemException e) {
			debug("Cód. Erro:" + e.getErroCics() + ", Complemento: " + e.getComplementoErroCics() + ", Cód. Sql: "
					+ e.getCodSql() + ", Resp: " + e.getResp() + ", Resp2: " + e.getResp2() + ": " + e);

		} catch (ExecuteException e) {
			debug("Cód. Erro:" + e.getErroCics() + ", Complemento: " + e.getComplementoErroCics() + ", Cód. Sql: "
					+ e.getCodSql() + ", Resp: " + e.getResp() + ", Resp2: " + e.getResp2() + ": " + e);

		}

	}

	public void criaUsuario(String dadosCreate) throws Exception {

		init();

		jcisConnect = new JCicsConnect();

		mensage = new MensagemCics();

		try {

			mensage.setNomeServidor(nomeServidorSics);
			mensage.setNumPorta(Integer.parseInt(numPortaSics));
			mensage.setUserName(userNameSics);
			mensage.setPassword(passwordSiscs);

			mensage = jcisConnect.login(mensage);
			mensage.setTransId("NA18");
			mensage.setProgramName("NAVPO018");
			mensage.setTipoPrograma(mensage.getTipoPrograma());

			mensage.setDadosEnvio(dadosCreate.toString());

			mensage = jcisConnect.execute(mensage);

			debug("Criar Usuario Retorno: " + mensage.getDadosRetorno());

		} catch (LoginException e) {
			debug("Cód. Erro:" + e.getErroCics() + ", Complemento: " + e.getComplementoErroCics() + ", Cód. Sql: "
					+ e.getCodSql() + ", Resp: " + e.getResp() + ", Resp2: " + e.getResp2() + ": " + e);

		} catch (LeMensagemException e) {
			debug("Cód. Erro:" + e.getErroCics() + ", Complemento: " + e.getComplementoErroCics() + ", Cód. Sql: "
					+ e.getCodSql() + ", Resp: " + e.getResp() + ", Resp2: " + e.getResp2() + ": " + e);

		} catch (ExecuteException e) {
			debug("Cód. Erro:" + e.getErroCics() + ", Complemento: " + e.getComplementoErroCics() + ", Cód. Sql: "
					+ e.getCodSql() + ", Resp: " + e.getResp() + ", Resp2: " + e.getResp2() + ": " + e);

		}

	}

	public void criaConexao(String dadosConexao) throws Exception {

		init();

		jcisConnect = new JCicsConnect();

		mensage = new MensagemCics();

		try {

			mensage.setNomeServidor(nomeServidorSics);
			mensage.setNumPorta(Integer.parseInt(numPortaSics));
			mensage.setUserName(userNameSics);
			mensage.setPassword(passwordSiscs);

			mensage = jcisConnect.login(mensage);
			mensage.setTransId("NA18");
			mensage.setProgramName("NAVPO020");
			mensage.setTipoPrograma(mensage.getTipoPrograma());

			debug("Conexao <dadosConexao: " + dadosConexao.toString() + ">");

			mensage.setDadosEnvio(dadosConexao);

			mensage = jcisConnect.execute(mensage);

			debug("Criar Conexao Retorno: " + mensage.getDadosRetorno());

		} catch (LoginException e) {
			debug("Cód. Erro:" + e.getErroCics() + ", Complemento: " + e.getComplementoErroCics() + ", Cód. Sql: "
					+ e.getCodSql() + ", Resp: " + e.getResp() + ", Resp2: " + e.getResp2() + ": " + e);

		} catch (LeMensagemException e) {
			debug("Cód. Erro:" + e.getErroCics() + ", Complemento: " + e.getComplementoErroCics() + ", Cód. Sql: "
					+ e.getCodSql() + ", Resp: " + e.getResp() + ", Resp2: " + e.getResp2() + ": " + e.getMessage());

		} catch (ExecuteException e) {
			debug("Cód. Erro:" + e.getErroCics() + ", Complemento: " + e.getComplementoErroCics() + ", Cód. Sql: "
					+ e.getCodSql() + ", Resp: " + e.getResp() + ", Resp2: " + e.getResp2() + ": " + e.getMessage());

		}
	}
}
