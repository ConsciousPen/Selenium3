package aaa.modules.e2e.home_ss.ho3;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.PolicyHelper;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.e2e.ScenarioBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestScenario8 extends ScenarioBaseTest {
	protected IPolicy policy;
	protected TestData tdPolicy;

	protected LocalDateTime policyEffectiveDate;
	protected LocalDateTime policyExpirationDate;

	protected List<LocalDateTime> installmentDueDates;

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Test
	public void TC01_createPolicy() {
		tdPolicy = testDataManager.policy.get(getPolicyType());

		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());
		policy = getPolicyType().get();

		mainApp().open();
		createCustomerIndividual();

		policyNum = createPolicy(policyCreationTD);
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		policyExpirationDate = PolicySummaryPage.getExpirationDate();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		CustomAssert.assertEquals("Billing Installments count for Monthly (Eleven Pay) payment plan", 11, installmentDueDates.size());
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC02_Generate_Second_Bill() {
		generateAndCheckBill(installmentDueDates.get(1));
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_Change_Payment_To_Quarterly() {
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		TestData endorsementTD = getStateTestData(tdPolicy, "Endorsement", "TestData");
		policy.endorse().performAndFill(getTestSpecificTD("TestData_Endorsement").adjust(endorsementTD));
		PolicyHelper.verifyEndorsementIsCreated();
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingAccountPoliciesVerifier().setPaymentPlan(BillingConstants.PaymentPlan.QUARTERLY).verifyRowWithEffectiveDate(policyEffectiveDate);
		BillingSummaryPage.tableInstallmentSchedule.verify.rowsCount(4, Collections.singletonMap(BillingConstants.BillingInstallmentScheduleTable.DESCRIPTION, BillingConstants.InstallmentDescription.INSTALLMENT));
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC04_Generate_Second_Quterly_Bill() {
		generateAndCheckBill(installmentDueDates.get(3), policyEffectiveDate);
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Pay_Second_Quterly_Bill() {
		payAndCheckBill(installmentDueDates.get(3));
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Change_Payment_To_Monthly() {
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		TestData endorsementTD = getStateTestData(tdPolicy, "Endorsement", "TestData");
		policy.endorse().performAndFill(getTestSpecificTD("TestData_Endorsement2").adjust(endorsementTD));

		PolicyHelper.verifyEndorsementIsCreated();
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingAccountPoliciesVerifier().setPaymentPlan(BillingConstants.PaymentPlan.ELEVEN_PAY).verifyRowWithEffectiveDate(policyEffectiveDate);
		BillingSummaryPage.tableInstallmentSchedule.verify.rowsCount(9, Collections.singletonMap(BillingConstants.BillingInstallmentScheduleTable.DESCRIPTION, BillingConstants.InstallmentDescription.INSTALLMENT));
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Generate_Forth_Monthly_Bill() {
		generateAndCheckBill(installmentDueDates.get(4), policyEffectiveDate);
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Pay_Forth_Monthly_Bill() {
		payAndCheckBill(installmentDueDates.get(4));
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Generate_Five_Monthly_Bill() {
		generateAndCheckBill(installmentDueDates.get(5), policyEffectiveDate);
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Change_Payment_To_Semi_Annaual() {
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		TestData endorsementTD = getStateTestData(tdPolicy, "Endorsement", "TestData");
		policy.endorse().performAndFill(getTestSpecificTD("TestData_Endorsement3").adjust(endorsementTD));
		PolicyHelper.verifyEndorsementIsCreated();
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingAccountPoliciesVerifier().setPaymentPlan(BillingConstants.PaymentPlan.SEMI_ANNUAL).verifyRowWithEffectiveDate(policyEffectiveDate);
		BillingSummaryPage.tableInstallmentSchedule.verify.rowsCount(5, Collections.singletonMap(BillingConstants.BillingInstallmentScheduleTable.DESCRIPTION, BillingConstants.InstallmentDescription.INSTALLMENT));
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Generate_Semi_Annual_Bill() {
		generateAndCheckBill(installmentDueDates.get(6), policyEffectiveDate);
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Pay_Semi_Annual_Bill() {
		payAndCheckBill(installmentDueDates.get(6));
	}

	/**
	 * TODO Add Test Change current Payment plan
	 */

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Renewal_Image_Generation() {
		LocalDateTime renewDateImage = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDateImage);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);

		PolicyHelper.verifyAutomatedRenewalGenerated(renewDateImage); // verify Not Generated in excel
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Renewal_Preview_Generation() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.buttonRenewals.verify.enabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC15_Change_Payment_In_Renewal_To_Monthly() {
		mainApp().open();
		SearchPage.openPolicy(policyNum);

		TestData endorsementTD = getStateTestData(tdPolicy, "Endorsement", "TestData");
		policy.renew().performAndFill(getTestSpecificTD("TestData_Endorsement4").adjust(endorsementTD));

		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.verify.enabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.showPriorTerms();

		/** TODO Why 5??? */
		new BillingAccountPoliciesVerifier().setPaymentPlan(BillingConstants.PaymentPlan.SEMI_ANNUAL).verifyRowWithEffectiveDate(policyEffectiveDate);
		BillingSummaryPage.tableInstallmentSchedule.verify.rowsCount(5, Collections.singletonMap(BillingConstants.BillingInstallmentScheduleTable.DESCRIPTION, BillingConstants.InstallmentDescription.INSTALLMENT));
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC16_Change_Payment_In_Policy_To_Quarterly() {
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		TestData endorsementTD = getStateTestData(tdPolicy, "Endorsement", "TestData");
		policy.endorse().performAndFill(getTestSpecificTD("TestData_Endorsement").adjust(endorsementTD));

		PolicyHelper.verifyEndorsementIsCreated();
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.verify.enabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.showPriorTerms();

		/** TODO Why 5??? */
		new BillingAccountPoliciesVerifier().setPaymentPlan(BillingConstants.PaymentPlan.QUARTERLY).verifyRowWithEffectiveDate(policyEffectiveDate);
		BillingSummaryPage.tableInstallmentSchedule.verify.rowsCount(5, Collections.singletonMap(BillingConstants.BillingInstallmentScheduleTable.DESCRIPTION, BillingConstants.InstallmentDescription.INSTALLMENT));
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC17_Renewal_Offer_Generation() {
		LocalDateTime policyExpirationDateOffer = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDateOffer);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.verify.enabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).setPaymentPlan(BillingConstants.PaymentPlan.QUARTERLY)
				.verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).setPaymentPlan(BillingConstants.PaymentPlan.QUARTERLY_RENEWAL)
				.verifyRowWithEffectiveDate(policyExpirationDate);
		verifyRenewOfferGenerated(policyExpirationDate, Arrays.asList(installmentDueDates.get(0), installmentDueDates.get(3), installmentDueDates.get(6), installmentDueDates.get(9)));

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(policyExpirationDateOffer).setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL).verifyPresent();

		/** TODO Why 9??? */
		BillingSummaryPage.tableInstallmentSchedule.verify.rowsCount(9, Collections.singletonMap(BillingConstants.BillingInstallmentScheduleTable.DESCRIPTION, BillingConstants.InstallmentDescription.INSTALLMENT));
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC18_Change_Payment_Plan_To_Quarterly() {
		mainApp().open();
		SearchPage.openPolicy(policyNum);

		TestData endorsementTD = getStateTestData(tdPolicy, "Endorsement", "TestData");
		policy.renew().performAndFill(getTestSpecificTD("TestData_Endorsement5").adjust(endorsementTD));

		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();

		new BillingAccountPoliciesVerifier().setPaymentPlan(BillingConstants.PaymentPlan.QUARTERLY_RENEWAL).verifyRowWithEffectiveDate(policyExpirationDate);
		BillingSummaryPage.tableInstallmentSchedule.verify.rowsCount(9, Collections.singletonMap(BillingConstants.BillingInstallmentScheduleTable.DESCRIPTION, BillingConstants.InstallmentDescription.INSTALLMENT));
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC19_Renewal_Premium_Notice() {
		LocalDateTime billDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).setPaymentPlan(BillingConstants.PaymentPlan.QUARTERLY)
				.verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).setPaymentPlan(BillingConstants.PaymentPlan.QUARTERLY_RENEWAL)
				.verifyRowWithEffectiveDate(policyExpirationDate);
		verifyRenewPremiumNotice(policyExpirationDate, billDate);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();

		BillingSummaryPage.showPriorTerms();
		BillingSummaryPage.tableInstallmentSchedule.verify.rowsCount(9, Collections.singletonMap(BillingConstants.BillingInstallmentScheduleTable.DESCRIPTION, BillingConstants.InstallmentDescription.INSTALLMENT));
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC20_Change_Payment_Plan_To_Monthly() {
		mainApp().open();
		SearchPage.openPolicy(policyNum);

		TestData endorsementTD = getStateTestData(tdPolicy, "Endorsement", "TestData");
		policy.renew().performAndFill(getTestSpecificTD("TestData_Endorsement4").adjust(endorsementTD));

		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();

		new BillingAccountPoliciesVerifier().setPaymentPlan(BillingConstants.PaymentPlan.ELEVEN_PAY_RENEWAL).verifyRowWithEffectiveDate(policyExpirationDate);
		BillingSummaryPage.tableInstallmentSchedule.verify.rowsCount(16, Collections.singletonMap(BillingConstants.BillingInstallmentScheduleTable.DESCRIPTION, BillingConstants.InstallmentDescription.INSTALLMENT));
	}
}
