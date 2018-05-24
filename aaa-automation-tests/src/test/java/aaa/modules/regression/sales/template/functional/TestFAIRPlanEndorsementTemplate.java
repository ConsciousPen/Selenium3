package aaa.modules.regression.sales.template.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.*;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.TimePoints;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.*;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.actiontabs.PolicyDocGenActionTab;
import aaa.main.modules.policy.home_ca.defaulttabs.*;
import aaa.main.pages.summary.MyWorkSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;
import toolkit.verification.CustomAssertions;

public class TestFAIRPlanEndorsementTemplate extends PolicyBaseTest {
	private ApplicantTab applicantTab = new ApplicantTab();
	private ReportsTab reportsTab = new ReportsTab();
	private EndorsementTab endorsementTab = new EndorsementTab();
	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
	private DocumentsTab documentsTab = new DocumentsTab();
	private BindTab bindTab = new BindTab();
	private PurchaseTab purchaseTab = new PurchaseTab();
	private ErrorTab errorTab = new ErrorTab();
	private PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
	private PolicyDocGenActionTab policyDocGenActionTab = new PolicyDocGenActionTab();

	private static final String ERROR_IS_THE_STOVE_THE_SOLE_SOURCE_OF_HEAT = "Wood burning stoves as the sole source of heat are ineligible.";
	private static final String ERROR_DOES_THE_DWELLING_HAVE_AT_LEAST_ONE_SMOKE_DETECTOR = "Dwellings with a wood burning stove without at least one smoke detector insta";

	private String formIdInXml;
	private String fairPlanEndorsementLabelInEndorsementTab;
	private PolicyType policyType;
	private DocGenEnum.Documents fairPlanEndorsementInODDTab;

	private TestFAIRPlanEndorsementTemplate() {}

	public TestFAIRPlanEndorsementTemplate(PolicyType policyType, String formIdInXml, String fairPlanEndorsementLabelInEndorsementTab, DocGenEnum.Documents fairPlanEndorsementInODDTab) {
		this.policyType = policyType;
		this.formIdInXml = formIdInXml;
		this.fairPlanEndorsementLabelInEndorsementTab = fairPlanEndorsementLabelInEndorsementTab;
		this.fairPlanEndorsementInODDTab = fairPlanEndorsementInODDTab;
	}

	@Override
	protected PolicyType getPolicyType() {
		return policyType;
	}

	///AC#1, AC#4

	public void pas13211_AC1_AC4_NB_PPC10_OtherThanLogHome_AAA_HO_CA02122017() {
		String ppcValue = "10";
		String constructionTypeValue = "Masonry";
		String licensedContractor = null; // Value for question "Is this a log home assembled by a licensed building contractor?"

		TestData testData = getTestData(ppcValue, constructionTypeValue, licensedContractor);

		mainApp().open();
		createCustomerIndividual();

		policyType.get().initiate();
		policyType.get().getDefaultView().fillUpTo(testData, PropertyInfoTab.class, true);
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());// Tab out to not fire the error "Dwellings located in PPC 10 are ineligible." for field "Roof Type"
		policyType.get().getDefaultView().fillFromTo(testData, EndorsementTab.class, BindTab.class, true);
		bindTab.submitTab();

		errorTab.verify.errorsPresent(true, ErrorEnum.Errors.ERROR_AAA_HO_CA02122017);
		errorTab.verify.errorsPresent(false, ErrorEnum.Errors.ERROR_AAA_HO_CA10100616);
		errorTab.cancel();

		switchToFAIRPlanEndorsementAndCalculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.DOCUMENTS.get());
		documentsTab.getDocumentsToIssueAssetList().getAsset(HomeCaMetaData.DocumentsTab.DocumentsToIssue.FAIR_PLAN_COMPANION_ENDORSEMENT_CALIFORNIA).setValue("Physically Signed");
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
		bindTab.submitTab();
		purchaseTab.fillTab(testData);
		purchaseTab.submitTab();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		generateRenewalImageAndRetrievePolicyRMinus67(getTimePoints());

		//AC#4
		PolicySummaryPage.buttonTasks.click();
		MyWorkSummaryPage.openAllQueuesSection();

		validateThatTaskIsNotGenerated("PPC 10"); //Exact name of task is Unknown, hence looking for Task Name containing "PPC 10"

	}

	//Note: Not applicable for Midterm Endorsement, because it is not possible to change PPC at Midterm Endorsement.

	public void pas13211_AC1_Renewal_PPC10_OtherThanLogHome_AAA_HO_CA02122017(TestData tdAddress) {
		String ppcValue = "5";
		String constructionTypeValue = "Masonry";
		String licensedContractor = null; // Value for question "Is this a log home assembled by a licensed building contractor?"

		TestData testData = getTestData(ppcValue, constructionTypeValue, licensedContractor);

		mainApp().open();
		createCustomerIndividual();

		createPolicy(testData);

		generateRenewalImageAndRetrievePolicy(getTimePoints());
		PolicySummaryPage.buttonRenewals.click();
		policyType.get().dataGather().start();

		changeAddressOrderPPCRateAndNavigateToBindTab(tdAddress);

		//Note: No need to validate that the Rule is fired if FAIR Plan Endorsement is added, because as per current implementation it is not fired for renewals

		switchToFAIRPlanEndorsementAndBind();

	}

	///AC#2

	public void pas13211_AC2_NB_PPC10_LogHome_AAA_HO_CA10100616() {
		String ppcValue = "9";
		String constructionTypeValue = "Log Home";
		String licensedContractor = "Yes"; // Value for question "Is this a log home assembled by a licensed building contractor?"

		TestData testData = getTestData(ppcValue, constructionTypeValue, licensedContractor);

		mainApp().open();
		createCustomerIndividual();

		policyType.get().initiate();
		policyType.get().getDefaultView().fillUpTo(testData, PropertyInfoTab.class, true);
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());// Tab out to not fire the error "Dwellings located in PPC 10 are ineligible." for field "Roof Type"
		policyType.get().getDefaultView().fillFromTo(testData, EndorsementTab.class, BindTab.class, true);
		bindTab.submitTab();

		errorTab.verify.errorsPresent(false, ErrorEnum.Errors.ERROR_AAA_HO_CA02122017);
		errorTab.verify.errorsPresent(true, ErrorEnum.Errors.ERROR_AAA_HO_CA10100616);
		errorTab.cancel();

		switchToFAIRPlanEndorsementAndCalculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.DOCUMENTS.get());
		documentsTab.getDocumentsToIssueAssetList().getAsset(HomeCaMetaData.DocumentsTab.DocumentsToIssue.FAIR_PLAN_COMPANION_ENDORSEMENT_CALIFORNIA).setValue("Physically Signed");
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
		bindTab.submitTab();
		purchaseTab.fillTab(testData);
		purchaseTab.submitTab();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

	}

	public void pas13211_AC2_MidtermEndorsement_PPC8X_LogHome_AAA_HO_CA10100616() {
		String ppcValue = "8X";
		String constructionTypeValue = "Masonry";
		String licensedContractor = null; // Value for question "Is this a log home assembled by a licensed building contractor?"

		TestData testData = getTestData(ppcValue, constructionTypeValue, licensedContractor);

		mainApp().open();
		createCustomerIndividual();

		createPolicy(testData);

		policyType.get().endorse().perform(getStateTestData(testDataManager.policy.get(policyType).getTestData("Endorsement"), "TestData_Plus3Days"));
		switchToLogHomeAndNavigateToBind("Yes");
		bindTab.submitTab();

		errorTab.verify.errorsPresent(false, ErrorEnum.Errors.ERROR_AAA_HO_CA02122017);
		errorTab.verify.errorsPresent(true, ErrorEnum.Errors.ERROR_AAA_HO_CA10100616);
		errorTab.cancel();

		switchToFAIRPlanEndorsementAndBind();

	}

	public void pas13211_AC2_Renewal_PPC1X_LogHome_AAA_HO_CA10100616() {
		String ppcValue = "1X";
		String constructionTypeValue = "Masonry";
		String licensedContractor = null; // Value for question "Is this a log home assembled by a licensed building contractor?"

		TestData testData = getTestData(ppcValue, constructionTypeValue, licensedContractor);

		mainApp().open();
		createCustomerIndividual();

		createPolicy(testData);

		generateRenewalImageAndRetrievePolicy(getTimePoints());
		PolicySummaryPage.buttonRenewals.click();
		policyType.get().dataGather().start();

		switchToLogHomeAndNavigateToBind("Yes");
		bindTab.submitTab();

		errorTab.verify.errorsPresent(false, ErrorEnum.Errors.ERROR_AAA_HO_CA02122017);
		errorTab.verify.errorsPresent(true, ErrorEnum.Errors.ERROR_AAA_HO_CA10100616);
		errorTab.cancel();

		switchToFAIRPlanEndorsementAndBind();

	}

	///////AC#3

	public void pas13211_AC3_NB_PPC10_LogHome_AAA_HO_CA10100616() {
		String ppcValue = "9";
		String constructionTypeValue = "Log Home";
		String licensedContractor = "No"; // Value for question "Is this a log home assembled by a licensed building contractor?"

		TestData testData = getTestData(ppcValue, constructionTypeValue, licensedContractor);

		mainApp().open();
		createCustomerIndividual();

		policyType.get().initiate();
		policyType.get().getDefaultView().fillUpTo(testData, PropertyInfoTab.class, true);
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());// Tab out to not fire the error "Dwellings located in PPC 10 are ineligible." for field "Roof Type"
		policyType.get().getDefaultView().fillFromTo(testData, EndorsementTab.class, BindTab.class, true);

		validateRuleIsFiredWithAndWithoutFAIRPlanEndorsement(); //because rule should still fire if "Is this a log home assembled by a licensed building contractor?" = "No"

		purchaseTab.fillTab(testData);
		purchaseTab.submitTab();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

	}

	public void pas13211_AC3_MidtermEndorsement_PPC8X_LogHome_AAA_HO_CA10100616() {
		String ppcValue = "8X";
		String constructionTypeValue = "Masonry";
		String licensedContractor = null; // Value for question "Is this a log home assembled by a licensed building contractor?"

		TestData testData = getTestData(ppcValue, constructionTypeValue, licensedContractor);

		mainApp().open();
		createCustomerIndividual();

		createPolicy(testData);

		policyType.get().endorse().perform(getStateTestData(testDataManager.policy.get(policyType).getTestData("Endorsement"), "TestData_Plus3Days"));
		switchToLogHomeAndNavigateToBind("No");

		validateRuleIsFiredWithAndWithoutFAIRPlanEndorsement(); //because rule should still fire if "Is this a log home assembled by a licensed building contractor?" = "No"

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

	}

	public void pas13211_AC3_Renewal_PPC1X_LogHome_AAA_HO_CA10100616() {
		String ppcValue = "1X";
		String constructionTypeValue = "Masonry";
		String licensedContractor = null; // Value for question "Is this a log home assembled by a licensed building contractor?"

		TestData testData = getTestData(ppcValue, constructionTypeValue, licensedContractor);

		mainApp().open();
		createCustomerIndividual();

		createPolicy(testData);

		generateRenewalImageAndRetrievePolicy(getTimePoints());
		PolicySummaryPage.buttonRenewals.click();
		policyType.get().dataGather().start();

		switchToLogHomeAndNavigateToBind("No");

		validateRuleIsFiredWithAndWithoutFAIRPlanEndorsement(); //because rule should still fire if "Is this a log home assembled by a licensed building contractor?" = "No"

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

	}

	////////////Start PAS-13242////////////////

	public void pas13242_pas14193_AC1_NB(TestData tdWithFAIRPlanEndorsement) {

		mainApp().open();
		createCustomerIndividual();

		createPolicy(tdWithFAIRPlanEndorsement);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		validateDocumentIsGeneratedInPackage(policyNumber, POLICY_ISSUE);

		//5. Validate that Subsequent transactions does not contain the form FPCECA
		//Perform mid-term endorsement, but don't switch away from FAIR Plan Endorsement
		policyType.get().endorse().perform(getStateTestData(testDataManager.policy.get(policyType).getTestData("Endorsement"), "TestData_Plus3Days"));

		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE).setValue("contains=1,500");
		premiumsAndCoveragesQuoteTab.calculatePremium();

		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
		new BindTab().submitTab();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		validateDocumentIsNotGeneratedInPackage(policyNumber, ENDORSEMENT_ISSUE, true);

		//PAS-14193
		//Validate that form FPCECA is generated at renewal, and is listed in other documents
		generateRenewalOfferAtOfferGenDate();
		validateDocumentIsGeneratedInPackage(policyNumber, RENEWAL_OFFER);

		//7. Make Renewal image endorsement without removing FAIR plan endorsement
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policyType.get().endorse().perform(getStateTestData(testDataManager.policy.get(policyType).getTestData("Endorsement"), "TestData"));

		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE).setValue("contains=1,000");
		premiumsAndCoveragesQuoteTab.calculatePremium();

		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
		new BindTab().submitTab();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		// 8. Validate that form FPCECA is included in revised renewal package and is also listed in other documents
		validateDocumentIsGeneratedInPackage(policyNumber, RENEWAL_OFFER);
	}

	public void pas13242_AC2_Endorsement() {

		// Create policy without FAIR Plan Endorsement
		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		validateDocumentIsNotGeneratedInPackage(policyNumber, POLICY_ISSUE, false);

		//Perform mid-term endorsement and add FAIR Plan Endorsement
		policyType.get().endorse().perform(getStateTestData(testDataManager.policy.get(policyType).getTestData("Endorsement"), "TestData_Plus3Days"));
		switchToFAIRPlanEndorsementAndBind();

		validateDocumentIsGeneratedInPackage(policyNumber, ENDORSEMENT_ISSUE);
	}

	public void pas13242_AC3_Renewal() {

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
		policyType.get().dataGather().start();

		//4. Switch to FAIR Plan Endorsement and Bind
		switchToFAIRPlanEndorsementAndBind();

		TimeSetterUtil.getInstance().nextPhase(renewalProposalDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		//JobUtils.executeJob(Jobs.aaaDocGenBatchJob);//not necessary - can be used if QA needs actual generated xml files

		validateDocumentIsGeneratedInPackage(policyNumber, RENEWAL_OFFER);
	}

	public void pas13242_AC3_Revised_Renewal_After_Renewal_Term_Change() {

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
		policyType.get().dataGather().start();

		//4. Switch to FAIR Plan Endorsement
		switchToFAIRPlanEndorsementAndBind();

		//JobUtils.executeJob(Jobs.aaaDocGenBatchJob);//not necessary - can be used if QA needs actual generated xml files
		validateDocumentIsGeneratedInPackage(policyNumber, RENEWAL_OFFER);
	}

	public void pas13242_pas14193_AC3_Revised_Renewal_After_Current_Term_Change() {

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
		policyType.get().endorse().perform(getStateTestData(testDataManager.policy.get(policyType).getTestData("Endorsement"), "TestData_Plus3Days"));

		//4. Switch FAIR Plan Endorsement
		switchToFAIRPlanEndorsementAndBind();
		//JobUtils.executeJob(Jobs.aaaDocGenBatchJob);//not necessary - can be used if QA needs actual generated xml files

		CustomAssert.enableSoftMode();

		//6. Validate that form FPCECA is included in Endorsement package
		//8. Validate that form FPCECA is included in Endorsement package only once
		validateDocumentIsGeneratedInPackage(policyNumber, ENDORSEMENT_ISSUE);

		//7. Validate that form FPCECA is included in Renewal package
		validateDocumentIsGeneratedInPackage(policyNumber, RENEWAL_OFFER); //PAS-14193

	}

	////////////End PAS-13242////////////////

	////////////Start PAS-13216////////////////

	public void pas13216_All_ACs_NB() {

		TestData testData = getPolicyDefaultTD();

		mainApp().open();
		createCustomerIndividual();

		policyType.get().initiate();
		policyType.get().getDefaultView().fillUpTo(testData, PropertyInfoTab.class, true);
		stoveQuestionValidationSteps();

	}

	public void pas13216_All_ACs_Endorsement() {
		TestData testData = getPolicyDefaultTD();

		mainApp().open();
		createCustomerIndividual();

		createPolicy(testData);
		policyType.get().endorse().perform(getStateTestData(testDataManager.policy.get(policyType).getTestData("Endorsement"), "TestData_Plus3Days"));
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
		stoveQuestionValidationSteps();
	}

	public void pas13216_All_ACs_Renewal() {
		TestData testData = getPolicyDefaultTD();

		mainApp().open();
		createCustomerIndividual();

		createPolicy(testData);

		generateRenewalImageAndRetrievePolicy(getTimePoints());
		PolicySummaryPage.buttonRenewals.click();
		policyType.get().dataGather().start();

		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
		stoveQuestionValidationSteps();
	}

	/////////////Start PAS-14004////////////////
	public void pas14004_AC1_AC2_Quote(TestData tdWithFAIRPlanEndorsement) {
		mainApp().open();
		createCustomerIndividual();

		policyType.get().initiate();
		policyType.get().getDefaultView().fillUpTo(tdWithFAIRPlanEndorsement, PremiumsAndCoveragesQuoteTab.class, true);
		premiumsAndCoveragesQuoteTab.saveAndExit();
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		policyType.get().quoteDocGen().start();

		//TODO-mstrazds:select document and valdiate that Central Print is disabled

		policyDocGenActionTab.verify.documentsPresent(true, fairPlanEndorsementInODDTab);
		policyDocGenActionTab.verify.documentsEnabled(true, fairPlanEndorsementInODDTab);

		policyDocGenActionTab.generateDocuments(DocGenEnum.DeliveryMethod.LOCAL_PRINT, fairPlanEndorsementInODDTab);

		//TODO-mstrazds:validate that docuemnt is generated in xml
		validateDocumentIsGeneratedInPackage(policyNumber, ADHOC_DOC_ON_DEMAND_GENERATE);

		//assertThat(policyDocGenActionTab.getAssetList().getAsset(HomeCaMetaData.PolicyDocGenActionTab.DELIVERY_METHOD).getAllValues())
		//policyDocGenActionTab.getAssetList().getAsset(HomeCaMetaData.PolicyDocGenActionTab.DELIVERY_METHOD).
		//policyDocGenActionTab.getAssetList().getAsset(HomeCaMetaData.PolicyDocGenActionTab.DELIVERY_METHOD).

	}

	public void pas14004_AC1_AC2_Quote_negative() {

	}

	public void pas14004_AC1_AC2_Policy(TestData tdWithFAIRPlanEndorsement) {

	}

	public void pas14004_AC1_AC2_Policy_negative() {

	}
	////////////End PAS-14004////////////////

	private void validateSmokeDetectorQuestion(boolean ruleShouldFire) {
		fillStovesSection("Yes", "No", "Yes", "Yes");

		propertyInfoTab.getStovesAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Stoves.DOES_THE_DWELLING_HAVE_AT_LEAST_ONE_SMOKE_DETECTOR_PER_STORY).setValue("No");

		if (ruleShouldFire) {
			assertThat(propertyInfoTab.getStovesAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Stoves.DOES_THE_DWELLING_HAVE_AT_LEAST_ONE_SMOKE_DETECTOR_PER_STORY).getWarning().get()
					.contains(ERROR_DOES_THE_DWELLING_HAVE_AT_LEAST_ONE_SMOKE_DETECTOR));
		} else {
			assertThat(propertyInfoTab.getStovesAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Stoves.DOES_THE_DWELLING_HAVE_AT_LEAST_ONE_SMOKE_DETECTOR_PER_STORY).getWarning().get()
					.contains(ERROR_DOES_THE_DWELLING_HAVE_AT_LEAST_ONE_SMOKE_DETECTOR)).isFalse();
		}

		propertyInfoTab.submitTab();
		endorsementTab.submitTab();
		premiumsAndCoveragesQuoteTab.calculatePremium();

		if (ruleShouldFire) {
			assertThat(errorTab.tableErrors.getRow(1).getCell("Message").getValue()).contains(ERROR_DOES_THE_DWELLING_HAVE_AT_LEAST_ONE_SMOKE_DETECTOR);
			assertThat(errorTab.tableErrors.getRowsCount()).isEqualTo(1); //assert that there are no other messages
			errorTab.cancel();
		}

		CustomAssertions.assertThat(premiumsAndCoveragesQuoteTab.btnCalculatePremium()).isPresent(); //to validate that Error tab is not displayed and P&C tab is displayed

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

		CustomAssertions.assertThat(premiumsAndCoveragesQuoteTab.btnCalculatePremium()).isPresent(); //to validate that Error tab is not displayed and P&C tab is displayed

	}

	private void fillStovesSection(String isWoodBurningStove, String isSourceOfHeat, String isLicensedContractor, String smokeDetector) {
		propertyInfoTab.getStovesAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Stoves.DOES_THE_PROPERTY_HAVE_A_WOOD_BURNING_STOVE).setValue(isWoodBurningStove);
		propertyInfoTab.getStovesAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Stoves.IS_THE_STOVE_THE_SOLE_SOURCE_OF_HEAT).setValue(isSourceOfHeat);
		propertyInfoTab.getStovesAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Stoves.WAS_THE_STOVE_INSTALLED_BY_A_LICENSED_CONTRACTOR).setValue(isLicensedContractor);
		propertyInfoTab.getStovesAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Stoves.DOES_THE_DWELLING_HAVE_AT_LEAST_ONE_SMOKE_DETECTOR_PER_STORY).setValue(smokeDetector);
	}

	////////////End PAS-13216////////////////

	private TestData getTestData(String ppcValue, String constructionTypeValue, String licensedContractor) {
		TestData testData = getPolicyDefaultTD();

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
				HomeCaMetaData.PropertyInfoTab.Construction.ROOF_TYPE.getLabel()),
				"Wood shingle/Wood shake");
		testData.adjust(TestData.makeKeyPath(HomeCaMetaData.PropertyInfoTab.class.getSimpleName(),
				HomeCaMetaData.PropertyInfoTab.Construction.class.getSimpleName(),
				HomeCaMetaData.PropertyInfoTab.Construction.IS_THIS_A_LOG_HOME_ASSEMBLED_BY_A_LICENSED_BUILDING_CONTRACTOR.getLabel()),
				licensedContractor);

		return testData;
	}

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

	private void generateRenewalImageAndRetrievePolicyRMinus67(TimePoints timePoints) {
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

	private void switchToLogHomeAndNavigateToBind(String licensedBuildingContractorValue) {
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
		propertyInfoTab.getConstructionAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Construction.CONSTRUCTION_TYPE).setValueStarts("Log Home");
		propertyInfoTab.getConstructionAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Construction.IS_THIS_A_LOG_HOME_ASSEMBLED_BY_A_LICENSED_BUILDING_CONTRACTOR)
				.setValue(licensedBuildingContractorValue);
		propertyInfoTab.submitTab();

		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsAndCoveragesQuoteTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.DOCUMENTS.get());
		if (documentsTab.getDocumentsToIssueAssetList().getAsset(HomeCaMetaData.DocumentsTab.DocumentsToIssue.FAIR_PLAN_COMPANION_ENDORSEMENT_CALIFORNIA).isPresent()) {
			documentsTab.getDocumentsToIssueAssetList().getAsset(HomeCaMetaData.DocumentsTab.DocumentsToIssue.FAIR_PLAN_COMPANION_ENDORSEMENT_CALIFORNIA).setValue("Physically Signed");
		}
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
	}

	private void changeAddressOrderPPCRateAndNavigateToBindTab(TestData tdAddress) {
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.APPLICANT.get());
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
		assertThat(docs.stream().map(Document::getTemplateId).toArray()).contains(formIdInXml);

		//Create list of documents other than FPCECA
		List<Document> docsOther = docs.stream().filter(document -> !document.getTemplateId().equals(formIdInXml)).collect(Collectors.toList());

		//Validate that form FPCECA is listed in other documents (test validates that at least in one other document)
		assertThat(docsOther.stream().filter(document -> document.toString().contains(formIdInXml)).toArray().length).isGreaterThan(0);

		//Validate that form FPCECA is included in Document Package only once
		assertThat(docs.stream().filter(document -> document.getTemplateId().equals(formIdInXml)).toArray().length).isEqualTo(1);

	}

	private void validateDocumentIsNotGeneratedInPackage(String policyNumber, AaaDocGenEntityQueries.EventNames eventName, boolean shouldBeListedInOtherDocs) {
		List<Document> docs = DocGenHelper.getDocumentsList(policyNumber, eventName);
		assertThat(docs.stream().map(Document::getTemplateId).toArray()).doesNotContain(formIdInXml);

		//Create list of documents other than FPCECA
		List<Document> docsOther = docs.stream().filter(document -> !document.getTemplateId().equals(formIdInXml)).collect(Collectors.toList());

		//Validate that document FPCECA is/is not listed in other documents (test validates that FPCECA is listed at least in one other document)
		if (shouldBeListedInOtherDocs) {
			assertThat(docsOther.stream().filter(document -> document.toString().contains(formIdInXml)).toArray().length).isGreaterThan(0);

		} else {
			assertThat(docsOther.stream().filter(document -> document.toString().contains(formIdInXml)).toArray().length).isEqualTo(0);

		}
	}

	private void generateRenewalOfferAtOfferGenDate() {
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		LocalDateTime renewInitiationDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewInitiationDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		//JobUtils.executeJob(Jobs.aaaDocGenBatchJob);//not necessary - can be used if QA needs actual generated xml files
	}

	private void switchToFAIRPlanEndorsement() {
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());//navigates to Endorsement Tab
		endorsementTab.getAddEndorsementLink(fairPlanEndorsementLabelInEndorsementTab).click();
		Page.dialogConfirmation.confirm();
		endorsementTab.btnSaveForm.click();
	}

	private void switchToFAIRPlanEndorsementAndCalculatePremium() {
		switchToFAIRPlanEndorsement();
		premiumsAndCoveragesQuoteTab.calculatePremium();
	}

	private void switchToFAIRPlanEndorsementAndBind() {
		switchToFAIRPlanEndorsementAndCalculatePremium();
		//Bind
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.DOCUMENTS.get());
		documentsTab.getDocumentsToIssueAssetList().getAsset(HomeCaMetaData.DocumentsTab.DocumentsToIssue.FAIR_PLAN_COMPANION_ENDORSEMENT_CALIFORNIA).setValue("Physically Signed");
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
		new BindTab().submitTab();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	private void switchToFAIRPlanEndorsementAndNavigateToBindTab() {
		switchToFAIRPlanEndorsementAndCalculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.DOCUMENTS.get());
		documentsTab.getDocumentsToIssueAssetList().getAsset(HomeCaMetaData.DocumentsTab.DocumentsToIssue.FAIR_PLAN_COMPANION_ENDORSEMENT_CALIFORNIA).setValue("Physically Signed");
		//Bind
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());

	}

	private void switchAwayFromFAIRPlanEndorsement() {
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());//navigates to Endorsement Tab
		endorsementTab.getRemoveEndorsementLink(fairPlanEndorsementLabelInEndorsementTab, 1).click();
		Page.dialogConfirmation.confirm();

		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsAndCoveragesQuoteTab.calculatePremium();

	}

	////////////End PAS-13242 methods////////////////

	private void stoveQuestionValidationSteps() {
		//-----AC#1, AC#7 Is the stove the sole source of heat?
		validateSourceOfHeatQuestion(true);

		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());

		//----- AC#3, AC#8 Does the dwelling have at least one smoke detector per story?
		validateSmokeDetectorQuestion(true);

		switchToFAIRPlanEndorsement();

		endorsementTab.submitTab();
		premiumsAndCoveragesQuoteTab.calculatePremium();
		CustomAssertions.assertThat(premiumsAndCoveragesQuoteTab.btnCalculatePremium()).isPresent(); //to validate that Error tab is not displayed and P&C tab is displayed

		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
		//-----AC#2, AC#5 Is the stove the sole source of heat?
		validateSourceOfHeatQuestion(false);

		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
		//----- AC#4, AC#6 Does the dwelling have at least one smoke detector per story?
		validateSmokeDetectorQuestion(false);

		premiumsAndCoveragesQuoteTab.saveAndExit();
		CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).isPresent();
	}

	private void validateThatTaskIsNotGenerated(String rulePartialName) {
		assertThat(MyWorkSummaryPage.tableTasks.getValuesFromRows(MyWorkConstants.MyWorkTasksTable.TASK_NAME).toString().contains(rulePartialName)).isFalse();
		while (MyWorkSummaryPage.tableTasks.getPagination().hasNextPage()) {
			MyWorkSummaryPage.tableTasks.getPagination().goToNextPage();
			assertThat(MyWorkSummaryPage.tableTasks.getValuesFromRows(MyWorkConstants.MyWorkTasksTable.TASK_NAME).toString().contains(rulePartialName)).isFalse();
		}
	}

	private void validateRuleIsFiredWithAndWithoutFAIRPlanEndorsement() {
		bindTab.submitTab();

		errorTab.verify.errorsPresent(false, ErrorEnum.Errors.ERROR_AAA_HO_CA02122017);
		errorTab.verify.errorsPresent(true, ErrorEnum.Errors.ERROR_AAA_HO_CA10100616);
		errorTab.cancel();

		switchToFAIRPlanEndorsementAndNavigateToBindTab();

		bindTab.submitTab();
		errorTab.verify.errorsPresent(false, ErrorEnum.Errors.ERROR_AAA_HO_CA02122017);
		errorTab.verify.errorsPresent(true, ErrorEnum.Errors.ERROR_AAA_HO_CA10100616);
		errorTab.overrideAllErrors();
		errorTab.submitTab();
	}

}
