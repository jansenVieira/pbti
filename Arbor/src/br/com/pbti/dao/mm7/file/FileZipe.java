package br.com.pbti.dao.mm7.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

public class FileZipe {

	public static Logger logger = Logger.getLogger(FileZipe.class);

	public void zipFolder(String srcFolder, String destZipFile) throws IOException {

		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;
		
		try {

			fileWriter = new FileOutputStream(destZipFile);
			zip = new ZipOutputStream(fileWriter);

			addFolderToZip("", srcFolder, zip);
			
			
		} catch (Exception e) {
			logger.error("Erro ao zipar arquivos " + e);
		} finally {
			
			
			
			zip.flush();
			zip.close();
			
			zip = null;
			fileWriter.flush();
			fileWriter.close();
			
			fileWriter = null;
			
			System.gc();
		}

	}

	@SuppressWarnings("resource")
	private void addFileToZip(String path, String srcFile, ZipOutputStream zip) {

		try {

			File folder = new File(srcFile);
			if (folder.isDirectory()) {
				addFolderToZip(path, srcFile, zip);
			} else {
				byte[] buf = new byte[1024];
				int len;
				FileInputStream in;

				in = new FileInputStream(srcFile);

				zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
				while ((len = in.read(buf)) > 0) {
					zip.write(buf, 0, len);
				}
			}
			
			
						
		} catch (FileNotFoundException e) {
			logger.error("Erro ao zipar arquivos ", e);
		} catch (Exception e) {
			logger.error("Erro ao zipar arquivos ", e);
		}
	}

	private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws IOException {
		File folder = new File(srcFolder);

		for (String fileName : folder.list()) {
			if (path.equals("")) {
				addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
			} else {
				addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
			}
		}
		
		zip.close();
	}

}
