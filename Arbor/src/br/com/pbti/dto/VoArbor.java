package br.com.pbti.dto;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

public class VoArbor {

	private Date dtEntrada;
	private String arquivo;
	private Date dtEntradaCritica;
	private Date dataProcessamento;
	private long codCritica;
	private long miuDispStatus;
	private long miuDispCod;
	private long totalRegistro;
	private long quantidade;
	private long noBil;
	private long dataNumero;
	private Date dataMM7;
	private String tipo;

	public Logger logger = Logger.getLogger(VoArbor.class);

	public long getNoBil() {
		return noBil;
	}

	public void setNoBil(long noBil) {
		this.noBil = noBil;
	}

	public Date getDataProcessamento() {
		return dataProcessamento;
	}

	public void setDataProcessamento(Date dataProcessamento) {
		this.dataProcessamento = dataProcessamento;
	}

	public Date getDtEntrada() {
		return dtEntrada;
	}

	public void setDtEntrada(Date dtEntrada) {
		this.dtEntrada = dtEntrada;
	}

	public String getArquivo() {
		return arquivo;
	}

	public void setArquivo(String arquivo) {

		String[] arrNome = arquivo.split("_");

		String sepLetras = arrNome[1].replaceAll("\\d*", "");

		setTipo(sepLetras.toUpperCase());

		try {
			setDataNumero(Long.parseLong(arrNome[2].toString()));
		} catch (NumberFormatException e) {
			setDataNumero(Long.parseLong(arrNome[3].toString()));
		}

				
		this.arquivo = arquivo.toUpperCase();
	}

	public long getDataNumero() {
		return dataNumero;
	}

	public void setDataNumero(long dataNumero) {

		try {

			DateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmss");
			java.util.Date datUtil = formatter.parse(Long.toString(dataNumero));
			Date dataSql = new Date(datUtil.getTime());
			setDataMM7(dataSql);

		} catch (ParseException e) {
			logger.debug("Erro ao converter data. " + e);
		}

		this.dataNumero = dataNumero;
	}

	public Date getDataMM7() {
		return dataMM7;
	}

	public void setDataMM7(Date dataMM7) {
		this.dataMM7 = dataMM7;
	}

	public long getCodCritica() {
		return codCritica;
	}

	public void setCodCritica(long codCritica) {
		this.codCritica = codCritica;
	}

	public long getMiuDispStatus() {
		return miuDispStatus;
	}

	public void setMiuDispStatus(long miuDispStatus) {
		this.miuDispStatus = miuDispStatus;
	}

	public long getMiuDispCod() {
		return miuDispCod;
	}

	public void setMiuDispCod(long miuDispCod) {
		this.miuDispCod = miuDispCod;
	}

	public long getTotalRegistro() {
		return totalRegistro;
	}

	public void setTotalRegistro(long totalRegistro) {
		this.totalRegistro = totalRegistro;
	}

	public long getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(long quantidade) {
		this.quantidade = quantidade;
	}

	public Date getDtEntradaCritica() {
		return dtEntradaCritica;
	}

	public void setDtEntradaCritica(Date dtEntradaCritica) {
		this.dtEntradaCritica = dtEntradaCritica;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

}
