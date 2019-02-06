package aaa.modules.regression.billing_and_payments.template.functional;

import static org.assertj.core.api.Assertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.product.PolicyHelper;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssertions;

public class TestPremiumAndMinDueAfterRPTemplate extends PolicyBaseTest {
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();

	public void checkMinDueForCurrentTerm(){
		LocalDateTime policyExpirationDate = createPolicyWithRenewalProposal();
		createEndorsement();
		//Navigate to BA
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		//Verify that Credit balance is not transferred automatically to the renewal
		String amount = BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON,
				BillingConstants.PaymentsAndOtherTransactionSubtypeReason.ENDORSEMENT_MAINTAIN_VECHICLES).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue().replaceAll("[( )]", "");
		assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.TOTAL_PAID).getValue()).isEqualTo(amount);
		verifyRenewalOffer(policyExpirationDate);
	}

	public void checkMinDueForRenewalTerm() {
		LocalDateTime policyExpirationDate = createPolicyWithRenewalProposal();
		createRevisedRenewalProposal();
		//Navigate to BA
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		verifyRenewalOffer(policyExpirationDate);
	}

	public LocalDateTime createPolicyWithRenewalProposal() {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		initiateRenewalProposal(policyNumber, policyExpirationDate);
		return policyExpirationDate;
	}

	public void initiateRenewalProposal(String policyNumber,LocalDateTime policyExpirationDate) {
		renewalImageGeneration(policyNumber, policyExpirationDate);
		renewalPreviewGeneration(policyNumber, policyExpirationDate);
		renewalOfferGeneration(policyNumber, policyExpirationDate);
	}

	public void renewalImageGeneration(String policyNumber, LocalDateTime policyExpirationDate) {
		LocalDateTime renewDateImage = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDateImage);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart1);

		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicyHelper.verifyAutomatedRenewalGenerated(renewDateImage);
	}

	public void renewalPreviewGeneration(String policyNumber, LocalDateTime policyExpirationDate) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate));
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		CustomAssertions.assertThat(PolicySummaryPage.buttonRenewals).isEnabled();

		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
	}

	public void renewalOfferGeneration(String policyNumber, LocalDateTime policyExpirationDate) {
		LocalDateTime renewDateOffer = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDateOffer);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		CustomAssertions.assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
		Tab.buttonBack.click();
	}

	public void createRevisedRenewalProposal() {
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.PROPERTY_DAMAGE_LIABILITY).setValueContains("$25,000");
		premiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
	}

	public void createEndorsement() {
		TestData testData = getTestSpecificTD("TestData_EndorsementRP")
				.adjust(getPolicyTD("Endorsement", "TestData"));
		policy.endorse().performAndFill(testData);
	}

	public void verifyRenewalOffer(LocalDateTime policyExpirationDate) {
		assertSoftly(softly -> {
			assertThat(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.TYPE).getValue()).isEqualTo(BillingConstants.BillsAndStatementsType.OFFER);
			assertThat(BillingSummaryPage.tableBillsStatements.getRow(2).getCell(BillingConstants.BillingBillsAndStatmentsTable.TYPE).getValue()).isEqualTo(BillingConstants.BillsAndStatementsType.DISCARDED_OFFER);
			assertThat(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.DUE_DATE).getValue()).isEqualTo(policyExpirationDate.format(DateTimeUtils.MM_DD_YYYY));
			assertThat(BillingSummaryPage.tableBillsStatements.getRow(2).getCell(BillingConstants.BillingBillsAndStatmentsTable.DUE_DATE).getValue()).isEqualTo(policyExpirationDate.format(DateTimeUtils.MM_DD_YYYY));
			assertThat(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE).getValue()).isNotEqualTo(BillingSummaryPage.tableBillsStatements.getRow(2).getCell(BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE).getValue());
		});
	}
}
