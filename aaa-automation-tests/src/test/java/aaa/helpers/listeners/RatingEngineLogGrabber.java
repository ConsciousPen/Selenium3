package aaa.helpers.listeners;

import java.io.File;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.xml.XmlTest;
import aaa.helpers.config.CustomTestProperties;
import aaa.helpers.openl.OpenLTestsManager;
import aaa.helpers.ssh.RemoteHelper;
import toolkit.config.PropertyProvider;
import toolkit.utils.logging.CustomLogger;

public class RatingEngineLogGrabber {

	public static final String RATING_REQUEST_TEST_CONTEXT_ATTR_NAME = "ratingRequestLog";
	public static final String RATING_RESPONSE_TEST_CONTEXT_ATTR_NAME = "ratingResponseLog";
	public static final String OPENL_RATING_LOGS_FOLDER = PropertyProvider.getProperty(CustomTestProperties.OPENL_RATING_LOGS_FOLDER);
	public static final String OPENL_RATING_LOGS_FILENAME_REGEXP = PropertyProvider.getProperty(CustomTestProperties.OPENL_RATING_LOGS_FILENAME_TEMPLATE).replaceAll("%s", ".*");
	private static Logger log = LoggerFactory.getLogger(RatingEngineLogGrabber.class);

	private String logFilePostfix = ".log";

	public File grabRatingRequestLog(XmlTest openLTest, int openLPolicyNumber, boolean archiveLog) {
		File logDestination = Paths.get(CustomLogger.getLogDirectory(), "openl", OpenLTestsManager.getFilePath(openLTest), "_" + openLPolicyNumber + "_request" + logFilePostfix)
				.normalize().toFile();
		return grabRatingRequestLog(logDestination, archiveLog);
	}

	public File grabRatingRequestLog(File ratingRequestLogDestination, boolean archiveLog) {
		try {
			if (ratingRequestLogDestination.getParentFile().mkdirs()) {
				log.info("Directory \"{}\" was created", ratingRequestLogDestination.getAbsolutePath());
			}

			Pattern pattern = Pattern.compile(OPENL_RATING_LOGS_FILENAME_REGEXP);
			List<String> ratingLogFileNames = RemoteHelper.get().getListOfFiles(OPENL_RATING_LOGS_FOLDER);
			ratingLogFileNames.removeIf(f -> !pattern.matcher(f).matches());
			ratingLogFileNames = ratingLogFileNames.stream().sorted(Comparator.comparing((String f) -> RemoteHelper.get().getLastModifiedTime(Paths.get(OPENL_RATING_LOGS_FOLDER, f).toString()))
					.reversed()).collect(Collectors.toList());
			for (String ratingLog : ratingLogFileNames) {
				//TODO-dchubkov: to be implemented
			}

		} catch (Exception e) {
			log.info(String.format("Can't retrieve \"%s\" rating request log", ratingRequestLogDestination), e);
		}
		return ratingRequestLogDestination;
	}

	public File grabRatingResponseLog(XmlTest openLTest, int openLPolicyNumber, boolean archiveLog) {
		File logDestination = Paths.get(CustomLogger.getLogDirectory(), "openl", OpenLTestsManager.getFilePath(openLTest), "_" + openLPolicyNumber + "_response" + logFilePostfix)
				.normalize().toFile();
		return grabRatingResponseLog(logDestination, archiveLog);
	}

	public File grabRatingResponseLog(File ratingResponseLogDestination, boolean archiveLog) {
		//TODO-dchubkov: to be implemented
		return ratingResponseLogDestination;
	}
}

