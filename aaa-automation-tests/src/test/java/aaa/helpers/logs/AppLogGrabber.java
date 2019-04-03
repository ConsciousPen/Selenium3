package aaa.helpers.logs;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.utils.logging.CustomLogger;
import toolkit.utils.teststoragex.utils.TestNGUtils;

public class AppLogGrabber {

	private static Logger log = LoggerFactory.getLogger(com.exigen.ipb.etcsa.utils.listener.AppLogGrabber.class);

	private String appUrl = "http://" + PropertyProvider.getProperty(TestProperties.APP_HOST);

	private String logFilePostfix = ".log";

	private List<String> urlParts = Arrays.asList(
			PropertyProvider.getProperty("e2e.logs","/user-logs/e2e/"));

	private String getTestLogName(ITestResult result) {
		String methodName = result.getTestClass().getName() + "." + result.getMethod().getMethodName();
		String testParameters = TestNGUtils.getTestParameters(result);
		if (testParameters != null) {
			methodName += "_" + testParameters.split(", ")[0];
		}
		return methodName;
	}

	private int getResponseCode(URL url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();
		return connection.getResponseCode();
	}

	private URL getUrl(String testClassName, String typeUrl) throws MalformedURLException {
		return new URL(appUrl + typeUrl + testClassName + logFilePostfix);
	}

	public String grabAppLog(ITestResult result) {
		String methodName = getTestLogName(result);
		try {
			File testAppLogFile = Paths.get(CustomLogger.getLogDirectory(), "app", methodName + logFilePostfix).toFile();
			if (testAppLogFile.getParentFile().mkdirs()) {
				log.info("Directory '" + testAppLogFile.getAbsolutePath() + "' was created");
			}
			for (String urlPart : urlParts) {
				if (getResponseCode(getUrl(methodName, urlPart)) != 404) {
					FileUtils.copyURLToFile(getUrl(methodName, urlPart), testAppLogFile);
					return testAppLogFile.getAbsolutePath();
				}
			}
		} catch (IOException e) {
			log.info(String.format("Can't retrieve %s application log: %s", methodName, e));
		}
		return null;
	}
}

