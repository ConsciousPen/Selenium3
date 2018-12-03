package aaa.modules.regression.conversions.home_ss.ho6.functional;

import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO6BaseTest;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import java.time.LocalDateTime;

import static toolkit.verification.CustomAssertions.assertThat;

/**
 * @author S. Jaraminas
 * @name Test Policy Renewal
 * @scenario
 * 1. Create Individual Customer / Account
 * 2. Select RME Action with HSS product
 * 3. Create first renewal with Active Membership number
 * 4. Check if AAA Membership discount is applied
 * 5. Accept the payment for the policy
 * 6. Start creating second renewal with Non-Active Membership number
 * 7. Check if AAA Membership discount is not applied
 */

public class TestPolicyRenewalMembershipDiscount extends HomeSSHO6BaseTest {

    PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
    ReportsTab reportsTab = new ReportsTab();
    BindTab bindTab = new BindTab();

    LocalDateTime policyEffectiveDate;
    LocalDateTime policyExpirationDate;
    LocalDateTime renewImageGenDate;
    String policyNumber;

    TestData td;

    @Parameters({"state"})
    @StateList(states = {Constants.States.VA, Constants.States.DE, Constants.States.NJ, Constants.States.AZ})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH, Groups.TIMEPOINT})
    @TestInfo(component = ComponentConstant.Conversions.HOME_SS_HO6, testCaseId = "PAS-10453")
    public void pas10453_PolicyRenewal(@Optional("NJ") String state) {

        td = getConversionPolicyDefaultTD();

        mainApp().open();
        createCustomerIndividual();
        customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());

        firstRenewal(); //Creates first renewal and checks if AAA Membership discount is applied
        policyDateSavingAndChanging(); //Saves policy time and change application time according to the current policy
        billingPaymentAcception(); //Accepts the policy payment bill
        secondRenewal(); //Starts creating second renewal and checks if AAA Membership discount is not applied

        assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts
                .getRow(2).getCell(2).getValue().contains("AAA Membership")).isEqualTo(false);
    }

    private void firstRenewal() {
        policy.getDefaultView().fillUpTo(td.adjust(TestData.makeKeyPath(
                PremiumsAndCoveragesQuoteTab.class.getSimpleName(),
                HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()), "Pay in Full (Renewal)"),
                PremiumsAndCoveragesQuoteTab.class, true);
        policyNumber = PremiumsAndCoveragesQuoteTab.labelForConversionPolicy.getValue();
        assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts
                .getRow(2).getCell(2).getValue().contains("AAA Membership")).isEqualTo(true);
        PremiumsAndCoveragesQuoteTab.buttonNext.click();
        policy.getDefaultView().fillFromTo(td, MortgageesTab.class, BindTab.class, true);
        bindTab.submitTab();
        if(PolicySummaryPage.buttonBackFromRenewals.isEnabled()){
            PolicySummaryPage.buttonBackFromRenewals.click();
        }
    }

    private void policyDateSavingAndChanging() {
        policyEffectiveDate = PolicySummaryPage.getEffectiveDate().plusYears(1);
        policyExpirationDate = PolicySummaryPage.getExpirationDate().plusYears(1);
        renewImageGenDate = getTimePoints().getRenewOfferGenerationDate(policyEffectiveDate);
        TimeSetterUtil.getInstance().nextPhase(renewImageGenDate); //-35 days
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate);
    }

    private void billingPaymentAcception() {
        mainApp().reopen();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        BillingSummaryPage.open();
        if(BillingSummaryPage.tableBillingAccounts.isPresent()) {
            BillingSummaryPage.tableBillingAccounts.getRow(1).getCell(BillingConstants.BillingAccountsTable.BILLING_ACCOUNT).controls.links.get(2).click();
        }
        Dollar totDue = new Dollar(BillingSummaryPage.tableBillingAccountPolicies
                .getRow(BillingConstants.BillingAccountPoliciesTable.POLICY_NUM, policyNumber)
                .getCell(BillingConstants.BillingAccountPoliciesTable.TOTAL_DUE).getValue());
        new BillingAccount().acceptPayment().perform(testDataManager.billingAccount
                .getTestData("AcceptPayment", "TestData_Cash"), totDue);
        TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
    }

    private void secondRenewal() {
        mainApp().reopen();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        PolicySummaryPage.buttonRenewals.click();
        policy.dataGather().start();
        policy.getDefaultView().fillUpTo(td
                        .adjust(TestData.makeKeyPath(ApplicantTab.class.getSimpleName(),
                                HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel(),
                                HomeSSMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel()),
                                "3111111111111121"),
                ReportsTab.class, false);
        reportsTab.reorderReports();
        premiumsAndCoveragesQuoteTab.calculatePremium();
    }
}
