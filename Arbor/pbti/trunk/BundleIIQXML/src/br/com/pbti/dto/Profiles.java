package br.com.pbti.dto;

import org.w3c.dom.Element;

import br.com.pbti.xml.MontarXml;

public class Profiles {

	// Objetos
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
	// Variaveis dinamicas
	private String applicatonRefName;
	private String constraintsListString;
	private String constraintsFilterProperty = "groups";
	// Estaciando metodo
	public MontarXml montarXml = new MontarXml();

	@SuppressWarnings("static-access")
	public void montarProfiles() {
		montarProfiles = montarXml.getDoc().createElement("Profiles");
		setProfiles(montarProfiles);
		profile();
	}

	@SuppressWarnings("static-access")
	private void profile() {
		profile = montarXml.getDoc().createElement("Profile");
		profiles.appendChild(profile);

		applicationRef = montarXml.getDoc().createElement("ApplicationRef");
		profile.appendChild(applicationRef);

		reference = montarXml.getDoc().createElement("Reference");
		reference.setAttribute("class", "sailpoint.object.Application");
		reference.setAttribute("name", getApplicatonRefName());
		applicationRef.appendChild(reference);

		constraints = montarXml.getDoc().createElement("Constraints");
		profile.appendChild(constraints);

		filter = montarXml.getDoc().createElement("Filter");
		filter.setAttribute("operation", "CONTAINS_ALL");
		filter.setAttribute("property", getConstraintsFilterProperty());
		constraints.appendChild(filter);

		value = montarXml.getDoc().createElement("Value");
		filter.appendChild(value);
		list = montarXml.getDoc().createElement("List");
		value.appendChild(list);
		string = montarXml.getDoc().createElement("String");
		string.appendChild(montarXml.getDoc().createTextNode(
				getConstraintsListString()));
		list.appendChild(string);

	}

	public String getApplicatonRefName() {
		return applicatonRefName;
	}

	public void setApplicatonRefName(String applicatonRefName) {
		this.applicatonRefName = applicatonRefName;
	}

	public String getConstraintsListString() {
		return constraintsListString;
	}

	public void setConstraintsListString(String constraintsListString) {
		this.constraintsListString = constraintsListString;
	}

	public Element getProfiles() {
		return profiles;
	}

	public void setProfiles(Element profiles) {
		this.profiles = profiles;
	}

	public String getConstraintsFilterProperty() {
		return constraintsFilterProperty;
	}

	public void setConstraintsFilterProperty(String constraintsFilterProperty) {
		this.constraintsFilterProperty = constraintsFilterProperty;
	}

}
