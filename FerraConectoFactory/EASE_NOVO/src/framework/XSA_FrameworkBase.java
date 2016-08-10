package framework;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ArrayList;

/**
 *  Sample ---- XModule Logic
 *  XModule puts all the data into hash tables, as follows -
 *
 *  XSA_Get_Entity_Hash     - For Get reply, contains ID fields + all keywords
 *  XSA_Get_Search_Hash     - Get request, ID/mask fields
 *  XSA_Set_Id_Hash         - For SET, contains the entity ID
 *  XSA_Set_Kwds_Hash       - For SET, contains the entity keywords
 *
 *  XSA_Operation_Hash      - All actions, general info
 *  XSA_Context_Hash        - context table
 *
 *  The XModule also supplies the following command extensions -
 *
 *  XSA_WriteMessage            - Input: text
 *  XSA_WriteSynchronization    - Input: object info
 *  XSA_WriteEntity             - Input: (reads XSA_Get_Entity_Hash)
 *  XSA_WriteDebug              - Input: level, text
 *  XSA_ReaParam                - Input: parameter name, Output: value
 *
 */

public class XSA_FrameworkBase
{

    /* ===================================================================== */
    /* ===================================================================== */
    /* ===         The following definitions are for ANY connector       === */
    /* ===================================================================== */
    /* ===================================================================== */
    /* return codes */
    public final static int XSA_RC_OK = 0;
    public final static int XSA_RC_MORE = 1;
    public final static int XSA_RC_NOT_IN_OE = 1;
    public final static int XSA_RC_NOT_ACTIVE = 1;
    public final static int XSA_RC_IGNORE_ENTITY = 5;
    public final static int XSA_RC_ERROR = 8;  /* connector reports error */

    /* index to input hashes object */
    public final static int XSA_GET_ENTITY_HASH = 0;
    public final static int XSA_GET_SEARCH_HASH = 1;
    public final static int XSA_SET_ID_HASH = 2;
    public final static int XSA_SET_KWDS_HASH = 3;
    public final static int XSA_OPERATION_HASH = 4;
    public final static int XSA_CONTEXT_HASH = 5;
    public final static int XSA_HASHES_COUNT = 6;

    /* Operation hash entries */
    public final static String XSA_OP_ACTION_STR = "XSA_ACTION";
    public final static String XSA_OP_FILTER_STR = "XSA_FILTER_TYPE";
    public final static String XSA_OP_GET_STAGE_STR = "XSA_GET_STAGE";
    public final static String XSA_OP_ENTITY_STR = "XSA_ENTITY";
    public final static String XSA_OP_FUNCTION_STR = "XSA_FUNCTION";
    public final static String XSA_OP_WORK_DIR_STR = "XSA_WORK_DIR";
    public final static String XSA_OP_ADMIN_STR = "XSA_ADMIN_ID";
    public final static String XSA_OP_PASS_STR = "XSA_ADMIN_PASSWORD";
    public final static String XSA_OP_PROC_STR = "XSA_PROCESS_NAME";
    public final static String XSA_OP_CONNECTOR_STR = "XSA_CONNECTOR";
    public final static String XSA_OP_SYSTEM_STR = "XSA_SYSTEM";
    public final static String XSA_OP_SERVICE_STR = "XSA_SERVICE_TYPE";
    public final static String XSA_OP_TRANSACTION_STR = "XSA_TRANSACTION_TYPE";


    public final static String XSA_OP_ACTION_INIT = "INIT";
    public final static String XSA_OP_ACTION_TERM = "TERM";
    public final static String XSA_OP_ACTION_GET = "GET";
    public final static String XSA_OP_ACTION_ADD = "ADD";
    public final static String XSA_OP_ACTION_DELETE = "DELETE";
    public final static String XSA_OP_ACTION_UPDATE = "UPDATE";
    public final static String XSA_OP_ACTION_QUERY = "QUERY";

    /* List of values for XSA_ENTITY_TYPE */
    public final static String XSA_OP_ENTITY_ACCOUNT = "ACCOUNT";
    public final static String XSA_OP_ENTITY_GROUP = "GROUP";
    public final static String XSA_OP_ENTITY_CONTAINER = "CONTAINER";
    public final static String XSA_OP_ENTITY_CONN = "CONNECTION";
    public final static String XSA_OP_ENTITY_SYSTEM = "SYSTEM";
    public final static String XSA_OP_ENTITY_CONNECTOR = "CONNECTOR";
    public final static String XSA_OP_ENTITY_MATCH = "MATCH";
    public final static String XSA_OP_ENTITY_RESOURCE = "RESOURCE";

/* list of values for get request */
    public final static String XSA_OP_FILTER_NA = "N/A";
    public final static String XSA_OP_FILTER_SINGLE = "SINGLE";
    public final static String XSA_OP_FILTER_MANY = "MANY";
    public final static String XSA_OP_FILTER_PREFIX = "PREFIX";

/* list of values for get mode */
    public final static String XSA_OP_GET_STAGE_START = "START";
    public final static String XSA_OP_GET_STAGE_NEXT = "NEXT";
    public final static String XSA_OP_GET_STAGE_END = "END";

/* context hash entries */
    public final static String XSA_CONTEXT_CONNECTOR_STR = "XSA_CONNECTOR_CONTEXT";
    public final static String XSA_CONTEXT_SYSTEM_STR = "XSA_SYSTEM_CONTEXT";
    public final static String XSA_CONTEXT_TRAN_STR = "XSA_TRANSACTION_CONTEXT";
    public final static String XSA_CONTEXT_ACTION_STR = "XSA_ACTION_CONTEXT";
    public final static String XSA_CONTEXT_SESSION_STR = "XSA_SESSION_CONTEXT";

/*Resource & ACL specific Logic Requirement*/
    public static int res_count;
    public static int have_more;
    public static int nSignle;

    public static String resource_type = null;
    public static String resource_name = null;
    public static String ace_name = null;

    public static int maxResources = 1;
    public static int maxResourcesACL = 1;

    /* ACCOUNT Entity Enum Values */

    public class ACCOUNT_PASSWORD_LIFE
    {
        public final static String PERMANENT = "PERMANENT";
        public final static String RESET = "RESET";
    }

    public class ACCOUNT_LOCK_STATUS
    {
        public final static String LOCKED = "LOCKED";
        public final static String UNLOCKED = "UNLOCKED";
    }

    public class ACCOUNT_REVOKE_STATUS
    {
        public final static String REVOKED = "REVOKED";
        public final static String ACTIVE = "ACTIVE";
    }

    public class ACCOUNT_ADMIN_STATUS
    {
        public final static String NONE = "NONE";
        public final static String AUDIT = "AUDIT";
        public final static String ADMIN = "ADMIN";
        public final static String ALL = "ALL";
    }

    public class ACCOUNT_DEF_GROUP_ACTION
    {
        public final static String REVOKED = "DROP";
        public final static String ACTIVE = "KEEP";
    }

    /* CONNECTION Entity Enum Values */
    public class CONNECTION_DEF_STATUS
    {
        public final static String REGULAR = "REGULAR";
        public final static String DEFAULT_GROUP = "DEFAULT_GROUP";
    }

    public class CONNECTION_ADMIN_STATUS
    {
        public final static String NONE = "NONE";
        public final static String AUDIT = "AUDIT";
        public final static String ADMIN = "ADMIN";
        public final static String ALL = "ALL";
    }

    public class RESOURCE_ACL_TYPE
    {
		public final static String ACE_TYPE_IGNORED = "ACE_TYPE_IGNORED";
		public final static String ACE_TYPE_UNDEFINED = "ACE_TYPE_UNDEFINED";
		public final static String ACE_TYPE_USER = "ACE_TYPE_USER";
		public final static String ACE_TYPE_GROUP = "ACE_TYPE_GROUP";
		public final static String ACE_TYPE_USER_AND_GROUP = "ACE_TYPE_USER_AND_GROUP";
		public final static String ACE_TYPE_WORLD = "ACE_TYPE_WORLD";
		public final static String ACE_TYPE_MASK = "ACE_TYPE_MASK";
		public final static String ACE_TYPE_OE = "ACE_TYPE_OE";
    }

    public class RESOURCE_ACL_ATTRIBUTE
    {
        public final static String ACE_ATTR_IGNORED = "ACE_ATTR_IGNORED";
        public final static String ACE_ATTR_REGULAR = "ACE_ATTR_REGULAR";
	    public final static String ACE_ATTR_PERMANENT = "ACE_ATTR_PERMANENT";
    }


/* Debug levels */
    public final static int XSA_DEBUG_ERROR = 0;
    public final static int XSA_DEBUG_WARNING = 10;
    public final static int XSA_DEBUG_INFO = 20;
    public final static int XSA_DEBUG_DETAIL = 40;
    public final static int XSA_DEBUG_FRAMEWORK = 95;

    /* Context Variables */
    public static Hashtable XSA_ContextHash = new Hashtable();
    private static Hashtable XSA_SessionContext = new Hashtable();
    private static String LastSessionContextKey;
    /* ===================================================================== */
    /* ===================================================================== */
    /* ===                 METHODS                                       === */
    /* ===================================================================== */
    /* ===================================================================== */

    /* C callback methods */

    public static native int XSA_WriteDebug(int level, String text);

    public static native int XSA_WriteDebugEnter(int level, String text);

    public static native int XSA_WriteDebugExit(int level, String text, int rc);

    public static native String XSA_ReadParam(String name);

    /**
     * Display contents of hash table
     *
     * @param IN_level display leve
     * @param IN_desc  Descriptive text
     * @param IN_hash  Hashtable
     */
    public static void XSA_DebugHash(int IN_level,
                                     String IN_desc,
                                     Hashtable IN_hash){

        XSA_DebugHash(IN_level, IN_desc, IN_hash, null);
    }

    /**
     * Display contents of hash table, discard any keywords in the IN_skipKwdIdList
     *
     * @param IN_level display leve
     * @param IN_desc  Descriptive text
     * @param IN_hash  Hashtable
     * @param IN_skipKwdIdList Deywords ID not to print
     */
    public static void XSA_DebugHash(int IN_level,
                                      String IN_desc,
                                      Hashtable IN_hash,
                                      ArrayList IN_skipKwdIdList)
    {

        // # -----------------------------------------------------------------
        // # ----                 Initialization                          ----
        // # -----------------------------------------------------------------
        XSA_Framework.XSA_WriteDebug(IN_level,
                "--------- " + IN_desc + " ------------");

        if (IN_hash == null) {
            XSA_Framework.XSA_WriteDebug(IN_level,
                    "   does not exist");
            return;
        }

        // # -----------------------------------------------------------------
        // # ----                 Processing                              ----
        // # -----------------------------------------------------------------

        // # ### loop on all entries and write it out
        for (Iterator i = IN_hash.keySet().iterator(); i.hasNext();) {

            String id = (String) i.next();
            if ( IN_skipKwdIdList == null ||
                    !IN_skipKwdIdList.contains(id) ){
                XSA_Framework.XSA_WriteDebug(IN_level,
                        "name=<" +
                                id +
                                "> value=<" +
                                IN_hash.get(id) +
                                ">");
            }else{
                // Don't print the keyword value
                XSA_Framework.XSA_WriteDebug(IN_level,
                        "name=<" + id + "> value=<****>");
            }
        }

        XSA_Framework.XSA_WriteDebug(IN_level,
                "----------- END -------------");

    } /* End method XSA_DebugHash */


    /**
     * Display exception trace. Used by Framework.
     *
     * @param IN_exception
     */
    public static void ExceptionDescribe(Throwable IN_exception)
    {

        XSA_Framework.XSA_WriteDebug(XSA_DEBUG_ERROR,
                "Exception: " + IN_exception);

        StackTraceElement[] stackTrace = IN_exception.getStackTrace();

        if (stackTrace != null) {
            XSA_Framework.XSA_WriteDebug(XSA_DEBUG_ERROR, "Exception Stacktrace: ");
            for (int i = 0; i < stackTrace.length; i++) {
                StackTraceElement element = stackTrace[i];
                XSA_Framework.XSA_WriteDebug(XSA_DEBUG_ERROR, "      " + element);
            }
        }

        XSA_Framework.XSA_WriteDebug(XSA_DEBUG_ERROR,
                "Exception: end");


    } /* End method ExceptionDescribe */

    /**
     * restore session context from the SessionHash table,
     * to XSA_ContextHash using the managed system name
     * and Administrator name from the operation hash
     *
     * @param IN_hashes
     * @return XSA_RC_OK if succeed
     */
    public static int XSA_RestoreSession(Hashtable[] IN_hashes)
    {
        String function = "XSA_RestoreSession";
        int level = XSA_DEBUG_FRAMEWORK;
        int rc = XSA_RC_OK; // return code
        Hashtable XSA_OperationHash = IN_hashes[XSA_OPERATION_HASH];

        XSA_WriteDebugEnter(level, function);


        String key = ""+ XSA_OperationHash.get(XSA_OP_SYSTEM_STR) + XSA_OperationHash.get(XSA_OP_ADMIN_STR).toString();

        XSA_WriteDebug(level, function + ": restoring key is <" + key + "> lastkey is <" + LastSessionContextKey + ">");

        if (LastSessionContextKey != null && key.compareTo(LastSessionContextKey) == 0) {
            //do nothing
            XSA_WriteDebug(level, function + ": The keys are the same returning");
            XSA_WriteDebugExit(level,function,rc);
            return rc;
        }

        Object currContext = XSA_ContextHash.get(XSA_CONTEXT_SESSION_STR);
        XSA_ContextHash.remove(XSA_CONTEXT_SESSION_STR);
        Object tobeRestoredContext = XSA_SessionContext.get(key);

        XSA_WriteDebug(level, function + ": Current to be saved Session context is <" + currContext + ">");
        XSA_WriteDebug(level, function + ": Restored Session context is <" + tobeRestoredContext + ">");

        if (tobeRestoredContext != null)
            XSA_ContextHash.put(XSA_CONTEXT_SESSION_STR, tobeRestoredContext);

        if (LastSessionContextKey != null && currContext != null) {
            XSA_SessionContext.put(LastSessionContextKey, currContext);
        }

        LastSessionContextKey = key;

        XSA_WriteDebugExit(level,function,rc);

        return rc;

    }  /* End XSA_RestoreSession*/

    /**
     * Create session -
     * Save the Session key for use later when restore session
     * puts the old Session context on the XSA_SessionContext hash
     *
     * @param IN_hashes
     * @return  XSA_RC_OK if succeed
     */
    public static int XSA_CreateSession(Hashtable[] IN_hashes)
    {

        String function = " XSA_CreateSession";
        int level = XSA_DEBUG_FRAMEWORK;
        int rc = XSA_RC_OK; // return code
        XSA_WriteDebugEnter(level, function);
        Hashtable XSA_OperationHash = IN_hashes[XSA_OPERATION_HASH];
        LastSessionContextKey = XSA_OperationHash.get(XSA_OP_SYSTEM_STR).toString() + XSA_OperationHash.get(XSA_OP_ADMIN_STR).toString();

        XSA_WriteDebug(level, function + ": lastkey is < " + LastSessionContextKey + ">");

        XSA_WriteDebugExit(level,function,rc);

        return rc;

    } /*End XSA_CreateSession*/

    /**
     * Free the context according to the $::XSA_OperationHash{$XSA_OP}
     * we know which context should be freed
     * in case of the session context the context should be freed from 2 hash tables.
     *
     * @param IN_hashes
     * @return XSA_RC_OK if succeed
     */
    public static int XSA_FreeContext(Hashtable[] IN_hashes)
    {
        String function = "XSA_FreeContext";
        int level = XSA_DEBUG_FRAMEWORK;
        int rc = XSA_RC_OK; // return code
        XSA_WriteDebugEnter(level, function);
        Hashtable XSA_OperationHash = IN_hashes[XSA_OPERATION_HASH];
        String key = XSA_OperationHash.get(XSA_OP_SYSTEM_STR).toString() + XSA_OperationHash.get(XSA_OP_ADMIN_STR).toString();
        //free the context according to the XSA_OperationHash{$XSA_OP} we know which context should be freed
        //in case of the session context the context should be freed from 2 hash tables.

        if (XSA_OperationHash.get(XSA_OP_FUNCTION_STR).toString().compareTo("SessionTerm") == 0) {
            XSA_SessionContext.remove(key);
            XSA_ContextHash.remove(XSA_CONTEXT_SESSION_STR);
            LastSessionContextKey = null;
        } else if (XSA_OperationHash.get(XSA_OP_FUNCTION_STR).toString().compareTo("TransactionEnd") == 0) {
            XSA_ContextHash.remove(XSA_CONTEXT_TRAN_STR);
        } else if (XSA_OperationHash.get(XSA_OP_FUNCTION_STR).toString().compareTo("ManageSystemTerm") == 0) {
            XSA_ContextHash.remove(XSA_CONTEXT_SYSTEM_STR);
        } else if (XSA_OperationHash.get(XSA_OP_FUNCTION_STR).toString().compareTo("ConnectorTerm") == 0) {
            XSA_ContextHash.remove(XSA_CONTEXT_CONNECTOR_STR);
        }

        XSA_WriteDebug(level, function + ": lastkey is < " + LastSessionContextKey + " >");

        XSA_WriteDebugExit(level,function,rc);

        return rc;

    } /* End XSA_FreeContext*/

    /**
     * free the  Action context ussually will be called before every transaction
     *
     * @param IN_hashes
     * @return XSA_RC_OK if succeed
     */
    public static int XSA_FreeActionContext(Hashtable[] IN_hashes)
    {
        String function = "XSA_FreeActionContext";
        int level = XSA_DEBUG_FRAMEWORK;
        int rc = XSA_RC_OK; // return code
        XSA_WriteDebugEnter(level, function);

        XSA_ContextHash.remove(XSA_CONTEXT_ACTION_STR);

        XSA_WriteDebugExit(level,function,rc);

        return rc;

    } /* End XSA_FreeActionContextn*/

}  /* end class */



