package br.com.pbti.executa;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

import javax.swing.plaf.synth.SynthSeparatorUI;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import br.com.pbti.arquivoUnico.Arquivao;
import br.com.pbti.arquivoUnico.CriaArquivoUnico;
import br.com.pbti.dto.DeleteSistema;
import br.com.pbti.dto.arquivo.DeleteArquivo;
import br.com.pbti.dto.banco.LimpaBanco;
import br.com.pbti.dto.transfere.mip04;
import br.com.pbti.dto.transfere.mip06;
import br.com.pbti.dto.transfere.trataAtributo.AtributoGrupo;
import br.com.pbti.dto.transfere.trataAtributo.DeletaNomeEmBranco;
import br.com.pbti.dto.transfere.trataAtributo.NomeAplicativo;
import br.com.pbti.dto.transfere.trataAtributo.Owner;
import br.com.pbti.manipulador.properties.CriarArquivoProperties;
import br.com.pbti.perfil.principal.XmlConfigPerfil;
import br.com.pbti.sistema.principal.XmlConfig;

public class Menu {

	public static int opcao;

	public static void menuInicial() throws ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException, IOException {
		Scanner ler = new Scanner(System.in);

		while (opcao != 10) {
			System.out.printf("======Bem Vindo ao Bundle XML IIQ=====\n");
			System.out.printf("======================================\n");
			System.out.printf("1 - Configurar Arquivo Properties\n");
			System.out.printf("2 - Alimentar Tabela MIP 04 do Banco de Dados\n");
			System.out.printf("3 - Criar XML Perfil\n");
			System.out.printf("4 - Criar XML Sistema\n");
			System.out.printf("5 - Criar XML Unico\n");
			System.out.printf("6 - Rodar item 2 ao 6\n");
			System.out.printf("7 - Deleta Sistema MIP_04 e MIP_06\n");
			System.out.printf("10 - Sair\n");
			System.out.printf("=> ");
			opcao = ler.nextInt();
			System.out.printf("=======================================\n");

			switch (opcao) {
			case 1: {
				try {

					CriarArquivoProperties criarArquivoProperties = new CriarArquivoProperties();

					criarArquivoProperties.criarConfiguracoes();

				} catch (IOException e) {
					System.out.println("Erro na Criacao do Arquivo");
					e.printStackTrace();
				}

				System.out.printf("=======================================\n");

				break;
			}

			case 2: {
				try {
					System.out.println("Inicio");

					LimpaBanco limpabanco = new LimpaBanco();

					limpabanco.deleteDados();

					mip04 transfereMip04 = new mip04();

					transfereMip04.lerMIP04();

					AtributoGrupo atributoGrupo = new AtributoGrupo();

					atributoGrupo.novoAtributoGrupo();

					NomeAplicativo nomeAplicativo = new NomeAplicativo();

					nomeAplicativo.novoAtributoGrupo();

					Owner woner = new Owner();

					woner.novoAtributoGrupo();

					DeletaNomeEmBranco deletaNomeEmBranco = new DeletaNomeEmBranco();

					deletaNomeEmBranco.selectNomeBranco();

				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (TransformerFactoryConfigurationError e) {
					e.printStackTrace();
				} catch (TransformerException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}

				System.out.printf("=======================================\n");

				break;
			}

			case 3: {

				int opcaoSubMenu;

				Scanner lerSubMenu = new Scanner(System.in);

				System.out.printf("1 - Todos Sistemas\n");
				System.out.printf("2 - Sistemas\n");
				System.out.printf("3 - Todos Sistemas apos sistema especificado\n");
				System.out.printf("4 - Varios Sistemas separados por virgula(SISTEMA1,SISTEMA2)\n");
				System.out.printf("=> ");
				opcaoSubMenu = lerSubMenu.nextInt();

				XmlConfigPerfil xmlConfigPerfil = new XmlConfigPerfil();

				switch (opcaoSubMenu) {
				case 1: {

					xmlConfigPerfil.trataDados("*", "*");

					break;
				}

				case 2: {

					Scanner lerSistema = new Scanner(System.in);

					System.out.printf("Digite o Sistemas\n");
					System.out.printf("=> ");
					String sistem = lerSistema.next();
					xmlConfigPerfil.trataDados(sistem, "*");

					break;

				}

				case 3: {

					Scanner lerSistema = new Scanner(System.in);

					System.out.printf("Digite o Sistemas\n");
					System.out.printf("=> ");
					String sistem = lerSistema.next();
					xmlConfigPerfil.trataDados(sistem, ">");

					break;

				}

				case 4: {

					Scanner lerSistema = new Scanner(System.in);

					System.out.printf("Digite os Sistemas\n");
					System.out.printf("=> ");
					String sistem = lerSistema.nextLine();

					String[] sepSistema = sistem.split(",");

					for (String sistema : sepSistema) {
						sistema = sistema.trim();
						xmlConfigPerfil.trataDados(sistema, "*");

					}

					break;

				}

				}

				System.out.println("Inicio");

				System.out.printf("=======================================\n");

				break;
			}

			case 4: {

				System.out.println("Inicio");

				try {

					XmlConfig xmlConfigSistema = new XmlConfig();

					xmlConfigSistema.trataDados();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (TransformerException e) {
					e.printStackTrace();
				}

				System.out.printf("=======================================\n");
				break;
			}

			case 5: {

				System.out.println("Inicio");

				CriaArquivoUnico arquivoUnico = new CriaArquivoUnico();

				arquivoUnico.arquivoUnico();

				Arquivao arquivao = new Arquivao();

				arquivao.criarArquivao();

				System.out.println("Passei do Arquivão");

				System.out.printf("=======================================\n");

				break;
			}

			case 6: {

				System.out.printf("==INICIO==\n");

				try {

					System.out.println("Inicio");

					LimpaBanco limpabanco = new LimpaBanco();

					limpabanco.deleteDados();

					mip04 transfereMip04 = new mip04();

					transfereMip04.lerMIP04();

					AtributoGrupo atributoGrupo = new AtributoGrupo();

					atributoGrupo.novoAtributoGrupo();

					NomeAplicativo nomeAplicativo = new NomeAplicativo();

					nomeAplicativo.novoAtributoGrupo();

					Owner woner = new Owner();

					woner.novoAtributoGrupo();

					DeletaNomeEmBranco deletaNomeEmBranco = new DeletaNomeEmBranco();

					deletaNomeEmBranco.selectNomeBranco();

					System.out.printf("===Limpa Pasta XML===\n");

					DeleteArquivo del = new DeleteArquivo();

					del.deletarArq();

					System.out.printf("===Gerando Perfil===\n");

					XmlConfigPerfil xmlConfigPerfil = new XmlConfigPerfil();

					xmlConfigPerfil.trataDados("*", "*");

					System.out.printf("===Gerando Sistema===\n");

					XmlConfig xmlConfigSistema = new XmlConfig();

					xmlConfigSistema.trataDados();

					System.out.printf("===Gerando Arquivo Unico===\n");

					CriaArquivoUnico arquivoUnico = new CriaArquivoUnico();

					arquivoUnico.arquivoUnico();

					Arquivao arquivao = new Arquivao();

					arquivao.criarArquivao();

				} catch (Exception e) {
					System.out.println(e);
				}

				System.out.printf("=======================================\n");

				break;
			}

			case 7: {

				Scanner lerSistema = new Scanner(System.in);

				System.out.printf("Digite o Sistemas\n");
				System.out.printf("=> ");
				String sistem = lerSistema.next();

				System.out.printf("Tem certeza que deseja deletar o sitema " + sistem + "\n");

				Scanner lerSubMenu = new Scanner(System.in);

				System.out.printf("1 - Sim quero deletar o sistema " + sistem + "\n");
				System.out.printf("2 - Não quero deletar o sistema " + sistem + "\n");
				System.out.printf("=> ");
				int opcaoSubMenu = lerSubMenu.nextInt();

				switch (opcaoSubMenu) {
				case 1: {

					DeleteSistema delSis = new DeleteSistema();

					delSis.deleteSis(sistem);

					System.out.printf("Sistema " + sistem + " deletado\n");

					break;
				}

				case 2: {

					System.out.printf("Sistema " + sistem + " não deletado\n");

					break;

				}

				}

				break;
			}

			default: {
				break;
			}

			}
		}
	}

	public static void main(String[] args) throws IOException, ParserConfigurationException,
			TransformerFactoryConfigurationError, TransformerException {

		menuInicial();
	}
}
