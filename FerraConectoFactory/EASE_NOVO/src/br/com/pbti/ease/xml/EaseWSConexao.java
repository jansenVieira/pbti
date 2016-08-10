package br.com.pbti.ease.xml;

import br.com.pbti.sigal.SigalPeticion;

import com.tecnocom.mediosdepago.sat.webservice.xsd.Parametro;

/**
 * Classe de configuracoes para chamadas aos servicos de Conexao do Webservice SAT 
 * (relação entre Usuarios e Perfil)
 * Elemento: <Registro> 
 * Transação: SGCLPER/SGCOPER
 * Operação: view/select
 * Função: CL/CO
 * 
 * @author Michael Alves Lins <malins@dba.com.br>
 */
public class EaseWSConexao {

    /**
     * Retorna os dados de todos os usuarios de determinado perfil
     * @param codPerfil
     * @return
     * @throws Exception
     */
    public SigalPeticion getUsuariosPorPerfil( String codPerfil ) throws Exception {
        SigalPeticion msgEnvio = SigalPeticion.getInstance( 3 );
        
        msgEnvio.addParametro( 0, new Parametro( "FUNCION", "CL" ) );
        msgEnvio.addParametro( 1, new Parametro( "CODPERFILC", codPerfil ) );
     // Apenas usuario ATIVOS [N]
        msgEnvio.addParametro( 2, new Parametro( "USUBAJAC", "N" ) );

        msgEnvio.setTipoOperacion( "view" );
        msgEnvio.setTransaccion( "SGCLUSU" );
        
        return msgEnvio;
    }
    
    /**
     * Vincula um usuario a um perfil
     * 
     * @param usuario
     * @param codPerfil
     * @return
     * @throws Exception
     */
    public SigalPeticion vinculaUsuarioPerfil( String usuarioLogin, String codPerfil ) 
            throws Exception {
        SigalPeticion msgEnvio = SigalPeticion.getInstance( 3 );
        
        msgEnvio.addParametro( 0, new Parametro( "FUNCION", "AS" ) );
        msgEnvio.addParametro( 1, new Parametro( IUsuarioXML.CODPERFIL, codPerfil ) );
        msgEnvio.addParametro( 2, new Parametro( IUsuarioXML.USUARIO, usuarioLogin) );
        
        msgEnvio.setTipoOperacion( "update" );
        msgEnvio.setTransaccion( "SGMOUSU" );
        
        return msgEnvio;
    }
    
    /**
     * Desvincula um usuario de seu perfil
     * 
     * No EASE/SAT o usuario pode ter apenas 1 (um) perfil, entao "desassociar"
     * significa associar um usuario a um novo perfil.
     *
     * @param usuario
     * @param codPerfil
     * @return
     * @throws Exception
     */
    public SigalPeticion desvinculaUsuarioPerfil( String usuarioLogin, String codPerfil )
        throws Exception {
        // O serviço disponível é o mesmo para ambos os casos
        return vinculaUsuarioPerfil( usuarioLogin, codPerfil );
    }
    
}
