package br.com.pbti.principal;

import java.io.FileNotFoundException;
import br.com.pbti.dto.TransObjetos;

public class Executavel {
	
	public static  TransObjetos transObjetos = new TransObjetos();
	
	public static void main(String[] args) throws FileNotFoundException {
		
		transObjetos.abrirCSV();
		
		
	}

}
