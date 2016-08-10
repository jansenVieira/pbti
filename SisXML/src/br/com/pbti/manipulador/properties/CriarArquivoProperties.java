package br.com.pbti.manipulador.properties;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import org.apache.commons.codec.binary.Base64;

public class CriarArquivoProperties {

	private String endArqCsvMip04;
	private String endArqCsvMip06;
	private String nomeClatura;
	private String atributoGrupo;
	private String endArqXML;
	private String urlBanco;
	private String driverBanco;
	private String userBanco;
	private String senhaBanco;
	private String localArquivoProperties;

	public void criarConfiguracoes() throws IOException {

		Scanner ler = new Scanner(System.in);

		System.out.printf("Configuracao Banco de Dados\n");
		System.out.printf("======================\n");

		System.out.printf("URL: ");
		setUrlBanco(ler.nextLine());

		System.out.printf("Drive: ");
		setDriverBanco(ler.nextLine());

		System.out.printf("Usuario: ");
		setUserBanco(ler.nextLine());

		System.out.printf("Senha: ");
		String abas = ler.nextLine();
		abas = new Base64().encodeToString(abas.getBytes());
		setSenhaBanco(abas);
		System.out.printf("\n");
		System.out.printf("======================\n");

		File localLinux = new File("/opt/sailpoint/MIP/");
		File localWindows = new File("C:\\Sailpoint\\MIP\\");
		FileWriter arq = null;
		
		if (localLinux.exists()) {
			arq = new FileWriter("/opt/sailpoint/MIP/dados.properties");
		} else if (localWindows.exists()) {
			arq = new FileWriter("C:\\Sailpoint\\MIP\\dados.properties");
		}
				

		PrintWriter gravarArq = new PrintWriter(arq);

		gravarArq.println("prop.banco.url=" + getUrlBanco());
		gravarArq.println("prop.banco.drive=" + getDriverBanco());
		gravarArq.println("prop.banco.user=" + getUserBanco());
		gravarArq.println("prop.banco.senha=" + getSenhaBanco());

		arq.close();

		System.out.println("Arquivo Properties foi criado com sucesso ");
	}

	public String getAtributoGrupo() {
		return atributoGrupo;
	}

	public void setAtributoGrupo(String atributoGrupo) {
		this.atributoGrupo = atributoGrupo;
	}

	public String getNomeClatura() {
		return nomeClatura;
	}

	public void setNomeClatura(String nomeClatura) {
		this.nomeClatura = nomeClatura;
	}

	public String getEndArqCsvMip04() {
		return endArqCsvMip04;
	}

	public void setEndArqCsvMip04(String endArqCsvMip04) {
		this.endArqCsvMip04 = endArqCsvMip04;
	}

	public String getEndArqCsvMip06() {
		return endArqCsvMip06;
	}

	public void setEndArqCsvMip06(String endArqCsvMip06) {
		this.endArqCsvMip06 = endArqCsvMip06;
	}

	public String getEndArqXML() {
		return endArqXML;
	}

	public void setEndArqXML(String endArqXML) {
		this.endArqXML = endArqXML;
	}

	public String getUrlBanco() {
		return urlBanco;
	}

	public void setUrlBanco(String urlBanco) {
		this.urlBanco = urlBanco;
	}

	public String getDriverBanco() {
		return driverBanco;
	}

	public void setDriverBanco(String driverBanco) {
		this.driverBanco = driverBanco;
	}

	public String getUserBanco() {
		return userBanco;
	}

	public void setUserBanco(String userBanco) {
		this.userBanco = userBanco;
	}

	public String getSenhaBanco() {
		return senhaBanco;
	}

	public void setSenhaBanco(String senhaBanco) {
		this.senhaBanco = senhaBanco;
	}

	public String getLocalArquivoProperties() {
		return localArquivoProperties;
	}

	public void setLocalArquivoProperties(String localArquivoProperties) {
		this.localArquivoProperties = localArquivoProperties;
	}

}
