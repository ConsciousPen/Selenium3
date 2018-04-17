package aaa.modules.regression.sales.auto_ss.functional;

import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssertions;

public class TestGoodStudentDiscount extends AutoSSBaseTest {
	SoftAssertions softly = new SoftAssertions();
	
	protected TestData tdPolicy;
	private String origPolicyNum;
	private String policyNum1;
	private String policyNum2;
	private String policyNum3;
	private String policyNum4;
	private String policyNum5;
	
	/**
	 * @author 
	 * @name Validation of Good Student discount for New Quote in the Premium & Coverages Discount section and Rating Details
	 * @scenario
	 * 1. Initiate a new Auto Quote with the Authority to create a Quote. Provide the required information to create a quote. 
	 * 2. Select the following field values for at least one driver: 
	 * Quote# 1: Driver Type = Available for Rating, Age = 24, Marital status = Separated, Occupation = Homemaker, Most Recent GPA = College Graduate; 
	 * Quote# 2: Driver Type = Available for Rating, Age = 23, Marital status = Single, Occupation = Farmer, Most Recent GPA = College Graduate; 
	 * Quote# 3: Driver Type = Available for Rating, Age = 16, Marital status = Single, Occupation = Student, Most Recent GPA = B Student; 
	 * Quote# 4: Driver Type = Available for Rating, Age = 25, Marital status = Divorced, Occupation = Student, Most Recent GPA = Pass; 
	 * Quote# 5: Driver Type = Available for Rating, Age = 24, Marital status = Separated, Occupation = Homemaker, Most Recent GPA = College Graduate, 
	 * 				Total Years Driving Experience = 10, DSR points >1, Has chargeable suspension within last 3 years; 
	 * 3. Calculate premium. 
	 * 4. Verify Good Student discount is applied to the driver and display Premium and Coverages Discount section.
	 * 5. Verify Good Student discount is applied to the driver and display in Rating Details. 
	 * 6. Bind the quote. 
	 */
	
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void TestValidationGoodStudentDiscountOnNewQuote() {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		
		TestData td_quote1 = getTestSpecificTD("TestData_1");
		TestData td_quote2 = getTestSpecificTD("TestData_2");
		TestData td_quote3 = getTestSpecificTD("TestData_3");
		TestData td_quote4 = getTestSpecificTD("TestData_4");
		TestData td_quote5 = getTestSpecificTD("TestData_5"); 
		TestData td_endorse = getTestSpecificTD("TestData_Endorsement");
		TestData td_GSDisNotApplied = getTestSpecificTD("TestData_GSDisNotApplied");
		TestData td_GSDisApplied = getTestSpecificTD("TestData_GSDisApplied");  
		
		mainApp().open();
		createCustomerIndividual();
		
		origPolicyNum = createPolicy();
		
		policyNum1 = createPolicyAndVerifyDiscount(td_quote1, td_GSDisApplied);    
		policyNum2 = createPolicyAndVerifyDiscount(td_quote2, td_GSDisApplied);
		policyNum3 = createPolicyAndVerifyDiscount(td_quote3, td_GSDisApplied);		
		policyNum4 = createPolicyAndVerifyDiscount(td_quote4, td_GSDisApplied);		
		policyNum5 = createPolicyAndVerifyDiscount(td_quote5, td_GSDisApplied);
		
		SearchPage.openPolicy(policyNum2);
		verifyDiscountOnEndorsement(td_endorse, td_GSDisNotApplied);
		
	}
	
	private String createPolicyAndVerifyDiscount(TestData td_quote, TestData td_ratingDetails) {
		SearchPage.openPolicy(origPolicyNum);
		
		policy.policyCopy().perform(td_quote);		
		policy.dataGather().start();
		
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get()); 
		new DriverTab().fillTab(td_quote); 
		new DriverTab().submitTab();
        new RatingDetailReportsTab().fillTab(td_quote).submitTab();
        
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.calculatePremium();
        softly.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).contains("Good Student Discount"); 
              
        PremiumAndCoveragesTab.buttonViewRatingDetails.click();
        softly.assertThat(new PremiumAndCoveragesTab().getRatingDetailsDriversData()).contains(td_ratingDetails); 
        PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
        new PremiumAndCoveragesTab().submitTab();

        new DriverActivityReportsTab().fillTab(td_quote).submitTab();
	    new DocumentsAndBindTab().fillTab(td_quote).submitTab();
        new PurchaseTab().fillTab(td_quote).submitTab();
        
        softly.assertThat(PolicySummaryPage.labelPolicyStatus.getValue()).isEqualTo(PolicyStatus.POLICY_ACTIVE);
        return PolicySummaryPage.labelPolicyNumber.getValue();
	}

	private void verifyDiscountOnEndorsement(TestData td, TestData td_ratingDetails) {
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get()); 
		new DriverTab().fillTab(td).submitTab(); 
		
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.calculatePremium();
        softly.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).doesNotContain("Good Student Discount"); 
        
        PremiumAndCoveragesTab.buttonViewRatingDetails.click();
        softly.assertThat(new PremiumAndCoveragesTab().getRatingDetailsDriversData()).contains(td_ratingDetails); 
        PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
        
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        new DocumentsAndBindTab().submitTab();
        
        softly.assertThat(PolicySummaryPage.labelPolicyStatus.getValue()).isEqualTo(PolicyStatus.POLICY_ACTIVE);
	}
	
	
}
