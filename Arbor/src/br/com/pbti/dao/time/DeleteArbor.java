package br.com.pbti.dao.time;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;

import br.com.pbti.dao.property.DataSource;
import br.com.pbti.dto.VoArbor;

public class DeleteArbor {

	public Connection connection;
	public PreparedStatement ps;
	public DataSource ds = new DataSource();
	Logger logger = Logger.getLogger(ReadDateTime.class);

	public ArrayList<VoArbor> deleteDadosArbor() throws SQLException {

		ArrayList<VoArbor> arrVoArbor = new ArrayList<VoArbor>();

		long tempoInicial = System.currentTimeMillis();
		try {
			ps = preparedStatement("DELETE ARBOR WHERE entrada_no_arbor < TO_DATE(SYSDATE-365, 'DD-MM-YY')");

			ps.execute();
			

			long tempoConsulta = System.currentTimeMillis();

			logger.debug(
					String.format("Tempo de Execusao Read: " + "%.3f ms%n ", (tempoConsulta - tempoInicial) / 1000d));

			ps.close();

		} catch (SQLException | ClassNotFoundException e) {
			logger.error("Erro ao ler dados " + e);
			e.printStackTrace();
		} finally {
			connection.close();
			connection = null;
		}

		long tempoFinal = System.currentTimeMillis();
		logger.debug(String.format("Tempo de Execusao Read: " + "%.3f ms%n ", (tempoFinal - tempoInicial) / 1000d));

		return arrVoArbor;
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
		//	prop.setProperty("internal_logon", "sysdba");

			Class.forName(ds.getDriverClassSpotfire());

			connection = DriverManager.getConnection(ds.getUrlConnectionSpotfire(), prop);

			logger.debug("Aberto conexao: " + ds.getUrlConnectionSpotfire());
		}
		return connection;
	}
	
	
}
