package br.com.pbti.dto;

import org.w3c.dom.Element;

import br.com.pbti.xml.MontaXml;

public class Bundle {

	private Element bundle;
	private Element montaBundle;

	public MontaXml montaXml = new MontaXml();
	
	public void montaBundle()
	{
		montaBundle = montaXml.getDoc().createElement("Bundle");
		montaBundle.setAttribute("name", "SIABM_TESTE");
		montaBundle.setAttribute("type", "PERFIL");
		
		setBundle(montaBundle);
		
	}

	public Element getBundle() {
		return bundle;
	}

	public void setBundle(Element bundle) {
		this.bundle = bundle;
	}
}
