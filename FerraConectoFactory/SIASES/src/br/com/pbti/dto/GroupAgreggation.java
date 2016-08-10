package br.com.pbti.dto;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.pbti.conexao.Connexao;
import br.com.pbti.vo.Group;

public class GroupAgreggation {

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

				Group gpr = (Group) mapGroup.get("Group");
				dados = new HashMap<String, Object>();

				dados.put("NU_PERFIL", gpr.getCodigoPerfil().trim());
				dados.put("CO_SISTEMA", gpr.getCodigoSistema().trim());
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

		Group gpr = (Group) mapUser.get("Group");
		dados = new HashMap<String, Object>();

		dados.put("NU_PERFIL", gpr.getCodigoPerfil().trim());
		dados.put("CO_SISTEMA", gpr.getCodigoSistema().trim());
		dados.put("NO_PERFIL", gpr.getNomePerfil().trim());
		dados.put("SIS_PERFIL", gpr.getCodigoSistema().trim() + "/" + gpr.getNomePerfil().trim());

		return dados;
	}

	public void consultaGroup(String filtro) throws Exception {

		Connexao conn = new Connexao();

		ResultSet rs = null;

		try {

//			conn.setUrlServerSybase("jdbc:sybase:Tds:10.192.230.59:40000/ASEDB001");
//			conn.setDriverClassServerSybase("com.sybase.jdbc4.jdbc.SybDriver");
//			conn.setUserServerSybase("SGAL001");
//			conn.setPasswordServerSybase("pwsgal001");

			System.out.println("Consulta ==" + urlServer + "==");

			 conn.setUrlServerSybase(urlServer);
			 conn.setDriverClassServerSybase(driveClass);
			 conn.setUserServerSybase(userServer);
			 conn.setPasswordServerSybase(passwordServer);

			System.out.println("Filtro ==" + filtro + "==");

			CallableStatement cs = null;

			if (filtro.isEmpty()) {

				cs = conn.callProcedure("{call ASESP502_LISTA_PERFIS (?, ?)}");
				cs.setString(1, null);
				cs.setString(2, null);

			} else {

				String sistema = "", perfil = "";
				String[] separa = filtro.split("/");
				sistema = separa[0].toString();
				perfil = separa[1].toString();

				cs = conn.callProcedure("{call ASESP502_LISTA_PERFIS (?, ?)}");
				cs.setString(1, sistema);
				cs.setString(2, perfil);
			}

			cs.execute();
			rs = cs.getResultSet();

			Map<String, Object> mapGroup;

			String codSistema;
			String nomePerfil;
			String codPerfil;

			while (rs.next()) {
				mapGroup = new HashMap<String, Object>();
				codSistema = rs.getString("CO_SISTEMA").trim();
				nomePerfil = rs.getString("NO_PERFIL");
				codPerfil = rs.getString("NU_PERFIL");

				mapGroup.put("Group", new Group(codSistema, codPerfil, nomePerfil));

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
