package aaa.helpers.browser;

import java.io.File;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.config.CsaaTestProperties;
import toolkit.config.PropertyProvider;
import toolkit.webdriver.Downloads;

public class DownloadsHelper {

	public static final String DOWNLOAD_DIR = PropertyProvider.getProperty(CsaaTestProperties.USER_DIR_PROP) + PropertyProvider.getProperty(CsaaTestProperties.LOCAL_DOWNLOAD_FOLDER_PROP);
	protected static Logger LOG = LoggerFactory.getLogger(DownloadsHelper.class);

	public static List<File> getAllFiles() {
		File dir = new File(DOWNLOAD_DIR);
		checkDirectory(dir);
		return Downloads.getAllFiles(dir.getAbsolutePath());
	}

	public static Optional<File> getFile(String fileName) {
		File dir = new File(DOWNLOAD_DIR);
		checkDirectory(dir);
		return Downloads.getFile(fileName, dir.getAbsolutePath());
	}

	public static List<String> listFiles() {
		return Downloads.listFiles();
	}

	public static void checkDirectory(File directory) {
		if (directory.mkdirs()) {
			LOG.info("\"{}\" folder was created", directory.getAbsolutePath());
		} /*else {
			try {
				FileUtils.cleanDirectory(directory);
			} catch (IOException e) {
				LOG.info("Unable to clean directory {}", directory.getAbsolutePath());
			}
		}*/
	}

	public static void checkFile(String dirPath, String fileName) {
		File directory = new File(dirPath);
		if (directory.mkdirs()) {
			LOG.info("\"{}\" folder was created", directory.getAbsolutePath());
		} else {
			FileUtils.deleteQuietly(new File(dirPath + fileName));
		}
	}
}
