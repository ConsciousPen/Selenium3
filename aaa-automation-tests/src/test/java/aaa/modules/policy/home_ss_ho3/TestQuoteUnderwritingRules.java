package aaa.modules.policy.home_ss_ho3;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.ProductConstants;
//import aaa.main.metadata.policy.HomeSSMetaData;
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

public class TestQuoteUnderwritingRules extends HomeSSHO3BaseTest {

	private TestData td = getStateTestData(tdPolicy, "DataGather", "TestData");
	private TestData td_uw1 = getStateTestData(tdPolicy, this.getClass().getSimpleName(), "TestData_UW1");
	private TestData td_uw2 = getStateTestData(tdPolicy, this.getClass().getSimpleName(), "TestData_UW2");
	private TestData td_uw3 = getStateTestData(tdPolicy, this.getClass().getSimpleName(), "TestData_UW3");
	private TestData td_uw4 = getStateTestData(tdPolicy, this.getClass().getSimpleName(), "TestData_UW4");
	
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
        //underwritingTab.verifyFieldHasMessage(HomeSSMetaData.UnderwritingAndApprovalTab.HAVE_ANY_OF_THE_APPLICANT_S_CURRENT_PETS_INJURED_ANOTHER_PERSON.getLabel(), 
        //		"Applicants/insureds with any dogs or other animals, reptiles, or pets with any prior biting history are unacceptable. Underwriting review will occur post bind.");  
        
        //underwritingTab.verifyFieldHasMessage(HomeSSMetaData.UnderwritingAndApprovalTab.IS_ANY_BUSINESS_CONDUCTED_ON_THE_PREMISES_FOR_WHICH_AN_ENDORSEMENT_IS_NOT_ATTACHED_TO_THE_POLICY.getLabel(), 
        //		"Risk must be endorsed with the appropriate business or farming endorsement when an eligible business or incidental farming exposure is present. Applicants that perform a home day care, including child, adult or pet day care, are unacceptable.");             
        
        underwritingTab.fillTab(td_uw2);
        underwritingTab.submitTab();       
        //underwritingTab.verifyFieldHasMessage("Remark Prior Insurance", "'Remarks' is required");
        //underwritingTab.verifyFieldHasMessage("Remark Foreclosure", "'Remarks' is required"); 
        //underwritingTab.verifyFieldHasMessage("Remark Resident Employees", "'Remarks' is required");
        
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
