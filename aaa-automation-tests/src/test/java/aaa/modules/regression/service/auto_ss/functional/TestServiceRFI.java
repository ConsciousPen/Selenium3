/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ss.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.main.enums.*;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.common.enums.Constants;
import aaa.helpers.rest.dtoDxp.*;
import aaa.helpers.xml.model.Document;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.service.helper.*;
import aaa.utils.StateList;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.rest.dtoAdmin.RfiDocumentResponse;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.sales.auto_ss.TestPolicyNano;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomSoftAssertions;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.AbstractEditableStringElement;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import javax.ws.rs.core.Response;

public class TestServiceRFI extends AutoSSBaseTest {
	private final DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
	private final TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
	private HelperMiniServices helperMiniServices = new HelperMiniServices();
	private final PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private ErrorTab errorTab = new ErrorTab();
	private TestMiniServicesVehiclesHelper vehiclesHelper = new TestMiniServicesVehiclesHelper();

	/**
	 * @author Jovita Pukenaite
	 * @name RFI View Service
	 * @scenario 1. Create policy.
	 * 2. Create endorsement outside of PAS.
	 * 3. Rate. Hit RFI service.
	 * 4. Check the response.
	 * 5. Update UM coverage.
	 * 6. Rate. Hit RFI service, generate doc = false.
	 * 7. Hit RFI service again, generate doc = true, check url.
	 * 8. Check if document is displaying.
	 * 9. Rate and bind.
	 * 10. Create a new endorsement.
	 * 11. Rate. Hit RFI service.
	 * 12. Check the response.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-19630", "PAS-19632"})
	public void pas19630_ViewServiceRFI(@Optional("VA") String state) {
		assertSoftly(softly -> {
			String policyNumber = openAppAndCreatePolicy();
			helperMiniServices.createEndorsementWithCheck(policyNumber);
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			RFIDocuments rfiServiceResponse = HelperCommon.rfiViewService(policyNumber, false);
			softly.assertThat(rfiServiceResponse.url).isNull();
			softly.assertThat(rfiServiceResponse.documents.isEmpty()).isTrue();

			//update UM coverage
			HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest("UMBI", "25000/50000"), PolicyCoverageInfo.class);

			checkDocumentInRfiService(policyNumber, "RUUELLUU", "IMPORTANT NOTICE - Uninsured Motorist Coverage", "policy", "NS");

			helperMiniServices.endorsementRateAndBind(policyNumber);
			helperMiniServices.createEndorsementWithCheck(policyNumber);
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			RFIDocuments rfiServiceResponse4 = HelperCommon.rfiViewService(policyNumber, false);
			softly.assertThat(rfiServiceResponse4.url).isNotNull();
			softly.assertThat(rfiServiceResponse4.documents.isEmpty()).isTrue();

			helperMiniServices.endorsementRateAndBind(policyNumber);
		});
	}

	/**
	 * @author Megha Gubbala
	 * @name RFI AACSDC Form
	 * @scenario 1. Create policy.
	 * 2. Create endorsement outside of PAS.
	 * 3. Rate. Hit RFI service.
	 * 4. Check the response.
	 * 5. Update UIMBI/PIPMedical/UIMPD/PIPWORKLOSS/FUNERAL coverage. Rate.
	 * 6. Hit RFI service, check if document is displaying.
	 * 7. Run bind service without signing document and verify error. and policy is not bound.
	 * 8. Run bind service with doccument id verify no error and we can bind the policy.
	 * 9. go to pas UI and verify if policy is bound
	 * 10 Go to doccument and bind page and verify if document is electronically signed.
	 * 11. go to the DB and verify doccument signed by is there in xml
	 * 12 .create and endorsement on policy from pas change  umbi and rate the policy
	 * 13. go to doccument and bind page verify if its reset to doccument not signed
	 * 14 Try to bind policy from pas and verify error.
	 * 14 Select document physically signed
	 * 15 Verify in db that we are not sending doccument signed by
	 * 16  Bind the policy verify there is no error message.
	 */

	@Parameters({"state"})
	@StateList(states = {Constants.States.DC})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-21423"})
	public void pas21423_aacsdcFormRFI(@Optional("DC") String state) {
		assertSoftly(softly -> {
			VerifyAacsdcScenarios(softly, "PIPMEDICAL", "100000");

			VerifyAacsdcScenarios(softly, "UIMPD", "25000");

			VerifyAacsdcScenarios(softly, "PIPWORKLOSS", "24000");

			VerifyAacsdcScenarios(softly, "FUNERAL", "4000");
		});
	}

	private void VerifyAacsdcScenarios(ETCSCoreSoftAssertions softly, String pipmedical, String s) {
		String policyNumber = policyCreationForAASCDC(pipmedical, s, getPolicyDefaultTD());
		verifyRateBindPasForAACSDC(softly, policyNumber);
	}

	private void verifyRateBindPasForAACSDC(ETCSCoreSoftAssertions softly, String policyNumber) {
		//Verify RFI service and verify it returns doccid
		String doccId = checkDocumentInRfiService(policyNumber, "AACSDC", "District of Columbia Coverage Selection/Rejection Form", "policy", "NS");

		//Verify Bind service
		bindEndorsement(policyNumber, doccId, ErrorEnum.Errors.ERROR_200900.getCode(), ErrorEnum.Errors.ERROR_200900.getMessage(), "attributeForRules", false);
		//Verify DB Endorsement xml Signed by field is there
		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "AACSDC", "ENDORSEMENT_ISSUE");
		verifyDoccInDb(softly, query, DocGenEnum.Documents.AACSDC, false);

		//Go to pas and and verify
		goToPasAndVerifyRuleAndSignedBy(softly, policyNumber, AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.DISTRICT_OF_COLUMBIA_COVERAGE_SELECTION_REJECTION_FORM,
				AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORISTS_BODILY_INJURY, "$50,000/$100,000", ErrorEnum.Errors.ERROR_200900, false);
	}

	/**
	 * @author Megha Gubbala
	 * @name Lets turn on eSignature for AA52VA
	 * @scenario 1. Create policy.
	 * 2. Create endorsement from PAS.
	 * 3. Change UMBI from pas
	 * 4. Rate the policy go to bind tab.
	 * 5. check IMPORTANT_NOTICE_UNINSURED_MOTORIST_COVERAGE electronically sgined from pas
	 * 6. Bind the policy
	 * 7. create endorsement on dxp.
	 * 8. change UMBI rate the policy go to bind page IMPORTANT_NOTICE_UNINSURED_MOTORIST_COVERAGE physically bind the policy
	 */

	@Parameters({"state"})
	@StateList(states = {Constants.States.VA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-23511"})
	public void pas23511_VirginiaAndAA52VA(@Optional("VA") String state) {
		assertSoftly(softly -> {
			mainApp().open();
			String policyNumber =
					createPolicyForAA52VA("$25,000/$50,000 (-$32.00)", "Electronically Signed");
			// TO DO add db check no signed by in db
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			PolicySummaryPage.buttonPendedEndorsement.click();
			policy.dataGather().start();
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			premiumAndCoveragesTab.setPolicyCoverageDetailsValue(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY.getLabel(), "$50,000/$100,000");
			premiumAndCoveragesTab.calculatePremium();
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
			softly.assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.IMPORTANT_NOTICE_UNINSURED_MOTORIST_COVERAGE).getValue()).isEqualTo("Not Signed");
			// TO DO add db check no signed by in db
		});
	}

	/**
	 * @author Megha Gubbala
	 * @name RFI AADNDE1 Form
	 * @scenario 1. Create policy.
	 * 2. Create endorsement outside of PAS.
	 * 3. Rate. Hit RFI service.
	 * 4. Check the response.
	 * 5. Update BI/PD/UMBI/PIP/PIPDED coverage. Rate.
	 * 6. Hit RFI service, check if document is displaying.
	 * 7. Run bind service without signing document and verify error. and policy is not bound.
	 * 8. Run bind service with doccument id verify no error and we can bind the policy.
	 * 9. go to pas UI and verify if policy is bound
	 * 10 Go to doccument and bind page and verify if document is electronically signed.
	 * 11. go to the DB and verify doccument signed by is there in xml
	 * 12 .create and endorsement on policy from pas change  umbi and rate the policy
	 * 13. go to doccument and bind page verify if its reset to doccument not signed
	 * 14 Try to bind policy from pas and verify error.
	 * 14 Select document physically signed
	 * 15 Verify in db that we are not sending doccument signed by
	 * 16  Bind the policy verify there is no error message.
	 */

	@Parameters({"state"})
	@StateList(states = {Constants.States.DE})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-21596", "PAS-21591", "PAS-24114"})
	public void pas21596_aadnde1FormRFI(@Optional("DE") String state) {

		DocGenEnum.Documents document = DocGenEnum.Documents.AADNDE1;
		AssetDescriptor<RadioGroup> documentAsset = AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.DELAWARE_MOTORISTS_PROTECTION_ACT;
		ErrorEnum.Errors error = ErrorEnum.Errors.ERROR_200123;

		verifyRFIScenarios("BI", AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY, "25000/50000", "$50,000/$100,000", document, documentAsset, error, getPolicyDefaultTD(), true, false);
		verifyRFIScenarios("PD", AutoSSMetaData.PremiumAndCoveragesTab.PROPERTY_DAMAGE_LIABILITY, "15000", "$25,000", document, documentAsset, error, getPolicyDefaultTD(), true, false);
		verifyRFIScenarios("UMBI", AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY, "25000/50000", "$50,000/$100,000", document, documentAsset, error, getPolicyDefaultTD(), true, false);
		verifyRFIScenarios("PIP", AutoSSMetaData.PremiumAndCoveragesTab.PERSONAL_INJURY_PROTECTION, "25000/50000", "$50,000/$100,000", document, documentAsset, error, getPolicyDefaultTD(), true, false);
		verifyRFIScenarios("PIPDED", AutoSSMetaData.PremiumAndCoveragesTab.PERSONAL_INJURY_PROTECTION_DEDUCTIBLE, "250", "$500", document, documentAsset, error, getPolicyDefaultTD(), true, false);

		//Override rule at NB
		TestData td = getPolicyDefaultTD();
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.DocumentsAndBindTab.class.getSimpleName(), AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND.getLabel(), documentAsset.getLabel()), "Not Signed");
		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");
		td = td.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError).resolveLinks();
		verifyRFIScenarios("BI",AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY, "25000/50000", "$50,000/$100,000",document,documentAsset,error, td,true,true);

	}

	private void verifyAadndeScenarios(String coverageCd, AssetDescriptor<ComboBox> coverageAsset, String updateLimitDXP, String updateLimitPAS) {
		assertSoftly(softly -> {

			String policyNumber = policyCreationForAASCDC(coverageCd, updateLimitDXP, getPolicyDefaultTD());

			String doccId = checkDocumentInRfiService(policyNumber, DocGenEnum.Documents.AADNDE1.getId(), DocGenEnum.Documents.AADNDE1.getName(), "policy", "NS");

			bindEndorsement(policyNumber, doccId, ErrorEnum.Errors.ERROR_200123.getCode(), ErrorEnum.Errors.ERROR_200123.getMessage(), "attributeForRules", false);

			//PAS-24114
			String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, DocGenEnum.Documents.AADNDE1.getId(), AaaDocGenEntityQueries.EventNames.ENDORSEMENT_ISSUE);
			verifyDoccInDb(softly, query, DocGenEnum.Documents.AADNDE1, false);

			//Go to pas and and verify
			goToPasAndVerifyRuleAndSignedBy(softly, policyNumber, AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.DELAWARE_MOTORISTS_PROTECTION_ACT,
					coverageAsset, updateLimitPAS, ErrorEnum.Errors.ERROR_200123, false);

			String query1 = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, DocGenEnum.Documents.AADNDE1.getId(), AaaDocGenEntityQueries.EventNames.ENDORSEMENT_ISSUE);
			softly.assertThat(DocGenHelper.getDocument(DocGenEnum.Documents.AADNDE1, query1).toString().contains("DocSignedBy")).isFalse();
			softly.assertThat(DocGenHelper.getDocument(DocGenEnum.Documents.AADNDE1, query1).toString().contains("DocSignedDate")).isFalse();
		});
	}

	/**
	 * @author Maris Strazds
	 * @name RFI AAFPPA Form
	 * @scenario 1
	 * 1. Create policy.
	 * 2. Create endorsement outside of PAS.
	 * 3. Rate. Hit RFI service.
	 * 4. Check the response.
	 * 5. Update FPB/MEDPM/ADBC/FUNERAL/IL/EMB coverage. Rate.
	 * 6. Hit RFI service, check if document is displaying.
	 * 7. Run bind service without signing document and verify error. and policy is not bound.
	 * 8. Run bind service with document id verify no error and we can bind the policy.
	 * 9. go to pas UI and verify if policy is bound
	 * 10 Go to document and bind page and verify if document is electronically signed.
	 * 12 .create and endorsement on policy from pas change coverage and rate the policy
	 * 13. go to document and bind page verify if its reset to document not signed
	 * 14 Try to bind policy from pas and verify error.
	 * 14 Select document physically signed
	 * 15 Verify in db that we are not sending document signed by
	 * 16  Bind the policy verify there is no error message.
	 * @scenario 2
	 * 1. Create policy and override the rule
	 * 2. Create endorsement outside of PAS.
	 * 3. Trigger the document by updating one of the coverages (FPB/MEDPM/ADBC/FUNERAL/IL/EMB)
	 * 4. Hit RFI service and check that docuemnt is returned
	 * 5. Bind Endorsement ---> No rule is fired (as it was overridden at NB)
	 *
	 */

	@Parameters({"state"})
	@StateList(states = {Constants.States.PA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-23300"})
	public void pas23300_AAFPPAFormRFI(@Optional("PA") String state) {
		DocGenEnum.Documents document = DocGenEnum.Documents.AAFPPA;
		AssetDescriptor<RadioGroup> documentAsset = AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.FIRST_PARTY_BENEFITS_COVERAGE_AND_LIMITS_SELECTION_FORM;
		ErrorEnum.Errors error = ErrorEnum.Errors.ERROR_200305;

		TestData td = getPolicyDefaultTD();
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.TORT_THRESHOLD.getLabel()), "contains=Full Tort");//to not get TORT rule

		//First Party Benefits <> Added
		verifyRFIScenarios("FPB", AutoSSMetaData.PremiumAndCoveragesTab.FIRST_PARTY_BENEFITS, CoverageLimits.COV_FPB_ADDED.getLimit(), "Basic", document, documentAsset, error, td, false, false);

		//First Party Benefits = Added
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.FIRST_PARTY_BENEFITS.getLabel()), "contains=Added");
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.ACCIDENTAL_DEATH_BENEFIT.getLabel()), "contains=$15,000");
		verifyRFIScenarios("MEDPM", AutoSSMetaData.PremiumAndCoveragesTab.MEDICAL_BENEFIT, "50000", "$10,000", document, documentAsset, error, td, false, false);
		verifyRFIScenarios("ADBC", AutoSSMetaData.PremiumAndCoveragesTab.ACCIDENTAL_DEATH_BENEFIT, "0", "$25,000", document, documentAsset, error, td, false, false);
		verifyRFIScenarios("FUNERAL", AutoSSMetaData.PremiumAndCoveragesTab.FUNERAL_EXPENSE_BENEFIT, "2500", "No Coverage", document, documentAsset, error, td, false, false);
		verifyRFIScenarios("IL", AutoSSMetaData.PremiumAndCoveragesTab.WORK_LOSS_BENEFIT, CoverageLimits.COV_IL_25000_MAX_PA.getLimit(), CoverageLimits.COV_IL_15000_MAX_PA.getDisplay(), document, documentAsset, error, td, false, false);
		verifyRFIScenarios("EMB", AutoSSMetaData.PremiumAndCoveragesTab.EXTRAORDINARY_MEDICAL_EXPENSE_BENEFITS, CoverageLimits.COV_EMB_1000.getLimit(), CoverageLimits.COV_EMB_0.getDisplay(), document, documentAsset, error, td, false, false);

		//Create policy and override rule
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.DocumentsAndBindTab.class.getSimpleName(), AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND.getLabel(), AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.FIRST_PARTY_BENEFITS_COVERAGE_AND_LIMITS_SELECTION_FORM.getLabel()), "Not Signed");
		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");
		td = td.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError).resolveLinks();
		verifyRFIScenarios("MEDPM", AutoSSMetaData.PremiumAndCoveragesTab.MEDICAL_BENEFIT, "50000", "$10,000", document, documentAsset, error, td, false, true);

		//First Party Benefits <> Added
		TestData td2 = getPolicyDefaultTD();//BUG: Prod Issue - Can not update FPB to Combo. Similar to 1st scenario.//TODO-mstrazds:Add Bug ID. Moved this to the end of the test, so that test doesn't fail at the beginning
		td2.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.TORT_THRESHOLD.getLabel()), "contains=Full Tort");//to not get TORT rule
		verifyRFIScenarios("FPB", AutoSSMetaData.PremiumAndCoveragesTab.FIRST_PARTY_BENEFITS, CoverageLimits.COV_FPB_50K_TOTAL.getLimit(), CoverageLimits.COV_FPB_100K_TOTAL_PAS_UI_DISPLAY.getDisplay(), document, documentAsset, error, td2, false, false);

	}

	private void verifyRFIScenarios(String coverageCd, AssetDescriptor<? extends AbstractEditableStringElement> coverageAsset, String updateLimitDXP, String updateLimitPAS, DocGenEnum.Documents document, AssetDescriptor<RadioGroup> documentAsset, ErrorEnum.Errors error, TestData td, boolean checkDocXML, boolean isRuleOverridden) {
		assertSoftly(softly -> {

			String policyNumber = policyCreationForAASCDC(coverageCd, updateLimitDXP, td);
			String doccId = checkDocumentInRfiService(policyNumber, document.getId(), document.getName(), "policy", "NS");
			bindEndorsement(policyNumber, doccId, error.getCode(), error.getMessage(), "attributeForRules", isRuleOverridden);

			String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, document.getIdInXml(), AaaDocGenEntityQueries.EventNames.ENDORSEMENT_ISSUE);
			if (checkDocXML) {
				verifyDoccInDb(softly, query, document, isRuleOverridden);
			}

			//Go to pas and and verify
			goToPasAndVerifyRuleAndSignedBy(softly, policyNumber, documentAsset, coverageAsset, updateLimitPAS, error, isRuleOverridden);
			//Verify Signed by is not there in XML when Signed from PAS UI
			if (checkDocXML) {
				validateDocSignTagsNotExist(document, query);
			}
		});
	}

	private void validateDocSignTagsNotExist(DocGenEnum.Documents document, String query) {
		assertThat(DocGenHelper.getDocument(document, query).toString().contains("DocSignedBy")).isFalse();
		assertThat(DocGenHelper.getDocument(document, query).toString().contains("DocSignedDate")).isFalse();
	}

	/**
	 * @author Maris Strazds
	 * @name RFI AAFPPA Form
	 * @scenario 1
	 * 1. Create policy.
	 * 2. Create endorsement outside of PAS.
	 * 3. Add vehicle through service
	 * 4. Rate. Hit RFI service.
	 * 5. Check the response.
	 * 6. Check if document is displaying.
	 * 7. Run bind service without signing document and verify error. and policy is not bound.
	 * 8. Run bind service with document id verify no error and we can bind the policy.
	 * 9. go to pas UI and verify if policy is bound
	 * 10. Go to document and bind page and verify if document is electronically signed.
	 * 11 .create and endorsement on policy from pas , add vehicle and rate the policy
	 * 12. go to document and bind page verify if its reset to document not signed
	 * 13 Try to bind policy from pas and verify error.
	 * 14 Select document physically signed
	 * 15 Verify in db that we are not sending document signed by
	 * 16  Bind the policy verify there is no error message.
	 * @scenario 2
	 * 1. Create policy and override the rule
	 * 2. Create endorsement outside of PAS.
	 * 3. Trigger the document by replacing vehicle
	 * 4. Hit RFI service and check that document is returned
	 * 5. Bind Endorsement ---> No rule is fired (as it was overridden at NB)
	 *
	 */

	@Parameters({"state"})
	@StateList(states = {Constants.States.PA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-23301"})
	public void pas23301_AA52IPAAFormAddReplaceVehicleTriggerRFI(@Optional("PA") String state) {
		DocGenEnum.Documents document = DocGenEnum.Documents.AA52UPAA;
		AssetDescriptor<RadioGroup> documentAsset = AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNUNSURED_MOTORISTS_COVERAGE_SELECTION_REJECTION ;
		ErrorEnum.Errors error = ErrorEnum.Errors.ERROR_200306;

		TestData td = getPolicyDefaultTD();
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.TORT_THRESHOLD.getLabel()), "contains=Full Tort");//to not get TORT rule
		validateRFIScenariosAddVehicle(document, documentAsset, error, td, false);

		//Create policy and override rule
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.DocumentsAndBindTab.class.getSimpleName(), AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND.getLabel(), AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNUNSURED_MOTORISTS_COVERAGE_SELECTION_REJECTION.getLabel()), "Not Signed");
		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");
		td = td.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError).resolveLinks();
		validateRFIScenariosAddVehicle(document, documentAsset, error, td, true);

	}

	private void validateRFIScenariosAddVehicle(DocGenEnum.Documents document, AssetDescriptor<RadioGroup> documentAsset, ErrorEnum.Errors error, TestData td, boolean isRuleOverridden) {
		String policyNumber = openAppAndCreatePolicy(td);

		helperMiniServices.createEndorsementWithCheck(policyNumber);
		vehiclesHelper.addVehicleWithChecks(policyNumber, "2013-02-22", "1HGEM21504L055795", true);

		String docId = checkDocumentInRfiService(policyNumber, document.getId(), document.getName(), "policy", "NS");
		bindEndorsement(policyNumber, docId, error.getCode(), error.getMessage(), "attributeForRules", isRuleOverridden);

		SoftAssertions.assertSoftly(softly -> {

			//create endorsement from pas go to bind page verify document is electronically signed
			mainApp().open();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

			if (!isRuleOverridden) {
				softly.assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(documentAsset).getValue()).isEqualTo("Electronically Signed");
			} else {
				softly.assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(documentAsset).getValue()).isEqualTo("Not Signed");
			}

			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

			TestData td1 = getPolicyTD("DataGather", "TestData");
			TestData testData = td1.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_AddVewhicle").getTestDataList("VehicleTab")).resolveLinks();
			policy.getDefaultView().fillFromTo(testData, VehicleTab.class, VehicleTab.class, true);
			premiumAndCoveragesTab.calculatePremium();
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

			softly.assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(documentAsset).getValue()).isEqualTo("Not Signed");
			documentsAndBindTab.submitTab();
			if (!isRuleOverridden) {
				//On bind verify error message
				errorTab.verify.errorsPresent(true, error);
				errorTab.cancel();
				//Physically sign the document and bind policy
				documentsAndBindTab.getRequiredToBindAssetList().getAsset(documentAsset).setValue("Physically Signed");
				documentsAndBindTab.submitTab();
			}
			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE); //indicates Endorsement bound successfully
		});
	}

	/**
	 * @author Megha gubbala
	 * @name RFI View Service / Override part
	 * @scenario 1. Create quote:
	 * eValue = Yes, PAA = Not Sign in, Insured Motorist = Not Sign in
	 * 2. Change UM limit, to the lower one in P&C page.
	 * 3. Issue quote. Override all errors.
	 * 4. Create endorsement outside of PAS.
	 * 5. Rate and hit RFI service.
	 * 6. Check the response.
	 * 7. Rate and bind endorsement.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-21366"})
	public void pas21366_VirginiaAndAA52VA(@Optional("VA") String state) {
		assertSoftly(softly -> {

			String policyNumber = createPolicyForAA52VA("$25,000/$50,000 (-$32.00)", "Physically Signed");

			RFIDocuments rfiServiceResponse = HelperCommon.rfiViewService(policyNumber, false);
			softly.assertThat(rfiServiceResponse.url).isNull();
			softly.assertThat(rfiServiceResponse.documents.isEmpty()).isTrue();

			HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest("UMPD", "40000"), PolicyCoverageInfo.class);
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			String doccId = checkDocumentInRfiService(policyNumber, "RUUELLUU", "IMPORTANT NOTICE - Uninsured Motorist Coverage", "policy", "NS");

			helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorEnum.Errors.ERROR_200037_VA.getCode(), ErrorEnum.Errors.ERROR_200037_VA.getMessage(), "attributeForRules");

			HelperCommon.endorsementBind(policyNumber, "Megha Gubbala", Response.Status.OK.getStatusCode(), doccId);

			//Verify Signed by is there in XML
			String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "AA52VA", "ENDORSEMENT_ISSUE");
			verifyDoccInDb(softly, query, DocGenEnum.Documents.AA52VA, false);

			goToPasAndVerifyRuleAndSignedBy(softly, policyNumber, AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.IMPORTANT_NOTICE_UNINSURED_MOTORIST_COVERAGE,
					AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY, "$50,000/$100,000", ErrorEnum.Errors.ERROR_200037_VA, false);

			//Verify Signed by is not there in XML
			String query1 = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "AA52VA", "ENDORSEMENT_ISSUE");
			softly.assertThat(DocGenHelper.getDocument(DocGenEnum.Documents.AA52VA, query1).toString().contains("DocSignedBy")).isFalse();
			softly.assertThat(DocGenHelper.getDocument(DocGenEnum.Documents.AA52VA, query1).toString().contains("DocSignedDate")).isFalse();

		});
	}

	private String createPolicyForAA52VA(String limit, String signType) {
		mainApp().open();
		createCustomerIndividual();
		createQuote();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");

		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY).setValue(limit);
		premiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.IMPORTANT_NOTICE_UNINSURED_MOTORIST_COVERAGE).setValue(signType);
		documentsAndBindTab.saveAndExit();

		String policyNumber = testEValueDiscount.simplifiedQuoteIssue();
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		return policyNumber;
	}

	/**
	 * @author Jovita Pukenaite
	 * @name RFI View Service / Override part
	 * @scenario 1. Create quote:
	 * eValue = Yes, PAA = Not Sign in, Insured Motorist = Not Sign in
	 * 2. Change UM limit, to the lower one in P&C page.
	 * 3. Issue quote. Override all errors.
	 * 4. Create endorsement outside of PAS.
	 * 5. Rate and hit RFI service.
	 * 6. Check the response.
	 * 7. Rate and bind endorsement.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-19631"})
	public void pas19631_OverrideRulesCheckViewServiceRFI(@Optional("VA") String state) {
		assertSoftly(softly -> {
			TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
			PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();

			mainApp().open();
			createCustomerIndividual();
			createQuote();
			policy.dataGather().start();
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
			premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY).setValue("$25,000/$50,000 (-$32.00)");
			premiumAndCoveragesTab.calculatePremium();
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
			documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION).setValue("Not Signed");
			documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.EVALUE_ACKNOWLEDGEMENT).setValue("Not Signed");
			documentsAndBindTab.saveAndExit();

			String policyNumber = testEValueDiscount.simplifiedQuoteIssue();

			helperMiniServices.createEndorsementWithCheck(policyNumber);
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			RFIDocuments rfiServiceResponse = HelperCommon.rfiViewService(policyNumber, false);
			softly.assertThat(rfiServiceResponse.url).isNull();
			softly.assertThat(rfiServiceResponse.documents.isEmpty()).isTrue();

			helperMiniServices.endorsementRateAndBind(policyNumber);
		});
	}

	private String checkDocumentInRfiService(String policyNumber, String documentCode, String documentName, String parent, String status) {
		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		RFIDocuments rfiServiceResponse = HelperCommon.rfiViewService(policyNumber, false);
		String doccId = rfiServiceResponse.documents.get(0).documentId;
		assertSoftly(softly -> {

			softly.assertThat(rfiServiceResponse.url).isNull();
			softly.assertThat(rfiServiceResponse.documents.get(0).documentCode).isEqualTo(documentCode);
			softly.assertThat(rfiServiceResponse.documents.get(0).documentName).isEqualTo(documentName);
			softly.assertThat(rfiServiceResponse.documents.get(0).documentId).startsWith(documentCode);
			softly.assertThat(rfiServiceResponse.documents.get(0).status).startsWith(status);
			softly.assertThat(rfiServiceResponse.documents.get(0).parent).isEqualTo(parent);
			softly.assertThat(rfiServiceResponse.documents.get(0).parentOid).isNotEmpty();

			RFIDocuments rfiServiceResponse2 = HelperCommon.rfiViewService(policyNumber, true);
			softly.assertThat(rfiServiceResponse2.url).isNotEmpty();
			softly.assertThat(rfiServiceResponse2.documents).isNotEmpty();

		});
		return doccId;
	}

	/**
	 * @author Oleg Stasyuk
	 * @name RFI
	 * @scenario
	 * 1.Initiate quote creation.
	 * Insured1
	 * Proof of Prior Insurance (including original inception date of policy and prior BI limits)	Prior BI overridden by agent
	 *
	 * Driver1
	 * Proof of Good Student
	 *
	 * Driver2
	 * DL - Foreign
	 * Smart Driver Course Completed?
	 *
	 * Driver3 - Not Available for Rating, insured with other carrier
	 * Proof of Current Insurance for all "Not Available for Rating" drivers
	 *
	 * Driver4 - Not Available for Rating
	 *
	 * Vehicle1 -
	 * Photos showing all 4 sides of salvaged vehicles	select salvaged
	 * Proof of purchase date (bill of sale) for new vehicle(s) - less than 30 days
	 *
	 * Vehicle2 -
	 * Proof of equivalent new car added protection coverage with prior carrier for new vehicle(s)	new car added protection; date is more than 30 days ago
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-349", "PAS-341", "PAS-20333"})
	public void pas349_rfiAuto(@Optional("WY") String state) {
		String today = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);

		TestData td;
		if (null == getTestSpecificTD("TestData_" + state)) {
			td = getTestSpecificTD("TestData");
		} else {
			td = getTestSpecificTD("TestData_" + state);
		}
		if (!("VA".equals(state) || "CA".equals(state) || "NY".equals(state))) {
			td.mask("AssignmentTab").resolveLinks();
		}
		//NY is handled separately in it's vehicle testData
		if ("ID, KS, KY, MT, NV, OR, UT, WY, MD, WV".contains(state)) {
			td = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_No_UBI").getTestDataList("VehicleTab")).resolveLinks();
		}

		mainApp().open();
		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(td, DocumentsAndBindTab.class, false);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

		createQuoteWithCustomData(state);

		CustomSoftAssertions.assertSoftly(softly -> {
			String policyNumber = testEValueDiscount.simplifiedQuoteIssue();
			rfiDocumentContentCheck(policyNumber, state, softly);

			//PAS-341 Start
			RfiDocumentResponse[] result = HelperCommon.executeRequestRfi(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			//BUG Question to Karen - BUG1 - Auto Insurance Application inconsistent with other states for KS, MD, NY
			//BUG Question to Karen - BUG2 - WY doesnt have Proof of Current Insurance for , like all other states
			HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION.getLabel(), "PAA", "NS", softly);
			//BUG Question to Karen - BUG3 - UBI is not applicable for this states, so the Smart Trek can be tested only for them - "ID, KS, KY, MT, NV, NY, OR, UT, WY, MD, WV"
			if (!"ID, KS, KY, MT, NV, NY, OR, UT, WY, MD, WV".contains(state)) {
				HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT_OF_TERMS.getLabel(), "AAAUBI1B", "NS", softly);
			}

			HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_CURRENT_INSURANCE_FOR.getLabel(), "PIF", "NS", softly);
			HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_GOOD_STUDENT_DISCOUNT.getLabel(), "PGSD", "NS", softly);
			HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_SMART_DRIVER_COURSE_COMPLETION.getLabel(), "PSDCC", "NS", softly);
			HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_PRIOR_INSURANCE.getLabel(), "PPI", "NS", softly);
			HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_PURCHASE_DATE_BILL_OF_SALE_FOR_NEW_VEHICLES.getLabel(), "PPDNV", "NS", softly);
			HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_EQUIVALENT_NEW_CAR_ADDED_PROTECTION_WITH_PRIOR_CARRIER_FOR_NEW_VEHICLES
					.getLabel(), "PENCAP", "NS", softly);
			HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.CANADIAN_MVR_FOR_DRIVER.getLabel(), "CMD", "NS", softly);
			HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PHOTOS_FOR_SALVATAGE_VEHICLE_WITH_PHYSICAL_DAMAGE_COVERAGE.getLabel(), "PSVPDC", "NS", softly);
			HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_DEFENSIVE_DRIVER_COURSE_COMPLETION.getLabel(), "PODDCC", "NS", softly);
			//PAS-341 End

			uploadDocuments(policyNumber, state, softly);

			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			endorseRateDocuments();
			softly.assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION)).hasValue("Pending Review (Uploaded " + today + ")");
			if (!"ID, KS, KY, MT, NV, NY, OR, UT, WY, MD, WV".contains(state)) {
				softly.assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT_OF_TERMS)).hasValue("Pending Review (Uploaded " + today + ")");
			}
			softly.assertThat(documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_GOOD_STUDENT_DISCOUNT)).hasValue("Pending Review (Uploaded " + today + ")");
			softly.assertThat(documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_SMART_DRIVER_COURSE_COMPLETION)).hasValue("Pending Review (Uploaded " + today + ")");

			//check Upload Pending is present after value is changed
			if (!"ID, KS, KY, MT, NV, NY, OR, UT, WY, MD, WV".contains(state)) {
				documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT_OF_TERMS)
						.setValue("Physically Signed");
			}
			documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_SMART_DRIVER_COURSE_COMPLETION).setValue("Yes");
			documentsAndBindTab.submitTab();

			endorseRateDocuments();
			softly.assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION)).hasValue("Pending Review (Uploaded " + today + ")");
			if (!"ID, KS, KY, MT, NV, NY, OR, UT, WY, MD, WV".contains(state)) {
				softly.assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT_OF_TERMS)).hasValue("Physically Signed");
			}
			softly.assertThat(documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_GOOD_STUDENT_DISCOUNT)).hasValue("Pending Review (Uploaded " + today + ")");
			softly.assertThat(documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_SMART_DRIVER_COURSE_COMPLETION)).hasValue("Yes");
		});
	}

	/**
	 * @author Oleg Stasyuk
	 * @name RFI
	 * @scenario
	 * Valid only for  <parameter name="state" value="AZ, IN, OH, OK, NV"/>
	 * 1.Initiate quote creation.
	 * Auto Insurance Application
	 * Non-Owner Automobile Endorsement
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-349", "PAS-341", "PAS-20333"})
	public void pas349_rfiNano(@Optional("NV") String state) {
		createQuoteWithCustomDataNano(state);

		assertSoftly(softly -> {
			//TODO Question to Karen: IN has Uninsured Property Damage = No COverage, disabled, but required to have at least 25k
			String policyNumber = testEValueDiscount.simplifiedQuoteIssue();
			rfiDocumentContentCheckNano(policyNumber, softly);

			//PAS-341 Start
			RfiDocumentResponse[] result = HelperCommon.executeRequestRfi(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION.getLabel(), "PAA", "NS", softly);
			//TODO Question to Karen: NV returns "Non-Owner Automobile Endorsement Form" instead of "Non-Owner Automobile Endorsement"
			HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.NON_OWNER_AUTOMOBILE_ENDORSEMENT.getLabel(), "NONOE", "NS", softly);
			//PAS-341 End
		});
	}

	private void createQuoteWithCustomData(String state) {
		assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION)).hasValue("Not Signed");
		if (!"ID, KS, KY, MT, NV, NY, OR, UT, WY, MD, WV".contains(state)) {
			assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT_OF_TERMS)).hasValue("Not Signed");
		}
		assertThat(documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_CURRENT_INSURANCE_FOR)).hasValue("No");
		assertThat(documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_GOOD_STUDENT_DISCOUNT)).hasValue("No");
		assertThat(documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_SMART_DRIVER_COURSE_COMPLETION)).hasValue("No");
		assertThat(documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_PRIOR_INSURANCE)).hasValue("No");
		assertThat(documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_PURCHASE_DATE_BILL_OF_SALE_FOR_NEW_VEHICLES)).hasValue("No");
		assertThat(documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_EQUIVALENT_NEW_CAR_ADDED_PROTECTION_WITH_PRIOR_CARRIER_FOR_NEW_VEHICLES)).hasValue("No");
		assertThat(documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.CANADIAN_MVR_FOR_DRIVER)).hasValue("No");
		assertThat(documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PHOTOS_FOR_SALVATAGE_VEHICLE_WITH_PHYSICAL_DAMAGE_COVERAGE)).hasValue("No");
		assertThat(documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_DEFENSIVE_DRIVER_COURSE_COMPLETION)).hasValue("No");

		documentsAndBindTab.saveAndExit();
	}

	private void uploadDocuments(String policyNumber, String state, ETCSCoreSoftAssertions softly) {
		LocalDateTime uploadDate = DateTimeUtils.getCurrentDateTime();
		String formattedDate = uploadDate.format(DateTimeUtils.MM_DD_YYYY);
		DBService.get()
				.executeUpdate(String.format(HelperRfi.UPDATE_DOCUMENT_STATUS, formattedDate, AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION.getLabel(), policyNumber));
		DBService.get().executeUpdate(String
				.format(HelperRfi.UPDATE_DOCUMENT_STATUS, formattedDate, AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT_OF_TERMS
						.getLabel(), policyNumber));
		DBService.get().executeUpdate(String
				.format(HelperRfi.UPDATE_DOCUMENT_STATUS, formattedDate, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_GOOD_STUDENT_DISCOUNT.getLabel(), policyNumber));
		DBService.get().executeUpdate(String
				.format(HelperRfi.UPDATE_DOCUMENT_STATUS, formattedDate, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_SMART_DRIVER_COURSE_COMPLETION.getLabel(), policyNumber));

		String currentVersion = DBService.get().getValue(String.format(HelperRfi.GET_POLICY_SUMMARY_FIELD, "version", policyNumber)).get();
		String latestPolicySummaryId = DBService.get().getValue(String.format(HelperRfi.GET_POLICY_SUMMARY_FIELD, "id", policyNumber)).get();
		DBService.get().executeUpdate(String.format(HelperRfi.UPDATE_POLICY_VERSION, String.valueOf(Integer.valueOf(currentVersion) + 1), latestPolicySummaryId));

		RfiDocumentResponse[] result = HelperCommon.executeRequestRfi(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION.getLabel(), "PAA", "PS", softly);
		if (!"ID, KS, KY, MT, NV, NY, OR, UT, WY, MD, WV".contains(state)) {
			HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT_OF_TERMS.getLabel(), "AAAUBI1B", "PS", softly);
		}

		HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_CURRENT_INSURANCE_FOR.getLabel(), "PIF", "NS", softly);
		HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_GOOD_STUDENT_DISCOUNT.getLabel(), "PGSD", "PS", softly);
		HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_SMART_DRIVER_COURSE_COMPLETION.getLabel(), "PSDCC", "PS", softly);
		HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_PRIOR_INSURANCE.getLabel(), "PPI", "NS", softly);
		HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_PURCHASE_DATE_BILL_OF_SALE_FOR_NEW_VEHICLES.getLabel(), "PPDNV", "NS", softly);
		HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_EQUIVALENT_NEW_CAR_ADDED_PROTECTION_WITH_PRIOR_CARRIER_FOR_NEW_VEHICLES
				.getLabel(), "PENCAP", "NS", softly);
		HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.CANADIAN_MVR_FOR_DRIVER.getLabel(), "CMD", "NS", softly);
		HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PHOTOS_FOR_SALVATAGE_VEHICLE_WITH_PHYSICAL_DAMAGE_COVERAGE.getLabel(), "PSVPDC", "NS", softly);
		HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_DEFENSIVE_DRIVER_COURSE_COMPLETION.getLabel(), "PODDCC", "NS", softly);
	}

	private static void rfiDocumentContentCheck(String policyNum, String state, ETCSCoreSoftAssertions softly) {
		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNum, "AARFIXX", "POLICY_ISSUE");
		//BUG PAS-7702 When issuing some policies, documents are not generated
		DocGenHelper.getDocumentDataSectionsByName("CoverageDetails", DocGenEnum.Documents.AARFIXX, query).get(0).getDocumentDataElements();

		HelperRfi.rfiTagCheck(query, "PrevInsDiscYN", "Y", softly);
		HelperRfi.rfiTagCheck(query, "GoodStuDiscYN", "Y", softly);
		HelperRfi.rfiTagCheck(query, "VehNwAddPrtcYN", "Y", softly);
		HelperRfi.rfiTagCheck(query, "CurInsDrvrYN", "Y", softly);
		HelperRfi.rfiTagCheck(query, "SmrtDrvrCrseCertYN", "Y", softly);
		HelperRfi.rfiTagCheck(query, "VehNwAddPrtcPrevCrirYN", "Y", softly);
		HelperRfi.rfiTagCheck(query, "SalvVehYN", "Y", softly);
		HelperRfi.rfiTagCheck(query, "PsnlAutoApplYN", "Y", softly);
		HelperRfi.rfiTagCheck(query, "CanMVRYN", "Y", softly);
		if (!"ID, KS, KY, MT, NV, NY, OR, UT, WY, MD, WV".contains(state)) {
			//TODO UBITrmCndtnYN is N, but the RFI contains it. Kinda illogical
			HelperRfi.rfiTagCheck(query, "UBITrmCndtnYN", "N", softly);
		}
		HelperRfi.rfiTagCheck(query, "DfnsDrvrCmplYN", "Y", softly);
	}

	private static void rfiDocumentContentCheckNano(String policyNum, ETCSCoreSoftAssertions softly) {
		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNum, "AARFIXX", "POLICY_ISSUE");
		DocGenHelper.getDocumentDataSectionsByName("CoverageDetails", DocGenEnum.Documents.AARFIXX, query).get(0).getDocumentDataElements();

		HelperRfi.rfiTagCheck(query, "PsnlAutoApplYN", "Y", softly);
		//Non-Owner Automobile Endorsement tag
		HelperRfi.rfiTagCheck(query, "VehTyp", "NO", softly);
	}

	private void createQuoteWithCustomDataNano(String state) {
		mainApp().open();
		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(testDataManager.getDefault(TestPolicyNano.class).getTestData("TestData_" + state), DocumentsAndBindTab.class, false);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

		assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION)).hasValue("Not Signed");
		assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.NON_OWNER_AUTOMOBILE_ENDORSEMENT)).hasValue("Not Signed");
		documentsAndBindTab.saveAndExit();
	}

	private void endorseRateDocuments() {
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
	}

	private void bindEndorsement(String policyNumber, String doccId, String errorCode, String errorMessage, String field, boolean isRuleOverridden) {
		//Verify Error Code and Error message on bind
		//if rule is overridden at NB or Endorsement, then it will not fire anymore
		if (!isRuleOverridden) {
			helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, errorCode, errorMessage, field);
			//Bind policy with doccId for successful bind and doccument is electronically signed
			HelperCommon.endorsementBind(policyNumber, "Megha Gubbala", Response.Status.OK.getStatusCode(), doccId);
		} else {
			//Bind without Document Sign ----Rule is not fired as it was overridden at NB
			HelperCommon.endorsementBind(policyNumber, "Megha Gubbala", Response.Status.OK.getStatusCode());
		}
	}

	private String policyCreationForAASCDC(String coverageId, String newCoverage, TestData td) {
		//Create Policy
		String policyNumber = openAppAndCreatePolicy(td);

		//Create endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		//update coverage
		HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageId, newCoverage), PolicyCoverageInfo.class);
		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		//TODO-mstrazds: no document needs to be signed ?
		return policyNumber;
	}

	private void verifyDoccInDb(ETCSCoreSoftAssertions softly, String query, DocGenEnum.Documents aa52va, boolean isRuleOverridden) {
		if (isRuleOverridden) {
			validateDocSignTagsNotExist(aa52va, query);
		} else {
			Document thankYouLetter615121 = DocGenHelper.getDocument(aa52va, query);
			String name = DocGenHelper.getDocumentDataElemByName("DocSignedBy", thankYouLetter615121).getDataElementChoice().getTextField();
			String date = DocGenHelper.getDocumentDataElemByName("DocSignedDate", thankYouLetter615121).getDataElementChoice().getDateTimeField();
			String currentDate = DateTimeUtils.getCurrentDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			softly.assertThat(name).isEqualTo("Megha Gubbala");
			softly.assertThat(date).startsWith(currentDate);
		}
	}

	private void goToPasAndVerifyRuleAndSignedBy(ETCSCoreSoftAssertions softly, String policyNumber,
			AssetDescriptor<RadioGroup> documentAsset, AssetDescriptor<? extends AbstractEditableStringElement> coverageAsset,
			String coverageLimit, ErrorEnum.Errors error, boolean isRuleOverridden) {
		//create endorsement from pas go to bind page verify document is electronically signed
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

		if (!isRuleOverridden) {
			softly.assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(documentAsset).getValue()).isEqualTo("Electronically Signed");
		} else {
			softly.assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(documentAsset).getValue()).isEqualTo("Not Signed");
		}

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		//From P&C page change coverage again to verify signed by is resetting to  not signed
		premiumAndCoveragesTab.setPolicyCoverageDetailsValue(coverageAsset.getLabel(), coverageLimit);
		premiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		//add line to verify not signed
		softly.assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(documentAsset).getValue()).isEqualTo("Not Signed");
		documentsAndBindTab.submitTab();
		if (!isRuleOverridden) {
			//On bind verify error message
			errorTab.verify.errorsPresent(true, error);
			errorTab.cancel();
			//Physically sign the doccument and bind policy
			documentsAndBindTab.getRequiredToBindAssetList().getAsset(documentAsset).setValue("Physically Signed");
			documentsAndBindTab.submitTab();
		}
	}
}
