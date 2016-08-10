package br.com.pbti.dto.arquivo;

import java.io.File;
import java.io.IOException;

import br.com.pbti.manipulador.properties.LerProperties;

public class DeleteArquivo {

	public static void remover(File f) {
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; ++i) {
				remover(files[i]);
			}
		}
		f.delete();
	}

	public void deletarArq() {
		LerProperties lerproperties = new LerProperties();

		try {
			lerproperties.dadosProperties();
		} catch (IOException e) {
			e.printStackTrace();
		}

		remover(new File(lerproperties.getUrlXml() + "/"));

		File diretorio = new File(lerproperties.getUrlXml());
		diretorio.mkdir();

		diretorio = new File(lerproperties.getUrlXml() + "/XMLUnificado");

		diretorio.mkdir();

	}

}
