package br.com.pbti.ease.xml;

/**
 * Constantes para manipulação do retorno XML dos serviços de Usuário
 * Elemento: <Registro>
 * Transação: SGCLUSU
 * Operação: view/select
 * Função: CL/CO
 * 
 * @author Michael Alves Lins <malins@dba.com.br>
 */
public interface IUsuarioXML {
    // Os campos abaixo não foram identificados, aguardando dicionario de dados
    public static final String _INDMASDATOS       = "INDMASDATOS";
    public static final String _CONTCUR           = "CONTCUR";
    /**
     * Tipo de elemento
     */
    public static final String XML_ELEMENT = "Registro";
    /**
     * Tipo do Usuario
     */
    public static final String TIPOUSU = "TIPOUSU";
    /**
     * Maximo de senhas incorretas
     */
    public static final String NUMMAXCON = "NUMMAXCON";
    /**
     * Se o usuario pode visualizar ou nao o numero PAN dos cartoes
     */
    public static final String VERPAN = "VERPAN";
    /**
     * Estacao de trabalho
     */
    public static final String CENTTRA = "CENTTRA";
    /**
     * Codigo do idioma (PO)
     */
    public static final String CODIDIOMA = "CODIDIOMA";
    /**
     * Codigo do perfil para vincular usuario e perfil
     */
//    public static final String CODPERFILAJENO = "CODPERFAJENO";
    /**
     * Codigo do perfil para preencher usuario do retorno de consulta de usuarios
     */
    public static final String CODPERFIL = "CODPERFIL";
    /**
     * Numero de tentativas
     */
    public static final String CONTADOR = "CONTADOR";
    /**
     * Agencia
     */
    public static final String OFICINA = "OFICINA";
    /**
     * Data de inclusao
     */
    public static final String FECALTA = "FECALTA";
    /**
     * Data ultima modificacao
     */
    public static final String FECULTMOD = "FECULTMOD";
    /**
     * Nivel de seguranca do usuario
     */
    public static final String NIVSEGUSU = "NIVSEGUSU";
    /**
     * Descricao do perfil
     */
    public static final String DESPERFIL = "DESPERFIL";
    /**
     * Data de ativacao
     */
    public static final String FECACTIVA = "FECACTIVA";
    /**
     * Data de desativacao
     */
    public static final String FECDESACT = "FECDESACT";
    /**
     * Data de inicio da vigencia
     */
    public static final String FECINICON = "FECINICON";
    /**
     * Data do fim da vigencia do acesso
     */
    public static final String FECFINCON = "FECFINCON";
    /**
     * Data de exclusao do usuario
     */
    public static final String FECBAJA = "FECBAJA";
    /**
     * Nome completo do usuario
     */
    public static final String NOMBREUSU = "NOMBREUSU";
    /**
     * Login
     */
    public static final String USUARIO = "USUARIO";
    /**
     * Senha de acesso
     */
    public static final String PASSWORD = "PASSWORDD";
    /**
     * USUBAJAC
     */
    public static final String USUBAJAC = "USUBAJAC";
    
}