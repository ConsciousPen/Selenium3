package aaa.helpers.cft;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import aaa.modules.BaseTest;

public class CFTHelper extends BaseTest {

	public static void checkDirectory(File directory) throws IOException {
		if (directory.mkdirs()) {
			log.info("\"{}\" folder was created", directory.getAbsolutePath());
		} else {
			FileUtils.cleanDirectory(directory);
		}
	}
}
