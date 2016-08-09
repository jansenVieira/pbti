package br.com.pbti.dto;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

public class VoMm7 {

	private String nome;
	private long dataNumero;
	private Date data;
	private String tipo;
	private long tamanho;
	private long quantidade;

	public Logger logger = Logger.getLogger(VoMm7.class);
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome.toUpperCase();
	}

	public long getDataNumero() {
		return dataNumero;
	}

	public void setDataNumero(long data) {

		try {
			
			DateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmss");
			java.util.Date datUtil = formatter.parse(Long.toString(data));
			Date dataSql = new Date(datUtil.getTime());
			setData(dataSql);

		} catch (ParseException e) {
			logger.debug("Erro ao converter data. "+e);
		}
		
		this.dataNumero = data;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo.toUpperCase();
	}

	public long getTamanho() {
		return tamanho;
	}

	public void setTamanho(long tamanho) {
		this.tamanho = tamanho;
	}

	public long getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(long quantidade) {
		this.quantidade = quantidade;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}
}
