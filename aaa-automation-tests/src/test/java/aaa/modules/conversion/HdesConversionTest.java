package aaa.modules.conversion;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.constants.Groups;
import aaa.helpers.conversion.ConversionPolicyData;
import aaa.helpers.conversion.ConversionUtils;
import aaa.helpers.conversion.HdesConversionData;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.MyWorkConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.MyWorkSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.ITestContext;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

public class HdesConversionTest extends HomeCaHO3BaseTest {

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO3ConversionTest1(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion("HO3/1.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO3ConversionTest2(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion("HO3/2.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO3ConversionTest3(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion("HO3/3.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO3ConversionTest4(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion("HO3/4.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO4ConversionTest1(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion("HO4/1.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO4ConversionTest2(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion("HO4/2.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO4ConversionTest3(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion("HO4/3.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO6ConversionTest1(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion("HO6/1.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO6ConversionTest2(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion("HO6/2.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO6ConversionTest3(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion("HO6/3.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO6ConversionTest4(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion("HO6/4.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO6ConversionTest5(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion("HO6/5.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO6ConversionTest6(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion("HO6/6.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO3ConversionTest_renewWithLapse1(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewWithLapse("HO3/1.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO3ConversionTest_renewWithLapse2(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewWithLapse("HO3/2.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO3ConversionTest_renewWithLapse3(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewWithLapse("HO3/3.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO3ConversionTest_renewWithLapse4(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewWithLapse("HO3/4.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO4ConversionTest_renewWithLapse1(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewWithLapse("HO4/1.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO4ConversionTest_renewWithLapse2(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewWithLapse("HO4/2.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO4ConversionTest_renewWithLapse3(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewWithLapse("HO4/3.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO6ConversionTest_renewWithLapse1(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewWithLapse("HO6/1.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO6ConversionTest_renewWithLapse2(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewWithLapse("HO6/2.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO6ConversionTest_renewWithLapse3(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewWithLapse("HO6/3.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO6ConversionTest_renewWithLapse4(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewWithLapse("HO6/4.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO6ConversionTest_renewWithLapse5(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewWithLapse("HO6/5.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO6ConversionTest_renewWithLapse6(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewWithLapse("HO6/6.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO3ConversionTest_renewAfterPayment1(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewAfterPayment("HO3/1.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO3ConversionTest_renewAfterPayment2(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewAfterPayment("HO3/2.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO3ConversionTest_renewAfterPayment3(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewAfterPayment("HO3/3.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO3ConversionTest_renewAfterPayment4(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewAfterPayment("HO3/4.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO4ConversionTest_renewAfterPayment1(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewAfterPayment("HO4/1.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO4ConversionTest_renewAfterPayment2(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewAfterPayment("HO4/2.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO4ConversionTest_renewAfterPayment3(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewAfterPayment("HO4/3.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO6ConversionTest_renewAfterPayment1(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewAfterPayment("HO6/1.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO6ConversionTest_renewAfterPayment2(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewAfterPayment("HO6/2.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO6ConversionTest_renewAfterPayment3(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewAfterPayment("HO6/3.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO6ConversionTest_renewAfterPayment4(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewAfterPayment("HO6/4.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO6ConversionTest_renewAfterPayment5(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewAfterPayment("HO6/5.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	public void hdesCAHO6ConversionTest_renewAfterPayment6(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewAfterPayment("HO6/6.xml", context);
	}

	public void hdesCAConversion(String file, ITestContext context) {
		LocalDateTime effDate = getTimePoints().getConversionEffectiveDate();
		ConversionPolicyData data = new HdesConversionData(file, effDate);
		String policyNum = ConversionUtils.importPolicy(data, context);
//		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
//		new BillingAccount().update().perform(testDataManager.billingAccount.getTestData("Update", "TestData_AddAutopay")
//				.adjust(TestData.makeKeyPath("UpdateBillingAccountActionTab","Billing Account Name Type"), "Individual"));

		mainApp().open();
		SearchPage.openPolicy(policyNum);
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

	public void hdesCAConversion_renewWithLapse(String file, ITestContext context) {
		LocalDateTime effDate = getTimePoints().getConversionEffectiveDate();
		ConversionPolicyData data = new HdesConversionData(file, effDate);
		String policyNum = ConversionUtils.importPolicy(data, context);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
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

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.verifyLapseExistFlagPresent();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(effDate);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getPayLapsedRenewLong(effDate).plusDays(1))
				.setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();
	}

	public void hdesCAConversion_renewAfterPayment(String file, ITestContext context) {
		LocalDateTime effDate = getTimePoints().getConversionEffectiveDate();
		ConversionPolicyData data = new HdesConversionData(file, effDate);
		String policyNum = ConversionUtils.importPolicy(data, context);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(effDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(effDate));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewCustomerDeclineDate(effDate));
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.CUSTOMER_DECLINED).verifyRowWithEffectiveDate(effDate);

//		TimeSetterUtil.getInstance().nextPhase(effDate.plusDays(15));
//		mainApp().open();
//		SearchPage.openBilling(policyNum);
//		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.CUSTOMER_DECLINED).verifyRowWithEffectiveDate(effDate);

		Dollar rAmount = BillingHelper.getBillMinDueAmount(effDate, BillingConstants.BillsAndStatementsType.OFFER);
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), rAmount);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(effDate);
	}
}
