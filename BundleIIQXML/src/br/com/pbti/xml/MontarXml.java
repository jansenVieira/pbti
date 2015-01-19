package br.com.pbti.xml;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

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

import br.com.pbti.dto.Attributes;
import br.com.pbti.dto.Bundle;
import br.com.pbti.dto.Inheritance;
import br.com.pbti.dto.Owner;
import br.com.pbti.dto.Profiles;
import br.com.pbti.dto.Selector;

public class MontarXml {

	//variaveis/constantes
	private static DocumentBuilderFactory factory;

	private static DocumentBuilder builder;

	private static Document doc;
	
	private static Transformer trans;
	
	//estaciando as classes do pacote tags
	public static Bundle bundle = new Bundle();	
	public static Attributes attributes = new Attributes();
	public static Inheritance inheritance = new Inheritance();
	public static Owner owner = new Owner();
	public static Selector selector = new Selector();
	public static Profiles profiles = new Profiles();
	
	public static void cabecalho() throws ParserConfigurationException,
	TransformerFactoryConfigurationError, TransformerException, FileNotFoundException
	{
		setFactory(DocumentBuilderFactory.newInstance());

		setBuilder(factory.newDocumentBuilder());

		setDoc(builder.newDocument());		
		
		//chama o metodo que criar o corpo da estrutura
		chamarTags();

		//Append atributos
		getDoc().appendChild(bundle.getBundle());
		bundle.getBundle().appendChild(attributes.getAttributes());
		bundle.getBundle().appendChild(inheritance.getInheritance());
		bundle.getBundle().appendChild(owner.getOwner());
		bundle.getBundle().appendChild(selector.getSelector());
		bundle.getBundle().appendChild(profiles.getProfiles());
		
		
		StreamResult result = new StreamResult(new FileOutputStream("D:\\teste\\teste.xml"));
		
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
	
	
	//metodo que chama todos os metodos
	public static void chamarTags()
	{
		bundle.montarBundle();
		attributes.montarAttributes();
		inheritance.montarInheritance();
		owner.montarOwner();
		selector.montarSelector();
		profiles.montarProfiles();
	}
	
	public Transformer getTrans() {
		return trans;
	}


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
	
}