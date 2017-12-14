package aaa.modules.conversion;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.constants.Groups;
import aaa.helpers.conversion.ConversionPolicyData;
import aaa.helpers.conversion.ConversionUtils;
import aaa.helpers.conversion.MaigConversionData;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.metadata.policy.AutoSSMetaData.*;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.ITestContext;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.datetime.DateTimeUtils;

import java.time.LocalDateTime;

public class MaigConversionTest extends AutoSSBaseTest {

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void maigConversionTest1(@Optional("VA") String state, ITestContext context) {
		maigConversion("1.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void maigConversionTest2(@Optional("DE") String state, ITestContext context) {
		maigConversion("2.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void maigConversionTest3(@Optional("PA") String state, ITestContext context) {
		maigConversion("3.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void maigConversionTest4(@Optional("MD") String state, ITestContext context) {
		maigConversion("4.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void maigConversionTest5(@Optional("NJ") String state, ITestContext context) {
		maigConversion("5.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void maigConversionTest_customerDeclined1(@Optional("VA") String state, ITestContext context) {
		maigConversion_customerDeclined("1.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void maigConversionTest_customerDeclined2(@Optional("DE") String state, ITestContext context) {
		maigConversion_customerDeclined("2.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void maigConversionTest_customerDeclined3(@Optional("PA") String state, ITestContext context) {
		maigConversion_customerDeclined("3.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void maigConversionTest_customerDeclined4(@Optional("MD") String state, ITestContext context) {
		maigConversion_customerDeclined("4.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void maigConversionTest_customerDeclined5(@Optional("NJ") String state, ITestContext context) {
		maigConversion_customerDeclined("5.xml", context);
	}

	public void maigConversion(String file, ITestContext context) {
		LocalDateTime effDate = getTimePoints().getConversionEffectiveDate();
		ConversionPolicyData data = new MaigConversionData(file, effDate);
		String policyNum = ConversionUtils.importPolicy(data, context);
//		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
//		new BillingAccount().update().perform(testDataManager.billingAccount.getTestData("Update", "TestData_AddAutopay")
//				.adjust(TestData.makeKeyPath("UpdateBillingAccountActionTab","Billing Account Name Type"), "Individual"));

		mainApp().open();
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
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
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

	public void maigConversion_customerDeclined(String file, ITestContext context) {
		LocalDateTime effDate = getTimePoints().getConversionEffectiveDate();
		ConversionPolicyData data = new MaigConversionData(file, effDate);
		String policyNum = ConversionUtils.importPolicy(data, context);

		mainApp().open();
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

		BillingSummaryPage.openPolicy(1);
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());
		new aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab().getAssetList().getAsset(AutoSSMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY).click();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		new aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab().submitTab();

		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(effDate);
	}

	private void fillPolicy(LocalDateTime effDate) {
		policy.dataGather().start();
		policy.getDefaultView().fill(getPolicyTD().adjust(TestData.makeKeyPath(PrefillTab.class.getSimpleName(), PrefillTab.DATE_OF_BIRTH.getLabel()), "08/08/1977")
				.adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), "NamedInsuredInformation[0]", "Base Date"), effDate.format(DateTimeUtils.MM_DD_YYYY))
				.mask(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), GeneralTab.POLICY_INFORMATION.getLabel()))
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), DriverTab.GENDER.getLabel()), "index=1")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), DriverTab.MARITAL_STATUS.getLabel()), "index=1")
				.adjust(TestData.makeKeyPath(VehicleTab.class.getSimpleName(), VehicleTab.TYPE.getLabel()), "Private Passenger Auto")
				.adjust(PremiumAndCoveragesTab.class.getSimpleName(), new SimpleDataProvider())
				.mask(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName(), DriverActivityReportsTab.HAS_THE_CUSTOMER_EXPRESSED_INTEREST_IN_PURCHASING_THE_QUOTE.getLabel()))
				.mask(TestData.makeKeyPath(DocumentsAndBindTab.class.getSimpleName(), DocumentsAndBindTab.AGREEMENT.getLabel()))
				.adjust(TestData.makeKeyPath(DocumentsAndBindTab.class.getSimpleName(), DocumentsAndBindTab.AUTHORIZED_BY.getLabel()), "qa")
				.mask(new PurchaseTab().getMetaKey()));
	}
}
