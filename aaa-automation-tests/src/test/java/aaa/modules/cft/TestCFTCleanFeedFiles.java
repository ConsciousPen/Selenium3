package aaa.modules.cft;

import org.testng.annotations.Test;

import toolkit.utils.TestInfo;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.ssh.RemoteHelper;

import com.exigen.ipb.etcsa.utils.batchjob.Job;
import com.exigen.ipb.etcsa.utils.batchjob.JobGroup;
import com.exigen.ipb.etcsa.utils.batchjob.SoapJobActions;

public class TestCFTCleanFeedFiles extends ControlledFinancialBaseTest {

	@Test(groups = {Groups.CFT, Groups.PRECONDITION})
	@TestInfo(component = Groups.CFT)
	public void cleanFeedFiles() {
		new SoapJobActions().createJob(new JobGroup("remittanceFeedBatchReceiveJob", new Job("aaaRemittanceFeedAsyncBatchReceiveJob")));
		new SoapJobActions().createJob(new JobGroup("policyTransactionLedgerJob_NonMonthly", new Job("policyTransactionLedgerJob")));
		JobUtils.executeJob(Jobs.policyTransactionLedgerJob_NonMonthly);
		RemoteHelper.clearFolder(SOURCE_DIR);
	}
}
