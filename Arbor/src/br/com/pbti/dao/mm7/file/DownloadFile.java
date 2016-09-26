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
	private String[] arrayFile = null;
	Logger logger = Logger.getLogger(DownloadFile.class);

	public void retrieveFile() throws IOException {

		try {

			for (String arquivo : readFile()) {

				logger.debug("Arquivo: " + arquivo);

				if (arquivo.startsWith("DISTARBOR")) {

					String server = "10.61.178.240";
					int port = 21;
					String user = "sbaom6001";
					String pass = "Jq5An8BE";

					FTPClient ftpClient = new FTPClient();

					try {

						ftpClient.connect(server, port);
						ftpClient.login(user, pass);
						ftpClient.enterLocalPassiveMode();
						ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

						// APPROACH #1: using retrieveFile(String, OutputStream)
						String remoteFile1 = ds.getRemoteFileMM7() + arquivo;
						File downloadFile1 = new File(ds.getDownloadFileMM7() + arquivo);
						OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
						boolean success = ftpClient.retrieveFile(remoteFile1, outputStream1);
						outputStream1.close();

						if (success) {
							logger.debug("Download com sucesso do arquivo: " + arquivo);
							
//							boolean successDelete = ftpClient.deleteFile(ds.getRemoteFileMM7() + arquivo);
//							if(successDelete)
//							{
//							   logger.debug("Arquivo Deleatado Com sucesso: " + arquivo);
//							} else {
//								logger.debug("Erro ao Deletar arquivo: " + arquivo);
//							}
						}

					} catch (IOException ex) {
						logger.debug("Error: " + ex.getMessage());
						ex.printStackTrace();
					} finally {
						try {
							if (ftpClient.isConnected()) {
								ftpClient.logout();
								ftpClient.disconnect();
							}
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}

				} else {
					logger.debug("Arquivo errado para dowload " + arquivo);
				}

			}

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

	public String[] readFile() {

		FTPClient ftp = new FTPClient();

		try {
			ftp.connect(ds.getServerMM7());
			ftp.login(ds.getUserMM7(), ds.getPasswordMM7());
			logger.debug("conectado: " + ftp.isConnected());
			ftp.enterLocalPassiveMode();

			ftp.changeWorkingDirectory(ds.getRemoteFileMM7());

			logger.debug("Status: " + ftp.getStatus());
			logger.debug(ftp.printWorkingDirectory());

			arrayFile = ftp.listNames();

		} catch (IOException e) {
			logger.debug("Error: " + e);
		} catch (Exception e) {
			logger.debug("Erro ao conseguir conectar no ftp");
		} finally {
			try {
				if (ftp != null && ftp.isConnected()) {
					ftp.logout();
					ftp.disconnect();
				}
			} catch (IOException ex) {
				logger.debug("Error: " + ex);
			}
		}
		return arrayFile;

	}
}
