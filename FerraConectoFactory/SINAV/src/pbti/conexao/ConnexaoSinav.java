package br.com.pbti.conexao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import br.com.pbti.dto.AccountAgreggationSinav;

public class ConnexaoSinav {

	public Connection connection;
	public PreparedStatement ps;
	public CallableStatement cs;
	private String userServerSybase;
	private String passwordServerSybase;
	private String urlServerSybase;
	private String driverClassServerSybase;
	private String urlServerCripto;
	private String userServerCripto;
	private String sernhaServerCripto;
	private String nameDllServerCripto;
	private String dominioServerCripto;
	private String keyServerCripto;

	// conexao ao banco
	protected Connection getConnection() throws SQLException, ClassNotFoundException {

		if (connection == null) {
			Class.forName(getDriverClassServerSybase());
			connection = DriverManager.getConnection(getUrlServerSybase(), getUserServerSybase(),
					getPasswordServerSybase());
		}

		return connection;
	}

	// Prepared Satatement
	public PreparedStatement preparedStatement(String sql) throws SQLException, ClassNotFoundException {

		ps = null;
		ps = getConnection().prepareStatement(sql);

		return ps;
	}

	//Call Procedure
	public CallableStatement callProcedure(String procedure) throws Exception {
		cs = null;
		cs = getConnection().prepareCall(procedure);

		return cs;
	}

	public void close() throws SQLException {

//		if (ps != null) {
//			if (ps.isClosed()) {
//				ps.close();
//				ps = null;
//			}
//		}
//
//		if (cs != null) {
//			if (!cs.isClosed()) {
//
//				cs.close();
//				cs = null;
//			}
//		}

		if (connection != null) {
			if (!connection.isClosed()) {
				connection.close();
				connection = null;
			}
		}
	}

	
	public String getUserServerSybase() {
		return userServerSybase;
	}

	public void setUserServerSybase(String userServerSybase) {
		this.userServerSybase = userServerSybase;
	}

	public String getPasswordServerSybase() {
		return passwordServerSybase;
	}

	public void setPasswordServerSybase(String passwordServerSybase) {
		this.passwordServerSybase = passwordServerSybase;
	}

	public String getUrlServerSybase() {
		return urlServerSybase;
	}

	public void setUrlServerSybase(String urlServerSybase) {
		this.urlServerSybase = urlServerSybase;
	}

	public String getDriverClassServerSybase() {
		return driverClassServerSybase;
	}

	public void setDriverClassServerSybase(String driverClassServerSybase) {
		this.driverClassServerSybase = driverClassServerSybase;
	}

	public String getUrlServerCripto() {
		return urlServerCripto;
	}

	public void setUrlServerCripto(String urlServerCripto) {
		this.urlServerCripto = urlServerCripto;
	}

	public String getUserServerCripto() {
		return userServerCripto;
	}

	public void setUserServerCripto(String userServerCripto) {
		this.userServerCripto = userServerCripto;
	}

	public String getSernhaServerCripto() {
		return sernhaServerCripto;
	}

	public void setSernhaServerCripto(String sernhaServerCripto) {
		this.sernhaServerCripto = sernhaServerCripto;
	}

	public String getNameDllServerCripto() {
		return nameDllServerCripto;
	}

	public void setNameDllServerCripto(String nameDllServerCripto) {
		this.nameDllServerCripto = nameDllServerCripto;
	}

	public String getDominioServerCripto() {
		return dominioServerCripto;
	}

	public void setDominioServerCripto(String dominioServerCripto) {
		this.dominioServerCripto = dominioServerCripto;
	}

	public String getKeyServerCripto() {
		return keyServerCripto;
	}

	public void setKeyServerCripto(String keyServerCripto) {
		this.keyServerCripto = keyServerCripto;
	}

}
