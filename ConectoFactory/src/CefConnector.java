import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import openconnector.AbstractConnector;
import openconnector.Connector;
import openconnector.ConnectorConfig;
import openconnector.ConnectorException;
import openconnector.Filter;
import sailpoint.integration.Cryptographer;

public class CefConnector extends AbstractConnector {
	
    /* CONSTANTS WHICH ARE USED TO FETCH THE VALUES FROM DATABASE*/

    public  final String ATTR_USERID = "UserID";   // used as nativeIdentityAttribute for account objectType.
    public  final String ATTR_FIRSTNAME = "FirstName";
    public  final String ATTR_LASTNAME = "LastName";
    public  final String ATTR_EMAIL = "Email";
    public  final String ATTR_GROUPS = "Groups";
    public  final String ATTR_PASSWORD = "*password*";

    public  final String GROUP_ATTR_NAME = "GroupName"; // used as nativeIdentityAttribute for group objectType.
    public  final String GROUP_ATTR_DESCRIPTION = "GroupDesc";

    private String m_sDriverName; 
    private String m_sUser;
    private String m_sPasswd;
    private String m_sUrl;
    
    private Connection m_dbConn;     
    private boolean m_bIsDriverLoaded ;

    public static final  String EMPTY_STR = " "; 

    private List<Map<String,Object>> resultObjectList;
    private Iterator<Map<String, Object>> it;
    private Map<String,Object> account;
    private Map<String,Object> group;
    
    
    Connector abas;
    
    sailpoint.connector2.Connector abassss = new sailpoint.connector2.Connecto();
    
    abassss.
    
    abas.
    
    
    
    abas.g
    
    
    
    
    
    
    public CefConnector() throws Exception 
	{

        super();
        m_bIsDriverLoaded = false;
        it = null;
       }
      
    public CefConnector(ConnectorConfig config, openconnector.Log log) throws Exception 
	{
        super(config, log);
        m_bIsDriverLoaded = false;
        it = null;

    } 
    
    ////////////////////////////////////////////////////////////////////////////
    //
    // CONNECTOR CAPABILITIES
    //
    ////////////////////////////////////////////////////////////////////////////	
	
    public List<String> getSupportedObjectTypes() 
	{
        String funcName="getSupportedObjectTypes";
        enter(funcName);
        List<String> types = super.getSupportedObjectTypes();
        types.add(OBJECT_TYPE_GROUP); 
        exit(funcName);
        return types;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    //
    // CONNECTOR DIAGNOSTICS / UTILITIES
    //
    ////////////////////////////////////////////////////////////////////////////
    
    public void testConnection() throws ConnectorException 
	{

        String funcName = "testConnection";
        enter(funcName);
        try
        {
            init();

        }
        catch (Exception e)
        {
            if (log.isDebugEnabled())
                log.error("Error for TestConnection ",e);
            throw new ConnectorException("Test Connection failed with message : " + e.getMessage() + ".  Please check the connection details.");
        }
        
        //close();
        exit(funcName);

    }
    
	 ////////////////////////////////////////////////////////////////////////////
    //
    // OBJECT ACCESS
    //
    ////////////////////////////////////////////////////////////////////////////	
    
	@Override
	public Iterator<Map<String, Object>> iterate(Filter arg0)
			throws ConnectorException, UnsupportedOperationException { 

		String funcName="iterate";
        String filter = "*";              
        enter(funcName);
        
        it = null;
               
        if (OBJECT_TYPE_ACCOUNT.equals(this.objectType))
            {
        	it = getAccounts(filter);
            }
        else if (OBJECT_TYPE_GROUP.equals(this.objectType))
            {
        		it = getGroups(filter);
            }
        else
            throw new ConnectorException("Unhandled object type: " + this.objectType);
        
        exit(funcName);
        
		return it;
	}
	
	public Map<String,Object> read(String nativeIdentifier)
    throws ConnectorException 
	   {
        String funcName="read";
        account = null;
        group = null;
        it = null;
        
        enter(funcName);
        
        if (OBJECT_TYPE_ACCOUNT.equals(this.objectType))
            {
        		it = getAccounts(nativeIdentifier);
        		
        		if (it.hasNext()) {
        			account = it.next();
        			exit(funcName);
        			return account;
        		}
            }
            else if (OBJECT_TYPE_GROUP.equals(this.objectType))
            {
                	it = getGroups(nativeIdentifier);
            		
            		if (it.hasNext()) {
            			group = it.next();
            			exit(funcName);
            			return group;
            		}
                }
            else
            throw new ConnectorException("Unhandled object type: " + this.objectType);
        
        exit(funcName);
        return null;
    }
     
    ////////////////////////////////////////////////////////////////////////////
    //
    // METODOS AUXILIARES
    //
    //////////////////////////////////////////////////////////////////////////// 

    void enter(String functionName)
    {     	
        if (log.isDebugEnabled())
        	log.debug("Entering " + functionName + " ...");   	
    }

    void exit(String functionName)
    {
        if (log.isDebugEnabled())
            log.debug("Exitting " + functionName + " ...");
    }
    
    public void init()   throws Exception
    {

        String funcName="init";
        enter(funcName);
        
        if(!m_bIsDriverLoaded)
        {
            m_sUser = config.getString("user");
            m_sPasswd = config.getString("password");
            m_sUrl = config.getString("url");
            m_sDriverName = config.getString("driverClass");
            
            Class.forName(m_sDriverName);
            if (log.isDebugEnabled())
                log.debug("driver loaded ");
            m_dbConn = DriverManager.getConnection(m_sUrl,m_sUser,m_sPasswd);
            m_bIsDriverLoaded = true;
        }
        
        exit(funcName);

    }
    
	 public void close() 
	    {
	        String funcName="close";
	        enter(funcName);

	        try
	        {

	            if (null != m_dbConn)
	            {
	                m_dbConn.close();
	            }

	        }
	        catch (Exception e)
	        {
	            if (log.isDebugEnabled())
	                log.debug("Exception occurred ",e);
	        }
	        m_dbConn = null;
	        m_bIsDriverLoaded = false;
	        
	        exit(funcName);
	    }
	 
	 public Iterator<Map<String, Object>> getAccounts(String filter) {
		 
	     account = null; 
	     resultObjectList = null;
	     resultObjectList =  new ArrayList<Map<String,Object>>();
	     
	     ResultSet queryResult=null;
	     Statement stmt=null;
	     
	     String query=null;
	     if (filter.equalsIgnoreCase("*")) {
	    	 query = "select userid,firstname,lastname,email,islock,isrevoked from accounts";
	     }
	     else {
	    	 query = "select userid,firstname,lastname,email,islock,isrevoked from accounts where userid='" +  filter  + "'";
	    	
	     }
	     
	        try
	        {
	             init();
	             
		         stmt = m_dbConn.createStatement();
		         if (log.isDebugEnabled())
		             log.debug(" query " + query);
		         queryResult = stmt.executeQuery(query);
		
		         while (queryResult.next())
		         {
		        	 account  = new HashMap<String,Object>(); 
		        	 String userid = queryResult.getString("userid");
		             account.put(ATTR_USERID,userid);
		             account.put(ATTR_FIRSTNAME, queryResult.getString("firstname"));
		             account.put(ATTR_LASTNAME, queryResult.getString("lastname"));
		             account.put(ATTR_EMAIL, queryResult.getString("email"));
		             account.put(ATTR_GROUPS, getUserEntitlements(userid));
		
		             
		             if (queryResult.getString("userid").equals("Y"))
		            	 account.put(Connector.ATT_LOCKED, new Boolean(true));
		             else
		            	 account.put(Connector.ATT_LOCKED,new Boolean(false));
		
		             if (queryResult.getString("isrevoked").equals("Y"))
		            	 account.put(Connector.ATT_DISABLED, new Boolean(true));
		             else
		            	 account.put(Connector.ATT_DISABLED, new Boolean(false));
		             
		             resultObjectList.add(account);
		         }
		         
		         if (queryResult != null)
		        	 queryResult.close();

		         if (stmt != null)
		        	 stmt.close();
		         
		         close();
	        }
	        catch (Exception e)
	        {
	            if (log.isDebugEnabled())
	                log.debug("Exception occured " , e);
	            throw new ConnectorException(e);
	        }
	        
		 return resultObjectList.iterator();
	 }
	 
 public Iterator<Map<String, Object>> getGroups(String filter) {
		 
	     group = null; 
	     resultObjectList = null;
	     resultObjectList =  new ArrayList<Map<String,Object>>();
	     
	     ResultSet queryResult=null;
	     Statement stmt=null;	     
	     String query=null;
	     
	     if (filter.equalsIgnoreCase("*")) {
	    	 query = "select groupName,groupDesc from groups";
	     }
	     else {
	    	 query = "select groupName,groupDesc from groups where groupName='" +  filter  + "'";
	     }
	     
	        try
	        {
	             init();
	             
		         stmt = m_dbConn.createStatement();
		         if (log.isDebugEnabled())
		             log.debug(" query " + query);
		         queryResult = stmt.executeQuery(query);
		
		         while (queryResult.next())
		         {
		        	 group = new HashMap<String,Object>();
	                 group.put(GROUP_ATTR_NAME,queryResult.getString("groupName") );
	                 group.put(GROUP_ATTR_DESCRIPTION, queryResult.getString("groupDesc"));		             
		             resultObjectList.add(group);
		         }
		         
		         if (queryResult != null)
		        	 queryResult.close();

		         if (stmt != null)
		        	 stmt.close();
		         
		         close();
	        }
	        catch (Exception e)
	        {
	            if (log.isDebugEnabled())
	                log.debug("Exception occured " , e);
	            throw new ConnectorException(e);
	        }
	        
		 return resultObjectList.iterator();
	 }
 
	 public ArrayList<String> getUserEntitlements(String userName)  throws Exception
	 {
	     String funcName="getUserEntitlements";
	     enter(funcName);
	     String query = "select groupid from connectionusergroup where userid='" + userName + "'";
	     ArrayList<String> entitlements = new ArrayList<String>();
	     Statement stmt = m_dbConn.createStatement();
	     ResultSet rs = stmt.executeQuery(query);
	     while (rs.next())
	     {
	    	 entitlements.add(rs.getString("groupid"));
	     }
	     if (rs != null)
	         rs.close();
	
	     rs = null;
	     if (stmt != null)
	         stmt.close();
	     stmt = null;
	     exit(funcName);
	     return entitlements;
	
	 }
}
