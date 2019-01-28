package aaa.modules.regression.sales.pup;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.modules.policy.pup.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderwritingAndApprovalTab;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.utils.TestInfo;

public class TestQuoteAdvancedRater extends PersonalUmbrellaBaseTest {

    /**
     * @author Xiaolan Ge
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

	//TODO jdemb: feature of the original test was checking if premium deltas are added to calculated premium correctly
	//as I can see in old project - deltas are removed. So this test has no meaning. Temporary removed from run, probably need to be removed completely.
	@Parameters({"state"})
	//@StateList("All")
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, enabled = false)
	@TestInfo(component = ComponentConstant.Sales.PUP )
    public void testQuoteAdvancedRater(@Optional("") String state) {
        String expectedPersonalUmbrellaValue = "$1,000,000";
        mainApp().open();
        createCustomerIndividual();
        createQuote();
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());
        Dollar oldTotalPremiumSummary = PremiumAndCoveragesQuoteTab.getPolicyTermPremium();
        assertThat(policy.getDefaultView().getTab(PremiumAndCoveragesQuoteTab.class).getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PERSONAL_UMBRELLA)).hasValue(expectedPersonalUmbrellaValue);

        String newPersonalUmbrellaValue = policy.getDefaultView().getTab(PremiumAndCoveragesQuoteTab.class).getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PERSONAL_UMBRELLA).getAllValues().get(1);
        policy.getDefaultView().getTab(PremiumAndCoveragesQuoteTab.class).getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PERSONAL_UMBRELLA).setValue(newPersonalUmbrellaValue);

        PremiumAndCoveragesQuoteTab.getPolicyTermPremium().verify.equals(new Dollar(0));
        policy.getDefaultView().getTab(PremiumAndCoveragesQuoteTab.class).calculatePremium();
        PremiumAndCoveragesQuoteTab.getPolicyTermPremium().verify.notEquals(oldTotalPremiumSummary);


        PremiumAndCoveragesQuoteTab.btnContinue.click();
        policy.getDefaultView().fillFromTo(getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks()), UnderwritingAndApprovalTab.class, PurchaseTab.class, true);
        policy.getDefaultView().getTab(PurchaseTab.class).submitTab();
    }
}
