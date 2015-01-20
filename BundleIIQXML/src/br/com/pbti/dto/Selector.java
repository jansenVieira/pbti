package br.com.pbti.dto;

import java.util.ArrayList;
import java.util.List;

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
	private String operacao = "AND";
	private String operacaoFil1 = "EQ";
	private String property1 = "sg_unde_ltco";
	private String value1 = "GESET";
	private String operacaoFil2 = "EQ";
	private String property2 = "tipo_usuario";
	private String value2 = "C";
	private String operacaoFil3 = "IN";
	private String property3 = "funcao";
	private String value3 = "";

	public MontarXml montarXml = new MontarXml();

	public void montarSelector() {
		montarSelector = montarXml.getDoc().createElement("Selector");
		setSelector(montarSelector);
		IdentitySelector();
	}

	private void IdentitySelector() {
		identityselector = montarXml.getDoc().createElement("IdentitySelector");
		selector.appendChild(identityselector);
		CompoundFilter();
	}

	private void CompoundFilter() {
		compoundfilter = montarXml.getDoc().createElement("CompoundFilter");
		identityselector.appendChild(compoundfilter);
		CompositeFilter();
	}

	private void CompositeFilter() {
		compositefilter = montarXml.getDoc().createElement("CompositeFilter");
		compositefilter.setAttribute("operation", operacao);
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
		string.appendChild(montarXml.getDoc().createTextNode("278"));
		list.appendChild(string);
		string2 = montarXml.getDoc().createElement("String");
		string2.appendChild(montarXml.getDoc().createTextNode("279"));
		list.appendChild(string2);
		string3 = montarXml.getDoc().createElement("String");
		string3.appendChild(montarXml.getDoc().createTextNode("564"));
		list.appendChild(string3);

	}

	public Element getSelector() {
		return selector;
	}

	public void setSelector(Element selector) {
		this.selector = selector;
	}

	public String getOperacao() {
		return operacao;
	}

	public void setOperacao(String operacao) {
		this.operacao = operacao;
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

	public String getValue3() {
		return value3;
	}

	public void setValue3(String value3) {
		this.value3 = value3;
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

}
