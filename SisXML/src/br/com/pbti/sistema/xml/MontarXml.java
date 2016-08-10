package br.com.pbti.sistema.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import br.com.pbti.manipulador.properties.LerProperties;
import br.com.pbti.sistema.dto.Attributes;
import br.com.pbti.sistema.dto.Bundle;
import br.com.pbti.sistema.dto.Owner;

public class MontarXml {

	//Variaveis/Constantes
	private static DocumentBuilderFactory factory;
	private static DocumentBuilder builder;
	private static Document doc;
	private static Transformer trans;
	
	//Estaciando as classes 
	public static Bundle bundle = new Bundle();	
	public static Attributes attributes = new Attributes();
	public static Owner owner = new Owner();
	public static String nomeXml;
	private static String nomeSistema;
	
	static LerProperties lerproperties = new LerProperties();
	
	public static void cabecalho() throws ParserConfigurationException,
	TransformerFactoryConfigurationError, TransformerException, FileNotFoundException
	{
		setFactory(DocumentBuilderFactory.newInstance());

		setBuilder(factory.newDocumentBuilder());

		try {
			lerproperties.dadosProperties();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		setDoc(builder.newDocument());		
		
		//chama o metodo que criar o corpo da estrutura
		chamarTags();

		//Append atributos
		getDoc().appendChild(bundle.getBundle());
		bundle.getBundle().appendChild(attributes.getAttributes());
//		bundle.getBundle().appendChild(inheritance.getInheritance());
		bundle.getBundle().appendChild(owner.getOwner());
//		bundle.getBundle().appendChild(selector.getSelector());
//		bundle.getBundle().appendChild(profiles.getProfiles());
		
		//Caminho onde é salvo o arquivo xml
		
		File file = new File(lerproperties.getUrlXml()+"/"+getNomeSistema()+"/");
		file.mkdirs();
		
		StreamResult result = new StreamResult(new FileOutputStream(lerproperties.getUrlXml()+"/"+getNomeSistema()+"/"+getNomeSistema()+".xml"));
		
						
				
		//Cabecalho
		trans = TransformerFactory.newInstance().newTransformer();
		
		//retirar o standalone
		doc.setXmlStandalone(true);
		
		
		trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		
		//primeira linha crianda com o "yes"
		trans.setOutputProperty(OutputKeys.INDENT, "yes");
		
		trans.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "sailpoint.dtd");

		trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "sailpoint.dtd");

		trans.transform(new javax.xml.transform.dom.DOMSource(doc),
				result);
		
		
		
	}
	
	
	//Metodo que chama todos os metodos
	public static void chamarTags()
	{
		bundle.montarBundle();
		attributes.montarAttributes();
//		inheritance.montarInheritance();
		owner.montarOwner();
//		selector.montarSelector();
//		profiles.montarProfiles();
	}
	
	public Transformer getTrans() {
		return trans;
	}


	@SuppressWarnings("static-access")
	public void setTrans(Transformer trans) {
		this.trans = trans;
	}
	
	public DocumentBuilderFactory getFactory() {
		return factory;
	}

	public static DocumentBuilder getBuilder() {
		return builder;
	}

	public static void setBuilder(DocumentBuilder builder) {
		MontarXml.builder = builder;
	}

	public static Document getDoc() {
		return doc;
	}

	public static void setDoc(Document doc) {
		MontarXml.doc = doc;
	}

	public static Bundle getBundle() {
		return bundle;
	}

	public static void setBundle(Bundle bundle) {
		MontarXml.bundle = bundle;
	}

	public static void setFactory(DocumentBuilderFactory factory) {
		MontarXml.factory = factory;
	}


	public static String getNomeXml() {
		return nomeXml;
	}

	public static void setNomeXml(String nomeXml) {
		MontarXml.nomeXml = nomeXml;
	}


	public static String getNomeSistema() {
		return nomeSistema;
	}


	public static void setNomeSistema(String nomeSistema) {
		MontarXml.nomeSistema = nomeSistema;
	}
	
	
	
}