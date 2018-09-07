package aaa.modules.regression.finance.operational_reports;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.product.OperationalReportsHelper;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.exigen.istf.exec.testng.TimeShiftTestUtil;
import com.exigen.istf.timesetter.client.TimeSetterClient;
import com.jayway.awaitility.Awaitility;
import com.jayway.awaitility.Duration;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;
import toolkit.config.PropertyProvider;
import toolkit.utils.SSHController;
import toolkit.utils.TestInfo;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import static aaa.helpers.cft.CFTHelper.*;
import static toolkit.verification.CustomAssertions.assertThat;


public class TestOperationalReportsCheckEuwDetailsHeaders extends OperationalReportsBaseTest {


    private static LocalDate today = TimeSetterUtil.istfDateToJava(new TimeSetterClient().getStartTime()).toLocalDate();
    private static final String REMOTE_DOWNLOAD_FOLDER_PROP = "test.remotefile.location";
    private static final String REMOTE_DOWNLOAD_FOLDER = "/home/autotest/Downloads";
    private static final String DOWNLOAD_DIR = System.getProperty("user.dir") + PropertyProvider.getProperty("test.downloadfiles.location");
    private static final String FILE_NAME = "PAS+Earned+_+Unearned+_+Written+(EUW)+-+Detail_" + today.format(DateTimeFormatter.ofPattern("yyyy_MMM_d"));
    private String remoteFileLocation = PropertyProvider.getProperty(REMOTE_DOWNLOAD_FOLDER_PROP);

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
        File downloadDir = new File(DOWNLOAD_DIR);
        checkDirectory(downloadDir);

        opReportApp().open();
        // get map from OR reports
        if (StringUtils.isNotEmpty(remoteFileLocation)) {
            String monitorInfo = TimeShiftTestUtil.getContext().getBrowser().toString();
            String monitorAddress = monitorInfo.substring(monitorInfo.indexOf(" ") + 1, monitorInfo.indexOf(":", monitorInfo.indexOf(" ")));
            log.info("Monitor address: {}", monitorAddress);
            SSHController sshControllerRemote = new SSHController(
                    monitorAddress,
                    PropertyProvider.getProperty("test.ssh.user"),
                    PropertyProvider.getProperty("test.ssh.password"));
            Duration threeMinutes = new Duration(180L, TimeUnit.SECONDS);
            sshControllerRemote.deleteFile(new File(REMOTE_DOWNLOAD_FOLDER + "/*.*"));
            Awaitility.await().atMost(threeMinutes).until(() -> remoteDownloadComplete(sshControllerRemote, new File(REMOTE_DOWNLOAD_FOLDER)) == 0);
            operationalReport.create(getOperationalReportsTD("DataGather", "TestData_EUW_Detail"));
            Awaitility.await().atMost(threeMinutes).until(() -> remoteDownloadComplete(sshControllerRemote, new File(REMOTE_DOWNLOAD_FOLDER)) == 1);
            // moving Balances from monitor to download dir
            sshControllerRemote.downloadFolder(new File(REMOTE_DOWNLOAD_FOLDER), downloadDir);
            Awaitility.await().atMost(threeMinutes).until(() -> downloadComplete(downloadDir, OperationalReportsHelper.EXCEL_FILE_EXTENSION) == 1);
            sshControllerRemote.deleteFile(new File(REMOTE_DOWNLOAD_FOLDER + "/*.*"));
        } else {
            operationalReport.create(getOperationalReportsTD("DataGather", "TestData_EUW_Detail"));
            Awaitility.await().atMost(Duration.TWO_MINUTES).until(() -> downloadComplete(downloadDir, OperationalReportsHelper.EXCEL_FILE_EXTENSION) == 1);
        }

        assertThat(OperationalReportsHelper.getOpReportTableHeaders(FILE_NAME)).isEqualTo(getTestSpecificTD("TestData_CheckHeaders").getList("Headers"));
    }
}



