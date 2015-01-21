package br.com.pbti.dto;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import br.com.pbti.principal.XmlConfig;
import br.com.pbti.vo.Variaveis;

public class TransObjetos {
	// variaveis
	private static String linha;
	private static ArrayList<String> listaLinhas = new ArrayList<String>();
	//Estanciar classe

	public void abrirCSV() throws FileNotFoundException {
		// abrir o arquivo csv
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(
				new FileReader(
						"C://Users//leonardo.moura//Downloads//PBTI//Caixa//SailPoint//MIP_06-SIAAD_TESTE.csv"))
		.useDelimiter(",");
		// comecar a ler o arquivo
		while (scanner.hasNext()) {
			//adiciona a linha dentro de um array
			linha = scanner.nextLine();
			listaLinhas.add(linha);
		}

		for (String receberListaLinhas : listaLinhas) {
			//zera valor do array
			ArrayList<String> recebePalavraSeparados = new ArrayList<String>();
			//receber o valor do receberListaLinhas
			for (String separaCampo : receberListaLinhas.split(",")) {

				recebePalavraSeparados.add(separaCampo.replace("\"", ""));
			}

			Variaveis.setCodSistema (recebePalavraSeparados.get(0));
			Variaveis.setCodPerfil (recebePalavraSeparados.get(1));
			Variaveis.setTipoUnidade (recebePalavraSeparados.get(2));
			Variaveis.setFuncao (recebePalavraSeparados.get(3));
			Variaveis.setCodFuncao (recebePalavraSeparados.get(4));
			Variaveis.setCodUnidade (recebePalavraSeparados.get(5));
			Variaveis.setTipoUsu (recebePalavraSeparados.get(6));
			Variaveis.setCodCargo (recebePalavraSeparados.get(7));
			Variaveis.setIndUnidade (recebePalavraSeparados.get(8));
		}

	}

	@SuppressWarnings("static-access")
	public static void chamaMetodo() throws FileNotFoundException,
	ParserConfigurationException, TransformerFactoryConfigurationError,
	TransformerException {

		String nomeXml;

		nomeXml = Variaveis.getCodSistema() + "-" + Variaveis.getCodPerfil() + "-" + Variaveis.getTipoUnidade();

		XmlConfig xc = new XmlConfig();

		xc.setNomeXml(nomeXml);
		// xc.s
		xc.bundle.setNomeBundle(Variaveis.getCodSistema());

		xc.montaXml();

	}
	

}
