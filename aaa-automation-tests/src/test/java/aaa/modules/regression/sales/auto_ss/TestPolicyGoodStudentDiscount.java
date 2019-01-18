package aaa.modules.regression.sales.auto_ss;

import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssertions;

public class TestPolicyGoodStudentDiscount extends AutoSSBaseTest {
	protected TestData tdPolicy;
	private String origPolicyNum;
	private String policyNum1;
	private String policyNum2;
	private String policyNum3;
	private String policyNum4;
	
	/**
	 * @author Olga Reva
	 * @name Validation of Good Student discount for New Quote in the Premium & Coverages Discount section and Rating Details
	 * @scenario
	 * 1. Verify Good Student Discount at new quote creation. 
	 * 1.1. Initiate a new Auto Quote with the Authority to create a Quote. Provide the required information to create a quote. 
	 * 1.2. Select the following field values for at least one driver: 
	 * 	Quote# 1: Driver Type = Available for Rating, Age = 24, Marital status = Separated, Occupation = Homemaker, Most Recent GPA = College Graduate; 
	 * 	Quote# 2: Driver Type = Available for Rating, Age = 23, Marital status = Single, Occupation = Farmer, Most Recent GPA = College Graduate; 
	 * 	Quote# 3: Driver Type = Available for Rating, Age = 16, Marital status = Single, Occupation = Student, Most Recent GPA = B Student; 
	 * 	Quote# 4: Driver Type = Available for Rating, Age = 25, Marital status = Divorced, Occupation = Student, Most Recent GPA = Pass;  
	 * 1.3. Calculate premium. 
	 * 1.4. Verify Good Student discount is applied to the driver and display Premium and Coverages Discount section.
	 * 1.5. Verify Good Student discount is applied to the driver and display as 'Yes' in Rating Details. 
	 * 1.6. Bind the quote. 
	 * 
	 * 2. Verify Good Student Discount at endorsement. 
	 * 2.1. Retrieve Policy# 2 and initiate an endorsement for it. 
	 * 2.2. Navigate to Driver tab and change Marital Status to "Married". 
	 * 2.3. Calculate premium. 
	 * 2.4. Verify Good Student discount is removed and doesn't display on Premium and Coverages Discount section.
	 * 2.5. Verify Good Student discount is removed and display as 'No' in Rating Details. 
	 * 2.6. Bind endorsement. 
	 * 
	 * 3. Verify Good Student Discount at Renewal image. 
	 * 3.1. Retrieve for Policies# 1, 3, 4 renewal image in status Premium calculated in Iquiry mode. 
	 * 3.2. Navigate to Premium & Coverages tab. 
	 * 3.3. Policies# 1, 3: Verify Good Student discount is applied to the driver and display Premium and Coverages Discount section.
	 * 3.4. Policies# 1, 3: Verify Good Student discount is applied to the driver and display as 'Yes' in Rating Details.
	 * 3.5. Policy# 4: Verify Good Student discount isn't applied to the driver and displaying as 'No' in Rating Details.
	 */
	
	@Parameters({"state"})
	@StateList(statesExcept = {Constants.States.CA})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testValidationGoodStudentDiscount(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		
		TestData td_quote1 = getTestSpecificTD("TestData_1");
		TestData td_quote2 = getTestSpecificTD("TestData_2");
		TestData td_quote3 = getTestSpecificTD("TestData_3");
		TestData td_quote4 = getTestSpecificTD("TestData_4"); 
		TestData td_endorse = getTestSpecificTD("TestData_Endorsement");
		
		mainApp().open();
		createCustomerIndividual();
		
		origPolicyNum = createPolicy();
		
		policyNum1 = createPolicyAndVerifyGoodStudentDiscount(td_quote1, true);
		log.info("TEST: Policy 1 is created with #" +policyNum1);		
		policyNum2 = createPolicyAndVerifyGoodStudentDiscount(td_quote2, true);
		log.info("TEST: Policy 2 is created with #" +policyNum2);
		policyNum3 = createPolicyAndVerifyGoodStudentDiscount(td_quote3, true);
		log.info("TEST: Policy 3 is created with #" +policyNum3);
		policyNum4 = createPolicyAndVerifyGoodStudentDiscount(td_quote4, true);
		log.info("TEST: Policy 4 is created with #" +policyNum4);		

		verifyGoodStudentDiscountOnEndorsement(policyNum2, td_endorse, false);

		verifyGoodStudentDiscountOnRenewal(policyNum1, true);
		verifyGoodStudentDiscountOnRenewal(policyNum3, true);
		verifyGoodStudentDiscountOnRenewal(policyNum4, false);
		
	}
	
	private String createPolicyAndVerifyGoodStudentDiscount(TestData td_quote, boolean isDiscountApplied) {
		SearchPage.openPolicy(origPolicyNum);
		
		policy.policyCopy().perform(td_quote);		
		policy.dataGather().start();
		
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get()); 
		new GeneralTab().fillTab(td_quote); 
		new GeneralTab().submitTab();
		
		new DriverTab().fillTab(td_quote); 
		new DriverTab().submitTab();
		
        new RatingDetailReportsTab().fillTab(td_quote).submitTab();
        
        if (getState().equals(Constants.States.NY)) {
        	NavigationPage.toViewTab(NavigationEnum.AutoSSTab.ASSIGNMENT.get()); 
        	new AssignmentTab().submitTab();
        }
        
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();

		String discountApplied;
		if (isDiscountApplied) {
			CustomAssertions.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).contains("Good Student Discount");
			discountApplied = "Yes";
		}
		else {
			CustomAssertions.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).doesNotContain("Good Student Discount");
			discountApplied = "No";
		}
              
        PremiumAndCoveragesTab.RatingDetailsView.open();
        new PremiumAndCoveragesTab().getRatingDetailsDriversData().forEach(i -> CustomAssertions.assertThat(i.getValue("Good Student Discount")).isEqualTo(discountApplied));
        PremiumAndCoveragesTab.RatingDetailsView.close();
        new PremiumAndCoveragesTab().submitTab();

        if (new ErrorTab().buttonOverride.isPresent()) {
        	new ErrorTab().fillTab(td_quote);
        	new ErrorTab().buttonOverride.click();
        	new PremiumAndCoveragesTab().submitTab();
        }

        new DriverActivityReportsTab().fillTab(td_quote).submitTab();
	    new DocumentsAndBindTab().fillTab(td_quote).submitTab();
        new PurchaseTab().fillTab(td_quote).submitTab();

		CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        return PolicySummaryPage.labelPolicyNumber.getValue();
	}

	private void verifyGoodStudentDiscountOnEndorsement(String policyNum, TestData td, boolean isDiscountApplied) {
		SearchPage.openPolicy(policyNum);
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
		new DriverTab().fillTab(td).submitTab();

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();

		String discountApplied;
		if (isDiscountApplied) {
			CustomAssertions.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).contains("Good Student Discount");
			discountApplied = "Yes";
		}
		else {
			CustomAssertions.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).doesNotContain("Good Student Discount");
			discountApplied = "No";
		}

		PremiumAndCoveragesTab.RatingDetailsView.open();

		Map<String, String> goodStudentDiscount = new HashMap<>();
		goodStudentDiscount.put("", "Good Student Discount");
		goodStudentDiscount.put("Proposed Changes", discountApplied);

		CustomAssertions.assertThat(PremiumAndCoveragesTab.tableRatingDetailsDrivers.getRowContains(goodStudentDiscount)).isPresent();
		PremiumAndCoveragesTab.RatingDetailsView.close();

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        new DocumentsAndBindTab().submitTab();

		CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        log.info("TEST: Endorsement is created for policy with #" + policyNum2);
	}

	private void verifyGoodStudentDiscountOnRenewal(String policyNum, boolean isDiscountApplied) {
		SearchPage.openPolicy(policyNum);
		log.info("TEST: Verifying Good Student discount for renewal of " + policyNum);

		policy.renew().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();

		String discountApplied;
		if (isDiscountApplied) {
			CustomAssertions.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).contains("Good Student Discount");
			discountApplied = "Yes";
		}
		else {
			CustomAssertions.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).doesNotContain("Good Student Discount");
			discountApplied = "No";
		}

		PremiumAndCoveragesTab.RatingDetailsView.open();
		new PremiumAndCoveragesTab().getRatingDetailsDriversData().forEach(i -> CustomAssertions.assertThat(i.getValue("Good Student Discount")).isEqualTo(discountApplied));
		PremiumAndCoveragesTab.RatingDetailsView.close();
        new PremiumAndCoveragesTab().saveAndExit();

		CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		CustomAssertions.assertThat(PolicySummaryPage.buttonRenewals.isEnabled()).isTrue();
        PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
		
		policy.policyInquiry().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		if (isDiscountApplied) {
			CustomAssertions.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).contains("Good Student Discount");
		}
		else {
			CustomAssertions.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).doesNotContain("Good Student Discount");
		}
		PremiumAndCoveragesTab.RatingDetailsView.open();
		new PremiumAndCoveragesTab().getRatingDetailsDriversData().forEach(i -> CustomAssertions.assertThat(i.getValue("Good Student Discount")).isEqualTo(discountApplied));
        PremiumAndCoveragesTab.RatingDetailsView.close();
		new PremiumAndCoveragesTab().cancel();	
	}
	
}
