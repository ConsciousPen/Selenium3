package aaa.modules.regression.conversions.home_ca.dp3;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import org.testng.ITestContext;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.conversion.ConversionPolicyData;
import aaa.helpers.conversion.ConversionUtils;
import aaa.helpers.conversion.SisConversionData;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaDP3BaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(states = {Constants.States.CA})
public class SisConversionTest extends HomeCaDP3BaseTest {

	@Parameters({"state", "file"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_DP3)
	public void sisCADP3ConversionTest(@Optional("CA") String state, @Optional("1.xml") String file) {
		sisConversion(file, context);
	}

	@Parameters({"state", "file"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_DP3)
	public void sisCADP3ConversionTest_renewWithLapse(@Optional("CA") String state, @Optional("1.xml") String file) {
		sisConversion_renewWithLapse(file, context);
	}

	public void sisConversion(String file, ITestContext context) {
		LocalDateTime effDate = getTimePoints().getConversionEffectiveDate();
		ConversionPolicyData data = new SisConversionData(file, effDate);
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
		if (!PolicySummaryPage.tableRenewals.getRow(1).getCell("Status").getValue().equals(ProductConstants.PolicyStatus.PROPOSED)) {
			overridePremiumVariationAndPropose();
		}
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
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		assertThat(PolicySummaryPage.getExpirationDate()).isEqualTo(effDate.plusYears(1));
	}

	public void sisConversion_renewWithLapse(String file, ITestContext context) {
		LocalDateTime effDate = getTimePoints().getConversionEffectiveDate();
		ConversionPolicyData data = new SisConversionData(file, effDate);
		String policyNum = ConversionUtils.importPolicy(data, context);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(effDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		if (!PolicySummaryPage.tableRenewals.getRow(1).getCell("Status").getValue().equals(ProductConstants.PolicyStatus.PROPOSED)) {
			overridePremiumVariationAndPropose();
		}
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
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.verifyLapseExistFlagPresent();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(effDate);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getPayLapsedRenewLong(effDate).plusDays(1))
				.setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();
	}

	private void overridePremiumVariationAndPropose() {
		BindTab bindTab = new BindTab();
		ErrorTab errorTab = new ErrorTab();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
		bindTab.submitTab();
		errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_CA_HOCN0400);
		errorTab.override();
		bindTab.submitTab();
	}
}
