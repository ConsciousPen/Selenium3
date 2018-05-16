package aaa.modules.regression.sales.home_ca.ho3.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.*;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.TimePoints;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class TestFAIRPlanEndorsement extends HomeCaHO3BaseTest {

	private ApplicantTab applicantTab = new ApplicantTab();
	private ReportsTab reportsTab = new ReportsTab();
	private EndorsementTab endorsementTab = new EndorsementTab();
	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
	private BindTab bindTab = new BindTab();
	private PurchaseTab purchaseTab = new PurchaseTab();
	private ErrorTab errorTab = new ErrorTab();
	private PropertyInfoTab propertyInfoTab = new PropertyInfoTab();

	private final String formId = DocGenEnum.Documents.FPCECA.getIdInXml();
	private final String formDesc = DocGenEnum.Documents.FPCECA.getName();

	private static final  String ERROR_IS_THE_STOVE_THE_SOLE_SOURCE_OF_HEAT = "Wood burning stoves as the sole source of heat are ineligible.";
	//private static final String ERROR_WAS_THE_STOVE_INSTALLED_BY_A_LICENSED_CONTRACTOR = "Wood burning stoves are ineligible unless professionally installed by a licensed contractor.";
	private static final String ERROR_DOES_THE_DWELLING_HAVE_AT_LEAST_ONE_SMOKE_DETECTOR_PER_STORY =
			"Dwellings with a wood burning stove without at least one smoke detector insta";

	///AC#1, AC#4

	/**
	 * @author Maris Strazds
	 * @name Test Membership Override - PAS-13211 AC#1, AC4 (New Business)
	 * @scenario
	 * Precondition: Agent is expected to have the Membership override privilege.
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
	 * 11. Vakidate that UW task is not generated at
	 * Retrieve renewal Image at R-67 or R-57 and validate that UW task is not generated at R-67 (Renewal UW rules validation time point)
	 * @details
	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-12925 FAIR Plan Endorsement (formerly known as Difference in Conditions)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-13211")

	public void pas13211_AC1_NB1_PPC10_OtherThanLogHome_AAA_HO_CA02122017(@Optional("") String state) {
		String ppcValue = "10";
		String constructionTypeValue = "Masonry";
		String licensedContractor = null; // Value for question "Is this a log home assembled by a licensed building contractor?"

		TestData testData = getTestData(ppcValue, constructionTypeValue, licensedContractor);

		mainApp().open();
		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(testData, PropertyInfoTab.class, true);
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());// Tab out to not fire the error "Dwellings located in PPC 10 are ineligible." for field "Roof Type"
		policy.getDefaultView().fillFromTo(testData, EndorsementTab.class, BindTab.class, true);
		bindTab.submitTab();

		errorTab.verify.errorsPresent(true, ErrorEnum.Errors.ERROR_AAA_HO_CA02122017);
		errorTab.verify.errorsPresent(false, ErrorEnum.Errors.ERROR_AAA_HO_CA10100616);
		errorTab.cancel();

		switchToFAIRplanEndorsementAndCalculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
		bindTab.submitTab();
		purchaseTab.fillTab(testData);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		generateRenewalImageAndRetrievePolicyRminus67(getTimePoints()); //TODO-mstrazds:validate that works
		//TODO-mstrazds: validate task is not generated

		//TODO-mstrazds:1 why Purchese Tab fails

	}

	//TODO-mstrazds: AC#1 not possible for Endorsement - not possible to change PPC during Endorsement (and also rule should not fire as per existing implementation on Master)

	/**
	 * @author Maris Strazds
	 * @name Test Membership Override - PAS-13211 AC#1, AC4 (Renewal) //TODO-mstrazds: this TC possibly needs to be removed, because per current implementation this rule doesn't fair for Endorsement and Renewal
	 * @scenario
	 * Precondition: Agent is expected to have the Membership override privilege.
	 * 1. Create Customer.
	 * 2. Create CA HO3 Policy
	 *      PPC other than 10,
	 *      not a 'Log Home'.
	 * 3. Retrieve Renewal Image in data gathering
	 * 3. Navigate to Property info tab
	 * 4. Change Dwelling address so that PPC reports PPC 10 (order PPC report)
	 * 6. Fill all mandatory details and try to bind policy ----> Error ERROR_AAA_HO_CA02122017 should NOT be displayed  (Existing behavior on Master)
	 * @details
	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-12925 FAIR Plan Endorsement (formerly known as Difference in Conditions)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-13211")

	public void pas13211_AC1_Renewal_PPC10_OtherThanLogHome_AAA_HO_CA02122017(@Optional("") String state) {
		String ppcValue = "5";
		String constructionTypeValue = "Masonry";
		String licensedContractor = null; // Value for question "Is this a log home assembled by a licensed building contractor?"

		TestData testData = getTestData(ppcValue, constructionTypeValue, licensedContractor);

		mainApp().open();
		createCustomerIndividual();

		createPolicy(testData);

		generateRenewalImageAndRetrievePolicy(getTimePoints());
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();

		changeAddressOrderPPCRateAndNavigateToBindTab();
		bindTab.submitTab();

		errorTab.verify.errorsPresent(false, ErrorEnum.Errors.ERROR_AAA_HO_CA02122017);
		errorTab.verify.errorsPresent(true, ErrorEnum.Errors.ERROR_AAA_HO_CA10100616);
		errorTab.cancel();

		switchToFAIRplanEndorsementAndBind();

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
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-12925 FAIR Plan Endorsement (formerly known as Difference in Conditions)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-13211")

	public void pas13211_AC2_NB_PPC10_LogHome_AAA_HO_CA10100616(@Optional("") String state) {
		String ppcValue = "9";
		String constructionTypeValue = "Log Home";
		String licensedContractor = "Yes"; // Value for question "Is this a log home assembled by a licensed building contractor?"

		TestData testData = getTestData(ppcValue, constructionTypeValue, licensedContractor);

		mainApp().open();
		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(testData, PropertyInfoTab.class, true);
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());// Tab out to not fire the error "Dwellings located in PPC 10 are ineligible." for field "Roof Type"
		policy.getDefaultView().fillFromTo(testData, EndorsementTab.class, BindTab.class, true);
		bindTab.submitTab();

		errorTab.verify.errorsPresent(false, ErrorEnum.Errors.ERROR_AAA_HO_CA02122017);
		errorTab.verify.errorsPresent(true, ErrorEnum.Errors.ERROR_AAA_HO_CA10100616);
		errorTab.cancel();

		switchToFAIRplanEndorsementAndCalculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
		bindTab.submitTab();
		purchaseTab.fillTab(testData);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		//TODO-mstrazds: Add steps to validate AC#4?

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
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-12925 FAIR Plan Endorsement (formerly known as Difference in Conditions)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-13211")

	public void pas13211_AC2_MidtermEndorsement_PPC8X_LogHome_AAA_HO_CA10100616(@Optional("") String state) {
		String ppcValue = "8X";
		String constructionTypeValue = "Masonry";
		String licensedContractor = null; // Value for question "Is this a log home assembled by a licensed building contractor?"

		TestData testData = getTestData(ppcValue, constructionTypeValue, licensedContractor);

		mainApp().open();
		createCustomerIndividual();

		createPolicy(testData);

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus3Days"));
		switchToLogHomeAndNavigateToBind("Yes");
		bindTab.submitTab();

		errorTab.verify.errorsPresent(false, ErrorEnum.Errors.ERROR_AAA_HO_CA02122017);
		errorTab.verify.errorsPresent(true, ErrorEnum.Errors.ERROR_AAA_HO_CA10100616);
		errorTab.cancel();

		switchToFAIRplanEndorsementAndBind();

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
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-12925 FAIR Plan Endorsement (formerly known as Difference in Conditions)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-13211")

	public void pas13211_AC2_Renewal_PPC1X_LogHome_AAA_HO_CA10100616(@Optional("") String state) {
		String ppcValue = "1X";
		String constructionTypeValue = "Masonry";
		String licensedContractor = null; // Value for question "Is this a log home assembled by a licensed building contractor?"

		TestData testData = getTestData(ppcValue, constructionTypeValue, licensedContractor);

		mainApp().open();
		createCustomerIndividual();

		createPolicy(testData);

		generateRenewalImageAndRetrievePolicy(getTimePoints());
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();

		switchToLogHomeAndNavigateToBind("Yes");
		bindTab.submitTab();

		errorTab.verify.errorsPresent(false, ErrorEnum.Errors.ERROR_AAA_HO_CA02122017);
		errorTab.verify.errorsPresent(true, ErrorEnum.Errors.ERROR_AAA_HO_CA10100616);
		errorTab.cancel();

		switchToFAIRplanEndorsementAndBind();

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
	 * 67. Fill all mandatory details and try to bind policy ----> Error ERROR_AAA_HO_CA10100616 should be displayed
	 * 8. Navigate back to Endorsement Tab and Add FAIR Plan Endorsement
	 * 9. Rate the quote, navigate to Bind tab and click on bind
	 * 10. Validate that UW rule ERROR_AAA_HO_CA10100616 IS NOT fired and policy is bound
	 * @details
	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-12925 FAIR Plan Endorsement (formerly known as Difference in Conditions)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-13211")

	public void pas13211_AC3_NB_PPC10_LogHome_AAA_HO_CA10100616(@Optional("") String state) {
		String ppcValue = "9";
		String constructionTypeValue = "Log Home";
		String licensedContractor = "No"; // Value for question "Is this a log home assembled by a licensed building contractor?"

		TestData testData = getTestData(ppcValue, constructionTypeValue, licensedContractor);

		mainApp().open();
		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(testData, PropertyInfoTab.class, true);
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());// Tab out to not fire the error "Dwellings located in PPC 10 are ineligible." for field "Roof Type"
		policy.getDefaultView().fillFromTo(testData, EndorsementTab.class, BindTab.class, true);
		bindTab.submitTab();

		errorTab.verify.errorsPresent(false, ErrorEnum.Errors.ERROR_AAA_HO_CA02122017);
		errorTab.verify.errorsPresent(true, ErrorEnum.Errors.ERROR_AAA_HO_CA10100616);
		errorTab.cancel();

		switchToFAIRplanEndorsementAndCalculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
		bindTab.submitTab();

		errorTab.verify.errorsPresent(false, ErrorEnum.Errors.ERROR_AAA_HO_CA02122017);
		errorTab.verify.errorsPresent(true, ErrorEnum.Errors.ERROR_AAA_HO_CA10100616);
		errorTab.overrideAllErrors();
		errorTab.submitTab();

		purchaseTab.fillTab(testData);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		//TODO-mstrazds: Add steps to validate AC#4?

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
	 * 10. Validate that UW rule ERROR_AAA_HO_CA10100616 IS fired and policy is bound
	 * @details
	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-12925 FAIR Plan Endorsement (formerly known as Difference in Conditions)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-13211")

	public void pas13211_AC3_MidtermEndorsement_PPC8X_LogHome_AAA_HO_CA10100616(@Optional("") String state) {
		String ppcValue = "8X";
		String constructionTypeValue = "Masonry";
		String licensedContractor = null; // Value for question "Is this a log home assembled by a licensed building contractor?"

		TestData testData = getTestData(ppcValue, constructionTypeValue, licensedContractor);

		mainApp().open();
		createCustomerIndividual();

		createPolicy(testData);

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus3Days"));
		switchToLogHomeAndNavigateToBind("No");
		bindTab.submitTab();

		errorTab.verify.errorsPresent(false, ErrorEnum.Errors.ERROR_AAA_HO_CA02122017);
		errorTab.verify.errorsPresent(true, ErrorEnum.Errors.ERROR_AAA_HO_CA10100616);
		errorTab.cancel();

		switchToFAIRplanEndorsementAndNavigateToBindTab();

		bindTab.submitTab();
		errorTab.verify.errorsPresent(false, ErrorEnum.Errors.ERROR_AAA_HO_CA02122017);
		errorTab.verify.errorsPresent(true, ErrorEnum.Errors.ERROR_AAA_HO_CA10100616);
		errorTab.overrideAllErrors();
		errorTab.submitTab();

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

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
	 * 10. Validate that UW rule ERROR_AAA_HO_CA10100616 IS fired and policy is bound
	 * @details
	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-12925 FAIR Plan Endorsement (formerly known as Difference in Conditions)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-13211")

	public void pas13211_AC3_Renewal_PPC1X_LogHome_AAA_HO_CA10100616(@Optional("") String state) {
		String ppcValue = "1X";
		String constructionTypeValue = "Masonry";
		String licensedContractor = null; // Value for question "Is this a log home assembled by a licensed building contractor?"

		TestData testData = getTestData(ppcValue, constructionTypeValue, licensedContractor);

		mainApp().open();
		createCustomerIndividual();

		createPolicy(testData);

		generateRenewalImageAndRetrievePolicy(getTimePoints());
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();

		switchToLogHomeAndNavigateToBind("No");
		bindTab.submitTab();

		errorTab.verify.errorsPresent(false, ErrorEnum.Errors.ERROR_AAA_HO_CA02122017);
		errorTab.verify.errorsPresent(true, ErrorEnum.Errors.ERROR_AAA_HO_CA10100616);
		errorTab.cancel();

		switchToFAIRplanEndorsementAndNavigateToBindTab();

		bindTab.submitTab();
		errorTab.verify.errorsPresent(false, ErrorEnum.Errors.ERROR_AAA_HO_CA02122017);
		errorTab.verify.errorsPresent(true, ErrorEnum.Errors.ERROR_AAA_HO_CA10100616);
		errorTab.overrideAllErrors();
		errorTab.submitTab();

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

	}

	////////////Start PAS-13242////////////////

	/**
	 *@author Maris Strazds
	 *@name Test that form FPCECA is included in New Business Package
	 *@scenario
	 * 1. Create Customer
	 * 2. Create CA HO3 Policy with FAIR Plan Endorsement
	 * 3. Validate that form FPCECA is included in New Business Package
	 * 4. Validate that form FPCECA is included in New Business Package only once
	 * 5. Validate that form FPCECA is not included in Subsequent transactions, but it is listed in related documents (Make midterm endorsement
	 *    but do not remove FAIR Plan Endorsement and validate)
	 * 6. Validate that form FPCECA is not generated at renewal, but it is listed in other documents
	 *@details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12466")
	public void pas12466_AC1_NB(@Optional("MD") String state) {

		TestData tdWithFAIRplanEndorsement = getPolicyTD().adjust(EndorsementTab.class.getSimpleName(), getTestSpecificTD("EndorsementTab_Add"));

		mainApp().open();
		createCustomerIndividual();

		createPolicy(tdWithFAIRplanEndorsement);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		validateDocumentIsGeneratedInPackage(policyNumber, POLICY_ISSUE);

		//5. Validate that Subsequent transactions does not contain the form FPCECA
		//Perform mid-term endorsement, but don't switch away from FAIR Plan Endorsement
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus3Days"));

		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
		premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE).setValue("contains=1500");
		premiumsAndCoveragesQuoteTab.calculatePremium();

		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
		new BindTab().submitTab();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		validateDocumentIsNotGeneratedInPackage(policyNumber, ENDORSEMENT_ISSUE, true);

		//Validate that form FPCECA is not generated at renewal, but it is listed in other documents
		generateRenewalOfferAtOfferGenDate();
		validateDocumentIsNotGeneratedInPackage(policyNumber, RENEWAL_OFFER, true);
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
	 *@details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12466")
	public void pas12466_AC2_Endorsement(@Optional("MD") String state) {

		// Create policy without FAIR Plan Endorsement
		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		validateDocumentIsNotGeneratedInPackage(policyNumber, POLICY_ISSUE, false);

		//Perform mid-term endorsement and add FAIR Plan Endorsement
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus3Days"));
		switchToFAIRplanEndorsementAndBind();

		validateDocumentIsGeneratedInPackage(policyNumber, ENDORSEMENT_ISSUE);
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
	 *@details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12466")
	public void pas12466_AC3_Renewal(@Optional("MD") String state) {

		// Create policy without FAIR Plan Endorsement
		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		validateDocumentIsNotGeneratedInPackage(policyNumber, POLICY_ISSUE, false);

		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		LocalDateTime renewalProposalDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);

		//3. Generate renewal image
		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		//reopen app and retrieve policy by number
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonRenewals.click();

		// Open Renewal Image in Data Gather mode
		policy.dataGather().start();

		//4. Switch to FAIR Plan Endorsement and Bind
		switchToFAIRplanEndorsementAndBind();

		TimeSetterUtil.getInstance().nextPhase(renewalProposalDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		//JobUtils.executeJob(Jobs.aaaDocGenBatchJob);//not necessary - can be used if QA needs actual generated xml files

		validateDocumentIsGeneratedInPackage(policyNumber, RENEWAL_OFFER);
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
	 * 6. Validate that form FPCECA is included in Renewal package
	 * 7. Validate that form FPCECA is included in Renewal package only once
	 *@details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12466")
	public void pas12466_AC3_Revised_Renewal_After_Renewal_Term_Change(@Optional("MD") String state) {

		// Create policy without FAIR Plan Endorsement
		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		generateRenewalOfferAtOfferGenDate();
		validateDocumentIsNotGeneratedInPackage(policyNumber, RENEWAL_OFFER, false);

		//Reopen app and retrieve policy by number
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonRenewals.click();

		//Retrieve Renewal image in data gathering mode
		policy.dataGather().start();

		//4. Switch to FAIR Plan Endorsement
		switchToFAIRplanEndorsementAndBind();

		//JobUtils.executeJob(Jobs.aaaDocGenBatchJob);//not necessary - can be used if QA needs actual generated xml files
		validateDocumentIsGeneratedInPackage(policyNumber, RENEWAL_OFFER);
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
	 * 7. Validate that form FPCECA is not included in Renewal package
	 * 8. Validate that form FPCECA is included in Endorsement package only once
	 *@details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12466")
	public void pas12466_AC3_Revised_Renewal_After_Current_Term_Change(@Optional("MD") String state) {

		// Create policy without FAIR Plan Endorsement
		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		generateRenewalOfferAtOfferGenDate();
		validateDocumentIsNotGeneratedInPackage(policyNumber, RENEWAL_OFFER, false);

		//reopen app and retrieve policy by number
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		// PolicySummaryPage.buttonRenewals.click(); change current term not endorsement

		//Initiate Endorsement for current term
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus3Days"));

		//4. Switch FAIR Plan Endorsement
		switchToFAIRplanEndorsementAndBind();
		//JobUtils.executeJob(Jobs.aaaDocGenBatchJob);//not necessary - can be used if QA needs actual generated xml files

		CustomAssert.enableSoftMode();

		//6. Validate that form FPCECA is included in Endorsement package
		//8. Validate that form FPCECA is included in Endorsement package only once
		validateDocumentIsGeneratedInPackage(policyNumber, ENDORSEMENT_ISSUE);

		//7. Validate that form FPCECA is not included in Renewal package
		validateDocumentIsNotGeneratedInPackage(policyNumber, RENEWAL_OFFER, true);

	}

	////////////End PAS-13242////////////////

	////////////Start PAS-13216////////////////

	/**
	 * @author Maris Strazds
	 * @name Test Membership Override - PAS-13216 All ACs, (New Business)
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-12925 FAIR Plan Endorsement (formerly known as Difference in Conditions)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-13216")

	public void pas13216_All_ACs_NB(@Optional("") String state) {

		TestData testData = getPolicyDefaultTD();

		mainApp().open();
		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(testData, PropertyInfoTab.class, true);
		stoveQuestionValidationSteps();

	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership Override - PAS-13216 All ACs, (Endorsement)
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-12925 FAIR Plan Endorsement (formerly known as Difference in Conditions)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-13216")

	public void pas13216_All_ACs_Endorsement(@Optional("") String state) {
		TestData testData = getPolicyDefaultTD();

		mainApp().open();
		createCustomerIndividual();

		createPolicy(testData);
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus3Days"));
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
		stoveQuestionValidationSteps();
	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership Override - PAS-13216 All ACs, (Renewal)
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-12925 FAIR Plan Endorsement (formerly known as Difference in Conditions)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-13216")

	public void pas13216_All_ACs_Renewal(@Optional("") String state) {
		TestData testData = getPolicyDefaultTD();

		mainApp().open();
		createCustomerIndividual();

		createPolicy(testData);

		generateRenewalImageAndRetrievePolicy(getTimePoints());
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();

		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
		stoveQuestionValidationSteps();
	}

	private void validateSmokeDetectorQuestion(boolean ruleShouldFire) {
		fillStovesSection("Yes", "No", "Yes", "Yes");

		propertyInfoTab.getStovesAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Stoves.DOES_THE_DWELLING_HAVE_AT_LEAST_ONE_SMOKE_DETECTOR_PER_STORY).setValue("No");

		if (ruleShouldFire) {
			assertThat(propertyInfoTab.getStovesAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Stoves.DOES_THE_DWELLING_HAVE_AT_LEAST_ONE_SMOKE_DETECTOR_PER_STORY).getWarning().get()
					.contains(ERROR_DOES_THE_DWELLING_HAVE_AT_LEAST_ONE_SMOKE_DETECTOR_PER_STORY));
		} else {
			assertThat(propertyInfoTab.getStovesAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Stoves.DOES_THE_DWELLING_HAVE_AT_LEAST_ONE_SMOKE_DETECTOR_PER_STORY).getWarning().get()
					.contains(ERROR_DOES_THE_DWELLING_HAVE_AT_LEAST_ONE_SMOKE_DETECTOR_PER_STORY)).isFalse();
		}

		propertyInfoTab.submitTab();
		endorsementTab.submitTab();
		premiumsAndCoveragesQuoteTab.calculatePremium();

		if (ruleShouldFire) {
			assertThat(errorTab.tableErrors.getRow(1).getCell("Message").getValue()).contains(ERROR_DOES_THE_DWELLING_HAVE_AT_LEAST_ONE_SMOKE_DETECTOR_PER_STORY);
			assertThat(errorTab.tableErrors.getRowsCount()).isEqualTo(1); //assert that there are no other messages
			errorTab.cancel();
		}

		assertThat(premiumsAndCoveragesQuoteTab.btnCalculatePremium()).isPresent(); //to validate that Error tab is not displayed and P&C tab is displayed

	}

	private void validateSourceOfHeatQuestion(boolean ruleShouldFire) {
		fillStovesSection("Yes", "No", "Yes", "Yes");

		propertyInfoTab.getStovesAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Stoves.IS_THE_STOVE_THE_SOLE_SOURCE_OF_HEAT).setValue("Yes");
		if (ruleShouldFire) {
			assertThat(propertyInfoTab.getStovesAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Stoves.IS_THE_STOVE_THE_SOLE_SOURCE_OF_HEAT).getWarning().get()
					.contains(ERROR_IS_THE_STOVE_THE_SOLE_SOURCE_OF_HEAT));
		} else {
			assertThat(propertyInfoTab.getStovesAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Stoves.IS_THE_STOVE_THE_SOLE_SOURCE_OF_HEAT).getWarning().get()
					.contains(ERROR_IS_THE_STOVE_THE_SOLE_SOURCE_OF_HEAT)).isFalse();
		}

		propertyInfoTab.submitTab();
		endorsementTab.submitTab();
		premiumsAndCoveragesQuoteTab.calculatePremium();
		if (ruleShouldFire) {
			assertThat(errorTab.tableErrors.getRow(1).getCell("Message").getValue()).contains(ERROR_IS_THE_STOVE_THE_SOLE_SOURCE_OF_HEAT);
			assertThat(errorTab.tableErrors.getRowsCount()).isEqualTo(1); //assert that there are no other messages
			errorTab.cancel();
		}

		assertThat(premiumsAndCoveragesQuoteTab.btnCalculatePremium()).isPresent(); //to validate that Error tab is not displayed and P&C tab is displayed

	}

	private void fillStovesSection(String isWoodBurningStove, String isSourceOfHeat, String isLicensedContractor, String smokeDetector) {
		propertyInfoTab.getStovesAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Stoves.DOES_THE_PROPERTY_HAVE_A_WOOD_BURNING_STOVE).setValue(isWoodBurningStove);
		propertyInfoTab.getStovesAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Stoves.IS_THE_STOVE_THE_SOLE_SOURCE_OF_HEAT).setValue(isSourceOfHeat);
		propertyInfoTab.getStovesAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Stoves.WAS_THE_STOVE_INSTALLED_BY_A_LICENSED_CONTRACTOR).setValue(isLicensedContractor);
		propertyInfoTab.getStovesAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Stoves.DOES_THE_DWELLING_HAVE_AT_LEAST_ONE_SMOKE_DETECTOR_PER_STORY).setValue(smokeDetector);
	}

	////////////End PAS-13216////////////////

	private TestData getTestData(String ppcValue, String constructionTypeValue, String licensedContractor) {
		TestData testData = getPolicyTD();

		testData.adjust(TestData.makeKeyPath(HomeCaMetaData.PropertyInfoTab.class.getSimpleName(),
				HomeCaMetaData.PropertyInfoTab.PublicProtectionClass.class.getSimpleName(),
				HomeCaMetaData.PropertyInfoTab.PublicProtectionClass.PUBLIC_PROTECTION_CLASS.getLabel()),
				ppcValue);
		testData.adjust(TestData.makeKeyPath(HomeCaMetaData.PropertyInfoTab.class.getSimpleName(),
				HomeCaMetaData.PropertyInfoTab.Construction.class.getSimpleName(),
				HomeCaMetaData.PropertyInfoTab.Construction.CONSTRUCTION_TYPE.getLabel()),
				constructionTypeValue);

		testData.adjust(TestData.makeKeyPath(HomeCaMetaData.PropertyInfoTab.class.getSimpleName(),
				HomeCaMetaData.PropertyInfoTab.Construction.class.getSimpleName(),
				HomeCaMetaData.PropertyInfoTab.Construction.IS_THIS_A_LOG_HOME_ASSEMBLED_BY_A_LICENSED_BUILDING_CONTRACTOR.getLabel()),
				licensedContractor);

		return testData;
	}

	//TODO-mstrazds:move to some helper or use from already existing test class
	private void generateRenewalImageAndRetrievePolicy(TimePoints timePoints) {
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		//Generate renewal image at renewal image generation date
		LocalDateTime renewImageGenDate = timePoints.getRenewImageGenerationDate(policyExpirationDate);

		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
	}

	private void generateRenewalImageAndRetrievePolicyRminus67(TimePoints timePoints) {
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		//Generate renewal image at renewal image generation date
		LocalDateTime renewCheckUWRules = timePoints.getRenewCheckUWRules(policyExpirationDate);

		TimeSetterUtil.getInstance().nextPhase(renewCheckUWRules);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
	}

	private void switchToLogHomeAndNavigateToBind(String licensedBuildingConstractorValue) {
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
		propertyInfoTab.getConstructionAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Construction.CONSTRUCTION_TYPE).setValueStarts("Log Home");
		propertyInfoTab.getConstructionAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Construction.IS_THIS_A_LOG_HOME_ASSEMBLED_BY_A_LICENSED_BUILDING_CONTRACTOR)
				.setValue(licensedBuildingConstractorValue);
		propertyInfoTab.submitTab();

		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsAndCoveragesQuoteTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
	}

	private void changeAddressOrderPPCRateAndNavigateToBindTab() {
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.APPLICANT.get());
		TestData tdAddress = getTestSpecificTD("TestData_Endorsement").resolveLinks();
		applicantTab.fillTab(tdAddress);
		applicantTab.getDwellingAddressAssetList().getAsset(HomeCaMetaData.ApplicantTab.DwellingAddress.COUNTY)
				.setValue("Los Angeles"); //County field clears out after Address validation, so entering it again

		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.REPORTS.get());
		reportsTab.reorderReports();

		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsAndCoveragesQuoteTab.calculatePremium();

		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());

	}

	////////////Start PAS-13242 methods////////////////

	private void validateDocumentIsGeneratedInPackage(String policyNumber, AaaDocGenEntityQueries.EventNames eventName) {
		List<Document> docs = DocGenHelper.getDocumentsList(policyNumber, eventName);
		assertThat(docs.stream().map(Document::getTemplateId).toArray()).contains(DocGenEnum.Documents.FPCECA.getIdInXml());

		//Create list of documents other than FPCECA
		List<Document> docsOther = docs.stream().filter(document -> !document.getTemplateId().equals(DocGenEnum.Documents.FPCECA.getIdInXml())).collect(Collectors.toList());

		//Validate that form FPCECA is listed in other documents (test validates that at least in one other document)
		assertThat(docsOther.stream().filter(document -> document.toString().contains(DocGenEnum.Documents.FPCECA.getIdInXml())).toArray().length).isGreaterThan(0);

		//Validate that form FPCECA is included in Document Package only once
		assertThat(docs.stream().filter(document -> document.getTemplateId().equals(DocGenEnum.Documents.FPCECA.getIdInXml())).toArray().length).isEqualTo(1);

	}

	private void validateDocumentIsNotGeneratedInPackage(String policyNumber, AaaDocGenEntityQueries.EventNames eventName, boolean shouldBeListedInOtherDocs) {
		List<Document> docs = DocGenHelper.getDocumentsList(policyNumber, eventName);
		assertThat(docs.stream().map(Document::getTemplateId).toArray()).doesNotContain(DocGenEnum.Documents.FPCECA.getIdInXml());

		//Create list of documents other than FPCECA
		List<Document> docsOther = docs.stream().filter(document -> !document.getTemplateId().equals(DocGenEnum.Documents.FPCECA.getIdInXml())).collect(Collectors.toList());

		//Validate that document FPCECA is/is not listed in other documents (test validates that FPCECA is listed at least in one other document)
		if (shouldBeListedInOtherDocs) {
			assertThat(docsOther.stream().filter(document -> document.toString().contains(DocGenEnum.Documents.FPCECA.getIdInXml())).toArray().length).isGreaterThan(0);

		} else {
			assertThat(docsOther.stream().filter(document -> document.toString().contains(DocGenEnum.Documents.FPCECA.getIdInXml())).toArray().length).isEqualTo(0);

		}
	}

	private void generateRenewalOfferAtOfferGenDate() {
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);

		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		//JobUtils.executeJob(Jobs.aaaDocGenBatchJob);//not necessary - can be used if QA needs actual generated xml files
	}

	private void switchToFAIRplanEndorsement() {
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());//navigates to Endorsement Tab
		endorsementTab.getAddEndorsementLink(HomeCaMetaData.EndorsementTab.FPCECA.getLabel()).click();
		Page.dialogConfirmation.confirm();
		endorsementTab.btnSaveForm.click();
	}

	private void switchToFAIRplanEndorsementAndCalculatePremium() {
		switchToFAIRplanEndorsement();
		premiumsAndCoveragesQuoteTab.calculatePremium();
	}

	private void switchToFAIRplanEndorsementAndBind() {
		switchToFAIRplanEndorsementAndCalculatePremium();
		//Bind
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
		new BindTab().submitTab();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	private void switchToFAIRplanEndorsementAndNavigateToBindTab() {
		switchToFAIRplanEndorsementAndCalculatePremium();
		//Bind
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());

	}

	private void switchAwayFromFAIRplanEndorsement() {
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());//navigates to Endorsement Tab
		endorsementTab.getRemoveEndorsementLink(formId, 1).click();
		Page.dialogConfirmation.confirm();

		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsAndCoveragesQuoteTab.calculatePremium();

	}

	private void switchAwayFromFAIRplanEndorsementAndBind() {
		switchAwayFromFAIRplanEndorsement();
		//Bind
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
		new BindTab().submitTab();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	////////////End PAS-13242 methods////////////////

	private void stoveQuestionValidationSteps() {
		//-----AC#1, AC#7 Is the stove the sole source of heat?
		validateSourceOfHeatQuestion(true);

		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());

		//----- AC#3, AC#8 Does the dwelling have at least one smoke detector per story?
		validateSmokeDetectorQuestion(true);

		switchToFAIRplanEndorsement();

		endorsementTab.submitTab();
		premiumsAndCoveragesQuoteTab.calculatePremium();
		assertThat(premiumsAndCoveragesQuoteTab.btnCalculatePremium()).isPresent(); //to validate that Error tab is not displayed and P&C tab is displayed

		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
		//-----AC#2, AC#5 Is the stove the sole source of heat?
		validateSourceOfHeatQuestion(false);

		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
		//----- AC#4, AC#6 Does the dwelling have at least one smoke detector per story?
		validateSmokeDetectorQuestion(false);

		premiumsAndCoveragesQuoteTab.saveAndExit();
		assertThat(PolicySummaryPage.labelPolicyStatus).isPresent();
	}

}


