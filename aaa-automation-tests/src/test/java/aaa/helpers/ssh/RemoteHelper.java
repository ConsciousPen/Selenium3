package aaa.helpers.ssh;

import static toolkit.verification.CustomAssertions.assertThat;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.exceptions.IstfException;
import toolkit.verification.CustomAssert;

public final class RemoteHelper {
	
	private static final Logger log = LoggerFactory.getLogger(RemoteHelper.class);
	
	private static String defaultHostName = PropertyProvider.getProperty(TestProperties.APP_HOST);
	private static String defaultUser = PropertyProvider.getProperty(TestProperties.SSH_USER);
	private static String defaultPassword = PropertyProvider.getProperty(TestProperties.SSH_PASSWORD);
	private static Ssh ssh;
	
	private static Ssh getSession() {
		return getSession(defaultHostName, defaultUser, defaultPassword);
	}
	
	private static Ssh getSession(String hostName, String user, String password) {
		if (ssh == null || !defaultHostName.equals(hostName) || !user.equals(defaultUser)) {
			try {
				ssh = new Ssh(hostName, user, password);
			} catch (RuntimeException e) {
				throw new IstfException(String.format("Unable to create ssh connection to host=\"%1$s\" as user=\"%2$s\"", hostName, user), e);
			}
		}
		return ssh;
	}

	public static String getServerTimeZone() {
		String cmd = "timedatectl | grep -oP 'Time zone: \\K.*(?= \\()'";
		return executeCommand(cmd).trim();
	}

	public static void clearFolder(List<String> folderNames) {
		for (String folder : folderNames) {
			clearFolder(folder);
		}
	}

	public static void clearFolder(String folder) {
		if (isPathExist(folder)) {
			getSession().removeFiles(folder);
		} else {
			log.warn("SSH: Folder '{}' doesn't exist.", folder);
		}
	}

	public static synchronized void downloadFileWithWait(String source, String destination, long timeout) {
		log.info(String.format("SSH: File '%s' downloading to '%s' destination folder has been started.", source, destination));
		long endTime = System.currentTimeMillis() + timeout;
		while (!isPathExist(source)) {
			if (endTime < System.currentTimeMillis()) {
				throw new AssertionError(String.format("File '%s' wasn't found after %s ms of wait", source, timeout));
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				throw new IstfException(e);
			}
		}
		getSession().downloadFile(source, destination);
	}

	public static void downloadFile(String source, String destination) {
		log.info(String.format("SSH: File '%s' downloading to '%s' destination folder has been started.", source, destination));
		getSession().downloadFile(source, destination);
	}

	public static void downloadBatchFiles(String source, File destination) {
		log.info(String.format("SSH: Files downloading from '%s' has been started,", source));
		getSession().downloadBatchFile(source, destination);
	}

	public static synchronized void uploadFile(String source, String destination) {
		assertThat(source).as("Source file is NULL").isNotNull();
		log.info(String.format("SSH: File '%s' uploading to '%s' destination folder has been started.", source, destination));
//		Folders creation moved to Ssh class and made with sftp commands
//		File destinationFile = File destinationFile = new File(destination);;
//		if (!isPathExist(destinationFile.getParent())) {
		//			executeCommand("mkdir -p -m 777 " + getSession().parseFileName(destinationFile.getParent()));
//			if (destinationFile.getParentFile().getParentFile() != null) {
		//				executeCommand("chmod -R 777 " + getSession().parseFileName(destinationFile.getParentFile().getParent()));
//			}
//		}
		getSession().putFile(source, destination);
	}

	public static void removeFile(String file) {
		log.info(String.format("SSH: File '%s' deleting has been started.", file));
		getSession().removeFile(file);
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
		return executeCommandAsUser(command, defaultUser, defaultPassword);
	}
	
	public static String executeCommandAsUser(String command, String user, String password) {
		log.info(String.format("SSH: Executing on host \"%1$s\" shell command: \"%2$s\" as user \"%3$s\"", defaultHostName, command, user));
		String result = getSession(defaultHostName, user, password).executeCommand(command);
		log.info(String.format("SSH: command output is: \"%s\"", result));
		return result;
	}

	public static boolean isPathExist(String path) {
		SftpATTRS attrs = null;
		path = getSession().parseFileName(path);

		try {
			ChannelSftp channel = getSession().getSftpChannel();
			attrs = channel.stat(path);
		} catch (SftpException | RuntimeException e) {
			log.debug("SSH: File/folder '{}' doesn't exist.", path);
		}
		return attrs != null;
	}

	public static void createFolder(String source) {
		source = getSession().parseFileName(source);

		log.info(String.format("SSH: Creating folder '%s'", source));
		try {
			executeCommand("mkdir -p " + source);
			executeCommand("chmod 777 " + source);
		} catch (RuntimeException e) {
			throw new IstfException("SSH: Folder '" + source + "' couldn't be created. " + e.getMessage());
		}
		log.info(String.format("SSH: Folder '%s' was created", source));
	}

	public static void closeSession() {
		getSession().closeSession();
	}

	public static String getFileContent(String filePath) {
		log.info(String.format("SSH: Getting content from \"%s\" file", filePath));
		return getSession().getFileContent(filePath);
	}

	public static List<String> waitForFilesAppearance(String sourceFolder, int timeout, String... textsToSearchPatterns) {
		return waitForFilesAppearance(sourceFolder, null, timeout, textsToSearchPatterns);
	}

	/**
	 * Wait for file(s) appearance with <b>fileExtension</b> containing all text patterns from <b>textsToSearchPatterns</b> array and sorted by modification date (latest one comes first)
	 *
	 * @param sourceFolder          folder where file(s) search will be performed
	 * @param fileExtension         file extension filter, no filter will be used if value is null
	 * @param textsToSearchPatterns texts to be searched patterns. If array is empty then search by file extension only.
	 *                              If <b>fileExtension</b> is also not provided (value is null) then method will return all existing files from <b>sourceFolder</b> sorted by modification date
	 * @param timeoutInSeconds      timeout in seconds
	 * @return list of absolute paths of found files in chronological order (latest one comes first)
	 * @throws AssertionError if no files where found within provided timeout
	 */
	public static List<String> waitForFilesAppearance(String sourceFolder, String fileExtension, int timeoutInSeconds, String... textsToSearchPatterns) {
		long conditionCheckPoolingIntervalInSeconds = 1;
		StringBuilder grepCmd = new StringBuilder();
		for (String textToSearch : textsToSearchPatterns) {
			grepCmd.append(" | xargs -r grep -li '").append(textToSearch).append("'");
		}
		String cmd = String.format("cd %1$s; find . -type f -iname '*.%2$s' -print%3$s | xargs -r ls -t | xargs -r readlink -f", sourceFolder,
				fileExtension == null ? "*" : fileExtension, grepCmd.toString());
		String searchParams = String.format("%1$s%2$s in \"%3$s\" folder with %4$s seconds timeout.",
				fileExtension != null ? String.format(" with file extension \"%s\"", fileExtension) : "",
				textsToSearchPatterns.length > 0 ? String.format(" containing text pattern(s): %s", Arrays.asList(textsToSearchPatterns)) : "",
				sourceFolder, timeoutInSeconds);
		
		log.info("Searching for file(s) {}", searchParams);
		long searchStart = System.currentTimeMillis();
		long timeout = searchStart + timeoutInSeconds * 1000;
		String commandOutput = "";
		do {
			if (!(commandOutput = executeCommand(cmd)).isEmpty()) {
				break;
			}
			try {
				TimeUnit.SECONDS.sleep(conditionCheckPoolingIntervalInSeconds);
			} catch (InterruptedException e) {
				log.debug(e.getMessage());
			}
		}
		while (timeout > System.currentTimeMillis());
		long searchTime = System.currentTimeMillis() - searchStart;

		CustomAssert.assertTrue("No files have been found" + searchParams, !commandOutput.isEmpty());
		List<String> foundFiles = Arrays.asList(commandOutput.split("\n"));
		log.info(String.format("Found file(s): %1$s after %2$s milliseconds", foundFiles, searchTime));
		return foundFiles;
	}

	@SuppressWarnings("unchecked")
	public static synchronized void verifyFolderIsEmpty(String source) {
		source = getSession().parseFileName(source);
		try {
			Vector<ChannelSftp.LsEntry> list = getSession().getSftpChannel().ls("*");
			CustomAssert.assertTrue("SSH: Folder should be empty", list.isEmpty());
		} catch (SftpException | RuntimeException e) {
			throw new IstfException("SSH: Folder '" + source + "' doesn't exist.", e);
		}
	}
}
