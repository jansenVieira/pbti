import java.sql.Connection;
import java.sql.SQLException;


public enum EnumConnection {
	
	INSTANCE;
	
	
	
	private Connection connection;
	
	public Connection getConnection()
	{
		if(connection == null)
		{
			try {
				this.connection = new SIRIC().getConnection();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return connection;
	}
}
