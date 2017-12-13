package aaa.modules.conversion;

import static aaa.main.metadata.policy.AutoSSMetaData.DriverTab.LICENSE_STATE;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.AAAProductOwned.LAST_NAME;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.POLICY_INFORMATION;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.PolicyInformation.LEAD_SOURCE;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TERM;
import static aaa.main.metadata.policy.AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN;
import java.time.LocalDateTime;
import aaa.common.Tab;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

public class RmeConversionTest extends AutoSSBaseTest {

	@Parameters({"state"})
	@Test
	public void maig(@Optional("VA") String state) {
		LocalDateTime effDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(45);//getTimePoints().getConversionEffectiveDate();
		mainApp().open();
		createCustomerIndividual();
		customer.initiateRenewalEntry().perform(getTestSpecificTD("TestData"));
		policy.getDefaultView().fill(prepareConvTD(getPolicyTD()));
		String policyNum = PolicySummaryPage.linkPolicy.getValue();
		SearchPage.openPolicy(policyNum);
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(effDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);

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

	private TestData prepareConvTD(TestData policyTd) {
		Tab generalTab = new GeneralTab();
		Tab premiumCovTab = new PremiumAndCoveragesTab();
		return policyTd.mask(TestData.makeKeyPath(generalTab.getMetaKey(), POLICY_INFORMATION.getLabel(), EFFECTIVE_DATE.getLabel())).
				adjust(TestData.makeKeyPath(generalTab.getMetaKey(), "NamedInsuredInformation[0]", "Base Date"), "$<today+45d:MM/dd/yyyy>").//effDate.format(DateTimeUtils.MM_DD_YYYY)).
				mask(TestData.makeKeyPath(generalTab.getMetaKey(), POLICY_INFORMATION.getLabel(), LEAD_SOURCE.getLabel())).
				mask(TestData.makeKeyPath(generalTab.getMetaKey(), AAA_PRODUCT_OWNED.getLabel(), LAST_NAME.getLabel())).
				adjust(TestData.makeKeyPath(generalTab.getMetaKey(), AAA_PRODUCT_OWNED.getLabel(), CURRENT_AAA_MEMBER.getLabel()), "No").
				adjust(TestData.makeKeyPath(new DriverTab().getMetaKey(), LICENSE_STATE.getLabel()), getState()).
				adjust(TestData.makeKeyPath(new VehicleTab().getMetaKey(), AutoSSMetaData.VehicleTab.TYPE.getLabel()), "Private Passenger Auto").
				mask(TestData.makeKeyPath(premiumCovTab.getMetaKey(), POLICY_TERM.getLabel())).
				adjust(TestData.makeKeyPath(premiumCovTab.getMetaKey(), PAYMENT_PLAN.getLabel()), "Annual (Renewal)").
				mask(TestData.makeKeyPath(AutoSSMetaData.DriverActivityReportsTab.class.getSimpleName(), AutoSSMetaData.DriverActivityReportsTab.HAS_THE_CUSTOMER_EXPRESSED_INTEREST_IN_PURCHASING_THE_QUOTE.getLabel())).
				mask(TestData.makeKeyPath(AutoSSMetaData.DocumentsAndBindTab.class.getSimpleName(), AutoSSMetaData.DocumentsAndBindTab.AGREEMENT.getLabel())).
				mask(new PurchaseTab().getMetaKey());
	}
}
