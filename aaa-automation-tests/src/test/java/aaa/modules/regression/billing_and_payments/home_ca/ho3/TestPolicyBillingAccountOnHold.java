package aaa.modules.regression.billing_and_payments.home_ca.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;
import aaa.common.enums.NavigationEnum.AppMainTabs;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.BillingConstants.BillingHoldsAndMoratoriumsTable;
import aaa.main.enums.BillingConstants.BillingAddOnHoldPoliciesTable;
import aaa.main.enums.BillingConstants.HoldsAndMoratoriumsActions;
import aaa.main.enums.BillingConstants.BillingStatus;
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

	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_CA_HO3) 
    public void testPolicyBillingAccountOnHold(@Optional("CA") String state) {

        AddHoldActionTab addHoldActoinTab = new AddHoldActionTab();

        mainApp().open();
        getCopiedPolicy();

        NavigationPage.toMainTab(AppMainTabs.BILLING.get());
        // 4-6
        new BillingAccount().addHold().start();
        addHoldActoinTab.fillTab(getTestSpecificTD("AddHold"));
        AddHoldActionTab.buttonAddUpdate.click();
        addHoldActoinTab.getAssetList().getWarning(BillingAccountMetaData.AddHoldActionTab.HOLD_EFFECTIVE_DATE.getLabel()).verify.value("Cannot be earlier than today");
        addHoldActoinTab.getAssetList().getWarning(BillingAccountMetaData.AddHoldActionTab.HOLD_EXPIRATION_DATE.getLabel()).verify.value("Date must be after effective date");

        // 7. Change 'Effective Date' = current date, 'Expiration Date' = current date + 2 days, 'Reason' to 'Other' and verify error
        addHoldActoinTab.fillTab(new SimpleDataProvider().adjust(getTestSpecificTD("AddHold_Adjustment")));
        AddHoldActionTab.buttonAddUpdate.click();
        addHoldActoinTab.getAssetList().getWarning(BillingAccountMetaData.AddHoldActionTab.ADDITIONAL_INFO.getLabel()).verify.value("Value is required");

        // 8.  Fill 'Additional information' field
        addHoldActoinTab.fillTab(new SimpleDataProvider().adjust(BillingAccountMetaData.AddHoldActionTab.class.getSimpleName(),
                new SimpleDataProvider().adjust(BillingAccountMetaData.AddHoldActionTab.ADDITIONAL_INFO.getLabel(), "Additional Text")));
        AddHoldActionTab.buttonAddUpdate.click();

        // 9.  Verify billing account status is "On hold"
        AddHoldActionTab.tablePolicies.getRow(1).getCell(BillingAddOnHoldPoliciesTable.BILLING_STATUS).verify.value(BillingStatus.ON_HOLD);

        // 10. Remove Hold
        AddHoldActionTab.tableHoldsAndMoratoriums.getRow(1).getCell(BillingHoldsAndMoratoriumsTable.ACTIONS).controls.links.get(HoldsAndMoratoriumsActions.REMOVE).click();
        Page.dialogConfirmation.confirm();

        // 11. Verify billing account status is "Active"
        AddHoldActionTab.tablePolicies.getRow(1).getCell(BillingAddOnHoldPoliciesTable.BILLING_STATUS).verify.contains(BillingStatus.ACTIVE);
    }
}
