package funcoes.group;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import br.com.pbti.ease.EaseWSClientFacade;
import br.com.pbti.ease.xml.EaseWSRetorno;
import to.GroupTO;
import util.UtilFuncoes;
import framework.XSA_Framework;

@SuppressWarnings("unchecked")
public class ListarGroup extends UtilFuncoes {

	private static List<GroupTO> listaGroup;

	private static int maxGroup;
	
	public static int groupGet(Hashtable[] IN_hashes) {

		String function = "GroupGet";
		int rc = XSA_Framework.XSA_RC_OK;

		Hashtable operationHash = IN_hashes[XSA_Framework.XSA_OPERATION_HASH];
		Hashtable contextHash = IN_hashes[XSA_Framework.XSA_CONTEXT_HASH];
		Hashtable searchHash = IN_hashes[XSA_Framework.XSA_GET_SEARCH_HASH];
		Hashtable entityHash = IN_hashes[XSA_Framework.XSA_GET_ENTITY_HASH];

		UtilFuncoes.startDebug(function, operationHash, contextHash, searchHash, entityHash);

		String stage = (String) operationHash.get("XSA_GET_STAGE");

		if ( stage.equals("START") ) {
			maxGroup = 0;
			listaGroup = new ArrayList<GroupTO>();
			rc = ListarGroup.getPerfisEase( (String) searchHash.get( "XSA_GROUP_NAME" ) );
		} else {
			for ( int i = 0; i < 10; i++ ) {
			    GroupTO group;
				try {
					group = listaGroup.get( i + maxGroup );
				} catch (IndexOutOfBoundsException ioobe ) {
                    // Sinaliza que foi finalizado o processamento deste metodo
                    rc = XSA_Framework.XSA_RC_OK;
                    break;
                }
				
				try {
				    // XSA_GROUP_NAME deve ser String
					entityHash.put("XSA_GROUP_NAME", ""+ group.getCodPerfil() );
				} catch ( Exception e ) {
				    XSA_Framework.ExceptionDescribe( e );
		            XSA_Framework.XSA_WriteMessage("[51]ListarGroup.groupGet: Erro ao recuperar o codigo do Perfil. " + e.toString());
		            rc = XSA_Framework.XSA_RC_ERROR;
				}
				
				try {
				    entityHash.put("EASE_DESPERFIL", group.getDesPerfil() );
				} catch ( Exception e ) {
				    XSA_Framework.ExceptionDescribe( e );
				    XSA_Framework.XSA_WriteMessage("[59]ListarGroup.groupGet: Erro ao recuperar a descricao do Perfil. " + e.toString());
		            rc = XSA_Framework.XSA_RC_ERROR;
				}

				try {
				    entityHash.put("EASE_TIPPERFIL", group.getTipPerfil() );
				} catch ( Exception e ) {
				    XSA_Framework.ExceptionDescribe( e );
				    XSA_Framework.XSA_WriteMessage("[67]ListarGroup.groupGet: Erro ao recuperar o tipo do Perfil. " + e.toString());
		            rc = XSA_Framework.XSA_RC_ERROR;
				}

				try {
				    XSA_Framework.XSA_WriteEntity();
				} catch ( Exception e ) {
				    XSA_Framework.ExceptionDescribe( e );
				    XSA_Framework.XSA_WriteMessage("[75]ListarGroup.groupGet: Erro ao escrever a entidade no Control-SA. " + e.toString());				    
		            rc = XSA_Framework.XSA_RC_ERROR;
				}
				// Sinaliza que deve retornar a este metodo
				rc = XSA_Framework.XSA_RC_MORE;
			}
			maxGroup = maxGroup + 10;
		}

		// send a debug message when exiting the function
		XSA_Framework.XSA_WriteDebugExit(XSA_Framework.XSA_DEBUG_DETAIL,
				function, rc);

		return rc;
	}

	/**
	 * Retorna os perfis do EASE, todos ou conforme filtro informado
	 * @param codPerfil
	 * @return
	 */
	private static int getPerfisEase( String codPerfil ) {

	    int rc = XSA_Framework.XSA_RC_OK;

		try {
		    EaseWSClientFacade easeWS = EaseWSClientFacade.getInstance();
		    EaseWSRetorno wsRetorno = null;

			if ( codPerfil != null && !codPerfil.equals("") ) {
			    wsRetorno = easeWS.getPerfil( codPerfil );
			} else {
			    wsRetorno = easeWS.getPerfis();
			}
			rc = easeWS.validaCodigoRetornoXML( wsRetorno );

			if ( rc == XSA_Framework.XSA_RC_OK ) {
			    listaGroup = easeWS.populateGroups( wsRetorno.getXml() );
			    // Sinaliza que devera retornar ao metodo ListarGroup.groupGet()
			    rc = XSA_Framework.XSA_RC_MORE;
			}
		} catch (Throwable e) {
			XSA_Framework.ExceptionDescribe(e);
			XSA_Framework.XSA_WriteMessage("Exception: " + e.toString());
			rc = XSA_Framework.XSA_RC_ERROR;
		}
		return rc;
	}
}