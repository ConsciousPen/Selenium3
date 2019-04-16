/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;

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
import toolkit.verification.CustomSoftAssertions;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.waiters.Waiters;

public class TestPaperlessPreferences extends AutoSSBaseTest {

    private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
    private InquiryAssetList inquiryAssetListDPD = new InquiryAssetList(new DocumentsAndBindTab().getAssetList().getLocator(), AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.class);
    private InquiryAssetList inquiryAssetListPP = new InquiryAssetList(new DocumentsAndBindTab().getAssetList().getLocator(), AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.class);

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
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-283")
    public void pas283_eValuePaperlessPreferences(@Optional("VA") String state) {
        eValueConfigCheck();
        TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
        testEValueDiscount.eValueQuoteCreation();

        CustomSoftAssertions.assertSoftly(softly -> {
            policy.dataGather().start();
            NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

            //PAS-282, PAS-268 start
            softly.assertThat(documentsAndBindTab.isSectionPresent("Paperless Preferences")).as("'Paperless Preferences' section should be present").isTrue();
            softly.assertThat(documentsAndBindTab.isSectionPresent("Document Delivery Details")).as("'Document Delivery Details' section should be absent").isFalse();
            softly.assertThat(documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS)).isPresent();
            softly.assertThat(documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS)).isEnabled(false);
            softly.assertThat(documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS)).hasValue("Yes");

            softly.assertThat(documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES)).isPresent();
            softly.assertThat(documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES)).isEnabled();
         //   softly.assertThat(documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.EDIT_PAPERLESS_PREFERENCES_BTN_DONE)).isEnabled();

            //left overs of previous functionality. Showing Hiding rules will change with new story
            softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.SUPPRESS_PRINT)).isPresent();
            softly.assertThat(documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY)).isAbsent();
            softly.assertThat(documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL)).isAbsent();
            //left overs of previous functionality. Lookup list will change with new story

            //PAS-3097 remove the Issue Date field from Bind tab (VA state)
            softly.assertThat(documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.ISSUE_DATE)).isPresent(false);
            //PAS-3097 end

            softly.assertThat(documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY)).isAbsent();
            softly.assertThat(documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL)).isAbsent();

            //PAS-266 start
            documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES).click();
            Waiters.SLEEP(20).go();
             while(documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.EDIT_PAPERLESS_PREFERENCES_BTN_DONE).isVisible())
            documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.EDIT_PAPERLESS_PREFERENCES_BTN_DONE).click();
            //PAS-282, PAS-268, PAS-266 end

            //PAS-287 start
            String helpMessageHT10001 = "Indicates the customer's paperless notifications enrollment status. If \"Pending\", advise the customer to accept the terms and conditions. During mid-term, you may not be able to complete the endorsement until the status has changed to \"Yes\".";
            softly.assertThat(DocumentsAndBindTab.helpIconPaperlessPreferences.getAttribute("title")).isEqualTo(helpMessageHT10001);
            //PAS-287 end

            //PAS-277 start
            softly.assertThat(documentsAndBindTab.isSectionPresent("Document Delivery Details")).as("'Document Delivery Details' section should be absent").isFalse();
            softly.assertThat(documentsAndBindTab.isFieldThatIsNotInAssetListIsPresent("Send To")).as("Field 'Send To' is absent").isFalse();
            softly.assertThat(documentsAndBindTab.isFieldThatIsNotInAssetListIsPresent("Country")).as("Field 'Country' is absent").isFalse();
            softly.assertThat(documentsAndBindTab.isFieldThatIsNotInAssetListIsPresent("Zip/Postal Code")).as("Field 'Zip/Postal Code' is absent").isFalse();
            softly.assertThat(documentsAndBindTab.isFieldThatIsNotInAssetListIsPresent("Address Line 1")).as("Field 'Address Line 1' is absent").isFalse();
            softly.assertThat(documentsAndBindTab.isFieldThatIsNotInAssetListIsPresent("Address Line 2")).as("Field 'Address Line 2' is absent").isFalse();
            softly.assertThat(documentsAndBindTab.isFieldThatIsNotInAssetListIsPresent("Address Line 3")).as("Field 'Address Line 3' is absent").isFalse();
            softly.assertThat(documentsAndBindTab.isFieldThatIsNotInAssetListIsPresent("City")).as("Field 'City' is absent").isFalse();
            softly.assertThat(documentsAndBindTab.isFieldThatIsNotInAssetListIsPresent("State / Province")).as("Field 'State / Province' is absent").isFalse();
            softly.assertThat(documentsAndBindTab.isFieldThatIsNotInAssetListIsPresent("Notes")).as("Field 'Notes' is absent").isFalse();
            softly.assertThat(documentsAndBindTab.isFieldThatIsNotInAssetListIsPresent("Issue Date")).as("Field 'Issue Date' is absent").isFalse();
            //PAS-277 end

            documentsAndBindTab.saveAndExit();
            policy.quoteInquiry().start();

            //PAS-269 start
            NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
            softly.assertThat(documentsAndBindTab.isSectionPresent("Paperless Preferences")).as("'Paperless Preferences' section should be present").isTrue();
            softly.assertThat(inquiryAssetListPP.getStaticElement(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES)).isPresent();
            softly.assertThat(inquiryAssetListPP.getStaticElement(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES)).isEnabled(false);
            softly.assertThat(inquiryAssetListPP.getStaticElement(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS)).isPresent();
            softly.assertThat(inquiryAssetListDPD.getStaticElement(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.ISSUE_DATE)).isPresent(false);
            softly.assertThat(inquiryAssetListDPD.getStaticElement(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY)).isPresent(false);
            softly.assertThat(inquiryAssetListDPD.getStaticElement(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL)).isPresent(false);//will change based on View/Hide rules
            documentsAndBindTab.cancel();
            //PAS-269 end

            //PAS-283 continue
            testEValueDiscount.simplifiedQuoteIssue();

            policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
            NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

            softly.assertThat(documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS)).isPresent();
            softly.assertThat(documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS)).isEnabled(false);
            softly.assertThat(documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS)).hasValue("Yes");

            softly.assertThat(documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES)).isPresent();
            softly.assertThat(documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES)).isEnabled();

            softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.SUPPRESS_PRINT)).isPresent();
            //PAS-283 continue end

            //PAS-3097 start
            softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.ISSUE_DATE)).isPresent(false);
            softly.assertThat(documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY)).isPresent(false);
            softly.assertThat(documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL)).isPresent(false);
            documentsAndBindTab.saveAndExit();
            //PAS-3097 end
        });
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
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3097")
    public void pas3097_PaperlessPreferencesOnBindPageFinalCleanUp(@Optional("MD") String state) {
        eValueConfigCheck();
        TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
        testEValueDiscount.eValueQuoteCreation();

        CustomSoftAssertions.assertSoftly(softly -> {
            policy.dataGather().start();
            NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

            softly.assertThat(documentsAndBindTab.isSectionPresent("Paperless Preferences")).as("'Paperless Preferences' section should be present").isTrue();

            softly.assertThat(documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS)).isPresent();
            softly.assertThat(documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS)).isEnabled(false);
            softly.assertThat(documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS)).hasValue("No");
            softly.assertThat(documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES)).isPresent();

            softly.assertThat(documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY)).isPresent(false);
            softly.assertThat(documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL)).isPresent(false);
            softly.assertThat(documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.ISSUE_DATE)).isPresent(false);

            documentsAndBindTab.saveAndExit();

            testEValueDiscount.simplifiedQuoteIssue();

            policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
            NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

            softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.ISSUE_DATE)).isPresent(false);

            softly.assertThat(documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS)).hasValue("No");

            softly.assertThat(documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY)).hasValue("Mail");
            softly.assertThat(documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL)).isPresent(false);
            softly.assertThat(documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY)).containsAllOptions("", "Mail", "Email");
            documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).setValue("Email");
            softly.assertThat(documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL)).isPresent();
            softly.assertThat(documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL)).hasValue("Email with Link");
            softly.assertThat(documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL)).containsAllOptions("Email with Link", "Email with Attachment");
            documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).setValue("Mail");
            softly.assertThat(documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL)).isPresent(false);

            documentsAndBindTab.saveAndExit();
        });
    }

}
