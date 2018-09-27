package aaa.modules.regression.billing_and_payments.pup.functional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import aaa.admin.pages.general.GeneralSchedulerPage;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.billing_and_payments.auto_ss.functional.preconditions.TestRefundProcessPreConditions;
import aaa.modules.regression.billing_and_payments.helpers.RefundProcessHelper;
import aaa.modules.regression.service.helper.HelperWireMockLastPaymentMethod;
import aaa.modules.regression.service.helper.wiremock.HelperWireMockStub;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;

public class TestRefundProcess extends PolicyBaseTest implements TestRefundProcessPreConditions {

	private static final String PENDING_REFUND_AMOUNT = "1000";
	private static final String APPROVED_REFUND_AMOUNT = "999.99";
	private final List<HelperWireMockStub> requestIdList = new LinkedList<>();
	private TestData tdBilling = testDataManager.billingAccount;
	private BillingAccount billingAccount = new BillingAccount();
	private PremiumAndCoveragesQuoteTab premiumAndCoveragesQuoteTab = new PremiumAndCoveragesQuoteTab();
	private BindTab bindTab = new BindTab();
	private PrefillTab prefillTab = new PrefillTab();
	private RefundProcessHelper refundProcessHelper = new RefundProcessHelper();
	private HelperWireMockLastPaymentMethod helperWireMockLastPaymentMethod = new HelperWireMockLastPaymentMethod();

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}

	@Test(description = "Precondition for TestRefundProcess tests")
	public void precondJobAdding() {
		adminApp().open();
		NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.GENERAL_SCHEDULER.get());
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AAA_REFUND_GENERATION_ASYNC_JOB);
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AAA_REFUND_DISBURSEMENT_ASYNC_JOB);
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AAA_REFUND_DISBURSEMENT_RECEIVE_INFO_JOB);
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AAA_REFUNDS_DISBURSMENT_REJECTIONS_ASYNC_JOB);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.PUP, testCaseId = {"PAS-7039", "PAS-7196", "PAS-450"})
	public void pas7039_newDataElementsDeceasedYes(@Optional("VA") String state) throws SftpException, JSchException, IOException {
		String manualRefundAmount = "100";
		String automatedRefundAmount = "101";

		String policyNumber = preconditionPolicyCreationPup();

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(NavigationEnum.PersonalUmbrellaTab.PREFILL.get());
		prefillTab.getAssetList().getAsset(PersonalUmbrellaMetaData.PrefillTab.NAMED_INSURED.getLabel(), MultiAssetList.class)
				.getAsset(PersonalUmbrellaMetaData.PrefillTab.NamedInsured.RELATIONSHIP_TO_PRIMARY_NAMED_INSURED.getLabel(), ComboBox.class).setValue("Deceased");

		premiumAndCoveragesQuoteTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.BIND.get());
		bindTab.submitTab();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		billingAccount.refund().manualRefundPerform("Check", manualRefundAmount);

		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusHours(2));

		JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);
		refundProcessHelper.refundRecordInFileCheck(policyNumber, "M", "CHCK", "PU", "4WUIC", "Y", "VA", manualRefundAmount, "", "Y");

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		Dollar totalDue = BillingSummaryPage.getTotalDue();
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), totalDue.add(new Dollar(automatedRefundAmount)));
		LocalDateTime refundDate = getTimePoints().getRefundDate(DateTimeUtils.getCurrentDateTime());
		TimeSetterUtil.getInstance().nextPhase(refundDate);

		JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob);
		JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);
		refundProcessHelper.refundRecordInFileCheck(policyNumber, "R", "CHCK", "PU", "4WUIC", "Y", "VA", automatedRefundAmount, "", "Y");
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.PUP, testCaseId = {"PAS-7039", "PAS-7196", "PAS-450"})
	public void pas7039_newDataElementsDeceasedNo(@Optional("VA") String state) throws SftpException, JSchException, IOException {
		String manualRefundAmount = "100";
		String automatedRefundAmount = "101";

		String policyNumber = preconditionPolicyCreationPup();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		billingAccount.refund().manualRefundPerform("Check", manualRefundAmount);

		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusHours(2));

		JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);
		refundProcessHelper.refundRecordInFileCheck(policyNumber, "M", "CHCK", "PU", "4WUIC", "N", "VA", manualRefundAmount, "", "Y");

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		Dollar totalDue = BillingSummaryPage.getTotalDue();
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), totalDue.add(new Dollar(automatedRefundAmount)));
		LocalDateTime refundDate = getTimePoints().getRefundDate(DateTimeUtils.getCurrentDateTime());
		TimeSetterUtil.getInstance().nextPhase(refundDate);

		JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob);
		JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);
		refundProcessHelper.refundRecordInFileCheck(policyNumber, "R", "CHCK", "PU", "4WUIC", "N", "VA", automatedRefundAmount, "", "Y");
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.PUP, testCaseId = {"PAS-7298"})
	public void pas7298_pendingManualRefundsCC(@Optional("VA") String state) throws IllegalAccessException {

		String paymentMethod = "contains=Credit Card";

		String policyNumber = preconditionPolicyCreationPup();
		HelperWireMockStub stubRequestCC = helperWireMockLastPaymentMethod.getHelperWireMockStubCC(policyNumber, PENDING_REFUND_AMOUNT);
		requestIdList.add(stubRequestCC);

		try {
			refundProcessHelper.pas7298_pendingManualRefunds(PENDING_REFUND_AMOUNT, APPROVED_REFUND_AMOUNT, paymentMethod);
		} finally {
			stubRequestCC.cleanUp();
		}
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.PUP, testCaseId = {"PAS-7298"})
	public void pas7298_pendingManualRefundsCheck(@Optional("VA") String state) {

		String paymentMethod = "Check";

		preconditionPolicyCreationPup();

		refundProcessHelper.pas7298_pendingManualRefunds(PENDING_REFUND_AMOUNT, APPROVED_REFUND_AMOUNT, paymentMethod);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.PUP, testCaseId = {"PAS-7298"})
	public void pas7298_pendingAutomatedRefundsCC(@Optional("VA") String state) throws IllegalAccessException {

		String paymentMethod = "Credit Card";

		String policyNumber = preconditionPolicyCreationPup();
		HelperWireMockStub stubRequestCC = helperWireMockLastPaymentMethod.getHelperWireMockStubCC(policyNumber, PENDING_REFUND_AMOUNT);
		requestIdList.add(stubRequestCC);

		try {
			refundProcessHelper.pas7298_pendingAutomatedRefunds(policyNumber, APPROVED_REFUND_AMOUNT, PENDING_REFUND_AMOUNT, paymentMethod, getTimePoints());
		} finally {
			stubRequestCC.cleanUp();
		}
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.PUP, testCaseId = {"PAS-7298"})
	public void pas7298_pendingAutomatedRefundsCheck(@Optional("VA") String state) {

		String paymentMethod = "Check";

		String policyNumber = preconditionPolicyCreationPup();

		refundProcessHelper.pas7298_pendingAutomatedRefunds(policyNumber, APPROVED_REFUND_AMOUNT, PENDING_REFUND_AMOUNT, paymentMethod, getTimePoints());
	}

	private String preconditionPolicyCreationPup() {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();
		log.info("policyNumber: {}", policyNumber);
		return policyNumber;
	}

	@AfterSuite(alwaysRun = true)
	private void deleteMultipleLastPaymentRequests() {
		for (HelperWireMockStub wireMockStubObject : requestIdList) {
			wireMockStubObject.cleanUp();
		}
		requestIdList.clear();
	}

}
