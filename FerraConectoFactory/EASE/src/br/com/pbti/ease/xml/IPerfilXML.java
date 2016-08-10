package br.com.pbti.ease.xml;

/**
 * Constantes para manipulação do retorno XML dos serviços de Perfil
 * Elemento: <Registro> 
 * Transação: SGCLPAJ/SGCOPAJ
 * Operação: view/select
 * 
 * @author EaseWSTests Alves Lins <malins@dba.com.br>
 */
public interface IPerfilXML {
    public static final String XML_ELEMENT        = "Registro";
    public static final String CODIGO             = "CODPERFILAJENO";
    public static final String DESCRICAO          = "DESPERFILAJENO";
    //public static final String MODIFICAVEL        = "INDREGMOD";
//    public static final String VISIVEL            = "INDREGVIS";
//    public static final String VIGENCIA_SENHA     = "VIGCONPER";
//    public static final String DATA_EXCLUSAO      = "FECBAJA";
//    public static final String DATA_INCLUSAO      = "FECALTA";
//    public static final String MAX_SENHA_ERRADA   = "NUMMAXCON";
//    public static final String DESCRICAO_REDUZIDA = "DESPERRED";
//    public static final String ULTIMO_ACESSO      = "CONTCUR";

    // Os campos abaixo não foram identificados, aguardando dicionario de dados
//    public static final String _PANTPAG           = "PANTPAG";
//    public static final String _INDMASDATOS       = "INDMASDATOS";
}