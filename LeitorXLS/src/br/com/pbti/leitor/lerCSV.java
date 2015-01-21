package br.com.pbti.leitor;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import br.com.pbti.principal.XmlConfig;

public class lerCSV {
	
	
 	static String codSistema;
	static String codPerfil;
	static String tipoUnidade;
	static String funcao;
	static String codFuncao;
	static String codUnidade;
	static String tipoUsu;
	static String codCargo;
	static String indUnidade;
	
	
	
	
	  public static void main(String[] args) throws FileNotFoundException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {

		    String linha;	
		  
		 
		  
			ArrayList<String> listaLinhas = new ArrayList<String>();
			
		  
		  Scanner scanner = new Scanner(new FileReader("C://Users//tic//Desktop//ler xsl//MIP_06-SIAADTeste.csv")).useDelimiter(",");
		 
		  while (scanner.hasNext()) {
			
			linha = scanner.nextLine();
			listaLinhas.add(linha);
		  }
		  
		  
	  for (String teste :listaLinhas)
	  {
		  ArrayList<String> testePalavras = new ArrayList<String>();
		  
		  for (String separaCampo : teste.split(",")) 
		  {
			 
			  testePalavras.add(separaCampo.replace("\"", ""));
		  }

		  codSistema = testePalavras.get(0);
		  codPerfil  = testePalavras.get(1);
		  tipoUnidade  = testePalavras.get(2);
		  funcao  = testePalavras.get(3);
		  codFuncao  = testePalavras.get(4);
		  codUnidade  = testePalavras.get(5);
		  tipoUsu  = testePalavras.get(6);
		  codCargo  = testePalavras.get(7);
		  indUnidade  = testePalavras.get(8);
			  
			  chamaMerda();

	  }
	  
	
	  }
	  
	  
	  public static void chamaMerda() throws FileNotFoundException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException
	  {
		  
		  String nomeXml; 
		  
		  nomeXml = codSistema+"-"+codPerfil+"-"+tipoUnidade;
		  
		  XmlConfig xc = new XmlConfig();
		  
		  xc.setNomeXml(nomeXml);
//		  xc.s
		  xc.bundle.setNomeBundle(codSistema);
		  
		  xc.montaXml();
		  
		  
		  
		  
	  }
	  
}
