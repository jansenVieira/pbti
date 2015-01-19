package br.com.pbti.dto;

import org.w3c.dom.Element;
import br.com.pbti.xml.MontarXml;

public class Bundle {

	//
	private Element bundle;
	private Element montarBundle;
	
	public MontarXml montarXml = new MontarXml();
	public Attributes attributes = new Attributes();

	public void montarBundle()
	{
		montarBundle= montarXml.getDoc().createElement("Bundle");
		montarBundle.setAttribute("name", "SIABM_TESTE3");
		montarBundle.setAttribute("type", "PERFIL");
		setBundle(montarBundle);
	}
	//

	public Element getBundle() {
		return bundle;
	}

	public void setBundle(Element bundle) {
		this.bundle = bundle;
	}
	
}

