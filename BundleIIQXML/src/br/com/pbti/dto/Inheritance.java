package br.com.pbti.dto;

import org.w3c.dom.Element;

import br.com.pbti.xml.MontarXml;

public class Inheritance {

	//
	private Element inheritance;
	private Element montarInheritance;
	private Element refence;
	//
	private String nomeSistema;


	public MontarXml montarXml = new MontarXml();

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
		refence.setAttribute("name", getNomeSistema());
		inheritance.appendChild(refence);
		
	}

	public String getNomeSistema() {
		return nomeSistema;
	}

	public void setNomeSistema(String nomeSistema) {
		this.nomeSistema = nomeSistema;
	}

	public Element getInheritance() {
		return inheritance;
	}

	public void setInheritance(Element inheritance) {
		this.inheritance = inheritance;
	}
	
}
