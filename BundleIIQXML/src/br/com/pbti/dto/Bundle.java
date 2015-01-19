package br.com.pbti.dto;

import org.w3c.dom.Element;
import br.com.pbti.xml.MontarXml;

public class Bundle {

	//
	private Element bundle;
	private Element montarBundle;
	
	public MontarXml montarXml = new MontarXml();
	public Attributes attributes = new Attributes();
	
	private String nomeBundle;
	private String type;

	public void montarBundle()
	{
		montarBundle= montarXml.getDoc().createElement("Bundle");
		montarBundle.setAttribute("name", getNomeBundle());
		montarBundle.setAttribute("type", getType());
		setBundle(montarBundle);
	}
	//

	public Element getBundle() {
		return bundle;
	}

	public void setBundle(Element bundle) {
		this.bundle = bundle;
	}
	
	public String getNomeBundle() {
		return nomeBundle;
	}

	public void setNomeBundle(String nomeBundle) {
		this.nomeBundle = nomeBundle;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}

