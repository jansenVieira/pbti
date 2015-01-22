package br.com.pbti.dto;

import org.w3c.dom.Element;

import br.com.pbti.xml.MontarXml;

public class Selector {

	// Objetos
	private Element selector;
	private Element montarSelector;
	private Element identityselector;
	private Element compoundfilter;
	private Element compositefilter;
	private Element filter;
	private Element value;
	private Element list;
	private Element string;
	private Element string2;
	private Element string3;
	
	// variaveis dinamincas
	private String operacaoCompositer;
	private String operacaoFil1;
	private String property1;
	private String value1;
	private String operacaoFil2;
	private String property2;
	private String value2;
	private String operacaoFil3;
	private String property3;
	private String listString;
	private String listString2;
	private String listString3;

	public MontarXml montarXml = new MontarXml();

	@SuppressWarnings("static-access")
	public void montarSelector() {
		montarSelector = montarXml.getDoc().createElement("Selector");
		setSelector(montarSelector);
		IdentitySelector();
	}

	@SuppressWarnings("static-access")
	private void IdentitySelector() {
		identityselector = montarXml.getDoc().createElement("IdentitySelector");
		selector.appendChild(identityselector);
		CompoundFilter();
	}

	@SuppressWarnings("static-access")
	private void CompoundFilter() {
		compoundfilter = montarXml.getDoc().createElement("CompoundFilter");
		identityselector.appendChild(compoundfilter);
		CompositeFilter();
	}

	@SuppressWarnings("static-access")
	private void CompositeFilter() {
		compositefilter = montarXml.getDoc().createElement("CompositeFilter");
		compositefilter.setAttribute("operation", operacaoCompositer);
		compoundfilter.appendChild(compositefilter);

		filter = montarXml.getDoc().createElement("Filter");
		filter.setAttribute("operation", operacaoFil1);
		filter.setAttribute("property", property1);
		filter.setAttribute("value", value1);
		compositefilter.appendChild(filter);

		filter = montarXml.getDoc().createElement("Filter");
		filter.setAttribute("operation", operacaoFil2);
		filter.setAttribute("property", property2);
		filter.setAttribute("value", value2);
		compositefilter.appendChild(filter);

		filter = montarXml.getDoc().createElement("Filter");
		filter.setAttribute("operation", operacaoFil3);
		filter.setAttribute("property", property3);
		compositefilter.appendChild(filter);

		value = montarXml.getDoc().createElement("Value");
		filter.appendChild(value);
		list = montarXml.getDoc().createElement("List");
		value.appendChild(list);
		string = montarXml.getDoc().createElement("String");
		string.appendChild(montarXml.getDoc().createTextNode(listString));
		list.appendChild(string);
		string2 = montarXml.getDoc().createElement("String");
		string2.appendChild(montarXml.getDoc().createTextNode(listString2));
		list.appendChild(string2);
		string3 = montarXml.getDoc().createElement("String");
		string3.appendChild(montarXml.getDoc().createTextNode(listString3));
		list.appendChild(string3);

	}

	public Element getSelector() {
		return selector;
	}

	public void setSelector(Element selector) {
		this.selector = selector;
	}

	public String getOperacao() {
		return operacaoCompositer;
	}

	public void setOperacao(String operacao) {
		this.operacaoCompositer = operacao;
	}

	public String getOperacaoFil1() {
		return operacaoFil1;
	}

	public void setOperacaoFil1(String operacaoFil1) {
		this.operacaoFil1 = operacaoFil1;
	}

	public String getProperty1() {
		return property1;
	}

	public void setProperty1(String property1) {
		this.property1 = property1;
	}

	public String getValue1() {
		return value1;
	}

	public void setValue1(String value1) {
		this.value1 = value1;
	}

	public String getOperacaoFil2() {
		return operacaoFil2;
	}

	public void setOperacaoFil2(String operacaoFil2) {
		this.operacaoFil2 = operacaoFil2;
	}

	public String getProperty2() {
		return property2;
	}

	public void setProperty2(String property2) {
		this.property2 = property2;
	}

	public String getValue2() {
		return value2;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}

	public String getOperacaoFil3() {
		return operacaoFil3;
	}

	public void setOperacaoFil3(String operacaoFil3) {
		this.operacaoFil3 = operacaoFil3;
	}

	public String getProperty3() {
		return property3;
	}

	public void setProperty3(String property3) {
		this.property3 = property3;
	}

	public Element getString() {
		return string;
	}

	public void setString(Element string) {
		this.string = string;
	}

	public Element getString2() {
		return string2;
	}

	public void setString2(Element string2) {
		this.string2 = string2;
	}

	public Element getString3() {
		return string3;
	}

	public void setString3(Element string3) {
		this.string3 = string3;
	}

	public String getOperacaoCompositer() {
		return operacaoCompositer;
	}

	public void setOperacaoCompositer(String operacaoCompositer) {
		this.operacaoCompositer = operacaoCompositer;
	}

	public String getListString() {
		return listString;
	}

	public void setListString(String listString) {
		this.listString = listString;
	}

	public String getListString2() {
		return listString2;
	}

	public void setListString2(String listString2) {
		this.listString2 = listString2;
	}

	public String getListString3() {
		return listString3;
	}

	public void setListString3(String listString3) {
		this.listString3 = listString3;
	}
	
	

}
