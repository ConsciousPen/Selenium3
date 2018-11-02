package aaa.modules.regression.finance.operational_reports;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.exigen.istf.exec.testng.TimeShiftTestUtil;
import com.jayway.awaitility.Awaitility;
import com.jayway.awaitility.Duration;
import com.jayway.awaitility.core.ConditionTimeoutException;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import aaa.admin.modules.reports.operationalreports.OperationalReport;
import aaa.config.CsaaTestProperties;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.ssh.RemoteHelper;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.config.PropertyProvider;
import toolkit.utils.TestInfo;

public class TestOperationalReportsCreate extends PolicyBaseTest {

	private static final String REMOTE_DOWNLOAD_FOLDER = "/home/autotest/Downloads";
	private static final String DOWNLOAD_DIR = System.getProperty("user.dir") + PropertyProvider.getProperty(CsaaTestProperties.LOCAL_DOWNLOAD_FOLDER_PROP);
	private static final String EXCEL_FILE_EXTENSION = "xlsx";

	/**
	 * @author Jurij Kuznecov
	 *
	 * 1. Initiate Operational Report's creating
	 * 2. Check Operational report file exists in corresponding folder
	 *
	 * for executing locally add to local.property file 'test.remotefile.location='
	 */

	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Finance.OPERATIONAL_REPORTS)
	public void testOperationalReportsCreate() {

		String remoteFileLocation = PropertyProvider.getProperty(CsaaTestProperties.REMOTE_DOWNLOAD_FOLDER_PROP);
		String reportName = getTestSpecificTD("OperationalReport").getValue("ReportName");
		LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
		String expectedFileName = String.format("%s_%s_%s_%s.%s", reportName, today.getYear(), today.getMonth().getDisplayName(TextStyle.SHORT, Locale.US), today.getDayOfMonth(), EXCEL_FILE_EXTENSION);

		File downloadDir = new File(DOWNLOAD_DIR);
		File expectedFile = new File(downloadDir, expectedFileName);
		if (downloadDir.mkdirs()) {
			log.info("\"{}\" folder was created", downloadDir.getAbsolutePath());
		} else {
			if (expectedFile.exists()) {
				expectedFile.delete();
			}
		}

		opReportApp().open();

		if (StringUtils.isNotEmpty(remoteFileLocation)) {        // execute on remote monitor
			String expectedFilePath = new File(REMOTE_DOWNLOAD_FOLDER + "/" + expectedFileName).getAbsolutePath();
			String monitorInfo = TimeShiftTestUtil.getContext().getBrowser().toString();
			String monitorAddress = monitorInfo.substring(monitorInfo.indexOf(" ") + 1, monitorInfo.indexOf(":", monitorInfo.indexOf(" ")));
			RemoteHelper remoteHelper = RemoteHelper.with().host(monitorAddress).user(PropertyProvider.getProperty("test.ssh.user"), PropertyProvider.getProperty("test.ssh.password")).get();
			if (remoteHelper.isPathExist(expectedFilePath)) {
				remoteHelper.removeFile(expectedFilePath);
				Awaitility.await().atMost(Duration.TWO_MINUTES).until(() -> !remoteHelper.isPathExist(expectedFilePath));
			}
			new OperationalReport().create(getTestSpecificTD("TestData_CreateReport"));
			try {
				Awaitility.await().atMost(Duration.TWO_MINUTES).until(() -> remoteHelper.isPathExist(expectedFilePath));
				remoteHelper.removeFile(expectedFilePath);
			} catch (ConditionTimeoutException e) {
				Assert.fail(String.format("Report file %s is not created in folder %s on %s monitor instance within 120 seconds", expectedFileName, REMOTE_DOWNLOAD_FOLDER, monitorAddress));
			}

			remoteHelper.downloadFile(new File(REMOTE_DOWNLOAD_FOLDER).getAbsolutePath(), downloadDir.getAbsolutePath());
			try {
				Awaitility.await().atMost(Duration.TWO_MINUTES).until(expectedFile::exists);
			} catch (ConditionTimeoutException e) {
				Assert.fail(String.format("Report file %s is not downloaded into folder %s within 120 seconds", expectedFileName, downloadDir));
			}

		} else {        // execute locally
			new OperationalReport().create(getTestSpecificTD("TestData_CreateReport"));
			try {
				Awaitility.await().atMost(Duration.TWO_MINUTES).until(expectedFile::exists);
			} catch (ConditionTimeoutException e) {
				Assert.fail(String.format("Report file %s is not created in folder %s within 120 seconds", expectedFileName, downloadDir));
			}
		}
		log.info("Operational Report {} was created", expectedFileName);
	}
}