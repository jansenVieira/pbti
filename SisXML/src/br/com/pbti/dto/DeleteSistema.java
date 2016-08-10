package br.com.pbti.dto;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import br.com.pbti.manipulador.properties.LerProperties;

public class DeleteSistema {

	static String URL;
	static String SENHA;
	static String USUARIO;
	static String DRIVE;

	private static Connection connection;
	public static PreparedStatement ps;

	static ArrayList<String> arrayMIP04 = new ArrayList<String>();

	static ArrayList<String> sistema = new ArrayList<String>();
	static ArrayList<String> perfil = new ArrayList<String>();

	static LerProperties lerproperties = new LerProperties();

	public static void deleteSis(String sistema) {

		try {
			deleteMip04(sistema);
			deleteMip06(sistema);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// conexao ao banco
	protected static PreparedStatement preparedStatement(String sql)
			throws SQLException, ClassNotFoundException, IOException {
		valorConexao();
		ps = null;
		ps = getConnection().prepareStatement(sql);

		return ps;
	}

	// conexao ao banco
	protected static Connection getConnection() throws SQLException, ClassNotFoundException {
		if (connection == null) {
			Class.forName(DRIVE);
			connection = DriverManager.getConnection(URL, USUARIO, SENHA);
		}
		return connection;
	}

	// Obtem os dados para acesso ao banco
	public static void valorConexao() throws IOException {
		if (URL == null) {

			lerproperties.dadosProperties();
			URL = lerproperties.getUrlBanco();
			SENHA = lerproperties.getSenhaBanco();
			USUARIO = lerproperties.getUserBanco();
			DRIVE = lerproperties.getDriveBanco();
		}

	}

	public static void deleteMip04(String sistema) throws IOException {

		try {

			String sql = "delete from mip_04 where COD_SISTEMA ='" + sistema + "'";

			ps = preparedStatement(sql);
			ps.execute();

			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

	}

	public static void deleteMip06(String sistema) throws IOException {

		try {
			String sql = "delete from mip_06 where COD_SISTEMA ='" + sistema + "'";

			ps = preparedStatement(sql);
			ps.execute();

			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

	}

}
