package aaa.modules.regression.billing_and_payments.home_ss.ho3.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.BillingConstants;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestPremiumAndMinDueAfterRP extends HomeSSHO3BaseTest {

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
     * 6. Verify that first Offer is declined and New offer is created, Min due updated to reflect policy changes
     * @details
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.HIGH })
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_SS_HO3, testCaseId = "PAS-13762")
    public void testPremiumAndMinDueAfterRPForCurrentTerm(@Optional("NV") String state) {

        mainApp().open();
        createCustomerIndividual();
        policyNumber = createPolicy();
        policyExpirationDate = PolicySummaryPage.getExpirationDate();
        //Initiate Renewal Proposal
        createRenewalProposal();
        createEndorsement();
        //Navigate to BA
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());

        Dollar amount = new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.ENDORSEMENT_PROPERTY_EXPOSURES).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue());

        assertSoftly(softly -> {
        assertThat(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.TYPE).getValue()).isEqualTo(BillingConstants.BillsAndStatementsType.OFFER);
        assertThat(BillingSummaryPage.tableBillsStatements.getRow(2).getCell(BillingConstants.BillingBillsAndStatmentsTable.TYPE).getValue()).isEqualTo(BillingConstants.BillsAndStatementsType.DISCARDED_OFFER);
            assertThat(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.DUE_DATE).getValue()).isEqualTo(policyExpirationDate);
            assertThat(BillingSummaryPage.tableBillsStatements.getRow(2).getCell(BillingConstants.BillingBillsAndStatmentsTable.DUE_DATE).getValue()).isEqualTo(policyExpirationDate);

            assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.PREPAID).getValue()).isEqualTo(amount);
        });
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
     * 6. Verify that first Offer is declined and New offer is created, Min due updated to reflect policy changes
     * @details
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.HIGH })
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_SS_HO3, testCaseId = "PAS-13762")
    public void testPremiumAndMinDueAfterRPForRenewalTerm(@Optional("NV") String state) {

        mainApp().open();
        createCustomerIndividual();
        policyNumber = createPolicy();
        policyExpirationDate = PolicySummaryPage.getExpirationDate();
        //Initiate Renewal Proposal
        createRenewalProposal();
        createRevisedRenewalProposal();
        //Navigate to BA
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());

        Dollar amount = new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.ENDORSEMENT_PROPERTY_EXPOSURES).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue());

        assertSoftly(softly -> {
            assertThat(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.TYPE).getValue()).isEqualTo(BillingConstants.BillsAndStatementsType.OFFER);
            assertThat(BillingSummaryPage.tableBillsStatements.getRow(2).getCell(BillingConstants.BillingBillsAndStatmentsTable.TYPE).getValue()).isEqualTo(BillingConstants.BillsAndStatementsType.DISCARDED_OFFER);
            assertThat(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.DUE_DATE).getValue()).isEqualTo(policyExpirationDate);
            assertThat(BillingSummaryPage.tableBillsStatements.getRow(2).getCell(BillingConstants.BillingBillsAndStatmentsTable.DUE_DATE).getValue()).isEqualTo(policyExpirationDate);

            assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.PREPAID).getValue()).isEqualTo(amount);
        });
    }

    protected void createRenewalProposal() {
        LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
        TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
        //Propose Renewal
        mainApp().open();
        SearchPage.openPolicy(policyNumber);

        PolicySummaryPage.buttonRenewals.click();
        policy.dataGather().start();
        premiumsAndCoveragesQuoteTab.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
        new BindTab().submitTab();
    }

    protected void createRevisedRenewalProposal() {
        PolicySummaryPage.buttonRenewals.click();
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewSubTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE).setValueContains("$7,500");
        premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E).setValueContains("$1,00,000");
        premiumsAndCoveragesQuoteTab.calculatePremium();
    }

    protected void createEndorsement() {
        TestData testData = testDataManager.getDefault(TestPremiumAndMinDueAfterRP.class)
                .getTestData("TestData_EndorsementRP").adjust(getPolicyTD("Endorsement", "TestData"));
        policy.endorse().performAndFill(testData);
    }
}
