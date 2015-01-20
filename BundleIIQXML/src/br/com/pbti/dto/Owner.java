package br.com.pbti.dto;

import org.w3c.dom.Element;

import br.com.pbti.xml.MontarXml;

public class Owner {
	
	//
	private Element owner;
	private Element montarOwner;
	private Element refence;
	//
	private String nomeOwner = "spadmin";


	public MontarXml montarXml = new MontarXml();

	public void montarOwner()
	{
		montarOwner = montarXml.getDoc().createElement("Owner");
		setOwner(montarOwner);
		refence();
	}

	private void refence()
	{
		refence = montarXml.getDoc().createElement("Reference");
		refence.setAttribute("class", "sailpoint.object.Identity");
		refence.setAttribute("name", getNomeOwner());
		owner.appendChild(refence);
		
	}

	
	
	public String getNomeOwner() {
		return nomeOwner;
	}

	public void setNomeOwner(String nomeOwner) {
		this.nomeOwner = nomeOwner;
	}

	public Element getOwner() {
		return owner;
	}

	public void setOwner(Element owner) {
		this.owner = owner;
	}
}
