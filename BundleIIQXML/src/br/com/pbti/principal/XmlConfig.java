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
		
		
		/*nomeXml = codSistema + "-" + codPerfil + "-" + tipoUnidade;

		XmlConfig xc = new XmlConfig();

		xc.setNomeXml(nomeXml);
		// xc.s
		xc.bundle.setNomeBundle(codSistema+"_"+codPerfil);
		xc.inheritance.setNomeSistema(codSistema);
		xc.owner.setNomeOwner("spadmin");
		xc.profiles.setParametro(UG_NAME);
		xc.profiles.setApplicaton(RSS_TYPE);
		//setar o valores no selector
		xc.selector.setOperacao("OR");
		xc.selector.setOperacao("AND");
		xc.selector.setOperacaoFil1("EQ");
		xc.selector.setProperty1("sg_unde_ltco_fisica");
		xc.selector.setValue1(tipoUnidade);
		
		xc.selector.setOperacaoFil2("EQ");
		xc.selector.setProperty2("tipo_usuario");
		xc.selector.setValue2(tipoUsu);
		
		xc.selector.setOperacaoFil3("IN");
		xc.selector.setProperty3("funcao");
		xc.selector.setListString(codFuncao);
		xc.selector.setListString2(codFuncao);
		xc.selector.setListString3(codFuncao);
		
		
		xc.montaXml();*/
		
		
	}

	public static String getNomeXml() {
		return nomeXml;
	}

	public static void setNomeXml(String nomeXml) {
		XmlConfig.nomeXml = nomeXml;
	}

	
	
}
