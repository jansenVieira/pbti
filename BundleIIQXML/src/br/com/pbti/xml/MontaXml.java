package br.com.pbti.xml;

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

public class MontaXml {

	private static DocumentBuilderFactory factory;

	private static DocumentBuilder builder;

	private static Document doc;
	
	private static Transformer trans;
	
	public static Bundle bundle = new Bundle();
	
	public static Attributes attributes = new Attributes();
	
	
	public static void chamaMonta() throws TransformerFactoryConfigurationError, TransformerException, ParserConfigurationException {
		
		setFactory(DocumentBuilderFactory.newInstance());

		setBuilder(factory.newDocumentBuilder());

		setDoc(builder.newDocument());
		
		chamaTags();
		
		getDoc().appendChild(bundle.getBundle());
		
		bundle.getBundle().appendChild(attributes.getAttributes());
		
		
		trans = TransformerFactory.newInstance().newTransformer();

		trans.setOutputProperty(OutputKeys.STANDALONE, "yes");

		trans.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

		trans.setOutputProperty(OutputKeys.INDENT, "yes");

		trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "minhadtd.dtd");

		trans.transform(new javax.xml.transform.dom.DOMSource(doc),
				new StreamResult(System.out));
		
	}
	
	
	public static void chamaTags()
	{
		bundle.montaBundle();
		attributes.montaAttributes();
		
		
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
		MontaXml.builder = builder;
	}

	public static Document getDoc() {
		return doc;
	}

	public static void setDoc(Document doc) {
		MontaXml.doc = doc;
	}

	public static Bundle getBundle() {
		return bundle;
	}

	public static void setBundle(Bundle bundle) {
		MontaXml.bundle = bundle;
	}

	public static void setFactory(DocumentBuilderFactory factory) {
		MontaXml.factory = factory;
	}
}
