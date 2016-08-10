package br.com.pbti.sistema.dto;

import org.w3c.dom.Element;

import br.com.pbti.sistema.xml.MontarXml;

public class Attributes {

	// Objetos
	private Element attributes;
	private Element montarAttributes;
	private Element map;
	private Element entry;
	private Element value;
	private Element bolean;
	private Element mapInt;
	private Element entryInt;
	private String duplicidade;
	private String eventual;

	public MontarXml montarXml = new MontarXml();

	@SuppressWarnings("static-access")
	public void montarAttributes() {
		montarAttributes = montarXml.getDoc().createElement("Attributes");
		setAttributes(montarAttributes);
		map();

	}

	@SuppressWarnings("static-access")
	public void map() {
		map = montarXml.getDoc().createElement("Map");
		attributes.appendChild(map);
		entry();
	}

	@SuppressWarnings("static-access")
	private void entry() {
		
		entry = montarXml.getDoc().createElement("entry");
		entry.setAttribute("key", "accountSelectorRules");
		map.appendChild(entry);

		entry = montarXml.getDoc().createElement("entry");
		entry.setAttribute("key", "allowDuplicateAccounts");
		entry.setAttribute("value", "false");
		map.appendChild(entry);

		entry = montarXml.getDoc().createElement("entry");
		entry.setAttribute("key", "allowMultipleAssignments");
		entry.setAttribute("value", "false");
		map.appendChild(entry);

//Preencher esse campo getDuplicidade		
		
		entry = montarXml.getDoc().createElement("entry");
		entry.setAttribute("key", "considerar_eventual");
		value = montarXml.getDoc().createElement("value");
		entry.appendChild(value);
		bolean = montarXml.getDoc().createElement("Boolean");
		value.appendChild(bolean);
		bolean.appendChild(montarXml.getDoc().createTextNode(getEventual()));
		map.appendChild(entry);
		
		entry = montarXml.getDoc().createElement("entry");
		entry.setAttribute("key", "duplicidade");
		value = montarXml.getDoc().createElement("value");
		entry.appendChild(value);
		bolean = montarXml.getDoc().createElement("Boolean");
		value.appendChild(bolean);
		bolean.appendChild(montarXml.getDoc().createTextNode(getDuplicidade()));
		map.appendChild(entry);

		entry = montarXml.getDoc().createElement("entry");
		entry.setAttribute("key", "mergeTemplates");
		entry.setAttribute("value", "false");
		map.appendChild(entry);

		entry = montarXml.getDoc().createElement("entry");
		entry.setAttribute("key", "sysDescriptions");
		value = montarXml.getDoc().createElement("value");
		entry.appendChild(value);
		mapInt = montarXml.getDoc().createElement("Map");
		value.appendChild(mapInt);
		entryInt = montarXml.getDoc().createElement("entry");
		mapInt.appendChild(entryInt);
		entryInt.setAttribute("key", "en_US");
		map.appendChild(entry);

	}

	public Element getAttributes() {
		return attributes;
	}

	public void setAttributes(Element attributes) {
		this.attributes = attributes;
	}

	public String getDuplicidade() {
		return duplicidade;
	}

	public void setDuplicidade(String duplicidade) {
		this.duplicidade = duplicidade;
	}

	public String getEventual() {
		return eventual;
	}

	public void setEventual(String eventual) {
		this.eventual = eventual;
	}
}
