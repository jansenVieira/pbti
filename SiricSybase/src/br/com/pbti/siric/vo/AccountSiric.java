package br.com.pbti.siric.vo;

public class AccountSiric {

	private String coUsuario;
	private String noUsuario;
	private String status;
	private String lotacaoId;
	private String codUnidLotacao;
	private String nuFuncao;

	public AccountSiric(String coUsuario, String noUsuario, String status, String lotacaoId, String codUnidLotacao,
			String nuFuncao) {

		this.coUsuario = coUsuario;
		this.noUsuario = noUsuario;
		this.status = status;
		this.lotacaoId = lotacaoId;
		this.codUnidLotacao = codUnidLotacao;
		this.nuFuncao = nuFuncao;
	}

	public AccountSiric(String coUsuario, String noUsuario) {

		this.coUsuario = coUsuario;
		this.noUsuario = noUsuario;
	}

	public String getNoUsuario() {
		if (noUsuario == null || noUsuario.isEmpty()) {
			noUsuario = "-";
		}

		return noUsuario;
	}

	public void setNoUsuario(String noUsuario) {
		this.noUsuario = noUsuario;
	}

	public String getCoUsuario() {
		if (coUsuario == null || coUsuario.isEmpty()) {
			coUsuario = "-";
		}

		return coUsuario;
	}

	public void setCoUsuario(String coUsuario) {
		this.coUsuario = coUsuario;
	}

	public String getStatus() {
		if (status == null || status.isEmpty()) {
			status = "-";
		}

		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLotacaoId() {
		if (lotacaoId == null || lotacaoId.isEmpty()) {
			lotacaoId = "-";
		}
		return lotacaoId;
	}

	public void setLotacaoId(String lotacaoId) {
		this.lotacaoId = lotacaoId;
	}

	public String getCodUnidLotacao() {
		if (codUnidLotacao == null || codUnidLotacao.isEmpty()) {
			codUnidLotacao = "0";
		}
		return codUnidLotacao;
	}

	public void setCodUnidLotacao(String codUnidLotacao) {
		this.codUnidLotacao = codUnidLotacao;
	}

	public String getNuFuncao() {

		if (nuFuncao == null || nuFuncao.isEmpty()) {
			nuFuncao = "0";
		}
		return nuFuncao;
	}

	public void setNuFuncao(String nuFuncao) {
		this.nuFuncao = nuFuncao;
	}

}
