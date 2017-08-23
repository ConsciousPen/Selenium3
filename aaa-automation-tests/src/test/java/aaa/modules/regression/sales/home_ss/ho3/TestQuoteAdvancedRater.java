package aaa.modules.regression.sales.home_ss.ho3;

import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;

public class TestQuoteAdvancedRater extends HomeSSHO3BaseTest {

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
     * 10. Select other E Coverage value
     * 11. Check System resets the premium to $0
     * 12. Click Calculate Premium button
     * 13. Check Premium changed according to selected value
     * 14. Issue quote. Check Total Premium Summary
     */

    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
    public void testQuoteAdvancedRater() {
        PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
        String expectedCoverageEValue = "$100,000 (+$0.00)";
        String expectedDeductibleValue = "$1,000 (+$0.00)";
        Dollar newDeductiblePremiumValue = new Dollar(632);
        Dollar newCoverageEPremiumValue = new Dollar(653);
        mainApp().open();
        createCustomerIndividual();

        policy.initiate();
        policy.getDefaultView().fillUpTo(getPolicyTD(), PremiumsAndCoveragesQuoteTab.class);
        premiumsAndCoveragesQuoteTab.calculatePremium();

        premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E).verify.value(expectedCoverageEValue);
        premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE).verify.value(expectedDeductibleValue);

        String newDeductibleValue = premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE).getAllValues().get(0);
        premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE).setValue(newDeductibleValue);

        PremiumsAndCoveragesQuoteTab.getPolicyTermPremium().verify.equals(new Dollar(0));

        premiumsAndCoveragesQuoteTab.calculatePremium();
        PremiumsAndCoveragesQuoteTab.getPolicyTermPremium().verify.equals(newDeductiblePremiumValue);

        premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E).setValueByIndex(1);

        PremiumsAndCoveragesQuoteTab.getPolicyTermPremium().verify.equals(new Dollar(0));

        premiumsAndCoveragesQuoteTab.calculatePremium();
        PremiumsAndCoveragesQuoteTab.getPolicyTermPremium().verify.equals(newCoverageEPremiumValue);

        PremiumsAndCoveragesQuoteTab.btnContinue.click();
        policy.getDefaultView().fillFromTo(getPolicyTD(), MortgageesTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();
        PolicySummaryPage.getTotalPremiumSummaryForProperty().verify.equals(newCoverageEPremiumValue);
    }
}
