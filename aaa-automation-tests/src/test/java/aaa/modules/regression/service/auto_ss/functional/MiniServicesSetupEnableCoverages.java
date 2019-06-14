package aaa.modules.regression.service.auto_ss.functional;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.Groups;
import aaa.helpers.listeners.AaaTestListener;

@Listeners({AaaTestListener.class})
public class MiniServicesSetupEnableCoverages extends MiniServicesSetup {

	@Test(description = "Enabling 'canChange' for UM for AZ on test environment", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void enableUMForAZ() {
		enableCoverageForState(Constants.States.AZ, "UM");
	}

	@Test(description = "Enabling 'canChange' for UIM for AZ on test environment", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void enableUIMForAZ() {
		enableCoverageForState(Constants.States.AZ, "UIM");
	}

	@Test(description = "Enabling 'canChange' for UMBI for CO on test environment", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void enableUMBIForCO() {
		enableCoverageForState(Constants.States.CO, "UMBI");
	}

	@Test(description = "Enabling 'canChange' for MEDPM for CO on test environment", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void enableMEDPMForCO() {
		enableCoverageForState(Constants.States.CO, "MEDPM");
	}
}
