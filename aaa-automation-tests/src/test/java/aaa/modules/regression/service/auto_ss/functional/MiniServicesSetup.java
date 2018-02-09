package aaa.modules.regression.service.auto_ss.functional;

import org.testng.annotations.Test;
import aaa.modules.regression.service.auto_ss.functional.preconditions.MiniServicesSetupPreconditions;
import toolkit.db.DBService;

public class MiniServicesSetup extends MiniServicesSetupPreconditions {

	@Test(description = "Precondition")
	public static void miniServicesEndorsementDeleteDelayConfigInsert() {
		DBService.get().executeUpdate(MiniServicesSetupPreconditions.AAA_CUSTOMER_ENDORSEMENT_DAYS_CONFIG_INSERT);
	}

	@Test(description = "Precondition")
	public static void myPolicyUserEnableUpdate() {
		DBService.get().executeUpdate(MiniServicesSetupPreconditions.MY_POLICY_USER_ENABLE_UPDATE);
	}

	@Test(description = "Precondition")
	public static void myPolicyUserAddAllPrivilegesUpdate() {
		DBService.get().executeUpdate(MiniServicesSetupPreconditions.MY_POLICY_USER_ADD_ALL_PRIVILEGES_UPDATE);
	}
}
