package aaa.helpers.ssh;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static toolkit.verification.CustomAssertions.assertThat;
import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import toolkit.exceptions.IstfException;

public final class RemoteHelper {

	private static final Logger log = LoggerFactory.getLogger(RemoteHelper.class);
	private static Map<ConnectionParams, RemoteHelper> sshConnections = new HashMap<>();

	private ConnectionParams connectionParams;
	private Ssh ssh;

	private RemoteHelper(ConnectionParams connectionParams) {
		this.connectionParams = connectionParams;
		log.info("Establishing remote connection with {}", connectionParams);
		this.ssh = new Ssh(connectionParams);
	}

	public String getServerTimeZone() {
		String cmd = "timedatectl | grep -oP 'Time zone: \\K.*(?= \\()'";
		return executeCommand(cmd).getOutput();
	}

	public static RemoteHelper get() {
		return get(ConnectionParams.DEFAULT);
	}

	public static ConnectionParams with() {
		return new ConnectionParams();
	}

	static synchronized RemoteHelper get(ConnectionParams cp) {
		if (!sshConnections.containsKey(cp)) {
			RemoteHelper remoteHelper = new RemoteHelper(cp);
			sshConnections.put(cp, remoteHelper);
			return remoteHelper;
		}
		return sshConnections.get(cp);
	}

	public RemoteHelper clearFolder(List<String> folderNames) {
		for (String folder : folderNames) {
			clearFolder(folder);
		}
		return this;
	}

	public RemoteHelper clearFolder(String folder, Boolean includeSubFolders, Boolean onlyFiles) {
		if (isPathExist(folder)) {
			ssh.removeFiles(folder, includeSubFolders, onlyFiles);
		} else {
			log.warn("SSH: Folder \"{}\" doesn't exist.", folder);
		}
		return this;
	}

	public RemoteHelper clearFolder(String folder) {
		if (isPathExist(folder)) {
			ssh.removeFiles(folder);
		} else {
			log.warn("SSH: Folder \"{}\" doesn't exist.", folder);
		}
		return this;
	}

	public synchronized RemoteHelper downloadFileWithWait(String source, String destination, long timeout) {
		log.info("SSH: File '{}' downloading to '{}' destination folder has been started.", source, destination);
		long endTime = System.currentTimeMillis() + timeout;
		while (!isPathExist(source)) {
			if (endTime < System.currentTimeMillis()) {
				throw new AssertionError(String.format("File '%s' wasn't found after %s ms of wait", source, timeout));
			}
			try {
				TimeUnit.MILLISECONDS.sleep(2000);
			} catch (InterruptedException e) {
				throw new IstfException(e);
			}
		}
		ssh.downloadFile(source, destination);
		return this;
	}

	public RemoteHelper downloadFile(String source, String destination) {
		log.info("SSH: File '{}' downloading to '{}' destination folder has been started.", source, destination);
		ssh.downloadFile(source, destination);
		return this;
	}

	public RemoteHelper downloadBatchFiles(String source, File destination) {
		log.info("SSH: Files downloading from '{}' has been started,", source);
		ssh.downloadBatchFile(source, destination);
		return this;
	}

	public synchronized RemoteHelper uploadFile(String source, String destination) {
		assertThat(source).as("Source file is NULL").isNotNull();
		log.info("SSH: File '{}' uploading to '{}' destination folder has been started.", source, destination);
		//		Folders creation moved to Ssh class and made with sftp commands
		//		File destinationFile = File destinationFile = new File(destination);;
		//		if (!isPathExist(destinationFile.getParent())) {
		//			executeCommand("mkdir -p -m 777 " + ssh.parseFileName(destinationFile.getParent()));
		//			if (destinationFile.getParentFile().getParentFile() != null) {
		//				executeCommand("chmod -R 777 " + ssh.parseFileName(destinationFile.getParentFile().getParent()));
		//			}
		//		}
		ssh.putFile(source, destination);
		return this;
	}

	public RemoteHelper removeFile(String file) {
		log.info("SSH: File '{}' deleting has been started.", file);
		ssh.removeFile(file);
		return this;
	}

	public RemoteHelper uploadFiles(String sourceFolder, String destinationFolder) {
		File directory = new File(sourceFolder);
		File[] files = directory.listFiles(File::isFile);
		log.info("SSH: Files uploading from '{}' folder to '{}' destination folder has been started.", sourceFolder, destinationFolder);
		if (files != null && files.length != 0) {
			for (File file : files) {
				uploadFile(file.getAbsolutePath(), Paths.get(destinationFolder, file.getName()).normalize().toString());
			}
		}
		log.info("SSH: All files from '{}' folder were uploaded to '{}' destination folder.", sourceFolder, destinationFolder);
		return this;
	}

	public CommandResults executeCommand(String command) {
		return executeCommand(command, ExecutionParams.DEFAULT);
	}

	public CommandResults executeCommand(String command, ExecutionParams execParams) {
		log.info("SSH: Executing on host \"{}\" shell command: \"{}\" as user \"{}\" with {}", connectionParams.getHost(), command, connectionParams.getUser(), execParams);
		return ssh.executeCommand(command, execParams);
	}

	public boolean isPathExist(String path) {
		SftpATTRS attrs = null;
		path = ssh.parseFileName(path);

		try {
			ChannelSftp channel = ssh.getSftpChannel();
			attrs = channel.stat(path);
		} catch (SftpException | RuntimeException e) {
			log.debug("SSH: File/folder \"{}\" doesn't exist.", path);
		}
		return attrs != null;
	}

	public RemoteHelper createFolder(String source) {
		source = ssh.parseFileName(source);

		log.info("SSH: Creating folder '{}'", source);
		try {
			executeCommand("mkdir -p " + source);
			executeCommand("chmod 777 " + source);
		} catch (RuntimeException e) {
			throw new IstfException("SSH: Folder '" + source + "' couldn't be created. " + e.getMessage());
		}
		log.info("SSH: Folder '{}' was created", source);
		return this;
	}

	public RemoteHelper closeSession() {
		ssh.closeSession();
		return this;
	}

	public List<String> getListOfFiles(String folderPath) {
		return getFolderContent(folderPath, true, null);
	}

	public List<String> getFolderContent(String folderPath, boolean filesOnly, Ssh.SortBy sortBy) {
		log.info("SSH: Getting {}content from \"{}\" folder sorted by {}", filesOnly ? "files only " : EMPTY,
				folderPath, sortBy != null ? sortBy.name() : EMPTY);
		return ssh.getFolderContent(folderPath, filesOnly, sortBy);
	}

	public String getFileContent(String filePath) {
		log.info("SSH: Getting content from \"{}\" file", filePath);
		return ssh.getFileContent(filePath);
	}

	public List<String> waitForFilesAppearance(String sourceFolder, int timeout, String... textsToSearchPatterns) {
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
	public List<String> waitForFilesAppearance(String sourceFolder, String fileExtension, int timeoutInSeconds, List<String> textsToSearchPatterns) {
		long conditionCheckPoolingIntervalInSeconds = 1;

		String searchParams = String.format("%1$s%2$s in \"%3$s\" folder with %4$s seconds timeout.",
				fileExtension != null ? String.format(" with file extension \"%s\"", fileExtension) : "",
				textsToSearchPatterns.size() > 0 ? String.format(" containing text pattern(s): %s", textsToSearchPatterns) : "",
				sourceFolder, timeoutInSeconds);

		log.info("Searching for file(s) {}", searchParams);
		long searchStart = System.currentTimeMillis();
		long timeout = searchStart + timeoutInSeconds * 1000L;
		List<String> foundFiles;
		do {
			foundFiles = getFilesListBySearchPattern(sourceFolder, fileExtension, textsToSearchPatterns);
			if (!foundFiles.isEmpty()) {
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

		assertThat(foundFiles).as("No files have been found%s", searchParams).isNotEmpty();
		log.info("Found file(s): {} after {} milliseconds", foundFiles, searchTime);
		return foundFiles;
	}

	public List<String> waitForFilesAppearance(String sourceFolder, String fileExtension, int timeoutInSeconds, String... textsToSearchPatterns) {
		return waitForFilesAppearance(sourceFolder, fileExtension, timeoutInSeconds, Arrays.asList(textsToSearchPatterns));
	}

	/**
	 * Wait for file(s) appearance with <b>fileExtension</b> containing all text patterns from <b>textsToSearchPatterns</b> array and sorted by modification date (latest one comes first)
	 *
	 * @param sourceFolder          folder where file(s) search will be performed
	 * @param textsToSearchPatterns texts to be searched patterns. If array is empty then search by file extension only.
	 *                              If <b>fileExtension</b> is also not provided (value is null) then method will return all existing files from <b>sourceFolder</b> sorted by modification date
	 * @param timeoutInSeconds      timeout in seconds
	 * @return list of absolute paths of found files in chronological order (latest one comes first)
	 * @throws AssertionError if no files where found within provided timeout
	 */
	public List<String> waitForDocAppearance(String sourceFolder, int timeoutInSeconds, String policyNum, List<String> textsToSearchPatterns) {
		log.info("Searching for file(s) for {}", policyNum);
		long searchStart = System.currentTimeMillis();
		long timeout = searchStart + timeoutInSeconds * 1000L;
		long conditionCheckPoolingIntervalInSeconds = 1;
		List<String> foundFiles;
		List<String> interimList;
		String searchParams = String.format("%1$s in \"%2$s\" folder with %3$s seconds timeout.",
				textsToSearchPatterns.size() > 0 ? String.format(" containing text pattern(s): %s", textsToSearchPatterns) : "",
				sourceFolder, timeoutInSeconds);
		do {
			interimList = getFilesListBySearchPattern(sourceFolder, "xml", textsToSearchPatterns);
			foundFiles = interimList.stream().filter(s -> s.contains(policyNum) && isTimeDiffMatch(s)).collect(Collectors.toList());
			if (!foundFiles.isEmpty()) {
				break;
			}
			try {
				TimeUnit.SECONDS.sleep(conditionCheckPoolingIntervalInSeconds);
			} catch (InterruptedException e) {
				log.error(e.getMessage());
			}
		}
		while (timeout > System.currentTimeMillis());
		long searchTime = System.currentTimeMillis() - searchStart;
		assertThat(foundFiles).as("No files have been found%s", searchParams).isNotEmpty();
		log.info("Found file(s): {} after {} milliseconds", foundFiles, searchTime);
		return foundFiles;
	}

	public LocalDateTime getLastModifiedTime(String path) {
		log.info("SSH: Getting last modified time for \"{}\"", path);
		return ssh.getLastModifiedTime(path);
	}

	public List<String> getFilesListBySearchPattern(String sourceFolder, String fileExtension, List<String> textsToSearchPatterns) {
		StringBuilder grepCmd = new StringBuilder();
		for (String textToSearch : textsToSearchPatterns) {
			grepCmd.append(" | xargs -r grep -li '").append(textToSearch).append("'");
		}
		String cmd = String.format("cd %1$s; find . -type f -iname '*.%2$s' -print%3$s | xargs -r ls -t | xargs -r readlink -f", sourceFolder,
				fileExtension == null ? "*" : fileExtension, grepCmd.toString());
		String commandOutput = executeCommand(cmd).getOutput();
		return !commandOutput.isEmpty() ? Arrays.asList(commandOutput.split("\n")) : new ArrayList<>();
	}

	private Boolean isTimeDiffMatch(String fileName) {
		long delta = 180000;
		String timestampRegex = "[A-Z]*\\d*_(\\d*)_\\d*.*.xml";
		Pattern r = Pattern.compile(timestampRegex);
		Matcher m = r.matcher(fileName);

		if (!m.find()) {
			throw new IstfException(String.format("Cannot extract regex '%1$s' from string %2$s", timestampRegex, fileName));
		}

		long date = TimeSetterUtil.getInstance().getCurrentTime().atZone(ZoneId.systemDefault()).toEpochSecond();

		return date - Long.parseLong(m.group(1)) < delta;
	}
}
