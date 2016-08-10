package br.com.pbti.siric.dto;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import br.com.pbti.siric.conexao.ConnexaoSiric;
import br.com.pbti.siric.vo.AccountSiric;

public class AccountAgreggationSiric {

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
		
//		System.out.println("tamanhoArray - Agreg Geral "+tamanhoArray);

		for (int i = 0; i < 10; i++) {
			if (listUser.size() > 0) {
				Map<String, Object> mapUser = listUser.get(0);

				AccountSiric acc = (AccountSiric) mapUser.get("Account");
				dados = new HashMap<String, Object>();

				dados.put("codigo_usuario", acc.getCoUsuario().trim());
				dados.put("nome", acc.getNoUsuario().trim());
				dados.put("cod_unid_lotacao", acc.getCodUnidLotacao().trim());
				dados.put("status_usuario", acc.getStatus().trim());
				dados.put("lotacaoid", acc.getLotacaoId().trim());
				dados.put("NU_FUNCAO", acc.getNuFuncao().trim());
				dados.put("NO_GRUPO", mapUser.get("NO_GRUPO"));

				if (acc.getStatus().equals("C")) {
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

//		System.out.println("conta - Agreg Geral "+conta);
		
		
		return conta;

	}

	public Map<String, Object> read() throws ClassNotFoundException, SQLException {

		// System.out.println("Account Reader");

		Map<String, Object> dados = new HashMap<String, Object>();

		Map<String, Object> mapUser = listUser.get(0);

		AccountSiric acc = (AccountSiric) mapUser.get("Account");
		dados = new HashMap<String, Object>();

		dados.put("codigo_usuario", acc.getCoUsuario().trim());
		dados.put("codigo_usuario", acc.getNoUsuario().trim());
		dados.put("cod_unid_lotacao", acc.getCodUnidLotacao().trim());
		dados.put("status_usuario", acc.getStatus().trim());
		dados.put("lotacaoid", acc.getLotacaoId().trim());
		dados.put("NU_FUNCAO", acc.getNuFuncao().trim());
		dados.put("NO_GRUPO", mapUser.get("NO_GRUPO"));

		if (acc.getStatus().equals("C")) {
			dados.put(openconnector.Connector.ATT_DISABLED, new Boolean(true));
		} else {
			dados.put(openconnector.Connector.ATT_DISABLED, new Boolean(false));
		}

		return dados;
	}

	public void consulta(String filtro) throws Exception {

		ConnexaoSiric conn = new ConnexaoSiric();
		ResultSet rs = null;

		try {

			conn.setUrlServerSybase("jdbc:sybase:Tds:10.192.230.59:55000/RICDB001");
			conn.setDriverClassServerSybase("com.sybase.jdbc4.jdbc.SybDriver");
			conn.setUserServerSybase("siric_des");
			conn.setPasswordServerSybase("warp11");

			 System.out.println("Consulta ==" + urlServer + "==");

			// conn.setUrlServerSybase(urlServer);
			// conn.setDriverClassServerSybase(driveClass);
			// conn.setUserServerSybase(userServer);
			// conn.setPasswordServerSybase(passwordServer);

			ArrayList<String> listString = new ArrayList<String>();

			 System.out.println("Filtro ==" + filtro + "==");

			CallableStatement cs = null;

			if (filtro.isEmpty()) {

				cs = conn.callProcedure("{call RICSPT17_CONS_GRU_USU_SIGAL(?, ?) }");
				cs.setString(1, null);
				cs.setString(2, null);

			} else {
				cs = conn.callProcedure("{call RICSPT17_CONS_GRU_USU_SIGAL(?, ?) }");
				cs.setString(1, filtro);
				cs.setString(2, null);
			}

			System.out.println("procedure ==  RICSPT17_CONS_GRU_USU_SIGAL + ");

			cs.execute();

			rs = cs.getResultSet();

			Map<String, Object> mapDados = new HashMap<String, Object>();
			Map<String, Object> mapDadosFinal = new HashMap<String, Object>();

			String coUsuario;
			String noUsuario;
			String status;
			String lotacaoId;
			String codUnidLotacao;
			String nuFuncao;
			String nomGrupo;

			while (rs.next()) {
				
				coUsuario = rs.getString("codigo_usuario");
				noUsuario = rs.getString("nome");
				status = rs.getString("status_usuario");
				lotacaoId = rs.getString("lotacaoid");

					codUnidLotacao = rs.getString("cod_unid_lotacao");
					nuFuncao = rs.getString("NU_FUNCAO");
					nomGrupo = rs.getString("descricao_grupo");
					
				if (mapDados == null || mapDados.isEmpty()) {
					mapDados.put(coUsuario, coUsuario);
					mapDados.put("Account",
							new AccountSiric(coUsuario, noUsuario, status, lotacaoId, codUnidLotacao, nuFuncao));

					listString.add(nomGrupo);
					mapDados.put("NO_GRUPO", listString);
					
					mapDadosFinal.put(coUsuario, mapDados);
					
				} else {

					boolean booleanMap = mapDadosFinal.containsKey(coUsuario);

					if (booleanMap == true) {

						
						Map<String, Object> mapDadosEstrai = (Map<String, Object>) mapDadosFinal.get(coUsuario);
						
						
						
						
						listString.add(nomGrupo);
						mapDados.put("NO_GRUPO", listString);

					} else {
						
						mapDadosFinal.put(coUsuario, mapDados);

						mapDados = new HashMap<String, Object>();
						listString = new ArrayList<String>();

						mapDados.put(coUsuario, coUsuario);
						mapDados.put("Account",
								new AccountSiric(coUsuario, noUsuario, status, lotacaoId, codUnidLotacao, nuFuncao));
						listString.add(nomGrupo);
						mapDados.put("NO_GRUPO", listString);
					}
				}
			}

			listUser.add(mapDadosFinal);
			
			sizeListUser = listUser.size();

			if (!rs.isClosed()) {
				rs.close();
				rs = null;
			}

			conn.close();

		} catch (Exception e) {
			System.out.println(e);

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
	
	
//	public void consulta(String filtro) throws Exception {
//
//		ConnexaoSiric conn = new ConnexaoSiric();
//		ResultSet rs = null;
//
//		try {
//
//			conn.setUrlServerSybase("jdbc:sybase:Tds:10.192.230.59:55000/RICDB001");
//			conn.setDriverClassServerSybase("com.sybase.jdbc4.jdbc.SybDriver");
//			conn.setUserServerSybase("siric_des");
//			conn.setPasswordServerSybase("warp11");
//
//			 System.out.println("Consulta ==" + urlServer + "==");
//
//			// conn.setUrlServerSybase(urlServer);
//			// conn.setDriverClassServerSybase(driveClass);
//			// conn.setUserServerSybase(userServer);
//			// conn.setPasswordServerSybase(passwordServer);
//
//			ArrayList<String> listString = new ArrayList<String>();
//
//			 System.out.println("Filtro ==" + filtro + "==");
//
//			CallableStatement cs = null;
//
//			if (filtro.isEmpty()) {
//
//				cs = conn.callProcedure("{call RICSPT17_CONS_GRU_USU_SIGAL(?, ?) }");
//				cs.setString(1, null);
//				cs.setString(2, null);
//
//			} else {
//				cs = conn.callProcedure("{call RICSPT17_CONS_GRU_USU_SIGAL(?, ?) }");
//				cs.setString(1, filtro);
//				cs.setString(2, null);
//			}
//
//			System.out.println("procedure ==  RICSPT17_CONS_GRU_USU_SIGAL + ");
//
//			cs.execute();
//
//			rs = cs.getResultSet();
//
//			Map<String, Object> mapDados = new HashMap<String, Object>();
//
//			String coUsuario;
//			String noUsuario;
//			String status;
//			String lotacaoId;
//			String codUnidLotacao;
//			String nuFuncao;
//			String nomGrupo;
//
//			while (rs.next()) {
//				
//				coUsuario = rs.getString("codigo_usuario");
//				noUsuario = rs.getString("nome");
//				status = rs.getString("status_usuario");
//				lotacaoId = rs.getString("lotacaoid");
//
//					codUnidLotacao = rs.getString("cod_unid_lotacao");
//					nuFuncao = rs.getString("NU_FUNCAO");
//					nomGrupo = rs.getString("descricao_grupo");
//					
//				if (mapDados == null || mapDados.isEmpty()) {
//					mapDados.put(coUsuario, coUsuario);
//					mapDados.put("Account",
//							new AccountSiric(coUsuario, noUsuario, status, lotacaoId, codUnidLotacao, nuFuncao));
//
//					listString.add(nomGrupo);
//					mapDados.put("NO_GRUPO", listString);
//				} else {
//
//					boolean booleanMap = mapDados.containsKey(coUsuario);
//
//					if (booleanMap == true) {
//
//						listString.add(nomGrupo);
//						mapDados.put("NO_GRUPO", listString);
//
//					} else {
//						listUser.add(mapDados);
//
//						mapDados = new HashMap<String, Object>();
//						listString = new ArrayList<String>();
//
//						mapDados.put(coUsuario, coUsuario);
//						mapDados.put("Account",
//								new AccountSiric(coUsuario, noUsuario, status, lotacaoId, codUnidLotacao, nuFuncao));
//						listString.add(nomGrupo);
//						mapDados.put("NO_GRUPO", listString);
//					}
//				}
//			}
//
//			listUser.add(mapDados);
//
//			sizeListUser = listUser.size();
//
//			if (!rs.isClosed()) {
//				rs.close();
//				rs = null;
//			}
//
//			conn.close();
//
//		} catch (Exception e) {
//			System.out.println(e);
//
//		} finally {
//			if (rs != null) {
//				if (!rs.isClosed()) {
//					rs.close();
//					rs = null;
//				}
//			}
//			conn.close();
//		}
//
//	}
	
	

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
