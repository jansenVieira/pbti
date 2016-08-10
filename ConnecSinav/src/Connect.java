import javax.naming.NamingException;

import br.gov.caixa.framework.services.locator.ServiceLocatorException;
import br.gov.caixa.seguranca.LoginException;
import br.gov.caixa.util.jcicsconnect.ExecuteException;
import br.gov.caixa.util.jcicsconnect.JCicsConnect;
import br.gov.caixa.util.jcicsconnect.LeMensagemException;
import br.gov.caixa.util.jcicsconnect.MensagemCics;

public class Connect {

	private static JCicsConnect jcisConnect = new JCicsConnect();

	public static void criarUsuario() {

		try {
			MensagemCics msg = new MensagemCics();
			msg.setNomeServidor("ibmaplacs.des.extranet.caixa");
			msg.setNumPorta(3183);
			msg.setUserName("iqpointd");
			msg.setPassword("Point#16");

			StringBuilder dadoProvision = new StringBuilder();

			// dadoProvision.append("0102");

			// CD-USUARIO X(08)
			dadoProvision.append("C001320 ");
			// NO-USUARIO X(40)
			dadoProvision.append("Teste Nome                              ");
			// CD-UNIDADE 9(04)
			dadoProvision.append("5405");
			// CD-SIS-DEFAUL x(08)
			dadoProvision.append("PON     ");
			// CD-SIS-USU x(01)
			dadoProvision.append("A");
			// CD-SUREG-LOT 9(04)
			dadoProvision.append("0099");
			// ID-DIRETORIA X(01)
			dadoProvision.append("N");
			// ID-STATUS X(01)
			dadoProvision.append("A");
			// ID-CEF X(01) --
			dadoProvision.append("S");
			// DT-PRV-DESL X(10)
			dadoProvision.append("          ");
			// CD-FUNCAO 9(04)--aceita nulo
			dadoProvision.append("0000");
			// NU-MATR-EMP 9(08) --- diferente de C passa 0
			dadoProvision.append("00000002");
			// NU-CA-EN-FISICA 9(04)
			dadoProvision.append("0000");
			// NU-UNIDADE-FISICA 9(04)
			dadoProvision.append("0000");

			msg = jcisConnect.login(msg);

			System.out.println("CodRetorno: " + msg.getCodRetorno());
			System.out.println("EstaLogado: " + msg.getEstaLogado());
			System.out.println("Resp: " + msg.getResp());
			System.out.println("Resp2: " + msg.getResp2());
			System.out.println("Tipo Programa: " + msg.getTipoPrograma());

			System.out.println("<" + dadoProvision.toString() + ">");

	//		String dados = dadoProvision.toString();
			
			String dados = "C001320 LISIAN MARIA CARDOSO                    2196GED     A0183SAS          03880000023400000000";

			System.out.println("<" + dados + ">");

			msg.setTransId("NA18");
			msg.setProgramName("NAVPO018");
			msg.setTipoPrograma(msg.getTipoPrograma());
			msg.setDadosEnvio(dados);

			System.out.println("Dados Envio: " + msg.getDadosEnvio());
			System.out.println("Nome Programa: " + msg.getProgramName());

			jcisConnect.setTimeOut(120000);
			msg = jcisConnect.execute(msg);

			System.out.println("Dados Retorno: " + msg.getDadosRetorno());

			System.out.println("CodRetorno: " + msg.getCodRetorno());
			System.out.println("EstaLogado: " + msg.getEstaLogado());
			System.out.println("Resp: " + msg.getResp());
			System.out.println("Resp2: " + msg.getResp2());
			System.out.println("Tipo Programa: " + msg.getTipoPrograma());

		} catch (LoginException e) {
			System.out.println("Cód. Erro:" + e.getErroCics()
					+ ", Complemento: " + e.getComplementoErroCics()
					+ ", Cód. Sql: " + e.getCodSql() + ", Resp: " + e.getResp()
					+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
		} catch (LeMensagemException e) {
			e.printStackTrace();
			System.out.println("Cód. Erro:" + e.getErroCics()
					+ ", Complemento: " + e.getComplementoErroCics()
					+ ", Cód. Sql: " + e.getCodSql() + ", Resp: " + e.getResp()
					+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
		} catch (ExecuteException e) {
			e.printStackTrace();
			System.out.println("Cód. Erro:" + e.getErroCics()
					+ ", Complemento: " + e.getComplementoErroCics()
					+ ", Cód. Sql: " + e.getCodSql() + ", Resp: " + e.getResp()
					+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
		}

	}

	public static void excluiUsuario() {
		try {
			MensagemCics msg = new MensagemCics();
			msg.setNomeServidor("ibmaplacs.des.extranet.caixa");
			msg.setNumPorta(3183);
			msg.setUserName("iqpointd");
			msg.setPassword("Point#15");

			StringBuilder dadoProvision = new StringBuilder();

			dadoProvision.append("c000534 ");

			msg = jcisConnect.login(msg);

			System.out.println("CodRetorno: " + msg.getCodRetorno());
			System.out.println("EstaLogado: " + msg.getEstaLogado());
			System.out.println("Resp: " + msg.getResp());
			System.out.println("Resp2: " + msg.getResp2());
			System.out.println("Tipo Programa: " + msg.getTipoPrograma());

			System.out.println(dadoProvision);

			String dados = dadoProvision.toString();

			msg.setTransId("NA18");
			msg.setProgramName("NAVPO019");
			msg.setTipoPrograma(msg.getTipoPrograma());
			msg.setDadosEnvio(dados);

			System.out.println("Dados Envio: " + msg.getDadosEnvio());
			System.out.println("Nome Programa: " + msg.getProgramName());

			jcisConnect.setTimeOut(120000);
			msg = jcisConnect.execute(msg);

			System.out.println("Dados Retorno: " + msg.getDadosRetorno());

			System.out.println("CodRetorno: " + msg.getCodRetorno());
			System.out.println("EstaLogado: " + msg.getEstaLogado());
			System.out.println("Resp: " + msg.getResp());
			System.out.println("Resp2: " + msg.getResp2());
			System.out.println("Tipo Programa: " + msg.getTipoPrograma());

		} catch (LoginException e) {
			System.out.println("Cód. Erro:" + e.getErroCics()
					+ ", Complemento: " + e.getComplementoErroCics()
					+ ", Cód. Sql: " + e.getCodSql() + ", Resp: " + e.getResp()
					+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
		} catch (LeMensagemException e) {
			e.printStackTrace();
			System.out.println("Cód. Erro:" + e.getErroCics()
					+ ", Complemento: " + e.getComplementoErroCics()
					+ ", Cód. Sql: " + e.getCodSql() + ", Resp: " + e.getResp()
					+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
		} catch (ExecuteException e) {
			e.printStackTrace();
			System.out.println("Cód. Erro:" + e.getErroCics()
					+ ", Complemento: " + e.getComplementoErroCics()
					+ ", Cód. Sql: " + e.getCodSql() + ", Resp: " + e.getResp()
					+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
		}
	}

	public static void incluiUsuarioAoGrupo() {
		try {
			MensagemCics msg = new MensagemCics();
			msg.setNomeServidor("ibmaplacs.des.extranet.caixa");
			msg.setNumPorta(3183);
			msg.setUserName("iqpointd");
			msg.setPassword("Point#16");

			StringBuilder dadoProvision = new StringBuilder();

			// CD-SISTEMA x(08)
			dadoProvision.append("RAR     ");
			// CD-USUARIO x(08)
			dadoProvision.append("c000534 ");
			// CD-PERFIL x(03)
			dadoProvision.append("799");
			// CD-NIV-SEG x(01)
			dadoProvision.append("6");
			// CD-CEADM-AUT 9(04)---aceita nulo
			dadoProvision.append("0000");
			// CD-SUREG-AUT 9(04)
			dadoProvision.append("0000");
			// CD-UNID-AUT 9(04)
			dadoProvision.append("1032");
			// ID-DIRETORIA x(01) -- CARGO ALTA ADM
			dadoProvision.append("N");
			// ID-STATUS x(01)
			dadoProvision.append("A");
			// DT-PRV-DESL x(10)
			dadoProvision.append("          ");
			// DT-ULT-ACESSO x(10)
			dadoProvision.append("          ");
			// QT-ACESSOS 9(09)
			dadoProvision.append("000000000");
			// QT-MENS-GERAL 9(04)
			dadoProvision.append("0000");
			// QT-MENS-PERFIL 9(04)
			dadoProvision.append("0000");
			// ID-USU-UNID x(01)
			dadoProvision.append("N");
			// ID-VISAO-NACIONAL x(01) -- consulta Nacional
			dadoProvision.append("S");
			// IC-ATUAL-NACIONAL x(01) --- aceita nulo -- atualização Nacional
			dadoProvision.append("N");

			msg = jcisConnect.login(msg);

			System.out.println("CodRetorno: " + msg.getCodRetorno());
			System.out.println("EstaLogado: " + msg.getEstaLogado());
			System.out.println("Resp: " + msg.getResp());
			System.out.println("Resp2: " + msg.getResp2());
			System.out.println("Tipo Programa: " + msg.getTipoPrograma());

			System.out.println(dadoProvision);

//			String dados = dadoProvision.toString();
			
			String dados = "ACI     C001320 0026000001830198NA                    00000000000000000NSS";

			System.out.println("<" + dados + ">");

			msg.setTransId("NA18");
			msg.setProgramName("NAVPO020");
			msg.setTipoPrograma(msg.getTipoPrograma());
			msg.setDadosEnvio(dados);

			System.out.println("Dados Envio: " + msg.getDadosEnvio());
			System.out.println("Nome Programa: " + msg.getProgramName());

			jcisConnect.setTimeOut(120000);
			msg = jcisConnect.execute(msg);

			System.out.println("Dados Retorno: " + msg.getDadosRetorno());

			System.out.println("CodRetorno: " + msg.getCodRetorno());
			System.out.println("EstaLogado: " + msg.getEstaLogado());
			System.out.println("Resp: " + msg.getResp());
			System.out.println("Resp2: " + msg.getResp2());
			System.out.println("Tipo Programa: " + msg.getTipoPrograma());

		} catch (LoginException e) {
			System.out.println("Cód. Erro:" + e.getErroCics()
					+ ", Complemento: " + e.getComplementoErroCics()
					+ ", Cód. Sql: " + e.getCodSql() + ", Resp: " + e.getResp()
					+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
		} catch (LeMensagemException e) {
			e.printStackTrace();
			System.out.println("Cód. Erro:" + e.getErroCics()
					+ ", Complemento: " + e.getComplementoErroCics()
					+ ", Cód. Sql: " + e.getCodSql() + ", Resp: " + e.getResp()
					+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
		} catch (ExecuteException e) {
			e.printStackTrace();
			System.out.println("Cód. Erro:" + e.getErroCics()
					+ ", Complemento: " + e.getComplementoErroCics()
					+ ", Cód. Sql: " + e.getCodSql() + ", Resp: " + e.getResp()
					+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
		}
	}

	public static void desvincularUsuarioSistema() {
		try {
			MensagemCics msg = new MensagemCics();
			msg.setNomeServidor("ibmaplacs.des.extranet.caixa");
			msg.setNumPorta(3183);
			msg.setUserName("iqpointd");
			msg.setPassword("Point#15");

			StringBuilder dadoProvision = new StringBuilder();

			// dadoProvision.append("0020");

			// CD-SISTEMA x(08)
			dadoProvision.append("ACO     ");
			// CD-USUARIO x(08)
			dadoProvision.append("C000534 ");

			msg = jcisConnect.login(msg);

			System.out.println("CodRetorno: " + msg.getCodRetorno());
			System.out.println("EstaLogado: " + msg.getEstaLogado());
			System.out.println("Resp: " + msg.getResp());
			System.out.println("Resp2: " + msg.getResp2());
			System.out.println("Tipo Programa: " + msg.getTipoPrograma());

			System.out.println(dadoProvision);

			String dados = dadoProvision.toString();

			msg.setTransId("NA18");
			msg.setProgramName("NAVPO021");
			msg.setTipoPrograma(msg.getTipoPrograma());
			msg.setDadosEnvio(dados);

			System.out.println("Dados Envio: " + msg.getDadosEnvio());
			System.out.println("Nome Programa: " + msg.getProgramName());

			jcisConnect.setTimeOut(120000);
			msg = jcisConnect.execute(msg);

			System.out.println("Dados Retorno: " + msg.getDadosRetorno());

			System.out.println("CodRetorno: " + msg.getCodRetorno());
			System.out.println("EstaLogado: " + msg.getEstaLogado());
			System.out.println("Resp: " + msg.getResp());
			System.out.println("Resp2: " + msg.getResp2());
			System.out.println("Tipo Programa: " + msg.getTipoPrograma());

		} catch (LoginException e) {
			System.out.println("Cód. Erro:" + e.getErroCics()
					+ ", Complemento: " + e.getComplementoErroCics()
					+ ", Cód. Sql: " + e.getCodSql() + ", Resp: " + e.getResp()
					+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
		} catch (LeMensagemException e) {
			e.printStackTrace();
			System.out.println("Cód. Erro:" + e.getErroCics()
					+ ", Complemento: " + e.getComplementoErroCics()
					+ ", Cód. Sql: " + e.getCodSql() + ", Resp: " + e.getResp()
					+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
		} catch (ExecuteException e) {
			e.printStackTrace();
			System.out.println("Cód. Erro:" + e.getErroCics()
					+ ", Complemento: " + e.getComplementoErroCics()
					+ ", Cód. Sql: " + e.getCodSql() + ", Resp: " + e.getResp()
					+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
		}
	}

	public static void alteraUsuario() {
		try {
			MensagemCics msg = new MensagemCics();
			msg.setNomeServidor("ibmaplacs.des.extranet.caixa");
			msg.setNumPorta(3183);
			msg.setUserName("iqpointd");
			msg.setPassword("Point#15");

			StringBuilder dadoProvision = new StringBuilder();

			// CD-USUARIO X(08)
			dadoProvision.append("C000002 ");
			// NO-USUARIO X(40)
			dadoProvision.append("Olavo Silva Do Nascimento               ");
			// CD-UNIDADE 9(04)
			dadoProvision.append("0000");
			// CD-SIS-DEFAUL x(08)
			dadoProvision.append("        ");
			// CD-SIS-USU x(01)
			dadoProvision.append(" ");
			// CD-SUREG-LOT 9(04)
			dadoProvision.append("0000");
			// ID-DIRETORIA X(01)
			dadoProvision.append(" ");
			// ID-STATUS X(01)
			dadoProvision.append("A");
			// ID-CEF X(01)
			dadoProvision.append(" ");
			// DT-PRV-DESL-I X(1)
			dadoProvision.append("N");
			// DT-PRV-DESL X(10)
			dadoProvision.append("          ");
			// CD-FUNCAO-I X(1)
			dadoProvision.append("N");
			// CD-FUNCAO 9(04)
			dadoProvision.append("0000");
			// NU-CA-EN-FISICA-I X(01)
			dadoProvision.append("N");
			// NU-CA-EN-FISICA 9(04)
			dadoProvision.append("0000");
			// NU-UNIDADE-FISICA-I X(1)
			dadoProvision.append("N");
			// NU-UNIDADE-FISICA 9(04)
			dadoProvision.append("0000");

			msg = jcisConnect.login(msg);

			System.out.println("CodRetorno: " + msg.getCodRetorno());
			System.out.println("EstaLogado: " + msg.getEstaLogado());
			System.out.println("Resp: " + msg.getResp());
			System.out.println("Resp2: " + msg.getResp2());
			System.out.println("Tipo Programa: " + msg.getTipoPrograma());

			System.out.println(dadoProvision);

//			String dados = dadoProvision.toString();
			
			String dados = "C000266 GISELE PINTO RESENDE COSTA DA SILVA     0000         0000 A N          N0000N0000N0000";

			msg.setTransId("NA18");
			msg.setProgramName("NAVPO022");
			msg.setTipoPrograma(msg.getTipoPrograma());
			msg.setDadosEnvio(dados);

			System.out.println("Dados Envio: " + msg.getDadosEnvio());
			System.out.println("Nome Programa: " + msg.getProgramName());

			jcisConnect.setTimeOut(120000);
			msg = jcisConnect.execute(msg);

			System.out.println("Dados Retorno: " + msg.getDadosRetorno());

			System.out.println("CodRetorno: " + msg.getCodRetorno());
			System.out.println("EstaLogado: " + msg.getEstaLogado());
			System.out.println("Resp: " + msg.getResp());
			System.out.println("Resp2: " + msg.getResp2());
			System.out.println("Tipo Programa: " + msg.getTipoPrograma());

		} catch (LoginException e) {
			System.out.println("Cód. Erro:" + e.getErroCics()
					+ ", Complemento: " + e.getComplementoErroCics()
					+ ", Cód. Sql: " + e.getCodSql() + ", Resp: " + e.getResp()
					+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
		} catch (LeMensagemException e) {
			e.printStackTrace();
			System.out.println("Cód. Erro:" + e.getErroCics()
					+ ", Complemento: " + e.getComplementoErroCics()
					+ ", Cód. Sql: " + e.getCodSql() + ", Resp: " + e.getResp()
					+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
		} catch (ExecuteException e) {
			e.printStackTrace();
			System.out.println("Cód. Erro:" + e.getErroCics()
					+ ", Complemento: " + e.getComplementoErroCics()
					+ ", Cód. Sql: " + e.getCodSql() + ", Resp: " + e.getResp()
					+ ", Resp2: " + e.getResp2() + ": " + e.getMessage());
		}
	}

	public static void main(String[] args) throws ServiceLocatorException,
			NamingException {

//		 criarUsuario();
//		 excluiUsuario();
		 incluiUsuarioAoGrupo();
//		 desvincularUsuarioSistema();
//		 alteraUsuario();
	}

}
