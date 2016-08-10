package funcoes.connection;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import br.com.pbti.ease.EaseWSClientFacade;
import br.com.pbti.ease.xml.EaseWSRetorno;
import to.ConnectionTO;
import util.UtilFuncoes;
import framework.XSA_Framework;

@SuppressWarnings("unchecked")
public class ListarConnection extends UtilFuncoes {

    private static List<ConnectionTO> listaConnection;

    private static int maxUser;

    public static int connectionGet(Hashtable[] IN_hashes) {

        String function = "ConnectionGet";
        int rc = XSA_Framework.XSA_RC_OK;

        Hashtable operationHash = IN_hashes[XSA_Framework.XSA_OPERATION_HASH];
        Hashtable contextHash = IN_hashes[XSA_Framework.XSA_CONTEXT_HASH];
        Hashtable searchHash = IN_hashes[XSA_Framework.XSA_GET_SEARCH_HASH];
        Hashtable entityHash = IN_hashes[XSA_Framework.XSA_GET_ENTITY_HASH];

        UtilFuncoes.startDebug(function, operationHash, contextHash, searchHash, entityHash);

        String stage = (String) operationHash.get("XSA_GET_STAGE");

        if ( stage.equals("START") ) {
            maxUser = 0;
            listaConnection = new ArrayList<ConnectionTO>();
            rc = ListarConnection.getConexoesEASE( 
                     (String) searchHash.get("XSA_ACCOUNT_NAME") // LOGIN USUARIO
                    ,(String) searchHash.get("XSA_GROUP_NAME")   // COD PERFIL
                    );
        } else {
            for (int i = 0; i < 10; i++) {
                try {
                    ConnectionTO connection = listaConnection.get( i + maxUser );

                    entityHash.put( "XSA_ACCOUNT_NAME", connection.getUsuarioLogin() );
                    // XSA_GROUP_NAME deve ser String
                    entityHash.put( "XSA_GROUP_NAME",   connection.getCodPerfil() +"" );
                    entityHash.put( "EASE_DESPERFIL",   connection.getDesPerfil() );
                    entityHash.put( "EASE_FECDESACT",   connection.getFecDesact() );
                    
                    XSA_Framework.XSA_WriteEntity();
                    rc = XSA_Framework.XSA_RC_MORE;
                    
                } catch (IndexOutOfBoundsException e) {
                    rc = XSA_Framework.XSA_RC_OK;
                    break;
                }
            }
            maxUser = maxUser + 10;
        }

        // send a debug message when exiting the function
        XSA_Framework.XSA_WriteDebugExit(XSA_Framework.XSA_DEBUG_DETAIL,
                function, rc);

        return rc;
    }

    private static int getConexoesEASE( String usuarioLogin, String codPerfil ) {
        int rc = XSA_Framework.XSA_RC_OK;
        EaseWSRetorno wsRetorno = null;

        try {
            EaseWSClientFacade easeWS = EaseWSClientFacade.getInstance();

            boolean informouUsuario = usuarioLogin != null && !usuarioLogin.trim().equals("");
            boolean informouPerfil = codPerfil != null && !codPerfil.trim().equals("");
            // Para o caso de ter como filtro tanto Usuario quanto Perfil
            boolean validarPerfil = false;

            // Consulta por Usuario e Perfil
            if ( informouUsuario && informouPerfil ) {
                // Consulta conexao conforme usuario e valida o perfil existente
                wsRetorno = easeWS.getUsuario( usuarioLogin );
                validarPerfil = true;
            } else
            if ( informouUsuario ) {
                // Retorna conexao conforme Usuario (e seu unico perfil)
                wsRetorno = easeWS.getUsuario( usuarioLogin );  
            } else 
            if ( informouPerfil ) {
                // Retorna TODOS os usuarios de APENAS UM perfil
                wsRetorno = easeWS.getUsuariosPorPerfil( codPerfil );
            } else 
            /* Nao informou nenhum dos dois (!usuario e !perfil) */{
                // RETORNA TODOS os usuarios de TODOS os perfis
                wsRetorno = easeWS.getUsuariosPorPerfil( "" );   
            }

            // Se retornar erro, verificar se foi em razao de usuario NAO existir
            // Essa mensagem deveria ser "Usuario inexistente", porem Maria (Technocon) disse que foi solicitacao da Caixa...
            String MSG_USUARIO_INEXISTENTE = "MPE0269"; // MPE0269 - USUARIO OU SENHA INCORRETO
            if ( wsRetorno.possuiErro() && wsRetorno.getMensagem().contains( MSG_USUARIO_INEXISTENTE ) ) {

                // Nao existindo, retorna acao sem erro
                rc = XSA_Framework.XSA_RC_OK;

            } else {

                // Valida o retorno xml que vem do EASE
                rc = easeWS.validaCodigoRetornoXML( wsRetorno );

                // Trata conexoes caso nao tenham retornado erros
                if ( rc == XSA_Framework.XSA_RC_OK ) {
                    boolean popularLista = true;
                    
                    // Se passou apenas usuario, verifica se nao possui perfil, se nao possuir, nao preenche a lista
                    // Para correcao de erro na exclusao de vinculo
                    if ( informouUsuario && !informouPerfil ) {
                        List<ConnectionTO> lista = easeWS.populateConnections( wsRetorno.getXml() );
                        if ( !lista.isEmpty() ) {
                            ConnectionTO connection = lista.get(0);
                            popularLista = !connection.getCodPerfil().trim().equals("");
                        }
                    }
                    
                    if ( popularLista )
                        // Popula uma lista de usuarios a partir do retorno XML
                        listaConnection = easeWS.populateConnections( wsRetorno.getXml() );
    //                /**
    //                 * Verifica se o usuario retornado esta vinculado ao perfil solicitado
    //                 */
    //                if ( validarPerfil ) {
    //                    ConnectionTO conexao = listaConnection.get(0);
    //                    if ( conexao.getCodPerfil() != Integer.parseInt(codPerfil) ) {
    //                        rc = XSA_Framework.XSA_RC_ERROR;
    //                        XSA_Framework.XSA_WriteMessage( "Usuario encontrado porem nao conectado ao perfil informado.(Usuario: "+ usuarioLogin +" - Perfil: "+ codPerfil +")" );
    //                    }
    //                } else 
                    rc = XSA_Framework.XSA_RC_MORE;
                }
            }
        } catch ( Throwable e ) {
            XSA_Framework.ExceptionDescribe( e );
            XSA_Framework.XSA_WriteMessage( "Exception: " + e.toString() );
            rc = XSA_Framework.XSA_RC_ERROR;
        }
        return rc;
    }
}