package br.com.pbti.sistema.principal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import br.com.pbti.sistema.lerCSV.LerArquivo;
import br.com.pbti.sistema.xml.MontarXml;

public class XmlConfig extends MontarXml
	{

		// private static String nomeXml;
		private static ArrayList<Map<String, Object>> listaCodFuncao = new ArrayList<Map<String, Object>>();

//		public static void main(String[] args)
//				throws ParserConfigurationException,
//				TransformerFactoryConfigurationError, TransformerException,
//				IOException {
//
//			trataDados();
//
//			// testeXml();
//
//			// montaXml();
//		}

		public static void montaXml() throws FileNotFoundException,
				ParserConfigurationException,
				TransformerFactoryConfigurationError, TransformerException {

			MontarXml.setNomeXml(getNomeXml());

			MontarXml.cabecalho();
		}

		public static void trataDados() throws FileNotFoundException,
				ParserConfigurationException,
				TransformerFactoryConfigurationError, TransformerException {

			ArrayList<Map<String, Object>> arraySistema = new ArrayList<Map<String, Object>>();

			LerArquivo leraquivo = new LerArquivo();

			leraquivo.lerArquivoNovo();
			

			arraySistema.addAll(leraquivo.arrayDadosSistema);

			for (Map<String, Object> dadosMip : arraySistema)
				{

					String cod_sistema = "cod_sistema", permite_dup = "permite_dup", prioridade_dup = "prioridade_dup", atributoOwner = "owner";

					String sistema = dadosMip.get(cod_sistema).toString();
					String permitDupliciade = dadosMip.get(permite_dup)
							.toString();
					
					String prioridadeDup = dadosMip.get(prioridade_dup)
							.toString();
					
					String valorOwner = "";
					
					if(dadosMip.get(atributoOwner) == null)
					{
						valorOwner = "Sem dono";
					} else {
						
						valorOwner = dadosMip.get(atributoOwner).toString();
						
					}
					
					 
				
					
					
					

					setNomeXml(sistema);
					setNomeSistema(sistema);
					bundle.setNomeBundle(sistema);
					owner.setOwnerName(valorOwner);

					String permiteDupSim = "S";
					String permiteDupNao = "N";

					if (permiteDupSim.equals(permitDupliciade))
						{
							attributes.setDuplicidade("true");
						} else if (permiteDupNao.equals(permitDupliciade))
						{
							attributes.setDuplicidade("false");
						}

					String eventual = "EVENTUAL";

					if (eventual.equals(prioridadeDup))
						{
							attributes.setEventual("true");
						} else
						{

							attributes.setEventual("false");
						}

					cabecalho();

					System.out.println("##--------------" + "SISTEMA: "
							+ getNomeSistema() + "-------------------##");
					System.out
							.println("##-------------####-------------####-------------##");
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
