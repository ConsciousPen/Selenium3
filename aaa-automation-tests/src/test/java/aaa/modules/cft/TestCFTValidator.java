package aaa.modules.cft;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.db.DBService;
import toolkit.utils.SSHController;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.waiters.Waiters;
import aaa.helpers.constants.Groups;
import aaa.modules.cft.csv.model.FinancialPSFTGLObject;
import aaa.modules.cft.csv.model.Footer;
import aaa.modules.cft.csv.model.Header;
import aaa.modules.cft.csv.model.Record;
import aaa.modules.cft.report.ReportGeneratorService;

import com.exigen.ipb.etcsa.utils.ExcelUtils;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class TestCFTValidator extends ControlledFinancialBaseTest {

	private static final String DOWNLOAD_DIR = System.getProperty("user.dir") + PropertyProvider.getProperty("test.downloadfiles.location");
	private static final String SOURCE_DIR = "/home/mp2/pas/sit/FIN_E_EXGPAS_PSFTGL_7000_D/outbound";
	private static final String SQL_GET_LEDGER_DATA = "select le.LEDGERACCOUNTNO, sum (case when le.entrytype = 'CREDIT' then (to_number(le.entryamt) * -1) else to_number(le.entryamt) end) as AMOUNT from ledgertransaction lt, ledgerentry le where lt.id = le.ledgertransaction_id group by  le.LEDGERACCOUNTNO";
	private static final String EXCEL_FILE_EXTENSION = "xlsx";
	private static final String FEED_FILE_EXTENSION = "fix";
	private static final String CFT_VALIDATION_DIRECTORY = System.getProperty("user.dir") + "/src/test/resources/cft/";
	private static final String CFT_VALIDATION_REPORT = "CFT_Validations.xls";

	private SSHController sshController = new SSHController(
		PropertyProvider.getProperty(TestProperties.APP_HOST),
		PropertyProvider.getProperty(TestProperties.SSH_USER),
		PropertyProvider.getProperty(TestProperties.SSH_PASSWORD));

	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void validate(@Optional(StringUtils.EMPTY) String state) throws SftpException, JSchException, IOException {

		File downloadDir = new File(DOWNLOAD_DIR);
		File cftResultDir = new File(CFT_VALIDATION_DIRECTORY);
		checkDirectory(downloadDir);
		checkDirectory(cftResultDir);
		// We configured CFT job to stay on the same date when all cft scenarios were finished, so time switching not needed
		// TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getStartTime().plusYears(1).plusMonths(13).plusDays(25));
		runCFTJobs();
		// get map from OR reports
		opReportApp().open();
		operationalReport.create(getTestSpecificTD(DEFAULT_TEST_DATA_KEY).getTestData("Policy Trial Balance"));
		Waiters.SLEEP(15000).go(); // add agile wait till file occurs, awaitatility (IGarkusha added dependency, read in www)
		// condition that download/remote download folder listfiles.size==1
		operationalReport.create(getTestSpecificTD(DEFAULT_TEST_DATA_KEY).getTestData("Billing Trial Balance"));
		Waiters.SLEEP(15000).go(); // add agile wait till file occurs, awaitatility (IGarkusha added dependency, read in www)
		// condition that download/remote download folder listfiles.size==2
		// moving data from monitor to download dir
		// String monitorInfo = TimeShiftTestUtil.getContext().getBrowser().toString();
		// String monitorAddress = monitorInfo.substring(monitorInfo.indexOf("selenium=") + 9, monitorInfo.indexOf(":"));
		// SSHController sshControllerRemote = new SSHController(
		// monitorAddress,
		// PropertyProvider.getProperty("test.ssh.user"),
		// PropertyProvider.getProperty("test.ssh.password"));
		// sshControllerRemote.downloadFolder(new File(PropertyProvider.getProperty("test.remotefile.location")),downloadDir);
		// //add wait till files will appear in the folder
		// Waiters.SLEEP(15000).go(); // add agile wait till file occurs in local folder, awaitatility (IGarkusha added dependency, read in www)
		Map<String, Double> accountsMapSummaryFromOR = getExcelValues();
		// Remote path from server -
		sshController.downloadFolder(new File(SOURCE_DIR), downloadDir);
		Map<String, Double> accountsMapSummaryFromFeedFile = getFeedFilesValues();
		// get Map from DB
		Map<String, Double> accountsMapSummaryFromDB = getDataBaseValues();
		// Round all values to 2
		roundValuesToTwo(accountsMapSummaryFromFeedFile);
		roundValuesToTwo(accountsMapSummaryFromDB);
		roundValuesToTwo(accountsMapSummaryFromOR);

		ReportGeneratorService
			.generateReport(ReportGeneratorService
				.generateReportObjects(accountsMapSummaryFromDB, accountsMapSummaryFromFeedFile, accountsMapSummaryFromOR)
				, CFT_VALIDATION_DIRECTORY + CFT_VALIDATION_REPORT);

	}

	// TODO move additional methods defined in TestClass to CFTHelper.class
	// rename cft->csv package to helper package
	// move CFTHelper to helper package

	private void checkDirectory(File directory) throws IOException {
		if (directory.mkdirs()) {
			log.info("\"{}\" folder was created", directory.getAbsolutePath());
		} else {
			FileUtils.cleanDirectory(directory);
		}
	}

	private List<FinancialPSFTGLObject> transformToObject(String fileContent) throws IOException {
		// if we fill know approach used in dev application following hardcoded indexes related approach can be changed to used in app
		List<FinancialPSFTGLObject> objectsFromCSV;
		try (CSVParser parser = CSVParser.parse(fileContent, CSVFormat.DEFAULT)) {
			objectsFromCSV = new ArrayList<>();
			FinancialPSFTGLObject object = null;
			for (CSVRecord record : parser.getRecords()) {
				// Each header has length == 22, footer ==56 and record == 123
				switch (record.get(0).length()) {
					case 22 : {
						// parse header here
						object = new FinancialPSFTGLObject();
						Header entryHeader = new Header();
						entryHeader.setCode(record.get(0).substring(0, 11).trim());
						entryHeader.setDate(record.get(0).substring(11, record.get(0).length() - 3).trim());
						entryHeader.setNotKnownAttribute(record.get(0).substring(record.get(0).length() - 3, record.get(0).length()).trim());
						object.setHeader(entryHeader);
						break;
					}
					case 56 : {
						// parse footer here
						Footer footer = new Footer();
						footer.setCode(record.get(0).substring(0, 11).trim());
						footer.setOverallExpSum(record.get(0).substring(11, 30).trim());
						footer.setOverallSum(record.get(0).substring(30, 46).trim());
						footer.setAmountOfRecords(record.get(0).substring(46, record.get(0).length()).trim());
						object.setFooter(footer);
						objectsFromCSV.add(object);
						break;
					}
					case 123 : {
						// parse record body here
						Record entryRecord = new Record();
						entryRecord.setCode(record.get(0).substring(0, 11).trim());
						entryRecord.setBillingAccountNumber(record.get(0).substring(11, 21).trim());
						entryRecord.setProductCode(record.get(0).substring(21, 31).trim());
						entryRecord.setStateInfo(record.get(0).substring(31, 43).trim());
						entryRecord.setAmount(record.get(0).substring(43, 57).trim());
						entryRecord.setAction(record.get(0).substring(57, 87).trim());
						entryRecord.setActionDescription(record.get(0).substring(87, 117).trim());
						entryRecord.setPlusMinus(record.get(0).substring(117, record.get(0).length()).trim());
						object.getRecords().add(entryRecord);
						break;
					}
					default : {
						// ignore
					}
				}
			}
		}
		return objectsFromCSV;
	}

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
		List<Map<String, String>> dbResult = DBService.get().getRows(SQL_GET_LEDGER_DATA);
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
					allEntries.addAll(transformToObject(FileUtils.readFileToString(file, Charset.defaultCharset())));
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

	private void roundValuesToTwo(Map<String, Double> stringDoubleMap) {
		// Rounding values to 2
		for (Map.Entry<String, Double> stringDoubleEntry : stringDoubleMap.entrySet()) {
			stringDoubleMap.put(stringDoubleEntry.getKey(), BigDecimal.valueOf(stringDoubleEntry.getValue())
				.setScale(2, RoundingMode.HALF_UP)
				.doubleValue());
		}
	}

}
