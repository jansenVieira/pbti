package br.com.pbti.principal;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import br.com.pbti.dto.Bundle;
import br.com.pbti.dto.Inheritance;
import br.com.pbti.dto.Owner;
import br.com.pbti.dto.Profiles;
import br.com.pbti.dto.Selector;
import br.com.pbti.xml.MontarXml;


public class Principal extends MontarXml{
	
	public static void main(String[] args) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException, IOException{
		
		testeXml();
		
		MontarXml.cabecalho();
	}
	
	
	public static void testeXml()
	{
		Bundle bundle = new Bundle();
		bundle.setNomeBundle("SIABM_TESTE3");
		bundle.setType("PERFIL");
		
		Inheritance inheritance = new Inheritance();
		inheritance.setNomeSistema("SIABM");
		
		Owner owner = new Owner();
		owner.setNomeOwner("spadmin");
		
		Selector selector = new Selector();
		
		selector.setOperacao("AND");
		selector.setOperacaoFil1("EQ");
		selector.setProperty1("sg_unde_ltco");
//		String value1 = "GESET";
//		String operacaoFil2 = "EQ";
//		String property2 ="tipo_usuario";
//		String value2 = "C";
//		String operacaoFil3 = "IN";
//		String property3 ="funcao";
//		String value3 = "";
		
		Profiles profile = new Profiles();
		profile.setApplicaton("LDAPSUN");
		profile.setParametro("GESTOR");
		
	
	}
	
}
