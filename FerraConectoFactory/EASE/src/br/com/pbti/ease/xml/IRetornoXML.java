package br.com.pbti.ease.xml;

/**
 * Constantes para manipulação do retorno XML
 * Elemento: <Retorno>
 * Transação: Todas
 * Operação: Todas
 * Função: Todas
 * 
 * @author EaseWSTests Alves Lins <malins@dba.com.br>
 */
public interface IRetornoXML {
    public static final String XML_ELEMENT = "Retorno";
    public static final String CODIGO = "CODIGO_RETORNO";
    public static final String DESCRICAO = "DESCRIPCION_RETORNO";
    public static final String TEMPO_EXECUCAO = "TIEMPO_EJECUCION";
    public static final String RETORNO_OK = "000";
    public static final String RETORNO_ERRO = "200";
    public static final String MSG_USUARIO_SENHA_INCORRTA = "MPE0269";
}