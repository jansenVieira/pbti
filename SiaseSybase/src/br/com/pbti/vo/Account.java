package br.com.pbti.vo;

public class Account {

	private String coUsuario;
	private String noUsuario;
	private String noPerfil;
	private String noSistema;
	private String icStatus;
	private String cdSenha;
	private String icTipo;
	private String nuMatricula;

	public Account(String coUsuario, String noUsuario, String icStatus, String icTipo, String nuMatricula) {

		this.coUsuario = coUsuario;
		this.noUsuario = noUsuario;
		this.icStatus = icStatus;
		this.icTipo = icTipo;
		this.nuMatricula = nuMatricula;
	}

	public Account(String coUsuario, String noUsuario) {

		this.coUsuario = coUsuario;
		this.noUsuario = noUsuario;
	}

	public String getNuMatricula() {
		if (nuMatricula == null || nuMatricula.isEmpty()) {
			nuMatricula = "-";
		}

		return nuMatricula;
	}

	public void setNuMatricula(String nuMatricula) {
		this.nuMatricula = nuMatricula;
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

	public String getNoPerfil() {
		if (noPerfil == null || noPerfil.isEmpty()) {
			noPerfil = "-";
		}
		return noPerfil;
	}

	public void setNoPerfil(String noPerfil) {
		this.noPerfil = noPerfil;
	}

	public String getNoSistema() {
		if (noSistema == null || noSistema.isEmpty()) {
			noSistema = "-";
		}

		return noSistema;
	}

	public void setNoSistema(String noSistema) {
		this.noSistema = noSistema;
	}

	public String getIcStatus() {
		if (icStatus == null || icStatus.isEmpty()) {
			icStatus = "-";
		}

		return icStatus;
	}

	public void setIcStatus(String icStatus) {
		this.icStatus = icStatus;
	}

	public String getCdSenha() {
		if (cdSenha == null || cdSenha.isEmpty()) {
			cdSenha = "-";
		}

		return cdSenha;
	}

	public void setCdSenha(String cdSenha) {
		this.cdSenha = cdSenha;
	}

	public String getIcTipo() {
		if (icTipo == null || icTipo.isEmpty()) {
			icTipo = "-";
		}

		return icTipo;
	}

	public void setIcTipo(String icTipo) {
		this.icTipo = icTipo;
	}

}
