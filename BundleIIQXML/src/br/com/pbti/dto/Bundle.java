package br.com.pbti.dto;

import org.w3c.dom.Element;

import br.com.pbti.principal.XmlConfig;
import br.com.pbti.xml.MontarXml;

public class Bundle {

	//
	private Element bundle;
	private Element montarBundle;

	public MontarXml montarXml = new MontarXml();
	public Attributes attributes = new Attributes();
	public XmlConfig xmlConfig = new XmlConfig();
	
	// variaveis dinamicas
	private String nomeBundle;
	private String type = "PERFIL";
	

	@SuppressWarnings("static-access")
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNomeBundle() {
		return nomeBundle;
	}

	public void setNomeBundle(String nomeBundle) {
		this.nomeBundle = nomeBundle;
	}

}
