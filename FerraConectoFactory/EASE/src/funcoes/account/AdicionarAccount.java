package funcoes.account;

import java.util.Date;
import java.util.Hashtable;

import br.com.pbti.ease.EaseWSClientFacade;
import br.com.pbti.ease.xml.EaseWSRetorno;
import br.com.pbti.ease.xml.IRetornoXML;
import to.AccountTO;
import util.CriptografiaEase;
import util.UtilDate;
import util.UtilFuncoes;
import framework.XSA_Framework;

@SuppressWarnings("unchecked")
public class AdicionarAccount {

    /**
     * Classe auxiliar para refletir no EASE inclusao de usuarios no Control-SA
     * 
     * @author Michael Alves Lins <malins@dba.com.br> 
     */
	public static int accountAdd(Hashtable[] IN_hashes) {

		String function = "AccountAdd";

		int rc = XSA_Framework.XSA_RC_OK;

		Hashtable operationHash = IN_hashes[XSA_Framework.XSA_OPERATION_HASH];
		Hashtable contextHash = IN_hashes[XSA_Framework.XSA_CONTEXT_HASH];
		Hashtable setIdHash = IN_hashes[XSA_Framework.XSA_SET_ID_HASH];
		Hashtable setKwsdHash = IN_hashes[XSA_Framework.XSA_SET_KWDS_HASH];
		Hashtable searchHash = IN_hashes[XSA_Framework.XSA_GET_SEARCH_HASH];
		Hashtable entityHash = IN_hashes[XSA_Framework.XSA_GET_ENTITY_HASH];

		UtilFuncoes.startDebug(function, operationHash, contextHash, searchHash, entityHash);

		String usuarioLogin = (String) setIdHash.get("XSA_ACCOUNT_NAME");
		String usuarioPassword = (String) setKwsdHash.get("XSA_PASSWORD");

		try {

		    EaseWSClientFacade easeWS = EaseWSClientFacade.getInstance();

	        AccountTO usuario = new AccountTO();
	        usuario.setUsuario( usuarioLogin );
	        usuario.setPassword( usuarioPassword );

	        try {
	            // Nome do usuario
	            usuario.setNombreusu( setKwsdHash.get("EASE_NOMBREUSU").toString() );
	        } catch ( Exception e ) {
	            XSA_Framework.ExceptionDescribe( e );
	            XSA_Framework.XSA_WriteMessage("[54] AdicionarAccount.accountAdd: keyword EASE_NOMBREUSU nao encontrada. Provavelmente nao foi informado valor no 'TEMPLATE' ou 'Create Like'.");
	            rc = XSA_Framework.XSA_RC_ERROR;
	        }

	        try {// Codigo do Perfil 
	             // CAMPO OPICIONAL, alteração feita na especificação v. 5.0
	            if ( setKwsdHash.get("EASE_CODPERFIL") != null && 
	                 !setKwsdHash.get("EASE_CODPERFIL").toString().trim().equals("") )
	            {
	                usuario.setCodperfil( setKwsdHash.get("EASE_CODPERFIL").toString() );
	            }
            } catch ( Exception e ) {
                XSA_Framework.ExceptionDescribe( e );
                XSA_Framework.XSA_WriteMessage("[67] AdicionarAccount.accountAdd: keyword EASE_CODPERFIL nao encontrada. Provavelmente nao foi informado valor no 'TEMPLATE' ou 'Create Like'.");
                rc = XSA_Framework.XSA_RC_ERROR;
            }

            // Data de desativacao do usuario
//	        String easeFecdesact = "";
//            try {
//                easeFecdesact = setKwsdHash.get("EASE_FECDESACT").toString();
//            } catch ( NullPointerException npe ) {
//                XSA_Framework.ExceptionDescribe( npe );
//                XSA_Framework.XSA_WriteMessage("[78] AdicionarAccount.accountAdd: keyword EASE_FECDESACT nao encontrada. Provavelmente nao foi informado valor no 'TEMPLATE' ou 'Create Like'.");
//                rc = XSA_Framework.XSA_RC_ERROR;
//            }
//            try {
//                usuario.setFecdesact( UtilDate.parseDateForControlSA( easeFecdesact ) );
//            } catch ( Exception e ) {
//                XSA_Framework.ExceptionDescribe( e );
//                XSA_Framework.XSA_WriteMessage("[85] AdicionarAccount.accountAdd: keyword EASE_FECDESACT contendo DATA INVALIDA("+ easeFecdesact +"). Deve estar no formato yyyyMMddHHmmss.");
//                rc = XSA_Framework.XSA_RC_ERROR;                
//            }
	        /**
	         * Gerenciamento interno a pedido do Gestor GESET07 (Gustavo) em 22/05/2012 na Homologacao
	         * Desativacao em 50 anos
	         */
            usuario.setFecdesact( UtilDate.getDataAtivacaoDefaultForControlSA() );

            // Data de ativacao do usuario (Calendar.getInstance().getTime())
            String easeFecactiva = "";
            try {
                easeFecactiva = setKwsdHash.get("EASE_FECACTIVA").toString();
            } catch ( NullPointerException npe ) {
                XSA_Framework.ExceptionDescribe( npe );
                XSA_Framework.XSA_WriteMessage("[95] AdicionarAccount.accountAdd: keyword EASE_FECACTIVA nao encontrada. Provavelmente nao foi informado valor no 'TEMPLATE' ou 'Create Like'.");
                rc = XSA_Framework.XSA_RC_ERROR;
            }
            try {
                // Data de desativacao deve ser maior ou igual a data corrente
                Date dataAtivacao = UtilDate.parseDateForControlSA( easeFecactiva );
                if ( dataAtivacao.compareTo( UtilDate.parseDateForEASE( UtilDate.getHojeEase() ) ) < 0 ) {
                    XSA_Framework.XSA_WriteMessage("[102] AdicionarAccount.accountAdd: EASE_FECACTIVA deve ser maior ou igual a data corrente. >>"+ UtilDate.formatDateEase(dataAtivacao) );
                    rc = XSA_Framework.XSA_RC_ERROR;
                } else
                    usuario.setFecactiva( dataAtivacao );
            } catch ( Exception e ) {
                XSA_Framework.ExceptionDescribe( e );
                XSA_Framework.XSA_WriteMessage("[108] AdicionarAccount.accountAdd: keyword EASE_FECACTIVA contendo DATA INVALIDA("+ easeFecactiva +"). Deve estar no formato yyyyMMddHHmmss.");
                rc = XSA_Framework.XSA_RC_ERROR;                
            }
	        
	        try {
	            // Código da estação de trabalho do usuário ("")
	            //  CAMPO OPICIONAL, alteração feita na especificação v. 5.0
                if ( setKwsdHash.get("EASE_CENTTRA") != null && 
                    !setKwsdHash.get("EASE_CENTTRA").toString().trim().equals("") )
                   {
                    usuario.setCenttra( setKwsdHash.get("EASE_CENTTRA").toString() );
                   }
            } catch ( NullPointerException npe ) {
                XSA_Framework.ExceptionDescribe( npe );
                XSA_Framework.XSA_WriteMessage("[122] AdicionarAccount.accountAdd: keyword EASE_CENTTRA nao encontrada. Provavelmente nao foi informado valor no 'TEMPLATE' ou 'Create Like'.");
                rc = XSA_Framework.XSA_RC_ERROR;
            }
        
	        
	        try {
	            // Local de trabalho do usuário ("0001")
                usuario.setOficina( setKwsdHash.get("EASE_OFICINA").toString() );
            } catch ( NullPointerException npe ) {
                XSA_Framework.ExceptionDescribe( npe );
                XSA_Framework.XSA_WriteMessage("[132] AdicionarAccount.accountAdd: keyword EASE_OFICINA nao encontrada. Provavelmente nao foi informado valor no 'TEMPLATE' ou 'Create Like'.");
                rc = XSA_Framework.XSA_RC_ERROR;
            }
	        
            try {
                // Idioma ("PO")
                usuario.setCodidioma( setKwsdHash.get("EASE_CODIDIOMA").toString() );
            } catch ( NullPointerException npe ) {
                XSA_Framework.ExceptionDescribe( npe );
                XSA_Framework.XSA_WriteMessage("[141] AdicionarAccount.accountAdd Error: keyword EASE_CODIDIOMA nao encontrada. Provavelmente nao foi informado valor no 'TEMPLATE' ou 'Create Like'.");
                rc = XSA_Framework.XSA_RC_ERROR;
            }
	        
            try {
                // Nivel de seguranca ("0")
                usuario.setNivsegusu( setKwsdHash.get("EASE_NIVSEGUSU").toString() );
            } catch ( NullPointerException npe ) {
                XSA_Framework.ExceptionDescribe( npe );
                XSA_Framework.XSA_WriteMessage("[150] AdicionarAccount.accountAdd: keyword EASE_NIVSEGUSU nao encontrada. Provavelmente nao foi informado valor no 'TEMPLATE' ou 'Create Like'.");
                rc = XSA_Framework.XSA_RC_ERROR;
            }

	        EaseWSRetorno wsRetorno = easeWS.addUsuario( usuario );
	        easeWS.validaCodigoRetornoXML( wsRetorno );
		} catch (Exception e) {
			XSA_Framework.ExceptionDescribe(e);
			XSA_Framework.XSA_WriteMessage("[158]AdicionarAccount.accountAdd: Erro desconhecido >> \n" + e.toString());
			rc = XSA_Framework.XSA_RC_ERROR;
		}
		
        XSA_Framework.XSA_DebugHash(XSA_Framework.XSA_DEBUG_DETAIL, "XSA_GetEntityHash", entityHash);
        XSA_Framework.XSA_WriteDebugExit(XSA_Framework.XSA_DEBUG_DETAIL, function, rc);

		return rc;
	}
}
