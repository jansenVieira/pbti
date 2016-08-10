package to;

import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;

import framework.XSA_Framework;
import util.CriptografiaEase;
import util.UtilDate;

/**
 * Entidade Usuario(EASE) e Account(Control-SA)
 * 
 * @author Michael Alves Lins <malins@dba.com.br>
 */
public class AccountTO {

	private String desperfil; // Descricao do perfil associado ao usuario
	private Date fecactiva; // Data de ativação (Formato dd/mm/aaaa)
	private Date fecdesact; // Data de desativação do usuario
	private Date fecbaja; // Data de exclusao do usuario
	private String usuario; // Login do usuário
	private String codperfil; // Código do perfil do novo usuario
	private String nombreusu; // Nome completo do novo usuario
	private String nivsegusu; // Nível de segurança do usuario
	private String codidioma; // Código do Idioma do usuario
	private String centtra; // Código da estação de trabalho do usuario
	private String oficina; // Agencia do usuário
	private String password; /*
							 * Senha do usuário (a senha devera' ser sempre o
							 * login e criptografada em hash
							 */
	private String maxSenhasIncorretas;
	private String usubajac;

	/**
	 * @return the maxSenhasIncorretas
	 * @default 5 senhas incorretas
	 */
	public String getMaxSenhasIncorretas() {
		if ((maxSenhasIncorretas == null)
				|| maxSenhasIncorretas.trim().equals(""))
			maxSenhasIncorretas = "5";
		return maxSenhasIncorretas;
	}

	/**
	 * @param maxSenhasIncorretas
	 *            the maxSenhasIncorretas to set
	 */
	public void setMaxSenhasIncorretas(String maxSenhasIncorretas) {
		this.maxSenhasIncorretas = maxSenhasIncorretas;
	}

	/**
	 * @return the usuario
	 */
	public String getUsuario() {
		return usuario;
	}

	/**
	 * @param usuario
	 *            the usuario to set
	 */
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	/**
	 * @return the fecactiva in Control-SA format
	 */
	public String getFecactivaForControlSA() {
		String result = null;
		if (fecactiva != null && !fecactiva.equals(""))
			result = UtilDate.formatDateControlSA(fecactiva);
		return result;
	}

	/**
	 * @return the fecActiva in Ease format
	 */
	public String getFecactivaForEASE() {
		String result = null;
		if (fecactiva != null && !fecactiva.equals(""))
			result = UtilDate.formatDateEase(fecactiva);
		return result;
	}

	/**
	 * @return the fecactiva as Date
	 */
	public Date getFecactiva() {
		return fecactiva;
	}

	/**
	 * @param fecactiva
	 *            the fecactiva to set
	 */
	public void setFecactiva(Date fecactiva) {
		this.fecactiva = fecactiva;
	}

	/**
	 * @return the fecdesact in Control-SA format
	 */
	public String getFecdesactForControlSA() {
		String result = null;
		if (fecdesact != null && !fecdesact.equals(""))
			result = UtilDate.formatDateControlSA(fecdesact);
		return result;
	}

	/**
	 * @return the fecDesact in EASE format
	 */
	public String getFecdesactForEASE() {
		String result = null;
		if (fecdesact != null && !fecdesact.equals(""))
			result = UtilDate.formatDateEase(fecdesact);
		return result;
	}

	/**
	 * @return the fecdesact as Date
	 */
	public Date getFecdesact() {
		return fecdesact;
	}

	/**
	 * @param fecdesact
	 *            the fecdesact to set
	 */
	public void setFecdesact(Date fecdesact) {
		this.fecdesact = fecdesact;
	}

	/**
	 * @return the nivsegusu
	 */
	public String getNivsegusu() {
		return nivsegusu;
	}

	/**
	 * @param nivsegusu
	 *            the nivsegusu to set
	 */
	public void setNivsegusu(String nivsegusu) {
		this.nivsegusu = nivsegusu;
	}

	/**
	 * @return the centtra
	 */
	public String getCenttra() {
		return centtra;
	}

	/**
	 * @param centtra
	 *            the centtra to set
	 */
	public void setCenttra(String centtra) {
		this.centtra = centtra;
	}

	/**
	 * @return the codperfil
	 */
	public String getCodperfil() {
		return codperfil;
	}

	/**
	 * @param codperfil
	 *            the codperfil to set
	 */
	public void setCodperfil(String codperfil) {
		this.codperfil = codperfil;
	}

	/**
	 * @return the desperfil
	 */
	public String getDesperfil() {
		return desperfil;
	}

	/**
	 * @param desperfil
	 *            the desperfil to set
	 */
	public void setDesperfil(String desperfil) {
		this.desperfil = desperfil;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) throws Exception {
		this.password = CriptografiaEase.getInstance().hash(
				password.toUpperCase());
	}

	/**
	 * Saves the password hash string directly
	 * 
	 * Para os casos de recuperacao de usuarios diretamente do Ease para
	 * comparacao de senha com Control-SA
	 * 
	 * @param hash
	 */
	public void setHashPassword(String hash) {
		this.password = hash;
	}

	/**
	 * @return the nombreusu
	 */
	public String getNombreusu() {
		return nombreusu;
	}

	/**
	 * @param nombreusu
	 *            the nombreusu to set
	 */
	public void setNombreusu(String nombreusu) {
		this.nombreusu = nombreusu;
	}

	/**
	 * @return the oficina
	 */
	public String getOficina() {
		return oficina;
	}

	/**
	 * @param oficina
	 *            the oficina to set
	 */
	public void setOficina(String oficina) {
		this.oficina = oficina;
	}

	/**
	 * @return the codidioma
	 */
	public String getCodidioma() {
		return codidioma;
	}

	/**
	 * @param codidioma
	 *            the codidioma to set
	 */
	public void setCodidioma(String codidioma) {
		this.codidioma = codidioma;
	}

	/**
	 * Retorna o valor para usuario ativo/inativo segundo o Control-SA O
	 * indicador no EASE de desativacao é o preenchimento da data de Desativacao
	 * Se preenchida, ser menor que a data atual
	 * 
	 * @return
	 */
	public String getRevokeStatus() {
		String retorno = "";
		Date dataDesativacao = fecdesact;
		if (dataDesativacao == null) {
			retorno = XSA_Framework.ACCOUNT_REVOKE_STATUS.ACTIVE;
		} else {
			Date hoje = UtilDate.parseDateForEASE(UtilDate.getHojeEase());
			if (dataDesativacao.compareTo(hoje) < 0)
				retorno = XSA_Framework.ACCOUNT_REVOKE_STATUS.REVOKED;
		}
		return retorno;
	}

	/**
	 * Retorna o valor para usuario ativo/inativo segundo o Control-SA O
	 * indicador no EASE de desativacao é o preenchimento da data de Desativacao
	 * Se preenchida, ser menor que a data atual
	 * 
	 * @return
	 */
	public boolean isDesativado() {
		boolean retorno = false;
		Date dataDesativacao = fecdesact;
		if (dataDesativacao == null) {
			retorno = false;
		} else {
			Date hoje = UtilDate.parseDateForEASE(UtilDate.getHojeEase());
			if (dataDesativacao.compareTo(hoje) < 0)
				retorno = true;
		}
		return retorno;
	}

	/**
	 * @return the fecbaja as Date
	 */
	public Date getFecbaja() {
		return fecbaja;
	}

	/**
	 * 
	 * @return tehe fecbaja in Control-SA format
	 */
	public String getFecbajaForControlSA() {
		String result = null;
		if (fecbaja != null && !fecbaja.equals(""))
			result = UtilDate.formatDateControlSA(fecbaja);
		return result;
	}

	/**
	 * @return the fecbaja in EASE format
	 */
	public String getFecbajaForEASE() {
		String result = null;
		if (fecbaja != null && !fecbaja.equals(""))
			result = UtilDate.formatDateEase(fecbaja);
		return result;
	}

	/**
	 * @param fecbaja
	 *            the fecbaja to set
	 */
	public void setFecbaja(Date fecbaja) {
		this.fecbaja = fecbaja;
	}

	public String getUsubajac() {
		return usubajac;
	}

	public void setUsubajac(String usubajac) {
		this.usubajac = usubajac;
	}

}