package br.com.pbti.dao.mm7.file;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Properties;

import org.apache.log4j.Logger;

import br.com.pbti.dao.property.DataSource;

public class FileTransfers {

	Logger logger = Logger.getLogger(FileTransfers.class);

	public Connection connection;
	public PreparedStatement ps;
	public DataSource ds = new DataSource();
	public java.util.Date dt = new java.util.Date();
	public Calendar c = Calendar.getInstance();

	public File dirSucesso = new File(ds.getDirSucessoMM7());
	public File dirFail = new File(ds.getDirFailMM7());

	public void transfersSucess(LinkedHashSet<File> arrFile) {

		boolean ok = false;

		for (File arquivo : arrFile) {
			
			insertDados(arquivo.getName(), "Sucesso", dt);
			
			ok = arquivo.renameTo(new File(dirSucesso, arquivo.getName()));	

			if (ok == true) {
				logger.info("Sucesso ao tranferir o arquivo: " + arquivo);
			} else {
				logger.error("Falha ao transferir o arquivo: " + arquivo);
			}
		}

	}

	public void transfersFail(LinkedHashSet<File> arrFile) {

		boolean ok = false;

		for (File arquivo : arrFile) {
			
			insertDados(arquivo.getName(), "Falha", dt);
			
			ok = arquivo.renameTo(new File(dirFail, arquivo.getName()));

			if (ok == true) {
				logger.info("Sucesso ao tranferir o arquivo: " + arquivo);
			} else {
				logger.error("Falha ao transferir o arquivo: " + arquivo);
			}
		}

	}

	public void delete(Collection<File> arrFile) {

		boolean ok = false;

		for (File arquivo : arrFile) {

			ok = arquivo.delete();

			if (ok == true) {
				logger.info("Sucesso ao deletar o arquivo: " + arquivo);
			} else {
				logger.error("Falha ao deletar o arquivo: " + arquivo);
			}
		}
	}

	public void insertDados(String arquivo, String status, java.util.Date data) {

		Date dataSql = new Date(data.getTime());
				
		c.setTime(data);
		c.add(Calendar.DATE, +1);
		data = c.getTime();
				
		Date dataLogConvert = new Date(data.getTime());

		try {

			String sql = "INSERT INTO logMM7 (ARQUIVO, STATUS, DATA, LOG_DATA) " + "VALUES (?,?,?,?)";

			PreparedStatement ps = preparedStatement(sql);

			ps.setString(1, arquivo);
			ps.setString(2, status);
			ps.setDate(3, (Date) dataSql);
			ps.setDate(4, (Date) dataLogConvert);
			ps.executeUpdate();
			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
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
