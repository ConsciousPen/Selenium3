package aaa.modules.regression.finance.operational_reports;

import aaa.admin.modules.security.role.RoleType;
import org.testng.annotations.Test;

public class PreconditionConfigurationChanges extends OperationalReportsBaseTest {

    @Test
    public void updateRole() {
        adminApp().open();
        searchAndUpdateOrCreateRole(
                testDataManager.securityRole.get(RoleType.CORPORATE).getTestData("DataGather", "TestData"), RoleType.CORPORATE);
    }
}
