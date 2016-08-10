package funcoes.connection;

import java.util.Hashtable;

import br.com.pbti.ease.EaseWSClientFacade;
import br.com.pbti.ease.xml.EaseWSRetorno;
import util.UtilFuncoes;
import framework.XSA_Framework;

/**
 * Classe auxiliar para refletir no EASE exclusão de conexoes de usuarios e 
 * perfis do Control-SA 
 * 
 * @author Michael Alves Lins <malins@dba.com.br> 
 */
public class DeletarConnection {

    public static int connectionDelete( Hashtable IN_hashes[] ) {

        String function = "ConnectionDelete";
        
        int rc = XSA_Framework.XSA_RC_OK;
        
        Hashtable operationHash = IN_hashes[XSA_Framework.XSA_OPERATION_HASH];
        Hashtable contextHash = IN_hashes[XSA_Framework.XSA_CONTEXT_HASH];
        Hashtable searchHash = IN_hashes[XSA_Framework.XSA_SET_ID_HASH];
        Hashtable entityHash = IN_hashes[XSA_Framework.XSA_GET_ENTITY_HASH];

        UtilFuncoes.startDebug(function, operationHash, contextHash, searchHash
                , entityHash);

        try {
            String usuarioLogin = (String) searchHash.get( "XSA_ACCOUNT_NAME" );
            EaseWSRetorno wsRetorno = EaseWSClientFacade.getInstance().desvinculaUsuarioPerfil( usuarioLogin );

            // Se retornar erro, verificar se foi em razao de usuario NAO existir
            // Essa mensagem deveria ser "Usuario inexistente", porem Maria (Technocon) disse que foi solicitacao da Caixa...
            String MSG_USUARIO_INEXISTENTE = "MPE0269"; // MPE0269 - USUARIO OU SENHA INCORRETO
            String MSG_USUARIO_INEXISTENTE_INATIVO = "MPE0238";
            if ( wsRetorno.possuiErro() && ( wsRetorno.getMensagem().contains( MSG_USUARIO_INEXISTENTE_INATIVO ) ||
                                             wsRetorno.getMensagem().contains( MSG_USUARIO_INEXISTENTE ) ) ) {

                // Nao existindo, retorna acao sem erro
                rc = XSA_Framework.XSA_RC_OK;

            } else {
                rc = EaseWSClientFacade.getInstance().validaCodigoRetornoXML( wsRetorno );
            }
        } catch ( Exception e ) {
            XSA_Framework.ExceptionDescribe(e);
            XSA_Framework.XSA_WriteMessage("Exception: " + e.getMessage()); 
        }

        return rc;
    }
}
