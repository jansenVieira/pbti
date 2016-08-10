package br.com.pbti.siric.dto;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.pbti.siric.conexao.ConnexaoSiric;
import br.com.pbti.siric.vo.GroupSiric;

public class GroupAgreggationSiric {

	private String urlServer;
	private String driveClass;
	private String userServer;
	private String passwordServer;

	private ArrayList<Map<String, Object>> listGroup = new ArrayList<Map<String, Object>>();
	private int sizeListGroup;

	public Map<String, Map<String, Object>> agregGeral() throws ClassNotFoundException, SQLException {
		Map<String, Object> dados = new HashMap<String, Object>();
		Map<String, Map<String, Object>> grupo = new HashMap<String, Map<String, Object>>();

		int tamanhoArray = listGroup.size();

		for (int i = 0; i < 10; i++) {
			if (listGroup.size() > 0) {
				Map<String, Object> mapGroup = listGroup.get(0);

				GroupSiric gpr = (GroupSiric) mapGroup.get("Group");
				dados = new HashMap<String, Object>();

				dados.put("CO_GRUPO", gpr.getNumGrupo().trim());
				dados.put("NO_GRUPO", gpr.getDescGrupo().trim());

				grupo.put(gpr.getNumGrupo().trim(), dados);

				listGroup.remove(0);

				sizeListGroup = tamanhoArray - 1;
			} else {
				break;
			}
		}

		return grupo;

	}

	public Map<String, Object> read() throws ClassNotFoundException, SQLException {

//		System.out.println("Account Reader");

		Map<String, Object> dados = new HashMap<String, Object>();

		Map<String, Object> mapUser = listGroup.get(0);

		GroupSiric gpr = (GroupSiric) mapUser.get("Group");
		dados = new HashMap<String, Object>();

		dados.put("CO_GRUPO", gpr.getNumGrupo().trim());
		dados.put("NO_GRUPO", gpr.getDescGrupo().trim());

		return dados;
	}

	public void consultaGroup(String filtro) throws Exception {

		ConnexaoSiric conn = new ConnexaoSiric();

		ResultSet rs = null;

		try {

//			conn.setUrlServerSybase("jdbc:sybase:Tds:10.192.230.59:40000/ASEDB001");
//			conn.setDriverClassServerSybase("com.sybase.jdbc4.jdbc.SybDriver");
//			conn.setUserServerSybase("SGAL001");
//			conn.setPasswordServerSybase("pwsgal001");

//			System.out.println("Consulta ==" + urlServer + "==");

			 conn.setUrlServerSybase(urlServer);
			 conn.setDriverClassServerSybase(driveClass);
			 conn.setUserServerSybase(userServer);
			 conn.setPasswordServerSybase(passwordServer);

//			System.out.println("Filtro ==" + filtro + "==");

			CallableStatement cs = null;

			if (filtro.isEmpty()) {

				cs = conn.callProcedure("{call RICSPT20_CONSULTA_GRU_SIGAL(?)}");
				cs.setString(1, null);

			} else {

				cs = conn.callProcedure("{RICSPT20_CONSULTA_GRU_SIGAL(?)}");
				cs.setString(1, filtro);
			}

			cs.execute();
			rs = cs.getResultSet();

			Map<String, Object> mapGroup;

			String numGrupo;
			String descGrupo;

			while (rs.next()) {
				mapGroup = new HashMap<String, Object>();
				numGrupo = rs.getString("Código do grupo").trim();
				descGrupo = rs.getString("Descrição do grupo");

				mapGroup.put("Group", new GroupSiric(numGrupo, descGrupo));

				listGroup.add(mapGroup);
			}

			sizeListGroup = listGroup.size();

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

	public ArrayList<Map<String, Object>> getListGroup() {
		return listGroup;
	}

	public void setListGroup(ArrayList<Map<String, Object>> listGroup) {
		this.listGroup = listGroup;
	}

	public int getSizeListGroup() {
		return sizeListGroup;
	}

	public void setSizeListGroup(int sizeListGroup) {
		this.sizeListGroup = sizeListGroup;
	}

}
