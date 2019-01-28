package aaa.modules.regression.sales.home_ss.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.enums.Constants;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public class TestQuoteAdvancedRater extends HomeSSHO3BaseTest {

	PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();

	/**
	 * @author Jurij Kuznecov
	 * @name Test HSS Quote Advanced Rater
	 * @scenario
	 * 1. Create new or open existent Customer
	 * 2. Initiate HO3 quote creation
	 * 3. Fill General, Applicant, Property Info tabs. Order reports. Navigate to P&C screen
	 * 4. Click Calculate Premium button
	 * 5. Check values sent to Rating Services has premium change of (+$0.00)
	 * 6. Select other Deductible value
	 * 7. Check System resets the premium to $0
	 * 8. Click Calculate Premium button
	 * 9. Check Premium changed according to selected value
	 * 10. Select other B Coverage value
	 * 11. Check System resets the premium to $0
	 * 12. Click Calculate Premium button
	 * 13. Check Premium changed according to selected value
	 * 14. Issue quote. Check Total Premium Summary
	 */

	@Parameters({"state"})
	@StateList(statesExcept = {States.CA})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testQuoteAdvancedRater(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(getPolicyTD(), PremiumsAndCoveragesQuoteTab.class);
		premiumsAndCoveragesQuoteTab.calculatePremium();

		Dollar origPremiumValue = new Dollar(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium());

		if (PremiumsAndCoveragesQuoteTab.tableTaxesSurchargesSummary.getRowsCount() > 0) {
			origPremiumValue = origPremiumValue.subtract(new Dollar(PremiumsAndCoveragesQuoteTab.getTaxesSurchargesPremium()));
		}

		verifyPremiumChangeOf(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_B);
		verifyPremiumChangeOf(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_D);
		verifyPremiumChangeOf(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E);
		verifyPremiumChangeOf(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_F);
		verifyPremiumChangeOf(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE);

		Dollar premiuimChangeOf = changeCoverage(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE);
		Dollar newPremiumValue = origPremiumValue.add(premiuimChangeOf);

		Dollar calculatedPremium = PremiumsAndCoveragesQuoteTab.getPolicyTermPremium();

		if (PremiumsAndCoveragesQuoteTab.tableTaxesSurchargesSummary.getRowsCount() > 0) {
			calculatedPremium = calculatedPremium.subtract(new Dollar(PremiumsAndCoveragesQuoteTab.getTaxesSurchargesPremium()));
		}

		if (getState().equals(Constants.States.OK)) {
			PremiumsAndCoveragesQuoteTab.getPolicyTermPremium().verify.isEqual(newPremiumValue, 260.0);
			newPremiumValue = new Dollar(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium());
		} else {
			calculatedPremium.verify.isEqual(newPremiumValue, 1.0);
		}

		premiuimChangeOf = changeCoverage(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_B);
		newPremiumValue = newPremiumValue.add(premiuimChangeOf);

		calculatedPremium = PremiumsAndCoveragesQuoteTab.getPolicyTermPremium();
		if (PremiumsAndCoveragesQuoteTab.tableTaxesSurchargesSummary.getRowsCount() > 0) {
			calculatedPremium = calculatedPremium.subtract(new Dollar(PremiumsAndCoveragesQuoteTab.getTaxesSurchargesPremium()));
		}

		calculatedPremium.verify.isEqual(newPremiumValue, 1.0);

		PremiumsAndCoveragesQuoteTab.btnContinue.click();
		policy.getDefaultView().fillFromTo(getPolicyTD(), MortgageesTab.class, PurchaseTab.class, true);
		new PurchaseTab().submitTab();

		calculatedPremium = PolicySummaryPage.getTotalPremiumSummaryForProperty();

		if (PolicySummaryPage.tableTotalStateTaxesProperty.getRowsCount() > 0) {
			calculatedPremium = calculatedPremium.subtract(new Dollar(PolicySummaryPage.getTotalStateTaxesForProperty()));
		}

		calculatedPremium.verify.isEqual(newPremiumValue, 1.0);
	}

	private void verifyPremiumChangeOf(AssetDescriptor<ComboBox> field) {
		String origValue = premiumsAndCoveragesQuoteTab.getAssetList().getAsset(field).getValue();
		Dollar premiumChangeOf = new Dollar(origValue.substring(origValue.indexOf('(') + 1, origValue.indexOf(')')).replace("$", ""));
		premiumChangeOf.verify.equals(new Dollar(0));
	}

	private Dollar changeCoverage(AssetDescriptor<ComboBox> field) {
		int index = premiumsAndCoveragesQuoteTab.getAssetList().getAsset(field).getSelectedIndex();
		if (index == 0) {
			index++;
		} else {
			index--;
		}
		String fieldValue = premiumsAndCoveragesQuoteTab.getAssetList().getAsset(field).getAllValues().get(index);
		Dollar premiumChangeOf = new Dollar(fieldValue.substring(fieldValue.indexOf('(') + 1, fieldValue.indexOf(')')).replace("$", ""));
		premiumsAndCoveragesQuoteTab.getAssetList().getAsset(field).setValue(fieldValue);

		PremiumsAndCoveragesQuoteTab.getPolicyTermPremium().verify.equals(new Dollar(0));
		premiumsAndCoveragesQuoteTab.calculatePremium();

		fieldValue = premiumsAndCoveragesQuoteTab.getAssetList().getAsset(field).getAllValues().get(index);
		Dollar premiumNewChangeOf = new Dollar(fieldValue.substring(fieldValue.indexOf('(') + 1, fieldValue.indexOf(')')).replace("$", ""));

		return premiumChangeOf.subtract(premiumNewChangeOf);
	}
}
