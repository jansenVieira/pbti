package br.com.pbti.dto.transfere.trataAtributo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import br.com.pbti.manipulador.properties.LerProperties;


public class AtributoGrupo
	{

		public static List<Map<String, Object>> arrayMip04 = new ArrayList<Map<String, Object>>();
		public static Map<String, Object> mapMip04;
		public static List<Map<String, Object>> arraySemNome = new ArrayList<Map<String, Object>>();
		public static Map<String, Object> mapSemNome;

		public static List<String> arrayTemporario = new ArrayList<String>();
		public static List<Map<String, Object>> arrayAtributo = new ArrayList<Map<String, Object>>();
		public static Map<String, Object> mapAtributo;

		static String URL = "url";
		static String SENHA = "password";
		static String USUARIO = "user";
		static String DRIVE = "driverClass";

		static Connection connection;
		static PreparedStatement ps;
		
		static LerProperties lerproperties = new LerProperties();

		// conexao ao banco
		protected static PreparedStatement preparedStatement(String sql)
				throws SQLException, ClassNotFoundException {
			try {
				valorConexao();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ps = null;
			ps = getConnection().prepareStatement(sql);

			return ps;
		}

		// conexao ao banco
		protected static Connection getConnection() throws SQLException,
				ClassNotFoundException {
			if (connection == null)
				{
					Class.forName(DRIVE);
					connection = DriverManager.getConnection(URL, USUARIO,
							SENHA);
				}
			return connection;
		}

		// Obtem os dados para acesso ao banco
		public static void valorConexao() throws IOException {
			lerproperties.dadosProperties();
			
			URL = lerproperties.getUrlBanco();
			SENHA = lerproperties.getSenhaBanco();
			USUARIO = lerproperties.getUserBanco();
			DRIVE = lerproperties.getDriveBanco();

		}

		public static void selectRssName(String sistema, String atributo) {

			try
				{
					ps = preparedStatement("SELECT cod_sistema, cod_perfil, ug_name, rss_type  FROM mip04_full where rss_type ='"
							+ sistema + "'");
					ResultSet rs = ps.executeQuery();

					while (rs.next())
						{

							mapMip04 = new HashMap<String, Object>();
							mapMip04.put("cod_sistema", rs.getString("cod_sistema"));
							mapMip04.put("cod_perfil", rs.getString("cod_perfil"));
							mapMip04.put("ug_name", rs.getString("ug_name"));
							mapMip04.put("rss_type", rs.getString("rss_type"));
							mapMip04.put("atributo", atributo);

							arrayMip04.add(mapMip04);

						}

				} catch (SQLException e)
				{
					e.printStackTrace();
					System.out.println(e.getMessage());
				} catch (ClassNotFoundException e)
				{
					e.printStackTrace();
					System.out.println(e.getMessage());
				}
		}

		public static void selectSemNome() {

			try
				{
					ps = preparedStatement("SELECT cod_sistema, cod_perfil, ug_name, rss_name, nome_aplicativo  FROM mip04_full where nome_aplicativo ='Sem nome'");
					ResultSet rs = ps.executeQuery();

					while (rs.next())
						{

							mapSemNome = new HashMap<String, Object>();
							mapSemNome.put("cod_sistema", rs.getString("cod_sistema"));
							mapSemNome.put("cod_perfil", rs.getString("cod_perfil"));
							mapSemNome.put("ug_name", rs.getString("ug_name"));
							mapSemNome.put("rss_name", rs.getString("rss_name"));

							arraySemNome.add(mapSemNome);

						}

				} catch (SQLException e)
				{
					e.printStackTrace();
					System.out.println(e.getMessage());
				} catch (ClassNotFoundException e)
				{
					e.printStackTrace();
					System.out.println(e.getMessage());
				}
		}

		static int i =0;
		
		public void insertMip04(String cod_sistema, String cod_perfil, String ug_name, String atributo) {

			try
				{
					ps = preparedStatement("UPDATE mip04_full SET nome_atributo_aplicativo = '"
							+ atributo + "' WHERE cod_sistema ='" + cod_sistema +"' and cod_perfil ='"+cod_perfil+"' and ug_name='"+ug_name+"'");
					ps.execute();

					i++;
					System.out.println("Sucesso Atributo Grupo "+i);
					ps.close();

				} catch (SQLException e)
				{
					e.printStackTrace();
					System.out.println(e.getMessage());
				} catch (ClassNotFoundException e)
				{
					e.printStackTrace();
					System.out.println(e.getMessage());
				}

		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private Collection filesToFilenames(Collection files) {
			Collection filenames = new java.util.ArrayList(files.size());
			Iterator i = files.iterator();
			while (i.hasNext())
				{
					filenames.add(((File) i.next()).getName());
				}
			return filenames;
		}

		@SuppressWarnings("unchecked")
		public void lerCsvAtributo() throws ParserConfigurationException,
				TransformerFactoryConfigurationError, TransformerException, IOException {
			// arrayMIP04 = new ArrayList<String>();

			lerproperties.dadosProperties();
			
			Scanner scannerMip04 = new Scanner(new FileReader(
					lerproperties.getUrlAtributoGrupo())).useDelimiter(";");

			while (scannerMip04.hasNext())
				{
					arrayTemporario.add(scannerMip04.nextLine());
				}

			arrayTemporario.remove(0);

			for (String nomeAtributo : arrayTemporario)
				{
					mapAtributo = new HashMap<String, Object>();

					String[] separaNomeAtributo = nomeAtributo.split(";");

					mapAtributo.put("Sistema", separaNomeAtributo[0]);
					mapAtributo.put("Atributo", separaNomeAtributo[1]);

					arrayAtributo.add(mapAtributo);

				}

		}

		public void inserirNovoAtributo()  {

			for (Map<String, Object> mapArrayMip : arrayMip04)
				{
					if(mapArrayMip.isEmpty())
					{
						System.out.println("Vazia");
					}else{
					String cod_sistema = mapArrayMip.get("cod_sistema").toString();
					String cod_perfil = mapArrayMip.get("cod_perfil").toString();
					String ug_name = mapArrayMip.get("ug_name").toString();
					String atributo = mapArrayMip.get("atributo").toString();

					insertMip04(cod_sistema, cod_perfil, ug_name, atributo);
					}

				}
		}

		public void novoAtributoGrupo() {

			try {
				
			
			
			lerCsvAtributo();

			for (Map<String, Object> mapAtributo : arrayAtributo)
				{
					String sistema = mapAtributo.get("Sistema")
							.toString();
					String atributo = mapAtributo.get("Atributo").toString();

					selectRssName(sistema, atributo);

				}

			inserirNovoAtributo();
			
			} catch (Exception e) {
				// TODO: handle exception
			}

		}

//		public static void main(String[] args) throws ParserConfigurationException,
//				TransformerFactoryConfigurationError, TransformerException, IOException {
//			novoAtributoGrupo();
//
//		}

		
		
		
		
		
	}
