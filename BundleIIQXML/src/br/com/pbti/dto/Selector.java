package br.com.pbti.dto;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import br.com.pbti.xml.MontarXml;


public class Selector {

	//
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
	//variaveis dinamincas
	String operacao = "AND";
	String operacaoFil1 = "EQ";
	String property1 ="sg_unde_ltco";
	String value1 = "GESET";
	String operacaoFil2 = "EQ";
	String property2 ="tipo_usuario";
	String value2 = "C";
	String operacaoFil3 = "IN";
	String property3 ="funcao";
	String value3 = "";

	public MontarXml montarXml = new MontarXml();
	
	public void montarSelector()
	{
		montarSelector = montarXml.getDoc().createElement("Selector");
		setSelector(montarSelector);
		IdentitySelector();
	}

	private void IdentitySelector()
	{
		identityselector = montarXml.getDoc().createElement("IdentitySelector");
		selector.appendChild(identityselector);
		CompoundFilter();
	}

	private void CompoundFilter()
	{
		compoundfilter = montarXml.getDoc().createElement("CompoundFilter");
		identityselector.appendChild(compoundfilter);
		CompositeFilter();
	}
	
	private void CompositeFilter()
	{
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
		list =  montarXml.getDoc().createElement("List");
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
}
