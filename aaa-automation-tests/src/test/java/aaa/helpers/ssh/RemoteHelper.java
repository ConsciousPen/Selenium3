package aaa.helpers.ssh;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;

import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.exceptions.IstfException;
import toolkit.verification.CustomAssert;

public class RemoteHelper {

	private static Logger log = LoggerFactory.getLogger(RemoteHelper.class);
	private static String hostName = PropertyProvider.getProperty(TestProperties.APP_HOST);
	private static String user = PropertyProvider.getProperty(TestProperties.SSH_USER);
	private static String password = PropertyProvider.getProperty(TestProperties.SSH_PASSWORD);
	private static Ssh ssh = new Ssh(hostName, user, password);

	public static void clearFolder(List<String> folderNames) {
		for (String folder : folderNames) {
			clearFolder(folder);
		}
	}

	public static void clearFolder(String folder) {
		if (isPathExist(folder))
			ssh.removeFiles(folder);
		else
			log.warn("SSH: Folder '" + folder + "' doesn't exist.");
	}

	public static void downloadFile(String source, String destination) {
		log.info(String.format("SSH: File '%s' downloading to '%s' destination folder has been started.", source, destination));
		ssh.downloadFile(source, destination);
	}

	public static void uploadFile(String source, String destination) {
		log.info(String.format("SSH: File '%s' uploading to '%s' destination folder has been started.", source, destination));
		ssh.putFile(source, destination);
	}

	public static void removeFile(String file) {
		log.info(String.format("SSH: File '%s' deleting has been started.", file));
		ssh.removeFile(file);
	}

	public static void uploadFiles(String sourceFolder, String destinationFolder) {
		File directory = new File(sourceFolder);
		File[] files = directory.listFiles(File::isFile);
		log.info(String.format("SSH: Files uploading from '%s' folder to '%s' destination folder has been started.", sourceFolder, destinationFolder));
		if (files != null && files.length != 0) {
			for (File file : files) {
				uploadFile(sourceFolder + file.getName(), destinationFolder + file.getName());
			}
		}
		log.info(String.format("SSH: All files from '%s' folder were uploaded to '%s' destination folder.", sourceFolder, destinationFolder));
	}

	public static String executeCommand(String command) {
		log.info(String.format("SSH: Executing on host \"%s\" shell command: \"%s\"", hostName, command));
		String result = ssh.executeCommand(command);
		log.info(String.format("SSH: command output is: \"%s\"", result));
		return result;
	}

	public static boolean isPathExist(String path) {
		SftpATTRS attrs = null;
		path = ssh.parseFileName(path);

		try {
			ChannelSftp channel = ssh.getSftpChannel();
			attrs = channel.stat(path);
		} catch (Exception e) {
			log.debug("SSH: File/folder '" + path + "' doesn't exist.", e);
		}
		return attrs != null;
	}

	public static void createFolder(String source) {
		source = ssh.parseFileName(source);

		log.info(String.format("SSH: Creating folder '%s'", source));
		try {
			executeCommand("mkdir -p " + source);
			executeCommand("chmod 777 " + source);
		} catch (Exception e) {
			throw new IstfException("SSH: Folder '" + source + "' couldn't be created. " + e.getMessage());
		}
		log.info(String.format("SSH: Folder '%s' was created", source));
	}

	@SuppressWarnings("unchecked")
	public synchronized void verifyFolderIsEmpty(String source) {
		source = ssh.parseFileName(source);
		try {
			Vector<ChannelSftp.LsEntry> list = ssh.getSftpChannel().ls("*");
			CustomAssert.assertTrue("SSH: Folder should be empty", list.size() == 0);
		} catch (Exception e) {
			throw new IstfException("SSH: Folder '" + source + "' doesn't exist.", e);
		}
	}

	public static void closeSession() {
		ssh.closeSession();
	}

	public static String getFileContent(String filePath) {
		log.info(String.format("SSH: Getting content from \"%s\" file", filePath));
		return ssh.getFileContent(filePath);
	}

}
