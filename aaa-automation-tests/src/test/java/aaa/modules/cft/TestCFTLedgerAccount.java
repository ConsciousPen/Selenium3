package aaa.modules.cft;

import aaa.helpers.constants.Groups;
import aaa.modules.cft.report.ReportAccountEntry;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Test;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static aaa.helpers.cft.CFTHelper.checkDirectory;
import static aaa.helpers.cft.CFTHelper.checkFile;

public class TestCFTLedgerAccount extends ControlledFinancialBaseTest {
	private static final String CFT_LEDGER_ACCOUNT_REPORT = "CFT_Account1065Entries.xls";
	private String query1 = "select BILLINGACCOUNTNUMBER as ACCNUMBER, sum (case when ENTRYTYPE = 'CREDIT' then (to_number(ENTRYAMT) * -1) else to_number(ENTRYAMT) end) as AMOUNT from LEDGERENTRY where LEDGERACCOUNTNO =1065 group by  BILLINGACCOUNTNUMBER order by BILLINGACCOUNTNUMBER";
	private String query2 = "select BILLINGACCOUNTNUMBER as ACCNUMBER, TXDATE from LEDGERENTRY where BILLINGACCOUNTNUMBER = %s and LEDGERACCOUNTNO =1065 and to_char(txdate, 'yyyymmdd') >= %s order by TXDATE";

	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	public void ledgerAccountValidation() throws IOException {
		File cftResultDir = new File(CFT_VALIDATION_DIRECTORY);
		checkFile(CFT_VALIDATION_DIRECTORY, CFT_LEDGER_ACCOUNT_REPORT);
		List<List<Map<String, String>>> accNumberTable = new ArrayList<>();
		List<Map<String, String>> dbResult = DBService.get().getRows(query1);
		for (Map<String, String> dbEntry : dbResult) {
//			LocalDateTime txDate = TimeSetterUtil.getInstance().parse(dbEntry.get("TXDATE"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//			String query = String.format(query2, dbEntry.get("ACCNUMBER"), txDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
//			List<Map<String, String>> dbTransactions = DBService.get().getRows(query);
//			accNumberTable.add(dbTransactions);
		}
		ReportAccountEntry.generateReport(accNumberTable, CFT_VALIDATION_DIRECTORY+CFT_LEDGER_ACCOUNT_REPORT);

		log.info("Ledger Account 1065 was verified");

	}
}
