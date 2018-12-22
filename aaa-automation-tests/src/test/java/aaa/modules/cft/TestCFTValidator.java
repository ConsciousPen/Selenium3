package aaa.modules.cft;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.jayway.awaitility.Awaitility;
import com.jayway.awaitility.Duration;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.db.DBService;
import toolkit.utils.SSHController;
import toolkit.utils.TestInfo;
import aaa.helpers.constants.Groups;
import aaa.modules.cft.report.ReportGeneratorService;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.exigen.istf.exec.testng.TimeShiftTestUtil;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import static aaa.helpers.cft.CFTHelper.*;

public class TestCFTValidator extends ControlledFinancialBaseTest {

	private static final String REMOTE_DOWNLOAD_FOLDER_PROP = "test.remotefile.location"; // location /root/Downloads
	private static final String REMOTE_DOWNLOAD_FOLDER = "/home/autotest/Downloads";
	private static final String DOWNLOAD_DIR = System.getProperty("user.dir") + PropertyProvider.getProperty("test.downloadfiles.location");
	private static final String EXCEL_FILE_EXTENSION = "xlsx";
	private static final String FEED_FILE_EXTENSION = "fix";
	private static final String CFT_VALIDATION_REPORT = "CFT_Validations.xls";

	private String sqlGetLedgerData = "select le.LEDGERACCOUNTNO, sum (case when le.entrytype = 'CREDIT' then (to_number(le.entryamt) * -1) else to_number(le.entryamt) end) as AMOUNT from ledgertransaction lt, ledgerentry le where lt.id = le.ledgertransaction_id and to_char(lt.txdate, 'yyyymmdd') >= %s group by  le.LEDGERACCOUNTNO";
	private String remoteFileLocation = PropertyProvider.getProperty(REMOTE_DOWNLOAD_FOLDER_PROP);

	private SSHController sshController = new SSHController(
			PropertyProvider.getProperty(TestProperties.APP_HOST),
			PropertyProvider.getProperty(TestProperties.SSH_USER),
			PropertyProvider.getProperty(TestProperties.SSH_PASSWORD));

	@Test(groups = {Groups.CFT, Groups.TIMEPOINT})
	@TestInfo(component = Groups.CFT)
	public void validate() throws SftpException, JSchException, IOException {
		// refreshReports
		DBService.get().executeUpdate(PropertyProvider.getProperty("cft.refresh.or"));

		File downloadDir = new File(DOWNLOAD_DIR);
		checkDirectory(downloadDir);
		checkFile(CFT_VALIDATION_DIRECTORY, CFT_VALIDATION_REPORT);

		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getStartTime().plusDays(391));
		//runCFTJobs();

		opReportApp().open();
		// get map from OR reports
		if (StringUtils.isNotEmpty(remoteFileLocation)) {
			String monitorInfo = TimeShiftTestUtil.getContext().getBrowser().toString();
			String monitorAddress = monitorInfo.substring(monitorInfo.indexOf(" ") + 1, monitorInfo.indexOf(":", monitorInfo.indexOf(" ")));
			log.info("Monitor address: {}", monitorAddress);
			SSHController sshControllerRemote = new SSHController(
					monitorAddress,
					PropertyProvider.getProperty("test.ssh.user"),
					PropertyProvider.getProperty("test.ssh.password"));
			Duration threeMinutes = new Duration(180L, TimeUnit.SECONDS);
			sshControllerRemote.deleteFile(new File(REMOTE_DOWNLOAD_FOLDER + "/*.*"));
			Awaitility.await().atMost(threeMinutes).until(() -> remoteDownloadComplete(sshControllerRemote, new File(REMOTE_DOWNLOAD_FOLDER)) == 0);
			operationalReport.create(getTestSpecificTD(DEFAULT_TEST_DATA_KEY).getTestData("Policy Trial Balance"));
			Awaitility.await().atMost(threeMinutes).until(() -> remoteDownloadComplete(sshControllerRemote, new File(REMOTE_DOWNLOAD_FOLDER)) == 1);
			operationalReport.create(getTestSpecificTD(DEFAULT_TEST_DATA_KEY).getTestData("Billing Trial Balance"));
			Awaitility.await().atMost(threeMinutes).until(() -> remoteDownloadComplete(sshControllerRemote, new File(REMOTE_DOWNLOAD_FOLDER)) == 2);
			// moving Balances from monitor to download dir
			sshControllerRemote.downloadFolder(new File(REMOTE_DOWNLOAD_FOLDER), downloadDir);
			Awaitility.await().atMost(threeMinutes).until(() -> downloadComplete(downloadDir, EXCEL_FILE_EXTENSION) == 2);
			sshControllerRemote.deleteFile(new File(REMOTE_DOWNLOAD_FOLDER + "/*.*"));
		} else {
			operationalReport.create(getTestSpecificTD(DEFAULT_TEST_DATA_KEY).getTestData("Policy Trial Balance"));
			Awaitility.await().atMost(Duration.TWO_MINUTES).until(() -> downloadComplete(downloadDir, EXCEL_FILE_EXTENSION) == 1);
			operationalReport.create(getTestSpecificTD(DEFAULT_TEST_DATA_KEY).getTestData("Billing Trial Balance"));
			Awaitility.await().atMost(Duration.TWO_MINUTES).until(() -> downloadComplete(downloadDir, EXCEL_FILE_EXTENSION) == 2);
		}
		Map<String, Double> accountsMapSummaryFromOR = getExcelValues(DOWNLOAD_DIR, EXCEL_FILE_EXTENSION);

		// moving Feed file from App server to download dir
		sshController.downloadFolder(new File(SOURCE_DIR), downloadDir);
		Map<String, Double> accountsMapSummaryFromFeedFile = getFeedFilesValues(DOWNLOAD_DIR, FEED_FILE_EXTENSION);

		// get Map from DB
		Map<String, Double> accountsMapSummaryFromDB = getDataBaseValues(sqlGetLedgerData);

		// Round all values to 2
		roundValuesToTwo(accountsMapSummaryFromFeedFile);
		roundValuesToTwo(accountsMapSummaryFromDB);
		roundValuesToTwo(accountsMapSummaryFromOR);

		ReportGeneratorService
				.generateReport(ReportGeneratorService
								.generateReportObjects(accountsMapSummaryFromDB, accountsMapSummaryFromFeedFile, accountsMapSummaryFromOR)
						, CFT_VALIDATION_DIRECTORY + CFT_VALIDATION_REPORT);
	}
}