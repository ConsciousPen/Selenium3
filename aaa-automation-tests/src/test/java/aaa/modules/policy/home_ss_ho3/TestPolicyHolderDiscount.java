package aaa.modules.policy.home_ss_ho3;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Olga Reva
 * @name Test Policy Holder discounts
 * @scenario
 * 1. Find customer or create new if customer does not exist.
 * 2. Initiate new HSS policy creation. 
 * 3. Fill all mandatory fields on all tabs, calculate premium. 
 * 4. Verify Young Home Owner discount:
 * 		4a. Navigate to Applicant tab and enter so Date of birth that insured age will be 30 years.
 * 		4b. Re-order reports.
 * 		4c. Navigate to Premiums&Coverages Quote tab and re-calculate premium. 
 * 		4d. Verify Young Home owner discount applying and displaying in Discounts section and in Rating Details. 
 * 5. Verify Mature HomeOwner discount:
 * 		5a. Navigate to Applicant tab and enter so Date of birth that insured age will be 55 years.
 * 		5b. Re-order reports.
 * 		5c. Navigate to Premiums&Coverages Quote tab and re-calculate premium. 
 * 		5d. Verify Mature Home owner discount applying and displaying in Discounts section and in Rating Details. 
 * 6. Verify no Policy Holder Category discounts apply:
 * 		6a. Navigate to Applicant tab and enter so Date of birth that insured age will be 50 years.
 * 		6b. Re-order reports.
 * 		6c. Navigate to Premiums&Coverages Quote tab and re-calculate premium. 
 * 		6d. Verify Young and Mature HomeOwner discounts are not apply. 
 * 7. Verify AAA Employee discount. 
 * 		7a. Navigate to Applicant tab and set AAA Employee to 'Yes'.
 * 		7b. Re-order reports.
 * 		7c. Navigate to Premiums&Coverages Quote tab and re-calculate premium. 
 * 		7d. Verify AAA Employee discount applying and displaying in Discounts section and in Rating Details. 
 * 8. Save&Exit quote.
 * 
 * @details
 */
public class TestPolicyHolderDiscount extends HomeSSHO3BaseTest {

	private TestData td_YoungHomeOwner = getStateTestData(tdPolicy, this.getClass().getSimpleName(), "TestData_YoungHomeOwner"); 
	private TestData td_NoPolicyHolderDiscount = getStateTestData(tdPolicy, this.getClass().getSimpleName(), "TestData_NoPolicyHolderDiscount"); 
	private TestData td_MatureHomeowner = getStateTestData(tdPolicy, this.getClass().getSimpleName(), "TestData_MatureHomeowner"); 
	private TestData td_EmployeeDiscount = getStateTestData(tdPolicy, this.getClass().getSimpleName(), "TestData_EmployeeDiscount"); 	
	
	@Test
    @TestInfo(component = "Policy.HomeSS")
	public void testPolicyHolderDiscount() {
		mainApp().open();

        //getCopiedQuote(); 
		createCustomerIndividual();
		
		createQuote();
        
        policy.dataGather().start();
        
        //Policyholder: Young Homeowner discount applies
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
        ApplicantTab applicantTab = new ApplicantTab(); 
        applicantTab.fillTab(td_YoungHomeOwner);
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        ReportsTab reportsTab = new ReportsTab();
        reportsTab.fillTab(td_YoungHomeOwner);
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        PremiumsAndCoveragesQuoteTab premiumsTab = new PremiumsAndCoveragesQuoteTab();
        premiumsTab.calculatePremium();
        
        CustomAssert.enableSoftMode();
        
        Map<String, String> youngHomeOwnerDiscount_dataRow = new HashMap<>();
        youngHomeOwnerDiscount_dataRow.put("Discount Category", "Policyholder");
        youngHomeOwnerDiscount_dataRow.put("Discounts Applied", "Young Homeowner");
        
        Map<String, String> matureHomeOwnerDiscount_dataRow = new HashMap<>();
        matureHomeOwnerDiscount_dataRow.put("Discount Category", "Policyholder");
        matureHomeOwnerDiscount_dataRow.put("Discounts Applied", "Mature Homeowner");
        
        Map<String, String> employeeDiscount_dataRow = new HashMap<>();
        employeeDiscount_dataRow.put("Discount Category", "Policyholder");
        employeeDiscount_dataRow.put("Discounts Applied", "AAA Employee");
        
        PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(youngHomeOwnerDiscount_dataRow).verify.present();
        
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
        CustomAssert.assertFalse("Incorrect value of Policy holder Discount Category in Rating Details", 
        		PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Policy holder Discount Category").equals("0.0")); 
 
        CustomAssert.assertFalse("Incorrect value of Young Homeowner discount in Rating Details", 
        		PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Young Homeowner discount").equals("0.0")); 
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();

        
        //Policyholder: Mature Homeowner discount applies
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get()); 
        applicantTab.fillTab(td_MatureHomeowner);
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        reportsTab.fillTab(td_MatureHomeowner); 
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        premiumsTab.calculatePremium();
        
        PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(matureHomeOwnerDiscount_dataRow).verify.present();
        
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
        CustomAssert.assertFalse("Incorrect value of Policy holder Discount Category in Rating Details", 
        		PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Policy holder Discount Category").equals("0.0")); 
 
        CustomAssert.assertFalse("Incorrect value of Mature Homeowner discount in Rating Details", 
        		PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Mature Homeowner Discount").equals("0.0")); 
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
        
        
        //Policyholder: No discounts apply
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get()); 
        applicantTab.fillTab(td_NoPolicyHolderDiscount);
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        reportsTab.fillTab(td_NoPolicyHolderDiscount); 
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        premiumsTab.calculatePremium();
        
        PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(youngHomeOwnerDiscount_dataRow).verify.present(false);
        PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(matureHomeOwnerDiscount_dataRow).verify.present(false);
        
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
        CustomAssert.assertTrue("Incorrect value of Policy holder Discount Category in Rating Details", 
        		PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Policy holder Discount Category").equals("0.0%")); 
 
        //CustomAssert.assertTrue("Incorrect value of Mature Homeowner discount in Rating Details", 
        //		PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Mature Homeowner Discount").equals("0.0")); 
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
        
        //Policyholder: AAA Employee discount applies
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get()); 
        applicantTab.fillTab(td_EmployeeDiscount);
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        premiumsTab.calculatePremium();
        
        PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(employeeDiscount_dataRow).verify.present();
        
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
        CustomAssert.assertTrue("Incorrect value of 'Employee' in Rating Details", 
        		PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Employee").equals("Yes")); 
 
        CustomAssert.assertFalse("Incorrect value of Employee discount in Rating Details", 
        		PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Employee discount").equals("0.0")); 
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
        
        PremiumsAndCoveragesQuoteTab.buttonSaveAndExit.click();
        log.info("TEST Policy Holder Discount: HSS Quote created with #" + PolicySummaryPage.labelPolicyNumber.getValue());
        
        CustomAssert.assertAll();    
        
	}
}
