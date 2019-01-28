package aaa.modules.regression.sales.home_ss.ho3;

import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomSoftAssertions;

/**
 * @author Olga Reva
 * @name Test Policy Membership and Loyalty discounts
 * @scenario
 * 1. Find customer or create new if customer does not exist.
 * 2. Create new HSS policy 'Policy#1' with Current AAA Member = 'Yes'.
 * 		2a. During Policy#1 creation verify Membership and Loyalty discount discounts are applied on Discounts section and in Rating Details. 
 * 		2b. Verify status of Policy#1 is Active and term premium on Consolidated view equals of term premium calculated on Premiums&Coverages Quote tab. 
 * 3. Copy Policy#1 3 times to get Policy#2, Policy#3, Policy#4.
 * 4. Retrieve Policy#1 and initiate endorsement with effective date as 'Today'. 
 * 		4a. Initiate endorsement
 * 		4b. Order membership report. 
 * 		4c. Navigate to Premiums&Coverages Quote Tab, calculate premium and verify that Membership and Loyalty discounts are applied both on Discounts section and in Rating Details. 
 * 		4d. Bind endorsement. 
 * 		4e. Verify policy status is Active and term premium on Consolidated view equals to term premium calculated during endorsement.
 * 5. Retrieve Policy#2 and initiate endorsement with effective date as 'Today + 1 day'.  
 * 		5a. Navigate to Applicant tab and change Current AAA Member to 'Yes', enter membership# and Last Name. 
 * 		5b. Order membership report. 
 * 		5c. Navigate to Premiums&Coverages Quote Tab, calculate premium and verify that Membership and Loyalty discounts are applied both on Discounts section and in Rating Details. 
 * 		5d. Bind endorsement. 
 * 		5e. Verify policy status is Active and term premium on Consolidated view equals to term premium calculated during endorsement.
 * 6. Retrieve Policy#3 and initiate endorsement with effective date as 'Today'. 
 * 		6a. Navigate to Applicant tab and change Current AAA Member to 'No'. 
 * 		6b. Navigate to Premiums&Coverages Quote Tab, calculate premium and verify that Membership and Loyalty discounts are not applied on Discounts section and in Rating Details. 
 * 		6c. Bind endorsement. 
 * 		6d. Verify policy status is Active and term premium on Consolidated view equals to term premium calculated during endorsement.
 * 7. Retrieve Policy#3 and initiate endorsement with effective date as 'Today + 1 day'. 
 * 		7a. Navigate to Applicant tab and change Current AAA Member to 'No'. 
 * 		7b. Navigate to Premiums&Coverages Quote Tab, calculate premium and verify that Membership and Loyalty discounts are not applied on Discounts section and in Rating Details. 
 * 		7c. Bind endorsement. 
 * 		7d. Verify policy status is Active and term premium on Consolidated view equals to term premium calculated during endorsement.
 * @details
 */
public class TestPolicyDiscountMembership extends HomeSSHO3BaseTest {
	
	private String policyNumber1; 
	private String policyNumber2; 
	private String policyNumber3; 
	private String policyNumber4; 
	private Dollar origPolicyTermPremium;
	
	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups = { Groups.REGRESSION, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
    public void testPolicyMembershipDiscount(@Optional("") String state) {
        mainApp().open();
        
        TestData td_MembershipPending = getTestSpecificTD("TestData_MembershipPending"); 
        TestData td_CopyPolicy = getTestSpecificTD("TestData_CopyPolicy"); 
    	TestData td_MembershipYes = getTestSpecificTD("TestData_MembershipYes");
    	TestData td_MembershipNo = getTestSpecificTD("TestData_MembershipNo");
    	TestData td_endorsement_today = getPolicyTD("Endorsement", "TestData");
    	TestData td_endorsement_todayPlus1Day = getPolicyTD("Endorsement", "TestData_Plus1Day");
        
        createCustomerIndividual();
        
        //Policy#1 creation
        policy.initiate();
        policy.getDefaultView().fillUpTo(td_MembershipPending, PremiumsAndCoveragesQuoteTab.class, true);
        
        //Policy#1: Premium&Coverages Quote tab 
        origPolicyTermPremium = PremiumsAndCoveragesQuoteTab.getPolicyTermPremium();
        
        Map<String, String> membershipDiscount_dataRow = new HashMap<>();
        membershipDiscount_dataRow.put("Discount Category", "Affinity");
        membershipDiscount_dataRow.put("Discounts Applied", "AAA Membership");
        
        Map<String, String> loyaltyDiscount_dataRow = new HashMap<>();
        loyaltyDiscount_dataRow.put("Discount Category", "Affinity");
        loyaltyDiscount_dataRow.put("Discounts Applied", "AAA Loyalty");
        
        Map<String, String> membershipAndLoyaltyDiscounts_dataRow = new HashMap<>();
        membershipAndLoyaltyDiscounts_dataRow.put("Discount Category", "Affinity");
        membershipAndLoyaltyDiscounts_dataRow.put("Discounts Applied", "AAA Membership, AAA Loyalty");

		CustomSoftAssertions.assertSoftly(softly -> {

			if (getState().equals("NY")) {
				softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(membershipDiscount_dataRow)).exists();
			} else {
				softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(membershipAndLoyaltyDiscounts_dataRow)).exists();
			}

			//Policy#1: Rating Details verification
			if (getState().equals("NY")) {
					softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(membershipDiscount_dataRow)).exists();
					PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
					softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("AAA Membership Discount")).as("Policy#1 Endorsement: Membership discount is not applied")
							.isNotEqualTo("0.0");
					softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Loyality discount")).as("Policy#1 Endorsement: Loyalty discount is applied").isEqualTo("0.0");
					PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
			} else {
				PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
				softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("AAA Membership Discount")).as("Policy#1 Creation: Membership discount is not applied")
						.isNotEqualTo("0.0");
				softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Loyality discount")).as("Policy#1 Creation: Loyalty discount is not applied").isNotEqualTo("0.0");
				PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
			}
		
			new PremiumsAndCoveragesQuoteTab().submitTab();

			policy.getDefaultView().fillFromTo(td_MembershipPending, MortgageesTab.class, PurchaseTab.class, true);
			new PurchaseTab().submitTab();

			softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
			policyNumber1 = PolicySummaryPage.labelPolicyNumber.getValue();
			log.info("TEST: HSS Policy1 created with #" + policyNumber1);

			softly.assertThat(origPolicyTermPremium).as("Policy#1 creation: Term Premium is wrong on Cons view").isEqualTo(PolicySummaryPage.getTotalPremiumSummaryForProperty());

			policy.copyPolicy(td_CopyPolicy);
			policyNumber2 = PolicySummaryPage.labelPolicyNumber.getValue();

			policy.copyPolicy(td_CopyPolicy);
			policyNumber3 = PolicySummaryPage.labelPolicyNumber.getValue();

			policy.copyPolicy(td_CopyPolicy);
			policyNumber4 = PolicySummaryPage.labelPolicyNumber.getValue();

			//==============================Policy#1===================================
			//Policy#1 endorsement with effective date is Today, set Membership to 'Yes'
			SearchPage.openPolicy(policyNumber1);
			log.info("TEST: Endorsement for Policy# " + policyNumber1 + " started");
			policy.endorse().perform(td_endorsement_today);

			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
			policy.getDefaultView().fillFromTo(td_MembershipYes, ApplicantTab.class, PremiumsAndCoveragesQuoteTab.class, true);

			origPolicyTermPremium = PremiumsAndCoveragesQuoteTab.getEndorsedPolicyTermPremium();

			if (getState().equals("NY")) {
				softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(membershipDiscount_dataRow)).exists();
				PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
				softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("AAA Membership Discount")).as("Policy#1 Endorsement: Membership discount is not applied")
						.isNotEqualTo("0.0");
				softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Loyality discount")).as("Policy#1 Endorsement: Loyalty discount is applied").isEqualTo("0.0");
				PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
			} else {
				softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(membershipAndLoyaltyDiscounts_dataRow)).exists();

				PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
				softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("AAA Membership Discount")).as("Policy#1 Endorsement: Membership discount is not applied")
						.isNotEqualTo("0.0");
				softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Loyality discount")).as("Policy#1 Endorsement: Loyalty discount is not applied").isNotEqualTo("0.0");
				PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
			}

			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
			new BindTab().submitTab();

			softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

			softly.assertThat(origPolicyTermPremium).as("Policy#1 endorsement: Term Premium is wrong on Cons view").isEqualTo(PolicySummaryPage.getTotalPremiumSummaryForProperty());

			//=================================Policy#2=================================
			//Policy#2 endorsement with effective date is Today + 1 day, set Membership to 'Yes'
			SearchPage.openPolicy(policyNumber2);
			log.info("TEST: Endorsement for Policy# " + policyNumber2 + " started");
			policy.endorse().perform(td_endorsement_todayPlus1Day);

			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
			policy.getDefaultView().fillFromTo(td_MembershipYes, ApplicantTab.class, PremiumsAndCoveragesQuoteTab.class, true);

			origPolicyTermPremium = PremiumsAndCoveragesQuoteTab.getEndorsedPolicyTermPremium();

			if (getState().equals("NY")) {
				softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(membershipDiscount_dataRow)).exists();
				PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
				softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("AAA Membership Discount")).as("Policy#1 Endorsement: Membership discount is not applied")
						.isNotEqualTo("0.0");
				softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Loyality discount")).as("Policy#1 Endorsement: Loyalty discount is applied").isEqualTo("0.0");
				PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
			} else {
				softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(membershipAndLoyaltyDiscounts_dataRow)).exists();

				PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
				softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("AAA Membership Discount")).as("Policy#1 Endorsement: Membership discount is not applied")
						.isNotEqualTo("0.0");
				softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Loyality discount")).as("Policy#1 Endorsement: Loyalty discount is not applied").isNotEqualTo("0.0");
				PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
			}

			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
			new BindTab().submitTab();
			softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

			softly.assertThat(origPolicyTermPremium).as("Policy#2 endorsement: Term Premium is wrong on Cons view").isEqualTo(PolicySummaryPage.getTotalPremiumSummaryForProperty());

			//===============================Policy#3===================================
			//Policy#3 endorsement with effective date is Today, set Membership to 'No'
			SearchPage.openPolicy(policyNumber3);
			log.info("TEST: Endorsement for Policy# " + policyNumber3 + " started");
			policy.endorse().perform(td_endorsement_today);

			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
			policy.getDefaultView().fillFromTo(td_MembershipNo, ApplicantTab.class, PremiumsAndCoveragesQuoteTab.class, true);

			origPolicyTermPremium = PremiumsAndCoveragesQuoteTab.getEndorsedPolicyTermPremium();

			if (getState().equals("CO") | getState().equals("IN") | getState().equals("KS") | getState().equals("KY") | getState().equals("OH") | getState().equals("OK")) {
				softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(loyaltyDiscount_dataRow)).exists();
			} else {
				softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(membershipDiscount_dataRow)).isPresent(false);
				softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(membershipAndLoyaltyDiscounts_dataRow)).isPresent(false);
			}

			PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
			softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("AAA Membership Discount")).as("Policy#3 Endorsement: Membership discount is applied").isEqualTo("0.0");
			if (getState().equals("CO") | getState().equals("IN") | getState().equals("KS") | getState().equals("KY") | getState().equals("OH") | getState().equals("OK")) {
				softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Loyality discount")).as("Policy#3 Endorsement: Loyalty discount is not applied").isNotEqualTo("0.0");
			} else {
				softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Loyality discount")).as("Policy#3 Endorsement: Loyalty discount is applied").isEqualTo("0.0");
			}
			PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();

			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
			new BindTab().submitTab();
			softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

			softly.assertThat(origPolicyTermPremium).as("Policy#3 endorsement: Term Premium is wrong on Cons view").isEqualTo(PolicySummaryPage.getTotalPremiumSummaryForProperty());

			//===============================Policy#4==================================
			//Policy#4 endorsement with effective date is Today + 1 day, set Membership to 'No'
			SearchPage.openPolicy(policyNumber4);
			log.info("TEST: Endorsement for Policy# " + policyNumber4 + " started");
			policy.endorse().perform(td_endorsement_todayPlus1Day);

			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
			policy.getDefaultView().fillFromTo(td_MembershipNo, ApplicantTab.class, PremiumsAndCoveragesQuoteTab.class, true);

			origPolicyTermPremium = PremiumsAndCoveragesQuoteTab.getEndorsedPolicyTermPremium();

			if (getState().equals("CO") | getState().equals("IN") | getState().equals("KS") | getState().equals("KY") | getState().equals("OH") | getState().equals("OK")) {
				softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(loyaltyDiscount_dataRow)).exists();
			} else {
				softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(membershipDiscount_dataRow)).isPresent(false);
				softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(membershipAndLoyaltyDiscounts_dataRow)).isPresent(false);
			}

			PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
			softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("AAA Membership Discount")).as("Policy#4 Endorsement: Membership discount is applied").isEqualTo("0.0");
			if (getState().equals("CO") | getState().equals("IN") | getState().equals("KS") | getState().equals("KY") | getState().equals("OH") | getState().equals("OK")) {
				softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Loyality discount")).as("Policy#4 Endorsement: Loyalty discount is not applied").isNotEqualTo("0.0");
			} else {
				softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Loyality discount")).as("Policy#4 Endorsement: Loyalty discount is applied").isEqualTo("0.0");
			}
			PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();

			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
			new BindTab().submitTab();
			softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

			softly.assertThat(origPolicyTermPremium).as("Policy#4 endorsement: Term Premium is wrong on Cons view").isEqualTo(PolicySummaryPage.getTotalPremiumSummaryForProperty());
		});
	}

}
