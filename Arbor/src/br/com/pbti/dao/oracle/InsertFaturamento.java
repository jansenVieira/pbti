package br.com.pbti.dao.oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;

import br.com.pbti.dao.property.DataSource;
import br.com.pbti.dto.VoArborFaturamento;

public class InsertFaturamento {

	public Connection connection;
	public PreparedStatement ps;
	public DataSource ds = new DataSource();
	Logger logger = Logger.getLogger(InsertFaturamento.class);
	
	public void insertDados() throws SQLException {
		ReadFaturamento read = new ReadFaturamento();

		ArrayList<VoArborFaturamento> arrDad = read.lerDados();

		int i = 0;
		long tempoInicial = System.currentTimeMillis();
		for (VoArborFaturamento vd : arrDad) {
			try {

				String sql = "INSERT INTO arbor_faturamento (DATA_ENTRADA, TIPO, ARQUIVOS, CDRS, "
						+ "DATA_NUMERO) " + "VALUES (?,?,?,?,?)";

				PreparedStatement ps = preparedStatement(sql);
				ps.setDate(1, vd.getData());
				ps.setString(2, vd.getTipo());
				ps.setLong(3, vd.getArquivos());
				ps.setLong(4, vd.getCdrs());
				ps.setLong(5, vd.getDataNumero());
				
				
				ps.executeUpdate();
				ps.close();

				if (i == 0) {
					i = 1;
				} else {

					i = i + 1;
				}
			} catch (SQLException | ClassNotFoundException e) {
				logger.error("Erro ao inserir dados "+e);
				e.printStackTrace();
			}
		}
		 long tempoFinal = System.currentTimeMillis();
		logger.debug(String.format("Tempo de Execusao InsertFaturamento: "+ "%.3f ms%n ", (tempoFinal - tempoInicial) / 1000d));
		
		connection.close();
		connection = null;
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
