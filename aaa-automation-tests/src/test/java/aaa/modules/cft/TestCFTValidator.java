package aaa.modules.cft;

import static aaa.helpers.cft.CFTHelper.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.DownloadService;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import aaa.helpers.constants.Groups;
import aaa.modules.cft.report.ReportGeneratorService;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.db.DBService;
import toolkit.utils.SSHController;
import toolkit.utils.TestInfo;

public class TestCFTValidator extends ControlledFinancialBaseTest {

	private static final String DOWNLOAD_DIR = PropertyProvider.getProperty("test.downloadfiles.location");
	private static final String EXCEL_FILE_EXTENSION = "xlsx";
	private static final String FEED_FILE_EXTENSION = "fix";
	private static final String CFT_VALIDATION_REPORT = "CFT_Validations.xls";

	private String sqlGetLedgerData = "select le.LEDGERACCOUNTNO, sum (case when le.entrytype = 'CREDIT' then (to_number(le.entryamt) * -1) else to_number(le.entryamt) end) as AMOUNT from ledgertransaction lt, ledgerentry le where lt.id = le.ledgertransaction_id and to_char(lt.txdate, 'yyyymmdd') >= %s group by  le.LEDGERACCOUNTNO";

	private SSHController sshController = new SSHController(
			PropertyProvider.getProperty(TestProperties.APP_HOST),
			PropertyProvider.getProperty(TestProperties.SSH_USER),
			PropertyProvider.getProperty(TestProperties.SSH_PASSWORD));

	@Test(groups = {Groups.CFT, Groups.TIMEPOINT})
	@TestInfo(component = Groups.CFT)
	public void validate() throws SftpException, JSchException, IOException {
		// refreshReports
		DBService.get().executeUpdate(PropertyProvider.getProperty("cft.refresh.or"));

		checkDirectory(new File(DOWNLOAD_DIR));
		checkFile(CFT_VALIDATION_DIRECTORY, CFT_VALIDATION_REPORT);

		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getStartTime().plusMonths(13));
		runCFTJobs();

		opReportApp().open();
		operationalReport.create(getTestSpecificTD(DEFAULT_TEST_DATA_KEY).getTestData("Policy Trial Balance"));
		DownloadService.download("*.*");
		opReportApp().close();

		opReportApp().open();
		operationalReport.create(getTestSpecificTD(DEFAULT_TEST_DATA_KEY).getTestData("Billing Trial Balance"));
		DownloadService.download("*.*");

		Map<String, Double> accountsMapSummaryFromOR = getExcelValues(DOWNLOAD_DIR, EXCEL_FILE_EXTENSION);

		// moving Feed file from App server to download dir
		sshController.downloadFolder(new File(SOURCE_DIR), new File(DOWNLOAD_DIR));
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