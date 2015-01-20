package br.com.pbti.dto;

import org.w3c.dom.Element;
import br.com.pbti.xml.MontarXml;

public class Inheritance {

	//
	private Element inheritance;
	private Element montarInheritance;
	private Element refence;
	//
	private String perfilHerdado= "SIABM";


	public MontarXml montarXml = new MontarXml();

	public void montarInheritance()
	{
		montarInheritance = montarXml.getDoc().createElement("Inheritance");
		setInheritance(montarInheritance);
		refence();
	}

	private void refence()
	{
		refence = montarXml.getDoc().createElement("Reference");
		refence.setAttribute("class", "sailpoint.object.Bundle");
		refence.setAttribute("name", getPerfilHerdado());
		inheritance.appendChild(refence);
		
	}

	
	
	public String getPerfilHerdado() {
		return perfilHerdado;
	}

	public void setPerfilHerdado(String perfilHerdado) {
		this.perfilHerdado = perfilHerdado;
	}

	public Element getInheritance() {
		return inheritance;
	}

	public void setInheritance(Element inheritance) {
		this.inheritance = inheritance;
	}
	
}
