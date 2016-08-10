package br.com.pbti.ease.xml;

import framework.XSA_Framework;

/**
 * Classe para manipular os dados de retorno do XML apos execucao dos servicos
 * 
 * @author Michael Alves Lins <malins@dba.com.br>
 */
public class EaseWSRetorno {

    private String xml;
    private String codigo;
    private String mensagem;
    private String tempoExecucao;
    private boolean erro;
    
    @SuppressWarnings("unused")
    private EaseWSRetorno() {}
    /**
     * Extrai os dados do XML de retorno da execuao do servico
     * 
     * @param retornoXML XML de retorno da execucao do servico 
     */
    public EaseWSRetorno ( String retornoXML ) throws Exception {
        EaseXMLParser parser = new EaseXMLParser( retornoXML );
        EaseXMLNode node = parser.getNext( IRetornoXML.XML_ELEMENT );
        
        // XML de retorno da execucao do servico
        xml = retornoXML;
        // Codigo de retorno
        codigo = node.getString( IRetornoXML.CODIGO );
        // Verifica se houve erro no processamento
        erro = !codigo.equals( IRetornoXML.RETORNO_OK );
        // Mensagem de sucesso ou erro
        mensagem = node.getString( IRetornoXML.DESCRICAO );
        // Tempo de execucao do servico remoto
        tempoExecucao = node.getString( IRetornoXML.TEMPO_EXECUCAO );
    }

    /**
     * Interpreta codigos de retorno conforme padrao Control-SA
     * @return
     */
    public int getControlSA_RC() {
        int rc = XSA_Framework.XSA_RC_OK;
        if ( erro )
            rc = XSA_Framework.XSA_RC_ERROR;
        return rc;
    }
    
    /**
     * XML completo de retorno da execuao do servico remoto
     * 
     * @return the xml
     */
    public String getXml() {
        return xml;
    }

    /**
     * Codigo de retorno apos execucao do servico
     * 
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Mensagem de retorno apos execucao do servico
     * 
     * @return the mensagem
     */
    public String getMensagem() {
        return mensagem;
    } 
    
    /**
     * Tempo de execucao do servico remoto
     * @return
     */
    public String getTempoExecucao() {
        return tempoExecucao;
    }
    
    /**
     * Sinaliza se houve erro ou nao no processamento
     * @return
     */
    public boolean possuiErro() {
        return erro;
    }
}