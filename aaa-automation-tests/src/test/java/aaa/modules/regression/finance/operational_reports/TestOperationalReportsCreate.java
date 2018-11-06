package aaa.modules.regression.finance.operational_reports;

import static toolkit.verification.CustomAssertions.assertThat;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.admin.modules.reports.operationalreports.OperationalReport;
import aaa.helpers.browser.DownloadsHelper;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.utils.TestInfo;

public class TestOperationalReportsCreate extends PolicyBaseTest {

	private static final String FILE_NAME = "%s_%s.xlsx";

	/**
	 * @author Jurij Kuznecov
	 *
	 * 1. Initiate Operational Report's creating
	 * 2. Check Operational report file exists in corresponding folder
	 */

	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Finance.OPERATIONAL_REPORTS)
	public void testOperationalReportsCreate() {
		String postfix = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy_MMM_d"));
		String reportName = String.format(FILE_NAME, getTestSpecificTD("OperationalReport").getValue("ReportName"), postfix);

		opReportApp().open();

		new OperationalReport().create(getTestSpecificTD("TestData_CreateReport"));
		log.info("Operational Report {} was created", reportName);
		Optional<File> report = DownloadsHelper.getFile(reportName);
		assertThat(report).as(reportName + " file doesn't exist.").isPresent().isNotNull();

	}
}