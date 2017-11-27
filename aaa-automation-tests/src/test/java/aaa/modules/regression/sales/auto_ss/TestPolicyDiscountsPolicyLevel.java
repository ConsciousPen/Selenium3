package aaa.modules.regression.sales.auto_ss;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class TestPolicyDiscountsPolicyLevel extends AutoSSBaseTest {

	/**
	 * @author Natalija Belakova
	 * @name Policy Level discounts (US 29094)
	 * @scenario
	 * 1. Create quote without discounts and check discounts not displayed
	 * 2. Enter data to apply discounts and check discounts displayed:
	 *    Affinity Discount: Driver is Available for Rating, Driver is Named Insured, Affinity group is 'AAA Employee'
	 *    Membership Discount: Current Member is 'Membership Pending', 
	 *    Payment Plan Discount: Payment Plan is 'Annual', Term is 'Annual'
	 *    Multi-policy discount (Motorcycle, Life, Home): Motorcycle = Yes, Life = Yes, Home = Yes, Renters = Yes, Condo = yes
	 *    Loyalty discount: Days Lapsed = 0, Months with Carrier = 35
	 * 3. Check discounts is NOT displayed:   
	 *    Non-stacking discount: UMBI, UIMBI and MP has Coverage, but 1 PPA and not applicable (applicable only for NV)
	 *    Employee Benefit: not applicable (applicable only for CA Select)
	 *    Persistency Discount: Months with Carrier = 35 but not applicable (applicable only for CA Choice)
	 * 4. Enter data to update discounts and check discounts displayed:
	 *    Multi-policy discount (Motorcycle, Life, Condo): Motorcycle = Yes, Life = Yes, Home = No, Renters = Yes, Condo = yes
	 * 5. Enter data to update discounts and check discounts NOT displayed:
	 *    Loyalty discount: Days Lapsed = 4, Months with Carrier = 35
	 * 6. Perform Flat Endorsement
	 * 7. Enter data to update discounts and check discounts displayed:
	 *    Payment Plan Discount: Payment Plan is 'Semi-Annual', Term is 'Annual'
	 *    Multi-policy discount (Motorcycle, Life, Renters): Motorcycle = Yes, Life = Yes, Home = No, Renters = Yes, Condo = No
	 *    Loyalty discount: Days Lapsed = 3, Months with Carrier = 12
	 * 8. Perform Mid-term Endorsement 1
	 * 9. Enter data to update discounts and check discounts displayed:
	 *    Affinity Discount: Driver is Available for Rating, Driver is Named Insured, Affinity group is 'None' - can NOT be removed at Midterm
	 *    Multi-policy discount (Motorcycle, Life, Renters): Motorcycle = No, Life = No, Home = No, Renters = No, Condo = No - can NOT be removed at Midterm
	 * 10.Enter data to update discounts and check discounts NOT displayed:   
	 *    Payment Plan Discount: Payment Plan is 'Eleven Pay - Standard', Term is 'Annual'
	 *    Membership Discount: Current Member is 'No'
	 * 11.Perform Mid-term Endorsement 2
	 * 12.Enter data to update discounts and check discounts displayed:
	 *    Affinity Discount: Driver is Available for Rating, Driver is Named Insured, Affinity group is 'None' - can NOT be removed at Midterm
	 *    Multi-policy discount (Motorcycle, Life, Home): Motorcycle = No, Life = No, Home = Yes, Renters = No, Condo = No 
	 *    Membership Discount: Current Member is 'Yes'
	 *    Payment Plan Discount: Payment Plan is 'Quarterly', Term is 'Annual'
	 * 13.Perform Manual Renewal
	 * 14.Check discounts displayed:   
	 *    Payment Plan Discount: Payment Plan is 'Quarterly', Term is 'Annual'
	 *    Membership Discount: Current Member is 'Yes'
	 *    Multi-policy discount (Home): Motorcycle = No, Life = No, Home = Yes, Renters = No, Condo = No
	 *    Loyalty discount: Days Lapsed = 368, Months with Carrier = 12, BUT Current Member is 'Yes'
	 * 15.Check discounts NOT displayed:
	 *    Affinity Discount: Driver is Available for Rating, Driver is Named Insured, Affinity group is 'None' 
	 *    Non-stacking discount: UMBI, UIMBI and MP has Coverage, but 1 PPA and not applicable (applicable only for NV)
	 *    Employee Benefit: not applicable (applicable only for CA Select)    
	 */
	
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testPolicyLevelDiscounts(@Optional("") String state) {

		PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
		GeneralTab generalTab = new GeneralTab();
		DriverTab driverTab = new DriverTab();
		PurchaseTab purchaseTab = new PurchaseTab();
		DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
		RatingDetailReportsTab ratingDetailReportsTab = new RatingDetailReportsTab();
		
		
		mainApp().open();
		
		createCustomerIndividual();
		
		
		// ----------- NB Quote
		
		policy.initiate();
		policy.getDefaultView().fillUpTo(getTestSpecificTD("DataGather_WO_Discounts"), PremiumAndCoveragesTab.class, true);
		
		CustomAssert.enableSoftMode();
		
		// check No Policy Level discounts displayed on Premium&Coverages tab - displayed only Passive Restraint Discount(2011, MERCEDES-BENZ, G55AMG)
		checkDiscountsNotDisplayed(getTestSpecificTD("PolicyLevel_Discounts_MessageN"));
		
		// check all policy level discounts is None on Rating Details
		checkPolicyLevelDiscountsValueRatingDetails(getTestSpecificTD("PolicyLevel_Discounts"));
	
		// enter data to apply discounts
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.fillTab(getTestSpecificTD("GeneralTab_Discounts"));
		generalTab.submitTab();
		driverTab.fillTab(getTestSpecificTD("DriverTab_Discounts"));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
	
		// check discounts is displayed on Premium&Coverages tab
		checkDiscountsDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message"));
		
		// enter data to apply discounts
		premiumAndCoveragesTab.fillTab(getTestSpecificTD("PremiumAndCoveragesTab_Discounts"));
		
		// check discounts is displayed on Premium&Coverages tab
		checkDiscountsDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message2"));
		// check discounts is not displayed on Premium&Coverages tab
		checkDiscountsNotDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message2N"));
		// check policy level discounts values on Rating Details
		checkPolicyLevelDiscountsValueRatingDetails(getTestSpecificTD("PolicyLevel_Discounts2"));
		
		// enter data to update discount Multi-policy discount 
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.fillTab(getTestSpecificTD("GeneralTab_Discounts3"));
		
		//order membership report
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.RATING_DETAIL_REPORTS.get());
		ratingDetailReportsTab.fillTab(getTestSpecificTD("RatingDetailReportsTab_Discounts"));
		
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		// check Multi-policy discount (Motorcycle, Life, Condo) is displayed on Premium&Coverages tab
		checkDiscountsDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message3"));
		// check discounts is not displayed on Premium&Coverages tab
		checkDiscountsNotDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message3N"));
		// check Condo discount value on Rating Details
		checkPolicyLevelDiscountsValueRatingDetails(getTestSpecificTD("PolicyLevel_Discounts3"));
		String currentDiscounts = PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString();
		premiumAndCoveragesTab.submitTab();
		
		//issue policy
		policy.getDefaultView().fillFromTo(getTestSpecificTD("DataGather_WO_Discounts"), DriverActivityReportsTab.class, PurchaseTab.class, true);
		purchaseTab.submitTab();
		
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		log.info("Policy created: "+policyNumber+" with Discounts: "+currentDiscounts);
		
		
		// ----------- Flat Endorsement
		
		policy.endorse().perform(getTestSpecificTD("EndorsementFlat"));
		
		//set Condo = No
		generalTab.fillTab(getTestSpecificTD("GeneralTab_Discounts4"));
		
		//set Payment plan to 'Semi-annual'
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.fillTab(getTestSpecificTD("PremiumAndCoveragesTab_Discounts4"));
		
		currentDiscounts = PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString();
		//check discounts is displayed on Premium&Coverages tab
		checkDiscountsDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message4"));

		
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.submitTab();
		
		log.info("Policy Flat Endorsement completed: "+policyNumber+" with Discounts: "+currentDiscounts);
		
		
		// ----------- Mid-term Endorsement 1
		
		policy.endorse().perform(getTestSpecificTD("EndorsementPlusM"));
		
		generalTab.fillTab(getTestSpecificTD("GeneralTab_Discounts5"));
		
		generalTab.submitTab();
		
		driverTab.fillTab(getTestSpecificTD("DriverTab_Discounts5"));
		
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.fillTab(getTestSpecificTD("PremiumAndCoveragesTab_Discounts5"));
		
		//check discounts are displayed on Premium&Coverages tab
		checkDiscountsDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message5"));
		//check discounts are not displayed on Premium&Coverages tab
		checkDiscountsNotDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message5N"));
		
		currentDiscounts = PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString();
		
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.submitTab();
		
		log.info("Policy Mid-term Endorsement completed: "+policyNumber+" with Discounts: "+currentDiscounts);
		
		
		// ----------- Mid-term Endorsement 2
		
		policy.endorse().perform(getTestSpecificTD("EndorsementPlus2M"));
				
		generalTab.fillTab(getTestSpecificTD("GeneralTab_Discounts6"));
				
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.fillTab(getTestSpecificTD("PremiumAndCoveragesTab_Discounts6"));
				
		//check discounts are displayed on Premium&Coverages tab
		checkDiscountsDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message6"));
		//check discounts are not displayed on Premium&Coverages tab		
		currentDiscounts = PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString();
				
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.submitTab();
				
		log.info("Policy Mid-term Endorsement completed: "+policyNumber+" with Discounts: "+currentDiscounts);
			
		
		// ----------- Manual Renewal
		
		policy.renew().start();	
		
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		
		//check discounts are displayed on Premium&Coverages tab
		checkDiscountsDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message7"));
		
		currentDiscounts = PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString();
		
		documentsAndBindTab.saveAndExit();
		
		log.info("Policy manual Renewal created: "+policyNumber+" with Discounts: "+currentDiscounts);
		
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}
	

	private void checkPolicyLevelDiscountsValueRatingDetails(TestData td_Discounts){
		
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();

		for (String discountName : td_Discounts.getKeys()) {		
			CustomAssert.assertEquals(discountName+": ", td_Discounts.getValue(discountName), new PremiumAndCoveragesTab().getRatingDetailsQuoteInfoData().getValue(discountName));
		}
		
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
	}
	
	private void checkDiscountsDisplayed(TestData td_Discounts){
		
		for (String discountName : td_Discounts.getValue("Discounts").split("\\|")) {
			CustomAssert.assertTrue(discountName+" is not displayed in Discount message, but must be displayed ", PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString().contains(discountName));
		}
	}
	
	private void checkDiscountsNotDisplayed(TestData td_Discounts){
		
		for (String discountName : td_Discounts.getValue("Discounts").split("\\|")) {
			CustomAssert.assertFalse(discountName+" is displayed in Discount message, but not be displayed ", PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString().contains(discountName));
		}
	}

}