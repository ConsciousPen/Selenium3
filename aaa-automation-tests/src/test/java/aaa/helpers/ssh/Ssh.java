package aaa.helpers.ssh;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

@SuppressWarnings("unchecked")
public class Ssh {

	private static Logger log = LoggerFactory.getLogger(Ssh.class);
	// private static Ssh instance;
	private Session session;
	private JSch jsch = new JSch();
	private ChannelSftp sftpChannel;
	private ChannelExec execChannel;
	private static ArrayList<Session> sessionPool = new ArrayList<>();
	private static ArrayList<ChannelSftp> sftpChannelPool = new ArrayList<>();
	private String host;
	private String user;
	private String password;

	public Ssh(String hostName, String user, String password) {
		this.host = hostName;
		this.user = user;
		this.password = password;
		
		try {
			createSession();
			openSftpChannel();
			sessionPool.add(session);
			sftpChannelPool.add(sftpChannel);
		} catch (Exception e) {
			throw new RuntimeException("SSH: Unable to establish ssh connection", e);
		}
	}

	/*
	 * public static Ssh getInstance(String hostName, String user, String
	 * password) { if (instance == null) { instance = new Ssh(hostName, user,
	 * password); } return instance; }
	 */

	public synchronized ArrayList<String> getListOfFiles(String folderName) {
		ArrayList<String> listOfFiles = new ArrayList<String>();

		folderName = parseFileName(folderName);

		try {
			openSftpChannel();
			sftpChannel.cd(folderName);
			Vector<ChannelSftp.LsEntry> list = sftpChannel.ls("*");
			for (ChannelSftp.LsEntry file : list) {
				listOfFiles.add(file.getFilename());
			}
		} catch (Exception e) {
			throw new RuntimeException("SSH: Folder '" + folderName + "' doesn't exist.", e);
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

			log.info("SSH: File '" + source + "' was downloaded to '" + destinationFile.getAbsolutePath() + "'.");
		} catch (Exception e) {
			throw new RuntimeException("SSH: Unable to download file '" + source + "': " + e.getMessage(), e);
		}
	}

	public synchronized void removeFiles(String source) {

		source = parseFileName(source);

		try {
			openSftpChannel();
			sftpChannel.cd(source);
			Vector<ChannelSftp.LsEntry> list = sftpChannel.ls("*");

			for (ChannelSftp.LsEntry file : list) {
				if (!file.getAttrs().isDir())
					sftpChannel.rm(file.getFilename());
			}
			log.info("SSH: Files were removed from the folder '" + source + "'.");
		} catch (Exception e) {
			throw new RuntimeException("SSH: Folder '" + source + "' doesn't exist.", e);
		}
	}

	public synchronized File downloadBatchFile(String source, File destination) {

		source = parseFileName(source);

		try {
			openSftpChannel();
			destination.getAbsoluteFile().getParentFile().mkdir();
			sftpChannel.cd(source);
			Vector<ChannelSftp.LsEntry> list = sftpChannel.ls("*");
			File response = null;
			for (ChannelSftp.LsEntry file : list) {
				File request = new File(destination.getAbsolutePath() + "/" + file.getFilename());
				sftpChannel.get(file.getFilename(), destination.getAbsolutePath() + "/" + file.getFilename());
				String newName = file.getFilename() + ".fullfill";
				response = new File(destination.getAbsolutePath() + "/" + newName);
				request.renameTo(response);
			}

			log.info("SSH: File '" + source + "' was downloaded to '" + destination.getAbsolutePath() + "'.");
			return response;
		} catch (Exception e) {
			throw new RuntimeException("SSH: Unable to download file: ", e);
		}
	}

	public synchronized void putFile(String source, String destination) {
		destination = parseFileName(destination);
		InputStream fis = null;
		try {
			openSftpChannel();
			fis = new BufferedInputStream(new FileInputStream(new File(source)));
			sftpChannel.put(fis, destination, ChannelSftp.OVERWRITE);
			log.info("SSH: File '" + source + "' was put to '" + destination + "'.");
		} catch (Exception e) {
			throw new RuntimeException("SSH: Unable to put file: ", e);
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
				}
		}
	}

	public synchronized void renameFile(String oldPath, String newPath) {

		try {
			openSftpChannel();
			sftpChannel.rename(oldPath, newPath);
			log.info("SSH: File '" + oldPath + "' was rename to '" + newPath + "'.");
		} catch (Exception e) {
			throw new RuntimeException("SSH: Unable to rename file: ", e);
		}
	}

	public synchronized String executeCommand(String command) {

		StringBuilder output = new StringBuilder();
		InputStream in = null;
		int totalNumberOfIterations = 4200; // ~ 7 minutes (4200 * 100 / 60)

		try {
			try {
				createSession();
				execChannel = (ChannelExec) session.openChannel("exec");
				execChannel.setCommand(command);
				execChannel.setInputStream(null);
				execChannel.setErrStream(System.err);

				in = execChannel.getInputStream();

				execChannel.connect();
				log.info("SSH: Started EXEC Channel");
			} catch (JSchException e) {
				throw new RuntimeException("Unable to execute command: " + command + "\n", e);
			}

			byte[] tmp = new byte[1024];
			while (true && (totalNumberOfIterations > 0)) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0) {
						break;
					}
					output.append(new String(tmp, 0, i));
				}

				if (execChannel.isClosed()) {
					// System.out.println("exit-status: "+channel.getExitStatus());
					break;
				}

				try {
					Thread.sleep(100);
				} catch (Exception ee) {
					throw new RuntimeException("SSH: Exception in Thread.sleep", ee);
				}
				totalNumberOfIterations--;
			}

			if (totalNumberOfIterations == 0) {
				throw new RuntimeException("Exec Command Time exeeded! Timed out after : 7 minutes!");
			}

		} catch (IOException ex2) {
			throw new RuntimeException(ex2.getMessage());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (!execChannel.isClosed()) {
				execChannel.disconnect();
			}
		}
		return output.toString();
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
			log.info("SSH: File was deleted'" + pathToSourceFile);

		} catch (Exception e) {
			throw new RuntimeException("SSH: Unable to remove files: ", e);
		}
	}

	public synchronized void closeSession() {

		try {
			if (sftpChannel != null)
				sftpChannel.disconnect();
			if (session != null) {
				session.disconnect();
				session = null;
			}
			log.debug("SSH: Session is closed");
		} catch (Exception e) {
			throw new RuntimeException("SSH: Unable to close a session : ", e);
		}
	}

	public synchronized static void closeAllSessions() {
		try {
			log.debug("SFTP Channel pool has" + sftpChannelPool.size() + "channels");
			log.debug("SSH session pool has" + sessionPool.size() + "sessions");
			for (ChannelSftp sftpChannel : sftpChannelPool) {
				if (sftpChannel != null)
					sftpChannel.disconnect();
			}
			for (Session session : sessionPool) {
				log.debug(session.getHost() + " session is closed");
				if (session != null) {
					session.disconnect();
					session = null;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("SSH: Unable to close a session : ", e);
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
				throw new RuntimeException("Unable to open SFTP channel: ", e);
			}
		}
	}

	private synchronized void createSession() {
		if (session == null || !session.isConnected()) {
			try {
				session = jsch.getSession(user, host, 22);

				session.setPassword(password);
				session.setConfig("StrictHostKeyChecking", "no");
				session.connect();
				log.info("SSH: Started SSH Session for " + session.getHost() + " host");
			} catch (JSchException e) {
				throw new RuntimeException("Unable to start SSH session: ", e);
			}
		}
	}

	public synchronized ChannelSftp getSftpChannel() {
		openSftpChannel();
		return sftpChannel;
	}
}
