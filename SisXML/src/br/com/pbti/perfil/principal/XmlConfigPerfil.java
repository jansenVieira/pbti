package br.com.pbti.perfil.principal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import br.com.pbti.perfil.testMipP2.mipPorTipoUnidadePerfil;
import br.com.pbti.perfil.xml.MontarXmlPerfil;

public class XmlConfigPerfil extends MontarXmlPerfil {

	// private static String nomeXml;
	private static ArrayList<Map<String, Object>> arraylistaCodFuncao = new ArrayList<Map<String, Object>>();
	private static ArrayList<Map<String, Object>> listTipoUnidade = new ArrayList<Map<String, Object>>();
	private static ArrayList<Map<String, Object>> listaRssName = new ArrayList<Map<String, Object>>();

	static String codSistema;
	static String valorOwner;
	static String codPerfil;
	static String status;
	static String consultaNacional;
	static String atualizacoaNacional;
	static String nivelSeguranca;
	static String visaoDiretoria;
	static String funcionalidade;

//	public static void main(String[] args) throws ParserConfigurationException,
//			TransformerFactoryConfigurationError, TransformerException,
//			IOException {
//
//		trataDados();
//
//		// testeXml();
//
//		// montaXml();
//	}

	public static void montaXml() throws FileNotFoundException,
			ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException {

		MontarXmlPerfil.setNomeXml(getNomeXml());

		MontarXmlPerfil.cabecalho();
	}

	@SuppressWarnings({ "unused", "static-access" })
	public static void trataDados(String codsistema, String maior) throws FileNotFoundException,
			ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException {

		String cod_sistema = "cod_sistema", listaDados = "listaDados", cod_perfil = "cod_perfil", listRssName = "listRssName", status = "status", listaTipoUnidade = "listaTipoUnidade";

		List<String> dadosSistema = new ArrayList<String>();
		ArrayList<Map<String, Object>> arrayDadosMip = new ArrayList<Map<String, Object>>();
		ArrayList<String> arrayperfil = new ArrayList<String>();

		mipPorTipoUnidadePerfil leraquivo = new mipPorTipoUnidadePerfil();

		leraquivo.selectSistemas(codsistema, maior);
		
		
		dadosSistema.addAll(leraquivo.arraySistema);

		int contSistem = 0;
		
		try{
		
		for (String sis : dadosSistema) {

			// Runnable runnable = new mipPorTipoUnidade();
			// Thread thread = new Thread(runnable);
			// thread.start();

			leraquivo.mip04(sis);
			
			contSistem = contSistem + 1;
			
			// mipPorTipoUnidade.trataDados();

			arrayDadosMip = new ArrayList<Map<String, Object>>();
			arrayDadosMip.addAll(leraquivo.arrayDados);

			for (Map<String, Object> dadosMip : arrayDadosMip) {

				ArrayList<Map<String, Object>> arrayListaDados = new ArrayList<Map<String, Object>>();

				codSistema = dadosMip.get(cod_sistema).toString();
				arrayListaDados
						.addAll((Collection<? extends Map<String, Object>>) dadosMip
								.get(listaDados));

				

				int contPerf = 0;
				
				for (Map<String, Object> dadosListDados : arrayListaDados) {

					listaRssName = new ArrayList<Map<String, Object>>();
					listaRssName
							.addAll((Collection<? extends Map<String, Object>>) dadosListDados
									.get(listRssName));

					// Object rssName = dadosListDados.get(listRssName);

					codPerfil = dadosListDados.get(cod_perfil).toString();

					for (Map<String, Object> dadosListaRssName : listaRssName) {
						XmlConfigPerfil.status = dadosListaRssName.get(status)
								.toString();

						atualizacoaNacional = dadosListaRssName.get(
								"atualizacao_nacional").toString();
						consultaNacional = dadosListaRssName.get(
								"consulta_nacional").toString();
						nivelSeguranca = dadosListaRssName.get(
								"nivel_seguranca").toString();
						
						visaoDiretoria = dadosListaRssName.get(
								"visao_diretoria").toString();

						funcionalidade = dadosListaRssName
								.get("funcionalidade").toString();

						if (nivelSeguranca.isEmpty()) {
							attributes.setNivelSeguranca("0");

						} else {
							String[] separNivel = nivelSeguranca.split(",");

							if(separNivel.length > 1)
							{
								attributes.setNivelSeguranca(separNivel[1]);
							} else {
								attributes.setNivelSeguranca(separNivel[0]);
							}
							
							
//							if(separNivel[1].isEmpty())
////							{
////								attributes.setNivelSeguranca("0");
////							} else {
//								attributes.setNivelSeguranca(separNivel[1]);
////							}
							
							
						}

						arraylistaCodFuncao = new ArrayList<Map<String, Object>>();
						arraylistaCodFuncao
								.addAll((Collection<? extends Map<String, Object>>) dadosListDados
										.get(listaTipoUnidade));

						valorOwner = dadosListaRssName.get("owner").toString();
						dadosXML();

						cabecalho();

						contPerf = contPerf +1;
						
//						System.out.println("## Perfil: " +contPerf+"-"+ codPerfil);

					}
				}

			}

			// dadosXML();

			// setListaCodFuncao((ArrayList<Map<String, Object>>)
			// dadosMip.get("cod_funcao"));
			// setListaCodFuncao((ArrayList<Map<String, Object>>)
			// dadosMip.get("listTipo_unidade"));

		}
		}catch (Exception e ) {
			e.printStackTrace();
			System.out.println(e);
		}

	}

	public static void dadosXML() {

		setNomeXml(codSistema + "_" + codPerfil);
		setNomeSistema(codSistema);
		bundle.setNomeBundle(codSistema + "_" + codPerfil);
		inheritance.setInheritanceName(codSistema);
		owner.setOwnerName(valorOwner);
		// profiles.setApplicatonRefName("TESTE RSSTYPE");
		// profiles.setConstraintsListString("TESTE UGNAME");

		String statusAutomatico = "A";
		String statusSobDemanda = "D";

		if (consultaNacional.equals("S")) {
			attributes.setConsultaNacional("true");
		} else if (!consultaNacional.equals("S")) {
			attributes.setConsultaNacional("false");
		}

		if (atualizacoaNacional.equals("S")) {
			attributes.setAtualizacaoNacional("true");
		} else if (!atualizacoaNacional.equals("S")) {
			attributes.setAtualizacaoNacional("false");
		}
		
		if (visaoDiretoria.equals("S")) {
			attributes.setVisaoDiretoria("true");
		} else if (!visaoDiretoria.equals("S")) {
			attributes.setVisaoDiretoria("false");
		}

		if (statusAutomatico.equals(status)) {
			attributes.setStatus("true");
		} else if (statusSobDemanda.equals(status)) {
			attributes.setStatus("false");
		}

		if (funcionalidade.isEmpty()) {
			attributes.setFuncionalidade(" ");
		} else {
			attributes.setFuncionalidade(funcionalidade);
		}

		String sistemaPerfil = codSistema.substring(2) + "/" + codPerfil;

		profiles.setSistemaPerfilSinav(sistemaPerfil);

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

	public static ArrayList<Map<String, Object>> getListTipoUnidade() {
		return listTipoUnidade;
	}

	public static ArrayList<Map<String, Object>> getArraylistaCodFuncao() {
		return arraylistaCodFuncao;
	}

	public static void setArraylistaCodFuncao(
			ArrayList<Map<String, Object>> arraylistaCodFuncao) {
		XmlConfigPerfil.arraylistaCodFuncao = arraylistaCodFuncao;

	}

	public static void setListTipoUnidade(
			ArrayList<Map<String, Object>> listTipoUnidade) {
		XmlConfigPerfil.listTipoUnidade = listTipoUnidade;
	}

	public static ArrayList<Map<String, Object>> getListaRssName() {
		return listaRssName;
	}

	public static void setListaRssName(
			ArrayList<Map<String, Object>> listaRssName) {
		XmlConfigPerfil.listaRssName = listaRssName;
	}
}
