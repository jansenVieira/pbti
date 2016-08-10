package br.com.pbti.dto.transfere.trataAtributo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import br.com.pbti.manipulador.properties.LerProperties;

public class DeletaNomeEmBranco
	{

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
		
		public void selectNomeBranco() throws ClassNotFoundException, SQLException
		{
			ps = preparedStatement("select cod_sistema, cod_perfil, ug_name from mip04_full where nome_aplicativo is null or nome_aplicativo='Sem nome'");
			ResultSet rs = ps.executeQuery();
			
			while (rs.next())
			{
				String cod_sistema = rs.getString("cod_sistema");
				String cod_perfil = rs.getString("cod_perfil");
				String ug_name = rs.getString("ug_name");
				deleteMip04(cod_sistema, cod_perfil, ug_name);
			}
			
		}

		static int i =0;
		
		public void deleteMip04(String cod_sistema, String cod_perfil, String ug_name) {

			try
				{
					ps = preparedStatement("delete from mip04_full where cod_sistema = '" + cod_sistema+"'"
							+ "and cod_perfil='"+cod_perfil+"' and ug_name='"+ug_name+"'");
					ps.execute();

					i++;
					System.out.println("Sucesso Delete Nome em Branco "+i);
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

}