import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
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
import openconnector.Schema;
import to.AccountTO;
import to.GroupTO;
import br.com.dba.ease.EaseWSClientFacade;
import br.com.dba.ease.xml.EaseWSRetorno;
import br.gov.caixa.seguranca.LoginException;
import br.gov.caixa.util.jcicsconnect.ExecuteException;
import br.gov.caixa.util.jcicsconnect.JCicsConnect;
import br.gov.caixa.util.jcicsconnect.LeMensagemException;
import br.gov.caixa.util.jcicsconnect.MensagemCics;

/*

 This file  provides a skeletal structure of the openconnector interfaces.
 It is the responsiblity of the connector developer to implement the methods.

 */
public class SIORM extends AbstractConnector {

	// ////////////////////////////////////////////
	// ////////////////////////////
	//
	// INNER CLASS
	//
	// //////////////////////////////////////////////////////////////////////////

	private String m_sLastUser;
	public static final String EMPTY_STR = " ";
	private static final String RETORNOSUCESSO = "OK";
	private Iterator<Map<String, Object>> it;
	private Map<String, Map<String, Object>> m_accountsMap = new HashMap<String, Map<String, Object>>();

	/**
	 * matricula <br>
	 * co_usuario
	 */
	private final String USUARIO = "co_usuario";
	/** tp_operacao */
	private final String OPERACAO = "tp_operacao";
	/** ic_perfil_usuario */
	private final String PERFILUSUARIO = "ic_perfil_usuario";
	/**
	 * unidade de locatao <br>
	 * unid_lotacao_usuario
	 */
	private final String UNIDADELOTACAOUSUARIO = "unid_lotacao_usuario";
	/**
	 * funcao do usuario <br>
	 * cargo_usuario
	 */
	private final String FUNCAOUSUARIO = "cargo_usuario";
	/** telefone_usuario */
	private final String TELEFONEUSUARIO = "telefone_usuario";
	/** no_usuario */
	private final String NOMEUSUARIO = "no_usuario";
	/** tipo_operacao_grupo */
	private final String TIPOOPERACAOGRUPO = "tipo_operacao_grupo";
	/** ic_perfil_grupo */
	private final String PERFILGRUPO = "ic_perfil_grupo";

	private final int TAMANHOMENSAGEM = 82;

	/*
	 * templates de informacoes para o CICS
	 */
	// private static String templateORMPO610 = "%-40s%8sE%-5s%-83s";
	private static String templateORMPO610 = "%-40s%8sR%015d%-83s";

	private static String templateAtivacao = "%1s%-8s%-12s%-4s%4s%-15s%-40s";
	/** %1s%-8s%-12s */
	private static String templateVinculacao = "%1s%-8s%-12s";
	private static String templateAlteracao = "%1s%-8s%-12s%-4s%4s%-15s%-40s";
	private static String templateExclusao = "%1s%-8s";
	private static String templateBloqueio = "%1s%-8s";
	private static String templateRestauracao = "%1s%-8s";
	// private static String templateDesvinculacao =
	// "%1s%-7s%-12s%-4s%4s%-15s%-40s";
	private static String templateSolicitacaoInformacoes = "%1s%-8s";
	private static String templateDadosGrupo = "%1s%-8s";
	private static String templateDadosUsuariosTodos = "%-1s";
	private static String templateDadosGruposTodos = "%-1s";
	private static String templateTelefone = "%015d";

	/** 0 */
	private static String codAtivacao = "0";
	/** 1 */
	private static String codVinculacao = "1";
	/** 3 */
	private static String codAlteracao = "3";
	/** 4 */
	private static String codExclusao = "4";
	/** 5 */
	private static String codBloqueio = "5";
	/** 6 */
	private static String codRestauracao = "6";
	/** 7 */
	private static String codDesvinculacao = "7";
	/** 8 */
	private static String codSolicitacaoInformacoes = "8";
	/** 9 */
	private static String codDadosUsuarios = "9";
	/** 2 */
	private static String codDadosGrupos = "2";
	/** 9* */
	private static String codDadosUsuariosTodos = "9*";
	/** 2* */
	private static String codDadosGrupoTodos = "2*";

	private static String programaCicsORMPOM12 = "ORMPOM12";
	private static String programaCicsORMPO610 = "ORMPO610";

	private String mensagemPOM612;

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
	public SIORM() {
		super();
		m_boleanteste = false;
	}

	/**
	 * Constructor for an OpenConnectorSample.
	 * 
	 * @param config
	 *            The ConnectorConfig to use.
	 * @param log
	 *            The Log to use.
	 */
	public SIORM(ConnectorConfig config, openconnector.Log log)
			throws Exception {
		super(config, log);
		m_boleanteste = false;
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
		init();
		// add code to test the connection to the resource.
	}

	/**
	 * Discover the Schema for the currently configured object type.
	 * 
	 * @throws ConnectorException
	 *             If the operation fails.
	 * @throws UnsupportedOperationException
	 *             If the DISCOVER_SCHEMA feature is not supported.
	 */
	public Schema discoverSchema() throws ConnectorException,
			UnsupportedOperationException {
		Schema schema = new Schema();

		if (OBJECT_TYPE_ACCOUNT.equals(this.objectType)) {

		} else {

		}

		return schema;
	}

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
	
	private Map<String, Object> account;
	private Map<String, Object> groups;
	
	
	public Map<String, Object> read(String nativeIdentifier)
			throws ConnectorException, IllegalArgumentException,
			UnsupportedOperationException {

		init();
//		Map<String, Object> readMap = new HashMap<String, Object>();
//
//		mensagemPOM612 = String.format(templateSolicitacaoInformacoes,
//				codSolicitacaoInformacoes, nativeIdentifier).toUpperCase();
//
//		String retorno = enviarMsgToCICS(mensagemPOM612);
//
//		int totalRegistros = Integer.valueOf(retorno.subSequence(3, 5)
//				.toString());
//		retorno = retorno.substring(5);
//		int caractereInicial = 0;
//		int caracteresRegistro = 108;
//
//		List<String> contasSiorm = new ArrayList<String>();
//		Map<String, Object> account = new HashMap<String, Object>();
//
//		for (int i = 1; i <= totalRegistros; i++) {
//			int indexfinal = (caracteresRegistro + caractereInicial) * i;
//			int indexinicial = indexfinal - caracteresRegistro;
//			String r = retorno.subSequence(indexinicial, indexfinal).toString()
//					.trim();
//			// System.out.println(r );
//			// popular lista com o resultado
//			contasSiorm.add(r.substring(0, 8).trim()); // matricula
//			contasSiorm.add(r.substring(8, 20).trim()); // perfil
//			contasSiorm.add(r.substring(20, 28).trim()); // lotacao
//			contasSiorm.add(r.substring(28, 52).trim()); // funcao
//			contasSiorm.add(r.substring(52, 67).trim()); // telefone
//			contasSiorm.add(r.substring(67, 107).trim()); // nome
//			contasSiorm.add(r.substring(107, 108).trim()); // ativo inativo
//
//			System.out.println(r.substring(0, 8).trim() + "."
//					+ r.substring(8, 20).trim() + "."
//					+ r.substring(20, 28).trim() + "."
//					+ r.substring(28, 52).trim() + "."
//					+ r.substring(52, 67).trim() + "."
//					+ r.substring(67, 107).trim() + "."
//					+ r.substring(107, 108).trim());
//
//			account.put(USUARIO, r.substring(0, 8).trim());
//			account.put(PERFILGRUPO, r.substring(8, 20).trim());
//			account.put(UNIDADELOTACAOUSUARIO, r.substring(20, 28).trim());
//			account.put(FUNCAOUSUARIO, r.substring(28, 52).trim());
//			account.put(TELEFONEUSUARIO, r.substring(52, 67).trim());
//			account.put(NOMEUSUARIO, r.substring(67, 107).trim());
//
//			String temp = contasSiorm.get(6);
//			if (temp.equals("A")) {
//				account.put(openconnector.Connector.ATT_DISABLED, new Boolean(
//						false));
//			} else {
//				account.put(openconnector.Connector.ATT_DISABLED, new Boolean(
//						true));
//			}
//
//			log.info(account);
//		}
//
//		System.out.println(retorno);
//		log.info(retorno);
		
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
		log.info("[iterate] preparando aggregate ");
		// // Return the iterator on a copy of the list to avoid concurrent mod
		// // exceptions if entries are added/removed while iterating.
		//
		// it = null;
		// log.info("[iterate] objectType = " + this.objectType);
		// if (OBJECT_TYPE_ACCOUNT.equals(this.objectType)) {
		// log.info("[iterate] buscando iterate account");
		// // it = new ArrayList<Map<String, Object>>(getObjectsMap()
		// // .values()).iterator();
		// it = new ArrayList<Map<String, Object>>(aggregate(this.objectType)
		// .values()).iterator();
		// log.info("[iterate] iterate recuperado account");
		//
		// } else if (OBJECT_TYPE_GROUP.equals(this.objectType)) {
		//
		// log.info("[iterate] buscando iterate group");
		// // it = new ArrayList<Map<String, Object>>(getObjectsMap()
		// // .values()).iterator();
		// it = new ArrayList<Map<String, Object>>(aggregate(this.objectType)
		// .values()).iterator();
		// log.info("[iterate] iterate recuperado group");
		//
		// }
		// // if (OBJECT_TYPE_GROUP.equals(this.objectType)) {
		// // it = getGroups(filter);
		// // }
		// // return new PagingIterator(new FilteredIterator(it, filter));

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
	public Result create(String nativeIdentifier, List<Item> items)
			throws ConnectorException, ObjectAlreadyExistsException {
		/*
		 * nativeIdentifier = matricula
		 */

		if (isDebug == true) {
			log.debug("CREATE <codUsuario: " + nativeIdentifier + ">");
		}
		log.info("CREATE <codUsuario: " + nativeIdentifier + ">");

		String perfil = "", locatao = "", funcao = "", telefone = "", nome = "";

		for (openconnector.Item item : items) {

			// traz o valor do atributo , exemplo = co_usuario
			String name = item.getName();
			// traz o valor do atributo
			String value = item.getValue().toString();

			log.info("nome: " + name + " # valor: " + value + ">");

			if (name.equalsIgnoreCase(PERFILGRUPO)) {
				perfil = value;
				log.info("IC_PERFIL_GRUPO = " + perfil);
			}
			if (name.equalsIgnoreCase(UNIDADELOTACAOUSUARIO)) {
				locatao = value;
			}
			if (name.equalsIgnoreCase(FUNCAOUSUARIO)) {
				funcao = value;
			}
			if (name.equalsIgnoreCase(TELEFONEUSUARIO)) {
				telefone = String.format(templateTelefone,
						Long.parseLong(value));
			}
			if (name.equalsIgnoreCase(NOMEUSUARIO)) {
				nome = value;
			}

		}

		// montar a tripa de informacao aqui para o CICS
		String mensagemORMPOM12 = String.format(templateAtivacao, codAtivacao,
				nativeIdentifier, perfil, locatao, funcao, telefone, nome);

		Result result = checarResultado(enviarMsgToCICS(mensagemORMPOM12));

		return result;

		// Result result = new Result(Result.Status.Committed);
		// Add the code here for handling the create operation
		// return result;
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

		// nativeIdentifier = matrcula

		Map<String, Object> existing = read(nativeIdentifier);
		if (null == existing) {
			throw new ObjectNotFoundException(nativeIdentifier);
		}

		if (items != null) {
			for (Item item : items) {
				String name = item.getName();
				Object value = item.getValue();
				Item.Operation op = item.getOperation();

				switch (op) {
				case Add: {
					log.info("Update <codUsuario: " + nativeIdentifier + ">");

					String perfil = "";
					log.info("nome: " + name + " # valor: " + value + ">");

					if (name.equalsIgnoreCase(PERFILGRUPO)) {
						perfil = value.toString();
					}

					// montar a tripa de informacao aqui para o CICS
					String mensagemORMPOM12 = String.format(templateVinculacao,
							codVinculacao, nativeIdentifier, perfil);
					result = checarResultado(enviarMsgToCICS(mensagemORMPOM12));
				}
					break;
				case Remove: {

					mensagemPOM612 = String.format(
							templateSolicitacaoInformacoes,
							codSolicitacaoInformacoes, nativeIdentifier)
							.toUpperCase();
					String retorno = enviarMsgToCICS(mensagemPOM612);

					log.info("CREATE <codUsuario: " + nativeIdentifier + ">");
					String perfil = "", locatao = "", funcao = "", telefone = "", nome = "";

					log.info("nome: " + name + " # valor: " + value + ">");

					perfil = " ";

					if (retorno.substring(20, 28).trim().length() != 0) {
						locatao = retorno.substring(20, 28).trim();
					} else {
						locatao = "0000";
					}

					if (retorno.substring(28, 52).trim().length() != 0) {
						funcao = retorno.substring(28, 52).trim();
					} else {
						funcao = "0000";
					}

					if (retorno.substring(52, 67).trim().length() != 0) {
						telefone = retorno.substring(52, 67).trim();
					} else {
						telefone = "000000000000000";
					}

					nome = retorno.substring(67, 107).trim();

					log.info("UPDATE REMOVE:  lotacao = " + locatao
							+ " funcai = " + funcao + " telefone =  "
							+ telefone + " nome = " + nome);

					// montar a tripa de informacao aqui para o CICS
					String mensagemORMPOM12 = String.format(templateAlteracao,
							codAlteracao, nativeIdentifier, perfil, locatao,
							funcao, telefone, nome);

					result = checarResultado(enviarMsgToCICS(mensagemORMPOM12));
				}
					break;
				case Set: {
					existing.put(name, value);

					mensagemPOM612 = String.format(
							templateSolicitacaoInformacoes,
							codSolicitacaoInformacoes, nativeIdentifier)
							.toUpperCase();
					String retorno = enviarMsgToCICS(mensagemPOM612);

					log.info("SET <codUsuario: " + nativeIdentifier + ">");

					String perfil = "", locatao = "", funcao = "", telefone = "", nome = "";
					log.info("nome: " + name + " # valor: " + value + ">");

					if (name.equalsIgnoreCase("nu_funcao")) {
						funcao = value.toString();
					} else {
						funcao = retorno.substring(28, 52).trim();
					}

					if (name.equalsIgnoreCase("nu_unde_ltco_fisica")) {
						locatao = value.toString();
					} else {
						locatao = retorno.substring(20, 28).trim();

					}
					telefone = retorno.substring(52, 67).trim();
					nome = retorno.substring(67, 107).trim();
					// montar a tripa de informacao aqui para o CICS
					String mensagemORMPOM12 = String.format(templateAlteracao,
							codAlteracao, nativeIdentifier, perfil, locatao,
							funcao, telefone, nome);

					result = checarResultado(enviarMsgToCICS(mensagemORMPOM12));

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

		/*
		 * deletar conta
		 */

		String mensagem = String.format(templateExclusao, codExclusao,
				nativeIdentifier);
		String resultado = enviarMsgToCICS(mensagem);

		result = checarResultado(resultado);
		Map<String, Object> obj = read(nativeIdentifier);
		if (null == obj) {
			throw new ObjectNotFoundException(nativeIdentifier);
		}

		return result;
	}

	private Result checarResultado(String resultado) {
		Result result = new Result();
		log.info("CHEANDO RESULTADO STRING = " + resultado);
		if (resultado.trim().substring(0, 2).equals(RETORNOSUCESSO)) {
			result = new Result(Result.Status.Committed);
			log.info("CHEANDO RESULTADO = COMITADO");
		} else {
			result = new Result(Result.Status.Failed);
			log.info("CHEANDO RESULTADO = FALHA");
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

		/*
		 * desbloquear conta
		 */

		String mensagem = String.format(templateRestauracao, codRestauracao,
				nativeIdentifier);

		Result result = checarResultado(enviarMsgToCICS(mensagem));

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

		/*
		 * bloquear conta
		 */

		String mensagemORMPOM12 = String.format(templateBloqueio, codBloqueio,
				nativeIdentifier);
		Result result = checarResultado(enviarMsgToCICS(mensagemORMPOM12));

		/*
		 * se o CICS retornar falha Result result = new
		 * Result(Result.Status.Failed); result.add("mensagem falha qualquer");
		 */

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

	/*
	 * ABAIXO E IMPLEMENTACAO DA CAIXA ECONOMICA FEDERAL
	 */

	// Variavel Debug
	private boolean isDebug;

	private boolean m_boleanteste;
	private String nomeServidorCics;
	private String numPortaCics;
	private String userNameCics;
	private String passwordCics;
	private String programaCics;
	private String transacaoCics;

	// variaveis CICS
	private JCicsConnect jCicsConnect = new JCicsConnect();

	// public MensagemCics mensagemCICS;

	void init() {
		if (!m_boleanteste) {
			// os valores sao recuperados do xhtml , substituir pelos valores do
			// CICS
			nomeServidorCics = config.getString("nomeServidorCics");
			numPortaCics = config.getString("numPortaCics");
			userNameCics = config.getString("userNameCics");
			passwordCics = config.getString("passwordCics");
			programaCics = config.getString("programaCics");
			transacaoCics = config.getString("transacaoCics");

			log.info("DADOS CICS - " + nomeServidorCics + " " + numPortaCics
					+ " " + userNameCics + " " + passwordCics + " "
					+ programaCics + " " + transacaoCics);

			MensagemCics msg = new MensagemCics();
			msg.setNomeServidor(nomeServidorCics);
			msg.setNumPorta(Integer.parseInt(numPortaCics));
			msg.setUserName(userNameCics);
			msg.setPassword(passwordCics);
			msg.setProgramName(programaCics);
			msg.setTransId(transacaoCics.toUpperCase());
			// msg.setPrefix("OQ");

			log.info("nome maquina = " + msg.getNomeServidor()
					+ " __ logado = " + msg.getEstaLogado());

			try {
				msg = jCicsConnect.login(msg);
				log.info("[jcics] logado " + msg.getEstaLogado() + "\n "
						+ "============== erro cics: " + msg.getErroCics()
						+ "\n" + "================ dados retorno: "
						+ msg.getDadosRetorno());
				// jCicsConnect.fechaConexao(null);

			} catch (LoginException e) {
				log.error("erro de conexao com CICS# servidor = "
						+ nomeServidorCics + " # porta = " + numPortaCics
						+ " # usuario = " + userNameCics + " # transacao = "
						+ transacaoCics);
				e.printStackTrace();
			} catch (LeMensagemException e) {
				log.error("erro de conexao com CICS# servidor = "
						+ nomeServidorCics + " # porta = " + numPortaCics
						+ " # usuario = " + userNameCics + " # transacao = "
						+ transacaoCics);
				e.printStackTrace();
			}
		}
	}

	private String enviarMsgToCICS(String mensagemORMPOM12) {
		init();

		MensagemCics msg = new MensagemCics();
		msg.setNomeServidor(nomeServidorCics);
		msg.setNumPorta(Integer.parseInt(numPortaCics));
		msg.setUserName(userNameCics);
		msg.setPassword(passwordCics);
		msg.setProgramName(programaCics);
		msg.setTransId(transacaoCics.toUpperCase());
		// msg.setPrefix("OQ");

		InetAddress inet;
		String mensagemFinal = null;

		try {
			inet = NetworkInterface.getNetworkInterfaces().nextElement()
					.getInetAddresses().nextElement().getLocalHost();
			mensagemFinal = String.format(templateORMPO610,
					inet.getHostAddress(), programaCicsORMPOM12, 0l,
					mensagemORMPOM12).toUpperCase();

		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		log.info("== [ ENVIANDO PARA CICS ] ==\n[" + mensagemFinal
				+ "]\n== [ FIM ENVIO PARA CICS ] ==");
		msg.setDadosEnvio(mensagemFinal);

		try {
			msg = jCicsConnect.login(msg);
			msg = jCicsConnect.execute(msg);
			log.info("===== [JCICS] =====\n" + "mensagem para ORMPOM12: "
					+ mensagemORMPOM12 + "\n" + "mensagem para ORMPO610: "
					+ mensagemFinal + "\n" + "retorno : "
					+ msg.getDadosRetorno());

		} catch (LoginException e) {
			e.printStackTrace();
		} catch (LeMensagemException e) {
			e.printStackTrace();
		} catch (ExecuteException e) {
			e.printStackTrace();
		}

		return msg.getDadosRetorno();
	}

	public void debub() {
		boolean debug = config.getBoolean("debug");

		if (debug == true) {

			isDebug = true;
		} else {

			isDebug = false;
		}
	}

	/**
	 * metodo AGREGATE tanto pra conta quanto para grupo
	 * 
	 * @return
	 * @throws Exception
	 */
	private List<Map<String, Object>> resultObjectList;

//	private Map<String, Map<String, Object>> getObjectsMap() {
//		resultObjectList = null;
//		resultObjectList = new ArrayList<Map<String, Object>>();
//
//		getAccountAggregate();
//
//		return m_accountsMap;
//	}
//
//	private Map<String, Map<String, Object>> aggregate(String objectType) {
//		resultObjectList = null;
//		resultObjectList = new ArrayList<Map<String, Object>>();
//
//		if (OBJECT_TYPE_ACCOUNT.equals(objectType)) {
//			getAccountAggregate();
//		} else if (OBJECT_TYPE_GROUP.equals(objectType)) {
//			getGroupAggregate();
//		}
//
//		return m_accountsMap;
//	}

	/**
	 * Retorna uma lista dos grupos no SIORM<br>
	 * Usado no momento do aggregate
	 */
	private void getGroupAggregate() {

		mensagemPOM612 = String.format(templateDadosGruposTodos,
				codDadosGrupoTodos).toUpperCase();
		String retorno = enviarMsgToCICS(mensagemPOM612);
		int totalRegistros = Integer.valueOf(retorno.subSequence(3, 5)
				.toString());

		// List<String> contasSiorm = new ArrayList<String>();
		// Map<String, Object> grupos = new HashMap<String, Object>();

		for (int i = 0; i < totalRegistros; i++) {
			Map<String, Object> grupos = new HashMap<String, Object>();
			String valor = retorno.substring(5 + i * 12, 5 + (i + 1) * 12)
					.trim();
			// contasSiorm.add( valor );
			grupos.put(PERFILGRUPO, valor);
			m_accountsMap.put(String.valueOf(i), grupos);
			System.out.println(grupos);
			grupos = null;
			// System.out.println(retorno.substring(5+i*12, 5+(i+1)*12
			// ).trim());
			// System.out.print( retorno.charAt(i) );
		}

	}

	/**
	 * Retorna uma lista dos usuario no SIORM e seus grupos<br>
	 * Usado no momento do aggregate
	 */
//	private void getAccountAggregate() {
//
//		mensagemPOM612 = String.format(templateDadosUsuariosTodos,
//				codDadosUsuariosTodos).toUpperCase();
//
//		String retorno = enviarMsgToCICS(mensagemPOM612);
//
//		int totalRegistros = Integer.valueOf(retorno.subSequence(3, 5)
//				.toString());
//		retorno = retorno.substring(5);
//		int caractereInicial = 0;
//		int caracteresRegistro = 108;
//
//		List<String> contasSiorm = new ArrayList<String>();
//		Map<String, Object> account = new HashMap<String, Object>();
//
//		// consulta CICS 9* ; desmembrar o resultado
//		for (int i = 1; i <= totalRegistros; i++) {
//			int indexfinal = (caracteresRegistro + caractereInicial) * i;
//			int indexinicial = indexfinal - caracteresRegistro;
//			String r = retorno.subSequence(indexinicial, indexfinal).toString()
//					.trim();
//			// System.out.println(r );
//			// popular lista com o resultado
//			contasSiorm.add(r.substring(0, 8).trim()); // matricula
//			contasSiorm.add(r.substring(8, 20).trim()); // perfil
//			contasSiorm.add(r.substring(20, 28).trim()); // lotacao
//			contasSiorm.add(r.substring(28, 52).trim()); // funcao
//			contasSiorm.add(r.substring(52, 67).trim()); // telefone
//			contasSiorm.add(r.substring(67, 107).trim()); // nome
//			contasSiorm.add(r.substring(107, 108).trim()); // ativo inativo
//
//			System.out.println(r.substring(0, 8).trim() + "."
//					+ r.substring(8, 20).trim() + "."
//					+ r.substring(20, 28).trim() + "."
//					+ r.substring(28, 52).trim() + "."
//					+ r.substring(52, 67).trim() + "."
//					+ r.substring(67, 107).trim() + "."
//					+ r.substring(107, 108).trim());
//
//			account.put(USUARIO, r.substring(0, 8).trim());
//			account.put(PERFILGRUPO, r.substring(8, 20).trim());
//			account.put(UNIDADELOTACAOUSUARIO, r.substring(20, 28).trim());
//			account.put(FUNCAOUSUARIO, r.substring(28, 52).trim());
//			account.put(TELEFONEUSUARIO, r.substring(52, 67).trim());
//			account.put(NOMEUSUARIO, r.substring(67, 107).trim());
//
//			String temp = contasSiorm.get(6);
//			if (temp.equals("A")) {
//				account.put(openconnector.Connector.ATT_DISABLED, new Boolean(
//						false));
//			} else {
//				account.put(openconnector.Connector.ATT_DISABLED, new Boolean(
//						true));
//			}
//
//			log.info(account);
//
//			m_accountsMap.put(String.valueOf(account.get(USUARIO)), account);
//		}
//		log.info("quantidade maxima iterator " + totalRegistros
//				+ " tamanho do MAP " + m_accountsMap.size());
//	}
//
//	public Iterator<Map<String, Object>> IterateNextPage(Filter filter) {
//		String funcName = "IterateNextPage";
//		try {
//			log.debug("IterateNextPage");
//
//			if (OBJECT_TYPE_ACCOUNT.equals(this.objectType)) {
//				log.debug("Dentro do if");
//
//				if (m_sLastUser.equals(EMPTY_STR)) {
//					log.debug("IterateNextPage IF");
//					if (log.isDebugEnabled()) {
//						log.debug("end of user iteration...");
//					}
//				} else {
//					log.debug("IterateNextPage ELSE ");
//					it = (getObjectsMap().values()).iterator();
//				}
//
//			}
//			// else if (OBJECT_TYPE_GROUP.equals(this.objectType))
//			// {
//			// if (m_sLastGroup.equals(EMPTY_STR)) //do not call getobjectmap if
//			// you donot know the last record. this will start a fresh aggregate
//			// {
//			// if (log.isDebugEnabled())
//			// log.debug("end of group iteration...");
//			// }
//			// else
//			// {
//			//
//			// it = (getObjectsMap().values()).iterator();
//			// }
//			// }
//		} catch (Exception e) {
//			if (log.isDebugEnabled())
//				log.debug("Exception occured ", e);
//		}
//		// exit(funcName);
//		return it;
//	}
//
//	public boolean hasNext() {
//
//		if (it.hasNext() == false) {
//			it = IterateNextPage(null);
//			boolean hasNext = it.hasNext();
//
//			return hasNext;
//		} else {
//			return it.hasNext();
//		}
//	}

	// Alteracao

	public Iterator<Map<String, Object>> getAccounts(String filter)
			throws Exception {

		resultObjectList = null;
		resultObjectList = new ArrayList<Map<String, Object>>();

		if (filter.equalsIgnoreCase("*")) {

			mensagemPOM612 = String.format(templateDadosUsuariosTodos,
					codDadosUsuariosTodos).toUpperCase();

			String retorno = enviarMsgToCICS(mensagemPOM612);

			int totalRegistros = Integer.valueOf(retorno.subSequence(3, 5)
					.toString());
			retorno = retorno.substring(5);
			int caractereInicial = 0;
			int caracteresRegistro = 108;

			List<String> contasSiorm = new ArrayList<String>();
			Map<String, Object> account = new HashMap<String, Object>();

			// consulta CICS 9* ; desmembrar o resultado
			for (int i = 1; i <= totalRegistros; i++) {
				int indexfinal = (caracteresRegistro + caractereInicial) * i;
				int indexinicial = indexfinal - caracteresRegistro;
				String r = retorno.subSequence(indexinicial, indexfinal)
						.toString().trim();
				// System.out.println(r );
				// popular lista com o resultado
				contasSiorm.add(r.substring(0, 8).trim()); // matricula
				contasSiorm.add(r.substring(8, 20).trim()); // perfil
				contasSiorm.add(r.substring(20, 28).trim()); // lotacao
				contasSiorm.add(r.substring(28, 52).trim()); // funcao
				contasSiorm.add(r.substring(52, 67).trim()); // telefone
				contasSiorm.add(r.substring(67, 107).trim()); // nome
				contasSiorm.add(r.substring(107, 108).trim()); // ativo inativo

				System.out.println(r.substring(0, 8).trim() + "."
						+ r.substring(8, 20).trim() + "."
						+ r.substring(20, 28).trim() + "."
						+ r.substring(28, 52).trim() + "."
						+ r.substring(52, 67).trim() + "."
						+ r.substring(67, 107).trim() + "."
						+ r.substring(107, 108).trim());

				account.put(USUARIO, r.substring(0, 8).trim());
				account.put(PERFILGRUPO, r.substring(8, 20).trim());
				account.put(UNIDADELOTACAOUSUARIO, r.substring(20, 28).trim());
				account.put(FUNCAOUSUARIO, r.substring(28, 52).trim());
				account.put(TELEFONEUSUARIO, r.substring(52, 67).trim());
				account.put(NOMEUSUARIO, r.substring(67, 107).trim());

				String temp = contasSiorm.get(6);
				if (temp.equals("A")) {
					account.put(openconnector.Connector.ATT_DISABLED,
							new Boolean(false));
				} else {
					account.put(openconnector.Connector.ATT_DISABLED,
							new Boolean(true));
				}

				log.info(account);

				resultObjectList.add(account);
			}
			log.info("quantidade maxima iterator " + totalRegistros
					+ " tamanho do MAP " + m_accountsMap.size());

		} else {

			mensagemPOM612 = String.format(templateSolicitacaoInformacoes,
					codSolicitacaoInformacoes, filter).toUpperCase();

			String retorno = enviarMsgToCICS(mensagemPOM612);

			int totalRegistros = Integer.valueOf(retorno.subSequence(3, 5)
					.toString());
			retorno = retorno.substring(5);
			int caractereInicial = 0;
			int caracteresRegistro = 108;

			List<String> contasSiorm = new ArrayList<String>();
			Map<String, Object> account = new HashMap<String, Object>();

			// consulta CICS 9* ; desmembrar o resultado
			for (int i = 1; i <= totalRegistros; i++) {
				int indexfinal = (caracteresRegistro + caractereInicial) * i;
				int indexinicial = indexfinal - caracteresRegistro;
				String r = retorno.subSequence(indexinicial, indexfinal)
						.toString().trim();
				// System.out.println(r );
				// popular lista com o resultado
				contasSiorm.add(r.substring(0, 8).trim()); // matricula
				contasSiorm.add(r.substring(8, 20).trim()); // perfil
				contasSiorm.add(r.substring(20, 28).trim()); // lotacao
				contasSiorm.add(r.substring(28, 52).trim()); // funcao
				contasSiorm.add(r.substring(52, 67).trim()); // telefone
				contasSiorm.add(r.substring(67, 107).trim()); // nome
				contasSiorm.add(r.substring(107, 108).trim()); // ativo inativo

				System.out.println(r.substring(0, 8).trim() + "."
						+ r.substring(8, 20).trim() + "."
						+ r.substring(20, 28).trim() + "."
						+ r.substring(28, 52).trim() + "."
						+ r.substring(52, 67).trim() + "."
						+ r.substring(67, 107).trim() + "."
						+ r.substring(107, 108).trim());

				account.put(USUARIO, r.substring(0, 8).trim());
				account.put(PERFILGRUPO, r.substring(8, 20).trim());
				account.put(UNIDADELOTACAOUSUARIO, r.substring(20, 28).trim());
				account.put(FUNCAOUSUARIO, r.substring(28, 52).trim());
				account.put(TELEFONEUSUARIO, r.substring(52, 67).trim());
				account.put(NOMEUSUARIO, r.substring(67, 107).trim());

				String temp = contasSiorm.get(6);
				if (temp.equals("A")) {
					account.put(openconnector.Connector.ATT_DISABLED,
							new Boolean(false));
				} else {
					account.put(openconnector.Connector.ATT_DISABLED,
							new Boolean(true));
				}

				log.info(account);

				resultObjectList.add(account);
			}
			log.info("quantidade maxima iterator " + totalRegistros
					+ " tamanho do MAP " + m_accountsMap.size());
		}

		return resultObjectList.iterator();

	}

	@SuppressWarnings("unused")
	public Iterator<Map<String, Object>> getGroups(String filter)
			throws Exception {

		mensagemPOM612 = String.format(templateDadosGruposTodos,
				codDadosGrupoTodos).toUpperCase();
		String retorno = enviarMsgToCICS(mensagemPOM612);
		int totalRegistros = Integer.valueOf(retorno.subSequence(3, 5)
				.toString());

		// List<String> contasSiorm = new ArrayList<String>();
		// Map<String, Object> grupos = new HashMap<String, Object>();

		for (int i = 0; i < totalRegistros; i++) {
			Map<String, Object> grupos = new HashMap<String, Object>();
			String valor = retorno.substring(5 + i * 12, 5 + (i + 1) * 12)
					.trim();
			// contasSiorm.add( valor );
			grupos.put(PERFILGRUPO, valor);
			m_accountsMap.put(String.valueOf(i), grupos);
			System.out.println(grupos);
			grupos = null;
			// System.out.println(retorno.substring(5+i*12, 5+(i+1)*12
			// ).trim());
			// System.out.print( retorno.charAt(i) );
		}


		return resultObjectList.iterator();
	}

}
