package br.com.pbti;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import br.com.pbti.dao.mm7.Insert;
import br.com.pbti.dao.mm7.file.DownloadFile;
import br.com.pbti.dao.mm7.file.FileTransfers;
import br.com.pbti.dao.mm7.file.FileZipe;
import br.com.pbti.dao.oracle.InsertFaturamento;
import br.com.pbti.dao.property.DataSource;
import br.com.pbti.dao.time.DeleteArbor;
import br.com.pbti.dao.time.DeleteMM7;
import br.com.pbti.dao.time.ReadDateTime;

public class Main {

	public static Logger logger = Logger.getLogger(Main.class);
	public static DataSource ds = new DataSource();

	public static void main(String[] args) throws ParseException, IOException, SQLException {

		long tempoInicial = System.currentTimeMillis();
		
		ReadDateTime rdt = new ReadDateTime();
		rdt.lerDadosArbo();
		rdt.lerDadosMM7();
		
		DeleteArbor da = new DeleteArbor();
		da.deleteDadosArbor();
		
		DeleteMM7 dmm7 = new DeleteMM7();
		dmm7.deleteDadosMM7();
		
		zipArquivos();

		DownloadFile down = new DownloadFile();
		down.retrieveFile();
		
		Insert is = new Insert();
		is.insertDados();
		
		br.com.pbti.dao.oracle.Insert isOracle = new br.com.pbti.dao.oracle.Insert();
		isOracle.insertDados();
		
		InsertFaturamento isFaturamento = new InsertFaturamento();
		isFaturamento.insertDados();

		long tempoFinal = System.currentTimeMillis();
		logger.debug(String.format("Tempo de Execusao da Aplicacao: "+ "%.3f ms%n ", (tempoFinal - tempoInicial) / 1000d));
		
	}

	public static void zipArquivos() {
		try {

			String date = new SimpleDateFormat("dd_MM_yyyy").format(new Date());

			FileZipe fileZipe = new FileZipe();

			fileZipe.zipFolder(ds.getDirSucessoMM7()+"\\", ds.getDirSucessoZipMM7() + date + ".zip");

			fileZipe.zipFolder(ds.getDirFailMM7()+"\\", ds.getDirFailZipMM7()+ date + ".zip");

			fileZipe.zipFolder(ds.getDirLog(), ds.getDirLogZip() + date + ".zip");

			deleteArquivos();

		} catch (Exception e) {
			logger.error("Falha ao zipar arquivos " + e);

		}

	}

	public static void deleteArquivos() {
		try {

			Collection<File> files = FileUtils.listFiles(new File(ds.getDirSucessoMM7()), null, true);

			files.addAll((FileUtils.listFiles(new File(ds.getDirFailMM7()), null, true)));

			FileTransfers fileTrans = new FileTransfers();

			fileTrans.delete(files);
			
		} catch (Exception e) {
			logger.error("Falha ao zipar arquivos " + e);

		}

	}

}
