package aaa.modules.regression.sales.auto_ca.choice.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
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
import aaa.modules.regression.sales.template.functional.TestOffCycleBillNoInstallmentDateAbstract;
import aaa.toolkit.webdriver.customcontrols.DetailedVehicleCoveragesRepeatAssetList;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.ComboBox;

@StateList(states = Constants.States.CA)
public class TestOffCycleBillNoInstallmentDate extends TestOffCycleBillNoInstallmentDateAbstract {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_CHOICE;
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
    protected void navigateToPremiumAndCoveragesTab() {
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
    }

    @Override
    protected void navigateToBindTab() {
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
    }

    @Override
    protected void adjustPremiumBearingValue() {
        new PremiumAndCoveragesTab().getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.DETAILED_VEHICLE_COVERAGES.getLabel(), DetailedVehicleCoveragesRepeatAssetList.class)
                .getAsset(AutoCaMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.RENTAL_REIMBURSEMENT.getLabel(), ComboBox.class).setValueContains("Yes");
    }

    @Override
    protected void calculatePremium() {
        new PremiumAndCoveragesTab().getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.CALCULATE_PREMIUM).click();
    }

    /**
     * @author Josh Carpenter, Dakota Berg
     * @name Test balance is invoiced as off cycle bill for CA Choice policy when there is no future installment date for endorsements
     * @scenario
     * 1. Create new customer
     * 2. Create/bind CA Choice policy (pay in full)
     * 3. Create/bind premium-bearing endorsement (decrease deductible) at eff. date plus 5 days
     * 4. Validate the min due is zero
     * 5. Run offCycleBillingInvoiceAsyncJob
     * 6. Refresh policy and navigate back to Billing Summary page
     * 7. Validate the off cycle bill has been generated with correct amounts
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_CHOICE, testCaseId = {"PAS-9001"})
    public void pas9001_testInvoiceWithNoDownPaymentEndorsement_Choice(@Optional("CA") String state) {

        pas9001_testOffCycleBillPremiumBearingEndorsement();

    }

}





