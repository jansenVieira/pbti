package br.com.pbti.dto.banco.connection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import br.com.pbti.manipulador.properties.LerProperties;

public class ConectionBanco {

	public Connection connection;
	public PreparedStatement ps;
	public LerProperties lerProperties = new LerProperties();

	public PreparedStatement preparedStatement(String sql) throws SQLException, ClassNotFoundException {

		try {
			
			
			
			lerProperties.dadosProperties();
		
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ps = null;
		ps = getConnection().prepareStatement(sql);

		return ps;
	}

	public Connection getConnection() throws SQLException, ClassNotFoundException {
		if (connection == null) {
			Class.forName(lerProperties.getDriveBanco());
			connection = DriverManager.getConnection(lerProperties.getUrlBanco(), lerProperties.getUserBanco(), lerProperties.getSenhaBanco());
		}
		return connection;
	}

}
