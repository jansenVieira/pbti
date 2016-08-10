package br.com.pbti.perfil.dto;

import org.w3c.dom.Element;

import br.com.pbti.perfil.principal.XmlConfigPerfil;
import br.com.pbti.perfil.xml.MontarXmlPerfil;

public class BundlePerfil {

	//
	private Element bundle;
	private Element montarBundle;

	public MontarXmlPerfil montarXml = new MontarXmlPerfil();
	public AttributesPerfil attributes = new AttributesPerfil();
	public XmlConfigPerfil xmlConfig = new XmlConfigPerfil();
	
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
