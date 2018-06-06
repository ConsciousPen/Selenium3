package aaa.modules.regression.sales.home_ss.dp3.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.modules.regression.sales.template.functional.TestInvoiceWithNoDownPaymentAbstract;
import aaa.toolkit.webdriver.customcontrols.JavaScriptButton;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

@StateList(statesExcept = Constants.States.CA)
public class TestInvoiceWithNoDownPayment extends TestInvoiceWithNoDownPaymentAbstract {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_SS_DP3;
    }

	@Override
	protected PurchaseTab getPurchaseTab() {
		return new PurchaseTab();
	}

	@Override
	protected BindTab getBindTab() {
		return new BindTab();
	}

	@Override
	protected PremiumsAndCoveragesQuoteTab getPremiumAndCoveragesTab() {
		return new PremiumsAndCoveragesQuoteTab();
	}

	@Override
	protected void navigateToPremiumAndCoveragesTab() {
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
	}

	@Override
	protected void navigateToBindTab() {
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
	}

	@Override
	protected AssetDescriptor<ComboBox> getDeductible() {
		return HomeSSMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE;
	}

	@Override
	protected AssetDescriptor<JavaScriptButton> getCalculatePremiumButton() {
		return HomeSSMetaData.PremiumsAndCoveragesQuoteTab.CALCULATE_PREMIUM;
	}

	/**
	 * @author Josh Carpenter
	 * @name Test balance is invoiced as off cycle bill for SS DP3 policy when there is no future installment date on NB
	 * @scenario
	 * 1. Create new customer
	 * 2. Initiate SS DP3 policy and fill up to Purchase tab
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
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = {"PAS-9001"})
    public void pas9001_testInvoiceWithNoDownPaymentNB_DP3(@Optional("") String state) {

        pas9001_testInvoiceWithNoDownPaymentNB();

    }

	/**
	 * @author Josh Carpenter
	 * @name Test balance is invoiced as off cycle bill for SS DP3 policy when there is no future installment date for endorsements
	 * @scenario
	 * 1. Create new customer
	 * 2. Create/bind SS DP3 policy (pay in full)
	 * 3. Create/bind premium-bearing endorsement (decrease deductible) at eff. date plus 5 days
	 * 4. Validate the min due is zero
	 * 5. Run offCycleBillingInvoiceAsyncJob
	 * 6. Refresh policy and navigate back to Billing Summary page
	 * 7. Validate the off cycle bill has been generated with correct amounts
	 * @details
	 */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Service.HOME_SS_DP3, testCaseId = {"PAS-9001"})
    public void pas9001_testInvoiceWithNoDownPaymentEndorsement_DP3(@Optional("") String state) {

        pas9001_testInvoiceWithNoDownPaymentEndorsement();

    }

}
