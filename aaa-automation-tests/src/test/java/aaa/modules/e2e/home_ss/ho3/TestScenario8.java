package aaa.modules.e2e.home_ss.ho3;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.PolicyHelper;
import aaa.main.enums.BillingConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.CommonErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.e2e.ScenarioBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class TestScenario8 extends ScenarioBaseTest {
	protected IPolicy policy;
	protected TestData tdPolicy;
	protected BillingAccount billingAccount = new BillingAccount();
	protected TestData tdBilling = testDataManager.billingAccount;

	protected LocalDateTime policyEffectiveDate;
	protected LocalDateTime policyExpirationDate;

	protected List<LocalDateTime> installmentDueDates;
	protected int installmentsCount = 11;

	protected Tab premiumTab;
	protected CommonErrorTab errorTab;

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Test
	public void TC01_createPolicy() {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		premiumTab = new PremiumsAndCoveragesQuoteTab();
		errorTab = new ErrorTab();

		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData")
				.adjust(getTestSpecificTD("TestData").resolveLinks());
		policy = getPolicyType().get();

		mainApp().open();
		createCustomerIndividual();

		if (getPolicyType().equals(PolicyType.PUP)) {
			policyCreationTD = new PrefillTab().adjustWithRealPolicies(policyCreationTD, getPrimaryPoliciesForPup());
		}
		policyNum = createPolicy(policyCreationTD);

		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		policyExpirationDate = PolicySummaryPage.getExpirationDate();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		CustomAssert.assertEquals("Billing Installments count for Monthly (Eleven Pay) payment plan", installmentsCount, installmentDueDates.size());
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC02_Generate_First_Bill() {
		generateAndCheckBill(installmentDueDates.get(1));
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_Endorse_Policy() {
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
		generateAndCheckBill(installmentDueDates.get(2));
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Pay_Third_Second_Bill() {
		payAndCheckBill(installmentDueDates.get(2));
	}
}
