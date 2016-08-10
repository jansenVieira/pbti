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
import br.gov.caixa.seguranca.LoginException;
import br.gov.caixa.util.jcicsconnect.ExecuteException;
import br.gov.caixa.util.jcicsconnect.JCicsConnect;
import br.gov.caixa.util.jcicsconnect.LeMensagemException;
import br.gov.caixa.util.jcicsconnect.MensagemCics;

/*

 This file  provides a skeletal structure of the openconnector interfaces.
 It is the responsiblity of the connector developer to implement the methods.

 */
public class SINAVTestePart extends AbstractConnector {

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
	private Map<String, Object> groups;
	private List<Map<String, Object>> resultObjectList = new ArrayList<Map<String, Object>>();

	// Variavel acct
	private String CD_USUARIO = "CD_USUARIO";
	private String NO_USUARIO = "NO_USUARIO";
	private String CD_UNIDADE = "CD_UNIDADE";
	private String CD_SENHA = "CD_SENHA";
	private String CD_SIS_DEFAUL = "CD_SIS_DEFAUL";
	private String DT_ALT_SENHA = "DT_ALT_SENHA";
	private String CD_IMPR_REMOT = "CD_IMPR_REMOT";
	private String CD_SIT_USU = "CD_SIT_USU";
	private String DT_ULT_ACESSO = "DT_ULT_ACESSO";
	private String CD_SUREG_LOT = "CD_SUREG_LOT";
	private String CD_CEADM_AUT = "CD_CEADM_AUT";
	private String CD_SUREG_AUT = "CD_SUREG_AUT";
	private String CD_UNID_AUT = "CD_UNID_AUT";
	private String ID_DIRETORIA = "ID_DIRETORIA";
	private String QT_ACESSOS = "QT_ACESSOS";
	private String QT_MENS_GERAL = "QT_MENS_GERAL";
	private String CD_TERMINAL = "CD_TERMINAL";
	private String QT_MENS_PERFIL = "QT_MENS_PERFIL";
	private String ID_STATUS = "ID_STATUS";
	private String ID_CEF = "ID_CEF";
	private String DT_PRV_DESL = "DT_PRV_DESL";
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
	private String idStatus = "ID_STATUS";
	private String idCef = "ID_CEF";
	private String cdFuncao = "CD_FUNCAO";

	// Variavel Sics
	public JCicsConnect jcisConnect;
	public MensagemCics mensage;

	// Variavel MAPs Arrays
	public Map<String, Object> dadosUsuario;
	public List arrayGrupoUsuario = new ArrayList();

	// Novas Variaveis
	private String m_sLastUser;
	private String m_sLastGroup;
	public static final String EMPTY_STR = " ";
	private Filter _filter;
	private int m_iChunkSize = 10;
	private Map<String, Map<String, Object>> m_acctsMap = new HashMap<String, Map<String, Object>>();
	private Map<String, Map<String, Object>> m_groupsMap = new HashMap<String, Map<String, Object>>();
	private int contador = 0;

	// Variavel Debug
	private boolean isDebug;

	// variavel teste
	public ResultSet queryResult = null;
	public ResultSet queryResultGrupo = null;
	ArrayList<Integer> numRows = new ArrayList<Integer>();

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
	public SINAVTestePart() {
		super();

		if (isDebug == true) {
			log.debug("Construtor: <valor m_iChunkSize: " + m_iChunkSize
					+ "> <valor m_sLastUser: " + m_sLastUser
					+ "> <m_sLastGroup: " + m_sLastGroup);
		}

		m_iChunkSize = 10;
		m_sLastUser = EMPTY_STR;
		m_sLastGroup = EMPTY_STR;
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
	public SINAVTestePart(ConnectorConfig config, openconnector.Log log)
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
			throws ConnectorException, IllegalArgumentException,
			UnsupportedOperationException {

		Map<String, Object> acct = null;
		Map<String, Object> grp = null;

		ResultSet queryResultRead = null;
		Statement stmtRead = null;

		try {

			init();
			String query = null;

			if (OBJECT_TYPE_ACCOUNT.equals(this.objectType)) {

				query = "SELECT * FROM gal.GALVWS07_USRO_SAILPOINT LEFT JOIN gal.GALVWS09_PRFL_SAILPOINT "
						+ "ON gal.GALVWS07_USRO_SAILPOINT.CD_USUARIO = gal.GALVWS09_PRFL_SAILPOINT.CD_USUARIO where gal.GALVWS07_USRO_SAILPOINT.CD_USUARIO = '"
						+ nativeIdentifier + "'";
				
				
				stmtRead = m_dbConn.createStatement();

				if (isDebug == true) {
					log.debug("READ Account: <query: " + query
							+ ">\n <FILTER: " + nativeIdentifier + ">");
				}

				queryResultRead = stmtRead.executeQuery(query);
				
				ArrayList<String> sisperfil = new ArrayList<String>();

					while (queryResultRead.next()) {

						String userid = "";

						if ((acct != null)
								&& (acct.get(CD_USUARIO) == queryResultRead
										.getString("CD_USUARIO"))) {

							String noPerfil = queryResultRead.getString(NO_PERFIL);
							String noSistema = queryResultRead
									.getString(CD_SISTEMA);

							if (noPerfil != null && noSistema != null) {

								String sistemaPerfil = noSistema.trim() + "/"
										+ noPerfil.trim();

								sisperfil.add(sistemaPerfil);
							}

						} else {

							if ((acct != null)) {

								acct.put(this.sisPerfil, sisperfil);

								if (isDebug == true) {
									log.debug("READ Account MAP: <acct"
											+ acct.toString() + ">");
								}
							}

							acct = new HashMap<String, Object>();

							userid = queryResultRead.getString("CD_USUARIO");
							if (userid != null) {
								acct.put(CD_USUARIO, userid);
							} else {
								acct.put(CD_USUARIO, "");
							}

							String noUsuario = queryResultRead
									.getString(NO_USUARIO);
							noUsuario = noUsuario.trim();
							if (noUsuario != null) {
								acct.put(NO_USUARIO, noUsuario);
							} else {
								acct.put(NO_USUARIO, "");
							}

							String codUnidade = queryResultRead
									.getString(CD_UNIDADE);
							if (codUnidade != null) {
								acct.put(CD_UNIDADE, codUnidade);
							} else {
								acct.put(CD_UNIDADE, "");
							}

							String cdSenha = queryResultRead.getString(CD_SENHA);
							if (cdSenha != null) {
								acct.put(CD_SENHA, cdSenha);
							} else {
								acct.put(CD_SENHA, "");
							}

							String cdSisDefaul = queryResultRead
									.getString(CD_SIS_DEFAUL);
							if (cdSisDefaul != null) {
								acct.put(CD_SIS_DEFAUL, cdSisDefaul);
							} else {
								acct.put(CD_SIS_DEFAUL, "");
							}

							String dtAltSenha = queryResultRead
									.getString(DT_ALT_SENHA);
							if (dtAltSenha != null) {
								acct.put(DT_ALT_SENHA, dtAltSenha);
							} else {
								acct.put(DT_ALT_SENHA, "");
							}

							String cdImprRemot = queryResultRead
									.getString(CD_IMPR_REMOT);
							if (cdImprRemot != null) {
								acct.put(CD_IMPR_REMOT, cdImprRemot);
							} else {
								acct.put(CD_IMPR_REMOT, "");
							}

							String cdSitUsu = queryResultRead.getString(CD_SIT_USU);
							if (cdSitUsu != null) {
								acct.put(CD_SIT_USU, cdSitUsu);
							} else {
								acct.put(CD_SIT_USU, "");
							}

							String dtUltAcesso = queryResultRead
									.getString(DT_ULT_ACESSO);
							if (dtUltAcesso != null) {
								acct.put(DT_ULT_ACESSO, dtUltAcesso);
							} else {
								acct.put(DT_ULT_ACESSO, "");
							}

							String cdSuregLot = queryResultRead
									.getString(CD_SUREG_LOT);
							if (cdSuregLot != null) {
								acct.put(CD_SUREG_LOT, cdSuregLot);
							} else {
								acct.put(CD_SUREG_LOT, "");
							}

							String cdCeadmAut = queryResultRead
									.getString(CD_CEADM_AUT);
							if (cdCeadmAut != null) {
								acct.put(CD_CEADM_AUT, cdCeadmAut);
							} else {
								acct.put(CD_CEADM_AUT, "");
							}

							String cdSuregAut = queryResultRead
									.getString(CD_SUREG_AUT);
							if (cdSuregAut != null) {
								acct.put(CD_SUREG_AUT, cdSuregAut);
							} else {
								acct.put(CD_SUREG_AUT, "");
							}

							String cdUnidAut = queryResultRead
									.getString(CD_UNID_AUT);
							if (cdUnidAut != null) {
								acct.put(CD_UNID_AUT, cdUnidAut);
							} else {
								acct.put(CD_UNID_AUT, "");
							}

							String idDiretoria = queryResultRead
									.getString(ID_DIRETORIA);
							if (idDiretoria != null) {
								acct.put(ID_DIRETORIA, idDiretoria);
							} else {
								acct.put(ID_DIRETORIA, "");
							}

							String qtAcessos = queryResultRead
									.getString(QT_ACESSOS);
							if (qtAcessos != null) {
								acct.put(QT_ACESSOS, qtAcessos);
							} else {
								acct.put(QT_ACESSOS, "");
							}

							String qtMensGeral = queryResultRead
									.getString(QT_MENS_GERAL);
							if (qtAcessos != null) {
								acct.put(QT_MENS_GERAL, qtMensGeral);
							} else {
								acct.put(QT_MENS_GERAL, "");
							}

							String cdTerminal = queryResultRead
									.getString(CD_TERMINAL);
							if (cdTerminal != null) {
								acct.put(CD_TERMINAL, cdTerminal);
							} else {
								acct.put(CD_TERMINAL, "");
							}

							String qtMensPerfil = queryResultRead
									.getString(QT_MENS_PERFIL);
							if (qtMensPerfil != null) {
								acct.put(QT_MENS_PERFIL, qtMensPerfil);
							} else {
								acct.put(QT_MENS_PERFIL, "");
							}

							String idStatus = queryResultRead.getString(ID_STATUS);
							if (idStatus != null) {
								acct.put(ID_STATUS, idStatus);
							} else {
								acct.put(ID_STATUS, "");
							}

							String idCef = queryResultRead.getString(ID_CEF);
							if (idCef != null) {
								acct.put(ID_CEF, idCef);
							} else {
								acct.put(ID_CEF, "");
							}

							String dtPrvDesl = queryResultRead
									.getString(DT_PRV_DESL);
							if (dtPrvDesl != null) {
								acct.put(DT_PRV_DESL, dtPrvDesl);
							} else {
								acct.put(DT_PRV_DESL, "");
							}

							String cdNoRemot = queryResultRead
									.getString(CD_NO_REMOT);
							if (cdNoRemot != null) {
								acct.put(CD_NO_REMOT, cdNoRemot);
							} else {
								acct.put(CD_NO_REMOT, "");
							}

							String cdFuncao = queryResultRead.getString(CD_FUNCAO);
							if (cdFuncao != null) {
								acct.put(CD_FUNCAO, cdFuncao);
							} else {
								acct.put(CD_FUNCAO, "");
							}

							String coSegmento = queryResultRead
									.getString(CO_SEGMENTO);
							if (coSegmento != null) {
								acct.put(CO_SEGMENTO, coSegmento);
							} else {
								acct.put(CO_SEGMENTO, "");
							}

							String nuCnpj = queryResultRead.getString(NU_CNPJ);
							if (nuCnpj != null) {
								acct.put(NU_CNPJ, nuCnpj);
							} else {
								acct.put(NU_CNPJ, "");
							}

							String nuMatrEmp = queryResultRead
									.getString(NU_MATR_EMP);
							if (nuMatrEmp != null) {
								acct.put(NU_MATR_EMP, nuMatrEmp);
							} else {
								acct.put(NU_MATR_EMP, "");
							}

							String nuCaEnFisica = queryResultRead
									.getString(NU_CA_EN_FISICA);
							if (nuCaEnFisica != null) {
								acct.put(NU_CA_EN_FISICA, nuCaEnFisica);
							} else {
								acct.put(NU_CA_EN_FISICA, "");
							}

							String noPerfil = queryResultRead.getString(NO_PERFIL);
							String noSistema = queryResultRead
									.getString(CD_SISTEMA);

							if (noPerfil != null && noSistema != null) {

								String sistemaPerfil = noSistema.trim() + "/"
										+ noPerfil.trim();

								sisperfil.add(sistemaPerfil);

							}
							
							acct.put(this.sisPerfil, sisperfil);

							if (isDebug == true) {
								log.debug("READ Account MAP: <acct"
										+ acct.toString() + ">");
							}
						}

					}

			} else if (OBJECT_TYPE_GROUP.equals(this.objectType)) {
				String[] part = nativeIdentifier.split("/");
				String sistema = part[0];
				String perfil = part[1];

				query = "SELECT * FROM gal.GALVWS09_PRFL_SAILPOINT where CO_SISTEMA='"
						+ sistema + "' AND NO_PERFIL='" + perfil + "'";
				stmtRead = m_dbConn.createStatement();
				if (isDebug == true) {
					log.debug("READ Group : <query" + query + ">");
				}
				queryResultRead = stmtRead.executeQuery(query);

				if (queryResultRead.next()) {
					grp = new HashMap<String, Object>();

					String numeroPerfil = queryResultRead.getString(CD_PERFIL);
					String codSistema = queryResultRead.getString(CD_SISTEMA);
					String nomePerfil = queryResultRead.getString(NO_PERFIL);
					String perfilComposto = codSistema.trim() + "/"
							+ nomePerfil.trim();

					grp.put(sisPerfil, perfilComposto);
					grp.put(CD_PERFIL, numeroPerfil);
					grp.put(CD_SISTEMA, codSistema);
					grp.put(NO_PERFIL, nomePerfil);

					if (isDebug == true) {
						log.debug("READ Group : <grp" + grp.toString() + ">");
					}
				}

			}

			if (queryResultRead != null)
				queryResultRead.close();

			if (stmtRead != null)
				stmtRead.close();

		} catch (Exception e) {
			if (isDebug == true) {
				log.debug("Exception occured " + e + ">");
			}
			throw new ConnectorException(e);
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
				log.debug("Exception occured " + e + ">");
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
	public Result create(String nativeIdentifier, List<Item> items)
			throws ConnectorException, ObjectAlreadyExistsException {

		Result result = new Result();

		ArrayList<String> arrayPerfilSistema = new ArrayList<String>();

		String consultaNaci = "", atualizacaoNac = "", nivelSeg = "", codUsuario = "", nomeUsuario = "";
		String codigoUnidade = "", codigoSisDefaul = "", codigoSistUsua = "", codigoSuregLot = "", idDeret = "";
		String status = "", cef = "", funcao = "", sistema = "", perfil = "", matricula = "";

		codUsuario = nativeIdentifier;

		if (isDebug == true) {
			log.debug("CREATE <codUsuario: " + codUsuario + ">");
		}

		try {

			for (openconnector.Item item : items) {

				String name = item.getName();
				String value = item.getValue().toString();

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

			if (idDeret.isEmpty()) {
				idDeret = "N";
			} else {
				idDeret = "S";
			}

			if (codigoUnidade.isEmpty()) {
				codigoUnidade = "0";
			}

			if (codigoSuregLot.isEmpty()) {
				codigoSuregLot = "0";
			}

			if (funcao.isEmpty()) {
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

			for (String sistemPerfis : arrayPerfilSistema) {

				String[] array = sistemPerfis.split("/");

				sistema = array[0];
				perfil = array[1];

				perfil = recuperaCodPerfil(perfil, sistema);

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

					if (isDebug == true) {
						log.debug("CREATE USUARIO EXISTE ALTERANDO STATUS <codUsuario: "
								+ codUsuario
								+ "> <noUsuario: "
								+ noUsuario
								+ "> <codUnidade: "
								+ codUnidade
								+ "> <cdSistemDefaul: "
								+ cdSistemDefaul
								+ "> <codSisUsu: "
								+ codSisUsu
								+ "> <codSuregLota: "
								+ codSuregLota
								+ "> <idDiretoria: "
								+ idDiretoria
								+ "> <idStat: "
								+ idStat
								+ "> <idCf: "
								+ idCf
								+ ">");
					}

					trataDadosAtualizacao(codUsuario, noUsuario, codUnidade,
							cdSistemDefaul, codSisUsu, codSuregLota,
							idDiretoria, idStat, idCf);

				} else {

					if (isDebug == true) {
						log.debug("CREATE USUARIO CRIAR USUARIO <codUsuario: "
								+ codUsuario + "> <nomeUsuario: " + nomeUsuario
								+ "> <codigoUnidade: " + codigoUnidade
								+ "> <codigoSisDefaul: " + codigoSisDefaul
								+ "> <codigoSistUsua: " + codigoSistUsua
								+ "> <codigoSuregLot: " + codigoSuregLot
								+ "> <idDeret: " + idDeret + "> <status: "
								+ status + "> <cef: " + cef + "> <funcao: "
								+ funcao + "> <matricula" + matricula + ">");
					}

					trataDadoCreate(codUsuario, nomeUsuario, codigoUnidade,
							codigoSisDefaul, codigoSistUsua, codigoSuregLot,
							idDeret, status, cef, funcao, matricula);
				}

				if (isDebug == true) {
					log.debug("CREATE USUARIO CRIAR CONEXAO <sistema: "
							+ sistema + "> <codUsuario: " + codUsuario
							+ "> <perfil: " + perfil + "> <nivelSeg: "
							+ nivelSeg + "> <codigoSuregLot: " + codigoSuregLot
							+ "> <codigoUnidade: " + codigoUnidade
							+ "> <idDeret: " + idDeret + "> <status: " + status
							+ "> <atualizacaoNac: " + atualizacaoNac
							+ "> <consultaNaci: " + consultaNaci + ">");
				}

				trataDadoConexao(sistema, codUsuario, perfil, nivelSeg,
						codigoSuregLot, codigoUnidade, idDeret, status,
						atualizacaoNac, consultaNaci);

				boolean usuarioCriado = consultaCreate(codUsuario);
				boolean usuarioNoGrupo = consultaUsuarioGrupo(codUsuario,
						sistema, perfil);

				if (usuarioCriado == true && usuarioNoGrupo == true) {
					result = new Result(Result.Status.Committed);
				}
				if (usuarioCriado == false && usuarioNoGrupo == false) {
					result = new Result(Result.Status.Failed);
				}
			}

		} catch (Exception e) {
			result = new Result(Result.Status.Failed);
			result.add("TIPO APP SINAV " + e.getMessage());
			if (isDebug == true) {
				log.debug("Exception occured ", e);
			}
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

		String codUsuario = "", nivelSeg = "", codigoSuregLot = "", codigoUnidade = "", idDeret = "", status = "", atualizacaoNac = "", consultaNaci = "", sistema = "", perfil = "";

		if (null == existing) {
			throw new ObjectNotFoundException(nativeIdentifier);
		}

		codUsuario = nativeIdentifier;

		if (items != null) {
			for (Item item : items) {
				String name = item.getName();
				String value = item.getValue().toString();
				Item.Operation op = item.getOperation();

				switch (op) {
				case Add: {

					List<String> arrayPerfilSistema = new ArrayList<String>();
					try {
						boolean usuarioCriado = consultaCreate(codUsuario);

						if (usuarioCriado == true) {

							if (name.equalsIgnoreCase(nivelSeguranca)) {
								nivelSeg = value;
							}

							if (name.equalsIgnoreCase(atualizacaoNacional)) {

								atualizacaoNac = value;
							}

							if (atualizacaoNac.equals("true")) {
								atualizacaoNac = "S";
							} else {
								atualizacaoNac = "N";
							}

							if (name.equalsIgnoreCase(consultaNacional)) {

								consultaNaci = value;
							}

							if (consultaNaci.equals("true")) {
								consultaNaci = "S";
							} else {
								consultaNaci = "N";
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

									String sistemaPerfis = valueList.get(0)
											.toString();

									arrayPerfilSistema.add(sistemaPerfis);
								}
							}

							for (String sistemPerfis : arrayPerfilSistema) {

								String[] array = sistemPerfis.split("/");

								sistema = array[0];
								perfil = array[1];

								perfil = recuperaCodPerfil(perfil, sistema);

								Map<String, Object> recuperaDados = dadosUsuario(codUsuario);

								cdSuregLot = recuperaDados.get(CD_SUREG_LOT)
										.toString();
								cdUnidade = recuperaDados.get(CD_UNIDADE)
										.toString();
								idDeretoria = recuperaDados.get(ID_DIRETORIA)
										.toString();
								idStatus = recuperaDados.get(ID_STATUS)
										.toString();

								if (idDeret.isEmpty()) {
									idDeret = "N";
								} else {
									idDeret = "S";
								}

								if (codigoUnidade.isEmpty()) {
									codigoUnidade = "0";
								}

								if (codigoSuregLot.isEmpty()) {
									codigoSuregLot = "0";
								}

								if (isDebug == true) {
									log.debug("UPDATE ADD CONEXAO <sistema: "
											+ sistema + "> <codUsuario: "
											+ codUsuario + "> <perfil: "
											+ perfil + "> <nivelSeg: "
											+ nivelSeg + "> <codigoSuregLot: "
											+ codigoSuregLot
											+ "> <codigoUnidade: "
											+ codigoUnidade + "> <idDeret: "
											+ idDeret + "> <status: " + status
											+ "> <atualizacaoNac: "
											+ atualizacaoNac
											+ "> <consultaNaci: "
											+ consultaNaci + ">");
								}

								trataDadoConexao(sistema, codUsuario, perfil,
										nivelSeg, codigoSuregLot,
										codigoUnidade, idDeret, status,
										atualizacaoNac, consultaNaci);

								boolean usuarioNoGrupo = consultaUsuarioGrupo(
										codUsuario, sistema, perfil);

								if (usuarioNoGrupo == true) {
									result = new Result(Result.Status.Committed);
								}
								if (usuarioNoGrupo == false) {
									result = new Result(Result.Status.Failed);
								}
							}

						}
						if (usuarioCriado == false) {
							result = new Result(Result.Status.Failed);
							result.add("O usuario não esta cadastrado no Sinav");

						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
					break;
				case Remove: {

					List<String> arrayPerfilSistema = new ArrayList<String>();

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

							if (isDebug == true) {
								log.debug("UPDATE REMOVE CONEXAO <sistema: "
										+ sistema + "> <codUsuario: "
										+ codUsuario + ">");
							}
							trataDadoDesvincularConexao(sistema, codUsuario);

							boolean usuarioNoGrupo = consultaUsuarioGrupo(
									codUsuario, sistema, perfil);

							if (usuarioNoGrupo == true) {
								result = new Result(Result.Status.Failed);
							}
							if (usuarioNoGrupo == false) {
								result = new Result(Result.Status.Committed);

							}

						}

					} catch (Exception e) {
						e.printStackTrace();
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

		Result result = new Result(Result.Status.Committed);

		try {

			String cdUsuario, sistema;

			cdUsuario = nativeIdentifier;

			listaGrupoUsuario(cdUsuario);

			if (arrayGrupoUsuario.isEmpty()) {

			} else {

				for (Object sis : arrayGrupoUsuario) {

					sistema = sis.toString();

					trataDadoDesvincularConexao(sistema, cdUsuario);
				}

				trataExclusaoUsuario(cdUsuario);

				boolean usuarioCriado = consultaCreate(cdUsuario);

				if (usuarioCriado == true) {
					result = new Result(Result.Status.Failed);
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
		// add code to free resources like file or database connection
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

			debub();

			if (isDebug == true) {
				log.debug("INIT  user <" + m_sUser + ">  m_sUrl <" + m_sUrl
						+ "> m_sDriverName <" + m_sDriverName
						+ "> nomeServidorSics <" + nomeServidorSics
						+ "> numPortaSics <" + numPortaSics
						+ "> userNameSics <" + userNameSics + ">");
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

	private Map<String, Map<String, Object>> getObjectsMap() throws Exception {

		Filter filter = _filter;
		Statement stmt = null;
		String query = null;
		Map<String, Object> acct = null;
		Map<String, Object> grp = null;

		try {
			init();

		} catch (Exception e) {
			throw new Exception(e);
		}

		String chaveMapa = "";

		m_acctsMap.clear();
		m_groupsMap.clear();

		try {
			if (OBJECT_TYPE_ACCOUNT.equals(this.objectType)) {

				if (isDebug == true) {
					log.debug("Get Objects Map Account....");
					log.debug("Account Filter <" + filter + ">");
				}

				if (filter != null) {

					query = "SELECT * FROM gal.GALVWS07_USRO_SAILPOINT LEFT JOIN gal.GALVWS09_PRFL_SAILPOINT "
							+ "ON gal.GALVWS07_USRO_SAILPOINT.CD_USUARIO = gal.GALVWS09_PRFL_SAILPOINT.CD_USUARIO where ID_STATUS = 'A' and"
							+ " gal.GALVWS07_USRO_SAILPOINT.CD_USUARIO = '"
							+ filter + "'";

					stmt = m_dbConn.createStatement();

					if (isDebug == true) {
						log.debug("Query <" + filter + ">");
					}

					queryResult = stmt.executeQuery(query);

					ArrayList<String> sisperfil = new ArrayList<String>();

					while (queryResult.next()) {

						String userid = "";

						if ((acct != null)
								&& (acct.get(CD_USUARIO) == queryResult
										.getString("CD_USUARIO"))) {

							String noPerfil = queryResult.getString(NO_PERFIL);
							String noSistema = queryResult
									.getString(CD_SISTEMA);

							if (noPerfil != null && noSistema != null) {

								String sistemaPerfil = noSistema.trim() + "/"
										+ noPerfil.trim();

								sisperfil.add(sistemaPerfil);
							}

						} else {

							if ((acct != null)) {

								acct.put(this.sisPerfil, sisperfil);
								
								m_acctsMap.put(chaveMapa, acct);

								if (isDebug == true) {
									log.debug("M_AcctsMap <" + m_acctsMap + ">");
								}
							}

							acct = new HashMap<String, Object>();

							userid = queryResult.getString("CD_USUARIO");
							chaveMapa = userid;
							if (userid != null) {
								acct.put(CD_USUARIO, userid);
							} else {
								acct.put(CD_USUARIO, "");
							}

							String noUsuario = queryResult
									.getString(NO_USUARIO);
							noUsuario = noUsuario.trim();
							if (noUsuario != null) {
								acct.put(NO_USUARIO, noUsuario);
							} else {
								acct.put(NO_USUARIO, "");
							}

							String codUnidade = queryResult
									.getString(CD_UNIDADE);
							if (codUnidade != null) {
								acct.put(CD_UNIDADE, codUnidade);
							} else {
								acct.put(CD_UNIDADE, "");
							}

							String cdSenha = queryResult.getString(CD_SENHA);
							if (cdSenha != null) {
								acct.put(CD_SENHA, cdSenha);
							} else {
								acct.put(CD_SENHA, "");
							}

							String cdSisDefaul = queryResult
									.getString(CD_SIS_DEFAUL);
							if (cdSisDefaul != null) {
								acct.put(CD_SIS_DEFAUL, cdSisDefaul);
							} else {
								acct.put(CD_SIS_DEFAUL, "");
							}

							String dtAltSenha = queryResult
									.getString(DT_ALT_SENHA);
							if (dtAltSenha != null) {
								acct.put(DT_ALT_SENHA, dtAltSenha);
							} else {
								acct.put(DT_ALT_SENHA, "");
							}

							String cdImprRemot = queryResult
									.getString(CD_IMPR_REMOT);
							if (cdImprRemot != null) {
								acct.put(CD_IMPR_REMOT, cdImprRemot);
							} else {
								acct.put(CD_IMPR_REMOT, "");
							}

							String cdSitUsu = queryResult.getString(CD_SIT_USU);
							if (cdSitUsu != null) {
								acct.put(CD_SIT_USU, cdSitUsu);
							} else {
								acct.put(CD_SIT_USU, "");
							}

							String dtUltAcesso = queryResult
									.getString(DT_ULT_ACESSO);
							if (dtUltAcesso != null) {
								acct.put(DT_ULT_ACESSO, dtUltAcesso);
							} else {
								acct.put(DT_ULT_ACESSO, "");
							}

							String cdSuregLot = queryResult
									.getString(CD_SUREG_LOT);
							if (cdSuregLot != null) {
								acct.put(CD_SUREG_LOT, cdSuregLot);
							} else {
								acct.put(CD_SUREG_LOT, "");
							}

							String cdCeadmAut = queryResult
									.getString(CD_CEADM_AUT);
							if (cdCeadmAut != null) {
								acct.put(CD_CEADM_AUT, cdCeadmAut);
							} else {
								acct.put(CD_CEADM_AUT, "");
							}

							String cdSuregAut = queryResult
									.getString(CD_SUREG_AUT);
							if (cdSuregAut != null) {
								acct.put(CD_SUREG_AUT, cdSuregAut);
							} else {
								acct.put(CD_SUREG_AUT, "");
							}

							String cdUnidAut = queryResult
									.getString(CD_UNID_AUT);
							if (cdUnidAut != null) {
								acct.put(CD_UNID_AUT, cdUnidAut);
							} else {
								acct.put(CD_UNID_AUT, "");
							}

							String idDiretoria = queryResult
									.getString(ID_DIRETORIA);
							if (idDiretoria != null) {
								acct.put(ID_DIRETORIA, idDiretoria);
							} else {
								acct.put(ID_DIRETORIA, "");
							}

							String qtAcessos = queryResult
									.getString(QT_ACESSOS);
							if (qtAcessos != null) {
								acct.put(QT_ACESSOS, qtAcessos);
							} else {
								acct.put(QT_ACESSOS, "");
							}

							String qtMensGeral = queryResult
									.getString(QT_MENS_GERAL);
							if (qtAcessos != null) {
								acct.put(QT_MENS_GERAL, qtMensGeral);
							} else {
								acct.put(QT_MENS_GERAL, "");
							}

							String cdTerminal = queryResult
									.getString(CD_TERMINAL);
							if (cdTerminal != null) {
								acct.put(CD_TERMINAL, cdTerminal);
							} else {
								acct.put(CD_TERMINAL, "");
							}

							String qtMensPerfil = queryResult
									.getString(QT_MENS_PERFIL);
							if (qtMensPerfil != null) {
								acct.put(QT_MENS_PERFIL, qtMensPerfil);
							} else {
								acct.put(QT_MENS_PERFIL, "");
							}

							String idStatus = queryResult.getString(ID_STATUS);
							if (idStatus != null) {
								acct.put(ID_STATUS, idStatus);
							} else {
								acct.put(ID_STATUS, "");
							}

							String idCef = queryResult.getString(ID_CEF);
							if (idCef != null) {
								acct.put(ID_CEF, idCef);
							} else {
								acct.put(ID_CEF, "");
							}

							String dtPrvDesl = queryResult
									.getString(DT_PRV_DESL);
							if (dtPrvDesl != null) {
								acct.put(DT_PRV_DESL, dtPrvDesl);
							} else {
								acct.put(DT_PRV_DESL, "");
							}

							String cdNoRemot = queryResult
									.getString(CD_NO_REMOT);
							if (cdNoRemot != null) {
								acct.put(CD_NO_REMOT, cdNoRemot);
							} else {
								acct.put(CD_NO_REMOT, "");
							}

							String cdFuncao = queryResult.getString(CD_FUNCAO);
							if (cdFuncao != null) {
								acct.put(CD_FUNCAO, cdFuncao);
							} else {
								acct.put(CD_FUNCAO, "");
							}

							String coSegmento = queryResult
									.getString(CO_SEGMENTO);
							if (coSegmento != null) {
								acct.put(CO_SEGMENTO, coSegmento);
							} else {
								acct.put(CO_SEGMENTO, "");
							}

							String nuCnpj = queryResult.getString(NU_CNPJ);
							if (nuCnpj != null) {
								acct.put(NU_CNPJ, nuCnpj);
							} else {
								acct.put(NU_CNPJ, "");
							}

							String nuMatrEmp = queryResult
									.getString(NU_MATR_EMP);
							if (nuMatrEmp != null) {
								acct.put(NU_MATR_EMP, nuMatrEmp);
							} else {
								acct.put(NU_MATR_EMP, "");
							}

							String nuCaEnFisica = queryResult
									.getString(NU_CA_EN_FISICA);
							if (nuCaEnFisica != null) {
								acct.put(NU_CA_EN_FISICA, nuCaEnFisica);
							} else {
								acct.put(NU_CA_EN_FISICA, "");
							}

							String noPerfil = queryResult.getString(NO_PERFIL);
							String noSistema = queryResult
									.getString(CD_SISTEMA);

							if (noPerfil != null && noSistema != null) {

								String sistemaPerfil = noSistema.trim() + "/"
										+ noPerfil.trim();

								sisperfil.add(sistemaPerfil);
							}
							
							acct.put(this.sisPerfil, sisperfil);

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

					query = "SELECT * FROM gal.GALVWS07_USRO_SAILPOINT LEFT JOIN gal.GALVWS09_PRFL_SAILPOINT "
							+ "ON gal.GALVWS07_USRO_SAILPOINT.CD_USUARIO = gal.GALVWS09_PRFL_SAILPOINT.CD_USUARIO where ID_STATUS = 'A' and"
							+ " gal.GALVWS07_USRO_SAILPOINT.CD_USUARIO > '"
							+ m_sLastUser
							+ "'  order by gal.GALVWS07_USRO_SAILPOINT.CD_USUARIO asc";

					stmt = m_dbConn.createStatement();

					log.debug("Query <" + query + ">");

					if (isDebug == true) {
						log.debug("Query <" + query + ">");
					}

					queryResult = stmt.executeQuery(query);

				}

				ArrayList<String> sisperfil = new ArrayList<String>();

				int fetched = 0;
				while (queryResult.next()) {

					String userid = "";

					if ((acct != null)
							&& (acct.get(CD_USUARIO) == queryResult
									.getString("CD_USUARIO"))) {

						String noPerfil = queryResult.getString(NO_PERFIL);
						String noSistema = queryResult.getString(CD_SISTEMA);

						if (noPerfil != null && noSistema != null) {

							String sistemaPerfil = noSistema.trim() + "/" + noPerfil.trim();

							sisperfil.add(sistemaPerfil);
						}

					} else {

						if ((acct != null)) {

							acct.put(this.sisPerfil, sisperfil);
							
							m_acctsMap.put(chaveMapa, acct);

							if (isDebug == true) {
								log.debug("M_acctsMap <" + m_acctsMap + ">");
							}
						}

						acct = new HashMap<String, Object>();

						userid = queryResult.getString("CD_USUARIO");
						chaveMapa = userid;
						if (userid != null) {
							acct.put(CD_USUARIO, userid);
						} else {
							acct.put(CD_USUARIO, "");
						}

						String noUsuario = queryResult.getString(NO_USUARIO);
						noUsuario = noUsuario.trim();
						if (noUsuario != null) {
							acct.put(NO_USUARIO, noUsuario);
						} else {
							acct.put(NO_USUARIO, "");
						}

						String codUnidade = queryResult.getString(CD_UNIDADE);
						if (codUnidade != null) {
							acct.put(CD_UNIDADE, codUnidade);
						} else {
							acct.put(CD_UNIDADE, "");
						}

						String cdSenha = queryResult.getString(CD_SENHA);
						if (cdSenha != null) {
							acct.put(CD_SENHA, cdSenha);
						} else {
							acct.put(CD_SENHA, "");
						}

						String cdSisDefaul = queryResult
								.getString(CD_SIS_DEFAUL);
						if (cdSisDefaul != null) {
							acct.put(CD_SIS_DEFAUL, cdSisDefaul);
						} else {
							acct.put(CD_SIS_DEFAUL, "");
						}

						String dtAltSenha = queryResult.getString(DT_ALT_SENHA);
						if (dtAltSenha != null) {
							acct.put(DT_ALT_SENHA, dtAltSenha);
						} else {
							acct.put(DT_ALT_SENHA, "");
						}

						String cdImprRemot = queryResult
								.getString(CD_IMPR_REMOT);
						if (cdImprRemot != null) {
							acct.put(CD_IMPR_REMOT, cdImprRemot);
						} else {
							acct.put(CD_IMPR_REMOT, "");
						}

						String cdSitUsu = queryResult.getString(CD_SIT_USU);
						if (cdSitUsu != null) {
							acct.put(CD_SIT_USU, cdSitUsu);
						} else {
							acct.put(CD_SIT_USU, "");
						}

						String dtUltAcesso = queryResult
								.getString(DT_ULT_ACESSO);
						if (dtUltAcesso != null) {
							acct.put(DT_ULT_ACESSO, dtUltAcesso);
						} else {
							acct.put(DT_ULT_ACESSO, "");
						}

						String cdSuregLot = queryResult.getString(CD_SUREG_LOT);
						if (cdSuregLot != null) {
							acct.put(CD_SUREG_LOT, cdSuregLot);
						} else {
							acct.put(CD_SUREG_LOT, "");
						}

						String cdCeadmAut = queryResult.getString(CD_CEADM_AUT);
						if (cdCeadmAut != null) {
							acct.put(CD_CEADM_AUT, cdCeadmAut);
						} else {
							acct.put(CD_CEADM_AUT, "");
						}

						String cdSuregAut = queryResult.getString(CD_SUREG_AUT);
						if (cdSuregAut != null) {
							acct.put(CD_SUREG_AUT, cdSuregAut);
						} else {
							acct.put(CD_SUREG_AUT, "");
						}

						String cdUnidAut = queryResult.getString(CD_UNID_AUT);
						if (cdUnidAut != null) {
							acct.put(CD_UNID_AUT, cdUnidAut);
						} else {
							acct.put(CD_UNID_AUT, "");
						}

						String idDiretoria = queryResult
								.getString(ID_DIRETORIA);
						if (idDiretoria != null) {
							acct.put(ID_DIRETORIA, idDiretoria);
						} else {
							acct.put(ID_DIRETORIA, "");
						}

						String qtAcessos = queryResult.getString(QT_ACESSOS);
						if (qtAcessos != null) {
							acct.put(QT_ACESSOS, qtAcessos);
						} else {
							acct.put(QT_ACESSOS, "");
						}

						String qtMensGeral = queryResult
								.getString(QT_MENS_GERAL);
						if (qtAcessos != null) {
							acct.put(QT_MENS_GERAL, qtMensGeral);
						} else {
							acct.put(QT_MENS_GERAL, "");
						}

						String cdTerminal = queryResult.getString(CD_TERMINAL);
						if (cdTerminal != null) {
							acct.put(CD_TERMINAL, cdTerminal);
						} else {
							acct.put(CD_TERMINAL, "");
						}

						String qtMensPerfil = queryResult
								.getString(QT_MENS_PERFIL);
						if (qtMensPerfil != null) {
							acct.put(QT_MENS_PERFIL, qtMensPerfil);
						} else {
							acct.put(QT_MENS_PERFIL, "");
						}

						String idStatus = queryResult.getString(ID_STATUS);
						if (idStatus != null) {
							acct.put(ID_STATUS, idStatus);
						} else {
							acct.put(ID_STATUS, "");
						}

						String idCef = queryResult.getString(ID_CEF);
						if (idCef != null) {
							acct.put(ID_CEF, idCef);
						} else {
							acct.put(ID_CEF, "");
						}

						String dtPrvDesl = queryResult.getString(DT_PRV_DESL);
						if (dtPrvDesl != null) {
							acct.put(DT_PRV_DESL, dtPrvDesl);
						} else {
							acct.put(DT_PRV_DESL, "");
						}

						String cdNoRemot = queryResult.getString(CD_NO_REMOT);
						if (cdNoRemot != null) {
							acct.put(CD_NO_REMOT, cdNoRemot);
						} else {
							acct.put(CD_NO_REMOT, "");
						}

						String cdFuncao = queryResult.getString(CD_FUNCAO);
						if (cdFuncao != null) {
							acct.put(CD_FUNCAO, cdFuncao);
						} else {
							acct.put(CD_FUNCAO, "");
						}

						String coSegmento = queryResult.getString(CO_SEGMENTO);
						if (coSegmento != null) {
							acct.put(CO_SEGMENTO, coSegmento);
						} else {
							acct.put(CO_SEGMENTO, "");
						}

						String nuCnpj = queryResult.getString(NU_CNPJ);
						if (nuCnpj != null) {
							acct.put(NU_CNPJ, nuCnpj);
						} else {
							acct.put(NU_CNPJ, "");
						}

						String nuMatrEmp = queryResult.getString(NU_MATR_EMP);
						if (nuMatrEmp != null) {
							acct.put(NU_MATR_EMP, nuMatrEmp);
						} else {
							acct.put(NU_MATR_EMP, "");
						}

						String nuCaEnFisica = queryResult
								.getString(NU_CA_EN_FISICA);
						if (nuCaEnFisica != null) {
							acct.put(NU_CA_EN_FISICA, nuCaEnFisica);
						} else {
							acct.put(NU_CA_EN_FISICA, "");
						}

						String noPerfil = queryResult.getString(NO_PERFIL);
						String noSistema = queryResult.getString(CD_SISTEMA);

						if (noPerfil != null && noSistema != null) {

							String sistemaPerfil = noSistema.trim() + "/" + noPerfil.trim();

							sisperfil.add(sistemaPerfil);
						}
						
						acct.put(this.sisPerfil, sisperfil);

					}

					fetched = fetched + 1;
					contador = contador + 1;

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

						break;
					} else {
						
						queryResult.rowDeleted();
					}

					
				}

//				valorFullqueryResult = queryResult;
//
//				if (queryResult != null) {
//					queryResult.close();
//					if (isDebug == true) {
//						log.debug("queryResult Close");
//					}
//				}
//				queryResult = null;

//				if (stmt != null)
//					stmt.close();
//				stmt = null;

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
					
						String sis = "", per = "";

						String[] separa = seperaGrupo.split("/");
						sis = separa[0].toString();
						per = separa[1].toString();
						
						query = "SELECT * FROM gal.GALVWS09_PRFL_SAILPOINT where NO_PERFIL='"+per+"' AND CD_SISTEMA='"+sis+"'" ;

						stmt = m_dbConn.createStatement();

						if (isDebug == true) {
							log.debug(" query " + query);
						}

						
						
						queryResult = stmt.executeQuery(query);
						
						while (queryResult.next()) {
							grp = new HashMap<String, Object>();
							grp = new HashMap<String, Object>();

							String numeroPerfil = queryResult.getString(CD_PERFIL).trim();
							String codSistema = queryResult.getString(CD_SISTEMA).trim();
							String nomePerfil = queryResult.getString(NO_PERFIL).trim();
							String perfilComposto = codSistema.trim() + "/"
									+ nomePerfil.trim();

							grp.put(sisPerfil, perfilComposto);
							grp.put(CD_PERFIL, numeroPerfil);
							grp.put(CD_SISTEMA, codSistema);
							grp.put(NO_PERFIL, nomePerfil);

							resultObjectList.add(groups);

							m_groupsMap.put(perfilComposto, grp);

							if (isDebug == true) {
								log.debug("m_groupsMap <" + m_groupsMap.toString()
										+ ">");
							}

						}
				}
				

				
				if (queryResultGrupo == null) {
					
					query = "SELECT * FROM gal.GALVWS09_PRFL_SAILPOINT";

					stmt = m_dbConn.createStatement();

					if (isDebug == true) {
						log.debug(" query " + query);
					}

					queryResultGrupo = stmt.executeQuery(query);
					
				}
				
				

				int fetched = 0;
				while (queryResultGrupo.next()) {
					grp = new HashMap<String, Object>();
					grp = new HashMap<String, Object>();

					String numeroPerfil = queryResultGrupo.getString(CD_PERFIL).trim();
					String codSistema = queryResultGrupo.getString(CD_SISTEMA).trim();
					String nomePerfil = queryResultGrupo.getString(NO_PERFIL).trim();
					String perfilComposto = codSistema.trim() + "/"
							+ nomePerfil.trim();

					grp.put(sisPerfil, perfilComposto);
					grp.put(CD_PERFIL, numeroPerfil);
					grp.put(CD_SISTEMA, codSistema);
					grp.put(NO_PERFIL, nomePerfil);

					resultObjectList.add(groups);

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

	public Iterator<Map<String, Object>> IterateNextPage(Filter filter) {
		try {

			if (isDebug) {
				log.debug("IterateNextPage");
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

	public Map<String, Object> dadosUsuario(String codUsuario) throws Exception {

		init();

		dadosUsuario = new HashMap<String, Object>();

		if (isDebug == true) {
			log.debug("Dados Usuario codUsuario <" + codUsuario + ">");
		}

		String query = "SELECT * FROM gal.GALVWS07_USRO_SAILPOINT where CD_USUARIO='"
				+ codUsuario + "'";

		if (isDebug == true) {
			log.debug("Dados Usuario Query <" + query + ">");
		}

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

			String cdSenha = rs.getString(CD_SENHA);
			if (cdSenha != null) {
				dadosUsuario.put(CD_SENHA, cdSenha);
			} else {
				dadosUsuario.put(CD_SENHA, "");
			}

			String cdSisDefaul = rs.getString(CD_SIS_DEFAUL);
			if (cdSisDefaul != null) {
				dadosUsuario.put(CD_SIS_DEFAUL, cdSisDefaul);
			} else {
				dadosUsuario.put(CD_SIS_DEFAUL, "");
			}

			String dtAltSenha = rs.getString(DT_ALT_SENHA);
			if (dtAltSenha != null) {
				dadosUsuario.put(DT_ALT_SENHA, dtAltSenha);
			} else {
				dadosUsuario.put(DT_ALT_SENHA, "");
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

			String dtUltAcesso = rs.getString(DT_ULT_ACESSO);
			if (dtUltAcesso != null) {
				dadosUsuario.put(DT_ULT_ACESSO, dtUltAcesso);
			} else {
				dadosUsuario.put(DT_ULT_ACESSO, "");
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

			String qtAcessos = rs.getString(QT_ACESSOS);
			if (qtAcessos != null) {
				dadosUsuario.put(QT_ACESSOS, qtAcessos);
			} else {
				dadosUsuario.put(QT_ACESSOS, "");
			}

			String qtMensGeral = rs.getString(QT_MENS_GERAL);
			if (qtAcessos != null) {
				dadosUsuario.put(QT_MENS_GERAL, qtMensGeral);
			} else {
				dadosUsuario.put(QT_MENS_GERAL, "");
			}

			String cdTerminal = rs.getString(CD_TERMINAL);
			if (cdTerminal != null) {
				dadosUsuario.put(CD_TERMINAL, cdTerminal);
			} else {
				dadosUsuario.put(CD_TERMINAL, "");
			}

			String qtMensPerfil = rs.getString(QT_MENS_PERFIL);
			if (qtMensPerfil != null) {
				dadosUsuario.put(QT_MENS_PERFIL, qtMensPerfil);
			} else {
				dadosUsuario.put(QT_MENS_PERFIL, "");
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

			String dtPrvDesl = rs.getString(DT_PRV_DESL);
			if (dtPrvDesl != null) {
				dadosUsuario.put(DT_PRV_DESL, dtPrvDesl);
			} else {
				dadosUsuario.put(DT_PRV_DESL, "");
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

		if (isDebug == true) {
			log.debug("Dados Usuario dadosUsuario <" + dadosUsuario.toString()
					+ ">");
		}

		return dadosUsuario;

	}

	public List listaGrupoUsuario(String codUsuario) throws Exception {
		String sistema = "";

		init();
		String query = "SELECT * FROM gal.GALVWS08_USRO_SSTMA_SAILPOINT Where CD_USUARIO= '"
				+ codUsuario + "'";

		if (isDebug == true) {

			log.debug("Consulta usaurio no Grupo Query: <" + query + ">");
		}

		Statement stmt = m_dbConn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		while (rs.next()) {
			sistema = rs.getString(CD_SISTEMA);

			arrayGrupoUsuario.add(sistema);
		}

		stmt.close();

		if (isDebug == true) {

			log.debug("Consulta usaurio no Grupo arrayGrupoUsuario: <"
					+ arrayGrupoUsuario.toString() + ">");
		}

		return arrayGrupoUsuario;
	}

	public String nomeGroup(String codPerfil, String codSistema)
			throws Exception {

		String query = "SELECT * FROM gal.GALVWS09_PRFL_SAILPOINT where CD_PERFIL='"
				+ codPerfil + "' AND CD_SISTEMA='" + codSistema + "'";

		String noGrupo = "";
		Statement stmt = m_dbConn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		while (rs.next()) {
			noGrupo = rs.getString(NO_PERFIL);
		}

		stmt.close();

		return noGrupo;

	}

	public boolean consultaCreate(String codUsuario) throws Exception {
		boolean ret = false;
		String usuario = "";

		init();
		String query = "SELECT * FROM gal.GALVWS07_USRO_SAILPOINT where gal.GALVWS07_USRO_SAILPOINT.CD_USUARIO = '"
				+ codUsuario + "'";

		if (isDebug == true) {
			log.debug("Query Consulta Usuario Criado Query: <" + query + ">");
		}

		Statement stmt = m_dbConn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		if (rs != null) {
			while (rs.next()) {
				usuario = rs.getString(CD_USUARIO);
			}
		} else {
			ret = false;
		}

		stmt.close();

		if (!usuario.isEmpty()) {
			ret = true;
		}
		if (isDebug == true) {
			log.debug("Consulta Usuario Criado Retorno: <" + ret + ">");
		}

		return ret;
	}

	public boolean consultaUsuarioGrupo(String codUsuario, String sistema,
			String perfil) throws Exception {
		boolean ret = false;
		String usuario = "";

		init();
		String query = "SELECT * FROM gal.GALVWS08_USRO_SSTMA_SAILPOINT Where CD_USUARIO= '"
				+ codUsuario
				+ "' and CD_SISTEMA='"
				+ sistema
				+ "' and CD_PERFIL='" + perfil + "'";

		if (isDebug == true) {
			log.debug("Consulta Usuario Grupo Query: " + query);
		}

		Statement stmt = m_dbConn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		if (rs != null) {
			while (rs.next()) {
				usuario = rs.getString(CD_USUARIO);
			}
		} else {
			ret = false;
		}

		stmt.close();

		if (!usuario.isEmpty()) {
			ret = true;
		}

		if (isDebug == true) {
			log.debug("Consulta Usuario Grupo Return: " + ret);
		}

		return ret;
	}

	public String recuperaCodPerfil(String codPerfil, String codSistema)
			throws Exception {

		init();
		String query = "SELECT * FROM gal.GALVWS09_PRFL_SAILPOINT where NO_PERFIL='"
				+ codPerfil + "' AND CD_SISTEMA='" + codSistema + "'";

		if (isDebug == true) {
			log.debug("Recupera numero Perfil Query <" + query + ">");
		}

		String numPerfil = "";
		Statement stmt = m_dbConn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		while (rs.next()) {
			numPerfil = rs.getString(CD_PERFIL);
		}

		stmt.close();

		if (isDebug == true) {
			log.debug("Recupera numero Perfil numPerfil <" + numPerfil + ">");
		}

		return numPerfil;
	}

	public void trataDadoCreate(String codUsuario, String nomeUsuario,
			String codigoUnidade, String codigoSisDefaul,
			String codigoSistUsua, String codigoSuregLot, String idDeret,
			String status, String cef, String funcao, String matricula)
			throws Exception {

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
		codigoSuregLot = String
				.format("%04d", Integer.parseInt(codigoSuregLot));
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

		if (isDebug == true) {
			log.debug("Trata Dados Create dadosCreate: <"
					+ dadosCreate.toString() + ">");
		}

		criaUsuario(dadosCreate.toString());

	}

	public void trataDadoConexao(String sistema, String codUsuario,
			String perfil, String nivelSeg, String codigoSuregLot,
			String codigoUnidade, String idDeret, String status,
			String consultaNaci, String atualizacaoNac) throws Exception {

		StringBuilder dadosConexao = new StringBuilder();

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
		codigoSuregLot = String
				.format("%04d", Integer.parseInt(codigoSuregLot));
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

		if (isDebug == true) {
			log.debug("Trata Dados Conexao <dadosConexao: "
					+ dadosConexao.toString() + ">");
		}

		criaConexao(dadosConexao.toString());

	}

	public void trataDadoDesvincularConexao(String sistema, String codUsuario)
			throws Exception {

		StringBuilder dadosDesvincular = new StringBuilder();

		// CD-SISTEMA x(08)
		sistema = String.format("%-8s", sistema);
		dadosDesvincular.append(sistema);

		// CD-USUARIO x(08)
		codUsuario = String.format("%-8s", codUsuario);
		dadosDesvincular.append(codUsuario);

		if (isDebug == true) {
			log.debug("Trada dados Desvincular Conexao : <dadosDesvincular: "
					+ dadosDesvincular.toString() + ">");
		}

		desvincularConexao(dadosDesvincular.toString());

	}

	public void trataDadosAtualizacao(String codUsuario, String nomeUsuario,
			String codUnidade, String cdSistemDefaul, String codSisUsu,
			String codSuregLota, String idDiretoria, String idStat, String idCf)
			throws Exception {

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

		if (isDebug == true) {
			log.debug("TrataDadosAtualizacao <dadoAtualizacao: "
					+ dadoAtualizacao.toString() + ">");
		}

		atulizacaoUsuario(dadoAtualizacao.toString());

	}

	public void trataExclusaoUsuario(String cdUsuario) throws Exception {

		StringBuilder excluiUsuario = new StringBuilder();

		// CD-USUARIO X(08)
		cdUsuario = String.format("%-8s", cdUsuario);
		excluiUsuario.append(cdUsuario);

		if (isDebug == true) {
			log.debug("Trata Exclusao Usuario ExcluiUsuario: <"
					+ excluiUsuario.toString() + ">");
		}

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

			if (isDebug == true) {
				log.debug("Exclui Usuario Retorno: "
						+ mensage.getDadosRetorno());
			}

		} catch (LoginException e) {
			if (isDebug == true) {
				log.debug("Cód. Erro:" + e.getErroCics() + ", Complemento: "
						+ e.getComplementoErroCics() + ", Cód. Sql: "
						+ e.getCodSql() + ", Resp: " + e.getResp()
						+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
			}
		} catch (LeMensagemException e) {
			e.printStackTrace();
			if (isDebug == true) {
				log.debug("Cód. Erro:" + e.getErroCics() + ", Complemento: "
						+ e.getComplementoErroCics() + ", Cód. Sql: "
						+ e.getCodSql() + ", Resp: " + e.getResp()
						+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
			}
		} catch (ExecuteException e) {
			e.printStackTrace();
			if (isDebug == true) {
				log.debug("Cód. Erro:" + e.getErroCics() + ", Complemento: "
						+ e.getComplementoErroCics() + ", Cód. Sql: "
						+ e.getCodSql() + ", Resp: " + e.getResp()
						+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
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

			if (isDebug == true) {
				log.debug("AtulizacaoUsuario Retorno: "
						+ mensage.getDadosRetorno());
			}

		} catch (LoginException e) {
			if (isDebug == true) {
				log.debug("Cód. Erro:" + e.getErroCics() + ", Complemento: "
						+ e.getComplementoErroCics() + ", Cód. Sql: "
						+ e.getCodSql() + ", Resp: " + e.getResp()
						+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
			}
		} catch (LeMensagemException e) {
			e.printStackTrace();
			if (isDebug == true) {
				log.debug("Cód. Erro:" + e.getErroCics() + ", Complemento: "
						+ e.getComplementoErroCics() + ", Cód. Sql: "
						+ e.getCodSql() + ", Resp: " + e.getResp()
						+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
			}
		} catch (ExecuteException e) {
			e.printStackTrace();
			if (isDebug == true) {
				log.debug("Cód. Erro:" + e.getErroCics() + ", Complemento: "
						+ e.getComplementoErroCics() + ", Cód. Sql: "
						+ e.getCodSql() + ", Resp: " + e.getResp()
						+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
			}
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

			if (isDebug == true) {
				log.debug("Desvincular Conexao Retorno: "
						+ mensage.getDadosRetorno());
			}

		} catch (LoginException e) {
			if (isDebug == true) {
				log.debug("Cód. Erro:" + e.getErroCics() + ", Complemento: "
						+ e.getComplementoErroCics() + ", Cód. Sql: "
						+ e.getCodSql() + ", Resp: " + e.getResp()
						+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
			}
		} catch (LeMensagemException e) {
			e.printStackTrace();
			if (isDebug == true) {
				log.debug("Cód. Erro:" + e.getErroCics() + ", Complemento: "
						+ e.getComplementoErroCics() + ", Cód. Sql: "
						+ e.getCodSql() + ", Resp: " + e.getResp()
						+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
			}
		} catch (ExecuteException e) {
			e.printStackTrace();
			if (isDebug == true) {
				log.debug("Cód. Erro:" + e.getErroCics() + ", Complemento: "
						+ e.getComplementoErroCics() + ", Cód. Sql: "
						+ e.getCodSql() + ", Resp: " + e.getResp()
						+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
			}
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

			if (isDebug == true) {
				log.debug("Criar Usuario Retorno: " + mensage.getDadosRetorno());
			}

		} catch (LoginException e) {
			if (isDebug == true) {
				log.debug("Cód. Erro:" + e.getErroCics() + ", Complemento: "
						+ e.getComplementoErroCics() + ", Cód. Sql: "
						+ e.getCodSql() + ", Resp: " + e.getResp()
						+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
			}
		} catch (LeMensagemException e) {
			e.printStackTrace();
			if (isDebug == true) {
				log.debug("Cód. Erro:" + e.getErroCics() + ", Complemento: "
						+ e.getComplementoErroCics() + ", Cód. Sql: "
						+ e.getCodSql() + ", Resp: " + e.getResp()
						+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
			}
		} catch (ExecuteException e) {
			e.printStackTrace();
			if (isDebug == true) {
				log.debug("Cód. Erro:" + e.getErroCics() + ", Complemento: "
						+ e.getComplementoErroCics() + ", Cód. Sql: "
						+ e.getCodSql() + ", Resp: " + e.getResp()
						+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
			}
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
			mensage.setDadosEnvio(dadosConexao);

			mensage = jcisConnect.execute(mensage);

			if (isDebug == true) {
				log.debug("Criar Conexao Retorno: " + mensage.getDadosRetorno());
			}

		} catch (LoginException e) {
			if (isDebug == true) {
				log.debug("Cód. Erro:" + e.getErroCics() + ", Complemento: "
						+ e.getComplementoErroCics() + ", Cód. Sql: "
						+ e.getCodSql() + ", Resp: " + e.getResp()
						+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
			}
		} catch (LeMensagemException e) {
			e.printStackTrace();
			if (isDebug == true) {
				log.debug("Cód. Erro:" + e.getErroCics() + ", Complemento: "
						+ e.getComplementoErroCics() + ", Cód. Sql: "
						+ e.getCodSql() + ", Resp: " + e.getResp()
						+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
			}
		} catch (ExecuteException e) {
			e.printStackTrace();
			if (isDebug == true) {
				log.debug("Cód. Erro:" + e.getErroCics() + ", Complemento: "
						+ e.getComplementoErroCics() + ", Cód. Sql: "
						+ e.getCodSql() + ", Resp: " + e.getResp()
						+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
			}
		}
	}
}
