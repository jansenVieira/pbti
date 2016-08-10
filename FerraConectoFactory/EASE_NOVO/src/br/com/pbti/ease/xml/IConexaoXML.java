package br.com.pbti.ease.xml;
/**
 * Constantes para manipulação do retorno XML dos serviços de Conexao 
 * (relação entre Usuarios e Perfil)
 * Elemento: <Registro> 
 * Transação: SGCLPER/SGCOPER
 * Operação: view/select
 * Função: CL/CO
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