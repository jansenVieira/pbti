package br.com.pbti.ease.xml;

import java.util.Date;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import util.UtilDate;

/**
 * Classe de apoio para controle dos nodes com campos/valores
 * 
 * @author EaseWSTests Alves Lins <malins@dba.com.br
 */
public class EaseXMLNode {

    private int posCurrentNode;
    private NodeList nodeList;
    
    public EaseXMLNode( NodeList nodeList ) {
        this.nodeList = nodeList;
        posCurrentNode = -1; // Inicializa antes do cursor
    }

    /**
     * Retorno o No atual
     * 
     * @return
     */
    private Element getCurrentNode() {
        return (Element) nodeList.item( posCurrentNode );
    }
    
    /**
     * Retorna o elemento referente ao Campo procurado
     * Os campos sao unicos para o mesmo pai (no EASE), recuperar o primeiro.
     * 
     * @param campo
     * @return
     */
    private Element getCampo ( String campo ) {
        NodeList campoNode = getCurrentNode().getElementsByTagName( campo );
        return (Element) campoNode.item(0);
    }
    
    /**
     * Verifica se existe outro Node Pai com campos
     * 
     * @return
     */
    public boolean hasNext() {
        posCurrentNode++;
        return posCurrentNode < nodeList.getLength();
    }
    
    /**
     * Recupera valor String para node de propriedade dentro do Node Pai
     * Element parent = getNextElement( "Retorno" ); 
     * Ex: getFieldValue( "CODRETORNO", parent ); // Returns "12" 
     *    <Retorno>
     *         <CODRETORNO>
     *         </CODRETORNO>
     *         <DEMAIS_CAMPOS>
     *         </DEMAIS_CAMPOS>
     *     </Retorno>
     * @param campo
     * @return
     */
    private String getValorCampo( String nomeCampo ) {
        CharacterData valorData = (CharacterData) getCampo( nomeCampo ).getFirstChild();
        String valorString = "";
        if ( valorData != null && !valorData.toString().trim().equals("") )
            valorString = valorData.getData();
        return valorString;
    }
    
    /**
     * Recupera valor String para node de propriedade dentro do Node Pai
     * Element parent = getNextElement( "Retorno" ); 
     * Ex: getFieldValue( "CODRETORNO", parent ); // Returns "12" 
     *    <Retorno>
     *         <CODRETORNO>
     *         </CODRETORNO>
     *         <DEMAIS_CAMPOS>
     *         </DEMAIS_CAMPOS>
     *     </Retorno>
     * @param campo
     * @return
     */
    public String getString( String nomeCampo ) {
        return getValorCampo( nomeCampo );
    }
    
    /**
     * Recupera valor int para node de propriedade dentro do Node Pai
     * Element parent = getNextElement( "Retorno" ); 
     * Ex: getFieldValue( "CODRETORNO", parent ); // Returns "12" 
     *    <Retorno>
     *         <CODRETORNO>
     *         </CODRETORNO>
     *         <DEMAIS_CAMPOS>
     *         </DEMAIS_CAMPOS>
     *     </Retorno>
     * @param campo
     * @return
     */
    public int getInt( String nomeCampo ) {
        return Integer.parseInt( getValorCampo( nomeCampo ) );
    }
    
    /**
     * Recupera valor Date para node de propriedade dentro do Node Pai
     * Element parent = getNextElement( "Retorno" ); 
     * Ex: getFieldValue( "CODRETORNO", parent ); // Returns "12" 
     *    <Retorno>
     *         <CODRETORNO>
     *         </CODRETORNO>
     *         <DEMAIS_CAMPOS>
     *         </DEMAIS_CAMPOS>
     *     </Retorno>
     * @param campo
     * @return
     */
    public Date getDate( String nomeCampo ) {
        Date date = null;
        String dateString = getValorCampo( nomeCampo );
        if ( dateString != null && !dateString.equals("") )
            date = UtilDate.dateFromString( dateString , UtilDate.PATTERN_DATE_EASE );
        return date;
    }
    
    /**
     * Recupera valor String para node de propriedade dentro do Node Pai
     * Element parent = getNextElement( "Retorno" ); 
     * Ex: getFieldValue( "CODRETORNO", parent ); // Returns "12" 
     *    <Retorno>
     *         <CODRETORNO>12</CODRETORNO>
     *         <DEMAIS_CAMPOS>
     *         </DEMAIS_CAMPOS>
     *     </Retorno>
     * @param campo
     * @return
     */
    public String getDebugValue( String nomeCampo ) {
        Element campo = getCampo( nomeCampo );
        CharacterData valorData = (CharacterData) campo.getFirstChild();
        String valorString = "";
        if ( valorData != null )
            valorString = valorData.getData();
        return campo.getNodeName() +": "+ valorString;
    }
}