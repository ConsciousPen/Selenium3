/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ss.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import static aaa.main.enums.ErrorEnum.Errors.*;
import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import aaa.common.pages.Page;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.main.enums.*;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.rest.dtoAdmin.RfiDocumentResponse;
import aaa.helpers.rest.dtoDxp.*;
import aaa.helpers.xml.model.Document;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.sales.auto_ss.TestPolicyNano;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import aaa.modules.regression.service.helper.*;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
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
	private static final String VIN_LESS_THAN_7_YEARS = "WAUDGAFL1EA123034";
	private static final String VIN_MORE_THAN_7_YEARS = "WBAAD1300J8851614";
	private final VehicleTab vehicleTab = new VehicleTab();
	private final DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
	private final TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
	private HelperMiniServices helperMiniServices = new HelperMiniServices();
	private final PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private ErrorTab errorTab = new ErrorTab();
	private static final TestMiniServicesVehiclesHelper VEH_HELPER = new TestMiniServicesVehiclesHelper();
	private static final TestMiniServicesCoveragesHelper COV_HELPER = new TestMiniServicesCoveragesHelper();

	private ComboBox tortCoverage = premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.TORT_THRESHOLD);
	private ComboBox biCoverage = premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY);
	private ComboBox uimbiCoverage = premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY);

	private RadioGroup aadnpabRule = documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.PA_NOTICE_NAMED_INSURED_REGARDING_TORT_OPTIONS);
	private RadioGroup ruuelluuRule = documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.IMPORTANT_NOTICE_UNINSURED_MOTORIST_COVERAGE);
	private RadioGroup aadnde1Rule = documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.DELAWARE_MOTORISTS_PROTECTION_ACT);
	private RadioGroup aacsdcRule = documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.DISTRICT_OF_COLUMBIA_COVERAGE_SELECTION_REJECTION_FORM);
	private static final AssetDescriptor<RadioGroup> REQUIRED_TO_BIND_AAIFNJ3 = AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.ACNOWLEDGEMENT_OF_REQUIREMENT_FOR_INSURANCE_INSPECTION;
	private static final AssetDescriptor<RadioGroup> REQUIRED_TO_BIND_AAIFNJ4 = AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.INSPECTION_WAIVER_SALES_AGREEMENT_REQUIRED;
	private static final AssetDescriptor<RadioGroup> REQUIRED_TO_BIND_AAIFNYD = AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.ACKNOWLEDGEMENT_OF_REQUIREMENT_FOR_PHOTO_INSPECTION;
	private static final AssetDescriptor<RadioGroup> REQUIRED_TO_BIND_AAIFNYE = AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.COPY_OF_SALES_AGREEMENT;

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

			verifyRFIHasNoDocuments(policyNumber);

			//update UM coverage
			HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest("UMBI", "25000/50000"), PolicyCoverageInfo.class);

			checkDocumentInRfiService(policyNumber, "RUUELLUU", "IMPORTANT NOTICE - Uninsured Motorist Coverage");

			helperMiniServices.endorsementRateAndBind(policyNumber);
			helperMiniServices.createEndorsementWithCheck(policyNumber);
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			verifyRFIHasNoDocuments(policyNumber);

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
		String policyNumber = policyCreationForRFI(pipmedical, s, getPolicyDefaultTD());
		verifyRateBindPasForAACSDC(softly, policyNumber);
	}

	private void verifyRateBindPasForAACSDC(ETCSCoreSoftAssertions softly, String policyNumber) {
		//Verify RFI service and verify it returns doccid
		String doccId = checkDocumentInRfiService(policyNumber, "AACSDC", "District of Columbia Coverage Selection/Rejection Form");

		//Verify Bind service
		bindEndorsement(policyNumber, doccId, ERROR_200900.getCode(), ERROR_200900.getMessage(), false);
		//Verify DB Endorsement xml Signed by field is there
		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "AACSDC", "ENDORSEMENT_ISSUE");
		verifyDocInDb(softly, query, DocGenEnum.Documents.AACSDC, true);

		//Go to pas and and verify
		goToPasAndVerifyRuleAndSignedBy(softly, policyNumber, AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.DISTRICT_OF_COLUMBIA_COVERAGE_SELECTION_REJECTION_FORM,
				AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORISTS_BODILY_INJURY, "$50,000/$100,000", ERROR_200900, false);
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
		ErrorEnum.Errors error = ERROR_200123;

		verifyRFIScenarios("BI", AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY, "25000/50000", "$50,000/$100,000", document, documentAsset, error, getPolicyDefaultTD(), true, false);
		verifyRFIScenarios("PD", AutoSSMetaData.PremiumAndCoveragesTab.PROPERTY_DAMAGE_LIABILITY, "15000", "$25,000", document, documentAsset, error, getPolicyDefaultTD(), true, false);
		verifyRFIScenarios("UMBI", AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY, "25000/50000", "$50,000/$100,000", document, documentAsset, error, getPolicyDefaultTD(), true, false);
		verifyRFIScenarios("PIP", AutoSSMetaData.PremiumAndCoveragesTab.PERSONAL_INJURY_PROTECTION, "25000/50000", "$50,000/$100,000", document, documentAsset, error, getPolicyDefaultTD(), true, false);
		verifyRFIScenarios("PIPDED", AutoSSMetaData.PremiumAndCoveragesTab.PERSONAL_INJURY_PROTECTION_DEDUCTIBLE, "250", "$500", document, documentAsset, error, getPolicyDefaultTD(), true, false);
		verifyRFIScenarios("PIPDEDAPPTO", AutoSSMetaData.PremiumAndCoveragesTab.PERSONAL_INJURY_PROTECTION_APPLIES_TO_DEDUCTIBLE, "F", "Named Insured Only", document, documentAsset, error, getPolicyDefaultTD(), true, false);

		//Override rule at NB
		TestData td = getPolicyDefaultTD();
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.DocumentsAndBindTab.class.getSimpleName(), AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND.getLabel(), documentAsset.getLabel()), "Not Signed");
		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");
		td = td.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError).resolveLinks();
		verifyRFIScenarios("BI", AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY, "25000/50000", "$50,000/$100,000", document, documentAsset, error, td, true, true);
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
		ErrorEnum.Errors error = ERROR_200305;

		TestData td = getPolicyDefaultTD();
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.TORT_THRESHOLD.getLabel()), "contains=Full Tort");//to not get TORT rule

		//First Party Benefits <> Added
		verifyRFIScenarios("FPB", AutoSSMetaData.PremiumAndCoveragesTab.FIRST_PARTY_BENEFITS, CoverageLimits.COV_FPB_ADDED.getLimit(), "Basic", document, documentAsset, error, td, true, false);
		verifyRFIScenarios("FPB", AutoSSMetaData.PremiumAndCoveragesTab.FIRST_PARTY_BENEFITS, CoverageLimits.COV_FPB_50K_TOTAL.getLimit(), CoverageLimits.COV_FPB_100K_TOTAL_PAS_UI_DISPLAY.getDisplay(), document, documentAsset, error, td, true, false);

		//First Party Benefits = Added
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.FIRST_PARTY_BENEFITS.getLabel()), "contains=Added");
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.ACCIDENTAL_DEATH_BENEFIT.getLabel()), "contains=$15,000");
		verifyRFIScenarios("MEDPM", AutoSSMetaData.PremiumAndCoveragesTab.MEDICAL_BENEFIT, "50000", "$10,000", document, documentAsset, error, td, true, false);
		verifyRFIScenarios("ADBC", AutoSSMetaData.PremiumAndCoveragesTab.ACCIDENTAL_DEATH_BENEFIT, "0", "$25,000", document, documentAsset, error, td, true, false);
		verifyRFIScenarios("FUNERAL", AutoSSMetaData.PremiumAndCoveragesTab.FUNERAL_EXPENSE_BENEFIT, "2500", "No Coverage", document, documentAsset, error, td, true, false);
		verifyRFIScenarios("IL", AutoSSMetaData.PremiumAndCoveragesTab.WORK_LOSS_BENEFIT, CoverageLimits.COV_IL_25000_MAX_PA.getLimit(), CoverageLimits.COV_IL_15000_MAX_PA.getDisplay(), document, documentAsset, error, td, true, false);
		verifyRFIScenarios("EMB", AutoSSMetaData.PremiumAndCoveragesTab.EXTRAORDINARY_MEDICAL_EXPENSE_BENEFITS, CoverageLimits.COV_EMB_1000.getLimit(), CoverageLimits.COV_EMB_0.getDisplay(), document, documentAsset, error, td, true, false);

		//Create policy and override rule
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.DocumentsAndBindTab.class.getSimpleName(), AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND.getLabel(), AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.FIRST_PARTY_BENEFITS_COVERAGE_AND_LIMITS_SELECTION_FORM.getLabel()), "Not Signed");
		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");
		td = td.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError).resolveLinks();
		verifyRFIScenarios("MEDPM", AutoSSMetaData.PremiumAndCoveragesTab.MEDICAL_BENEFIT, "50000", "$10,000", document, documentAsset, error, td, true, true);
	}

	/**
	 * @name RFI AA52UPAA Form
	 * @scenario 1
	 * 1. Create policy.
	 * 2. Create endorsement outside of PAS.
	 * 3. Rate. Hit RFI service.
	 * 4. Check the response.
	 * 5. Update UMBI/UMSU coverage. Rate.
	 * 6. Hit RFI service, check if document is displaying.
	 * 7. Run bind service without signing document and verify error. and policy is not bound.
	 * 8. Run bind service with document id verify no error and we can bind the policy.
	 * 9. go to pas UI and verify if policy is bound
	 * 10. Go to document and bind page and verify if document is electronically signed.
	 *
	 * 11. create an endorsement on policy from pas change coverage and rate the policy
	 * 12. go to document and bind page verify if its reset to document not signed
	 * 13. Try to bind policy from pas and verify error.
	 * 14. Select document physically signed
	 * 15.  Bind the policy verify there is no error message.
	 *
	 * @scenario 2
	 * 1. Create policy and override the rule
	 * 2. Create endorsement outside of PAS.
	 * 3. Trigger the document by updating one of the coverages (UMBI or UMSU)
	 * 4. Hit RFI service and check that docuemnt is returned
	 * 5. Bind Endorsement ---> No rule is fired (as it was overriden at NB)
	 *
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.PA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-23301", "PAS-24637", "PAS-24637"})
	public void pas23301_AA52UPAAFormRFI(@Optional("PA") String state) {
		DocGenEnum.Documents document = DocGenEnum.Documents.AA52UPAA;
		AssetDescriptor<RadioGroup> documentAsset = AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNUNSURED_MOTORISTS_COVERAGE_SELECTION_REJECTION;
		ErrorEnum.Errors error = ERROR_200306;

		// scenario 1
		TestData td = getPolicyDefaultTD();
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.TORT_THRESHOLD.getLabel()), "contains=Full Tort");//to not get TORT rule

		verifyRFIScenarios("UMBI", AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORISTS_BODILY_INJURY, CoverageLimits.COV_50100.getLimit(), CoverageLimits.COV_00.getDisplay(), document, documentAsset, error, td, true, false);
		verifyRFIScenarios("UMBI", AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORISTS_BODILY_INJURY, CoverageLimits.COV_00.getLimit(), CoverageLimits.COV_2550.getDisplay(), document, documentAsset, error, td, true, false);
		verifyRFIScenarios("UMSU", AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORIST_STACKED_UNSTACKED, CoverageLimits.COV_UNSTACKED.getLimit(), CoverageLimits.COV_STACKED.getDisplay(), document, documentAsset, error, td, true, false);

		// sceanrio 2
		// Create policy and override rule
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.DocumentsAndBindTab.class.getSimpleName(), AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND.getLabel(), AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNUNSURED_MOTORISTS_COVERAGE_SELECTION_REJECTION.getLabel()), "Not Signed");
		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");
		td = td.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError).resolveLinks();

		verifyRFIScenarios("UMBI", AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORISTS_BODILY_INJURY, CoverageLimits.COV_50100.getLimit(), CoverageLimits.COV_2550.getDisplay(), document, documentAsset, error, td, true, true);
		verifyRFIScenarios("UMBI", AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORISTS_BODILY_INJURY, CoverageLimits.COV_00.getLimit(), CoverageLimits.COV_2550.getDisplay(), document, documentAsset, error, td, true, true);
		verifyRFIScenarios("UMSU", AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORIST_STACKED_UNSTACKED, CoverageLimits.COV_UNSTACKED.getLimit(), CoverageLimits.COV_STACKED.getDisplay(), document, documentAsset, error, td, true, true);
	}

	private void verifyRFIScenarios(String coverageCd, AssetDescriptor<? extends AbstractEditableStringElement> coverageAsset, String updateLimitDXP, String updateLimitPAS, DocGenEnum.Documents document, AssetDescriptor<RadioGroup> documentAsset, ErrorEnum.Errors error, TestData td, boolean checkDocXML, boolean isRuleOverridden) {
		assertSoftly(softly -> {

			String policyNumber = policyCreationForRFI(coverageCd, updateLimitDXP, td);
			String doccId = checkDocumentInRfiService(policyNumber, document.getId(), document.getName());
			bindEndorsement(policyNumber, doccId, error.getCode(), error.getMessage(), isRuleOverridden);

			String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, document.getIdInXml(), AaaDocGenEntityQueries.EventNames.ENDORSEMENT_ISSUE);
			if (checkDocXML) {
				verifyDocInDb(softly, query, document, true); //tags expected only for Electronically signed doc
			}

			//Go to PAS and verify
			goToPasAndVerifyRuleAndSignedBy(softly, policyNumber, documentAsset, coverageAsset, updateLimitPAS, error, isRuleOverridden);
			//Verify Signed by is not there in XML when Signed from PAS UI.
			if (checkDocXML) {
				if ((document.equals(DocGenEnum.Documents.AA52UPAA) || document.equals(DocGenEnum.Documents.AA52IPAA) || document.equals(DocGenEnum.Documents.AAFPPA)) && !isRuleOverridden) { //isRuleOverridden means that Document was not signed.
					DocGenHelper.checkDocumentsDoesNotExistInXml(policyNumber, AaaDocGenEntityQueries.EventNames.ENDORSEMENT_ISSUE, document);// Document does not exist
				} else {
					validateDocSignTagsNotExist(document, query); //Document doesn't contain DocSignTags if signed in PAS
				}
			}
		});
	}

	/**
	 * @name RFI AA52IPAA Form
	 * @scenario 1
	 * 1. Create policy.
	 * 2. Create endorsement outside of PAS.
	 * 3. Rate. Hit RFI service.
	 * 4. Check the response.
	 * 5. Update UIMBI/UIMSU coverage. Rate.
	 * 6. Hit RFI service, check if document is displaying.
	 * 7. Run bind service without signing document and verify error. and policy is not bound.
	 * 8. Run bind service with document id verify no error and we can bind the policy.
	 * 9. go to pas UI and verify if policy is bound
	 * 10. Go to document and bind page and verify if document is electronically signed.
	 * 11. create an endorsement on policy from pas change coverage and rate the policy
	 * 12. go to document and bind page verify if its reset to document not signed
	 * 13. Try to bind policy from pas and verify error.
	 * 14. Select document physically signed
	 * 15.  Bind the policy verify there is no error message.
	 *
	 * @scenario 2
	 * 1. Create policy and override the rule
	 * 2. Create endorsement outside of PAS.
	 * 3. Trigger the document by updating one of the coverages (UMBI or UMSU)
	 * 4. Hit RFI service and check that docuemnt is returned
	 * 5. Bind Endorsement ---> No rule is fired (as it was overriden at NB)
	 *
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.PA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-23302", "PAS-24637"})
	public void pas23302_AA52IPAAFormRFI(@Optional("PA") String state) {
		DocGenEnum.Documents document = DocGenEnum.Documents.AA52IPAA;
		AssetDescriptor<RadioGroup> documentAsset = AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNDERINSURED_MOTORISTS_COVERAGE_SELECTION_REJECTION;
		ErrorEnum.Errors error = ERROR_AAA_200307;

		TestData td = getPolicyDefaultTD();
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.TORT_THRESHOLD.getLabel()), "contains=Full Tort");//do not get TORT rule

		verifyRFIScenarios("UIMBI", AutoSSMetaData.PremiumAndCoveragesTab.UNDERINSURED_MOTORISTS_BODILY_INJURY, CoverageLimits.COV_50100.getLimit(), CoverageLimits.COV_00.getDisplay(), document, documentAsset, error, td, true, false);
		verifyRFIScenarios("UIMBI", AutoSSMetaData.PremiumAndCoveragesTab.UNDERINSURED_MOTORISTS_BODILY_INJURY, CoverageLimits.COV_00.getLimit(), CoverageLimits.COV_2550.getDisplay(), document, documentAsset, error, td, true, false);
		verifyRFIScenarios("UIMSU", AutoSSMetaData.PremiumAndCoveragesTab.UNDERINSURED_MOTORIST_STACKED_UNSTACKED, CoverageLimits.COV_UNSTACKED.getLimit(), CoverageLimits.COV_STACKED.getDisplay(), document, documentAsset, error, td, true, false);

		//Create policy and override rule
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.DocumentsAndBindTab.class.getSimpleName(), AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND.getLabel(), AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNDERINSURED_MOTORISTS_COVERAGE_SELECTION_REJECTION.getLabel()), "Not Signed");
		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");
		td = td.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError).resolveLinks();
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.TORT_THRESHOLD.getLabel()), "contains=Full Tort");//to not get TORT rule
		verifyRFIScenarios("UIMBI", AutoSSMetaData.PremiumAndCoveragesTab.UNDERINSURED_MOTORISTS_BODILY_INJURY, CoverageLimits.COV_50100.getLimit(), CoverageLimits.COV_2550.getDisplay(), document, documentAsset, error, td, true, true);
		verifyRFIScenarios("UIMBI", AutoSSMetaData.PremiumAndCoveragesTab.UNDERINSURED_MOTORISTS_BODILY_INJURY, CoverageLimits.COV_00.getLimit(), CoverageLimits.COV_2550.getDisplay(), document, documentAsset, error, td, true, true);
		verifyRFIScenarios("UIMSU", AutoSSMetaData.PremiumAndCoveragesTab.UNDERINSURED_MOTORIST_STACKED_UNSTACKED, CoverageLimits.COV_UNSTACKED.getLimit(), CoverageLimits.COV_STACKED.getDisplay(), document, documentAsset, error, td, true, true);
	}

	private void validateDocSignTagsNotExist(DocGenEnum.Documents document, String query) {
		assertThat(DocGenHelper.getDocument(document, query).toString().contains("DocSignedBy")).isFalse();
		assertThat(DocGenHelper.getDocument(document, query).toString().contains("DocSignedDate")).isFalse();
	}

	/**
	 * @author Maris Strazds
	 * @name RFI AA52UPAA and AA52IPAA vehen Adding Vehicle
	 * @scenario 1
	 * 1. Create policy.
	 * 2. Create endorsement outside of PAS.
	 * 3. Add/replace vehicle through service
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
	 * @scenario 2
	 * 1. Create policy and override the rule
	 * 2. Create endorsement outside of PAS.
	 * 3. Trigger the document by adding vehicle
	 * 4. Hit RFI service and check that document is returned
	 * 5. Bind Endorsement ---> No rule is fired (as it was overridden at NB)
	 *
	 */

	@Parameters({"state"})
	@StateList(states = {Constants.States.PA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-23301", "PAS-23302", "PAS-24637"})
	public void pas23301_AA52UPAAFormAddReplaceVehicleTriggerRFI(@Optional("PA") String state) {
		TestData td = getPolicyDefaultTD();
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.TORT_THRESHOLD.getLabel()), "contains=Full Tort");//to not get TORT rule
		validateRFIScenariosAddVehicle_pas23301_pas23302(td, false);

		//Create policy and override rule 200306 and rule 200307
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.DocumentsAndBindTab.class.getSimpleName(), AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND.getLabel(), AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNUNSURED_MOTORISTS_COVERAGE_SELECTION_REJECTION.getLabel()), "Not Signed");
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.DocumentsAndBindTab.class.getSimpleName(), AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND.getLabel(), AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNDERINSURED_MOTORISTS_COVERAGE_SELECTION_REJECTION.getLabel()), "Not Signed");
		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");
		td = td.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError).resolveLinks();
		validateRFIScenariosAddVehicle_pas23301_pas23302(td, true);

	}

	private void validateRFIScenariosAddVehicle_pas23301_pas23302(TestData td, boolean isRuleOverridden) {
		DocGenEnum.Documents documentAA52UPAA = DocGenEnum.Documents.AA52UPAA;
		DocGenEnum.Documents documentAA52IPAA = DocGenEnum.Documents.AA52IPAA;
		AssetDescriptor<RadioGroup> documentAssetUninsured = AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNUNSURED_MOTORISTS_COVERAGE_SELECTION_REJECTION;
		AssetDescriptor<RadioGroup> documentAssetUnderinsured = AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNDERINSURED_MOTORISTS_COVERAGE_SELECTION_REJECTION;
		ErrorEnum.Errors error200306 = ERROR_200306;
		ErrorEnum.Errors error200307 = ERROR_AAA_200307;

		String policyNumber = openAppAndCreatePolicy(td);
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		VEH_HELPER.helperMiniServices.addVehicleWithChecks(policyNumber, "2013-02-22", "1HGEM21504L055795", true);

		String docId1 = checkDocumentInRfiService(policyNumber, documentAA52UPAA.getId(), documentAA52UPAA.getName());
		String docId2 = checkDocumentInRfiService(policyNumber, documentAA52IPAA.getId(), documentAA52IPAA.getName());

		if (!isRuleOverridden) {
			helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, error200306.getCode(), error200306.getMessage());
			helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, error200307.getCode(), error200307.getMessage());
		}
		//Bind policy with docId and document is electronically signed
		HelperCommon.endorsementBind(policyNumber, "Megha Gubbala", Response.Status.OK.getStatusCode(), docId1, docId2);

		assertSoftly(softly -> {
			String queryAA52IPAA = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, documentAA52IPAA.getIdInXml(), AaaDocGenEntityQueries.EventNames.ENDORSEMENT_ISSUE);
			String queryAA52UPAA = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, documentAA52UPAA.getIdInXml(), AaaDocGenEntityQueries.EventNames.ENDORSEMENT_ISSUE);
			verifyDocInDb(softly, queryAA52IPAA, documentAA52IPAA, true);
			verifyDocInDb(softly, queryAA52UPAA, documentAA52UPAA, true);

			//create endorsement from, pas go to bind page verify document is electronically signed
			mainApp().open();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
			softly.assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(documentAssetUninsured).getValue()).isEqualTo("Electronically Signed");
			softly.assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(documentAssetUnderinsured).getValue()).isEqualTo("Electronically Signed");
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

			//Add Vehicle from PAS UI and validate
			TestData td1 = getPolicyTD("DataGather", "TestData");
			TestData testData = td1.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_AddVewhicle").getTestDataList("VehicleTab")).resolveLinks();
			policy.getDefaultView().fillFromTo(testData, VehicleTab.class, VehicleTab.class, true);
			premiumAndCoveragesTab.calculatePremium();
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

			softly.assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(documentAssetUninsured).getValue()).isEqualTo("Not Signed");
			softly.assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(documentAssetUnderinsured).getValue()).isEqualTo("Not Signed");
			documentsAndBindTab.submitTab();
			if (isRuleOverridden) {
				assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE); //indicates Endorsement bound successfully
				//Document Package should contain document if it is required and not Signed in PAS
				verifyDocInDb(softly, queryAA52IPAA, documentAA52IPAA, false);
				verifyDocInDb(softly, queryAA52UPAA, documentAA52UPAA, false);
			} else {
				//On bind verify error message
				errorTab.verify.errorsPresent(true, error200306);
				errorTab.verify.errorsPresent(true, error200307);
				errorTab.cancel();
				//Physically sign the document and bind policy
				documentsAndBindTab.getRequiredToBindAssetList().getAsset(documentAssetUninsured).setValue("Physically Signed");
				documentsAndBindTab.getRequiredToBindAssetList().getAsset(documentAssetUnderinsured).setValue("Physically Signed");
				documentsAndBindTab.submitTab();

				assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE); //indicates Endorsement bound successfully
				//Document Package should not contain document if it is required and Signed in PAS
				DocGenHelper.checkDocumentsDoesNotExistInXml(policyNumber, AaaDocGenEntityQueries.EventNames.ENDORSEMENT_ISSUE, documentAA52IPAA);// Document does not exist
				DocGenHelper.checkDocumentsDoesNotExistInXml(policyNumber, AaaDocGenEntityQueries.EventNames.ENDORSEMENT_ISSUE, documentAA52UPAA);// Document does not exist
			}
		});

		//Verify Replace vehicle scenario (DXP)
		if (!isRuleOverridden) {
			helperMiniServices.createEndorsementWithCheck(policyNumber);
			ViewVehicleResponse viewVehicles = HelperCommon.viewPolicyVehicles(policyNumber);
			String vehicleOid = viewVehicles.vehicleList.get(0).oid;
			helperMiniServices.createEndorsementWithCheck(policyNumber);
			String replacedVehicleVin = "2T1BURHE4JC034340"; //Toyota Corolla 2018
			TestMiniServicesVehiclesHelper vehiclesHelper1 = new TestMiniServicesVehiclesHelper();
			vehiclesHelper1.replaceVehicleWithUpdates(policyNumber, vehicleOid, replacedVehicleVin, true, true);

			docId1 = checkDocumentInRfiService(policyNumber, documentAA52UPAA.getId(), documentAA52UPAA.getName());
			docId2 = checkDocumentInRfiService(policyNumber, documentAA52IPAA.getId(), documentAA52IPAA.getName());

			helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, error200306.getCode(), error200306.getMessage());
			helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, error200307.getCode(), error200307.getMessage());

			//Bind policy with docId and document is electronically signed
			HelperCommon.endorsementBind(policyNumber, "Megha Gubbala", Response.Status.OK.getStatusCode(), docId1, docId2);
			assertSoftly(softly -> {
				String queryAA52IPAA = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, documentAA52IPAA.getIdInXml(), AaaDocGenEntityQueries.EventNames.ENDORSEMENT_ISSUE);
				String queryAA52UPAA = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, documentAA52UPAA.getIdInXml(), AaaDocGenEntityQueries.EventNames.ENDORSEMENT_ISSUE);
				verifyDocInDb(softly, queryAA52IPAA, documentAA52IPAA, true);
				verifyDocInDb(softly, queryAA52UPAA, documentAA52UPAA, true);

				//create endorsement from, pas go to bind page verify document is electronically signed
				mainApp().open();
				SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
				policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
				NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
				softly.assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(documentAssetUninsured).getValue()).isEqualTo("Electronically Signed");
				softly.assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(documentAssetUnderinsured).getValue()).isEqualTo("Electronically Signed");
				documentsAndBindTab.saveAndExit();
			});
		}
	}

	/**
	 * @author Maris Strazds
	 * @name
	 * @scenario
	 * 1. Create policy in PAS
	 * 2. Create endorsement in PAS.
	 * 3. Add/replace vehicle in PAS
	 * 4. Check that Required for Bind Section does NOT include AAIFNJ3 OR AAIFNJ4
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.NJ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-23573", "PAS-24605"})
	public void pas23573_CARCONotNeededInsidePASAddReplaceVehicleTC01(@Optional("NJ") String state) {
		carcoNeededNotNeededInsidePASAddReplaceVehicle(true, 4,false, false,
                false, false, REQUIRED_TO_BIND_AAIFNJ3, REQUIRED_TO_BIND_AAIFNJ4,
                ERROR_200200_NJ, ERROR_200204_NJ, state);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.NJ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-23573", "PAS-24605"})
	public void pas23573_CARCONotNeededInsidePASAddReplaceVehicleTC02(@Optional("NJ") String state) {
		carcoNeededNotNeededInsidePASAddReplaceVehicle(true, 4, true, false,
                false, false, REQUIRED_TO_BIND_AAIFNJ3, REQUIRED_TO_BIND_AAIFNJ4,
                ERROR_200200_NJ, ERROR_200204_NJ, state);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.NJ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-23573", "PAS-24605"})
	public void pas23573_CARCONotNeededInsidePASAddReplaceVehicleTC03(@Optional("NJ") String state) {
		carcoNeededNotNeededInsidePASAddReplaceVehicle(true, 4, true, true,
                false, false, REQUIRED_TO_BIND_AAIFNJ3, REQUIRED_TO_BIND_AAIFNJ4,
                ERROR_200200_NJ, ERROR_200204_NJ, state);
	}

	/**
	 * @author Sabra Domeika
	 * @name
	 * @scenario
	 * 1. Create policy in PAS
	 * 2. Create endorsement in PAS.
	 * 3. Add/replace vehicle in PAS
	 * 4. Check that Required for Bind Section does NOT include AAIFNYD OR AAIFNYE
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.NY})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-25024", "PAS-27414"})
	public void pas25024_CARCONotNeededInsidePASAddReplaceVehicleTC01(@Optional("NY") String state) {
		carcoNeededNotNeededInsidePASAddReplaceVehicle(true, 2,false, false,
				false, false, REQUIRED_TO_BIND_AAIFNYD, REQUIRED_TO_BIND_AAIFNYE,
				ERROR_AAA_200200_NY, ERROR_AAA_200200_NY, state);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.NY})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-25024", "PAS-27414"})
	public void pas25024_CARCONotNeededInsidePASAddReplaceVehicleTC02(@Optional("NY") String state) {
		carcoNeededNotNeededInsidePASAddReplaceVehicle(true, 2, true, false,
				false, false, REQUIRED_TO_BIND_AAIFNYD, REQUIRED_TO_BIND_AAIFNYE,
				ERROR_AAA_200200_NY, ERROR_AAA_200200_NY, state);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.NY})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-25024", "PAS-27414"})
	public void pas25024_CARCONotNeededInsidePASAddReplaceVehicleTC03(@Optional("NY") String state) {
		carcoNeededNotNeededInsidePASAddReplaceVehicle(true, 2, true, true,
				false, false, REQUIRED_TO_BIND_AAIFNYD, REQUIRED_TO_BIND_AAIFNYE,
				ERROR_AAA_200200_NY, ERROR_AAA_200200_NY, state);
	}

	/**
	 * @author Maris Strazds
	 * @name
	 * @scenario
	 * 1. Create policy in PAS
	 * 2. Create endorsement in PAS
	 * 3. Add/replace qualifying vehicle inside PAS ('Less Than 1,000 miles' = Yes)
	 * 4. Check that Required for Bind Section includes AAIFNJ4
	 * 5. Try to bind without signing the document - error is displayed
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.NJ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-26123", "PAS-24605"})
	public void pas26123_CARCOFormAAIFNJ4InsidePASAddReplaceVehicle(@Optional("NJ") String state) {
		carcoNeededNotNeededInsidePASAddReplaceVehicle(false, 4, true, true,
				false, true, REQUIRED_TO_BIND_AAIFNJ3, REQUIRED_TO_BIND_AAIFNJ4,
				ERROR_200200_NJ, ERROR_200204_NJ, state);
	}

	/**
	 * @author Maris Strazds
	 * @name
	 * @scenario
	 * 1. Create policy in PAS
	 * 2. Create endorsement in PAS
	 * 3. Add/replace qualifying vehicle inside PAS ('Less Than 1,000 miles' = No or not required)
	 * 4. Check that Required for Bind Section includes AAIFNJ3 and it is reset to Not Signed
	 * 5. Try to bind without signing the document - error is displayed
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.NJ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-21648", "PAS-24605"})
	public void pas21648_CARCOFormAAIFNJ3InsidePASAddReplaceVehicleTC01(@Optional("NJ") String state) {
		carcoNeededNotNeededInsidePASAddReplaceVehicle(false, 4, true, false,
				true, false, REQUIRED_TO_BIND_AAIFNJ3, REQUIRED_TO_BIND_AAIFNJ4,
				ERROR_200200_NJ, ERROR_200204_NJ, state);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.NJ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-21648", "PAS-24605"})
	public void pas21648_CARCOFormAAIFNJ3InsidePASAddReplaceVehicleTC02(@Optional("NJ") String state) {
		carcoNeededNotNeededInsidePASAddReplaceVehicle(false, 4, false, false,
				false, false, REQUIRED_TO_BIND_AAIFNJ3, REQUIRED_TO_BIND_AAIFNJ4,
				ERROR_200200_NJ, ERROR_200204_NJ, state);
	}

	/**
	 * @author Maris Strazds
	 * @name
	 * @scenario
	 * 1. Create policy in PAS
	 * 2. Create endorsement in PAS
	 * 3. Add/replace qualifying vehicle inside PAS ('Less Than 1,000 miles' = Yes)
	 * 4. Check that Required for Bind Section includes AAIFNYE
	 * 5. Try to bind without signing the document - error is displayed
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.NY})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-25027"})
	public void pas25027_CARCOFormAAIFNYEInsidePASAddReplaceVehicle(@Optional("NY") String state) {
		carcoNeededNotNeededInsidePASAddReplaceVehicle(false, 2, true, true,
				false, true, REQUIRED_TO_BIND_AAIFNYD, REQUIRED_TO_BIND_AAIFNYE,
				ERROR_AAA_200200_NY, ERROR_AAA_200200_NY, state);
	}

	/**
	 * @author Maris Strazds
	 * @name
	 * @scenario
	 * 1. Create policy in PAS
	 * 2. Create endorsement in PAS
	 * 3. Add/replace qualifying vehicle inside PAS ('Less Than 1,000 miles' = No or not required)
	 * 4. Check that Required for Bind Section includes AAIFNYD and it is reset to Not Signed
	 * 5. Try to bind without signing the document - error is displayed
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.NY})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-25029"})
	public void pas25029_CARCOFormAAIFNYDInsidePASAddReplaceVehicleTC01(@Optional("NY") String state) {
		carcoNeededNotNeededInsidePASAddReplaceVehicle(false, 2, true, false,
				true, false, REQUIRED_TO_BIND_AAIFNYD, REQUIRED_TO_BIND_AAIFNYE,
				ERROR_AAA_200200_NY, ERROR_AAA_200200_NY, state);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.NY})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-25029"})
	public void pas25029_CARCOFormAAIFNYDInsidePASAddReplaceVehicleTC02(@Optional("NY") String state) {
		carcoNeededNotNeededInsidePASAddReplaceVehicle(false, 2, false, false,
				false, false, REQUIRED_TO_BIND_AAIFNYD, REQUIRED_TO_BIND_AAIFNYE,
				ERROR_AAA_200200_NY, ERROR_AAA_200200_NY, state);
	}

	/**
	 * @author Maris Strazds
	 * @name
	 * @scenario
	 * 1. Create policy in PAS with qualifying vehicle ('Less Than 1,000 miles' = No or not required), but without COMPDED
	 * 2. Create endorsement in PAS
	 * 3. Update vehicle to have COMPDED
	 * 4. Check that Required for Bind Section includes AAIFNJ3 and it is reset to Not Signed
	 * 5. Try to bind without signing the document - error is displayed
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.NJ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-21648", "PAS-24605"})
	public void pas21648_CARCOFormAAIFNJ3InsidePASUpdateCompTC01(@Optional("NJ") String state) {
		carcoUpdateCompScenariosInsidePAS(4, true, false, REQUIRED_TO_BIND_AAIFNJ3, REQUIRED_TO_BIND_AAIFNJ4, ERROR_200200_NJ, ERROR_200204_NJ);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.NJ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-21648", "PAS-24605"})
	public void pas21648_CARCOFormAAIFNJ3InsidePASUpdateCompTC02(@Optional("NJ") String state) {
		carcoUpdateCompScenariosInsidePAS(4, false, false, REQUIRED_TO_BIND_AAIFNJ3, REQUIRED_TO_BIND_AAIFNJ4, ERROR_200200_NJ, ERROR_200204_NJ);
	}

	/**
	 * @author Maris Strazds
	 * @name
	 * @scenario
	 * 1. Create policy in PAS with qualifying vehicle ('Less Than 1,000 miles' = Yes), but without COMPDED
	 * 2. Create endorsement in PAS
	 * 3. Update vehicle to have COMPDED
	 * 4. Check that Required for Bind Section includes AAIFNJ4 and it is reset to Not Signed
	 * 5. Try to bind without signing the document - error is displayed
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.NJ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-24531", "PAS-24605"})
	public void pas24531_CARCOFormAAIFNJ4InsidePASUpdateComp(@Optional("NJ") String state) {
		carcoUpdateCompScenariosInsidePAS(4, true, true, REQUIRED_TO_BIND_AAIFNJ3, REQUIRED_TO_BIND_AAIFNJ4, ERROR_200200_NJ, ERROR_200204_NJ);
	}

	/**
	 * @author Maris Strazds
	 * @name
	 * @scenario
	 * 1. Create policy in PAS with qualifying vehicle ('Less Than 1,000 miles' = No or not required), but without COMPDED
	 * 2. Create endorsement in PAS
	 * 3. Update vehicle to have COMPDED
	 * 4. Check that Required for Bind Section includes AAIFNYD and it is reset to Not Signed
	 * 5. Try to bind without signing the document - error is displayed
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.NY})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-21648", "PAS-24605"})
	public void pas25027_CARCOFormAAIFNYDInsidePASUpdateCompTC01(@Optional("NY") String state) {
		carcoUpdateCompScenariosInsidePAS(2, true, false, REQUIRED_TO_BIND_AAIFNYD, REQUIRED_TO_BIND_AAIFNYE, ERROR_AAA_200200_NY, ERROR_AAA_200200_NY);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.NY})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-21648", "PAS-24605"})
	public void pas25027_CARCOFormAAIFNYDInsidePASUpdateCompTC02(@Optional("NY") String state) {
		carcoUpdateCompScenariosInsidePAS(2, false, false, REQUIRED_TO_BIND_AAIFNYD, REQUIRED_TO_BIND_AAIFNYE, ERROR_AAA_200200_NY, ERROR_AAA_200200_NY);
	}

	/**
	 * @author Maris Strazds
	 * @name
	 * @scenario
	 * 1. Create policy in PAS with qualifying vehicle ('Less Than 1,000 miles' = Yes), but without COMPDED
	 * 2. Create endorsement in PAS
	 * 3. Update vehicle to have COMPDED
	 * 4. Check that Required for Bind Section includes AAIFNYE and it is reset to Not Signed
	 * 5. Try to bind without signing the document - error is displayed
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.NY})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-24531", "PAS-24605"})
	public void pas25027_CARCOFormAAIFNYEInsidePASUpdateComp(@Optional("NY") String state) {
		carcoUpdateCompScenariosInsidePAS(2, true, true, REQUIRED_TO_BIND_AAIFNYD, REQUIRED_TO_BIND_AAIFNYE, ERROR_AAA_200200_NY, ERROR_AAA_200200_NY);
	}

	//CARCO Outside PAS

	/**
	 * @author Maris Strazds
	 * @name
	 * @scenario
	 * 1. Create policy in PAS
	 * 2. Create endorsement outside PAS
	 * 3. Add/replace qualifying vehicle outside PAS ('Less Than 1,000 miles' = No or Not required)
	 * 4. Run RFI service - AAIFNJ3 is returned
	 * 5. Try to bind without signed AAIFNJ3 - error is returned
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.NJ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-21648", "PAS-24531"})
	public void pas21648_CARCOFormAAIFNJ3OutsidePASAddReplaceVehicleTC01(@Optional("NJ") String state) {
		carcoAddReplaceVehicleOutsidePAS(true, false, REQUIRED_TO_BIND_AAIFNJ3, REQUIRED_TO_BIND_AAIFNJ4);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.NJ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-21648", "PAS-24531", "PAS-24605"})
	public void pas21648_CARCOFormAAIFNJ3OutsidePASAddReplaceVehicleTC02(@Optional("NJ") String state) {
		carcoAddReplaceVehicleOutsidePAS(false, false, REQUIRED_TO_BIND_AAIFNJ3, REQUIRED_TO_BIND_AAIFNJ4);
	}

	/**
	 * @author Maris Strazds
	 * @name
	 * @scenario
	 * 1. Create policy in PAS
	 * 2. Create endorsement outside PAS
	 * 3. Add/replace qualifying vehicle outside PAS ('Less Than 1,000 miles' = Yes)
	 * 4. Run RFI service - AAIFNJ4 is returned
	 * 5. Try to bind without signed AAIFNJ4 - error is returned
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.NJ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-24531", "PAS-24605"})
	public void pas24531_CARCOFormAAIFNJ4OutsidePASAddReplaceVehicle(@Optional("NJ") String state) {
		carcoAddReplaceVehicleOutsidePAS(true, true, REQUIRED_TO_BIND_AAIFNJ3, REQUIRED_TO_BIND_AAIFNJ4);
	}

	/**
	 * @author Maris Strazds
	 * @name
	 * @scenario
	 * 1. Create policy in PAS
	 * 2. Create endorsement outside PAS
	 * 3. Add/replace qualifying vehicle outside PAS ('Less Than 1,000 miles' = No or Not required)
	 * 4. Run RFI service - AAIFNYD is returned
	 * 5. Try to bind without signed AAIFNYD - error is returned
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.NY})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-25029"})
	public void pas25029_CARCOFormAAIFNYDOutsidePASAddReplaceVehicleTC01(@Optional("NY") String state) {
		carcoAddReplaceVehicleOutsidePAS(true, false, REQUIRED_TO_BIND_AAIFNYD, REQUIRED_TO_BIND_AAIFNYE);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.NY})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-25029"})
	public void pas25029_CARCOFormAAIFNYDOutsidePASAddReplaceVehicleTC02(@Optional("NY") String state) {
		carcoAddReplaceVehicleOutsidePAS(false, false, REQUIRED_TO_BIND_AAIFNYD, REQUIRED_TO_BIND_AAIFNYE);
	}

	/**
	 * @author Maris Strazds
	 * @name
	 * @scenario
	 * 1. Create policy in PAS
	 * 2. Create endorsement outside PAS
	 * 3. Add/replace qualifying vehicle outside PAS ('Less Than 1,000 miles' = Yes)
	 * 4. Run RFI service - AAIFNYE is returned
	 * 5. Try to bind without signed AAIFNJ4 - error is returned
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.NY})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-25028"})
	public void pas25028_CARCOFormAAIFNYEOutsidePASAddReplaceVehicle(@Optional("NY") String state) {
		carcoAddReplaceVehicleOutsidePAS(true, true, REQUIRED_TO_BIND_AAIFNYD, REQUIRED_TO_BIND_AAIFNYE);
	}

	/**
	 * @author Maris Strazds
	 * @name
	 * @scenario
	 * 1. Create policy in PAS with qualifying vehicle ('Less Than 1,000 miles' = No or not required), but without COMPDED
	 * 2. Create endorsement outside PAS
	 * 3. Update vehicle to have COMPDED
	 * 4. Run RFI service - AAIFNJ3 is returned
	 * 5. Try to bind without signed AAIFNJ3 - error is returned
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.NJ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-21648", "PAS-24605"})
	public void pas21648_CARCOFormAAIFNJ3OutsidePASUpdateCompTC01(@Optional("NJ") String state) {
		carcoFormOutsidePASUpdateComp(true, false, REQUIRED_TO_BIND_AAIFNJ3, REQUIRED_TO_BIND_AAIFNJ4);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.NJ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-21648", "PAS-24605"})
	public void pas21648_CARCOFormAAIFNJ3OutsidePASUpdateCompTC02(@Optional("NJ") String state) {
		carcoFormOutsidePASUpdateComp(false, false, REQUIRED_TO_BIND_AAIFNJ3, REQUIRED_TO_BIND_AAIFNJ4);
	}

	/**
	 * @author Maris Strazds
	 * @name
	 * @scenario
	 * 1. Create policy in PAS with qualifying vehicle ('Less Than 1,000 miles' = Yes), but without COMPDED
	 * 2. Create endorsement outside PAS
	 * 3. Update vehicle to have COMPDED
	 * 4. Run RFI service - AAIFNJ4 is returned
	 * 5. Try to bind without signed AAIFNJ4 - error is returned
	 */

	@Parameters({"state"})
	@StateList(states = {Constants.States.NJ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-24588", "PAS-24605"})
	public void pas24588_CARCOFormAAIFNJ4OutsidePASUpdateComp(@Optional("NJ") String state) {
		carcoFormOutsidePASUpdateComp(true, true, REQUIRED_TO_BIND_AAIFNJ3, REQUIRED_TO_BIND_AAIFNJ4);
	}

	/**
	 * @author Maris Strazds
	 * @name
	 * @scenario
	 * 1. Create policy in PAS with qualifying vehicle ('Less Than 1,000 miles' = No or not required), but without COMPDED
	 * 2. Create endorsement outside PAS
	 * 3. Update vehicle to have COMPDED
	 * 4. Run RFI service - AAIFNYD is returned
	 * 5. Try to bind without signed AAIFNYD - error is returned
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.NY})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-25029"})
	public void pas25029_CARCOFormAAIFNYDOutsidePASUpdateCompTC01(@Optional("NY") String state) {
		carcoFormOutsidePASUpdateComp(true, false, REQUIRED_TO_BIND_AAIFNYD, REQUIRED_TO_BIND_AAIFNYE);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.NY})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-25029"})
	public void pas25029_CARCOFormAAIFNYDOutsidePASUpdateCompTC02(@Optional("NY") String state) {
		carcoFormOutsidePASUpdateComp(false, false, REQUIRED_TO_BIND_AAIFNYD, REQUIRED_TO_BIND_AAIFNYE);
	}

	/**
	 * @author Maris Strazds
	 * @name
	 * @scenario
	 * 1. Create policy in PAS with qualifying vehicle ('Less Than 1,000 miles' = Yes), but without COMPDED
	 * 2. Create endorsement outside PAS
	 * 3. Update vehicle to have COMPDED
	 * 4. Run RFI service - AAIFNYE is returned
	 * 5. Try to bind without signed AAIFNYE - error is returned
	 */

	@Parameters({"state"})
	@StateList(states = {Constants.States.NY})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-25028"})
	public void pas25028_CARCOFormAAIFNYEOutsidePASUpdateComp(@Optional("NY") String state) {
		carcoFormOutsidePASUpdateComp(true, true, REQUIRED_TO_BIND_AAIFNYD, REQUIRED_TO_BIND_AAIFNYE);
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

			checkIfRfiIsEmpty(policyNumber);

			HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest("UMPD", "40000"), PolicyCoverageInfo.class);

			String doccId = checkDocumentInRfiService(policyNumber, "RUUELLUU", "IMPORTANT NOTICE - Uninsured Motorist Coverage");

			helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorEnum.Errors.ERROR_200037_VA.getCode(), ErrorEnum.Errors.ERROR_200037_VA.getMessage());

			HelperCommon.endorsementBind(policyNumber, "Megha Gubbala", Response.Status.OK.getStatusCode(), doccId);

			//Verify Signed by is there in XML
			String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "AA52VA", "ENDORSEMENT_ISSUE");
			verifyDocInDb(softly, query, DocGenEnum.Documents.AA52VA, true);

			goToPasAndVerifyRuleAndSignedBy(softly, policyNumber, AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.IMPORTANT_NOTICE_UNINSURED_MOTORIST_COVERAGE,
					AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY, "$50,000/$100,000", ERROR_200037_VA, false);

			//Verify Signed by is not there in XML
			String query1 = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "AA52VA", "ENDORSEMENT_ISSUE");
			softly.assertThat(DocGenHelper.getDocument(DocGenEnum.Documents.AA52VA, query1).toString().contains("DocSignedBy")).isFalse();
			softly.assertThat(DocGenHelper.getDocument(DocGenEnum.Documents.AA52VA, query1).toString().contains("DocSignedDate")).isFalse();
		});
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
			checkIfRfiIsEmpty(policyNumber);
			helperMiniServices.endorsementRateAndBind(policyNumber);
		});
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Update Tort, Sign AADNPAB - Internal/External Endorsement to PAS
	 * @scenario 1. Create policy. Where aadnpab = Signed In
	 * and Tort = "Limited Tort".
	 * 2. Create endorsement inside in PAS.
	 * 3. Update Tort = Full Tort.
	 * 4. Go to Document and Bind page, check if document = Not signed in
	 * 5. Try bind, check the error.
	 * 6. Delete endorsement.
	 * 7. Create new endorsement outside of PAS. Check RFI response.
	 * 8. Update coverage to Full, rate.
	 * 9. Hit RFI service, check the response.
	 * 10. Try bind, check the error.
	 * 11. Sign in and bind again.
	 * 12. Repeat the same steps from 7-11. But this time TORT = "Limit Tort"
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.PA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-23303", "PAS-24559"})
	public void pas23303_UpdateTortSignInAADNPAB(@Optional("PA") String state) {
		assertSoftly(softly -> {
			String policyNumber = createPolicyForAnyDocument("Limited Tort", "Physically Signed", tortCoverage, aadnpabRule);
			policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus5Day"));
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			tortCoverage.setValue("Full Tort");
			premiumAndCoveragesTab.calculatePremium();
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
			softly.assertThat(documentsAndBindTab.getRequiredToBindAssetList()
					.getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.PA_NOTICE_NAMED_INSURED_REGARDING_TORT_OPTIONS)).hasValue("Not Signed");
			documentsAndBindTab.submitTab();
			//On bind check error message
			errorTab.verify.errorsPresent(true, ERROR_AAA_SS190125);
			errorTab.cancel();

			//delete endorsement
			documentsAndBindTab.cancel(false);
			Page.dialogConfirmation.buttonDeleteEndorsement.click();

			//Create endorsement outside of PAS
			dxpOnlyCreateEndorsementCheckDocumentAADNPAB("TRUE", policyNumber);

			//create one more endorsement
			dxpOnlyCreateEndorsementCheckDocumentAADNPAB("FALSE", policyNumber);
		});
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Re-trigger the document - ignore the overridden rule in PAS
	 * @scenario1 1. Create quote. Override the rule. Issue.
	 * 2. Create endorsement inside in PAS.
	 * 3. Make change, that triggers a document that needs to be signed.
	 * 4. Navigate to the Document and Bind page. Check the status.
	 * 5. Delete endorsement, create new one but outside of PAS.
	 * 6. Hit RFI service, check the status.
	 * 7. Make changes, that triggers a document that needs to be signed.
	 * 8. Rate and hit RFI service, check the status.
	 * @scenario2 1. Create policy.
	 * 2. Create endorsement and make change, that triggers a document that needs to be signed.
	 * 3. Override the rule and bind endorsement. Create new endorsement.
	 * 4. Make changes that triggers the rule again.
	 * 5. Go to the Document and Bind tab. Document = not signed.
	 * 6. Delete endorsement. Create new one, but outside of PAS.
	 * 7. Check RFI service.
	 * 8. Make changes that triggers the rule again.
	 * 9. Rate and hit RFI service.
	 * 10. Sign in and Bind.
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.VA, Constants.States.DE, Constants.States.DC})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-23334"})
	public void pas23334_RetriggerDocumentIgnoreTheOverriddenRule(@Optional("DC") String state) {

		if (state.contains("VA")) {
			//TC1
			String policyNumber = createPolicyForAnyDocument("$25,000/$50,000", "Not Signed", uimbiCoverage, ruuelluuRule);
			createEndorsementInPasUpdateCoverage("$50,000/$100,000", uimbiCoverage);
			assertThat(ruuelluuRule).hasValue("Not Signed");
			deleteEndorsementInPas();
			dxpOnlyCreateEndorsementCheckDocument("UMBI", "50000/100000",
					"IMPORTANT NOTICE - Uninsured Motorist Coverage", "RUUELLUU", policyNumber);
			//TC2
			String policyNumber2 = openAppAndCreatePolicy();
			createEndorsementInPasUpdateCoverage("$25,000/$50,000", uimbiCoverage);
			assertThat(ruuelluuRule).hasValue("Not Signed");
			ruuelluuRule.setValue("Physically Signed");
			documentsAndBindTab.submitTab();
			createEndorsementInPasUpdateCoverage("$50,000/$100,000", uimbiCoverage);
			assertThat(ruuelluuRule).hasValue("Not Signed");
			deleteEndorsementInPas();
			dxpOnlyCreateEndorsementCheckDocument("UMBI", "50000/100000",
					"IMPORTANT NOTICE - Uninsured Motorist Coverage", "RUUELLUU", policyNumber2);

		} else if (state.contains("DE")) {
			//TC1
			String policyNumber = createPolicyForAnyDocument("$25,000/$50,000", "Not Signed", uimbiCoverage, aadnde1Rule);
			createEndorsementInPasUpdateCoverage("$50,000/$100,000", uimbiCoverage);
			assertThat(aadnde1Rule).hasValue("Not Signed");
			deleteEndorsementInPas();
			dxpOnlyCreateEndorsementCheckDocument("UMBI", "50000/100000",
					"Delaware Motorists Protection Act", "AADNDE1", policyNumber);
			//TC2
			String policyNumber2 = openAppAndCreatePolicy();
			createEndorsementInPasUpdateCoverage("$25,000/$50,000", uimbiCoverage);
			assertThat(aadnde1Rule).hasValue("Not Signed");
			aadnde1Rule.setValue("Physically Signed");
			documentsAndBindTab.submitTab();
			createEndorsementInPasUpdateCoverage("$50,000/$100,000", uimbiCoverage);
			assertThat(aadnde1Rule).hasValue("Not Signed");
			deleteEndorsementInPas();
			dxpOnlyCreateEndorsementCheckDocument("UMBI", "50000/100000",
					"Delaware Motorists Protection Act", "AADNDE1", policyNumber2);

		} else if (state.contains("DC")) {
			//TC1
			String policyNumber = createPolicyForAnyDocument("$25,000/$50,000", "Not Signed", biCoverage, aacsdcRule);
			createEndorsementInPasUpdateCoverage("$50,000/$100,000", biCoverage);
			assertThat(aacsdcRule).hasValue("Not Signed");
			deleteEndorsementInPas();
			dxpOnlyCreateEndorsementCheckDocument("BI", "50000/100000",
					"District of Columbia Coverage Selection/Rejection Form", "AACSDC", policyNumber);
			//TC2
			String policyNumber2 = openAppAndCreatePolicy();
			createEndorsementInPasUpdateCoverage("$50,000/$100,000", biCoverage);
			assertThat(aacsdcRule).hasValue("Not Signed");
			aacsdcRule.setValue("Physically Signed");
			documentsAndBindTab.submitTab();
			createEndorsementInPasUpdateCoverage("$250,000/$500,000", biCoverage);
			assertThat(aacsdcRule).hasValue("Not Signed");
			deleteEndorsementInPas();
			dxpOnlyCreateEndorsementCheckDocument("BI", "250000/500000",
					"District of Columbia Coverage Selection/Rejection Form", "AACSDC", policyNumber2);
		}
	}

	private void deleteEndorsementInPas() {
		documentsAndBindTab.cancel(false);
		Page.dialogConfirmation.buttonDeleteEndorsement.click();
	}

	private void createEndorsementInPasUpdateCoverage(String coverageValue, ComboBox coverage) {
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		coverage.setValue("contains=" + coverageValue);
		premiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
	}

	private void dxpOnlyCreateEndorsementCheckDocument(String coverageId, String coverageValue, String documentName, String documentCode, String policyNumber) {
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		checkIfRfiIsEmpty(policyNumber);
		HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageId, coverageValue), PolicyCoverageInfo.class);
		String docId = checkDocumentInRfiService(policyNumber, documentCode, documentName);
		HelperCommon.endorsementBind(policyNumber, "Jovita Pukenaite", Response.Status.OK.getStatusCode(), docId);
	}

	private void dxpOnlyCreateEndorsementCheckDocumentAADNPAB(String tortCoverageValue, String policyNumber) {
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		checkIfRfiIsEmpty(policyNumber);
		HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest("TORT", tortCoverageValue), PolicyCoverageInfo.class);
		String docId = checkDocumentInRfiService(policyNumber, DocGenEnum.Documents.AADNPAB.getId(), DocGenEnum.Documents.AADNPAB.getName());

		helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorEnum.Errors.ERROR_AAA_SS190125.getCode(), ErrorEnum.Errors.ERROR_AAA_SS190125.getMessage());
		HelperCommon.endorsementBind(policyNumber, "Megha Gubbala", Response.Status.OK.getStatusCode(), docId);

		assertSoftly(softly -> {
			String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, DocGenEnum.Documents.AADNPAB.getIdInXml(), AaaDocGenEntityQueries.EventNames.ENDORSEMENT_ISSUE);
			verifyDocInDb(softly, query, DocGenEnum.Documents.AADNPAB, true);
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

	private String createPolicyForAnyDocument(String limit, String signType, ComboBox coverage, RadioGroup rule) {
		mainApp().open();
		createCustomerIndividual();
		createQuote();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		coverage.setValue("contains=" + limit);
		premiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		rule.setValue(signType);
		documentsAndBindTab.saveAndExit();

		return testEValueDiscount.simplifiedQuoteIssue();
	}

	private void checkIfRfiIsEmpty(String policyNumber) {
		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		RFIDocuments rfiServiceResponse = HelperCommon.rfiViewService(policyNumber, false);
		assertSoftly(softly -> {
			softly.assertThat(rfiServiceResponse.url).isNull();
			softly.assertThat(rfiServiceResponse.documents.isEmpty()).isTrue();
		});
	}

	private String checkDocumentInRfiService(String policyNumber, String documentCode, String documentName) {
		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		RFIDocuments rfiServiceResponse = HelperCommon.rfiViewService(policyNumber, false);
		RFIDocument rfiDocument = rfiServiceResponse.documents.stream().filter(document -> document.documentCode.equals(documentCode)).findFirst().orElse(null);
		assertSoftly(softly -> {

			softly.assertThat(rfiServiceResponse.url).isNull();
			softly.assertThat(rfiDocument.documentCode).isEqualTo(documentCode);
			softly.assertThat(rfiDocument.documentName).isEqualTo(documentName);
			softly.assertThat(rfiDocument.documentId).startsWith(documentCode);
			softly.assertThat(rfiDocument.status).startsWith("NS");
			softly.assertThat(rfiDocument.parent).isEqualTo("policy");
			softly.assertThat(rfiDocument.parentOid).isNotEmpty();

			RFIDocuments rfiServiceResponse2 = HelperCommon.rfiViewService(policyNumber, true);
			softly.assertThat(rfiServiceResponse2.url).endsWith(".pdf");
			softly.assertThat(rfiServiceResponse2.documents).isNotEmpty();

			//Verify that URL works
			HttpURLConnection con = null;
			try {
				URL url = new URL(rfiServiceResponse2.url);
				con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("GET");
				softly.assertThat(con.getResponseCode()).isEqualTo(Response.Status.OK.getStatusCode());
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (con != null) {
					con.disconnect();
				}
			}

		});
		return rfiDocument.documentId;
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
	public void pas349_rfiAuto(@Optional("PA") String state) {
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
			if ("PA".contains(state)) {
				documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.PENNSYLVANIA_NOTICE_TO_NAMED_INSURED_REGARDING_TORT_OPTIONS)
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

	/**
	 * @author Chaitanya Boyapati
	 * @name RFI AACSNJ Form
	 * @scenario 1. Create policy.
	 * 2. Create endorsement outside of PAS.
	 * 3. Rate. Hit RFI service.
	 * 4. Check the response.
	 * 5. Update Primary Care Selection/PIPMedical coverage. Rate.
	 * 6. Hit RFI service, check if document is displaying.
	 * 7. Run bind service without signing document and verify error. and policy is not bound.
	 * 8. Run bind service with document id verify no error and we can bind the policy.
	 * 9. go to pas UI and verify if policy is bound
	 * 10 Go to document and bind page and verify if document is electronically signed.
	 * 11. go to the DB and verify document signed by is there in xml
	 * 12 .create and endorsement on policy from pas change  primary care selection and rate the policy
	 * 13. go to document and bind page verify if its reset to document not signed
	 * 14 Try to bind policy from pas and verify error.
	 * 14 Select document physically signed
	 * 15 Verify in db that we are not sending document signed by
	 * 16  Bind the policy verify there is no error message.
	 */

	@Parameters({"state"})
	@StateList(states = {Constants.States.NJ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-21646"})
	public void pas21646_aacsnjFormRFI(@Optional("NJ") String state) {
		assertSoftly(softly -> {
			DocGenEnum.Documents document = DocGenEnum.Documents.AACSNJ;
			AssetDescriptor<RadioGroup> documentAsset = AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.NJ_AUTO_STANDARD_POLICY_COVERAGE_SELECTION_FORM;
			ErrorEnum.Errors error = ERROR_200900;
			TestData td = getPolicyDefaultTD();

			verifyRFIScenarios("PIPMEDEXP", AutoSSMetaData.PremiumAndCoveragesTab.PolicyLevelPersonalInjuryProtectionCoverages.MEDICAL_EXPENSE, CoverageLimits.COV_75000.getLimit(), CoverageLimits.COV_150000.getDisplay(), document, documentAsset, error, td, true, false);
			verifyRFIScenarios("PIPPRIMINS", AutoSSMetaData.PremiumAndCoveragesTab.PolicyLevelPersonalInjuryProtectionCoverages.PRIMARY_INSURER, CoverageLimits.COV_PIPPRIMINS_PERSONAL_HEALTH_INSURANCE.getLimit(), CoverageLimits.COV_PIPPRIMINS_AUTO_INSURANCE.getDisplay(), document, documentAsset, error, td, true, false);
		});
	}

	/**
	 * @name RFI AA52NY Form and BI update in DXP
	 * @scenario
	 * 1. Create policy.
	 * 2. Create endorsement outside of PAS.
	 * 4. Update BI coverage to 50/100. Rate.
	 * 5. Hit RFI service, check if document is not in the list.
	 * 6. Update BI coverage to 25/50. Rate.
	 * 7. Hit RFI service, check if document is displaying.
	 * 8. Run bind service without signing document and verify error. and policy is not bound.
	 * 9. Run bind service with document id verify no error and we can bind the policy.
	 * 10. go to pas inquiry and verify if policy is bound
	 * 11. Go to document and bind page and verify if document is electronically signed.
	 * 12. Check DB that document was generated and DocSignedBy and DocSignedDate fields are present
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.NY})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-24562"})
	public void pas24562_AA52NY_BIUpdateInDXP(@Optional("NY") String state) {
		DocGenEnum.Documents document = DocGenEnum.Documents.AA52NY;
		AssetDescriptor<RadioGroup> documentAsset = AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.SUPPLEMENTARY_UNINSURED_MOTORISTS_COVERAGE_REJECTION;
		ErrorEnum.Errors error = ERROR_200115_NY;
		// scenario 1
		TestData td = getPolicyDefaultTD();
		//Create Policy
		String policyNumber = openAppAndCreatePolicy(td);
		//Verify BI update in DXP
		assertSoftly(softly -> {
			//validate there is not document in new business
			verifyRFINoDocumentInInquiry(softly, policyNumber, documentAsset);
			//Create endorsement
			helperMiniServices.createEndorsementWithCheck(policyNumber);
			//update BI to 50/100
			HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(CoverageInfo.BI.getCode(), CoverageLimits.COV_50100.getLimit()), PolicyCoverageInfo.class);
			helperMiniServices.rateEndorsementWithCheck(policyNumber);
			//no document should be produced
			verifyRFINoDocumentInDXP(softly, policyNumber, document.getId());
			//update BI to 25/50
			HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(CoverageInfo.BI.getCode(), CoverageLimits.COV_2550.getLimit()), PolicyCoverageInfo.class);
			helperMiniServices.rateEndorsementWithCheck(policyNumber);
			//Check RFI document exists
			String docId = checkDocumentInRfiService(policyNumber, document.getId(), document.getName());
			bindEndorsement(policyNumber, docId, error.getCode(), error.getMessage(), false);
			verifyDocInDb(softly, policyNumber, AaaDocGenEntityQueries.EventNames.ENDORSEMENT_ISSUE, document, true);
			verifyRFIDocumentElectronicallySingedInInquiry(softly, policyNumber, documentAsset);
		});
	}

	/**
	 * @name RFI AA52NY Form and BI update in PAS
	 * @scenario
	 * 1. Create policy.
	 * 2. Create endorsement in PAS.
	 * 3. Update BI coverage to 50/100. Rate.
	 * 4. Verify Documents & Bind page. Document is not displayed on UI
	 * 5. Update BI coverage to 25/50. Rate.
	 * 6. Verify Documents & Bind page. Document is not displayed on UI
	 * 7. Bind without signing document and verify error. and policy is not bound.
	 * 8. Physically sign document and bind to verify no error and we can bind the policy.
	 * 9. Check DB that document was generated
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.NY})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-24562"})
	public void pas24562_AA52NY_BIUpdateInPAS(@Optional("NY") String state) {
		DocGenEnum.Documents document = DocGenEnum.Documents.AA52NY;
		AssetDescriptor<RadioGroup> documentAsset = AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.SUPPLEMENTARY_UNINSURED_MOTORISTS_COVERAGE_REJECTION;
		ErrorEnum.Errors error = ERROR_200115_NY;
		AssetDescriptor<ComboBox> coverageAsset = AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY;
		TestData td = getPolicyDefaultTD();
		//Create Policy
		String policyNumber = openAppAndCreatePolicy(td);
		assertSoftly(softly -> {
			verifyRFINoDocumentInInquiry(softly, policyNumber, documentAsset);
			//Create endorsement
			policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
			//Got to P&C and change BI to 50/100
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			premiumAndCoveragesTab.setPolicyCoverageDetailsValue(coverageAsset.getLabel(), CoverageLimits.COV_50100.getDisplay());
			premiumAndCoveragesTab.calculatePremium();
			//Go to D&B check that document is not displayed on UI
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
			softly.assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(documentAsset)).isPresent(false);
			//Got to P&C and change BI to 25/50
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			premiumAndCoveragesTab.setPolicyCoverageDetailsValue(coverageAsset.getLabel(), CoverageLimits.COV_2550.getDisplay());
			premiumAndCoveragesTab.calculatePremium();
			//Go to D&B check that document is displayed and selected as Not Signed
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
			softly.assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(documentAsset).getValue()).isEqualTo("Not Signed");
			documentsAndBindTab.submitTab();
			//On bind verify error message
			errorTab.verify.errorsPresent(true, error);
			errorTab.cancel();
			//Physically sign the document and bind policy
			documentsAndBindTab.getRequiredToBindAssetList().getAsset(documentAsset).setValue("Physically Signed");
			documentsAndBindTab.submitTab();
			String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, document.getIdInXml(), AaaDocGenEntityQueries.EventNames.ENDORSEMENT_ISSUE);
			validateDocSignTagsNotExist(document, query); //Document doesn't contain DocSignTags if signed in PAS
		});
	}

	/**
	 * @name RFI AA52NY Form and UM/SUM update in PAS and DXP
	 * @scenario 1
	 * 1. Create policy.
	 * 2. Create endorsement outside of PAS.
	 * 3. Update UM/SUM coverage to 50/100. Rate.
	 * 4. Hit RFI service, check that document is returned
	 * 5. Run bind service without signing document and verify error and policy is not bound.
	 * 6. Run bind service with document id verify no error and we can bind the policy.
	 * 7. Check DB that document was generated and DocSignedBy and DocSignedDate fields are present.
	 * 8. Go to pas inquiry and verify if policy is bound
	 * 9. Go to document and bind page and verify if document is electronically signed.
	 * 10. Create another endorsement in PAS.
	 * 11. Update UM/SUM coverage to 25/50. Rate.
	 * 12. Go to D&B page and check document is reset to Not Signed
	 * 13. Bind without signing document and verify error. and policy is not bound.
	 * 14. Physically sign document and bind to verify no error and we can bind the policy.
	 * 15. Check DB that document was generated
	 *
	 * @scenario 2
	 * 1. Create policy with BI 500/500 and UM/SUM 100/300
	 * 2. Override document rule in PAS.
	 * 3. Run scenario 1 again, this time do not expect that rule will
	 * be triggered if document is left as Not Signed
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.NY})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-24562"})
	public void pas24562_AA52NY_UMSU(@Optional("NY") String state) {
		assertSoftly(softly -> {
			DocGenEnum.Documents document = DocGenEnum.Documents.AA52NY;
			AssetDescriptor<RadioGroup> documentAsset = AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.SUPPLEMENTARY_UNINSURED_MOTORISTS_COVERAGE_REJECTION;
			ErrorEnum.Errors error = ERROR_200115_NY;
			TestData td = getPolicyDefaultTD();

			verifyRFIScenarios(CoverageInfo.UM_SUM_NY.getCode(), AutoSSMetaData.PremiumAndCoveragesTab.SUPPLEMENTARY_UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY,
					CoverageLimits.COV_50100.getLimit(), CoverageLimits.COV_2550.getDisplay(), document, documentAsset, error, td, true, false);

			//Create policy and override rule
			td.adjust(TestData.makeKeyPath(documentsAndBindTab.getMetaKey(), AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND.getLabel(),
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.SUPPLEMENTARY_UNINSURED_MOTORISTS_COVERAGE_REJECTION.getLabel()), "Not Signed");
			td.adjust(TestData.makeKeyPath(premiumAndCoveragesTab.getMetaKey(),
					AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY.getLabel()), "contains=500,000/$500,000");
			td.adjust(TestData.makeKeyPath(premiumAndCoveragesTab.getMetaKey(),
					AutoSSMetaData.PremiumAndCoveragesTab.SUPPLEMENTARY_UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY.getLabel()), "contains=$100,000/$300,000");
			TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");
			td = td.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError).resolveLinks();

			verifyRFIScenarios(CoverageInfo.UM_SUM_NY.getCode(), AutoSSMetaData.PremiumAndCoveragesTab.SUPPLEMENTARY_UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY,
					CoverageLimits.COV_50100.getLimit(), CoverageLimits.COV_2550.getDisplay(), document, documentAsset, error, td, true, true);
		});
	}

	private void verifyRFINoDocumentInDXP(ETCSCoreSoftAssertions softly, String policyNumber, String documentCode) {
		RFIDocuments rfiServiceResponse = HelperCommon.rfiViewService(policyNumber, false);
		softly.assertThat(rfiServiceResponse.documents.stream()
				.noneMatch(doc -> documentCode.equals(doc.documentCode))).isTrue();
	}

	private void verifyRFINoDocumentInInquiry(ETCSCoreSoftAssertions softly, String policyNumber, AssetDescriptor<RadioGroup> documentAsset) {
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.policyInquiry().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		softly.assertThat(documentsAndBindTab.getInquiryAssetList().getInquiryAssetList(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND)
				.getStaticElement(documentAsset)).isPresent(false);
		documentsAndBindTab.cancel(false);
	}

	private void verifyRFIDocumentElectronicallySingedInInquiry(ETCSCoreSoftAssertions softly, String policyNumber, AssetDescriptor<RadioGroup> documentAsset) {
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.policyInquiry().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		softly.assertThat(documentsAndBindTab.getInquiryAssetList().getInquiryAssetList(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND)
				.getStaticElement(documentAsset).getValue()).isEqualTo("Electronically Signed");
		documentsAndBindTab.cancel(false);
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

	private void bindEndorsement(String policyNumber, String doccId, String errorCode, String errorMessage, boolean isRuleOverridden) {
		if (!isRuleOverridden) {
			//Check that rule is fired when rule is not overridden. Not checking if rule is fired without signing, as per Digital flow it must always be signed.
			helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, errorCode, errorMessage);
		}
		//Bind policy with docId for successful bind and document is electronically signed. Not checking if rule is fired without signing, as per Digital flow it must always be signed.
		HelperCommon.endorsementBind(policyNumber, "Megha Gubbala", Response.Status.OK.getStatusCode(), doccId);
	}

	private String policyCreationForRFI(String coverageId, String newCoverage, TestData td) {
		//Create Policy
		String policyNumber = openAppAndCreatePolicy(td);

		//Create endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		//update coverage
		if (getState().equals(Constants.States.NJ) && coverageId.equals(CoverageInfo.PIPPRIMINS_NJ.getCode())) {
			Coverage covToUpdatePIPPRIMINS = Coverage.create(CoverageInfo.PIPPRIMINS_NJ).changeLimit(CoverageLimits.COV_PIPPRIMINS_PERSONAL_HEALTH_INSURANCE).addInsurerName("John"). addCertNum("12345");
			COV_HELPER.updateCoverageWithInsNameAndCertNum(policyNumber, covToUpdatePIPPRIMINS);
		} else {
			HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageId, newCoverage), PolicyCoverageInfo.class);
		}
		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		//TODO-mstrazds: no document needs to be signed ?
		return policyNumber;
	}

	private void verifyDocInDb(ETCSCoreSoftAssertions softly, String query, DocGenEnum.Documents document, boolean isDocSignTagsExpected) {
		if (isDocSignTagsExpected) {
			Document docInXml = DocGenHelper.getDocument(document, query);
			String name = DocGenHelper.getDocumentDataElemByName("DocSignedBy", docInXml).getDataElementChoice().getTextField();
			String date = DocGenHelper.getDocumentDataElemByName("DocSignedDate", docInXml).getDataElementChoice().getDateTimeField();
			String currentDate = DateTimeUtils.getCurrentDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			softly.assertThat(name).isEqualTo("Megha Gubbala");
			softly.assertThat(date).startsWith(currentDate);
			softly.assertThat(date).endsWith("-07:00"); // validates that the document's DocSignedDate ends with an AZ timestamp
		} else {
			validateDocSignTagsNotExist(document, query);
		}
	}


	private void verifyDocInDb(ETCSCoreSoftAssertions softly, String policyNumber, AaaDocGenEntityQueries.EventNames event, DocGenEnum.Documents document, boolean isDocSignTagsExpected) {
		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, document.getIdInXml(), event);
		verifyDocInDb(softly, query, document, isDocSignTagsExpected);
	}

	private void goToPasAndVerifyRuleAndSignedBy(ETCSCoreSoftAssertions softly, String policyNumber,
			AssetDescriptor<RadioGroup> documentAsset, AssetDescriptor<? extends AbstractEditableStringElement> coverageAsset,
			String coverageLimit, ErrorEnum.Errors error, boolean isRuleOverridden) {
		//create endorsement from pas go to bind page verify document is electronically signed
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		softly.assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(documentAsset).getValue()).isEqualTo("Electronically Signed");

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		//From P&C page change coverage again to verify signed by is resetting to  not signed
		if ((getState().equals(Constants.States.NJ) && coverageAsset.equals(AutoSSMetaData.PremiumAndCoveragesTab.PolicyLevelPersonalInjuryProtectionCoverages.MEDICAL_EXPENSE)) ||
				(getState().equals(Constants.States.NJ) && coverageAsset.equals(AutoSSMetaData.PremiumAndCoveragesTab.PolicyLevelPersonalInjuryProtectionCoverages.PRIMARY_INSURER))) {
			premiumAndCoveragesTab.setPolicyPersonalInjuryProtectionCoverageDetailsValue(coverageAsset.getLabel(), coverageLimit);
		} else {
			premiumAndCoveragesTab.setPolicyCoverageDetailsValue(coverageAsset.getLabel(), coverageLimit);
		}

		if (getState().equals(Constants.States.NJ) && coverageAsset.equals(AutoSSMetaData.PremiumAndCoveragesTab.PolicyLevelPersonalInjuryProtectionCoverages.PRIMARY_INSURER) && coverageLimit.equals(CoverageLimits.COV_PIPPRIMINS_PERSONAL_HEALTH_INSURANCE.getDisplay())) {
			premiumAndCoveragesTab.setPolicyPersonalInjuryProtectionCoverageDetailsValue(AutoSSMetaData.PremiumAndCoveragesTab.PolicyLevelPersonalInjuryProtectionCoverages.INSURER_NAME.getLabel()
					, "Peter");
			premiumAndCoveragesTab.setPolicyPersonalInjuryProtectionCoverageDetailsValue(AutoSSMetaData.PremiumAndCoveragesTab.PolicyLevelPersonalInjuryProtectionCoverages.POLICY_GROUP_NUM_CERTIFICATE_NUM.getLabel()
					, "658585");
		}

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

	private void carcoNeededNotNeededInsidePASAddReplaceVehicle(boolean baseDateGreaterThanThreshold, int baseDateRange, boolean is1000MilesQuestionRequired,
																boolean lessThan1000Miles, boolean isSalesAgreementExpected, boolean isInspectionExpected,
																AssetDescriptor<RadioGroup> salesAgreementFormName, AssetDescriptor<RadioGroup> inspectionFormName,
                                                                ErrorEnum.Errors salesAgreementError, ErrorEnum.Errors inspectionError,
																String state) {
		String replaceVin;
		String addVin;
		String baseDate;
		if (is1000MilesQuestionRequired) {
			replaceVin = VIN_LESS_THAN_7_YEARS; //vehicle age must be less tha 7 years
			addVin = "TestData_VehicleTabLessThan7YearOld"; //vehicle age must be less tha 7 years
		} else {
			replaceVin = VIN_MORE_THAN_7_YEARS; //vehicle age must be more than 7 years
			addVin = "TestData_VehicleTabMoreThan7YearOld"; //vehicle age must be more than 7 years
		}

		if (baseDateGreaterThanThreshold) {
			baseDate = "$<today-" + baseDateRange + "y-1d:MM/dd/yyyy>";
		} else {
			baseDate = "$<today-" + baseDateRange + "y:MM/dd/yyyy>";
		}

		TestData td = getPolicyDefaultTD();
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel() + "[0]",
				AutoSSMetaData.GeneralTab.NamedInsuredInformation.BASE_DATE.getLabel()), baseDate);

		openAppAndCreatePolicy(td);
		//Create Endorsement and Replace Vehicle from PAS UI and validate
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus5Day"));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.LIST_OF_VEHICLE).getTable().
				getRow(1).getCell(5).controls.links.get("Replace").click();
		Page.dialogConfirmation.confirm();
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN).setValue(replaceVin);
		if (is1000MilesQuestionRequired) {
			if (lessThan1000Miles) {
				vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.LESS_THAN_1000_MILES).setValue("Yes");
			} else {
				vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.LESS_THAN_1000_MILES).setValue("No");
			}
		}

		if ("NY".equals(state)) {
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.ASSIGNMENT.get());
		}
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

		//Verify CARCO in Documents and Bind tab
        verifyCarcoDocumentsInDocAndBindTab(isSalesAgreementExpected, salesAgreementFormName, inspectionFormName,
                salesAgreementError, salesAgreementError.equals(inspectionError) ? null : inspectionError);
        verifyCarcoDocumentsInDocAndBindTab(isInspectionExpected, inspectionFormName, salesAgreementFormName,
                inspectionError, inspectionError.equals(salesAgreementError) ? null : salesAgreementError);
		documentsAndBindTab.submitTab();
		// Document does not exist for endorsements inside PAS
		verifyCARCODocsNotGenerated(AaaDocGenEntityQueries.EventNames.POLICY_ISSUE);

		//Create Endorsement and Add Vehicle from PAS UI and validate
		TestData tdAddVehicle = getPolicyDefaultTD().adjust(new VehicleTab().getMetaKey(), getTestSpecificTD(addVin)).resolveLinks();
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus5Day"));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		if (isSalesAgreementExpected) {
			assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(salesAgreementFormName)).hasValue("Physically Signed");
		} else {
			assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(salesAgreementFormName)).isPresent(false);
		}
		if (isInspectionExpected) {
			assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(inspectionFormName)).hasValue("Physically Signed");
		} else {
			assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(inspectionFormName)).isPresent(false);
		}

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		policy.getDefaultView().fillFromTo(tdAddVehicle, VehicleTab.class, VehicleTab.class, true);
		if (is1000MilesQuestionRequired) {
			if (lessThan1000Miles) {
				vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.LESS_THAN_1000_MILES).setValue("Yes");
			} else {
				vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.LESS_THAN_1000_MILES).setValue("No");
			}
		}

		if ("NY".equals(state)) {
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.ASSIGNMENT.get());
		}
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.ASSIGNMENT.get());
		premiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		//Verify CARCO in Documents and Bind tab
        verifyCarcoDocumentsInDocAndBindTab(isSalesAgreementExpected, salesAgreementFormName, inspectionFormName,
                salesAgreementError, salesAgreementError.equals(inspectionError) ? null : inspectionError);
        verifyCarcoDocumentsInDocAndBindTab(isInspectionExpected, inspectionFormName, salesAgreementFormName,
                inspectionError, inspectionError.equals(salesAgreementError) ? null : salesAgreementError);
		documentsAndBindTab.submitTab();
		// Document does not exist for endorsements inside PAS
		if (salesAgreementFormName.equals(REQUIRED_TO_BIND_AAIFNJ3) || inspectionFormName.equals(REQUIRED_TO_BIND_AAIFNJ4)) {
			verifyCARCODocsNotGenerated(AaaDocGenEntityQueries.EventNames.ENDORSEMENT_ISSUE);
		}
	}

	private void verifyCarcoDocumentsInDocAndBindTab(boolean expectDocument, AssetDescriptor<RadioGroup> documentToValidate,
                                                     AssetDescriptor<RadioGroup> documentNotExpected,
                                                     ErrorEnum.Errors errorExpected,
                                                     ErrorEnum.Errors errorNotExpected) {
	    if (expectDocument) {
            assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(documentToValidate)).isPresent(true).hasValue("Not Signed");
            assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(documentNotExpected)).isPresent(false);//just in case
            documentsAndBindTab.submitTab();
            errorTab.verify.errorsPresent(true, errorExpected);
            if (errorNotExpected != null) {
                errorTab.verify.errorsPresent(false, errorNotExpected);//just in case
            }
            errorTab.cancel();
            documentsAndBindTab.getRequiredToBindAssetList().getAsset(documentToValidate).setValue("Physically Signed");
        } else {
            assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(documentToValidate)).isPresent(false);
        }
    }

	private void carcoUpdateCompScenariosInsidePAS(int baseDateRange, boolean is1000MilesQuestionRequired, boolean isLessThan1000Miles, AssetDescriptor<RadioGroup> salesAgreementFormName, AssetDescriptor<RadioGroup> inspectionFormName, ErrorEnum.Errors salesAgreementError, ErrorEnum.Errors inspectionError) {
		String vin;
		TestData td = getPolicyDefaultTD();
		String lessThan1000MilesSelection;
		if (is1000MilesQuestionRequired) {
			vin = VIN_LESS_THAN_7_YEARS; //vehicle age must be less than 7 years

		} else {
			vin = VIN_MORE_THAN_7_YEARS; //vehicle age must be more than 7 years
		}

		if (isLessThan1000Miles) {
			lessThan1000MilesSelection = "Yes";
		} else {
			lessThan1000MilesSelection = "No";
		}

		if (is1000MilesQuestionRequired) {
			td.adjust(TestData.makeKeyPath(AutoSSMetaData.VehicleTab.class.getSimpleName(), AutoSSMetaData.VehicleTab.LESS_THAN_1000_MILES.getLabel()), lessThan1000MilesSelection);
		}
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel() + "[0]",
				AutoSSMetaData.GeneralTab.NamedInsuredInformation.BASE_DATE.getLabel()), "$<today-" + baseDateRange + "y:MM/dd/yyyy>");
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.VehicleTab.class.getSimpleName(), AutoSSMetaData.VehicleTab.VIN.getLabel()), vin);
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.COMPREGENSIVE_DEDUCTIBLE.getLabel()), "contains=No Coverage");

		mainApp().open();
		createCustomerIndividual();
		createQuoteAndFillUpTo(td, DocumentsAndBindTab.class, true);
		verifyCarcoDocumentsInDocAndBindTab(false, inspectionFormName, salesAgreementFormName, inspectionError, inspectionError.equals(salesAgreementError) ? null : salesAgreementError);
		verifyCarcoDocumentsInDocAndBindTab(false, salesAgreementFormName, inspectionFormName, salesAgreementError, salesAgreementError.equals(inspectionError) ? null : inspectionError);

		documentsAndBindTab.submitTab();
		policy.getDefaultView().fillFromTo(td, PurchaseTab.class, PurchaseTab.class, true);
		new PurchaseTab().submitTab();
		// Document does not exist for endorsements inside PAS
		verifyCARCODocsNotGenerated(AaaDocGenEntityQueries.EventNames.POLICY_ISSUE);

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus5Day"));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		if (isLessThan1000Miles) {
			assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(salesAgreementFormName)).isPresent(false);
		} else {
			assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(inspectionFormName)).isPresent(false);
		}

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.COMPREGENSIVE_DEDUCTIBLE.getLabel(), "$100");
		premiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

		if (is1000MilesQuestionRequired) {
			if (isLessThan1000Miles) {
				verifyCarcoDocumentsInDocAndBindTab(true, inspectionFormName, salesAgreementFormName, inspectionError, inspectionError.equals(salesAgreementError) ? null : salesAgreementError);
				verifyCarcoDocumentsInDocAndBindTab(false, salesAgreementFormName, inspectionFormName, salesAgreementError, salesAgreementError.equals(inspectionError) ? null : inspectionError);

			} else {
				verifyCarcoDocumentsInDocAndBindTab(false, inspectionFormName, salesAgreementFormName, inspectionError, inspectionError.equals(salesAgreementError) ? null : salesAgreementError);
				verifyCarcoDocumentsInDocAndBindTab(true, salesAgreementFormName, inspectionFormName, salesAgreementError, salesAgreementError.equals(inspectionError) ? null : inspectionError);
			}

		} else {
			verifyCarcoDocumentsInDocAndBindTab(false, inspectionFormName, salesAgreementFormName, inspectionError, inspectionError.equals(salesAgreementError) ? null : salesAgreementError);
			verifyCarcoDocumentsInDocAndBindTab(false, salesAgreementFormName, inspectionFormName, salesAgreementError, salesAgreementError.equals(inspectionError) ? null : inspectionError);
		}

		documentsAndBindTab.submitTab();
		// Document does not exist for endorsements inside PAS
		verifyCARCODocsNotGenerated(AaaDocGenEntityQueries.EventNames.ENDORSEMENT_ISSUE);
	}

	private void verifyCARCODocsNotGenerated(AaaDocGenEntityQueries.EventNames eventName, DocGenEnum.Documents... documents) {
		DocGenHelper.checkDocumentsDoesNotExistInXml(PolicySummaryPage.getPolicyNumber(), eventName, DocGenEnum.Documents.AAIFNJ3);
		DocGenHelper.checkDocumentsDoesNotExistInXml(PolicySummaryPage.getPolicyNumber(), eventName, DocGenEnum.Documents.AAIFNJ4);
		DocGenHelper.checkDocumentsDoesNotExistInXml(PolicySummaryPage.getPolicyNumber(), eventName, DocGenEnum.Documents.AAIFNYD);
		DocGenHelper.checkDocumentsDoesNotExistInXml(PolicySummaryPage.getPolicyNumber(), eventName, DocGenEnum.Documents.AAIFNYE);
	}

	private void carcoAddReplaceVehicleOutsidePAS(boolean isQualifyingVehicle, boolean isLessThan1000Miles, AssetDescriptor<RadioGroup> salesAgreementFormName, AssetDescriptor<RadioGroup> inspectionFormName) {
		String policyNumber = openAppAndCreatePolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		SearchPage.openPolicy(policyNumber);
		if (isQualifyingVehicle) {
			String addedVehicleOid = VEH_HELPER.helperMiniServices.addVehicleWithChecks(policyNumber, "2017-02-22", VIN_LESS_THAN_7_YEARS, true);//vehicle age must be less tha 7 years
			String docId1;
			DocGenEnum.Documents expectedDocument;
			ErrorEnum.Errors error;
			if (getState().equals(Constants.States.NJ)) {
				if (isLessThan1000Miles) {
					expectedDocument = DocGenEnum.Documents.AAIFNJ4;
					error = ERROR_200204_NJ;
					//Update isLessThan1000Miles to true/yes
					updateLessThan1000Miles(policyNumber, addedVehicleOid, true);
				} else {
					expectedDocument = DocGenEnum.Documents.AAIFNJ3;
					error = ERROR_200200_NJ;
				}
			} else {//for NY
				if (isLessThan1000Miles) {
					expectedDocument = DocGenEnum.Documents.AAIFNYE;
					//Update isLessThan1000Miles to true/yes
					updateLessThan1000Miles(policyNumber, addedVehicleOid, true);
				} else {
					expectedDocument = DocGenEnum.Documents.AAIFNYD;
					updateLessThan1000Miles(policyNumber, addedVehicleOid, false);
				}
				error = ERROR_AAA_200200_NY;
			}

			//Try to bind without signing
			docId1 = checkDocumentInRfiService(policyNumber, expectedDocument.getId(), expectedDocument.getName());
			helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, error.getCode(), error.getMessage());
			//Bind policy with docId and document is electronically signed
			HelperCommon.endorsementBind(policyNumber, "Megha Gubbala", Response.Status.OK.getStatusCode(), docId1);

			String queryExpectedDocument = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, expectedDocument.getIdInXml(), AaaDocGenEntityQueries.EventNames.ENDORSEMENT_ISSUE);
			if (!expectedDocument.equals(DocGenEnum.Documents.AAIFNYD)) {//TODO-mstrazds: Currently document not implemented for NY. Remove IF when implemented. (in next sprint)
				assertSoftly(softly -> {
					verifyDocInDb(softly, queryExpectedDocument, expectedDocument, true);
				});
			}

			//In PAS go to bind page verify document is electronically signed
			mainApp().open();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
			if (isLessThan1000Miles) {
				assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(inspectionFormName).getValue()).isEqualTo("Electronically Signed");
				assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(salesAgreementFormName)).isPresent(false);
			} else {
				assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(inspectionFormName)).isPresent(false);
				assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(salesAgreementFormName).getValue()).isEqualTo("Electronically Signed");
			}
			documentsAndBindTab.saveAndExit();

			//Replace Vehicle
			helperMiniServices.createEndorsementWithCheck(policyNumber);
			ViewVehicleResponse viewVehicles = HelperCommon.viewPolicyVehicles(policyNumber);
			String vehicleOid = viewVehicles.vehicleList.get(0).oid;
			helperMiniServices.createEndorsementWithCheck(policyNumber);
			String replacedVehicleVin = "2T1BURHE4JC034340"; //Toyota Corolla 2018
			String replacedVehicleOid = VEH_HELPER.replaceVehicleWithUpdates(policyNumber, vehicleOid, replacedVehicleVin, true, true);

			if (isLessThan1000Miles) {
				//Update isLessThan1000Miles to true/yes
				updateLessThan1000Miles(policyNumber, replacedVehicleOid, true);
			} else {
				updateLessThan1000Miles(policyNumber, replacedVehicleOid, false);
			}
			docId1 = checkDocumentInRfiService(policyNumber, expectedDocument.getId(), expectedDocument.getName());
			helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, error.getCode(), error.getMessage());

			//Bind policy with docId and document is electronically signed
			HelperCommon.endorsementBind(policyNumber, "Megha Gubbala", Response.Status.OK.getStatusCode(), docId1);
			if (!expectedDocument.equals(DocGenEnum.Documents.AAIFNYD)) {//TODO-mstrazds: Currently document not implemented for NY. Remove IF when implemented. (in next sprint)
				assertSoftly(softly -> {
					verifyDocInDb(softly, queryExpectedDocument, expectedDocument, true);
				});
			}
			//create endorsement from PAS, go to bind page, verify document is electronically signed
			mainApp().open();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
			if (isLessThan1000Miles) {
				assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(inspectionFormName).getValue()).isEqualTo("Electronically Signed");
				assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(salesAgreementFormName)).isPresent(false);
			} else {
				assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(inspectionFormName)).isPresent(false);
				assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(salesAgreementFormName).getValue()).isEqualTo("Electronically Signed");
			}
			documentsAndBindTab.saveAndExit();

		} else {
			VEH_HELPER.helperMiniServices.addVehicleWithChecks(policyNumber, "2017-02-22", VIN_MORE_THAN_7_YEARS, true);//vehicle age must be more than 7 years
			helperMiniServices.rateEndorsementWithCheck(policyNumber);
			verifyRFIHasNoDocuments(policyNumber);
			helperMiniServices.endorsementRateAndBind(policyNumber);
			verifyCARCODocsNotGenerated(AaaDocGenEntityQueries.EventNames.ENDORSEMENT_ISSUE);
		}
	}

	private void updateLessThan1000Miles(String policyNumber, String addedVehicleOid, Boolean value) {
		VehicleUpdateDto updateVehicleRequest = new VehicleUpdateDto();
		updateVehicleRequest.isLessThan1000Miles = value;
		Vehicle updateVehicleResponse = HelperCommon.updateVehicle(policyNumber, addedVehicleOid, updateVehicleRequest);
		assertThat(updateVehicleResponse.isLessThan1000Miles).isEqualTo(value);
	}

	private void carcoFormOutsidePASUpdateComp(boolean is1000MilesQuestionRequired, boolean isLessThan1000Miles, AssetDescriptor<RadioGroup> salesAgreementFormName, AssetDescriptor<RadioGroup> inspectionFormName) {
		String vin;
		String lessThan1000MilesSelection;
		if (is1000MilesQuestionRequired) {
			vin = VIN_LESS_THAN_7_YEARS; //vehicle age must be less tha 7 years
		} else {
			vin = VIN_MORE_THAN_7_YEARS; //vehicle age must be more than 7 years
		}

		if (isLessThan1000Miles) {
			lessThan1000MilesSelection = "Yes";
		} else {
			lessThan1000MilesSelection = "No";
		}

		TestData td = getPolicyDefaultTD();
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.VehicleTab.class.getSimpleName(), AutoSSMetaData.VehicleTab.VIN.getLabel()), vin); //vehicle age < 7 years
		if (is1000MilesQuestionRequired) {
			td.adjust(TestData.makeKeyPath(AutoSSMetaData.VehicleTab.class.getSimpleName(), AutoSSMetaData.VehicleTab.LESS_THAN_1000_MILES.getLabel()), lessThan1000MilesSelection);
		}

		td.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.COMPREGENSIVE_DEDUCTIBLE.getLabel()), "contains=No Coverage");

		String policyNumber = openAppAndCreatePolicy(td);
		verifyCARCODocsNotGenerated(AaaDocGenEntityQueries.EventNames.POLICY_ISSUE);
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		//Check that RFI doesn't return any document as no changes yet
		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		verifyRFIHasNoDocuments(policyNumber);

		//Update coverage
		String vehicleOid = VEH_HELPER.findVehicleByVin(HelperCommon.viewEndorsementVehicles(policyNumber), vin).oid;
		HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, vehicleOid, DXPRequestFactory.createUpdateCoverageRequest("COMPDED", "100"), PolicyCoverageInfo.class);
		String docId;
		DocGenEnum.Documents expectedDocument;
		ErrorEnum.Errors error;
		if (is1000MilesQuestionRequired) {
			if (getState().equals(Constants.States.NJ)) {
				if (isLessThan1000Miles) {
					expectedDocument = DocGenEnum.Documents.AAIFNJ4;
					error = ERROR_200204_NJ;
				} else {
					expectedDocument = DocGenEnum.Documents.AAIFNJ3;
					error = ERROR_200200_NJ;
				}
			} else {//for NY
				if (isLessThan1000Miles) {
					expectedDocument = DocGenEnum.Documents.AAIFNYE;
					error = ERROR_AAA_200200_NY;
				} else {
					expectedDocument = DocGenEnum.Documents.AAIFNYD;
					error = ERROR_AAA_200200_NY;
				}
			}
			docId = checkDocumentInRfiService(policyNumber, expectedDocument.getId(), expectedDocument.getName());
			//Try to bind without signing the document
			helperMiniServices.bindEndorsementWithErrorCheck(policyNumber,error.getCode(), error.getMessage());

			//Bind policy with docId and document is electronically signed
			HelperCommon.endorsementBind(policyNumber, "Megha Gubbala", Response.Status.OK.getStatusCode(), docId);
			String queryExpectedDocument = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, expectedDocument.getIdInXml(), AaaDocGenEntityQueries.EventNames.ENDORSEMENT_ISSUE);
			if (!expectedDocument.equals(DocGenEnum.Documents.AAIFNYD)) {//TODO-mstrazds: Currently document not implemented for NY. Remove IF when implemented. (in next sprint)
				assertSoftly(softly -> {
					verifyDocInDb(softly, queryExpectedDocument, expectedDocument, true);
				});
			}

		} else {
			HelperCommon.endorsementRate(policyNumber, 200);
			HelperCommon.endorsementBind(policyNumber, "Megha Gubbala", 200);
			verifyCARCODocsNotGenerated(AaaDocGenEntityQueries.EventNames.ENDORSEMENT_ISSUE);
		}

		//create endorsement from PAS, go to bind page, verify document is electronically signed
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		if (is1000MilesQuestionRequired) {
			if (isLessThan1000Miles) {
				assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(inspectionFormName).getValue()).isEqualTo("Electronically Signed");
			} else {
				assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(salesAgreementFormName).getValue()).isEqualTo("Electronically Signed");
			}
		} else {
			assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(inspectionFormName)).isPresent(false);
			assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(salesAgreementFormName)).isPresent(false);
		}

		documentsAndBindTab.saveAndExit();
	}

	private void verifyRFIHasNoDocuments(String policyNumber) {
		assertSoftly(softly -> {
			RFIDocuments rfiServiceResponse = HelperCommon.rfiViewService(policyNumber, false);
			softly.assertThat(rfiServiceResponse.url).isNull();
			softly.assertThat(rfiServiceResponse.documents.isEmpty()).isTrue();
		});
	}
}



