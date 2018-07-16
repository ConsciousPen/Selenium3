package aaa.helpers.listeners;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.xml.XmlTest;
import aaa.helpers.config.CustomTestProperties;
import aaa.helpers.openl.OpenLTestsManager;
import aaa.helpers.ssh.RemoteHelper;
import aaa.helpers.ssh.Ssh;
import toolkit.config.PropertyProvider;
import toolkit.utils.logging.CustomLogger;

public class RatingEngineLogsGrabber {

	public static final String RATING_REQUEST_TEST_CONTEXT_ATTR_NAME = "ratingRequestLog";
	public static final String RATING_RESPONSE_TEST_CONTEXT_ATTR_NAME = "ratingResponseLog";
	public static final String OPENL_RATING_LOGS_FOLDER = PropertyProvider.getProperty(CustomTestProperties.OPENL_RATING_LOGS_FOLDER);
	public static final String OPENL_RATING_LOGS_FILENAME_REGEXP = PropertyProvider.getProperty(CustomTestProperties.OPENL_RATING_LOGS_FILENAME_REGEXP);

	private static final String LOG_SECTIONS_SEPARATOR = "--------------------------------------";
	private static final String LOG_END_ENVIRONMENT_SEPARATOR = "************* End Display Current Environment *************"; // may exist in WebSphere server logs
	private static final String LOG_START_MARKER = "Payload:";
	private static Logger log = LoggerFactory.getLogger(RatingEngineLogsGrabber.class);

	public String makeDefaultOpenLRequestLogPath(XmlTest openLTest, int openLPolicyNumber) {
		return makeDefaultOpenLLogPath(openLTest, openLPolicyNumber, "request");
	}

	public String makeDefaultOpenLResponseLogPath(XmlTest openLTest, int openLPolicyNumber) {
		return makeDefaultOpenLLogPath(openLTest, openLPolicyNumber, "response");
	}

	public RatingEngineLogsHolder grabRatingLogs() {
		RatingEngineLogsHolder ratingLogs = new RatingEngineLogsHolder();
		boolean isRequestLogRetrieved = false;
		boolean isResponseLogRetrieved = false;
		StringBuilder completeLogContent = new StringBuilder();

		try {
			Pattern ratingLogFilenamePattern = Pattern.compile(OPENL_RATING_LOGS_FILENAME_REGEXP);
			List<String> ratingLogFileNames = RemoteHelper.get().getFolderContent(OPENL_RATING_LOGS_FOLDER, true, Ssh.SortBy.DATE_MODIFIED);
			ratingLogFileNames.removeIf(f -> !ratingLogFilenamePattern.matcher(f).matches());
			if (ratingLogFileNames.isEmpty()) {
				log.warn("No rating engine log files were found in \"{}\" folder", OPENL_RATING_LOGS_FOLDER);
				return ratingLogs;
			}

			Collections.reverse(ratingLogFileNames); // make reverse order to start searching for response log from oldest file

			//TODO-dchubkov: maybe need to refactor this algorithm to make it more clear
			for (int i = 0; i < ratingLogFileNames.size(); i++) {
				String logContent = RemoteHelper.get().getFileContent(Paths.get(OPENL_RATING_LOGS_FOLDER, ratingLogFileNames.get(i)).normalize().toString());
				if (logContent.contains(LOG_END_ENVIRONMENT_SEPARATOR)) {
					logContent = StringUtils.substringAfter(logContent, LOG_END_ENVIRONMENT_SEPARATOR);
				}

				if (i == 0) {
					//first log from list (which is oldest one) always has log separator at the end
					logContent = StringUtils.substringBeforeLast(logContent, LOG_SECTIONS_SEPARATOR);
				}

				String logPart = StringUtils.substringAfterLast(logContent, LOG_START_MARKER).trim();
				if (!logPart.isEmpty()) {
					completeLogContent.insert(0, logPart);

					if (isResponseLogRetrieved) {
						ratingLogs.setRatingRequestLogContent(completeLogContent.toString());
						isRequestLogRetrieved = true;
						break;
					}

					ratingLogs.setRatingResponseLogContent(completeLogContent.toString());
					isResponseLogRetrieved = true;

					logContent = StringUtils.substringBeforeLast(logContent, logPart);
					logContent = StringUtils.substringBeforeLast(logContent, LOG_SECTIONS_SEPARATOR);
					logPart = StringUtils.substringAfterLast(logContent, LOG_START_MARKER).trim();
					if (!logPart.isEmpty()) { // request and response log parts exist in same file
						ratingLogs.setRatingRequestLogContent(logPart);
						isRequestLogRetrieved = true;
						break;
					}
					completeLogContent = new StringBuilder(logContent.trim());
				} else {
					completeLogContent.insert(0, logContent.trim());
				}
			}
		} catch (Throwable e) {
			log.error("Unable to retrieve rating engine logs", e);
		}

		if (!isRequestLogRetrieved) {
			log.warn("Rating engine requst log is empty or was not found");
		}

		if (!isResponseLogRetrieved) {
			log.warn("Rating engine response log is empty or was not found");
		}

		return ratingLogs;
	}

	private String makeDefaultOpenLLogPath(XmlTest openLTest, int openLPolicyNumber, String logPostfix) {
		return Paths.get(CustomLogger.getLogDirectory() + File.separator + "openl", OpenLTestsManager.getFilePath(openLTest) + "_" + openLPolicyNumber + "_" + logPostfix + ".log")
				.normalize().toFile().toString();
	}
}

