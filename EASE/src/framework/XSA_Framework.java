package framework;

public class XSA_Framework extends XSA_FrameworkBase {

 /* ===================================================================== */
 /* ===================================================================== */
 /* ===                 METHODS                                       === */
 /* ===================================================================== */
 /* ===================================================================== */

  /* C callback methods */

  public static native int XSA_WriteMessage         (String message);
  public static native int XSA_WriteSynchronization (String action,
                                                     String type,
                                                     String id1,
                                                     String id2);
  public static native int XSA_WriteEntity          ();


}  /* end class */



