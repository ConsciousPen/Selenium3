package aaa.modules.regression.finance.operational_reports;

import aaa.helpers.product.OperationalReportsHelper;
import com.exigen.istf.exec.testng.TimeShiftTestUtil;
import com.jayway.awaitility.Awaitility;
import com.jayway.awaitility.Duration;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;
import toolkit.config.PropertyProvider;
import toolkit.utils.SSHController;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static aaa.helpers.cft.CFTHelper.downloadComplete;
import static aaa.helpers.cft.CFTHelper.remoteDownloadComplete;


public class GenerateReport extends OperationalReportsBaseTest {

    private static final String REMOTE_DOWNLOAD_FOLDER_PROP = "test.remotefile.location"; // location /root/Downloads
    private static final String REMOTE_DOWNLOAD_FOLDER = "/home/autotest/Downloads";
    private static final String DOWNLOAD_DIR = System.getProperty("user.dir") + "/src/test/resources/op_reports/";

    private String remoteFileLocation = PropertyProvider.getProperty(REMOTE_DOWNLOAD_FOLDER_PROP);

    @Test
    public void generateReport() throws SftpException, JSchException, IOException {
        File downloadDir = new File(DOWNLOAD_DIR);
        OperationalReportsHelper.checkDirectory(downloadDir);
        OperationalReportsHelper.checkFile(DOWNLOAD_DIR, "Validations.xlsx");

        opReportApp().open();
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
            sshControllerRemote.downloadFolder(new File(REMOTE_DOWNLOAD_FOLDER), downloadDir);
            Awaitility.await().atMost(threeMinutes).until(() -> downloadComplete(downloadDir, OperationalReportsHelper.EXCEL_FILE_EXTENSION) == 1);
            sshControllerRemote.deleteFile(new File(REMOTE_DOWNLOAD_FOLDER + "/*.*"));
        } else {
            operationalReport.create(getOperationalReportsTD("DataGather", "TestData_EUW_Detail"));
            Awaitility.await().atMost(Duration.TWO_MINUTES).until(() -> downloadComplete(downloadDir, OperationalReportsHelper.EXCEL_FILE_EXTENSION) == 1);
        }
    }
}
