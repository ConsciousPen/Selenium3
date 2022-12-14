package aaa.modules.regression.billing_and_payments.template.functional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.rest.wiremock.HelperWireMockStub;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.billing_and_payments.helpers.RefundProcessHelper;
import aaa.modules.regression.billing_and_payments.template.PolicyBilling;
import aaa.modules.regression.service.helper.HelperWireMockLastPaymentMethod;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;

public class TestRefundProcessTemplate extends PolicyBilling {
	private static final String PENDING_REFUND_AMOUNT_HO_PUP = "1000";
	private static final String APPROVED_REFUND_AMOUNT_HO_PUP = "999.99";
	private static final String APPROVED_REFUND_AMOUNT_AUTO = "499.99";
	private static final String PENDING_REFUND_AMOUNT_AUTO = "500";
	private final List<HelperWireMockStub> requestIdList = new LinkedList<>();
	private TestData tdBilling = testDataManager.billingAccount;
	private BillingAccount billingAccount = new BillingAccount();
	private RefundProcessHelper refundProcessHelper = new RefundProcessHelper();
	private HelperWireMockLastPaymentMethod helperWireMockLastPaymentMethod = new HelperWireMockLastPaymentMethod();

	private PolicyType policyType;

	private TestRefundProcessTemplate() {}
	public TestRefundProcessTemplate(PolicyType policyType) {
		this.policyType = policyType;
	}

	@Override
	protected PolicyType getPolicyType() {
		return policyType;
	}

	private String getPendingRefundAmountForTest() {
		if (getPolicyType().isAutoPolicy()) {
			return PENDING_REFUND_AMOUNT_AUTO;
		}
		return PENDING_REFUND_AMOUNT_HO_PUP;
	}

	private String getApprovedRefundAmountForTest() {
		if (getPolicyType().isAutoPolicy()) {
			return APPROVED_REFUND_AMOUNT_AUTO;
		}
		return APPROVED_REFUND_AMOUNT_HO_PUP;
	}

	@Test(description = "Precondition for TestRefundProcess tests", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public void precondJobAdding() {
		JobUtils.createJob(BatchJob.aaaRefundGenerationAsyncJob);
		JobUtils.createJob(BatchJob.aaaRefundDisbursementAsyncJob);
		JobUtils.createJob(BatchJob.aaaRefundsDisbursementReceiveInfoAsyncJob);
		JobUtils.createJob(BatchJob.aaaRefundCancellationAsyncJob);
		JobUtils.createJob(BatchJob.aaaRefundsDisbursementRejectionsAsyncJob);
	}

	public void pas7039_Debug(@Optional("VA") String state) throws SftpException, JSchException, IOException {
		String manualRefundAmount = "100";
		String automatedRefundAmount = "101";
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, "VAH3952521998");
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		refundProcessHelper.refundDebug(policyNumber, "M", "CHCK", "HO", "4WUIC", "Y", "VA", manualRefundAmount, "", "Y");
	}

	public void pas7039_newDataElementsDeceasedYes(@Optional("VA") String state) throws SftpException, JSchException, IOException {
		String manualRefundAmount = "100";
		String automatedRefundAmount = "101";
		mainApp().open();

		String policyNumber = preconditionPolicyCreation();

		policyType.get().endorse().perform(getStateTestData(testDataManager.policy.get(policyType).getTestData("Endorsement"), "TestData"));

		if (Constants.States.CA.equals(getState()) && !policyType.equals(PolicyType.PUP)) {
			NavigationPage.toViewSubTab(NavigationEnum.HomeCaTab.APPLICANT.get());
			new aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab().getAssetList().getAsset(HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel(), MultiAssetList.class)
					.getAsset(HomeCaMetaData.ApplicantTab.NamedInsured.RELATIONSHIP_TO_PRIMARY_NAMED_INSURED.getLabel(), ComboBox.class).setValue("Deceased");
			new aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab().calculatePremium();
			NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
			new aaa.main.modules.policy.home_ca.defaulttabs.BindTab().submitTab();
		} else if (!Constants.States.CA.equals(getState()) && !policyType.equals(PolicyType.PUP)) {
			NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.APPLICANT.get());
			new ApplicantTab().getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), MultiAssetList.class)
					.getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.RELATIONSHIP_TO_PRIMARY_NAMED_INSURED.getLabel(), ComboBox.class).setValue("Deceased");
			new PremiumsAndCoveragesQuoteTab().calculatePremium();
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
			new BindTab().submitTab();
		} else if (policyType.equals(PolicyType.PUP)) {
			NavigationPage.toViewSubTab(NavigationEnum.PersonalUmbrellaTab.PREFILL.get());
			new PrefillTab().getAssetList().getAsset(PersonalUmbrellaMetaData.PrefillTab.NAMED_INSURED.getLabel(), MultiAssetList.class)
					.getAsset(PersonalUmbrellaMetaData.PrefillTab.NamedInsured.RELATIONSHIP_TO_PRIMARY_NAMED_INSURED.getLabel(), ComboBox.class).setValue("Deceased");
			new PremiumAndCoveragesQuoteTab().calculatePremium();
			NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.BIND.get());
			new BindTab().submitTab();
		}

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		billingAccount.refund().manualRefundPerform("Check", manualRefundAmount);

		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusHours(2));

		JobUtils.executeJob(BatchJob.aaaRefundDisbursementAsyncJob);
		refundProcessHelper.refundRecordInFileCheck(getPolicyType(), policyNumber, "M", "CHCK", "4WUIC", "Y", "VA", manualRefundAmount, "", "Y");

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		Dollar totalDue = BillingSummaryPage.getTotalDue();
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), totalDue.add(new Dollar(automatedRefundAmount)));

		LocalDateTime refundDate = getTimePoints().getRefundDate(DateTimeUtils.getCurrentDateTime());
		TimeSetterUtil.getInstance().nextPhase(refundDate);

		JobUtils.executeJob(BatchJob.aaaRefundGenerationAsyncJob);
		JobUtils.executeJob(BatchJob.aaaRefundDisbursementAsyncJob);
		refundProcessHelper.refundRecordInFileCheck(getPolicyType(), policyNumber, "R", "CHCK", "4WUIC", "Y", "VA", automatedRefundAmount, "", "Y");
	}

	public void pas7039_newDataElementsDeceasedNo(@Optional("VA") String state) throws SftpException, JSchException, IOException {
		String manualRefundAmount = "100";
		String automatedRefundAmount = "101";

		String policyNumber = preconditionPolicyCreation();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		billingAccount.refund().manualRefundPerform("Check", manualRefundAmount);

		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusHours(2));

		JobUtils.executeJob(BatchJob.aaaRefundDisbursementAsyncJob);
		refundProcessHelper.refundRecordInFileCheck(getPolicyType(), policyNumber, "M", "CHCK", "4WUIC", "N", "VA", manualRefundAmount, "", "Y");

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		Dollar totalDue = BillingSummaryPage.getTotalDue();
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), totalDue.add(new Dollar(automatedRefundAmount)));
		LocalDateTime refundDate = getTimePoints().getRefundDate(DateTimeUtils.getCurrentDateTime());
		TimeSetterUtil.getInstance().nextPhase(refundDate);

		JobUtils.executeJob(BatchJob.aaaRefundGenerationAsyncJob);
		JobUtils.executeJob(BatchJob.aaaRefundDisbursementAsyncJob);
		refundProcessHelper.refundRecordInFileCheck(getPolicyType(), policyNumber, "R", "CHCK", "4WUIC", "N", "VA", automatedRefundAmount, "", "Y");
	}

	public void pas7298_pendingManualRefundsCC(@Optional("VA") String state) throws IllegalAccessException {

		String paymentMethod = "contains=Credit Card";

		String policyNumber = preconditionPolicyCreation();
		HelperWireMockStub stubRequestCC = helperWireMockLastPaymentMethod.getHelperWireMockStubCC(policyNumber, getPendingRefundAmountForTest());
		requestIdList.add(stubRequestCC);

		try {
			refundProcessHelper.pas7298_pendingManualRefunds(getPendingRefundAmountForTest(), getApprovedRefundAmountForTest(), paymentMethod);
		} finally {
			stubRequestCC.cleanUp();
		}
	}

	public void pas7298_pendingManualRefundsACH(@Optional("MD") String state) throws IllegalAccessException {

		String paymentMethod = "contains=ACH";

		String policyNumber = preconditionPolicyCreation();
		HelperWireMockStub stubRequestACH = helperWireMockLastPaymentMethod.getHelperWireMockStubACH(policyNumber, getPendingRefundAmountForTest());
		requestIdList.add(stubRequestACH);

		try {
			refundProcessHelper.pas7298_pendingManualRefunds(getPendingRefundAmountForTest(), getApprovedRefundAmountForTest(), paymentMethod);
		} finally {
			stubRequestACH.cleanUp();
		}
	}

	public void pas7298_pendingAutomatedRefundsCC(@Optional("VA") String state) throws IllegalAccessException {

		String paymentMethod = "Credit Card";

		String policyNumber = preconditionPolicyCreation();
		HelperWireMockStub stubRequestCC = helperWireMockLastPaymentMethod.getHelperWireMockStubCC(policyNumber, getPendingRefundAmountForTest());
		requestIdList.add(stubRequestCC);

		try {
			refundProcessHelper.pas7298_pendingAutomatedRefunds(policyNumber, getApprovedRefundAmountForTest(), getPendingRefundAmountForTest(), paymentMethod, getTimePoints());
		} finally {
			stubRequestCC.cleanUp();
		}
	}

	public void pas7298_pendingAutomatedRefundsACH(@Optional("MD") String state) throws IllegalAccessException {

		String paymentMethod = "ACH";

		String policyNumber = preconditionPolicyCreation();

		HelperWireMockStub stubRequestACH = helperWireMockLastPaymentMethod.getHelperWireMockStubACH(policyNumber, getPendingRefundAmountForTest());
		requestIdList.add(stubRequestACH);

		try {
			refundProcessHelper.pas7298_pendingAutomatedRefunds(policyNumber, getApprovedRefundAmountForTest(), getPendingRefundAmountForTest(), paymentMethod, getTimePoints());
		} finally {
			stubRequestACH.cleanUp();
		}
	}

	private String preconditionPolicyCreation() {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();
		log.info("policyNumber: {}", policyNumber);
		return policyNumber;
	}

	public void deleteMultiplePaperlessPreferencesRequests() {
		for (HelperWireMockStub wireMockStubObject : requestIdList) {
			wireMockStubObject.cleanUp();
		}
		requestIdList.clear();
	}
}
