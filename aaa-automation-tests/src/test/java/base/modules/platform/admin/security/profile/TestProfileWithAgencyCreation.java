/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.platform.admin.security.profile;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import aaa.admin.metadata.agencyvendor.AgencyMetaData;
import aaa.admin.metadata.agencyvendor.AgencyMetaData.AgencyInfoTab;
import aaa.admin.metadata.security.ProfileMetaData;
import aaa.admin.metadata.security.ProfileMetaData.GeneralProfileTab;
import aaa.admin.modules.agencyvendor.AgencyVendorType;
import aaa.admin.modules.agencyvendor.IAgencyVendor;
import aaa.admin.modules.security.profile.IProfile;
import aaa.admin.modules.security.profile.ProfileType;
import aaa.admin.pages.agencyvendor.AgencyVendorPage;
import aaa.admin.pages.security.ProfilePage;
import aaa.common.Tab;
import aaa.main.enums.AdminConstants;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Andrey Shashenka
 * @name Test for Profile with Agency creation
 * @scenario
 * 1. Open Admin app
 * 2. Create Agency
 * 3. Verify that Agency can be searched correctly
 * 4. Create Profile with created Agency
 * 5. Verify that Profile can be searched correctly
 * 6. Verify that it's possible to login as created Profile
 * @details
 */

public class TestProfileWithAgencyCreation extends BaseTest {

    private AgencyVendorType agencyType = AgencyVendorType.AGENCY;
    private IAgencyVendor agency = agencyType.get();
    private TestData tdAgency = testDataManager.agency.get(agencyType);
    private TestData tdAgencyCreate = tdAgency.getTestData("DataGather", "TestData");

    private ProfileType profileType = ProfileType.CORPORATE;
    private IProfile profile = profileType.get();
    private TestData tdProfile = testDataManager.profiles.get(profileType);
    private TestData tdProfileCreate = tdProfile.getTestData("DataGather", "TestData_WithAgency");

    Map<String, String> mapValues = new HashMap<>();

    @Test
    @TestInfo(component = "Platform.Admin")
    public void testProfileWithAgencyCreation() {

        storeValues();

        adminApp().open();

        log.info("TEST: Create Agency");
        agency.create(tdAgencyCreate);

        log.info("TEST: Search and Verify Agency");
        agency.search(tdAgency.getTestData("SearchData", "TestData").adjust(
                TestData.makeKeyPath(AgencyMetaData.SearchByField.class.getSimpleName(),
                        AgencyMetaData.SearchByField.AGENCY_NAME.getLabel()),
                mapValues.get("agency name")));

        AgencyVendorPage.tableAgencies.getRow(1).getCell(AdminConstants.AdminAgenciesTable.AGENCY_NAME).verify.value(mapValues.get("agency name"));

        log.info("TEST: Create Profile");
        profile.create(tdProfileCreate.adjust(TestData.makeKeyPath(GeneralProfileTab.class.getSimpleName(),
                GeneralProfileTab.AGENCY_LOCATIONS.getLabel(), AgencyMetaData.SearchByField.AGENCY_NAME.getLabel()),
                mapValues.get("agency name")));

        log.info("TEST: Search and Verify Profile");
        profile.search(tdProfile.getTestData("SearchData", "TestData").adjust(
                TestData.makeKeyPath(ProfileMetaData.SearchByField.class.getSimpleName(),
                        ProfileMetaData.SearchByField.USER_LOGIN.getLabel()),
                mapValues.get("login")));

        ProfilePage.tableProfileSearchResults.getRow(1).getCell(AdminConstants.AdminProfileSearchResultsTable.FIRST_NAME).verify.value(mapValues.get("first name"));
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(AdminConstants.AdminProfileSearchResultsTable.LAST_NAME).verify.value(mapValues.get("last name"));

        adminApp().close();

        log.info("TEST: Login as created Profile");
        mainApp().open(mapValues.get("login"), mapValues.get("password"));

        Tab.labelLoggedUser.verify.value(String.format("%s %s", mapValues.get("first name"), mapValues.get("last name")));
    }

    private void storeValues() {
        mapValues.put("agency name", tdAgencyCreate.getValue(AgencyInfoTab.class.getSimpleName(),
                AgencyInfoTab.AGENCY_NAME.getLabel()));
        mapValues.put("login", tdProfileCreate.getValue(GeneralProfileTab.class.getSimpleName(),
                GeneralProfileTab.USER_LOGIN.getLabel()));
        mapValues.put("password", tdProfileCreate.getValue(GeneralProfileTab.class.getSimpleName(),
                GeneralProfileTab.PASSWORD.getLabel()));
        mapValues.put("first name", tdProfileCreate.getValue(GeneralProfileTab.class.getSimpleName(),
                GeneralProfileTab.FIRST_NAME.getLabel()));
        mapValues.put("last name", tdProfileCreate.getValue(GeneralProfileTab.class.getSimpleName(),
                GeneralProfileTab.LAST_NAME.getLabel()));
    }

}
