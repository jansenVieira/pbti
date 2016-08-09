package br.com.pbti.dao.oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;

import br.com.pbti.dao.property.DataSource;
import br.com.pbti.dto.VoArbor;

public class Insert {

	public Connection connection;
	public PreparedStatement ps;
	public DataSource ds = new DataSource();
	Logger logger = Logger.getLogger(Insert.class);
	
	public void insertDados() throws SQLException {
		Read read = new Read();

		ArrayList<VoArbor> arrDad = read.lerDados();

		int i = 0;
		long tempoInicial = System.currentTimeMillis();
		for (VoArbor vd : arrDad) {
			try {

				String sql = "INSERT INTO arbor (ENTRADA_NO_ARBOR, ARQUIVO, TOTAL_DE_REGISTROS, ENTRADA_EM_CRITICA, "
						+ "CODIGO_DA_CRITICA, MIU_DISP_STATUS, MIU_DISP_CODE, QUANTIDADE, NO_BILL, DT_PROCESSAMENTO, "
						+ "DT_NUMERO, DT_MM7, TIPO) " + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

				PreparedStatement ps = preparedStatement(sql);
				ps.setDate(1, vd.getDtEntrada());
				ps.setString(2, vd.getArquivo());
				ps.setLong(3, vd.getTotalRegistro());
				ps.setDate(4, vd.getDtEntradaCritica());
				ps.setLong(5, vd.getCodCritica());
				ps.setLong(6, vd.getMiuDispStatus());
				ps.setLong(7, vd.getMiuDispCod());	
				ps.setLong(8, vd.getQuantidade());	
				ps.setLong(9, vd.getNoBil());
				ps.setDate(10, vd.getDataProcessamento());		
				ps.setLong(11, vd.getDataNumero());
				ps.setDate(12, vd.getDataMM7());
				ps.setString(13, vd.getTipo());
				
				
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
		logger.debug(String.format("Tempo de Execusao Insert: "+ "%.3f ms%n ", (tempoFinal - tempoInicial) / 1000d));
		
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
