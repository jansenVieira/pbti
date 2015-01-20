package br.com.pbti.dto;

import org.w3c.dom.Element;

import br.com.pbti.xml.MontarXml;

public class Profiles {
	
	//Objetos
	private Element profiles;
	private Element montarProfiles;
	private Element profile;
	private Element applicationRef;
	private Element reference;
	private Element constraints;
	private Element filter;
	private Element value;
	private Element string;
	private Element list;
	//Variaveis dinamicas
	private String applicaton = "LDAPSUN";
	private String parametro = "GESTOR";
	private String parametro2 = "SIABM";
	private String parametro3 = "Groups";
	private String parametro4 = "caixa";
	private String addDireito = "groups";
	//Estaciando metodo
	public MontarXml montarXml = new MontarXml();

	@SuppressWarnings("static-access")
	public void montarProfiles()
	{
		montarProfiles = montarXml.getDoc().createElement("Profiles");
		setProfiles(montarProfiles);
		profile();
	}

	@SuppressWarnings("static-access")
	private void profile()
	{
		profile = montarXml.getDoc().createElement("Profile");
		profiles.appendChild(profile);
		
		applicationRef = montarXml.getDoc().createElement("ApplicationRef");
		profile.appendChild(applicationRef);
		
		reference = montarXml.getDoc().createElement("Reference");
		reference.setAttribute("class", "sailpoint.object.Application");
		reference.setAttribute("name", getApplicaton());
		applicationRef.appendChild(reference);
		
		constraints = montarXml.getDoc().createElement("Constraints");
		profile.appendChild(constraints);
		
		filter = montarXml.getDoc().createElement("Filter");
		filter.setAttribute("operation", "CONTAINS_ALL");
		filter.setAttribute("property", getAddDireito());
		constraints.appendChild(filter);
		
		value = montarXml.getDoc().createElement("Value");
		filter.appendChild(value);
		list =  montarXml.getDoc().createElement("List");
		value.appendChild(list);
		string = montarXml.getDoc().createElement("String");
		string.appendChild(montarXml.getDoc().createTextNode("cn="+parametro+","+"cn="+parametro2+","+"ou="+parametro3+","+"o="+parametro4));
		list.appendChild(string);
		
	}

	
	public String getApplicaton() {
		return applicaton;
	}

	public void setApplicaton(String applicaton) {
		this.applicaton = applicaton;
	}

	public String getParametro() {
		return parametro;
	}

	public void setParametro(String parametro) {
		this.parametro = parametro;
	}

	public Element getProfiles() {
		return profiles;
	}

	public void setProfiles(Element profiles) {
		this.profiles = profiles;
	}

	public String getAddDireito() {
		return addDireito;
	}

	public void setAddDireito(String addDireito) {
		this.addDireito = addDireito;
	}

	
}
