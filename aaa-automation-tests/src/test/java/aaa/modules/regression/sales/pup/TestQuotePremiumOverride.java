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
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

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
 */

public class TestQuotePremiumOverride extends PersonalUmbrellaBaseTest {   
	@Parameters({"state"})
	//@StateList("All")
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.PUP )
    public void testQuotePremiumOverride(@Optional("") String state) {
		PremiumAndCoveragesQuoteTab premiumQuoteTab = policy.getDefaultView().getTab(PremiumAndCoveragesQuoteTab.class);
        mainApp().open();
        createCustomerIndividual();
        createQuote();
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());
        String newPersonalUmbrellaValue = premiumQuoteTab.getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PERSONAL_UMBRELLA).getAllValues().get(1);
        premiumQuoteTab.getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PERSONAL_UMBRELLA).setValue(newPersonalUmbrellaValue);
        PremiumAndCoveragesQuoteTab.getPolicyTermPremium().verify.equals(new Dollar(0));
        
        //set value back to 1,000
        newPersonalUmbrellaValue = premiumQuoteTab.getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PERSONAL_UMBRELLA).getAllValues().get(0);
        premiumQuoteTab.getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PERSONAL_UMBRELLA).setValue(newPersonalUmbrellaValue);
        
        premiumQuoteTab.calculatePremium();
        
//		Override premium by -101%, check error message appears.
       // premiumQuoteTab.calculatePremium();
        PremiumAndCoveragesQuoteTab.btnOverridePremium.click();
        premiumQuoteTab.fillTab(getTestSpecificTD("TestData_Percentage").resolveLinks(),false);
        assertThat(PremiumAndCoveragesQuoteTab.lblErrorMessage).isPresent();
        assertThat(PremiumAndCoveragesQuoteTab.lblErrorMessage).hasValue("The premium cannot be decreased by more than 100%.");
        
//		Override premium by Percentage (20%), check calculated values.
        premiumQuoteTab.fillTab(getTestSpecificTD("TestData_Percentage").adjust(TestData.makeKeyPath("PremiumAndCoveragesQuoteTab","OverridePremium","Percentage"),"20"),false);
		PremiumAndCoveragesQuoteTab.getPolicyTermPremium().verify.notEquals(PremiumAndCoveragesQuoteTab.getFinalTermPremium());
		
//		Override premium by by Percentage (100%), check calculated values.
//		premiumQuoteTab.fillTab(getTestSpecificTD("TestData_Percentage").adjust(TestData.makeKeyPath("PremiumAndCoveragesQuoteTab","OverridePremium","Percentage"),"100"), false);
//		CustomAssert.assertTrue(PremiumAndCoveragesQuoteTab.getPolicyTermPremium().notEquals(PremiumAndCoveragesQuoteTab.getFinalTermPremium()));
//      override premium by 100$
		premiumQuoteTab.fillTab(getTestSpecificTD("TestData_Amount"), false);
		assertThat(PremiumAndCoveragesQuoteTab.getPolicyTermPremium()).isNotEqualTo(PremiumAndCoveragesQuoteTab.getFinalTermPremium());

//		10. Override premium by Percentage (-20%), check calculated values. Confirm Override.
		premiumQuoteTab.fillTab(getTestSpecificTD("TestData_Percentage").adjust(TestData.makeKeyPath("PremiumAndCoveragesQuoteTab","OverridePremium","Percentage"),"-20"),false);
		PremiumAndCoveragesQuoteTab.getPolicyTermPremium().verify.notEquals(PremiumAndCoveragesQuoteTab.getFinalTermPremium());

		PremiumAndCoveragesQuoteTab.dialogOverridePremium.confirm();
		assertThat(PremiumAndCoveragesQuoteTab.lblOverridenPremium).hasValue("Original term premium has been overridden.");
		log.info("Override message is displayed on Premium&Coverages tab");
        PremiumAndCoveragesQuoteTab.btnContinue.click();
        policy.getDefaultView().fillFromTo(getPolicyTD(), UnderwritingAndApprovalTab.class, PurchaseTab.class, true);
        policy.getDefaultView().getTab(PurchaseTab.class).submitTab();
        
    }
}
