package aaa.modules.policy.home_ca_ho3;

import org.testng.annotations.Test;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;
import aaa.common.enums.NavigationEnum.AppMainTabs;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.main.enums.BillingConstants.BillingHoldsAndMoratoriumsTable;
import aaa.main.enums.BillingConstants.BillingPoliciesResultsTable;
import aaa.main.enums.BillingConstants.HoldsAndMoratoriumsActions;
import aaa.main.enums.BillingConstants.PoliciesResultsBillingStatus;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AddHoldActionTab;
import aaa.modules.policy.HomeCaHO3BaseTest;

public class TestPolicyBillingAccountOnHold extends HomeCaHO3BaseTest {

    /**
     * @author Jurij Kuznecov
     * @name Test CAH Policy Add Billing Account On Hold
     * @scenario 
     * 1.  Create new or open existent Customer
     * 2.  Create a new HO3 policy
     * 3.  On Billing Tab select action 'Hold'
     * 4.  Enter 'Effective Date' = current date - 2 days
     * 5.  Enter 'Expiration Date' = current date - 3 days
     * 6.  Fill other mandatory fields, submit tab and verify errors
     * 7.  Change 'Effective Date' = current date, 'Expiration Date' = current date + 2 days, 'Reason' to 'Other' and verify error 
     * 8.  Fill 'Additional information' field
     * 9.  Verify billing account status is "On hold"
     * 10. Remove Hold
     * 11. Verify billing account status is "Active"
     */

    @Test
    @TestInfo(component = "Policy.HomeCA")
    public void testPolicyBillingAccountOnHold() {

        AddHoldActionTab addHoldActoinTab = new AddHoldActionTab();

        mainApp().open();
        createCustomerIndividual();
        createPolicy();

        NavigationPage.toMainTab(AppMainTabs.BILLING.get());
        // 4-6
        new BillingAccount().addHold().perform(getTestSpecificTD("AddHold"));
        addHoldActoinTab.getAssetList().getWarning(BillingAccountMetaData.AddHoldActionTab.HOLD_EFFECTIVE_DATE.getLabel()).verify.contains("Cannot be earlier than today");
        addHoldActoinTab.getAssetList().getWarning(BillingAccountMetaData.AddHoldActionTab.HOLD_EXPIRATION_DATE.getLabel()).verify.contains("Date must be after effective date");

        // 7. Change 'Effective Date' = current date, 'Expiration Date' = current date + 2 days, 'Reason' to 'Other' and verify error
        addHoldActoinTab.fillTab(new SimpleDataProvider().adjust(getTestSpecificTD("AddHold_Adjustment")));
        addHoldActoinTab.submitTab();
        addHoldActoinTab.getAssetList().getWarning(BillingAccountMetaData.AddHoldActionTab.ADDITIONAL_INFO.getLabel()).verify.contains("Value is required");

        // 8.  Fill 'Additional information' field
        addHoldActoinTab.fillTab(new SimpleDataProvider().adjust(BillingAccountMetaData.AddHoldActionTab.class.getSimpleName(),
                new SimpleDataProvider().adjust(BillingAccountMetaData.AddHoldActionTab.ADDITIONAL_INFO.getLabel(), "Additional Text")));
        addHoldActoinTab.submitTab();

        // 9.  Verify billing account status is "On hold"
        AddHoldActionTab.tablePolicyResults.getRow(1).getCell(BillingPoliciesResultsTable.BILLING_STATUS).verify.contains(PoliciesResultsBillingStatus.ON_HOLD);

        // 10. Remove Hold
        AddHoldActionTab.tableHoldsAndMoratoriums.getRow(1).getCell(BillingHoldsAndMoratoriumsTable.ACTIONS).controls.links.get(HoldsAndMoratoriumsActions.REMOVE).click();
        Page.dialogConfirmation.confirm();

        // 11. Verify billing account status is "Active"
        AddHoldActionTab.tablePolicyResults.getRow(1).getCell(BillingPoliciesResultsTable.BILLING_STATUS).verify.contains(PoliciesResultsBillingStatus.ACTIVE);
    }
}
