package funcoes.connection;

import java.util.Hashtable;
import java.util.List;

import br.com.pbti.ease.EaseWSClientFacade;
import br.com.pbti.ease.xml.EaseWSRetorno;
import to.AccountTO;
import util.UtilFuncoes;
import framework.XSA_Framework;

@SuppressWarnings("unchecked")
/**
 * Classe auxiliar para refletir no EASE inclusao de conexoes de usuarios e 
 * perfis do Control-SA 
 * 
 * @author Michael Alves Lins <malins@dba.com.br> 
 */
public class AdicionarConnection {

    public static int connectionAdd(Hashtable[] IN_hashes) {

        String function = "ConnectionAdd";
        int rc = XSA_Framework.XSA_RC_OK;

        Hashtable operationHash = IN_hashes[XSA_Framework.XSA_OPERATION_HASH];
        Hashtable contextHash = IN_hashes[XSA_Framework.XSA_CONTEXT_HASH];
        Hashtable setIdHash = IN_hashes[XSA_Framework.XSA_SET_ID_HASH];
        Hashtable setKwsdHash = IN_hashes[XSA_Framework.XSA_SET_KWDS_HASH];
        Hashtable entityHash = IN_hashes[XSA_Framework.XSA_GET_ENTITY_HASH];

        UtilFuncoes.startDebug(function, operationHash, contextHash, setIdHash, entityHash);

        String usuarioLogin = (String) setIdHash.get("XSA_ACCOUNT_NAME");
        String codPerfil = (String) setIdHash.get("XSA_GROUP_NAME");

        try {
            if ( !existeConnection( usuarioLogin, codPerfil ) ) {
                rc = addConexaoEASE( usuarioLogin, codPerfil );                               
            } else {
                /**
                 * Deve retornar erro caso conexao exista, conforme regra abaixo:
                 * 
                 * "If the Connection already exists on the Managed System, the
                 *  function should return an error (XSA_RC_ERROR)."
                 *  
                 *  @see idmXmodule_DeveloperGuide_5100.pdf,  page 109
                 */
                rc = XSA_Framework.XSA_RC_ERROR;
                XSA_Framework.XSA_WriteMessage("[49]AdicionarConnection: Usuario ja possui esse perfil no sistema. Usuario:"+ usuarioLogin +" Perfil:"+ codPerfil);
            }
        } catch (Exception e) {
            XSA_Framework.ExceptionDescribe(e);
            XSA_Framework.XSA_WriteMessage("Exception: " + e.getMessage());
        }

        XSA_Framework.XSA_WriteDebugExit(XSA_Framework.XSA_DEBUG_DETAIL,function, rc);

        return rc;
    }

    /**
     * Verifica se a conexao informada ja existe no EASE
     * @param usuarioLogin
     * @param codPerfil
     * @throws Exception
     */
    private static boolean existeConnection( String usuarioLogin, String codPerfil ) 
            throws Exception {
        boolean result = false;
        EaseWSClientFacade easeWS = EaseWSClientFacade.getInstance();
        EaseWSRetorno wsRetorno = easeWS.getUsuario( usuarioLogin );
        List<AccountTO> list = easeWS.populateAccounts( wsRetorno.getXml() );
        AccountTO usuarioTO = null;
        if ( !list.isEmpty() ) {
            usuarioTO = list.get(0);
            result = usuarioTO.getCodperfil().equals( codPerfil );
        }
        return result;
    }
    
    /**
     * Associa o usuario ao perfil informado 
     * @param codPerfil
     * @param usuarioLogin
     * @return Return Code do framework
     * @throws Exception
     */
    private static int addConexaoEASE( String usuarioLogin, String codPerfil )
            throws Exception {
        EaseWSClientFacade easeWS = EaseWSClientFacade.getInstance();
        EaseWSRetorno wsRetorno = easeWS.vinculaUsuarioPerfil( usuarioLogin, codPerfil );
        return easeWS.validaCodigoRetornoXML( wsRetorno );
    }
}