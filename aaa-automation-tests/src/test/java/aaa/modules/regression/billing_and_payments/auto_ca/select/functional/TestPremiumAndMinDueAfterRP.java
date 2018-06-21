package aaa.modules.regression.billing_and_payments.auto_ca.select.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
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
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssertions;

public class TestPremiumAndMinDueAfterRP extends AutoCaSelectBaseTest {

    private static String policyNumber;
    private static LocalDateTime policyExpirationDate;
    private static LocalDateTime renewImageGenDate;
    private static PremiumAndCoveragesTab
            premiumAndCoveragesTab = new PremiumAndCoveragesTab();

    /**
     * @author Reda Kazlauskiene
     * @name Test CA Renewal Premium and Minimum Due to be accurately updated after an Return Premium endorsement
     * @scenario
     * 1. Create new Customer;
     * 2. Create CA Auto Select Policy: Monthly or Annual payment plan
     * 3. Create Renewal proposal at R-35
     * 4. Create RP Endorsement for CURRENT TERM by reducing coverages
     * 5. Navigate to Billing Account and review changes
     * 6. Verify that first Offer is declined and New offer is created
     * @details
     */
    @Parameters({"state"})
    @StateList(states = {Constants.States.CA})
    @Test(groups = { Groups.FUNCTIONAL, Groups.HIGH })
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_CA_SELECT, testCaseId = "PAS-13762")
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
        verifyRenewalOffer();
    }

    /**
     * @author Reda Kazlauskiene
     * @name Test CA Renewal Premium and Minimum Due to be accurately updated after an Return Premium endorsement
     * @scenario
     * 1. Create new Customer;
     * 2. Create CA Auto Select Policy: Monthly or Annual payment plan
     * 3. Create Renewal proposal at R-35
     * 4. Create RP Endorsement for RENEWAL TERM by reducing coverages
     * 5. Navigate to Billing Account and review changes
     * 6. Verify that first Offer is declined and New offer is created
     * @details
     */
    @Parameters({"state"})
    @StateList(states = {Constants.States.CA})
    @Test(groups = { Groups.FUNCTIONAL, Groups.HIGH })
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_CA_SELECT, testCaseId = "PAS-13762")
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
        CustomAssertions.assertThat(PolicySummaryPage.buttonRenewals).isEnabled();

        PolicySummaryPage.buttonRenewals.click();
        new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
    }

    private void renewalOfferGeneration() {
        LocalDateTime renewDateOffer = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
        TimeSetterUtil.getInstance().nextPhase(renewDateOffer);
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        CustomAssertions.assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
        PolicySummaryPage.buttonRenewals.click();
        new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
        Tab.buttonBack.click();
    }

    private void createRevisedRenewalProposal() {
        PolicySummaryPage.buttonRenewals.click();
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        premiumAndCoveragesTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.PROPERTY_DAMAGE_LIABILITY).setValueContains("$10,000");
        premiumAndCoveragesTab.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
        new DocumentsAndBindTab().submitTab();
    }

    private void createEndorsement() {
        TestData testData = getTestSpecificTD("TestData_EndorsementRP")
                .adjust(getPolicyTD("Endorsement", "TestData"));
        policy.endorse().performAndFill(testData);
    }

    private void verifyRenewalOffer() {
        assertSoftly(softly -> {
            assertThat(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.TYPE).getValue()).isEqualTo(BillingConstants.BillsAndStatementsType.OFFER);
            assertThat(BillingSummaryPage.tableBillsStatements.getRow(2).getCell(BillingConstants.BillingBillsAndStatmentsTable.TYPE).getValue()).isEqualTo(BillingConstants.BillsAndStatementsType.DISCARDED_OFFER);
            assertThat(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.DUE_DATE).getValue()).isEqualTo(policyExpirationDate.format(DateTimeUtils.MM_DD_YYYY));
            assertThat(BillingSummaryPage.tableBillsStatements.getRow(2).getCell(BillingConstants.BillingBillsAndStatmentsTable.DUE_DATE).getValue()).isEqualTo(policyExpirationDate.format(DateTimeUtils.MM_DD_YYYY));
            assertThat(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE).getValue()).isNotEqualTo((BillingSummaryPage.tableBillsStatements.getRow(2).getCell(BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE).getValue()));
        });
    }
}
