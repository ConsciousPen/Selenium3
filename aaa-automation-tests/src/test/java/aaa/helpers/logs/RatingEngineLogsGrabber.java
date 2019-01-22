package aaa.helpers.logs;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.xml.XmlTest;
import aaa.config.CsaaTestProperties;
import aaa.helpers.openl.OpenLTestsManager;
import aaa.helpers.ssh.RemoteHelper;
import aaa.helpers.ssh.Ssh;
import toolkit.config.PropertyProvider;
import toolkit.utils.logging.CustomLogger;

public class RatingEngineLogsGrabber {

	public static final String RATING_REQUEST_TEST_CONTEXT_ATTR_NAME = "ratingRequestLog";
	public static final String RATING_RESPONSE_TEST_CONTEXT_ATTR_NAME = "ratingResponseLog";
	public static final String OPENL_RATING_LOGS_FOLDER = PropertyProvider.getProperty(CsaaTestProperties.OPENL_RATING_LOGS_FOLDER);
	public static final Pattern OPENL_RATING_LOGS_FILENAME_PATTERN = Pattern.compile(PropertyProvider.getProperty(CsaaTestProperties.OPENL_RATING_LOGS_FILENAME_REGEXP));
	public static final Pattern RATING_LOG_SECTION_ID_PATTERN = Pattern.compile("ID:\\s(\\d+)\\R", Pattern.MULTILINE);
	public static final Pattern RATING_LOG_SECTION_POLICY_PREMIUM_ADDRESS_PATTERN = Pattern.compile(".*Address:\\s(.*/[dD]eterminePolicyPremium(?:\\d+)?)\\R.*", Pattern.DOTALL);

	private static final String LOG_SECTIONS_SEPARATOR = "--------------------------------------";
	private static final String LOG_END_ENVIRONMENT_SEPARATOR = "************* End Display Current Environment *************"; // may exist in WebSphere server logs
	private static final String JSON_START_MARKER = "Payload:";
	private static final Logger LOG = LoggerFactory.getLogger(RatingEngineLogsGrabber.class);

	public String makeDefaultOpenLRequestLogPath(XmlTest openLTest, int openLPolicyNumber) {
		return makeDefaultOpenLLogPath(openLTest, openLPolicyNumber, "request");
	}

	public String makeDefaultOpenLResponseLogPath(XmlTest openLTest, int openLPolicyNumber) {
		return makeDefaultOpenLLogPath(openLTest, openLPolicyNumber, "response");
	}

	public RatingEngineLogsHolder grabRatingLogs() {
		RatingEngineLogsHolder ratingLogsHolder = new RatingEngineLogsHolder();
		StringBuilder previousLogSectionParts = new StringBuilder();

		try {
			List<String> ratingLogFileNames = RemoteHelper.get().getFolderContent(OPENL_RATING_LOGS_FOLDER, true, Ssh.SortBy.DATE_MODIFIED);
			ratingLogFileNames.removeIf(f -> !OPENL_RATING_LOGS_FILENAME_PATTERN.matcher(f).matches());
			if (ratingLogFileNames.isEmpty()) {
				LOG.warn("No rating engine log files were found in \"{}\" folder", OPENL_RATING_LOGS_FOLDER);
				return ratingLogsHolder;
			}

			Collections.reverse(ratingLogFileNames); // make reverse order to start searching for response log from oldest file
			for (String logFileName : ratingLogFileNames) {
				String logContent = RemoteHelper.get().getFileContent(Paths.get(OPENL_RATING_LOGS_FOLDER, logFileName).normalize().toString()).trim();
				if (logContent.contains(LOG_END_ENVIRONMENT_SEPARATOR)) {
					logContent = StringUtils.substringAfter(logContent, LOG_END_ENVIRONMENT_SEPARATOR).trim();
				}

				previousLogSectionParts = gatherLogs(logContent, previousLogSectionParts, ratingLogsHolder);
				if (!ratingLogsHolder.getRequestLog().getLogContent().isEmpty() && !ratingLogsHolder.getResponseLog().getLogContent().isEmpty()) {
					break;
				}
			}
		} catch (Throwable e) {
			LOG.error("Unable to retrieve rating engine logs", e);
		}

		if (ratingLogsHolder.getRequestLog().getLogContent().isEmpty()) {
			LOG.warn("Rating engine requst log is empty or was not found");
		}

		if (ratingLogsHolder.getResponseLog().getLogContent().isEmpty()) {
			LOG.warn("Rating engine response log is empty or was not found");
		}

		return ratingLogsHolder;
	}

	private StringBuilder gatherLogs(String logContent, StringBuilder previousLogSectionParts, RatingEngineLogsHolder ratingLogsHolder) {
		while (true) {
			String logSectionId = null;
			String logSectionPart = StringUtils.substringAfterLast(logContent, LOG_SECTIONS_SEPARATOR).trim();

			if (!logSectionPart.isEmpty() || logContent.endsWith(LOG_SECTIONS_SEPARATOR)) {
				logSectionPart = logSectionPart.replaceAll("(INFO|WARN|DEBUG|ERROR)\\s+\\|\\s+jvm\\s+\\d+\\s+\\|\\s+\\d{4}\\/\\d{2}\\/\\d{2}\\s\\d{2}:\\d{2}:\\d{2}\\s+\\|\\s+", "");
				logSectionPart = logSectionPart.replaceAll("(INFO|WARN|DEBUG|ERROR)\\s+\\|\\s+wrapper\\s+\\s+\\|\\s+\\d{4}\\/\\d{2}\\/\\d{2}\\s\\d{2}:\\d{2}:\\d{2}\\s+\\|.*", "");
				previousLogSectionParts.insert(0, logSectionPart);
				String fullSectionLog = previousLogSectionParts.toString().trim();
				if (fullSectionLog.isEmpty()) {
					logContent = StringUtils.removeEnd(logContent, LOG_SECTIONS_SEPARATOR).trim();
					logContent = StringUtils.substringBeforeLast(logContent, "}").trim() + "}";
					continue;
				}
				String json = StringUtils.substringAfter(fullSectionLog, JSON_START_MARKER).trim();

				Matcher ratingLogSectionIdMatcher = RATING_LOG_SECTION_ID_PATTERN.matcher(fullSectionLog);
				while (ratingLogSectionIdMatcher.find()) {
					logSectionId = ratingLogSectionIdMatcher.group(1);
				}
				if (logSectionId == null) {
					LOG.warn("Unable to retrieve section log ID");
					logSectionId = "UNKNOWN_SECTION_ID";
				}

				if (fullSectionLog.contains("Outbound Message")) {
					ratingLogsHolder.setResponseLog(json, logSectionId);
				} else if (fullSectionLog.contains("Inbound Message")) {
					Matcher ratingLogSectionAddressMatcher = RATING_LOG_SECTION_POLICY_PREMIUM_ADDRESS_PATTERN.matcher(fullSectionLog);
					if (ratingLogSectionAddressMatcher.matches()) {
						if (Objects.equals(ratingLogsHolder.getResponseLog().getLogSectionId(), logSectionId)) {
							ratingLogsHolder.setRequestLog(json, logSectionId);
							return previousLogSectionParts;
						}
						LOG.warn("Request and Response log sections has different IDs, request section ID is \"{}\", response section ID is \"{}\"", logSectionId, ratingLogsHolder.getResponseLog().getLogSectionId());
					}

					ratingLogsHolder.setResponseLog("", "");
				} else {
					LOG.warn("Unknown rating log secton detected. Continue searching request/response parts");
				}

				logContent = StringUtils.substringBeforeLast(logContent, LOG_SECTIONS_SEPARATOR).trim();
				logContent = StringUtils.substringBeforeLast(logContent, "}").trim() + "}";
				previousLogSectionParts = new StringBuilder();
				continue;
			}

			return previousLogSectionParts.insert(0, logContent.trim());
		}
	}

	private String makeDefaultOpenLLogPath(XmlTest openLTest, int openLPolicyNumber, String logPostfix) {
		return Paths.get(CustomLogger.getLogDirectory() + File.separator + "openl", OpenLTestsManager.getFilePath(openLTest) + "_" + openLPolicyNumber + "_" + logPostfix + ".log")
				.normalize().toFile().toString();
	}
}
