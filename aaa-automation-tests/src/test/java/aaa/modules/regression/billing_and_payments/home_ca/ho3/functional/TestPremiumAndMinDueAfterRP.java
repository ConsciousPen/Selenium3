package aaa.modules.regression.billing_and_payments.home_ca.ho3.functional;

import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.PolicyHelper;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;

import java.time.LocalDateTime;

import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;

public class TestPremiumAndMinDueAfterRP extends HomeCaHO3BaseTest {

	private static String policyNumber;
	private static LocalDateTime policyExpirationDate;
	private static LocalDateTime renewImageGenDate;
	private static PremiumsAndCoveragesQuoteTab
			premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();

	/**
	 * @author Reda Kazlauskiene
	 * @name Test CA Renewal Premium and Minimum Due to be accurately updated after an Return Premium endorsement
	 * @scenario
	 * 1. Create new Customer;
	 * 2. Create CA HOME Policy: Monthly or Annual payment plan
	 * 3. Create Renewal proposal at R-35
	 * 4. Create RP Endorsement for CURRENT TERM by reducing coverages
	 * 5. Navigate to Billing Account and review changes
	 * 6. Verify that the credit balance is not transferred automatically
	 * 7. Verify that first Offer is declined and New offer is created
	 * @details
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.HOME_CA_HO3, testCaseId = "PAS-13762")
	public void testPremiumAndMinDueAfterRPForCurrentTerm(@Optional("CA") String state) {
		mainApp().open();
		createCustomerIndividual();
		policyNumber = createPolicy();
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		//Initiate Renewal Proposal
		renewalImageGeneration();
		renewalPreviewGeneration();
		renewalOfferGeneration();
		createEndorsement();
		//Navigate to BA
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		//Verify that Credit balance is not transferred automatically to the renewal
		String amount = BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON,
				BillingConstants.PaymentsAndOtherTransactionSubtypeReason.ENDORSEMENT_PROPERTY_EXPOSURES).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue();
		assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(2).getCell(BillingConstants.BillingAccountPoliciesTable.PREPAID)).hasValue(amount);
		verifyRenewalOffer();
	}

	/**
	 * @author Reda Kazlauskiene
	 * @name Test CA Renewal Premium and Minimum Due to be accurately updated after an Return Premium endorsement
	 * @scenario
	 * 1. Create new Customer;
	 * 2. Create CA HOME Policy: Monthly or Annual payment plan
	 * 3. Create Renewal proposal at R-35
	 * 4. Create RP Endorsement for RENEWAL TERM by reducing coverages
	 * 5. Navigate to Billing Account and review changes
	 * 6. Verify that first Offer is declined and New offer is created
	 * @details
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.HOME_CA_HO3, testCaseId = "PAS-13762")
	public void testPremiumAndMinDueAfterRPForRenewalTerm(@Optional("CA") String state) {
		mainApp().open();
		createCustomerIndividual();
		policyNumber = createPolicy();
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		//Initiate Renewal Proposal
		renewalImageGeneration();
		renewalPreviewGeneration();
		renewalOfferGeneration();
		createRevisedRenewalProposal();
		//Navigate to BA
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		verifyRenewalOffer();
	}

	private void renewalImageGeneration() {
		LocalDateTime renewDateImage = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDateImage);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicyHelper.verifyAutomatedRenewalGenerated(renewDateImage);
	}

	private void renewalPreviewGeneration() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.buttonRenewals).isEnabled();

		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
	}

	private void renewalOfferGeneration() {
		LocalDateTime renewDateOffer = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDateOffer);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
		Tab.buttonBack.click();
	}

	private void createRevisedRenewalProposal() {
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE).setValueContains("$7,500");
		premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E).setValueContains("$100,000");
		premiumsAndCoveragesQuoteTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
		new BindTab().submitTab();
	}

	private void createEndorsement() {
		TestData testData = getTestSpecificTD("TestData_EndorsementRP")
				.adjust(getPolicyTD("Endorsement", "TestData"));
		policy.endorse().performAndFill(testData);
	}

	private void verifyRenewalOffer() {
		assertSoftly(softly -> {
			assertThat(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.TYPE)).hasValue(BillingConstants.BillsAndStatementsType.OFFER);
			assertThat(BillingSummaryPage.tableBillsStatements.getRow(2).getCell(BillingConstants.BillingBillsAndStatmentsTable.TYPE)).hasValue(BillingConstants.BillsAndStatementsType.DISCARDED_OFFER);
			assertThat(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.DUE_DATE).getValue()).isEqualTo(policyExpirationDate.format(DateTimeUtils.MM_DD_YYYY));
			assertThat(BillingSummaryPage.tableBillsStatements.getRow(2).getCell(BillingConstants.BillingBillsAndStatmentsTable.DUE_DATE).getValue()).isEqualTo(policyExpirationDate.format(DateTimeUtils.MM_DD_YYYY));
			assertThat(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE).getValue()).isNotEqualTo(BillingSummaryPage.tableBillsStatements.getRow(2).getCell(BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE).getValue());
		});
	}
}
