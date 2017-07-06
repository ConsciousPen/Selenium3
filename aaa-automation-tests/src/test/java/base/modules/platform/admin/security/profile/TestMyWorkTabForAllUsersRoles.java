package base.modules.platform.admin.security.profile;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.testng.annotations.Test;

import aaa.admin.pages.security.RolePage;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.modules.RestBaseTest;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class TestMyWorkTabForAllUsersRoles extends RestBaseTest {

    @Test
    @TestInfo(component = "Platform.Admin", testCaseId = "IPBQA-22297")
    public void testMyWorkAllUsersAccessRoles() {
        List<String> expectedPrivileges =
                Arrays.asList("Add Inbox Sharing", "Assign Other User Task", "Assign Task to Other User",
                        "Assign Task to a Common Work Queue", "Change Automated Task Field Values",
                        "Change Subordinate User Work Status", "Change User Work Status", "Dates Update on Manual Tasks",
                        "MyWork Access", "MyWork Change Task Agency", "MyWork Create Manual Task", "MyWork Services Access", "MyWork Task Inquiry", "MyWork Update Task",
                        "Task Suspend", "User Profile Backup Assignment");

        adminApp().open();
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.SECURITY.get());
        NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.SECURITY_ROLE.get());
        role.search(tdMyWorkRest.getTestData("SecurityAdministratorRole"));
        RolePage.tableRolesSearchResult.getRow(1).getCell(1).controls.links.get(1).click();
        List<String> actualPrivileges = Arrays.asList(RolePage.rolesPrivileges.getValue().split("\\n"));
        CustomAssert.assertTrue(String.format("Role list for \"Security administrator\" does not contains  \"MyWork Services Access\" role."
                + "\n Actual roles: %1$s", actualPrivileges), actualPrivileges.contains("MyWork Services Access"));
        RolePage.buttonReturn.click();
        role.search(tdMyWorkRest.getTestData("MyWorkTaskAndUserRole"));
        RolePage.tableRolesSearchResult.getRow(1).getCell(1).controls.links.get(1).click();
        actualPrivileges = Arrays.asList(RolePage.rolesPrivileges.getValue().split("\\n"));
        CustomAssert.assertTrue(String.format("Role list for \"MyWork and Task User\" is incorrect"
                + "\n Actual list: %1$s"
                + "\n Expected list: %2$s", actualPrivileges, expectedPrivileges), CollectionUtils.disjunction(actualPrivileges, expectedPrivileges).size() == 0);

    }

}
