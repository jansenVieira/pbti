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

	public static void main(String[] args) throws FileNotFoundException,
			ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException {

		String linha;

		ArrayList<String> listaLinhas = new ArrayList<String>();

		Scanner scanner = new Scanner(new FileReader(
				"C://Users//tic//Desktop//ler xsl//MIP_06-SIAADTeste.csv"))
				.useDelimiter(",");

		while (scanner.hasNext()) {

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

			codSistema = recebePalavraSeparados.get(0);
			codPerfil = recebePalavraSeparados.get(1);
			tipoUnidade = recebePalavraSeparados.get(2);
			funcao = recebePalavraSeparados.get(3);
			codFuncao = recebePalavraSeparados.get(4);
			codUnidade = recebePalavraSeparados.get(5);
			tipoUsu = recebePalavraSeparados.get(6);
			codCargo = recebePalavraSeparados.get(7);
			indUnidade = recebePalavraSeparados.get(8);

			chamaMetodo();

		}

	}
	
	

	public static void chamaMetodo() throws FileNotFoundException,
			ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException {

		String nomeXml;

		nomeXml = codSistema + "-" + codPerfil + "-" + tipoUnidade;

		XmlConfig xc = new XmlConfig();

		xc.setNomeXml(nomeXml);
		// xc.s
		xc.bundle.setNomeBundle(codSistema);

		xc.montaXml();

	}

}
