package br.com.pbti.perfil.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.w3c.dom.Element;

import br.com.pbti.perfil.principal.XmlConfigPerfil;
import br.com.pbti.perfil.xml.MontarXmlPerfil;


public class SelectorPerfil {

	// Objetos
	private Element selector;
	private Element montarSelector;
	private Element identityselector;
	private Element compoundfilter;
	private Element compositefilterOr;
	private Element compositefilterAnd;
	private Element filter;
	private Element value;
	private Element list;
	private Element tagString;

	// variaveis dinamincas
	private String operacaoCompositerAnd = "AND";
	private String operacaoCompositerOr = "OR";

	public MontarXmlPerfil montarXml = new MontarXmlPerfil();

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

	@SuppressWarnings({ "static-access", "unchecked" })
	private void CompositeFilter() {

		ArrayList<Map<String, Object>> listaPerfil = new ArrayList<Map<String, Object>>();

		listaPerfil.addAll(XmlConfigPerfil.getArraylistaCodFuncao());

		if (listaPerfil == null || listaPerfil.isEmpty()) {
			System.out.println("Lista Vazia");

		} else {

			compositefilterOr = montarXml.getDoc().createElement(
					"CompositeFilter");
			compositefilterOr.setAttribute("operation", operacaoCompositerOr);
			compoundfilter.appendChild(compositefilterOr);

			for (Map<String, Object> listaPerfils : listaPerfil) {

				compositefilterAnd = montarXml.getDoc().createElement(
						"CompositeFilter");
				compositefilterAnd.setAttribute("operation",
						operacaoCompositerAnd);
				compositefilterOr.appendChild(compositefilterAnd);

				String FUNCAO = "funcao", COD_UNIDADE = "cod_unidade", TIPO_USU = "tipo_usuario", COD_CARGO = "cod_cargo", IND_UNIDADE = "ind_unidade", asteri = "*", lotacaoFisica = "F", lotacaoAdm = "A", funcaoSEM = "SEMF", funcaoCOM = "COMF", LIST_FUNCAO = "cod_funcao", listTipo_unidade = "listTipo_unidade";

				ArrayList<Object> arrayTipoUnidade = new ArrayList<Object>();
				arrayTipoUnidade
						.addAll((Collection<? extends Object>) listaPerfils
								.get(listTipo_unidade));

				if (listaPerfils.get(IND_UNIDADE).equals(lotacaoFisica)) {

					if ((arrayTipoUnidade.size() == 1 && !arrayTipoUnidade.get(
							0).equals(asteri))
							|| (arrayTipoUnidade.size() > 1 && !arrayTipoUnidade
									.get(0).equals(asteri))) {

						filter = montarXml.getDoc().createElement("Filter");
						filter.setAttribute("operation", "IN");
						filter.setAttribute("property", "sg_unde_ltco_fisica");
						compositefilterAnd.appendChild(filter);

						value = montarXml.getDoc().createElement("Value");
						filter.appendChild(value);
						list = montarXml.getDoc().createElement("List");
						value.appendChild(list);

						for (Object tipoUnidade : arrayTipoUnidade) {

							if (!tipoUnidade.equals(asteri)) {

								tagString = montarXml.getDoc().createElement(
										"String");
								tagString
										.appendChild(montarXml.getDoc()
												.createTextNode(
														tipoUnidade.toString()));
								list.appendChild(getString());

							}

						}
					}

				} else if (listaPerfils.get(IND_UNIDADE).equals(lotacaoAdm)) {

					if ((arrayTipoUnidade.size() == 1 && !arrayTipoUnidade.get(
							0).equals(asteri))
							|| (arrayTipoUnidade.size() > 1 && !arrayTipoUnidade
									.get(0).equals(asteri))) {

						filter = montarXml.getDoc().createElement("Filter");
						filter.setAttribute("operation", "IN");
						filter.setAttribute("property", "sg_unde_ltco");
						compositefilterAnd.appendChild(filter);

						value = montarXml.getDoc().createElement("Value");
						filter.appendChild(value);
						list = montarXml.getDoc().createElement("List");
						value.appendChild(list);

						for (Object tipoUnidade : arrayTipoUnidade) {

							if (!tipoUnidade.equals(asteri)) {

								tagString = montarXml.getDoc().createElement(
										"String");
								tagString
										.appendChild(montarXml.getDoc()
												.createTextNode(
														tipoUnidade.toString()));
								list.appendChild(getString());
							}

						}

					}

				} else {

					System.out
							.println("Dado inconsistente na coluna 9 IND_UNIDADE");
				}

				if (!listaPerfils.get(COD_UNIDADE).equals(asteri)) {

					if (listaPerfils.get(IND_UNIDADE).equals(lotacaoFisica)) {

						int intCodUnidade = Integer.parseInt(listaPerfils.get(
								COD_UNIDADE).toString());

						String uni = Integer.toString(intCodUnidade);

						filter = montarXml.getDoc().createElement("Filter");
						filter.setAttribute("operation", "EQ");
						filter.setAttribute("property", "nu_unde_ltco_fisica");
						filter.setAttribute("value", uni.toString());

						compositefilterAnd.appendChild(filter);
					} else if (listaPerfils.get(IND_UNIDADE).equals(lotacaoAdm)) {

						int intCodUnidade = Integer.parseInt(listaPerfils.get(
								COD_UNIDADE).toString());

						String uni = Integer.toString(intCodUnidade);

						filter = montarXml.getDoc().createElement("Filter");
						filter.setAttribute("operation", "EQ");
						filter.setAttribute("property", "nu_unde_ltco");
						filter.setAttribute("value", uni.toString());
						compositefilterAnd.appendChild(filter);
					} else {

						System.out
								.println("Dado inconsistente na coluna 9 IND_UNIDADE");
					}
				}

				if (!listaPerfils.get(TIPO_USU).equals(asteri)) {

					filter = montarXml.getDoc().createElement("Filter");
					filter.setAttribute("operation", "EQ");
					filter.setAttribute("property", "tipo_usuario");
					filter.setAttribute("value", listaPerfils.get(TIPO_USU)
							.toString());
					compositefilterAnd.appendChild(filter);
				}

				if (!listaPerfils.get(COD_CARGO).equals(asteri)) {

					filter = montarXml.getDoc().createElement("Filter");
					filter.setAttribute("operation", "EQ");
					filter.setAttribute("property", "co_cargo");
					filter.setAttribute("value", listaPerfils.get(COD_CARGO)
							.toString());
					compositefilterAnd.appendChild(filter);
				}

				if (!listaPerfils.get(FUNCAO).equals(asteri)) {

					if (listaPerfils.get(FUNCAO).equals(funcaoCOM)) {
						filter = montarXml.getDoc().createElement("Filter");
						filter.setAttribute("operation", "NOTNULL");
						filter.setAttribute("property", "nu_funcao");
						compositefilterAnd.appendChild(filter);
					}

					if (listaPerfils.get(FUNCAO).equals(funcaoSEM)) {
						filter = montarXml.getDoc().createElement("Filter");
						filter.setAttribute("operation", "ISNULL");
						filter.setAttribute("property", "nu_funcao");
						compositefilterAnd.appendChild(filter);
					}
				}

				// ArrayList<Object> arrayCodFuncao = new ArrayList<Object>();
				// arrayCodFuncao.addAll((Collection<? extends Object>)
				// listaPerfils
				// .get(LIST_FUNCAO));

				if (!listaPerfils.get(LIST_FUNCAO).equals(asteri)) {

					int intFunc = Integer.parseInt(listaPerfils
							.get(LIST_FUNCAO).toString());

					String func = Integer.toString(intFunc);

					filter = montarXml.getDoc().createElement("Filter");
					filter.setAttribute("operation", "EQ");
					filter.setAttribute("property", "nu_funcao");
					filter.setAttribute("value", func);
					compositefilterAnd.appendChild(filter);
				}
			}

		}
	}

	public Element getSelector() {
		return selector;
	}

	public void setSelector(Element selector) {
		this.selector = selector;
	}

	public String getOperacao() {
		return operacaoCompositerAnd;
	}

	public void setOperacao(String operacao) {
		this.operacaoCompositerAnd = operacao;
	}

	public Element getString() {
		return tagString;
	}

	public void setString(Element string) {
		this.tagString = string;
	}

	public Element getCompositefilterAnd() {
		return compositefilterAnd;
	}

	public void setCompositefilterAnd(Element compositefilterAnd) {
		this.compositefilterAnd = compositefilterAnd;
	}

	public String getOperacaoCompositerOr() {
		return operacaoCompositerOr;
	}

	public void setOperacaoCompositerOr(String operacaoCompositerOr) {
		this.operacaoCompositerOr = operacaoCompositerOr;
	}
}
