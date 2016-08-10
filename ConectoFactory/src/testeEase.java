import br.com.dba.ease.EaseWSClientFacade;
import br.com.dba.ease.xml.EaseWSRetorno;


public class testeEase {

public static void main(String[] args) throws Exception {
		
		String wsdl =  "http://10.192.228.181:22080/axis2/services/SAT?wsdl"; // DES
        String login = "gal ease";//XSA_Framework.XSA_ReadParam("WS_EASE_USUARIO");
        String password = "administ";//XSA_Framework.XSA_ReadParam("WS_EASE_SENHA");
		
		
		EaseWSClientFacade.init(login, password, wsdl);
		
		EaseWSRetorno wsRetorno;
		
		
		 wsRetorno = EaseWSClientFacade.getInstance().getUsuarios();
		 System.out.println("Todos Usuarios "+wsRetorno.getXml());
		
		
		
		
	}
	
	
}
