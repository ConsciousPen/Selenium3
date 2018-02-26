package aaa.modules.regression.service.auto_ss.functional;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import aaa.helpers.constants.Groups;
import aaa.helpers.listeners.AaaTestListener;
import aaa.modules.regression.service.auto_ss.functional.preconditions.MiniServicesSetupPreconditions;
import toolkit.db.DBService;

@Listeners({AaaTestListener.class})
public class MiniServicesSetup extends MiniServicesSetupPreconditions {

	@Test(description = "Precondition", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void miniServicesEndorsementDeleteDelayConfigInsert() {
		DBService.get().executeUpdate(MiniServicesSetupPreconditions.AAA_CUSTOMER_ENDORSEMENT_DAYS_CONFIG_INSERT);
		DBService.get().executeUpdate(MiniServicesSetupPreconditions.AAA_CUSTOMER_ENDORSMENT_DAYS_DEFAULD_CONFIG_UPDATE);
	}

	@Test(description = "Precondition", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void myPolicyUserEnableUpdate() {
		DBService.get().executeUpdate(MiniServicesSetupPreconditions.MY_POLICY_USER_ENABLE_UPDATE);
	}

	@Test(description = "Precondition", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void myPolicyUserAddAllPrivilegesUpdate() {
		DBService.get().executeUpdate(MiniServicesSetupPreconditions.MY_POLICY_USER_ADD_ALL_PRIVILEGES_UPDATE);
	}
}
