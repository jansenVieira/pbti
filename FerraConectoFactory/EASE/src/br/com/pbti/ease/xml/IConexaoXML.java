package br.com.pbti.ease.xml;
/**
 * Constantes para manipula��o do retorno XML dos servi�os de Conexao 
 * (rela��o entre Usuarios e Perfil)
 * Elemento: <Registro> 
 * Transa��o: SGCLPER/SGCOPER
 * Opera��o: view/select
 * Fun��o: CL/CO
 * 
 * @author EaseWSTests Alves Lins <malins@dba.com.br>
 */
public interface IConexaoXML {
    public static final String XML_ELEMENT        = "Registro";
    public static final String CODIGO             = "CODPERFIL";
    public static final String DESCRICAO          = "DESPERFIL";
    public static final String TIPO_PERFIL        = "TIPPERFIL";
    public static final String USUARIO            = "USUARIO";
    public static final String FECDESACT          = "FECDESACT";
}