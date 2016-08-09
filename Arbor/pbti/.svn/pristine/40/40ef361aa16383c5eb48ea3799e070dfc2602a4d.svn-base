package br.com.pbti.lerCSV;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.io.FileUtils;

public class LerArquivo {

	public ArrayList<String> arrayMIP04 = new ArrayList<String>();
	public ArrayList<String> arrayMIP06 = new ArrayList<String>();
	public Map<String, Object> mip04;
	public ArrayList<Map<String, Object>> arrayMapMip04 = new ArrayList<Map<String, Object>>();
	public ArrayList<Map<String, Object>> arrayMapMip04Mip06 = new ArrayList<Map<String, Object>>();
	public Map<String, Object> mip06;
	public Map<String, Object> mipFinal;
	public ArrayList<Map<String, Object>> arrayMapMip06 = new ArrayList<Map<String, Object>>();
	public ArrayList<Map<String, Object>> mip06Mpi04 = new ArrayList<Map<String, Object>>();
	public ArrayList<Map<String, Object>> arrayMipCodFuncao = new ArrayList<Map<String, Object>>();
	public ArrayList<Map<String, Object>> arrayMipFinal = new ArrayList<Map<String, Object>>();
	public Map<String, Object> arrayMipSistemaPerfil;

	public String LIST_PERFIL = "LIST_PERFIL";
	public String COD_SISTEMA = "COD_SISTEMA";
	public String COD_PERFIL = "COD_PERFIL";
	public String UG_NAME = "UG_NAME";
	public String STATUS = "STATUS";
	public String RSS_TYPE = "RSS_TYPE";
	public String LIST_FUNCAO = "LIST_FUNCAO";
	public String PERMITE_DUP = "PERMITE_DUP";
	public String NomeArquivoMIP04;
	public String NomeArquivoMIP06;
//	public String MIP04 = "MIP04";
//	public String MIP06 = "MIP06";

	public Object ugNameGlobal;
	public Object rssTypeGlobal;
	public Object permiteDupGlobal;
	public Object statusGlobal;

	public String COD_SISTEMA_MIP06 = "COD_SISTEMA",
			COD_PERFIL_MIP06 = "COD_PERFIL", TIPO_UNIDADE = "TIPO_UNIDADE",
			FUNCAO = "FUNCAO", COD_FUNCAO = "COD_FUNCAO",
			COD_UNIDADE = "COD_UNIDADE", TIPO_USU = "TIPO_USU",
			COD_CARGO = "COD_CARGO", IND_UNIDADE = "IND_UNIDADE";
	public String Mip04;
	public String Mip06;

	@SuppressWarnings("rawtypes")
	public void nomeMip() {
		ArrayList<Map<String, Object>> arrayMapMip04 = new ArrayList<Map<String, Object>>();
		ArrayList<Map<String, Object>> arrayMapMip06 = new ArrayList<Map<String, Object>>();
		Map<String, Object> mapMip04;
		Map<String, Object> mapMip06;
		Map<String, Object> mapMip04Mip06;

		File file = new File("D:\\BundleIIQXML\\MIP\\");
		file.mkdirs();
		String[] extensions = { "csv" };

		Collection files = FileUtils.listFiles(file, extensions, false);
		Collection filenames = filesToFilenames(files);

		String mip04 = "MIP04.csv";
		String mip06 = "MIP06.csv";

		for (Object nomeFile : filenames) {
			mapMip04 = new HashMap<String, Object>();
			mapMip06 = new HashMap<String, Object>();

			String[] separa = nomeFile.toString().split("-");
			String sistema = separa[0];
			String mip = separa[1];

			if (mip.equals(mip04)) {

				mapMip04.put("Sistema", sistema);
				mapMip04.put("NomeArquivoMIP", nomeFile);

				arrayMapMip04.add(mapMip04);
			}

			if (mip.equals(mip06)) {

				mapMip06.put("Sistema", sistema);
				mapMip06.put("NomeArquivoMIP", nomeFile);

				arrayMapMip06.add(mapMip06);
			}
		}
		
		for(Map<String, Object> aMip04:arrayMapMip04)
		{
			mapMip04Mip06 = new HashMap<String, Object>();
			
			Object sistemaMip04 = aMip04.get("Sistema");
			Object nomeAquivoMip04 = aMip04.get("NomeArquivoMIP");
			
			for(Map<String, Object> aMip06:arrayMapMip06){
				
				Object sistemaMip06 = aMip06.get("Sistema");
				Object nomeAquivoMip06 = aMip06.get("NomeArquivoMIP");
				
				if(sistemaMip04.equals(sistemaMip06))
				{
					mapMip04Mip06.put("MIP04", nomeAquivoMip04);
					mapMip04Mip06.put("MIP06", nomeAquivoMip06);
					
					arrayMapMip04Mip06.add(mapMip04Mip06);
					
					break;
				}
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Collection filesToFilenames(Collection files) {
		Collection filenames = new java.util.ArrayList(files.size());
		Iterator i = files.iterator();
		while (i.hasNext()) {
			filenames.add(((File) i.next()).getName());
		}
		return filenames;
	}

	public void lerMIP04(String NomeArquivoMIP04) throws FileNotFoundException,
			ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException {
		arrayMIP04 = new ArrayList<String>(); 
		
		Scanner scannerMip04 = new Scanner(new FileReader(
				"D://BundleIIQXML//MIP//"+NomeArquivoMIP04)).useDelimiter(",");

		while (scannerMip04.hasNext()) {
			arrayMIP04.add(scannerMip04.nextLine());
		}

		arrayMIP04.remove(0);

		for (String dadosMIP04 : arrayMIP04) {
			mip04 = new HashMap<String, Object>();
			ArrayList<String> recebePalavraSeparados = new ArrayList<String>();
			// receber o valor do receberListaLinhas
			for (String separaCampo : dadosMIP04.split("\"")) {

				if (!separaCampo.equals(",")) {

					recebePalavraSeparados.add(separaCampo);
				}

				if (recebePalavraSeparados.get(0).equals("")) {
					recebePalavraSeparados.remove(0);
				}
			}
			mip04.put(COD_SISTEMA, recebePalavraSeparados.get(0));
			mip04.put(COD_PERFIL, recebePalavraSeparados.get(1));
			mip04.put(UG_NAME, recebePalavraSeparados.get(2));
			mip04.put(STATUS, recebePalavraSeparados.get(5));
			mip04.put(RSS_TYPE, recebePalavraSeparados.get(8));
			mip04.put(PERMITE_DUP, recebePalavraSeparados.get(19));

			arrayMapMip04.add(mip04);
		}

	}

	public void lerMIP06(String NomeArquivoMIP06) throws FileNotFoundException,
			ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException {
		arrayMIP06 = new ArrayList<String>();
		
		Scanner scannerMip06 = new Scanner(new FileReader(
				"D://BundleIIQXML//MIP//"+NomeArquivoMIP06)).useDelimiter(",");

		while (scannerMip06.hasNext()) {
			arrayMIP06.add(scannerMip06.nextLine());
		}

		arrayMIP06.remove(0);

		for (String dadosMIP06 : arrayMIP06) {
			mip06 = new HashMap<String, Object>();
			ArrayList<String> recebePalavraSeparados = new ArrayList<String>();
			// receber o valor do receberListaLinhas
			for (String separaCampo : dadosMIP06.split("\"")) {

				if (!separaCampo.equals(",")) {

					recebePalavraSeparados.add(separaCampo);
				}


				if (recebePalavraSeparados.get(0).equals("")) {
					recebePalavraSeparados.remove(0);
				}
			}

			mip06.put(COD_SISTEMA, recebePalavraSeparados.get(0));
			mip06.put(COD_PERFIL, recebePalavraSeparados.get(1));
			mip06.put(TIPO_UNIDADE, recebePalavraSeparados.get(2));
			mip06.put(FUNCAO, recebePalavraSeparados.get(3));
			mip06.put(COD_FUNCAO, recebePalavraSeparados.get(4));
			mip06.put(COD_UNIDADE, recebePalavraSeparados.get(5));
			mip06.put(TIPO_USU, recebePalavraSeparados.get(6));
			mip06.put(COD_CARGO, recebePalavraSeparados.get(7));
			mip06.put(IND_UNIDADE, recebePalavraSeparados.get(8));

			arrayMapMip06.add(mip06);
		}
	}

	public void juntarMips(Object codSistema, Object codPerfil) {

		for (Map<String, Object> separaMip04 : arrayMapMip04) {
			Object codSistemaMip04 = separaMip04.get(COD_SISTEMA);
			Object codPerfilMip04 = separaMip04.get(COD_PERFIL);
			Object ugName = separaMip04.get(UG_NAME);
			Object rssType = separaMip04.get(RSS_TYPE);
			Object permiteDup = separaMip04.get(PERMITE_DUP);
			Object status = separaMip04.get(STATUS);

			if (codSistemaMip04.equals(codSistema)
					&& codPerfilMip04.equals(codPerfil)) {
				ugNameGlobal = ugName;
				rssTypeGlobal = rssType;
				permiteDupGlobal = permiteDup;
				statusGlobal = status;
			}
		}
	}

	public void separaCodFuncao() {

		ArrayList<Map<String, Object>> mipTemporaria = new ArrayList<Map<String, Object>>();
		ArrayList<Object> arrayFuncao = new ArrayList<Object>();
		mipFinal = new HashMap<String, Object>();
		arrayMipCodFuncao =  new ArrayList<Map<String, Object>>(); 

		int index = arrayMapMip06.size() - 1;
		int contador = 0;
		int i = 0;

		for (Map<String, Object> separaMip06Mip04 : arrayMapMip06) {

			contador = i++;

			Object codFuncao = separaMip06Mip04.get(COD_FUNCAO);
			Object codSistema = separaMip06Mip04.get(COD_SISTEMA_MIP06);
			Object codPerfil = separaMip06Mip04.get(COD_PERFIL_MIP06);
			Object tipoUnidade = separaMip06Mip04.get(TIPO_UNIDADE);
			Object funcao = separaMip06Mip04.get(FUNCAO);
			Object codUnidade = separaMip06Mip04.get(COD_UNIDADE);
			Object tipoUsu = separaMip06Mip04.get(TIPO_USU);
			Object codCargo = separaMip06Mip04.get(COD_CARGO);
			Object indUnidade = separaMip06Mip04.get(IND_UNIDADE);

			if (mipTemporaria.isEmpty()) {
				mipTemporaria.add(separaMip06Mip04);
				arrayFuncao.add(codFuncao);

			} else {

				Object codSistemaTmp = mipTemporaria.get(0).get(COD_SISTEMA);
				Object codPerfilTmp = mipTemporaria.get(0).get(COD_PERFIL);
				Object tipoUnidadeTmp = mipTemporaria.get(0).get(TIPO_UNIDADE);
				Object funcaoTmp = mipTemporaria.get(0).get(FUNCAO);
				Object codUnidadeTmp = mipTemporaria.get(0).get(COD_UNIDADE);
				Object tipoUsuTmp = mipTemporaria.get(0).get(TIPO_USU);
				Object codCargoTmp = mipTemporaria.get(0).get(COD_CARGO);
				Object indUnidadeTmp = mipTemporaria.get(0).get(IND_UNIDADE);

				if (codSistemaTmp.equals(codSistema)
						&& codPerfilTmp.equals(codPerfil)
						&& tipoUnidadeTmp.equals(tipoUnidade)
						&& funcaoTmp.equals(funcao)
						&& codUnidadeTmp.equals(codUnidade)
						&& tipoUsuTmp.equals(tipoUsu)
						&& codCargoTmp.equals(codCargo)
						&& indUnidadeTmp.equals(indUnidade)) {
					arrayFuncao.add(codFuncao);

				} else {

					mipFinal = new HashMap<String, Object>();

					mipFinal.put(COD_SISTEMA, codSistemaTmp);
					mipFinal.put(COD_PERFIL, codPerfilTmp);
					mipFinal.put(TIPO_UNIDADE, tipoUnidadeTmp);
					mipFinal.put(FUNCAO, funcaoTmp);
					mipFinal.put(COD_UNIDADE, codUnidadeTmp);
					mipFinal.put(TIPO_USU, tipoUsuTmp);
					mipFinal.put(COD_CARGO, codCargoTmp);
					mipFinal.put(IND_UNIDADE, indUnidadeTmp);
					mipFinal.put(LIST_FUNCAO, arrayFuncao);

					arrayMipCodFuncao.add(mipFinal);

					mipTemporaria.remove(0);

					mipTemporaria.add(separaMip06Mip04);

					arrayFuncao = new ArrayList<Object>();
					arrayFuncao.add(codFuncao);
				}

				if (index == contador) {
					mipFinal = new HashMap<String, Object>();

					mipFinal.put(COD_SISTEMA, codSistema);
					mipFinal.put(COD_PERFIL, codPerfil);
					mipFinal.put(TIPO_UNIDADE, tipoUnidade);
					mipFinal.put(FUNCAO, funcao);
					mipFinal.put(COD_UNIDADE, codUnidade);
					mipFinal.put(TIPO_USU, tipoUsu);
					mipFinal.put(COD_CARGO, codCargo);
					mipFinal.put(IND_UNIDADE, indUnidade);
					mipFinal.put(LIST_FUNCAO, arrayFuncao);

					arrayMipCodFuncao.add(mipFinal);
				}
			}
		}
	}

	public void juntaCodPerfil() {

		ArrayList<Map<String, Object>> mipTemporaria = new ArrayList<Map<String, Object>>();
		ArrayList<Map<String, Object>> arrayMipPerfil = new ArrayList<Map<String, Object>>();

		Map<String, Object> mipPerfilTemp = new HashMap<String, Object>();
		arrayMipSistemaPerfil = new HashMap<String, Object>();
		arrayMipFinal = new ArrayList<Map<String, Object>>();

		int index = arrayMipCodFuncao.size() - 1;
		int contador = 0;
		int i = 0;

		for (Map<String, Object> separaMIP : arrayMipCodFuncao) {

			contador = i++;

			Object codSistema = separaMIP.get(COD_SISTEMA);
			Object codPerfil = separaMIP.get(COD_PERFIL);

			if (mipTemporaria.isEmpty()) {
				mipPerfilTemp = new HashMap<String, Object>();
				mipPerfilTemp.putAll(separaMIP);
				mipPerfilTemp.remove(COD_SISTEMA);
				mipPerfilTemp.remove(COD_PERFIL);

				mipTemporaria.add(separaMIP);
				arrayMipPerfil.add(mipPerfilTemp);

			} else {

				Object codSistemaTmp = mipTemporaria.get(0).get(COD_SISTEMA);
				Object codPerfilTmp = mipTemporaria.get(0).get(COD_PERFIL);

				if (codPerfilTmp.equals(codPerfil)) {
					mipPerfilTemp = new HashMap<String, Object>();
					mipPerfilTemp.putAll(separaMIP);
					mipPerfilTemp.remove(COD_SISTEMA);
					mipPerfilTemp.remove(COD_PERFIL);

					arrayMipPerfil.add(mipPerfilTemp);

				} else {
					arrayMipSistemaPerfil = new HashMap<String, Object>();

					juntarMips(codSistemaTmp, codPerfilTmp);

					arrayMipSistemaPerfil.put(COD_SISTEMA, codSistemaTmp);
					arrayMipSistemaPerfil.put(COD_PERFIL, codPerfilTmp);
					arrayMipSistemaPerfil.put(UG_NAME, ugNameGlobal);
					arrayMipSistemaPerfil.put(STATUS, statusGlobal);
					arrayMipSistemaPerfil.put(RSS_TYPE, rssTypeGlobal);
					arrayMipSistemaPerfil.put(PERMITE_DUP, permiteDupGlobal);
					arrayMipSistemaPerfil.put(LIST_PERFIL, arrayMipPerfil);

					arrayMipFinal.add(arrayMipSistemaPerfil);

					mipTemporaria.remove(0);

					mipPerfilTemp = new HashMap<String, Object>();

					mipPerfilTemp.putAll(separaMIP);
					mipPerfilTemp.remove(COD_SISTEMA);
					mipPerfilTemp.remove(COD_PERFIL);

					mipTemporaria.add(separaMIP);

					arrayMipPerfil = new ArrayList<Map<String, Object>>();
					arrayMipPerfil.add(mipPerfilTemp);
				}

				if (index == contador) {
					arrayMipSistemaPerfil = new HashMap<String, Object>();

					juntarMips(codSistema, codPerfil);

					arrayMipSistemaPerfil.put(COD_SISTEMA, codSistema);
					arrayMipSistemaPerfil.put(COD_PERFIL, codPerfil);
					arrayMipSistemaPerfil.put(UG_NAME, ugNameGlobal);
					arrayMipSistemaPerfil.put(STATUS, statusGlobal);
					arrayMipSistemaPerfil.put(RSS_TYPE, rssTypeGlobal);
					arrayMipSistemaPerfil.put(PERMITE_DUP, permiteDupGlobal);
					arrayMipSistemaPerfil.put(LIST_PERFIL, arrayMipPerfil);

					arrayMipFinal.add(arrayMipSistemaPerfil);
				}
			}
		}

	}

	public void init(String NomeArquivoMIP04, String NomeArquivoMIP06) throws FileNotFoundException,
			ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException {

//		nomeMip();
			
		lerMIP04(NomeArquivoMIP04);

		lerMIP06(NomeArquivoMIP06);
		
		separaCodFuncao();

		juntaCodPerfil();
	}

	// public static void main(String[] args) throws FileNotFoundException,
	// ParserConfigurationException, TransformerFactoryConfigurationError,
	// TransformerException {
	//
	// lerMIP04();
	//
	// // for( Map<String, Object> testeArrayMap: arrayMapMip04)
	// // {
	// //
	// // System.out.println(testeArrayMap.get(COD_PERFIL));
	// // System.out.println(testeArrayMap.get(COD_SISTEMA));
	// // System.out.println(testeArrayMap.get(UG_NAME));
	// // System.out.println(testeArrayMap.get(RSS_TYPE));
	// //
	// // }
	//
	// lerMIP06();
	//
	// // juntarMips();
	//
	// separaCodFuncao();
	//
	// juntaCodPerfil();
	//
	// // System.out.println(arrayMipFinal.get(0).get(LIST_FUNCAO));
	//
	// }

}
