package br.com.pbti.dao.oracle;

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

public class Read {

	public Connection connection;
	public PreparedStatement ps;
	public DataSource ds = new DataSource();
	Logger logger = Logger.getLogger(Read.class);

	public ArrayList<VoArbor> lerDados() throws SQLException {

		ArrayList<VoArbor> arrVoArbor = new ArrayList<VoArbor>();

		long tempoInicial = System.currentTimeMillis();
		try {
			ps = preparedStatement("select   trunc (entry_create_dt) entrada_no_arbor, "
					+ "trim (substr(file_name,5,80)) arquivo, " + "total_records total_de_registros, "
					+ "trunc(mps_miu_dt) entrada_em_critica, " + "trunc(file_process_dt) dt_processamento, "
					+ "miu_error_code1 codigo_da_critica, " + "no_bill, " + "miu_disp_status, " + "miu_disp_code, "
					+ "count ( * ) quantidade " + "from   cdr_data_work a, file_status b "
					+ "where   b.entry_create_dt > TO_DATE(SYSDATE-9, 'DD-MM-YY') and a.file_id = b.file_id "
					+ "group by   trunc (entry_create_dt), " + "trim (substr(file_name,5,80)), " + "total_records, "
					+ "trunc(mps_miu_dt), " + "trunc(file_process_dt), " + "no_bill, " + "miu_error_code1, "
					+ "miu_disp_status, " + "miu_disp_code order by  1,2,6");

			ResultSet rs = ps.executeQuery();

			long tempoConsulta = System.currentTimeMillis();

			logger.debug(
					String.format("Tempo de Execusao Read: " + "%.3f ms%n ", (tempoConsulta - tempoInicial) / 1000d));

			while (rs.next()) {

				VoArbor voArbor = new VoArbor();

				if (rs.getString("ARQUIVO").toUpperCase().startsWith("ARBOR")) {

					voArbor.setDtEntrada(rs.getDate("ENTRADA_NO_ARBOR"));
					voArbor.setArquivo(rs.getString("ARQUIVO"));
					voArbor.setTotalRegistro(rs.getLong("TOTAL_DE_REGISTROS"));
					voArbor.setDtEntradaCritica(rs.getDate("ENTRADA_EM_CRITICA"));
					voArbor.setCodCritica(rs.getLong("CODIGO_DA_CRITICA"));
					voArbor.setMiuDispStatus(rs.getLong("MIU_DISP_STATUS"));
					voArbor.setMiuDispCod(rs.getLong("MIU_DISP_CODE"));
					voArbor.setQuantidade(rs.getLong("QUANTIDADE"));
					voArbor.setNoBil(rs.getLong("NO_BILL"));
					voArbor.setDataProcessamento(rs.getDate("DT_PROCESSAMENTO"));

					arrVoArbor.add(voArbor);
				} else {
					logger.debug("Registros nao estao conforme o necessario: "+rs.getString("ARQUIVO"));
				}
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
			prop.setProperty("user", ds.getUserArbor());
			prop.setProperty("password", ds.getPasswordArbor());
			// prop.setProperty("internal_logon", "sysdba");

			Class.forName(ds.getDriverClassArbor());

			connection = DriverManager.getConnection(ds.getUrlConnectionArbor(), prop);
			logger.debug("Aberto conexao: " + ds.getUrlConnectionArbor());
		}
		return connection;
	}

}
