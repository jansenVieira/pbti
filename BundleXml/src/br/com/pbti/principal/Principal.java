package br.com.pbti.principal;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;

public class Principal {

	public static void main(String[] args) throws ParserConfigurationException,
			TransformerFactoryConfigurationError, TransformerException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		DocumentBuilder builder = factory.newDocumentBuilder();

		Document doc = builder.newDocument();

		// Cria o elemento Root pessoa

		Element compoundFilter = doc.createElement("CompoundFilter");
		
		Element compositeFilter = doc.createElement("CompositeFilter");
		compositeFilter.setAttribute("operation", "AND");
		
		compoundFilter.appendChild(compositeFilter);
		
		Element filterUnidade = doc.createElement("Filter");
		filterUnidade.setAttribute("operation", "EQ");
		filterUnidade.setAttribute("property", "sg_unde_ltco");
		filterUnidade.setAttribute("value", "GESET");
		
		compositeFilter.appendChild(filterUnidade);
		
		Element filterUsuario = doc.createElement("Filter");
		filterUsuario.setAttribute("operation", "EQ");
		filterUsuario.setAttribute("property", "tipo_usuario");
		filterUsuario.setAttribute("value", "C");
		
		compositeFilter.appendChild(filterUsuario);
		
		
		Element filterFuncao = doc.createElement("Filter");
		filterFuncao.setAttribute("operation", "IN");
		filterFuncao.setAttribute("property", "funcao");
			
			Element value = doc.createElement("Value");
				
			filterFuncao.appendChild(value);
		
				Element list = doc.createElement("List");
		
				value.appendChild(list);
		
					Element stringElemnt = doc.createElement("String");
					
					stringElemnt.appendChild(doc.createTextNode("278"));
							
					list.appendChild(stringElemnt);
		
					Element stringElent2 = doc.createElement("String");
					stringElent2.appendChild(doc.createTextNode("279"));
					
					list.appendChild(stringElent2);
		
					Element stringElent3 = doc.createElement("String");
					stringElent3.appendChild(doc.createTextNode("280"));
					
					list.appendChild(stringElent3);
		
		compositeFilter.appendChild(filterFuncao);
		
		
		
		doc.appendChild(compoundFilter);

		Transformer trans = TransformerFactory.newInstance().newTransformer();

		trans.setOutputProperty(OutputKeys.STANDALONE, "yes");

		trans.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

		trans.setOutputProperty(OutputKeys.INDENT, "yes");

		trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "minhadtd.dtd");

		trans.transform(new javax.xml.transform.dom.DOMSource(doc),
				new StreamResult(System.out));
	}
}
