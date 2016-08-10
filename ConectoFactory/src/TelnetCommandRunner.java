import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import openconnector.ConnectorException;
import openconnector.Result;

import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;



public class TelnetCommandRunner {

	public static int time;
	public static String m_sUser;
	public static String m_sPasswd;
	public static String m_sUrl;
	public static String m_time_provision;
	public static String m_time_aggregation;
	static TelnetCommandRunner client = null;
	
	public static Map<String, Object> account;
	public static List<Map<String, Object>> resultObjectList;
	public static String GET_ACCOUNT_BY_GROUP = "9";

	
		private TelnetClient tc = new TelnetClient();
		private InputStream in;
		private PrintStream out;
		private String prompt = "==>";
		private String timeoutmsg = "Timeout period expired";
		private String logedoutmsg = "logged out";
		private String unauthorizedmsg = "User authorization failure";
		private long TIMEOUT = 1000 * 60 * time;
		Result result;


		public TelnetCommandRunner(String server, String user, String password) throws Exception {
			try {

				tc.connect(server, 23);
				TerminalTypeOptionHandler ttopt = new TerminalTypeOptionHandler(
						"VT100", false, false, true, false);
				EchoOptionHandler echoopt = new EchoOptionHandler(true, false,
						true, false);
				SuppressGAOptionHandler gaopt = new SuppressGAOptionHandler(true,
						true, true, true);

				tc.addOptionHandler(ttopt);
				tc.addOptionHandler(echoopt);
				tc.addOptionHandler(gaopt);

				in = tc.getInputStream();
				out = new PrintStream(tc.getOutputStream());

				readUntil("Username:");
				write(user);

				readUntil("Password:");
				write(password);

				readUntil(prompt);
			} catch (Exception e) {

				disconnect();
				
				System.out.println("O tempo de conexão com o servidor do SISFIN expirou");
				
//				log.debug("O tempo de conexão com o servidor do SISFIN expirou");
				
				result = new Result(Result.Status.Failed);
				result.add("SISFIN PASSWORD - NÃO FOI POSSÍVEL TROCAR A SENHA RETORNO VAZIO");
				result.setRetryInterval(3);
				
				throw new Exception("O tempo de conexão com o servidor do SISFIN expirou");
			}
		}

		public String readUntil(String pattern) throws Exception {

			Result result;
			
			char lastChar = pattern.charAt(pattern.length() - 1);
			StringBuilder sb = new StringBuilder(10);
			byte buffer[] = new byte[500];
			int totalBytesLidos = in.read(buffer);

			long startTime = System.currentTimeMillis();
			long endTime = startTime + TIMEOUT;

			System.out.println("Start at: " + new Time(System.currentTimeMillis()));
			while (totalBytesLidos > 0) {
				String linha = new String(buffer, 0, (totalBytesLidos));
				sb.append(linha);

				if (System.currentTimeMillis() > endTime) {
					
					System.out.println("O tempo de conexão com o servidor do SISFIN expirou");
					
//					log.debug("O tempo de conexão com o servidor do SISFIN expirou");
					
					result = new Result(Result.Status.Failed);
					result.add("SISFIN PASSWORD - NÃO FOI POSSÍVEL TROCAR A SENHA RETORNO VAZIO");
					result.setRetryInterval(3);
					
					throw new Exception("O tempo de conexão com o servidor do SISFIN expirou");
				}

				if (linha.contains(pattern)) {
					return sb.toString();
				} else if (sb.toString().endsWith(timeoutmsg)) {
					throw new Exception(sb.toString());
				} else if (sb.toString().endsWith(logedoutmsg)) {
					throw new Exception(sb.toString());
				} else if (sb.toString().endsWith(unauthorizedmsg)) {
					throw new Exception(sb.toString());
				}
				totalBytesLidos = in.read(buffer);
			}
			return sb.toString();
		}

		public void write(String value) {
			out.println(value);
			out.flush();
		}

		public String sendCommand(String command) throws Exception {

			System.out.println(command);

			write(command);
			String result = readUntil(prompt);
			if (result.endsWith(prompt)) {
				result = result.replaceAll(prompt, "");
			}
			return result;
		}

		public void disconnect() {
			try {
				tc.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	
	
	
	public static void init() {

		m_sUser = "CTSAD";
		m_sPasswd = "CTSAD";
		m_sUrl = "sdges1.desenvolvimento.extracaixa";
		m_time_provision = "0";
		m_time_aggregation = "0";
		
		Result result; 
		
//		if (isDebug == true) {
			
			System.out.println("INIT " + "<user " + m_sUser + "> <m_sUrl " + m_sUrl+ ">");
//			log.debug("INIT " + "<user " + m_sUser + "> <m_sUrl " + m_sUrl
//					+ ">");
//		}

		try {
			client = new TelnetCommandRunner(m_sUrl, m_sUser, m_sPasswd);
			
		} catch (Exception e) {
			
			
			System.err.println("Teste Erro");
			System.out.println(e);
			
//			result = new Result(Result.Status.Failed);
//			result.add("SISFIN PASSWORD - NÃO FOI POSSÍVEL TROCAR A SENHA");
//			result.setRetryInterval(3);
			
		} 
		// m_bIsDriverLoaded = true;
	}
	
	
	
	
	public static Iterator<Map<String, Object>> getAccounts(String filter) {

		Result result;
		account = null;
		resultObjectList = null;
		String retorno = null;
		resultObjectList = new ArrayList<Map<String, Object>>();

		init();

		try {
		
		time = Integer.parseInt(m_time_aggregation); 
		
		System.out.println("Account " + "<time " + time + ">");	
		
		System.out.println("Account " + "<filter " + filter + ">");

		if (filter.equalsIgnoreCase("*")) {

			String command = GET_ACCOUNT_BY_GROUP + ";*;*";

			System.out.println("command " + "<command " + command + ">");

			retorno = client.sendCommand(command);

		} else {

			System.out.println("Account Filter TRUE" + "<filter " + filter + ">");

			String classe = filter.substring(0, 1);
			String matricula = filter.substring(1);

			System.out.println("Account Valor" + "<classe " + classe
						+ "> <matricula " + matricula + ">");

			String commandFilter = "8" + ";" + classe + ";" + matricula;

			System.out.println("<command " + classe + "> <matricula " + matricula
						+ ">");

			init();

			retorno = client.sendCommand(commandFilter);

			System.out.println("Account " + "<retorno " + retorno + ">");

		}

			if (retorno != null) {

				String[] linha = retorno.split("\r\n");

				String l = linha[0];
				
				for (String linhaSeparada : l.split("\n")) {
					account = new HashMap<String, Object>();

					System.out.println("Account linhas: " + linhaSeparada);

					if (!linhaSeparada.isEmpty()) {
						
						String[] array = linhaSeparada.split(";");

						int tamanho = array.length;
						
						System.out.println("tamanho" +tamanho);

						if (tamanho > 9) {
							String classe = array[0];
							String numero = array[1];

								account.put("MATRICULA", classe + numero);
								account.put("NOME", array[2]);
								account.put("UNIDADE", array[3]);
								account.put("UNIDADE_ADM", array[4]);
								account.put("FUNC_EFET", array[5]);
								account.put("FUNC_EVENT", array[6]);
								account.put("FUNC_EXERC", array[7]);
								String sisperfil = array[8] + "/" + array[9];
								account.put("SIS_PERFIL", sisperfil);

								System.out.println("Account "
											+ "\n <MATRICULA: " + classe+numero 
											+ ">\n <NOME: " + array[2]
											+ ">\n <UNIDADE: " + array[3]
											+ ">\n <UNIDADE_ADM: " + array[4]
											+ ">\n <FUNC_EFET: " + array[5]
											+ ">\n <FUNC_EVENT: " + array[6]
											+ ">\n <FUNC_EXERC: " + array[7]
											+ ">\n <sisperfil: " + sisperfil.trim()+ ">");
								
								
								resultObjectList.add(account);
						}
					}
				}
			} 
			
			if(resultObjectList.isEmpty())
			{
				result = new Result(Result.Status.Failed);
				result.add("SISFIN AGREGGATION ACCOUNT - LISTA SISFIN ESTA VAZIA");
				result.setRetryInterval(3);
				
				throw new ConnectorException("LISTA GET SISFIN VAZIA");
			}

			// client.disconnect();

		} catch (Exception e) {

			System.out.println("Exception occured Account" + e);

			result = new Result(Result.Status.Failed);
			result.add("SISFIN AGREGGATION ACCOUNT - NÃO FOI POSSÍVEL GERAR AGREGAÇÃO DA CONTA");
			result.setRetryInterval(3);
			
			throw new ConnectorException("ERRO SISFIN GET ACCOUNT");
			
			
		}
		
		return resultObjectList.iterator();

	}
	
	
	
	
	
	
	public static void main(String[] args) {
		Iterator<Map<String, Object>> abas = getAccounts("*");
	}
	
	
	
	
	
	
}
