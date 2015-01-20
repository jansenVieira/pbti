package br.com.pbti.dto;

import org.w3c.dom.Element;

import br.com.pbti.xml.MontarXml;

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

	public MontarXml montarXml = new MontarXml();

	public void montarAttributes() {
		montarAttributes = montarXml.getDoc().createElement("Attributes");
		setAttributes(montarAttributes);
		map();

	}

	public void map() {
		map = montarXml.getDoc().createElement("Map");
		attributes.appendChild(map);
		entry();
	}

	private void entry() {
		entry = montarXml.getDoc().createElement("entry");
		entry.setAttribute("key", "Prioridade");
		entry.setAttribute("value", "0");
		map.appendChild(entry);

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

		entry = montarXml.getDoc().createElement("entry");
		entry.setAttribute("key", "aprovacao_gestor");
		value = montarXml.getDoc().createElement("value");
		entry.appendChild(value);
		bolean = montarXml.getDoc().createElement("Boolean");
		value.appendChild(bolean);
		bolean.appendChild(montarXml.getDoc().createTextNode(""));
		map.appendChild(entry);

		entry = montarXml.getDoc().createElement("entry");
		entry.setAttribute("key", "mergeTemplates");
		entry.setAttribute("value", "false");
		map.appendChild(entry);

		entry = montarXml.getDoc().createElement("entry");
		entry.setAttribute("key", "perfilAutomatico");
		value = montarXml.getDoc().createElement("value");
		entry.appendChild(value);
		bolean = montarXml.getDoc().createElement("Boolean");
		value.appendChild(bolean);
		bolean.appendChild(montarXml.getDoc().createTextNode(""));
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

		entry = montarXml.getDoc().createElement("entry");
		entry.setAttribute("key", "unidade_gestora");
		map.appendChild(entry);
	}

	public Element getAttributes() {
		return attributes;
	}

	public void setAttributes(Element attributes) {
		this.attributes = attributes;
	}
}
