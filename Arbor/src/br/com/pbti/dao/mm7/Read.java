package br.com.pbti.dao.mm7;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import br.com.pbti.dao.mm7.file.FileTransfers;
import br.com.pbti.dao.property.DataSource;
import br.com.pbti.dto.VoMm7;

public class Read {

	public Logger logger = Logger.getLogger(Read.class);

	public FileTransfers ft = new FileTransfers();
	public LinkedHashSet<File> arrFileSucesso = new LinkedHashSet<File>();
	public LinkedHashSet<File> arrFileFalha = new LinkedHashSet<File>();
	public DataSource ds = new DataSource();

	public Collection<File> fileSeach() {
		Collection<File> files = FileUtils.listFiles(new File(ds.getDownloadFileMM7()), null, true);
		return files;
	}

	public ArrayList<VoMm7> tratafile() throws IOException {

		Collection<File> files = fileSeach();

		ArrayList<VoMm7> arrVo = new ArrayList<VoMm7>();

		logger.debug("Arquivos Encontrados: " + files);

		BufferedReader br = null;

		for (File extFiles : files) {

			FileReader fr;
			try {

				fr = new FileReader(extFiles);

				br = new BufferedReader(fr);

				while (br.ready()) {
					VoMm7 voMm7 = new VoMm7();

					String[] linha = br.readLine().split(";");

					if (linha.length == 3) {
						String[] pgTip = extFiles.getName().split("_");

						voMm7.setTipo(pgTip[1]);

						String[] arrNome = linha[0].split("_");

						try {

							voMm7.setDataNumero(Long.parseLong(arrNome[2].toString()));
						} catch (NumberFormatException e) {
							voMm7.setDataNumero(Long.parseLong(arrNome[3].toString()));
						}

						voMm7.setTamanho(Long.parseLong(linha[1]));

						voMm7.setQuantidade(Long.parseLong(linha[2]));

						voMm7.setNome(linha[0]);

						arrVo.add(voMm7);

					} else {
						logger.error("Arquivos: " + br.readLine() + " com dados incoerentes");
						arrFileFalha.add(extFiles);
						new Exception("Linha com dados incoerentes");
					}
				}

			} catch (FileNotFoundException e) {
				logger.error("Falha ao tratar dados " + e);
				arrFileFalha.add(extFiles);
			} catch (IOException e) {
				logger.error("Falha ao tratar dados " + e);
				arrFileFalha.add(extFiles);
			} finally {
				br.close();
			}

			if (arrFileFalha.contains(extFiles)) {
				logger.error("Ocorreu Falha no Arquivos: " + extFiles);
			} else {
				logger.info("Ocorreu Sucesso no Arquivos: " + extFiles);

				arrFileSucesso.add(extFiles);
			}
		}

		if (arrFileFalha.isEmpty()) {
			ft.transfersSucess(arrFileSucesso);
		} else if (arrFileSucesso.isEmpty()) {

			ft.transfersFail(arrFileFalha);
		} else {
			ft.transfersSucess(arrFileSucesso);

			ft.transfersFail(arrFileFalha);
		}

		return arrVo;

	}

}
