package aaa.modules.cft;

import org.apache.commons.lang3.StringUtils;
import org.mortbay.log.Log;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.utils.TestInfo;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.ssh.RemoteHelper;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class TestCFTCleanFeedFiles extends ControlledFinancialBaseTest {
	private static final String SOURCE_DIR = "/home/mp2/pas/sit/FIN_E_EXGPAS_PSFTGL_7000_D/outbound";

	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void cleanFeedFiles(@Optional(StringUtils.EMPTY) String state) throws JSchException, SftpException {
		JobUtils.executeJob(Jobs.policyTransactionLedgerJob_NonMonthly);
		RemoteHelper.clearFolder(SOURCE_DIR);

		// SSHController sshControllerRemote = CFTHelper.getSSHController();
		// File feedFile = new File(SOURCE_DIR + "*.*");
		// sshControllerRemote.deleteFile(feedFile);
		Log.info("Feed File deleted");
	}
}
