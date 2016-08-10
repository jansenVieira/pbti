package br.com.pbti.perfil.testMipP2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import br.com.pbti.manipulador.properties.LerProperties;

public class mipPorTipoUnidadePerfil {

	static String URL = "url";
	static String SENHA = "password";
	static String USUARIO = "user";
	static String DRIVE = "driverClass";
	static LerProperties lerproperties = new LerProperties();

	private static Connection connection;
	public static PreparedStatement ps;

	// Map Pesquisa
	static Map<String, Object> mapListaFuncao;

	// Array Pesquisa
	static List<Map<String, Object>> arrayListFuncao = new ArrayList<Map<String, Object>>();
	static List<String> arrayTipoUnidade;
	public static List<String> arraySistema = new ArrayList<String>();
	public static List<String> arrayPerfil = new ArrayList<String>();
	public static List<String> listUgName = new ArrayList<String>();

	// Array MIP04
	public static List<Map<String, Object>> arrayCampoMip04 = new ArrayList<Map<String, Object>>();
	public static List<Map<String, Object>> arraySelectRssNomeMip04 = new ArrayList<Map<String, Object>>();
	public static List<Map<String, Object>> arrayRssNomeMip04 = new ArrayList<Map<String, Object>>();
	public static List<String> arrayUgNomeMip04 = new ArrayList<String>();
	public static List<Map<String, Object>> arrayPerfilMip04 = new ArrayList<Map<String, Object>>();
	public static List<Map<String, Object>> arraySistemaMip04 = new ArrayList<Map<String, Object>>();

	// Map MIP04
	static Map<String, Object> mapCampoMip04;
	static Map<String, Object> mapRssNameMip04;
	static Map<String, Object> mapPerfilMip04;
	static Map<String, Object> mapSistemaMip04;

	// Array MIP06
	public static List<Map<String, Object>> arrayDadosMip06 = new ArrayList<Map<String, Object>>();
	public static List<Map<String, Object>> arrayDadosPerfilMip06 = new ArrayList<Map<String, Object>>();

	// Map MIP06
	static Map<String, Object> mapDadosMip06;
	static Map<String, Object> mapDadosPerfilMip06;

	// Array Final
	static Map<String, Object> mapDados;

	// Map Final
	public static ArrayList<Map<String, Object>> arrayDados = new ArrayList<Map<String, Object>>();

	static String listaDados = "listaDados";

	// variavel SQL MIP06
	static String cod_sistemaMIP06 = "cod_sistema";
	static String cod_perfilMIP06 = "cod_perfil";
	static String tipo_unidade = "tipo_unidade";
	static String funcao = "funcao";
	static String cod_funcao = "cod_funcao";
	static String cod_unidade = "cod_unidade";
	static String tipo_usuario = "tipo_usuario";
	static String cod_cargo = "cod_cargo";
	static String ind_unidade = "ind_unidade";
	static String listaTipoUnidade = "listaTipoUnidade";

	// variavel SQL MIP04
	static String cod_sistemaMIP04 = "cod_sistema";
	static String cod_perfilMIP04 = "cod_perfil";
	static String ug_name = "ug_name";
	static String rss_name = "rss_name";
	static String status = "status";
	static String permite_dup = "permite_dup";
	static String prioridade_dup = "prioridade_dup";
	static String funcionalidade = "funcionalidade";
	static String nivel_seguranca = "nivel_seguranca";
	static String atualizacao_nacional = "atualizacao_nacional";
	static String visao_diretoria = "visao_diretoria";
	static String sis_sinav = "sis_sinav";
	static String consulta_nacional = "consulta_nacional";
	static String rss_type = "rss_type";
	static String listaUGNAME = "listUgName";
	static String listaRSSNAME = "listRssName";
	static String listaPerfil = "listaPerfil";

	// variavel MAP
	static String listTipo_unidade = "listTipo_unidade";

	@SuppressWarnings("unused")
	public static void mip04(String sistema) {
		int cont = 0;
		int i = 1;

		arraySistemaMip04 = new ArrayList<Map<String, Object>>();
		arrayDados = new ArrayList<Map<String, Object>>();
		arraySistema = new ArrayList<String>();

		// selectSistemas();

		// int tamanho = arraySistema.size() -1;
		//
		// for (String sis : arraySistema) {

		// new Thread().start();

		// if(cont < 2) {
		// String sistema;

		// String sistema = "SIINB";

		// sistema = sis;

		arrayPerfil = new ArrayList<String>();

		try{
		
		selectPerfil(sistema);

		arrayPerfilMip04 = new ArrayList<Map<String, Object>>();

		for (String perf : arrayPerfil) {
			String perfil;

			perfil = perf;

			arrayRssNomeMip04 = new ArrayList<Map<String, Object>>();

			trataMip04(sistema, perfil);

			mapPerfilMip04 = new HashMap<String, Object>();

			mapPerfilMip04.put(cod_perfilMIP04, perfil);
			mapPerfilMip04.put(listaRSSNAME, arrayRssNomeMip04);

			arrayPerfilMip04.add(mapPerfilMip04);

		}

		mapSistemaMip04 = new HashMap<String, Object>();
		mapSistemaMip04.put(cod_sistemaMIP04, sistema);
		mapSistemaMip04.put(listaPerfil, arrayPerfilMip04);

		arraySistemaMip04.add(mapSistemaMip04);

		cont = i++;

		// System.out.println(cont + " - Sucesso total MAP04");

		// } else {
		// break;
		// }
		// }

		// System.out.println("Sucesso Termino MAP04");

		preparaParaMip06();
		
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}

	public static void preparaParaMip06() {
		int cont = 0;
		int i = 1;

		int cont2 = 0;
		int ia = 1;

		int tamanho = arraySistemaMip04.size() - 1;
		
		try{
		for (Map<String, Object> dados : arraySistemaMip04) {

			// if(cont < 2) {

			String codSistema, codPerfil, ugName, rssName, statusMip4, rssType;
			ArrayList<Map<String, Object>> arrayListaPerfil = new ArrayList<Map<String, Object>>();

			codSistema = dados.get(cod_sistemaMIP04).toString();
			arrayListaPerfil.addAll((Collection<? extends Map<String, Object>>) dados.get(listaPerfil));

			arrayDadosPerfilMip06 = new ArrayList<Map<String, Object>>();

			int tamanho2 = arrayListaPerfil.size() - 1;

			// System.out.println(cont + " - Sistema: " + codSistema);

			for (Map<String, Object> listPerfil : arrayListaPerfil) {

				// if(cont2 < 2)
				// {

				codPerfil = listPerfil.get(cod_perfilMIP04).toString();

				trataDados(codSistema, codPerfil);

				mapDadosPerfilMip06 = new HashMap<String, Object>();

				mapDadosPerfilMip06.put(cod_perfilMIP04, codPerfil);
				ArrayList<Map<String, Object>> arrayRss = new ArrayList<Map<String, Object>>();
				arrayRss.addAll((Collection<? extends Map<String, Object>>) listPerfil.get(listaRSSNAME));

				mapDadosPerfilMip06.put(listaRSSNAME, arrayRss);
				mapDadosPerfilMip06.put(listaTipoUnidade, arrayDadosMip06);

				arrayDadosPerfilMip06.add(mapDadosPerfilMip06);

				cont2 = ia++;
				// System.out.println(cont2 + " - Perfil: " + codPerfil);
				// } else {
				// break;
				// }
			}

			mapDados = new HashMap<String, Object>();

			mapDados.put(cod_sistemaMIP04, codSistema);
			mapDados.put(listaDados, arrayDadosPerfilMip06);

			arrayDados.add(mapDados);

			cont = i++;

			//
			// } else {
			// break;
			// }
		}
		
		}catch (Exception e){
			e.printStackTrace();
			System.out.println(e);
		}
	}

	@SuppressWarnings("unused")
	public static void trataDados(String codSistema, String codPerfil) {

		int cont = 0;
		int i = 0;

		selectFuncao(codSistema, codPerfil);
		arrayDadosMip06 = new ArrayList<Map<String, Object>>();

		try{
		
		for (Map<String, Object> dados : arrayListFuncao) {
			String noFuncao, codFuncao, codUnidade, tipoUsuario, codCargo, indUnidade;

			noFuncao = dados.get(funcao).toString();
			codFuncao = dados.get(cod_funcao).toString();
			codUnidade = dados.get(cod_unidade).toString();
			tipoUsuario = dados.get(tipo_usuario).toString();
			codCargo = dados.get(cod_cargo).toString();
			indUnidade = dados.get(ind_unidade).toString();

			selectTipoUnidade(codSistema, codPerfil, noFuncao, codFuncao, codUnidade, tipoUsuario, codCargo,
					indUnidade);

			mapDadosMip06 = new HashMap<String, Object>();

			// mapDadosMip06.put(cod_sistemaMIP06, codSistema);
			// mapDadosMip06.put(cod_perfilMIP06, codPerfil);
			mapDadosMip06.put(listTipo_unidade, arrayTipoUnidade);
			mapDadosMip06.put(funcao, dados.get(funcao));
			mapDadosMip06.put(cod_funcao, dados.get(cod_funcao));
			mapDadosMip06.put(cod_unidade, dados.get(cod_unidade));
			mapDadosMip06.put(tipo_usuario, dados.get(tipo_usuario));
			mapDadosMip06.put(cod_cargo, dados.get(cod_cargo));
			mapDadosMip06.put(ind_unidade, dados.get(ind_unidade));
			// mapDadosMip06.put(ug_name,ugName);
			// mapDadosMip06.put(rss_name,rssName);
			// mapDadosMip06.put(rss_type,rssType);
			// mapDadosMip06.put(status,statusMip4);

			arrayDadosMip06.add(mapDadosMip06);
			// cont = i++;

			// System.out.println(cont+" - Dados: "+codPerfil);
		}
		
		} catch (Exception e){
			e.printStackTrace();
			System.out.println(e);
		}
		
	}

	public static void trataMip04(String sistema, String perfil) {
		arrayCampoMip04 = new ArrayList<Map<String, Object>>();

		selectCampoMip04(sistema, perfil);

		try{
			
		
		for (Map<String, Object> dados : arrayCampoMip04) {
			String statusFor, permite_dupFor, prioridade_dupFor, nivel_segurancaFor, atualizacao_nacionalFor,
					consulta_nacionalFor, sis_sinavFor, funcionalidadeFor, visaoDiretoriaFor;

			statusFor = dados.get(status).toString();
			permite_dupFor = dados.get(permite_dup).toString();
			prioridade_dupFor = dados.get(prioridade_dup).toString();
			funcionalidadeFor = dados.get(funcionalidade).toString();
			nivel_segurancaFor = dados.get(nivel_seguranca).toString();
			atualizacao_nacionalFor = dados.get(atualizacao_nacional).toString();
			visaoDiretoriaFor = dados.get(visao_diretoria).toString();
			sis_sinavFor = dados.get(sis_sinav).toString();

			consulta_nacionalFor = dados.get(consulta_nacional).toString();

			arraySelectRssNomeMip04 = new ArrayList<Map<String, Object>>();

			selectRssName(statusFor, permite_dupFor, prioridade_dupFor, sistema, perfil);

			for (Map<String, Object> dadosRss : arraySelectRssNomeMip04) {
				String rss_nameFor, rssTyperFor, ownerFor, nomeAtributoAplicativoFor;

				rss_nameFor = dadosRss.get(rss_name).toString();
				rssTyperFor = dadosRss.get(rss_type).toString();
				ownerFor = dadosRss.get("owner").toString();
				nomeAtributoAplicativoFor = dadosRss.get("nome_atributo_aplicativo").toString();

				arrayUgNomeMip04 = new ArrayList<String>();

				selectUgName(sistema, perfil, rss_nameFor, rssTyperFor);

				mapRssNameMip04 = new HashMap<String, Object>();

				mapRssNameMip04.put(rss_name, rss_nameFor);
				mapRssNameMip04.put(rss_type, rssTyperFor);
				mapRssNameMip04.put("owner", ownerFor);
				mapRssNameMip04.put("nome_atributo_aplicativo", nomeAtributoAplicativoFor);
				mapRssNameMip04.put(status, statusFor);
				mapRssNameMip04.put(permite_dup, permite_dupFor);
				mapRssNameMip04.put(funcionalidade, funcionalidadeFor);
				mapRssNameMip04.put(prioridade_dup, prioridade_dupFor);
				mapRssNameMip04.put(nivel_seguranca, nivel_segurancaFor);
				mapRssNameMip04.put(atualizacao_nacional, atualizacao_nacionalFor);
				mapRssNameMip04.put(visao_diretoria, visaoDiretoriaFor);
				mapRssNameMip04.put(sis_sinav, sis_sinavFor);

				mapRssNameMip04.put(consulta_nacional, consulta_nacionalFor);
				mapRssNameMip04.put(listaUGNAME, arrayUgNomeMip04);

				arrayRssNomeMip04.add(mapRssNameMip04);
			}
		}
		
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
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
	public static void selectSistemas(String cod_sistema, String maior) {

		try {
			
			if(cod_sistema.equals("*") && maior.equals("*"))
			{
				ps = preparedStatement("SELECT distinct cod_sistema FROM mip04_full order by cod_sistema");
			} 
			
			if(!cod_sistema.equals("*") && maior.equals("*"))
			{
				ps = preparedStatement("SELECT distinct cod_sistema FROM mip04_full where cod_sistema = '"+cod_sistema+"' order by cod_sistema");
			}
			
			if(maior.equals(">"))
			{
				ps = preparedStatement("SELECT distinct cod_sistema FROM mip04_full where cod_sistema >= '"+cod_sistema+"' order by cod_sistema");
			}
									
			// ps = preparedStatement("SELECT distinct cod_sistema FROM mip04"
			// + " where cod_sistema ='SIDEC'"
			// + " and cod_sistema ='SID00'"
			// + " and cod_sistema ='SIRIC'"
			// + " and cod_sistema ='SIGSJ'"
			// + " and cod_sistema ='SIGPF'"
			// + " and cod_sistema ='SIAUT'"
			// + " and cod_sistema ='PWCTR'"
			// + " and cod_sistema ='DESBP'"
			// + " and cod_sistema ='SICAC'"
			// + " and cod_sistema ='PCSIS'"
			// + " order by cod_sistema");

			// ps = preparedStatement("SELECT distinct cod_sistema FROM mip04
			// where cod_sistema cod_sistema>'SIGAN' order by cod_sistema");
			// ps = preparedStatement("SELECT distinct cod_sistema FROM mip04
			// where sis_sinav = 's' order by cod_sistema");
			// ps = preparedStatement("SELECT distinct cod_sistema FROM mip04
			// where cod_sistema = 'SIGCB' order by cod_sistema");
			// ps = preparedStatement("SELECT distinct cod_sistema FROM mip04
			// where cod_sistema >= 'SISIB' order by cod_sistema");
			// ps = preparedStatement("SELECT distinct cod_sistema FROM mip04
			// where cod_sistema >= 'SIGCB' and (status='' or permite_dup='' or
			// prioridade_dup='') order by cod_sistema");

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {

				arraySistema.add(rs.getString(cod_sistemaMIP04));
			}

			rs.close();

			// arraySistema.add("SIDEC");
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
		} finally {

			try {
				ps.close();

			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	// Select Todos os Perfil para cada sistema
	public static void selectPerfil(String sistema) {

		try {
			ps = preparedStatement("SELECT distinct cod_perfil FROM mip04_full where" + " cod_sistema='" + sistema + "'"
					+ " order by cod_perfil");

			// ps = preparedStatement("SELECT distinct cod_perfil FROM mip04
			// where cod_sistema='SIAGE' and cod_perfil = '990' order by
			// cod_perfil");

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {

				arrayPerfil.add(rs.getString(cod_perfilMIP04));
			}

			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {

			try {
				ps.close();

			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	public static void selectRssName(String status, String permite_dup, String prioridade_dup, String sistema,
			String perfil) {

		try {
			ps = preparedStatement(
					"SELECT distinct rss_name, rss_type, owner, nome_aplicativo, nome_atributo_aplicativo   FROM mip04_full where"
							+ " cod_sistema='" + sistema + "'" + " and cod_perfil='" + perfil + "'"
							+ " order by rss_name");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				mapRssNameMip04 = new HashMap<String, Object>();

				if (rs.getString("nome_aplicativo") == null) {
					mapRssNameMip04.put(rss_name, "");
				} else {
					mapRssNameMip04.put(rss_name, rs.getString("nome_aplicativo"));
				}

				if (rs.getString(rss_type) == null) {
					mapRssNameMip04.put(rss_type, "");
				} else {
					mapRssNameMip04.put(rss_type, rs.getString(rss_type));
				}

				if (rs.getString("owner") == null) {
					mapRssNameMip04.put("owner", "");
				} else {
					mapRssNameMip04.put("owner", rs.getString("owner"));
				}

				if (rs.getString("nome_atributo_aplicativo") == null) {
					mapRssNameMip04.put("nome_atributo_aplicativo", "");
				} else {
					mapRssNameMip04.put("nome_atributo_aplicativo", rs.getString("nome_atributo_aplicativo"));
				}
				// atualização 15/06

				arraySelectRssNomeMip04.add(mapRssNameMip04);
			}

			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {

			try {
				ps.close();

			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	public static void selectUgName(String sistema, String perfil, String rss_name, String rssType) {

		try {
			ps = preparedStatement("SELECT distinct ug_name FROM mip04_full where" + " cod_sistema ='" + sistema + "'"
					+ " and cod_perfil='" + perfil + "'" + " and nome_aplicativo='" + rss_name + "'" + " and rss_type='"
					+ rssType + "'" + " order by ug_name");
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				arrayUgNomeMip04.add(rs.getString(ug_name));
			}

			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {

			try {
				ps.close();

			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	public static void selectCampoMip04(String sistema, String perfil) {

		try {
			ps = preparedStatement(
					"SELECT distinct status, permite_dup, prioridade_dup, nivel_seguranca, atualizacao_nacional, visao_diretoria, "
							+ "consulta_nacional, sis_sinav, funcionalidade FROM mip04_full  where" + " cod_sistema='"
							+ sistema + "'" + " and cod_perfil='" + perfil + "'");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				mapCampoMip04 = new HashMap<String, Object>();

				if (rs.getString(status) == null) {
					mapCampoMip04.put(status, "");
				} else {
					mapCampoMip04.put(status, rs.getString(status));
				}

				if (rs.getString(permite_dup) == null) {
					mapCampoMip04.put(permite_dup, "");
				} else {
					mapCampoMip04.put(permite_dup, rs.getString(permite_dup));
				}

				if (rs.getString(sis_sinav) == null) {
					mapCampoMip04.put(sis_sinav, "");
				} else {
					mapCampoMip04.put(sis_sinav, rs.getString(sis_sinav));
				}

				if (rs.getString(prioridade_dup) == null) {
					mapCampoMip04.put(prioridade_dup, "");
				} else {
					mapCampoMip04.put(prioridade_dup, rs.getString(prioridade_dup));
				}

				if (rs.getString(funcionalidade) == null) {
					mapCampoMip04.put(funcionalidade, "");
				} else {
					mapCampoMip04.put(funcionalidade, rs.getString(funcionalidade));
				}

				if (rs.getString(nivel_seguranca) == null) {
					mapCampoMip04.put(nivel_seguranca, "");
				} else {
					mapCampoMip04.put(nivel_seguranca, rs.getString(nivel_seguranca));
				}

				if (rs.getString(atualizacao_nacional) == null) {
					mapCampoMip04.put(atualizacao_nacional, "");
				} else {
					mapCampoMip04.put(atualizacao_nacional, rs.getString(atualizacao_nacional));
				}

				if (rs.getString(consulta_nacional) == null) {
					mapCampoMip04.put(consulta_nacional, "");
				} else {
					mapCampoMip04.put(consulta_nacional, rs.getString(consulta_nacional));
				}

				if (rs.getString(visao_diretoria) == null) {
					mapCampoMip04.put(visao_diretoria, "");
				} else {
					mapCampoMip04.put(visao_diretoria, rs.getString(visao_diretoria));
				}
				// alteração 15/06
				// mapCampoMip04.put(prioridade_dup,
				// rs.getString(prioridade_dup));

				if (arrayCampoMip04.isEmpty()) {
					arrayCampoMip04.add(mapCampoMip04);
				} else if (!mapCampoMip04.get(sis_sinav).equals("")
						|| !mapCampoMip04.get(atualizacao_nacional).equals("")) {
					arrayCampoMip04 = new ArrayList<Map<String, Object>>();

					arrayCampoMip04.add(mapCampoMip04);
				}

				else {
					break;
				}

			}

			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {

			try {
				ps.close();

			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	public static void selectFuncao(String sistema, String perfil) {

		try {
			arrayListFuncao = new ArrayList<Map<String, Object>>();

			ps = preparedStatement(
					"SELECT distinct funcao, cod_funcao, cod_unidade, tipo_usu, cod_cargo, ind_unidade FROM mip_06 "
							+ "where cod_sistema ='" + sistema + "'" + "and cod_perfil ='" + perfil + "'"
							+ "order by cod_funcao");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				mapListaFuncao = new HashMap<String, Object>();

				mapListaFuncao.put(funcao, rs.getString(funcao));
				mapListaFuncao.put(cod_funcao, rs.getString(cod_funcao));
				mapListaFuncao.put(cod_unidade, rs.getString(cod_unidade));
				mapListaFuncao.put(tipo_usuario, rs.getString("tipo_usu"));
				mapListaFuncao.put(cod_cargo, rs.getString(cod_cargo));
				mapListaFuncao.put(ind_unidade, rs.getString(ind_unidade));

				arrayListFuncao.add(mapListaFuncao);
			}

			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {

			try {
				ps.close();

			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	public static void selectTipoUnidade(String sistema, String perfil, String noFuncao, String codFuncao,
			String codUnidade, String tipoUsuario, String codCargo, String indUnidade) {

		try {
			ps = preparedStatement("SELECT distinct tipo_unidade FROM mip_06 where " + " cod_sistema = '" + sistema + "'"
					+ " and cod_perfil = '" + perfil + "'" + " and funcao = '" + noFuncao + "'" + " and cod_funcao ='"
					+ codFuncao + "'" + " and cod_unidade = '" + codUnidade + "'" + " and tipo_usu = '"
					+ tipoUsuario + "'" + " and cod_cargo = '" + codCargo + "'" + " and ind_unidade = '" + indUnidade
					+ "'" + " order by tipo_unidade");
			ResultSet rs = ps.executeQuery();

			arrayTipoUnidade = new ArrayList<String>();

			while (rs.next()) {

				arrayTipoUnidade.add(rs.getString(tipo_unidade));
			}

			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {

			try {
				ps.close();

			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

//	public static void main(String[] args) throws FileNotFoundException, ParserConfigurationException,
//			TransformerFactoryConfigurationError, TransformerException, SQLException, ClassNotFoundException {
//
//		// mip04();
//
//	}

	// public void run(String sistema) {
	//
	// mip04(sistema);
	//
	// }
}
