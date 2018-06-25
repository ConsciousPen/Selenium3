package aaa.helpers.ssh;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jcraft.jsch.*;
import toolkit.config.PropertyProvider;
import toolkit.exceptions.IstfException;

public class Ssh {

	private static Logger log = LoggerFactory.getLogger(Ssh.class);
	private static ArrayList<Session> sessionPool = new ArrayList<>();
	private static ArrayList<ChannelSftp> sftpChannelPool = new ArrayList<>();
	private Session session;
	private JSch jsch;
	private ChannelSftp sftpChannel;
	private ChannelExec execChannel;

	private String host;
	private int port;
	private String user;
	private String password;
	private String privateKeyPath;

	public Ssh(String host, String user, String password) {
		this(new ConnectionParams().host(host).user(user, password));
	}

	Ssh(ConnectionParams cp) {
		this.host = cp.getHost();
		this.port = cp.getPort();
		this.user = cp.getUser();
		this.password = cp.getPassword();
		this.privateKeyPath = cp.getPrivateKeyPath();

		try {
			this.jsch = new JSch();
			if (privateKeyPath != null) {
				this.jsch.addIdentity(privateKeyPath);
			}

			createSession();
			openSftpChannel();
			sessionPool.add(session);
			sftpChannelPool.add(sftpChannel);
		} catch (JSchException e) {
			throw new IstfException("SSH: Unable to establish ssh connection", e);
		}
	}

	public synchronized ChannelSftp getSftpChannel() {
		openSftpChannel();
		return sftpChannel;
	}

	public static synchronized void closeAllSessions() {
		try {
			log.debug("SFTP Channel pool has {} channels", sftpChannelPool.size());
			log.debug("SSH session pool has {} sessions", sessionPool.size());
			for (ChannelSftp sftpChannel : sftpChannelPool) {
				if (sftpChannel != null) {
					sftpChannel.disconnect();
				}
			}
			for (Session session : sessionPool) {
				if (session != null) {
					session.disconnect();
					log.debug("SSH: {} session is closed", session.getHost());
				}
			}
		} catch (RuntimeException e) {
			throw new IstfException("SSH: Unable to close a session : ", e);
		}
	}

	@SuppressWarnings("unchecked")
	public synchronized ArrayList<String> getListOfFiles(String folderName) {
		ArrayList<String> listOfFiles = new ArrayList<>();

		folderName = parseFileName(folderName);

		try {
			openSftpChannel();
			sftpChannel.cd("/");
			sftpChannel.cd(folderName);
			Vector<ChannelSftp.LsEntry> list = sftpChannel.ls("*");
			for (ChannelSftp.LsEntry file : list) {
				listOfFiles.add(file.getFilename());
			}
		} catch (SftpException | RuntimeException e) {
			throw new IstfException("SSH: Folder '" + folderName + "' doesn't exist.", e);
		}
		return listOfFiles;
	}

	public synchronized void downloadFile(String source, String destination) {

		source = parseFileName(source);
		File destinationFile = new File(destination);
		try {
			openSftpChannel();
			destinationFile.getAbsoluteFile().getParentFile().mkdir();
			sftpChannel.get(source, destinationFile.getAbsolutePath());

			log.info("SSH: File \"{}\" was downloaded to \"{}\".", source, destinationFile.getAbsolutePath());
		} catch (SftpException | RuntimeException e) {
			throw new IstfException("SSH: Unable to download file '" + source + "': " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public synchronized void removeFiles(String source) {

		source = parseFileName(source);

		try {
			closeSession(); //added to avoid hanging during file removal
			openSftpChannel();
			//sftpChannel.cd("/"); //replaced with closing session above
			sftpChannel.cd(source);
			Vector<ChannelSftp.LsEntry> list = sftpChannel.ls("*");

			if (list.isEmpty()) {
				//closeSession();
				log.info("SSH: No files to delete in \"{}\".", source);
				return;
			}
			for (ChannelSftp.LsEntry file : list) {
				if (!file.getAttrs().isDir()) {
					sftpChannel.rm(file.getFilename());
				}
			}
			log.info("SSH: Files were removed from the folder \"{}\".", source);
		} catch (SftpException | RuntimeException e) {
			throw new IstfException("SSH: Error deleting files from folder '" + source + "'", e);
		}
	}

	@SuppressWarnings("unchecked")
	public synchronized File downloadBatchFile(String source, File destination) {

		source = parseFileName(source);

		try {
			openSftpChannel();
			destination.getAbsoluteFile().getParentFile().mkdir();
			sftpChannel.cd("/");
			sftpChannel.cd(source);
			Vector<ChannelSftp.LsEntry> list = sftpChannel.ls("*");
			File response = null;
			for (ChannelSftp.LsEntry file : list) {
				File request = new File(destination.getAbsolutePath() + "/" + file.getFilename());
				sftpChannel.get(file.getFilename(), destination.getAbsolutePath() + "/" + file.getFilename());
				String newName = file.getFilename();// + ".fullfill";
				response = new File(destination.getAbsolutePath() + "/" + newName);
				request.renameTo(response);
			}

			log.info("SSH: File \"{}\" was downloaded to \"{}\".", source, destination.getAbsolutePath());
			return response;
		} catch (SftpException | RuntimeException e) {
			throw new IstfException("SSH: Unable to download file: ", e);
		}
	}

	public synchronized void putFile(String source, String destination) {
		destination = parseFileName(destination);
		try (InputStream fis = new BufferedInputStream(new FileInputStream(new File(source)))) {
			openSftpChannel();
			String[] folders = destination.split("/");
			folders = Arrays.copyOf(folders, folders.length - 1);
			sftpChannel.cd("/");
			for (String folder : folders) {
				if (!folder.isEmpty()) {
					try {
						sftpChannel.cd(folder);
					} catch (SftpException e) {
						sftpChannel.mkdir(folder);
						sftpChannel.cd(folder);
					}
				}
			}
			sftpChannel.put(fis, destination, ChannelSftp.OVERWRITE);
			log.info("SSH: File \"{}\" was put to \"{}\".", source, destination);
		} catch (SftpException | IOException | RuntimeException e) {
			throw new IstfException(String.format("SSH: Unable to put file \"%1$s\" to \"%2$s\" folder", source, destination), e);
		}
	}

	public synchronized void renameFile(String oldPath, String newPath) {

		try {
			openSftpChannel();
			sftpChannel.rename(oldPath, newPath);
			log.info("SSH: File \"{}\" was rename to \"{}\".", oldPath, newPath);
		} catch (SftpException | RuntimeException e) {
			throw new IstfException("SSH: Unable to rename file: ", e);
		}
	}

	public synchronized String executeCommand(String command, ExecutionParams execParams) {
		StringBuilder outputBuilder = new StringBuilder();
		StringBuilder errorOutputBuilder = new StringBuilder();
		String errorOutput;

		try {
			createSession();
			execChannel = (ChannelExec) session.openChannel("exec");
			execChannel.setCommand(command);
			execChannel.setInputStream(null);
			execChannel.setErrStream(System.err);
			execChannel.connect();
			log.info("SSH: Started EXEC Channel");
		} catch (JSchException e) {
			throw new IstfException("SSH: Unable to execute command: " + command + "\n", e);
		}

		try (InputStream in = execChannel.getInputStream(); InputStream errStream = execChannel.getErrStream()) {
			byte[] tmp = new byte[1024];
			long currentTime = System.currentTimeMillis();
			long startTime = currentTime;
			long endTime = currentTime + TimeUnit.SECONDS.toMillis(execParams.getTimeoutInSeconds());
			while (currentTime < endTime) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0) {
						break;
					}
					outputBuilder.append(new String(tmp, 0, i));
				}

				while (errStream.available() > 0) {
					int i = errStream.read(tmp, 0, 1024);
					if (i < 0) {
						break;
					}
					errorOutputBuilder.append(new String(tmp, 0, i));
				}

				if (execChannel.isClosed()) {
					long execDuration = System.currentTimeMillis() - startTime;
					log.info("SSH: Command execution has been finished after {}",
							execDuration > 1000 ? TimeUnit.MILLISECONDS.toSeconds(execDuration) + " seconds" : execDuration + " milliseconds");
					break;
				}

				try {
					TimeUnit.MILLISECONDS.sleep(execParams.getRetryIntervalInMilliseconds());
					currentTime = System.currentTimeMillis();
				} catch (InterruptedException | RuntimeException e) {
					throw new IstfException("SSH: Exception in Thread.sleep", e);
				}
			}

			if (currentTime >= endTime) {
				String message = String.format("SSH: Command execution time has exeeded! Timed out after: %s seconds!", TimeUnit.MILLISECONDS.toSeconds(currentTime - startTime));
				if (execParams.isFailOnTimeout()) {
					throw new IstfException(message);
				}
				log.error(message);
			}

			errorOutput = errorOutputBuilder.toString();
			if (execChannel.getExitStatus() != 0 || !errorOutput.isEmpty()) {
				String message = String.format("Command returned exit code %1$s and %2$s", execChannel.getExitStatus(), errorOutput.isEmpty() ? "empty error message" : "error message:\n" + errorOutput);
				if (execParams.isFailOnError()) {
					throw new IstfException(message);
				}
				log.error(message);
			}
		} catch (IOException e) {
			throw new IstfException("SSH: Unable to execute command: " + command + "\n", e);
		} finally {
			if (execChannel != null && !execChannel.isClosed()) {
				execChannel.disconnect();
			}
		}

		if (execParams.isReturnErrorOutput()) {
			return errorOutput;
		}
		return outputBuilder.toString();
	}

	public String parseFileName(String source) {
		if (source.contains("file://")) {
			source = source.replace("file://", "");
		}
		if (source.contains(":")) {
			source = source.replace(":", "");
			source = "/" + source;
		}
		source = source.replace("\\", "/");
		return source;
	}

	public synchronized void removeFile(String pathToSourceFile) {
		try {
			openSftpChannel();
			sftpChannel.rm(pathToSourceFile);
			log.info("SSH: File was deleted \"{}\"", pathToSourceFile);
		} catch (SftpException | RuntimeException e) {
			throw new IstfException("SSH: Unable to remove files: ", e);
		}
	}

	public synchronized void closeSession() {
		try {
			if (sftpChannel != null) {
				sftpChannel.disconnect();
			}
			if (session != null) {
				session.disconnect();
				session = null;
			}
			log.debug("SSH: Session is closed");
		} catch (RuntimeException e) {
			throw new IstfException("SSH: Unable to close a session : ", e);
		}
	}

	public synchronized String getFileContent(String filePath) {
		filePath = parseFileName(filePath);
		try (InputStream is = sftpChannel.get(filePath)) {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			return IOUtils.toString(br);
		} catch (IOException | SftpException e) {
			throw new IstfException("SSH: Unable to get content from file: " + filePath, e);
		}
	}

	private synchronized void openSftpChannel() {
		createSession();
		if (sftpChannel == null || sftpChannel.isClosed()) {
			try {
				sftpChannel = (ChannelSftp) session.openChannel("sftp");
				sftpChannel.connect();
				log.info("SSH: Started SFTP Channel");
			} catch (JSchException e) {
				throw new IstfException("SSH: Unable to open SFTP channel: ", e);
			}
		}
	}

	private synchronized void createSession() {
		if (session == null || !session.isConnected()) {
			try {
				session = jsch.getSession(user, host, port);

				session.setPassword(password);
				session.setConfig("StrictHostKeyChecking", "no");
				session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
				//todo remove redundant condition
				if (Boolean.parseBoolean(PropertyProvider.getProperty("scrum.envs.ssh", "false"))) {
					session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
				}
				session.connect();
				log.info("SSH: Started SSH Session for \"{}\" host and user \"{}\"", session.getHost(), session.getUserName());
			} catch (JSchException e) {
				throw new IstfException("Unable to start SSH session: ", e);
			}
		}
	}
}
