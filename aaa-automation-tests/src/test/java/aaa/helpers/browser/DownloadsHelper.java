package aaa.helpers.browser;

import java.io.File;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.config.CsaaTestProperties;
import toolkit.config.PropertyProvider;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.Downloads;

/**
 * CSAA Wrapper for {@link Downloads} class
 * Manages selenoid browser downloads.
 * */
public class DownloadsHelper {

	public static final String DOWNLOAD_DIR = PropertyProvider.getProperty(CsaaTestProperties.USER_DIR_PROP) + PropertyProvider.getProperty(CsaaTestProperties.LOCAL_DOWNLOAD_FOLDER_PROP);
	protected static Logger LOG = LoggerFactory.getLogger(DownloadsHelper.class);

	/**
	 * Downloads all files from selenoid browser download folder.
	 * */
	public static List<File> getAllFiles() {
		File dir = new File(DOWNLOAD_DIR);
		listFiles().forEach(p -> checkFile(DOWNLOAD_DIR, p));
		return Downloads.getAllFiles(dir.getAbsolutePath());
	}

	/**
	 * Downloads file by File name from selenoid browser download folder.
	 * Wildcard can be used in file path.
	 * @param fileName
	 * */
	public static Optional<File> getFile(String fileName) {
		File dir = new File(DOWNLOAD_DIR);
		checkFile(DOWNLOAD_DIR, fileName);
		return Downloads.getFile(fileName, dir.getAbsolutePath());
	}

	/**
	 * Returns List of files from selenoid browser download folder.
	 * */
	public static List<String> listFiles() {
		return Downloads.listFiles();
	}

	/**
	 * Checks whether destination folder exists and file with the same name doesn't exist on test executor
	 * */
	public static void checkFile(String dirPath, String fileName) {
		File dir = new File(dirPath);
		if (dir.mkdirs()) {
			LOG.info("\"{}\" folder was created", dir.getAbsolutePath());
		}
		File file = new File(dir.getPath() + fileName);
		if (file.exists()) {
			file.renameTo(new File(file.getName().concat(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.TIME_STAMP)).concat(".old")));
		}
	}
}
