package br.com.pbti.perfil.dto;

import org.w3c.dom.Element;

import br.com.pbti.perfil.xml.MontarXmlPerfil;

public class InheritancePerfil {

	//
	private Element inheritance;
	private Element montarInheritance;
	private Element refence;
	//
	private String inheritanceName;


	public MontarXmlPerfil montarXml = new MontarXmlPerfil();

	@SuppressWarnings("static-access")
	public void montarInheritance()
	{
		montarInheritance = montarXml.getDoc().createElement("Inheritance");
		setInheritance(montarInheritance);
		refence();
	}

	@SuppressWarnings("static-access")
	private void refence()
	{
		refence = montarXml.getDoc().createElement("Reference");
		refence.setAttribute("class", "sailpoint.object.Bundle");
		refence.setAttribute("name", getInheritanceName());
		inheritance.appendChild(refence);
		
	}

	public String getInheritanceName() {
		return inheritanceName;
	}

	public void setInheritanceName(String inheritanceName) {
		this.inheritanceName = inheritanceName;
	}

	public Element getInheritance() {
		return inheritance;
	}

	public void setInheritance(Element inheritance) {
		this.inheritance = inheritance;
	}
	
}
