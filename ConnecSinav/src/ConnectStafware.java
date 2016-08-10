import static org.jinterop.dcom.impls.automation.IJIDispatch.IID;

import java.io.IOException;
import java.net.UnknownHostException;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.core.JIClsid;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.JIObjectFactory;
import org.jinterop.dcom.impls.automation.IJIDispatch;

public class ConnectStafware
	{

		public static JIComServer comServer;
		public static JISession session;
		public static IJIDispatch comLocator;
		public static String passowrdCriptografada;
		public static String keyCripto = "__Sis#Ctrl*ccess{Sec}__";

		public static void encripta(String clearPassword) throws JIException,
				SecurityException, IOException {

			connectServerCriptografia();

			JISystem.setAutoRegisteration(true);

			// Object[] param1 = new Object[] {
			// // new JIString("SIWFM DES"),
			// new JIString("cxextrnt043|"),
			// new JIString("Staffw_homo|"),
			// new JIString("10.192.228.150|"),
			// new JIString("9560"),
			// // new JIString("swadmin"),
			// // new JIString("caixa2000"),
			// };

			StringBuilder dadoProvision = new StringBuilder();

			dadoProvision.append("cxextrnt043|");
			dadoProvision.append("Staffw_homo|");
			dadoProvision.append("10.192.228.150|");
			dadoProvision.append("9560");

			Object[] param1 = new Object[] { new JIString(
					dadoProvision.toString()), };

			Object[] param2 = new Object[] { new JIString("swadmin"), };

			StringBuilder dadoProvision2 = new StringBuilder();

			dadoProvision2.append("cxextrnt043|");
			dadoProvision2.append("Staffw_homo");

			//
			Object[] param3 = new Object[] {
					new JIString(dadoProvision2.toString()),
					new JIString("caixa2000"), };

			// String a = "cxextrnt043|Staffw_homo|10.192.228.150|9560";

			// id = comLocator.callMethodA("SWEnterprise.MakeNodeInfoByTag",
			// param1);
			JIVariant[] resultsCript2 = comLocator.callMethodA(
					"MakeNodeInfoByTag", param1);
			JIVariant[] resultsCript3 = comLocator.callMethodA(
					"CreateEntUsers", param2);
			JIVariant[] resultsCript4 = comLocator.callMethodA(
					"SWEntUser.Login", param3);

			Object objectCripto = resultsCript2[0].getObject();
			Object objectCripto2 = resultsCript3[0].getObject();
			Object objectCripto3 = resultsCript4[0].getObject();

			System.out.println("Object Parm1 " + objectCripto);
			System.out.println("Object Parm2 " + objectCripto2);
			System.out.println("Object Parm3 " + objectCripto3);

			// @SuppressWarnings("unused")
			// Object[] paramsDecripto = new Object[] {
			// new JIString(senhaCripto),
			// new JIString(keyCripto + clearPassword) };

			// passowrdCriptografada =
			// resultsCript[0].getObjectAsString2().toString();

			// System.out.println("senhaCripto "+passowrdCriptografada);

		}

		public static String dominioServerCripto = "corp.caixa.gov.br";
		public static String userServerCripto = "F500195";
		public static String senhaServerCripto = "Partner20";
		public static String nameDLL = "SWEOcom.SWEnterprise.1";
//		public static String nameDLL = "SWEOcom.SWEntUser.1";
		// public static String urlServerCripto = "10.196.20.145";
		public static String urlServerCripto = "10.208.10.156";

		public static void connectServerCriptografia()
				throws UnknownHostException, JIException {

			try
				{
					JISystem.setInBuiltLogHandler(true);
				} catch (SecurityException e)
				{
					e.printStackTrace();
				} catch (IOException e)
				{
					e.printStackTrace();
				}

			// if (log.isDebugEnabled())
			// {
			try
				{
					JISystem.setInBuiltLogHandler(true);
				} catch (SecurityException e1)
				{
					e1.printStackTrace();
				} catch (IOException e1)
				{
					e1.printStackTrace();
				}
			// }

			session = JISession.createSession(dominioServerCripto,
					userServerCripto, senhaServerCripto);

			try
				{
					try
						{
							comServer = new JIComServer(
									JIProgId.valueOf(nameDLL), urlServerCripto,
									session);
						} catch (UnknownHostException e)
						{
							e.printStackTrace();
						}
				} catch (JIException e)
				{
					e.printStackTrace();
					comServer = new JIComServer(JIProgId.valueOf(nameDLL),
							urlServerCripto, session);
				}

			try
				{
					comLocator = (IJIDispatch) JIObjectFactory
							.narrowObject(comServer.createInstance()
									.queryInterface(IID));
				} catch (JIException e)
				{
					e.printStackTrace();
				}
		}

		public static void main(String[] args) throws SecurityException,
				JIException, IOException {

			encripta("Teste123");

		}

	}
