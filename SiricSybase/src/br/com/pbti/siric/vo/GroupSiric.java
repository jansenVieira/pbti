package br.com.pbti.siric.vo;

public class GroupSiric {

	private String numGrupo;
	private String descGrupo;

	public GroupSiric(String numGrupo, String descGrupo) {

		this.numGrupo = numGrupo;
		this.descGrupo = descGrupo;
	}

	public String getNumGrupo() {
		return numGrupo;
	}

	public void setNumGrupo(String numGrupo) {
		this.numGrupo = numGrupo;
	}

	public String getDescGrupo() {
		return descGrupo;
	}

	public void setDescGrupo(String descGrupo) {
		this.descGrupo = descGrupo;
	}

}
