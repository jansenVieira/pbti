package br.com.pbti.ease.xml;

import to.AccountTO;
import util.CriptografiaEase;
import util.UtilDate;
import br.com.pbti.sigal.SigalPeticion;

import com.tecnocom.mediosdepago.sat.webservice.xsd.Parametro;

/**
 * Classe de configuracoes para chamadas aos servicos de Usuario do Webservice SAT
 * Elemento: <Registro>
 * Transação: SGCLUSU
 * Operação: view/select
 * Função: CL/CO
 * 
 * @author Michael Alves Lins <malins@dba.com.br>
 */
public class EaseWSUsuario {
    
    /**
     * Adciona um usuario
     * 
     * @param usuario
     * @return
     * @throws Exception
     */
    public SigalPeticion addUsuario( AccountTO usuario ) throws Exception {
        
        // Tratamento para controle de campos opicionais
        int totalParams = 14;
        boolean possuiCenttra = true;
        boolean possuiCodPerfil = true;

        if ( usuario.getCenttra() == null || usuario.getCenttra().trim().equals("") ) {
            --totalParams;
            possuiCenttra = false;
        }
        
        if ( usuario.getCodperfil() == null || usuario.getCodperfil().trim().equals("") ) {
            --totalParams;
            possuiCodPerfil = false;
        }

        SigalPeticion msgEnvio = SigalPeticion.getInstance( totalParams );

        msgEnvio.addParametro( --totalParams, new Parametro( "FUNCION", "AL" ) );

        msgEnvio.addParametro( --totalParams, new Parametro( IUsuarioXML.USUARIO, 
                usuario.getUsuario() ) );
        msgEnvio.addParametro( --totalParams, new Parametro( IUsuarioXML.OFICINA,
                usuario.getOficina() ) );
        msgEnvio.addParametro( --totalParams, new Parametro( IUsuarioXML.NOMBREUSU, 
                usuario.getNombreusu() ) );
        msgEnvio.addParametro( --totalParams, new Parametro( IUsuarioXML.PASSWORD, 
                usuario.getPassword() ) );
        msgEnvio.addParametro( --totalParams, new Parametro( IUsuarioXML.NIVSEGUSU, 
                usuario.getNivsegusu() ) );
        msgEnvio.addParametro( --totalParams, new Parametro( IUsuarioXML.CODIDIOMA, 
                usuario.getCodidioma() ) );
        msgEnvio.addParametro( --totalParams, new Parametro( IUsuarioXML.FECACTIVA, 
                usuario.getFecactivaForEASE() ) );
        msgEnvio.addParametro( --totalParams, new Parametro( IUsuarioXML.FECDESACT, 
                usuario.getFecdesactForEASE() ) );
        msgEnvio.addParametro( --totalParams, new Parametro( IUsuarioXML.TIPOUSU, 
                "I" ) ); // Usuario (I)nterno = usuarios caixa / (E)xterno = externo a caia
        msgEnvio.addParametro( --totalParams, new Parametro( IUsuarioXML.NUMMAXCON, 
                usuario.getMaxSenhasIncorretas() ) );
        msgEnvio.addParametro( --totalParams, new Parametro( IUsuarioXML.VERPAN, 
                "N" ) );
        
        
        // CAMPO OPICIONAL, alteração feita na especificação v. 5.0
        if ( possuiCenttra )
        msgEnvio.addParametro( --totalParams, new Parametro( IUsuarioXML.CENTTRA, 
                usuario.getCenttra() ) );
        
        // CAMPO OPICIONAL, alteração feita na especificação v. 5.0
        if ( possuiCodPerfil ) {
            msgEnvio.addParametro( --totalParams, new Parametro( IUsuarioXML.CODPERFIL, 
                usuario.getCodperfil() ) );
        }

        msgEnvio.setTipoOperacion( "insert" );
        msgEnvio.setTransaccion( "SGALUSU" );
        
        return msgEnvio;        
    }
    
    /**
     * Retorna os dados apenas do usuario informado
     * 
     * @param usuario
     * @return
     * @throws Exception
     */
    public SigalPeticion getUsuario( String usuarioLogin ) throws Exception {
        SigalPeticion msgEnvio = SigalPeticion.getInstance( 2 );

        msgEnvio.addParametro( 0, new Parametro( "FUNCION", "CO" ) );
        msgEnvio.addParametro( 1, new Parametro( "USUARIOC", usuarioLogin ) );

        msgEnvio.setTipoOperacion( "select" );
        msgEnvio.setTransaccion( "SGCLUSU" );

        return msgEnvio;
    }
    
    /**
     * Retorna os dados de todos os usuarios do sistema
     * @return
     * @throws Exception
     */
    public SigalPeticion getUsuarios() throws Exception {
        SigalPeticion msgEnvio = SigalPeticion.getInstance( 2 );

        msgEnvio.addParametro( 0,  new Parametro( "FUNCION", "CL" ) );        
        msgEnvio.addParametro( 1, 
                /**
                 * Somente usuarios ATIVOS
                 * N = Ativos
                 * S = Ativos e Inativos
                 */
                new Parametro( "USUBAJAC", "N" )  
                );

        msgEnvio.setTipoOperacion( "view" );
        msgEnvio.setTransaccion( "SGCLUSU" );

        return msgEnvio;
    }

    /**
     * Exclui usuario da base do EASE 
     * @param usuarioLogin
     * @return
     */
    public SigalPeticion excluirUsuario( String usuarioLogin ) throws Exception {
        SigalPeticion msgEnvio = SigalPeticion.getInstance( 2 );

        msgEnvio.addParametro( 0, new Parametro( "FUNCION", "BA" ) );
        msgEnvio.addParametro( 1, new Parametro( IUsuarioXML.USUARIO, usuarioLogin ) );

        msgEnvio.setTipoOperacion( "delete" );
        msgEnvio.setTransaccion( "SGALUSU" );

        return msgEnvio;
    }

    /**
     * Altera a senha do usuario
     * 
     * @param usuarioLogin
     * @param senhaNova
     * @return
     * @throws Exception
     */
    public SigalPeticion setNewPassword( String usuarioLogin, String senhaNova ) 
            throws Exception {
        SigalPeticion msgEnvio = SigalPeticion.getInstance( 3 );

        CriptografiaEase easeCript = CriptografiaEase.getInstance();

        msgEnvio.addParametro( 0, new Parametro( "FUNCION", "CT" ) );
        msgEnvio.addParametro( 1, new Parametro( IUsuarioXML.USUARIO, usuarioLogin ) );

        String hashSenhaNova = easeCript.hash( senhaNova.toUpperCase() );
        msgEnvio.addParametro( 2, new Parametro( 
                IUsuarioXML.PASSWORD, hashSenhaNova ) ); // Senha nova em hash

        msgEnvio.setTipoOperacion( "update" );
        msgEnvio.setTransaccion( "SGMOUSU" );
        
        return msgEnvio;
    }

    /**
     * Habilita usuario no sistema, informando sua data de desativacao maior que
     * a data corrente
     * 
     * @param usuarioLogin
     * @param dataHabilitacao
     * @return
     * @throws Exception
     */
    public SigalPeticion habilitarUsuario( String usuarioLogin
            , String dataHabilitacao ) throws Exception {
        SigalPeticion msgEnvio = SigalPeticion.getInstance( 3 );

        msgEnvio.addParametro( 0, new Parametro( "FUNCION", "HB" ) );
        msgEnvio.addParametro( 1, new Parametro( "USUARIO", usuarioLogin ) );
        msgEnvio.addParametro( 2, new Parametro( "FECDESACT", dataHabilitacao ) );

        msgEnvio.setTipoOperacion( "update" );
        msgEnvio.setTransaccion( "SGMOUSU" );

        return msgEnvio;
    }

    /**
     * Desabilita usuario no sistema, informando data D-1 em relacao a data 
     * corrente
     * 
     * @param usuarioLogin
     * @param dataHabilitacao
     * @return
     * @throws Exception
     */
    public SigalPeticion desabilitarUsuario( String usuarioLogin ) throws Exception {
        return habilitarUsuario( usuarioLogin, UtilDate.getDataDesativacaoDefault() );
    }

    /**
     * Reativa um usuario excluido logicamente na base EASE
     * 
     * @param usuarioLogin
     * @return
     * @throws Exception
     */
    public SigalPeticion reativarUsuario ( AccountTO usuario ) throws Exception {
        
        // Tratamento para controle de campos opicionais
        int totalParams = 11;
        boolean possuiCenttra = true;
        boolean possuiCodPerfil = true;

        if ( usuario.getCenttra() == null || usuario.getCenttra().trim().equals("") ) {
            --totalParams;
            possuiCenttra = false;
        }

        if ( usuario.getCodperfil() == null || usuario.getCodperfil().trim().equals("") ) {
            --totalParams;
            possuiCodPerfil = false;
        }

        SigalPeticion msgEnvio = SigalPeticion.getInstance( totalParams );

        msgEnvio.addParametro( --totalParams, new Parametro( "FUNCION", "RE" ) );

        msgEnvio.addParametro( --totalParams, new Parametro( IUsuarioXML.USUARIO,
                usuario.getUsuario() ) );
        msgEnvio.addParametro( --totalParams, new Parametro( IUsuarioXML.OFICINA,
                usuario.getOficina() ) );
        msgEnvio.addParametro( --totalParams, new Parametro( IUsuarioXML.NOMBREUSU,
                usuario.getNombreusu() ) );
        msgEnvio.addParametro( --totalParams, new Parametro( IUsuarioXML.PASSWORD,
                usuario.getPassword() ) );
        msgEnvio.addParametro( --totalParams, new Parametro( IUsuarioXML.NIVSEGUSU,
                usuario.getNivsegusu() ) );
        msgEnvio.addParametro( --totalParams, new Parametro( IUsuarioXML.CODIDIOMA,
                usuario.getCodidioma() ) );
        msgEnvio.addParametro( --totalParams, new Parametro( IUsuarioXML.FECACTIVA,
                usuario.getFecactivaForEASE() ) );
        msgEnvio.addParametro( --totalParams, new Parametro( IUsuarioXML.FECDESACT,
                usuario.getFecdesactForEASE() ) );

        // CAMPO OPICIONAL, alteração feita na especificação v. 5.0
        if ( possuiCenttra )
        msgEnvio.addParametro( --totalParams, new Parametro( IUsuarioXML.CENTTRA,
                usuario.getCenttra() ) );

        // CAMPO OPICIONAL, alteração feita na especificação v. 5.0
        if ( possuiCodPerfil ) {
            msgEnvio.addParametro( --totalParams, new Parametro( IUsuarioXML.CODPERFIL,
                usuario.getCodperfil() ) );
        }

        msgEnvio.setTipoOperacion( "update" );
        msgEnvio.setTransaccion( "SGREUSU" );

        return msgEnvio; 
    }
}