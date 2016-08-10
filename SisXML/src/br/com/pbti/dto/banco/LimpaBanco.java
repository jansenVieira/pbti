package br.com.pbti.dto.banco;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import br.com.pbti.dto.banco.connection.ConectionBanco;

public class LimpaBanco {
	
	PreparedStatement ps;
	
	ConectionBanco conn = new ConectionBanco();
	
	
	
	public void deleteDados() throws ClassNotFoundException, SQLException
	{
		deletaMip04();
		
//		deletaMip06();
	}
	
	
	public void deletaMip04() throws ClassNotFoundException, SQLException
	{
		ps = conn.preparedStatement("DELETE FROM mip04_full");
		ps.execute();
		ps.close();
		
	}
	
	
//	public void deletaMip06() throws ClassNotFoundException, SQLException
//	{
//		ps = conn.preparedStatement("DELETE FROM mip06");
//		ps.execute();
//		ps.close();
//	}

}
