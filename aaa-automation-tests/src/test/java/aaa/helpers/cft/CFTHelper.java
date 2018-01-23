package aaa.helpers.cft;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import toolkit.config.PropertyProvider;
import toolkit.utils.SSHController;
import aaa.modules.BaseTest;

import com.exigen.istf.exec.testng.TimeShiftTestUtil;

public class CFTHelper extends BaseTest {

	public static void checkDirectory(File directory) throws IOException {
		if (directory.mkdirs()) {
			log.info("\"{}\" folder was created", directory.getAbsolutePath());
		} else {
			FileUtils.cleanDirectory(directory);
		}
	}

	public static SSHController getSSHController() {
		String monitorInfo = TimeShiftTestUtil.getContext().getBrowser().toString();
		String monitorAddress = monitorInfo.substring(monitorInfo.indexOf("selenium=") + 9, monitorInfo.indexOf(":"));
		return new SSHController(monitorAddress,
			PropertyProvider.getProperty("test.ssh.user"),
			PropertyProvider.getProperty("test.ssh.password"));
	}
}
