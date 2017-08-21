package aaa.modules.regression.sales.pup;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.ErrorPage;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.pup.defaulttabs.BindTab;
import aaa.main.modules.policy.pup.defaulttabs.DocumentsTab;
import aaa.main.modules.policy.pup.defaulttabs.ErrorTab;
import aaa.main.modules.policy.pup.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderwritingAndApprovalTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;

/**
 * @author Lina Li
 * @name Test Quote Underwriting rules
 * @scenario
 * 1. Create new or open existed customer.
 * 2. Initiate PUP quote creation.
 * 3. Fill all mandatory fields on all tabs, order reports, calculate premium. 
 * 4. Navigate to Underwriting And Approval tab.
 * 5. Fill tab according to "TestData_UW1",
 * 6. Press Continue and verify error messages displaying on tab:   
 * 7. Fill tab according to "TestData_UW2",
 * 8. Press Continue button and verify that all Remarks fields are displaying error messages. 
 * 9. Fill tab according to "TestData_UW3",
 * 10. Navigate to Bind tab and click Purchase button. 
 * 11. Verify Error tab is opened with errors messages: 
 * 12. Navigate to Underwriting And Approval tab and fill tab with correct values (TestData_UW4). 
 * 13. Purchase policy.
 * 14. Verify policy status is Active on Consolidated policy view.
 * @details
 */

public class TestQuoteUnderwritingRules extends PersonalUmbrellaBaseTest{
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.PUP)
	public void testQuoteUnderwritingRules(){
		mainApp().open();
		createCustomerIndividual();
		
		TestData td = getPolicyTD("DataGather", "TestData");
		td = adjustWithRealPolicies(td, getPrimaryPoliciesForPup());
		TestData td_uw1 = getTestSpecificTD("TestData_UW1");
		TestData td_uw2 = getTestSpecificTD("TestData_UW2");
		TestData td_uw3 = getTestSpecificTD("TestData_UW3");
		TestData td_uw4 = getTestSpecificTD("TestData_UW4");	
		
		policy.initiate();
		policy.getDefaultView().fillUpTo(td, UnderwritingAndApprovalTab.class, false);
		
		
//		Enter td_uw1 and verify the wringing message
		policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).fillTab(td_uw1);
		policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).submitTab();
        
        if (getState().equals("MD")){
        	policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).verifyFieldHasMessage("Is any business or farming activity conducted on the premises?","Business or farming activity is ineligible");
        	ErrorPage.provideLabelErrorMessage("Applicants who have been sued for libel or slander are ineligible.").verify.present();
            ErrorPage.provideLabelErrorMessage("Applicants/insureds with any dogs or other animals, reptiles, or pets with any prior biting history are unacceptable.").verify.present();
            ErrorPage.provideLabelErrorMessage("Applicants without underlying bodily injury and property damage liability coverage are ineligible.").verify.present();
            ErrorPage.provideLabelErrorMessage("Applicants who use their personal vehicles for wholesale or retail delivery are ineligible.").verify.present();
            ErrorPage.provideLabelErrorMessage("Business or farming activity is ineligible").verify.present();
        	
        }
        else if(getState().equals("OR")){
        	policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).verifyFieldHasMessage("Is any business, adult day care, pet day care or farming activity conducted on the premises?","Adult day care, or pet day care are not eligible.");
        	ErrorPage.provideLabelErrorMessage("Applicants who have been sued for libel or slander are ineligible.").verify.present();
            ErrorPage.provideLabelErrorMessage("Applicants/insureds with any dogs or other animals, reptiles, or pets with any prior biting history are unacceptable.").verify.present();
            ErrorPage.provideLabelErrorMessage("Applicants without underlying bodily injury and property damage liability coverage are ineligible.").verify.present();
            ErrorPage.provideLabelErrorMessage("Applicants who use their personal vehicles for wholesale or retail delivery are ineligible.").verify.present();
            ErrorPage.provideLabelErrorMessage("Adult day care, or pet day care are not eligible.").verify.present();
        }
        else if (getState().equals("CA")){
        	policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).verifyFieldHasMessage("Has the applicant been sued for libel or slander?","Applicants who have been sued for libel or slander are ineligible.");
        	policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).verifyFieldHasMessage("Do you have a license?","Dwellings or applicants that perform a home day care, including child day care, adult day care, or pet day care, are unacceptable unless they are licensed and insured.");
        	policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).verifyFieldHasMessage("Is it a for-profit business?","Farming/Ranching on premises is unacceptable unless it is incidental and not for profit.");
        	policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).verifyFieldHasMessage("Others","Other business exposures on premises are unacceptable.");
        	policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).verifyFieldHasMessage("Have any of the applicant(s)' current pets injured, intentionally or unintentionally, another creature or person?","Animals with any prior bite history are not acceptable.");
        	policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).verifyFieldHasMessage("Are there any owned, leased or rented watercraft, recreational vehicles, motorcycles or automobiles without liability coverage?","Applicants without underlying bodily injury and property damage liability coverage are ineligible.");
        	policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).verifyFieldHasMessage("Do any applicants or drivers use their personal vehicles for wholesale or retail delivery of cargo or persons?","Applicants who use their personal vehicles for wholesale or retail delivery are ineligible.");
        }
        else{
        	ErrorPage.provideLabelErrorMessage("Applicants who have been sued for libel or slander are ineligible.").verify.present();
            ErrorPage.provideLabelErrorMessage("Risk must be endorsed with the appropriate business or farming endorsement when an eligible business or incidental farming exposure is present. Applicants that perform a home day care, including child, adult or pet day care, are unacceptable.").verify.present();
            ErrorPage.provideLabelErrorMessage("Applicants/insureds with any dogs or other animals, reptiles, or pets with any prior biting history are unacceptable.").verify.present();
            ErrorPage.provideLabelErrorMessage("Applicants without underlying bodily injury and property damage liability coverage are ineligible.").verify.present();
            ErrorPage.provideLabelErrorMessage("Applicants who use their personal vehicles for wholesale or retail delivery are ineligible.").verify.present();
        }
           
//		Enter td_uw2 and verify the mandatory fields for Remark
        policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).fillTab(td_uw2);
        policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).submitTab(); 
        
        if (getState().equals("CT")||getState().equals("KY")){
        	policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).verifyFieldHasMessage("Remark Property Outside US", "'Remarks' is required");
        	policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).verifyFieldHasMessage("Remark Vehicles not for personal/pleasure use", "'Remarks' is required");
        	policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).verifyFieldHasMessage("Remark Commercial Vehicle", "'Remarks' is required");
        	policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).verifyFieldHasMessage("Remark Celebrity", "'Remarks' is required");
        }
        else if(getState().equals("MD")){
        	policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).verifyFieldHasMessage("Remark Cancelled Policy Extn", "'Remarks' is required");
        	policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).verifyFieldHasMessage("Remark Property Outside US", "'Remarks' is required");
        	policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).verifyFieldHasMessage("Remark Vehicles not for personal/pleasure use", "'Remarks' is required");
        	policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).verifyFieldHasMessage("Remark Commercial Vehicle", "'Remarks' is required");
        	policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).verifyFieldHasMessage("Remark Celebrity", "'Remarks' is required");
        }
        else if(getState().equals("CA")){
        	policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).verifyFieldHasMessage("Remark Cancelled Policy", "'Remarks' is required");
        	policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).verifyFieldHasMessage("Remark Property Outside US", "'Remarks' is required");
        	policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).verifyFieldHasMessage("Remark Vehicles not for personal/pleasure use", "'Remarks' is required");
        	policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).verifyFieldHasMessage("Remark Commercial Vehicle", "'Remarks' is required");	
        }
        else{
        	policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).verifyFieldHasMessage("Remark Cancelled Policy", "'Remarks' is required");
        	policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).verifyFieldHasMessage("Remark Property Outside US", "'Remarks' is required");
        	policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).verifyFieldHasMessage("Remark Vehicles not for personal/pleasure use", "'Remarks' is required");
        	policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).verifyFieldHasMessage("Remark Commercial Vehicle", "'Remarks' is required");
        	policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).verifyFieldHasMessage("Remark Celebrity", "'Remarks' is required");
        }
        
//		Enter td_uw3 for Remark fields and verify override messages
        policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).fillTab(td_uw3);
        policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).submitTab();
        policy.getDefaultView().fillFromTo(td, DocumentsTab.class, BindTab.class, true);
        policy.getDefaultView().getTab(BindTab.class).btnPurchase.click();
        
        Map<String, String> err1_dataRow = new HashMap<>();
        err1_dataRow.put("Severity", "Error");
        err1_dataRow.put("Message", "UW approval is required to bind the policy if any applicants or insureds are ...");
        
        Map<String, String> err2_dataRow = new HashMap<>();
        err2_dataRow.put("Severity", "Error");
        err2_dataRow.put("Message", "Applicants who own property, or reside for extended periods, outside of the U...");
        
        Map<String, String> err3_dataRow = new HashMap<>();
        err3_dataRow.put("Severity", "Error");
        err3_dataRow.put("Message", "Vehicles used for business, promotional or racing are ineligible.");
        
        Map<String, String> err4_dataRow = new HashMap<>();
        err4_dataRow.put("Severity", "Error");
        err4_dataRow.put("Message", "Applicants who have been cancelled, refused insurance or non-renewed in the p...");
        
        Map<String, String> err5_dataRow = new HashMap<>();
        err5_dataRow.put("Severity", "Error");
        err5_dataRow.put("Message", "Residents holding one of the special occupations or holder of any elected or ...");
        
        
        if(getState().equals("CT")||getState().equals("KY")){
        	policy.getDefaultView().getTab(ErrorTab.class).errorsList.getTable().getRowContains(err1_dataRow).verify.present();
        	policy.getDefaultView().getTab(ErrorTab.class).errorsList.getTable().getRowContains(err2_dataRow).verify.present();
        	policy.getDefaultView().getTab(ErrorTab.class).errorsList.getTable().getRowContains(err3_dataRow).verify.present();
        }
        else if (getState().equals("CA")){
        	policy.getDefaultView().getTab(ErrorTab.class).errorsList.getTable().getRowContains(err5_dataRow).verify.present();
        	policy.getDefaultView().getTab(ErrorTab.class).errorsList.getTable().getRowContains(err2_dataRow).verify.present();
        	policy.getDefaultView().getTab(ErrorTab.class).errorsList.getTable().getRowContains(err3_dataRow).verify.present();
        	policy.getDefaultView().getTab(ErrorTab.class).errorsList.getTable().getRowContains(err4_dataRow).verify.present();       	
        }
        else{
        	policy.getDefaultView().getTab(ErrorTab.class).errorsList.getTable().getRowContains(err1_dataRow).verify.present();
        	policy.getDefaultView().getTab(ErrorTab.class).errorsList.getTable().getRowContains(err2_dataRow).verify.present();
        	policy.getDefaultView().getTab(ErrorTab.class).errorsList.getTable().getRowContains(err3_dataRow).verify.present();
        	policy.getDefaultView().getTab(ErrorTab.class).errorsList.getTable().getRowContains(err4_dataRow).verify.present();
        }

        policy.getDefaultView().getTab(ErrorTab.class).cancel();
//		Enter td_uw4 to change all UW questions as No
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.UNDERWRITING_AND_APPROVAL.get());
        policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).fillTab(td_uw4);
        policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).submitTab();
        
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.BIND.get());
        policy.getDefaultView().fillFromTo(td, BindTab.class, PurchaseTab.class, true);
        policy.getDefaultView().getTab(PurchaseTab.class).submitTab();
        
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);    
        log.info("TEST Underwriting rules: PUP Policy created with #" + PolicySummaryPage.labelPolicyNumber.getValue());
        

	}
}
