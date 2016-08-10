package funcoes.account;
import java.util.Hashtable;

import br.com.pbti.ease.EaseWSClientFacade;
import br.com.pbti.ease.xml.EaseWSRetorno;
import br.com.pbti.ease.xml.IRetornoXML;
import to.AccountTO;
import util.UtilDate;
import util.UtilFuncoes;
import framework.XSA_Framework; 

@SuppressWarnings("unchecked")
/**
 * Classe auxiliar para refletir no EASE atualizacao de usuarios no Control-SA
 * 
 * @author Michael Alves Lins <malins@dba.com.br> 
 */
public class AtualizarAccount extends UtilFuncoes {

	/**
	 * @param IN_hashes
	 * @return
	 */
	public static int accountUpdate(Hashtable[] IN_hashes) {

		String function = "AccountUpdate";

		int rc = XSA_Framework.XSA_RC_OK;

		Hashtable operationHash = IN_hashes[XSA_Framework.XSA_OPERATION_HASH];
		Hashtable contextHash = IN_hashes[XSA_Framework.XSA_CONTEXT_HASH];
		Hashtable setIdHash = IN_hashes[XSA_Framework.XSA_SET_ID_HASH];
		Hashtable setKwsdHash = IN_hashes[XSA_Framework.XSA_SET_KWDS_HASH];
		Hashtable entityHash = IN_hashes[XSA_Framework.XSA_GET_ENTITY_HASH];

		startDebug(function, operationHash, contextHash, setIdHash, entityHash);

		// Keywords repassadas pelo Control-SA
		String usuarioLogin = (String) setIdHash.get("XSA_ACCOUNT_NAME");
		String password =     (String) setKwsdHash.get("XSA_PASSWORD");
		String revoked =      (String) setKwsdHash.get("XSA_REVOKE_STATUS");
		String perfilNovoKW = (String) setKwsdHash.get("EASE_CODPERFIL");
		 
		 // Para verificacao ao salvar alteracao de senha
		 boolean usuarioAtivo = false;

		try {
		    EaseWSClientFacade easeWS = EaseWSClientFacade.getInstance();
		    String dataStatus = null;
	        /**
	         * Caso o usuario nao exista deve emitir mensagem de errro, conform regra:
	         * 
	         * "If the Account does not exist on the Managed System, 
	         * the function should return an error (XSA_RC_ERROR)."
	         * 
	         * @see idmXmodule_DeveloperGuide_5100.pdf, page 107
	         */
	        EaseWSRetorno wsRetorno = easeWS.getUsuario( usuarioLogin );
	        boolean usuarioExiste = wsRetorno.getCodigo().equals( IRetornoXML.RETORNO_OK );
	        if ( !usuarioExiste ) {
	            XSA_Framework.XSA_WriteMessage("[74]AtualizarAccount.accountUpdate: Usuario inexistente no EASE => "+ usuarioLogin );
	            rc = XSA_Framework.XSA_RC_ERROR;

	        } else {
	            /**
	             * Se nao vier o novo status do control-sa recuperar a data de desativacao do EASE 
	             */
    			if ( revoked == null ) { 
    			    AccountTO usuario = easeWS.populateAccounts( wsRetorno.getXml() ).get(0);
    			    dataStatus = usuario.getFecdesactForControlSA();
    			    usuarioAtivo = !usuario.isDesativado();
    			} else {
                    /**
                     * Caso seja informado o status no control-sa, entao utiliza-lo
                     * e gerar a data de desativacao conforme o status fornecido
                     */
    			    usuarioAtivo = revoked.equals( XSA_Framework.ACCOUNT_REVOKE_STATUS.ACTIVE );
    			    // Trata status REVOKED: data anterior a hoje (D-1)
    			    if ( !usuarioAtivo ) {
    			        // Recupera data D-1 para desabilitar usuario
    			        dataStatus = UtilDate.getDataDesativacaoDefault();
    			    } else
    			    if ( usuarioAtivo ) {
    			        // Informa data futura (aprox. 5 anos)
    			        dataStatus = UtilDate.getDataAtivacaoDefaultForEASE();
    			    }
    			}
    			
                // Atualiza status do usuario (Habilitado ou nao)
                if ( !dataStatus.equals("") ) {
                    wsRetorno = easeWS.habilitarUsuario( usuarioLogin, dataStatus );
                    rc = easeWS.validaCodigoRetornoXML( wsRetorno );
                }

                // Verifica se atualizou corretamente dados acima
                if ( rc == XSA_Framework.XSA_RC_OK ) {
                    // Atualiza Perfil apenas se usuario estiver ATIVO
                    if( usuarioAtivo ) {
                        if ( perfilNovoKW != null && !perfilNovoKW.equals("") ){
                            wsRetorno = easeWS.vinculaUsuarioPerfil( usuarioLogin, perfilNovoKW );
                            rc = easeWS.validaCodigoRetornoXML( wsRetorno );
                        }

                        // Verifica se atualizou corretamente dados acima
                        if ( rc == XSA_Framework.XSA_RC_OK ) {
                            // Muda senha apenas se usuario estiver ATIVO
                            if( password != null && !password.equals("") ){
                                wsRetorno = easeWS.setNewPassword( usuarioLogin, password );
                                rc = easeWS.validaCodigoRetornoXML( wsRetorno );
                            }
                        }
                    }
                }
	        }
		} catch ( Exception e ) {
			XSA_Framework.ExceptionDescribe( e );
			XSA_Framework.XSA_WriteMessage( "Exception: " + e.toString() );
			rc = XSA_Framework.XSA_RC_ERROR;
		}

		XSA_Framework.XSA_WriteDebugExit(XSA_Framework.XSA_DEBUG_DETAIL,
				function, rc);

		return rc;
	}
}
