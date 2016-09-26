package br.com.pbti.dto;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

public class VoArborFaturamento {

	private Date data;
	private long dataNumero;
	private String tipo;
	private long arquivos;
	private long cdrs;

	public Logger logger = Logger.getLogger(VoArborFaturamento.class);
	
	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		
		try {

			DateFormat formatter = new SimpleDateFormat("yy-MM-dd");
			java.util.Date datUtil = formatter.parse(data.toString());

			DateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
			String dat = format.format(datUtil);
			
			setDataNumero(Long.parseLong(dat));

		} catch (ParseException e) {
			logger.debug("Erro ao converter data. " + e);
		}
		
		this.data = data;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public long getArquivos() {
		return arquivos;
	}

	public void setArquivos(long arquivos) {
		this.arquivos = arquivos;
	}

	public long getCdrs() {
		return cdrs;
	}

	public void setCdrs(long cdrs) {
		this.cdrs = cdrs;
	}

	public long getDataNumero() {
		return dataNumero;
	}

	public void setDataNumero(long dataNumero) {
		this.dataNumero = dataNumero;
	}
}
