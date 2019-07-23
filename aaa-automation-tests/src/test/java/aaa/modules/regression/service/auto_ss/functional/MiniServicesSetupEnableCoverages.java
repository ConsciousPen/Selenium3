package aaa.modules.regression.service.auto_ss.functional;

import static aaa.modules.regression.service.auto_ss.functional.MiniServicesSetup.enableCoverageForState;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.Groups;
import aaa.helpers.listeners.AaaTestListener;

@Listeners({AaaTestListener.class})
public class MiniServicesSetupEnableCoverages {

	@Test(description = "Enabling 'canChange' for UMBI for AZ on test environment", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void enableUMForAZ() {
		enableCoverageForState(Constants.States.AZ, "UMBI");
	}

	@Test(description = "Enabling 'canChange' for UIM for AZ on test environment", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void enableUIMForAZ() {
		enableCoverageForState(Constants.States.AZ, "UIMBI");
	}

	@Test(description = "Enabling 'canChange' for UMBI for CO on test environment", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void enableUMBIForCO() {
		enableCoverageForState(Constants.States.CO, "UMBI");
	}

	@Test(description = "Enabling 'canChange' for MEDPM for CO on test environment", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void enableMEDPMForCO() {
		enableCoverageForState(Constants.States.CO, "MEDPM");
	}

	@Test(description = "Enabling 'canChange' for UMBI for CT on test environment", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void enableUMBIForCT() {
		enableCoverageForState(Constants.States.CT, "UMBI");
	}

	@Test(description = "Enabling 'canChange' for UIMCONV for CT on test environment", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void enableUIMCONVForCT() {
		enableCoverageForState(Constants.States.CT, "UIMCONV");
	}

	@Test(description = "Enabling 'canChange' for UMBI for KS on test environment", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void enableUMBIForKS() {
		enableCoverageForState(Constants.States.KS, "UMBI");
	}

	@Test(description = "Enabling 'canChange' for UMBI for ID on test environment", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void enableUMBIForID() {
		enableCoverageForState(Constants.States.ID, "UMBI");
	}

	@Test(description = "Enabling 'canChange' for UIMBI for ID on test environment", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void enableUIMBIForID() {
		enableCoverageForState(Constants.States.ID, "UIMBI");
	}
}
