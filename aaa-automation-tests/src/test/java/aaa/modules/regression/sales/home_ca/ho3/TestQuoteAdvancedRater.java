package aaa.modules.regression.sales.home_ca.ho3;

import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;

public class TestQuoteAdvancedRater extends HomeCaHO3BaseTest {

    /**
     * @author Jurij Kuznecov
     * @name Test CAH Quote Advanced Rater
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

	@Test(groups = { Groups.REGRESSION, Groups.HIGH })
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
    public void testQuoteAdvancedRater() {
        PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
        String expectedCoverageEValue = "$500,000 (+$0.00)";
        String expectedDeductibleValue = "$1,000 (+$0.00)";
        Dollar newDeductiblePremiumValue = new Dollar(423);
        Dollar newCoverageEPremiumValue = new Dollar(396);

        mainApp().open();
        createCustomerIndividual();

        policy.initiate();
        policy.getDefaultView().fillUpTo(getPolicyTD(), PremiumsAndCoveragesQuoteTab.class);
        premiumsAndCoveragesQuoteTab.calculatePremium();

        premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E).verify.value(expectedCoverageEValue);
        premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE).verify.value(expectedDeductibleValue);

        String newDeductibleValue = premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE).getAllValues().get(0);
        premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE).setValue(newDeductibleValue);

        PremiumsAndCoveragesQuoteTab.getPolicyTermPremium().verify.equals(new Dollar(0));
        premiumsAndCoveragesQuoteTab.calculatePremium();
        PremiumsAndCoveragesQuoteTab.getPolicyTermPremium().verify.equals(newDeductiblePremiumValue);

        String newCoveragEValue = premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E).getAllValues().get(0);
        premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E).setValue(newCoveragEValue);

        PremiumsAndCoveragesQuoteTab.getPolicyTermPremium().verify.equals(new Dollar(0));
        premiumsAndCoveragesQuoteTab.calculatePremium();
        PremiumsAndCoveragesQuoteTab.getPolicyTermPremium().verify.equals(newCoverageEPremiumValue);

        PremiumsAndCoveragesQuoteTab.btnContinue.click();
        policy.getDefaultView().fillFromTo(getPolicyTD(), MortgageesTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();

        PolicySummaryPage.getTotalPremiumSummary().verify.equals(newCoverageEPremiumValue);
    }
}
