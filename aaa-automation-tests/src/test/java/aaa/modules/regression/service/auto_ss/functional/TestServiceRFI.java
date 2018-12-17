/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ss.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import aaa.common.enums.Constants;
import aaa.helpers.rest.dtoDxp.*;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.service.helper.HelperMiniServices;
import aaa.utils.StateList;
import org.assertj.core.api.Assertions;
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

import javax.ws.rs.core.Response;

public class TestServiceRFI extends AutoSSBaseTest {
	private final DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
	private final TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
	private HelperMiniServices helperMiniServices = new HelperMiniServices();
	private boolean flag;

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

			checkDocumentInRfiService(policyNumber, "RUUELLUU", "IMPORTANT NOTICE - Uninsured Motorist Coverage", "RUUELLUU_", "policy", "NS");

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
	 * @author Jovita Pukenaite
	 * @name RFI AACSDC Form
	 * @scenario 1. Create policy.
	 * 2. Create endorsement outside of PAS.
	 * 3. Rate. Hit RFI service.
	 * 4. Check the response.
	 * 5. Update UIMBI/PIPMedical/UIMPD/PIPWORKLOSS/FUNERAL coverage. Rate.
	 * 6. Hit RFI service, check if document is displaying.
	 * 7. Try bind check error
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.DC})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-21423"})
	public void pas21423_aacsdcFormRFI(@Optional("DC") String state) {
		assertSoftly(softly -> {

			TestData td = getPolicyDefaultTD();
			td.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName()
					, AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY.getLabel()), "contains=$50,000/$100,000");
			String policyNumber = openAppAndCreatePolicy();

			//update PIPMedical coverage
			checkRfiResponseAfterCovWasUpdated_pas21423(policyNumber, "PIPMEDICAL", "100000");

			//Update UIMPD coverage
			checkRfiResponseAfterCovWasUpdated_pas21423(policyNumber, "UIMPD", "25000/50000");

			//Update PIPWORKLOSS coverage
			checkRfiResponseAfterCovWasUpdated_pas21423(policyNumber, "PIPWORKLOSS", "12000");

			//Update FUNERAL coverage
			checkRfiResponseAfterCovWasUpdated_pas21423(policyNumber, "FUNERAL", "4000");

			//Update UIMBI coverage
			checkRfiResponseAfterCovWasUpdated_pas21423(policyNumber, "UIMBI", "-1");
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
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			RFIDocuments rfiServiceResponse = HelperCommon.rfiViewService(policyNumber, false);
			softly.assertThat(rfiServiceResponse.url).isNull();
			softly.assertThat(rfiServiceResponse.documents.isEmpty()).isTrue();

			helperMiniServices.endorsementRateAndBind(policyNumber);
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
			TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
			PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();

			mainApp().open();
		/*	createCustomerIndividual();
			createQuote();
			policy.dataGather().start();
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");

			premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY).setValue("$25,000/$50,000 (-$32.00)");
			premiumAndCoveragesTab.calculatePremium();
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
			documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION).setValue("Physically Signed");
			documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.IMPORTANT_NOTICE_UNINSURED_MOTORIST_COVERAGE).setValue("Physically Signed");
			documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.EVALUE_ACKNOWLEDGEMENT).setValue("Physically Signed");
			documentsAndBindTab.saveAndExit();

			String policyNumber = testEValueDiscount.simplifiedQuoteIssue(); */

			String policyNumber ="VASS952918546";

			helperMiniServices.createEndorsementWithCheck(policyNumber);
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			RFIDocuments rfiServiceResponse = HelperCommon.rfiViewService(policyNumber, false);
			softly.assertThat(rfiServiceResponse.url).isNull();
			softly.assertThat(rfiServiceResponse.documents.isEmpty()).isTrue();

			String coverageId="UMPD";
			String newCoverage="40000";

			HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageId, newCoverage), PolicyCoverageInfo.class);
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			RFIDocuments rfiServiceResponse1 = HelperCommon.rfiViewService(policyNumber, true);
			String doccId= rfiServiceResponse1.documents.get(0).documentId;
			softly.assertThat(rfiServiceResponse1.documents.get(0).documentCode).isEqualTo("RUUELLUU");
			softly.assertThat(rfiServiceResponse1.documents.get(0).documentName).isEqualTo("IMPORTANT NOTICE - Uninsured Motorist Coverage");

			helperMiniServices.bindEndorsementWithErrorCheck(policyNumber,"200037_VA","A signed IMPORTANT NOTICE - UNINSURED MOTORIST COVERAGE form must be received prior to issuing this transaction","attributeForRules");

			//helperMiniServices.bindEndorsementWithCheck(policyNumber,);




		});
	}

	private void checkRfiResponseAfterCovWasUpdated_pas21423(String policyNumber, String coverageId, String newCoverage ){
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageId, newCoverage), PolicyCoverageInfo.class);

			checkDocumentInRfiService(policyNumber, "AACSDC", "District of Columbia Coverage Selection/Rejection Form", "AACSDC_h", "policy", "NS");
			//TODO jpukenaite: uncomment when the story for bind error will be done
			//helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, "", "", "");

			HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());
		}

	private void checkDocumentInRfiService(String policyNumber, String documentCode, String documentName, String documentId, String parent, String status){
		helperMiniServices.rateEndorsementWithCheck(policyNumber);

		assertSoftly(softly -> {
			RFIDocuments rfiServiceResponse = HelperCommon.rfiViewService(policyNumber, false);
			softly.assertThat(rfiServiceResponse.url).isNull();
			softly.assertThat(rfiServiceResponse.documents.get(0).documentCode).isEqualTo(documentCode);
			softly.assertThat(rfiServiceResponse.documents.get(0).documentName).isEqualTo(documentName);
			softly.assertThat(rfiServiceResponse.documents.get(0).documentId).startsWith(documentId);
			softly.assertThat(rfiServiceResponse.documents.get(0).status).startsWith(status);
			softly.assertThat(rfiServiceResponse.documents.get(0).parent).isEqualTo(parent);
			softly.assertThat(rfiServiceResponse.documents.get(0).parentOid).isNotEmpty();

			RFIDocuments rfiServiceResponse2 = HelperCommon.rfiViewService(policyNumber, true);
			softly.assertThat(rfiServiceResponse2.url).isNotEmpty();
			softly.assertThat(rfiServiceResponse2.documents).isNotEmpty();
		});
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

		CustomSoftAssertions.assertSoftly(softly -> {
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
}
