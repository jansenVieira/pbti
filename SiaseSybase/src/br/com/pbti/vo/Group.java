package br.com.pbti.vo;

public class Group {

	private String codigoSistema;
	private String codigoPerfil;
	private String nomePerfil;

	public Group(String codigoSistema, String codigoPerfil, String nomePerfil) {

		this.codigoSistema = codigoSistema;
		this.codigoPerfil = codigoPerfil;
		this.nomePerfil = nomePerfil;
	}
	
	
	public String getCodigoSistema() {
		return codigoSistema;
	}

	public void setCodigoSistema(String codigoSistema) {
		this.codigoSistema = codigoSistema;
	}

	public String getCodigoPerfil() {
		return codigoPerfil;
	}

	public void setCodigoPerfil(String codigoPerfil) {
		this.codigoPerfil = codigoPerfil;
	}

	public String getNomePerfil() {
		return nomePerfil;
	}

	public void setNomePerfil(String nomePerfil) {
		this.nomePerfil = nomePerfil;
	}
}
