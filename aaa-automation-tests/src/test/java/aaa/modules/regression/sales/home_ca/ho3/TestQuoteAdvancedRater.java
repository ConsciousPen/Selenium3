package aaa.modules.regression.sales.home_ca.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public class TestQuoteAdvancedRater extends HomeCaHO3BaseTest {

    PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();

    /**
     * @author Jurij Kuznecov
     * @name Test CAH Quote Advanced Rater
     * @scenario
     * 1.  Create new or open existent Customer
     * 2.  Initiate HO3 quote creation
     * 3.  Fill General, Applicant, Property Info tabs. Order reports. Navigate to P&C screen
     * 4.  Click Calculate Premium button
     * 5.  Check values sent to Rating Services has premium change of (+$0.00)
     * 6.  Select other Deductible value
     * 7.  Check System resets the premium to $0
     * 8.  Click Calculate Premium button
     * 9.  Check Premium changed according to selected value
     * 10. Select other E Coverage value
     * 11. Check System resets the premium to $0
     * 12. Click Calculate Premium button
     * 13. Check Premium changed according to selected value
     * 14. Issue quote. Check Total Premium Summary
     */

    @Parameters({"state"})
    @StateList(states =  States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
    public void testQuoteAdvancedRater(@Optional("CA") String state) {
        mainApp().open();
        createCustomerIndividual();

        policy.initiate();
        policy.getDefaultView().fillUpTo(getPolicyTD(), PremiumsAndCoveragesQuoteTab.class);
        premiumsAndCoveragesQuoteTab.calculatePremium();
        Dollar origPremiumValue = new Dollar(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium());

        verifyPremiumChangeOf(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE);
        verifyPremiumChangeOf(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E);

        Dollar premiuimChangeOf = changeCoverage(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE);
        Dollar newPremiumValue = origPremiumValue.add(premiuimChangeOf);
        PremiumsAndCoveragesQuoteTab.getPolicyTermPremium().verify.equals(newPremiumValue);

        premiuimChangeOf = changeCoverage(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E);
        newPremiumValue = newPremiumValue.add(premiuimChangeOf);
        PremiumsAndCoveragesQuoteTab.getPolicyTermPremium().verify.equals(newPremiumValue);

        PremiumsAndCoveragesQuoteTab.btnContinue.click();
        policy.getDefaultView().fillFromTo(getPolicyTD(), MortgageesTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();

        PolicySummaryPage.getTotalPremiumSummaryForProperty().verify.equals(newPremiumValue);
    }

    private void verifyPremiumChangeOf(AssetDescriptor<ComboBox> field) {
        String origValue = premiumsAndCoveragesQuoteTab.getAssetList().getAsset(field).getValue();
		Dollar premiumChangeOf = new Dollar(origValue.substring(origValue.indexOf('(') + 1, origValue.indexOf(')')).replace("$", ""));
        premiumChangeOf.verify.equals(new Dollar(0));
    }

    private Dollar changeCoverage(AssetDescriptor<ComboBox> field) {
        int index = premiumsAndCoveragesQuoteTab.getAssetList().getAsset(field).getSelectedIndex();
        if (index == 0)
            index++;
        else
            index--;
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
