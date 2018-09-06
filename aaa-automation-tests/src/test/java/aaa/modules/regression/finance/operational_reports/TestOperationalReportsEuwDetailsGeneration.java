package aaa.modules.regression.finance.operational_reports;

import static aaa.helpers.cft.CFTHelper.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;
import com.jayway.awaitility.Awaitility;
import com.jayway.awaitility.Duration;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.product.OperationalReportsHelper;
import toolkit.config.PropertyProvider;
import toolkit.utils.SSHController;
import toolkit.utils.TestInfo;


public class TestOperationalReportsEuwDetailsGeneration extends OperationalReportsBaseTest {

    private static final String REMOTE_DOWNLOAD_FOLDER_PROP = "test.remotefile.location"; // location /root/Downloads
    private static final String REMOTE_DOWNLOAD_FOLDER = "/home/autotest/Downloads";
    private static final String DOWNLOAD_DIR = System.getProperty("user.dir") + PropertyProvider.getProperty("test.downloadfiles.location");

    private String remoteFileLocation = PropertyProvider.getProperty(REMOTE_DOWNLOAD_FOLDER_PROP);

    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Finance.OPERATIONAL_REPORTS, testCaseId = "PAS-17631")
    public void testOperationalReportsEuwDetailsGeneration() throws SftpException, JSchException, IOException {
        File downloadDir = new File(DOWNLOAD_DIR);
        checkDirectory(downloadDir);

        opReportApp().open();
        // get map from OR reports
        if (StringUtils.isNotEmpty(remoteFileLocation)) {
            String monitorInfo = "To implement after migration to Selenoids"; //TimeShiftTestUtil.getContext().getBrowser().toString();
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
    }
}