package util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Classe de criptografia para atender aos requisitos do sistema EASE/SAT/SICPS
 * As senhas no EASE sao criptografadas em maiusculo (toUpperCase) 
 * Integração entre SIGAL e EASE
 * 
 * 18/03/2013 - Alteracao do algoritmo de hash de MD5 para SHA1 conforme nova
 * especificacao do webservice da INDRA/SAT
 * 
 * @author Michael Alves Lins <malins@dba.com.br>
 */
public class CriptografiaEase {

//    public static void main ( String [] args ) throws Exception {
//        System.out.print( "hash(senha): "+ CriptografiaEase.getInstance().hash( "sigalease".toUpperCase() ) );
//    }

    private static CriptografiaEase _instance;

    private CriptografiaEase() {
    }
    
    public static synchronized CriptografiaEase getInstance() {
        if ( _instance == null )
            _instance = new CriptografiaEase();
        return _instance;
    }

    /**
     * Retorna hash string
     * @param s
     * @return
     * @throws NoSuchAlgorithmException
     */
    public String hash ( String s ) throws Exception {
        return sha1(s);
    }
    
    /**
     * Retorna um hash MD5 sem salt
     * Aplicando o padrao EASE para MD5, tudo em MAIUSCULO
     * 
     * @deprecated Foi substituido o metodo para SHA1
     * @param s
     * @return
     * @throws NoSuchAlgorithmException
     */
    public String md5( String s ) throws NoSuchAlgorithmException  {
        // As senhas no EASE sao criptografadas em maiusculo (toUpperCase)
        byte hash[] = MessageDigest.getInstance("MD5").digest( s.toUpperCase().getBytes() );
        BigInteger bi = new BigInteger( 1, hash );
        String hashString = bi.toString( 16 );
        while ( hashString.length() < 32 )
            hashString = "0"+ hashString;
        return hashString;
    }

    /**
     * Retorna um hash SHA1, segundo nova especificacao do SIPCS
     * 
     * @param s
     * @return
     * @throws Exception
     */
    public String sha1 ( String s ) throws Exception {
        MessageDigest mDigest = MessageDigest.getInstance("SHA-1");
        byte[] result = new byte[40]; 
        mDigest.update( s.getBytes( "iso-8859-1") , 0, s.length() );
        result = mDigest.digest();

        StringBuffer sb = new StringBuffer();

        for ( int j=0; j < result.length; j++) {
            int halfbyte = (result[j] >> 4) & 0x0F;
            int two_halfs = 0;

            do {
                    if ( (0 <= halfbyte) && halfbyte <= 9 )
                        sb.append( (char) ('0' + halfbyte) );
                    else
                        sb.append( (char) ('a' + ( halfbyte-10 ) ) );
                    halfbyte = result[j] & 0x0F;
            } while ( two_halfs++ < 1 );
         }

        return sb.toString();
    }
}