package aaa.modules.regression.finance.operational_reports;

import aaa.admin.metadata.security.RoleMetaData;
import aaa.admin.modules.security.role.IRole;
import aaa.admin.modules.security.role.RoleType;
import aaa.admin.pages.security.RolePage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;

public class OperationalReportsBaseTest extends BaseTest {

    protected void searchAndUpdateOrCreateRole(TestData role, RoleType roleType) {
        IRole securityRole = roleType.get();
        TestData tdSecurityRole = testDataManager.securityRole.get(roleType);
        securityRole.navigate();
        securityRole.search(tdSecurityRole.getTestData("SearchData", "TestData")
                .adjust(TestData.makeKeyPath(RoleMetaData.SearchByField.class.getSimpleName(), RoleMetaData.SearchByField.ROLE_NAME.getLabel()), role.getTestData(RoleMetaData.GeneralRoleTab.class.getSimpleName()).getValue(RoleMetaData.GeneralRoleTab.ROLE_NAME.getLabel())));
        if (RolePage.tableRolesSearchResult.getRow(RoleMetaData.GeneralRoleTab.ROLE_NAME.getLabel(), role.getTestData(RoleMetaData.GeneralRoleTab.class.getSimpleName()).getValue(RoleMetaData.GeneralRoleTab.ROLE_NAME.getLabel())).isPresent()) {
            securityRole.update(role);
            return;
        }
        securityRole.create(role);
    }
}
