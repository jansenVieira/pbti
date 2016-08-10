package funcoes.account;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import br.com.pbti.ease.EaseWSClientFacade;
import br.com.pbti.ease.xml.EaseWSRetorno;
import br.com.pbti.ease.xml.EaseWSUsuario;
import to.AccountTO;
import framework.XSA_Framework;
import util.UtilDate;
import util.UtilFuncoes;

/**
 * Classe que importa os usuarios do EASE para o Control-SA (CTSA)
 * 
 * @author Michael Alves Lins <malins@dba.com.br> 
 */
@SuppressWarnings("unchecked")
public class ListarAccount extends UtilFuncoes {

    private static List<AccountTO> listaAccounts;

    private static int maxUser;
    
    public static int accountGet(Hashtable[] IN_hashes) {

        String function = "AccountGet";

        int rc = XSA_Framework.XSA_RC_OK;

        Hashtable operationHash = IN_hashes[ XSA_Framework.XSA_OPERATION_HASH ];
        Hashtable contextHash   = IN_hashes[ XSA_Framework.XSA_CONTEXT_HASH ];
        Hashtable searchHash    = IN_hashes[ XSA_Framework.XSA_GET_SEARCH_HASH ];
        Hashtable entityHash    = IN_hashes[ XSA_Framework.XSA_GET_ENTITY_HASH ];

        UtilFuncoes.startDebug(function, operationHash, contextHash, searchHash, entityHash);

        String stage = (String) operationHash.get( "XSA_GET_STAGE" );

        if ( stage.equals( "START" ) ) {
            maxUser = 0;
            listaAccounts = new ArrayList<AccountTO>();
            rc = ListarAccount.getUsuariosEase (
                    (String) searchHash.get( "XSA_ACCOUNT_NAME" )
                    );

            // Só tem um registro (Ver metodo getUsuariosEase usado acima)
            if ( rc == XSA_Framework.XSA_RC_OK ) {
                try {
                    adicionarAccountControlSA( 0, entityHash );
                } catch ( IndexOutOfBoundsException e ) {
                    rc = XSA_Framework.XSA_RC_OK;
                }
            }
        } else {
            for (int i = 0; i < 10; i++) {
                try {
                    adicionarAccountControlSA( i, entityHash );
                    rc = XSA_Framework.XSA_RC_MORE;

                } catch ( IndexOutOfBoundsException e ) {
                    // Nao possui mais registros
                    rc = XSA_Framework.XSA_RC_OK;
                    break;
                }
            }
            maxUser = maxUser + 10;
        }

        XSA_Framework.XSA_WriteDebugExit( XSA_Framework.XSA_DEBUG_DETAIL, 
                function, rc );

        return rc;
    }
    
    /**
     * Consulta os usuarios no EASE e carrega lista de AccountTO
     * @param rc
     * @param XSA_OperationHash
     * @param XSA_GetSearchHash
     * @return
     */
    private static int getUsuariosEase( String usuarioLogin ) {

        int rc = XSA_Framework.XSA_RC_OK;
        EaseWSRetorno wsRetorno = null;

        try {
            EaseWSClientFacade easeWS = EaseWSClientFacade.getInstance(); 
            if ( usuarioLogin != null ) {
                wsRetorno = easeWS.getUsuario( usuarioLogin );
            } else {
                wsRetorno = easeWS.getUsuarios();
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

                // Trata usuarios caso nao tenham retornado erros
                if ( rc == XSA_Framework.XSA_RC_OK ) {
                    // Popula uma lista de usuarios a partir do retorno XML
                    listaAccounts = easeWS.populateAccounts( wsRetorno.getXml() );

                    // Para que o Control-SA volte a chamar o metodo AccountGet 
                    // e trabalhe a lista listaAccounts
                    if ( usuarioLogin == null ) {
                        rc = XSA_Framework.XSA_RC_MORE;
                    }
                }
            }
        } catch ( Throwable e ) {
            XSA_Framework.ExceptionDescribe( e );
            XSA_Framework.XSA_WriteMessage( "Exception: " + e.toString() );
            rc = XSA_Framework.XSA_RC_ERROR;
        }
        return rc;
    }

    /**
     * Inclui usuarios do EASE no Control-SA
     * @param index
     * @param entityHash
     */
    private static void adicionarAccountControlSA(int index,
            Hashtable entityHash) {
        // Preenche Keywords definidas no XModule para este Managed System
        AccountTO usuario = listaAccounts.get( index + maxUser );
        // Trata apenas usuarios ATIVOS (nao excluidos logicamente)
        if ( usuario.getFecbaja().equals( UtilDate.getFecbajaUsuarioNaoExcluido() ) ) {
            entityHash.put( "XSA_ACCOUNT_NAME",  usuario.getUsuario() );
            entityHash.put( "EASE_NIVSEGUSU",    usuario.getNivsegusu() );
            entityHash.put( "EASE_CENTTRA",      usuario.getCenttra() );
            entityHash.put( "EASE_DESPERFIL",    usuario.getDesperfil() );
            entityHash.put( "EASE_CODPERFIL",    usuario.getCodperfil() );
            entityHash.put( "EASE_FECACTIVA",    usuario.getFecactivaForControlSA() );
            entityHash.put( "EASE_FECDESACT",    usuario.getFecdesactForControlSA() );
            entityHash.put( "EASE_NOMBREUSU",    usuario.getNombreusu() );
            entityHash.put( "EASE_OFICINA",      usuario.getOficina() );
            entityHash.put( "EASE_CODIDIOMA",    usuario.getCodidioma() );
            entityHash.put( "XSA_REVOKE_STATUS", usuario.getRevokeStatus() );
    
            // Grava account no Control-SA
            XSA_Framework.XSA_WriteEntity();
        }
    }
}