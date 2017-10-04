/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;


import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.modules.policy.AutoSSBaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

import java.util.Arrays;
import java.util.List;

public class PaperlessPreferences extends AutoSSBaseTest {

    private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();

    /**
     * @author Oleg Stasyuk
     * @name Test Paperless Preferences properties and Inquiry mode
     * @scenario 1. Create new Paperless Preferences eligible quote but for the not eligible state (PA)
     * 2. Check Enrolled in Paperless? field and Manage Paperless Preferences button are shown in Documents tab
     * 3. Check Manage Paperless Preferences button is enabled
     * 4. Check Documents Delivery section fields are present
     * 4. Save and Exist
     * 5. Open quote in Inquiry mode
     * 6. Check Manage Paperless Preferences button is disabled
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-283")
    public void pas283_eValuePaperlessPreferences(@Optional("VA") String state) {

        EValueDiscount eValueDiscount = new EValueDiscount();
        eValueDiscount.eValueQuoteCreationVA();

        CustomAssert.enableSoftMode();
        policy.dataGather().start();
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

        //PAS-282, PAS-268 start
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.present();
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.enabled(false);
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.value("Yes");

        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES).verify.present();
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES).verify.enabled();

        //left overs of previous functionality. Showing Hiding rules will change with new story
        documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.SUPPRESS_PRINT).verify.present();
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.ISSUE_DATE).verify.present();
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).verify.present();
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL).verify.present();
        //left overs of previous functionality. Lookup list will change with new story
        List<String> expectedMethodsOfDelivery = Arrays.asList("", "Mail", "Email");
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).verify.optionsContain(expectedMethodsOfDelivery);
        List<String> expectedIncludeWithEmail = Arrays.asList("", "Email with Link", "Email with Attachment");
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL).verify.optionsContain(expectedIncludeWithEmail);

        //PAS-266 start
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES).click();
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.EDIT_PAPERLESS_PREFERENCES_BTN_DONE).click();
        //PAS-282, PAS-268, PAS-266 end

        documentsAndBindTab.saveAndExit();
        policy.quoteInquiry().start();

        //PAS-269 start
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES).verify.present();
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES).verify.enabled(false);
        //PAS-269 end

        documentsAndBindTab.cancel();

        //PAS-283 continue
        policy.bind().start();
        DocumentsAndBindTab.btnPurchase.click();
        ErrorTab errorTab = new ErrorTab();
        errorTab.overrideAllErrors();
        policy.bind().submit();
        new PurchaseTab().fillTab(getPolicyTD("DataGather", "TestData")).submitTab();

        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.present();
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.enabled(false);
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.value("Yes");

        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES).verify.present();
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES).verify.enabled();

        documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.SUPPRESS_PRINT).verify.present();
        //TODO OSI: Confirm with Karen if the below fields should be visible
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.ISSUE_DATE).verify.present(false);
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).verify.present(false);
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL).verify.present(false);
        //PAS-283 continue end

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @author Oleg Stasyuk
     * @name Test Paperless Preferences not shown for state where it is not configured
     * @scenario 1. Create new Paperless Preferences eligible quote but for the not eligible state (PA)
     * 2. Check Enrolled in Paperless? field and Manage Paperless Preferences button are not shown in Documents tab
     * 3. Check Document Printing Details section's fields are present
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-838")
    public void pas838_eValuePaperlessPreferencesNotConfiguredForState(@Optional("PA") String state) {

        EValueDiscount eValueDiscount = new EValueDiscount();
        eValueDiscount.eValueQuoteCreationVA();

        CustomAssert.enableSoftMode();
        policy.dataGather().start();
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.present(false);
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES).verify.present(false);

        documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.SUPPRESS_PRINT).verify.present();
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.ISSUE_DATE).verify.present();
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).verify.present();
        documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL).verify.present();

        documentsAndBindTab.saveAndExit();

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }
}