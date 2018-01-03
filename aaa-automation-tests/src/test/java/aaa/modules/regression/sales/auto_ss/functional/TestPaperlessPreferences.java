/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;

import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.customcontrols.InquiryAssetList;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.waiters.Waiters;

public class TestPaperlessPreferences extends AutoSSBaseTest {

    private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
    private InquiryAssetList inquiryAssetList = new InquiryAssetList(new DocumentsAndBindTab().getAssetList().getLocator(), AutoSSMetaData.DocumentsAndBindTab.class);

    @Test(description = "Preconditions")
    public static void eValueConfigCheck() {
        TestEValueDiscount.eValueConfigCheck();
    }

    /**
     * @author Oleg Stasyuk
     * @name Test Paperless Preferences properties and Inquiry mode
     * @scenario 1. Create new Paperless Preferences eligible quote but for the not eligible state (PA)
     * 2. Check Enrolled in Paperless? field and Manage Paperless Preferences button are shown in Documents tab
     * 3. Check Manage Paperless Preferences button is enabled
     * 4. Check Documents Delivery section fields are present
     * 4. Check Help text for Paperless option
     * 4. Save and Exist
     * 5. Open quote in Inquiry mode
     * 6. Check Manage Paperless Preferences button is disabled
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, dependsOnMethods = "eValueConfigCheck")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-283")
    public void pas283_eValuePaperlessPreferences(@Optional("VA") String state) {

        TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
        testEValueDiscount.eValueQuoteCreation();

        CustomAssert.enableSoftMode();
        policy.dataGather().start();
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

        //PAS-282, PAS-268 start
        inquiryAssetList.assetSectionPresence("Paperless Preferences");
        inquiryAssetList.assetSectionPresence("Document Delivery Details", false);
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.present();
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.enabled(false);
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.value("Yes");

        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES).verify.present();
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES).verify.enabled();

        //left overs of previous functionality. Showing Hiding rules will change with new story
        documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.SUPPRESS_PRINT).verify.present();
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).verify.present(false);
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL).verify.present(false);
        //left overs of previous functionality. Lookup list will change with new story

        //PAS-3097 remove the Issue Date field from Bind tab (VA state)
        inquiryAssetList.assetFieldsAbsence( "Issue Date");
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.ISSUE_DATE).verify.present(false);
        //PAS-3097 end

        inquiryAssetList.assetFieldsAbsence( "Method Of Delivery");
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).verify.present(false);
        inquiryAssetList.assetFieldsAbsence( "Include with Email");
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL).verify.present(false);

        //PAS-266 start
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES).click();
        Waiters.SLEEP(15000).go();
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.EDIT_PAPERLESS_PREFERENCES_BTN_DONE).click();
        //PAS-282, PAS-268, PAS-266 end

        //PAS-287 start
        String helpMessageHT10001 = "Indicates the customer's paperless notifications enrollment status. If \"Pending\", advise the customer to accept the terms and conditions. During mid-term, you may not be able to complete the endorsement until the status has changed to \"Yes\".";
        CustomAssert.assertTrue(DocumentsAndBindTab.helpIconPaperlessPreferences.getAttribute("title").equals(helpMessageHT10001));
        //PAS-287 end

        //PAS-277 start
        inquiryAssetList.assetSectionPresence("Document Delivery Details", false);
        inquiryAssetList.assetFieldsAbsence("Send To", "Country", "Zip/Postal Code", "Address Line 1", "Address Line 2", "Address Line 3", "City", "State / Province", "Notes", "Issue Date");
        //PAS-277 end

        documentsAndBindTab.saveAndExit();
        policy.quoteInquiry().start();

        //PAS-269 start
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        inquiryAssetList.assetSectionPresence("Paperless Preferences");
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES).verify.present();
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES).verify.enabled(false);
        inquiryAssetList.getStaticElement(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS.getLabel()).verify.present();
        inquiryAssetList.getStaticElement(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.ISSUE_DATE.getLabel()).verify.present(false);
        inquiryAssetList.getStaticElement(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY.getLabel()).verify.present(false);
        inquiryAssetList.getStaticElement(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL.getLabel()).verify.present(false);//will change based on View/Hide rules
        documentsAndBindTab.cancel();
        //PAS-269 end

        //PAS-283 continue
        testEValueDiscount.simplifiedQuoteIssue();

        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.present();
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.enabled(false);
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.value("Yes");

        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES).verify.present();
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES).verify.enabled();

        documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.SUPPRESS_PRINT).verify.present();
        //PAS-283 continue end

        //PAS-3097 start
        documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.ISSUE_DATE).verify.present(false);
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).verify.present(false);
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL).verify.present(false);
        documentsAndBindTab.saveAndExit();
        //PAS-3097 end

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @author Oleg Stasyuk
     * @name Test Paperless Preferences not shown for state where it is not configured
     * @scenario 1. Create new Paperless Preferences eligible quote but for the not eligible state (PA)
     * 2. Check Enrolled in Paperless? field and Manage Paperless Preferences button are not shown in Documents tab
     * 3. Check Document Printing Details section's fields are present
     * 4. Check Document Delivery related Address fields are removed
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM}, dependsOnMethods = "eValueConfigCheck")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-838")
    public void pas838_eValuePaperlessPreferencesNotConfiguredForState(@Optional("PA") String state) {

        TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
        testEValueDiscount.eValueQuoteCreation();

        CustomAssert.enableSoftMode();
        policy.dataGather().start();
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

        inquiryAssetList.assetSectionPresence("Paperless Preferences", false);
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.present(false);
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES).verify.present(false);

        inquiryAssetList.assetSectionPresence("Document Print Details", true);
        documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.SUPPRESS_PRINT).verify.present();

        //PAS-277 start
        inquiryAssetList.assetSectionPresence("Document Delivery Details", false);
        inquiryAssetList.assetFieldsAbsence("Send To", "Country", "Zip/Postal Code", "Address Line 1", "Address Line 2", "Address Line 3", "City", "State / Province", "Notes", "Issue Date");
        //PAS-277 end

        //PAS-3097 start
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).verify.present(false);
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL).verify.present(false);
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.ISSUE_DATE).verify.present(false);

        documentsAndBindTab.saveAndExit();

        testEValueDiscount.simplifiedQuoteIssue();

        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

        documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.ISSUE_DATE).verify.present(false);

        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).verify.value("Mail");
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL).verify.present(false);
        List<String> expectedMethodsOfDelivery = Arrays.asList("", "Mail", "Email");
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).verify.optionsContain(expectedMethodsOfDelivery);
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).setValue("Email");
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL).verify.present(true);
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL).verify.value("Email with Link");
        List<String> expectedIncludeWithEmail = Arrays.asList("Email with Link", "Email with Attachment");
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL).verify.optionsContain(expectedIncludeWithEmail);
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).setValue("Mail");
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL).verify.present(false);

        documentsAndBindTab.saveAndExit();
        //PAS-3097 end

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @author Alex Tinkovan
     * @name Test Paperless Preferences properties NO and hidden Included With Email when Method Of Delivery is Mail in endorsement
     * @scenario 1. Create new Paperless Preferences eligible quote for eligible state (MD)
     * 2. Check Enrolled in Paperless? - disabled and No
     * 3. Check Method Of Delivery
     * 4. Check Included With Email
     * 5. Check Issue Date
     * 6. Perform Endorsement
     * 7. Check Enrolled in Paperless? - disabled and No
     * 8. Check Method Of Delivery
     * 9. Check Included With Email
     * 10. Check Issue Date
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM}, dependsOnMethods = "eValueConfigCheck")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3097")
    public void pas3097_PaperlessPreferencesOnBindPageFinalCleanUp(@Optional("MD") String state) {

        TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
        testEValueDiscount.eValueQuoteCreation();

        CustomAssert.enableSoftMode();
        policy.dataGather().start();
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

        inquiryAssetList.assetSectionPresence("Paperless Preferences");

        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.present();
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.enabled(false);
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.value("No");
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES).verify.present(true);

        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).verify.present(false);
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL).verify.present(false);
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.ISSUE_DATE).verify.present(false);

        documentsAndBindTab.saveAndExit();

        testEValueDiscount.simplifiedQuoteIssue();

        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

        documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.ISSUE_DATE).verify.present(false);

        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.value("No");

        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).verify.value("Mail");
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL).verify.present(false);
        List<String> expectedMethodsOfDelivery = Arrays.asList("", "Mail", "Email");
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).verify.optionsContain(expectedMethodsOfDelivery);
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).setValue("Email");
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL).verify.present(true);
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL).verify.value("Email with Link");
        List<String> expectedIncludeWithEmail = Arrays.asList("Email with Link", "Email with Attachment");
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL).verify.optionsContain(expectedIncludeWithEmail);
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).setValue("Mail");
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL).verify.present(false);

        documentsAndBindTab.saveAndExit();

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

}