package br.com.pbti.dto;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.pbti.conexao.ConnexaoSinav;
import br.com.pbti.vo.AccountSinav;

public class AccountAgreggationSinav {

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

		
//		System.out.println("AgregGeral");
		
		int tamanhoArray = listUser.size();

		for (int i = 0; i < 10; i++) {
			if (listUser.size() > 0) {
				Map<String, Object> mapUser = listUser.get(0);

				AccountSinav acc = (AccountSinav) mapUser.get("Account");
				dados = new HashMap<String, Object>();

				dados.put("CD_USUARIO", acc.getCdUsuario().trim());
				dados.put("NO_USUARIO", acc.getNoUsuario().trim());
				dados.put("CD_UNIDADE", acc.getCodUnidade().trim());
				dados.put("CD_SIS_DEFAUL", acc.getCdSisDefaul().trim());
				dados.put("CD_IMPR_REMOT", acc.getCdImprRemot().trim());
				dados.put("CD_SIT_USU", acc.getCdSitUsu().trim());
				dados.put("CD_SUREG_LOT", acc.getCdSuregLot().trim());
				dados.put("CD_CEADM_AUT", acc.getCdCeadmAut().trim());
				dados.put("CD_SUREG_AUT", acc.getCdSuregAut().trim());
				dados.put("CD_UNID_AUT", acc.getCdUnidAut().trim());
				dados.put("ID_DIRETORIA", acc.getIdDiretoria().trim());
				dados.put("CD_TERMINAL", acc.getCdTerminal().trim());
				dados.put("ID_STATUS", acc.getIdStatus().trim());
				dados.put("ID_CEF", acc.getIdCef().trim());
				dados.put("CD_NO_REMOT", acc.getCdNoRemot().trim());
				dados.put("CD_FUNCAO", acc.getCdFuncao().trim());
				dados.put("CO_SEGMENTO", acc.getCoSegmento().trim());
				dados.put("NU_CNPJ", acc.getNuCnpj().trim());
				dados.put("NU_MATR_EMP", acc.getNuMatrEmp().trim());
				dados.put("NU_CA_EN_FISICA", acc.getNuCaEnFisica().trim());
				dados.put("SIS_PERFIL", mapUser.get("SISPERFIL"));

				conta.put(acc.getCdUsuario().trim(), dados);

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

		AccountSinav acc = (AccountSinav) mapUser.get("Account");
		dados = new HashMap<String, Object>();

		dados.put("CD_USUARIO", acc.getCdUsuario().trim());
		dados.put("NO_USUARIO", acc.getNoUsuario().trim());
		dados.put("CD_UNIDADE", acc.getCodUnidade().trim());
		dados.put("CD_SIS_DEFAUL", acc.getCdSisDefaul().trim());
		dados.put("CD_IMPR_REMOT", acc.getCdImprRemot().trim());
		dados.put("CD_SIT_USU", acc.getCdSitUsu().trim());
		dados.put("CD_SUREG_LOT", acc.getCdSuregLot().trim());
		dados.put("CD_CEADM_AUT", acc.getCdCeadmAut().trim());
		dados.put("CD_SUREG_AUT", acc.getCdSuregAut().trim());
		dados.put("CD_UNID_AUT", acc.getCdUnidAut().trim());
		dados.put("ID_DIRETORIA", acc.getIdDiretoria().trim());
		dados.put("CD_TERMINAL", acc.getCdTerminal().trim());
		dados.put("ID_STATUS", acc.getIdStatus().trim());
		dados.put("ID_CEF", acc.getIdCef().trim());
		dados.put("CD_NO_REMOT", acc.getCdNoRemot().trim());
		dados.put("CD_FUNCAO", acc.getCdFuncao().trim());
		dados.put("CO_SEGMENTO", acc.getCoSegmento().trim());
		dados.put("NU_CNPJ", acc.getNuCnpj().trim());
		dados.put("NU_MATR_EMP", acc.getNuMatrEmp().trim());
		dados.put("NU_CA_EN_FISICA", acc.getNuCaEnFisica().trim());
		dados.put("SIS_PERFIL", mapUser.get("SISPERFIL"));

		return dados;
	}

	public void consulta(String filtro) throws ClassNotFoundException, SQLException  {

		ConnexaoSinav conn = new ConnexaoSinav();
		ResultSet rs = null;
		try {

//			 conn.setUrlServerSybase("jdbc:db2://10.192.224.76:5021/CSD1");
//			 conn.setDriverClassServerSybase("com.ibm.db2.jcc.DB2Driver");
//			 conn.setUserServerSybase("SGALSRHD");
//			 conn.setPasswordServerSybase("s160772");

//			System.out.println("Consulta ==" + urlServer + "==");

			conn.setUrlServerSybase(urlServer);
			conn.setDriverClassServerSybase(driveClass);
			conn.setUserServerSybase(userServer);
			conn.setPasswordServerSybase(passwordServer);

			ArrayList<String> listString = new ArrayList<String>();

			String query;

//			System.out.println("Caralho ==" + filtro + "==");

			if (filtro.isEmpty()) {

				query = "SELECT * FROM " + "gal.GALVWS07_USRO_SAILPOINT LEFT JOIN gal.GALVWS08_USRO_SSTMA_SAILPOINT "
						+ "ON gal.GALVWS07_USRO_SAILPOINT.CD_USUARIO = gal.GALVWS08_USRO_SSTMA_SAILPOINT.CD_USUARIO "
						+ "and gal.GALVWS08_USRO_SSTMA_SAILPOINT.ID_STATUS = 'A' "
						+ "where gal.GALVWS07_USRO_SAILPOINT.ID_STATUS != 'E' "
						+ "and gal.GALVWS07_USRO_SAILPOINT.CD_USUARIO is not null "
						+ "and gal.GALVWS07_USRO_SAILPOINT.CD_USUARIO > 'C'"
						+ "order by gal.GALVWS07_USRO_SAILPOINT.CD_USUARIO asc";

			} else {
				query = "SELECT * FROM " + "gal.GALVWS07_USRO_SAILPOINT LEFT JOIN gal.GALVWS08_USRO_SSTMA_SAILPOINT "
						+ "ON gal.GALVWS07_USRO_SAILPOINT.CD_USUARIO = gal.GALVWS08_USRO_SSTMA_SAILPOINT.CD_USUARIO "
						+ "and gal.GALVWS08_USRO_SSTMA_SAILPOINT.ID_STATUS = 'A' "
						+ "where gal.GALVWS07_USRO_SAILPOINT.ID_STATUS = 'A'  "
						+ "and gal.GALVWS07_USRO_SAILPOINT.CD_USUARIO = '" + filtro + "'";
			}

//			System.out.println("Query ==" + query + "==");

			PreparedStatement ps = conn.preparedStatement(query);

			rs = ps.executeQuery();

			Map<String, Object> mapDados = new HashMap<String, Object>();

			String cdUsuario;
			String noUsuario;
			String codUnidade;
			String cdSisDefaul;
			String cdImprRemot;
			String cdSitUsu;
			String cdSuregLot;
			String cdCeadmAut;
			String cdSuregAut;
			String cdUnidAut;
			String idDiretoria;
			String cdTerminal;
			String idStatus;
			String idCef;
			String cdNoRemot;
			String cdFuncao;
			String coSegmento;
			String nuCnpj;
			String nuMatrEmp;
			String nuCaEnFisica;
			String noPerfil;
			String noSistema;

			while (rs.next()) {

				cdUsuario = rs.getString("CD_USUARIO");
				noUsuario = rs.getString("NO_USUARIO");
				codUnidade = rs.getString("CD_UNIDADE");
				cdSisDefaul = rs.getString("CD_SIS_DEFAUL");
				cdImprRemot = rs.getString("CD_IMPR_REMOT");
				cdSitUsu = rs.getString("CD_SIT_USU");
				cdSuregLot = rs.getString("CD_SUREG_LOT");
				cdCeadmAut = rs.getString("CD_CEADM_AUT");
				cdSuregAut = rs.getString("CD_SUREG_AUT");
				cdUnidAut = rs.getString("CD_UNID_AUT");
				idDiretoria = rs.getString("ID_DIRETORIA");
				cdTerminal = rs.getString("CD_TERMINAL");
				idStatus = rs.getString("ID_STATUS");
				idCef = rs.getString("ID_CEF");
				cdNoRemot = rs.getString("CD_NO_REMOT");
				cdFuncao = rs.getString("CD_FUNCAO");
				coSegmento = rs.getString("CO_SEGMENTO");
				nuCnpj = rs.getString("NU_CNPJ");
				nuMatrEmp = rs.getString("NU_MATR_EMP");
				nuCaEnFisica = rs.getString("NU_CA_EN_FISICA");

				if (rs.getString("CD_SISTEMA") != null) {
					noSistema = rs.getString("CD_SISTEMA");
				} else {
					noSistema = "";
				}

				if (rs.getString("CD_PERFIL") != null) {
					noPerfil = rs.getString("CD_PERFIL");
				} else {
					noPerfil = "";
				}

				if (mapDados == null || mapDados.isEmpty()) {
					mapDados.put(cdUsuario, cdUsuario);
					mapDados.put("Account",
							new AccountSinav(cdUsuario, noUsuario, codUnidade, cdSisDefaul, cdImprRemot, cdSitUsu,
									cdSuregLot, cdCeadmAut, cdSuregAut, cdUnidAut, idDiretoria, cdTerminal, idStatus,
									idCef, cdNoRemot, cdFuncao, coSegmento, nuCnpj, nuMatrEmp, nuCaEnFisica));

					String sisperfil;

					if (noSistema.isEmpty() || noPerfil.isEmpty()) {
						sisperfil = "";
					} else {
						sisperfil = noSistema.trim() + "/" + noPerfil.trim();
					}

					listString.add(sisperfil);
					mapDados.put("SISPERFIL", listString);
				} else {

					boolean booleanMap = mapDados.containsKey(cdUsuario);

					if (booleanMap == true) {
						String sisperfil;

						if (noSistema.isEmpty() || noPerfil.isEmpty()) {
							sisperfil = "";
						} else {
							sisperfil = noSistema.trim() + "/" + noPerfil.trim();
						}
						listString.add(sisperfil);
						mapDados.put("SISPERFIL", listString);

					} else {
						listUser.add(mapDados);

						mapDados = new HashMap<String, Object>();
						listString = new ArrayList<String>();

						mapDados.put(cdUsuario, cdUsuario);
						mapDados.put("Account", new AccountSinav(cdUsuario, noUsuario, codUnidade, cdSisDefaul, cdImprRemot, cdSitUsu,
								cdSuregLot, cdCeadmAut, cdSuregAut, cdUnidAut, idDiretoria, cdTerminal, idStatus,
								idCef, cdNoRemot, cdFuncao, coSegmento, nuCnpj, nuMatrEmp, nuCaEnFisica));
						
						String sisperfil;

						if (noSistema.isEmpty() || noPerfil.isEmpty()) {
							sisperfil = "";
						} else {
							sisperfil = noSistema.trim() + "/" + noPerfil.trim();
						}
						listString.add(sisperfil);
						mapDados.put("SISPERFIL", listString);
					}
				}
			}

			listUser.add(mapDados);

			
			
			sizeListUser = listUser.size();
			
//			System.out.println("Lista SIZE ACCOUNT AGREGGATION ==" + sizeListUser + "==");

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
