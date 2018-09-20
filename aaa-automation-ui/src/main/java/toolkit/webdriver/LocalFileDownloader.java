package toolkit.webdriver;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;

/**
 * File locator for files downloaded by browser launched locally.
 * Does not actually download anything, only constructs an instance of a File from specified path parts. 
 */
public class LocalFileDownloader implements Function<String, Optional<File>> {
	@Override
	public Optional<File> apply(String fileName) {
		String downloadsDir = PropertyProvider.getProperty(TestProperties.BROWSER_DOWNLOAD_FILES_LOCATION);
		if (StringUtils.isBlank(downloadsDir)) {
			return Optional.empty();
		} else {
			String firstFound = Arrays.stream(new File(downloadsDir).list(new WildcardFileFilter(fileName)))
					.findFirst().orElse(fileName);
			return Optional.of(new File(downloadsDir, firstFound));
		}
	}
}