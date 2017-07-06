/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.platform.admin.security.profile;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import aaa.admin.metadata.security.ProfileMetaData;
import aaa.admin.metadata.security.ProfileMetaData.GeneralProfileTab;
import aaa.admin.modules.security.ChannelType;
import aaa.admin.modules.security.profile.IProfile;
import aaa.admin.modules.security.profile.ProfileType;
import aaa.admin.pages.security.ProfilePage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Deivydas Piliukaitis
 * @name Test for Profile Search
 * @scenario
 * 1. Create Profile with channel Corporate
 * 2. Create Profile with channel Agency
 * 3. Create Profile with channel Agency Non EIS User
 * 4. Create Profile with channel Vendor
 * 5. Verify that created profiles can be searched correctly
 * @details
 */

public class TestProfileSearch extends BaseTest {

    private String corpoarteAgencyName = "QA Agency";
    private String agencyName = "Broker1Name";

    private String LOGIN = ProfileMetaData.SearchByField.USER_LOGIN.getLabel();
    private String FIRST_NAME = ProfileMetaData.SearchByField.FIRST_NAME.getLabel();
    private String LAST_NAME = ProfileMetaData.SearchByField.LAST_NAME.getLabel();
    private String CHANNEL = ProfileMetaData.SearchByField.CHANNEL.getLabel();
    private String PROFILE_TYPE = ProfileMetaData.SearchByField.PROFILE_TYPE.getLabel();
    private String EIS_USER = "EIS User";
    private String NON_EIS_USER = "Non-EIS User";
    private String ALL = "All";

    private ProfileType corporateProfileType = ProfileType.CORPORATE;
    private IProfile corporateProfile = corporateProfileType.get();
    private TestData tdCoporateProfile = testDataManager.profiles.get(corporateProfileType);
    private TestData tdCoporateProfileCreate = tdCoporateProfile.getTestData("DataGather", "TestData");
    private Map<String, String> mapCorporateValues = new HashMap<>();

    private ProfileType agencyProfileType = ProfileType.AGENCY;
    private IProfile agencyProfile = agencyProfileType.get();
    private TestData tdAgencyProfile = testDataManager.profiles.get(agencyProfileType);
    private TestData tdAgencyProfileCreate = tdAgencyProfile.getTestData("DataGather", "TestData");
    private Map<String, String> mapAgencyValues = new HashMap<>();
    private TestData tdAgencyProfileNotEisUserCreate = tdAgencyProfile.getTestData("DataGather", "TestData_NotEisUser");
    private Map<String, String> mapAgencyNonEisUserValues = new HashMap<>();

    private ProfileType vendorProfileType = ProfileType.VENDOR;
    private IProfile vendorProfile = vendorProfileType.get();
    private TestData tdVendorProfile = testDataManager.profiles.get(vendorProfileType);
    private TestData tdVendorProfileCreate = tdVendorProfile.getTestData("DataGather", "TestData");
    private Map<String, String> mapVendorValues = new HashMap<>();

    @Test(groups = "6.2.2_All_LoginSearchCriteria")
    @TestInfo(component = "Platform.Admin")
    public void testProfileSearch() {

        storeValues(mapCorporateValues, tdCoporateProfileCreate);
        storeValues(mapAgencyValues, tdAgencyProfileCreate);
        storeValues(mapAgencyNonEisUserValues, tdAgencyProfileNotEisUserCreate);
        storeValues(mapVendorValues, tdVendorProfileCreate);

        adminApp().open();

        log.info("TEST: Start Creating Corporate Profile");
        corporateProfile.create(tdCoporateProfileCreate, corpoarteAgencyName);
        log.info("TEST: Start Creating Agency Profile");
        agencyProfile.create(tdAgencyProfileCreate, agencyName);
        log.info("TEST: Start Creating Agency Profile Not Eis User");
        agencyProfile.createNonEisUser(tdAgencyProfileNotEisUserCreate);
        log.info("TEST: Start Creating Vendor Profile");
        vendorProfile.create(tdVendorProfileCreate);

        log.info("TEST: Profile Search Validation");
        CustomAssert.enableSoftMode();
        userLoginValidation();
        firstNameValidation();
        lastNameValidation();
        channelValidation();
        profileTypeValidation();
        CustomAssert.disableSoftMode();
    }

    private void storeValues(Map<String, String> mapValues, TestData tdProfileCreate) {
        mapValues.put(LOGIN, tdProfileCreate.getValue(GeneralProfileTab.class.getSimpleName(),
                GeneralProfileTab.USER_LOGIN.getLabel()));
        mapValues.put(FIRST_NAME, tdProfileCreate.getValue(GeneralProfileTab.class.getSimpleName(),
                GeneralProfileTab.FIRST_NAME.getLabel()));
        mapValues.put(LAST_NAME, tdProfileCreate.getValue(GeneralProfileTab.class.getSimpleName(),
                GeneralProfileTab.LAST_NAME.getLabel()));
    }

    private void userLoginValidation() {
        ProfilePage.buttonClear.click();
        ProfilePage.search(ProfilePage.getSearchTestData(LOGIN, mapCorporateValues.get(LOGIN)));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(FIRST_NAME).verify.value(mapCorporateValues.get(FIRST_NAME));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(LAST_NAME).verify.value(mapCorporateValues.get(LAST_NAME));
        CustomAssert.assertEquals(1, ProfilePage.tableProfileSearchResults.getRowsCount());

        ProfilePage.buttonClear.click();
        ProfilePage.search(ProfilePage.getSearchTestData(LOGIN, mapAgencyValues.get(LOGIN)));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(FIRST_NAME).verify.value(mapAgencyValues.get(FIRST_NAME));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(LAST_NAME).verify.value(mapAgencyValues.get(LAST_NAME));
        CustomAssert.assertEquals(1, ProfilePage.tableProfileSearchResults.getRowsCount());

        ProfilePage.buttonClear.click();
        ProfilePage.search(ProfilePage.getSearchTestData(LOGIN, mapVendorValues.get(LOGIN)));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(FIRST_NAME).verify.value(mapVendorValues.get(FIRST_NAME));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(LAST_NAME).verify.value(mapVendorValues.get(LAST_NAME));
        CustomAssert.assertEquals(1, ProfilePage.tableProfileSearchResults.getRowsCount());
    }

    private void firstNameValidation() {
        ProfilePage.buttonClear.click();
        ProfilePage.search(ProfilePage.getSearchTestData(FIRST_NAME, mapCorporateValues.get(FIRST_NAME)));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(LOGIN).verify.value(mapCorporateValues.get(LOGIN));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(LAST_NAME).verify.value(mapCorporateValues.get(LAST_NAME));
        CustomAssert.assertEquals(1, ProfilePage.tableProfileSearchResults.getRowsCount());

        ProfilePage.buttonClear.click();
        ProfilePage.search(ProfilePage.getSearchTestData(FIRST_NAME, mapAgencyValues.get(FIRST_NAME)));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(LOGIN).verify.value(mapAgencyValues.get(LOGIN));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(LAST_NAME).verify.value(mapAgencyValues.get(LAST_NAME));
        CustomAssert.assertEquals(1, ProfilePage.tableProfileSearchResults.getRowsCount());

        ProfilePage.buttonClear.click();
        ProfilePage.search(ProfilePage.getSearchTestData(FIRST_NAME, mapVendorValues.get(FIRST_NAME)));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(LOGIN).verify.value(mapVendorValues.get(LOGIN));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(LAST_NAME).verify.value(mapVendorValues.get(LAST_NAME));
        CustomAssert.assertEquals(1, ProfilePage.tableProfileSearchResults.getRowsCount());
    }

    private void lastNameValidation() {
        ProfilePage.buttonClear.click();
        ProfilePage.search(ProfilePage.getSearchTestData(LAST_NAME, mapCorporateValues.get(LAST_NAME)));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(LOGIN).verify.value(mapCorporateValues.get(LOGIN));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(FIRST_NAME).verify.value(mapCorporateValues.get(FIRST_NAME));
        CustomAssert.assertEquals(1, ProfilePage.tableProfileSearchResults.getRowsCount());

        ProfilePage.buttonClear.click();
        ProfilePage.search(ProfilePage.getSearchTestData(LAST_NAME, mapAgencyValues.get(LAST_NAME)));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(LOGIN).verify.value(mapAgencyValues.get(LOGIN));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(FIRST_NAME).verify.value(mapAgencyValues.get(FIRST_NAME));
        CustomAssert.assertEquals(1, ProfilePage.tableProfileSearchResults.getRowsCount());

        ProfilePage.buttonClear.click();
        ProfilePage.search(ProfilePage.getSearchTestData(LAST_NAME, mapVendorValues.get(LAST_NAME)));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(LOGIN).verify.value(mapVendorValues.get(LOGIN));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(FIRST_NAME).verify.value(mapVendorValues.get(FIRST_NAME));
        CustomAssert.assertEquals(1, ProfilePage.tableProfileSearchResults.getRowsCount());
    }

    private void channelValidation() {
        ProfilePage.buttonClear.click();
        ProfilePage.search(ProfilePage.getSearchTestData(new SimpleDataProvider().adjust(FIRST_NAME, mapCorporateValues.get(FIRST_NAME)).adjust(CHANNEL,
                ChannelType.CORPORATE.getName())));
        ProfilePage.tableProfileSearchResults.getRow(LOGIN, mapCorporateValues.get(LOGIN)).verify.present();
        ProfilePage.tableProfileSearchResults.getRow(FIRST_NAME, mapCorporateValues.get(FIRST_NAME)).verify.present();
        ProfilePage.search(ProfilePage.getSearchTestData(FIRST_NAME, mapAgencyValues.get(FIRST_NAME)));
        CustomAssert.assertFalse(ProfilePage.isProfileFound());
        ProfilePage.search(ProfilePage.getSearchTestData(FIRST_NAME, mapVendorValues.get(FIRST_NAME)));
        CustomAssert.assertFalse(ProfilePage.isProfileFound());

        ProfilePage.buttonClear.click();
        ProfilePage.search(ProfilePage.getSearchTestData(new SimpleDataProvider().adjust(FIRST_NAME, mapAgencyValues.get(FIRST_NAME)).adjust(CHANNEL, ChannelType.AGENCY.getName())));
        ProfilePage.tableProfileSearchResults.getRow(LOGIN, mapAgencyValues.get(LOGIN)).verify.present();
        ProfilePage.tableProfileSearchResults.getRow(FIRST_NAME, mapAgencyValues.get(FIRST_NAME)).verify.present();
        ProfilePage.search(ProfilePage.getSearchTestData(FIRST_NAME, mapCorporateValues.get(FIRST_NAME)));
        CustomAssert.assertFalse(ProfilePage.isProfileFound());
        ProfilePage.search(ProfilePage.getSearchTestData(FIRST_NAME, mapVendorValues.get(FIRST_NAME)));
        CustomAssert.assertFalse(ProfilePage.isProfileFound());

        ProfilePage.buttonClear.click();
        ProfilePage.search(ProfilePage.getSearchTestData(new SimpleDataProvider().adjust(FIRST_NAME, mapVendorValues.get(FIRST_NAME)).adjust(CHANNEL, ChannelType.VENDOR.getName())));
        ProfilePage.tableProfileSearchResults.getRow(LOGIN, mapVendorValues.get(LOGIN)).verify.present();
        ProfilePage.tableProfileSearchResults.getRow(FIRST_NAME, mapVendorValues.get(FIRST_NAME)).verify.present();
        ProfilePage.search(ProfilePage.getSearchTestData(FIRST_NAME, mapAgencyValues.get(FIRST_NAME)));
        CustomAssert.assertFalse(ProfilePage.isProfileFound());
        ProfilePage.search(ProfilePage.getSearchTestData(FIRST_NAME, mapCorporateValues.get(FIRST_NAME)));
        CustomAssert.assertFalse(ProfilePage.isProfileFound());
    }

    private void profileTypeValidation() {
        ProfilePage.buttonClear.click();
        ProfilePage.search(ProfilePage.getSearchTestData(new SimpleDataProvider().adjust(FIRST_NAME, mapCorporateValues.get(FIRST_NAME)).adjust(PROFILE_TYPE,
                NON_EIS_USER)));
        ProfilePage.assetListSearchForm.getControl(LOGIN).verify.enabled(false);
        CustomAssert.assertFalse(ProfilePage.isProfileFound());
        ProfilePage.search(ProfilePage.getSearchTestData(FIRST_NAME, mapAgencyValues.get(FIRST_NAME)));
        CustomAssert.assertFalse(ProfilePage.isProfileFound());
        ProfilePage.search(ProfilePage.getSearchTestData(FIRST_NAME, mapVendorValues.get(FIRST_NAME)));
        CustomAssert.assertFalse(ProfilePage.isProfileFound());
        ProfilePage.search(ProfilePage.getSearchTestData(FIRST_NAME, mapAgencyNonEisUserValues.get(FIRST_NAME)));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(LAST_NAME).verify.value(mapAgencyNonEisUserValues.get(LAST_NAME));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(FIRST_NAME).verify.value(mapAgencyNonEisUserValues.get(FIRST_NAME));

        ProfilePage.search(ProfilePage.getSearchTestData(PROFILE_TYPE, EIS_USER));
        ProfilePage.assetListSearchForm.getControl(LOGIN).verify.enabled();
        ProfilePage.search(ProfilePage.getSearchTestData(FIRST_NAME, mapCorporateValues.get(FIRST_NAME)));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(LAST_NAME).verify.value(mapCorporateValues.get(LAST_NAME));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(LOGIN).verify.value(mapCorporateValues.get(LOGIN));
        ProfilePage.search(ProfilePage.getSearchTestData(FIRST_NAME, mapAgencyValues.get(FIRST_NAME)));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(LAST_NAME).verify.value(mapAgencyValues.get(LAST_NAME));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(LOGIN).verify.value(mapAgencyValues.get(LOGIN));
        ProfilePage.search(ProfilePage.getSearchTestData(FIRST_NAME, mapVendorValues.get(FIRST_NAME)));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(LAST_NAME).verify.value(mapVendorValues.get(LAST_NAME));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(LOGIN).verify.value(mapVendorValues.get(LOGIN));
        ProfilePage.search(ProfilePage.getSearchTestData(FIRST_NAME, mapAgencyNonEisUserValues.get(FIRST_NAME)));
        CustomAssert.assertFalse(ProfilePage.isProfileFound());

        ProfilePage.search(ProfilePage.getSearchTestData(PROFILE_TYPE, ALL));
        ProfilePage.assetListSearchForm.getControl(LOGIN).verify.enabled();
        ProfilePage.search(ProfilePage.getSearchTestData(FIRST_NAME, mapCorporateValues.get(FIRST_NAME)));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(LAST_NAME).verify.value(mapCorporateValues.get(LAST_NAME));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(LOGIN).verify.value(mapCorporateValues.get(LOGIN));
        ProfilePage.search(ProfilePage.getSearchTestData(FIRST_NAME, mapAgencyValues.get(FIRST_NAME)));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(LAST_NAME).verify.value(mapAgencyValues.get(LAST_NAME));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(LOGIN).verify.value(mapAgencyValues.get(LOGIN));
        ProfilePage.search(ProfilePage.getSearchTestData(FIRST_NAME, mapVendorValues.get(FIRST_NAME)));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(LAST_NAME).verify.value(mapVendorValues.get(LAST_NAME));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(LOGIN).verify.value(mapVendorValues.get(LOGIN));
        ProfilePage.search(ProfilePage.getSearchTestData(FIRST_NAME, mapAgencyNonEisUserValues.get(FIRST_NAME)));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(LAST_NAME).verify.value(mapAgencyNonEisUserValues.get(LAST_NAME));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(FIRST_NAME).verify.value(mapAgencyNonEisUserValues.get(FIRST_NAME));
    }

}
