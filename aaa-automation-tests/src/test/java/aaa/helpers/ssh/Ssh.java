package aaa.helpers.ssh;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
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
	public synchronized List<String> getFolderContent(String folderPath, boolean filesOnly, SortBy sortBy) {
		List<ChannelSftp.LsEntry> folderEntities = new ArrayList<>();
		folderPath = parseFileName(folderPath);

		try {
			openSftpChannel();
			sftpChannel.cd("/");
			sftpChannel.cd(folderPath);
			Vector<ChannelSftp.LsEntry> list = sftpChannel.ls("*");
			for (ChannelSftp.LsEntry fileOrFolder : list) {
				if (!filesOnly || !fileOrFolder.getAttrs().isDir()) {
					folderEntities.add(fileOrFolder);
				}
			}
		} catch (SftpException | RuntimeException e) {
			throw new IstfException("SSH: Unable to get content from \"" + folderPath + "\" directory", e);
		}

		if (sortBy != null) {
			switch (sortBy) {
				case NAME:
					folderEntities = folderEntities.stream().sorted(Comparator.comparing(ChannelSftp.LsEntry::getFilename)).collect(Collectors.toList());
					break;
				case DATE_ACCESS:
					folderEntities = folderEntities.stream().sorted(Comparator.comparing((ChannelSftp.LsEntry f) -> f.getAttrs().getATime())).collect(Collectors.toList());
					break;
				case DATE_MODIFIED:
					folderEntities = folderEntities.stream().sorted(Comparator.comparing((ChannelSftp.LsEntry f) -> f.getAttrs().getMTime())).collect(Collectors.toList());
					break;
				case SIZE:
					folderEntities = folderEntities.stream().sorted(Comparator.comparing((ChannelSftp.LsEntry f) -> f.getAttrs().getSize())).collect(Collectors.toList());
					break;
			}
		}

		return folderEntities.stream().map(ChannelSftp.LsEntry::getFilename).collect(Collectors.toList());
	}

	public synchronized LocalDateTime getLastModifiedTime(String path) {
		path = parseFileName(path);

		try {
			ChannelSftp channel = getSftpChannel();
			SftpATTRS attrs = channel.stat(path);
			Date dateModify = new Date(attrs.getMTime() * 1000L);
			return dateModify.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		} catch (SftpException e) {
			throw new IstfException("SSH: Unable to get last modified time from : " + path, e);
		}
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

	public synchronized CommandResults executeCommand(String command, ExecutionParams execParams) {
		StringBuilder outputBuilder = new StringBuilder();
		StringBuilder errorOutputBuilder = new StringBuilder();
		CommandResults results = new CommandResults();

		try {
			createSession();
			execChannel = (ChannelExec) session.openChannel("exec");
			execChannel.setCommand(command);
			execChannel.connect();
			log.info("SSH: Started EXEC Channel");
		} catch (JSchException e) {
			throw new CommandExecutionException("SSH: Unable to execute command: " + command + "\n", e);
		}

		byte[] tmp = new byte[1024];
		Instant currentTime = Instant.now();
		results.setExecutionStartTime(currentTime);
		Instant endTime = currentTime.plus(execParams.getTimeout());
		try (InputStream in = execChannel.getInputStream(); InputStream err = execChannel.getErrStream()) {
			while (currentTime.isBefore(endTime)) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0) {
						break;
					}
					outputBuilder.append(new String(tmp, 0, i));
				}

				while (err.available() > 0) {
					int i = err.read(tmp, 0, 1024);
					if (i < 0) {
						break;
					}
					errorOutputBuilder.append(new String(tmp, 0, i));
				}

				try {
					TimeUnit.MILLISECONDS.sleep(execParams.getRetryPollingInterval().toMillis());
					currentTime = Instant.now();
				} catch (InterruptedException | RuntimeException e) {
					throw new CommandExecutionException("SSH: Exception in Thread.sleep", e);
				}

				if (execChannel.isClosed() && in.available() == 0 && err.available() == 0) {
					break;
				}
			}
			results.setExecutionEndTime(currentTime);
			results.setOutput(outputBuilder);
			results.setExitCode(execChannel.getExitStatus());
			results.setErrorOutput(errorOutputBuilder);
			log.info("SSH: Command execution has been finished. {}", results);

			if (currentTime.isAfter(endTime)) {
				String message = String.format("SSH: Command execution time has exeeded! Timed out after: %s seconds!", results.getDuration().getSeconds());
				if (execParams.isFailOnTimeout()) {
					throw new CommandExecutionException(message);
				}
				log.warn(message);
			}

			if (results.getExitCode() != 0 || !results.getErrorOutput().isEmpty()) {
				String message = String.format("Command returned exit code %1$s and %2$s",
						results.getExitCode(), results.getErrorOutput().isEmpty() ? "empty error message" : "error message: '" + results.getErrorOutput()) + "'";
				if (execParams.isFailOnError()) {
					if (!execParams.getExitCodesToIgnore().contains(results.getExitCode())) {
						throw new CommandExecutionException(message);
					}
				}
				log.warn(message);
			}
		} catch (IOException e) {
			throw new CommandExecutionException("SSH: Unable to execute command: " + command + "\n", e);
		} finally {
			if (execChannel != null && !execChannel.isClosed()) {
				execChannel.disconnect();
			}
		}

		return results;
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

	public synchronized String getFileContent(String filePath) {
		filePath = parseFileName(filePath);
		try (InputStream is = sftpChannel.get(filePath)) {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			return IOUtils.toString(br);
		} catch (IOException | SftpException e) {
			throw new IstfException("SSH: Unable to get content from file: " + filePath, e);
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

	public enum SortBy {
		NAME,
		DATE_ACCESS,
		DATE_MODIFIED,
		SIZE
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
