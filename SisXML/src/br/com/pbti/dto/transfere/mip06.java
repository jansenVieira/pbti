package br.com.pbti.dto.transfere;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import br.com.pbti.manipulador.properties.LerProperties;

public class mip06 {

	static String codSistema;
	static String codPerfil;
	static String tipoUnidade;
	static String funcao;
	static String codFuncao;
	static String codUnidade;
	static String tipoUsu;
	static String codCargo;
	static String indUnidade;
	static int refMip;

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
	static int num = 1;
	int a = 0;
	
	public static void lerMIP04() throws FileNotFoundException,
			ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException, SQLException, ClassNotFoundException {
		
		try {
			lerproperties.dadosProperties();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Scanner scannerMip04 = new Scanner(new FileReader(
				lerproperties.getUrlCsvMip06())).useDelimiter(",");

		while (scannerMip04.hasNext()) {
			arrayMIP04.add(scannerMip04.nextLine());
		}

		arrayMIP04.remove(0);

		for (String dadosMIP04 : arrayMIP04) {

			ArrayList<String> recebePalavraSeparados = new ArrayList<String>();
			// receber o valor do receberListaLinhas
			// for (String separaCampo : dadosMIP04.split(";")) {
			for (String separaCampo : dadosMIP04.split("\"")) {

				if (!separaCampo.equals(",0,0,")) {
					if (!separaCampo.equals(",")) {

						recebePalavraSeparados.add(separaCampo);
					}
				} else {

					recebePalavraSeparados.add("0");
					recebePalavraSeparados.add("0");
				}

				if (recebePalavraSeparados.get(0).equals("")) {
					recebePalavraSeparados.remove(0);
				}
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

					// selectMip04(codSistema, codPerfil);

					insertMip04(codSistema, codPerfil, tipoUnidade, funcao,
							codFuncao, codUnidade, tipoUsu, codCargo,
							indUnidade);


				}

	}

	// conexao ao banco
	protected static PreparedStatement preparedStatement(String sql)
			throws SQLException, ClassNotFoundException {
		valorConexao();
		ps = null;
		ps = getConnection().prepareStatement(sql);

		return ps;
	}

	// conexao ao banco
	protected static Connection getConnection() throws SQLException,
			ClassNotFoundException {
		
		if (connection == null) {
		
			Class.forName(DRIVE);
			connection = DriverManager.getConnection(URL, USUARIO, SENHA);
		}
		return connection;
	}

	// Obtem os dados para acesso ao banco
	public static void valorConexao() {
		if(URL == null)
		{
			try {
				lerproperties.dadosProperties();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			URL = lerproperties.getUrlBanco();
			SENHA = lerproperties.getSenhaBanco();
			USUARIO = lerproperties.getUserBanco();
			DRIVE = lerproperties.getDriveBanco();
		}
	}

	public static void selectMip04(String codSistema, String codPerfil) throws SQLException {

		ResultSet rs = null;
		
		try {
			ps = preparedStatement("SELECT * FROM mip04_full where cod_sistema ="
					+ "'" + codSistema + "'" + "and cod_perfil =" + "'"
					+ codPerfil + "'");
			 rs = ps.executeQuery();
			while (rs.next()) {
				refMip = rs.getInt("idmip04");
			}
			

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {
			
			rs.close();
			ps.close();
			
//			connection.close();
			
		}

	}

	public static void insertMip04(String codSistema, String codPerfil,
			String tipoUnidade, String funcao, String codFuncao,
			String codUnidade, String tipoUsu, String codCargo,
			String indUnidade) {

		try {
			num++;
			
			ps = preparedStatement("insert into mip06 "
					+ "(idmip06, cod_sistema, cod_perfil, tipo_unidade, funcao, cod_funcao, "
					+ "cod_unidade, tipo_usuario, cod_cargo, ind_unidade) "
					+ "values (?,?,?,?,?,?,?,?,?,?)");
			ps.setInt(1, num);
			ps.setString(2, codSistema);
			ps.setString(3, codPerfil);
			ps.setString(4, tipoUnidade);
			ps.setString(5, funcao);
			ps.setString(6, codFuncao);
			ps.setString(7, codUnidade);
			ps.setString(8, tipoUsu);
			ps.setString(9, codCargo);
			ps.setString(10, indUnidade);
			ps.execute();
			
			System.out.println("Sucesso "+num);

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {
			
			try {
				ps.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
//			try {
//				connection.close();
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
		}

	}

//	public static void main(String[] args) throws FileNotFoundException,
//			ParserConfigurationException, TransformerFactoryConfigurationError,
//			TransformerException, SQLException, ClassNotFoundException {
//
//		lerMIP06();
//
//	}

}
