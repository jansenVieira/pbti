package br.com.pbti.perfil.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.w3c.dom.Element;

import br.com.pbti.perfil.principal.XmlConfigPerfil;
import br.com.pbti.perfil.xml.MontarXmlPerfil;

public class ProfilesPerfil {

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
	private String sistemaPerfilSinav;
	private boolean sinavPreechido = false;
	
	// Estaciando metodo
	public MontarXmlPerfil montarXml = new MontarXmlPerfil();

	@SuppressWarnings("static-access")
	public void montarProfiles() {
		montarProfiles = montarXml.getDoc().createElement("Profiles");
		setProfiles(montarProfiles);
		profile();
	}

	@SuppressWarnings({ "static-access", "unchecked" })
	private void profile() {

		ArrayList<Map<String, Object>> listaRSSName = new ArrayList<Map<String, Object>>();

		listaRSSName.addAll(XmlConfigPerfil.getListaRssName());

		@SuppressWarnings("unused")
		String rss_name = "rss_name", listUgName = "listUgName", rss_type = "rss_type", nomeAtributoAplicativo = "nome_atributo_aplicativo";
		
		sinavPreechido = false;

		for (Map<String, Object> dados : listaRSSName) {

			ArrayList<Map<String, Object>> listaUgName = new ArrayList<Map<String, Object>>();

			listaUgName
					.addAll((Collection<? extends Map<String, Object>>) dados
							.get(listUgName));

			profile = montarXml.getDoc().createElement("Profile");
			profiles.appendChild(profile);

			applicationRef = montarXml.getDoc().createElement("ApplicationRef");
			profile.appendChild(applicationRef);

			reference = montarXml.getDoc().createElement("Reference");
			reference.setAttribute("class", "sailpoint.object.Application");
			reference.setAttribute("name", dados.get(rss_name).toString());
			applicationRef.appendChild(reference);

			constraints = montarXml.getDoc().createElement("Constraints");
			profile.appendChild(constraints);

			filter = montarXml.getDoc().createElement("Filter");
			filter.setAttribute("operation", "CONTAINS_ALL");
			filter.setAttribute("property", dados.get(nomeAtributoAplicativo)
					.toString());
			constraints.appendChild(filter);

			value = montarXml.getDoc().createElement("Value");
			filter.appendChild(value);
			list = montarXml.getDoc().createElement("List");
			value.appendChild(list);

			for (Object dadosUgNaem : listaUgName) {
				string = montarXml.getDoc().createElement("String");
				string.appendChild(montarXml.getDoc().createTextNode(
						dadosUgNaem.toString()));
				list.appendChild(string);
			}

			if (dados.get("sis_sinav").equals("S")) {

				if(sinavPreechido == false)
				{
					sisSinav();
				}
			}

		}
	}

	@SuppressWarnings({ "static-access", "unchecked" })
	private void sisSinav() {

		ArrayList<Map<String, Object>> listaRSSName = new ArrayList<Map<String, Object>>();

		listaRSSName.addAll(XmlConfigPerfil.getListaRssName());

		@SuppressWarnings("unused")
		String rss_name = "rss_name", listUgName = "listUgName", rss_type = "rss_type", nomeAtributoAplicativo = "nome_atributo_aplicativo";

		for (Map<String, Object> dados : listaRSSName) {

			ArrayList<Map<String, Object>> listaUgName = new ArrayList<Map<String, Object>>();

			if(sinavPreechido == true)
			{
				break;
			}
			
			listaUgName
					.addAll((Collection<? extends Map<String, Object>>) dados
							.get(listUgName));

			profile = montarXml.getDoc().createElement("Profile");
			profiles.appendChild(profile);

			applicationRef = montarXml.getDoc().createElement("ApplicationRef");
			profile.appendChild(applicationRef);

			reference = montarXml.getDoc().createElement("Reference");
			reference.setAttribute("class", "sailpoint.object.Application");
			reference.setAttribute("name", "BSA-SINAV-PRD");
			applicationRef.appendChild(reference);

			constraints = montarXml.getDoc().createElement("Constraints");
			profile.appendChild(constraints);

			filter = montarXml.getDoc().createElement("Filter");
			filter.setAttribute("operation", "CONTAINS_ALL");
			filter.setAttribute("property", "SIS_PERFIL");
			constraints.appendChild(filter);

			value = montarXml.getDoc().createElement("Value");
			filter.appendChild(value);
			list = montarXml.getDoc().createElement("List");
			value.appendChild(list);

			string = montarXml.getDoc().createElement("String");
			string.appendChild(montarXml.getDoc().createTextNode(
					getSistemaPerfilSinav()));
			list.appendChild(string);
			
			sinavPreechido = true;
			

		}
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

	public String getSistemaPerfilSinav() {
		return sistemaPerfilSinav;
	}

	public void setSistemaPerfilSinav(String sistemaPerfilSinav) {
		this.sistemaPerfilSinav = sistemaPerfilSinav;
	}

}
