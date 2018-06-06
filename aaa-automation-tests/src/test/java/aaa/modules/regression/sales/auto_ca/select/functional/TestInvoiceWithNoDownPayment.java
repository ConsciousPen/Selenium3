package aaa.modules.regression.sales.auto_ca.select.functional;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PurchaseTab;
import aaa.modules.regression.sales.template.functional.TestInvoiceWithNoDownPaymentAbstract;
import aaa.toolkit.webdriver.customcontrols.JavaScriptButton;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

@StateList(states = Constants.States.CA)
public class TestInvoiceWithNoDownPayment extends TestInvoiceWithNoDownPaymentAbstract {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_SELECT;
    }

    @Override
    protected PurchaseTab getPurchaseTab() {
        return new PurchaseTab();
    }

    @Override
    protected DocumentsAndBindTab getBindTab() {
        return new DocumentsAndBindTab();
    }

    @Override
    protected PremiumAndCoveragesTab getPremiumAndCoveragesTab() {
        return new PremiumAndCoveragesTab();
    }

    @Override
    protected void navigateToPremiumAndCoveragesTab() {
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
    }

    @Override
    protected void navigateToBindTab() {
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
    }

    @Override
    protected AssetDescriptor<ComboBox> getDeductible() {
        return AutoCaMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.COLLISION_DEDUCTIBLE;
    }

    @Override
    protected AssetDescriptor<JavaScriptButton> getCalculatePremiumButton() {
        return AutoCaMetaData.PremiumAndCoveragesTab.CALCULATE_PREMIUM;
    }

    /**
     * @author Josh Carpenter, Dakota Berg
     * @name Test balance is invoiced as off cycle bill for CA Select policy when there is no future installment date on NB
     * @scenario
     * 1. Create new customer
     * 2. Initiate CA Select policy and fill up to Purchase tab
     * 3. Check option to change the minimum down payment, set to $10, and selection reason in drop down (first option)
     * 4. Bind/purchase policy
     * 5. Validate the min due is zero
     * 6. Run offCycleBillingInvoiceAsyncJob
     * 7. Refresh policy and navigate back to Billing Summary page
     * 8. Validate the off cycle bill has been generated with correct amounts
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = {"PAS-9001"})
    public void pas9001_testInvoiceWithNoDownPaymentNB_Select(@Optional("") String state) {

        pas9001_testInvoiceWithNoDownPaymentNB();

    }

    /**
     * @author Josh Carpenter, Dakota Berg
     * @name Test balance is invoiced as off cycle bill for CA Select policy when there is no future installment date for endorsements
     * @scenario
     * 1. Create new customer
     * 2. Create/bind CA Select policy (pay in full)
     * 3. Create/bind premium-bearing endorsement (decrease deductible) at eff. date plus 5 days
     * 4. Validate the min due is zero
     * 5. Run offCycleBillingInvoiceAsyncJob
     * 6. Refresh policy and navigate back to Billing Summary page
     * 7. Validate the off cycle bill has been generated with correct amounts
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-9001"})
    public void pas9001_testInvoiceWithNoDownPaymentEndorsement_Select(@Optional("") String state) {

        pas9001_testInvoiceWithNoDownPaymentEndorsement();

    }

}





