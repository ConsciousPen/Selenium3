package aaa.helpers.listeners;

import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.helpers.config.CustomTestProperties;
import aaa.helpers.ssh.RemoteHelper;
import toolkit.config.PropertyProvider;

public class RatingEngineLogsGrabber {

	public static final String RATING_REQUEST_TEST_CONTEXT_ATTR_NAME = "ratingRequestLog";
	public static final String RATING_RESPONSE_TEST_CONTEXT_ATTR_NAME = "ratingResponseLog";
	public static final String OPENL_RATING_LOGS_FOLDER = PropertyProvider.getProperty(CustomTestProperties.OPENL_RATING_LOGS_FOLDER);
	public static final String OPENL_RATING_LOGS_FILENAME_REGEXP = PropertyProvider.getProperty(CustomTestProperties.OPENL_RATING_LOGS_FILENAME_REGEXP);
	private static final String LOG_SECTIONS_SEPARATOR = "--------------------------------------";
	private static final String LOG_END_ENVIRONMENT_SEPARATOR = "************* End Display Current Environment *************"; // occurs in WebSphere server logs
	private static Logger log = LoggerFactory.getLogger(RatingEngineLogsGrabber.class);

	public RatingEngineLogsHolder grabRatingLogs() {
		RatingEngineLogsHolder ratingLogsHolder = new RatingEngineLogsHolder();
		boolean isRequestLogContentCollected = false;
		boolean isResponseLogContentCollected = false;
		StringBuilder fullLogContent = new StringBuilder();

		try {
			Pattern pattern = Pattern.compile(OPENL_RATING_LOGS_FILENAME_REGEXP);
			List<String> ratingLogFileNames = RemoteHelper.get().getListOfFiles(OPENL_RATING_LOGS_FOLDER);
			ratingLogFileNames.removeIf(f -> !pattern.matcher(f).matches());
			ratingLogFileNames = ratingLogFileNames.stream().sorted(Comparator.comparing((String f) -> RemoteHelper.get().getLastModifiedTime(Paths.get(OPENL_RATING_LOGS_FOLDER, f).toString()))
					.reversed()).collect(Collectors.toList());

			for (String ratingLog : ratingLogFileNames) {
				String content = RemoteHelper.get().getFileContent(Paths.get(OPENL_RATING_LOGS_FOLDER, ratingLog).normalize().toString());
				content = StringUtils.substringAfter(content, LOG_END_ENVIRONMENT_SEPARATOR).trim();
				content = StringUtils.removeEnd(content, LOG_SECTIONS_SEPARATOR);

				String logPart = StringUtils.substringAfterLast(content, "Payload:\n");
				if (!logPart.isEmpty()) {
					fullLogContent.insert(0, logPart);

					if (isResponseLogContentCollected) {
						ratingLogsHolder.setRatingRequestLogContent(fullLogContent.toString());
						isRequestLogContentCollected = true;
						break;
					}
					ratingLogsHolder.setRatingResponseLogContent(fullLogContent.toString());

					content = StringUtils.removeEnd(content, logPart);
					content = StringUtils.substringBeforeLast(content, LOG_SECTIONS_SEPARATOR);
					logPart = StringUtils.substringAfterLast(content, "Payload:\n");
					if (!logPart.isEmpty()) {
						ratingLogsHolder.setRatingRequestLogContent(fullLogContent.toString());
						isRequestLogContentCollected = true;
						break;
					}
					fullLogContent = new StringBuilder(content);
					isResponseLogContentCollected = true;
				} else {
					fullLogContent.insert(0, content);
				}
			}
		} catch (Exception e) {
			log.error("Can't retrieve rating logs", e);
		}

		if (!isRequestLogContentCollected) {
			log.warn("...................");
		}

		if (!isResponseLogContentCollected) {
			log.warn("...................");
		}

		return ratingLogsHolder;
	}
}

