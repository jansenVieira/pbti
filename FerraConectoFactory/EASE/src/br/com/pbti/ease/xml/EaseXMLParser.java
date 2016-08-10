package br.com.pbti.ease.xml;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Classe para manipulação dos retornos XML dos serviços do webservice do EASE
 * 
 * @author Michael Alves Lins <malins@dba.com.br>
 */
public class EaseXMLParser {

    private Document parsedXML;
    private java.util.HashMap<String,EaseXMLNode> nodes;

    public EaseXMLParser ( String xml2Parse ) 
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream( new StringReader( xml2Parse ) );
        parsedXML = db.parse( is );
        nodes = new HashMap<String, EaseXMLNode>();
    }

    /**
     * Recupera o nodeList de elementos para o node informado
     * 
     * @param nodeName
     * @return
     */
    private NodeList loadNode( String nodeName ) { 
        return parsedXML.getElementsByTagName( nodeName );
    }

    /**
     * Verifica se existe outro node do tipo informado e o retorna
     * Caso nao encontre, retorna <null>
     * 
     * @param nodeName
     * @return
     */
    public EaseXMLNode getNext( String nodeName ) {
        EaseXMLNode node = null;

        if ( hasNode( nodeName ) ) {
            node = (EaseXMLNode) nodes.get( nodeName );
            if ( !node.hasNext() )
                node = null;
        }
        return node;
    }

    /**
     * Verifica se existe node do tipo informado
     * 
     * @param nodeName
     * @return
     */
    public boolean hasNode( String nodeName ) {
        if ( !nodes.containsKey( nodeName ) ) {
            NodeList nodeList = loadNode( nodeName );
            if ( nodeList != null )
                nodes.put( nodeName , new EaseXMLNode( nodeList ) );
        }

        return nodes.containsKey( nodeName );
    }
}