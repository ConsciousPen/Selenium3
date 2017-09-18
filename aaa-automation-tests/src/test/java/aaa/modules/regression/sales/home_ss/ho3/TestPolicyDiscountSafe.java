package aaa.modules.regression.sales.home_ss.ho3;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.DocumentsTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Olga Reva
 * @name Test Policy Safe discounts
 * @scenario
 * 1. Find customer or create new if customer does not exist.
 * 2. Initiate new HSS policy creation. 
 * 3. Fill all mandatory fields on all tabs, calculate premium. 
 * 4. Navigate to Property Info tab and set check boxes: 
 * 		Central fire alarm, Full residential sprinklers, 
 * 		Central theft alarm, Gated community.
 * 5. Navigate to Premiums&Coverages Quote tab and re-calculate premium. 
 * 6. Verify Theft Protection, Newer Home, Fire Protection discounts are applied and displaying in Discounts section and in Rating Details. 
 * 7. Verify that premium changed after discounts applied.
 * 8. Bind and purchase policy. 
 * 9. Verify policy status is Active and premium is the same as on P&C screen.
 * @details
 */

public class TestPolicyDiscountSafe extends HomeSSHO3BaseTest {
	
	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testPolicySafeDiscount(String state) {
		mainApp().open();

		TestData td = getPolicyTD("DataGather", "TestData"); 
		TestData td_safeHome = getTestSpecificTD("TestData"); 
		Dollar premiumWithoutDiscount;
		Dollar premiumWithDiscount;
		
        createCustomerIndividual();
        
        policy.initiate();
        policy.getDefaultView().fillUpTo(td, BindTab.class);

        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        premiumWithoutDiscount = PremiumsAndCoveragesQuoteTab.getPolicyTermPremium();   
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
        PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
        propertyInfoTab.fillTab(td_safeHome);
        propertyInfoTab.submitTab();
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        PremiumsAndCoveragesQuoteTab premiumsTab = new PremiumsAndCoveragesQuoteTab();
        premiumsTab.calculatePremium();
        premiumWithDiscount = PremiumsAndCoveragesQuoteTab.getPolicyTermPremium();

        CustomAssert.enableSoftMode();
        CustomAssert.assertTrue("Premium after Safe Home discount applied equals to initial premium", 
        		!premiumWithoutDiscount.equals(premiumWithDiscount));
        
        Map<String, String> safeHomeDiscounts_dataRow = new HashMap<>();
        safeHomeDiscounts_dataRow.put("Discount Category", "Safe Home");
        safeHomeDiscounts_dataRow.put("Discounts Applied", "Theft Protection, Newer Home, Fire Protection");
        
        PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(safeHomeDiscounts_dataRow).verify.present();
		
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
        CustomAssert.assertFalse("Safe Home Discount is not applied", 
        		PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Safe Home Discount Category").equals("0.0")); 
        CustomAssert.assertFalse("Newer Home discount is not applied", 
        		PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Newer Home discount").equals("0.0")); 
        CustomAssert.assertTrue("Incorrect value of Theft Alarm in Rating Details", 
        		PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Theft Alarm").equals("Central")); 
        CustomAssert.assertTrue("Incorrect value of Private community in Rating Details", 
        		PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Private community").equals("Yes")); 
        CustomAssert.assertFalse("Theft Protective Devices discount is not applied", 
        		PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Theft Protective Devices discount").equals("0.0")); 
        CustomAssert.assertTrue("Incorrect value of Fire alarm in Rating Details", 
        		PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Fire alarm").equals("Central")); 
        CustomAssert.assertTrue("Incorrect value of Sprinkler protection in Rating Details", 
        		PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Sprinkler protection").equals("Full")); 
        CustomAssert.assertFalse("Fire Protective Device discount is not applied", 
        		PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Fire Protective Device discount").equals("0.0")); 
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.DOCUMENTS.get());
        DocumentsTab documentsTab = new DocumentsTab(); 
        documentsTab.fillTab(td_safeHome);
        documentsTab.submitTab();
        
        policy.getDefaultView().fillFromTo(td, BindTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();
        
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        log.info("TEST: HSS Policy created with #" + PolicySummaryPage.labelPolicyNumber.getValue());
        
        CustomAssert.assertTrue("Incorrect premium value on Consolidated page", 
        		premiumWithDiscount.equals(PolicySummaryPage.getTotalPremiumSummaryForProperty()));
        CustomAssert.assertAll();
        
	}

}
