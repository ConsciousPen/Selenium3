package aaa.modules.regression.finance.operational_reports;

import aaa.admin.metadata.security.RoleMetaData;
import aaa.admin.modules.reports.operationalreports.OperationalReport;
import aaa.admin.modules.security.role.IRole;
import aaa.admin.modules.security.role.RoleType;
import aaa.admin.modules.security.role.defaulttabs.GeneralRoleTab;
import aaa.admin.pages.security.RolePage;
import aaa.main.enums.ActionConstants;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OperationalReportsBaseTest extends BaseTest {

    protected OperationalReport operationalReport = new OperationalReport();

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

    protected void verifyRole(TestData role, RoleType roleType, List<String> roleNames) {
        IRole securityRole = roleType.get();
        TestData tdSecurityRole = testDataManager.securityRole.get(roleType);
        securityRole.navigate();
        securityRole.search(tdSecurityRole.getTestData("SearchData", "TestData")
                .adjust(TestData.makeKeyPath(RoleMetaData.SearchByField.class.getSimpleName(), RoleMetaData.SearchByField.ROLE_NAME.getLabel()), role.getTestData(RoleMetaData.GeneralRoleTab.class.getSimpleName()).getValue(RoleMetaData.GeneralRoleTab.ROLE_NAME.getLabel())));
        RolePage.tableRolesSearchResult.getRow(1).getCell(3).controls.links.get(ActionConstants.CHANGE).click();
        assertThat(new GeneralRoleTab().getAssetList().getAsset(RoleMetaData.GeneralRoleTab.ROLES_PRIVILEGES).getValue()).contains(roleNames);
    }
}
