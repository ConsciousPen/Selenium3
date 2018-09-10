package aaa.modules.regression.finance.operational_reports;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.product.OperationalReportsHelper;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.exigen.istf.timesetter.client.TimeSetterClient;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static aaa.helpers.cft.CFTHelper.checkDirectory;
import static toolkit.verification.CustomAssertions.assertThat;


public class TestOperationalReportsCheckEuwDetailsHeaders extends OperationalReportsBaseTest {


    private static LocalDate today = TimeSetterUtil.istfDateToJava(new TimeSetterClient().getStartTime()).toLocalDate();
    private static final String FILE_NAME = "PAS+Earned+_+Unearned+_+Written+(EUW)+-+Detail_" + today.format(DateTimeFormatter.ofPattern("yyyy_MMM_d"));

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
        opReportApp().open();
        OperationalReportsHelper.downloadReport(getOperationalReportsTD("DataGather", "TestData_EUW_Detail"));

        assertThat(OperationalReportsHelper.getOpReportTableHeaders(FILE_NAME)).isEqualTo(getTestSpecificTD("TestData_CheckHeaders").getList("Headers"));
        checkDirectory(new File(OperationalReportsHelper.DOWNLOAD_DIR));
    }
}



