package br.com.pbti.lerCSV;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import br.com.pbti.principal.XmlConfig;
import br.com.pbti.xml.MontarXml;

public class lerCSV extends MontarXml{

	/*
	

	static String codSistema;
	static String codPerfil;
	static String tipoUnidade;
	static String funcao;
	static String codFuncao;
	static String codUnidade;
	static String tipoUsu;
	static String codCargo;
	static String indUnidade;

	static String COD_SISTEMA;
	static String COD_PERFIL;
	static String UG_NAME;
	static String RSS_NAME;
	static String FUNCIONALIDADE;
	static String STATUS;
	static String COD_RESTRICAO;
	static String QUANTIDADE;
	static String RSS_TYPE;

	static String linha;


	*/
	
	//		setNomeXml("TESTE");
//	bundle.setNomeBundle("ADM-ADM");
//	inheritance.setNomeSistema("SIADD");
//	owner.setNomeOwner("spadmin");
//	profiles.setParametro("LDAPSUN");
//	profiles.setApplicaton("LDAPSUN");
//	//setar o valores no selector
//	selector.setOperacaoCompositerOr("OR");
//	selector.setOperacao("OR");
//	selector.setOperacao("AND");
//	selector.setOperacaoFil1("EQ");
//	selector.setProperty1("sg_unde_ltco_fisica");
//	selector.setValue1("GSET");
//	
//	selector.setOperacaoFil2("EQ");
//	selector.setProperty2("tipo_usuario");
//	selector.setValue2("C");
//	
//	selector.setOperacaoFil3("IN");
//	selector.setProperty3("funcao");
//	selector.setListString("474");
//	selector.setListString2("566");
//	selector.setListString3("599");
	
	
//public static void testeCriarXML() throws FileNotFoundException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
	
	
	
		
	
	
//	
//	@SuppressWarnings("resource")
//	Scanner scanner = new Scanner(
//			new FileReader(
//					"C://Users//tic//Desktop//ler xsl//MIP_06-SIAADTeste.csv"))
//			.useDelimiter(",");
//
//	while (scanner.hasNext()) {
//
//		linha = scanner.nextLine();
//		listaLinhas.add(linha);
//	}
//
//	
//
//	for (String receberListaLinhas : listaLinhas) {
//		// zera valor do array
//		ArrayList<String> recebePalavraSeparados = new ArrayList<String>();
//		// receber o valor do receberListaLinhas
//		
//		
//		selector.filtroAndArray.add(receberListaLinhas);
//		
////		for (String separaCampo : receberListaLinhas.split(",")) {
////
////			recebePalavraSeparados.add(separaCampo.replace("\"", ""));
////		}
////
////		codSistema = recebePalavraSeparados.get(0);
////		codPerfil = recebePalavraSeparados.get(1);
////		tipoUnidade = recebePalavraSeparados.get(2);
////		funcao = recebePalavraSeparados.get(3);
////		codFuncao = recebePalavraSeparados.get(4);
////		codUnidade = recebePalavraSeparados.get(5);
////		tipoUsu = recebePalavraSeparados.get(6);
////		codCargo = recebePalavraSeparados.get(7);
////		indUnidade = recebePalavraSeparados.get(8);
//
////		separa2planilha();
//
//		// chamaMetodo();
//		
////		break;
//
//	}
//
//}

//public static void separa2planilha() throws FileNotFoundException,
//		ParserConfigurationException, TransformerFactoryConfigurationError,
//		TransformerException {
//
//	for (String receberListaLinhas : listaLinhas2) {
//		// zera valor do array
//		ArrayList<String> recebePalavraSeparados = new ArrayList<String>();
//		// receber o valor do receberListaLinhas
//		for (String separaCampo : receberListaLinhas.split("\"")) {
//
//			if (!separaCampo.equals(",")) {
//
//				recebePalavraSeparados.add(separaCampo);
//			}
//
//			if (recebePalavraSeparados.get(0).equals("")) {
//				recebePalavraSeparados.remove(0);
//			}
//		}
//
//		COD_SISTEMA = recebePalavraSeparados.get(0);
//		COD_PERFIL = recebePalavraSeparados.get(1);
//		UG_NAME = recebePalavraSeparados.get(2);
//		RSS_NAME = recebePalavraSeparados.get(3);
//		FUNCIONALIDADE = recebePalavraSeparados.get(4);
//		STATUS = recebePalavraSeparados.get(5);
//		COD_RESTRICAO = recebePalavraSeparados.get(6);
//		QUANTIDADE = recebePalavraSeparados.get(7);
//		RSS_TYPE = recebePalavraSeparados.get(8);
//
////		if(codPerfil.equals(COD_PERFIL))
////		{
////			chamaMetodo();
//			break;
			
//		}
		
		
		//if (codPerfil == COD_PERFIL) {
			
		//}

//	}
//}
	

	
public static void main(String[] args) throws FileNotFoundException {
	
	lerMIP04();
	
}	
	
	
	public static String coSistemaMIP04;
	public static String coPerfilMIP04;
	public static String ugNameMIP04;
	public static String rssTypeMIP04;
	
	public static String coSistemaMIP06;
	public static String coPerfilMIP06;
	public static String tipoUnidadeMIP06;
	public static String funcaoMIP06;
	public static String codFuncaoMPI06;
	public static String codUnidadeMIP06;
	public static String tipoUsuarioMIP06;
	public static String codCargoMIP06;
	public static String indUnidadeMIP06;
	
	

	
	static ArrayList<String> arrayMIP06 = new ArrayList<String>();
	static ArrayList<String> arrayMIP04 = new ArrayList<String>();

	
	@SuppressWarnings("resource")
	public static void lerMIP04() throws FileNotFoundException
	{
		Scanner scannerMip04 = new Scanner(new FileReader("C://Users//leonardo.moura//Downloads//PBTI//Caixa//SailPoint//MIP_04-SIAAD_TESTE.csv")).useDelimiter(",");

		while (scannerMip04.hasNext()) 
		{
			arrayMIP04.add(scannerMip04.nextLine());
		}
		
		arrayMIP04.remove(0);
		
		for(String dadosMIP04 :arrayMIP04)
		{
			ArrayList<String> recebePalavraSeparados = new ArrayList<String>();
			// receber o valor do receberListaLinhas
			for (String separaCampo : dadosMIP04.split("\"")) 
			{

				if (!separaCampo.equals(",")) 
				{

					recebePalavraSeparados.add(separaCampo);
				}

				if (recebePalavraSeparados.get(0).equals("")) 
				{
					recebePalavraSeparados.remove(0);
				}
			}
			
			
			coSistemaMIP04 = recebePalavraSeparados.get(0);
			coPerfilMIP04 = recebePalavraSeparados.get(1);
			ugNameMIP04 = recebePalavraSeparados.get(2);
			rssTypeMIP04 = recebePalavraSeparados.get(8);
			
			lerMIP06();
		}
	}
	
	
	public static ArrayList  collection = new ArrayList();
	public static ArrayList  collection2 = new ArrayList();
	public static ArrayList  collection3 = new ArrayList();
	
	@SuppressWarnings("resource")
	public static void lerMIP06() throws FileNotFoundException
	{
		
		Scanner scannerMIP06 = new Scanner(new FileReader("C://Users//leonardo.moura//Downloads//PBTI//Caixa//SailPoint//MIP_06-SIAAD_TESTE.csv")).useDelimiter(",");
	
		while (scannerMIP06.hasNext()) {
	
			arrayMIP06.add(scannerMIP06.nextLine());
		}
		
		arrayMIP06.remove(0);
		
		ArrayList<String> teste1 = new ArrayList<String>();
		ArrayList<String> teste2 = new ArrayList<String>();
		
		
		for(String dadosMIP06 :arrayMIP06)
		{
			ArrayList<String> recebePalavraSeparados = new ArrayList<String>();
			// receber o valor do receberListaLinhas
			
			for (String separaCampo : dadosMIP06.split(",")) 
			{
				recebePalavraSeparados.add(separaCampo.replace("\"", ""));
			}
			
			
			if(teste1.size() != 0)
			{
				if(recebePalavraSeparados.size() != 0)
				{	
				
					if((teste1.get(0).equals(recebePalavraSeparados.get(0)))
							&& (teste1.get(1).equals(recebePalavraSeparados.get(1))) 
							&& (teste1.get(2).equals(recebePalavraSeparados.get(2)))
							&& (teste1.get(3).equals(recebePalavraSeparados.get(3)))
							&& (teste1.get(5).equals(recebePalavraSeparados.get(5)))
							&& (teste1.get(6).equals(recebePalavraSeparados.get(6)))
							&& (teste1.get(7).equals(recebePalavraSeparados.get(7)))
							&& (teste1.get(8).equals(recebePalavraSeparados.get(8)))
							&& (!teste1.get(4).equals(recebePalavraSeparados.get(4))))
					{
						teste2.add(recebePalavraSeparados.get(4));
						System.out.println(teste2.toString());
						teste1.removeAll(recebePalavraSeparados);
						teste1.remove(0);
						teste1.addAll(recebePalavraSeparados);
					} else {
						
						
						coSistemaMIP06 = recebePalavraSeparados.get(0);
						coPerfilMIP06 = recebePalavraSeparados.get(1);
						tipoUnidadeMIP06 = recebePalavraSeparados.get(2);
						funcaoMIP06 = recebePalavraSeparados.get(3);
//						codFuncaoMPI06 = recebePalavraSeparados.get(4);
						codUnidadeMIP06 = recebePalavraSeparados.get(5);
						tipoUsuarioMIP06 = recebePalavraSeparados.get(6);
						codCargoMIP06 = recebePalavraSeparados.get(7);
						indUnidadeMIP06 = recebePalavraSeparados.get(8);
						
						
						collection.add(0, coSistemaMIP06);
						collection.add(0, coPerfilMIP06);
						collection.add(0, tipoUnidadeMIP06);
						collection.add(0, funcaoMIP06);
						collection.add(0, codUnidadeMIP06);
						collection.add(0, tipoUsuarioMIP06);
						collection.add(0, codCargoMIP06);
						collection.add(0, indUnidadeMIP06);
						collection.add(0, teste2);
						collection2.add(collection);
						
						
						tratamentoPassagem();
						
						teste1.removeAll(recebePalavraSeparados);
						teste1.remove(0);
						teste1.addAll(recebePalavraSeparados);
						teste2.add(recebePalavraSeparados.get(4));
					}
				} else {
					teste2 = new ArrayList<>();
					teste2.add(teste1.get(4));
				}
				
			} else {
				teste1.addAll(recebePalavraSeparados);
				teste2.add(recebePalavraSeparados.get(4));
			}
			
			
		}
		
	}
	
	public static void tratamentoPassagem()
	{
		ArrayList<String> teste = new ArrayList<String>();
		
		
		
		
		
		System.out.println(collection2);
		
		
		
		
		
		
		
		
		
		
		if(coSistemaMIP04.equals(coSistemaMIP06) && coPerfilMIP04.equals(coPerfilMIP06))
		{
			
			
			setNomeXml(coSistemaMIP06+coPerfilMIP06);
			bundle.setNomeBundle(coPerfilMIP06);
			inheritance.setNomeSistema(coSistemaMIP06);
			owner.setNomeOwner("spadmin");
//			profiles.setParametro(rssTypeMIP04);
//			profiles.setApplicaton(rssTypeMIP04);
		
			
			
			
			
			
			
		}
		
		
		
		
		
		
		
	}
	
	public static void chamaCriaXML()
	{
		ArrayList<String> teste = new ArrayList<String>();
		
		if(coSistemaMIP04.equals(coSistemaMIP06) && coPerfilMIP04.equals(coPerfilMIP06))
		{
			
			setNomeXml(coSistemaMIP06+coPerfilMIP06);
			bundle.setNomeBundle(coPerfilMIP06);
			inheritance.setNomeSistema(coSistemaMIP06);
			owner.setNomeOwner("spadmin");
//			profiles.setParametro(rssTypeMIP04);
//			profiles.setApplicaton(rssTypeMIP04);
			//setar o valores no selector
//			selector.setOperacaoCompositerOr("OR");
//			selector.setOperacao("OR");
//			selector.setOperacao("AND");
//			selector.setOperacaoFil1("EQ");
//			selector.setProperty1("sg_unde_ltco_fisica");
//			selector.setValue1("GSET");
		//	
//			selector.setOperacaoFil2("EQ");
//			selector.setProperty2("tipo_usuario");
//			selector.setValue2("C");
		//	
//			selector.setOperacaoFil3("IN");
//			selector.setProperty3("funcao");
//			selector.setListString("474");
//			selector.setListString2("566");
//			selector.setListString3("599");
			
			
			
			
			
			
			
		}
		
		
		
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
