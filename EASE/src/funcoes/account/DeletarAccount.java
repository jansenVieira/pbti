package funcoes.account;

import java.util.Hashtable;

import br.com.pbti.ease.EaseWSClientFacade;
import br.com.pbti.ease.xml.EaseWSRetorno;
import br.com.pbti.ease.xml.EaseXMLNode;
import br.com.pbti.ease.xml.EaseXMLParser;
import br.com.pbti.ease.xml.IRetornoXML;
import util.UtilFuncoes;
import framework.XSA_Framework;

/**
 * Classe auxiliar para refletir no EASE exclusão de usuarios 
 * do Control-SA (CTSA)
 * 
 * @author Michael Alves Lins <malins@dba.com.br> 
 */
public class DeletarAccount {

    public static int accountDelete( Hashtable[] IN_hashes ) {
        
        String function = "AccountDelete";

        int rc = XSA_Framework.XSA_RC_OK;

        Hashtable operationHash = IN_hashes[ XSA_Framework.XSA_OPERATION_HASH ];
        Hashtable contextHash = IN_hashes[ XSA_Framework.XSA_CONTEXT_HASH ];
        Hashtable setIdHash = IN_hashes[ XSA_Framework.XSA_SET_ID_HASH ];
        Hashtable entityHash = IN_hashes[ XSA_Framework.XSA_GET_ENTITY_HASH ];

        UtilFuncoes.startDebug(function, operationHash, contextHash, setIdHash, entityHash);

        String loginUsuario = (String) setIdHash.get( "XSA_ACCOUNT_NAME" );

        try {
            EaseWSRetorno wsRetorno = EaseWSClientFacade.getInstance().excluirUsuario( loginUsuario );

            rc = EaseWSClientFacade.getInstance().validaCodigoRetornoXML( wsRetorno );
            
        } catch ( Exception e ) {
            XSA_Framework.ExceptionDescribe( e );
            XSA_Framework.XSA_WriteMessage( "Exception: "+ e.toString() );
            rc = XSA_Framework.XSA_RC_ERROR;
        }
        
        XSA_Framework.XSA_WriteDebugExit( XSA_Framework.XSA_DEBUG_DETAIL,
                function, rc );
        
        return rc;
    }
}