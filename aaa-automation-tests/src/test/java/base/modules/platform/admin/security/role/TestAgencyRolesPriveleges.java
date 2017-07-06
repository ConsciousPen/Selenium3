/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.platform.admin.security.role;

import java.util.Arrays;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.controls.AdvancedSelector;

import aaa.admin.metadata.security.RoleMetaData;
import aaa.admin.modules.security.role.IRole;
import aaa.admin.modules.security.role.RoleType;
import aaa.admin.modules.security.role.defaulttabs.GeneralRoleTab;
import aaa.admin.pages.security.RolePage;
import aaa.main.enums.ActionConstants;
import aaa.main.enums.AdminConstants;
import base.modules.platform.PlatformBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.TextBox;

/**
 * @author N. Belakova
 * @name Test for Add/Edit/Delete Security Roles and Privileges for Business Domain Agency
 * @scenario
 * 1. Initiate Role creation (Business Domain Agency)
 * 2. Check that Channel text field shows correct information
 * 3. Check mandatory field error messages
 * 4. Fill fields
 * 4. Verify add/delete Role's Privileges functionality
 * 5. Save Role
 * 6. Change created Role
 * 7. Check previous entered data are saved
 * 8. Update data in fields and press Cancel
 * 9. Check previous entered data are not saved
 * 10. Update data in fields and press Update
 * 11. Check previous entered data are saved
 * 12. Try to add role with the same name: check message
 * 13. Delete role
 * 14. Check role deleted
 * @details
 */

public class TestAgencyRolesPriveleges extends PlatformBaseTest {

    private RoleType securityRoleType = RoleType.AGENCY;
    private IRole securityRole = securityRoleType.get();

    private String roleName1;
    private TestData tdRole1 = tdSpecific.getTestData("Role1");

    @Test(groups = {"7.2_All_UC_Add/EditSecurityRole", "7.2_All_UC_Add/DeleteSecuirtyRolePrivileges", "7.2_All_UC_DeleteSecurityRole"})
    @TestInfo(component = "Platform.Admin")
    public void testRolesPrivilegesFunctionality() {

        roleName1 = tdRole1.getValue(GeneralRoleTab.class.getSimpleName(), RoleMetaData.GeneralRoleTab.ROLE_NAME.getLabel());

        adminApp().open();

        CustomAssert.enableSoftMode();

        log.info("TEST: Role Tab Validation - Add");
        addRole();

        log.info("TEST: Role Tab Validation - Update");
        updateRole();

        log.info("TEST: Unique Message Validation");
        notUniqueRole();

        log.info("TEST: Delete Role");
        deleteRole();

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    private void addRole() {
        GeneralRoleTab generalRoleTab = new GeneralRoleTab();

        securityRole.initiate();

        generalRoleTab.verifyFieldHasValue(RoleMetaData.GeneralRoleTab.CHANNEL.getLabel(), RoleType.AGENCY.getName());

        GeneralRoleTab.buttonSave.click();

        generalRoleTab.verifyFieldHasMessage(RoleMetaData.GeneralRoleTab.ROLE_NAME.getLabel(), "Name is required");

        generalRoleTab.fillTab(tdRole1);

        //check add/delete Role's Privileges
        validatePrivilegesPopup();

        GeneralRoleTab.buttonSave.click();

    }

    private void notUniqueRole() {
        GeneralRoleTab generalRoleTab = new GeneralRoleTab();

        securityRole.initiate();

        generalRoleTab.verifyFieldHasValue(RoleMetaData.GeneralRoleTab.CHANNEL.getLabel(), RoleType.AGENCY.getName());

        //try to add role with the same name
        generalRoleTab.getAssetList().getControl(RoleMetaData.GeneralRoleTab.ROLE_NAME.getLabel(), TextBox.class).setValue(roleName1);

        GeneralRoleTab.buttonSave.click();

        generalRoleTab.verifyFieldHasMessage(RoleMetaData.GeneralRoleTab.ROLE_NAME.getLabel(), "Authority with this name already exists");

        GeneralRoleTab.buttonCancel.click();

    }

    private void updateRole() {
        GeneralRoleTab generalRoleTab = new GeneralRoleTab();

        RolePage.change(tdSpecific.getTestData("Role1Search").adjust("SearchByField|Role Name", roleName1));

        //check data are saved
        generalRoleTab.verifyFieldHasValue(RoleMetaData.GeneralRoleTab.CHANNEL.getLabel(), RoleType.AGENCY.getName());
        generalRoleTab.verifyFieldHasValue(RoleMetaData.GeneralRoleTab.ROLE_NAME.getLabel(), roleName1);
        generalRoleTab.verifyFieldHasValue(RoleMetaData.GeneralRoleTab.CATEGORY.getLabel(), tdRole1.getValue(GeneralRoleTab.class.getSimpleName(), RoleMetaData.GeneralRoleTab.CATEGORY.getLabel()));
        GeneralRoleTab.labelAddedPriveleges.verify.value("Policy Access");

        generalRoleTab.fillTab(tdSpecific.getTestData("Role1Update"));

        generalRoleTab.verifyFieldHasValue(RoleMetaData.GeneralRoleTab.CATEGORY.getLabel(),
                tdSpecific.getTestData("Role1Update").getValue(GeneralRoleTab.class.getSimpleName(), RoleMetaData.GeneralRoleTab.CATEGORY.getLabel()));
        GeneralRoleTab.labelAddedPriveleges.verify.contains("Policy Access");
        GeneralRoleTab.labelAddedPriveleges.verify.contains("Policy Renewal");

        GeneralRoleTab.buttonCancel.click();

        RolePage.change(tdSpecific.getTestData("Role1Search").adjust("SearchByField|Role Name", roleName1));

        //check data are not updated
        generalRoleTab.verifyFieldHasValue(RoleMetaData.GeneralRoleTab.CATEGORY.getLabel(), tdRole1.getValue(GeneralRoleTab.class.getSimpleName(), RoleMetaData.GeneralRoleTab.CATEGORY.getLabel()));
        GeneralRoleTab.labelAddedPriveleges.verify.value("Policy Access");

        generalRoleTab.fillTab(tdSpecific.getTestData("Role1Update"));

        generalRoleTab.verifyFieldHasValue(RoleMetaData.GeneralRoleTab.CATEGORY.getLabel(),
                tdSpecific.getTestData("Role1Update").getValue(GeneralRoleTab.class.getSimpleName(), RoleMetaData.GeneralRoleTab.CATEGORY.getLabel()));
        GeneralRoleTab.labelAddedPriveleges.verify.contains("Policy Access");
        GeneralRoleTab.labelAddedPriveleges.verify.contains("Policy Renewal");

        GeneralRoleTab.buttonUpdate.click();

        RolePage.change(tdSpecific.getTestData("Role1Search").adjust("SearchByField|Role Name", roleName1));

        //check data are updated
        generalRoleTab.verifyFieldHasValue(RoleMetaData.GeneralRoleTab.CATEGORY.getLabel(),
                tdSpecific.getTestData("Role1Update").getValue(GeneralRoleTab.class.getSimpleName(), RoleMetaData.GeneralRoleTab.CATEGORY.getLabel()));
        GeneralRoleTab.labelAddedPriveleges.verify.contains("Policy Access");
        GeneralRoleTab.labelAddedPriveleges.verify.contains("Policy Renewal");

        GeneralRoleTab.buttonCancel.click();

    }

    private void deleteRole() {

        RolePage.search(tdSpecific.getTestData("Role1Search").adjust("SearchByField|Role Name", roleName1));

        //delete confirmation - no
        RolePage.tableRolesSearchResult.getRow(1).getCell(AdminConstants.AdminAgencySearchResultTable.ACTION).controls.links.get(ActionConstants.DELETE).click();
        RolePage.dialogRemoveRoleConfirmation.labelMessage.verify.contains("Do you really want to delete this role?");
        RolePage.dialogRemoveRoleConfirmation.buttonNo.click();

        RolePage.search(tdSpecific.getTestData("Role1Search").adjust("SearchByField|Role Name", roleName1));

        //delete confirmation - yes
        RolePage.tableRolesSearchResult.getRow(1).getCell(AdminConstants.AdminAgencySearchResultTable.ACTION).controls.links.get(ActionConstants.DELETE).click();
        RolePage.dialogRemoveRoleConfirmation.labelMessage.verify.contains("Do you really want to delete this role?");
        RolePage.dialogRemoveRoleConfirmation.buttonYes.click();

        //check role deleted
        RolePage.search(tdSpecific.getTestData("Role1Search").adjust("SearchByField|Role Name", roleName1));
        RolePage.labelItemNotFound.verify.contains("Search Item not Found");

    }

    private void validatePrivilegesPopup() {
        GeneralRoleTab generalRoleTab = new GeneralRoleTab();
        AdvancedSelector popup = generalRoleTab.getAssetList().getControl(RoleMetaData.GeneralRoleTab.PRIVILEGES.getLabel(), AdvancedSelector.class);

        popup.buttonOpenPopup.click();
        popup.textBoxSearch.setValue(RandomStringUtils.randomAlphabetic(7));
        popup.buttonSearch.click();

        popup.errorMessage.verify.contains("No Privileges Found");

        popup.textBoxSearch.setValue("Policy");
        popup.buttonSearch.click();

        popup.errorMessage.verify.present(Boolean.FALSE);
        popup.listboxAvailableItems.setValue("Policy Inquiry");
        popup.buttonAdd.click();
        CustomAssert.assertEquals(Arrays.asList("Policy Inquiry"), popup.getSelectedItems());
        popup.buttonCancel.click();

        GeneralRoleTab.labelAddedPriveleges.verify.present(Boolean.FALSE);

        popup.buttonOpenPopup.click();
        popup.buttonSearch.click();
        popup.addValue(Arrays.asList("Policy Access", "Policy Issue"));
        popup.buttonSave.click();

        GeneralRoleTab.labelAddedPriveleges.verify.contains("Policy Access");
        GeneralRoleTab.labelAddedPriveleges.verify.contains("Policy Issue");

        popup.buttonOpenPopup.click();
        popup.removeValue(Arrays.asList("Policy Issue"));
        popup.buttonSave.click();

        GeneralRoleTab.labelAddedPriveleges.verify.value("Policy Access");

    }

}
