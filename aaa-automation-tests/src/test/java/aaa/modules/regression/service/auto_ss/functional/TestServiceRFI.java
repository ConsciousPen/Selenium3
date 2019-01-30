/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ss.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import aaa.common.pages.Page;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.main.enums.ErrorEnum;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.common.enums.Constants;
import aaa.helpers.rest.dtoDxp.*;
import aaa.helpers.xml.model.Document;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.service.helper.HelperMiniServices;
import aaa.utils.StateList;
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
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.sales.auto_ss.TestPolicyNano;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import aaa.modules.regression.service.helper.HelperCommon;
import aaa.modules.regression.service.helper.HelperRfi;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomSoftAssertions;
import toolkit.verification.ETCSCoreSoftAssertions;
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

	private ComboBox tortCoverage = premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.TORT_THRESHOLD);
	private ComboBox biCoverage = premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY);
	private ComboBox uimbiCoverage = premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY);

	private RadioGroup aadnpabRule = documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.PA_NOTICE_NAMED_INSURED_REGARDING_TORT_OPTIONS);
	private RadioGroup ruuelluuRule = documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.IMPORTANT_NOTICE_UNINSURED_MOTORIST_COVERAGE);
	private RadioGroup aadnde1Rule = documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.DELAWARE_MOTORISTS_PROTECTION_ACT);
	private RadioGroup aacsdcRule = documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.DISTRICT_OF_COLUMBIA_COVERAGE_SELECTION_REJECTION_FORM);

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
		String policyNumber = policyCreationForAASCDC(pipmedical, s);
		verifyRateBindPasForAACSDC(softly, policyNumber);
	}

	private void verifyRateBindPasForAACSDC(ETCSCoreSoftAssertions softly, String policyNumber) {
		//Verify RFI service and verify it returns doccid
		String doccId = checkDocumentInRfiService(policyNumber, "AACSDC", "District of Columbia Coverage Selection/Rejection Form", "policy", "NS");

		//Verify Bind service
		bindEndorsement(policyNumber, doccId, ErrorEnum.Errors.ERROR_200900.getCode(), ErrorEnum.Errors.ERROR_200900.getMessage(), "attributeForRules");
		//Verify DB Endorsement xml Signed by field is there
		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "AACSDC", "ENDORSEMENT_ISSUE");
		verifyDoccInDb(softly, query, DocGenEnum.Documents.AACSDC);

		//Go to pas and and verify
		goToPasAndVerifyRuleAndSignedBy(softly, policyNumber, AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.DISTRICT_OF_COLUMBIA_COVERAGE_SELECTION_REJECTION_FORM,
				AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORISTS_BODILY_INJURY, "$50,000/$100,000", ErrorEnum.Errors.ERROR_200900);
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
		verifyAadndeScenarios("BI", "25000/50000");
		verifyAadndeScenarios("PD", "15000");
		verifyAadndeScenarios("UMBI", "25000/50000");
		verifyAadndeScenarios("PIP", "25000/50000");
		verifyAadndeScenarios("PIPDED", "250");
	}

	private void verifyAadndeScenarios(String pd, String s) {
		assertSoftly(softly -> {

			String policyNumber = policyCreationForAASCDC(pd, s);

			String doccId = checkDocumentInRfiService(policyNumber, DocGenEnum.Documents.AADNDE1.getId(), DocGenEnum.Documents.AADNDE1.getName(), "policy", "NS");

			bindEndorsement(policyNumber, doccId, ErrorEnum.Errors.ERROR_200123.getCode(), ErrorEnum.Errors.ERROR_200123.getMessage(), "attributeForRules");

			//PAS-24114
			String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, DocGenEnum.Documents.AADNDE1.getId(), AaaDocGenEntityQueries.EventNames.ENDORSEMENT_ISSUE);
			verifyDoccInDb(softly, query, DocGenEnum.Documents.AADNDE1);

			//Go to pas and and verify
			goToPasAndVerifyRuleAndSignedBy(softly, policyNumber, AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.DELAWARE_MOTORISTS_PROTECTION_ACT,
					AutoSSMetaData.PremiumAndCoveragesTab.PROPERTY_DAMAGE_LIABILITY, "$25,000", ErrorEnum.Errors.ERROR_200123);

			String query1 = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, DocGenEnum.Documents.AADNDE1.getId(), AaaDocGenEntityQueries.EventNames.ENDORSEMENT_ISSUE);
			softly.assertThat(DocGenHelper.getDocument(DocGenEnum.Documents.AADNDE1, query1).toString().contains("DocSignedBy")).isFalse();
			softly.assertThat(DocGenHelper.getDocument(DocGenEnum.Documents.AADNDE1, query1).toString().contains("DocSignedDate")).isFalse();
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

			checkIfRfiIsEmpty(policyNumber);

			HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest("UMPD", "40000"), PolicyCoverageInfo.class);

			String doccId = checkDocumentInRfiService(policyNumber, "RUUELLUU", "IMPORTANT NOTICE - Uninsured Motorist Coverage", "policy", "NS");

			helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorEnum.Errors.ERROR_200037_VA.getCode(), ErrorEnum.Errors.ERROR_200037_VA.getMessage(), "attributeForRules");

			HelperCommon.endorsementBind(policyNumber, "Megha Gubbala", Response.Status.OK.getStatusCode(), doccId);

			//Verify Signed by is there in XML
			String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "AA52VA", "ENDORSEMENT_ISSUE");
			verifyDoccInDb(softly, query, DocGenEnum.Documents.AA52VA);

			goToPasAndVerifyRuleAndSignedBy(softly, policyNumber, AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.IMPORTANT_NOTICE_UNINSURED_MOTORIST_COVERAGE,
					AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY, "$50,000/$100,000", ErrorEnum.Errors.ERROR_200037_VA);

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
			String policyNumber = createPolicyForAnyDocument("Limited Tort", "Physically Signed", tortCoverage, aadnpabRule );
			policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus5Day"));
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			tortCoverage.setValue("Full Tort");
			premiumAndCoveragesTab.calculatePremium();
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
			softly.assertThat(documentsAndBindTab.getRequiredToBindAssetList()
					.getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.PA_NOTICE_NAMED_INSURED_REGARDING_TORT_OPTIONS)).hasValue("Not Signed");
			documentsAndBindTab.submitTab();
			//On bind check error message
			errorTab.verify.errorsPresent(true, ErrorEnum.Errors.ERROR_AAA_SS190125);
			errorTab.cancel();

			//delete endorsement
			documentsAndBindTab.cancel();
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
			String policyNumber = createPolicyForAnyDocument("$25,000/$50,000 (-$32.00)", "Not Signed", uimbiCoverage, ruuelluuRule);
			createEndorsementInPasUpdateCoverage("$50,000/$100,000 (+$16.00)", uimbiCoverage);
			assertThat(ruuelluuRule).hasValue("Not Signed");
			deleteEndorsementInPas();
			dxpOnlyCreateEndorsementCheckDocument("UMBI", "50000/100000",
					"IMPORTANT NOTICE - Uninsured Motorist Coverage", "RUUELLUU", policyNumber);
			//TC2
			String policyNumber2 = openAppAndCreatePolicy();
			createEndorsementInPasUpdateCoverage("$25,000/$50,000 (-$32.00)", uimbiCoverage);
			assertThat(ruuelluuRule).hasValue("Not Signed");
			ruuelluuRule.setValue("Physically Signed");
			documentsAndBindTab.submitTab();
			createEndorsementInPasUpdateCoverage("$50,000/$100,000 (+$16.00)", uimbiCoverage);
			assertThat(ruuelluuRule).hasValue("Not Signed");
			deleteEndorsementInPas();
			dxpOnlyCreateEndorsementCheckDocument("UMBI", "50000/100000",
					"IMPORTANT NOTICE - Uninsured Motorist Coverage", "RUUELLUU",  policyNumber2);

		} else if (state.contains("DE")) {
			//TC1
			String policyNumber = createPolicyForAnyDocument("$25,000/$50,000 (-$48.00)", "Not Signed", uimbiCoverage,aadnde1Rule);
			createEndorsementInPasUpdateCoverage("$50,000/$100,000 (+$17.00)", uimbiCoverage);
			assertThat(aadnde1Rule).hasValue("Not Signed");
			deleteEndorsementInPas();
			dxpOnlyCreateEndorsementCheckDocument("UMBI", "50000/100000",
					"Delaware Motorists Protection Act", "AADNDE1", policyNumber);
			//TC2
			String policyNumber2 = openAppAndCreatePolicy();
			createEndorsementInPasUpdateCoverage("$25,000/$50,000 (-$48.00)", uimbiCoverage);
			assertThat(aadnde1Rule).hasValue("Not Signed");
			aadnde1Rule.setValue("Physically Signed");
			documentsAndBindTab.submitTab();
			createEndorsementInPasUpdateCoverage("$50,000/$100,000 (+$17.00)", uimbiCoverage);
			assertThat(aadnde1Rule).hasValue("Not Signed");
			deleteEndorsementInPas();
			dxpOnlyCreateEndorsementCheckDocument("UMBI", "50000/100000",
					"Delaware Motorists Protection Act", "AADNDE1", policyNumber2);

		} else if (state.contains("DC")) {
			//TC1
			String policyNumber = createPolicyForAnyDocument("$25,000/$50,000 (-$32.00)", "Not Signed", biCoverage, aacsdcRule);
			createEndorsementInPasUpdateCoverage("$50,000/$100,000 (+$14.00)", biCoverage);
			assertThat(aacsdcRule).hasValue("Not Signed");
			deleteEndorsementInPas();
			dxpOnlyCreateEndorsementCheckDocument("BI", "50000/100000",
					"District of Columbia Coverage Selection/Rejection Form", "AACSDC", policyNumber);
			//TC2
			String policyNumber2 = openAppAndCreatePolicy();
			createEndorsementInPasUpdateCoverage("$50,000/$100,000 (-$18.00)", biCoverage);
			assertThat(aacsdcRule).hasValue("Not Signed");
			aacsdcRule.setValue("Physically Signed");
			documentsAndBindTab.submitTab();
			createEndorsementInPasUpdateCoverage("$250,000/$500,000 (+$34.00)", biCoverage);
			assertThat(aacsdcRule).hasValue("Not Signed");
			deleteEndorsementInPas();
			dxpOnlyCreateEndorsementCheckDocument("BI", "50000/100000",
					"District of Columbia Coverage Selection/Rejection Form", "AACSDC", policyNumber2);
		}
	}

	private void deleteEndorsementInPas(){
		documentsAndBindTab.cancel();
		Page.dialogConfirmation.buttonDeleteEndorsement.click();
	}

	private void createEndorsementInPasUpdateCoverage(String coverageValue, ComboBox coverage){
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		coverage.setValue(coverageValue);
		premiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
	}

	private void dxpOnlyCreateEndorsementCheckDocument(String coverageId, String coverageValue, String documentName, String documentCode, String policyNumber) {
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		checkIfRfiIsEmpty(policyNumber);
		HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageId, coverageValue), PolicyCoverageInfo.class);
		String docId = checkDocumentInRfiService(policyNumber, documentCode, documentName, "policy", "NS");
		HelperCommon.endorsementBind(policyNumber, "Jovita Pukenaite", Response.Status.OK.getStatusCode(), docId);
	}

    private void dxpOnlyCreateEndorsementCheckDocumentAADNPAB(String tortCoverageValue, String policyNumber){
        helperMiniServices.createEndorsementWithCheck(policyNumber);
        checkIfRfiIsEmpty(policyNumber);
        HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest("TORT", tortCoverageValue), PolicyCoverageInfo.class);
        String docId = checkDocumentInRfiService(policyNumber, "AADNPAB", "Pennsylvania Notice to Named Insured Regarding Tort Options", "policy", "NS");

        helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorEnum.Errors.ERROR_AAA_SS190125.getCode(), ErrorEnum.Errors.ERROR_AAA_SS190125.getMessage(), "attributeForRules");
        HelperCommon.endorsementBind(policyNumber, "Jovita Pukenaite", Response.Status.OK.getStatusCode(), docId);
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

	private String createPolicyForAnyDocument(String limit, String signType, ComboBox coverage , RadioGroup rule) {
		mainApp().open();
		createCustomerIndividual();
		createQuote();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		(coverage).setValue(limit);
		premiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		(rule).setValue(signType);
		documentsAndBindTab.saveAndExit();

		String policyNumber = testEValueDiscount.simplifiedQuoteIssue();
		return policyNumber;
	}

	private void checkIfRfiIsEmpty(String policyNumber){
		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		RFIDocuments rfiServiceResponse = HelperCommon.rfiViewService(policyNumber, false);
		assertSoftly(softly -> {
			softly.assertThat(rfiServiceResponse.url).isNull();
			softly.assertThat(rfiServiceResponse.documents.isEmpty()).isTrue();
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

	private void bindEndorsement(String policyNumber, String doccId, String errorCode, String errorMessage, String field) {
		//Verify Error Code and Error message on bind
		helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, errorCode, errorMessage, field);
		//Bind policy with doccId for successful bind and doccument is electronically signed
		HelperCommon.endorsementBind(policyNumber, "Megha Gubbala", Response.Status.OK.getStatusCode(), doccId);

	}

	private String policyCreationForAASCDC(String coverageId, String newCoverage) {
		//Create Policy
		TestData td = getPolicyDefaultTD();
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName()
				, AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY.getLabel()), "contains=$50,000/$100,000");
		String policyNumber = openAppAndCreatePolicy();

		//Create endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		//update coverage
		HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageId, newCoverage), PolicyCoverageInfo.class);
		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		//TODO-mstrazds: no document needs to be signed
		return policyNumber;
	}

	private void verifyDoccInDb(ETCSCoreSoftAssertions softly, String query, DocGenEnum.Documents aa52va) {
		Document thankYouLetter615121 = DocGenHelper.getDocument(aa52va, query);
		String name = DocGenHelper.getDocumentDataElemByName("DocSignedBy", thankYouLetter615121).getDataElementChoice().getTextField();
		String date = DocGenHelper.getDocumentDataElemByName("DocSignedDate", thankYouLetter615121).getDataElementChoice().getDateTimeField();
		String currentDate = DateTimeUtils.getCurrentDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		softly.assertThat(name).isEqualTo("Megha Gubbala");
		softly.assertThat(date).startsWith(currentDate);

	}

	private void goToPasAndVerifyRuleAndSignedBy(ETCSCoreSoftAssertions softly, String policyNumber,
			AssetDescriptor<RadioGroup> documentAsset, AssetDescriptor<ComboBox> coverageAsset,
			String coverageLimit, ErrorEnum.Errors error) {
		//create endorsement from pas go to bind page verify document is electronically signed
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

		softly.assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(documentAsset).getValue()).isEqualTo("Electronically Signed");

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		//From P&C page change coverage again to verify signed by is resetting to  not signed
		premiumAndCoveragesTab.setPolicyCoverageDetailsValue(coverageAsset.getLabel(), coverageLimit);
		premiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		//add line to verify not signed
		softly.assertThat(documentsAndBindTab.getRequiredToBindAssetList().getAsset(documentAsset).getValue()).isEqualTo("Not Signed");
		documentsAndBindTab.submitTab();
		//On bind verify error message
		errorTab.verify.errorsPresent(true, error);
		errorTab.cancel();
		//Physically sign the doccument and bind policy
		documentsAndBindTab.getRequiredToBindAssetList().getAsset(documentAsset).setValue("Physically Signed");
		documentsAndBindTab.submitTab();

	}
}
