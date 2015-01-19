package br.com.pbti.dto;

import org.w3c.dom.Element;

import br.com.pbti.xml.MontarXml;

public class Profiles {
	
	//
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
	//
	String applicaton = "LDAPSUN";
	String parametro = "cn=GESTOR,cn=SIABM,ou=Groups,o=caixa";
	


	public MontarXml montarXml = new MontarXml();

	public void montarProfiles()
	{
		montarProfiles = montarXml.getDoc().createElement("Profiles");
		setProfiles(montarProfiles);
		profile();
	}

	private void profile()
	{
		profile = montarXml.getDoc().createElement("Profile");
		profiles.appendChild(profile);
		
		applicationRef = montarXml.getDoc().createElement("ApplicationRef");
		profile.appendChild(applicationRef);
		
		reference = montarXml.getDoc().createElement("Reference");
		reference.setAttribute("class", "sailpoint.object.Application");
		reference.setAttribute("name", applicaton);
		applicationRef.appendChild(reference);
		
		constraints = montarXml.getDoc().createElement("Constraints");
		profile.appendChild(constraints);
		
		filter = montarXml.getDoc().createElement("Filter");
		filter.setAttribute("operation", "CONTAINS_ALL");
		filter.setAttribute("property", "groups");
		constraints.appendChild(filter);
		
		value = montarXml.getDoc().createElement("Value");
		filter.appendChild(value);
		list =  montarXml.getDoc().createElement("List");
		value.appendChild(list);
		string = montarXml.getDoc().createElement("String");
		string.appendChild(montarXml.getDoc().createTextNode(parametro));
		list.appendChild(string);
		
	}

	public Element getProfiles() {
		return profiles;
	}

	public void setProfiles(Element profiles) {
		this.profiles = profiles;
	}

	
}
