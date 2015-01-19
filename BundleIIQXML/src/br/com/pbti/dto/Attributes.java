package br.com.pbti.dto;

import org.w3c.dom.Element;

import br.com.pbti.xml.MontaXml;

public class Attributes {

	
	private Element attributes;
	private Element attributes2;
	private Element montaAttributes;

	public MontaXml montaXml = new MontaXml();
	
	public void montaAttributes()
	{
		montaAttributes = montaXml.getDoc().createElement("Attributes");
		montaAttributes.setAttribute("name", "SIABM_TESTE");
		montaAttributes.setAttribute("type", "PERFIL");
		
		setAttributes(montaAttributes);
		
	}

	public Element getAttributes() {
		return attributes;
	}

	public void setAttributes(Element attributes) {
		this.attributes = attributes;
	}

	
	
}
