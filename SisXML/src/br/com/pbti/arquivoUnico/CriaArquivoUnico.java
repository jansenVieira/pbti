package br.com.pbti.arquivoUnico;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import br.com.pbti.manipulador.properties.LerProperties;

public class CriaArquivoUnico {

	public static ArrayList<Object> arrayMIP04 = new ArrayList<Object>();
	public static ArrayList<Map<String, Object>> arrayValorMIP04 = new ArrayList<Map<String, Object>>();
	public static Map<String, Object> valorMip04;
	public static ArrayList<Map<String, Object>> arraySistemaMIP04 = new ArrayList<Map<String, Object>>();
	public static Map<String, Object> sistemaMip04;
	public static ArrayList<Map<String, Object>> arrayPerfilMIP04 = new ArrayList<Map<String, Object>>();
	public static Map<String, Object> perfilMip04;
	public static ArrayList<Map<String, Object>> arrayArquivos = new ArrayList<Map<String, Object>>();
	public static ArrayList<Object> arrayArquivosPerfil = new ArrayList<Object>();
	public static Map<String, Object> mapArquivos;
	public static Map<String, Object> mapSistemaPerfil;
	public static ArrayList<Map<String, Object>> arraySistemaPerfil = new ArrayList<Map<String, Object>>();

	public static String SISTEMA = "SISTEMA";
	public static String ARQUIVO = "ARQUIVO";
	public static String ARQUIVO_SISTEMA = "ARQUIVO_SISTEMA";
	public static String LISTA_ARQUIVO_PEFIL = "LISTA_ARQUIVO_PEFIL";
	public static String LISTA_LINHAS = "LISTA_LINHAS";

	static LerProperties lerproperties = new LerProperties();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void nomeMip() {
		
		try {
			lerproperties.dadosProperties();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		File file = new File(lerproperties.getUrlXml()+"/");
		file.mkdirs();

		Collection files = FileUtils.listFiles(file, null, true);

		arrayMIP04.addAll(files);

	}

	public static void trataNomes() {

		for (Object separaArquivos : arrayMIP04) {
			valorMip04 = new HashMap<String, Object>();

			String url = separaArquivos.toString();

			String s[] = url.split("/");
//			String s[] = url.split("\\\\");
			
//			System.out.println(s.toString());
			
			int tm = s.length;
			valorMip04.put(SISTEMA, s[tm-2]);
			valorMip04.put(ARQUIVO, s[tm-1]);

			arrayValorMIP04.add(valorMip04);
		}
	}

	public static void separaSistemaPerfil() {

		for (Map<String, Object> arquivos : arrayValorMIP04) {
			sistemaMip04 = new HashMap<String, Object>();
			perfilMip04 = new HashMap<String, Object>();

			String sistema = arquivos.get(SISTEMA).toString();
			String nomeArquivo = arquivos.get(ARQUIVO).toString();

			String s[] = nomeArquivo.split("\\.");
			String arquivoSistema = s[0];

			if (sistema.equals(arquivoSistema)) {
				sistemaMip04.put(SISTEMA, sistema);
				sistemaMip04.put(ARQUIVO, nomeArquivo);

				arraySistemaMIP04.add(sistemaMip04);
			} else {

				perfilMip04.put(SISTEMA, sistema);
				perfilMip04.put(ARQUIVO, nomeArquivo);

				arrayPerfilMIP04.add(perfilMip04);
			}
		}
	}

	public static void juntaSistmaPerfil() {

		for (Map<String, Object> sistema : arraySistemaMIP04) {
			mapArquivos = new HashMap<String, Object>();
			arrayArquivosPerfil = new ArrayList<Object>();

			Object nomeSistema = sistema.get(SISTEMA);
			Object nomeArquivo = sistema.get(ARQUIVO);

			mapArquivos.put(SISTEMA, nomeSistema);
			mapArquivos.put(ARQUIVO_SISTEMA, nomeArquivo);

			for (Map<String, Object> perfil : arrayPerfilMIP04) {
				Object nomeSistemaPerfil = perfil.get(SISTEMA);
				Object nomeArquivoPerfil = perfil.get(ARQUIVO);

				if (nomeSistema.equals(nomeSistemaPerfil)) {
					arrayArquivosPerfil.add(nomeArquivoPerfil);
				}
			}

			mapArquivos.put(LISTA_ARQUIVO_PEFIL, arrayArquivosPerfil);

			arrayArquivos.add(mapArquivos);
		}
	}

	@SuppressWarnings({ "unchecked", "resource" })
	public static void lerArquivos() throws IOException {


		for(Map<String, Object> arquivos :arrayArquivos)
		{
			ArrayList<Object> arrayPerfil = new ArrayList<Object>();
			ArrayList<String> arrayPerfilTemp = new ArrayList<String>();
			mapSistemaPerfil = new HashMap<String, Object>();
			
			Object nomeSistema = arquivos.get(SISTEMA);
			Object arquivoSistema = arquivos.get(ARQUIVO_SISTEMA);
			Object listaPerfil = arquivos.get(LISTA_ARQUIVO_PEFIL);
			String sitemaPerfil;
			String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
			String doctypeBundle = "<!DOCTYPE Bundle PUBLIC \"sailpoint.dtd\" \"sailpoint.dtd\">";
			
			arrayPerfil = (ArrayList<Object>) listaPerfil;
			
			System.out.println(lerproperties.getUrlXml()+"/"+nomeSistema+"/"+arquivoSistema);
			
			BufferedReader buffeReaderSistema = new BufferedReader(new FileReader(lerproperties.getUrlXml()+"/"+nomeSistema+"/"+arquivoSistema));
			
			while ((sitemaPerfil = buffeReaderSistema.readLine()) != null) {

				if(!xml.equals(sitemaPerfil) && !doctypeBundle.equals(sitemaPerfil))
				{
					arrayPerfilTemp.add(sitemaPerfil);
				}
			}
			
//			System.out.println(arrayPerfil);
			
			for(Object listPerfil :arrayPerfil)
			{
				BufferedReader buffeReaderPerfil = new BufferedReader(new FileReader(lerproperties.getUrlXml()+"/"+nomeSistema+"/"+listPerfil));
				
				while ((sitemaPerfil = buffeReaderPerfil.readLine()) != null) {

					if(!xml.equals(sitemaPerfil) && !doctypeBundle.equals(sitemaPerfil))
					{
						arrayPerfilTemp.add(sitemaPerfil);
					}
				}
			}
			
			mapSistemaPerfil.put(SISTEMA, nomeSistema);
			mapSistemaPerfil.put(LISTA_LINHAS, arrayPerfilTemp);
			
			arraySistemaPerfil.add(mapSistemaPerfil);
			
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public static void escreveArquivo() throws IOException 
	{
		for(Map<String, Object> arquivos :arraySistemaPerfil)
		{
			ArrayList<Object> arrayLinhas = new ArrayList<Object>();
			
			Object nomeSistema = arquivos.get(SISTEMA);
			Object arquivoSistema = arquivos.get(LISTA_LINHAS);
			
			arrayLinhas = (ArrayList<Object>) arquivoSistema;
			
			try {
				lerproperties.dadosProperties();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			BufferedWriter out = new BufferedWriter(new FileWriter(lerproperties.getUrlXml()+"/XMLUnificado/"+nomeSistema+"Unificaodo.xml"));
			String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
			String doctypeSailpoint = "<!DOCTYPE sailpoint PUBLIC \"sailpoint.dtd\" \"sailpoint.dtd\">";
			String abriSailpoint = "<sailpoint>";
			String fechaSailpoint = "</sailpoint>";
			
			out.write(xml);
			out.write("\n"+doctypeSailpoint);
			out.write("\n"+abriSailpoint);
			
			for(Object linhas: arrayLinhas)
			{
				out.write("\n"+linhas.toString());
			}
			
			out.write("\n"+fechaSailpoint);
			out.close();
			
			System.out.println("-------------Sistema-"+nomeSistema+"-----------");
			System.out.println("-------------Arquivo-"+nomeSistema+"Unificaodo-----------");
		}
		
	}
	
	
	public void arquivoUnico()
	{
		nomeMip();
		
		 trataNomes();
		
		 separaSistemaPerfil();
		 
		 juntaSistmaPerfil();
		 
		 try {
			lerArquivos();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 try {
			escreveArquivo();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) throws IOException {

		 nomeMip();
		
		 trataNomes();
		
		 separaSistemaPerfil();
		 
		 juntaSistmaPerfil();
		 
		 lerArquivos();
		 
		 escreveArquivo();
	}
}
