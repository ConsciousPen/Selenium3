package aaa.modules.regression.sales.auto_ss;

import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.PolicyConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestTelematicsParticipationDiscount extends AutoSSBaseTest {

	@StateList(statesExcept = Constants.States.CA)
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testTelematicsParticipationDiscount(@Optional("AZ") String state) {
		//TODO set
		/*adminApp().open();
		NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.GENERAL_DB_LOOKUPS.get());*/
		//
		String expectedTelematicsDiscountText = "Safety Score is now set to 'No Score' and telematics participation discount will be granted per auto tier, if available";

		TestData tdAuto = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS), "DataGather", TEST_DATA_KEY)
				.adjust("VehicleTab", getTestSpecificTD("VehicleTab")).adjust("DocumentsAndBindTab", getTestSpecificTD("DocumentsAndBindTab"));
		mainApp().open();
		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(tdAuto, VehicleTab.class, true);
		// Check telematics discount text
		assertThat(VehicleTab.telematicsDiscountText.getValue()).as("Telematics Discount Text displays on Vehicle tab").isEqualTo(expectedTelematicsDiscountText);
		new VehicleTab().submitTab();
		policy.getDefaultView().fillFromTo(tdAuto, FormsTab.class, PremiumAndCoveragesTab.class, true);

		//Check Discounts & Surcharges section
		assertThat(PremiumAndCoveragesTab.discountsAndSurcharges.getValue()).as("Discount and Surchrges Section").contains("Telematics Participation Discount");

		new PremiumAndCoveragesTab().submitTab();
		policy.getDefaultView().fillFromTo(tdAuto, DriverActivityReportsTab.class, PurchaseTab.class, true);
		policy.getDefaultView().getTab(PurchaseTab.class).submitTab();
		String policyNum = PolicySummaryPage.getPolicyNumber();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		LocalDateTime policyExpirationDate2 = PolicySummaryPage.getExpirationDate().plusYears(1);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(policyNum, DocGenEnum.Documents.AA02AZ);
		//perform Renewals
		performRenewalProcess(policyNum, 4);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
	}

	@StateList(statesExcept = Constants.States.CA)
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void test(@Optional("AZ") String state) {
		String policyNum = "AZSS926262824";
		String unableToScoreTelematicsDiscountText = "Unable to Score";
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(policyEffectiveDate.plusMonths(1)));
		policy.endorse().perform(getPolicyTD("Endorsement", TEST_DATA_KEY));
		policy.getDefaultView().fillUpTo(getTestSpecificTD("TestDataEndorsement"), VehicleTab.class, true);
		assertThat(VehicleTab.telematicsDiscountText.getValue()).as("Telematics Discount Text displays on Vehicle tab").isEqualTo(unableToScoreTelematicsDiscountText);
		policy.getDefaultView().fillFromTo(getTestSpecificTD("TestDataEndorsement"), VehicleTab.class, PurchaseTab.class, true);
		policy.dataGather().getView().getTab(PurchaseTab.class).submitTab();
		//step 48
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		//TODO step 50
		DocGenHelper.getDocument(DocGenEnum.Documents.AA02AZ, "query");

	}

	private void performRenewalProcess(String policyNum, int countsOfRenewals) {
		LocalDateTime policyExpirationDate;
		for (int i = 0; i < countsOfRenewals; i++) {
			mainApp().open();
			SearchPage.openPolicy(policyNum);
			policyExpirationDate = PolicySummaryPage.getExpirationDate();
			//R-96
			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewImageGenerationDate(policyExpirationDate));
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
			//R-63
			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewReportsDate(policyExpirationDate));
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
			//R-45
			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate));
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
			mainApp().open();
			SearchPage.openPolicy(policyNum);
			PolicySummaryPage.buttonRenewals.click();
			assertThat(PolicySummaryPage.tableRenewals.getRow(1).getCell(PolicyConstants.PolicyRenewalsTable.STATUS).getValue()).isEqualTo(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
			//R-35
			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
			mainApp().open();
			SearchPage.openPolicy(policyNum);
			PolicySummaryPage.buttonRenewals.click();
			//Check status of renewal
			assertThat(PolicySummaryPage.tableRenewals.getRow(1).getCell(PolicyConstants.PolicyRenewalsTable.STATUS).getValue()).isEqualTo(ProductConstants.PolicyStatus.PROPOSED);
			Dollar renewalPremium = new Dollar(PolicySummaryPage.tableRenewals.getRow(1).getCell(PolicyConstants.PolicyRenewalsTable.PREMIUM).getValue());
			//R-20
			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(policyExpirationDate));
			JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
			//R
			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(policyExpirationDate));
			mainApp().open();
			SearchPage.openBilling(policyNum);
			new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), renewalPremium);
			//R+1
			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate));
			JobUtils.executeJob(Jobs.policyStatusUpdateJob);
			JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		}
	}
}
