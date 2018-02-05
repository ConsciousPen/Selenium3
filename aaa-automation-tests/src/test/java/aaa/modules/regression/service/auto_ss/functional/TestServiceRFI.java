/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ss.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.sales.auto_ss.TestPolicyNano;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import aaa.modules.regression.service.helper.HelperCommon;
import aaa.modules.regression.service.helper.HelperRfi;
import aaa.modules.regression.service.helper.RfiDocumentResponse;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;

public class TestServiceRFI extends AutoSSBaseTest {
    private final DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
    private final TestEValueDiscount testEValueDiscount = new TestEValueDiscount();

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
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-349", "PAS-341"})
    public void pas349_rfiAuto(@Optional("VA") String state) {
        String today = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);

        createQuoteWithCustomData();

        CustomAssert.enableSoftMode();
        String policyNumber = testEValueDiscount.simplifiedQuoteIssue();
        rfiDocumentContentCheck(policyNumber);

        //PAS-341 Start
        RfiDocumentResponse[] result = HelperCommon.executeRequestRfi(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION.getLabel(), "NBA", "NS");
        HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT_OF_TERMS.getLabel(), "DISC", "NS");
        HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_CURRENT_INSURANCE_FOR.getLabel(), "POI", "NS");
        HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_GOOD_STUDENT_DISCOUNT.getLabel(), "DISC", "NS");
        HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_SMART_DRIVER_COURSE_COMPLETION.getLabel(), "DISC", "NS");
        HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_PRIOR_INSURANCE.getLabel(), "POI", "NS");
        HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_PURCHASE_DATE_BILL_OF_SALE_FOR_NEW_VEHICLES.getLabel(), "DVUD", "NS");
        HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_EQUIVALENT_NEW_CAR_ADDED_PROTECTION_WITH_PRIOR_CARRIER_FOR_NEW_VEHICLES.getLabel(), "DISC", "NS");
        HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.CANADIAN_MVR_FOR_DRIVER.getLabel(), "DVUD", "NS");
        HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PHOTOS_FOR_SALVATAGE_VEHICLE_WITH_PHYSICAL_DAMAGE_COVERAGE.getLabel(), "INSPP", "NS");
        //PAS-341 End

        uploadDocuments(policyNumber);

        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        endorseRateDocuments();
        documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION).verify.value("Pending Review (Uploaded " + today + ")");
        documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT_OF_TERMS).verify.value("Pending Review (Uploaded " + today + ")");
        documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_GOOD_STUDENT_DISCOUNT).verify.value("Pending Review (Uploaded " + today + ")");
        documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_SMART_DRIVER_COURSE_COMPLETION).verify.value("Pending Review (Uploaded " + today + ")");

        //check Upload Pending is present after value is changed
        documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT_OF_TERMS).setValue("Physically Signed");
        documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_SMART_DRIVER_COURSE_COMPLETION).setValue("Yes");
        documentsAndBindTab.submitTab();

        endorseRateDocuments();
        documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION).verify.value("Pending Review (Uploaded " + today + ")");
        documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT_OF_TERMS).verify.value("Physically Signed");
        documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_GOOD_STUDENT_DISCOUNT).verify.value("Pending Review (Uploaded " + today + ")");
        documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_SMART_DRIVER_COURSE_COMPLETION).verify.value("Yes");

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
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
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-349", "PAS-341"})
    public void pas349_rfiNano(@Optional("OH") String state) {
        createQuoteWithCustomDataNano(state);

        CustomAssert.enableSoftMode();
        //TODO Question to Karen: IN has Uninsured Property Damage = No COverage, disabled, but required to have at least 25k
        String policyNumber = testEValueDiscount.simplifiedQuoteIssue();
        rfiDocumentContentCheckNano(policyNumber);

        //PAS-341 Start
        RfiDocumentResponse[] result = HelperCommon.executeRequestRfi(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION.getLabel(), "NBA", "NS");
        //TODO Question to Karen: NV returns "Non-Owner Automobile Endorsement Form" instead of "Non-Owner Automobile Endorsement"
        HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.NON_OWNER_AUTOMOBILE_ENDORSEMENT.getLabel(), "MISC", "NS");
        //PAS-341 End

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    private void createQuoteWithCustomData() {
        mainApp().open();
        createCustomerIndividual();

        policy.initiate();
        policy.getDefaultView().fillUpTo(getTestSpecificTD("TestData"), DocumentsAndBindTab.class, false);
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

        documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION).verify.value("Not Signed");
        documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT_OF_TERMS).verify.value("Not Signed");

        documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_CURRENT_INSURANCE_FOR).verify.value("No");
        documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_GOOD_STUDENT_DISCOUNT).verify.value("No");
        documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_SMART_DRIVER_COURSE_COMPLETION).verify.value("No");
        documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_PRIOR_INSURANCE).verify.value("No");
        documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_PURCHASE_DATE_BILL_OF_SALE_FOR_NEW_VEHICLES).verify.value("No");
        documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_EQUIVALENT_NEW_CAR_ADDED_PROTECTION_WITH_PRIOR_CARRIER_FOR_NEW_VEHICLES).verify.value("No");
        documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.CANADIAN_MVR_FOR_DRIVER).verify.value("No");
        documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PHOTOS_FOR_SALVATAGE_VEHICLE_WITH_PHYSICAL_DAMAGE_COVERAGE).verify.value("No");

        documentsAndBindTab.saveAndExit();
    }

    private void uploadDocuments(String policyNumber) {
        LocalDateTime uploadDate = DateTimeUtils.getCurrentDateTime();
        String formattedDate = uploadDate.format(DateTimeUtils.MM_DD_YYYY);
        DBService.get().executeUpdate(String.format(HelperRfi.UPDATE_DOCUMENT_STATUS, formattedDate, AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION.getLabel(), policyNumber));
        DBService.get().executeUpdate(String.format(HelperRfi.UPDATE_DOCUMENT_STATUS, formattedDate, AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT_OF_TERMS.getLabel(), policyNumber));
        DBService.get().executeUpdate(String.format(HelperRfi.UPDATE_DOCUMENT_STATUS, formattedDate, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_GOOD_STUDENT_DISCOUNT.getLabel(), policyNumber));
        DBService.get().executeUpdate(String.format(HelperRfi.UPDATE_DOCUMENT_STATUS, formattedDate, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_SMART_DRIVER_COURSE_COMPLETION.getLabel(), policyNumber));

        String currentVersion = DBService.get().getValue(String.format(HelperRfi.GET_POLICY_SUMMARY_FIELD, "version", policyNumber)).get();
        String latestPolicySummaryId = DBService.get().getValue(String.format(HelperRfi.GET_POLICY_SUMMARY_FIELD, "id", policyNumber)).get();
        DBService.get().executeUpdate(String.format(HelperRfi.UPDATE_POLICY_VERSION, String.valueOf(Integer.valueOf(currentVersion) + 1), latestPolicySummaryId));

        RfiDocumentResponse[] result = HelperCommon.executeRequestRfi(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION.getLabel(), "NBA", "PS");
        HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT_OF_TERMS.getLabel(), "DISC", "PS");
        HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_CURRENT_INSURANCE_FOR.getLabel(), "POI", "NS");
        HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_GOOD_STUDENT_DISCOUNT.getLabel(), "DISC", "PS");
        HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_SMART_DRIVER_COURSE_COMPLETION.getLabel(), "DISC", "PS");
        HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_PRIOR_INSURANCE.getLabel(), "POI", "NS");
        HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_PURCHASE_DATE_BILL_OF_SALE_FOR_NEW_VEHICLES.getLabel(), "DVUD", "NS");
        HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_EQUIVALENT_NEW_CAR_ADDED_PROTECTION_WITH_PRIOR_CARRIER_FOR_NEW_VEHICLES.getLabel(), "DISC", "NS");
        HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.CANADIAN_MVR_FOR_DRIVER.getLabel(), "DVUD", "NS");
        HelperRfi.policyServiceRfiValuesCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PHOTOS_FOR_SALVATAGE_VEHICLE_WITH_PHYSICAL_DAMAGE_COVERAGE.getLabel(), "INSPP", "NS");
    }

    private static void rfiDocumentContentCheck(String policyNum) {
        String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNum, "AARFIXX", "POLICY_ISSUE");
        //BUG PAS-7702 When issuing some policies, documents are not generated
        DocGenHelper.getDocumentDataSectionsByName("CoverageDetails", DocGenEnum.Documents.AARFIXX, query).get(0).getDocumentDataElements();

        HelperRfi.rfiTagCheck(query, "PrevInsDiscYN", "Y");
        HelperRfi.rfiTagCheck(query, "GoodStuDiscYN", "Y");
        HelperRfi.rfiTagCheck(query, "VehNwAddPrtcYN", "Y");
        HelperRfi.rfiTagCheck(query, "CurInsDrvrYN", "Y");
        HelperRfi.rfiTagCheck(query, "SmrtDrvrCrseCertYN", "Y");
        HelperRfi.rfiTagCheck(query, "VehNwAddPrtcPrevCrirYN", "Y");
        HelperRfi.rfiTagCheck(query, "SalvVehYN", "Y");
        HelperRfi.rfiTagCheck(query, "PsnlAutoApplYN", "Y");
        HelperRfi.rfiTagCheck(query, "CanMVRYN", "Y");
        //TODO UBITrmCndtnYN is N, but the RFI contains it. Kinda illogical
        HelperRfi.rfiTagCheck(query, "UBITrmCndtnYN", "N");
    }

    private static void rfiDocumentContentCheckNano(String policyNum) {
        String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNum, "AARFIXX", "POLICY_ISSUE");
        DocGenHelper.getDocumentDataSectionsByName("CoverageDetails", DocGenEnum.Documents.AARFIXX, query).get(0).getDocumentDataElements();

        HelperRfi.rfiTagCheck(query, "PsnlAutoApplYN", "Y");
        //Non-Owner Automobile Endorsement tag
        HelperRfi.rfiTagCheck(query, "VehTyp", "NO");
    }

    private void createQuoteWithCustomDataNano(String state) {
        mainApp().open();
        createCustomerIndividual();

        policy.initiate();
        policy.getDefaultView().fillUpTo(testDataManager.getDefault(TestPolicyNano.class).getTestData("TestData_" + state), DocumentsAndBindTab.class, false);
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

        documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION).verify.value("Not Signed");
        documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.NON_OWNER_AUTOMOBILE_ENDORSEMENT).verify.value("Not Signed");
        documentsAndBindTab.saveAndExit();
    }

    private void endorseRateDocuments() {
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
    }
}
