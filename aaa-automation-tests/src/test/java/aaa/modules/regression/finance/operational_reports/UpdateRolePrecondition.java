package aaa.modules.regression.finance.operational_reports;

import aaa.admin.modules.security.role.RoleType;
import aaa.helpers.product.OperationalReportsHelper;
import aaa.main.enums.OperationalReportsConstants;
import org.testng.annotations.Test;

import java.util.Arrays;


public class UpdateRolePrecondition extends OperationalReportsBaseTest {

    @Test
    public void updateRole() {
        OperationalReportsHelper.prepareEuwOpReportsPrivileges();
        adminApp().open();
        verifyRole(testDataManager.securityRole.get(RoleType.CORPORATE).getTestData("DataGather", "TestData"), RoleType.CORPORATE,
                Arrays.asList(OperationalReportsConstants.RolesPriviliges.REPORTS_OPERATIONAL_EUW_SCHEDULE,
                OperationalReportsConstants.RolesPriviliges.REPORTS_OPERATIONAL_EUW_VIEW));
    }
}