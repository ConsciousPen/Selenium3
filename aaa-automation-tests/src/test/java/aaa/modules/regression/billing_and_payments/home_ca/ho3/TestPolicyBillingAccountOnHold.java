package aaa.modules.regression.billing_and_payments.home_ca.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum.AppMainTabs;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.BillingConstants.BillingAddOnHoldPoliciesTable;
import aaa.main.enums.BillingConstants.BillingHoldsAndMoratoriumsTable;
import aaa.main.enums.BillingConstants.BillingStatus;
import aaa.main.enums.BillingConstants.HoldsAndMoratoriumsActions;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AddHoldActionTab;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.utils.StateList;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;

public class TestPolicyBillingAccountOnHold extends HomeCaHO3BaseTest {

    /**
     * @author Jurij Kuznecov
	 * <b> Test CAH Policy Add Billing Account On Hold </b>
	 * <p> Steps:
	 * <p> 1.  Create new or open existent Customer
	 * <p> 2.  Create a new HO3 policy
	 * <p> 3.  On Billing Tab select action 'Hold'
	 * <p> 4.  Enter 'Effective Date' = current date - 2 days
	 * <p> 5.  Enter 'Expiration Date' = current date - 3 days
	 * <p> 6.  Fill other mandatory fields, submit tab and verify errors
	 * <p> 7.  Change 'Effective Date' = current date, 'Expiration Date' = current date + 2 days, 'Reason' to 'Other' and verify error
	 * <p> 8.  Fill 'Additional information' field
	 * <p> 9.  Verify billing account status is "On hold"
	 * <p> 10. Remove Hold
	 * <p> 11. Verify billing account status is "Active"
     */

	@Parameters({"state"})
	@StateList(states =  States.CA)
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
        assertThat(addHoldActoinTab.getAssetList().getWarning(BillingAccountMetaData.AddHoldActionTab.HOLD_EFFECTIVE_DATE)).hasValue("Cannot be earlier than today");
        assertThat(addHoldActoinTab.getAssetList().getWarning(BillingAccountMetaData.AddHoldActionTab.HOLD_EXPIRATION_DATE)).hasValue("Date must be after effective date");

        // 7. Change 'Effective Date' = current date, 'Expiration Date' = current date + 2 days, 'Reason' to 'Other' and verify error
        addHoldActoinTab.fillTab(new SimpleDataProvider().adjust(getTestSpecificTD("AddHold_Adjustment")));
        AddHoldActionTab.buttonAddUpdate.click();
        assertThat(addHoldActoinTab.getAssetList().getWarning(BillingAccountMetaData.AddHoldActionTab.ADDITIONAL_INFO)).hasValue("Value is required");

        // 8.  Fill 'Additional information' field
        addHoldActoinTab.fillTab(new SimpleDataProvider().adjust(BillingAccountMetaData.AddHoldActionTab.class.getSimpleName(),
                new SimpleDataProvider().adjust(BillingAccountMetaData.AddHoldActionTab.ADDITIONAL_INFO.getLabel(), "Additional Text")));
        AddHoldActionTab.buttonAddUpdate.click();

        // 9.  Verify billing account status is "On hold"
        assertThat(AddHoldActionTab.tablePolicies.getRow(1).getCell(BillingAddOnHoldPoliciesTable.BILLING_STATUS)).hasValue(BillingStatus.ON_HOLD);

        // 10. Remove Hold
        AddHoldActionTab.tableHoldsAndMoratoriums.getRow(1).getCell(BillingHoldsAndMoratoriumsTable.ACTIONS).controls.links.get(HoldsAndMoratoriumsActions.REMOVE).click();
        Page.dialogConfirmation.confirm();

        // 11. Verify billing account status is "Active"
        assertThat(AddHoldActionTab.tablePolicies.getRow(1).getCell(BillingAddOnHoldPoliciesTable.BILLING_STATUS)).valueContains(BillingStatus.ACTIVE);
    }
}
