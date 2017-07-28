package aaa.helpers.ssh;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;

import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
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
		if (isFolderExist(folder))
			ssh.removeFiles(folder);
		else
			log.info("SSH: Folder '" + folder + "' doesn't exist.");
	}

	public static void downloadFile(String source, String destination) {
		ssh.downloadFile(source, destination);
	}

	public static void uploadFile(String source, String destination) {
		ssh.putFile(source, destination);
	}

	public static void removeFile(String file) {
		ssh.removeFile(file);
	}

	public static void downloadFile(String host, String source, String destination) {
		ssh.downloadFile(source, destination);
	}

	public static void uploadFile(String host, String source, String destination) {
		ssh.putFile(destination, source);
	}

	public static void downloadAndRemoveFilesByPolicy(String sourceFolder, String destinationFolder, String policy) throws FileNotFoundException {
		String searchCommandTemplate = "cd %s; find . -type f -iname '*.xml' -print | xargs grep -li '%s'";
		String searchCommand = String.format(searchCommandTemplate, sourceFolder, policy);

		log.debug(String.format("Executing on host %s shell command: %s", hostName, searchCommand));
		String result = ssh.executeCommand(searchCommand);
		log.debug("Result of shell cmd: " + result);

		if (result.isEmpty()) {
			throw new FileNotFoundException("Files not found for policy:" + policy + ",path:" + sourceFolder);
		}

		String[] files = result.split("\n");
		String file;

		for (int i = 0; i < files.length; i++) {
			file = files[i].substring(files[i].indexOf('/') + 1).trim();
			ssh.downloadFile(sourceFolder + file, destinationFolder + file);
			ssh.removeFile(sourceFolder + file);
		}
	}

	public static void downloadFilesByPolicy(String sourceFolder, String destinationFolder, String policy) throws FileNotFoundException {
		String searchCommandTemplate = "cd %s; find . -type f -iname '*.xml' -print | xargs grep -li '%s'";
		String searchCommand = String.format(searchCommandTemplate, sourceFolder, policy);

		String result = ssh.executeCommand(searchCommand);

		if (result.isEmpty()) {
			throw new FileNotFoundException("Files not found for policy:" + policy + ",path:" + sourceFolder);
		}

		String[] files = result.split("\n");
		String file;

		for (int i = 0; i < files.length; i++) {
			file = files[i].substring(files[i].indexOf('/') + 1).trim();
			ssh.downloadFile(sourceFolder + file, destinationFolder + file);
		}
	}

	public static boolean downloadAndRemoveDocumentFiles(String sourceFolder, String destinationFolder, String policyNum, String formID, String xPathInfo) {
		String searchCommandTemplate = "cd %s; find . -type f -iname '*.xml' -print | xargs grep -li '%s' | xargs grep -li '%s' | xargs grep -li '%s'";
		String searchCommand = String.format(searchCommandTemplate, sourceFolder, policyNum, formID, xPathInfo);

		String result = ssh.executeCommand(searchCommand);

		if (result.isEmpty()) {
			return false;
		}

		String[] files = result.split("\n");
		String file;

		for (int i = 0; i < files.length; i++) {
			file = files[i].substring(files[i].indexOf('/') + 1).trim();
			ssh.downloadFile(sourceFolder + file, destinationFolder + file);
			ssh.removeFile(sourceFolder + file);
		}
		return true;
	}

	public static boolean downloadDocumentFiles(String sourceFolder, String destinationFolder, String policyNum, String formID, String xPathInfo) {
		String searchCommandTemplate = "cd %s; find . -type f -iname '*.xml' -print | xargs grep -li '%s' | xargs grep -li '%s' | xargs grep -li '%s'";
		String searchCommand = String.format(searchCommandTemplate, sourceFolder, policyNum, formID, xPathInfo);

		String result = ssh.executeCommand(searchCommand);

		if (result.isEmpty()) {
			return false;
		}

		String[] files = result.split("\n");
		String file;

		for (int i = 0; i < files.length; i++) {
			file = files[i].substring(files[i].indexOf('/') + 1).trim();
			ssh.downloadFile(sourceFolder + file, destinationFolder + file);
		}
		return true;
	}

	public static void uploadFiles(String sourceFolder, String destinationFolder) {

		File directory = new File(sourceFolder);

		File[] files = directory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isFile();
			}
		});
		if (files != null && files.length != 0) {
			for (File file : files) {
				ssh.putFile(sourceFolder + file.getName(), destinationFolder + file.getName());
			}
		}
	}

	public static String executeCommand(String command) {
		return ssh.executeCommand(command);
	}

	public static Boolean isFolderExist(String source) {
		SftpATTRS attrs = null;
		Boolean returnValue;
		source = ssh.parseFileName(source);

		try {
			ChannelSftp channel = ssh.getSftpChannel();
			attrs = channel.stat(source);
		} catch (Exception e1) {
			System.out.println("SSH: Folder '" + source + "' doesn't exist.");
		}
		if (attrs != null) {
			returnValue = true;
		} else
			returnValue = false;

		return returnValue;
	}

	public static void createFolder(String source) {
		source = ssh.parseFileName(source);

		try {
			executeCommand("mkdir -p " + source);
			executeCommand("chmod 777 " + source);
		} catch (Exception e1) {
			throw new RuntimeException("SSH: Folder '" + source + "' couldn't be created. " + e1.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public synchronized void verifyFolderIsEmpty(String source) {
		source = ssh.parseFileName(source);
		try {
			Vector<ChannelSftp.LsEntry> list = ssh.getSftpChannel().ls("*");
			CustomAssert.assertTrue("SSH: Folder should be empty", list.size() == 0);
		} catch (Exception e) {
			throw new RuntimeException("SSH: Folder '" + source + "' doesn't exist.", e);
		}
	}

	public static void closeSession() {
		ssh.closeSession();
	}

}
