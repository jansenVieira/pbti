package br.com.pbti.principal;

import java.io.FileNotFoundException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import br.com.pbti.dto.TransObjetos;

public class Executavel {
	
	public static  TransObjetos transObjetos = new TransObjetos();
	
	public static void main(String[] args) throws FileNotFoundException,
			ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		
		transObjetos.abrirCSV();
		
		
	}
	
}
