package br.com.pbti.dao.property;

public class DataSource {

	// Conexao Arbor Oracle
	private String userArbor;
	private String passwordArbor;
	private String driverClassArbor;
	private String urlConnectionArbor;

	// Conexao Arquivo MM7
	private String serverMM7;
	private int portMM7;
	private String userMM7;
	private String passwordMM7;
	private String remoteFileMM7;
	private String downloadFileMM7;

	// Conexao Diretorios
	private String dirSucessoMM7;
	private String dirFailMM7;
	private String dirFailZipMM7;
	private String dirSucessoZipMM7;
	private String dirLog;
	private String dirLogZip;

	// Conexao Spotfire MM7 e Arbor
	private String userSpotfire;
	private String passwordSpotfire;
	private String driverClassSpotfire;
	private String urlConnectionSpotfire;

	public DataSource(String recb)
	{
		
	}
	
	
	public DataSource() {

		// Conexao Arbor Oracle
		userArbor = "SBAO6001";
		passwordArbor = "#S3B5A7O9#";
		driverClassArbor = "oracle.jdbc.driver.OracleDriver";
		urlConnectionArbor = "jdbc:oracle:thin:@kenpx24:1562:catprd";

		// Conexao Arquivo MM7
		serverMM7 = "10.61.178.240";
		portMM7 = 21;
		userMM7 = "SBAOM6001";
		passwordMM7 = "Jq5An8BE";
		remoteFileMM7 = "/sftpprd1/u006/InputMediacao/spotifire/";
		downloadFileMM7 = "D:\\Arbor\\MM7\\Dados\\";

		// Conexao Diretorios
		dirSucessoMM7 = "D:\\Arbor\\MM7\\Sucesso";
		dirFailMM7 = "D:\\Arbor\\MM7\\Falha";
		dirFailZipMM7 = "D:\\Arbor\\MM7\\Zip\\Falha\\falha_";
		dirSucessoZipMM7 = "D:\\Arbor\\MM7\\Zip\\Sucesso\\sucesso_";
		dirLog = "D:\\Arbor\\Log\\";
		dirLogZip = "D:\\Arbor\\MM7\\Zip\\Log\\log_";

		// Conexao Spotfire MM7 e Arbor
		userSpotfire = "sbao6002";
		passwordSpotfire = "$3rvr3f1re.p";
		driverClassSpotfire = "oracle.jdbc.driver.OracleDriver";
		urlConnectionSpotfire = "jdbc:oracle:thin:@//spot-p1:1549/spot";
	}

	public String getUserArbor() {
		return userArbor;
	}

	public void setUserArbor(String userArbor) {
		this.userArbor = userArbor;
	}

	public String getPasswordArbor() {
		return passwordArbor;
	}

	public void setPasswordArbor(String passwordArbor) {
		this.passwordArbor = passwordArbor;
	}

	public String getDriverClassArbor() {
		return driverClassArbor;
	}

	public void setDriverClassArbor(String driverClassArbor) {
		this.driverClassArbor = driverClassArbor;
	}

	public String getUrlConnectionArbor() {
		return urlConnectionArbor;
	}

	public void setUrlConnectionArbor(String urlConnectionArbor) {
		this.urlConnectionArbor = urlConnectionArbor;
	}

	public String getServerMM7() {
		return serverMM7;
	}

	public void setServerMM7(String serverMM7) {
		this.serverMM7 = serverMM7;
	}

	public int getPortMM7() {
		return portMM7;
	}

	public void setPortMM7(int portMM7) {
		this.portMM7 = portMM7;
	}

	public String getUserMM7() {
		return userMM7;
	}

	public void setUserMM7(String userMM7) {
		this.userMM7 = userMM7;
	}

	public String getPasswordMM7() {
		return passwordMM7;
	}

	public void setPasswordMM7(String passwordMM7) {
		this.passwordMM7 = passwordMM7;
	}

	public String getRemoteFileMM7() {
		return remoteFileMM7;
	}

	public void setRemoteFileMM7(String remoteFileMM7) {
		this.remoteFileMM7 = remoteFileMM7;
	}

	public String getDownloadFileMM7() {
		return downloadFileMM7;
	}

	public void setDownloadFileMM7(String downloadFileMM7) {
		this.downloadFileMM7 = downloadFileMM7;
	}

	public String getUserSpotfire() {
		return userSpotfire;
	}

	public void setUserSpotfire(String userSpotfire) {
		this.userSpotfire = userSpotfire;
	}

	public String getPasswordSpotfire() {
		return passwordSpotfire;
	}

	public void setPasswordSpotfire(String passwordSpotfire) {
		this.passwordSpotfire = passwordSpotfire;
	}

	public String getDriverClassSpotfire() {
		return driverClassSpotfire;
	}

	public void setDriverClassSpotfire(String driverClassSpotfire) {
		this.driverClassSpotfire = driverClassSpotfire;
	}

	public String getUrlConnectionSpotfire() {
		return urlConnectionSpotfire;
	}

	public void setUrlConnectionSpotfire(String urlConnectionSpotfire) {
		this.urlConnectionSpotfire = urlConnectionSpotfire;
	}

	public String getDirSucessoMM7() {
		return dirSucessoMM7;
	}

	public void setDirSucessoMM7(String dirSucessoMM7) {
		this.dirSucessoMM7 = dirSucessoMM7;
	}

	public String getDirFailMM7() {
		return dirFailMM7;
	}

	public void setDirFailMM7(String dirFailMM7) {
		this.dirFailMM7 = dirFailMM7;
	}

	public String getDirFailZipMM7() {
		return dirFailZipMM7;
	}

	public void setDirFailZipMM7(String dirFailZipMM7) {
		this.dirFailZipMM7 = dirFailZipMM7;
	}

	public String getDirSucessoZipMM7() {
		return dirSucessoZipMM7;
	}

	public void setDirSucessoZipMM7(String dirSucessoZipMM7) {
		this.dirSucessoZipMM7 = dirSucessoZipMM7;
	}

	public String getDirLog() {
		return dirLog;
	}

	public void setDirLog(String dirLog) {
		this.dirLog = dirLog;
	}

	public String getDirLogZip() {
		return dirLogZip;
	}

	public void setDirLogZip(String dirLogZip) {
		this.dirLogZip = dirLogZip;
	}

}
