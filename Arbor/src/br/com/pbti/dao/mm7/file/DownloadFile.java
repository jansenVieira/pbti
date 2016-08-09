package br.com.pbti.dao.mm7.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;

import br.com.pbti.dao.property.DataSource;

public class DownloadFile {

	public DataSource ds = new DataSource();

	private FTPClient ftpClient;
	private ArrayList<Map<String, String>> arrayFile = new ArrayList<Map<String, String>>();
	Logger logger = Logger.getLogger(DownloadFile.class);

	public void retrieveFile() throws IOException {

		try {

			for (Map<String, String> map : readFile()) {

				String nome = map.get("nome");
				String caminho = map.get("caminho");

				File file = new File(ds.getDownloadFileMM7() + nome);
				OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
				boolean success = ftpClient.retrieveFile(caminho, outputStream);

				if (success && file.exists()) {
					logger.debug("Arquivo " + file.getName() + " trasferido com sucesso");
					ftpClient.dele(caminho);
					logger.debug("Arquivo " + caminho + " deletado da origem");
				}
			}

		} catch (IOException ex) {
			logger.debug("Error: " + ex);

		} finally {
			try {
				if (ftpClient != null && ftpClient.isConnected()) {
					ftpClient.logout();
					ftpClient.disconnect();
				}
			} catch (IOException ex) {
				logger.debug("Error: " + ex);
			}
		}
	}

	public FTPClient conectioFtp() {

		if (ftpClient != null && ftpClient.isConnected()) {
			return ftpClient;
		} else {

			ftpClient = new FTPClient();

			try {
				ftpClient.connect(ds.getServerMM7(), ds.getPortMM7());
				ftpClient.login(ds.getUserMM7(), ds.getPasswordMM7());
				ftpClient.enterLocalPassiveMode();
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

			} catch (SocketException e) {
				logger.debug("Error: " + e);
			} catch (IOException e) {
				logger.debug("Error: " + e);
			}
		}
		return ftpClient;
	}

	public ArrayList<Map<String, String>> readFile() {

		String[] listFile = null;

		try {
			listFile = conectioFtp().listNames(ds.getRemoteFileMM7());

			Map<String, String> mapFile;

			for (String name : listFile) {

				mapFile = new HashMap<String, String>();

				mapFile.put("caminho", name);

				String[] arrArquivo = name.split("/");

				mapFile.put("nome", arrArquivo[3]);

				arrayFile.add(mapFile);
			}

		} catch (IOException e) {
			logger.debug("Error: " + e);
		} catch (Exception e) {
			logger.debug("Erro ao conseguir conectar no ftp");
		}

		return arrayFile;

	}
}
