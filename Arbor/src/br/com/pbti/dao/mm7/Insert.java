package br.com.pbti.dao.mm7;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;

import br.com.pbti.dao.property.DataSource;
import br.com.pbti.dto.VoMm7;

public class Insert {

	public Connection connection;
	public PreparedStatement ps;
	public DataSource ds = new DataSource();
	Logger logger = Logger.getLogger(Insert.class);

	public void insertDados() throws ParseException, IOException, SQLException {
		Read read = new Read();

		ArrayList<VoMm7> arrDad = read.tratafile();


		long tempoInicial = System.currentTimeMillis();

		if (arrDad.size() == 0) {

			logger.error("Arquivos nao existentes ou vazios");
			new Exception("Arquivos nao existentes ou vazios");
			
		} else {

			for (VoMm7 vd : arrDad) {
				try {

					String sql = "INSERT INTO mm7 (NOME, DATANUMERO, DATA, TIPO, TAMANHO, QUANTIDADE) "
							+ "VALUES (?,?,?,?,?,?)";

					PreparedStatement ps = preparedStatement(sql);
					ps.setString(1, vd.getNome());
					ps.setLong(2, vd.getDataNumero());
					ps.setDate(3, (Date) vd.getData());
					ps.setString(4, vd.getTipo());
					ps.setLong(5, vd.getTamanho());
					ps.setLong(6, vd.getQuantidade());
					ps.executeUpdate();
					ps.close();

				} catch (SQLException | ClassNotFoundException e) {
					logger.error("Erro ao inserir os dados - metodo: inserirDados " + e);
				}
			}
			long tempoFinal = System.currentTimeMillis();
			logger.debug(
					String.format("Tempo de Execusao Insert: " + "%.3f ms%n ", (tempoFinal - tempoInicial) / 1000d));

			if (connection != null) {
				connection.close();
				connection = null;
			}
		}
	}

	public PreparedStatement preparedStatement(String sql) throws SQLException, ClassNotFoundException {

		ps = null;
		ps = getConnection().prepareStatement(sql);

		return ps;
	}

	public Connection getConnection() throws SQLException, ClassNotFoundException {
		if (connection == null) {

			Properties prop = new Properties();
			prop.setProperty("user", ds.getUserSpotfire());
			prop.setProperty("password", ds.getPasswordSpotfire());
	//		prop.setProperty("internal_logon", "sysdba");

			Class.forName(ds.getDriverClassSpotfire());

			connection = DriverManager.getConnection(ds.getUrlConnectionSpotfire(), prop);

			logger.debug("Aberto conexao: " + ds.getUrlConnectionSpotfire());
		}
		return connection;
	}

}
