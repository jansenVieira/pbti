package br.com.pbti.sistema.lerCSV;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.pbti.manipulador.properties.LerProperties;

public class LerArquivo {

	static String URL = "url";
	static String SENHA = "password";
	static String USUARIO = "user";
	static String DRIVE = "driverClass";

	private static Connection connection;
	public static PreparedStatement ps;

	public static ArrayList<String> arraySistema = new ArrayList<String>();
	public static ArrayList<Map<String, Object>> arrayDadosSistema = new ArrayList<Map<String, Object>>();
	public static Map<String, Object> mapSistema;
	public static Map<String, Object> mapDadosSistema;

	static LerProperties lerproperties = new LerProperties();

	public static void lerArquivoNovo() {

		selectSistemas();

		for (String sistema : arraySistema) {

			dadosSistema(sistema);

		}
	}

	// conexao ao banco
	protected static PreparedStatement preparedStatement(String sql) throws SQLException, ClassNotFoundException {
		try {
			valorConexao();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ps = null;
		ps = getConnection().prepareStatement(sql);

		return ps;
	}

	// conexao ao banco
	protected static Connection getConnection() throws SQLException, ClassNotFoundException {
		if (connection == null) {
			Class.forName(DRIVE);
			connection = DriverManager.getConnection(URL, USUARIO, SENHA);
		}
		return connection;
	}

	// Obtem os dados para acesso ao banco
	public static void valorConexao() throws IOException {
		lerproperties.dadosProperties();

		URL = lerproperties.getUrlBanco();
		SENHA = lerproperties.getSenhaBanco();
		USUARIO = lerproperties.getUserBanco();
		DRIVE = lerproperties.getDriveBanco();

	}

	// Select todos os Sitemas
	public static void selectSistemas() {

		try {
			ps = preparedStatement("SELECT distinct cod_sistema FROM mip04_full order by cod_sistema");
			ResultSet rs = ps.executeQuery();

			String cod_sistema = "cod_sistema";

			while (rs.next()) {

				arraySistema.add(rs.getString(cod_sistema));
			}

			// arraySistema.add("SID00");
			// arraySistema.add("SIGSJ");
			// arraySistema.add("SIGPF");
			// arraySistema.add("SIAUT");
			// arraySistema.add("PWCTR");
			// arraySistema.add("DESBP");
			// arraySistema.add("SICAC");

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

	}

	public static void dadosSistema(String sistema) {

		try {
			ps = preparedStatement(
					"SELECT distinct cod_sistema, permite_dup, prioridade_dup, owner FROM mip04_full where cod_sistema='"
							+ sistema + "' order by cod_sistema");
			ResultSet rs = ps.executeQuery();

			String cod_sistema = "cod_sistema", permite_dup = "permite_dup", prioridade_dup = "prioridade_dup",
					owner = "owner";

			ArrayList<Map<String, Object>> arrayDadosSistemaTemp = new ArrayList<Map<String, Object>>();

			while (rs.next()) {

				mapSistema = new HashMap<String, Object>();
				if (rs.getString(cod_sistema) == null) {
					mapSistema.put(cod_sistema, "");
				} else {
					mapSistema.put(cod_sistema, rs.getString(cod_sistema));
				}

				if (rs.getString(permite_dup) == null) {
					mapSistema.put(permite_dup, "");
				} else {
					mapSistema.put(permite_dup, rs.getString(permite_dup));
				}

				if (rs.getString(prioridade_dup) == null) {
					mapSistema.put(prioridade_dup, "");
				} else {
					mapSistema.put(prioridade_dup, rs.getString(prioridade_dup));
				}

				if (rs.getString(owner) == null) {
					mapSistema.put(owner, "");
				} else {
					mapSistema.put(owner, rs.getString(owner));
				}

				arrayDadosSistemaTemp.add(mapSistema);
			}

			int tamanho = arrayDadosSistemaTemp.size();

			if (tamanho >= 2) {

				for (Map<String, Object> dadosMap : arrayDadosSistemaTemp) {

					mapDadosSistema = new HashMap<String, Object>();
					Object sist;
					Object dup;
					Object priorid;
					Object ownerd;

					if (dadosMap.get(cod_sistema) == null) {
						sist = "";
					} else {
						sist = dadosMap.get(cod_sistema);
					}

					if (dadosMap.get(permite_dup) == null) {
						dup = "";
					} else {
						dup = dadosMap.get(permite_dup);
					}

					if (dadosMap.get(prioridade_dup) == null) {
						priorid = "";
					} else {
						priorid = dadosMap.get(prioridade_dup);
					}

					if (dadosMap.get(owner) == null) {
						ownerd = dadosMap.get(owner);
					} else {
						ownerd = dadosMap.get(owner);
					}
					Object traco = "-";
					Object vazio = "";
					if (!priorid.equals(traco) && !priorid.equals(vazio) && priorid != null) {
						mapDadosSistema.put(cod_sistema, sist);
						mapDadosSistema.put(permite_dup, dup);
						mapDadosSistema.put(prioridade_dup, priorid);
						mapSistema.put(owner, ownerd);

						arrayDadosSistema.add(mapDadosSistema);
					}
				}
			} else {

				arrayDadosSistema.addAll(arrayDadosSistemaTemp);

			}

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

	}

	// public static void main(String[] args) {
	//
	// lerArquivoNovo();
	//
	// System.out.println(arrayDadosSistema);
	// }

}
