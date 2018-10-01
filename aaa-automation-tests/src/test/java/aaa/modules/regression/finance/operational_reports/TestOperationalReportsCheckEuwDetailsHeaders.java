package aaa.modules.regression.finance.operational_reports;

import static toolkit.verification.CustomAssertions.assertThat;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.product.OperationalReportsHelper;
import aaa.modules.BaseTest;
import toolkit.utils.TestInfo;

public class TestOperationalReportsCheckEuwDetailsHeaders extends BaseTest {

	/**
	 * @author Maksim Piatrouski
	 * Objectives : check "View Earn, Unearned, Written Premium (EUW) - Detail" report headers
	 * TC Steps:
	 * 1. Login app
	 * 2. Navigate Reports -> Operational Reports;
	 * 3. Fill Category, Type, Name
	 * 4. Click "Generate' button
	 * 5. In downloaded file check table headers
	 */

	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Finance.OPERATIONAL_REPORTS, testCaseId = "PAS-17631")
	public void testOperationalReportsCheckEuwDetailsHeaders() throws SftpException, JSchException, IOException {

		LocalDate today = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate();

		String FILE_NAME = "PAS+Earned+_+Unearned+_+Written+(EUW)+-+Detail_" + today.format(DateTimeFormatter.ofPattern("yyyy_MMM_d"));
		opReportApp().open();
		OperationalReportsHelper.downloadReport(getOperationalReportsTD("DataGather", "TestData_EUW_Detail"));

		assertThat(OperationalReportsHelper.getOpReportTableHeaders(FILE_NAME)).isEqualTo(getTestSpecificTD("TestData_CheckHeaders").getList("Headers"));
	}
}



