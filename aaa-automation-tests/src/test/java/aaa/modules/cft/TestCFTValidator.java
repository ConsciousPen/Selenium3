package aaa.modules.cft;

import aaa.helpers.constants.Groups;
import aaa.modules.cft.csv.model.FinancialPSFTGLObject;
import aaa.modules.cft.csv.model.Footer;
import aaa.modules.cft.csv.model.Header;
import aaa.modules.cft.csv.model.Record;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.db.DBService;
import toolkit.utils.SSHController;
import toolkit.utils.TestInfo;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestCFTValidator extends ControlledFinancialBaseTest {

	private static final String DOWNLOAD_DIR = "src/test/resources/cft";
	private static final String SOURCE_DIR = "/home/mp2/pas/sit/FIN_E_EXGPAS_PSFTGL_7000_D/outbound";
	private static final String SQL_GET_LEDGER_DATA = "select le.LEDGERACCOUNTNO, sum (case when le.entrytype = 'CREDIT' then (to_number(le.entryamt) * -1) else to_number(le.entryamt) end) as AMOUNT from ledgertransaction lt, ledgerentry le where lt.id = le.ledgertransaction_id group by  le.LEDGERACCOUNTNO";

	private SSHController sshController = new SSHController(
			PropertyProvider.getProperty(TestProperties.APP_HOST),
			PropertyProvider.getProperty(TestProperties.SSH_USER),
			PropertyProvider.getProperty(TestProperties.SSH_PASSWORD));

	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void validate(@Optional(StringUtils.EMPTY) String state) throws SftpException, JSchException, IOException {
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getStartTime().plusYears(1).plusDays(25).plusMonths(13));
		runCFTJobs();
		//Remote path from server -
		sshController.downloadFolder(new File(SOURCE_DIR), new File(DOWNLOAD_DIR));
		List<FinancialPSFTGLObject> allEntries = new ArrayList<>();
		for (File file : new File(DOWNLOAD_DIR).listFiles()) {
			allEntries.addAll(transformToObject(FileUtils.readFileToString(file, Charset.defaultCharset())));
		}

		Map<String, Double> accountsMapSummaryFromFeedFile = new HashMap<>();
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
		// Rounding values to 2
		for (Map.Entry<String, Double> stringDoubleEntry : accountsMapSummaryFromFeedFile.entrySet()) {
			accountsMapSummaryFromFeedFile.put(stringDoubleEntry.getKey(), BigDecimal.valueOf(stringDoubleEntry.getValue())
					.setScale(2, RoundingMode.HALF_UP)
					.doubleValue());
		}

		// get Map from DB
		Map<String, Double> accountsMapSummaryFromDB = new HashMap<>();
		List<Map<String, String>> dbResult = DBService.get().getRows(SQL_GET_LEDGER_DATA);
		for (Map<String, String> dbEntry : dbResult) {
			accountsMapSummaryFromDB.put(dbEntry.get("LEDGERACCOUNTNO"), Double.parseDouble(dbEntry.get("AMOUNT")));
		}
		log.info("");
	}

	private List<FinancialPSFTGLObject> transformToObject(String fileContent) throws IOException {
		List<FinancialPSFTGLObject> objectsFromCSV;
		try (CSVParser parser = CSVParser.parse(fileContent, CSVFormat.DEFAULT)) {
			objectsFromCSV = new ArrayList<>();
			FinancialPSFTGLObject object = null;
			for (CSVRecord record : parser.getRecords()) {
				//Each header has length == 22, footer ==56 and record == 123
				switch (record.get(0).length()) {
					case 22: {
						//parse header here
						object = new FinancialPSFTGLObject();
						Header entryHeader = new Header();
						entryHeader.setCode(record.get(0).substring(0, 11).trim());
						entryHeader.setDate(record.get(0).substring(11, record.get(0).length() - 3).trim());
						entryHeader.setNotKnownAttribute(record.get(0).substring(record.get(0).length() - 3, record.get(0).length()).trim());
						object.setHeader(entryHeader);
						break;
					}
					case 56: {
						//parse footer here
						Footer footer = new Footer();
						footer.setCode(record.get(0).substring(0, 11).trim());
						footer.setOverallExpSum(record.get(0).substring(11, 30).trim());
						footer.setOverallSum(record.get(0).substring(30, 46).trim());
						footer.setAmountOfRecords(record.get(0).substring(46, record.get(0).length()).trim());
						object.setFooter(footer);
						objectsFromCSV.add(object);
						break;
					}
					case 123: {
						//parse record body here
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
					default: {
						//ignore
					}
				}
			}
		}
		return objectsFromCSV;
	}

}
