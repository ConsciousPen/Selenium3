package aaa.modules.conversion;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.constants.Groups;
import aaa.helpers.conversion.ConversionPolicyData;
import aaa.helpers.conversion.ConversionUtils;
import aaa.helpers.conversion.FoxProConversionData;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.pup.defaulttabs.BindTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.ITestContext;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.Map;

public class FoxProConversionTest extends PersonalUmbrellaBaseTest {

	@Parameters({"state"})
	@Test
	public void foxProCAPUPConversionTest1(@Optional("CA") String state, ITestContext context) {
		foxProConversion("1.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void foxProCAPUPConversionTest2(@Optional("CA") String state, ITestContext context) {
		foxProConversion("2.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void foxProCAPUPConversionTest3(@Optional("CA") String state, ITestContext context) {
		foxProConversion("3.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void foxProCAPUPConversionTest4(@Optional("CA") String state, ITestContext context) {
		foxProConversion("4.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void foxProCAPUPConversionTest5(@Optional("CA") String state, ITestContext context) {
		foxProConversion("5.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void foxProCAPUPConversionTest_customerDeclined1(@Optional("CA") String state, ITestContext context) {
		foxProConversion_renewWithLapse("1.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void foxProCAPUPConversionTest_customerDeclined2(@Optional("CA") String state, ITestContext context) {
		foxProConversion_renewWithLapse("2.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void foxProCAPUPConversionTest_customerDeclined3(@Optional("CA") String state, ITestContext context) {
		foxProConversion_renewWithLapse("3.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void foxProCAPUPConversionTest_customerDeclined4(@Optional("CA") String state, ITestContext context) {
		foxProConversion_renewWithLapse("4.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void foxProCAPUPConversionTest_customerDeclined5(@Optional("CA") String state, ITestContext context) {
		foxProConversion_renewWithLapse("5.xml", context);
	}

	public void foxProConversion(String file, ITestContext context) {
		LocalDateTime effDate = getTimePoints().getConversionEffectiveDate();
		mainApp().open();
		createCustomerIndividual();
		Map<String, String> policies = getPrimaryPoliciesForPup();
		ConversionPolicyData data = new FoxProConversionData(file, effDate, policies.get("Primary_HO3"), policies.get("Primary_Auto"));
		String policyNum = ConversionUtils.importPolicy(data, context);

		SearchPage.openPolicy(policyNum);
		fillPolicy(effDate);
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);

		//TODO Verify coverages
		//TODO Generate notification letter

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(effDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);

		//Add billing verifications?

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(effDate));
		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(effDate, BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE));
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);

		//TODO  For autopay policies – generate banking reminder letter (I think around R-10)

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(effDate));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	public void foxProConversion_renewWithLapse(String file, ITestContext context) {
		LocalDateTime effDate = getTimePoints().getConversionEffectiveDate();
		mainApp().open();
		createCustomerIndividual();
		Map<String, String> policies = getPrimaryPoliciesForPup();
		ConversionPolicyData data = new FoxProConversionData(file, effDate, policies.get("Primary_HO3"), policies.get("Primary_Auto"));
		String policyNum = ConversionUtils.importPolicy(data, context);

		SearchPage.openPolicy(policyNum);
		fillPolicy(effDate);
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(effDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(effDate));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(effDate);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewCustomerDeclineDate(effDate));
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.CUSTOMER_DECLINED).verifyRowWithEffectiveDate(effDate);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getPayLapsedRenewLong(effDate).plusDays(1));
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.CUSTOMER_DECLINED).verifyRowWithEffectiveDate(effDate);

		SearchPage.openPolicy(policyNum);
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.CUSTOMER_DECLINED).verify(1);
		policy.manualRenewalWithOrWithoutLapse().perform(getPolicyTD("ManualRenewalWithOrWithoutLapse", "TestData"));

		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.verifyLapseExistFlagPresent();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(effDate);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getPayLapsedRenewLong(effDate).plusDays(1))
				.setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();
	}

	private void fillPolicy(LocalDateTime effDate) {
		policy.dataGather().start();
		PrefillTab prefillTab = new PrefillTab();
		prefillTab.getNamedInsuredListChangeLink(2).click();
		prefillTab.fillTab(new SimpleDataProvider()
				.adjust(PersonalUmbrellaMetaData.PrefillTab.class.getSimpleName(), new SimpleDataProvider())
				.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.PrefillTab.class.getSimpleName(), "NamedInsured"), getTestSpecificTD("NamedInsured2")));
		prefillTab.getNamedInsuredListChangeLink(1).click();

		policy.getDefaultView().fillUpTo(getPolicyTD()
				.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.PrefillTab.class.getSimpleName(), "NamedInsured"), getTestSpecificTD("NamedInsured"))
				.mask(TestData.makeKeyPath(PersonalUmbrellaMetaData.PrefillTab.class.getSimpleName(), "ActiveUnderlyingPolicies"))
				.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.GeneralTab.class.getSimpleName(), "PolicyInfo"), getTestSpecificTD("PolicyInfo"))
				.adjust(PersonalUmbrellaMetaData.UnderlyingRisksAllResidentsTab.class.getSimpleName(), getTestSpecificTD("UnderlyingRisksAllResidentsTab"))
				, BindTab.class);
		Tab.buttonSaveAndExit.click();
	}
}
