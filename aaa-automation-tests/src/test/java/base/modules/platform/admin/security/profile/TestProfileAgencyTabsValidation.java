/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.platform.admin.security.profile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.controls.AdvancedSelector;

import aaa.admin.metadata.security.ProfileMetaData;
import aaa.admin.modules.agencyvendor.AgencyVendorType;
import aaa.admin.modules.agencyvendor.IAgencyVendor;
import aaa.admin.modules.security.profile.IProfile;
import aaa.admin.modules.security.profile.ProfileType;
import aaa.admin.modules.security.profile.defaulttabs.AuthorityLevelsTab;
import aaa.admin.modules.security.profile.defaulttabs.BankingDetailsTab;
import aaa.admin.modules.security.profile.defaulttabs.GeneralProfileTab;
import aaa.admin.pages.security.ProfilePage;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.main.enums.ActionConstants;
import aaa.main.enums.AdminConstants;
import base.modules.platform.PlatformBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTime;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AbstractContainer;

/**
 * @author Deivydas Piliukaitis
 * @name Test for Profile General Info Tab, Authority Levels Tab
 * @scenario
 * Scenario 1:
 * 1. Initiate Profile creation (channel Agency)
 * 2. Check that EIS User Radio group is enabled, Channel text field shows correct information
 * 3. Check mandatory field error messages
 * 4. Check email, phone, fax error messages
 * 5. Verify add agency/location functionality
 * 6. Verify User Specific Information section
 * 7. Verify Roles section
 * 8. Verify Product Access Roles section
 * 9. Fill all fields
 * 10. Authority Levels Tab: verify Add/Edit/Delete Authority Levels functionality
 * 11. Save profile
 * 12. Search created profile and click Change
 * 13. Verify that all fields saved correctly
 * Scenario 2:
 * 1. Create Agency with 'Commission Payment Sent To' indicator set to 'Agent (Individual)'
 * 2. Create User's Profiles for use in Relationship as Manager and User Subordinates
 * 3. Initiate Profile creation (channel Agency)
 * 4. Profile General Tab - Relationship: Managers and User Subordinates Validation
 * 5. Profile Banking Details Tab Validation - Check
 * 6. Save Profile
 * 7. Search created Profile and click Change
 * 8. Profile Banking Details Tab Validation after Save - Check
 * 9. Profile Banking Details Tab - Update to EFT
 * 10. Update Profile
 * 11. Search created Profile and click Change
 * 12. Profile Banking Details Tab Validation after Save - EFT
 * 13. Press Cancel
 * 14. Check Relationship data are reflected for Manager and Subordinate
 * @details
 */

public class TestProfileAgencyTabsValidation extends PlatformBaseTest {

    private String LOGIN = ProfileMetaData.SearchByField.USER_LOGIN.getLabel();
    private String FIRST_NAME = ProfileMetaData.SearchByField.FIRST_NAME.getLabel();
    private String LAST_NAME = ProfileMetaData.SearchByField.LAST_NAME.getLabel();
    private String SUBPRODUCER_ID = "Profile/Subproducer ID";

    private String ext = "12345";
    private String email = "correct@email.lt";
    private String phone = "123456789";
    private String agencyName = "Broker1Name";
    private String agencyCode = "Broker1Code";
    private String secondAgencyName = "Broker2Name";
    private String secondAgencyCode = "Broker2Code";
    private String roleName = "QA All";
    private String secondRoleName = "Agency User";

    private AgencyVendorType agencyType = AgencyVendorType.AGENCY;
    private IAgencyVendor agency = agencyType.get();
    private IProfile agencyProfile = ProfileType.AGENCY.get();
    private TestData tdAgencyProfileData = tdSpecific.getTestData("ProfileInformationSection");
    private Map<String, String> mapAgencyValues = new HashMap<>();
    private Map<String, String> mapAgencyValues2 = new HashMap<>();
    private Map<String, String> mapAgencyValuesM = new HashMap<>();
    private Map<String, String> mapAgencyValuesS = new HashMap<>();
    private AbstractContainer<?, ?> assetList = new GeneralProfileTab().getAssetList();
    private AbstractContainer<?, ?> assetListSearchAgency = new GeneralProfileTab().assetListSearchForm;

    @Test(groups = {"7.2_All_UC_Add/EditProfileInformation", "7.2_All_UC_ManagingAgenciesForAUser", "6.2.2_All_Add/EditUserSpecificInformation", "7.2_All_UC_ManagingUserRoles",
        "7.2_All_UC_ManagingUserProductAccessRoles", "7.2_None_UC_Add/Edit/DeleteSecurityProfile-AuthorityLevels"})
    @TestInfo(component = "Platform.Admin")
    public void testAgencyProfileTabs() {
        storeValues(mapAgencyValues, tdAgencyProfileData);

        adminApp().open();

        create2PAR();

        agencyProfile.initiate();

        CustomAssert.enableSoftMode();

        log.info("TEST: Profile General Info Page Validation");
        validateProfileInformationSection();

        log.info("TEST: Profile Authority Levels Tab Validation");
        validateProfileAuthorityLevelsTab();

        ProfilePage.change(ProfilePage.getSearchTestData(LOGIN, mapAgencyValues.get(LOGIN)));

        log.info("TEST: Profile General Info Page Validation After Save");
        validateAfterSave();

        log.info("TEST: Profile Authority Levels Tab Validation After Save");
        validateAfterSaveProfileAuthorityLevelsTab();

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Test(groups = {"7.2_None_UC_Add/EditSecurityProfile-BankingDetails", "7.2_All_UC_ManagingUserManagers", "7.2_All_UC_ManagingUserSubordinates"})
    @TestInfo(component = "Platform.Admin")
    public void testAgencyProfileTabsWithBankingDetails() {
        GeneralProfileTab generalProfileTab = new GeneralProfileTab();

        TestData tdAgency = tdSpecific.getTestData("AgencyCreation");
        TestData tdProfile = tdSpecific.getTestData("ProfileCreation");
        TestData tdProfileManager = tdSpecific.getTestData("ProfileManager");
        TestData tdProfileSubordinate = tdSpecific.getTestData("ProfileSubordinate");

        storeValues(mapAgencyValues2, tdProfile);
        storeValues(mapAgencyValuesM, tdProfileManager);
        storeValues(mapAgencyValuesS, tdProfileSubordinate);

        adminApp().open();

        log.info("TEST: Create Agency");
        String agencyName = agency.createAgency(tdAgency);

        //Create User's Profiles for use in Relationship as Manager and User Subordinates
        agencyProfile.create(tdProfileManager, agencyName);
        agencyProfile.create(tdProfileSubordinate, agencyName);

        create2PAR();

        agencyProfile.initiate();
        generalProfileTab.fillTab(tdProfile.adjust("GeneralProfileTab|Agency/Locations|Agency Name", agencyName)
                .adjust("GeneralProfileTab|Managers Select", mapAgencyValuesM.get(LAST_NAME) + ", " + mapAgencyValuesM.get(FIRST_NAME))
                .adjust("GeneralProfileTab|User Subordinates Select", mapAgencyValuesS.get(LAST_NAME) + ", " + mapAgencyValuesS.get(FIRST_NAME)));

        CustomAssert.enableSoftMode();

        log.info("TEST: Profile General Tab - Relationship: Managers and User Subordinates Validation");
        generalProfileTab.verifyFieldHasValue("Managers", mapAgencyValuesM.get(LAST_NAME) + ", " + mapAgencyValuesM.get(FIRST_NAME));
        generalProfileTab.verifyFieldHasValue("User Subordinates", mapAgencyValuesS.get(LAST_NAME) + ", " + mapAgencyValuesS.get(FIRST_NAME));

        generalProfileTab.submitTab();

        log.info("TEST: Profile Banking Details Tab Validation");
        validateProfileBankingDetailsTab();

        //Inquire mode due to defect EISDEV-160450. After fix this defect it is required to update test accordingly: with/without "ABA Transit #" field.
        ProfilePage.inquiry(ProfilePage.getSearchTestData(LOGIN, mapAgencyValues2.get(LOGIN)));
        NavigationPage.toViewTab("Banking Details");
        NavigationPage.toViewTab("Authority Levels");
        GeneralProfileTab.buttonReturn.click();

        ProfilePage.change(ProfilePage.getSearchTestData(LOGIN, mapAgencyValues2.get(LOGIN)));

        log.info("TEST: Profile Banking Details Tab Validation After Save");
        validateAfterSaveProfileBankingDetailsTab(tdProfile);

        ProfilePage.change(ProfilePage.getSearchTestData(LOGIN, mapAgencyValues2.get(LOGIN)));

        log.info("TEST: Profile Banking Details Tab Validation After Save with EFT Method");
        validateAfterSaveProfileBankingDetailsTabEFT(tdProfile);

        //check Relationship data are reflected for Manager
        ProfilePage.inquiry(ProfilePage.getSearchTestData(LOGIN, mapAgencyValuesM.get(LOGIN)));
        generalProfileTab.verifyFieldHasValue("User Subordinates", mapAgencyValues2.get(LAST_NAME) + ", " + mapAgencyValues2.get(FIRST_NAME));
        GeneralProfileTab.buttonReturn.click();

        //check Relationship data are reflected for Subordinate
        ProfilePage.inquiry(ProfilePage.getSearchTestData(LOGIN, mapAgencyValuesS.get(LOGIN)));
        generalProfileTab.verifyFieldHasValue("Managers", mapAgencyValues2.get(LAST_NAME) + ", " + mapAgencyValues2.get(FIRST_NAME));

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    private void storeValues(Map<String, String> mapValues, TestData tdProfileCreate) {
        mapValues.put(LOGIN, tdProfileCreate.getValue(GeneralProfileTab.class.getSimpleName(), LOGIN));
        mapValues.put(FIRST_NAME, tdProfileCreate.getValue(GeneralProfileTab.class.getSimpleName(), FIRST_NAME));
        mapValues.put(LAST_NAME, tdProfileCreate.getValue(GeneralProfileTab.class.getSimpleName(), LAST_NAME));
        mapValues.put(SUBPRODUCER_ID, tdProfileCreate.getValue(GeneralProfileTab.class.getSimpleName(), SUBPRODUCER_ID));
    }

    private void validateProfileInformationSection() {
        assetList.getControl(ProfileMetaData.GeneralProfileTab.CHANNEL.getLabel(), StaticElement.class).verify.value(ProfileType.AGENCY.getName());
        CustomAssert.assertTrue(assetList.getControl(ProfileMetaData.GeneralProfileTab.EIS_USER.getLabel(), RadioGroup.class).isEnabled());
        CustomAssert.assertEquals("Yes", assetList.getControl(ProfileMetaData.GeneralProfileTab.EIS_USER.getLabel(), RadioGroup.class).getValue());
        CustomAssert.assertEquals("Yes", assetList.getControl(ProfileMetaData.GeneralProfileTab.SELLS_INSURANCE_PRODUCTS.getLabel(), RadioGroup.class).getValue());
        CustomAssert.assertEquals(Boolean.FALSE, assetList.getControl(ProfileMetaData.GeneralProfileTab.COMMISSIONABLE.getLabel(), CheckBox.class).getValue());

        GeneralProfileTab.buttonSave.click();
        //Profile information section errors
        assetList.getWarning(ProfileMetaData.GeneralProfileTab.FIRST_NAME.getLabel()).verify.value("First Name is required");
        assetList.getWarning(ProfileMetaData.GeneralProfileTab.LAST_NAME.getLabel()).verify.value("Last Name is required");
        assetList.getWarning(ProfileMetaData.GeneralProfileTab.PROFILE_SUBPRODUCER_ID.getLabel()).verify.value("Profile ID is required");
        //Agency/Locations section errors
        GeneralProfileTab.errorAgencyLocation.verify.value("One Agency/Location must be marked as the default");
        //User specific information section errors
        assetList.getWarning(ProfileMetaData.GeneralProfileTab.USER_NAME.getLabel()).verify.value("User Name is required");
        assetList.getWarning(ProfileMetaData.GeneralProfileTab.USER_LOGIN.getLabel()).verify.value("User Login is required");
        assetList.getWarning(ProfileMetaData.GeneralProfileTab.PASSWORD.getLabel()).verify.value("Password is required");
        assetList.getWarning(ProfileMetaData.GeneralProfileTab.CONFIRM_PASSWORD.getLabel()).verify.value("Confirm Password is required");

        validateAgencyLocations();

        new GeneralProfileTab().fillTab(tdAgencyProfileData);
        GeneralProfileTab.buttonSave.click();
        assetList.getWarning(ProfileMetaData.GeneralProfileTab.EXPIRATION_DATE.getLabel()).verify.value("Expiration Date should be after activation date");
        assetList.getControl(ProfileMetaData.GeneralProfileTab.EXPIRATION_DATE.getLabel(), TextBox.class).setValue(new DateTime(DateTime.MM_DD_YYYY).toString());

        assetList.getControl(ProfileMetaData.GeneralProfileTab.PHONE.getLabel(), TextBox.class).setValue("123");
        assetList.getWarning(ProfileMetaData.GeneralProfileTab.PHONE.getLabel()).verify.value("Phone number digits entered are less than 10");
        assetList.getControl(ProfileMetaData.GeneralProfileTab.PHONE.getLabel(), TextBox.class).setValue(phone);

        assetList.getControl(ProfileMetaData.GeneralProfileTab.FAX.getLabel(), TextBox.class).setValue("123");
        assetList.getWarning(ProfileMetaData.GeneralProfileTab.FAX.getLabel()).verify.value("Phone number digits entered are less than 10");
        assetList.getControl(ProfileMetaData.GeneralProfileTab.FAX.getLabel(), TextBox.class).setValue(phone);

        new GeneralProfileTab().fillTab(tdSpecific.getTestData("PasswordData"));
        assetList.getControl(ProfileMetaData.GeneralProfileTab.E_MAIL_ADDRESS.getLabel(), TextBox.class).setValue("wrongEmail");
        GeneralProfileTab.buttonSave.click();
        assetList.getWarning(ProfileMetaData.GeneralProfileTab.E_MAIL_ADDRESS.getLabel()).verify.value("Value is not valid email");
        assetList.getControl(ProfileMetaData.GeneralProfileTab.E_MAIL_ADDRESS.getLabel(), TextBox.class).setValue(email);

        assetList.getControl(ProfileMetaData.GeneralProfileTab.EXT.getLabel(), TextBox.class).setValue(ext + "678");
        assetList.getControl(ProfileMetaData.GeneralProfileTab.EXT.getLabel(), TextBox.class).verify.value(ext);

        validateUserSpecificInformation();

        validateUserRoles();

        validateProductAccessRoles();

        //    Page.buttonSave.click();
    }

    private void validateAfterSave() {
        assetList.getControl(ProfileMetaData.GeneralProfileTab.CHANNEL.getLabel(), StaticElement.class).verify.value(ProfileType.AGENCY.getName());
        CustomAssert.assertFalse(assetList.getControl(ProfileMetaData.GeneralProfileTab.EIS_USER.getLabel(), RadioGroup.class).isEnabled());
        CustomAssert.assertFalse(assetList.getControl(ProfileMetaData.GeneralProfileTab.PROFILE_SUBPRODUCER_ID.getLabel(), TextBox.class).isEnabled());
        CustomAssert.assertEquals("Yes", assetList.getControl(ProfileMetaData.GeneralProfileTab.EIS_USER.getLabel(), RadioGroup.class).getValue());
        CustomAssert.assertEquals("Yes", assetList.getControl(ProfileMetaData.GeneralProfileTab.SELLS_INSURANCE_PRODUCTS.getLabel(), RadioGroup.class).getValue());
        CustomAssert.assertEquals(Boolean.FALSE, assetList.getControl(ProfileMetaData.GeneralProfileTab.COMMISSIONABLE.getLabel(), CheckBox.class).getValue());

        assetList.getControl(ProfileMetaData.GeneralProfileTab.FIRST_NAME.getLabel(), TextBox.class).verify.value(mapAgencyValues.get(FIRST_NAME));
        assetList.getControl(ProfileMetaData.GeneralProfileTab.LAST_NAME.getLabel(), TextBox.class).verify.value(mapAgencyValues.get(LAST_NAME));
        assetList.getControl(ProfileMetaData.GeneralProfileTab.PROFILE_SUBPRODUCER_ID.getLabel(), TextBox.class).verify.value(mapAgencyValues.get(SUBPRODUCER_ID));

        assetList.getControl(ProfileMetaData.GeneralProfileTab.MIDDLE_NAME.getLabel(), TextBox.class).verify.value(
                tdAgencyProfileData.getValue(GeneralProfileTab.class.getSimpleName(), ProfileMetaData.GeneralProfileTab.MIDDLE_NAME.getLabel()));
        assetList.getControl(ProfileMetaData.GeneralProfileTab.EFFECTIVE_DATE.getLabel(), TextBox.class).verify.value(
                tdAgencyProfileData.getValue(GeneralProfileTab.class.getSimpleName(), ProfileMetaData.GeneralProfileTab.EFFECTIVE_DATE.getLabel()));
        assetList.getControl(ProfileMetaData.GeneralProfileTab.EXPIRATION_DATE.getLabel(), TextBox.class).verify.value(new DateTime(DateTime.MM_DD_YYYY).toString());
        assetList.getControl(ProfileMetaData.GeneralProfileTab.JOB_TITLE.getLabel(), TextBox.class).verify.value(
                tdAgencyProfileData.getValue(GeneralProfileTab.class.getSimpleName(), ProfileMetaData.GeneralProfileTab.JOB_TITLE.getLabel()));
        assetList.getControl(ProfileMetaData.GeneralProfileTab.SIGNATURE_URI.getLabel(), TextBox.class).verify.value(
                tdAgencyProfileData.getValue(GeneralProfileTab.class.getSimpleName(), ProfileMetaData.GeneralProfileTab.SIGNATURE_URI.getLabel()));

        assetList.getControl(ProfileMetaData.GeneralProfileTab.EXT.getLabel(), TextBox.class).verify.value(ext);
        assetList.getControl(ProfileMetaData.GeneralProfileTab.PHONE.getLabel(), TextBox.class).verify.value(phone);
        assetList.getControl(ProfileMetaData.GeneralProfileTab.FAX.getLabel(), TextBox.class).verify.value(phone);
        assetList.getControl(ProfileMetaData.GeneralProfileTab.E_MAIL_ADDRESS.getLabel(), TextBox.class).verify.value(email);

        //Agency/Location
        GeneralProfileTab.tableAgencyLocation.getRow(1).getCell(AdminConstants.AdminAgencyLocationTable.AGENCY_LOCATION_NAME).verify.value(agencyName);
        GeneralProfileTab.tableAgencyLocation.getRow(1).getCell(AdminConstants.AdminAgencyLocationTable.AGENCY_LOCATION_CODE).verify.value(agencyCode);
        GeneralProfileTab.tableAgencyLocation.getRow(2).getCell(AdminConstants.AdminAgencyLocationTable.AGENCY_LOCATION_NAME).verify.value(secondAgencyName);
        GeneralProfileTab.tableAgencyLocation.getRow(2).getCell(AdminConstants.AdminAgencyLocationTable.AGENCY_LOCATION_CODE).verify.value(secondAgencyCode);
        CustomAssert.assertEquals(Boolean.TRUE, GeneralProfileTab.tableAgencyLocation.getRow(1).getCell(AdminConstants.AdminAgencyLocationTable.DEFAULT).controls.checkBoxes.get(1).getValue());
        CustomAssert.assertEquals(Boolean.FALSE, GeneralProfileTab.tableAgencyLocation.getRow(2).getCell(AdminConstants.AdminAgencyLocationTable.DEFAULT).controls.checkBoxes.get(1).getValue());

        //User Specific Information
        assetList.getControl(ProfileMetaData.GeneralProfileTab.USER_LOGIN.getLabel(), TextBox.class).verify.value(mapAgencyValues.get(LOGIN));
        assetList.getControl(ProfileMetaData.GeneralProfileTab.USER_NAME.getLabel(), TextBox.class).verify.enabled(Boolean.FALSE);
        assetList.getControl(ProfileMetaData.GeneralProfileTab.USER_LOGIN.getLabel(), TextBox.class).verify.enabled(Boolean.TRUE); //EISDEV-154024
        assetList.getControl(ProfileMetaData.GeneralProfileTab.PASSWORD.getLabel(), TextBox.class).verify.value("");
        assetList.getControl(ProfileMetaData.GeneralProfileTab.CONFIRM_PASSWORD.getLabel(), TextBox.class).verify.value("");
        assetList.getControl(ProfileMetaData.GeneralProfileTab.USER_WORK_STATUS.getLabel(), RadioGroup.class).verify.value("Available");
        assetList.getControl(ProfileMetaData.GeneralProfileTab.CATEGORY.getLabel(), ComboBox.class).verify.value("Agent");
        assetList.getControl(ProfileMetaData.GeneralProfileTab.USER_LANGUAGE.getLabel(), ComboBox.class).verify.value("");
        assetList.getControl(ProfileMetaData.GeneralProfileTab.SECURITY_DOMAIN.getLabel(), ComboBox.class).verify.enabled(Boolean.FALSE);

        //Roles
        GeneralProfileTab.labelAddedRoles.verify.value(roleName);

        //Product Access Roles
        GeneralProfileTab.labelAddedPARoles.verify.value(parNameAndCode1);
    }

    private void validateAgencyLocations() {
        assetList.getControl(ProfileMetaData.GeneralProfileTab.RESTRICT_TO_SELECTED_AGENCY_LOCATIONS.getLabel(), RadioGroup.class).verify.present(Boolean.FALSE);

        assetListSearchAgency.getControl(ProfileMetaData.GeneralProfileTab.AddAgencyMetaData.BUTTON_OPEN_POPUP.getLabel(), Button.class).click();
        assetListSearchAgency.getControl(ProfileMetaData.GeneralProfileTab.AddAgencyMetaData.CHANNEL.getLabel(), ComboBox.class).verify.value(ProfileType.AGENCY.getName());
        assetListSearchAgency.getControl(ProfileMetaData.GeneralProfileTab.AddAgencyMetaData.AGENCY_NAME.getLabel(), TextBox.class).setValue(RandomStringUtils.randomAlphabetic(7));
        assetListSearchAgency.getControl(ProfileMetaData.GeneralProfileTab.AddAgencyMetaData.BUTTON_SEARCH.getLabel(), Button.class).click();
        GeneralProfileTab.labelAgencyNotFound.verify.value("Search item not found");

        assetListSearchAgency.getControl(ProfileMetaData.GeneralProfileTab.AddAgencyMetaData.AGENCY_NAME.getLabel(), TextBox.class).setValue(agencyName);
        assetListSearchAgency.getControl(ProfileMetaData.GeneralProfileTab.AddAgencyMetaData.BUTTON_SEARCH.getLabel(), Button.class).click();
        GeneralProfileTab.tableAgencySearchResult.getRow(1).getCell(AdminConstants.AdminAgencySearchResultTable.AGENCY_NAME).verify.value(agencyName);
        GeneralProfileTab.tableAgencySearchResult.getRow(1).getCell(AdminConstants.AdminAgencySearchResultTable.CODE).verify.value(agencyCode);
        assetListSearchAgency.getControl(ProfileMetaData.GeneralProfileTab.AddAgencyMetaData.BUTTON_CANCEL.getLabel(), Button.class).click();
        CustomAssert.assertEquals(0, GeneralProfileTab.tableAgencyLocation.getRowsCount());

        new GeneralProfileTab().fillTab(tdSpecific.getTestData("AgencyData"));
        CustomAssert.assertEquals(1, GeneralProfileTab.tableAgencyLocation.getRowsCount());
        new GeneralProfileTab().fillTab(tdSpecific.getTestData("SecondAgencyData"));
        CustomAssert.assertEquals(2, GeneralProfileTab.tableAgencyLocation.getRowsCount());
        GeneralProfileTab.tableAgencyLocation.getRow(1).getCell(AdminConstants.AdminAgencyLocationTable.AGENCY_LOCATION_NAME).verify.value(agencyName);
        GeneralProfileTab.tableAgencyLocation.getRow(1).getCell(AdminConstants.AdminAgencyLocationTable.AGENCY_LOCATION_CODE).verify.value(agencyCode);
        GeneralProfileTab.tableAgencyLocation.getRow(2).getCell(AdminConstants.AdminAgencyLocationTable.AGENCY_LOCATION_NAME).verify.value(secondAgencyName);
        GeneralProfileTab.tableAgencyLocation.getRow(2).getCell(AdminConstants.AdminAgencyLocationTable.AGENCY_LOCATION_CODE).verify.value(secondAgencyCode);
        CustomAssert.assertEquals(Boolean.FALSE, GeneralProfileTab.tableAgencyLocation.getRow(1).getCell(AdminConstants.AdminAgencyLocationTable.DEFAULT).controls.checkBoxes.get(1).getValue());
        CustomAssert.assertEquals(Boolean.FALSE, GeneralProfileTab.tableAgencyLocation.getRow(2).getCell(AdminConstants.AdminAgencyLocationTable.DEFAULT).controls.checkBoxes.get(1).getValue());

        GeneralProfileTab.buttonSave.click();
        GeneralProfileTab.errorAgencyLocation.verify.value("One Agency/Location must be marked as the default");

        GeneralProfileTab.tableAgencyLocation.getRow(1).getCell(AdminConstants.AdminAgencyLocationTable.DEFAULT).controls.checkBoxes.get(1).setValue(Boolean.TRUE);
        GeneralProfileTab.buttonSave.click();
        GeneralProfileTab.errorAgencyLocation.verify.present(Boolean.FALSE);
    }

    private void validateUserSpecificInformation() {
        assetList.getControl(ProfileMetaData.GeneralProfileTab.USER_WORK_STATUS.getLabel(), RadioGroup.class).verify.present();
        assetList.getControl(ProfileMetaData.GeneralProfileTab.USER_WORK_STATUS.getLabel(), RadioGroup.class).verify.value("Available");
        assetList.getControl(ProfileMetaData.GeneralProfileTab.SECURITY_DOMAIN.getLabel(), ComboBox.class).verify.enabled(Boolean.FALSE);
        assetList.getControl(ProfileMetaData.GeneralProfileTab.USER_LANGUAGE.getLabel(), ComboBox.class).verify.value("");
        assetList.getControl(ProfileMetaData.GeneralProfileTab.CATEGORY.getLabel(), ComboBox.class).verify.value("Agent");

        assetList.getControl(ProfileMetaData.GeneralProfileTab.PASSWORD.getLabel(), TextBox.class).setValue("qa");
        assetList.getControl(ProfileMetaData.GeneralProfileTab.CONFIRM_PASSWORD.getLabel(), TextBox.class).setValue("wrongPassword");
        GeneralProfileTab.buttonSave.click();

        GeneralProfileTab.errorDifferentPassword.verify.value("Different passwords were entered. Please retype.");

        new GeneralProfileTab().fillTab(tdSpecific.getTestData("PasswordData"));
    }

    private void validateUserRoles() {
        validateAdvanceSelectorPopup(assetList.getControl(ProfileMetaData.GeneralProfileTab.ROLES.getLabel(), AdvancedSelector.class), GeneralProfileTab.labelAddedRoles, roleName, secondRoleName,
                "No Roles Found");
    }

    private void validateProductAccessRoles() {
        validateAdvanceSelectorPopup(assetList.getControl(ProfileMetaData.GeneralProfileTab.PAR.getLabel(), AdvancedSelector.class), GeneralProfileTab.labelAddedPARoles, parNameAndCode1,
                parNameAndCode2, "No Product Access Roles Found");
    }

    private void validateAdvanceSelectorPopup(AdvancedSelector popup, StaticElement labelAddedItems, String firstName, String secondName, String errorMessage) {
        popup.buttonOpenPopup.click();
        popup.textBoxSearch.setValue(RandomStringUtils.randomAlphabetic(7));
        popup.buttonSearch.click();

        popup.errorMessage.verify.contains(errorMessage);

        popup.textBoxSearch.setValue(firstName.split(" ")[0]);
        popup.buttonSearch.click();

        popup.errorMessage.verify.present(Boolean.FALSE);
        popup.listboxAvailableItems.setValue(firstName);
        popup.buttonAdd.click();
        CustomAssert.assertEquals(Arrays.asList(firstName), popup.getSelectedItems());
        popup.buttonCancel.click();

        labelAddedItems.verify.present(Boolean.FALSE);

        popup.buttonOpenPopup.click();
        popup.buttonSearch.click();
        popup.addValue(Arrays.asList(firstName, secondName));
        popup.buttonSave.click();

        labelAddedItems.verify.contains(firstName);
        labelAddedItems.verify.contains(secondName);

        popup.buttonOpenPopup.click();
        popup.removeValue(Arrays.asList(secondName));
        popup.buttonSave.click();

        labelAddedItems.verify.value(firstName);
    }

    // 7.2_None_UC_Add/Edit/DeleteSecurityProfile-AuthorityLevels, UC2954359
    private void validateProfileAuthorityLevelsTab() {
        AuthorityLevelsTab authorityLevelsTab = new AuthorityLevelsTab();
        TestData tdProfileAuthorityLevels = tdSpecific.getTestData("ProfileAuthorityLevels");

        AuthorityLevelsTab.buttonNext.click();

        // check controls state
        authorityLevelsTab.verifyFieldIsDisplayed(ProfileMetaData.AuthorityLevelsTab.TYPE.getLabel());
        authorityLevelsTab.verifyFieldIsNotDisplayed(ProfileMetaData.AuthorityLevelsTab.PRODUCT.getLabel());
        authorityLevelsTab.verifyFieldIsNotDisplayed(ProfileMetaData.AuthorityLevelsTab.LEVEL.getLabel());
        authorityLevelsTab.verifyFieldIsDisabled(ProfileMetaData.AuthorityLevelsTab.SAVE_NEW_AUTHORITY.getLabel());

        // add Authority Level for Product
        authorityLevelsTab.fillTab(tdProfileAuthorityLevels.getTestData("Item1"));
        authorityLevelsTab.fillTab(tdProfileAuthorityLevels.getTestData("Item2"));
        authorityLevelsTab.fillTab(tdProfileAuthorityLevels.getTestData("Item3"));

        // check Authority Levels is added
        verifyUserAuthorityLevelsTable(tdProfileAuthorityLevels.getTestData("Item1", "AuthorityLevelsTab"), 1);
        verifyUserAuthorityLevelsTable(tdProfileAuthorityLevels.getTestData("Item2", "AuthorityLevelsTab"), 2);
        verifyUserAuthorityLevelsTable(tdProfileAuthorityLevels.getTestData("Item3", "AuthorityLevelsTab"), 3);

        // Check Authority level can be updated
        AuthorityLevelsTab.tableUserAuthorityLevels.getRow(1).getCell(AdminConstants.AdminUserAuthorityLevelsTable.ACTION).controls.links.get(ActionConstants.EDIT).click();
        authorityLevelsTab.fillTab(tdProfileAuthorityLevels.getTestData("Item1_Update"));
        verifyUserAuthorityLevelsTable(tdProfileAuthorityLevels.getTestData("Item1_Updated", "AuthorityLevelsTab"), 1);

        // Check Authority level can be deleted
        AuthorityLevelsTab.tableUserAuthorityLevels.getRow(2).getCell(AdminConstants.AdminUserAuthorityLevelsTable.ACTION).controls.links.get(ActionConstants.REMOVE).click();
        // cancel delete action
        Page.dialogConfirmation.buttonNo.click();
        // check no deleted
        verifyUserAuthorityLevelsTable(tdProfileAuthorityLevels.getTestData("Item1_Updated", "AuthorityLevelsTab"), 1);
        verifyUserAuthorityLevelsTable(tdProfileAuthorityLevels.getTestData("Item2", "AuthorityLevelsTab"), 2);
        verifyUserAuthorityLevelsTable(tdProfileAuthorityLevels.getTestData("Item3", "AuthorityLevelsTab"), 3);

        AuthorityLevelsTab.tableUserAuthorityLevels.getRow(2).getCell(AdminConstants.AdminUserAuthorityLevelsTable.ACTION).controls.links.get(ActionConstants.REMOVE).click();
        // check confirmation message
        Page.dialogConfirmation.labelMessage.verify.contains("Are you sure you want to delete this Authority Level?");
        // confirm delete action
        Page.dialogConfirmation.buttonYes.click();
        verifyUserAuthorityLevelsTable(tdProfileAuthorityLevels.getTestData("Item1_Updated", "AuthorityLevelsTab"), 1);
        verifyUserAuthorityLevelsTable(tdProfileAuthorityLevels.getTestData("Item3", "AuthorityLevelsTab"), 2);

        // check system Cancels Adding/Editing action
        AuthorityLevelsTab.tableUserAuthorityLevels.getRow(2).getCell(AdminConstants.AdminUserAuthorityLevelsTable.ACTION).controls.links.get(ActionConstants.EDIT).click();
        authorityLevelsTab.fillTab(tdProfileAuthorityLevels.getTestData("Item3_Update")); //press Cancel
        // check no changes
        verifyUserAuthorityLevelsTable(tdProfileAuthorityLevels.getTestData("Item3", "AuthorityLevelsTab"), 2);

        authorityLevelsTab.fillTab(tdProfileAuthorityLevels.getTestData("Item4")); //press Cancel
        // check no added
        AuthorityLevelsTab.tableUserAuthorityLevels.getRow(3).verify.present(false);
        // check controls state
        authorityLevelsTab.verifyFieldIsDisplayed(ProfileMetaData.AuthorityLevelsTab.TYPE.getLabel());
        authorityLevelsTab.verifyFieldIsNotDisplayed(ProfileMetaData.AuthorityLevelsTab.PRODUCT.getLabel());
        authorityLevelsTab.verifyFieldIsNotDisplayed(ProfileMetaData.AuthorityLevelsTab.LEVEL.getLabel());
        authorityLevelsTab.verifyFieldIsDisabled(ProfileMetaData.AuthorityLevelsTab.SAVE_NEW_AUTHORITY.getLabel());

        AuthorityLevelsTab.buttonSave.click();

    }

    private void validateAfterSaveProfileAuthorityLevelsTab() {

        AuthorityLevelsTab.buttonNext.click();

        verifyUserAuthorityLevelsTable(tdSpecific.getTestData("ProfileAuthorityLevels", "Item1_Updated", "AuthorityLevelsTab"), 1);
        verifyUserAuthorityLevelsTable(tdSpecific.getTestData("ProfileAuthorityLevels", "Item3", "AuthorityLevelsTab"), 2);
        AuthorityLevelsTab.tableUserAuthorityLevels.getRow(3).verify.present(false);

    }

    private void verifyUserAuthorityLevelsTable(TestData td, int rowNr) {
        Map<Object, String> itemRow = new HashMap<>();

        itemRow.put("Type", td.getValue("Type"));
        itemRow.put("Product", td.getValue("Product"));
        itemRow.put("Level", td.getValue("Level"));
        itemRow.put("Actions", "Edit Remove");

        AuthorityLevelsTab.tableUserAuthorityLevels.getRow(rowNr).verify.values(itemRow);

    }

    // 7.2_None_UC_Add/EditSecurityProfile-BankingDetails; UC2954361.
    private void validateProfileBankingDetailsTab() {
        BankingDetailsTab bankingDetailsTab = new BankingDetailsTab();

        // check controls state and default value
        bankingDetailsTab.verifyFieldIsDisplayed(ProfileMetaData.BankingDetailsTab.SETTLEMENT_METHOD.getLabel());
        bankingDetailsTab.verifyFieldHasValue(ProfileMetaData.BankingDetailsTab.SETTLEMENT_METHOD.getLabel(), "Check");

        bankingDetailsTab.verifyFieldIsNotDisplayed(ProfileMetaData.BankingDetailsTab.ACCOUNT_HOLDER_NAME.getLabel());
        bankingDetailsTab.verifyFieldIsNotDisplayed(ProfileMetaData.BankingDetailsTab.ACCOUNT_NUMBER.getLabel());
        bankingDetailsTab.verifyFieldIsNotDisplayed(ProfileMetaData.BankingDetailsTab.ABA_TRANSIT_NUMBER.getLabel());
        bankingDetailsTab.verifyFieldIsNotDisplayed(ProfileMetaData.BankingDetailsTab.EFFECTIVE_DATE.getLabel());
        bankingDetailsTab.verifyFieldIsNotDisplayed(ProfileMetaData.BankingDetailsTab.EXPIRATION_DATE.getLabel());
        bankingDetailsTab.verifyFieldIsNotDisplayed(ProfileMetaData.BankingDetailsTab.BANK_NAME.getLabel());

        AuthorityLevelsTab.buttonNext.click();

        //validate Authority Levels Tab is present
        AuthorityLevelsTab.tableDefaultAuthorityLevels.getRow(1).getCell(AdminConstants.AdminDefaultAuthorityLevelsTable.TYPE).verify.contains("Underwriting");

        AuthorityLevelsTab.buttonSave.click();
    }

    private void validateAfterSaveProfileBankingDetailsTab(TestData td) {
        BankingDetailsTab bankingDetailsTab = new BankingDetailsTab();

        AuthorityLevelsTab.buttonNext.click();

        // check controls state and value
        bankingDetailsTab.verifyFieldIsDisplayed(ProfileMetaData.BankingDetailsTab.SETTLEMENT_METHOD.getLabel());
        bankingDetailsTab.verifyFieldHasValue(ProfileMetaData.BankingDetailsTab.SETTLEMENT_METHOD.getLabel(), "Check");

        bankingDetailsTab.verifyFieldIsNotDisplayed(ProfileMetaData.BankingDetailsTab.ACCOUNT_HOLDER_NAME.getLabel());
        bankingDetailsTab.verifyFieldIsNotDisplayed(ProfileMetaData.BankingDetailsTab.ACCOUNT_NUMBER.getLabel());
        bankingDetailsTab.verifyFieldIsNotDisplayed(ProfileMetaData.BankingDetailsTab.ABA_TRANSIT_NUMBER.getLabel());
        bankingDetailsTab.verifyFieldIsNotDisplayed(ProfileMetaData.BankingDetailsTab.EFFECTIVE_DATE.getLabel());
        bankingDetailsTab.verifyFieldIsNotDisplayed(ProfileMetaData.BankingDetailsTab.EXPIRATION_DATE.getLabel());
        bankingDetailsTab.verifyFieldIsNotDisplayed(ProfileMetaData.BankingDetailsTab.BANK_NAME.getLabel());

        //set BankingDetails for EFT method
        bankingDetailsTab.fillTab(td); //"ABA Transit #" is not fills now - depends from EISDEV-160890

        //check fields default values
        bankingDetailsTab.verifyFieldHasValue(ProfileMetaData.BankingDetailsTab.EFFECTIVE_DATE.getLabel(), td.getValue("GeneralProfileTab", "Effective Date"));
        bankingDetailsTab.verifyFieldHasValue(ProfileMetaData.BankingDetailsTab.ACCOUNT_HOLDER_NAME.getLabel(), mapAgencyValues2.get(FIRST_NAME) + "  " + mapAgencyValues2.get(LAST_NAME));

        //check error message
        BankingDetailsTab.buttonNext.click();
        BankingDetailsTab.errorExpirationDate.verify.value("Expiration Date should be equal or later than Effective Date");

        //clear Expiration date field
        bankingDetailsTab.getAssetList().getControl(ProfileMetaData.GeneralProfileTab.EXPIRATION_DATE.getLabel(), TextBox.class).setValue("");

        GeneralProfileTab.buttonUpdate.click();
    }

    private void validateAfterSaveProfileBankingDetailsTabEFT(TestData td) {
        BankingDetailsTab bankingDetailsTab = new BankingDetailsTab();

        BankingDetailsTab.buttonNext.click();

        // check controls value
        bankingDetailsTab.verifyFieldHasValue(ProfileMetaData.BankingDetailsTab.SETTLEMENT_METHOD.getLabel(), td.getValue("BankingDetailsTab", "Settlement Method"));
        bankingDetailsTab.verifyFieldHasValue(ProfileMetaData.BankingDetailsTab.ACCOUNT_NUMBER.getLabel(), td.getValue("BankingDetailsTab", "Account #"));
        //bankingDetailsTab.verifyFieldHasValue(ProfileMetaData.BankingDetailsTab.ABA_TRANSIT_NUMBER.getLabel(), td.getValue("BankingDetailsTab", "ABA Transit #")); //waiting for EISDEV-160890
        bankingDetailsTab.verifyFieldIsDisplayed(ProfileMetaData.BankingDetailsTab.BANK_NAME.getLabel());
        bankingDetailsTab.verifyFieldHasValue(ProfileMetaData.BankingDetailsTab.EFFECTIVE_DATE.getLabel(), td.getValue("GeneralProfileTab", "Effective Date"));
        bankingDetailsTab.verifyFieldHasValue(ProfileMetaData.BankingDetailsTab.EXPIRATION_DATE.getLabel(), "");
        bankingDetailsTab.verifyFieldHasValue(ProfileMetaData.BankingDetailsTab.ACCOUNT_HOLDER_NAME.getLabel(), mapAgencyValues2.get(FIRST_NAME) + " " + mapAgencyValues2.get(LAST_NAME));

        BankingDetailsTab.buttonCancel.click(); //Page.buttonUpdate.click();
    }
}
