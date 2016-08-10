package br.com.pbti.sigal;

import br.com.pbti.ease.EaseWSClientFacade;

import com.tecnocom.mediosdepago.sat.webservice.xsd.Peticion;

/**
 * Singleton que abstrai as configuracoes da peticao vinda do Sigal
 * 
 * @author Michael Alves Lins <malins@dba.com.br>
 */
public final class SigalPeticion extends Peticion {

    private static SigalPeticion _instance;
    
    private SigalPeticion() {
    }
    
    /**
     * Get an instance without any Parametros
     * @return
     */
    public static synchronized SigalPeticion getInstance() throws Exception {
        return getInstance( 0 );
    }
    
    /**
     * Get an instance with numParametros of Parametros 
     * @param numParametros
     * @return
     */
    public static synchronized SigalPeticion getInstance( int numParametros ) 
            throws Exception {
        /**
         * DES-OFICIAL: S909090/202530, fornecido pela equipe SISPCS
         * 
         * (DES-ALTERNATIVO: WSINDRA/WSINDRAWS)
         */
        if ( _instance == null ) {
            _instance = new SigalPeticion();
            _instance.setUsuario( EaseWSClientFacade.getInstance().getEaseWSLogin() );     // Fornecido pelo PMZ (Agente)
            _instance.setPassword( EaseWSClientFacade.getInstance().getEaseWSPassword() ); // Fornecido pelo PMZ (Agente)
            _instance.setEntidad( "0104" ); // Valor default segundo INDRA
            _instance.setIdioma( "PO" );    // Valor default segundo INDRA
            _instance.setOficina( "0001" ); // Valor default segundo INDRA
            _instance.setPaginable( true ); // Nao paginar retorno
        }
        _instance.setTipoOperacion( "" );
        _instance.setTransaccion( "" );
        _instance.initParametros( numParametros );
        return _instance;
    }
}
