package aaa.modules.regression.billing_and_payments.home_ss.ho3.functional;

import java.io.IOException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.billing_and_payments.auto_ss.functional.preconditions.TestRefundProcessPreConditions;
import aaa.modules.regression.billing_and_payments.template.PolicyBilling;
import aaa.modules.regression.billing_and_payments.template.functional.TestRefundProcessTemplate;
import toolkit.utils.TestInfo;

public class TestRefundProcess extends PolicyBilling implements TestRefundProcessPreConditions {

	private TestRefundProcessTemplate testRefundProcessTemplate = new TestRefundProcessTemplate(PolicyType.HOME_SS_HO3);

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Test(description = "Precondition for TestRefundProcess tests")
	public void precondJobAdding() {
		JobUtils.createJob(BatchJob.aaaRefundGenerationAsyncJob);
		JobUtils.createJob(BatchJob.aaaRefundDisbursementAsyncJob);
		JobUtils.createJob(BatchJob.aaaRefundsDisbursementReceiveInfoAsyncJob);
		JobUtils.createJob(BatchJob.aaaRefundsDisbursementRejectionsAsyncJob);
	}

	/***
	 * The test is used to debug 7025 generated file using minimu actions, when file is already generated
	 * @param state
	 * @throws SftpException
	 * @throws JSchException
	 * @throws IOException
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.HOME_SS_HO3, testCaseId = {"PAS-7039", "PAS-7196"})
	public void pas7039_Debug(@Optional("VA") String state) throws SftpException, JSchException, IOException {
		testRefundProcessTemplate.pas7039_Debug(getState());
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.HOME_SS_HO3, testCaseId = {"PAS-7039", "PAS-7196", "PAS-450"})
	public void pas7039_newDataElementsDeceasedYes(@Optional("VA") String state) throws SftpException, JSchException, IOException {
		testRefundProcessTemplate.pas7039_newDataElementsDeceasedYes(getState());
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.HOME_SS_HO3, testCaseId = {"PAS-7039", "PAS-7196", "PAS-450"})
	public void pas7039_newDataElementsDeceasedNo(@Optional("VA") String state) throws SftpException, JSchException, IOException {
		testRefundProcessTemplate.pas7039_newDataElementsDeceasedNo(getState());
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.HOME_SS_HO3, testCaseId = {"PAS-7298"})
	public void pas7298_pendingManualRefundsCC(@Optional("VA") String state) throws IllegalAccessException {
		testRefundProcessTemplate.pas7298_pendingManualRefundsCC(getState());
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.HOME_SS_HO3, testCaseId = {"PAS-7298"})
	public void pas7298_pendingManualRefundsACH(@Optional("MD") String state) throws IllegalAccessException {
		testRefundProcessTemplate.pas7298_pendingManualRefundsACH(getState());
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.HOME_SS_HO3, testCaseId = {"PAS-7298"})
	public void pas7298_pendingAutomatedRefundsCC(@Optional("VA") String state) throws IllegalAccessException {
		testRefundProcessTemplate.pas7298_pendingAutomatedRefundsCC(getState());
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.HOME_SS_HO3, testCaseId = {"PAS-7298"})
	public void pas7298_pendingAutomatedRefundsACH(@Optional("MD") String state) throws IllegalAccessException {
		testRefundProcessTemplate.pas7298_pendingAutomatedRefundsACH(getState());
	}

	@AfterClass(alwaysRun = true)
	private void deleteMultiplePaperlessPreferencesRequests() {
		testRefundProcessTemplate.deleteMultiplePaperlessPreferencesRequests();
	}
}
