package aaa.modules.regression.sales.pup.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.BindTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.modules.policy.pup.defaulttabs.PurchaseTab;
import aaa.modules.regression.sales.template.functional.TestOffCycleBillNoInstallmentDateAbstract;
import toolkit.utils.TestInfo;

public class TestOffCycleBillNoInstallmentDate extends TestOffCycleBillNoInstallmentDateAbstract {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
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
	protected void navigateToPremiumAndCoveragesTab() {
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());
	}

	@Override
	protected void navigateToBindTab() {
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.BIND.get());
	}

	@Override
	protected void adjustPremiumBearingValue() {
		new PremiumAndCoveragesQuoteTab().getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PERSONAL_UMBRELLA).setValueByIndex(3);
	}

	@Override
	protected void calculatePremium() {
		new PremiumAndCoveragesQuoteTab().getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.CALCULATE_PREMIUM).click();
	}

	/**
	 * @author Josh Carpenter, Dakota Berg
	 * @name Test balance is invoiced as off cycle bill for PUP policy when there is no future installment date on NB
	 * @scenario
	 * 1. Create new customer
	 * 2. Initiate PUP policy and fill up to Purchase tab
	 * 3. Check option to change the minimum down payment, set to $0, and selection reason in drop down (first option)
	 * 4. Bind/purchase policy
	 * 5. Validate the min due is zero
	 * 6. Run offCycleBillingInvoiceAsyncJob
	 * 7. Refresh policy and navigate back to Billing Summary page
	 * 8. Validate the off cycle bill has been generated with correct amounts
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = {"PAS-9001"})
	public void pas9001_testInvoiceWithNoDownPaymentNB_PUP(@Optional("") String state) {

		pas9001_testOffCycleBillNoDownPayment_NB();

	}

	/**
	 * @author Josh Carpenter, Dakota Berg
	 * @name Test balance is invoiced as off cycle bill for PUP policy when there is no future installment date for endorsements
	 * @scenario
	 * 1. Create new customer
	 * 2. Create/bind PUP policy (pay in full)
	 * 3. Create/bind premium-bearing endorsement (decrease deductible) at eff. date plus 5 days
	 * 4. Validate the min due is zero
	 * 5. Run offCycleBillingInvoiceAsyncJob
	 * 6. Refresh policy and navigate back to Billing Summary page
	 * 7. Validate the off cycle bill has been generated with correct amounts
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Service.PUP, testCaseId = {"PAS-9001"})
	public void pas9001_testInvoiceWithNoDownPaymentEndorsement_PUP(@Optional("") String state) {

		pas9001_testOffCycleBillPremiumBearingEndorsement();

	}

}
