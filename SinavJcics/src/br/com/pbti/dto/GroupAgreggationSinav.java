package br.com.pbti.dto;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.pbti.conexao.ConnexaoSinav;
import br.com.pbti.vo.GroupSinav;

public class GroupAgreggationSinav {

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

				GroupSinav gpr = (GroupSinav) mapGroup.get("Group");
				dados = new HashMap<String, Object>();

				dados.put("CD_PERFIL", gpr.getCodigoPerfil().trim());
				dados.put("CD_SISTEMA", gpr.getCodigoSistema().trim());
				dados.put("NO_PERFIL", gpr.getNomePerfil().trim());
				dados.put("SIS_PERFIL", gpr.getCodigoSistema().trim() + "/" + gpr.getNomePerfil().trim());

				grupo.put(gpr.getCodigoSistema().trim() + "/" + gpr.getNomePerfil().trim(), dados);

				listGroup.remove(0);

				sizeListGroup = tamanhoArray - 1;
			} else {
				break;
			}
		}

		return grupo;

	}

	public Map<String, Object> read() throws ClassNotFoundException, SQLException {

		System.out.println("Account Reader");

		Map<String, Object> dados = new HashMap<String, Object>();

		Map<String, Object> mapUser = listGroup.get(0);

		GroupSinav gpr = (GroupSinav) mapUser.get("Group");
		dados = new HashMap<String, Object>();

		dados.put("CD_PERFIL", gpr.getCodigoPerfil().trim());
		dados.put("CD_SISTEMA", gpr.getCodigoSistema().trim());
		dados.put("NO_PERFIL", gpr.getNomePerfil().trim());
		dados.put("SIS_PERFIL", gpr.getCodigoSistema().trim() + "/" + gpr.getNomePerfil().trim());

		return dados;
	}

	public void consultaGroup(String filtro) throws Exception {

		ConnexaoSinav conn = new ConnexaoSinav();

		ResultSet rs = null;

		try {

			conn.setUrlServerSybase("jdbc:db2://10.192.224.76:5021/CSD1");
			conn.setDriverClassServerSybase("com.ibm.db2.jcc.DB2Driver");
			conn.setUserServerSybase("SGALSRHD");
			conn.setPasswordServerSybase("s160772");

			// System.out.println("Consulta ==" + urlServer + "==");

			// conn.setUrlServerSybase(urlServer);
			// conn.setDriverClassServerSybase(driveClass);
			// conn.setUserServerSybase(userServer);
			// conn.setPasswordServerSybase(passwordServer);

			// System.out.println("Filtro ==" + filtro + "==");

			String query;

			CallableStatement cs = null;

			if (filtro.isEmpty()) {

				query = "SELECT * FROM gal.GALVWS09_PRFL_SAILPOINT";

			} else {

				String seperaGrupo = filtro.toString();

				String sis = "", per = "";

				String[] separa = seperaGrupo.split("/");
				sis = separa[0].toString();
				per = separa[1].toString();

				query = "SELECT * FROM gal.GALVWS09_PRFL_SAILPOINT where CD_PERFIL='" + per + "' AND CD_SISTEMA='" + sis
						+ "'";

			}

			PreparedStatement ps = conn.preparedStatement(query);

			rs = ps.executeQuery();

			Map<String, Object> mapGroup;

			String codSistema;
			String nomePerfil;
			String codPerfil;

			while (rs.next()) {
				mapGroup = new HashMap<String, Object>();
				codSistema = rs.getString("CD_SISTEMA").trim();
				nomePerfil = rs.getString("CD_PERFIL");
				codPerfil = rs.getString("NO_PERFIL");

				mapGroup.put("Group", new GroupSinav(codSistema, codPerfil, nomePerfil));

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
