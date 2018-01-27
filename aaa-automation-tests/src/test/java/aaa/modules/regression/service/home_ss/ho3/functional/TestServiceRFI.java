/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.home_ss.ho3.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import aaa.modules.regression.service.helper.HelperCommon;
import aaa.modules.regression.service.helper.RfiDocumentResponse;
import aaa.toolkit.webdriver.customcontrols.InquiryAssetList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class TestServiceRFI extends HomeSSHO3BaseTest {

    private final InquiryAssetList inquiryAssetList = new InquiryAssetList(new GeneralTab().getAssetList().getLocator(), AutoSSMetaData.GeneralTab.class);
    private final ErrorTab errorTab = new ErrorTab();
    private final GeneralTab generalTab = new GeneralTab();
    private final DocumentsTab documentsTab = new DocumentsTab();
    private final PurchaseTab purchaseTab = new PurchaseTab();
    private final TestEValueDiscount testEValueDiscount = new TestEValueDiscount();

    /**
     * @author Oleg Stasyuk
     * @name RFI
     * @scenario
     * Signed policy application - Not Signed
     * Proof of ENERGY STAR appliances or green home features - No
     * Proof of central fire alarm - No
     * Proof of central theft alarm - No
     * Proof of plumbing, electrical, heating/cooling system and roof renovations - No
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = {"PAS-349", "PAS-341"})
    public void pas349_rfiHO3_1(@Optional("VA") String state) {
        String yearBuilt = TestData.makeKeyPath(PropertyInfoTab.class.getSimpleName(), HomeSSMetaData.PropertyInfoTab.CONSTRUCTION.getLabel(), "Year built");
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(getTestSpecificTD("TestData1").adjust(yearBuilt, "1939"), DocumentsTab.class, true);

        NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.DOCUMENTS.get());
        documentsTab.getRequiredToIssueAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToIssue.SIGNED_POLICY_APPLICATION).verify.value("Not Signed");
        documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_ENERGY_STAR_APPLIANCES).verify.value("No");
        documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_FIRE_ALARM).verify.value("No");
        documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_THEFT_ALARM).verify.value("No");
        documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_PLUMBING_AND_OTHER_RENOVATIONS).verify.value("No");
        documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_SUBSCRIPTION_TO_FIRE_DEPARTMENT).verify.value("No");
        documentsTab.saveAndExit();

        CustomAssert.enableSoftMode();
        String policyNumber = testEValueDiscount.simplifiedQuoteIssue();

        String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "HSRFIXX", "POLICY_ISSUE");
        DocGenHelper.getDocumentDataSectionsByName("FormData", DocGenEnum.Documents.HSRFIXX, query).get(0).getDocumentDataElements();
        rfiTagCheck(query, "AtBndFlg", "Y");
        rfiTagCheck(query, "OldHoModrnDiscYN", "Y");
        rfiTagCheck(query, "GrnHoDiscYN", "Y");
        rfiTagCheck(query, "RtCntrlAlrmForThft", "Y");
        rfiTagCheck(query, "RtCntrlAlrmForFire", "Y");
        rfiTagCheck(query, "SubFireDepYN", "Y");

        //PAS-341 Start
        RfiDocumentResponse[] result = HelperCommon.executeRequestRfi(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        policyServiceRfiStatusCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToIssue.SIGNED_POLICY_APPLICATION.getLabel(), "NS", "NBA");
        policyServiceRfiStatusCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_ENERGY_STAR_APPLIANCES.getLabel(), "NS", "DISC");
        policyServiceRfiStatusCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_FIRE_ALARM.getLabel(), "NS", "DISC");
        policyServiceRfiStatusCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_THEFT_ALARM.getLabel(), "NS", "DISC");
        policyServiceRfiStatusCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_PLUMBING_AND_OTHER_RENOVATIONS.getLabel(), "NS", "MISC");
        policyServiceRfiStatusCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_SUBSCRIPTION_TO_FIRE_DEPARTMENT.getLabel(), "NS", "MISC");
        //PAS-341 End

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @author Oleg Stasyuk
     * @name RFI
     * @scenario
     * 1.Initiate quote creation.
     * Signed policy application - Not Signed
     * Proof of ENERGY STAR appliances or green home features - No
     * Proof of central fire alarm - No
     * Proof of central theft alarm - No
     * Proof of home renovations for the Home Modernization discount - No
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = {"PAS-349", "PAS-341"})
    public void pas349_rfiHO3_2(@Optional("VA") String state) {
        String yearBuilt = TestData.makeKeyPath(PropertyInfoTab.class.getSimpleName(), HomeSSMetaData.PropertyInfoTab.CONSTRUCTION.getLabel(), "Year built");
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(getTestSpecificTD("TestData2").adjust(yearBuilt, "1941"), DocumentsTab.class, true);

        NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.DOCUMENTS.get());
        documentsTab.getRequiredToIssueAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToIssue.SIGNED_POLICY_APPLICATION).verify.value("Not Signed");
        documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_ENERGY_STAR_APPLIANCES).verify.value("No");
        documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_FIRE_ALARM).verify.value("No");
        documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_THEFT_ALARM).verify.value("No");
        documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_HOME_RENOVATIONS_FOR_MODERNIZATION).verify.value("No");
        documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_SUBSCRIPTION_TO_FIRE_DEPARTMENT).verify.value("No");
        documentsTab.saveAndExit();

        CustomAssert.enableSoftMode();
        String policyNumber = testEValueDiscount.simplifiedQuoteIssue();

        String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "HSRFIXX", "POLICY_ISSUE");
        DocGenHelper.getDocumentDataSectionsByName("FormData", DocGenEnum.Documents.HSRFIXX, query).get(0).getDocumentDataElements();
        rfiTagCheck(query, "AtBndFlg", "Y");
        rfiTagCheck(query, "NewHoModrnDiscYN", "Y");
        rfiTagCheck(query, "GrnHoDiscYN", "Y");
        rfiTagCheck(query, "RtCntrlAlrmForThft", "Y");
        rfiTagCheck(query, "RtCntrlAlrmForFire", "Y");
        rfiTagCheck(query, "SubFireDepYN", "Y");

        //PAS-341 Start
        RfiDocumentResponse[] result = HelperCommon.executeRequestRfi(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        policyServiceRfiStatusCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToIssue.SIGNED_POLICY_APPLICATION.getLabel(), "NS", "NBA");
        policyServiceRfiStatusCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_ENERGY_STAR_APPLIANCES.getLabel(), "NS", "DISC");
        policyServiceRfiStatusCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_FIRE_ALARM.getLabel(), "NS", "DISC");
        policyServiceRfiStatusCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_THEFT_ALARM.getLabel(), "NS", "DISC");
        policyServiceRfiStatusCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_HOME_RENOVATIONS_FOR_MODERNIZATION.getLabel(), "NS", "DISC");
        policyServiceRfiStatusCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_SUBSCRIPTION_TO_FIRE_DEPARTMENT.getLabel(), "NS", "MISC");
        //PAS-341 End

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    private void policyServiceRfiStatusCheck(RfiDocumentResponse[] result, String rfiName, String status, String documentType) {
        RfiDocumentResponse allDocuments = Arrays.stream(result).filter(doc -> rfiName.equals(doc.documentName)).findFirst().orElse(null);
        CustomAssert.assertTrue(rfiName + " rfiName not existent", allDocuments != null);
        CustomAssert.assertTrue(rfiName + " has incorrect status", status.equals(allDocuments.status));
        if (documentType == null) {
            CustomAssert.assertTrue(rfiName + " has incorrect documentType", allDocuments.documentType == null);
        } else {
            CustomAssert.assertTrue(rfiName + " has incorrect documentType", documentType.equals(allDocuments.documentType));
        }
    }

    private static void rfiTagCheck(String query, String tag, String tagValue) {
        CustomAssert.assertEquals(
                tag + "has a problem.", DocGenHelper.getDocumentDataElemByName(tag, DocGenEnum.Documents.HSRFIXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
                        .getTextField(), tagValue);
    }

}
