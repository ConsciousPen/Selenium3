package toolkit.webdriver;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.rholder.retry.*;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * File downloader from Selenoid/Ggr
 */
public class SelenoidFileDownloader implements Function<String, Optional<File>> {
	private static final Logger log = LoggerFactory.getLogger(SelenoidFileDownloader.class);

	private final Optional<URL> hubUrl;
	private final Optional<String> authEncoded;
	private final Retryer<File> retryer;

	public SelenoidFileDownloader() {
		URL tmpUrl = null;
		String tmpAuthEncoded = null;

		try {
			tmpUrl = new URL(PropertyProvider.getProperty(TestProperties.WEBDRIVER_HUB_URL));
			if (tmpUrl.getPath().equals("/wd/hub")) {
				String auth = tmpUrl.getUserInfo();
				if (!StringUtils.isBlank(auth)) {
					byte[] authEncBytes = Base64.encodeBase64(auth.getBytes());
					tmpAuthEncoded = new String(authEncBytes);
				}
			} else {
				log.error("URL " + tmpUrl + " is not a Selenoid/Ggr URL");
				tmpUrl = null;
			}
		} catch (MalformedURLException e) {
			log.warn("Failed to build hub URL", e);
			tmpUrl = null;
		}
		hubUrl = Optional.ofNullable(tmpUrl);
		authEncoded = Optional.ofNullable(tmpAuthEncoded);

		retryer = RetryerBuilder.<File>newBuilder()
				.retryIfExceptionOfType(FileNotFoundException.class)
				.withWaitStrategy(WaitStrategies.fixedWait(20, TimeUnit.SECONDS))
				.withStopStrategy(StopStrategies.stopAfterAttempt(3))
				.build();
	}

	@Override
	public Optional<File> apply(String fileName) {
		Waiters.SLEEP(1000L).go();    //	wait a little so perhaps no retries will be needed
		return BrowserController.getSessionId().flatMap(sid -> {
			return hubUrl.map(baseUrl -> {
				String path = String.format("/download/%1$s", sid.toString());
				URL url;
				try {
					url = new URL(baseUrl, path);
				} catch (MalformedURLException e) {
					log.error("Failed to build file download URL", e);
					return null;
				}
				try {
					log.debug("Downloading file by URL " + url);
					return getFile(url, fileName);
				} catch (Exception e) {
					log.error("Failed to download file by URL " + url, e);
					return null;
				}
			});
		});
	}

	protected File getFile(final URL url, final File file) throws ExecutionException, RetryException, IOException {
		return retryer.call(() -> {
			URLConnection urlConnection = url.openConnection();
			urlConnection.setReadTimeout(PropertyProvider.getProperty(TestProperties.BROWSER_FILE_DOWNLOAD_TIMEOUT, 30000));
			authEncoded.ifPresent(auth -> urlConnection.setRequestProperty("Authorization", "Basic " + auth));
			InputStream is = urlConnection.getInputStream();
			FileUtils.copyInputStreamToFile(is, file);
			return file;
		});
	}

	protected File getFile(final URL url, final String fileName) throws ExecutionException, RetryException, IOException {
		return retryer.call(() -> {
			String actualFileName = fileName;
			if (fileName.contains("?") || fileName.contains("*")) {
				AbstractList<String> availableFiles = getFileNames(url, fileName);
				if (availableFiles.size() > 0) {
					actualFileName = availableFiles.get(0);
				}
			}
			URL urlToFile = new URL(url, String.format("%1$s/%2$s", url.getPath(), actualFileName));
			URLConnection urlConnection = urlToFile.openConnection();
			urlConnection.setReadTimeout(PropertyProvider.getProperty(TestProperties.BROWSER_FILE_DOWNLOAD_TIMEOUT, 30000));
			authEncoded.ifPresent(auth -> urlConnection.setRequestProperty("Authorization", "Basic " + auth));
			InputStream is = urlConnection.getInputStream();
			File file = new File(System.getProperty("java.io.tmpdir"), generateWildcardFileName(fileName));
			FileUtils.copyInputStreamToFile(is, file);
			return file;
		});
	}

	private AbstractList<String> getFileNames(final URL url, final String fileName) throws ExecutionException, RetryException, IOException {
		URLConnection urlConnection = url.openConnection();
		urlConnection.setReadTimeout(PropertyProvider.getProperty(TestProperties.BROWSER_FILE_DOWNLOAD_TIMEOUT, 30000));
		authEncoded.ifPresent(auth -> urlConnection.setRequestProperty("Authorization", "Basic " + auth));
		InputStream is = urlConnection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		ArrayList<String> fileList = new ArrayList<String>();
		reader.lines().filter(s -> s.matches("<a.*>.*</a>")).forEach(s -> fileList.add(s.replaceAll("</*a[^>]*>", "")));
		fileList.removeIf(s -> !FilenameUtils.wildcardMatch(s, fileName));
		return fileList;
	}

	private String generateWildcardFileName(String fileName) {
		return fileName.replace("?", "#").replace("*", "#");
	}

}

