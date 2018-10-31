package aaa.modules.regression.finance.operational_reports;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.exigen.istf.exec.testng.TimeShiftTestUtil;
import com.jayway.awaitility.Awaitility;
import com.jayway.awaitility.Duration;
import com.jayway.awaitility.core.ConditionTimeoutException;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import aaa.admin.modules.reports.operationalreports.OperationalReport;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.config.PropertyProvider;
import toolkit.exceptions.IstfException;
import toolkit.utils.SSHController;
import toolkit.utils.TestInfo;

public class TestOperationalReportsCreate extends PolicyBaseTest {

	private static final String REMOTE_DOWNLOAD_FOLDER_PROP = "test.remotefile.location"; // location /root/Downloads
	private static final String REMOTE_DOWNLOAD_FOLDER = "/home/autotest/Downloads";
	private static final String DOWNLOAD_DIR = System.getProperty("user.dir") + PropertyProvider.getProperty("test.downloadfiles.location");
	private static final String EXCEL_FILE_EXTENSION = "xlsx";

	/**
	 * @author Jurij Kuznecov
	 *
	 * 1. Initiate Operational Report's creating
	 * 2. Check Operational report file exists in corresponding folder
	 *
	 * for executing locally add to local.property file 'test.remotefile.location='
	 */

	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Finance.OPERATIONAL_REPORTS)
	public void testOperationalReportsCreate() throws SftpException, JSchException {

		String remoteFileLocation = PropertyProvider.getProperty(REMOTE_DOWNLOAD_FOLDER_PROP);
		String reportName = getTestSpecificTD("OperationalReport").getValue("ReportName");
		LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
		String expectedFileName = String.format("%s_%s_%s_%s.%s", reportName, today.getYear(), today.getMonth().getDisplayName(TextStyle.SHORT, Locale.US), today.getDayOfMonth(), EXCEL_FILE_EXTENSION);

		opReportApp().open();

		if (StringUtils.isNotEmpty(remoteFileLocation)) {        // execute on remote monitor
			File expectedFile = new File(REMOTE_DOWNLOAD_FOLDER + "/" + expectedFileName);
			String monitorInfo = TimeShiftTestUtil.getContext().getBrowser().toString();
			String monitorAddress = monitorInfo.substring(monitorInfo.indexOf(" ") + 1, monitorInfo.indexOf(":", monitorInfo.indexOf(" ")));
			log.info("Monitor address: {}", monitorAddress);
			SSHController sshControllerRemote = new SSHController(
					monitorAddress,
					PropertyProvider.getProperty("test.ssh.user"),
					PropertyProvider.getProperty("test.ssh.password"));
			if (sshControllerRemote.pathExists(expectedFile)) {
				sshControllerRemote.deleteFile(expectedFile);
				Awaitility.await().atMost(Duration.TWO_MINUTES).until(() -> !sshControllerRemote.pathExists(expectedFile));
			}
			new OperationalReport().create(getTestSpecificTD("TestData_CreateReport"));
			try {
				Awaitility.await().atMost(Duration.TWO_MINUTES).until(() -> sshControllerRemote.pathExists(expectedFile));
				sshControllerRemote.deleteFile(expectedFile);
			} catch (ConditionTimeoutException e) {
				throw new IstfException(String.format("Report file %s is not created in folder %s on %s monitor instance within 120 seconds", expectedFileName, REMOTE_DOWNLOAD_FOLDER, monitorAddress));
			}
		} else {        // execute locally
			File downloadDir = new File(DOWNLOAD_DIR);
			File expectedFile = new File(downloadDir, expectedFileName);
			if (downloadDir.mkdirs()) {
				log.info("\"{}\" folder was created", downloadDir.getAbsolutePath());
			} else {
				if (expectedFile.exists()) {
					expectedFile.delete();
				}
			}

			new OperationalReport().create(getTestSpecificTD("TestData_CreateReport"));
			try {
				Awaitility.await().atMost(Duration.TWO_MINUTES).until(expectedFile::exists);
			} catch (ConditionTimeoutException e) {
				throw new IstfException(String.format("Report file %s is not created in folder %s within 120 seconds", expectedFileName, downloadDir));
			}
		}
		log.info("Operational Report {} is created", expectedFileName);
	}
}