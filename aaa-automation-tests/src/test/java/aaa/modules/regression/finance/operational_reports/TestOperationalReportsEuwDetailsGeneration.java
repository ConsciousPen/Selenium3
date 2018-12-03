package aaa.modules.regression.finance.operational_reports;

import static aaa.helpers.cft.CFTHelper.checkDirectory;
import java.io.File;
import java.io.IOException;
import org.testng.annotations.Test;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.product.OperationalReportsHelper;
import aaa.modules.BaseTest;
import toolkit.utils.TestInfo;

public class TestOperationalReportsEuwDetailsGeneration extends BaseTest {

	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Finance.OPERATIONAL_REPORTS, testCaseId = "PAS-17631")
	public void testOperationalReportsEuwDetailsGeneration() throws SftpException, JSchException, IOException {
		opReportApp().open();

		OperationalReportsHelper.downloadReport(getOperationalReportsTD("DataGather", "TestData_EUW_Detail"));
		checkDirectory(new File(OperationalReportsHelper.DOWNLOAD_DIR));
	}
}
