package aaa.modules.regression.finance.operational_reports;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Test;
import aaa.admin.metadata.security.RoleMetaData;
import aaa.admin.modules.security.role.IRole;
import aaa.admin.modules.security.role.RoleType;
import aaa.admin.modules.security.role.defaulttabs.GeneralRoleTab;
import aaa.admin.pages.security.RolePage;
import aaa.helpers.product.OperationalReportsHelper;
import aaa.main.enums.ActionConstants;
import aaa.main.enums.OperationalReportsConstants;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;

public class UpdateRolePrecondition extends BaseTest {
	/**
	 * @author Reda Kazlauskiene
	 * @author Maksim Piatrouski
	 * Objectives: Update Operational Reports euw priviliges
	 */
	@Test
	public void updateRole() {
		adminApp().open();
		OperationalReportsHelper.prepareEuwOpReportsPrivileges();
		verifyRole(testDataManager.securityRole.get(RoleType.CORPORATE).getTestData("DataGather", "TestData"), RoleType.CORPORATE,
				Arrays.asList(OperationalReportsConstants.RolesPriviliges.REPORTS_OPERATIONAL_EUW_SCHEDULE,
						OperationalReportsConstants.RolesPriviliges.REPORTS_OPERATIONAL_EUW_VIEW));
	}

	private void verifyRole(TestData role, RoleType roleType, List<String> roleNames) {
		IRole securityRole = roleType.get();
		TestData tdSecurityRole = testDataManager.securityRole.get(roleType);
		securityRole.navigate();
		securityRole.search(tdSecurityRole.getTestData("SearchData", "TestData")
				.adjust(TestData.makeKeyPath(RoleMetaData.SearchByField.class.getSimpleName(), RoleMetaData.SearchByField.ROLE_NAME.getLabel()), role.getTestData(RoleMetaData.GeneralRoleTab.class.getSimpleName()).getValue(RoleMetaData.GeneralRoleTab.ROLE_NAME.getLabel())));
		RolePage.tableRolesSearchResult.getRow(1).getCell(3).controls.links.get(ActionConstants.CHANGE).click();
		assertThat(new GeneralRoleTab().getAssetList().getAsset(RoleMetaData.GeneralRoleTab.ROLES_PRIVILEGES).getValue()).contains(roleNames);
	}
}
