package aaa.modules.regression.sales.auto_ca.choice.functional;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.MembershipTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestGoodStudentDiscount extends AutoCaChoiceBaseTest {
	
	SoftAssertions softly = new SoftAssertions();
	protected TestData tdPolicy;
	private String origPolicyNum;
	private String policyNum1;
	private String policyNum2;
	private String policyNum3;
	private String policyNum4;
	private String policyNum5;
	
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT)
	public void TestValidationGoodStudentDiscount() {
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
		
		policyNum1 = createPolicyAndVerifyGoodStudentDiscount(td_quote1, td_GSDisApplied);
		log.info("TEST: Policy 1 is created with #" +policyNum1);		
		policyNum2 = createPolicyAndVerifyGoodStudentDiscount(td_quote2, td_GSDisApplied);
		log.info("TEST: Policy 2 is created with #" +policyNum2);		
		policyNum3 = createPolicyAndVerifyGoodStudentDiscount(td_quote3, td_GSDisApplied);
		log.info("TEST: Policy 3 is created with #" +policyNum3);		
		policyNum4 = createPolicyAndVerifyGoodStudentDiscount(td_quote4, td_GSDisApplied);
		log.info("TEST: Policy 4 is created with #" +policyNum4); 
		policyNum5 = createPolicyAndVerifyGoodStudentDiscount(td_quote5, td_GSDisApplied);
		log.info("TEST: Policy 5 is created with #" +policyNum5);
		
		verifyGoodStudentDiscountOnEndorsement(policyNum2, td_endorse, td_GSDisNotApplied); 

		verifyGoodStudentDiscountOnRenewal(policyNum1, td_GSDisApplied);
		verifyGoodStudentDiscountOnRenewal(policyNum3, td_GSDisApplied);
		verifyGoodStudentDiscountOnRenewal(policyNum4, td_GSDisApplied);
		verifyGoodStudentDiscountOnRenewal(policyNum5, td_GSDisApplied);
	}
	
	private String createPolicyAndVerifyGoodStudentDiscount(TestData td_quote, TestData td_ratingDetails) {
		SearchPage.openPolicy(origPolicyNum);
		
		policy.policyCopy().perform(td_quote);		
		policy.dataGather().start();
		
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get()); 
		new DriverTab().fillTab(td_quote); 
		new DriverTab().submitTab();
        new MembershipTab().fillTab(td_quote).submitTab();
        
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().fillTab(td_quote);
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
	
	private void verifyGoodStudentDiscountOnEndorsement(String policyNum, TestData td, TestData td_ratingDetails) {
		SearchPage.openPolicy(policyNum);
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));
		
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get()); 
		new DriverTab().fillTab(td).submitTab(); 
		
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.calculatePremium();
        softly.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).doesNotContain("Good Student Discount"); 
        
        PremiumAndCoveragesTab.buttonViewRatingDetails.click();
        softly.assertThat(new PremiumAndCoveragesTab().getRatingDetailsDriversData()).contains(td_ratingDetails); 
        PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
        
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
        new DocumentsAndBindTab().submitTab();
        
        softly.assertThat(PolicySummaryPage.labelPolicyStatus.getValue()).isEqualTo(PolicyStatus.POLICY_ACTIVE);
        log.info("TEST: Endorsement is created for policy with #" + policyNum2);
	}
	
	private void verifyGoodStudentDiscountOnRenewal(String policyNum, TestData td_ratingDetails) {
		SearchPage.openPolicy(policyNum);
		log.info("TEST: Verifying Good Student discount for renewal of " + policyNum);
		
		policy.renew().start();
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.calculatePremium();		
		softly.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).contains("Good Student Discount"); 		
        PremiumAndCoveragesTab.buttonViewRatingDetails.click();	        
        softly.assertThat(new PremiumAndCoveragesTab().getRatingDetailsDriversData()).contains(td_ratingDetails); 
        PremiumAndCoveragesTab.buttonRatingDetailsOk.click(); 
        new PremiumAndCoveragesTab().saveAndExit();
		
        softly.assertThat(PolicySummaryPage.labelPolicyStatus.getValue()).isEqualTo(PolicyStatus.POLICY_ACTIVE);
        softly.assertThat(PolicySummaryPage.buttonRenewals.isEnabled()).isTrue();
        PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PREMIUM_CALCULATED).verify(1);
		
		policy.policyInquiry().start();
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
		softly.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).contains("Good Student Discount"); 
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();	        
        softly.assertThat(new PremiumAndCoveragesTab().getRatingDetailsDriversData()).contains(td_ratingDetails); 
        PremiumAndCoveragesTab.buttonRatingDetailsOk.click(); 
		new PremiumAndCoveragesTab().cancel();	
	}
}
