package aaa.modules.regression.sales.auto_ss.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.*;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.FormsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.customcontrols.endorsements.AutoSSForms;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.CheckBox;

@StateList(states = Constants.States.MD)
public class TestEUIMForms extends AutoSSBaseTest {

	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private FormsTab formsTab = new FormsTab();
	private CheckBox enhancedUIM = new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.ENHANCED_UIM);
	private final String formId = DocGenEnum.Documents.AAEUIMMD.getIdInXml();
	private final String formDesc = DocGenEnum.Documents.AAEUIMMD.getName();

	/**
	 *@author Josh Carpenter
	 *@name Test that form AAEUIMMD is displayed on the P&C and Forms tabs with MD EUIM coverage for NB
	 *@scenario
	 * 1. Create Customer
	 * 2. Initiate Auto SS MD Quote after 07/01/2018
	 * 3. Select EUIM coverage in the 'Policy Level Liability Coverages' section of the P&C page
	 * 3. Calculate premium
	 * 4. Verify form AAEUIMMD is displayed in the 'Forms' section of the P&C page and associated Term Premium is 0.00
	 * 5. Navigate to the Forms tab
	 * 6. Verify form AAEUIMMD 07 18 is displayed and the description is 'Maryland Enhanced Uninsured Motorist Coverage'
	 * 7. Save and Exit
	 * 8. Access 'Generate On Demand Document' page
	 * 9. Verify form AAEUIMMD is available for generation
	 *@details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-11302")
	public void pas11302_testEUIMMDFormNB(@Optional("MD") String state) {

		TestData tdEUIM = getPolicyTD().adjust(PremiumAndCoveragesTab.class.getSimpleName(), getTestSpecificTD("PremiumAndCoveragesTab_NB"));

		mainApp().open();
		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(tdEUIM, PremiumAndCoveragesTab.class, true);
		verifyFormsAndAmount();

	}

	/**
	 *@author Josh Carpenter
	 *@name Test that form AAEUIMMD is displayed on the P&C and Forms tabs with MD EUIM coverage for Conversion
	 *@scenario
	 * 1. Create Customer
	 * 2. Initiate Auto SS MD Conversion Quote after 07/01/2018
	 * 3. Select EUIM coverage in the 'Policy Level Liability Coverages' section of the P&C page
	 * 3. Calculate premium
	 * 4. Verify form AAEUIMMD is displayed in the 'Forms' section of the P&C page and associated Term Premium is 0.00
	 * 5. Navigate to the Forms tab
	 * 6. Verify form AAEUIMMD 07 18 is displayed and the description is 'Maryland Enhanced Uninsured Motorist Coverage'
	 * 7. Save and Exit
	 * 8. Access 'Generate On Demand Document' page
	 * 9. Verify form AAEUIMMD is available for generation
	 *@details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Conversions.AUTO_SS, testCaseId = "PAS-11302")
	public void pas11302_testEUIMMDFormConversion(@Optional("MD") String state) {

		TestData tdEUIM = getConversionPolicyDefaultTD().adjust(PremiumAndCoveragesTab.class.getSimpleName(), getTestSpecificTD("PremiumAndCoveragesTab_Conv"));

		mainApp().open();
		createCustomerIndividual();

		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());
		policy.getDefaultView().fillUpTo(tdEUIM, PremiumAndCoveragesTab.class, true);
		verifyFormsAndAmount();

	}

	/**
	 *@author Josh Carpenter
	 *@name Test that form AAEUIMMD is available for generation with MD EUIM coverage for Endorsements
	 *@scenario
	 * 1. Create Customer
	 * 2. Create Auto SS MD Policy after 07/01/2018 with Standard UIM coverage
	 * 3. Do a Midterm endorsement and switch UIM to EUIM coverage
	 * 3. Re-calculate premium and bind endorsement
	 * 4. Access 'Generate On Demand Document' page
	 * 5. Verify form AAEUIMMD is available for generation
	 *@details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = "PAS-11302")
	public void pas11302_testEUIMMDFormEndorsement(@Optional("MD") String state) {

		// Create policy with Standard UIM coverage
		mainApp().open();
		createCustomerIndividual();
		createPolicy();

		//Perform mid-term endorsement and switch to EUIM coverage
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));
		switchToEUIMCoverage();
		verifyFormsAndAmount();

	}

	/**
	 *@author Josh Carpenter
	 *@name Test that form AAEUIMMD is available for generation with MD EUIM coverage during Renewal
	 *@scenario
	 * 1. Create Customer
	 * 2. Create Auto SS MD Policy after 07/01/2018 with Standard UIM coverage
	 * 3. Initiate Renewal process
	 * 4. Change UIM to EUIM coverage for renewal
	 * 5. Re-calculate premium and propose
	 * 4. Access 'Generate On Demand Document' page
	 * 5. Verify form AAEUIMMD is available for generation
	 *@details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Renewal.AUTO_SS, testCaseId = "PAS-11302")
	public void pas11302_testEUIMMDFormRenewal(@Optional("MD") String state) {

		// Create policy with Standard UIM coverage
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();
		LocalDateTime expDate = PolicySummaryPage.getExpirationDate();
		mainApp().close();
		setDoNotRenewFlag(policyNumber);

		// Create renewal and switch to EUIM coverage
		TimeSetterUtil.getInstance().nextPhase(expDate.minusDays(45));
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.removeDoNotRenew().perform(getPolicyTD("DoNotRenew", "TestData"));
		policy.renew().perform();
		switchToEUIMCoverage();
		verifyFormsAndAmount();

	}
	////////////////////////////////Start PAS-12466 ///////////////////////////////////////////

	/**
	 *@author Maris Strazds
	 *@name Test that form AAEUIMMD is included in New Business Package
	 *@scenario
	 * 1. Create Customer
	 * 2. Create Auto SS MD Policy after 07/01/2018 with EUIM Coverage coverage
	 * 3. Validate that form AAEUIMMD is included in New Business Package
	 * 4. Validate that form AAEUIMMD is included in New Business Package only once
	 * 5. Validate that form AAEUIMMD is not included in Subsequent transactions, but it is listed in related documents (Make midterm endorsement
	 *    but do not switch away from EUIM Coverage and validate)
	 * 6. Validate that form AAEUIMMD is not generated at renewal, but it is listed in other documents
	 *@details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12466")
	public void pas12466_AC1_NB(@Optional("MD") String state) {

		TestData tdEUIM = getPolicyTD().adjust(PremiumAndCoveragesTab.class.getSimpleName(), getTestSpecificTD("PremiumAndCoveragesTab_NB"));

		mainApp().open();
		createCustomerIndividual();

		createPolicy(tdEUIM);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		validateDocumentIsGeneratedInPackage(policyNumber, POLICY_ISSUE);

		//5. Validate that Subsequent transactions does not contain the form AAEUIMMD
		//Perform mid-term endorsement, but don't switch away from EUIM coverage
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE).setValue("contains=750");
		premiumAndCoveragesTab.calculatePremium();

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		validateDocumentIsNotGeneratedInPackage(policyNumber, ENDORSEMENT_ISSUE, true);

		//Validate that form AAEUIMMD is not generated at renewal, but it is listed in other documents
		generateRenewalOfferAtOfferGenDate();
		validateDocumentIsNotGeneratedInPackage(policyNumber, RENEWAL_OFFER, true);
	}

	/**
	 *@author Maris Strazds
	 *@name Test that form AAEUIMMD is included in Endorsement Package
	 *@scenario
	 * 1. Create Customer
	 * 2. Create Auto SS MD Policy after 07/01/2018 with Standard UIM coverage
	 * 3. Do a Midterm endorsement and switch UIM to EUIM coverage
	 * 4. Re-calculate premium and bind endorsement
	 * 5. Validate that form AAEUIMMD is included in Endorsement Package
	 * 6. Validate that form AAEUIMMD is included in Endorsement Package only once
	 *@details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12466")
	public void pas12466_AC2_Endorsement(@Optional("MD") String state) {

		// Create policy with Standard UIM coverage
		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		validateDocumentIsNotGeneratedInPackage(policyNumber, POLICY_ISSUE, false);

		//Perform mid-term endorsement and switch to EUIM coverage
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));
		switchToEUIMCoverageAndBind();

		validateDocumentIsGeneratedInPackage(policyNumber, ENDORSEMENT_ISSUE);
	}

	/**
	 *@author Maris Strazds
	 *@name Test that form AAEUIMMD is included in Renewal Package for Conversion
	 *@scenario
	 * 1. Create Customer
	 * 2. Initiate Auto SS MD Conversion Quote after 07/01/2018
	 * 3. Select EUIM coverage in the 'Policy Level Liability Coverages' section of the P&C page
	 * 4. Calculate premium and Bind
	 * 5. Validate that form AAEUIMMD is included in Renewal Package
	 * 6. Validate that form AAEUIMMD is included in Renewal Package only once
	 *@details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12466")
	public void pas12466_AC3_Conversion(@Optional("MD") String state) {

		TestData tdEUIM = getConversionPolicyDefaultTD().adjust(PremiumAndCoveragesTab.class.getSimpleName(), getTestSpecificTD("PremiumAndCoveragesTab_Conv"));

		mainApp().open();
		createCustomerIndividual();

		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());
		policy.getDefaultView().fill(tdEUIM);
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.POLICY.get());
		String policyNumber = PolicySummaryPage.tablePolicyList.getRow(1).getCell(1).getValue();
		//JobUtils.executeJob(Jobs.aaaDocGenBatchJob); //not necessary - can be used if QA needs actual generated xml files

		validateDocumentIsGeneratedInPackage(policyNumber, RENEWAL_OFFER);
	}

	/**
	 *@author Maris Strazds
	 *@name Test that form AAEUIMMD is included in Renewal Package
	 *@scenario
	 * 1. Create Customer
	 * 2. Create Auto SS MD Policy after 07/01/2018 with Standard UIM coverage
	 * 3. Generate renewal image and retrieve it in Data Gathering mode (before Renewal Document package is generated)
	 * 4. Switch UIM to EUIM coverage
	 * 5. Re-calculate premium and bind
	 * 6. Generate Renewal Document Package at Renewal offer generation date
	 * 7. Validate that form AAEUIMMD is included in Renewal Package
	 * 8. Validate that form AAEUIMMD is included in Renewal package only once
	 *@details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12466")
	public void pas12466_AC3_Renewal(@Optional("MD") String state) {

		// Create policy with Standard UIM coverage
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();
		setDoNotRenewFlag(policyNumber);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		validateDocumentIsNotGeneratedInPackage(policyNumber, POLICY_ISSUE, false);

		//3. Create renewal image
		mainApp().close();
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.minusDays(45));
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.removeDoNotRenew().perform(getPolicyTD("DoNotRenew", "TestData"));
		policy.renew().perform();

		//4. Switch UIM to EUIM coverage and Bind
		switchToEUIMCoverageAndBind();

		mainApp().close();
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.minusDays(35));
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);

		validateDocumentIsGeneratedInPackage(policyNumber, RENEWAL_OFFER);
	}

	/**
	 *@author Maris Strazds
	 *@name Test that form AAEUIMMD is included in Renewal package, if endorsement is made for Renewal term when Renewal Document package is already generated (i.e Revised renewal)
	 *@scenario
	 * 1. Create Customer
	 * 2. Create Auto SS MD Policy after 07/01/2018 with Standard UIM coverage
	 * 3. Generate Renewal Document package at Renewal Offer Generation Date
	 * 4. Switch UIM to EUIM coverage for RENEWAL term
	 * 5. Re-calculate premium and bind endorsement
	 * 6. Validate that form AAEUIMMD is included in Renewal package
	 * 7. Validate that form AAEUIMMD is included in Renewal package only once
	 *@details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12466")
	public void pas12466_AC3_Revised_Renewal_After_Renewal_Term_Change(@Optional("MD") String state) {

		// Create policy with Standard UIM coverage
		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		generateRenewalOfferAtOfferGenDate();
		validateDocumentIsNotGeneratedInPackage(policyNumber, RENEWAL_OFFER, false);

		//Reopen app and retrieve policy by number
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonRenewals.click();

		//Retrieve Renewal image in data gathering mode
		policy.dataGather().start();

		//4. Switch UIM to EUIM coverage
		switchToEUIMCoverageAndBind();

		//JobUtils.executeJob(Jobs.aaaDocGenBatchJob);//not necessary - can be used if QA needs actual generated xml files
		validateDocumentIsGeneratedInPackage(policyNumber, RENEWAL_OFFER);
	}

	/**
	 *@author Maris Strazds
	 *@name Test that form AAEUIMMD is included in Endorsement package, but not in Renewal Package,
	 *  if endorsement is made for current term when Renewal Document package is already generated (i.e. revised renewal)
	 *@scenario
	 * 1. Create Customer
	 * 2. Create Auto SS MD Policy after 07/01/2018 with Standard UIM coverage
	 * 3. Generate Renewal Document package at Renewal Offer Generation Date
	 * 4. Switch UIM to EUIM coverage for CURRENT term
	 * 5. Re-calculate premium and bind endorsement
	 * 6. Validate that form AAEUIMMD is included in Endorsement package
	 * 7. Validate that form AAEUIMMD is not included in Renewal package
	 * 8. Validate that form AAEUIMMD is included in Endorsement package only once
	 *@details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12466")
	public void pas12466_AC3_Revised_Renewal_After_Current_Term_Change(@Optional("MD") String state) {

		// Create policy with Standard UIM coverage
		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		generateRenewalOfferAtOfferGenDate();
		validateDocumentIsNotGeneratedInPackage(policyNumber, RENEWAL_OFFER, false);

		//reopen app and retrieve policy by number
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		// PolicySummaryPage.buttonRenewals.click(); change current term not endorsement

		//Initiate Endorsement for current term
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));

		//4. Switch UIM to EUIM coverage
		switchToEUIMCoverageAndBind();
		//JobUtils.executeJob(Jobs.aaaDocGenBatchJob);//not necessary - can be used if QA needs actual generated xml files

		//6. Validate that form AAEUIMMD is included in Endorsement package
		//8. Validate that form AAEUIMMD is included in Endorsement package only once
		validateDocumentIsGeneratedInPackage(policyNumber, ENDORSEMENT_ISSUE);

		//7. Validate that form AAEUIMMD is not included in Renewal package
		validateDocumentIsNotGeneratedInPackage(policyNumber, RENEWAL_OFFER, true);

	}

	/**
	 *@author Maris Strazds
	 *@name Test that form AAEUIMMD is not included in document package if EUIM COVERAGE is removed
	 *@scenario
	 * 1. Create Customer
	 * 2. Create Auto SS MD Policy after 07/01/2018 with EUIM Coverage coverage
	 * 3. Make sure that form AAEUIMMD is included in New Business Package
	 * 4. Make a midterm endorsement, remove EUIM an bind
	 * 5. Validate that form AAEUIMMD is not included in the Document package and it is not listed in any other document
	 *@details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12466")
	public void pas12466_do_not_generate_AAEUIMMD_if_coverage_removed(@Optional("MD") String state) {

		TestData tdEUIM = getPolicyTD().adjust(PremiumAndCoveragesTab.class.getSimpleName(), getTestSpecificTD("PremiumAndCoveragesTab_NB"));

		mainApp().open();
		createCustomerIndividual();

		createPolicy(tdEUIM);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		validateDocumentIsGeneratedInPackage(policyNumber, POLICY_ISSUE);

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));
		switchAwayFromEUIMCoverageAndBind();

		validateDocumentIsNotGeneratedInPackage(policyNumber, ENDORSEMENT_ISSUE, false);

	}

	////////////////////////////////End PAS-12466 ///////////////////////////////////////////

	private void verifyFormsAndAmount() {
		//PAS-11302 AC1
		TestData formsData = premiumAndCoveragesTab.getFormsData();
		assertThat(formsData.getKeys()).contains(formId);
		assertThat(formsData.getValue(formId)).isEqualTo("$0.00");

		//PAS-11302 AC2
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.FORMS.get());
		AutoSSForms.AutoSSPolicyFormsController policyForms = formsTab.getAssetList().getAsset(AutoSSMetaData.FormsTab.POLICY_FORMS);
		assertThat(policyForms.tableSelectedForms.getRowContains("Name", formId).getCell(2)).hasValue(formDesc);
		assertThat(policyForms.getRemoveLink(formId)).isPresent(false);
	}

	private void switchToEUIMCoverage() {
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		enhancedUIM.setValue(true);
		premiumAndCoveragesTab.calculatePremium();
	}

	private void switchAwayFromEUIMCoverage() {
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		enhancedUIM.setValue(false);
		premiumAndCoveragesTab.calculatePremium();

	}

	private void switchToEUIMCoverageAndBind() {
		switchToEUIMCoverage();
		//Bind
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	private void switchAwayFromEUIMCoverageAndBind() {
		switchAwayFromEUIMCoverage();
		//Bind
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	private void generateRenewalOfferAtOfferGenDate() {
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);

		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		//JobUtils.executeJob(Jobs.aaaDocGenBatchJob);//not necessary - can be used if QA needs actual generated xml files
	}

	private void validateDocumentIsGeneratedInPackage(String policyNumber, AaaDocGenEntityQueries.EventNames eventName) {
		List<Document> docs = DocGenHelper.getDocumentsList(policyNumber, eventName);
		assertThat(docs.stream().map(Document::getTemplateId).toArray()).contains(DocGenEnum.Documents.AAEUIMMD.getIdInXml());

		//Create list of documents other than AAEUIMMD
		List<Document> docsOther = docs.stream().filter(document -> !document.getTemplateId().equals(DocGenEnum.Documents.AAEUIMMD.getIdInXml())).collect(Collectors.toList());

		//Validate that form AAEUIMMD is listed in other documents (test validates that at least in one other document)
		assertThat(docsOther.stream().filter(document -> document.toString().contains(DocGenEnum.Documents.AAEUIMMD.getIdInXml())).toArray().length).isGreaterThan(0);

		//Validate that form AAEUIMMD is included in Document Package only once
		assertThat(docs.stream().filter(document -> document.getTemplateId().equals(DocGenEnum.Documents.AAEUIMMD.getIdInXml())).toArray().length).isEqualTo(1);
	}

	private void validateDocumentIsNotGeneratedInPackage(String policyNumber, AaaDocGenEntityQueries.EventNames eventName, boolean shouldBeListedInOtherDocs) {
		List<Document> docs = DocGenHelper.getDocumentsList(policyNumber, eventName);
		assertThat(docs.stream().map(Document::getTemplateId).toArray()).doesNotContain(DocGenEnum.Documents.AAEUIMMD.getIdInXml());

		//Create list of documents other than AAEUIMMD
		List<Document> docsOther = docs.stream().filter(document -> !document.getTemplateId().equals(DocGenEnum.Documents.AAEUIMMD.getIdInXml())).collect(Collectors.toList());

		//Validate that document AAEUIMMD is/is not listed in other documents (test validates that AAEUIMMD is listed at least in one other document)
		if (shouldBeListedInOtherDocs) {
			assertThat(docsOther.stream().filter(document -> document.toString().contains(DocGenEnum.Documents.AAEUIMMD.getIdInXml())).toArray().length).isGreaterThan(0);

		} else {
			assertThat(docsOther.stream().filter(document -> document.toString().contains(DocGenEnum.Documents.AAEUIMMD.getIdInXml())).toArray().length).isEqualTo(0);
		}
	}
}
