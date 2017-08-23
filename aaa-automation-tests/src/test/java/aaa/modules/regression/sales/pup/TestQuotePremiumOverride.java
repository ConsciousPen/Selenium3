package aaa.modules.regression.sales.pup;

import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.Dollar;

import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.modules.policy.pup.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderwritingAndApprovalTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;

public class TestQuotePremiumOverride extends PersonalUmbrellaBaseTest {

    /**
     * @author Xiaolan Ge
     * @name Test PUP Quote Premium Override
     * @scenario 
     * 1. Create new or open existent Customer;
     * 2. Start PUP quote creation;
     * 3. Fill all mandatory fields;
     * 4. Calculate premium.
     * 5. On Premium and coverage tab change "Personal Umbrella" and check that calculated premium resets to zero.
     * 6. Calculate premium.
     * 7. Invoke Override Premium Dialog.
     * 8. Override premium by -101%, check error message appears. Cancel Override.
     * 9. Override premium by Percentage (20%), check calculated values. Cancel Override.
     * 10. Override premium by Percentage (100%), check calculated values. Cancel Override.
     * 11.Override premium by Percentage (-20%), check calculated values. Confirm Override.
     * 12.Check override success message.
     * 13.Issue Policy;
     * 14.Check Policy status is Active.
     * 15.Check new premium on Policy Summary screen
     */

	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.PUP )
    public void testQuoteAdvancedRater() {
        mainApp().open();
        //SearchPage.search(SearchFor.QUOTE, SearchBy.POLICY_QUOTE, "QUTPU927440468");
        createCustomerIndividual();
        createQuote();
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());
        String newPersonalUmbrellaValue = policy.getDefaultView().getTab(PremiumAndCoveragesQuoteTab.class).getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PERSONAL_UMBRELLA).getAllValues().get(1);
        policy.getDefaultView().getTab(PremiumAndCoveragesQuoteTab.class).getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PERSONAL_UMBRELLA).setValue(newPersonalUmbrellaValue);
        PremiumAndCoveragesQuoteTab.getPolicyTermPremium().verify.equals(new Dollar(0));
        policy.getDefaultView().getTab(PremiumAndCoveragesQuoteTab.class).calculatePremium();
        
//		Override premium by -101%, check error message appears.
        policy.getDefaultView().getTab(PremiumAndCoveragesQuoteTab.class).calculatePremium();
        PremiumAndCoveragesQuoteTab.btnOverridePremium.click();
        policy.getDefaultView().getTab(PremiumAndCoveragesQuoteTab.class).fillTab(getTestSpecificTD("TestData_Percentage").resolveLinks(),false);
        PremiumAndCoveragesQuoteTab.lblErrorMessage.verify.present();
        PremiumAndCoveragesQuoteTab.lblErrorMessage.verify.value("The premium cannot be decreased by more than 100%.");
        
//		Override premium by Percentage (20%), check calculated values.
		policy.getDefaultView().getTab(PremiumAndCoveragesQuoteTab.class).fillTab(getTestSpecificTD("TestData_Percentage").adjust(TestData.makeKeyPath("PremiumAndCoveragesQuoteTab","OverridePremium","Percentage"),"20"),false);
		PremiumAndCoveragesQuoteTab.getPolicyTermPremium().verify.notEquals(PremiumAndCoveragesQuoteTab.getFinalTermPremium());
		
//		Override premium by by Percentage (100%), check calculated values.
		policy.getDefaultView().getTab(PremiumAndCoveragesQuoteTab.class).fillTab(getTestSpecificTD("TestData_Percentage").adjust(TestData.makeKeyPath("PremiumAndCoveragesQuoteTab","OverridePremium","Percentage"),"100"), false);
		CustomAssert.assertTrue(PremiumAndCoveragesQuoteTab.getPolicyTermPremium().notEquals(PremiumAndCoveragesQuoteTab.getFinalTermPremium()));

//		10. Override premium by Percentage (-20%), check calculated values. Confirm Override.
		policy.getDefaultView().getTab(PremiumAndCoveragesQuoteTab.class).fillTab(getTestSpecificTD("TestData_Percentage").adjust(TestData.makeKeyPath("PremiumAndCoveragesQuoteTab","OverridePremium","Percentage"),"-20"),false);
		PremiumAndCoveragesQuoteTab.getPolicyTermPremium().verify.notEquals(PremiumAndCoveragesQuoteTab.getFinalTermPremium());

		PremiumAndCoveragesQuoteTab.dialogOverridePremium.confirm();
		PremiumAndCoveragesQuoteTab.lblOverridenPremium.verify.value("Original term premium has been overridden.");
		log.info("Override message is displayed on Premium&Coverages tab");
        PremiumAndCoveragesQuoteTab.btnContinue.click();
        policy.getDefaultView().fillFromTo(getPolicyTD().adjust(getTestSpecificTD("TestData_Error").resolveLinks()), UnderwritingAndApprovalTab.class, PurchaseTab.class, true);
        policy.getDefaultView().getTab(PurchaseTab.class).submitTab();
        PolicySummaryPage.getTotalPremiumSummaryForProperty().verify.equals(PremiumAndCoveragesQuoteTab.getPolicyTermPremium());
    }
}
