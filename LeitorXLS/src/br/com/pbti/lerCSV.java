package br.com.pbti;

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
	static String linha2;

	static ArrayList<String> listaLinhas = new ArrayList<String>();
	static ArrayList<String> listaLinhas2 = new ArrayList<String>();

	public static void main(String[] args) throws FileNotFoundException,
			ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException {

		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(
				new FileReader(
						"C://Users//leonardo.moura//Downloads//PBTI//Caixa//SailPoint//MIP_06-SIAAD_TESTE.csv"))
				.useDelimiter(",");

		while (scanner.hasNext()) {

			linha = scanner.nextLine();
			listaLinhas.add(linha);
		}

		@SuppressWarnings("resource")
		Scanner lerp2 = new Scanner(
				new FileReader(
						"C://Users//leonardo.moura//Downloads//PBTI//Caixa//SailPoint//MIP_04-SIAAD_TESTE.csv"))
				.useDelimiter(",");

		while (lerp2.hasNext()) {

			linha2 = lerp2.nextLine();
			listaLinhas2.add(linha2);
		}

		for (String receberListaLinhas : listaLinhas) {
			// zera valor do array
			ArrayList<String> recebePalavraSeparados = new ArrayList<String>();
			// receber o valor do receberListaLinhas
			for (String separaCampo : receberListaLinhas.split(",")) {

				recebePalavraSeparados.add(separaCampo.replace("\"", ""));
			}

			codSistema = recebePalavraSeparados.get(0);
			codPerfil = recebePalavraSeparados.get(1);
			tipoUnidade = recebePalavraSeparados.get(2);
			funcao = recebePalavraSeparados.get(3);
			codFuncao = recebePalavraSeparados.get(4);
			codUnidade = recebePalavraSeparados.get(5);
			tipoUsu = recebePalavraSeparados.get(6);
			codCargo = recebePalavraSeparados.get(7);
			indUnidade = recebePalavraSeparados.get(8);

			separa2planilha();

			// chamaMetodo();

		}

	}

	public static void separa2planilha() throws FileNotFoundException,
			ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException {

		for (String receberListaLinhas : listaLinhas2) {
			// zera valor do array
			ArrayList<String> recebePalavraSeparados = new ArrayList<String>();
			// receber o valor do receberListaLinhas
			for (String separaCampo : receberListaLinhas.split("\"")) {

				if (!separaCampo.equals(",")) {

					recebePalavraSeparados.add(separaCampo);
				}

				if (recebePalavraSeparados.get(0).equals("")) {
					recebePalavraSeparados.remove(0);
				}
			}

			COD_SISTEMA = recebePalavraSeparados.get(0);
			COD_PERFIL = recebePalavraSeparados.get(1);
			UG_NAME = recebePalavraSeparados.get(2);
			RSS_NAME = recebePalavraSeparados.get(3);
			FUNCIONALIDADE = recebePalavraSeparados.get(4);
			STATUS = recebePalavraSeparados.get(5);
			COD_RESTRICAO = recebePalavraSeparados.get(6);
			QUANTIDADE = recebePalavraSeparados.get(7);
			RSS_TYPE = recebePalavraSeparados.get(8);

			if(codPerfil.equals(COD_PERFIL))
			{
				chamaMetodo();
				break;
				
			}
			
			
			//if (codPerfil == COD_PERFIL) {
				
			//}

		}
	}

	@SuppressWarnings("static-access")
	public static void chamaMetodo() throws FileNotFoundException,
			ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException {

		String nomeXml;

		nomeXml = codSistema + "-" + codPerfil + "-" + tipoUnidade;

		XmlConfig xc = new XmlConfig();

		xc.setNomeXml(nomeXml);
		// xc.s
		xc.bundle.setNomeBundle(codSistema+"_"+codPerfil);
		xc.inheritance.setNomeSistema(codSistema);
		xc.owner.setNomeOwner("spadmin");
		xc.profiles.setParametro(UG_NAME);
		xc.profiles.setApplicaton(RSS_TYPE);

		xc.montaXml();
		
		System.out.println("GEROU XML");

	}

}
