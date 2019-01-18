package aaa.modules.regression.sales.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.ETCSCoreSoftAssertions;

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
	@StateList(states = { States.AZ, States.UT })
	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testPolicyLevelDiscounts(@Optional("") String state) {

		PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
		GeneralTab generalTab = new GeneralTab();
		DriverTab driverTab = new DriverTab();
		PurchaseTab purchaseTab = new PurchaseTab();
		DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
		RatingDetailReportsTab ratingDetailReportsTab = new RatingDetailReportsTab();
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		
		mainApp().open();
		
		createCustomerIndividual();
		
		
		// ----------- NB Quote
		
		policy.initiate();
		policy.getDefaultView().fillUpTo(getTestSpecificTD("DataGather_WO_Discounts"), PremiumAndCoveragesTab.class, true);
		
		
		// check No Policy Level discounts displayed on Premium&Coverages tab - displayed only Passive Restraint Discount(2011, MERCEDES-BENZ, G55AMG)
		checkDiscountsNotDisplayed(getTestSpecificTD("PolicyLevel_Discounts_MessageN"), softly);
		
		// check all policy level discounts is None on Rating Details
		checkPolicyLevelDiscountsValueRatingDetails(getTestSpecificTD("PolicyLevel_Discounts"), softly);
	
		// enter data to apply discounts
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.fillTab(getTestSpecificTD("GeneralTab_Discounts"));
		generalTab.submitTab();
		driverTab.fillTab(getTestSpecificTD("DriverTab_Discounts"));
		driverTab.submitTab();
		ratingDetailReportsTab.fillTab(getTestSpecificTD("RatingDetailReportsTab_Discounts"));
		
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
	
		// check discounts is displayed on Premium&Coverages tab
		checkDiscountsDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message"), softly);
		
		// enter data to apply discounts
		premiumAndCoveragesTab.fillTab(getTestSpecificTD("PremiumAndCoveragesTab_Discounts"));
		
		// check discounts is displayed on Premium&Coverages tab
		checkDiscountsDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message2"), softly);
		// check discounts is not displayed on Premium&Coverages tab
		checkDiscountsNotDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message2N"), softly);
		// check policy level discounts values on Rating Details
		checkPolicyLevelDiscountsValueRatingDetails(getTestSpecificTD("PolicyLevel_Discounts2"), softly);
		
		// enter data to update discount Multi-policy discount 
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.fillTab(getTestSpecificTD("GeneralTab_Discounts3"));
		
		//order membership report
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.RATING_DETAIL_REPORTS.get());
		ratingDetailReportsTab.fillTab(getTestSpecificTD("RatingDetailReportsTab_Discounts"));
		
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		// check Multi-policy discount (Motorcycle, Life, Condo) is displayed on Premium&Coverages tab
		checkDiscountsDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message3"), softly);
		// check discounts is not displayed on Premium&Coverages tab
		checkDiscountsNotDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message3N"), softly);
		// check Condo discount value on Rating Details
		checkPolicyLevelDiscountsValueRatingDetails(getTestSpecificTD("PolicyLevel_Discounts3"), softly);
		String currentDiscounts = PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString();
		premiumAndCoveragesTab.submitTab();
		
		//issue policy
		policy.getDefaultView().fillFromTo(getTestSpecificTD("DataGather_WO_Discounts"), DriverActivityReportsTab.class, PurchaseTab.class, true);
		purchaseTab.submitTab();
		
		softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		log.info("Policy created: "+policyNumber+" with Discounts: "+currentDiscounts);
		
		
		// ----------- Flat Endorsement
		
		policy.endorse().perform(getTestSpecificTD("EndorsementFlat"));
		
		//set Condo = No
		generalTab.fillTab(getTestSpecificTD("GeneralTab_Discounts4"));
		
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		
		currentDiscounts = PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString();
		//check discounts is displayed on Premium&Coverages tab
		checkDiscountsDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message4"), softly);

		
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
		checkDiscountsDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message5"), softly);
		//check discounts are not displayed on Premium&Coverages tab
		checkDiscountsNotDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message5N"), softly);
		
		currentDiscounts = PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString();
		
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.submitTab();
		
		log.info("Policy Mid-term Endorsement completed: "+policyNumber+" with Discounts: "+currentDiscounts);
		
		
		// ----------- Mid-term Endorsement 2
		
		policy.endorse().perform(getTestSpecificTD("EndorsementPlus2M"));
				
		generalTab.fillTab(getTestSpecificTD("GeneralTab_Discounts6"));
		
		//order membership report
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.RATING_DETAIL_REPORTS.get());
		ratingDetailReportsTab.fillTab(getTestSpecificTD("RatingDetailReportsTab_Discounts"));
				
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.fillTab(getTestSpecificTD("PremiumAndCoveragesTab_Discounts6"));
				
		//check discounts are displayed on Premium&Coverages tab
		checkDiscountsDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message6"), softly);
		
		currentDiscounts = PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString();
				
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.submitTab();
				
		log.info("Policy Mid-term Endorsement completed: "+policyNumber+" with Discounts: "+currentDiscounts);
			
		
		// ----------- Manual Renewal
		
		policy.renew().start();	
		
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		
		//check discounts are displayed on Premium&Coverages tab
		checkDiscountsDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message7"), softly);
		
		currentDiscounts = PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString();
		
		documentsAndBindTab.saveAndExit();
		
		log.info("Policy manual Renewal created: "+policyNumber+" with Discounts: "+currentDiscounts);
	}
	
	/**
	 * @author Natalija Belakova
	 * @name Policy Level discounts: Advanced Shopping Discount (US 29094)
	 * @scenario
	 * 1. Create quote without discounts and check discounts not displayed
	 * 2. Enter data to apply/remove discounts and check discounts displayed/not displayed
	 */
	
	@Parameters({"state"})
	@StateList(states = { States.AZ, States.UT })
	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testAdvShoppingDiscount(@Optional("") String state) {

		PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
		GeneralTab generalTab = new GeneralTab();
		PurchaseTab purchaseTab = new PurchaseTab();
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		
		mainApp().open();
		
		createCustomerIndividual();
	
		
		// ----------- NB Quote
		
		policy.initiate();
		policy.getDefaultView().fillUpTo(getTestSpecificTD("DataGather_WO_Discounts"), PremiumAndCoveragesTab.class, true);
		
		// check No Policy Level discounts displayed on Premium&Coverages tab - displayed only Passive Restraint Discount(2011, MERCEDES-BENZ, G55AMG)
		checkDiscountsNotDisplayed(getTestSpecificTD("PolicyLevel_Discounts_MessageN"), softly);
		
		// check all policy level discounts is None on Rating Details
		checkPolicyLevelDiscountsValueRatingDetails(getTestSpecificTD("PolicyLevel_Discounts"), softly);
	
		// enter data to apply discount: Advanced Shopping
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get()); 
		generalTab.fillTab(getTestSpecificTD("GeneralTab_AShDiscount"));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		
		// check Advanced Shopping discount is displayed on Premium&Coverages tab
		checkDiscountsDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message_ASh"), softly);
		
		// check policy level discounts values on Rating Details
		checkPolicyLevelDiscountsValueRatingDetails(getTestSpecificTD("PolicyLevel_AShDiscount"), softly);
		// check underwriting values on Rating Details
		checkPolicyLevelUnderwritingValueRatingDetails(getTestSpecificTD("PolicyLevel_Underwriting"), softly);
		
		// enter data to remove discount: Advanced Shopping
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get()); 
		generalTab.fillTab(getTestSpecificTD("GeneralTab_AShDiscount2"));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		
		// check Advanced Shopping discount is not displayed on Premium&Coverages tab
		checkDiscountsNotDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message_ASh"), softly);
		
		// enter data to apply discount: Advanced Shopping
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get()); 
		generalTab.fillTab(getTestSpecificTD("GeneralTab_AShDiscount3"));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
				
		// check Advanced Shopping discount is displayed on Premium&Coverages tab
		checkDiscountsDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message_ASh"), softly);
		
		// enter data to remove discount: Advanced Shopping
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get()); 
		generalTab.fillTab(getTestSpecificTD("GeneralTab_AShDiscount4"));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
						
		// check Advanced Shopping discount is not displayed on Premium&Coverages tab
		checkDiscountsNotDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message_ASh"), softly);
		
		// enter data to remove discount: Advanced Shopping
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get()); 
		generalTab.fillTab(getTestSpecificTD("GeneralTab_AShDiscount5"));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
								
		// check Advanced Shopping discount is displayed on Premium&Coverages tab
		checkDiscountsNotDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message_ASh"), softly);
		
		// enter data to remove discount: Advanced Shopping
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get()); 
		generalTab.fillTab(getTestSpecificTD("GeneralTab_AShDiscount6"));
		// enter data to apply discount: Payment Plan Discount
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
								
		// check Advanced Shopping discount is not displayed on Premium&Coverages tab
		checkDiscountsNotDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message_ASh"), softly);
		
		// enter data to remove discount: Advanced Shopping
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get()); 
		generalTab.fillTab(getTestSpecificTD("GeneralTab_AShDiscount7"));
		// enter data to apply discount: Payment Plan Discount for semi-annual
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.fillTab(getTestSpecificTD("PremiumAndCoveragesTab_AShDiscount7"));
										
		// check discounts is not displayed on Premium&Coverages tab
		checkDiscountsNotDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message_ASh"), softly);
		checkDiscountsDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message_PP"), softly);
				
		// enter data to apply discount: Advanced Shopping
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get()); 
		generalTab.fillTab(getTestSpecificTD("GeneralTab_AShDiscount8"));
		
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
										
		// check Advanced Shopping discount is displayed on Premium&Coverages tab
		checkDiscountsDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message_ASh"), softly);
		checkDiscountsDisplayed(getTestSpecificTD("PolicyLevel_Discounts_Message_PP"), softly);
		
		// check policy level discounts values on Rating Details
		checkPolicyLevelDiscountsValueRatingDetails(getTestSpecificTD("PolicyLevel_AShDiscount8"), softly);
		// check underwriting values on Rating Details
		checkPolicyLevelUnderwritingValueRatingDetails(getTestSpecificTD("PolicyLevel_Underwriting8"), softly);
		
		String currentDiscounts = PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString();
		
		premiumAndCoveragesTab.submitTab();
		
		//issue policy
		policy.getDefaultView().fillFromTo(getTestSpecificTD("DataGather_WO_Discounts"), DriverActivityReportsTab.class, PurchaseTab.class, true);
		purchaseTab.submitTab();
				
		softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_PENDING);
		
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		log.info("Policy created: "+policyNumber+" with Discounts: "+currentDiscounts);
	}


	private void checkPolicyLevelDiscountsValueRatingDetails(TestData td_Discounts, ETCSCoreSoftAssertions softAssertions){
		
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();

		for (String discountName : td_Discounts.getKeys()) {		
			softAssertions.assertThat(td_Discounts.getValue(discountName)).isEqualTo(new PremiumAndCoveragesTab().getRatingDetailsQuoteInfoData().getValue(discountName));
		}
		
		PremiumAndCoveragesTab.RatingDetailsView.buttonRatingDetailsOk.click();
	}
	
	private void checkPolicyLevelUnderwritingValueRatingDetails(TestData td_Discounts, ETCSCoreSoftAssertions softAssertions){
		
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();

		for (String discountName : td_Discounts.getKeys()) {		
			softAssertions.assertThat(td_Discounts.getValue(discountName)).isEqualTo(new PremiumAndCoveragesTab().getRatingDetailsUnderwritingValueData().getValue(discountName));
		}
		
		PremiumAndCoveragesTab.RatingDetailsView.buttonRatingDetailsOk.click();
	}
	
	private void checkDiscountsDisplayed(TestData td_Discounts, ETCSCoreSoftAssertions softAssertions){
		
		for (String discountName : td_Discounts.getValue("Discounts").split("\\|")) {
			softAssertions.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).contains(discountName);
		}
	}
	
	private void checkDiscountsNotDisplayed(TestData td_Discounts, ETCSCoreSoftAssertions softAssertions){
		
		for (String discountName : td_Discounts.getValue("Discounts").split("\\|")) {
			softAssertions.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).doesNotContain(discountName);
		}
	}

}