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
import com.exigen.ipb.eisa.base.app.CSAAApplicationFactory;
import toolkit.utils.logging.CustomLogger;
import toolkit.utils.teststoragex.utils.TestNGUtils;

public class AppLogGrabber {

	private static Logger log = LoggerFactory.getLogger(com.exigen.ipb.eisa.utils.listener.AppLogGrabber.class);

	private String envURL = String.format("%1$s://%2$s", CSAAApplicationFactory.get().mainApp().getProtocol(), CSAAApplicationFactory.get().mainApp().getHost());

	private String logFilePostfix = ".log";

	private List<String> urlParts = Arrays.asList("/user-logs/e2e/", ":9999/batchjob-logs/e2e/");

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
		return new URL(envURL + typeUrl + testClassName + logFilePostfix);
	}

	public String grabAppLog(ITestResult result) {
		String methodName = getTestLogName(result);

		File testAppLogFile = Paths.get(CustomLogger.getLogDirectory(), "app", methodName + logFilePostfix).toFile();
		if (testAppLogFile.getParentFile().mkdirs()) {
			log.info("Directory '{}' was created", testAppLogFile.getAbsolutePath());
		}

		URL url = null;
		for (String urlPart : urlParts) {
			try {
				url = getUrl(methodName, urlPart);
				if (getResponseCode(url) != 404) {
					log.info("Able to retrieve application log: {}", url);
					FileUtils.copyURLToFile(getUrl(methodName, urlPart), testAppLogFile);
					return testAppLogFile.getAbsolutePath();
				}
			} catch (IOException e) {

			}
		}
		return null;
	}
}

