package br.com.pbti.dto;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.pbti.conexao.Connexao;
import br.com.pbti.vo.Account;

public class AccountAgreggation {

	private String urlServer;
	private String driveClass;
	private String userServer;
	private String passwordServer;
	private ArrayList<Map<String, Object>> listUser = new ArrayList<Map<String, Object>>();
	private int sizeListUser;

	public Map<String, Map<String, Object>> agregGeral() throws ClassNotFoundException, SQLException {
		Map<String, Object> dados = new HashMap<String, Object>();
		Map<String, Map<String, Object>> conta = new HashMap<String, Map<String, Object>>();

		// if (continua == false) {
		// listUser = consulta();
		// }

		int tamanhoArray = listUser.size();

		for (int i = 0; i < 10; i++) {
			if (listUser.size() > 0) {
				Map<String, Object> mapUser = listUser.get(0);

				Account acc = (Account) mapUser.get("Account");
				dados = new HashMap<String, Object>();

				dados.put("CO_USUARIO", acc.getCoUsuario().trim());
				dados.put("NO_USUARIO", acc.getNoUsuario().trim());
				dados.put("IC_STATUS", acc.getIcStatus().trim());
				dados.put("IC_TIPO", acc.getIcTipo().trim());
				dados.put("NU_MATR", acc.getNuMatricula().trim());
				dados.put("SIS_PERFIL", mapUser.get("SISPERFIL"));

				if (acc.getIcStatus().equals("1")) {
					dados.put(openconnector.Connector.ATT_DISABLED, new Boolean(true));
				} else {
					dados.put(openconnector.Connector.ATT_DISABLED, new Boolean(false));
				}

				conta.put(acc.getCoUsuario().trim(), dados);

				listUser.remove(0);

				sizeListUser = tamanhoArray - 1;
			} else {
				break;
			}

		}

		return conta;

	}

	public Map<String, Object> read() throws ClassNotFoundException, SQLException {

//		System.out.println("Account Reader");

		Map<String, Object> dados = new HashMap<String, Object>();

		Map<String, Object> mapUser = listUser.get(0);

		Account acc = (Account) mapUser.get("Account");
		dados = new HashMap<String, Object>();

		dados.put("CO_USUARIO", acc.getCoUsuario().trim());
		dados.put("NO_USUARIO", acc.getNoUsuario().trim());
		dados.put("IC_STATUS", acc.getIcStatus().trim());
		dados.put("IC_TIPO", acc.getIcTipo().trim());
		dados.put("NU_MATR", acc.getNuMatricula().trim());
		dados.put("SIS_PERFIL", mapUser.get("SISPERFIL"));

		if (acc.getIcStatus().equals("1")) {
			dados.put(openconnector.Connector.ATT_DISABLED, new Boolean(true));
		} else {
			dados.put(openconnector.Connector.ATT_DISABLED, new Boolean(false));
		}

		return dados;
	}

	public void consulta(String filtro) throws ClassNotFoundException, SQLException {

		Connexao conn = new Connexao();
		ResultSet rs = null;

		try {

			// conn.setUrlServerSybase("jdbc:sybase:Tds:10.192.230.59:40000/ASEDB001");
			// conn.setDriverClassServerSybase("com.sybase.jdbc4.jdbc.SybDriver");
			// conn.setUserServerSybase("SGAL001");
			// conn.setPasswordServerSybase("pwsgal001");

//			System.out.println("Consulta ==" + urlServer + "==");

			conn.setUrlServerSybase(urlServer);
			conn.setDriverClassServerSybase(driveClass);
			conn.setUserServerSybase(userServer);
			conn.setPasswordServerSybase(passwordServer);

			ArrayList<String> listString = new ArrayList<String>();

			String query;

//			System.out.println("Filtro ==" + filtro + "==");

			if (filtro.isEmpty()) {

				query = "select distinct a.CO_USUARIO, a.NO_USUARIO, a.IC_STATUS, a.NU_MATR, a.IC_TIPO, b.CO_SISTEMA, b.NU_PERFIL, b.NO_PERFIL"
						+ " from ASEDB001.dbo.ASEVW021_USUARIO_SAILPOINT a, ASEDB001.dbo.ASEVW022_USUARIO_CONEXAO_SAILP b"
						+ " where a.CO_USUARIO != null and a.CO_USUARIO *= b.CO_USUARIO order by a.CO_USUARIO";

			} else {
				query = "select distinct a.CO_USUARIO, a.NO_USUARIO, a.IC_STATUS, a.NU_MATR, a.IC_TIPO, b.CO_SISTEMA, b.NU_PERFIL, b.NO_PERFIL"
						+ " from ASEDB001.dbo.ASEVW021_USUARIO_SAILPOINT a, ASEDB001.dbo.ASEVW022_USUARIO_CONEXAO_SAILP b"
						+ " where a.CO_USUARIO = '" + filtro
						+ "' and a.CO_USUARIO *= b.CO_USUARIO order by a.CO_USUARIO";
			}

//			System.out.println("Query ==" + query + "==");

			PreparedStatement ps = conn.preparedStatement(query);

			rs = ps.executeQuery();

			Map<String, Object> mapDados = new HashMap<String, Object>();

			String codUsuario;
			String noUsuario;
			String codSistema;
			String noPerf;
			String icStatus;
			String icTipo;
			String matricula;

			while (rs.next()) {
				codUsuario = rs.getString("CO_USUARIO");
				noUsuario = rs.getString("NO_USUARIO");
				icStatus = rs.getString("IC_STATUS");
				icTipo = rs.getString("IC_TIPO");

				if (rs.getString("CO_SISTEMA") != null) {
					codSistema = rs.getString("CO_SISTEMA");
				} else {
					codSistema = "";
				}

				if (rs.getString("NO_PERFIL") != null) {
					noPerf = rs.getString("NO_PERFIL");
				} else {
					noPerf = "";
				}

				matricula = rs.getString("NU_MATR");

				if (mapDados == null || mapDados.isEmpty()) {
					mapDados.put(codUsuario, codUsuario);
					mapDados.put("Account", new Account(codUsuario, noUsuario, icStatus, icTipo, matricula));

					String sisperfil;

					if (codSistema.isEmpty() || noPerf.isEmpty()) {
						sisperfil = "";
					} else {
						sisperfil = codSistema.trim() + "/" + noPerf.trim();
					}

					listString.add(sisperfil);
					mapDados.put("SISPERFIL", listString);
				} else {

					boolean booleanMap = mapDados.containsKey(codUsuario);

					if (booleanMap == true) {
						String sisperfil;

						if (codSistema.isEmpty() || noPerf.isEmpty()) {
							sisperfil = "";
						} else {
							sisperfil = codSistema.trim() + "/" + noPerf.trim();
						}
						listString.add(sisperfil);
						mapDados.put("SISPERFIL", listString);

					} else {
						listUser.add(mapDados);

						mapDados = new HashMap<String, Object>();
						listString = new ArrayList<String>();

						mapDados.put(codUsuario, codUsuario);
						mapDados.put("Account", new Account(codUsuario, noUsuario, icStatus, icTipo, matricula));
						String sisperfil;

						if (codSistema.isEmpty() || noPerf.isEmpty()) {
							sisperfil = "";
						} else {
							sisperfil = codSistema.trim() + "/" + noPerf.trim();
						}
						listString.add(sisperfil);
						mapDados.put("SISPERFIL", listString);
					}
				}
			}

			listUser.add(mapDados);

			sizeListUser = listUser.size();

			if (!rs.isClosed()) {
				rs.close();
				rs = null;
			}

			conn.close();

		} finally {
			if (rs != null) {
				if (!rs.isClosed()) {
					rs.close();
					rs = null;
				}
			}
			conn.close();
		}

	}

	public int getSizeListUser() {
		return sizeListUser;
	}

	public void setSizeListUser(int sizeListUser) {
		this.sizeListUser = sizeListUser;
	}

	public ArrayList<Map<String, Object>> getListUser() {
		return listUser;
	}

	public void setListUser(ArrayList<Map<String, Object>> listUser) {
		this.listUser = listUser;
	}

	public String getUrlServer() {
		return urlServer;
	}

	public void setUrlServer(String urlServer) {
		this.urlServer = urlServer;
	}

	public String getDriveClass() {
		return driveClass;
	}

	public void setDriveClass(String driveClass) {
		this.driveClass = driveClass;
	}

	public String getUserServer() {
		return userServer;
	}

	public void setUserServer(String userServer) {
		this.userServer = userServer;
	}

	public String getPasswordServer() {
		return passwordServer;
	}

	public void setPasswordServer(String passwordServer) {
		this.passwordServer = passwordServer;
	}

}
