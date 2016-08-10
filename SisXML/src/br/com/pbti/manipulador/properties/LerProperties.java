package br.com.pbti.manipulador.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;

public class LerProperties {

	private String urlCsvMip04;
	private String urlCsvMip06;
	private String urlNomeclatura;
	private String urlAtributoGrupo;
	private String urlXml;
	private String urlBanco;
	private String driveBanco;
	private String userBanco;
	private String senhaBanco;
	Properties prop;
	

	public Properties getProp() throws IOException {

		Properties props = new Properties();

		File localLinux = new File("/opt/sailpoint/MIP/");
		File localWindows = new File("C:\\Sailpoint\\MIP\\");

		if (localLinux.exists()) {
			FileInputStream file = new FileInputStream("/opt/sailpoint/MIP/dados.properties");

			props.load(file);

			file.close();

			return props;

		} else if (localWindows.exists()) {
			FileInputStream file = new FileInputStream("C:\\Sailpoint\\MIP\\dados.properties");

			props.load(file);

			file.close();

			return props;
		} else {

			return null;
		}
	}

	public void dadosProperties() throws IOException {

		File localLinux = new File("/opt/sailpoint/MIP/");
		File localWindows = new File("C:\\Sailpoint\\MIP\\");
		
		if (localLinux.exists()) {
			
			setUrlNomeclatura("/opt/sailpoint/MIP/CSV/NovaNomenclatura.csv");
			setUrlAtributoGrupo("/opt/sailpoint/MIP/CSV/AtributoGrupo.csv");
			setUrlXml("/opt/sailpoint/MIP/XML");

		} else if (localWindows.exists()) {
			setUrlNomeclatura("C:\\Sailpoint\\MIP\\CSV\\NovaNomenclatura.csv");
			setUrlAtributoGrupo("C:\\Sailpoint\\MIP\\CSV\\AtributoGrupo.csv");
			setUrlXml("C:\\Sailpoint\\MIP\\XML");
		}
		
		
		if (prop != null) {
			
			setUrlBanco(prop.getProperty("prop.banco.url"));
			setDriveBanco(prop.getProperty("prop.banco.drive"));
			setUserBanco(prop.getProperty("prop.banco.user"));
			String abas = prop.getProperty("prop.banco.senha");
			abas = new String(new Base64().decode(abas));
			// setSenhaBanco(prop.getProperty("prop.banco.senha"));
			setSenhaBanco(abas);

		} else {

			prop = getProp();

			setUrlBanco(prop.getProperty("prop.banco.url"));
			setDriveBanco(prop.getProperty("prop.banco.drive"));
			setUserBanco(prop.getProperty("prop.banco.user"));
			String abas = prop.getProperty("prop.banco.senha");
			abas = new String(new Base64().decode(abas));
			// setSenhaBanco(prop.getProperty("prop.banco.senha"));
			setSenhaBanco(abas);
		}
	}

	public String getUrlNomeclatura() {
		return urlNomeclatura;
	}

	public void setUrlNomeclatura(String urlNomeclatura) {
		this.urlNomeclatura = urlNomeclatura;
	}

	public String getUrlAtributoGrupo() {
		return urlAtributoGrupo;
	}

	public void setUrlAtributoGrupo(String urlAtributoGrupo) {
		this.urlAtributoGrupo = urlAtributoGrupo;
	}

	public String getUrlCsvMip04() {
		return urlCsvMip04;
	}

	public void setUrlCsvMip04(String urlCsvMip04) {
		this.urlCsvMip04 = urlCsvMip04;
	}

	public String getUrlCsvMip06() {
		return urlCsvMip06;
	}

	public void setUrlCsvMip06(String urlCsvMip06) {
		this.urlCsvMip06 = urlCsvMip06;
	}

	public String getUrlXml() {
		return urlXml;
	}

	public void setUrlXml(String urlXml) {
		this.urlXml = urlXml;
	}

	public String getUrlBanco() {
		return urlBanco;
	}

	public void setUrlBanco(String urlBanco) {
		this.urlBanco = urlBanco;
	}

	public String getDriveBanco() {
		return driveBanco;
	}

	public void setDriveBanco(String driveBanco) {
		this.driveBanco = driveBanco;
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
}
