package aaa.modules.regression.sales.home_ca.ho3.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.DocGenEnum;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.DocumentsTab;
import aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.modules.regression.sales.template.functional.TestFAIRPlanEndorsementTemplate;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestFAIRPlanEndorsement extends HomeCaHO3BaseTest {

	private String formIdInXml = DocGenEnum.Documents.FPCECA.getIdInXml();
	private String fairPlanEndorsementLabelInEndorsementTab = HomeCaMetaData.EndorsementTab.FPCECA.getLabel();
	private DocGenEnum.Documents fairPlanEndorsementInODDTab = DocGenEnum.Documents.FPCECA;

	private TestFAIRPlanEndorsementTemplate testFAIRPlanEndorsementTemplate =
			new TestFAIRPlanEndorsementTemplate(getPolicyType(), formIdInXml, fairPlanEndorsementLabelInEndorsementTab, fairPlanEndorsementInODDTab);

	///AC#1, AC#4

	/**
	 * @author Maris Strazds
	 * @name Test Membership Override - PAS-13211 AC#1, AC4 (New Business)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Initiate CA HO3 Quote.
	 * 3. Navigate to Property info tab
	 * 4. Select PPC 10
	 * 5. Select construction type as other than 'Log Home' - (Masonry)
	 * 6. Fill all mandatory details and try to bind policy ----> Error ERROR_AAA_HO_CA02122017 should be displayed
	 * 7. Navigate back to Endorsement Tab and Add FAIR Plan Endorsement
	 * 8. Rate the quote, navigate to Bind tab and click on bind
	 * 9. Validate that UW rule ERROR_AAA_HO_CA02122017 IS NOT fired and policy is bound
	 *      AC#4
	 * 10. Run renewal Part1 and Part 2 jobs at R-67 (Renewal UW rules validation time point), open Policy Consolidated view and click on 'Tasks'
	 * 11. Validate that task is not generated (Exact name of task is Unknown, hence looking for Task Name containing "PPC 10")
	 * @details
	 **/
	@Parameters({"state"})
	@Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-12925 FAIR Plan Endorsement (formerly known as Difference in Conditions)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-13211")

	public void pas13211_AC1_NB_PPC10_OtherThanLogHome_AAA_HO_CA02122017(@Optional("") String state) {
		testFAIRPlanEndorsementTemplate.pas13211_AC1_AC4_NB_PPC10_OtherThanLogHome_AAA_HO_CA02122017();
	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership Override - PAS-13211 AC#1, AC4 (Renewal)
	 * @scenario
	 * Note: negative scenario (scenario where the UW rule is fired at renewal if FAIR PLan Endorsement is added)
	 * 1. Create Customer.
	 * 2. Create CA HO3 Policy
	 *      PPC other than 10,
	 *      not a 'Log Home'.
	 * 3. Retrieve Renewal Image in data gathering
	 * 3. Navigate to Property info tab
	 * 4. Change Dwelling address so that PPC reports PPC 10 (order PPC report)
	 * 6. Fill all mandatory details and try to bind policy ----> Error ERROR_AAA_HO_CA02122017 should NOT be displayed  (Existing behavior on Master)
	 * NOTE: Negative scenario where the UW rule is fired if FAIR Plan Endorsement is not added is not possible, because as per current implementation it is not fired for renewals
	 * @details
	 **/
	@Parameters({"state"})
	@Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-12925 FAIR Plan Endorsement (formerly known as Difference in Conditions)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-13211")

	public void pas13211_AC1_Renewal_PPC10_OtherThanLogHome_AAA_HO_CA02122017(@Optional("") String state) {
		TestData tdAddress = getTestSpecificTD("TestData_Endorsement").resolveLinks();
		testFAIRPlanEndorsementTemplate.pas13211_AC1_Renewal_PPC10_OtherThanLogHome_AAA_HO_CA02122017(tdAddress);
	}

	///AC#2

	/**
	 * @author Maris Strazds
	 * @name Test Membership Override - PAS-13211 AC#2 (New Business)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Initiate CA HO3 Quote.
	 * 3. Navigate to Property info tab
	 * 4. Select PPC 9
	 * 5. Select construction type as 'Log Home'
	 * 6. Select 'Yes' to question "Is this a log home assembled by a licensed building contractor?"
	 * 7. Fill all mandatory details and try to bind policy ----> Error ERROR_AAA_HO_CA10100616 should be displayed
	 * 8. Navigate back to Endorsement Tab and Add FAIR Plan Endorsement
	 * 9. Rate the quote, navigate to Bind tab and click on bind
	 * 10. Validate that UW rule ERROR_AAA_HO_CA10100616 IS NOT fired and policy is bound
	 * @details
	 **/
	@Parameters({"state"})
	@Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-12925 FAIR Plan Endorsement (formerly known as Difference in Conditions)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-13211")

	public void pas13211_AC2_NB_PPC10_LogHome_AAA_HO_CA10100616(@Optional("") String state) {
		testFAIRPlanEndorsementTemplate.pas13211_AC2_NB_PPC10_LogHome_AAA_HO_CA10100616();
	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership Override - PAS-13211 AC#2 (Endorsement)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create CA HO3 Quote with PPC 8B, 9, 10, 1X-8X, or 1Y-8Y,
	 *      construction type other than 'Log Home'
	 *      and without FAIR Plan Endorsement
	 * 3. Initiate Midterm endorsement
	 * 3. Navigate to Property info tab
	 * 4. Select Construction type 'Log Home'
	 * 5. Select 'Yes' to question "Is this a log home assembled by a licensed building contractor?"
	 * 6. Fill all mandatory details and try to bind policy ----> Error ERROR_AAA_HO_CA10100616 should be displayed
	 * 7. Navigate back to Endorsement Tab and Add FAIR Plan Endorsement
	 * 8. Rate the quote, navigate to Bind tab and click on bind
	 * 9. Validate that UW rule ERROR_AAA_HO_CA10100616 IS NOT fired and policy is bound
	 * @details
	 **/
	@Parameters({"state"})
	@Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-12925 FAIR Plan Endorsement (formerly known as Difference in Conditions)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-13211")

	public void pas13211_AC2_MidtermEndorsement_PPC8X_LogHome_AAA_HO_CA10100616(@Optional("") String state) {
		testFAIRPlanEndorsementTemplate.pas13211_AC2_MidtermEndorsement_PPC8X_LogHome_AAA_HO_CA10100616();
	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership Override - PAS-13211 AC#2 (Renewal)
	 * @scenario
	 * Precondition: Agent is expected to have the Membership override privilege.
	 * 1. Create Customer.
	 * 2. Create CA HO3 Policy with PPC 8B, 9, 10, 1X-8X, or 1Y-8Y,
	 *      construction type other than 'Log Home'
	 *      and without FAIR Plan Endorsement
	 * 3. Generate Renewal Image and retrieve it in Data Gathering
	 * 4. Navigate to Property info tab
	 * 5. Select Construction type 'Log Home'
	 * 6. Select 'Yes' to question "Is this a log home assembled by a licensed building contractor?"
	 * 7. Fill all mandatory details and try to bind policy ----> Error ERROR_AAA_HO_CA10100616 should be displayed
	 * 78. Navigate back to Endorsement Tab and Add FAIR Plan Endorsement
	 * 9. Rate the quote, navigate to Bind tab and click on bind
	 * 10. Validate that UW rule ERROR_AAA_HO_CA10100616 IS NOT fired and policy is bound
	 * @details
	 **/
	@Parameters({"state"})
	@Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-12925 FAIR Plan Endorsement (formerly known as Difference in Conditions)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-13211")

	public void pas13211_AC2_Renewal_PPC1X_LogHome_AAA_HO_CA10100616(@Optional("") String state) {
		testFAIRPlanEndorsementTemplate.pas13211_AC2_Renewal_PPC1X_LogHome_AAA_HO_CA10100616();
	}

	///////AC#3

	/**
	 * @author Maris Strazds
	 * @name Test Membership Override - PAS-13211 AC#3 (New Business)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Initiate CA HO3 Quote.
	 * 3. Navigate to Property info tab
	 * 4. Select PPC 9
	 * 5. Select construction type as 'Log Home'
	 * 6. Select 'NO' to question "Is this a log home assembled by a licensed building contractor?"
	 * 7. Fill all mandatory details and try to bind policy ----> Error ERROR_AAA_HO_CA10100616 should be displayed
	 * 8. Navigate back to Endorsement Tab and Add FAIR Plan Endorsement
	 * 9. Rate the quote, navigate to Bind tab and click on bind
	 * 10. Validate that UW rule ERROR_AAA_HO_CA10100616 IS fired
	 * 11. Override all errors and Bind
	 * @details
	 **/
	@Parameters({"state"})
	@Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-12925 FAIR Plan Endorsement (formerly known as Difference in Conditions)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-13211")

	public void pas13211_AC3_NB_PPC10_LogHome_AAA_HO_CA10100616(@Optional("") String state) {
		testFAIRPlanEndorsementTemplate.pas13211_AC3_NB_PPC10_LogHome_AAA_HO_CA10100616();
	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership Override - PAS-13211 AC#3 (Endorsement)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create CA HO3 Quote with PPC 8B, 9, 10, 1X-8X, or 1Y-8Y,
	 *      construction type other than 'Log Home'
	 *      and without FAIR Plan Endorsement
	 * 3. Initiate Midterm endorsement
	 * 4. Navigate to Property info tab
	 * 5. Select Construction type 'Log Home'
	 * 6. Select 'NO' to question "Is this a log home assembled by a licensed building contractor?"
	 * 7. Fill all mandatory details and try to bind policy ----> Error ERROR_AAA_HO_CA10100616 should be displayed
	 * 8. Navigate back to Endorsement Tab and Add FAIR Plan Endorsement
	 * 9. Rate the quote, navigate to Bind tab and click on bind
	 * 10. Validate that UW rule ERROR_AAA_HO_CA10100616 IS fired
	 * 11. Override all errors and Bind
	 * @details
	 **/
	@Parameters({"state"})
	@Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-12925 FAIR Plan Endorsement (formerly known as Difference in Conditions)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-13211")

	public void pas13211_AC3_MidtermEndorsement_PPC8X_LogHome_AAA_HO_CA10100616(@Optional("") String state) {
		testFAIRPlanEndorsementTemplate.pas13211_AC3_MidtermEndorsement_PPC8X_LogHome_AAA_HO_CA10100616();
	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership Override - PAS-13211 AC#3 (Renewal)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create CA HO3 Quote with PPC 8B, 9, 10, 1X-8X, or 1Y-8Y,
	 *      construction type other than 'Log Home'
	 *      and without FAIR Plan Endorsement
	 * 3. Generate Renewal Image and retrieve it in Data Gathering
	 * 4. Navigate to Property info tab
	 * 5. Select Construction type 'Log Home'
	 * 6. Select 'NO' to question "Is this a log home assembled by a licensed building contractor?"
	 * 7. Fill all mandatory details and try to bind policy ----> Error ERROR_AAA_HO_CA10100616 should be displayed
	 * 8. Navigate back to Endorsement Tab and Add FAIR Plan Endorsement
	 * 9. Rate the quote, navigate to Bind tab and click on bind
	 * 10. Validate that UW rule ERROR_AAA_HO_CA10100616 IS fired
	 * 11. Override all errors and Bind
	 * @details
	 **/
	@Parameters({"state"})
	@Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-12925 FAIR Plan Endorsement (formerly known as Difference in Conditions)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-13211")

	public void pas13211_AC3_Renewal_PPC1X_LogHome_AAA_HO_CA10100616(@Optional("") String state) {
		testFAIRPlanEndorsementTemplate.pas13211_AC3_Renewal_PPC1X_LogHome_AAA_HO_CA10100616();
	}

	////////////Start PAS-13242 PAS-14193 PAS-14368 PAS-14632////////////////

	/**
	 *@author Maris Strazds
	 *@name Test that form FPCECA is included in New Business Package ---> not included in Endorsement package ---> Included in Renewal Package
	 *@scenario
	 * 1. Create Customer
	 * 2. Create CA HO3 Policy with FAIR Plan Endorsement
	 * 3. Validate that form FPCECA is included in New Business Package
	 * 4. Validate that form FPCECA is included in New Business Package only once
	 * 5. Validate that form FPCECA is not included in Subsequent transactions, but it is listed in related documents (Make midterm endorsement
	 *    but do not remove FAIR Plan Endorsement and validate)
	 *          -PAS-14193-
	 * 6. Validate that form FPCECA is generated at renewal, and is listed in other documents
	 * 7. Make Renewal image endorsement without removing FAIR plan endorsement
	 * 8. Validate that form FPCECA is not included in Revised renewal package, but is listed in other documents
	 *          -PAS-14368-
	 * 9. Validate that FPCECA has correct Sequence in XML (<doc:Sequence> is 91 for NB and Endorsement, and 171 for renewal)
	 *          -PAS-14632-
	 * 10. Validate that Renewal Thank You Letter (61 5121) (in cases when it is generated) contains tag FairPlanYN with value "Y" if policy contains FAIR Plan Endorsement, and
	 *     value "N" if policy doesn't contain FAIR Plan Endorsement
	 *@details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-13242 PAS-14193 PAS-14368 PAS-14632")
	public void pas13242_pas14193_AC1_NB(@Optional("") String state) {
		TestData tdWithFAIRPlanEndorsement = getPolicyDefaultTD().adjust(EndorsementTab.class.getSimpleName(), getTestSpecificTD("EndorsementTab_Add"));
		tdWithFAIRPlanEndorsement.adjust(DocumentsTab.class.getSimpleName(), getTestSpecificTD("DocumentsTab_SignFairPlanEndorsement"));
		testFAIRPlanEndorsementTemplate.pas13242_pas14193_AC1_NB(tdWithFAIRPlanEndorsement);
	}

	/**
	 *@author Maris Strazds
	 *@name Test that form FPCECA is included in Endorsement Package
	 *@scenario
	 * 1. Create Customer
	 * 2. Create CA HO3 Policy without FAIR Plan Endorsement
	 * 3. Do a Midterm endorsement and add FAIR Plan Endorsement
	 * 4. Re-calculate premium and bind endorsement
	 * 5. Validate that form FPCECA is included in Endorsement Package
	 * 6. Validate that form FPCECA is included in Endorsement Package only once
	 * 7.         -PAS-14368-
	 * 8. Validate that FPCECA has correct Sequence in XML (<doc:Sequence> is 91 for NB and Endorsement, and 171 for renewal)
	 *@details
	 */
	@Parameters({"state"})
	@Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-13242 PAS-14368")
	public void pas13242_AC2_Endorsement(@Optional("") String state) {
		testFAIRPlanEndorsementTemplate.pas13242_AC2_Endorsement();
	}

	/**
	 *@author Maris Strazds
	 *@name Test that form FPCECA is included in Renewal Package
	 *@scenario
	 * 1. Create Customer
	 * 2. Create CA HO3 Policy without FAIR Plan Endorsement
	 * 3. Generate renewal image and retrieve it in Data Gathering mode (before Renewal Document package is generated)
	 * 4. Add FAIR Plan Endorsement
	 * 5. Re-calculate premium and bind
	 * 6. Generate Renewal Document Package at Renewal offer generation date
	 * 7. Validate that form FPCECA is included in Renewal Package
	 * 8. Validate that form FPCECA is included in Renewal package only once
	 *           -PAS-14368-
	 * 9. Validate that FPCECA has correct Sequence in XML (<doc:Sequence> is 91 for NB and Endorsement, and 171 for renewal)
	 *           -PAS-14632-
	 * 10. Validate that Renewal Thank You Letter (61 5121) (in cases when it is generated) contains tag FairPlanYN with value "Y" if policy contains FAIR Plan Endorsement, and
	 *     value "N" if policy doesn't contain FAIR Plan Endorsement
	 *@details
	 */
	@Parameters({"state"})
	@Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.HIGH, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-13242 PAS-14193 PAS-14368 PAS-14632")
	public void pas13242_pas14193_AC3_Renewal(@Optional("") String state) {
		testFAIRPlanEndorsementTemplate.pas13242_pas14193_AC3_Renewal();
	}

	/**
	 *@author Maris Strazds
	 *@name Test that form FPCECA is included in Renewal package, if endorsement is made for Renewal term when Renewal Document package is already generated (i.e Revised renewal)
	 *@scenario
	 * 1. Create Customer
	 * 2. Create CA HO3 Policy without FAIR Plan Endorsement
	 * 3. Generate Renewal Document package at Renewal Offer Generation Date
	 * 4. Add FAIR Plan Endorsement to Renewal Term
	 * 5. Re-calculate premium and bind endorsement
	 * 6. Validate that form FPCECA is included in Revised Renewal package
	 * 7. Validate that form FPCECA is included in Revised Renewal package only once
	 * 	          -PAS-14368-
	 * 8. Validate that FPCECA has correct Sequence in XML (<doc:Sequence> is 91 for NB and Endorsement, and 171 for renewal)
	 * 	          -PAS-14632-
	 * 9. Validate that Renewal Thank You Letter (61 5121) (in cases when it is generated) contains tag FairPlanYN with value "Y" if policy contains FAIR Plan Endorsement, and
	 *     value "N" if policy doesn't contain FAIR Plan Endorsement
	 *@details
	 */
	@Parameters({"state"})
	@Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.HIGH, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-13242 PAS-14193 PAS-14368 PAS-14632")
	public void pas13242_pas14193_AC3_Revised_Renewal_After_Renewal_Term_Change(@Optional("") String state) {
		testFAIRPlanEndorsementTemplate.pas13242_pas14193_AC3_Revised_Renewal_After_Renewal_Term_Change();
	}

	/**
	 *@author Maris Strazds
	 *@name Test that form FPCECA is included in Endorsement package, but not in Renewal Package,
	 *  if endorsement is made for current term when Renewal Document package is already generated (i.e. revised renewal)
	 *@scenario
	 * 1. Create Customer
	 * 2. Create CA HO3 Policy without FAIR Plan Endorsement
	 * 3. Generate Renewal Document package at Renewal Offer Generation Date
	 * 4. Add FAIR Plan Endorsement for CURRENT term
	 * 5. Re-calculate premium and bind endorsement
	 * 6. Validate that form FPCECA is included in Endorsement package
	 * 7. Validate that form FPCECA is included in Revised Renewal package (PAS-14193)
	 * 8. Validate that form FPCECA is included in Endorsement package only once
	 *           -PAS-14368-
	 * 9. Validate that FPCECA has correct Sequence in XML (<doc:Sequence> is 91 for NB and Endorsement, and 171 for renewal)
	 *           -PAS-14632-
	 * 10. Validate that Renewal Thank You Letter (61 5121) (in cases when it is generated) contains tag FairPlanYN with value "Y" if policy contains FAIR Plan Endorsement, and
	 *     value "N" if policy doesn't contain FAIR Plan Endorsement
	 *@details
	 */
	@Parameters({"state"})
	@Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.HIGH, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-13242 PAS-14193 PAS-14368 PAS-14632")
	public void pas13242_pas14193_AC3_Revised_Renewal_After_Current_Term_Change(@Optional("") String state) {
		testFAIRPlanEndorsementTemplate.pas13242_pas14193_AC3_Revised_Renewal_After_Current_Term_Change();
	}

	////////////End PAS-13242 PAS-14193 PAS-14368 PAS-14632////////////////

	////////////Start PAS-13216////////////////

	/**
	 * @author Maris Strazds
	 * @name PAS-13216 All ACs, (New Business)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Initiate CA HO3 Quote. (don't add FAIR plan Endorsement)
	 * 3. Navigate to Property info tab
	 * 4. Answer 'No' to question 'Is the stove the sole source of heat?' and validate that the rule error message is displayed for the field, it is possible to continue via 'Continue' button
	 *      and error are displayed on Premium Calculation
	 * 5. Answer 'No' to question 'Does the dwelling have at least one smoke detector per story?' and validate that the rule error message is displayed for the field, it is possible to continue via 'Continue' button
	 *      and error is displayed on Premium Calculation
	 * 6. Navigate to Endorsement Tab and add FAIR Plan Endorsement
	 * 7. Answer 'No' to question 'Is the stove the sole source of heat?' and validate that the rule error message is not displayed for the field, it is possible to continue via 'Continue' button
	 *      and no errors are displayed on Premium Calculation
	 * 8. Answer 'No' to question 'Does the dwelling have at least one smoke detector per story?' and validate that the rule error message is not displayed for the field, it is possible to continue via 'Continue' button
	 *      and no errors are displayed on Premium Calculation
	 * 9. Save & Exit
	 * @details
	 **/
	@Parameters({"state"})
	@Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-12925 FAIR Plan Endorsement (formerly known as Difference in Conditions)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-13216")

	public void pas13216_All_ACs_NB(@Optional("") String state) {
		testFAIRPlanEndorsementTemplate.pas13216_All_ACs_NB();
	}

	/**
	 * @author Maris Strazds
	 * @name PAS-13216 All ACs, (Endorsement)
	 * @scenario
	 * 1. Create Customer
	 * 2. Create CA HO3 Policy. (don't add FAIR plan Endorsement)
	 * 3. Initiate Midterm Endorsement
	 * 4. Navigate to Property info tab
	 * 5. Answer 'No' to question 'Is the stove the sole source of heat?' and validate that the rule error message is displayed for the field, it is possible to continue via 'Continue' button
	 *      and error are displayed on Premium Calculation
	 * 6. Answer 'No' to question 'Does the dwelling have at least one smoke detector per story?' and validate that the rule error message is displayed for the field, it is possible to continue via 'Continue' button
	 *      and error is displayed on Premium Calculation
	 * 7. Navigate to Endorsement Tab and add FAIR Plan Endorsement
	 * 8. Answer 'No' to question 'Is the stove the sole source of heat?' and validate that the rule error message is not displayed for the field, it is possible to continue via 'Continue' button
	 *      and no errors are displayed on Premium Calculation
	 * 9. Answer 'No' to question 'Does the dwelling have at least one smoke detector per story?' and validate that the rule error message is not displayed for the field, it is possible to continue via 'Continue' button
	 *      and no errors are displayed on Premium Calculation
	 * 10. Save & Exit
	 * @details
	 **/
	@Parameters({"state"})
	@Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-12925 FAIR Plan Endorsement (formerly known as Difference in Conditions)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-13216")

	public void pas13216_All_ACs_Endorsement(@Optional("") String state) {
		testFAIRPlanEndorsementTemplate.pas13216_All_ACs_Endorsement();
	}

	/**
	 * @author Maris Strazds
	 * @name PAS-13216 All ACs, (Renewal)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create CA HO3 Policy. (don't add FAIR plan Endorsement)
	 * 3. Navigate to Property info tab
	 * 4. Answer 'No' to question 'Is the stove the sole source of heat?' and validate that the rule error message is displayed for the field, it is possible to continue via 'Continue' button
	 *      and error are displayed on Premium Calculation
	 * 5. Answer 'No' to question 'Does the dwelling have at least one smoke detector per story?' and validate that the rule error message is displayed for the field, it is possible to continue via 'Continue' button
	 *      and error is displayed on Premium Calculation
	 * 6. Navigate to Endorsement Tab and add FAIR Plan Endorsement
	 * 7. Answer 'No' to question 'Is the stove the sole source of heat?' and validate that the rule error message is not displayed for the field, it is possible to continue via 'Continue' button
	 *      and no errors are displayed on Premium Calculation
	 * 8. Answer 'No' to question 'Does the dwelling have at least one smoke detector per story?' and validate that the rule error message is not displayed for the field, it is possible to continue via 'Continue' button
	 *      and no errors are displayed on Premium Calculation
	 * 9. Save & Exit
	 * @details
	 **/
	@Parameters({"state"})
	@Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-12925 FAIR Plan Endorsement (formerly known as Difference in Conditions)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-13216")

	public void pas13216_All_ACs_Renewal(@Optional("") String state) {
		testFAIRPlanEndorsementTemplate.pas13216_All_ACs_Renewal();
	}

	////////////Start PAS-14004////////////////

	/**
	 * @author Maris Strazds
	 * @name Verify FPCECA/FPCECADP document is available for printing in On-Demand Documents tab for Quote, if FAIR Plan Endorsement IS added
	 * @scenario
	 *1. Initiate CA HO3/DP3 quote
	 *2. Fill all details, add FAIR Plan Endorsement
	 *3. Caluclate Premium
	 *4. Save & Exit the quote
	 *5. Navigate to ODD page
	 *6. Validate that FPCECA/FPCECADP document IS available for printing
	 *7. Validate that the 'Central Print' option for the FPCECA/FPCECAD document is disabled
	 *8. Validate that the 'Email', 'Fax', 'eSignature', 'Local Print' options for the FPCECA/FPCECAD document are enabled
	 * @details
	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-12925 FAIR Plan Endorsement (formerly known as Difference in Conditions)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14004")

	public void pas14004_AC1_AC2_Quote(@Optional("") String state) {
		TestData tdWithFAIRPlanEndorsement = getPolicyDefaultTD().adjust(EndorsementTab.class.getSimpleName(), getTestSpecificTD("EndorsementTab_Add"));
		tdWithFAIRPlanEndorsement.adjust(DocumentsTab.class.getSimpleName(), getTestSpecificTD("DocumentsTab_SignFairPlanEndorsement"));
		testFAIRPlanEndorsementTemplate.pas14004_AC1_AC2_Quote(tdWithFAIRPlanEndorsement);
	}

	/**
	 * @author Maris Strazds
	 * @name Verify FPCECA/FPCECADP document is not available for printing in On-Demand Documents tab for Quote, if FAIR Plan Endorsement IS NOT added
	 * @scenario
	 *1. Initiate CA HO3/DP3 quote
	 *2. Fill all details, do not add FAIR Plan Endorsement
	 *3. Caluclate Premium
	 *4. Save & Exit the quote
	 *5. Navigate to ODD page
	 *6. Validate that FPCECA/FPCECADP document IS NOT available for printing
	 * @details
	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-12925 FAIR Plan Endorsement (formerly known as Difference in Conditions)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14004")

	public void pas14004_AC1_AC2_Quote_negative(@Optional("") String state) {
		testFAIRPlanEndorsementTemplate.pas14004_AC1_AC2_Quote_negative();
	}

	/**
	 * @author Maris Strazds
	 * @name VVerify FPCECA/FPCECADP document is available for printing in On-Demand Documents tab for Policy, if FAIR Plan Endorsement IS added
	 * @scenario
	 *1. Create CA HO3/DP3 Policy with FAIR Plan Endorsement
	 *2. Navigate to ODD page
	 *3. Validate that FPCECA/FPCECADP document IS available for printing
	 *4. Validate that the 'Central Print' option for the FPCECA/FPCECAD document is disabled
	 *5. Validate that the 'Email', 'Fax', 'eSignature', 'Local Print' options for the FPCECA/FPCECAD document are enabled
	 * @details
	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-12925 FAIR Plan Endorsement (formerly known as Difference in Conditions)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14004")

	public void pas14004_AC1_AC2_Policy(@Optional("") String state) {
		TestData tdWithFAIRPlanEndorsement = getPolicyDefaultTD().adjust(EndorsementTab.class.getSimpleName(), getTestSpecificTD("EndorsementTab_Add"));
		tdWithFAIRPlanEndorsement.adjust(DocumentsTab.class.getSimpleName(), getTestSpecificTD("DocumentsTab_SignFairPlanEndorsement"));
		testFAIRPlanEndorsementTemplate.pas14004_AC1_AC2_Policy(tdWithFAIRPlanEndorsement);
	}

	/**
	 * @author Maris Strazds
	 * @name Verify FPCECA/FPCECADP document is not available for printing in On-Demand Documents tab for Policy, if FAIR Plan Endorsement IS NOT added
	 * @scenario
	 *1. Initiate CA HO3/DP3 quote
	 *2. Fill all details, do not add FAIR Plan Endorsement
	 *3. Caluclate Premium
	 *4. Save & Exit the quote
	 *5. Navigate to ODD page
	 *6. Validate that FPCECA/FPCECADP document IS NOT available for printing
	 * @details
	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-12925 FAIR Plan Endorsement (formerly known as Difference in Conditions)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14004")

	public void pas14004_AC1_AC2_Policy_negative(@Optional("") String state) {
		testFAIRPlanEndorsementTemplate.pas14004_AC1_AC2_Policy_negative();
	}
	////////////End PAS-14004////////////////

}


