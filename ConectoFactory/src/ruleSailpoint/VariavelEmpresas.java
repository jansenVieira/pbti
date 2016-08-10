package ruleSailpoint;
	  

import sailpoint.object.*;  

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sailpoint.object.*;
import sailpoint.api.*;


public class VariavelEmpresas {


	  Custom custom = context.getObject(Custom.class, "BD_CEATI");   

	  String driverClass = custom.getString("driverClass");
	  String dbHost = custom.getString("dbHost");
	  String dbPort = custom.getString("dbPort");
	  String dbInstance = custom.getString("dbInstance");                
	  String dbName = custom.getString("dbName");
	  String dbUser = custom.getString("dbUser");  
	  String encryptedDbPass = custom.getString("encryptedDbPass"); 
	  String clearDbPass = context.decrypt(encryptedDbPass);                    

	  String dbUrl = "jdbc:jtds:sqlserver://" + dbHost + ":" + dbPort + ";instance=" + dbInstance + ";databaseName=" + dbName;
	  
	  List listaEmpresas = new ArrayList();
	  
	  Connection dbCxn = null; 

	  try {  
	    dbCxn = DriverManager.getConnection(dbUrl, dbUser, clearDbPass);  	      
	  } catch (Exception ex) {  
	    String errMsg = "Error while connecting to database [" + dbUrl + "]";  
	    System.out.println(ex);
	  }  

	  try {

	    String sqlQuery = "exec spPassport_EmpresasSCP";  
	    PreparedStatement prStmt = dbCxn.prepareStatement(sqlQuery);

	    ResultSet rs = prStmt.executeQuery();  
		
		String chaveP = "P";
		String chaveE = "E";
		String chaveD = "D";
		String chaveJ = "J";
		String chaveF = "F";
		
		
		List listEmpresaP = new ArrayList();
		List listEmpresaE = new ArrayList();
		List listEmpresaD = new ArrayList();
		List listEmpresaJ = new ArrayList();
		List listEmpresaF = new ArrayList();
		Map mapValorEmpresa;
		Map mapEmpresa = new HashMap();

		
		
		
	    while ( (null != rs) && (rs.next()) ) { 
	      String codEmpresa =  rs.getString("Cod_Empresa");
	      String nomeEmpresa = rs.getString("Nome_Empresa");
	      String tipo = rs.getString("Tipo");	  
	      
		  if(nomeEmpresa.contains("&"))
		  {
			String[] separaEmpresa = nomeEmpresa.split("&");
			
			nomeEmpresa = separaEmpresa[0]+"E"+separaEmpresa[1];
		  }
		  
	      String empresa = nomeEmpresa +" ["+codEmpresa+"]";

	      String tipoUsuario = form.getField("tipoUsuario").getValue();
	       
		if(tipo.equals("I"))	
		{
			mapValorEmpresa = new HashMap();
			
			mapValorEmpresa.put("CodigoEmpresa", codEmpresa);
			mapValorEmpresa.put("NodigoEmpresa", nomeEmpresa);
			
			listEmpresaP.add(mapValorEmpresa);
		
		} 
		
		if(tipo.equals("E"))
		{
			mapValorEmpresa = new HashMap();
			
			mapValorEmpresa.put("CodigoEmpresa", codEmpresa);
			mapValorEmpresa.put("NodigoEmpresa", nomeEmpresa);
			
			listEmpresaE.add(mapValorEmpresa);
			
		} 
		
		if(tipo.equals("D"))
		{
			mapValorEmpresa = new HashMap();
			
			mapValorEmpresa.put("CodigoEmpresa", codEmpresa);
			mapValorEmpresa.put("NodigoEmpresa", nomeEmpresa);
			
			listEmpresaD.add(mapValorEmpresa);
				
		} 
		if(tipo.equals("J"))
		{
			mapValorEmpresa = new HashMap();
			
			mapValorEmpresa.put("CodigoEmpresa", codEmpresa);
			mapValorEmpresa.put("NodigoEmpresa", nomeEmpresa);
			
			listEmpresaJ.add(mapValorEmpresa);		
					
		} 
		if(tipo.equals("F"))
		{
			mapValorEmpresa = new HashMap();
			
			mapValorEmpresa.put("CodigoEmpresa", codEmpresa);
			mapValorEmpresa.put("NodigoEmpresa", nomeEmpresa);
			
			listEmpresaF.add(mapValorEmpresa);		
						
		}
	  }  

	  rs.close();  
	  prStmt.close();
	        
	    
	  mapEmpresa.put("P", listEmpresaP);
	  mapEmpresa.put("E", listEmpresaE);
	  mapEmpresa.put("D", listEmpresaD);
	  mapEmpresa.put("J", listEmpresaJ);
	  mapEmpresa.put("F", listEmpresaF);
	  
	  }
	  catch (Exception ex) {  
	    System.out.println(ex);

	  } finally {  

	    try {  
	      dbCxn.close();  	      
	    } catch (SQLException sqlEx) {  

	    }  
	    dbCxn = null;  

	  } 

	return mapEmpresa;
	
	
	
	
	
	
	
	
	
	
	
}
