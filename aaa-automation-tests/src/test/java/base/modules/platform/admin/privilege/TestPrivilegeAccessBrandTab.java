/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.platform.admin.privilege;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import aaa.admin.metadata.security.ProfileMetaData.GeneralProfileTab;
import aaa.admin.metadata.security.RoleMetaData.GeneralRoleTab;
import aaa.admin.modules.security.PrivilegesEnum;
import aaa.admin.modules.security.profile.IProfile;
import aaa.admin.modules.security.profile.ProfileType;
import aaa.admin.modules.security.role.IRole;
import aaa.admin.modules.security.role.RoleType;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.ErrorPage;
import aaa.common.pages.NavigationPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Rokas Lazdauskas
 * @name Test 'SecurityAdmin Brands Tab Access' privilege
 * @scenario
 * 1. Create Role with no "SecurityAdmin Brands Tab Access" Privilege
 * 2. Create Profile with roles from step 1.
 * 3. Login with user from step 2.
 * 4. User navigates to Admin Agency/Vendor > Brand
 * 5. Checks that Error table is shown with "Access Denied" message
 * @details
 */
public class TestPrivilegeAccessBrandTab extends BaseTest {

    private RoleType securityRoleType = RoleType.CORPORATE;
    private TestData tdSecurityRole = testDataManager.securityRole.get(securityRoleType);
    private IRole securityRole = securityRoleType.get();

    private ProfileType profileType = ProfileType.CORPORATE;
    private TestData tdSecurityProfile = testDataManager.profiles.get(profileType);
    private IProfile securityProfile = profileType.get();

    Map<String, String> mapValues = new HashMap<>();

    @Test(groups = {"7.2_All_UC_AccessBrandTab"})
    @TestInfo(component = "Platform.Admin")
    public void testPrivilegeNoAccessBrandTab() {
        adminApp().open();

        List<String> privileges = new ArrayList<>();
        privileges.add("ALL");
        privileges.add("EXCLUDE " + PrivilegesEnum.PLATFORM_ADMIN_BRANDS_TAB_ACCESS.get());

        String roleName = tdSecurityRole.getValue("DataGather", "TestData", GeneralRoleTab.class.getSimpleName(), GeneralRoleTab.ROLE_NAME.getLabel());

        securityRole.create(tdSecurityRole.getTestData("DataGather", "TestData").adjust(TestData.makeKeyPath(
                GeneralRoleTab.class.getSimpleName(), GeneralRoleTab.PRIVILEGES.getLabel()), privileges));

        String userLogin = tdSecurityProfile.getValue("DataGather", "TestData", GeneralProfileTab.class.getSimpleName(), GeneralProfileTab.USER_LOGIN.getLabel());
        String userPassword = tdSecurityProfile.getValue("DataGather", "TestData", GeneralProfileTab.class.getSimpleName(), GeneralProfileTab.PASSWORD.getLabel());

        securityProfile.create(tdSecurityProfile.getTestData("DataGather", "TestData").adjust(TestData.makeKeyPath(
                GeneralProfileTab.class.getSimpleName(), GeneralProfileTab.ROLES.getLabel()), roleName));

        adminApp().reopen(userLogin, userPassword);

        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.AGENCY_VENDOR.get());
        NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.AGENCY_VENDOR_BRAND.get());

        ErrorPage.tableError.verify.present("Access denied");
    }
}
