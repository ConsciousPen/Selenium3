package aaa.modules.cft;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.mortbay.log.Log;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import toolkit.utils.SSHController;
import toolkit.utils.TestInfo;
import aaa.helpers.cft.CFTHelper;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class TestCFTCleanFeedFiles extends ControlledFinancialBaseTest {
	private static final String SOURCE_DIR = "/home/mp2/pas/sit/FIN_E_EXGPAS_PSFTGL_7000_D/outbound";

	@BeforeSuite(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void cleanFeedFiles(@Optional(StringUtils.EMPTY) String state) throws JSchException, SftpException {
		JobUtils.executeJob(Jobs.policyTransactionLedgerJob_NonMonthly);
		SSHController sshControllerRemote = CFTHelper.getSSHController();
		File feedFile = new File(SOURCE_DIR + "*.*");
		sshControllerRemote.deleteFile(feedFile);
		Log.info("Feed File deleted");
	}
}
