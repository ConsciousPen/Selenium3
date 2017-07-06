/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.platform.admin.privilege;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import aaa.admin.metadata.security.ProfileMetaData;
import aaa.admin.metadata.security.ProfileMetaData.GeneralProfileTab;
import aaa.admin.metadata.security.RoleMetaData.GeneralRoleTab;
import aaa.admin.modules.security.PrivilegesEnum;
import aaa.admin.modules.security.profile.IProfile;
import aaa.admin.modules.security.profile.ProfileType;
import aaa.admin.modules.security.role.IRole;
import aaa.admin.modules.security.role.RoleType;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.composite.assets.AbstractContainer;

/**
 * @author Deivydas Piliukaitis
 * @name Test 'SecurityAdmin Profile Disable User' privilege
 * @scenario
 * 1. Create Role without "SecurityAdmin Profile Disable User" Privilege
 * 2. Create Profile with roles from step 1.
 * 3. Login with user from step 2.
 * 4. User navigates to Admin > Security > Profile and initiate profile creation
 * 5. Checks that User Work Status radio group option "Disabled" cannot be selected
 * @details
 */
public class TestPrivilegeProfileDisableUser extends BaseTest {

    private RoleType securityRoleType = RoleType.CORPORATE;
    private TestData tdSecurityRole = testDataManager.securityRole.get(securityRoleType);
    private IRole securityRole = securityRoleType.get();

    private ProfileType profileType = ProfileType.CORPORATE;
    private TestData tdSecurityProfile = testDataManager.profiles.get(profileType);
    private IProfile securityProfile = profileType.get();

    Map<String, String> mapValues = new HashMap<>();

    @Test(groups = {"6.2.2_All_Add/EditUserSpecificInformation"})
    @TestInfo(component = "Platform.Admin")
    public void testPrivilegeNoAccessBrandTab() {
        mainApp().open();

        List<String> privileges = new ArrayList<>();
        privileges.add("ALL");
        privileges.add("EXCLUDE " + PrivilegesEnum.PLATFORM_ADMIN_PROFILE_DISABLE_USER.get());

        String roleName = tdSecurityRole.getValue("DataGather", "TestData", GeneralRoleTab.class.getSimpleName(), GeneralRoleTab.ROLE_NAME.getLabel());

        securityRole.create(tdSecurityRole.getTestData("DataGather", "TestData").adjust(TestData.makeKeyPath(
                GeneralRoleTab.class.getSimpleName(), GeneralRoleTab.PRIVILEGES.getLabel()), privileges));

        String userLogin = tdSecurityProfile.getValue("DataGather", "TestData", GeneralProfileTab.class.getSimpleName(), GeneralProfileTab.USER_LOGIN.getLabel());
        String userPassword = tdSecurityProfile.getValue("DataGather", "TestData", GeneralProfileTab.class.getSimpleName(), GeneralProfileTab.PASSWORD.getLabel());

        securityProfile.create(tdSecurityProfile.getTestData("DataGather", "TestData").adjust(TestData.makeKeyPath(
                GeneralProfileTab.class.getSimpleName(), GeneralProfileTab.ROLES.getLabel()), roleName));

        adminApp().reopen(userLogin, userPassword);

        securityProfile.initiate();

        AbstractContainer<?, ?> assetList = new aaa.admin.modules.security.profile.defaulttabs.GeneralProfileTab().getAssetList();
        CustomAssert.assertFalse("User Work Status option 'Disabled' should be disabled for user without 'SecurityAdmin Profile Disable User' privilage",
                assetList.getControl(ProfileMetaData.GeneralProfileTab.USER_WORK_STATUS.getLabel(), RadioGroup.class).getWebElement()
                        .findElement(By.xpath("//td[label[contains(.,'Disabled')]]/input"))
                        .isEnabled());
    }
}
