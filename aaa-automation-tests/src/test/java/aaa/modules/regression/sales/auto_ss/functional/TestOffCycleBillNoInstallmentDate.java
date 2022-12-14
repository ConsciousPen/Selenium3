package aaa.modules.regression.sales.auto_ss.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.modules.regression.sales.template.functional.TestOffCycleBillNoInstallmentDateAbstract;
import aaa.toolkit.webdriver.customcontrols.DetailedVehicleCoveragesRepeatAssetList;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.ComboBox;

@StateList(statesExcept = Constants.States.CA)
public class TestOffCycleBillNoInstallmentDate extends TestOffCycleBillNoInstallmentDateAbstract {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
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
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
	}

	@Override
	protected void navigateToBindTab() {
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
	}

	@Override
	protected void adjustPremiumBearingValue() {
		new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.DETAILED_VEHICLE_COVERAGES.getLabel(), DetailedVehicleCoveragesRepeatAssetList.class)
				.getAsset(AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.COLLISION_DEDUCTIBLE.getLabel(), ComboBox.class).setValueByIndex(1);
	}

	@Override
	protected void calculatePremium() {
		new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.CALCULATE_PREMIUM).click();
	}

	/**
	 * @author Josh Carpenter
	 * @name Test balance is invoiced as off cycle bill for Auto SS policy when there is no future installment date for endorsements
	 * @scenario
	 * 1. Create new customer
	 * 2. Create/bind Auto SS policy (pay in full)
	 * 3. Create/bind premium-bearing endorsement (decrease deductible) at eff. date plus 5 days
	 * 4. Validate the min due is zero
	 * 5. Run offCycleBillingInvoiceAsyncJob
	 * 6. Refresh policy and navigate back to Billing Summary page
	 * 7. Validate the off cycle bill has been generated with correct amounts
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9001"})
	public void pas9001_testInvoiceWithNoDownPaymentEndorsement_AutoSS(@Optional("") String state) {

		pas9001_testOffCycleBillPremiumBearingEndorsement();

	}

}
