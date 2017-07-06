/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.platform.admin.security.role;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import aaa.admin.metadata.security.RoleMetaData;
import aaa.admin.modules.security.role.IRole;
import aaa.admin.modules.security.role.RoleType;
import aaa.admin.modules.security.role.defaulttabs.GeneralRoleTab;
import aaa.admin.pages.security.RolePage;
import aaa.main.enums.AdminConstants;
import base.modules.platform.PlatformBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.TextBox;

/**
 * @author N. Belakova
 * @name Test for Search/View Security Role
 * @scenario
 * 1. Create 3 Roles for search
 * 2. Search by Part of Role Name, Domain empty
 * 3. Check search result: 3 Roles found and displayed in alphabetical order
 * 4. Press Clear button
 * 5. Check all fields is cleared out
 * 6. Search by Part of Role Name and Domain
 * 7. Check search result: 1 Role found
 * 8. Search by Role Name and Domain is not corresponding
 * 9. Check search result: No Role found
 * 10. Search by incorrect Role Name, Domain empty
 * 11. Check search result: No Role found
 * 12. Search by Role Name, Domain empty
 * 13. Inquiry role: check fields values
 * 14. Delete created roles
 * @details
 */

public class TestSearchRoles extends PlatformBaseTest {

    private RoleType securityRoleAgencyType = RoleType.AGENCY;
    private IRole securityAgencyRole = securityRoleAgencyType.get();
    private RoleType securityRoleVendorType = RoleType.VENDOR;
    private IRole securityVendorRole = securityRoleVendorType.get();
    private RoleType securityRoleCorporateType = RoleType.CORPORATE;
    private IRole securityCorporateRole = securityRoleCorporateType.get();

    private TestData tdRoleAgency = tdSpecific.getTestData("RolesAgencyDG");
    private TestData tdRoleVendor = tdSpecific.getTestData("RolesVendorDG");
    private TestData tdRoleCorporate = tdSpecific.getTestData("RolesCorporateDG");
    private Map<String, String> mapRoleNames = new HashMap<>();

    @Test(groups = {"7.2_All_UC_Search/ViewSecurityRole"})
    //UC2954369
    @TestInfo(component = "Platform.Admin")
    public void testRolesPrivilegesFunctionality() {

        //store roles names
        mapRoleNames.put("RoleAgency", tdRoleAgency.getValue(GeneralRoleTab.class.getSimpleName(), RoleMetaData.GeneralRoleTab.ROLE_NAME.getLabel()));
        mapRoleNames.put("RoleVendor", tdRoleVendor.getValue(GeneralRoleTab.class.getSimpleName(), RoleMetaData.GeneralRoleTab.ROLE_NAME.getLabel()));
        mapRoleNames.put("RoleCorporate", tdRoleCorporate.getValue(GeneralRoleTab.class.getSimpleName(), RoleMetaData.GeneralRoleTab.ROLE_NAME.getLabel()));

        adminApp().open();

        //create roles
        securityAgencyRole.create(tdRoleAgency);
        securityVendorRole.create(tdRoleVendor);
        securityCorporateRole.create(tdRoleCorporate);

        CustomAssert.enableSoftMode();

        log.info("TEST: Search by Part of Role Name, Domain empty");
        RolePage.search(tdSpecific.getTestData("RolesSearch"));
        //check results: 3 Roles found and displayed in alphabetical order (not expected that any others roles with name starts 'RoleForSearchX' are in system)
        RolePage.tableRolesSearchResult.getColumn(AdminConstants.AdminRolesSearchResultTable.ROLE_NAME).getCell(1).verify.value(mapRoleNames.get("RoleAgency"));
        RolePage.tableRolesSearchResult.getColumn(AdminConstants.AdminRolesSearchResultTable.ROLE_NAME).getCell(2).verify.value(mapRoleNames.get("RoleCorporate"));
        RolePage.tableRolesSearchResult.getColumn(AdminConstants.AdminRolesSearchResultTable.ROLE_NAME).getCell(3).verify.value(mapRoleNames.get("RoleVendor"));
        CustomAssert.assertEquals(3, RolePage.tableRolesSearchResult.getRowsCount());

        //check Clear
        RolePage.buttonClear.click();
        RolePage.assetListSearchForm.getControl(RoleMetaData.SearchByField.ROLE_NAME.getLabel(), TextBox.class).verify.value("");
        CustomAssert.assertEquals("", RolePage.assetListSearchForm.getControl(RoleMetaData.SearchByField.BUSINESS_DOMAIN.getLabel()).getValue());
        CustomAssert.assertFalse("Roles Search Result table is present", RolePage.tableRolesSearchResult.isPresent());

        log.info("TEST: Search by Part of Role Name and Domain");
        RolePage.search(tdSpecific.getTestData("RolesVendorSearch"));
        //check results: 1 Role found
        RolePage.tableRolesSearchResult.getColumn(AdminConstants.AdminRolesSearchResultTable.ROLE_NAME).getCell(1).verify.value(mapRoleNames.get("RoleVendor"));
        CustomAssert.assertEquals(1, RolePage.tableRolesSearchResult.getRowsCount());

        log.info("TEST: Search by Role Name and Domain is not corresponding");
        RolePage.search(tdSpecific.getTestData("RolesDomainSearchNF").adjust("SearchByField|Role Name", mapRoleNames.get("RoleCorporate")));
        //check results: No Role found
        RolePage.labelItemNotFound.verify.contains("Search Item not Found");

        log.info("TEST: Search by incorrect Role Name, Domain empty");
        RolePage.search(tdSpecific.getTestData("RolesNameSearchNF"));
        //check results: No Role found
        RolePage.labelItemNotFound.verify.contains("Search Item not Found");

        log.info("TEST: Search by Role Name, Domain empty - Inquiry Role");
        RolePage.inquiry(tdSpecific.getTestData("RolesSearch").adjust("SearchByField|Role Name", mapRoleNames.get("RoleVendor")));
        //check fields values
        new GeneralRoleTab().verifyFieldHasValue(RoleMetaData.GeneralRoleTab.CHANNEL.getLabel(), RoleType.VENDOR.getName());
        GeneralRoleTab.labelRoleNameInquiry.verify.contains(mapRoleNames.get("RoleVendor"));
        GeneralRoleTab.labelAddedPriveleges.verify.contains("Policy Issue");
        GeneralRoleTab.labelAddedPriveleges.verify.contains("Policy Renewal");
        GeneralRoleTab.buttonReturn.click();

        log.info("TEST: Delete all created roles");
        RolePage.delete(tdSpecific.getTestData("RolesSearch").adjust("SearchByField|Role Name", mapRoleNames.get("RoleVendor")));
        RolePage.delete(tdSpecific.getTestData("RolesSearch").adjust("SearchByField|Role Name", mapRoleNames.get("RoleAgency")));
        RolePage.delete(tdSpecific.getTestData("RolesSearch").adjust("SearchByField|Role Name", mapRoleNames.get("RoleCorporate")));

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

}
