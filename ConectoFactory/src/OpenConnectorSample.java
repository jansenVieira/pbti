// (c) Copyright 2010 SailPoint Technologies, Inc., All Rights Reserved.


import java.io.IOException;

import java.net.MalformedURLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import openconnector.*;
import openconnector.Filter.Operator;




/**OpenConnector Sample.
 * This is sample connector that can be used as a reference for developing a new connector using openconnector interface.
 * This connector provisions entities in database.
 * @version  ConnectorFactory 6.0.
 *
 */
public class OpenConnectorSample extends AbstractConnector
{
    private String m_sDriverName; 
    private String m_sUser;
    private String m_sPasswd;
    private String m_sUrl;
    
   // private static final Log log = LogFactory.getLog(OpenConnectorSample.class);

    private int m_iChunkSize = 10;
    private Connection m_dbConn;
    private Iterator<Map<String, Object>> it;
    private Filter _filter;
    private String m_sLastUser ;
    private String m_sLastGroup ;
    
    private boolean m_bIsDriverLoaded ;

    public static final  String EMPTY_STR = " ";
	
	////////////////////////////////
    //
    // INNER CLASS 
    //
    ////////////////////////////////


    /**
     * An iterator that returns paging iterator of the maps that are returned.
     */

    private class PagingIterator implements Iterator<Map<String,Object>>
    {

        private Iterator<Map<String,Object>> itc;
        Map<String,Object> obj;
        public PagingIterator(Iterator<Map<String,Object>> it1)
        {
            this.itc = it;
        }

        /**
         * @version - ConnectorFactory 6.0.
         * @return boolean.
         *
         */
        public boolean hasNext()
        {
            if (it.hasNext() == false)
            {
                it = IterateNextPage(null);
                boolean hasNext = it.hasNext();

                return hasNext;
            }
            else
            {
                return it.hasNext();
            }
        }

        /**
         * @version - ConnectorFactory 6.0.
         * @return Map object.
         *
         */
        public Map<String,Object> next()
        {
            obj = it.next();
            if (obj != null)
                remove();
            return obj;
        }
        /**
         * @version - ConnectorFactory 6.0.
         *
         */

        public void remove() {

            it.remove();

        }
    }



    /* CONSTANTS WHICH ARE USED TO FETCH THE VALUES FROM DATABASE*/

    public  final String ATTR_USERID = "UserID";   // used as nativeIdentityAttribute for account objectType.
    public  final String ATTR_FIRSTNAME = "FirstName";
    public  final String ATTR_LASTNAME = "LastName";
    public  final String ATTR_EMAIL = "Email";
    public  final String ATTR_GROUPS = "Groups";
    public  final String ATTR_PASSWORD = "*password*";

    public  final String GROUP_ATTR_NAME = "GroupName"; // used as nativeIdentityAttribute for group objectType.
    public  final String GROUP_ATTR_DESCRIPTION = "GroupDesc";

    private  Map<String,Map<String,Object>> m_accountsMap =     new HashMap<String,Map<String,Object>>(); //stores account data
    private  Map<String,Map<String,Object>> m_groupsMap =     new HashMap<String,Map<String,Object>>();  //stores  group data

    /** Print all objects in memory to System.out.
     * @version - ConnectorFactory 6.0.
     *
     */
    public  void dump()
    {
        if (log.isDebugEnabled())
        {
            log.debug(m_accountsMap);
            log.debug(m_groupsMap);
        }
    }


    /** Enter method is used to start the application method.
     * @version - ConnectorFactory 6.0.
     *
     */
    void enter(String functionName)
    {   
        if (log.isDebugEnabled())
            log.debug("Entering " + functionName + " ...");
    }

    /** Exit method is used to get out of the application method.
   
     * @version - ConnectorFactory 6.0.
     *
     */
    void exit(String functionName)
    {
        if (log.isDebugEnabled())
            log.debug("Exitting " + functionName + " ...");
    }


    ////////////////////////////////////////////////////////////////////////////
    //
    // CONSTRUCTORS
    //
    ////////////////////////////////////////////////////////////////////////////


    /** DEFAULY CONSTRUCTOR Which initialise the variables.
     * @version - ConnectorFactory 6.0.
     * @exception java.lang.Exception.
     *
     */
    public OpenConnectorSample() throws Exception 
	{

        super();
        m_iChunkSize = 5;
        it =null;
        m_sLastUser =EMPTY_STR;
        m_sLastGroup=EMPTY_STR;
        m_bIsDriverLoaded = false;
    }


    /**Constructor for an  OpenConnectorSample.
     * @version ConnectorFactory 6.0.
     * @param               Config                    
     * @param               Log                       
     * @exception java.lang.Exception.
     *
     */
    public OpenConnectorSample(ConnectorConfig config, openconnector.Log log) throws Exception 
	{
        super(config, log);
        m_iChunkSize = 5;
        it =null;
        m_sLastUser =EMPTY_STR;
        m_sLastGroup=EMPTY_STR;
        m_bIsDriverLoaded = false;

    } 

    ////////////////////////////////////////////////////////////////////////////
    //
    // CONNECTOR CAPABILITIES
    //
    ////////////////////////////////////////////////////////////////////////////	

	
	 /** Method getSupportedFeatures :- this method used to support all the features and returns the special array list.
     *  @version   ConnectorFactory 6.0.
     * @param     objectType
     * @return    List<Feature>
     *
     */
    public List<Feature> getSupportedFeatures(String objectType) 
	{
        String funcName="getSupportedFeatures";
        enter(funcName);  
        exit(funcName);   
        return Arrays.asList(Feature.values());

    }

    /** Method  getSupportedObjectTypes :- Support accounts and groups.
     * @version   ConnectorFactory 6.0.
     * @return    group entity types in list format List<String>
     */
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
    // CONNECTOR DIAGNOSTICS
    //
    ////////////////////////////////////////////////////////////////////////////
	
	 /** Method testConnection :- this method is used to test the  connection and validate the credentials  with the database
     * @version   ConnectorFactory 6.0.
     * @exception ConnectorException
     *
     */
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
        exit(funcName);

    }

 /** Method discoverSchema :- Discover the Schema for the currently configured object type.
     * @version   ConnectorFactory 6.0.
     * @exception UnsupportedOperationException
     *
     */
    public Schema discoverSchema() throws ConnectorException, UnsupportedOperationException 
	{
        String funcName  ="discoverSchema";
        enter(funcName);
        exit(funcName);
        throw new UnsupportedOperationException();

    }

	 ////////////////////////////////////////////////////////////////////////////
    //
    // OBJECT ACCESS
    //
    ////////////////////////////////////////////////////////////////////////////	
	
    /**
     * Return the object of the configured object type that has the given
     * native identifier.
     * 
     * @param  id  A unique identifier for the object.
     * 
     * @return The object that was read from the resource, or null if an object
     *         with the given id was not found.
     *
     * @throws ConnectorException  If the operation fails.
     * @throws UnsupportedOperationException  If the GET feature is not
     *    supported.
     */
	public Map<String,Object> read(String nativeIdentifier)
    throws ConnectorException 
	   {
        String funcName="read";
        enter(funcName);
        Map<String,Object> acct  = null; 
        Map<String,Object> grp  = null; 

        ResultSet queryResult=null;
        Statement stmt=null;

        try
        {


            init();
            String query=null;


            if (OBJECT_TYPE_ACCOUNT.equals(this.objectType))
            {

                query = "select userid,firstname,lastname,email,islock,isrevoked from accounts where userid='" +  nativeIdentifier  + "'";
                stmt = m_dbConn.createStatement();
                if (log.isDebugEnabled())
                    log.debug(" query " + query);
                queryResult = stmt.executeQuery(query);




                if (queryResult.next())
                {
                    acct  = new HashMap<String,Object>(); 
                    String userid =   queryResult.getString("userid");
                    acct.put(ATTR_USERID,userid );
                    acct.put(ATTR_FIRSTNAME, queryResult.getString("firstname"));
                    acct.put(ATTR_LASTNAME, queryResult.getString("lastname"));
                    acct.put(ATTR_EMAIL, queryResult.getString("email"));
                    acct.put(ATTR_GROUPS, getDbUserConn(userid));

                    String temp =  queryResult.getString("islock");
                    if (temp.equals("Y"))
                        acct.put(Connector.ATT_LOCKED, new Boolean(true));
                    else
                        acct.put(Connector.ATT_LOCKED,new Boolean(false));

                    temp =  queryResult.getString("isrevoked");
                    if (temp.equals("Y"))
                        acct.put(Connector.ATT_DISABLED, new Boolean(true));
                    else
                        acct.put(Connector.ATT_DISABLED, new Boolean(false));

                }

            }
            else if (OBJECT_TYPE_GROUP.equals(this.objectType))
            {
                query = "select groupName,groupDesc from groups where groupName='" +  nativeIdentifier  + "'";
                stmt = m_dbConn.createStatement();
                if (log.isDebugEnabled())
                    log.debug(" query " + query);
                queryResult = stmt.executeQuery(query);

                if (queryResult.next())
                {
                    grp = new HashMap<String,Object>();
                    String groupName =   queryResult.getString("groupName");
                    grp.put(GROUP_ATTR_NAME,groupName );
                    grp.put(GROUP_ATTR_DESCRIPTION, queryResult.getString("groupDesc"));
                }

            }

            if (queryResult != null)
                queryResult.close();

            if (stmt != null)
                stmt.close();

        }
        catch (Exception e)
        {
            // 
            if (log.isDebugEnabled())
                log.debug("Exception occured " , e);
            throw new ConnectorException(e);
        }
        exit(funcName);
        if (OBJECT_TYPE_ACCOUNT.equals(this.objectType))
            return acct;
        else if (OBJECT_TYPE_GROUP.equals(this.objectType))
            return grp;
        else
            throw new ConnectorException("Unhandled object type: " + this.objectType);
    }
	
	 /**
     * Return an iterator over the objects of the configured object type that
     * match the given filter (or all objects if no filter is specified).
     * 
     * @param  filter  The possibly null filter to use to constrain which
     *                 objects are returned.
     * 
     * @return An iterator over the object of the configured object type that
     *         match the given filter.
     *
     * @throws ConnectorException  If the operation fails.
     * @throws UnsupportedOperationException  If the ITERATE feature is not
     *    supported.
     */

	public Iterator<Map<String,Object>> iterate(Filter filter) 
	{

        /* Return the iterator on a copy of the list to avoid concurrent mod
         exceptions if entries are added/removed while iterating.*/
      
        String funcName = "iterate";
        enter(funcName);
        try
        {

            _filter = filter;
            it = new ArrayList<Map<String,Object>>(getObjectsMap().values()).iterator();
        }
        catch (Exception e)
        {
            
            if (log.isDebugEnabled())
                log.debug("Exception occured " , e);
            throw new ConnectorException(e);  
        }

        
       
        exit(funcName);
        return new PagingIterator(it);
    }
	
	/**
    * Create an object of the currently configured type on the resource.
    * 
    * @param  nativeIdentifier  The nativeindentifier of the object to create.
    * @param  items   The initial objecttype  attributes.
    * 
    * @throws ConnectorException  If the operation fails.
    * @throws ObjectAlreadyExistsException  If this object already exists on the managed system.
    * @throws UnsupportedOperationException  If the CREATE feature is not  supported.
    */
	
	  public  Result create(String nativeIdentifier, List<Item> items)
    throws ConnectorException, ObjectAlreadyExistsException 
	{

        Result result = new Result(Result.Status.Committed);
        String userid = "", fname = "", passwd = "", lname = "", email = "", gdesc = "";
        int quota = 0;
        List<String> group = new ArrayList<String>();

        String funcName="create";
        enter(funcName);

        
        
        
        
        try
        {
            init();
//            Object existing = read(nativeIdentifier);
            
//            if (null != existing)
//            {
//                throw new ObjectAlreadyExistsException(nativeIdentifier);
//            }teste


            for (openconnector.Item item : items)
            {
                if (item.getName().equalsIgnoreCase(ATTR_GROUPS))
                {
                    if (item.getValue() instanceof String)
                    {
                        group.add((String)item.getValue());

                    }
                    else
                    {
                        group = (List) item.getValue();
                        
                    }
                    if (log.isDebugEnabled())
                        log.debug("Group - "+group);
                }

                if (item.getName().equalsIgnoreCase(ATTR_FIRSTNAME))
                {
                    fname = (String)item.getValue();
                    if (log.isDebugEnabled())
                        log.debug("Givenname - "+fname);
                }
                if (item.getName().equalsIgnoreCase(ATTR_LASTNAME))
                {
                    lname = (String)item.getValue();
                    if (log.isDebugEnabled())
                        log.debug("Familyname - "+lname);
                }
                if (item.getName().equalsIgnoreCase(ATTR_PASSWORD))
                {
                    passwd = (String)item.getValue();
                    if (log.isDebugEnabled())
                        log.debug("Password - "+passwd);
                }
                if (item.getName().equalsIgnoreCase(ATTR_EMAIL))
                {
                    email = (String)item.getValue();
                    if (log.isDebugEnabled())
                        log.debug("Adminp - "+email);
                }

                if (item.getName().equalsIgnoreCase(GROUP_ATTR_DESCRIPTION))
                {
                    gdesc = (String)item.getValue();
                    if (log.isDebugEnabled())
                        log.debug("description - "+gdesc);
                }

            }

            if (OBJECT_TYPE_ACCOUNT.equals(this.objectType))
            {

                createUser(nativeIdentifier,passwd,fname,lname,email);
                for (int i=0;i<group.size();i++)
                {
                    try
                    {
                        if (log.isDebugEnabled())
                            log.debug("Group is  " + group.get(i));

                        addMemberToGroup(nativeIdentifier,group.get(i),false);
                    }
                    catch (Exception e)
                    {
                       
                        if (log.isDebugEnabled())
                            log.debug("Excpe1 " , e);
                    }
                }
            }
            else if (OBJECT_TYPE_GROUP.equals(this.objectType))
            {

                createGroup(nativeIdentifier,gdesc);

            }
            if (log.isDebugEnabled())
                log.debug("List size is  " +  group.size());


        }
        catch (Exception e)
        {
            result = new Result(Result.Status.Failed);

            if (log.isDebugEnabled())
                log.debug("Exception occured " , e);
            String message ="Exception occured while creating the entity";
            result.add(message);
        }

        exit(funcName);
        return result;
    }
	 /**
     * Update the object of the given type using the given item list.
     * 
     * @param  nativeIdentitifer The object to update
     * @param  items A list of items to update
     *
     * @throws ConnectorException  If the operation fails.
     * @throws ObjectNotFoundException  If the object to update cannot be found.
     * @throws UnsupportedOperationException  If the UPDATE feature is not
     *    supported or the options map contains operations that are not
     *    supported.
     */
	 
	 @SuppressWarnings("unchecked")
    public Result update(String nativeIdentifier, List<Item> items)
    throws ConnectorException, ObjectNotFoundException 
	{

        Result result = new Result(Result.Status.Committed);
        String fname = null;
        String lname = null;
        String email = null;
        String gdesc = null;

        String funcName ="update";
        enter(funcName);

        try
        {
           

            init();
            if (items != null)
            {
                for (Item item : items)
                {

                    String name = item.getName();
                    Object value = item.getValue();
                    Item.Operation op = item.getOperation();

                    if (OBJECT_TYPE_ACCOUNT.equals(this.objectType))
                    {
                        switch (op)
                        {
                        case Add: {
                                if (log.isDebugEnabled())
                                    log.debug("Inside Add connection"  + item.getValue());
                                List group = new ArrayList();

                                if (item.getValue() instanceof String)
                                {
                                    group.add(item.getValue());
                                }
                                else
                                {
                                    group = (List) item.getValue();
                                }
                                if (log.isDebugEnabled())
                                {
                                    log.debug("Group/s - "+group);
                                    log.debug("Before add account to group");
                                }
                                for (int i=0; i<group.size(); i++)
                                {
                                    try
                                    {
                                        addMemberToGroup(nativeIdentifier,(String)group.get(i),true);     
                                    }
                                    catch (Exception e)
                                    {
                                        if (log.isDebugEnabled())
                                            log.debug("Excpe2 " , e);
                                        throw e;
								   
                                    }
                                }
                                if (log.isDebugEnabled())
                                    log.debug("After add account to group");

                            }
                            break;

                        case Remove: {
                                if (log.isDebugEnabled())
                                    log.debug("Inside Delete connection");
                                List group = new ArrayList();
                                if (item.getValue() instanceof String)
                                    group.add(item.getValue());
                                else
                                    group = (List) item.getValue();
                                if (log.isDebugEnabled())
                                {
                                    log.debug("Group/s - "+group);
                                    log.debug("Before delete account from group");
                                }
                                for (int i=0; i<group.size(); i++)
                                {
                                    deleteMemberFromGroup(nativeIdentifier,(String)group.get(i));
                                }
                                if (log.isDebugEnabled())
                                    log.debug("After delete account from group");
                            }
                            break;

                        case Set: {
                                if (log.isDebugEnabled())
                                    log.debug("Inside Set==> " + item.getName() + " ==  " + item.getValue());
                                if (item.getName().equalsIgnoreCase("password"))
                                {
                                    result = updatePassword(nativeIdentifier, (String)item.getValue());
                                }
                                else if (item.getName().equalsIgnoreCase(ATTR_FIRSTNAME))
                                {
                                    fname = (String)item.getValue();
                                    if (log.isDebugEnabled())
                                        log.debug("fname - "+fname);
                                }
                                else if (item.getName().equalsIgnoreCase(ATTR_LASTNAME))
                                {
                                    lname = (String)item.getValue();
                                    if (log.isDebugEnabled())
                                        log.debug("lname - "+lname);
                                }
                                else if (item.getName().equalsIgnoreCase(ATTR_EMAIL))
                                {
                                    email = (String)item.getValue();
                                    if (log.isDebugEnabled())
                                        log.debug("mail - "+email);
                                }
                            }
                            break;

                        default:
                            throw new IllegalArgumentException("Unknown operation: " + op);
                        }
                    }
                    else if (OBJECT_TYPE_GROUP.equals(this.objectType))
                    {
                        switch (op)
                        {
                        case Set: 
                            {
                                if (item.getName().equalsIgnoreCase(GROUP_ATTR_DESCRIPTION))
                                {
                                    gdesc = (String)item.getValue();
                                    if (log.isDebugEnabled())
                                        log.debug("description - "+gdesc);
                                }
                            }
                        }
                    }

                }



                if (fname != null || lname != null || email != null)
                {
                    if (log.isDebugEnabled())
                        log.debug("Firing update user ");
                    updateUser(nativeIdentifier,fname,lname,email);
                }
                else if (OBJECT_TYPE_GROUP.equals(this.objectType) && gdesc !=null)
                {
                    updateGroup(nativeIdentifier,gdesc);              
                }
            
            }
        }
        catch (Exception e)
        {
            result.setStatus(Result.Status.Failed);
            result.add(e.getMessage());
            if (log.isDebugEnabled())
                log.debug("Exception occured " , e);
            e.printStackTrace();
        }
        exit(funcName);
        return result;
    }
	
	
    /**
     * Delete the object of the configured object type that has the given 
     * native identifier.
     * 
     * @param  nativeIdentitifer  The value for the identity attribute.
     * @param  options            Options that may influence the deletion.
     *
     * @throws ConnectorException  If the operation fails.
     * @throws UnsupportedOperationException  If the DELETE feature is not
     *    supported.
     *
     */
	 public Result delete(String nativeIdentifier, Map<String,Object> options)
    throws ConnectorException, ObjectNotFoundException 
	{
        Result result = new Result(Result.Status.Committed);
        String funcName  ="delete";
        enter(funcName);

        try
        {
            init();

            Map<String,Object> obj = read(nativeIdentifier);
            if (null == obj)
            {
                throw new ObjectNotFoundException(nativeIdentifier);
            }
            /*
            need to delete the connections first and then the user
            */

            if (OBJECT_TYPE_ACCOUNT.equals(this.objectType))
            {
                deleteMembersOfUsers(nativeIdentifier);
                deleteUser(nativeIdentifier);
            }
            else if (OBJECT_TYPE_GROUP.equals(this.objectType))
            {
                deleteGroup(nativeIdentifier);
            }
            else
            {
                throw new ConnectorException("Object type " + this.objectType + " not supported");
            }


        }
        catch (Exception e)
        {
            result.setStatus(Result.Status.Failed);
            result.add(e.getMessage());
            if (log.isDebugEnabled())
                log.debug("Exception occured " , e);
        }

        exit(funcName);
        return result;
    }
	
	/**
     * Enable the object of the configured object type that has the given native
     * identifier.
     * 
     * @param  id The value for the identity attribute.
     * @param  options           Options that may influence the enable.
     *
     * @throws ConnectorException  If the operation fails.
     * @throws ObjectNotFoundException  If the object to enable cannot be found.
     * @throws UnsupportedOperationException  If the ENABLE feature is not
     *    supported.
     */
	 public Result enable(String nativeIdentifier, Map<String,Object> options)
    throws ConnectorException, ObjectNotFoundException 
	{
        Result result = new Result(Result.Status.Committed);

        String funcName  ="enable";
        enter(funcName);

        try
        {
            init();

            Map<String,Object> obj = read(nativeIdentifier);
            if (null == obj)
            {
                throw new ObjectNotFoundException(nativeIdentifier);
            }
            enableUser(nativeIdentifier);

        }
        catch (Exception e)
        {
            result.setStatus(Result.Status.Failed);
            result.add(e.getMessage());
            if (log.isDebugEnabled())
                log.debug("Exception occured " , e);
        }

        exit(funcName);
        return result;
    }
	
	
     /**
     * Disable the object of the configured object type that has the given
     * native identifier.
     * 
     * @param  id                The value for the identity attribute.
     * @param  options           Options that may influence the disable.
     *
     * @throws ConnectorException  If the operation fails.
     * @throws ObjectNotFoundException  If the object to disable cannot be found.
     * @throws UnsupportedOperationException  If the DISABLE feature is not
     *    supported.
     */
	 public Result disable(String nativeIdentifier, Map<String,Object> options)
    throws ConnectorException, ObjectNotFoundException 
	{
        Result result = new Result(Result.Status.Committed);
        String funcName  ="disable";
        enter(funcName);

        if (log.isDebugEnabled())
            log.debug("in connector disable");
        try
        {
            init();

            Map<String,Object> obj = read(nativeIdentifier);
            if (null == obj)
            {
                throw new ObjectNotFoundException(nativeIdentifier);
            }
            disableUser(nativeIdentifier);

        }
        catch (Exception e)
        {
            result.setStatus(Result.Status.Failed);
            result.add(e.getMessage());
            if (log.isDebugEnabled())
                log.debug("Exception occured " , e);
        }
        exit(funcName);
        return result;

    }
	  /**
     * Unlock the object of the configured object type that has the given native
     * identifier.  An account is typically locked due to excessive invalid
     * login attempts, whereas a disable is usually performed on accounts that
     * are no longer in use.
     * 
     * @param  nativeIdentifier   The value for the identity attribute.
     * @param  options           Options that may influence the unlock.
     *
     * @throws ConnectorException  If the operation fails.
     * @throws ObjectNotFoundException  If the object to unlock cannot be found.
     * @throws UnsupportedOperationException  If the UNLOCK feature is not
     *    supported.
     */
	 public Result unlock(String nativeIdentifier, Map<String,Object> options)
    throws ConnectorException, ObjectNotFoundException 
	{
        Result result = new Result(Result.Status.Committed);
        String funcName  ="unlock";
        enter(funcName);

        try
        {
            init();
            Map<String,Object> obj = read(nativeIdentifier);
            if (null == obj)
            {
                throw new ObjectNotFoundException(nativeIdentifier);
            }
            unLockUser(nativeIdentifier);

        }
        catch (Exception e)
        {
            result.setStatus(Result.Status.Failed);
            result.add(e.getMessage());
            if (log.isDebugEnabled())
                log.debug("Exception occured " , e);
        }
        exit(funcName);
        return result;
    }
	
	  /**
     * Set the password for the given object to the newPassword.  The current
     * password may be passed in, but is not typically required.  If available,
     * the current password should be passed in, since some resources require
     * this for full behavior around password policy checking, password history,
     * etc...
     * 
     * @param  nativeIdentifier   The value for the identity attribute.
     * @param  newPassword       The password to set for the account.
     * @param  currentPassword   The current password of the account.  This
     *                           should be provided if available, but is not
     *                           always required.
     * @param  expiration        The expiration date for the password.
     * @param  options           An optional map of options that can provide
     *                           additional information about the password
     *                           change.
     *
     * @throws ConnectorException  If the operation fails.
     * @throws ObjectNotFoundException  If the object to set the password on
     *    cannot be found.
     * @throws UnsupportedOperationException  If the SET_PASSWORD feature is not
     *    supported.
     */
	  
	  public Result setPassword(String nativeIdentifier, String newPassword,
                              String currentPassword, Date expiration,
                              Map<String,Object> options)
    throws ConnectorException, ObjectNotFoundException,
    UnsupportedOperationException 
	{

        Result result = new Result(Result.Status.Committed);
        String funcName  ="setPassword2";
        enter(funcName);

        try
        {
            init();

            Map<String,Object> obj = read(nativeIdentifier);
            if (null == obj )
            {
                throw new ObjectNotFoundException(nativeIdentifier);
            }
            updatePasswd(nativeIdentifier,newPassword);

        }
        catch (Exception e)
        {
            result.setStatus(Result.Status.Failed);
            result.add(e.getMessage());
            if (log.isDebugEnabled())
                log.debug("Exception occured " , e);
        }
        exit(funcName);
        return result;
    }
	
	 /**
     * Attempt to authenticate to the underlying resource using the given
     * identity and password.  The identity may be the value of the identity
     * attribute, but could also be a value that can be used to search for the
     * account (eg - sAMAccountName, full name, etc...).  If successful, this
     * returns the account that was authenticated to.
     * 
     * @param  identity  A value for an identifying attribute of the account to
     *                   authenticate with.
     * @param  password  The password to use to authenticate.
     * 
     * @return The authenticated account if authentication was successful.
     * 
     * @throws ConnectorException  If the operation fails.
     * @throws AuthenticationFailedException  If authentication failed.
     * @throws ExpiredPasswordException  If authentication failed because of an
     *    expired password.
     * @throws ObjectNotFoundException  If an object that matches the given
     *    identity could not be found.
     * @throws UnsupportedOperationException  If the AUTHENTICATE feature is not
     *    supported.
     */

 public Map<String,Object> authenticate(String identity, String password)
    throws ConnectorException, ObjectNotFoundException,
    AuthenticationFailedException, ExpiredPasswordException {


        Map<String,Object> obj = new HashMap<String,Object>();
        String funcName  ="authenticate";
        enter(funcName);

        ArrayList<String> arr = null;

        try
        {
            init();
            String search = config.getString("authSearchAttributes");
            if (search != null)
                arr = getSearchAttributes(search);


            if (arr == null)
                arr = new ArrayList<String>();

            if (arr.size() ==0)
                arr.add("userid");

            String query ="";



            for (int i=0;i<arr.size();i++)
            {
                query += arr.get(i) + "='" + identity  + "'";
                if (arr.size() > 1 )
                    query += " or ";
            }

            if (query.length() > 0)
                query = query.substring(0,query.length()-4);
            if (log.isDebugEnabled())
                log.debug("search is  " + search);
            arr= searchUser(query);

            if (arr != null && arr.size() > 0)
            {

                if (arr.size() > 1)
                {
                    /*
                    this means we have got multiple accounts for the query which is not right
                    so throw an exception
                    */
                    throw new  ConnectorException("Multiple accounts found for string " + identity);
                }
                String pwd = getPassword(arr.get(0));
                if (null == pwd)
                {
                    if (log.isDebugEnabled())
                        log.debug("   obj does not exist..");
                    throw new ObjectNotFoundException(identity);
                }

                if (pwd.equals(password))
                {
                    if (log.isDebugEnabled())
                        log.debug("   authenticate sucess..");
                    obj.put(ATTR_USERID, arr.get(0));
                }
                else
                {
                    if (log.isDebugEnabled())
                        log.debug("   authenticate failed..");
                    throw new AuthenticationFailedException("Authentication failed");
                }
            }
            else
            {
                if (log.isDebugEnabled())
                    log.debug("obj does not exist..");
                throw new ObjectNotFoundException(identity);

            }

        }
        catch (AuthenticationFailedException e)
        {
            if (log.isDebugEnabled())
                log.debug("AuthenticationFailedException occured " , e);
            throw new AuthenticationFailedException(e);

        }
        catch (ObjectNotFoundException e)
        {

            if (log.isDebugEnabled())
                log.debug("ObjectNotFoundException occured " , e);
            throw new ObjectNotFoundException(e);
        }
        catch (Exception e)
        {
            if (log.isDebugEnabled())
                log.debug("Exception occured " , e);
            throw new ConnectorException(e);
        }


        exit(funcName);

        /* If there was a problem we would have thrown already.  Return the
         matched object.*/
        return obj;

    }
	 /**
     * Using the id passed into this method to check the 
     * request to see if has completed.
     * 
     * This method is used to poll the system for status when things
     * in any of the results are marked Queued and have a requestToken. 
     *
     * The new status, returned result's errors, and warnings will be merged 
     * back into the main project.  
     * 
     * Returning a null result will have no change
     * on the request and it will remained queued.
     * 
     * @param id
     * @return Result
     */
    public Result checkStatus(String id) 
      throws ConnectorException, ObjectNotFoundException, UnsupportedOperationException
    {

        Result result = new Result();
        return result;
    }
	
	
    /**
    * Close the underlying resources that are being held by this connector.
    * Clients MUST call this when they are done with a connector or else there
    * may be resource leaks.
    */
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
        exit(funcName);
    }



    /**
     * @version ConnectorFactory 6.0.
     * @exception java.lang.Exception.
     *
     */


    public void init()   throws Exception
    {

        String funcName="init";
        enter(funcName);
        
        if(!m_bIsDriverLoaded)
        {
            m_sUser = config.getString("user");
            m_sPasswd = config.getString("password");
            m_sUrl = config.getString("url");
            m_sDriverName = config.getString("driver");
    
            Class.forName(m_sDriverName);
            if (log.isDebugEnabled())
                log.debug("driver loaded ");
            m_dbConn = DriverManager.getConnection(m_sUrl,m_sUser,m_sPasswd);
            m_bIsDriverLoaded = true;
        }

        
        exit(funcName);

    }



     /** Method  searchUser :- This will search the user based on the search criteria.
     * @version   ConnectorFactory 6.0.
     * @param      str                                       String
     * @return    ArrayList of userids
     * @exception java.lang.Exception
     */
    public ArrayList<String> searchUser(String str) throws Exception
    {
        String funcName="searchUser";
        enter(funcName); 
        ArrayList<String> arrList = new ArrayList<String>();
        String query = "select userid  from accounts where " +  str  ;
        Statement stmt = m_dbConn.createStatement();
        if (log.isDebugEnabled())
            log.debug("getSearchUser query " + query);
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next())
        {
            arrList.add(rs.getString("userid"));  
        }

        rs.close();
        rs = null;
        stmt.close();
        stmt = null;
        exit(funcName); 
        return arrList;
    }

    /** Method  getPassword :- getPassword is used to find out the password in database on the basis of query userid passed throught the method 
     * @version   ConnectorFactory 6.0.
     * @param     str                                       String
     * @return    String (password)
     * @exception java.lang.Exception
     */


    public String getPassword(String str)  throws Exception
    {
        String funcName="getPassword";
        enter(funcName); 
        String pwd = null;
        String query = "select password from accounts where userid='" +  str  + "'";
        Statement stmt = m_dbConn.createStatement();
        if (log.isDebugEnabled())
            log.debug("getPassword query " + query);
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next())
        {
            pwd= rs.getString("password");
        }
        if (rs != null)
            rs.close();
        stmt.close();
        stmt = null;
        exit(funcName); 
        return pwd;

    }

    /** Method  deleteUser :-this method is used to delete the user from database by reference id that is primary key
     * @version   ConnectorFactory 6.0.
     * @param     str                                      String
     * @exception java.lang.Exception
     */

    public void deleteUser(String str)  throws Exception
    {
        String funcName="deleteUser";
        enter(funcName);
        String query = "delete from accounts where userid='" + str + "'";
        Statement stmt = m_dbConn.createStatement();
        if (log.isDebugEnabled())
            log.debug("deleteUser query " + query);
        stmt.executeUpdate(query);
        stmt.close();
        stmt = null;
        exit(funcName); 

    }

    /** Method  deleteGroup :- deleteGroup is used to delete the group from database by reference id that is primary key
     * @version   ConnectorFactory 6.0.
     * @param     str                                      String
     * @exception java.lang.Exception
     */

    public void deleteGroup(String str)  throws Exception
    {
        String funcName="deleteGroup";
        enter(funcName);
        String query = "delete from groups where groupName='" + str + "'";
        Statement stmt = m_dbConn.createStatement();
        if (log.isDebugEnabled())
            log.debug("deleteGroup query " + query);
        stmt.executeUpdate(query);
        stmt.close();
        stmt = null;
        exit(funcName); 
    }


    /** Method  deleteGroup :- enableUser is used to disable the "isRevoke" state.
     * @version   ConnectorFactory 6.0.
     * @param     str                                      String
     * @exception java.lang.Exception
     */

    public void enableUser(String str)  throws Exception
    {
        String funcName="enableUser";
        enter(funcName);
        String query = "update accounts set isrevoked='N' where userid='" + str + "'";
        Statement stmt = m_dbConn.createStatement();
        if (log.isDebugEnabled())
            log.debug("enableUser query " + query);
        stmt.executeUpdate(query);
        stmt.close();
        stmt = null;
        exit(funcName); 

    }

    /** Method  deleteGroup :- enableUser is used to enable the "isRevoke" state.
     * @version   ConnectorFactory 6.0.
     * @param     str                                      String
     * @exception java.lang.Exception
     */
    public void disableUser(String str)  throws Exception
    {
        String funcName="disableUser";
        enter(funcName);
        String query = "update accounts set isrevoked='Y' where userid='" + str + "'";
        Statement stmt = m_dbConn.createStatement();
        if (log.isDebugEnabled())
            log.debug("disableUser query " + query);
        stmt.executeUpdate(query);
        stmt.close();
        stmt = null;
        exit(funcName); 
    }

    /** Method  unLockUser :- unLockUser is used to update the property of user in database by passing the reference id that is primary key
     * @version   ConnectorFactory 6.0.
     * @param     str                                      String     
     * @exception java.lang.Exception
     */

    public void unLockUser(String str)  throws Exception
    {
        String funcName="unLockUser";
        enter(funcName);
        String query = "update accounts set islock='N' where userid='" + str + "'";
        Statement stmt = m_dbConn.createStatement();
        if (log.isDebugEnabled())
            log.debug("unLockUser query " + query);
        stmt.executeUpdate(query);
        stmt.close();
        stmt= null;
        exit(funcName); 

    }
    /** Method  updatePasswd :- updatePasswd is used to update the property of user in database by passing the reference id that is primary key
     * @version   ConnectorFactory 6.0.
     * @param     userid                                    String                       
     * @param     pwd                                       String                   
     * @exception java.lang.Exception
     */

    public void updatePasswd(String userid,String pwd)  throws Exception
    {

        String funcName="updatePasswd";
        enter(funcName);
        String query = "update accounts set password='" + pwd + "' where userid='" + userid + "'";
        Statement stmt = m_dbConn.createStatement();
        if (log.isDebugEnabled())
            log.debug("updatePasswd query " + query);
        stmt.executeUpdate(query);
        stmt.close();
        stmt= null;
        exit(funcName); 

    }
    /** Method  createUser :- createUser is used to create the user in the database, first it will check the passed userID is null or not.
     * @version   ConnectorFactory 6.0.
     * @param     userid                                 String                         
     * @param     passwd                                 String      
     * @param     fname                                  String     
     * @param     lname                                  String     
     * @param     email                                  String     
     * @exception java.lang.Exception
     */

    
    public void createUser(String userid,String passwd,String fname,String lname,String email)  throws Exception
    {    
        String funcName="createUser";
        enter(funcName);
        if (userid==null)
            throw new Exception("UserId cannot be null");


        StringBuffer query = new StringBuffer("insert into accounts(userid,firstname,lastname,email,password,islock,isrevoked) values ('");
        query.append(userid).append("','");
        query.append(fname).append("','");
        query.append(lname).append("','");
        query.append(email).append("','");
        query.append(passwd).append("','N','N')");
        Statement stmt = m_dbConn.createStatement();
        if (log.isDebugEnabled())
            log.debug("createUser query " + query.toString());
        stmt.executeUpdate(query.toString());
        stmt.close();
        stmt= null;
        exit(funcName);
    }

    /** Method  createGroup :- createGroup is used to cretae the group in database by passing reference grouName that is     *  primary key
     * @version   ConnectorFactory 6.0.
     * @param     groupName                               String
     * @param     groupDesc                               String      	 
     * @exception java.lang.Exception
     */

    public void createGroup(String groupName,String groupDesc)  throws Exception
    {  
        String funcName="createGroup";
        enter(funcName);
        StringBuffer query = new StringBuffer("insert into groups values('");
        query.append(groupName).append("','");
        query.append(groupDesc).append("');");       
        Statement stmt = m_dbConn.createStatement();
        if (log.isDebugEnabled())
            log.debug("createGroup query " + query.toString());
        stmt.executeUpdate(query.toString());
        stmt.close();
        stmt= null;
        exit(funcName);

    }
    /** Method  updateUser :- updateUser is used to update the user in database by reference id (nativeIdentifier) that is primary key
     * @version   ConnectorFactory 6.0.
     * @param     nativeIdentifier                       String
	 * @param     fname                                  String
	 * @param     lname                                  String
     * @param     email                                  String
     * @exception java.lang.Exception
     */

    public void updateUser(String nativeIdentifier,String fname,String lname,String email) throws Exception
    {

        String funcName="updateUser";
        enter(funcName);
        String query = "update accounts set ";
        boolean fname_found = false;
        boolean lname_found = false;
        boolean email_found = false;

        if (fname != null)
        {
            query += "firstname='" + fname  + "'";
            fname_found = true;
        }
        if (lname != null)
        {
            if (fname_found)
                query += ",";
            lname_found = true;
            query += "lastname='" + lname + "'";

        }
        if (email != null)
        {
            if (lname_found)
                query += ",";
            query += "email='" + email + "' ";


        }


        query +=" where userid='" + nativeIdentifier + "'";

        Statement stmt = m_dbConn.createStatement();
        if (log.isDebugEnabled())
            log.debug("updateUser query " + query);
        stmt.executeUpdate(query);
        if (stmt != null)
            stmt.close();
        stmt = null;
        exit(funcName);
    }

	 /** Method  updateGroup :- updateGroup is used to update the group in database by reference id (nativeIdentifier) that is primary key
     * @version   ConnectorFactory 6.0.
     * @param     nativeIdentifier             String
	 * @param     gdesc                        String
     * @exception java.lang.Exception
     */

    /*UPDATE GROUP*/
    public void updateGroup(String nativeIdentifier,String gdesc) throws Exception
    {
        String funcName="updateGroup";
        enter(funcName);

        String query = "update groups set ";

        boolean gdesc_found = false;

        if (gdesc != null)
        {
            query += "groupDesc='"+ gdesc + "'";
            gdesc_found = true;
        }

        query +=" where groupName='"+ nativeIdentifier + "'";

        Statement stmt = m_dbConn.createStatement();
        if (log.isDebugEnabled())
            log.debug("updateUser query " + query);
        stmt.executeUpdate(query);

        if (stmt != null)
            stmt.close();
        stmt = null;
        exit(funcName);

    }

    /** Method  getDbUserConn :- updateGroup is used to update the group in database by reference id (nativeIdentifier) that is primary key
     * @version   ConnectorFactory 6.0.
     * @param     nativeIdentifier             String
     * @param     gdesc                        String
     * @exception java.lang.Exception
     */

    public ArrayList getDbUserConn(String userName)  throws Exception
    {
        String funcName="getDbUserConn";
        enter(funcName);
        String query = "select groupid from connectionusergroup where userid='" + userName + "'";
        ArrayList<String> grup = new ArrayList<String>();
        Statement stmt = m_dbConn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next())
        {
            grup.add(rs.getString("groupid"));
        }
        if (rs != null)
            rs.close();

        rs = null;
        if (stmt != null)
            stmt.close();
        stmt= null;
        exit(funcName);
        return grup;

    }


    /*
      then check if the group is already asigned to  user
      if not then only assign it
    */


    public void addGroupToUser(String userName,String grpName) throws Exception
    {
        String funcName="addGroupToUser";
        enter(funcName);
        boolean add = true;
        String query ="select groupid from connectionusergroup where userid='" + userName + "'";
        Statement stmt = m_dbConn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next())
        {
            if (grpName.equals(rs.getString("groupid")))
            {
                add = false;
                if (log.isDebugEnabled())
                    log.debug("group already added to user");
                break;
            }
        }
        if (add)
        {
            query = "insert into connectionusergroup values('" + userName + "','" + grpName + "')";
            stmt = m_dbConn.createStatement();
            if (log.isDebugEnabled())
                log.debug("addGroupToUser query " + query);
            stmt.executeUpdate(query);
        }

        if (rs != null)
            rs.close();
        if (stmt != null)
            stmt.close();
        rs = null;
        stmt= null;
        
        if (!add)
        {
           
            throw new Exception("Connection already exist on database");
        }
        
        exit(funcName);

    }

   
    public void removeGroupFromUser(String userName,String grpName) throws Exception
    {

        String funcName="removeGroupFromUser";
        enter(funcName);
        boolean remove = false;
        String query ="select groupid from connectionusergroup where userid='" + userName + "'";
        Statement stmt = m_dbConn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next())
        {
            if (grpName.equals(rs.getString("groupid")) )
            {
                if (log.isDebugEnabled())
                    log.debug("group already added to user");
                remove = true;
                break;
            }
        }

        if (remove)
        {
            query = "delete from connectionusergroup where userid='" + userName + "' and groupid='" + grpName + "'";
            stmt = m_dbConn.createStatement();
            if (log.isDebugEnabled())
                log.debug("removeGroupFromUser query " + query);
            stmt.executeUpdate(query);
        }

        if (rs != null)
            rs.close();
        if (stmt != null)
            stmt.close();

        rs = null;
        stmt= null;
        if(!remove)
        {
            throw new Exception("User " + userName + " is not connected to group " + grpName);
        }
        
        exit(funcName);
    }

    public void deleteMembersOfUsers(String userName)  throws Exception
    {
        String funcName="deleteMembersOfUsers";
        enter(funcName);
        String query ="delete from connectionusergroup where userid='" + userName + "'";
        Statement stmt = m_dbConn.createStatement();
        if (log.isDebugEnabled())
            log.debug("deleteMembersOfUsers query " + query);
        stmt.executeUpdate(query);
        if (stmt != null)
            stmt.close();
        stmt = null;
        exit(funcName);
    }



    /**
     * 
     * Return the Map that has the objects for the currently configured object
     * type.  This maps native identifier to the resource object with that
     * identifier.
     * @throws Exception 
     */
    private Map<String,Map<String,Object>> getObjectsMap() throws Exception 
    {
        String funcName="getObjectsMap";
        enter(funcName);
        Filter filter = _filter;
        ResultSet queryResult = null;
        Statement stmt = null;
        String query = null;
        Map<String,Object> acct = null;
        Map<String,Object> grp = null;

        try
        {
            init();
        }
        catch (Exception e)
        {
            throw new Exception(e);
        }



        m_accountsMap.clear();
        m_groupsMap.clear();

        try
        {

            if (OBJECT_TYPE_ACCOUNT.equals(this.objectType))
            {
                if (filter!=null)
                {
                    List<Filter> filterList = new ArrayList<Filter>();
                    filterList = filter.getFilters();
                    StringBuffer queryString =new StringBuffer("");

                    if (filterList != null && filterList.size()==2)
                    {
                        if (log.isDebugEnabled())
                            log.debug("Passed through authentication user...");
                        boolean orClause = false;
                        for (int i=0;i<filterList.size();i++)
                        {

                            Filter f = filterList.get(i);

                            queryString.append(f.getProperty());
                            if (f.getOp() == Operator.EQ)
                                queryString.append("=");
                            queryString.append("'").append(f.getValue()).append("'");
                            if (!orClause)
                            {
                                orClause = true;
                                queryString.append(" or ");
                            }
                        }

                        ArrayList<String> userList=searchUser(queryString.toString());
                        if (log.isDebugEnabled())
                            log.debug("userlist size " + userList.size());
                        for (int i=0;i<userList.size();i++)
                        {
                            if (log.isDebugEnabled())
                                log.debug("search retuned ==> " + userList.get(i));
                            m_accountsMap.put(userList.get(i),read(userList.get(i)));
                        }

                        m_sLastUser =  EMPTY_STR;
                        return m_accountsMap;

                    }



                }

                
                if (m_sLastUser.equals(EMPTY_STR))
                {
                    if (log.isDebugEnabled())
                        log.debug("First fetch ... ");
                }
                else
                {
                    if (log.isDebugEnabled())
                        log.debug("subsequent fecth of users");
                }




                query = "select userid,firstname,lastname,email,islock,isrevoked from accounts where userid > '" + m_sLastUser + "' order by userid asc ";
                stmt = m_dbConn.createStatement();
                if (log.isDebugEnabled())
                    log.debug(" query " + query);
                queryResult = stmt.executeQuery(query);




                int fetched = 0;
                while (queryResult.next())
                {

                    acct = new HashMap<String,Object>();
                    String userid =   queryResult.getString("userid");
                    acct.put(ATTR_USERID,userid );
                    acct.put(ATTR_FIRSTNAME, queryResult.getString("firstname"));
                    acct.put(ATTR_LASTNAME, queryResult.getString("lastname"));
                    acct.put(ATTR_EMAIL, queryResult.getString("email"));
                    acct.put(ATTR_GROUPS, getDbUserConn(userid));

                    String temp =  queryResult.getString("islock");
                    if (temp.equals("Y"))
                        acct.put(openconnector.Connector.ATT_LOCKED, new Boolean(true));
                    else
                        acct.put(openconnector.Connector.ATT_LOCKED,new Boolean(false));

                    temp =  queryResult.getString("isrevoked");
                    if (temp.equals("Y"))
                        acct.put(openconnector.Connector.ATT_DISABLED, new Boolean(true));
                    else
                        acct.put(openconnector.Connector.ATT_DISABLED, new Boolean(false));



                    fetched = fetched + 1;
                    m_accountsMap.put(userid,acct);
                    if ( fetched >= m_iChunkSize)
                    {
                        m_sLastUser =  userid;
                        if (log.isDebugEnabled())
                            log.debug("Chunk size is equal ,exitting ...");
                        
                        
                        break;
                    }


                }

                if (queryResult != null)
                    queryResult.close();

                queryResult = null;

                if (stmt != null)
                    stmt.close();
                stmt = null;



                if (fetched <  m_iChunkSize)
                {
                    if (log.isDebugEnabled())
                        log.debug(" fetch done ... ");
                    m_sLastUser = EMPTY_STR;
                }

                return m_accountsMap;
            }
            else if (OBJECT_TYPE_GROUP.equals(this.objectType))
            {

                if (log.isDebugEnabled())
                    log.debug("Before retrieve page of groups");
                if (m_sLastGroup != null && m_sLastGroup.equals(EMPTY_STR))
                {
                    if (log.isDebugEnabled())
                        log.debug("First group fetch ... ");
                }
                query = "select groupName,groupDesc from groups where groupName > '" + m_sLastGroup + "' order by groupName asc ";
                stmt = m_dbConn.createStatement();
                if (log.isDebugEnabled())
                    log.debug(" query " + query);
                queryResult = stmt.executeQuery(query);



                int fetched = 0;
                while (queryResult.next())
                {

                    grp = new HashMap<String,Object>();
                    String groupName =   queryResult.getString("groupName");
                    grp.put(GROUP_ATTR_NAME,groupName );
                    grp.put(GROUP_ATTR_DESCRIPTION, queryResult.getString("groupDesc"));

                    fetched = fetched + 1;
                    m_groupsMap.put(groupName,grp);
                    if ( fetched >= m_iChunkSize)
                    {
                        m_sLastGroup =  groupName;
                        if (log.isDebugEnabled())
                            log.debug("Chunk size is equal ,exiitng ...");
                        break;
                    }


                }

                if (queryResult != null)
                    queryResult.close();

                queryResult = null;

                if (stmt != null)
                    stmt.close();
                stmt = null;


                if (fetched <  m_iChunkSize)
                {
                    if (log.isDebugEnabled())
                        log.debug(" fetch done ... ");

                    m_sLastGroup = EMPTY_STR;
                }

                return m_groupsMap;
            }
        }
        catch (Exception e)
        {
            if (log.isDebugEnabled())
                log.debug("connector exception " , e);

            throw new ConnectorException(e);
        }

        exit(funcName);
        throw new ConnectorException("Unhandled object type: " + this.objectType); 


    }

   
    /*
    This method would be called for subsequent fetch of objects
    */

    public Iterator<Map<String,Object>> IterateNextPage(Filter filter) {
        String funcName="IterateNextPage";
        enter(funcName);
        try
        {

            if (OBJECT_TYPE_ACCOUNT.equals(this.objectType))
            {

                if (m_sLastUser.equals(EMPTY_STR)) //deciding condition that the users have been fetched
                {
                    if (log.isDebugEnabled())
                        log.debug("end of user iteration...");
                }
                else
                {

                    it = (getObjectsMap().values()).iterator();
                }

            }
            else if (OBJECT_TYPE_GROUP.equals(this.objectType))
            {
                if (m_sLastGroup.equals(EMPTY_STR)) //do not call getobjectmap if you donot know the last record.  this will start a fresh aggregate
                {
                    if (log.isDebugEnabled())
                        log.debug("end of group iteration...");
                }
                else
                {

                    it = (getObjectsMap().values()).iterator();
                }
            }
        }
        catch (Exception e)
        {
            if (log.isDebugEnabled())
                log.debug("Exception occured " , e);
        }
        exit(funcName);
        return it;      
    }
    


    public void addMemberToGroup(String  userName,String groupName ,boolean readAccount) throws ConnectorException, ObjectNotFoundException
    {
        /*
          During the create opereration we  do not require to read the account ,as it would be already created
          read it only for modify
        */
        String funcName ="addMemberToGroup";
        enter(funcName);
        Map<String,Object> obj = null;
        if (readAccount)
        {
            obj = read(userName);
            if (null == obj)
            {
                throw new ObjectNotFoundException(userName);
            }
        }

        this.objectType = OBJECT_TYPE_GROUP;
        obj = read(groupName);
        if (null == obj)
        {
            throw new ObjectNotFoundException(userName);
        }

        this.objectType = OBJECT_TYPE_ACCOUNT; //revert back to account
        try
        {
            addGroupToUser(userName,groupName);
        }
        catch (Exception e)
        {
            throw new ConnectorException(e);
        }
        exit(funcName);

    }


    public void deleteMemberFromGroup(String  userName,String groupName) throws ConnectorException, ObjectNotFoundException
    {
        String funcName ="deleteMemberFromGroup";
        enter(funcName);

        Map<String,Object> obj = read(userName);
        if (null == obj)
        {
            throw new ObjectNotFoundException(userName);
        }

        this.objectType = OBJECT_TYPE_GROUP;
        obj = read(groupName);
        if (null == obj)
        {
            throw new ObjectNotFoundException(groupName);
        }

        try
        {
            removeGroupFromUser(userName,groupName);
        }
        catch (Exception e)
        {
            throw new ConnectorException(e);
        }
        exit(funcName);
    }  


    public Result updatePassword(String nativeIdentifier, String newPassword)

    throws ConnectorException, ObjectNotFoundException {

        Result result = new Result(Result.Status.Committed);

        String funcName  ="setPassword1";
        enter(funcName);

        try
        {
            init();

            Map<String,Object> obj = read(nativeIdentifier);
            if (null == obj )
            {
                throw new ObjectNotFoundException(nativeIdentifier);
            }
            updatePasswd(nativeIdentifier,newPassword);

        }
        catch (Exception e)
        {
            result.setStatus(Result.Status.Failed);
            result.add(e.getMessage());
            if (log.isDebugEnabled())
                log.debug("Exception occured " , e);
        }
        exit(funcName);
        return result;
    }


    ////////////////////////////////////////////////////////////////////////////
    //
    // ADDITIONAL FEATURES
    //
    ////////////////////////////////////////////////////////////////////////////


    public ArrayList<String> getSearchAttributes(String searchString)
    {
        String funcName  ="getSearchAttributes";
        enter(funcName);
        String delim= ", ";
        String [] tmp = searchString.split(delim);
        ArrayList<String> values = new ArrayList<String>();

        for (int i = 0; i< tmp.length ; ++i)
        {
            String tmpStr = tmp[i].toString();
            if (i == 0)
            {
                if (tmpStr.startsWith("["))
                {
                    tmpStr = tmpStr.substring(1,tmpStr.length());
                }
            }
            if (i == tmp.length -1)
            {
                if (tmpStr.endsWith("]"))
                {
                    tmpStr = tmpStr.substring(0,tmpStr.length()-1);
                }
            }
            values.add(tmpStr);
        }
        exit(funcName);
        return values;


    } 


}


