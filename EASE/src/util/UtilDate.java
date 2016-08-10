package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UtilDate {

    public static final String PATTERN_DATE_CONTROLSA = "yyyyMMddHHmmss";
    public static final String PATTERN_DATE_EASE = "dd-MM-yyyy";
    
    /**
     * Retorna uma data (inc)(dec)rementada, conforme parametro +/-
     * @return
     */
    public static Date getDateInc( int dias ) {
        Calendar dataDesativacao = Calendar.getInstance();
        dataDesativacao.add( Calendar.DATE , dias );
        return dataDesativacao.getTime();
    }
    
    /**
     * Data default do EASE para usuario nao excluidos logicamente
     * @return
     */
    public static Date getFecbajaUsuarioNaoExcluido(){
        return UtilDate.parseDateForControlSA( "00010101000000" );
    }
    
    /**
     * Retorna o dia de hoje no formado EASE de data
     * @return
     */
    public static String getHojeEase() {
        return formatDateEase( Calendar.getInstance().getTime() );
    }
    
    /**
     * Gera uma data de ativacao para aprox. 50 anos (18250 dias)
     * @return
     */
    public static String getDataAtivacaoDefaultForEASE() {
        return formatDateEase( UtilDate.getDateInc( +18250 ) );
    }
    public static Date getDataAtivacaoDefaultForControlSA() {
        return UtilDate.getDateInc( +18250 );
    }

    /**
     * Gera uma data de desativacao para "ontem"
     * @return
     */
    public static String getDataDesativacaoDefault() {
        return formatDateEase( UtilDate.getDateInc( -1 ) );
    }
    
    /**
     * Retorna data formatada conforme padrao EASE
     * return 
     */
    public static String formatDateEase( Date data ) {
        return formatDate( 
                data 
               ,PATTERN_DATE_EASE
          );
    }
    
    /**
     * Retorna data formatada conforme padrao Control-SA
     * return 
     */
    public static String formatDateControlSA( Date data ) {
        return formatDate( 
                data 
               ,PATTERN_DATE_CONTROLSA
          );
    }
    
    public static Date dateFromString ( String dateString, String formatString ) {
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        dateFormat.applyPattern( formatString );
        Date parse = null;
        try {
            parse = dateFormat.parse( dateString );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parse;
    }
    
	public static String formatDate(Date data, String pattern) {
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		String format = "";
		dateFormat.applyPattern( pattern );
		if (data != null) {
			format = dateFormat.format( data );
		}
		return format;
	}

    public static String formatarDataDesativacao(String dataDesativacao) {
    	if ( dataDesativacao != null && !dataDesativacao.equals("") ) {
    		SimpleDateFormat dateFormat = new SimpleDateFormat();
    		dateFormat.applyPattern( PATTERN_DATE_CONTROLSA );
    		Date data = null;
    		try {
    			data = dateFormat.parse( dataDesativacao );
    		} catch (ParseException pe) {
    			pe.printStackTrace();
    		}
    		dataDesativacao = UtilDate.formatDate( data, PATTERN_DATE_EASE );
    	}
    	return dataDesativacao;
    }

    /**
     * Cria uma data a partir de uma string no formato para o EASE 
     * @param dateString
     * @return
     */
    public static Date parseDateForEASE( String dateString ) {
        return UtilDate.dateFromString(dateString, UtilDate.PATTERN_DATE_EASE);
    }

    /**
     * Cria uma data a partir de uma string no formato para o Control-SA 
     * @param dateString
     * @return
     */
    public static Date parseDateForControlSA( String dateString ) {
        return UtilDate.dateFromString(dateString, UtilDate.PATTERN_DATE_CONTROLSA);
    }
}
