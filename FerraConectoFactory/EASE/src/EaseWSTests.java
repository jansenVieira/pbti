//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//
//import to.AccountTO;
//import to.ConnectionTO;
//import to.GroupTO;
//import util.UtilDate;
//import br.com.dba.ease.EaseWSClientFacade;
//import br.com.dba.ease.xml.EaseWSRetorno;
//
///**
// * Testes realizados nos serviços disponibilizados pelo EASE/SAT
// * 
// * @author Michael Alves Lins <malins@dba.com.br>
// */
//public class EaseWSTests {
//
//    private static final String _GRUPO_SIGAL_HOMOLOGACAO_ = "[0001,0003]";
////    private static final String _USUARIO_SIGAL_HOMOLOGACAO_ = "gal ease";// DES
////    private static final String _USUARIO_SIGAL_HOMOLOGACAO_ = "gal ease"; // COD
//    private static final String _USUARIO_SIGAL_HOMOLOGACAO_ = "C441306";
//    
//    /**
//     * Casos de teste para os metodos de interacao com o webservice do EASE
//     * 
//     * @param args
//     * @throws Exception
//     */
//    public static void main ( String [] args ) throws Exception {
//
//        /**
//         * DES
//         */
//        String wsdl =  "http://10.192.228.181:22080/axis2/services/SAT?wsdl"; // DES
//        String login = "gal easel";//XSA_Framework.XSA_ReadParam("WS_EASE_USUARIO");
//        String password = "administ";//XSA_Framework.XSA_ReadParam("WS_EASE_SENHA");
//
//        /**
//         * COD
//         */
////        String wsdl = "http://10.192.228.180:12080/WebServiceSAT/services/SAT?wsdl"; // COD alechner/alechner
////        String login = "WSTESTE";//XSA_Framework.XSA_ReadParam("WS_EASE_USUARIO");
////        String password = "WSTESTE";//XSA_Framework.XSA_ReadParam("WS_EASE_SENHA");
//
////        String wsdl = "http://10.192.160.205:12080/axis2/services/SAT?wsdl"; // COD alechner/alechner
////        String login = "TSTCEDES";//XSA_Framework.XSA_ReadParam("WS_EASE_USUARIO");
////        String password = "TSTCEDES";//XSA_Framework.XSA_ReadParam("WS_EASE_SENHA");
////        
//        EaseWSClientFacade.init( login, password, wsdl );
//
//        /**
//         * Todos os testes sao realizados com o usuario _USUARIO_SIGAL_HOMOLOGACAO_
//         * Caso nao exista, criar um usuario e colocar seu login em _USUARIO_SIGAL_HOMOLOGACAO_
//         * 
//         * Os testes com perfil utilizam o perfil _GRUPO_SIGAL_HOMOLOGACAO_
//         * Caso nao exista, criar perfil e colocar seu codigo em _GRUPO_SIGAL_HOMOLOGACAO_
//         * 
//         * EaseWSTests.testIncluirUsuario() e' o u'nico metodo que nao utiliza o usuario citado.
//         * A cada chamada ele gera um login dinamicamente para inclusao, conforme abaixo:
//         * >> "SIGAL"+ loginDay, onde loginDay = UtilDate.formatDate( 
//         *       Calendar.getInstance().getTime()
//         *      ,"S" )
//         *      
//         *  Alterado 18/03/2013 - Ajustes conforme alteracoes no webservice
//         *  Alteracoes realizadas em conjunto com equipe Tecnocom/Indra (María) em COD
//         */
////        EaseWSTests.testConsultarUsuario( true );            // TEST OK // [OK] Ajuste com Tecnocom (Maria)               // [ok] 21/05/2013 - Testado
////        EaseWSTests.testConsultarUsuarios( true );           // TEST OK // [PENDENTE] Ajuste com Tecnocom (Maria)         // [ok] 21/05/2013 - Testado
////        EaseWSTests.testConsultarPerfis( true );             // TEST OK // [OK] Ajuste com Tecnocom (Maria)               // [ok] 21/05/2013 - Testado
////        EaseWSTests.testConsultarPerfil( true );             // TEST OK // [OK] Ajuste com Tecnocom (Maria)               // [ok] 21/05/2013 - Testado
////        EaseWSTests.testVinculaUsuarioPerfil( true );        // TEST OK // [OK] Ajuste com Tecnocom (Maria)               // [ok] 21/05/2013 - Testado
////        EaseWSTests.testDesvinculaUsuarioPerfil( true );     // TEST OK // [OK] Ajuste com Tecnocom (Maria)               // [ok] 21/05/2013 - Testado
////        EaseWSTests.testConsultarUsuariosPorPerfil( true );  // Test OK // [OK] Ajuste com Tecnocom (Maria)               // [ok] 21/05/2013 - Testado
////        EaseWSTests.testDesabilitarUsuario( true );          // TEST OK // [OK] Ajuste com Tecnocom (Maria)               // [ok] 21/05/2013 - Testado
////        EaseWSTests.testHabilitarUsuario( true );            // TEST OK // [OK] Ajuste com Tecnocom (María)               // [ok] 21/05/2013 - Testado
////        EaseWSTests.testNewPassword( true );                 // TEST OK // [OK] Ajuste com Tecnocom (Maria)               // [ok] 21/05/2013 - Testado
////        EaseWSTests.testConsultarUsuariosPorPerfis( true );  // Test OK // [PENDENTE] Ajuste com Tecnocom (Maria)         // [ok] 21/05/2013 - Testado
//        EaseWSTests.testIncluirUsuario( true );              // TEST OK (SEM PERFIL) // [OK] Ajuste com Tecnocom (Maria)  // [ok] 21/05/2013 - Testado
////        EaseWSTests.testExcluirUsuario( true );              // TEST OK // [OK] Ajuste com Tecnocom (Maria)               // [ok] 21/05/2013 - Testado
////        EaseWSTests.testIncluirUsuarioExcluido( true );      // TEST OK (SEM PERFIL) // [OK] Ajuste com Tecnocom (Maria)  // [ok] 21/05/2013 - Testado
//    }
//
//    /**
//     * Apresenta o resultado do teste
//     * 
//     * @param parser
//     */
//    private static void testResult( EaseWSRetorno wsRetorno, String testName ) {
//        testResult( wsRetorno, testName, null );
//    }
//    private static void testResult( EaseWSRetorno wsRetorno, String testName
//            , String exceptionMessage ) {
//        // Imprime os dados de retorno
//        System.out.println( "==XML==" );
//        System.out.println( "XML: "+ wsRetorno.getXml() );
//        System.out.println( "==Retorno==" );
//        System.out.println( "Codigo: "+ wsRetorno.getCodigo() );
//        System.out.println( "Mensagem: "+ wsRetorno.getMensagem() );
//        System.out.println( "Tempo de execucao: "+ wsRetorno.getTempoExecucao() );
//
//        // Resultado do teste
//        if ( !wsRetorno.possuiErro() ) {
//            if ( exceptionMessage == null ) {
//                System.out.println( "===========" );
//                System.out.println( "TEST PASSED >> "+ testName );
//                System.out.printf( "===========\n\n" );
//            } else {
//                System.out.println( "========================" );
//                System.out.println( "TEST PASSED WITH MESSAGE >> "+
//                        testName +" {"+ exceptionMessage +"} >> " );
//                System.out.printf( "========================\n\n" );
//            }
//        } else {
//            System.out.println( "===========" );
//            System.out.println( "TEST FAILED >> "+ testName );
//            System.out.printf( "===========\n\n" );
//            
//        }
//        
//    }
//
//    /**
//     * Testa desabilitacao/desativacao de usuario na base do EASE utilizando metodo da Facade
//     * 
//     * @param debugData
//     */
//    private static void testDesabilitarUsuario( boolean debugData ) {
//        // Apenas para fins de debug dos dados enviados
//        Calendar hoje = Calendar.getInstance();
//        hoje.add( Calendar.DATE , -1 );
//        String dataDesabilitacao = UtilDate.formatDate( 
//                 hoje.getTime()
//                ,UtilDate.PATTERN_DATE_EASE
//                );
//        try { 
//            EaseWSRetorno wsRetorno = EaseWSClientFacade.getInstance().desabilitarUsuario(
//                     _USUARIO_SIGAL_HOMOLOGACAO_
//                    );
//        
//            if ( debugData ) {
//                System.out.println( "==DesabilitarUsuario/Dados Enviados==" );
//                System.out.println( "Usuario: "+ _USUARIO_SIGAL_HOMOLOGACAO_ );
//                System.out.println( "Data de desativacao: "+ dataDesabilitacao );
//            }
//
//            testResult( wsRetorno, "testDesabilitarUsuario");
//            
//        } catch ( java.lang.Exception ex ) {
//            System.out.println( "Erro: "+ ex.getMessage() );
//        }
//    }
//
//    /**
//     * Testa habilitacao/ativacao de usuario na base do EASE utilizando metodo da Facade
//     * 
//     * @param debugData
//     */
//    private static void testHabilitarUsuario( boolean debugData ) {
//        String fecdesact = UtilDate.getDataAtivacaoDefaultForEASE();
//        try { 
//            EaseWSRetorno wsRetorno = EaseWSClientFacade.getInstance().habilitarUsuario(
//                    _USUARIO_SIGAL_HOMOLOGACAO_
//                    ,fecdesact
//                    );
//
//            if ( debugData ) {
//                System.out.println( "==HabilitarUsuario/Dados Enviados==" );
//                System.out.println( "Usuario: "+ _USUARIO_SIGAL_HOMOLOGACAO_ );
//                System.out.println( "Data de desativacao: "+ fecdesact );
//            }
//
//            testResult( wsRetorno, "testHabilitarUsuario" );
//
//        } catch ( java.lang.Exception ex ) {
//            System.out.println( "Erro: "+ ex.getMessage() );
//        }
//    }
//    
//    /**
//     * Testa vinculacao entre usuario e determinado perfil na base do EASE 
//     * utilizando metodo da Facade
//     * 
//     * @param debugData
//     */
//    private static void testVinculaUsuarioPerfil( boolean debugData ) {
//        try { 
//            EaseWSRetorno wsRetorno = EaseWSClientFacade.getInstance().vinculaUsuarioPerfil(
//                     _USUARIO_SIGAL_HOMOLOGACAO_
//                    ,_GRUPO_SIGAL_HOMOLOGACAO_ 
//                    );
//
//            if ( debugData ) {
//                System.out.println( "==VinculaUsuarioPeril/Dados Enviados==" );
//                System.out.println( "Usuario: "+ _USUARIO_SIGAL_HOMOLOGACAO_ );
//                System.out.println( "Perfil: "+ _GRUPO_SIGAL_HOMOLOGACAO_ );
//            }
//            
//            testResult( wsRetorno, "testVinculaUsuarioPerfil" );
//
//        } catch ( java.lang.Exception ex ) {
//            System.out.println( "Erro: "+ ex.getMessage() );
//        }
//    }
//    
//    /**
//     * Testa desvinculacao de usuario a determinado perfil na base do EASE 
//     * utilizando metodo da Facade
//     * 
//     * @param debugData
//     */
//    private static void testDesvinculaUsuarioPerfil( boolean debugData ) {
//        try {
//            EaseWSRetorno wsRetorno = EaseWSClientFacade.getInstance().desvinculaUsuarioPerfil(
//                    _USUARIO_SIGAL_HOMOLOGACAO_
//                    );
//            if ( debugData ) {
//                System.out.println( "==DesvinculaUsuarioPeril/Dados Enviados==" );
//                System.out.println( "Usuario: "+ _USUARIO_SIGAL_HOMOLOGACAO_ );
//            }
//            
//            testResult( wsRetorno, "testDesvinculaUsuarioPerfil" );
//            
//        } catch ( java.lang.Exception ex ) {
//            System.out.println( "Erro: "+ ex.getMessage() );
//        }
//    }
//
//    /**
//     * Testa atribuicao de nova senha a usuario na base do EASE utilizando metodo da Facade
//     * @param debugData
//     * @return
//     */
//    private static String testNewPassword( boolean debugData ) {
//        String newPwd = "WSINDRA1";
//        
//        try {
//            EaseWSRetorno wsRetorno = 
//                    EaseWSClientFacade.getInstance().setNewPassword(
//                     _USUARIO_SIGAL_HOMOLOGACAO_ // Usuario
//                    ,newPwd  // Nova senha
//                    );
//
//            if ( debugData ) {
//                System.out.println( "==NewPassword/Dados Enviados==" );
//                System.out.println( "Usuario: "+ _USUARIO_SIGAL_HOMOLOGACAO_ );
//                System.out.println( "Nova Senha: "+ newPwd );
//            }
//
//            testResult( wsRetorno, "testNewPassword" );
//            
//        } catch ( java.lang.Exception ex ) {
//            System.out.println( "Erro: "+ ex.getMessage() );
//        }
//        return newPwd;
//    }
//   
//    /**
//     * Testa inclusao de usuario na base do EASE utilizando metodo da Facade
//     * 
//     * @param debugData True para apresentar os dados utilizados no teste
//     */
//    private static void testIncluirUsuario( boolean debugData ) {
//
//        String loginDay = UtilDate.formatDate( 
//                Calendar.getInstance().getTime()
//               ,"S"
//               );
//
//        try {
//            EaseWSClientFacade easeWSDL = EaseWSClientFacade.getInstance();
//            
//            AccountTO usuario = new AccountTO();
//            usuario.setUsuario( _USUARIO_SIGAL_HOMOLOGACAO_ );// Qual tamanho desse campo? [8] Deve sempre ser maiusculo? Não
//            usuario.setCodperfil( _GRUPO_SIGAL_HOMOLOGACAO_ ); //CAMPO OPICIONAL, alteração feita na especificação v. 5.0
//            usuario.setNombreusu( "Abas" );// Qual tamanho desse campo? [20] Deve sempre ser maiusculo? [Nao, pode ser minusculo]
//            usuario.setPassword( "sigalease" );
//            usuario.setNivsegusu( "0" );
//            usuario.setCodidioma( "PO" );
//            usuario.setFecactiva( Calendar.getInstance().getTime() );
//            usuario.setFecdesact( UtilDate.getDateInc( 1000 ) );
//            usuario.setCenttra( "" );
//            usuario.setOficina( "0001" );
//            // 18/03/2013 - Incluido conforme nova especificacao webservice INDRA
//            usuario.setMaxSenhasIncorretas("5");
//
//            EaseWSRetorno wsRetorno = easeWSDL.addUsuario(usuario);
//            
//            if ( debugData ) {
//                System.out.println( "==IncluirUsuario/Dados Enviados==" );
//                System.out.println( "Usuario: "+ usuario.getUsuario() );
//                System.out.println( "Password: "+ usuario.getPassword() );
//                System.out.println( "Password(Puro): sigalease" );
//                System.out.println( "CodPerfil: "+ usuario.getCodperfil() );
//                System.out.println( "NombreUsu: "+ usuario.getNombreusu() );
//                System.out.println( "NivSegUsu: "+ usuario.getNivsegusu() );
//                System.out.println( "CodIdioma: "+ usuario.getCodidioma() );
//                System.out.println( "FecActiva: "+ usuario.getFecactivaForEASE() );
//                System.out.println( "FecDesact: "+ usuario.getFecdesactForEASE() );
//                System.out.println( "Centtra: "+ usuario.getCenttra() );
//                System.out.println( "Oficina: "+ usuario.getOficina() );
//            }
//
//            testResult( wsRetorno, "testIncluirUsuario" );
//            
//        } catch ( java.lang.Exception ex ) {
//            System.out.println( "Erro: "+ ex.getMessage() );
//        }
//    }
//    
//    /**
//     * Testa inclusao de usuario ja excluido na base do EASE utilizando metodo da Facade
//     * 
//     * @param debugData True para apresentar os dados utilizados no teste
//     */
//    private static void testIncluirUsuarioExcluido( boolean debugData ) {
//        try {
//            EaseWSClientFacade easeWSDL = EaseWSClientFacade.getInstance();
//            
//            AccountTO usuario = new AccountTO();
//            usuario.setUsuario( _USUARIO_SIGAL_HOMOLOGACAO_ ); 
//            usuario.setCodperfil( _GRUPO_SIGAL_HOMOLOGACAO_ ); //CAMPO OPICIONAL, alteração feita na especificação v. 5.0
//            usuario.setNombreusu( "blablabla" );
//            usuario.setPassword( "sigalease" );
//            usuario.setNivsegusu( "0" );
//            usuario.setCodidioma( "ES" );
//            usuario.setFecactiva( Calendar.getInstance().getTime() );
//            usuario.setFecdesact( UtilDate.getDateInc( 1000 ) );
//            usuario.setCenttra( "" );
//            usuario.setOficina( "0002" );
//            usuario.setMaxSenhasIncorretas("5");
//
//            EaseWSRetorno wsRetorno = easeWSDL.addUsuario( usuario );
//
//            if ( !wsRetorno.possuiErro() ) {
//
//                EaseWSRetorno wsUsuarioReativado = easeWSDL.getUsuario( usuario.getUsuario() );
//                usuario = easeWSDL.populateAccounts( wsUsuarioReativado.getXml() ).get(0);
//            }
//
//            if ( debugData ) {
//                System.out.println( "==IncluirUsuario/Dados Enviados==" );
//                System.out.println( "Usuario: "+ usuario.getUsuario() );
//                System.out.println( "Password: "+ usuario.getPassword() );
//                System.out.println( "CodPerfil: "+ usuario.getCodperfil() );
//                System.out.println( "NombreUsu: "+ usuario.getNombreusu() );
//                System.out.println( "NivSegUsu: "+ usuario.getNivsegusu() );
//                System.out.println( "CodIdioma: "+ usuario.getCodidioma() );
//                System.out.println( "FecActiva: "+ usuario.getFecactivaForEASE() );
//                System.out.println( "FecDesact: "+ usuario.getFecdesactForEASE() );
//                System.out.println( "Centtra: "+ usuario.getCenttra() );
//                System.out.println( "Oficina: "+ usuario.getOficina() );
//                System.out.println( "Fecbaja: "+ usuario.getFecbajaForEASE() );
//            }
//
//            testResult( wsRetorno, "testIncluirUsuarioExcluido" );
//            
//        } catch ( java.lang.Exception ex ) {
//            System.out.println( "Erro: "+ ex.getMessage() );
//        }
//    }
//    
//    /**
//     * Testa exclusao de usuario da base do EASE utilizando metodo da Facade
//     * @param debugData
//     */
//    private static void testExcluirUsuario( boolean debugData ) {
//        try {
//            EaseWSRetorno wsRetorno = EaseWSClientFacade.getInstance().excluirUsuario( _USUARIO_SIGAL_HOMOLOGACAO_ );
//            if ( debugData ) {
//                System.out.println( "==ExcluirUsuario/Dados Enviados==" );
//                System.out.println( "Usuario: "+ _USUARIO_SIGAL_HOMOLOGACAO_ );
//            }
//            testResult( wsRetorno, "testExcluirUsuario" );
//        } catch ( java.lang.Exception ex ) {
//            System.out.println( "Erro: "+ ex.getMessage() );
//        }
//    }
//    
//    /**
//     * Testa consulta de todos os usuarios da base do EASE utilizando metodo da Facade
//     * @param debugData
//     */
//    private static void testConsultarUsuarios( boolean debugData ) {
//        try {
//            EaseWSRetorno wsRetorno = EaseWSClientFacade.getInstance().getUsuarios();
//
//            System.out.println(wsRetorno.getXml());
//            
//            if ( debugData ) {
//                System.out.println( "==Usuarios==" );
//                List<AccountTO> listAccounts = EaseWSClientFacade.getInstance().populateAccounts( wsRetorno.getXml() );  
//
//                for ( int i = 0; i < listAccounts.size(); i++ ) {
//                    System.out.printf( "--- Inicio Usuario %d ---\n", i+1 );
//                    AccountTO account = listAccounts.get( i );
//                    System.out.println( "Usuario: "+ account.getUsuario() );
//                    System.out.println( "Password: "+ account.getPassword() );
//                    System.out.println( "CodPerfil: "+ account.getCodperfil() );
//                    System.out.println( "DesPerfil: "+ account.getDesperfil() );
//                    System.out.println( "NombreUsu: "+ account.getNombreusu() );
//                    System.out.println( "NivSegUsu: "+ account.getNivsegusu() );
//                    System.out.println( "CodIdioma: "+ account.getCodidioma() );
//                    System.out.println( "FecActiva: "+ account.getFecactivaForEASE() );
//                    System.out.println( "FecDesact: "+ account.getFecdesactForEASE() );
//                    System.out.println( "Centtra: "+ account.getCenttra() );
//                    System.out.println( "Oficina: "+ account.getOficina() );
//                    System.out.println( "Fecbaja: "+ account.getFecbajaForEASE() );
//                    System.out.printf( "--- Fim Usuario %d ---\n\n", i+1 );
//                }
//
//                System.out.printf( "------------\n Total de Usuarios: %d \n------------\n\n", listAccounts.size() );
//            }
//            
//            testResult( wsRetorno, "testConcultarUsuarios");
//            
//        } catch ( java.lang.Exception ex ) {
////            System.out.println( "Erro: "+ ex.getMessage() );
//        }
//    }
//    
//    /**
//     * Nesta consulta de um unico usuario da base do EASE utilizando metodo da Facade
//     * @param debugData
//     */
//    private static void testConsultarUsuario( boolean debugData ) {
//        try {
//            EaseWSRetorno wsRetorno = EaseWSClientFacade.getInstance().getUsuario( _USUARIO_SIGAL_HOMOLOGACAO_ );
//            String exceptionMessage = null;
//
//            System.out.println(wsRetorno.getXml());
//            
//            if ( debugData ) {
//                System.out.println( "==Usuarios==" );
//                
//                List<AccountTO> listAccounts = new ArrayList<AccountTO>();
//                
//                String MSG_USUARIO_INEXISTENTE = "MPE0269"; // MPE0269 - USUARIO OU SENHA INCORRETO
//                if ( wsRetorno.possuiErro() && wsRetorno.getMensagem().contains( MSG_USUARIO_INEXISTENTE ) ) {
//
//                    // Nao existindo, retorna acao sem erro
//                    System.out.println( "Usuario nao encontrado na base de dados do EASE (Mensagem SIGAL)" );
//
//                } else {
//                
//                    listAccounts = EaseWSClientFacade.getInstance().populateAccounts( wsRetorno.getXml() );  
//                    
//                    for ( int i = 0; i < listAccounts.size(); i++ ) {
//                        System.out.printf( "--- Inicio Usuario %d ---\n", i+1 );
//                        AccountTO account = listAccounts.get( i );
//                        System.out.println( "Usuario: "+ account.getUsuario() );
//                        System.out.println( "Password: "+ account.getPassword() );
//                        System.out.println( "CodPerfil: "+ account.getCodperfil() );
//                        System.out.println( "DesPerfil: "+ account.getDesperfil() );
//                        System.out.println( "NombreUsu: "+ account.getNombreusu() );
//                        System.out.println( "NivSegUsu: "+ account.getNivsegusu() );
//                        System.out.println( "CodIdioma: "+ account.getCodidioma() );
//                        System.out.println( "FecActiva: "+ account.getFecactivaForEASE() );
//                        System.out.println( "FecDesact: "+ account.getFecdesactForEASE() );
//                        System.out.println( "Centtra: "+ account.getCenttra() );
//                        System.out.println( "Oficina: "+ account.getOficina() );
//                        System.out.println( "Fecbaja: "+ account.getFecbajaForEASE() );
//                        System.out.println( "Tentativa: "+ account.getMaxSenhasIncorretas() );
//                        System.out.println("Usabajac: "+ account.getUsubajac());
//                       
//                        System.out.printf( "--- Fim Usuario %d ---\n\n", i+1 );
//                    }
//                }
//
//                System.out.printf( "------------\n Total de Usuarios: %d \n------------\n\n", listAccounts.size() );
//            }
//
//            testResult( wsRetorno, "testConcultarUsuario", exceptionMessage);
//
//        } catch ( java.lang.Exception ex ) {
//            System.out.println( "Erro: "+ ex.getMessage() );
//        }
//    }
//
//    /**
//     * Testa consulta de todos os usuarios de determinado perfil da base 
//     * do EASE utilizando metodo da Facade
//     * 
//     * @param debugData
//     */
//    private static void testConsultarUsuariosPorPerfil( boolean debugData ) {
//        try {
//            // Perfil de acesso do SIGAL ao SAT pelo WebService do EASE
//            EaseWSRetorno wsRetorno = EaseWSClientFacade.getInstance().getUsuariosPorPerfil( _GRUPO_SIGAL_HOMOLOGACAO_ );
//
//            if ( debugData ) {
//                System.out.println( "==Usuarios por Perfil==" );
//                java.util.List<ConnectionTO> listUsuario = EaseWSClientFacade.getInstance().populateConnections( wsRetorno.getXml() );
//
//                for ( int i = 0; i < listUsuario.size(); i++ ) {
//                    ConnectionTO connection = listUsuario.get( i );
//                    System.out.printf( "--- Inicio Usuario %d ---\n", (i+1) );
//                    System.out.println( "Usuario: "+ connection.getUsuarioLogin() );
//                    System.out.println( "DesPerfil: "+ connection.getDesPerfil() );
//                    System.out.println( "CodPerfil: "+ connection.getCodPerfil() );
//                    System.out.println( "FecDesact: "+ connection.getFecDesact() );
//                    System.out.printf( "--- Fim Usuario %d ---\n\n", (i+1) );                    
//                }
//                System.out.printf( "------------\n Total de Usuarios: %d \n------------\n\n", listUsuario.size() );
//            }
//
////            testResult( wsRetorno, "testConcultarUsuariosPorPerfil" );
//
//        } catch ( java.lang.Exception ex ) {
//            System.out.println( "[456] EaseWSTests.testConsultarUsuariosPorPerfil: "+ ex.getMessage() );
//        }        
//    }
//
//    /**
//     * Testa consulta de todos os usuarios de todos os perfis da base 
//     * do EASE utilizando metodo da Facade
//     * 
//     * @param debugData
//     */
//    private static void testConsultarUsuariosPorPerfis( boolean debugData ) {
//        try {
//            // Perfil de acesso do SIGAL ao SAT pelo WebService do EASE
//            EaseWSRetorno wsRetorno = EaseWSClientFacade.getInstance().getUsuariosPorPerfil( "" );
//            
//            if ( debugData ) {
//                System.out.println( "==Usuarios por Perfil==" );
//                java.util.List<ConnectionTO> listUsuario = EaseWSClientFacade.getInstance().populateConnections( wsRetorno.getXml() );
//
//                for ( int i = 0; i < listUsuario.size(); i++ ) {
//                    ConnectionTO connection = listUsuario.get( i );
//                    System.out.printf( "--- Inicio Usuario %d ---\n", (i+1) );
//                    System.out.println( "Usuario: "+ connection.getUsuarioLogin() );
//                    System.out.println( "DesPerfil: "+ connection.getDesPerfil() );
//                    System.out.println( "CodPerfil: "+ connection.getCodPerfil() );
//                    System.out.println( "FecDesact: "+ connection.getFecDesact() );
//                    System.out.printf( "--- Fim Usuario %d ---\n\n", (i+1) );                    
//                }
//                System.out.printf( "------------\n Total de Usuarios: %d \n------------\n\n", listUsuario.size() );
//            }
//
//            testResult( wsRetorno, "testConcultarUsuariosPorPerfil" );
//
//        } catch ( java.lang.Exception ex ) {
//            System.out.println( "[456] EaseWSTests.testConsultarUsuariosPorPerfil: "+ ex.getMessage() );
//        }        
//    }
//    
//    /**
//     * Testa consulta de um unico perfil da base do EASE utilizando metodo da Facade
//     * @param debugData
//     */
//    private static void testConsultarPerfil( boolean debugData ) {
//        try {
//            EaseWSRetorno wsRetorno = EaseWSClientFacade.getInstance().getPerfil(_GRUPO_SIGAL_HOMOLOGACAO_);
//            String exceptionMessage = null;
//
//            if ( debugData ) {
//                System.out.println( "==Perfil==" );
//                List<GroupTO> listGroup = EaseWSClientFacade.getInstance().populateGroups( wsRetorno.getXml() );  
//                
//                if ( listGroup.size() > 0 ) {
//                    GroupTO group = listGroup.get( 0 ); 
//                    System.out.println( "CodPerfil: "+ group.getCodPerfil() );
//                    System.out.println( "DesPerfil: "+ group.getDesPerfil() );
//                }
//            }
//
//            testResult( wsRetorno, "testConsultarPerfil", exceptionMessage);
//
//        } catch ( java.lang.Exception ex ) {
//            System.out.println( "Erro: "+ ex.getMessage() );
//        }
//    }
//
//    /**
//     * Testa consulta de todos os perfis da base do EASE utilizando metodo da Facade
//     * @param debugData
//     */
//    private static void testConsultarPerfis( boolean debugData ) {
//        try {
//            EaseWSRetorno wsRetorno = EaseWSClientFacade.getInstance().getPerfis();
//            String exceptionMessage = null;
//
//            if ( debugData ) {
//                System.out.println( "==Perfis==" );
//                List<GroupTO> listGroup = EaseWSClientFacade.getInstance().populateGroups( wsRetorno.getXml() );  
//                for ( int i = 0; i < listGroup.size(); i++ ) {
//                    GroupTO group = listGroup.get( i ); 
//                    System.out.println( "\nCodPerfil: "+ group.getCodPerfil() );
//                    System.out.println( "DesPerfil: "+ group.getDesPerfil() );
//                }
//                
//                System.out.printf( "------------\n Total de Perfis: %d \n------------\n\n", listGroup.size() );
//            }
//            
//            testResult( wsRetorno, "testConsultarPerfis", exceptionMessage);
//
//        } catch ( java.lang.Exception ex ) {
//            System.out.println( "Erro: "+ ex.getMessage() );
//        }
//    }
//}