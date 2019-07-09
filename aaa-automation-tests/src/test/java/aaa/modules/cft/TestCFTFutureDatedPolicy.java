package aaa.modules.cft;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.helpers.constants.Groups;
import aaa.modules.cft.report.ReportFutureDatedPolicy;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;

public class TestCFTFutureDatedPolicy extends ControlledFinancialBaseTest{
	private static final String CFT_FUTURE_DATED_REPORT = "CFT_FutureDatedPolicies.xls";
	private String query1 = "select distinct BILLINGACCOUNTNUMBER as ACCNUMBER, TXDATE from LEDGERENTRY where LEDGERACCOUNTNO = 1065 and TRANSACTIONTYPE is null order by BILLINGACCOUNTNUMBER";
	private String query2 = "select BILLINGACCOUNTNUMBER as ACCNUMBER, TXDATE from LEDGERENTRY where BILLINGACCOUNTNUMBER = %s and LEDGERACCOUNTNO =1065 and to_char(txdate, 'yyyymmdd') >= %s order by TXDATE";

	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	public void futureDatedPolicyValidation() {
		checkReportNotExist(CFT_VALIDATION_DIRECTORY, CFT_FUTURE_DATED_REPORT);
		List<List<Map<String, String>>> accNumberTable = new ArrayList<>();
		List<Map<String, String>> dbResult = DBService.get().getRows(query1);
		for (Map<String, String> dbEntry : dbResult) {
			LocalDateTime txDate = TimeSetterUtil.getInstance().parse(dbEntry.get("TXDATE"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			String query = String.format(query2, dbEntry.get("ACCNUMBER"), txDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
			List<Map<String, String>> dbTransactions = DBService.get().getRows(query);
			accNumberTable.add(dbTransactions);
		}
		ReportFutureDatedPolicy.generateReport(accNumberTable, CFT_VALIDATION_DIRECTORY + CFT_FUTURE_DATED_REPORT);
		log.info("Future dated policies were verified");
	}
}
