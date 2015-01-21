package br.com.pbti.leitor;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import br.com.pbti.dto.Inheritance;
import br.com.pbti.xml.MontarXml;

public class ExemploXls {

//	private static String coSistema;
//	private static String stringb2;
//	private static String stringc3;
//	private static String stringd4;
//	private static String stringe5;
//	private static String stringf6;
//	private static String stringg7;
//	private static String stringh8;
//	private static String stringi9;
//	private static int i = 0;

//	public static void main(String[] args)
//
//	throws BiffException, IOException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException
//
//	{
//
//		/**
//		 * 
//		 * Carrega a planilha
//		 */
//
//		Workbook workbook = Workbook
//				.getWorkbook(new File(
//						"C://Users//tic//Desktop//ler xsl//MIP_06_teste.xls"));
//
//		/**
//		 * 
//		 * Aqui √© feito o controle de qual aba do xls
//		 * 
//		 * ser√° realiza a leitura dos dados
//		 */
//
//		Sheet sheet = workbook.getSheet(0);
//
//		/**
//		 * 
//		 * Numero de linhas com dados do xls
//		 */
//
//		for (int i = 1; i < 2; i++) {
//
//		
//		/* pega os valores das cÈlulas como se numa matriz */
//		Cell coSistemaCell = sheet.getCell(0, i);
//		Cell b2 = sheet.getCell(1, i);
//		Cell c2 = sheet.getCell(2, i);
//		Cell d2 = sheet.getCell(3, i);
//		Cell e2 = sheet.getCell(4, i);
//		Cell f2 = sheet.getCell(5, i);
//		Cell g2 = sheet.getCell(6, i);
//		Cell h2 = sheet.getCell(7, i);
//		Cell i2 = sheet.getCell(8, i);
//
//		/* pega os conte˙dos das cÈlulas */
//
//		coSistema = coSistemaCell.getContents();
//		stringb2 = b2.getContents();
//		stringc3 = c2.getContents();
//		stringd4 = d2.getContents();
//		stringe5 = e2.getContents();
//		stringf6 = f2.getContents();
//		stringg7 = g2.getContents();
//		stringh8 = h2.getContents();
//		stringi9 = i2.getContents();
//		}
//		// imprimindo os valores
//		System.out.println(coSistema);
//		System.out.println(stringb2);
//		System.out.println(stringc3);
//		System.out.println(stringd4);
//		System.out.println(stringe5);
//		System.out.println(stringf6);
//		System.out.println(stringg7);
//		System.out.println(stringh8);
//		System.out.println(stringi9);
//
//		// int linhas = sheet.getRows();
//		
//		workbook.close(); 
//		
//		Inheritance inheritance = new Inheritance();
//		
//		inheritance.setNomeSistema(coSistema);
//		
//		MontarXml montarXml = new MontarXml();
//		
//		montarXml.cabecalho();
		
		

//	}
}
