package br.com.pbti.principal;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import br.com.pbti.xml.MontarXml;


public class Principal extends MontarXml{
	
	public static void main(String[] args) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException{
		
		MontarXml.cabecalho();
		
	}

}
