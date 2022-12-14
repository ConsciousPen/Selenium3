package aaa.modules.regression.sales.home_ss.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomSoftAssertions;

/**
 * @author Olga Reva
 * <b> Test Quote Underwriting rules </b>
 * <p> Steps:
 * <p> 1. Create new or open existed customer.
 * <p> 2. Initiate HSS quote creation.
 * <p> 3. Fill all mandatory fields on all tabs, order reports, calculate premium.
 * <p> 4. Navigate to Underwriting And Approval tab.
 * <p> 5. Fill tab according to "TestData_UW1":
 * <p> 		Set Yes to "Have any of the applicant(s)' current pets injured, intentionally or unintentionally, another creature or person?";
 * <p> 		Set Yes to "Do employees of any resident or applicant reside in the dwelling?"
 * <p> 			and enter "Total number of part time and full time resident employees" = 2;
 * <p> 		Set Yes to "Is any business, home day care, or farming activity conducted on the premises for which an endorsement is not already attached to the policy?".
 * <p> 6. Press Continue and verify error messages displaying on tab:
 * <p> 		"Applicants/insureds with any dogs or other animals, reptiles, or pets with any prior biting
 * <p> 			history are unacceptable. Underwriting review will occur post bind.";
 * <p>      "Risk must be endorsed with the appropriate business or farming endorsement when an eligible business or incidental
 * <p>      	farming exposure is present. Applicants that perform a home day care, including child, adult or pet day care, are unacceptable.";
 * <p> 7. Fill tab according to "TestData_UW2":
 * <p> 		Set No to "Have any of the applicant(s)' current pets injured, intentionally or unintentionally, another creature or person?"
 * <p> 			and "Is any business, home day care, or farming activity conducted on the premises for which an endorsement is not already attached to the policy?";
 * <p> 		Set Yes to all other questions;
 * <p> 		Change "Total number of part time and full time resident employees" to 3.
 * <p> 8. Press Continue button and verify that all Remarks fields are displaying error messages.
 * <p> 9. Fill tab according to "TestData_UW3":
 * <p> 		Fill all 'Remark' fields.
 * <p> 10. Navigate to Bind tab and click Purchase button.
 * <p> 11. Verify Error tab is opened with errors messages:
 * <p> 		"Risks with more than 2 resident employees are ineligible."
 * <p> 		"Applicants who have been cancelled, refused insurance or non-renewed in the p..."
 * <p> 		"Dwelling must not have been in foreclosure within the past 18 months unless a..."
 * <p> 12. Navigate to Underwriting And Approval tab and fill tab with correct values (TestData_UW4).
 * <p> 13. Purchase policy.
 * <p> 14. Verify policy status is Active on Consolidated policy view.
 *
 */
public class TestQuoteUnderwritingRules extends HomeSSHO3BaseTest {

	@Parameters({"state"})
	@StateList(statesExcept = {States.CA})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testQuoteUnderwritingRules(@Optional("") String state) {
		mainApp().open();

		TestData td = getPolicyTD("DataGather", "TestData");
		TestData td_uw1 = getTestSpecificTD("TestData_UW1");
		TestData td_uw2 = getTestSpecificTD("TestData_UW2");
		TestData td_uw3 = getTestSpecificTD("TestData_UW3");
		TestData td_uw4 = getTestSpecificTD("TestData_UW4");

		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(td, UnderwritingAndApprovalTab.class, false);

		UnderwritingAndApprovalTab underwritingTab = new UnderwritingAndApprovalTab();
		underwritingTab.fillTab(td_uw1);
		underwritingTab.submitTab();

		CustomSoftAssertions.assertSoftly(softly -> {
			softly.assertThat(underwritingTab.getAssetList()
					.getAsset(HomeSSMetaData.UnderwritingAndApprovalTab.HAVE_ANY_OF_THE_APPLICANT_S_CURRENT_PETS_INJURED_ANOTHER_PERSON)
					.getWarning().toString())
					.contains("Applicants/insureds with any dogs or other animals, reptiles, or pets with any prior biting history are unacceptable. Underwriting review will occur post bind.");
			if (getState().equals("KY")) {
				softly.assertThat(underwritingTab.getAssetList()
						.getAsset(HomeSSMetaData.UnderwritingAndApprovalTab.IS_ANY_BUSINESS_OR_FARMING_ACTIVITY_CONDUCTED_ON_THE_PREMISES_FOR_WHICH_AN_ENDORSEMENT_IS_NOT_ALREADY_ATTACHED_TO_THE_POLICY)
						.getWarning().toString())
						.contains("Risk must be endorsed with the appropriate business or farming endorsement when an eligible business or incidental farming exposure is present.");
			} else if (getState().equals("MD")) {
				softly.assertThat(underwritingTab.getAssetList()
						.getAsset(HomeSSMetaData.UnderwritingAndApprovalTab.IS_ANY_BUSINESS_OR_FARMING_ACTIVITY_CONDUCTED_ON_THE_PREMISES)
						.getWarning().toString())
						.contains("Business or farming activity is ineligible");
			} else if (getState().equals("OR")) {
				softly.assertThat(underwritingTab.getAssetList()
						.getAsset(HomeSSMetaData.UnderwritingAndApprovalTab.IS_ANY_BUSINESS__ADULT_DAY_CARE_OR_FARMING_ACTIVITY_CONDUCTED_ON_THE_PREMISES)
						.getWarning().toString())
						.contains("Risk must be endorsed with the appropriate business or farming endorsement when a business or incidental farming exposure is present and deemed eligible for coverage. Applicants that perform adult day care, or pet day care, are unacceptable");
			} else {
				softly.assertThat(underwritingTab.getAssetList()
						.getAsset(HomeSSMetaData.UnderwritingAndApprovalTab.IS_ANY_BUSINESS_CONDUCTED_ON_THE_PREMISES_FOR_WHICH_AN_ENDORSEMENT_IS_NOT_ATTACHED_TO_THE_POLICY)
						.getWarning().toString())
						.contains("Risk must be endorsed with the appropriate business or farming endorsement when an eligible business or incidental farming exposure is present. Applicants that perform a home day care, including child, adult or pet day care, are unacceptable.");
			}

			underwritingTab.fillTab(td_uw2);
			underwritingTab.submitTab();
			if (getState().equals("CT")) {
				softly.assertThat(underwritingTab.getAssetList().getAsset(HomeSSMetaData.UnderwritingAndApprovalTab.REMARK_RESIDENT_EMPLOYEES)
						.getWarning().toString()).contains("'Remarks' is required");
			} else if (getState().equals("KY")) {
				softly.assertThat(underwritingTab.getAssetList().getAsset(HomeSSMetaData.UnderwritingAndApprovalTab.REMARK_FORECLOSURE)
						.getWarning().toString()).contains("'Remarks' is required");
				softly.assertThat(underwritingTab.getAssetList().getAsset(HomeSSMetaData.UnderwritingAndApprovalTab.REMARK_RESIDENT_EMPLOYEES)
						.getWarning().toString()).contains("'Remarks' is required");
			} else if (getState().equals("MD")) {
				softly.assertThat(underwritingTab.getAssetList().getAsset(HomeSSMetaData.UnderwritingAndApprovalTab.REMARK_PRIOR_INSURANCE_MD)
						.getWarning().toString()).contains("'Remarks' is required");
				softly.assertThat(underwritingTab.getAssetList().getAsset(HomeSSMetaData.UnderwritingAndApprovalTab.REMARK_RESIDENT_EMPLOYEES)
						.getWarning()).contains("'Remarks' is required");
				softly.assertThat(underwritingTab.getAssetList().getAsset(HomeSSMetaData.UnderwritingAndApprovalTab.REMARK_RESIDENT_EMPLOYEES)
						.getWarning().toString()).contains("'Remarks' is required");
			} else {
				softly.assertThat(underwritingTab.getAssetList().getAsset(HomeSSMetaData.UnderwritingAndApprovalTab.REMARK_PRIOR_INSURANCE)
						.getWarning().toString()).contains("'Remarks' is required");
				softly.assertThat(underwritingTab.getAssetList().getAsset(HomeSSMetaData.UnderwritingAndApprovalTab.REMARK_FORECLOSURE)
						.getWarning().toString()).contains("'Remarks' is required");
				softly.assertThat(underwritingTab.getAssetList().getAsset(HomeSSMetaData.UnderwritingAndApprovalTab.REMARK_RESIDENT_EMPLOYEES)
						.getWarning().toString()).contains("'Remarks' is required");
			}

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
			err2_dataRow.put("Message", "Applicants who have been cancelled, refused insurance or non-renewed in the past 3 years are unacceptable unless approved by underwriting.");
			
			Map<String, String> err2MD_dataRow = new HashMap<>();
			err2MD_dataRow.put("Severity", "Error");
			err2MD_dataRow.put("Message", "Applicants who have been cancelled, refused insurance or non-renewed in the past 3 years due to Material Misrepresentation and Substantial Increase in Hazard are unacceptable.");

			Map<String, String> err3_dataRow = new HashMap<>();
			err3_dataRow.put("Severity", "Error");
			err3_dataRow.put("Message", "Dwelling must not have been in foreclosure within the past 18 months unless approved by underwriting.");

			ErrorTab errorTab = new ErrorTab();

			switch (getState()) {
				case "CT":
					//TODO-dchubkov: replace with errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_XXXXXX);
					softly.assertThat(errorTab.getErrorsControl().getTable().getRowContains(err1_dataRow)).exists();
					break;
				case "KY":
					//TODO-dchubkov: replace with errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_XXXXXX);
					softly.assertThat(errorTab.getErrorsControl().getTable().getRowContains(err1_dataRow)).exists();
					softly.assertThat(errorTab.getErrorsControl().getTable().getRowContains(err3_dataRow)).exists();
					break;
				case "MD":
					//TODO check delta US
					softly.assertThat(errorTab.getErrorsControl().getTable().getRowContains(err2MD_dataRow)).exists();
					softly.assertThat(errorTab.getErrorsControl().getTable().getRowContains(err3_dataRow)).exists();
					break;
				case "PA":
					//TODO check delta US
					softly.assertThat(errorTab.getErrorsControl().getTable().getRowContains(err2_dataRow)).exists();
					softly.assertThat(errorTab.getErrorsControl().getTable().getRowContains(err3_dataRow)).exists();
					break;
				default:
					//TODO-dchubkov: replace with errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_XXXXXX);
					assertThat(errorTab.getErrorsControl().getTable().getRowContains(err1_dataRow)).exists();
					assertThat(errorTab.getErrorsControl().getTable().getRowContains(err2_dataRow)).exists();
					assertThat(errorTab.getErrorsControl().getTable().getRowContains(err3_dataRow)).exists();
			}
			errorTab.cancel();

			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.UNDERWRITING_AND_APPROVAL.get());
			underwritingTab.fillTab(td_uw4);

			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
			policy.getDefaultView().fillFromTo(td, BindTab.class, PurchaseTab.class, true);
			new PurchaseTab().submitTab();

			softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
			log.info("TEST Underwriting rules: HSS Policy created with #" + PolicySummaryPage.labelPolicyNumber.getValue());
		});
	}
}
