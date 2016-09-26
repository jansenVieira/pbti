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

public class ReadDateTime {

	public Connection connection;
	public PreparedStatement ps;
	public DataSource ds = new DataSource();
	Logger logger = Logger.getLogger(ReadDateTime.class);

	public ArrayList<VoArbor> lerDadosArbo() throws SQLException {

		ArrayList<VoArbor> arrVoArbor = new ArrayList<VoArbor>();

		long tempoInicial = System.currentTimeMillis();
		try {
			ps = preparedStatement("SELECT * FROM ARBOR WHERE ENTRADA_NO_ARBOR < TO_DATE(SYSDATE-365, 'DD-MM-YY') order by entrada_no_arbor");

			ResultSet rs = ps.executeQuery();

			long tempoConsulta = System.currentTimeMillis();

			logger.debug(
					String.format("Tempo de Execusao Read: " + "%.3f ms%n ", (tempoConsulta - tempoInicial) / 1000d));

			while (rs.next()) {

				logger.debug("Linha Excluída Tabela Arbor: ");
				logger.debug("Excluída: "+"ENTRADA_NO_ARBOR: "+rs.getDate("ENTRADA_NO_ARBOR")
										+" ARQUIVO: "+rs.getString("ARQUIVO")
										+" TOTAL_DE_REGISTROS: "+rs.getString("TOTAL_DE_REGISTROS")
										+" ENTRADA_EM_CRITICA: "+rs.getString("ENTRADA_EM_CRITICA")
										+" CODIGO_DA_CRITICA: "+rs.getString("CODIGO_DA_CRITICA")
										+" MIU_DISP_STATUS: "+rs.getString("MIU_DISP_STATUS")
										+" MIU_DISP_CODE: "+rs.getString("MIU_DISP_CODE")
										+" QUANTIDADE: "+rs.getString("QUANTIDADE")
										+" NO_BILL: "+rs.getString("NO_BILL")
										+" DT_PROCESSAMENTO: "+rs.getString("DT_PROCESSAMENTO"));
			}

			ps.close();
			rs.close();

		} catch (Exception e ) {
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
	
	
	public ArrayList<VoArbor> lerDadosMM7() throws SQLException {

		ArrayList<VoArbor> arrVoArbor = new ArrayList<VoArbor>();

		long tempoInicial = System.currentTimeMillis();
		try {
			ps = preparedStatement("SELECT * FROM MM7 WHERE DATA < TO_DATE(SYSDATE-365, 'DD-MM-YY') order by DATA");

			ResultSet rs = ps.executeQuery();

			long tempoConsulta = System.currentTimeMillis();

			logger.debug(
					String.format("Tempo de Execusao Read: " + "%.3f ms%n ", (tempoConsulta - tempoInicial) / 1000d));

			while (rs.next()) {

				logger.debug("Linha Excluída Tabela Arbor: ");
				logger.debug("Excluída: "+"NOME: "+rs.getDate("NOME")
										+" DATA: "+rs.getString("DATA")
										+" TIPO: "+rs.getString("TIPO")
										+" TAMANHO: "+rs.getString("TAMANHO")
										+" QUANTIDADE: "+rs.getString("QUANTIDADE"));
			}

			ps.close();
			rs.close();

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
