package br.com.pbti.perfil.dto;

import org.w3c.dom.Element;

import br.com.pbti.perfil.xml.MontarXmlPerfil;

public class AttributesPerfil {

	// Objetos
	private Element attributes;
	private Element montarAttributes;
	private Element map;
	private Element entry;
	private Element value;
	private Element bolean;
	private Element mapInt;
	private Element entryInt;
	private String status;
	private String atualizacaoNacional;
	private String visaoDiretoria;
	private String consultaNacional;
	private String nivelSeguranca;
	private String funcionalidade;

	public MontarXmlPerfil montarXml = new MontarXmlPerfil();

	@SuppressWarnings("static-access")
	public void montarAttributes() {
		montarAttributes = montarXml.getDoc().createElement("Attributes");
		setAttributes(montarAttributes);
		map();

	}

	@SuppressWarnings("static-access")
	public void map() {
		map = montarXml.getDoc().createElement("Map");
		attributes.appendChild(map);
		entry();
	}

	@SuppressWarnings("static-access")
	private void entry() {

		// ArrayList<Map<String, Object>> listaPerfil = new
		// ArrayList<Map<String, Object>>();
		//
		// listaPerfil.addAll(XmlConfig.getListaCodFuncao());

		entry = montarXml.getDoc().createElement("entry");
		entry.setAttribute("key", "Prioridade");
		entry.setAttribute("value", "0");
		map.appendChild(entry);

		entry = montarXml.getDoc().createElement("entry");
		entry.setAttribute("key", "Nivel_Seguranca");
		entry.setAttribute("value", getNivelSeguranca().toString());
		map.appendChild(entry);

		entry = montarXml.getDoc().createElement("entry");
		entry.setAttribute("key", "accountSelectorRules");
		map.appendChild(entry);

		entry = montarXml.getDoc().createElement("entry");
		entry.setAttribute("key", "allowDuplicateAccounts");
		entry.setAttribute("value", "false");
		map.appendChild(entry);

		entry = montarXml.getDoc().createElement("entry");
		entry.setAttribute("key", "allowMultipleAssignments");
		entry.setAttribute("value", "false");
		map.appendChild(entry);

		entry = montarXml.getDoc().createElement("entry");
		entry.setAttribute("key", "aprovacao_gestor");
		value = montarXml.getDoc().createElement("value");
		entry.appendChild(value);
		bolean = montarXml.getDoc().createElement("Boolean");
		value.appendChild(bolean);
		bolean.appendChild(montarXml.getDoc().createTextNode(""));
		map.appendChild(entry);

		entry = montarXml.getDoc().createElement("entry");
		entry.setAttribute("key", "atualizacao_nacional");
		value = montarXml.getDoc().createElement("value");
		entry.appendChild(value);
		bolean = montarXml.getDoc().createElement("Boolean");
		value.appendChild(bolean);
		bolean.appendChild(montarXml.getDoc().createTextNode(
				getAtualizacaoNacional()));
		map.appendChild(entry);
		
		entry = montarXml.getDoc().createElement("entry");
		entry.setAttribute("key", "visao_diretoria");
		value = montarXml.getDoc().createElement("value");
		entry.appendChild(value);
		bolean = montarXml.getDoc().createElement("Boolean");
		value.appendChild(bolean);
		bolean.appendChild(montarXml.getDoc().createTextNode(
				getVisaoDiretoria()));
		map.appendChild(entry);
		

		entry = montarXml.getDoc().createElement("entry");
		entry.setAttribute("key", "consulta_nacional");
		value = montarXml.getDoc().createElement("value");
		entry.appendChild(value);
		bolean = montarXml.getDoc().createElement("Boolean");
		value.appendChild(bolean);
		bolean.appendChild(montarXml.getDoc().createTextNode(
				getConsultaNacional()));
		map.appendChild(entry);

		entry = montarXml.getDoc().createElement("entry");
		entry.setAttribute("key", "mergeTemplates");
		entry.setAttribute("value", "false");
		map.appendChild(entry);

		entry = montarXml.getDoc().createElement("entry");
		entry.setAttribute("key", "perfilAutomatico");
		value = montarXml.getDoc().createElement("value");
		entry.appendChild(value);
		bolean = montarXml.getDoc().createElement("Boolean");
		value.appendChild(bolean);
		bolean.appendChild(montarXml.getDoc().createTextNode(getStatus()));
		map.appendChild(entry);

		entry = montarXml.getDoc().createElement("entry");
		entry.setAttribute("key", "sysDescriptions");
		value = montarXml.getDoc().createElement("value");
		entry.appendChild(value);
		mapInt = montarXml.getDoc().createElement("Map");
		value.appendChild(mapInt);
		entryInt = montarXml.getDoc().createElement("entry");
		mapInt.appendChild(entryInt);
		entryInt.setAttribute("key", "en_US");
		entryInt.setAttribute("value", getFuncionalidade());
		map.appendChild(entry);

		entry = montarXml.getDoc().createElement("entry");
		entry.setAttribute("key", "unidade_gestora");
		map.appendChild(entry);
	}

	public Element getAttributes() {
		return attributes;
	}

	public void setAttributes(Element attributes) {
		this.attributes = attributes;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAtualizacaoNacional() {
		return atualizacaoNacional;
	}

	public void setAtualizacaoNacional(String atualizacaoNacional) {
		this.atualizacaoNacional = atualizacaoNacional;
	}

	public String getConsultaNacional() {
		return consultaNacional;
	}

	public void setConsultaNacional(String consultaNacional) {
		this.consultaNacional = consultaNacional;
	}

	public String getNivelSeguranca() {
		return nivelSeguranca;
	}

	public void setNivelSeguranca(String nivelSeguranca) {
		this.nivelSeguranca = nivelSeguranca;
	}

	public String getFuncionalidade() {
		return funcionalidade;
	}

	public void setFuncionalidade(String funcionalidade) {
		this.funcionalidade = funcionalidade;
	}

	public String getVisaoDiretoria() {
		return visaoDiretoria;
	}

	public void setVisaoDiretoria(String visaoDiretoria) {
		this.visaoDiretoria = visaoDiretoria;
	}

	
	
	
}
