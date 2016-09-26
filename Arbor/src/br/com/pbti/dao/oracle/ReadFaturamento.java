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
import br.com.pbti.dto.VoArborFaturamento;

public class ReadFaturamento {

	public Connection connection;
	public PreparedStatement ps;
	public DataSource ds = new DataSource();
	Logger logger = Logger.getLogger(ReadFaturamento.class);

	public ArrayList<VoArborFaturamento> lerDados() throws SQLException {

		ArrayList<VoArborFaturamento> arrVoArborFaturamento = new ArrayList<VoArborFaturamento>();

		long tempoInicial = System.currentTimeMillis();
		try {
			ps = preparedStatement("select trunc(entry_create_dt), case when file_name like '%Voz%'"
					+ " then 'VOZ' when file_name like '%SMS%' then 'SMS' when file_name like '%MMS%'"
					+ " then 'MMS' when file_name like '%GPRS%' then 'GPRS'"
					+ " when file_name like '%COBILLING%' then 'COBILLING' when file_name like '%CobNokia%'"
					+ " then 'COBILLING_INTERNO' when file_name like '%IESF%' then 'IPTV' else"
					+ " 'OUTROS' end tipo, count(*) arquivos, sum(total_records) cdrs"
					+ " from file_status fs where fs.entry_create_dt>sysdate-11 and fs.ext_contact_id=650"
					+ " and fs.file_id_serv=2 and file_type=0 group by trunc(entry_create_dt), case"
					+ " when file_name like '%Voz%' then 'VOZ' when file_name like '%SMS%' then 'SMS'"
					+ " when file_name like '%MMS%' then 'MMS' when file_name like '%GPRS%' then 'GPRS'"
					+ " when file_name like '%COBILLING%' then 'COBILLING' when file_name like '%CobNokia%'"
					+ " then 'COBILLING_INTERNO' when file_name like '%IESF%' then 'IPTV' else"
					+ " 'OUTROS' end order by 1,2");

			ResultSet rs = ps.executeQuery();

			long tempoConsulta = System.currentTimeMillis();

			logger.debug(String.format("Tempo de Execusao ReadFaturamento: " + "%.3f ms%n ",
					(tempoConsulta - tempoInicial) / 1000d));

			while (rs.next()) {

				VoArborFaturamento voArborFaturamento = new VoArborFaturamento();


					voArborFaturamento.setData(rs.getDate("trunc(entry_create_dt)"));
					voArborFaturamento.setTipo(rs.getString("TIPO"));
					voArborFaturamento.setArquivos(rs.getLong("ARQUIVOS"));
					voArborFaturamento.setCdrs(rs.getLong("CDRS"));

					arrVoArborFaturamento.add(voArborFaturamento);
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
		logger.debug(String.format("Tempo de Execusao ReadFaturamento: " + "%.3f ms%n ",
				(tempoFinal - tempoInicial) / 1000d));

		return arrVoArborFaturamento;
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
