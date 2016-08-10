package br.com.pbti.ease;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import util.CriptografiaEase;
import util.UtilDate;
import to.AccountTO;
import to.ConnectionTO;
import to.GroupTO;
import br.com.pbti.ease.xml.EaseWSConexao;
import br.com.pbti.ease.xml.EaseWSPerfil;
import br.com.pbti.ease.xml.EaseWSRetorno;
import br.com.pbti.ease.xml.EaseWSUsuario;
import br.com.pbti.ease.xml.EaseXMLNode;
import br.com.pbti.ease.xml.EaseXMLParser;
import br.com.pbti.ease.xml.IConexaoXML;
import br.com.pbti.ease.xml.IPerfilXML;
import br.com.pbti.ease.xml.IRetornoXML;
import br.com.pbti.ease.xml.IUsuarioXML;
import br.com.pbti.sigal.SigalPeticion;

import com.tecnocom.mediosdepago.sat.webservice.SATLocator;
import com.tecnocom.mediosdepago.sat.webservice.xsd.Parametro;

import framework.XSA_Framework;

/**
 * Fachada cliente de acesso ao webservice EASE/SAT
 * 
 * Integração entre SIGAL e EASE
 * 
 * @author Michael Alves Lins <malins@dba.com.br>
 * 
 */
public final class EaseWSClientFacade {

    private static EaseWSClientFacade _instance;

    private SATLocator easeWS;

    /**
     * Login utilizado para autenticar-se a cada chamada aos servicos do EASE
     */
    private String easeWS_Login;

    /**
     * Senha utilizada para autenticar-se a cada chamada aos servicos do EASE
     */
    private String easeWS_Password;
    
    /**
     * Parametros utilizados para autenticar-se a cada chamada aos servicos do EASE
     * 
     * WSDL:  URL do WSDL inicalizado na classe XSA_Connector, metodo SessionInit
     *        XSA_Framework.XSA_ReadParam("EASE_WSDL")
     *        
     * LOGIN: Este parametro eh inicializado na classe XSA_Connector, metodo SessionInit
     *        XSA_OperationHash.get("XSA_ADMIN_ID");//"S909090" 
     * 
     * PASSWORD: Este parametro eh inicializado na classe XSA_Connector, metodo SessionInit
     *           XSA_OperationHash.get("XSA_ADMIN_PASSWORD");//"202530"
     * 
     * @param login
     * @param password
     * @param wsdl
     * @throws Throwable 
     */
    public static synchronized void init(String login, String password, String wsdl ) throws Throwable {
        
    	System.out.println("Teste Conexao Valores <instance "+_instance);
    	
    	if(_instance != null)
    	{ 
    		_instance.finalize();
    		
    		System.out.println("INSTACE NULL<instance "+_instance);
    	}
    	
    	
    	if ( _instance == null ) {
            if ( wsdl != null ) {
                if ( login != null ) {
                    if ( password != null ) {
                        _instance = new EaseWSClientFacade();
                        _instance.easeWS = new SATLocator( wsdl );
                        
                        System.out.println("Instace EASEWS "+ _instance.easeWS);
                        _instance.easeWS_Login = login;
                        System.out.println("Instace Login "+ _instance.easeWS_Login);
                        
                        _instance.easeWS_Password = CriptografiaEase.getInstance().hash( password.toUpperCase() );
                        
                        System.out.println("Instace Password "+ _instance.easeWS_Password);
                        
                        System.out.println("instance "+_instance);
                        
                    } else {
                        throw new Exception( "[81] EaseWSClientFacade.init: Favor informar a Senha para acesso ao webservice do EASE." );
                    }
                } else {
                    throw new Exception( "[84] EaseWSClientFacade.init: Favor informar o Usuario para acesso ao webservice do EASE." );
                }
            } else {
                throw new Exception( "[87] EaseWSClientFacade.init: Favor informar o WSDL para acesso ao webservice do EASE." );
            }
        }   
    }

    /**
     * Singleton
     * 
     * Inicializa o Localizador do webservice SAT
     * 
     * @return
     */
    private EaseWSClientFacade() {
    }
    public static synchronized EaseWSClientFacade getInstance() throws Exception {
        if ( _instance == null ) {
            throw new Exception("[103] EaseWSClientFacade.getInstance: Favor invocar o metodo EaseWSClientFacade.init() antes de utilizar a instancia desse singleton.");
        }
        return _instance;
    }
    
    /**
     * Testa conexao com o webservice, recuperando a propria conta de autenticacao
     * 
     * @param login
     * @param password
     * @param wsdl
     * @return
     * @throws Throwable 
     */
    public static int testConnection(String login, String password, String wsdl ) 
        throws Throwable {
        init(login, password, wsdl );
        EaseWSRetorno wsRetorno = _instance.getUsuario( login );
        return wsRetorno.getControlSA_RC();
    }
    
    /**
     * Retorna o login utilizado para autentica-se a cada chamada aos servicos do Ease
     * @return
     */
    public String getEaseWSLogin() {
        return easeWS_Login;
    }
    
    /**
     * Retorna a senha utilizada para autentica-se a cada chamada aos servicos do Ease
     * @return
     */
    public String getEaseWSPassword() {
        return easeWS_Password;
    }
    
    /**
     * Executa o servico fornecido para recuperacao de dados no EASE/SAT
     * 
     * @param msgEnvio
     * @return
     * @throws Exception
     */
    private EaseWSRetorno executaServicoRemoto( SigalPeticion msgEnvio ) throws Exception {
        // Remove espaços na formatação do XML enviado para tratamento
        String retornoXML = easeWS.getSATHttpSoap11Endpoint().executeXml( msgEnvio );
        
        System.out.println(retornoXML);
        
        if ( retornoXML != null && !retornoXML.equals("") )
            retornoXML = retornoXML.replaceAll( "  ", "" );
        
        return new EaseWSRetorno( retornoXML );
    }

    /**
     * (Usuario) Adciona um usuario
     * 
     * 1. Tenta incluir o usuario na base do EASE
     * 2. Se retornar erro, verifica se foi em razao de usuario existir
     * 3. Existindo, verificar se esta excluido logicamente
     * 3.1 Reativa-lo
     * 
     * @param usuario
     * @return
     * @throws Exception
     */
    public EaseWSRetorno addUsuario( AccountTO account ) throws Exception {
        // 1. Tenta incluir o usuario na base do EASE
        EaseWSRetorno wsRetorno = executaServicoRemoto( new EaseWSUsuario().addUsuario( account ) );
        // 2. Se retornar erro, verifica se foi em razao de usuario existir
        // MPE0248 - USUARIO JA EXISTENTE NA BASE DE DADOS\nFIELD->  USUARIO
        if ( wsRetorno.possuiErro() && wsRetorno.getMensagem().contains("MPE0248") ) {
            // 3. Existindo, verificar se esta excluido logicamente
            EaseWSRetorno wsRetornoReativacao = executaServicoRemoto( new EaseWSUsuario().getUsuario( account.getUsuario() ) );
            AccountTO usuarioExistente = populateAccounts( wsRetornoReativacao.getXml() ).get(0);
            // Usuario excluido tem data de exclusao diferente de 01-01-0001
            if ( !usuarioExistente.getFecbaja().equals( UtilDate.getFecbajaUsuarioNaoExcluido() ) ) {
                // 3.1 Reativa-lo se estiver excluido logicamente
                wsRetorno = reativarUsuario( account, usuarioExistente  );
            } else {
                /**
                 * Emitir mensagem de erro caso nao esteja excluido, em razao da regra abaixo:
                 * 
                 * "If the Account already exists on the Managed System, the 
                 * function should return an error (XSA_RC_ERROR)"
                 * @see @see idmXmodule_DeveloperGuide_5100.pdf, page 105 
                 */
                // wsRetorno ja possui nesse momento o retorno com erro a ser tratado
            }
        }
        return wsRetorno;
    }
    
    /**
     * Reativa um usuario excluido logicamente
     * 
     * @param usuarioLogin
     * @return
     * @throws Exception
     */
    public EaseWSRetorno reativarUsuario( AccountTO usuarioIncluido, AccountTO usuarioExistente ) throws Exception {
        usuarioExistente.setCodperfil( usuarioIncluido.getCodperfil() );
        // Senha repassada a pedido do Gestor GESET07 (Gustavo) em 22/05/2012
        usuarioExistente.setHashPassword( usuarioIncluido.getPassword() );
        return executaServicoRemoto( new EaseWSUsuario().reativarUsuario( usuarioExistente ) );
    }
    
    /**
     * (Usuario) Retorna os dados apenas do usuario informado
     * 
     * @param usuario
     * @return
     * @throws Exception
     */
    public EaseWSRetorno getUsuario( String usuarioLogin ) throws Exception {
        return executaServicoRemoto( new EaseWSUsuario().getUsuario( usuarioLogin ) );
    }

    /**
     * (Usuario) Retorna os dados de todos os usuarios do sistema
     * @return
     * @throws Exception
     */
    public EaseWSRetorno getUsuarios() throws Exception {
        return executaServicoRemoto( new EaseWSUsuario().getUsuarios() );
    }
    
    /**
     * (Usuario) Exclui usuario da base do EASE 
     * @param usuarioLogin
     * @return
     */
    public EaseWSRetorno excluirUsuario( String usuarioLogin ) throws Exception {
        return executaServicoRemoto( new EaseWSUsuario().excluirUsuario( usuarioLogin ) );
    }
    
    /**
     * (Usuario) Altera a senha do usuario
     * 
     * @param usuarioLogin
     * @param senhaNova
     * @return
     * @throws Exception
     */
    public EaseWSRetorno setNewPassword( String usuarioLogin, String senhaNova ) 
            throws Exception {
        return executaServicoRemoto( 
                new EaseWSUsuario().setNewPassword( usuarioLogin, senhaNova ) 
        );
    }

    /**
     * (Usuario) Habilita usuario no sistema, informando sua data de desativacao 
     * maior que a data corrente
     * 
     * @param usuarioLogin
     * @param dataHabilitacao
     * @return
     * @throws Exception
     */
    public EaseWSRetorno habilitarUsuario( String usuarioLogin
            , String dataHabilitacao ) throws Exception {
        return executaServicoRemoto( 
                new EaseWSUsuario().habilitarUsuario( usuarioLogin, dataHabilitacao )
        );
    }

    /**
     * (Usuario) Desabilita usuario no sistema, informando data D-1 em relacao a data 
     * corrente
     * 
     * @param usuarioLogin
     * @param dataHabilitacao
     * @return
     * @throws Exception
     */
    public EaseWSRetorno desabilitarUsuario( String usuarioLogin ) throws Exception {
        return habilitarUsuario( usuarioLogin, UtilDate.getDataDesativacaoDefault() );
    }
    
    /**
     * (Perfil) Retorna os dados apenas o perfil informado
     * 
     * @param codPerfil
     * @return
     * @throws Exception
     */
    public EaseWSRetorno getPerfil( String codPerfil ) throws Exception {
        return executaServicoRemoto( new EaseWSPerfil().getPerfil( codPerfil ) );
    }
    
    /**
     * (Perfil) Retorna os dados de todos os perfis do sistema
     * 
     * @return
     * @throws Exception
     */
    public EaseWSRetorno getPerfis() throws Exception {
        return executaServicoRemoto( new EaseWSPerfil().getPerfis() );
    }
    
    /**
     * (Conexao) Retorna os dados de todos os usuarios de determinado perfil
     * @param codPerfil
     * @return
     * @throws Exception
     */
    public EaseWSRetorno getUsuariosPorPerfil( String codPerfil ) throws Exception {
        return executaServicoRemoto( new EaseWSConexao().getUsuariosPorPerfil( codPerfil ) );
    }
    
    /**
     * (Conexao) Vincula um usuario a um perfil
     * 
     * @param usuario
     * @param codPerfil
     * @return
     * @throws Exception
     */
    public EaseWSRetorno vinculaUsuarioPerfil( String usuarioLogin, String codPerfil ) 
            throws Exception {  
        return executaServicoRemoto( 
                new EaseWSConexao().vinculaUsuarioPerfil( usuarioLogin, codPerfil ) 
        );
    }
    
    /**
     * (Conexao) Desvincula um usuario de seu perfil
     * 
     * No EASE/SAT o usuario pode ter apenas 1 (um) perfil, entao "desassociar"
     * significa associar um usuario a um novo perfil.
     *
     * @param usuario
     * @param codPerfil
     * @return
     * @throws Exception
     */
    public EaseWSRetorno desvinculaUsuarioPerfil( String usuarioLogin )
        throws Exception {
        // O serviço disponível é o mesmo para ambos os casos, porem, perfil em branco
        return vinculaUsuarioPerfil( usuarioLogin, "" );
    }

    /**
     * Valida o retorno da chamada dos serviços no webservice do EASE/SAT conforme
     * codigos de retorno do Framework do XModule (XSA_Framework) 
     * @param parser
     */
    public int validaCodigoRetornoXML( EaseWSRetorno wsRetorno ) throws Exception {
        // Verifica se houve erro no processamento
        if ( !wsRetorno.getXml().trim().equals("") ) {
            if ( wsRetorno.possuiErro() ) {
                XSA_Framework.XSA_WriteMessage("[357] EaseWSClientFacade.validaCodigoRetornoXML: Webservice EASE retornou o seguinte erro["+
                        wsRetorno.getCodigo() +"]: "+ wsRetorno.getMensagem() );
            }
        } else {
            XSA_Framework.XSA_WriteMessage("[361] EaseWSClientFacade.validaCodigoRetornoXML: Webservice EASE retornou string vazia." );
        }
        return wsRetorno.getControlSA_RC();
    }

    /**
     * Valida o retorno da chamada dos serviços no webservice do EASE/SAT conforme
     * codigos de retorno do Framework do XModule (XSA_Framework) 
     * @param parser
     */
    public boolean comparaCodigoRetornoXML( String xmlRetorno, String codigo2Compare ) 
            throws Exception {

        EaseXMLParser parser = new EaseXMLParser( xmlRetorno );
        EaseXMLNode retorno = parser.getNext( IRetornoXML.XML_ELEMENT );

        // Verifica se o codigo de retorno do Ease esta conforme esperado
        return retorno.getString( IRetornoXML.CODIGO ).equals( codigo2Compare );
    }
    
    /**
     * Carrega lista de objetos AccountTO com dados vindos do Webservice Ease
     * @param xmlAccounts
     * @return
     * @throws Exception
     */
    public List<AccountTO> populateAccounts( String xmlAccounts ) 
            throws Exception {
        
        List<AccountTO> listaAccounts = new ArrayList<AccountTO>();
        EaseXMLParser parser = new EaseXMLParser( xmlAccounts );
        
        try {
            EaseXMLNode usuario = parser.getNext( IUsuarioXML.XML_ELEMENT );
    
            while ( usuario != null ) {
                AccountTO account = new AccountTO();
                account.setNombreusu( usuario.getString( IUsuarioXML.NOMBREUSU ) );
                account.setUsuario( usuario.getString( IUsuarioXML.USUARIO ) );
                account.setHashPassword( usuario.getString( IUsuarioXML.PASSWORD ) );
                account.setCenttra( usuario.getString( IUsuarioXML.CENTTRA ) );
                account.setCodidioma( usuario.getString( IUsuarioXML.CODIDIOMA ) );
                account.setCodperfil( usuario.getString( IUsuarioXML.CODPERFIL ) );
                account.setDesperfil( usuario.getString( IUsuarioXML.DESPERFIL ) );
                account.setFecactiva( usuario.getDate( IUsuarioXML.FECACTIVA ) );
                account.setFecdesact( usuario.getDate( IUsuarioXML.FECDESACT ) );
                account.setNivsegusu( usuario.getString( IUsuarioXML.NIVSEGUSU ) );
                account.setOficina( usuario.getString( IUsuarioXML.OFICINA ) );
                account.setFecbaja( usuario.getDate( IUsuarioXML.FECBAJA ) );
    
                listaAccounts.add( account );
                usuario = parser.getNext( IUsuarioXML.XML_ELEMENT );
            }
        } catch ( Exception e ) {
            throw new Exception( "[388]EASEWSClientFacade.populateAccounts: Erro ao recuperar o Usuario. Msg: "+ e.getMessage() +" >> Source: "+ xmlAccounts );
        }
        return listaAccounts;
    }
    
    /**
     * Carrega lista com os grupos
     * @param xmlPerfil
     * @return
     * @throws Exception
     */
    public List<GroupTO> populateGroups( String xmlPerfil ) throws Exception {
        List<GroupTO> listGroup = new ArrayList<GroupTO>();
        
        EaseXMLParser parser = new EaseXMLParser( xmlPerfil );
        try {
	        EaseXMLNode perfilNode = parser.getNext( IPerfilXML.XML_ELEMENT );
        
	        while ( perfilNode != null ) {
	            GroupTO group = new GroupTO();
	            group.setCodPerfil( perfilNode.getString( IPerfilXML.CODIGO ) );
	            group.setDesPerfil( perfilNode.getString( IPerfilXML.DESCRICAO ) );
	            listGroup.add( group );
	            perfilNode = parser.getNext( IPerfilXML.XML_ELEMENT );
	        }
    	} catch ( Exception e ) {
    		throw new Exception( "[415]EASEWSClientFacade.populateGroups: Erro ao recuperar o Grupo. Msg: "+ e.getMessage() +" >> Source: "+ xmlPerfil );
    	}

        return listGroup;
    }
    
    /**
     * Carrega lista com as conexoes entre usuarios e grupos/perfis
     * @param xmlConnection
     * @return
     * @throws Exception
     */
    public List<ConnectionTO> populateConnections( String xmlConnection ) 
            throws Exception {
        List<ConnectionTO> listConnection = new ArrayList<ConnectionTO>();
        EaseXMLParser parser = new EaseXMLParser( xmlConnection );
        EaseXMLNode perfilNode = parser.getNext( IConexaoXML.XML_ELEMENT );
        while ( perfilNode != null ) {
            ConnectionTO connection = new ConnectionTO();
            connection.setUsuarioLogin( perfilNode.getString( IConexaoXML.USUARIO ) );
            try {
                // Usuarios reativados nao possuem grupo, desconsidera-los
                connection.setCodPerfil( perfilNode.getString( IConexaoXML.CODIGO ) );
            } catch ( Exception e ) {
                perfilNode = parser.getNext( IPerfilXML.XML_ELEMENT );
                continue;
            }
            connection.setDesPerfil( perfilNode.getString( IConexaoXML.DESCRICAO ) );
            connection.setFecDesact( perfilNode.getDate( IConexaoXML.FECDESACT ) );
            listConnection.add( connection );
            perfilNode = parser.getNext( IPerfilXML.XML_ELEMENT );
        }

        return listConnection;
    }
} 