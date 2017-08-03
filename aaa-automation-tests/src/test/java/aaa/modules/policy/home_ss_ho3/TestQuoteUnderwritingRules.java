package aaa.modules.policy.home_ss_ho3;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.DocumentsTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.home_ss.defaulttabs.UnderwritingAndApprovalTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Olga Reva
 * @name Test Quote Underwriting rules
 * @scenario
 * 1. Create new or open existed customer.
 * 2. Initiate HSS quote creation.
 * 3. Fill all mandatory fields on all tabs, order reports, calculate premium. 
 * 4. Navigate to Underwriting And Approval tab.
 * 5. Fill tab according to "TestData_UW1": 
 * 		Set Yes to "Have any of the applicant(s)’ current pets injured, intentionally or unintentionally, another creature or person?";
 * 		Set Yes to "Do employees of any resident or applicant reside in the dwelling?" 
 * 			and enter "Total number of part time and full time resident employees" = 2; 
 * 		Set Yes to "Is any business, home day care, or farming activity conducted on the premises for which an endorsement is not already attached to the policy?".
 * 6. Press Continue and verify error messages displaying on tab: 
 * 		"Applicants/insureds with any dogs or other animals, reptiles, or pets with any prior biting 
 * 			history are unacceptable. Underwriting review will occur post bind.";  
 *      "Risk must be endorsed with the appropriate business or farming endorsement when an eligible business or incidental 
 *      	farming exposure is present. Applicants that perform a home day care, including child, adult or pet day care, are unacceptable.";     
 * 7. Fill tab according to "TestData_UW2": 
 * 		Set No to "Have any of the applicant(s)’ current pets injured, intentionally or unintentionally, another creature or person?"
 * 			and "Is any business, home day care, or farming activity conducted on the premises for which an endorsement is not already attached to the policy?"; 
 * 		Set Yes to all other questions; 
 * 		Change "Total number of part time and full time resident employees" to 3. 
 * 8. Press Continue button and verify that all Remarks fields are displaying error messages. 
 * 9. Fill tab according to "TestData_UW3": 
 * 		Fill all 'Remark' fields.
 * 10. Navigate to Bind tab and click Purchase button. 
 * 11. Verify Error tab is opened with errors messages: 
 * 		"Risks with more than 2 resident employees are ineligible."
 * 		"Applicants who have been cancelled, refused insurance or non-renewed in the p..." 
 * 		"Dwelling must not have been in foreclosure within the past 18 months unless a..."
 * 12. Navigate to Underwriting And Approval tab and fill tab with correct values (TestData_UW4). 
 * 13. Purchase policy.
 * 14. Verify policy status is Active on Consolidated policy view.
 * @details
 */
public class TestQuoteUnderwritingRules extends HomeSSHO3BaseTest {

	private TestData td = getPolicyTD("DataGather", "TestData");
	private TestData td_uw1 = getTestSpecificTD("TestData_UW1");
	private TestData td_uw2 = getTestSpecificTD("TestData_UW2");
	private TestData td_uw3 = getTestSpecificTD("TestData_UW3");
	private TestData td_uw4 = getTestSpecificTD("TestData_UW4");
	
	@Test
    @TestInfo(component = "Quote.HomeSS")
	public void testQuoteUnderwritingRules() {
		mainApp().open();
        
        createCustomerIndividual();

        policy.initiate();
        policy.getDefaultView().fillUpTo(td, UnderwritingAndApprovalTab.class, false);
        
        UnderwritingAndApprovalTab underwritingTab = new UnderwritingAndApprovalTab();
        underwritingTab.fillTab(td_uw1);
        underwritingTab.submitTab();
        
        CustomAssert.enableSoftMode();   
        underwritingTab.verifyFieldHasMessage(HomeSSMetaData.UnderwritingAndApprovalTab.HAVE_ANY_OF_THE_APPLICANT_S_CURRENT_PETS_INJURED_ANOTHER_PERSON.getLabel(), 
        		"Applicants/insureds with any dogs or other animals, reptiles, or pets with any prior biting history are unacceptable. Underwriting review will occur post bind.");  
        
        underwritingTab.verifyFieldHasMessage(HomeSSMetaData.UnderwritingAndApprovalTab.IS_ANY_BUSINESS_CONDUCTED_ON_THE_PREMISES_FOR_WHICH_AN_ENDORSEMENT_IS_NOT_ATTACHED_TO_THE_POLICY.getLabel(), 
        		"Risk must be endorsed with the appropriate business or farming endorsement when an eligible business or incidental farming exposure is present. Applicants that perform a home day care, including child, adult or pet day care, are unacceptable.");             
        
        underwritingTab.fillTab(td_uw2);
        underwritingTab.submitTab();       
        underwritingTab.verifyFieldHasMessage("Remark Prior Insurance", "'Remarks' is required");
        underwritingTab.verifyFieldHasMessage("Remark Foreclosure", "'Remarks' is required"); 
        underwritingTab.verifyFieldHasMessage("Remark Resident Employees", "'Remarks' is required");
        
        underwritingTab.fillTab(td_uw3);
        underwritingTab.submitTab();
        
        policy.getDefaultView().fillFromTo(td, DocumentsTab.class, BindTab.class, true);
        BindTab bindTab = new BindTab();
        bindTab.btnPurchase.click();
        
        Map<String, String> err1_dataRow = new HashMap<>();
        err1_dataRow.put("Severity", "Error");
        err1_dataRow.put("Message", "Risks with more than 2 resident employees are ineligible.");
        
        Map<String, String> err2_dataRow = new HashMap<>();
        err2_dataRow.put("Severity", "Error");
        err2_dataRow.put("Message", "Applicants who have been cancelled, refused insurance or non-renewed in the p...");
        
        Map<String, String> err3_dataRow = new HashMap<>();
        err3_dataRow.put("Severity", "Error");
        err3_dataRow.put("Message", "Dwelling must not have been in foreclosure within the past 18 months unless a...");
        
        ErrorTab errorTab = new ErrorTab();
        
        errorTab.tblErrorsList.getRowContains(err1_dataRow).verify.present();
        errorTab.tblErrorsList.getRowContains(err2_dataRow).verify.present();
        errorTab.tblErrorsList.getRowContains(err3_dataRow).verify.present();
        errorTab.cancel();
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.UNDERWRITING_AND_APPROVAL.get());
        underwritingTab.fillTab(td_uw4);
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
        policy.getDefaultView().fillFromTo(td, BindTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();
        
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);    
        log.info("TEST Underwriting rules: HSS Policy created with #" + PolicySummaryPage.labelPolicyNumber.getValue());
        
        CustomAssert.assertAll();    
	}
}
