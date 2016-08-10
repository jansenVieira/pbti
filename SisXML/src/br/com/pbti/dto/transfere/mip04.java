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
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import com.mysql.jdbc.interceptors.ResultSetScannerInterceptor;

import br.com.pbti.manipulador.properties.LerProperties;

public class mip04 {

	static String codSistema;
	static String codPerfil;
	static String ugName;
	static String rssName;
	static String status;
	static String rssType;
	static String permite_dup;
	static String prioridade_dup;
	static String funcionalidade;
	static String codRestricao;
	static String quantidade;
	static String sisSinav;
	static String sisDireto;
	static String atualizacaoNacional;
	static String nivelSeguranca;
	static String visaoDiretoria;
	static String indPrimario;
	static String sisRacfSP;
	static String indProduca;
	static String sisSiase;
	static String consultaNacional;
	static String indProducao;

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
	

	public static void lerMIP04() throws ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException, SQLException, ClassNotFoundException, IOException {
		
		
		insertMip();
		
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
	protected static Connection getConnection() throws SQLException,
			ClassNotFoundException {
		if (connection == null) {
			Class.forName(DRIVE);
			connection = DriverManager.getConnection(URL, USUARIO, SENHA);
		}
		return connection;
	}

	// Obtem os dados para acesso ao banco
	public static void valorConexao() throws IOException {
		if(URL == null)
		{
		
			lerproperties.dadosProperties();
			URL = lerproperties.getUrlBanco();
			SENHA = lerproperties.getSenhaBanco();
			USUARIO = lerproperties.getUserBanco();
			DRIVE = lerproperties.getDriveBanco();
		}
		

	}

	static int numero = 1;

	public static void insertMip() throws IOException {

		try {
			numero++;
			
			String sql = "insert into mip04_full "
					+ "(cod_sistema, cod_perfil, ug_name, rss_name, status, rss_type, permite_dup, prioridade_dup, funcionalidade, "
					+"cod_restricao, quantidade, sis_sinav, sis_siase, sis_direto, atualizacao_nacional, consulta_nacional, " 
					+"nivel_seguranca, visao_diretoria, ind_primario, sis_racfsp, ind_producao) "
					+"SELECT cod_sistema, cod_perfil, ug_name, rss_name, status, rss_type, permite_dup, prioridade_dup, funcionalidade, " 
					+"cod_restricao, quantidade, sis_sinav, sis_siase, sis_direto, atualizacao_nacional, consulta_nacional, " 
					+"nivel_seguranca, visao_diretoria, ind_primario, sis_racfsp, ind_producao FROM mip_04";
			
			ps = preparedStatement(sql);
			ps.execute();

			System.out.println("Sucesso "+numero);
			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

	}

	public static void main(String[] args) throws ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException, SQLException, ClassNotFoundException, IOException {

		lerMIP04();

	}

}
