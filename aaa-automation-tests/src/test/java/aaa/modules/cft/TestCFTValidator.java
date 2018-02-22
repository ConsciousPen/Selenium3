package aaa.modules.cft;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.db.DBService;
import toolkit.utils.SSHController;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.waiters.Waiters;
import aaa.helpers.cft.CFTHelper;
import aaa.helpers.constants.Groups;
import aaa.modules.cft.csv.model.FinancialPSFTGLObject;
import aaa.modules.cft.csv.model.Record;
import aaa.modules.cft.report.ReportFutureDatedPolicy;
import aaa.modules.cft.report.ReportGeneratorService;

import com.exigen.ipb.etcsa.utils.ExcelUtils;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.exigen.istf.exec.testng.TimeShiftTestUtil;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class TestCFTValidator extends ControlledFinancialBaseTest {

	private static final String REMOTE_DOWNLOAD_FOLDER_PROP = "test.remotefile.location"; // location /home/autotest/Downloads
	private static final String DOWNLOAD_DIR = System.getProperty("user.dir") + PropertyProvider.getProperty("test.downloadfiles.location");
	private static final String EXCEL_FILE_EXTENSION = "xlsx";
	private static final String FEED_FILE_EXTENSION = "fix";
	private static final String CFT_VALIDATION_DIRECTORY = System.getProperty("user.dir") + "/src/test/resources/cft/";
	private static final String CFT_VALIDATION_REPORT = "CFT_Validations.xls";
	private static final String FUTURE_DATED_REPORT = "CFT_FutureDatedPolicies.xls";

	private String sqlGetLedgerData = "select le.LEDGERACCOUNTNO, sum (case when le.entrytype = 'CREDIT' then (to_number(le.entryamt) * -1) else to_number(le.entryamt) end) as AMOUNT from ledgertransaction lt, ledgerentry le where lt.id = le.ledgertransaction_id and to_char(lt.txdate, 'yyyymmdd') >= %s group by  le.LEDGERACCOUNTNO";

	private SSHController sshController = new SSHController(
		PropertyProvider.getProperty(TestProperties.APP_HOST),
		PropertyProvider.getProperty(TestProperties.SSH_USER),
		PropertyProvider.getProperty(TestProperties.SSH_PASSWORD));

	private File downloadDir;
	private File cftResultDir;

	@BeforeClass
	public void precondition() throws IOException {
		// refreshReports
		DBService.get().executeUpdate(PropertyProvider.getProperty("cft.refresh.or"));

		downloadDir = new File(DOWNLOAD_DIR);
		cftResultDir = new File(CFT_VALIDATION_DIRECTORY);
		CFTHelper.checkDirectory(downloadDir);
		CFTHelper.checkDirectory(cftResultDir);
	}

	@Test(groups = {Groups.CFT}, priority = 1)
	@TestInfo(component = Groups.CFT)
	//@Parameters({STATE_PARAM})
	public void validate() throws SftpException, JSchException, IOException, SQLException {

		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getStartTime().plusMonths(27));
		runCFTJobs();
		// get map from OR reports
		opReportApp().open();
		operationalReport.create(getTestSpecificTD(DEFAULT_TEST_DATA_KEY).getTestData("Policy Trial Balance"));
		Waiters.SLEEP(30000).go();
		// Awaitility.await().atMost(Duration.TWO_MINUTES).until(() -> downloadDir.listFiles().length == 1);
		log.info("Policy Trial Balance created");
		operationalReport.create(getTestSpecificTD(DEFAULT_TEST_DATA_KEY).getTestData("Billing Trial Balance"));
		Waiters.SLEEP(30000).go();
		// Awaitility.await().atMost(Duration.TWO_MINUTES).until(() -> downloadDir.listFiles().length == 2);
		log.info("Billing Trial Balance created");
		// moving data from monitor to download dir
		String remoteFileLocation = PropertyProvider.getProperty(REMOTE_DOWNLOAD_FOLDER_PROP);
		if (StringUtils.isNotEmpty(remoteFileLocation)) {
			log.info("Moving data from monitor to download dir");
			String monitorInfo = TimeShiftTestUtil.getContext().getBrowser().toString();
			String monitorAddress = monitorInfo.substring(monitorInfo.indexOf(" ") + 1, monitorInfo.indexOf(":", monitorInfo.indexOf(" ")));
			log.info("Monitor Address: {}", monitorAddress);
			log.info("Remote file location: {}", remoteFileLocation);
			log.info("Download to directory {}", downloadDir);
			SSHController sshControllerRemote = new SSHController(
				monitorAddress,
				PropertyProvider.getProperty("test.ssh.user"),
				PropertyProvider.getProperty("test.ssh.password"));
			 sshControllerRemote.downloadFolder(new File(remoteFileLocation), downloadDir);
			Waiters.SLEEP(30000).go(); // add agile wait till file occurs in local folder, awaitatility (IGarkusha added dependency, read in www)
		}
		Map<String, Double> accountsMapSummaryFromOR = getExcelValues();
		// Remote path from server -
		sshController.downloadFolder(new File(SOURCE_DIR), downloadDir);
		Map<String, Double> accountsMapSummaryFromFeedFile = getFeedFilesValues();
		// get Map from DB
		Map<String, Double> accountsMapSummaryFromDB = getDataBaseValues();
		// Round all values to 2
		CFTHelper.roundValuesToTwo(accountsMapSummaryFromFeedFile);
		CFTHelper.roundValuesToTwo(accountsMapSummaryFromDB);
		CFTHelper.roundValuesToTwo(accountsMapSummaryFromOR);

		ReportGeneratorService
			.generateReport(ReportGeneratorService
				.generateReportObjects(accountsMapSummaryFromDB, accountsMapSummaryFromFeedFile, accountsMapSummaryFromOR)
				, CFT_VALIDATION_DIRECTORY + CFT_VALIDATION_REPORT);

	}

	@Test(groups = {Groups.CFT}, priority = 2)
	@TestInfo(component = Groups.CFT)
//	@Parameters({STATE_PARAM})
	public void futureDatedPolicy() {

		String query1 = "select distinct BILLINGACCOUNTNUMBER as ACCNUMBER, TXDATE from LEDGERENTRY where LEDGERACCOUNTNO = 1065 and TRANSACTIONTYPE is null order by BILLINGACCOUNTNUMBER";
		String query2 = "select BILLINGACCOUNTNUMBER as ACCNUMBER, TXDATE from LEDGERENTRY where BILLINGACCOUNTNUMBER = %s and LEDGERACCOUNTNO =1065 and to_char(txdate, 'yyyymmdd') >= %s order by TXDATE";
		List<List<Map<String, String>>> accNumberTable = new ArrayList<>();
		List<Map<String, String>> dbResult = DBService.get().getRows(query1);
		for (Map<String, String> dbEntry : dbResult) {
			LocalDateTime txDate = TimeSetterUtil.getInstance().parse(dbEntry.get("TXDATE"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			String query = String.format(query2, dbEntry.get("ACCNUMBER"), txDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
			List<Map<String, String>> dbTransactions = DBService.get().getRows(query);
			accNumberTable.add(dbTransactions);
		}
		ReportFutureDatedPolicy.generateReport(accNumberTable, CFT_VALIDATION_DIRECTORY + FUTURE_DATED_REPORT);
		log.info("Future dated policies were verified");
	}

	// TODO move additional methods defined in TestClass to CFTHelper.class
	// rename cft->csv package to helper package
	// move CFTHelper to helper package

	private Map<String, Double> getExcelValues() {
		Map<String, Double> accountsMapSummaryFromOR = new HashMap<>();
		for (File reportFile : new File(DOWNLOAD_DIR).listFiles()) {
			if (!reportFile.getName().contains(EXCEL_FILE_EXTENSION)) {
				continue;
			}
			int totalBalanceCell = reportFile.getName().contains("Policy") ? 14 : 15;
			Sheet sheet = ExcelUtils.getSheet(reportFile.getAbsolutePath());
			for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
				if (null == sheet.getRow(i)) {
					continue;
				}
				String cellValue = ExcelUtils.getCellValue(sheet.getRow(i).getCell(3));
				if (StringUtils.isEmpty(cellValue) || !cellValue.matches("\\d+")) {
					continue;
				}
				if (accountsMapSummaryFromOR.containsKey(ExcelUtils.getCellValue(sheet.getRow(i).getCell(3)))) {
					double amount = accountsMapSummaryFromOR.get(ExcelUtils.getCellValue(sheet.getRow(i).getCell(3)))
						+ Double.parseDouble(ExcelUtils.getCellValue(sheet.getRow(i).getCell(totalBalanceCell)));
					accountsMapSummaryFromOR.put(ExcelUtils.getCellValue(sheet.getRow(i).getCell(3)), amount);
				} else {
					accountsMapSummaryFromOR.put(ExcelUtils.getCellValue(sheet.getRow(i).getCell(3)), Double.parseDouble(ExcelUtils.getCellValue(sheet.getRow(i).getCell(totalBalanceCell))));
				}
			}
		}
		return accountsMapSummaryFromOR;
	}

	private Map<String, Double> getDataBaseValues() {
		Map<String, Double> accountsMapSummaryFromDB = new HashMap<>();
		String transactionDate = TimeSetterUtil.getInstance().getStartTime().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String query = String.format(sqlGetLedgerData, transactionDate);
		List<Map<String, String>> dbResult = DBService.get().getRows(query);
		for (Map<String, String> dbEntry : dbResult) {
			accountsMapSummaryFromDB.put(dbEntry.get("LEDGERACCOUNTNO"), Double.parseDouble(dbEntry.get("AMOUNT")));
		}
		return accountsMapSummaryFromDB;
	}

	private Map<String, Double> getFeedFilesValues() {
		Map<String, Double> accountsMapSummaryFromFeedFile = new HashMap<>();

		List<FinancialPSFTGLObject> allEntries = new ArrayList<>();
		for (File file : new File(DOWNLOAD_DIR).listFiles()) {
			if (file.getName().contains(FEED_FILE_EXTENSION)) {
				try {
					allEntries.addAll(CFTHelper.transformToObject(FileUtils.readFileToString(file, Charset.defaultCharset())));
				} catch (IOException e) {
					log.error("Unable to get objects from file \"{}\"", file.getAbsolutePath());
				}
			}
		}
		for (FinancialPSFTGLObject entry : allEntries) {
			for (Record record : entry.getRecords()) {
				if (accountsMapSummaryFromFeedFile.containsKey(record.getBillingAccountNumber())) {
					double amount = accountsMapSummaryFromFeedFile.get(record.getBillingAccountNumber()) + record.getAmountDoubleValue();
					accountsMapSummaryFromFeedFile.put(record.getBillingAccountNumber(), amount);
				} else {
					accountsMapSummaryFromFeedFile.put(record.getBillingAccountNumber(), record.getAmountDoubleValue());
				}
			}
		}
		return accountsMapSummaryFromFeedFile;
	}
}