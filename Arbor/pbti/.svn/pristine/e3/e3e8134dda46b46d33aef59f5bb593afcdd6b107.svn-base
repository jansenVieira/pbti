package br.com.pbti.principal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import br.com.pbti.lerCSV.LerArquivo;
import br.com.pbti.xml.MontarXml;

public class XmlConfig extends MontarXml {

	// private static String nomeXml;
	private static ArrayList<Map<String, Object>> listaCodFuncao = new ArrayList<Map<String, Object>>();

	public static void main(String[] args) throws ParserConfigurationException,
			TransformerFactoryConfigurationError, TransformerException,
			IOException {

		trataDados();

		// testeXml();

		// montaXml();
	}

	public static void montaXml() throws FileNotFoundException,
			ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException {

		MontarXml.setNomeXml(getNomeXml());

		MontarXml.cabecalho();
	}

	@SuppressWarnings({ "unused", "unchecked" })
	public static void trataDados() throws FileNotFoundException,
			ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException {

		String COD_SISTEMA = "COD_SISTEMA", COD_PERFIL = "COD_PERFIL", TIPO_UNIDADE = "TIPO_UNIDADE", FUNCAO = "FUNCAO", 
				COD_FUNCAO = "COD_FUNCAO", COD_UNIDADE = "COD_UNIDADE", TIPO_USU = "TIPO_USU", COD_CARGO = "COD_CARGO", 
				IND_UNIDADE = "IND_UNIDADE", UG_NAME = "UG_NAME", RSS_TYPE = "RSS_TYPE", LIST_PERFIL = "LIST_PERFIL", 
				PERMITE_DUP = "PERMITE_DUP", STATUS = "STATUS";

		String MIP04 = "MIP04";
		String MIP06 = "MIP06";

		ArrayList<Map<String, Object>> arrayMapMip04Mip06 = new ArrayList<Map<String, Object>>();
		ArrayList<String> arrayperfil = new ArrayList<String>();

		LerArquivo leraquivo = new LerArquivo();

		leraquivo.nomeMip();

		arrayMapMip04Mip06.addAll(leraquivo.arrayMapMip04Mip06);

		for (Map<String, Object> nomeMip : arrayMapMip04Mip06) {
			String NomeArquivoMIP04 = nomeMip.get(MIP04).toString();
			String NomeArquivoMIP06 = nomeMip.get(MIP06).toString();

			leraquivo.init(NomeArquivoMIP04, NomeArquivoMIP06);
			
			
			ArrayList<Map<String, Object>> listaMip = leraquivo.arrayMipFinal;

			for (Map<String, Object> dadosMip : listaMip) {

				setNomeXml(dadosMip.get(COD_SISTEMA).toString() + "_"
						+ dadosMip.get(COD_PERFIL).toString());
				setNomeSistema(dadosMip.get(COD_SISTEMA).toString());
				bundle.setNomeBundle(dadosMip.get(COD_SISTEMA).toString() + "_"
						+ dadosMip.get(COD_PERFIL).toString());
				inheritance.setInheritanceName(dadosMip.get(COD_SISTEMA)
						.toString());
				owner.setOwnerName("spadmin");
				profiles.setApplicatonRefName(dadosMip.get(RSS_TYPE).toString());
				profiles.setConstraintsListString(dadosMip.get(UG_NAME)
						.toString());
				
				
				String statusAutomatico = "A";
				String statusSobDemanda = "D";
				
				if(statusAutomatico.equals(dadosMip.get(STATUS)))
				{
					attributes.setStatus("true");
				} else if(statusSobDemanda.equals(dadosMip.get(STATUS)))
				{
					attributes.setStatus("false");
				}

				setListaCodFuncao((ArrayList<Map<String, Object>>) dadosMip
						.get(LIST_PERFIL));

				cabecalho();
				
				arrayperfil.add(getNomeXml());
			}
			
			System.out.println("##--------------"+"SISTEMA: "+getNomeSistema()+"-------------------##");
			for(String perfil: arrayperfil)
			System.out.println("Perfil: "+perfil);
			System.out.println("##-------------####-------------####-------------##");
		}
	}

	public static void testeXml() {
		// bundle.setNomeBundle("Abas");
		// bundle.setType("bestaquadrada");
		//
		// inheritance.setNomeSistema("Nadaadadsdas");
		//
		// owner.setNomeOwner("spadmin");
		//
		// selector.setOperacao("Nadaadadsdas");
		// selector.setOperacaoFil1("Nadaadadsdas");
		// selector.setProperty1("Nadaadadsdas");
		// String value1 = "GESET";
		// String operacaoFil2 = "EQ";
		// String property2 ="tipo_usuario";
		// String value2 = "C";
		// String operacaoFil3 = "IN";
		// String property3 ="funcao";
		// String value3 = "";

		// profiles.setApplicaton("Nadaadadsdas");
		// profiles.setParametro("Nadaadadsdas");
		//
		// setNomeXml("TESTE");
		// bundle.setNomeBundle("ADM-ADM");
		// // inheritance.setNomeSistema("SIADD");
		// // owner.setNomeOwner("spadmin");
		// // profiles.setParametro("LDAPSUN");
		// // profiles.setApplicaton("LDAPSUN");
		// //setar o valores no selector
		// selector.setOperacaoCompositerOr("OR");
		// selector.setOperacao("OR");
		// selector.setOperacao("AND");
		// selector.setOperacaoFil1("EQ");
		// selector.setProperty1("sg_unde_ltco_fisica");
		// selector.setValue1("GSET");
		//
		// selector.setOperacaoFil2("EQ");
		// selector.setProperty2("tipo_usuario");
		// selector.setValue2("C");
		//
		// selector.setOperacaoFil3("IN");
		// selector.setProperty3("funcao");
		// selector.setListString("474");
		// selector.setListString2("566");
		// selector.setListString3("599");
		// }
	}

	// public static String getNomeXml() {
	// return nomeXml;
	// }

	// public static void setNomeXml(String nomeXml) {
	// XmlConfig.nomeXml = nomeXml;
	// }

	public static ArrayList<Map<String, Object>> getListaCodFuncao() {
		return listaCodFuncao;
	}

	public static void setListaCodFuncao(
			ArrayList<Map<String, Object>> listaCodFuncao) {
		XmlConfig.listaCodFuncao = listaCodFuncao;
	}
}
