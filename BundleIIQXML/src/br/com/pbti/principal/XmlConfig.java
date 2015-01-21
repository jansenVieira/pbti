package br.com.pbti.principal;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import br.com.pbti.xml.MontarXml;

public class XmlConfig extends MontarXml {

	private static String nomeXml;
	
	
	public static void main(String[] args) throws ParserConfigurationException,
			TransformerFactoryConfigurationError, TransformerException,
			IOException {

//		testeXml();

		montaXml();
	}

	public static void montaXml() throws FileNotFoundException,
			ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException {
		
		MontarXml.setNomeXml(getNomeXml());
		
		MontarXml.cabecalho();
	}

	public static void testeXml() {
		bundle.setNomeBundle("Abas");
		bundle.setType("bestaquadrada");

		inheritance.setNomeSistema("Nadaadadsdas");

		owner.setNomeOwner("spadmin");

		selector.setOperacao("Nadaadadsdas");
		selector.setOperacaoFil1("Nadaadadsdas");
		selector.setProperty1("Nadaadadsdas");
		// String value1 = "GESET";
		// String operacaoFil2 = "EQ";
		// String property2 ="tipo_usuario";
		// String value2 = "C";
		// String operacaoFil3 = "IN";
		// String property3 ="funcao";
		// String value3 = "";

		profiles.setApplicaton("Nadaadadsdas");
		profiles.setParametro("Nadaadadsdas");
	}

	public static String getNomeXml() {
		return nomeXml;
	}

	public static void setNomeXml(String nomeXml) {
		XmlConfig.nomeXml = nomeXml;
	}

	
	
}
