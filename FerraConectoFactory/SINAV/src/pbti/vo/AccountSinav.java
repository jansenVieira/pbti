package br.com.pbti.vo;

public class AccountSinav {

	private String cdUsuario;
	private String noUsuario;
	private String codUnidade;
	private String cdSisDefaul;
	private String cdImprRemot;
	private String cdSitUsu;
	private String cdSuregLot;
	private String cdCeadmAut;
	private String cdSuregAut;
	private String cdUnidAut;
	private String idDiretoria;
	private String cdTerminal;
	private String idStatus;
	private String idCef;
	private String cdNoRemot;
	private String cdFuncao;
	private String coSegmento;
	private String nuCnpj;
	private String nuMatrEmp;
	private String nuCaEnFisica;

	public AccountSinav(String cdUsuario, String noUsuario, String codUnidade, String cdSisDefaul, String cdImprRemot,
			String cdSitUsu, String cdSuregLot, String cdCeadmAut, String cdSuregAut, String cdUnidAut,
			String idDiretoria, String cdTerminal, String idStatus, String idCef, String cdNoRemot, String cdFuncao,
			String coSegmento, String nuCnpj, String nuMatrEmp, String nuCaEnFisica) {

		this.cdUsuario = cdUsuario;
		this.noUsuario = noUsuario;
		this.codUnidade = codUnidade;
		this.cdSisDefaul = cdSisDefaul;
		this.cdImprRemot = cdImprRemot;
		this.cdSitUsu = cdSitUsu;
		this.cdSuregLot = cdSuregLot;
		this.cdCeadmAut = cdCeadmAut;
		this.cdSuregAut = cdSuregAut;
		this.cdUnidAut = cdUnidAut;
		this.idDiretoria = idDiretoria;
		this.cdTerminal = cdTerminal;
		this.idStatus = idStatus;
		this.idCef = idCef;
		this.cdNoRemot = cdNoRemot;
		this.cdFuncao = cdFuncao;
		this.coSegmento = coSegmento;
		this.nuCnpj = nuCnpj;
		this.nuMatrEmp = nuMatrEmp;
		this.nuCaEnFisica = nuCaEnFisica;
	}

	public AccountSinav(String cdUsuario, String noUsuario) {

		this.cdUsuario = cdUsuario;
		this.noUsuario = noUsuario;
	}

	public String getCdUsuario() {
		if (cdUsuario == null || cdUsuario.isEmpty()) {
			cdUsuario = "-";
		}

		return cdUsuario;
	}

	public void setCdUsuario(String cdUsuario) {
		this.cdUsuario = cdUsuario;
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

	public String getCodUnidade() {
		if (codUnidade == null || codUnidade.isEmpty()) {
			codUnidade = "-";
		}

		return codUnidade;
	}

	public void setCodUnidade(String codUnidade) {
		this.codUnidade = codUnidade;
	}

	public String getCdSisDefaul() {
		if (cdSisDefaul == null || cdSisDefaul.isEmpty()) {
			cdSisDefaul = "-";
		}
		return cdSisDefaul;
	}

	public void setCdSisDefaul(String cdSisDefaul) {
		this.cdSisDefaul = cdSisDefaul;
	}

	public String getCdImprRemot() {
		if (cdImprRemot == null || cdImprRemot.isEmpty()) {
			cdImprRemot = "-";
		}
		return cdImprRemot;
	}

	public void setCdImprRemot(String cdImprRemot) {
		this.cdImprRemot = cdImprRemot;
	}

	public String getCdSitUsu() {
		if (cdSitUsu == null || cdSitUsu.isEmpty()) {
			cdSitUsu = "-";
		}
		return cdSitUsu;
	}

	public void setCdSitUsu(String cdSitUsu) {
		this.cdSitUsu = cdSitUsu;
	}

	public String getCdSuregLot() {
		if (cdSuregLot == null || cdSuregLot.isEmpty()) {
			cdSuregLot = "-";
		}
		return cdSuregLot;
	}

	public void setCdSuregLot(String cdSuregLot) {
		this.cdSuregLot = cdSuregLot;
	}

	public String getCdCeadmAut() {
		if (cdCeadmAut == null || cdCeadmAut.isEmpty()) {
			cdCeadmAut = "-";
		}
		return cdCeadmAut;
	}

	public void setCdCeadmAut(String cdCeadmAut) {
		this.cdCeadmAut = cdCeadmAut;
	}

	public String getCdSuregAut() {
		if (cdSuregAut == null || cdSuregAut.isEmpty()) {
			cdSuregAut = "-";
		}
		return cdSuregAut;
	}

	public void setCdSuregAut(String cdSuregAut) {
		this.cdSuregAut = cdSuregAut;
	}

	public String getCdUnidAut() {
		if (cdUnidAut == null || cdUnidAut.isEmpty()) {
			cdUnidAut = "-";
		}

		return cdUnidAut;
	}

	public void setCdUnidAut(String cdUnidAut) {
		this.cdUnidAut = cdUnidAut;
	}

	public String getIdDiretoria() {
		if (idDiretoria == null || idDiretoria.isEmpty()) {
			idDiretoria = "-";
		}
		return idDiretoria;
	}

	public void setIdDiretoria(String idDiretoria) {
		this.idDiretoria = idDiretoria;
	}

	public String getCdTerminal() {
		if (cdTerminal == null || cdTerminal.isEmpty()) {
			cdTerminal = "-";
		}
		return cdTerminal;
	}

	public void setCdTerminal(String cdTerminal) {
		this.cdTerminal = cdTerminal;
	}

	public String getIdStatus() {
		if (idStatus == null || idStatus.isEmpty()) {
			idStatus = "-";
		}
		return idStatus;
	}

	public void setIdStatus(String idStatus) {
		this.idStatus = idStatus;
	}

	public String getIdCef() {
		if (idCef == null || idCef.isEmpty()) {
			idCef = "-";
		}
		return idCef;
	}

	public void setIdCef(String idCef) {
		this.idCef = idCef;
	}

	public String getCdNoRemot() {
		if (cdNoRemot == null || cdNoRemot.isEmpty()) {
			cdNoRemot = "-";
		}
		return cdNoRemot;
	}

	public void setCdNoRemot(String cdNoRemot) {
		this.cdNoRemot = cdNoRemot;
	}

	public String getCdFuncao() {
		if (cdFuncao == null || cdFuncao.isEmpty()) {
			cdFuncao = "-";
		}
		return cdFuncao;
	}

	public void setCdFuncao(String cdFuncao) {
		this.cdFuncao = cdFuncao;
	}

	public String getCoSegmento() {
		if (coSegmento == null || coSegmento.isEmpty()) {
			coSegmento = "-";
		}
		return coSegmento;
	}

	public void setCoSegmento(String coSegmento) {
		this.coSegmento = coSegmento;
	}

	public String getNuCnpj() {
		if (nuCnpj == null || nuCnpj.isEmpty()) {
			nuCnpj = "-";
		}
		return nuCnpj;
	}

	public void setNuCnpj(String nuCnpj) {
		this.nuCnpj = nuCnpj;
	}

	public String getNuMatrEmp() {
		if (nuMatrEmp == null || nuMatrEmp.isEmpty()) {
			nuMatrEmp = "-";
		}
		return nuMatrEmp;
	}

	public void setNuMatrEmp(String nuMatrEmp) {
		this.nuMatrEmp = nuMatrEmp;
	}

	public String getNuCaEnFisica() {
		if (nuCaEnFisica == null || nuCaEnFisica.isEmpty()) {
			nuCaEnFisica = "-";
		}
		return nuCaEnFisica;
	}

	public void setNuCaEnFisica(String nuCaEnFisica) {
		this.nuCaEnFisica = nuCaEnFisica;
	}

}
