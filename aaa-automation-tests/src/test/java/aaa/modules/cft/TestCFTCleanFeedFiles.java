package aaa.modules.cft;

import org.testng.annotations.Test;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.ssh.RemoteHelper;
import toolkit.utils.TestInfo;

public class TestCFTCleanFeedFiles extends ControlledFinancialBaseTest {

	@Test(groups = {Groups.CFT, Groups.PRECONDITION})
	@TestInfo(component = Groups.CFT)
	public void cleanFeedFiles() {
		JobUtils.executeJob(BatchJob.policyTransactionLedgerJob_NonMonthly);
		RemoteHelper.get().clearFolder(SOURCE_DIR);
	}
}
