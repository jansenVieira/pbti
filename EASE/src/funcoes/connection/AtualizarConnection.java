package funcoes.connection;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import br.com.pbti.ease.EaseWSClientFacade;
import br.com.pbti.ease.xml.EaseWSRetorno;
import util.UtilDate;
import util.UtilFuncoes;
import framework.XSA_Framework;

@SuppressWarnings("unchecked")
/**
 * Classe auxiliar para refletir no EASE atualizacao de conexoes de usuarios e 
 * perfis do Control-SA 
 * 
 * @author Michael Alves Lins <malins@dba.com.br> 
 */
public class AtualizarConnection {

    public static int atualizarConnection(Hashtable[] IN_hashes) {

        String function = "ConnectionUpdate";

        Hashtable operationHash = IN_hashes[XSA_Framework.XSA_OPERATION_HASH];
        Hashtable contextHash = IN_hashes[XSA_Framework.XSA_CONTEXT_HASH];
        Hashtable setIdHash = IN_hashes[XSA_Framework.XSA_SET_ID_HASH];
        Hashtable setKwsdHash = IN_hashes[XSA_Framework.XSA_SET_KWDS_HASH];
        Hashtable entityHash = IN_hashes[XSA_Framework.XSA_GET_ENTITY_HASH];

        UtilFuncoes.startDebug(function, operationHash, contextHash, setIdHash, entityHash);

        /**
         * No EASE a conexao nao possui atributos, entao se faz nada nessa acao
         */
        int rc = XSA_Framework.XSA_RC_OK;

//      String usuarioLogin = (String) setIdHash.get("XSA_ACCOUNT_NAME");
//      String codPerfil = (String) setIdHash.get("XSA_GROUP_NAME");
//        try {
//            rc = updateConexaoEASE( usuarioLogin, codPerfil );
//        } catch (Exception e) {
//            XSA_Framework.ExceptionDescribe(e);
//            XSA_Framework.XSA_WriteMessage("Exception: " + e.getMessage());
//        }

        XSA_Framework.XSA_WriteDebugExit(XSA_Framework.XSA_DEBUG_DETAIL,
                function, rc);

        return rc;
    }

    /**
     * Atualiza os dados de conexao do usuario/perfil no EASE
     * @param usuarioLogin
     * @param codPerfil
     * @return
     * @throws SQLException
     */
//    private static int updateConexaoEASE( String usuarioLogin, String codPerfil )
//            throws Exception {
//        EaseWSClientFacade easeWS = EaseWSClientFacade.getInstance();
//        EaseWSRetorno wsRetorno = easeWS.vinculaUsuarioPerfil( usuarioLogin, codPerfil );
//        return easeWS.validaCodigoRetornoXML( wsRetorno );
//    }
}