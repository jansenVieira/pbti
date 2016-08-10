package br.com.pbti.ease.xml;

import br.com.pbti.sigal.SigalPeticion;

import com.tecnocom.mediosdepago.sat.webservice.xsd.Parametro;

/**
 * Classe de configuracoes para chamadas aos servicos de Perfil do Webservice SAT
 * Elemento: <Registro> 
 * Transação: SGCLPAJ/SGCOPAJ
 * Operação: view/select
 * 
 * @author Michael Alves Lins <malins@dba.com.br>
 */
public class EaseWSPerfil {    
    /**
     * Retorna os dados apenas o perfil informado
     * 
     * @param codPerfil
     * @return
     * @throws Exception
     */
    public SigalPeticion getPerfil( String codPerfil ) throws Exception {
        SigalPeticion msgEnvio = SigalPeticion.getInstance( 1 );
        
        msgEnvio.addParametro( 0, new Parametro( IPerfilXML.CODIGO, codPerfil ) );

        msgEnvio.setTipoOperacion( "select" );
        msgEnvio.setTransaccion( "SGCOPAJ" );
        
        return msgEnvio;
    }
    
    /**
     * Retorna os dados de todos os perfis do sistema
     * 
     * @return
     * @throws Exception
     */
    public SigalPeticion getPerfis() throws Exception {
        SigalPeticion msgEnvio = SigalPeticion.getInstance( 1 );
        
        msgEnvio.addParametro( 0, new Parametro( "INDACTIVO", "S" ) ); // Perfis Ativos
//        msgEnvio.addParametro( 1, new Parametro( "INDACTIVO", "N" ) ); // Perfis Inativos
//        msgEnvio.addParametro( 1, new Parametro( "INDACTIVO", "" ) ); // Perfis Ativos e Inativos
        
        msgEnvio.setTipoOperacion( "view" );
        msgEnvio.setTransaccion( "SGCLPAJ" );
        
        return msgEnvio;
    }
}