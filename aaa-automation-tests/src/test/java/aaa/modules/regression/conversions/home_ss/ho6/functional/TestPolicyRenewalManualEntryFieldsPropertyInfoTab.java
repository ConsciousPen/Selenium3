package aaa.modules.regression.conversions.home_ss.ho6.functional;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.customer.actiontabs.InitiateRenewalEntryActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO6BaseTest;
import aaa.modules.regression.conversions.home_ss.helper;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;

import java.time.LocalDateTime;

/**
 * @author S. Sivaram
 * @name Test Policy Renewal
 * @scenario 1. Create Individual Customer / Account
 * 2. Select RME Action with HSS product
 * 3. Choose Data Gathering Action
 * 4. Verify fields: Masonry Veneer Enabled for First Renewal
 * 6. Verify fields: Oil Fuel or Propane Storage Tank dropdown is available for edit
 * 7. initiate renewal
 * 8. Verify fields: Masonry Veneer is not Enabled for Second Renewal
 * 9. Verify fields: Oil Fuel or Propane Storage Tank dropdown is not available for edit
 */

public class TestPolicyRenewalManualEntryFieldsPropertyInfoTab extends HomeSSHO6BaseTest {
    LocalDateTime policyEffectiveDate;
    LocalDateTime policyExpirationDate;
    LocalDateTime renewImageGenDate;
    PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
    helper hc = new helper();

    @Parameters({"state"})
    @StateList(states = {Constants.States.VA, Constants.States.DE, Constants.States.NJ,Constants.States.MD, Constants.States.PA, Constants.States.CT})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH, Groups.TIMEPOINT})
    @TestInfo(component = ComponentConstant.Conversions.HOME_SS_HO6, testCaseId = "PAS-10512, PAS-10978")
    public void propertyInfoTabconvPolicyRenewal(@Optional("CT") String state) {
        TestData td = getConversionPolicyDefaultTD();
        String inceptionDate = TimeSetterUtil.getInstance().getCurrentTime().minusDays(10).format(DateTimeUtils.MM_DD_YYYY);

        createConvPolicyAndMoveToPropertyInfoTab(td, inceptionDate);
        SoftAssertions.assertSoftly(softly -> {
        hc.assertMasonryVaneerFirstRenewal();
        hc.assertOilStorageTankFirstRenewalHo6(state);
        String policyNumber = saveAndExitPolicyOnBindTab(td);
        activeFirstRenewal(policyNumber);
        initiateSecondRenewal(policyNumber);
        navigateToPropertyInfoOnSecondRenewal();
        hc.assertMasonryVaneerSecondRenewal();
        hc.assertOilStorageTankSecondRenewal();});
    }

    /*
    method clicks the renewal button and starts datagather and navigates to the property info tab
    */
    private void navigateToPropertyInfoOnSecondRenewal() {
        /*if(PolicySummaryPage.buttonBackFromRenewals.isEnabled()){
            PolicySummaryPage.buttonBackFromRenewals.click();
        }*/
        PolicySummaryPage.buttonRenewals.click();
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
    }

    /*
    method searches for policynumber on the billing screen gets total due and makes payment for total due
    */
    private void activeFirstRenewal(String policyNumber) {
        policyEffectiveDate = PolicySummaryPage.getEffectiveDate().plusYears(1);
        policyExpirationDate = PolicySummaryPage.getExpirationDate().plusYears(1);
        renewImageGenDate = getTimePoints().getRenewOfferGenerationDate(policyEffectiveDate);
        TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate);

        mainApp().reopen();
        SearchPage.openBilling(policyNumber);
        if(BillingSummaryPage.tableBillingAccounts.isPresent()) {
            BillingSummaryPage.tableBillingAccounts.getRow(1).getCell(BillingConstants.BillingAccountsTable.BILLING_ACCOUNT).controls.links.get(2).click();
        }
        Dollar totalDue = new Dollar(BillingSummaryPage.tableBillingAccountPolicies
                .getRow(BillingConstants.BillingAccountPoliciesTable.POLICY_NUM, policyNumber)
                .getCell(BillingConstants.BillingAccountPoliciesTable.TOTAL_DUE).getValue());
        new BillingAccount().acceptPayment().perform(testDataManager.billingAccount
                .getTestData("AcceptPayment", "TestData_Cash"), totalDue);
    }

    /*
    method fills data from the P&C page and saves and exits on the bind page
    */
    private String saveAndExitPolicyOnBindTab(TestData td) {
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        policy.getDefaultView().fillFromTo(td, PremiumsAndCoveragesQuoteTab.class, BindTab.class, true).getTab(BindTab.class).btnPurchase.click();
        Page.dialogConfirmation.confirm();
        if (PolicySummaryPage.buttonRenewals.isPresent()) {
            PolicySummaryPage.buttonRenewals.click();
        }
        if(PolicySummaryPage.buttonBackFromRenewals.isEnabled()){
            PolicySummaryPage.buttonBackFromRenewals.click();
        }
        return PolicySummaryPage.labellinkPolicy.getValue();
    }

   /*
   method creates customer initiates renewal entry and fills data up to the property info tab
   */
    private void createConvPolicyAndMoveToPropertyInfoTab(TestData td, String inceptionDate) {
        mainApp().open();
        createCustomerIndividual();
        customer.initiateRenewalEntry().perform(getManualConversionInitiationTd()
                .adjust(TestData.makeKeyPath(InitiateRenewalEntryActionTab.class.getSimpleName(),
                        CustomerMetaData.InitiateRenewalEntryActionTab.INCEPTION_DATE.getLabel()), inceptionDate));
        policy.getDefaultView().fillUpTo(td, PropertyInfoTab.class, true);
    }

    /*
   method initiates second renewal and opens policy on new browser window
   */
    private void initiateSecondRenewal(String policyNumber) {
        TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
        mainApp().reopen();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
    }
}
