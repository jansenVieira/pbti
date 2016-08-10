package util;

import java.util.Hashtable;
import java.util.Map;
import framework.XSA_Framework;

public class UtilFuncoes {

	private static Map<String, String> mapaStatusUsuarioControlSA;

	@SuppressWarnings("unchecked")
	public static void startDebug(String function, Hashtable operationHash,
			Hashtable contextHash, Hashtable searchHash, Hashtable entityHash) {
		XSA_Framework.XSA_WriteDebugEnter(XSA_Framework.XSA_DEBUG_DETAIL,
				function);
		XSA_Framework.XSA_DebugHash(XSA_Framework.XSA_DEBUG_DETAIL,
				"XSA_OperationHash", operationHash);
		XSA_Framework.XSA_DebugHash(XSA_Framework.XSA_DEBUG_DETAIL,
				"XSA_ContextHash", contextHash);
		XSA_Framework.XSA_DebugHash(XSA_Framework.XSA_DEBUG_DETAIL,
				"XSA_GetSearchHash", searchHash);
		XSA_Framework.XSA_DebugHash(XSA_Framework.XSA_DEBUG_DETAIL,
				"XSA_GetEntityHash", entityHash);
	}
}
